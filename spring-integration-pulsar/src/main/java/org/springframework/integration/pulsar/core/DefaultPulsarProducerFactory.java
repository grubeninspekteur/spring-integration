/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.integration.pulsar.core;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.integration.pulsar.exception.PulsarException;
import org.springframework.integration.pulsar.support.PulsarUtils;
import org.springframework.util.Assert;

/**
 * A {@link ProducerFactory} that returns the same producer for the same topic. Topics are converted
 * to their canonical form {@code {persistent|non-persistent}://tenant/namespace/topic}.
 * <p>
 * This factory should not be used to create producers for short-lived or temporary topics.
 * The {@link Producer} is wrapped and the underlying instance is
 * not actually closed when {@link Producer#close()} is invoked. The {@link Producer}
 * is physically closed when {@link DisposableBean#destroy()} is invoked or when the
 * application context publishes a {@link ContextStoppedEvent}. You can also invoke
 * {@link #reset()}.
 * <p>
 * The producers created with this factory are thread-safe, under the condition that the provided
 * {@link Schema} instance is thread-safe.
 *
 * @param <V> the message payload type.
 *
 * @author Tim Feuerbach
 *
 * @since 5.TODO
 */
// TODO wrap producers so they can't be closed, close producers context close, configuration of producers
public class DefaultPulsarProducerFactory<V> implements ProducerFactory<V>, DisposableBean, ApplicationContextAware,
		ApplicationListener<ContextStoppedEvent> {

	private final PulsarClient pulsarClient;

	private final Schema<V> schema;

	private final ConcurrentHashMap<String, Producer<V>> producerMap = new ConcurrentHashMap<>();

	/**
	 * Construct a factory that creates producers using the supplied {@link PulsarClient} and {@link Schema}.
	 * @param pulsarClient the client.
	 * @param schema the schema instance.
	 */
	public DefaultPulsarProducerFactory(PulsarClient pulsarClient, Schema<V> schema) {
		Assert.notNull(pulsarClient, "pulsarClient must not be null");
		Assert.notNull(schema, "schema must not be null");
		this.pulsarClient = pulsarClient;
		this.schema = schema;
	}

	/**
	 * Return the cached producer for the given topic, or create one if none exists.
	 * <p>
	 * This operation is atomic.
	 * @param topic the topic name.
	 * @return the producer for the given topic.
	 * @throws org.springframework.integration.pulsar.exception.PulsarException if creation of the producer failed.
	 */
	@Override
	public Producer<V> createProducer(String topic) {
		Assert.notNull(topic, "topic must not be null");
		return this.producerMap.computeIfAbsent(PulsarUtils.canonicalizeTopic(topic), this::doCreateProducer);
	}

	/**
	 * Create a new producer instance, which will be added to the cache.
	 * @param canonicalTopic the canonical topic name.
	 * @return the created producer.
	 * @throws PulsarException when creation of the producer failed.
	 */
	protected Producer<V> doCreateProducer(String canonicalTopic) {
		try {
			return this.pulsarClient.newProducer(this.schema).topic(canonicalTopic).create();
		}
		catch (PulsarClientException e) {
			throw new PulsarException("Could not create pulsar producer", e);
		}
	}

	@Override
	public void reset() {
		// TODO
	}

	@Override
	public void destroy() throws Exception {
		// TODO
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		// TODO
	}

	@Override
	public void onApplicationEvent(ContextStoppedEvent event) {
		// TODO
	}
}

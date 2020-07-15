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

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import org.apache.pulsar.client.api.Producer;

import org.springframework.integration.pulsar.support.PulsarUtils;

/**
 * A {@linnk ProducerCache} backed by a {@link ConcurrentHashMap} that never evicts producers. Topics are converted
 * to their canonical form {@code {persistent|non-persistent}://tenant/namespace/topic}.
 *
 * @param <V> the message payload type.
 *
 * @author Tim Feuerbach
 *
 * @since 5.TODO
 */
public class DefaultProducerCache<V> implements ProducerCache<V> {

	protected ConcurrentHashMap<String, Producer<V>> producerMap = new ConcurrentHashMap<>();

	@Override
	public Producer<V> computeIfAbsent(String topic, Supplier<Producer<V>> producerSupplier) {
		return this.producerMap.computeIfAbsent(PulsarUtils.canonicalizeTopic(topic), k -> producerSupplier.get());
	}

	@Override
	public Collection<Producer<V>> getAll() {
		return new ArrayList<>(this.producerMap.values());
	}
}

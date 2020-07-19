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

import java.util.concurrent.CompletableFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.client.api.TypedMessageBuilder;

import org.springframework.integration.pulsar.exception.NoDefaultTopicException;
import org.springframework.integration.pulsar.exception.ProducerException;
import org.springframework.integration.pulsar.support.converter.MessagingMessageConverter;
import org.springframework.integration.pulsar.support.converter.PulsarMessageConverter;
import org.springframework.integration.pulsar.support.converter.PulsarOutboundMessage;
import org.springframework.messaging.Message;
import org.springframework.util.Assert;
import org.springframework.util.concurrent.CompletableToListenableFutureAdapter;
import org.springframework.util.concurrent.ListenableFuture;

/**
 * A template for sending messages to the Pulsar broker.
 * <p>
 * The template invokes {@link Producer#close()} after each operation to indicate a release
 * of the resource. In most cases producer instances should be kept open for reuse of the
 * connection The {@link DefaultPulsarProducerFactory} does this by wrapping Producer instances
 * so that closing is a no-op. See its documentation for details.
 *
 * @param <V> the message payload type.
 *
 * @author Tim Feuerbach
 *
 * @since 5.TODO
 */
public class PulsarTemplate<V> implements PulsarOperations<V> {

	protected final Log logger = LogFactory.getLog(this.getClass()); // NOSONAR - final

	private final ProducerFactory<V> producerFactory;

	private PulsarMessageConverter<V> messageConverter;

	private String defaultTopic;

	public PulsarTemplate(ProducerFactory<V> producerFactory) {
		Assert.notNull(producerFactory, "producerFactory must not be null");
		this.producerFactory = producerFactory;
		this.messageConverter = new MessagingMessageConverter();
	}

	/**
	 * The default topic for send methods where a topic is not
	 * provided.
	 * @return the topic.
	 */
	public String getDefaultTopic() {
		return this.defaultTopic;
	}

	/**
	 * Set the default topic for send methods where a topic is not
	 * provided.
	 * @param defaultTopic the topic.
	 */
	public void setDefaultTopic(String defaultTopic) {
		this.defaultTopic = defaultTopic;
	}

	/**
	 * Set the message converter to convert a Spring {@link Message} to a Pulsar message. If not set, defaults to a
	 * {@link MessagingMessageConverter}.
	 * @param messageConverter the message converter.
	 */
	public void setMessageConverter(
			PulsarMessageConverter messageConverter) {
		this.messageConverter = messageConverter;
	}

	@Override
	public MessageId sendDefault(V data) {
		checkDefaultTopic();
		return send(getDefaultTopic(), data);
	}

	@Override
	public ListenableFuture<MessageId> sendDefaultAsync(V data) {
		checkDefaultTopic();
		return sendAsync(getDefaultTopic(), data);
	}

	@Override
	public <T> MessageId sendDefault(T data, Schema<? super T> schema) {
		checkDefaultTopic();
		return send(getDefaultTopic(), data, schema);
	}

	@Override
	public <T> ListenableFuture<MessageId> sendDefaultAsync(T data, Schema<? super T> schema) {
		checkDefaultTopic();
		return sendAsync(getDefaultTopic(), data, schema);
	}

	@Override
	public MessageId send(String topic, V data) {
		Producer<V> producer = this.producerFactory.createProducer(topic);
		return doSendAndClose(producer, producer.newMessage().value(data));
	}

	@Override
	public ListenableFuture<MessageId> sendAsync(String topic, V data) {
		Producer<V> producer = this.producerFactory.createProducer(topic);
		return doSendAsyncAndClose(producer, producer.newMessage().value(data));
	}


	@Override
	public <T> MessageId send(String topic, T data, Schema<? super T> schema) {
		Producer<V> producer = this.producerFactory.createProducer(topic);
		return doSendAndClose(producer, producer.newMessage(schema).value(data));
	}

	@Override
	public <T> ListenableFuture<MessageId> sendAsync(String topic, T data, Schema<? super T> schema) {
		Producer<V> producer = this.producerFactory.createProducer(topic);
		return doSendAsyncAndClose(producer, producer.newMessage(schema).value(data));
	}

	@Override
	public MessageId send(Message<?> message) {
		ProducerMessageBuilderPair<V, ?> mappedMessage = mapMessage(message);
		return doSendAndClose(mappedMessage.producer, mappedMessage.messageBuilder);
	}

	@Override
	public ListenableFuture<MessageId> sendAsync(Message<?> message) {
		ProducerMessageBuilderPair<V, ?> mappedMessage = mapMessage(message);
		return doSendAsyncAndClose(mappedMessage.producer, mappedMessage.messageBuilder);
	}

	private void checkDefaultTopic() {
		if (this.defaultTopic == null) {
			throw new NoDefaultTopicException("Send failed, no default topic provided");
		}
	}

	private MessageId doSendAndClose(Producer<V> producer, TypedMessageBuilder<?> messageBuilder) {
		try {
			return messageBuilder.send();
		}
		catch (Exception e) {
			throw new ProducerException("Send message failed", e);
		}
		finally {
			try {
				producer.close();
			}
			catch (PulsarClientException e) {
				logger.error("Could not close Pulsar producer", e);
			}
		}
	}

	private ListenableFuture<MessageId> doSendAsyncAndClose(Producer<V> producer,
			TypedMessageBuilder<?> messageBuilder) {
		CompletableFuture<MessageId> future = messageBuilder.sendAsync().handle((messageId, exception) -> {
			if (exception != null) {
				throw new ProducerException("Send message failed", exception);
			}
			try {
				return messageId;
			}
			finally {
				try {
					producer.close();
				}
				catch (PulsarClientException e) {
					logger.error("Could not close Pulsar producer", e);
				}
			}
		});

		return new CompletableToListenableFutureAdapter<>(future);
	}

	private ProducerMessageBuilderPair<V, ?> mapMessage(Message<?> message) {
		PulsarOutboundMessage<V> pulsarOutboundMessage = this.messageConverter.fromMessage(message, getDefaultTopic());
		Producer<V> producer = this.producerFactory.createProducer(pulsarOutboundMessage.getTopic());
		TypedMessageBuilder<?> messageBuilder = pulsarOutboundMessage.getMessageCreator().createMessage(producer);
		return new ProducerMessageBuilderPair<>(producer, messageBuilder);
	}

	private static class ProducerMessageBuilderPair<V, T> {
		private final Producer<V> producer;

		private final TypedMessageBuilder<T> messageBuilder;

		private ProducerMessageBuilderPair(Producer<V> producer, TypedMessageBuilder<T> messageBuilder) {
			this.producer = producer;
			this.messageBuilder = messageBuilder;
		}
	}
}

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

import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.PulsarClientException.ProducerQueueIsFullError;
import org.apache.pulsar.client.api.Schema;

import org.springframework.integration.pulsar.exception.NoDefaultTopicException;
import org.springframework.integration.pulsar.exception.ProducerException;
import org.springframework.messaging.Message;
import org.springframework.util.concurrent.ListenableFuture;


/**
 * The basic Pulsar operations contract for sending messages.
 *
 * @param <V> the message payload type.
 *
 * @author Tim Feuerbach
 *
 * @since 5.TODO
 */
// TODO support auto flush property as in KafkaTemplate
public interface PulsarOperations<V> {

	/**
	 * Send the data to the default topic and wait for its acknowledgement by the Pulsar broker.
	 * @param data The data.
	 * @throws NoDefaultTopicException
	 * @throws ProducerException if the operation fails
	 * @return the message id assigned to the published message.
	 */
	MessageId sendDefault(V data);

	/**
	 * Send the data to the default topic.
	 * <p>Depending on the underlying producer configuration, when the producer queue is full, the future will fail
	 * with a {@link ProducerException} and an exception of {@link ProducerQueueIsFullError} as its cause.
	 * @param data The data.
	 * @throws NoDefaultTopicException if no default topic has been configured.
	 * @throws ProducerException if the operation fails.
	 * @return a future for the {@link MessageId} assigned to the published message.
	 */
	ListenableFuture<MessageId> sendDefaultAsync(V data);

	/**
	 * Send the data to the default topic using the given schema for serialization, and wait
	 * for its acknowledgement by the Pulsar broker. The schema does not have to conform with
	 * the operations type {@link V}.
	 * @param data The data.
	 * @param schema The schema for payload transformation.
	 * @param <T> type of the data.
	 * @throws NoDefaultTopicException if no default topic has been configured.
	 * @throws ProducerException if the operation fails.
	 * @return the message id assigned to the published message.
	 */
	<T> MessageId sendDefault(T data, Schema<? super T> schema);

	/**
	 * Send the data to the default topic using the given schema for serialization.
	 * <p>Depending on the underlying producer configuration, when the producer queue is full,
	 * the future will fail with a {@link ProducerException} and an exception of
	 * {@link ProducerQueueIsFullError} as its cause.
	 * @param data The data.
	 * @param schema The schema for payload transformation.
	 * @param <T> type of the data.
	 * @throws NoDefaultTopicException if no default topic has been configured.
	 * @throws ProducerException if the operation fails.
	 * @return a future for the {@link MessageId} assigned to the published message.
	 */
	<T> ListenableFuture<MessageId> sendDefaultAsync(T data, Schema<? super T> schema);

	/**
	 * Send the data to the given topic and wait for its acknowledgement by the Pulsar broker.
	 * @param topic The topic.
	 * @param data The data.
	 * @throws ProducerException if the operation fails
	 * @return the message id assigned to the published message.
	 */
	MessageId send(String topic, V data);

	/**
	 * Send the data to the given topic.
	 * <p>Depending on the underlying producer configuration, when the producer queue is full,
	 * the future will fail with a {@link ProducerException} and an exception of
	 * {@link ProducerQueueIsFullError} as its cause.
	 * @param topic The topic.
	 * @param data The data.
	 * @throws ProducerException if the operation fails.
	 * @return a future for the {@link MessageId} assigned to the published message.
	 */
	ListenableFuture<MessageId> sendAsync(String topic, V data);

	/**
	 * Send the data to the given topic using the given schema for serialization, and wait
	 * for its acknowledgement by the Pulsar broker. The schema does not have to conform with
	 * the operations type {@link V}.
	 * @param topic The topic.
	 * @param data The data.
	 * @param schema The schema for payload transformation.
	 * @param <T> type of the data.
	 * @throws ProducerException if the operation fails.
	 * @return the message id assigned to the published message.
	 */
	<T> MessageId send(String topic, T data, Schema<? super T> schema);

	/**
	 * Send the data to the given topic using the given schema for serialization. The schema
	 * does not have to conform with the operations type {@link V}.
	 * <p>Depending on the underlying producer configuration, when the producer queue is full,
	 * the future will fail with a {@link ProducerException} and an exception of
	 * {@link ProducerQueueIsFullError} as its cause.
	 * @param topic The topic.
	 * @param data The data.
	 * @param schema The schema for payload transformation.
	 * @param <T> type of the data.
	 * @throws ProducerException if the operation fails.
	 * @return a future for the {@link MessageId} assigned to the published message.
	 */
	<T> ListenableFuture<MessageId> sendAsync(String topic, T data, Schema<? super T> schema);

	/**
	 * Send a message with routing information and metadata in message headers. The message payload
	 * may be converted before sending.
	 * @param message the message to send.
	 * @return the message id assigned to the published message.
	 * @see PulsarHeaders#TOPIC
	 */
	MessageId send(Message<?> message);

	/**
	 * Send a message with routing information and metadata in message headers, synchronously waiting for its
	 * acknowledgement by the Pulsar broker. The message payload may be converted before sending.
	 * @param message the message to send.
	 * @return a Future for the {@link MessageId} assigned to the published message.
	 * @see PulsarHeaders#TOPIC
	 */
	ListenableFuture<MessageId> sendAsync(Message<?> message);

}

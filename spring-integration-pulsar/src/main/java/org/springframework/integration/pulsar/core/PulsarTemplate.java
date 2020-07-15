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
import org.apache.pulsar.client.api.Schema;

import org.springframework.messaging.Message;
import org.springframework.util.concurrent.ListenableFuture;

/**
 * A template for sending messages to the Pulsar broker.
 *
 * @param <V> the message payload type.
 *
 * @author Tim Feuerbach
 *
 * @since 5.TODO
 */
// TODO how to safely close all producers while there are incoming messages?
public class PulsarTemplate<V> implements PulsarOperations<V> {
	@Override
	public MessageId sendDefault(V data) {
		return null;
	}

	@Override
	public ListenableFuture<MessageId> sendDefaultAsync(V data) {
		return null;
	}

	@Override
	public <T> MessageId sendDefault(T data, Schema<? super T> schema) {
		return null;
	}

	@Override
	public MessageId send(String topic, V data) {
		return null;
	}

	@Override
	public ListenableFuture<MessageId> sendAsync(String topic, V data) {
		return null;
	}

	@Override
	public <T> ListenableFuture<MessageId> sendAsync(String topic, T data, Schema<? super T> schema) {
		return null;
	}

	@Override
	public ListenableFuture<MessageId> send(Message<?> message) {
		return null;
	}

	@Override
	public ListenableFuture<MessageId> sendAsync(Message<?> message) {
		return null;
	}

}

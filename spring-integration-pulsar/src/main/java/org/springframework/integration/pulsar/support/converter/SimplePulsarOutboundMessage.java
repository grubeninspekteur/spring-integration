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

package org.springframework.integration.pulsar.support.converter;

/**
 * A POJO implementation of a {@link PulsarOutboundMessage}.
 *
 * @param <V> type of the producer's default schema.
 *
 * @author Tim Feuerbach
 *
 * @since 5.TODO
 */
public class SimplePulsarOutboundMessage<V> implements PulsarOutboundMessage<V> {

	private final String topic;

	private final MessageCreator<V> messageCreator;

	/**
	 * Create a {@link SimplePulsarOutboundMessage}.
	 * @param topic the topic.
	 * @param messageCreator the message creator.
	 */
	public SimplePulsarOutboundMessage(String topic, MessageCreator<V> messageCreator) {
		this.topic = topic;
		this.messageCreator = messageCreator;
	}

	@Override
	public String getTopic() {
		return this.topic;
	}

	@Override
	public MessageCreator getMessageCreator() {
		return this.messageCreator;
	}
}

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
 * Captures the parts to create an outgoing Pulsar message. Since the topic a Producer publishes to must be known
 * beforehand, this interface captures the topic, as well as a {@link MessageCreator}. The latter holds the value
 * to send and may also use a custom schema.
 *
 * @param <V> type of the producer's default schema.
 *
 * @author Tim Feuerbach
 *
 * @since 5.TODO
 */
public interface PulsarOutboundMessage<V> {

	/**
	 * Return the topic to send the message to.
	 * @return the topic.
	 */
	String getTopic();

	/**
	 * Return the {@link MessageCreator} instance to create the message.
	 * @return the message creator.
	 */
	MessageCreator<V> getMessageCreator();
}

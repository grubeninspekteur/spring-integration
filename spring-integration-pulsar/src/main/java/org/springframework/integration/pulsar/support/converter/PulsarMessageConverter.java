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

import org.apache.pulsar.client.api.TypedMessageBuilder;

import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;

/**
 * Converts a {@link Message} to a {@link PulsarOutboundMessage} or vice versa.
 *
 * @param <V> type of the producer's default schema.
 *
 * @author Tim Feuerbach
 *
 * @since 5.TODO
 */
public interface PulsarMessageConverter<V> {


	/**
	 * Convert a {@link Message} to a {@link TypedMessageBuilder}.
	 * @param message the message.
	 * @param defaultTopic the default topic to use if no header found.
	 * @return the message builder obtained from the supplied producer.
	 */
	PulsarOutboundMessage<V> fromMessage(Message<?> message, @Nullable String defaultTopic);

	// TODO define toMessage contract
}

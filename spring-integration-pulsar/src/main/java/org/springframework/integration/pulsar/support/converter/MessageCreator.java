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

import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.TypedMessageBuilder;

/**
 * Strategy of a {@link PulsarOutboundMessage} which creates the actual message.
 *
 * @param <V> type of the producer's default schema.
 *
 * @author Tim Feuerbach
 *
 * @since 5.TODO
 */
@FunctionalInterface
public interface MessageCreator<V> {

	/**
	 * Create a new message with the data to send, potentially using a custom schema or customizing it further.
	 * @param producer the producer instance matching the topic from {@link PulsarOutboundMessage#getTopic()}.
	 * @return the created message builder.
	 */
	TypedMessageBuilder<?> createMessage(Producer<V> producer);
}

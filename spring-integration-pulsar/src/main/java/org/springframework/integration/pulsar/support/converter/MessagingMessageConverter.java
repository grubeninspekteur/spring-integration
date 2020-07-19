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

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.springframework.integration.pulsar.core.PulsarHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.util.Assert;

/**
 * TODO doc; mention no conversion (default Schema)
 *
 * @param <V> type of the producer's default schema.
 *
 * @author Tim Feuerbach
 *
 * @since 5.TODO
 */
public class MessagingMessageConverter<V> implements PulsarMessageConverter<V> {

	@Override
	public PulsarOutboundMessage<V> fromMessage(Message<?> message,
			String defaultTopic) {
		MessageHeaders headers = message.getHeaders();
		Object topicHeader = headers.get(PulsarHeaders.TOPIC);
		String topic = null;
		if (topicHeader instanceof byte[]) {
			topic = new String(((byte[]) topicHeader), StandardCharsets.UTF_8);
		}
		else if (topicHeader instanceof String) {
			topic = (String) topicHeader;
		}
		else if (topicHeader == null) {
			Assert.state(defaultTopic != null, "With no topic header, a defaultTopic is required");
		}
		else {
			throw new IllegalStateException(PulsarHeaders.TOPIC + " must be a String or byte[], not "
					+ topicHeader.getClass());
		}

		Map<String, Object> config = new HashMap<>();
		Object data = message.getPayload();

		// TODO map Pulsar headers
		// TODO map other headers
		// TODO the generic types won't work with a SchemaMessageConverter
		//noinspection unchecked
		return createPulsarMessage(topic, config, (V) data);
	}

	/**
	 * TODO doc
	 * @param topic
	 * @param config
	 * @param data
	 * @param <T>
	 * @return
	 */
	protected <T> ConfPulsarOutboundMessage<T> createPulsarMessage(String topic, Map<String, Object> config,
			T data) {
		return new ConfPulsarOutboundMessage<>(topic, data, config);
	}

}

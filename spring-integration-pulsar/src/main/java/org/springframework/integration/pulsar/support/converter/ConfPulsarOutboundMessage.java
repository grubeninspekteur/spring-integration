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

import java.util.Map;

import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.client.api.TypedMessageBuilder;

/**
 * A {@link PulsarOutboundMessage} that configures the {@link TypedMessageBuilder} using {@link TypedMessageBuilder#loadConf(Map)}.
 * Additionally, a {@link Schema} can be provided to convert the message before sending.
 *
 * @param <V> type of the producer's default schema.
 *
 * @author Tim Feuerbach
 *
 * @since 5.TODO
 */
public class ConfPulsarOutboundMessage<V> extends SimplePulsarOutboundMessage<V> {

	/**
	 * Create a {@link SimplePulsarOutboundMessage}.
	 * @param topic the topic.
	 * @param data   the data.
	 * @param config configuration to be loaded via {@link TypedMessageBuilder#loadConf(Map)}.
	 * @see org.apache.pulsar.client.api.Producer#newMessage()
	 */
	public ConfPulsarOutboundMessage(String topic, V data, Map<String, Object> config) {
		super(topic, producer -> producer.newMessage().value(data).loadConf(config));
	}

	/**
	 * Create a {@link SimplePulsarOutboundMessage} using the given {@link Schema} to transform the payload.
	 * @param topic the topic.
	 * @param data the data.
	 * @param config configuration to be loaded via {@link TypedMessageBuilder#loadConf(Map)}.
	 * @param schema schema to convert the data.
	 * @param <T> type of the data.
	 * @see org.apache.pulsar.client.api.Producer#newMessage(Schema)
	 */
	public <T> ConfPulsarOutboundMessage(String topic, T data, Map<String, Object> config, Schema<T> schema) {
		super(topic, producer -> producer.newMessage(schema).value(data).loadConf(config));
	}

}

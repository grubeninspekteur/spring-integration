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

import java.util.Collection;
import java.util.function.Supplier;

import org.apache.pulsar.client.api.Producer;

/**
 * Stores Pulsar producers on a per-topic basis for reuse. Pulsar producers are thread-safe.
 * <p>
 * Implementations of this cache must be thread-safe.
 *
 * @param <V> the message payload type.
 *
 * @author Tim Feuerbach
 *
 * @since 5.TODO
 */
public interface ProducerCache<V> {

	/**
	 * Return the cached producer for the given topic. If there is none, use the supplier to create and store a new
	 * producer.
	 * <p>
	 * This operation is atomic.
	 * @param topic the topic name.
	 * @param producerSupplier a supplier to create a new producer in case of a cache miss.
	 * @return the producer for the given topic.
	 */
	Producer<V> computeIfAbsent(String topic, Supplier<Producer<V>> producerSupplier);

	/**
	 * Enumerate all producers in the cache at invocation time. Result may be inaccurate if the cache
	 * @return the producers in the cache.
	 */
	Collection<Producer<V>> getAll();
}

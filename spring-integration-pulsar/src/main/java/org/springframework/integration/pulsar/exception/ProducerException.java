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

package org.springframework.integration.pulsar.exception;

/**
 * Exception thrown when an operation on a Pulsar {@link org.apache.pulsar.client.api.Producer} failed.
 *
 * @author Tim Feuerbach
 *
 * @since 5.TODO
 */
@SuppressWarnings("serial")
public class ProducerException extends PulsarException {
	/**
	 * Construct an instance with the provided properties.
	 * @param message the message.
	 */
	public ProducerException(String message) {
		super(message);
	}

	/**
	 * Construct an instance with the provided properties.
	 * @param message the message.
	 * @param cause   the cause.
	 */
	public ProducerException(String message, Throwable cause) {
		super(message, cause);
	}
}

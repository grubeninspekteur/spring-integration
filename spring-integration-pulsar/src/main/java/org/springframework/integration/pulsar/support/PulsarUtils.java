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

package org.springframework.integration.pulsar.support;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility methods for Pulsar.
 *
 * @author Tim Feuerbach
 *
 * @since 5.TODO
 */
public final class PulsarUtils {

	private PulsarUtils() {
	}

	private static final String CANONICAL_TOPIC_NAME_REGEX = "(?:persistent|non-persistent)://[^/]+/[^/]+/[^/]+";

	private static final String NO_TOPIC_DOMAIN_REGEX = "[^/]+/[^/]+/[^/]+";

	private static final String TOPIC_SHORT_FORM_REGEX = "[^/]+";

	private static final Pattern TOPIC_NAME_PATTERN = Pattern.compile("(" + CANONICAL_TOPIC_NAME_REGEX +
			")|(" + NO_TOPIC_DOMAIN_REGEX + ")|(" + TOPIC_SHORT_FORM_REGEX + ")");

	/**
	 * Canonicalize the given topic in the form {@code {persistent|non-persistent}://tenant/namespace/topic}. E.g.,
	 * "foo" becomes "persistent://public/default/foo".
	 * @param topic the topic name.
	 * @return the topic's canonical name, or the input if canonicalization was not successful.
	 */
	public static String canonicalizeTopic(String topic) {
		Objects.requireNonNull(topic, "topic");
		Matcher matcher = TOPIC_NAME_PATTERN.matcher(topic);
		if (matcher.matches()) {
			String fullyQualified = matcher.group(1);
			if (fullyQualified != null) {
				return fullyQualified;
			}
			String tenantNameSpaceTopic = matcher.group(2);
			if (tenantNameSpaceTopic != null) {
				return "persistent://" + tenantNameSpaceTopic;
			}
			String shortForm = matcher.group(3);
			if (shortForm != null) {
				return "persistent://public/default/" + shortForm;
			}
		}
		return topic;
	}
}

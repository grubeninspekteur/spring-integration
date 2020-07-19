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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

/**
 * Tests for {@link PulsarUtils}.
 *
 * @author Tim Feuerbach
 */
public class PulsarUtilsTests {

	@Test
	public void testCanonicalizeTopics() {
		assertThat(PulsarUtils.canonicalizeTopic("foo")).isEqualTo("persistent://public/default/foo");
		assertThat(PulsarUtils.canonicalizeTopic("gold/silver/bar-topic"))
				.isEqualTo("persistent://gold/silver/bar-topic");
		assertThat(PulsarUtils.canonicalizeTopic("gold/silver/bar%topic"))
				.isEqualTo("persistent://gold/silver/bar%topic");
		assertThat(PulsarUtils.canonicalizeTopic("persistent://gold/silver/bar"))
				.isEqualTo("persistent://gold/silver/bar");
		assertThat(PulsarUtils.canonicalizeTopic("non-persistent://gold/silver/bar"))
				.isEqualTo("non-persistent://gold/silver/bar");
		assertThat(PulsarUtils.canonicalizeTopic("not/supported")).isEqualTo("not/supported");
		assertThat(PulsarUtils.canonicalizeTopic("too/many/slashes/topic")).isEqualTo("too/many/slashes/topic");
	}
}
/*
 * Copyright 2002-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.integration.config.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.generic.GenericBeanFactoryAccessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.channel.ChannelRegistry;
import org.springframework.integration.channel.ChannelRegistryAware;
import org.springframework.integration.channel.MessageChannel;
import org.springframework.integration.channel.PollableChannel;
import org.springframework.integration.channel.SubscribableChannel;
import org.springframework.integration.config.xml.MessageBusParser;
import org.springframework.integration.endpoint.AbstractMessageConsumer;
import org.springframework.integration.endpoint.AbstractReplyProducingMessageConsumer;
import org.springframework.integration.endpoint.MessageEndpoint;
import org.springframework.integration.endpoint.PollingConsumerEndpoint;
import org.springframework.integration.endpoint.SubscribingConsumerEndpoint;
import org.springframework.integration.message.MessageConsumer;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Base class for Method-level annotation post-processors.
 *
 * @author Mark Fisher
 */
public abstract class AbstractMethodAnnotationPostProcessor<T extends Annotation> implements MethodAnnotationPostProcessor<T> {

	private static final String INPUT_CHANNEL_ATTRIBUTE = "inputChannel";

	private static final String OUTPUT_CHANNEL_ATTRIBUTE = "outputChannel";


	private final GenericBeanFactoryAccessor beanFactoryAccessor;

	protected final ChannelRegistry channelRegistry;


	public AbstractMethodAnnotationPostProcessor(ListableBeanFactory beanFactory) {
		Assert.notNull(beanFactory, "BeanFactory must not be null");
		this.beanFactoryAccessor = new GenericBeanFactoryAccessor(beanFactory);
		this.channelRegistry = this.beanFactoryAccessor.getBean(
				MessageBusParser.MESSAGE_BUS_BEAN_NAME, ChannelRegistry.class);
	}


	public Object postProcess(Object bean, String beanName, Method method, T annotation) {
		MessageConsumer consumer = this.createConsumer(bean, method, annotation);
		if (consumer instanceof ChannelRegistryAware) {
			((ChannelRegistryAware) consumer).setChannelRegistry(this.channelRegistry);
		}
		Poller pollerAnnotation = AnnotationUtils.findAnnotation(method, Poller.class);
		MessageEndpoint endpoint = this.createEndpoint(consumer, annotation, pollerAnnotation);
		if (endpoint != null) {
			if (endpoint instanceof InitializingBean) {
				try {
					((InitializingBean) endpoint).afterPropertiesSet();
				}
				catch (Exception e) {
					throw new IllegalStateException(
							"failed to initialize annotation-based consumer", e);
				}
			}
			return endpoint;
		}
		return consumer;
	}

	protected boolean shouldCreateEndpoint(T annotation) {
		return (StringUtils.hasText((String) AnnotationUtils.getValue(annotation, INPUT_CHANNEL_ATTRIBUTE)));
	}

	private MessageEndpoint createEndpoint(MessageConsumer consumer, T annotation, Poller pollerAnnotation) {
		MessageEndpoint endpoint = null;
		String inputChannelName = (String) AnnotationUtils.getValue(annotation, INPUT_CHANNEL_ATTRIBUTE);
		if (StringUtils.hasText(inputChannelName)) {
			MessageChannel inputChannel = this.channelRegistry.lookupChannel(inputChannelName);
			Assert.notNull(inputChannel, "unable to resolve inputChannel '" + inputChannelName + "'");
			if (consumer instanceof AbstractMessageConsumer) {
				if (inputChannel instanceof PollableChannel) {
					PollingConsumerEndpoint pollingEndpoint = new PollingConsumerEndpoint(
							consumer, (PollableChannel) inputChannel);
					if (pollerAnnotation != null) {
						AnnotationConfigUtils.configurePollingEndpointWithPollerAnnotation(
								pollingEndpoint, pollerAnnotation, this.beanFactoryAccessor.getBeanFactory());
					}
					endpoint = pollingEndpoint;
				}
				else if (inputChannel instanceof SubscribableChannel) {
					Assert.isTrue(pollerAnnotation == null,
							"The @Poller annotation should only be provided for a PollableChannel");
					endpoint = new SubscribingConsumerEndpoint(consumer, (SubscribableChannel) inputChannel);
				}
				else {
					throw new IllegalArgumentException("unsupported channel type: ["
							+ inputChannel.getClass() + "]");
				}
			}
			if (consumer instanceof AbstractReplyProducingMessageConsumer) {
				String outputChannelName = (String) AnnotationUtils.getValue(annotation, OUTPUT_CHANNEL_ATTRIBUTE);
				if (StringUtils.hasText(outputChannelName)) {
					MessageChannel outputChannel = this.channelRegistry.lookupChannel(outputChannelName);
					Assert.notNull(outputChannel, "unable to resolve outputChannel '" + outputChannelName + "'");
					((AbstractReplyProducingMessageConsumer) consumer).setOutputChannel(outputChannel);
				}
			}
		}
		return endpoint;
	}

	/**
	 * Subclasses must implement this method to create the MessageConsumer.
	 */
	protected abstract MessageConsumer createConsumer(Object bean, Method method, T annotation);

}

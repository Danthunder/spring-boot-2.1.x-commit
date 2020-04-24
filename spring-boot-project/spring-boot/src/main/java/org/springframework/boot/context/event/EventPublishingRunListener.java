/*
 * Copyright 2012-2019 the original author or authors.
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

package org.springframework.boot.context.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.ErrorHandler;

/**
 * {@link SpringApplicationRunListener} to publish {@link SpringApplicationEvent}s.
 * <p>
 * Uses an internal {@link ApplicationEventMulticaster} for the events that are fired
 * before the context is actually refreshed.
 *
 * @author Phillip Webb
 * @author Stephane Nicoll
 * @author Andy Wilkinson
 * @author Artsiom Yudovin
 * @since 1.0.0
 */
public class EventPublishingRunListener implements SpringApplicationRunListener, Ordered {

	private final SpringApplication application;

	private final String[] args;

	// 广播器
	private final SimpleApplicationEventMulticaster initialMulticaster;

	public EventPublishingRunListener(SpringApplication application, String[] args) {
		this.application = application;
		this.args = args;
		this.initialMulticaster = new SimpleApplicationEventMulticaster();
		for (ApplicationListener<?> listener : application.getListeners()) {
			this.initialMulticaster.addApplicationListener(listener);
		}
	}

	@Override
	public int getOrder() {
		return 0;
	}

	@Override
	public void starting() {
		/**
		 * initialMulticaster 的类 SimpleApplicationEventMulticaster 的工作方式
		 * 1. 首先他会广播一个事件
		 * 对应代码在 starting 方法内会调用 SimpleApplicationEventMulticaster#multicastEvent(ApplicationEvent,ResolvableType)方法，
		 * for (ApplicationListener<?> listener : getApplicationListeners(event, type)) {
		 *     if (executor != null) {
		 *         executor.execute(() -> invokeListener(listener, event));
		 *     } else {
		 *         invokeListener(listener, event);
		 *     }
		 * }
		 * getApplicationListeners(event, type)做了两件事情，首先传来两个参数：
		 * 这两个参数就是事件类型，即告诉所有的监听器现在有一个type类型的event，你们是否感兴趣？
		 *
		 * 2. 通知所有监听器
		 * getApplicationListeners 通知所有的监听器（遍历所有监听器）
		 * 然后监听器会接收到这个事件，继而监听器会判断这个事件自己是否感兴趣。
		 * 各监听器如何判断自己 是否对该事件感兴趣？主要有两个步骤来确定
		 *
		 * 第一个步骤：两个方法确定
		 * boolean supportsEventType(ResolvableType resolvableType)
		 * boolean supportsSourceType(Class<?> sourceType)
		 *
		 * 在supportsEvent方法（返回值boolean）中，遍历所有当前的监听器，判断
		 * smartListener.supportsEventType(eventType) && smartListener.supportsSourceType(sourceType)
		 * 这两个方法可以理解为通过传入一个事件类型，返回一个boolean，任意一个返回 false 表示该监听器
		 * 对该eventType的事件不感兴趣。如果感兴趣，则该监听器会被add到一个list中，在后续的代码中依次执行回调方法的调用
		 *
		 * 第二个步骤：回调方法
		 * 在监听器回调方法中，还是可以对事件的类型进行判断，如果事件类型不感兴趣，不执行就可以
		 *
		 * 3. 获得所有对这个事件感兴趣的监听器，遍历执行其 onApplicationEvent 方法，方法签名如下：
		 * void onApplicationEvent(ApplicationEvent applicationEvent)
		 * 这里传入一个ApplicationEvent的事件过去，在spring-boot初始化的11个监听器中，哪些对该
		 * ApplicationStartingEvent事件感兴趣（订阅该事件）？结果是5个，分别如下
		 * 根据上述第2点第一个步骤，查看源代码
		 * 1). org.springframework.boot.context.logging.LoggingApplicationListener
		 * 2). org.springframework.boot.autoconfigure.BackgroundPreinitializer
		 * 3). com.web.test.MyListener
		 * 4). org.springframework.boot.context.config.DelegatingApplicationListener
		 * 5). org.springframework.boot.liquibase.LiquibaseServiceLocatorApplicationListener
		 *
		 * 4. initialMulticaster 是 SimpleApplicationEventMulticaster 对象，主要两个方法
		 * 一个是广播事件
		 * 一个执行感兴趣listeners的onApplicationEvent()方法
		 */
		// 调用multicastEvent方法发布ApplicationStartingEvent事件，通知监听器该事件已经到来
		this.initialMulticaster.multicastEvent(new ApplicationStartingEvent(this.application, this.args));
	}

	@Override
	public void environmentPrepared(ConfigurableEnvironment environment) {
		this.initialMulticaster
				.multicastEvent(new ApplicationEnvironmentPreparedEvent(this.application, this.args, environment));
	}

	@Override
	public void contextPrepared(ConfigurableApplicationContext context) {
		this.initialMulticaster
				.multicastEvent(new ApplicationContextInitializedEvent(this.application, this.args, context));
	}

	@Override
	public void contextLoaded(ConfigurableApplicationContext context) {
		for (ApplicationListener<?> listener : this.application.getListeners()) {
			if (listener instanceof ApplicationContextAware) {
				((ApplicationContextAware) listener).setApplicationContext(context);
			}
			context.addApplicationListener(listener);
		}
		this.initialMulticaster.multicastEvent(new ApplicationPreparedEvent(this.application, this.args, context));
	}

	@Override
	public void started(ConfigurableApplicationContext context) {
		context.publishEvent(new ApplicationStartedEvent(this.application, this.args, context));
	}

	@Override
	public void running(ConfigurableApplicationContext context) {
		context.publishEvent(new ApplicationReadyEvent(this.application, this.args, context));
	}

	@Override
	public void failed(ConfigurableApplicationContext context, Throwable exception) {
		ApplicationFailedEvent event = new ApplicationFailedEvent(this.application, this.args, context, exception);
		if (context != null && context.isActive()) {
			// Listeners have been registered to the application context so we should
			// use it at this point if we can
			context.publishEvent(event);
		}
		else {
			// An inactive context may not have a multicaster so we use our multicaster to
			// call all of the context's listeners instead
			if (context instanceof AbstractApplicationContext) {
				for (ApplicationListener<?> listener : ((AbstractApplicationContext) context)
						.getApplicationListeners()) {
					this.initialMulticaster.addApplicationListener(listener);
				}
			}
			this.initialMulticaster.setErrorHandler(new LoggingErrorHandler());
			this.initialMulticaster.multicastEvent(event);
		}
	}

	private static class LoggingErrorHandler implements ErrorHandler {

		private static final Log logger = LogFactory.getLog(EventPublishingRunListener.class);

		@Override
		public void handleError(Throwable throwable) {
			logger.warn("Error calling ApplicationEventListener", throwable);
		}

	}

}

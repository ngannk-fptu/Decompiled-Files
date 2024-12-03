/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.beans.factory.config.CustomScopeConfigurer
 *  org.springframework.beans.factory.config.Scope
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.annotation.Bean
 *  org.springframework.core.task.TaskExecutor
 *  org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
 *  org.springframework.lang.Nullable
 *  org.springframework.messaging.MessageChannel
 *  org.springframework.messaging.SubscribableChannel
 *  org.springframework.messaging.converter.MappingJackson2MessageConverter
 *  org.springframework.messaging.simp.SimpMessageSendingOperations
 *  org.springframework.messaging.simp.SimpMessagingTemplate
 *  org.springframework.messaging.simp.SimpSessionScope
 *  org.springframework.messaging.simp.annotation.support.SimpAnnotationMethodMessageHandler
 *  org.springframework.messaging.simp.broker.AbstractBrokerMessageHandler
 *  org.springframework.messaging.simp.config.AbstractMessageBrokerConfiguration
 *  org.springframework.messaging.simp.stomp.StompBrokerRelayMessageHandler
 *  org.springframework.messaging.simp.user.SimpUserRegistry
 *  org.springframework.messaging.support.AbstractSubscribableChannel
 *  org.springframework.scheduling.TaskScheduler
 *  org.springframework.web.servlet.HandlerMapping
 */
package org.springframework.web.socket.config.annotation;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.beans.factory.config.Scope;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.lang.Nullable;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.SimpSessionScope;
import org.springframework.messaging.simp.annotation.support.SimpAnnotationMethodMessageHandler;
import org.springframework.messaging.simp.broker.AbstractBrokerMessageHandler;
import org.springframework.messaging.simp.config.AbstractMessageBrokerConfiguration;
import org.springframework.messaging.simp.stomp.StompBrokerRelayMessageHandler;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.messaging.support.AbstractSubscribableChannel;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.WebSocketMessageBrokerStats;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebMvcStompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;
import org.springframework.web.socket.messaging.DefaultSimpUserRegistry;
import org.springframework.web.socket.messaging.SubProtocolWebSocketHandler;
import org.springframework.web.socket.messaging.WebSocketAnnotationMethodMessageHandler;

public abstract class WebSocketMessageBrokerConfigurationSupport
extends AbstractMessageBrokerConfiguration {
    @Nullable
    private WebSocketTransportRegistration transportRegistration;

    protected SimpAnnotationMethodMessageHandler createAnnotationMethodMessageHandler(AbstractSubscribableChannel clientInboundChannel, AbstractSubscribableChannel clientOutboundChannel, SimpMessagingTemplate brokerMessagingTemplate) {
        return new WebSocketAnnotationMethodMessageHandler((SubscribableChannel)clientInboundChannel, (MessageChannel)clientOutboundChannel, (SimpMessageSendingOperations)brokerMessagingTemplate);
    }

    protected SimpUserRegistry createLocalUserRegistry(@Nullable Integer order) {
        DefaultSimpUserRegistry registry = new DefaultSimpUserRegistry();
        if (order != null) {
            registry.setOrder(order);
        }
        return registry;
    }

    @Bean
    public HandlerMapping stompWebSocketHandlerMapping(WebSocketHandler subProtocolWebSocketHandler, TaskScheduler messageBrokerTaskScheduler) {
        WebSocketHandler handler = this.decorateWebSocketHandler(subProtocolWebSocketHandler);
        WebMvcStompEndpointRegistry registry = new WebMvcStompEndpointRegistry(handler, this.getTransportRegistration(), messageBrokerTaskScheduler);
        ApplicationContext applicationContext = this.getApplicationContext();
        if (applicationContext != null) {
            registry.setApplicationContext(applicationContext);
        }
        this.registerStompEndpoints(registry);
        return registry.getHandlerMapping();
    }

    @Bean
    public WebSocketHandler subProtocolWebSocketHandler(AbstractSubscribableChannel clientInboundChannel, AbstractSubscribableChannel clientOutboundChannel) {
        return new SubProtocolWebSocketHandler((MessageChannel)clientInboundChannel, (SubscribableChannel)clientOutboundChannel);
    }

    protected WebSocketHandler decorateWebSocketHandler(WebSocketHandler handler) {
        for (WebSocketHandlerDecoratorFactory factory : this.getTransportRegistration().getDecoratorFactories()) {
            handler = factory.decorate(handler);
        }
        return handler;
    }

    protected final WebSocketTransportRegistration getTransportRegistration() {
        if (this.transportRegistration == null) {
            this.transportRegistration = new WebSocketTransportRegistration();
            this.configureWebSocketTransport(this.transportRegistration);
        }
        return this.transportRegistration;
    }

    protected void configureWebSocketTransport(WebSocketTransportRegistration registry) {
    }

    protected abstract void registerStompEndpoints(StompEndpointRegistry var1);

    @Bean
    public static CustomScopeConfigurer webSocketScopeConfigurer() {
        CustomScopeConfigurer configurer = new CustomScopeConfigurer();
        configurer.addScope("websocket", (Scope)new SimpSessionScope());
        return configurer;
    }

    @Bean
    public WebSocketMessageBrokerStats webSocketMessageBrokerStats(@Nullable AbstractBrokerMessageHandler stompBrokerRelayMessageHandler, WebSocketHandler subProtocolWebSocketHandler, @Qualifier(value="clientInboundChannelExecutor") TaskExecutor inboundExecutor, @Qualifier(value="clientOutboundChannelExecutor") TaskExecutor outboundExecutor, @Qualifier(value="messageBrokerTaskScheduler") TaskScheduler scheduler) {
        WebSocketMessageBrokerStats stats = new WebSocketMessageBrokerStats();
        stats.setSubProtocolWebSocketHandler((SubProtocolWebSocketHandler)subProtocolWebSocketHandler);
        if (stompBrokerRelayMessageHandler instanceof StompBrokerRelayMessageHandler) {
            stats.setStompBrokerRelay((StompBrokerRelayMessageHandler)stompBrokerRelayMessageHandler);
        }
        stats.setInboundChannelExecutor(inboundExecutor);
        stats.setOutboundChannelExecutor(outboundExecutor);
        stats.setSockJsTaskScheduler(scheduler);
        return stats;
    }

    protected MappingJackson2MessageConverter createJacksonConverter() {
        MappingJackson2MessageConverter messageConverter = super.createJacksonConverter();
        Jackson2ObjectMapperBuilder builder = Jackson2ObjectMapperBuilder.json();
        ApplicationContext applicationContext = this.getApplicationContext();
        if (applicationContext != null) {
            builder.applicationContext(applicationContext);
        }
        messageConverter.setObjectMapper(builder.build());
        return messageConverter;
    }
}


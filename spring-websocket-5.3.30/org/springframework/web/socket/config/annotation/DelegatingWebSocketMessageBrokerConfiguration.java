/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.messaging.converter.MessageConverter
 *  org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver
 *  org.springframework.messaging.handler.invocation.HandlerMethodReturnValueHandler
 *  org.springframework.messaging.simp.config.ChannelRegistration
 *  org.springframework.messaging.simp.config.MessageBrokerRegistry
 *  org.springframework.util.CollectionUtils
 */
package org.springframework.web.socket.config.annotation;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.handler.invocation.HandlerMethodReturnValueHandler;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.CollectionUtils;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurationSupport;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

@Configuration(proxyBeanMethods=false)
public class DelegatingWebSocketMessageBrokerConfiguration
extends WebSocketMessageBrokerConfigurationSupport {
    private final List<WebSocketMessageBrokerConfigurer> configurers = new ArrayList<WebSocketMessageBrokerConfigurer>();

    @Autowired(required=false)
    public void setConfigurers(List<WebSocketMessageBrokerConfigurer> configurers) {
        if (!CollectionUtils.isEmpty(configurers)) {
            this.configurers.addAll(configurers);
        }
    }

    @Override
    protected void registerStompEndpoints(StompEndpointRegistry registry) {
        for (WebSocketMessageBrokerConfigurer configurer : this.configurers) {
            configurer.registerStompEndpoints(registry);
        }
    }

    @Override
    protected void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        for (WebSocketMessageBrokerConfigurer configurer : this.configurers) {
            configurer.configureWebSocketTransport(registration);
        }
    }

    protected void configureClientInboundChannel(ChannelRegistration registration) {
        for (WebSocketMessageBrokerConfigurer configurer : this.configurers) {
            configurer.configureClientInboundChannel(registration);
        }
    }

    protected void configureClientOutboundChannel(ChannelRegistration registration) {
        for (WebSocketMessageBrokerConfigurer configurer : this.configurers) {
            configurer.configureClientOutboundChannel(registration);
        }
    }

    protected void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        for (WebSocketMessageBrokerConfigurer configurer : this.configurers) {
            configurer.addArgumentResolvers(argumentResolvers);
        }
    }

    protected void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
        for (WebSocketMessageBrokerConfigurer configurer : this.configurers) {
            configurer.addReturnValueHandlers(returnValueHandlers);
        }
    }

    protected boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        boolean registerDefaults = true;
        for (WebSocketMessageBrokerConfigurer configurer : this.configurers) {
            if (configurer.configureMessageConverters(messageConverters)) continue;
            registerDefaults = false;
        }
        return registerDefaults;
    }

    protected void configureMessageBroker(MessageBrokerRegistry registry) {
        for (WebSocketMessageBrokerConfigurer configurer : this.configurers) {
            configurer.configureMessageBroker(registry);
        }
    }
}


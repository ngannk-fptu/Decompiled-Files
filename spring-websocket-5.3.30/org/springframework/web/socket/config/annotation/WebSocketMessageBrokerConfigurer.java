/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.messaging.converter.MessageConverter
 *  org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver
 *  org.springframework.messaging.handler.invocation.HandlerMethodReturnValueHandler
 *  org.springframework.messaging.simp.config.ChannelRegistration
 *  org.springframework.messaging.simp.config.MessageBrokerRegistry
 */
package org.springframework.web.socket.config.annotation;

import java.util.List;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.handler.invocation.HandlerMethodReturnValueHandler;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

public interface WebSocketMessageBrokerConfigurer {
    default public void registerStompEndpoints(StompEndpointRegistry registry) {
    }

    default public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
    }

    default public void configureClientInboundChannel(ChannelRegistration registration) {
    }

    default public void configureClientOutboundChannel(ChannelRegistration registration) {
    }

    default public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
    }

    default public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
    }

    default public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        return true;
    }

    default public void configureMessageBroker(MessageBrokerRegistry registry) {
    }
}


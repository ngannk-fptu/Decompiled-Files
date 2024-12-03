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
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

@Deprecated
public abstract class AbstractWebSocketMessageBrokerConfigurer
implements WebSocketMessageBrokerConfigurer {
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
    }

    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        return true;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
    }

    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
    }
}


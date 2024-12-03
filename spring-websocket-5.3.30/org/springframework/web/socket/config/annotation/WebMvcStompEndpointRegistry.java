/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ApplicationEventPublisher
 *  org.springframework.lang.Nullable
 *  org.springframework.scheduling.TaskScheduler
 *  org.springframework.util.Assert
 *  org.springframework.util.MultiValueMap
 *  org.springframework.web.HttpRequestHandler
 *  org.springframework.web.servlet.handler.AbstractHandlerMapping
 *  org.springframework.web.util.UrlPathHelper
 */
package org.springframework.web.socket.config.annotation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.StompWebSocketEndpointRegistration;
import org.springframework.web.socket.config.annotation.WebMvcStompWebSocketEndpointRegistration;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;
import org.springframework.web.socket.messaging.StompSubProtocolHandler;
import org.springframework.web.socket.messaging.SubProtocolWebSocketHandler;
import org.springframework.web.socket.server.support.WebSocketHandlerMapping;
import org.springframework.web.util.UrlPathHelper;

public class WebMvcStompEndpointRegistry
implements StompEndpointRegistry {
    private final WebSocketHandler webSocketHandler;
    private final TaskScheduler sockJsScheduler;
    private int order = 1;
    @Nullable
    private UrlPathHelper urlPathHelper;
    private final SubProtocolWebSocketHandler subProtocolWebSocketHandler;
    private final StompSubProtocolHandler stompHandler;
    private final List<WebMvcStompWebSocketEndpointRegistration> registrations = new ArrayList<WebMvcStompWebSocketEndpointRegistration>();

    public WebMvcStompEndpointRegistry(WebSocketHandler webSocketHandler, WebSocketTransportRegistration transportRegistration, TaskScheduler defaultSockJsTaskScheduler) {
        Assert.notNull((Object)webSocketHandler, (String)"WebSocketHandler is required ");
        Assert.notNull((Object)transportRegistration, (String)"WebSocketTransportRegistration is required");
        this.webSocketHandler = webSocketHandler;
        this.subProtocolWebSocketHandler = WebMvcStompEndpointRegistry.unwrapSubProtocolWebSocketHandler(webSocketHandler);
        if (transportRegistration.getSendTimeLimit() != null) {
            this.subProtocolWebSocketHandler.setSendTimeLimit(transportRegistration.getSendTimeLimit());
        }
        if (transportRegistration.getSendBufferSizeLimit() != null) {
            this.subProtocolWebSocketHandler.setSendBufferSizeLimit(transportRegistration.getSendBufferSizeLimit());
        }
        if (transportRegistration.getTimeToFirstMessage() != null) {
            this.subProtocolWebSocketHandler.setTimeToFirstMessage(transportRegistration.getTimeToFirstMessage());
        }
        this.stompHandler = new StompSubProtocolHandler();
        if (transportRegistration.getMessageSizeLimit() != null) {
            this.stompHandler.setMessageSizeLimit(transportRegistration.getMessageSizeLimit());
        }
        this.sockJsScheduler = defaultSockJsTaskScheduler;
    }

    private static SubProtocolWebSocketHandler unwrapSubProtocolWebSocketHandler(WebSocketHandler handler) {
        WebSocketHandler actual = WebSocketHandlerDecorator.unwrap(handler);
        if (!(actual instanceof SubProtocolWebSocketHandler)) {
            throw new IllegalArgumentException("No SubProtocolWebSocketHandler in " + handler);
        }
        return (SubProtocolWebSocketHandler)actual;
    }

    @Override
    public StompWebSocketEndpointRegistration addEndpoint(String ... paths) {
        this.subProtocolWebSocketHandler.addProtocolHandler(this.stompHandler);
        WebMvcStompWebSocketEndpointRegistration registration = new WebMvcStompWebSocketEndpointRegistration(paths, this.webSocketHandler, this.sockJsScheduler);
        this.registrations.add(registration);
        return registration;
    }

    @Override
    public void setOrder(int order) {
        this.order = order;
    }

    protected int getOrder() {
        return this.order;
    }

    @Override
    public void setUrlPathHelper(@Nullable UrlPathHelper urlPathHelper) {
        this.urlPathHelper = urlPathHelper;
    }

    @Nullable
    protected UrlPathHelper getUrlPathHelper() {
        return this.urlPathHelper;
    }

    @Override
    public WebMvcStompEndpointRegistry setErrorHandler(StompSubProtocolErrorHandler errorHandler) {
        this.stompHandler.setErrorHandler(errorHandler);
        return this;
    }

    protected void setApplicationContext(ApplicationContext applicationContext) {
        this.stompHandler.setApplicationEventPublisher((ApplicationEventPublisher)applicationContext);
    }

    public AbstractHandlerMapping getHandlerMapping() {
        LinkedHashMap urlMap = new LinkedHashMap();
        for (WebMvcStompWebSocketEndpointRegistration registration : this.registrations) {
            MultiValueMap<HttpRequestHandler, String> mappings = registration.getMappings();
            mappings.forEach((httpHandler, patterns) -> {
                for (String pattern : patterns) {
                    urlMap.put(pattern, httpHandler);
                }
            });
        }
        WebSocketHandlerMapping hm = new WebSocketHandlerMapping();
        hm.setUrlMap(urlMap);
        hm.setOrder(this.order);
        if (this.urlPathHelper != null) {
            hm.setUrlPathHelper(this.urlPathHelper);
        }
        return hm;
    }
}


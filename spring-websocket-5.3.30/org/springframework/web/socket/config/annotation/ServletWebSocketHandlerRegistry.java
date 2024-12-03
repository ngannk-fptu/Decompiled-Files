/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.scheduling.TaskScheduler
 *  org.springframework.util.MultiValueMap
 *  org.springframework.web.servlet.handler.AbstractHandlerMapping
 *  org.springframework.web.util.UrlPathHelper
 */
package org.springframework.web.socket.config.annotation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.AbstractWebSocketHandlerRegistration;
import org.springframework.web.socket.config.annotation.ServletWebSocketHandlerRegistration;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistration;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.WebSocketHandlerMapping;
import org.springframework.web.util.UrlPathHelper;

public class ServletWebSocketHandlerRegistry
implements WebSocketHandlerRegistry {
    private final List<ServletWebSocketHandlerRegistration> registrations = new ArrayList<ServletWebSocketHandlerRegistration>(4);
    private int order = 1;
    @Nullable
    private UrlPathHelper urlPathHelper;

    @Override
    public WebSocketHandlerRegistration addHandler(WebSocketHandler handler, String ... paths) {
        ServletWebSocketHandlerRegistration registration = new ServletWebSocketHandlerRegistration();
        registration.addHandler(handler, paths);
        this.registrations.add(registration);
        return registration;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return this.order;
    }

    public void setUrlPathHelper(@Nullable UrlPathHelper urlPathHelper) {
        this.urlPathHelper = urlPathHelper;
    }

    @Nullable
    public UrlPathHelper getUrlPathHelper() {
        return this.urlPathHelper;
    }

    protected boolean requiresTaskScheduler() {
        return this.registrations.stream().anyMatch(r -> r.getSockJsServiceRegistration() != null && r.getSockJsServiceRegistration().getTaskScheduler() == null);
    }

    protected void setTaskScheduler(TaskScheduler scheduler) {
        this.registrations.stream().map(AbstractWebSocketHandlerRegistration::getSockJsServiceRegistration).filter(Objects::nonNull).filter(r -> r.getTaskScheduler() == null).forEach(registration -> registration.setTaskScheduler(scheduler));
    }

    public AbstractHandlerMapping getHandlerMapping() {
        LinkedHashMap urlMap = new LinkedHashMap();
        for (ServletWebSocketHandlerRegistration registration : this.registrations) {
            MultiValueMap mappings = (MultiValueMap)registration.getMappings();
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


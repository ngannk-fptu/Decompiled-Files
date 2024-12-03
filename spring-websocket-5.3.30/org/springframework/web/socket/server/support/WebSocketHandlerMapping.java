/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.http.HttpServletRequest
 *  org.springframework.context.Lifecycle
 *  org.springframework.context.SmartLifecycle
 *  org.springframework.lang.Nullable
 *  org.springframework.web.context.ServletContextAware
 *  org.springframework.web.servlet.HandlerExecutionChain
 *  org.springframework.web.servlet.handler.SimpleUrlHandlerMapping
 */
package org.springframework.web.socket.server.support;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.springframework.context.Lifecycle;
import org.springframework.context.SmartLifecycle;
import org.springframework.lang.Nullable;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.socket.server.support.WebSocketHttpRequestHandler;

public class WebSocketHandlerMapping
extends SimpleUrlHandlerMapping
implements SmartLifecycle {
    private boolean webSocketUpgradeMatch;
    private volatile boolean running;

    public void setWebSocketUpgradeMatch(boolean match) {
        this.webSocketUpgradeMatch = match;
    }

    protected void initServletContext(ServletContext servletContext) {
        for (Object handler : this.getUrlMap().values()) {
            if (!(handler instanceof ServletContextAware)) continue;
            ((ServletContextAware)handler).setServletContext(servletContext);
        }
    }

    public void start() {
        if (!this.isRunning()) {
            this.running = true;
            for (Object handler : this.getUrlMap().values()) {
                if (!(handler instanceof Lifecycle)) continue;
                ((Lifecycle)handler).start();
            }
        }
    }

    public void stop() {
        if (this.isRunning()) {
            this.running = false;
            for (Object handler : this.getUrlMap().values()) {
                if (!(handler instanceof Lifecycle)) continue;
                ((Lifecycle)handler).stop();
            }
        }
    }

    public boolean isRunning() {
        return this.running;
    }

    @Nullable
    protected Object getHandlerInternal(HttpServletRequest request) throws Exception {
        Object handler = super.getHandlerInternal(request);
        return this.matchWebSocketUpgrade(handler, request) ? handler : null;
    }

    private boolean matchWebSocketUpgrade(@Nullable Object handler, HttpServletRequest request) {
        Object object = handler = handler instanceof HandlerExecutionChain ? ((HandlerExecutionChain)handler).getHandler() : handler;
        if (this.webSocketUpgradeMatch && handler instanceof WebSocketHttpRequestHandler) {
            String header = request.getHeader("Upgrade");
            return request.getMethod().equals("GET") && header != null && header.equalsIgnoreCase("websocket");
        }
        return true;
    }
}


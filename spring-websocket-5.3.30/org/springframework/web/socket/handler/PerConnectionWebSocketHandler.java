/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 */
package org.springframework.web.socket.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BeanCreatingHandlerProvider;

public class PerConnectionWebSocketHandler
implements WebSocketHandler,
BeanFactoryAware {
    private static final Log logger = LogFactory.getLog(PerConnectionWebSocketHandler.class);
    private final BeanCreatingHandlerProvider<WebSocketHandler> provider;
    private final Map<WebSocketSession, WebSocketHandler> handlers = new ConcurrentHashMap<WebSocketSession, WebSocketHandler>();
    private final boolean supportsPartialMessages;

    public PerConnectionWebSocketHandler(Class<? extends WebSocketHandler> handlerType) {
        this(handlerType, false);
    }

    public PerConnectionWebSocketHandler(Class<? extends WebSocketHandler> handlerType, boolean supportsPartialMessages) {
        this.provider = new BeanCreatingHandlerProvider<WebSocketHandler>(handlerType);
        this.supportsPartialMessages = supportsPartialMessages;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.provider.setBeanFactory(beanFactory);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        WebSocketHandler handler = this.provider.getHandler();
        this.handlers.put(session, handler);
        handler.afterConnectionEstablished(session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        this.getHandler(session).handleMessage(session, message);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        this.getHandler(session).handleTransportError(session, exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        try {
            this.getHandler(session).afterConnectionClosed(session, closeStatus);
        }
        finally {
            this.destroyHandler(session);
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        return this.supportsPartialMessages;
    }

    private WebSocketHandler getHandler(WebSocketSession session) {
        WebSocketHandler handler = this.handlers.get(session);
        if (handler == null) {
            throw new IllegalStateException("WebSocketHandler not found for " + session);
        }
        return handler;
    }

    private void destroyHandler(WebSocketSession session) {
        block3: {
            WebSocketHandler handler = this.handlers.remove(session);
            try {
                if (handler != null) {
                    this.provider.destroy(handler);
                }
            }
            catch (Throwable ex) {
                if (!logger.isWarnEnabled()) break block3;
                logger.warn((Object)("Error while destroying " + handler), ex);
            }
        }
    }

    public String toString() {
        return "PerConnectionWebSocketHandlerProxy[handlerType=" + this.provider.getHandlerType() + "]";
    }
}


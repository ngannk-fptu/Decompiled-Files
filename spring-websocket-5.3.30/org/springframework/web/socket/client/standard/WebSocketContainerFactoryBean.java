/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.websocket.ContainerProvider
 *  javax.websocket.WebSocketContainer
 *  org.springframework.beans.factory.FactoryBean
 */
package org.springframework.web.socket.client.standard;

import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;
import org.springframework.beans.factory.FactoryBean;

public class WebSocketContainerFactoryBean
implements FactoryBean<WebSocketContainer> {
    private final WebSocketContainer webSocketContainer = ContainerProvider.getWebSocketContainer();

    public void setAsyncSendTimeout(long timeoutInMillis) {
        this.webSocketContainer.setAsyncSendTimeout(timeoutInMillis);
    }

    public long getAsyncSendTimeout() {
        return this.webSocketContainer.getDefaultAsyncSendTimeout();
    }

    public void setMaxSessionIdleTimeout(long timeoutInMillis) {
        this.webSocketContainer.setDefaultMaxSessionIdleTimeout(timeoutInMillis);
    }

    public long getMaxSessionIdleTimeout() {
        return this.webSocketContainer.getDefaultMaxSessionIdleTimeout();
    }

    public void setMaxTextMessageBufferSize(int bufferSize) {
        this.webSocketContainer.setDefaultMaxTextMessageBufferSize(bufferSize);
    }

    public int getMaxTextMessageBufferSize() {
        return this.webSocketContainer.getDefaultMaxTextMessageBufferSize();
    }

    public void setMaxBinaryMessageBufferSize(int bufferSize) {
        this.webSocketContainer.setDefaultMaxBinaryMessageBufferSize(bufferSize);
    }

    public int getMaxBinaryMessageBufferSize() {
        return this.webSocketContainer.getDefaultMaxBinaryMessageBufferSize();
    }

    public WebSocketContainer getObject() throws Exception {
        return this.webSocketContainer;
    }

    public Class<?> getObjectType() {
        return WebSocketContainer.class;
    }

    public boolean isSingleton() {
        return true;
    }
}


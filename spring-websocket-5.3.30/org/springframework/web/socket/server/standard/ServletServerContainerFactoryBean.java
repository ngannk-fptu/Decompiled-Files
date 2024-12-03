/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.websocket.WebSocketContainer
 *  javax.websocket.server.ServerContainer
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.web.context.ServletContextAware
 */
package org.springframework.web.socket.server.standard;

import javax.servlet.ServletContext;
import javax.websocket.WebSocketContainer;
import javax.websocket.server.ServerContainer;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.context.ServletContextAware;

public class ServletServerContainerFactoryBean
implements FactoryBean<WebSocketContainer>,
ServletContextAware,
InitializingBean {
    @Nullable
    private Long asyncSendTimeout;
    @Nullable
    private Long maxSessionIdleTimeout;
    @Nullable
    private Integer maxTextMessageBufferSize;
    @Nullable
    private Integer maxBinaryMessageBufferSize;
    @Nullable
    private ServletContext servletContext;
    @Nullable
    private ServerContainer serverContainer;

    public void setAsyncSendTimeout(Long timeoutInMillis) {
        this.asyncSendTimeout = timeoutInMillis;
    }

    @Nullable
    public Long getAsyncSendTimeout() {
        return this.asyncSendTimeout;
    }

    public void setMaxSessionIdleTimeout(Long timeoutInMillis) {
        this.maxSessionIdleTimeout = timeoutInMillis;
    }

    @Nullable
    public Long getMaxSessionIdleTimeout() {
        return this.maxSessionIdleTimeout;
    }

    public void setMaxTextMessageBufferSize(Integer bufferSize) {
        this.maxTextMessageBufferSize = bufferSize;
    }

    @Nullable
    public Integer getMaxTextMessageBufferSize() {
        return this.maxTextMessageBufferSize;
    }

    public void setMaxBinaryMessageBufferSize(Integer bufferSize) {
        this.maxBinaryMessageBufferSize = bufferSize;
    }

    @Nullable
    public Integer getMaxBinaryMessageBufferSize() {
        return this.maxBinaryMessageBufferSize;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public void afterPropertiesSet() {
        Assert.state((this.servletContext != null ? 1 : 0) != 0, (String)"A ServletContext is required to access the javax.websocket.server.ServerContainer instance");
        this.serverContainer = (ServerContainer)this.servletContext.getAttribute("javax.websocket.server.ServerContainer");
        Assert.state((this.serverContainer != null ? 1 : 0) != 0, (String)"Attribute 'javax.websocket.server.ServerContainer' not found in ServletContext");
        if (this.asyncSendTimeout != null) {
            this.serverContainer.setAsyncSendTimeout(this.asyncSendTimeout.longValue());
        }
        if (this.maxSessionIdleTimeout != null) {
            this.serverContainer.setDefaultMaxSessionIdleTimeout(this.maxSessionIdleTimeout.longValue());
        }
        if (this.maxTextMessageBufferSize != null) {
            this.serverContainer.setDefaultMaxTextMessageBufferSize(this.maxTextMessageBufferSize.intValue());
        }
        if (this.maxBinaryMessageBufferSize != null) {
            this.serverContainer.setDefaultMaxBinaryMessageBufferSize(this.maxBinaryMessageBufferSize.intValue());
        }
    }

    @Nullable
    public ServerContainer getObject() {
        return this.serverContainer;
    }

    public Class<?> getObjectType() {
        return this.serverContainer != null ? this.serverContainer.getClass() : ServerContainer.class;
    }

    public boolean isSingleton() {
        return true;
    }
}


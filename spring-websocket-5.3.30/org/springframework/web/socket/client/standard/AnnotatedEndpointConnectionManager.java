/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.websocket.ContainerProvider
 *  javax.websocket.Session
 *  javax.websocket.WebSocketContainer
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.core.task.SimpleAsyncTaskExecutor
 *  org.springframework.core.task.TaskExecutor
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.web.socket.client.standard;

import javax.websocket.ContainerProvider;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.socket.client.ConnectionManagerSupport;
import org.springframework.web.socket.handler.BeanCreatingHandlerProvider;

public class AnnotatedEndpointConnectionManager
extends ConnectionManagerSupport
implements BeanFactoryAware {
    @Nullable
    private final Object endpoint;
    @Nullable
    private final BeanCreatingHandlerProvider<Object> endpointProvider;
    private WebSocketContainer webSocketContainer = ContainerProvider.getWebSocketContainer();
    private TaskExecutor taskExecutor = new SimpleAsyncTaskExecutor("AnnotatedEndpointConnectionManager-");
    @Nullable
    private volatile Session session;

    public AnnotatedEndpointConnectionManager(Object endpoint, String uriTemplate, Object ... uriVariables) {
        super(uriTemplate, uriVariables);
        this.endpoint = endpoint;
        this.endpointProvider = null;
    }

    public AnnotatedEndpointConnectionManager(Class<?> endpointClass, String uriTemplate, Object ... uriVariables) {
        super(uriTemplate, uriVariables);
        this.endpoint = null;
        this.endpointProvider = new BeanCreatingHandlerProvider(endpointClass);
    }

    public void setWebSocketContainer(WebSocketContainer webSocketContainer) {
        this.webSocketContainer = webSocketContainer;
    }

    public WebSocketContainer getWebSocketContainer() {
        return this.webSocketContainer;
    }

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (this.endpointProvider != null) {
            this.endpointProvider.setBeanFactory(beanFactory);
        }
    }

    public void setTaskExecutor(TaskExecutor taskExecutor) {
        Assert.notNull((Object)taskExecutor, (String)"TaskExecutor must not be null");
        this.taskExecutor = taskExecutor;
    }

    public TaskExecutor getTaskExecutor() {
        return this.taskExecutor;
    }

    @Override
    public boolean isConnected() {
        Session session = this.session;
        return session != null && session.isOpen();
    }

    @Override
    protected void openConnection() {
        this.taskExecutor.execute(() -> {
            try {
                Object endpointToUse;
                if (this.logger.isInfoEnabled()) {
                    this.logger.info((Object)("Connecting to WebSocket at " + this.getUri()));
                }
                if ((endpointToUse = this.endpoint) == null) {
                    Assert.state((this.endpointProvider != null ? 1 : 0) != 0, (String)"No endpoint set");
                    endpointToUse = this.endpointProvider.getHandler();
                }
                this.session = this.webSocketContainer.connectToServer(endpointToUse, this.getUri());
                this.logger.info((Object)"Successfully connected to WebSocket");
            }
            catch (Throwable ex) {
                this.logger.error((Object)"Failed to connect to WebSocket", ex);
            }
        });
    }

    @Override
    protected void closeConnection() throws Exception {
        try {
            Session session = this.session;
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        finally {
            this.session = null;
        }
    }
}


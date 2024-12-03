/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.websocket.ClientEndpointConfig
 *  javax.websocket.ClientEndpointConfig$Builder
 *  javax.websocket.ClientEndpointConfig$Configurator
 *  javax.websocket.ContainerProvider
 *  javax.websocket.Decoder
 *  javax.websocket.Encoder
 *  javax.websocket.Endpoint
 *  javax.websocket.Extension
 *  javax.websocket.Session
 *  javax.websocket.WebSocketContainer
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.core.task.SimpleAsyncTaskExecutor
 *  org.springframework.core.task.TaskExecutor
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.web.socket.client.standard;

import java.util.Arrays;
import java.util.List;
import javax.websocket.ClientEndpointConfig;
import javax.websocket.ContainerProvider;
import javax.websocket.Decoder;
import javax.websocket.Encoder;
import javax.websocket.Endpoint;
import javax.websocket.Extension;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.socket.client.ConnectionManagerSupport;
import org.springframework.web.socket.handler.BeanCreatingHandlerProvider;

public class EndpointConnectionManager
extends ConnectionManagerSupport
implements BeanFactoryAware {
    @Nullable
    private final Endpoint endpoint;
    @Nullable
    private final BeanCreatingHandlerProvider<Endpoint> endpointProvider;
    private final ClientEndpointConfig.Builder configBuilder = ClientEndpointConfig.Builder.create();
    private WebSocketContainer webSocketContainer = ContainerProvider.getWebSocketContainer();
    private TaskExecutor taskExecutor = new SimpleAsyncTaskExecutor("EndpointConnectionManager-");
    @Nullable
    private volatile Session session;

    public EndpointConnectionManager(Endpoint endpoint, String uriTemplate, Object ... uriVariables) {
        super(uriTemplate, uriVariables);
        Assert.notNull((Object)endpoint, (String)"endpoint must not be null");
        this.endpoint = endpoint;
        this.endpointProvider = null;
    }

    public EndpointConnectionManager(Class<? extends Endpoint> endpointClass, String uriTemplate, Object ... uriVars) {
        super(uriTemplate, uriVars);
        Assert.notNull(endpointClass, (String)"endpointClass must not be null");
        this.endpoint = null;
        this.endpointProvider = new BeanCreatingHandlerProvider<Endpoint>(endpointClass);
    }

    public void setSupportedProtocols(String ... protocols) {
        this.configBuilder.preferredSubprotocols(Arrays.asList(protocols));
    }

    public void setExtensions(Extension ... extensions) {
        this.configBuilder.extensions(Arrays.asList(extensions));
    }

    public void setEncoders(List<Class<? extends Encoder>> encoders) {
        this.configBuilder.encoders(encoders);
    }

    public void setDecoders(List<Class<? extends Decoder>> decoders) {
        this.configBuilder.decoders(decoders);
    }

    public void setConfigurator(ClientEndpointConfig.Configurator configurator) {
        this.configBuilder.configurator(configurator);
    }

    public void setWebSocketContainer(WebSocketContainer webSocketContainer) {
        this.webSocketContainer = webSocketContainer;
    }

    public WebSocketContainer getWebSocketContainer() {
        return this.webSocketContainer;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
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
                Endpoint endpointToUse;
                if (this.logger.isInfoEnabled()) {
                    this.logger.info((Object)("Connecting to WebSocket at " + this.getUri()));
                }
                if ((endpointToUse = this.endpoint) == null) {
                    Assert.state((this.endpointProvider != null ? 1 : 0) != 0, (String)"No endpoint set");
                    endpointToUse = this.endpointProvider.getHandler();
                }
                ClientEndpointConfig endpointConfig = this.configBuilder.build();
                this.session = this.getWebSocketContainer().connectToServer(endpointToUse, endpointConfig, this.getUri());
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


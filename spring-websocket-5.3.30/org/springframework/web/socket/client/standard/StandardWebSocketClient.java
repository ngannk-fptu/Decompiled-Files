/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.websocket.ClientEndpointConfig
 *  javax.websocket.ClientEndpointConfig$Builder
 *  javax.websocket.ClientEndpointConfig$Configurator
 *  javax.websocket.ContainerProvider
 *  javax.websocket.Extension
 *  javax.websocket.HandshakeResponse
 *  javax.websocket.WebSocketContainer
 *  org.springframework.core.task.AsyncListenableTaskExecutor
 *  org.springframework.core.task.SimpleAsyncTaskExecutor
 *  org.springframework.http.HttpHeaders
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.concurrent.ListenableFuture
 *  org.springframework.util.concurrent.ListenableFutureTask
 */
package org.springframework.web.socket.client.standard;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import javax.websocket.ClientEndpointConfig;
import javax.websocket.ContainerProvider;
import javax.websocket.Extension;
import javax.websocket.HandshakeResponse;
import javax.websocket.WebSocketContainer;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureTask;
import org.springframework.web.socket.WebSocketExtension;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.adapter.standard.StandardWebSocketHandlerAdapter;
import org.springframework.web.socket.adapter.standard.StandardWebSocketSession;
import org.springframework.web.socket.adapter.standard.WebSocketToStandardExtensionAdapter;
import org.springframework.web.socket.client.AbstractWebSocketClient;

public class StandardWebSocketClient
extends AbstractWebSocketClient {
    private final WebSocketContainer webSocketContainer;
    private final Map<String, Object> userProperties = new HashMap<String, Object>();
    @Nullable
    private AsyncListenableTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();

    public StandardWebSocketClient() {
        this.webSocketContainer = ContainerProvider.getWebSocketContainer();
    }

    public StandardWebSocketClient(WebSocketContainer webSocketContainer) {
        Assert.notNull((Object)webSocketContainer, (String)"WebSocketContainer must not be null");
        this.webSocketContainer = webSocketContainer;
    }

    public void setUserProperties(@Nullable Map<String, Object> userProperties) {
        if (userProperties != null) {
            this.userProperties.putAll(userProperties);
        }
    }

    public Map<String, Object> getUserProperties() {
        return this.userProperties;
    }

    public void setTaskExecutor(@Nullable AsyncListenableTaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    @Nullable
    public AsyncListenableTaskExecutor getTaskExecutor() {
        return this.taskExecutor;
    }

    @Override
    protected ListenableFuture<WebSocketSession> doHandshakeInternal(WebSocketHandler webSocketHandler, HttpHeaders headers, URI uri, List<String> protocols, List<WebSocketExtension> extensions, Map<String, Object> attributes) {
        int port = this.getPort(uri);
        InetSocketAddress localAddress = new InetSocketAddress(this.getLocalHost(), port);
        InetSocketAddress remoteAddress = new InetSocketAddress(uri.getHost(), port);
        StandardWebSocketSession session = new StandardWebSocketSession(headers, attributes, localAddress, remoteAddress);
        ClientEndpointConfig endpointConfig = ClientEndpointConfig.Builder.create().configurator((ClientEndpointConfig.Configurator)new StandardWebSocketClientConfigurator(headers)).preferredSubprotocols(protocols).extensions(StandardWebSocketClient.adaptExtensions(extensions)).build();
        endpointConfig.getUserProperties().putAll(this.getUserProperties());
        StandardWebSocketHandlerAdapter endpoint = new StandardWebSocketHandlerAdapter(webSocketHandler, session);
        Callable<WebSocketSession> connectTask = () -> {
            this.webSocketContainer.connectToServer(endpoint, endpointConfig, uri);
            return session;
        };
        if (this.taskExecutor != null) {
            return this.taskExecutor.submitListenable(connectTask);
        }
        ListenableFutureTask task = new ListenableFutureTask(connectTask);
        task.run();
        return task;
    }

    private static List<Extension> adaptExtensions(List<WebSocketExtension> extensions) {
        ArrayList<Extension> result = new ArrayList<Extension>();
        for (WebSocketExtension extension : extensions) {
            result.add(new WebSocketToStandardExtensionAdapter(extension));
        }
        return result;
    }

    private InetAddress getLocalHost() {
        try {
            return InetAddress.getLocalHost();
        }
        catch (UnknownHostException ex) {
            return InetAddress.getLoopbackAddress();
        }
    }

    private int getPort(URI uri) {
        if (uri.getPort() == -1) {
            String scheme = uri.getScheme().toLowerCase(Locale.ENGLISH);
            return "wss".equals(scheme) ? 443 : 80;
        }
        return uri.getPort();
    }

    private class StandardWebSocketClientConfigurator
    extends ClientEndpointConfig.Configurator {
        private final HttpHeaders headers;

        public StandardWebSocketClientConfigurator(HttpHeaders headers) {
            this.headers = headers;
        }

        public void beforeRequest(Map<String, List<String>> requestHeaders) {
            requestHeaders.putAll((Map<String, List<String>>)this.headers);
            if (StandardWebSocketClient.this.logger.isTraceEnabled()) {
                StandardWebSocketClient.this.logger.trace((Object)("Handshake request headers: " + requestHeaders));
            }
        }

        public void afterResponse(HandshakeResponse response) {
            if (StandardWebSocketClient.this.logger.isTraceEnabled()) {
                StandardWebSocketClient.this.logger.trace((Object)("Handshake response headers: " + response.getHeaders()));
            }
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.websocket.api.Session
 *  org.eclipse.jetty.websocket.api.extensions.ExtensionConfig
 *  org.eclipse.jetty.websocket.client.ClientUpgradeRequest
 *  org.eclipse.jetty.websocket.client.WebSocketClient
 *  org.springframework.context.Lifecycle
 *  org.springframework.core.task.AsyncListenableTaskExecutor
 *  org.springframework.core.task.SimpleAsyncTaskExecutor
 *  org.springframework.http.HttpHeaders
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.ReflectionUtils
 *  org.springframework.util.concurrent.ListenableFuture
 *  org.springframework.util.concurrent.ListenableFutureTask
 *  org.springframework.web.util.UriComponents
 *  org.springframework.web.util.UriComponentsBuilder
 */
package org.springframework.web.socket.client.jetty;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.extensions.ExtensionConfig;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.springframework.context.Lifecycle;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureTask;
import org.springframework.web.socket.WebSocketExtension;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.adapter.jetty.Jetty10WebSocketHandlerAdapter;
import org.springframework.web.socket.adapter.jetty.JettyWebSocketHandlerAdapter;
import org.springframework.web.socket.adapter.jetty.JettyWebSocketSession;
import org.springframework.web.socket.adapter.jetty.WebSocketToJettyExtensionConfigAdapter;
import org.springframework.web.socket.client.AbstractWebSocketClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public class JettyWebSocketClient
extends AbstractWebSocketClient
implements Lifecycle {
    private static ClassLoader loader = JettyWebSocketClient.class.getClassLoader();
    private static final boolean jetty10Present = ClassUtils.isPresent((String)"org.eclipse.jetty.websocket.client.JettyUpgradeListener", (ClassLoader)loader);
    private static final Method setHeadersMethod;
    private final WebSocketClient client;
    @Nullable
    private AsyncListenableTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
    private final UpgradeHelper upgradeHelper = jetty10Present ? new Jetty10UpgradeHelper() : new Jetty9UpgradeHelper();

    public JettyWebSocketClient() {
        this.client = new WebSocketClient();
    }

    public JettyWebSocketClient(WebSocketClient client) {
        this.client = client;
    }

    public void setTaskExecutor(@Nullable AsyncListenableTaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    @Nullable
    public AsyncListenableTaskExecutor getTaskExecutor() {
        return this.taskExecutor;
    }

    public void start() {
        try {
            this.client.start();
        }
        catch (Exception ex) {
            throw new IllegalStateException("Failed to start Jetty WebSocketClient", ex);
        }
    }

    public void stop() {
        try {
            this.client.stop();
        }
        catch (Exception ex) {
            this.logger.error((Object)"Failed to stop Jetty WebSocketClient", (Throwable)ex);
        }
    }

    public boolean isRunning() {
        return this.client.isStarted();
    }

    @Override
    public ListenableFuture<WebSocketSession> doHandshake(WebSocketHandler webSocketHandler, String uriTemplate, Object ... uriVars) {
        UriComponents uriComponents = UriComponentsBuilder.fromUriString((String)uriTemplate).buildAndExpand(uriVars).encode();
        return this.doHandshake(webSocketHandler, null, uriComponents.toUri());
    }

    @Override
    public ListenableFuture<WebSocketSession> doHandshakeInternal(WebSocketHandler wsHandler, HttpHeaders headers, URI uri, List<String> protocols, List<WebSocketExtension> extensions, Map<String, Object> attributes) {
        ClientUpgradeRequest request = new ClientUpgradeRequest();
        request.setSubProtocols(protocols);
        for (WebSocketExtension e : extensions) {
            request.addExtensions(new ExtensionConfig[]{new WebSocketToJettyExtensionConfigAdapter(e)});
        }
        ReflectionUtils.invokeMethod((Method)setHeadersMethod, (Object)request, (Object[])new Object[]{headers});
        Principal user = this.getUser();
        JettyWebSocketSession wsSession = new JettyWebSocketSession(attributes, user);
        Callable<WebSocketSession> connectTask = () -> {
            Future<Session> future = this.upgradeHelper.connect(this.client, uri, request, wsHandler, wsSession);
            future.get(this.client.getConnectTimeout() + 2000L, TimeUnit.MILLISECONDS);
            return wsSession;
        };
        if (this.taskExecutor != null) {
            return this.taskExecutor.submitListenable(connectTask);
        }
        ListenableFutureTask task = new ListenableFutureTask(connectTask);
        task.run();
        return task;
    }

    @Nullable
    protected Principal getUser() {
        return null;
    }

    static {
        try {
            setHeadersMethod = ClientUpgradeRequest.class.getMethod("setHeaders", Map.class);
        }
        catch (NoSuchMethodException ex) {
            throw new IllegalStateException("No compatible Jetty version found", ex);
        }
    }

    private static class Jetty10UpgradeHelper
    implements UpgradeHelper {
        private static final Method connectMethod;

        private Jetty10UpgradeHelper() {
        }

        @Override
        public Future<Session> connect(WebSocketClient client, URI url, ClientUpgradeRequest request, WebSocketHandler handler, JettyWebSocketSession session) {
            Jetty10WebSocketHandlerAdapter adapter = new Jetty10WebSocketHandlerAdapter(handler, session);
            return (Future)ReflectionUtils.invokeMethod((Method)connectMethod, (Object)client, (Object[])new Object[]{adapter, url, request});
        }

        static {
            try {
                Class<?> type = loader.loadClass("org.eclipse.jetty.websocket.client.WebSocketClient");
                connectMethod = type.getMethod("connect", Object.class, URI.class, ClientUpgradeRequest.class);
            }
            catch (ClassNotFoundException | NoSuchMethodException ex) {
                throw new IllegalStateException("No compatible Jetty version found", ex);
            }
        }
    }

    private static class Jetty9UpgradeHelper
    implements UpgradeHelper {
        private Jetty9UpgradeHelper() {
        }

        @Override
        public Future<Session> connect(WebSocketClient client, URI url, ClientUpgradeRequest request, WebSocketHandler handler, JettyWebSocketSession session) throws IOException {
            JettyWebSocketHandlerAdapter adapter = new JettyWebSocketHandlerAdapter(handler, session);
            return client.connect((Object)adapter, url, request);
        }
    }

    private static interface UpgradeHelper {
        public Future<Session> connect(WebSocketClient var1, URI var2, ClientUpgradeRequest var3, WebSocketHandler var4, JettyWebSocketSession var5) throws IOException;
    }
}


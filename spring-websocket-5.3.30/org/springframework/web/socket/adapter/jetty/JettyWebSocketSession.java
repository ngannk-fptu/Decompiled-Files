/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.websocket.api.RemoteEndpoint
 *  org.eclipse.jetty.websocket.api.Session
 *  org.eclipse.jetty.websocket.api.extensions.ExtensionConfig
 *  org.springframework.http.HttpHeaders
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.ReflectionUtils
 */
package org.springframework.web.socket.adapter.jetty;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.nio.ByteBuffer;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.extensions.ExtensionConfig;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PingMessage;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketExtension;
import org.springframework.web.socket.adapter.AbstractWebSocketSession;

public class JettyWebSocketSession
extends AbstractWebSocketSession<Session> {
    private static final ClassLoader loader = JettyWebSocketSession.class.getClassLoader();
    private static final boolean jetty10Present = ClassUtils.isPresent((String)"org.eclipse.jetty.websocket.server.JettyWebSocketServerContainer", (ClassLoader)loader);
    private final String id = idGenerator.generateId().toString();
    @Nullable
    private URI uri;
    @Nullable
    private HttpHeaders headers;
    @Nullable
    private String acceptedProtocol;
    @Nullable
    private List<WebSocketExtension> extensions;
    @Nullable
    private Principal user;
    private final SessionHelper sessionHelper;

    public JettyWebSocketSession(Map<String, Object> attributes) {
        this(attributes, null);
    }

    public JettyWebSocketSession(Map<String, Object> attributes, @Nullable Principal user) {
        super(attributes);
        this.user = user;
        this.sessionHelper = jetty10Present ? new Jetty10SessionHelper() : new Jetty9SessionHelper();
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    @Nullable
    public URI getUri() {
        this.checkNativeSessionInitialized();
        return this.uri;
    }

    @Override
    public HttpHeaders getHandshakeHeaders() {
        Assert.state((this.headers != null ? 1 : 0) != 0, (String)"WebSocket session is not yet initialized");
        return this.headers;
    }

    @Override
    public String getAcceptedProtocol() {
        this.checkNativeSessionInitialized();
        return this.acceptedProtocol;
    }

    @Override
    public List<WebSocketExtension> getExtensions() {
        Assert.state((this.extensions != null ? 1 : 0) != 0, (String)"WebSocket session is not yet initialized");
        return this.extensions;
    }

    @Override
    public Principal getPrincipal() {
        return this.user;
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        this.checkNativeSessionInitialized();
        return this.sessionHelper.getLocalAddress((Session)this.getNativeSession());
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        this.checkNativeSessionInitialized();
        return this.sessionHelper.getRemoteAddress((Session)this.getNativeSession());
    }

    @Override
    public void setTextMessageSizeLimit(int messageSizeLimit) {
    }

    @Override
    public int getTextMessageSizeLimit() {
        this.checkNativeSessionInitialized();
        return this.sessionHelper.getTextMessageSizeLimit((Session)this.getNativeSession());
    }

    @Override
    public void setBinaryMessageSizeLimit(int messageSizeLimit) {
    }

    @Override
    public int getBinaryMessageSizeLimit() {
        this.checkNativeSessionInitialized();
        return this.sessionHelper.getBinaryMessageSizeLimit((Session)this.getNativeSession());
    }

    @Override
    public boolean isOpen() {
        return ((Session)this.getNativeSession()).isOpen();
    }

    @Override
    public void initializeNativeSession(Session session) {
        super.initializeNativeSession(session);
        this.uri = session.getUpgradeRequest().getRequestURI();
        HttpHeaders headers = new HttpHeaders();
        Map nativeHeaders = session.getUpgradeRequest().getHeaders();
        if (!CollectionUtils.isEmpty((Map)nativeHeaders)) {
            headers.putAll(nativeHeaders);
        }
        this.headers = HttpHeaders.readOnlyHttpHeaders((HttpHeaders)headers);
        this.acceptedProtocol = session.getUpgradeResponse().getAcceptedSubProtocol();
        this.extensions = this.sessionHelper.getExtensions(session);
        if (this.user == null) {
            this.user = session.getUpgradeRequest().getUserPrincipal();
        }
    }

    @Override
    protected void sendTextMessage(TextMessage message) throws IOException {
        this.getRemoteEndpoint().sendString((String)message.getPayload());
    }

    @Override
    protected void sendBinaryMessage(BinaryMessage message) throws IOException {
        this.getRemoteEndpoint().sendBytes((ByteBuffer)message.getPayload());
    }

    @Override
    protected void sendPingMessage(PingMessage message) throws IOException {
        this.getRemoteEndpoint().sendPing((ByteBuffer)message.getPayload());
    }

    @Override
    protected void sendPongMessage(PongMessage message) throws IOException {
        this.getRemoteEndpoint().sendPong((ByteBuffer)message.getPayload());
    }

    private RemoteEndpoint getRemoteEndpoint() {
        return ((Session)this.getNativeSession()).getRemote();
    }

    @Override
    protected void closeInternal(CloseStatus status) throws IOException {
        ((Session)this.getNativeSession()).close(status.getCode(), status.getReason());
    }

    private static class Jetty10SessionHelper
    implements SessionHelper {
        private static final Method getTextMessageSizeLimitMethod;
        private static final Method getBinaryMessageSizeLimitMethod;
        private static final Method getRemoteAddressMethod;
        private static final Method getLocalAddressMethod;

        private Jetty10SessionHelper() {
        }

        @Override
        public List<WebSocketExtension> getExtensions(Session session) {
            return Collections.emptyList();
        }

        @Override
        public int getTextMessageSizeLimit(Session session) {
            long result = (Long)ReflectionUtils.invokeMethod((Method)getTextMessageSizeLimitMethod, (Object)session.getPolicy());
            Assert.state((result <= Integer.MAX_VALUE ? 1 : 0) != 0, (String)"textMessageSizeLimit is larger than Integer.MAX_VALUE");
            return (int)result;
        }

        @Override
        public int getBinaryMessageSizeLimit(Session session) {
            long result = (Long)ReflectionUtils.invokeMethod((Method)getBinaryMessageSizeLimitMethod, (Object)session.getPolicy());
            Assert.state((result <= Integer.MAX_VALUE ? 1 : 0) != 0, (String)"binaryMessageSizeLimit is larger than Integer.MAX_VALUE");
            return (int)result;
        }

        @Override
        public InetSocketAddress getRemoteAddress(Session session) {
            SocketAddress address = (SocketAddress)ReflectionUtils.invokeMethod((Method)getRemoteAddressMethod, (Object)session);
            Assert.isInstanceOf(InetSocketAddress.class, (Object)address);
            return (InetSocketAddress)address;
        }

        @Override
        public InetSocketAddress getLocalAddress(Session session) {
            SocketAddress address = (SocketAddress)ReflectionUtils.invokeMethod((Method)getLocalAddressMethod, (Object)session);
            Assert.isInstanceOf(InetSocketAddress.class, (Object)address);
            return (InetSocketAddress)address;
        }

        static {
            try {
                Class<?> type = loader.loadClass("org.eclipse.jetty.websocket.api.Session");
                getTextMessageSizeLimitMethod = type.getMethod("getMaxTextMessageSize", new Class[0]);
                getBinaryMessageSizeLimitMethod = type.getMethod("getMaxBinaryMessageSize", new Class[0]);
                getRemoteAddressMethod = type.getMethod("getRemoteAddress", new Class[0]);
                getLocalAddressMethod = type.getMethod("getLocalAddress", new Class[0]);
            }
            catch (ClassNotFoundException | NoSuchMethodException ex) {
                throw new IllegalStateException("No compatible Jetty version found", ex);
            }
        }
    }

    private static class Jetty9SessionHelper
    implements SessionHelper {
        private Jetty9SessionHelper() {
        }

        @Override
        public List<WebSocketExtension> getExtensions(Session session) {
            List configs = session.getUpgradeResponse().getExtensions();
            if (!CollectionUtils.isEmpty((Collection)configs)) {
                ArrayList<WebSocketExtension> result = new ArrayList<WebSocketExtension>(configs.size());
                for (ExtensionConfig config : configs) {
                    result.add(new WebSocketExtension(config.getName(), config.getParameters()));
                }
                return Collections.unmodifiableList(result);
            }
            return Collections.emptyList();
        }

        @Override
        public int getTextMessageSizeLimit(Session session) {
            return session.getPolicy().getMaxTextMessageSize();
        }

        @Override
        public int getBinaryMessageSizeLimit(Session session) {
            return session.getPolicy().getMaxBinaryMessageSize();
        }

        @Override
        public InetSocketAddress getRemoteAddress(Session session) {
            return session.getRemoteAddress();
        }

        @Override
        public InetSocketAddress getLocalAddress(Session session) {
            return session.getLocalAddress();
        }
    }

    private static interface SessionHelper {
        public List<WebSocketExtension> getExtensions(Session var1);

        public int getTextMessageSizeLimit(Session var1);

        public int getBinaryMessageSizeLimit(Session var1);

        public InetSocketAddress getRemoteAddress(Session var1);

        public InetSocketAddress getLocalAddress(Session var1);
    }
}


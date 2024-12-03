/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.websocket.CloseReason
 *  javax.websocket.CloseReason$CloseCodes
 *  javax.websocket.Extension
 *  javax.websocket.Session
 *  org.springframework.http.HttpHeaders
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.CollectionUtils
 */
package org.springframework.web.socket.adapter.standard;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.ByteBuffer;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.websocket.CloseReason;
import javax.websocket.Extension;
import javax.websocket.Session;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PingMessage;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketExtension;
import org.springframework.web.socket.adapter.AbstractWebSocketSession;
import org.springframework.web.socket.adapter.standard.StandardToWebSocketExtensionAdapter;

public class StandardWebSocketSession
extends AbstractWebSocketSession<Session> {
    private final String id = idGenerator.generateId().toString();
    @Nullable
    private URI uri;
    private final HttpHeaders handshakeHeaders;
    @Nullable
    private String acceptedProtocol;
    @Nullable
    private List<WebSocketExtension> extensions;
    @Nullable
    private Principal user;
    @Nullable
    private final InetSocketAddress localAddress;
    @Nullable
    private final InetSocketAddress remoteAddress;

    public StandardWebSocketSession(@Nullable HttpHeaders headers, @Nullable Map<String, Object> attributes, @Nullable InetSocketAddress localAddress, @Nullable InetSocketAddress remoteAddress) {
        this(headers, attributes, localAddress, remoteAddress, null);
    }

    public StandardWebSocketSession(@Nullable HttpHeaders headers, @Nullable Map<String, Object> attributes, @Nullable InetSocketAddress localAddress, @Nullable InetSocketAddress remoteAddress, @Nullable Principal user) {
        super(attributes);
        headers = headers != null ? headers : new HttpHeaders();
        this.handshakeHeaders = HttpHeaders.readOnlyHttpHeaders((HttpHeaders)headers);
        this.user = user;
        this.localAddress = localAddress;
        this.remoteAddress = remoteAddress;
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
        return this.handshakeHeaders;
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
    @Nullable
    public InetSocketAddress getLocalAddress() {
        return this.localAddress;
    }

    @Override
    @Nullable
    public InetSocketAddress getRemoteAddress() {
        return this.remoteAddress;
    }

    @Override
    public void setTextMessageSizeLimit(int messageSizeLimit) {
        this.checkNativeSessionInitialized();
        ((Session)this.getNativeSession()).setMaxTextMessageBufferSize(messageSizeLimit);
    }

    @Override
    public int getTextMessageSizeLimit() {
        this.checkNativeSessionInitialized();
        return ((Session)this.getNativeSession()).getMaxTextMessageBufferSize();
    }

    @Override
    public void setBinaryMessageSizeLimit(int messageSizeLimit) {
        this.checkNativeSessionInitialized();
        ((Session)this.getNativeSession()).setMaxBinaryMessageBufferSize(messageSizeLimit);
    }

    @Override
    public int getBinaryMessageSizeLimit() {
        this.checkNativeSessionInitialized();
        return ((Session)this.getNativeSession()).getMaxBinaryMessageBufferSize();
    }

    @Override
    public boolean isOpen() {
        return ((Session)this.getNativeSession()).isOpen();
    }

    @Override
    public void initializeNativeSession(Session session) {
        super.initializeNativeSession(session);
        this.uri = session.getRequestURI();
        this.acceptedProtocol = session.getNegotiatedSubprotocol();
        List standardExtensions = ((Session)this.getNativeSession()).getNegotiatedExtensions();
        if (!CollectionUtils.isEmpty((Collection)standardExtensions)) {
            this.extensions = new ArrayList<WebSocketExtension>(standardExtensions.size());
            for (Extension standardExtension : standardExtensions) {
                this.extensions.add(new StandardToWebSocketExtensionAdapter(standardExtension));
            }
            this.extensions = Collections.unmodifiableList(this.extensions);
        } else {
            this.extensions = Collections.emptyList();
        }
        if (this.user == null) {
            this.user = session.getUserPrincipal();
        }
    }

    @Override
    protected void sendTextMessage(TextMessage message) throws IOException {
        ((Session)this.getNativeSession()).getBasicRemote().sendText((String)message.getPayload(), message.isLast());
    }

    @Override
    protected void sendBinaryMessage(BinaryMessage message) throws IOException {
        ((Session)this.getNativeSession()).getBasicRemote().sendBinary((ByteBuffer)message.getPayload(), message.isLast());
    }

    @Override
    protected void sendPingMessage(PingMessage message) throws IOException {
        ((Session)this.getNativeSession()).getBasicRemote().sendPing((ByteBuffer)message.getPayload());
    }

    @Override
    protected void sendPongMessage(PongMessage message) throws IOException {
        ((Session)this.getNativeSession()).getBasicRemote().sendPong((ByteBuffer)message.getPayload());
    }

    @Override
    protected void closeInternal(CloseStatus status) throws IOException {
        ((Session)this.getNativeSession()).close(new CloseReason(CloseReason.CloseCodes.getCloseCode((int)status.getCode()), status.getReason()));
    }
}


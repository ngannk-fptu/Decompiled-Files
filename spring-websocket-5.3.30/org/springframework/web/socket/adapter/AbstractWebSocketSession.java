/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.lang.Nullable
 *  org.springframework.util.AlternativeJdkIdGenerator
 *  org.springframework.util.Assert
 *  org.springframework.util.IdGenerator
 */
package org.springframework.web.socket.adapter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.AlternativeJdkIdGenerator;
import org.springframework.util.Assert;
import org.springframework.util.IdGenerator;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PingMessage;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.adapter.NativeWebSocketSession;

public abstract class AbstractWebSocketSession<T>
implements NativeWebSocketSession {
    protected static final IdGenerator idGenerator = new AlternativeJdkIdGenerator();
    protected static final Log logger = LogFactory.getLog(NativeWebSocketSession.class);
    private final Map<String, Object> attributes = new ConcurrentHashMap<String, Object>();
    @Nullable
    private T nativeSession;

    public AbstractWebSocketSession(@Nullable Map<String, Object> attributes) {
        if (attributes != null) {
            attributes.entrySet().stream().filter(entry -> entry.getKey() != null && entry.getValue() != null).forEach(entry -> this.attributes.put((String)entry.getKey(), entry.getValue()));
        }
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    public T getNativeSession() {
        Assert.state((this.nativeSession != null ? 1 : 0) != 0, (String)"WebSocket session not yet initialized");
        return this.nativeSession;
    }

    @Nullable
    public <R> R getNativeSession(@Nullable Class<R> requiredType) {
        return (R)(requiredType == null || requiredType.isInstance(this.nativeSession) ? this.nativeSession : null);
    }

    public void initializeNativeSession(T session) {
        Assert.notNull(session, (String)"WebSocket session must not be null");
        this.nativeSession = session;
    }

    protected final void checkNativeSessionInitialized() {
        Assert.state((this.nativeSession != null ? 1 : 0) != 0, (String)"WebSocket session is not yet initialized");
    }

    @Override
    public final void sendMessage(WebSocketMessage<?> message) throws IOException {
        this.checkNativeSessionInitialized();
        if (logger.isTraceEnabled()) {
            logger.trace((Object)("Sending " + message + ", " + this));
        }
        if (message instanceof TextMessage) {
            this.sendTextMessage((TextMessage)message);
        } else if (message instanceof BinaryMessage) {
            this.sendBinaryMessage((BinaryMessage)message);
        } else if (message instanceof PingMessage) {
            this.sendPingMessage((PingMessage)message);
        } else if (message instanceof PongMessage) {
            this.sendPongMessage((PongMessage)message);
        } else {
            throw new IllegalStateException("Unexpected WebSocketMessage type: " + message);
        }
    }

    protected abstract void sendTextMessage(TextMessage var1) throws IOException;

    protected abstract void sendBinaryMessage(BinaryMessage var1) throws IOException;

    protected abstract void sendPingMessage(PingMessage var1) throws IOException;

    protected abstract void sendPongMessage(PongMessage var1) throws IOException;

    @Override
    public final void close() throws IOException {
        this.close(CloseStatus.NORMAL);
    }

    @Override
    public final void close(CloseStatus status) throws IOException {
        this.checkNativeSessionInitialized();
        if (logger.isDebugEnabled()) {
            logger.debug((Object)("Closing " + this));
        }
        this.closeInternal(status);
    }

    protected abstract void closeInternal(CloseStatus var1) throws IOException;

    public String toString() {
        if (this.nativeSession != null) {
            return this.getClass().getSimpleName() + "[id=" + this.getId() + ", uri=" + this.getUri() + "]";
        }
        return this.getClass().getSimpleName() + "[nativeSession=null]";
    }
}


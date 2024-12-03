/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.context.Lifecycle
 *  org.springframework.context.SmartLifecycle
 *  org.springframework.lang.Nullable
 *  org.springframework.messaging.Message
 *  org.springframework.messaging.simp.stomp.BufferingStompDecoder
 *  org.springframework.messaging.simp.stomp.ConnectionHandlingStompSession
 *  org.springframework.messaging.simp.stomp.StompClientSupport
 *  org.springframework.messaging.simp.stomp.StompDecoder
 *  org.springframework.messaging.simp.stomp.StompEncoder
 *  org.springframework.messaging.simp.stomp.StompHeaderAccessor
 *  org.springframework.messaging.simp.stomp.StompHeaders
 *  org.springframework.messaging.simp.stomp.StompSession
 *  org.springframework.messaging.simp.stomp.StompSessionHandler
 *  org.springframework.messaging.support.MessageHeaderAccessor
 *  org.springframework.messaging.tcp.TcpConnection
 *  org.springframework.messaging.tcp.TcpConnectionHandler
 *  org.springframework.scheduling.TaskScheduler
 *  org.springframework.util.Assert
 *  org.springframework.util.MimeTypeUtils
 *  org.springframework.util.concurrent.ListenableFuture
 *  org.springframework.util.concurrent.ListenableFutureCallback
 *  org.springframework.util.concurrent.SettableListenableFuture
 *  org.springframework.web.util.UriComponentsBuilder
 */
package org.springframework.web.socket.messaging;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.Lifecycle;
import org.springframework.context.SmartLifecycle;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.BufferingStompDecoder;
import org.springframework.messaging.simp.stomp.ConnectionHandlingStompSession;
import org.springframework.messaging.simp.stomp.StompClientSupport;
import org.springframework.messaging.simp.stomp.StompDecoder;
import org.springframework.messaging.simp.stomp.StompEncoder;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.messaging.tcp.TcpConnection;
import org.springframework.messaging.tcp.TcpConnectionHandler;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.util.Assert;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.util.concurrent.SettableListenableFuture;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.handler.LoggingWebSocketHandlerDecorator;
import org.springframework.web.socket.sockjs.transport.SockJsSession;
import org.springframework.web.util.UriComponentsBuilder;

public class WebSocketStompClient
extends StompClientSupport
implements SmartLifecycle {
    private static final Log logger = LogFactory.getLog(WebSocketStompClient.class);
    private final WebSocketClient webSocketClient;
    private int inboundMessageSizeLimit = 65536;
    private boolean autoStartup = true;
    private int phase = Integer.MAX_VALUE;
    private volatile boolean running;

    public WebSocketStompClient(WebSocketClient webSocketClient) {
        Assert.notNull((Object)webSocketClient, (String)"WebSocketClient is required");
        this.webSocketClient = webSocketClient;
        this.setDefaultHeartbeat(new long[]{0L, 0L});
    }

    public WebSocketClient getWebSocketClient() {
        return this.webSocketClient;
    }

    public void setTaskScheduler(@Nullable TaskScheduler taskScheduler) {
        if (!this.isDefaultHeartbeatEnabled()) {
            this.setDefaultHeartbeat(new long[]{10000L, 10000L});
        }
        super.setTaskScheduler(taskScheduler);
    }

    public void setInboundMessageSizeLimit(int inboundMessageSizeLimit) {
        this.inboundMessageSizeLimit = inboundMessageSizeLimit;
    }

    public int getInboundMessageSizeLimit() {
        return this.inboundMessageSizeLimit;
    }

    public void setAutoStartup(boolean autoStartup) {
        this.autoStartup = autoStartup;
    }

    public boolean isAutoStartup() {
        return this.autoStartup;
    }

    public void setPhase(int phase) {
        this.phase = phase;
    }

    public int getPhase() {
        return this.phase;
    }

    public void start() {
        if (!this.isRunning()) {
            this.running = true;
            if (this.getWebSocketClient() instanceof Lifecycle) {
                ((Lifecycle)this.getWebSocketClient()).start();
            }
        }
    }

    public void stop() {
        if (this.isRunning()) {
            this.running = false;
            if (this.getWebSocketClient() instanceof Lifecycle) {
                ((Lifecycle)this.getWebSocketClient()).stop();
            }
        }
    }

    public boolean isRunning() {
        return this.running;
    }

    public ListenableFuture<StompSession> connect(String url, StompSessionHandler handler, Object ... uriVars) {
        return this.connect(url, (WebSocketHttpHeaders)null, handler, uriVars);
    }

    public ListenableFuture<StompSession> connect(String url, @Nullable WebSocketHttpHeaders handshakeHeaders, StompSessionHandler handler, Object ... uriVariables) {
        return this.connect(url, handshakeHeaders, (StompHeaders)null, handler, uriVariables);
    }

    public ListenableFuture<StompSession> connect(String url, @Nullable WebSocketHttpHeaders handshakeHeaders, @Nullable StompHeaders connectHeaders, StompSessionHandler handler, Object ... uriVariables) {
        Assert.notNull((Object)url, (String)"'url' must not be null");
        URI uri = UriComponentsBuilder.fromUriString((String)url).buildAndExpand(uriVariables).encode().toUri();
        return this.connect(uri, handshakeHeaders, connectHeaders, handler);
    }

    public ListenableFuture<StompSession> connect(URI url, @Nullable WebSocketHttpHeaders handshakeHeaders, @Nullable StompHeaders connectHeaders, StompSessionHandler sessionHandler) {
        Assert.notNull((Object)url, (String)"'url' must not be null");
        ConnectionHandlingStompSession session = this.createSession(connectHeaders, sessionHandler);
        WebSocketTcpConnectionHandlerAdapter adapter = new WebSocketTcpConnectionHandlerAdapter((TcpConnectionHandler<byte[]>)session);
        this.getWebSocketClient().doHandshake((WebSocketHandler)new LoggingWebSocketHandlerDecorator(adapter), handshakeHeaders, url).addCallback((ListenableFutureCallback)adapter);
        return session.getSessionFuture();
    }

    protected StompHeaders processConnectHeaders(@Nullable StompHeaders connectHeaders) {
        if ((connectHeaders = super.processConnectHeaders(connectHeaders)).isHeartbeatEnabled()) {
            Assert.state((this.getTaskScheduler() != null ? 1 : 0) != 0, (String)"TaskScheduler must be set if heartbeats are enabled");
        }
        return connectHeaders;
    }

    private static class StompWebSocketMessageCodec {
        private static final StompEncoder ENCODER = new StompEncoder();
        private static final StompDecoder DECODER = new StompDecoder();
        private final BufferingStompDecoder bufferingDecoder;

        public StompWebSocketMessageCodec(int messageSizeLimit) {
            this.bufferingDecoder = new BufferingStompDecoder(DECODER, messageSizeLimit);
        }

        public List<Message<byte[]>> decode(WebSocketMessage<?> webSocketMessage) {
            ByteBuffer byteBuffer;
            List result = Collections.emptyList();
            if (webSocketMessage instanceof TextMessage) {
                byteBuffer = ByteBuffer.wrap(((TextMessage)webSocketMessage).asBytes());
            } else if (webSocketMessage instanceof BinaryMessage) {
                byteBuffer = (ByteBuffer)((BinaryMessage)webSocketMessage).getPayload();
            } else {
                return result;
            }
            result = this.bufferingDecoder.decode(byteBuffer);
            if (result.isEmpty() && logger.isTraceEnabled()) {
                logger.trace((Object)("Incomplete STOMP frame content received, bufferSize=" + this.bufferingDecoder.getBufferSize() + ", bufferSizeLimit=" + this.bufferingDecoder.getBufferSizeLimit() + "."));
            }
            return result;
        }

        public WebSocketMessage<?> encode(Message<byte[]> message, Class<? extends WebSocketSession> sessionType) {
            StompHeaderAccessor accessor = (StompHeaderAccessor)MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
            Assert.notNull((Object)accessor, (String)"No StompHeaderAccessor available");
            byte[] payload = (byte[])message.getPayload();
            byte[] bytes = ENCODER.encode((Map)accessor.getMessageHeaders(), payload);
            boolean useBinary = payload.length > 0 && !SockJsSession.class.isAssignableFrom(sessionType) && MimeTypeUtils.APPLICATION_OCTET_STREAM.isCompatibleWith(accessor.getContentType());
            return useBinary ? new BinaryMessage(bytes) : new TextMessage(bytes);
        }
    }

    private class WebSocketTcpConnectionHandlerAdapter
    implements ListenableFutureCallback<WebSocketSession>,
    WebSocketHandler,
    TcpConnection<byte[]> {
        private final TcpConnectionHandler<byte[]> connectionHandler;
        private final StompWebSocketMessageCodec codec;
        @Nullable
        private volatile WebSocketSession session;
        private volatile long lastReadTime;
        private volatile long lastWriteTime;
        private final List<ScheduledFuture<?>> inactivityTasks;

        public WebSocketTcpConnectionHandlerAdapter(TcpConnectionHandler<byte[]> connectionHandler) {
            this.codec = new StompWebSocketMessageCodec(WebSocketStompClient.this.getInboundMessageSizeLimit());
            this.lastReadTime = -1L;
            this.lastWriteTime = -1L;
            this.inactivityTasks = new ArrayList(2);
            Assert.notNull(connectionHandler, (String)"TcpConnectionHandler must not be null");
            this.connectionHandler = connectionHandler;
        }

        public void onSuccess(@Nullable WebSocketSession webSocketSession) {
        }

        public void onFailure(Throwable ex) {
            this.connectionHandler.afterConnectFailure(ex);
        }

        @Override
        public void afterConnectionEstablished(WebSocketSession session) {
            this.session = session;
            this.connectionHandler.afterConnected((TcpConnection)this);
        }

        @Override
        public void handleMessage(WebSocketSession session, WebSocketMessage<?> webSocketMessage) {
            List<Message<byte[]>> messages;
            this.lastReadTime = this.lastReadTime != -1L ? System.currentTimeMillis() : -1L;
            try {
                messages = this.codec.decode(webSocketMessage);
            }
            catch (Throwable ex) {
                this.connectionHandler.handleFailure(ex);
                return;
            }
            for (Message<byte[]> message : messages) {
                this.connectionHandler.handleMessage(message);
            }
        }

        @Override
        public void handleTransportError(WebSocketSession session, Throwable ex) throws Exception {
            this.connectionHandler.handleFailure(ex);
        }

        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
            this.cancelInactivityTasks();
            this.connectionHandler.afterConnectionClosed();
        }

        private void cancelInactivityTasks() {
            for (ScheduledFuture<?> task : this.inactivityTasks) {
                try {
                    task.cancel(true);
                }
                catch (Throwable throwable) {}
            }
            this.lastReadTime = -1L;
            this.lastWriteTime = -1L;
            this.inactivityTasks.clear();
        }

        @Override
        public boolean supportsPartialMessages() {
            return false;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public ListenableFuture<Void> send(Message<byte[]> message) {
            this.updateLastWriteTime();
            SettableListenableFuture future = new SettableListenableFuture();
            try {
                WebSocketSession session = this.session;
                Assert.state((session != null ? 1 : 0) != 0, (String)"No WebSocketSession available");
                session.sendMessage(this.codec.encode(message, session.getClass()));
                future.set(null);
            }
            catch (Throwable ex) {
                future.setException(ex);
            }
            finally {
                this.updateLastWriteTime();
            }
            return future;
        }

        private void updateLastWriteTime() {
            long lastWriteTime = this.lastWriteTime;
            if (lastWriteTime != -1L) {
                this.lastWriteTime = System.currentTimeMillis();
            }
        }

        public void onReadInactivity(Runnable runnable, long duration) {
            Assert.state((WebSocketStompClient.this.getTaskScheduler() != null ? 1 : 0) != 0, (String)"No TaskScheduler configured");
            this.lastReadTime = System.currentTimeMillis();
            this.inactivityTasks.add(WebSocketStompClient.this.getTaskScheduler().scheduleWithFixedDelay(() -> {
                block3: {
                    if (System.currentTimeMillis() - this.lastReadTime > duration) {
                        try {
                            runnable.run();
                        }
                        catch (Throwable ex) {
                            if (!logger.isDebugEnabled()) break block3;
                            logger.debug((Object)"ReadInactivityTask failure", ex);
                        }
                    }
                }
            }, duration / 2L));
        }

        public void onWriteInactivity(Runnable runnable, long duration) {
            Assert.state((WebSocketStompClient.this.getTaskScheduler() != null ? 1 : 0) != 0, (String)"No TaskScheduler configured");
            this.lastWriteTime = System.currentTimeMillis();
            this.inactivityTasks.add(WebSocketStompClient.this.getTaskScheduler().scheduleWithFixedDelay(() -> {
                block3: {
                    if (System.currentTimeMillis() - this.lastWriteTime > duration) {
                        try {
                            runnable.run();
                        }
                        catch (Throwable ex) {
                            if (!logger.isDebugEnabled()) break block3;
                            logger.debug((Object)"WriteInactivityTask failure", ex);
                        }
                    }
                }
            }, duration / 2L));
        }

        public void close() {
            block3: {
                WebSocketSession session = this.session;
                if (session != null) {
                    try {
                        session.close();
                    }
                    catch (IOException ex) {
                        if (!logger.isDebugEnabled()) break block3;
                        logger.debug((Object)("Failed to close session: " + session.getId()), (Throwable)ex);
                    }
                }
            }
        }
    }
}


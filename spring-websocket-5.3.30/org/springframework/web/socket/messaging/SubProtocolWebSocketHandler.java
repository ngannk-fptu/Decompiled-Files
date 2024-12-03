/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.context.SmartLifecycle
 *  org.springframework.lang.Nullable
 *  org.springframework.messaging.Message
 *  org.springframework.messaging.MessageChannel
 *  org.springframework.messaging.MessageHandler
 *  org.springframework.messaging.MessagingException
 *  org.springframework.messaging.SubscribableChannel
 *  org.springframework.util.Assert
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.web.socket.messaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.SmartLifecycle;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.SubProtocolCapable;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.SessionLimitExceededException;
import org.springframework.web.socket.handler.WebSocketSessionDecorator;
import org.springframework.web.socket.messaging.SubProtocolHandler;
import org.springframework.web.socket.sockjs.transport.session.PollingSockJsSession;
import org.springframework.web.socket.sockjs.transport.session.StreamingSockJsSession;

public class SubProtocolWebSocketHandler
implements WebSocketHandler,
SubProtocolCapable,
MessageHandler,
SmartLifecycle {
    private static final int DEFAULT_TIME_TO_FIRST_MESSAGE = 60000;
    private final Log logger = LogFactory.getLog(SubProtocolWebSocketHandler.class);
    private final MessageChannel clientInboundChannel;
    private final SubscribableChannel clientOutboundChannel;
    private final Map<String, SubProtocolHandler> protocolHandlerLookup = new TreeMap<String, SubProtocolHandler>(String.CASE_INSENSITIVE_ORDER);
    private final Set<SubProtocolHandler> protocolHandlers = new LinkedHashSet<SubProtocolHandler>();
    @Nullable
    private SubProtocolHandler defaultProtocolHandler;
    private final Map<String, WebSocketSessionHolder> sessions = new ConcurrentHashMap<String, WebSocketSessionHolder>();
    private int sendTimeLimit = 10000;
    private int sendBufferSizeLimit = 524288;
    private int timeToFirstMessage = 60000;
    private volatile long lastSessionCheckTime = System.currentTimeMillis();
    private final ReentrantLock sessionCheckLock = new ReentrantLock();
    private final DefaultStats stats = new DefaultStats();
    private volatile boolean running;
    private final Object lifecycleMonitor = new Object();

    public SubProtocolWebSocketHandler(MessageChannel clientInboundChannel, SubscribableChannel clientOutboundChannel) {
        Assert.notNull((Object)clientInboundChannel, (String)"Inbound MessageChannel must not be null");
        Assert.notNull((Object)clientOutboundChannel, (String)"Outbound MessageChannel must not be null");
        this.clientInboundChannel = clientInboundChannel;
        this.clientOutboundChannel = clientOutboundChannel;
    }

    public void setProtocolHandlers(List<SubProtocolHandler> protocolHandlers) {
        this.protocolHandlerLookup.clear();
        this.protocolHandlers.clear();
        for (SubProtocolHandler handler : protocolHandlers) {
            this.addProtocolHandler(handler);
        }
    }

    public List<SubProtocolHandler> getProtocolHandlers() {
        return new ArrayList<SubProtocolHandler>(this.protocolHandlers);
    }

    public void addProtocolHandler(SubProtocolHandler handler) {
        List<String> protocols = handler.getSupportedProtocols();
        if (CollectionUtils.isEmpty(protocols)) {
            if (this.logger.isErrorEnabled()) {
                this.logger.error((Object)("No sub-protocols for " + handler));
            }
            return;
        }
        for (String protocol : protocols) {
            SubProtocolHandler replaced = this.protocolHandlerLookup.put(protocol, handler);
            if (replaced == null || replaced == handler) continue;
            throw new IllegalStateException("Cannot map " + handler + " to protocol '" + protocol + "': already mapped to " + replaced + ".");
        }
        this.protocolHandlers.add(handler);
    }

    public Map<String, SubProtocolHandler> getProtocolHandlerMap() {
        return this.protocolHandlerLookup;
    }

    public void setDefaultProtocolHandler(@Nullable SubProtocolHandler defaultProtocolHandler) {
        this.defaultProtocolHandler = defaultProtocolHandler;
        if (this.protocolHandlerLookup.isEmpty()) {
            this.setProtocolHandlers(Collections.singletonList(defaultProtocolHandler));
        }
    }

    @Nullable
    public SubProtocolHandler getDefaultProtocolHandler() {
        return this.defaultProtocolHandler;
    }

    @Override
    public List<String> getSubProtocols() {
        return new ArrayList<String>(this.protocolHandlerLookup.keySet());
    }

    public void setSendTimeLimit(int sendTimeLimit) {
        this.sendTimeLimit = sendTimeLimit;
    }

    public int getSendTimeLimit() {
        return this.sendTimeLimit;
    }

    public void setSendBufferSizeLimit(int sendBufferSizeLimit) {
        this.sendBufferSizeLimit = sendBufferSizeLimit;
    }

    public int getSendBufferSizeLimit() {
        return this.sendBufferSizeLimit;
    }

    public void setTimeToFirstMessage(int timeToFirstMessage) {
        this.timeToFirstMessage = timeToFirstMessage;
    }

    public int getTimeToFirstMessage() {
        return this.timeToFirstMessage;
    }

    public String getStatsInfo() {
        return this.stats.toString();
    }

    public Stats getStats() {
        return this.stats;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void start() {
        Assert.isTrue((this.defaultProtocolHandler != null || !this.protocolHandlers.isEmpty() ? 1 : 0) != 0, (String)"No handlers");
        Object object = this.lifecycleMonitor;
        synchronized (object) {
            this.clientOutboundChannel.subscribe((MessageHandler)this);
            this.running = true;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void stop() {
        Iterator<WebSocketSessionHolder> iterator = this.lifecycleMonitor;
        synchronized (iterator) {
            this.running = false;
            this.clientOutboundChannel.unsubscribe((MessageHandler)this);
        }
        for (WebSocketSessionHolder holder : this.sessions.values()) {
            try {
                holder.getSession().close(CloseStatus.GOING_AWAY);
            }
            catch (Throwable ex) {
                if (!this.logger.isWarnEnabled()) continue;
                this.logger.warn((Object)("Failed to close '" + holder.getSession() + "': " + ex));
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void stop(Runnable callback) {
        Object object = this.lifecycleMonitor;
        synchronized (object) {
            this.stop();
            callback.run();
        }
    }

    public final boolean isRunning() {
        return this.running;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        if (!session.isOpen()) {
            return;
        }
        this.stats.incrementSessionCount(session);
        session = this.decorateSession(session);
        this.sessions.put(session.getId(), new WebSocketSessionHolder(session));
        this.findProtocolHandler(session).afterSessionStarted(session, this.clientInboundChannel);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        WebSocketSessionHolder holder = this.sessions.get(session.getId());
        if (holder != null) {
            session = holder.getSession();
        }
        SubProtocolHandler protocolHandler = this.findProtocolHandler(session);
        protocolHandler.handleMessageFromClient(session, message, this.clientInboundChannel);
        if (holder != null) {
            holder.setHasHandledMessages();
        }
        this.checkSessions();
    }

    public void handleMessage(Message<?> message) throws MessagingException {
        block12: {
            String sessionId = this.resolveSessionId(message);
            if (sessionId == null) {
                if (this.logger.isErrorEnabled()) {
                    this.logger.error((Object)("Could not find session id in " + message));
                }
                return;
            }
            WebSocketSessionHolder holder = this.sessions.get(sessionId);
            if (holder == null) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug((Object)("No session for " + message));
                }
                return;
            }
            WebSocketSession session = holder.getSession();
            try {
                this.findProtocolHandler(session).handleMessageToClient(session, message);
            }
            catch (SessionLimitExceededException ex) {
                try {
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug((Object)("Terminating '" + session + "'"), (Throwable)ex);
                    } else if (this.logger.isWarnEnabled()) {
                        this.logger.warn((Object)("Terminating '" + session + "': " + ex.getMessage()));
                    }
                    this.stats.incrementLimitExceededCount();
                    this.clearSession(session, ex.getStatus());
                    session.close(ex.getStatus());
                }
                catch (Exception secondException) {
                    this.logger.debug((Object)("Failure while closing session " + sessionId + "."), (Throwable)secondException);
                }
            }
            catch (Exception ex) {
                if (!this.logger.isDebugEnabled()) break block12;
                this.logger.debug((Object)("Failed to send message to client in " + session + ": " + message), (Throwable)ex);
            }
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        this.stats.incrementTransportError();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        this.clearSession(session, closeStatus);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    protected WebSocketSession decorateSession(WebSocketSession session) {
        return new ConcurrentWebSocketSessionDecorator(session, this.getSendTimeLimit(), this.getSendBufferSizeLimit());
    }

    protected final SubProtocolHandler findProtocolHandler(WebSocketSession session) {
        SubProtocolHandler handler;
        String protocol = null;
        try {
            protocol = session.getAcceptedProtocol();
        }
        catch (Exception ex) {
            this.logger.error((Object)"Failed to obtain session.getAcceptedProtocol(): will use the default protocol handler (if configured).", (Throwable)ex);
        }
        if (StringUtils.hasLength((String)protocol)) {
            handler = this.protocolHandlerLookup.get(protocol);
            if (handler == null) {
                throw new IllegalStateException("No handler for '" + protocol + "' among " + this.protocolHandlerLookup);
            }
        } else if (this.defaultProtocolHandler != null) {
            handler = this.defaultProtocolHandler;
        } else if (this.protocolHandlers.size() == 1) {
            handler = this.protocolHandlers.iterator().next();
        } else {
            throw new IllegalStateException("Multiple protocol handlers configured and no protocol was negotiated. Consider configuring a default SubProtocolHandler.");
        }
        return handler;
    }

    @Nullable
    private String resolveSessionId(Message<?> message) {
        String sessionId;
        for (SubProtocolHandler handler : this.protocolHandlerLookup.values()) {
            String sessionId2 = handler.resolveSessionId(message);
            if (sessionId2 == null) continue;
            return sessionId2;
        }
        if (this.defaultProtocolHandler != null && (sessionId = this.defaultProtocolHandler.resolveSessionId(message)) != null) {
            return sessionId;
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void checkSessions() {
        long currentTime = System.currentTimeMillis();
        if (!this.isRunning() || currentTime - this.lastSessionCheckTime < (long)this.getTimeToFirstMessage()) {
            return;
        }
        if (this.sessionCheckLock.tryLock()) {
            try {
                for (WebSocketSessionHolder holder : this.sessions.values()) {
                    long timeSinceCreated;
                    if (holder.hasHandledMessages() || (timeSinceCreated = currentTime - holder.getCreateTime()) < (long)this.getTimeToFirstMessage()) continue;
                    WebSocketSession session = holder.getSession();
                    if (this.logger.isInfoEnabled()) {
                        this.logger.info((Object)("No messages received after " + timeSinceCreated + " ms. Closing " + holder.getSession() + "."));
                    }
                    try {
                        this.stats.incrementNoMessagesReceivedCount();
                        session.close(CloseStatus.SESSION_NOT_RELIABLE);
                    }
                    catch (Throwable ex) {
                        if (!this.logger.isWarnEnabled()) continue;
                        this.logger.warn((Object)("Failed to close unreliable " + session), ex);
                    }
                }
            }
            finally {
                this.lastSessionCheckTime = currentTime;
                this.sessionCheckLock.unlock();
            }
        }
    }

    private void clearSession(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Clearing session " + session.getId()));
        }
        if (this.sessions.remove(session.getId()) != null) {
            this.stats.decrementSessionCount(session);
        }
        this.findProtocolHandler(session).afterSessionEnded(session, closeStatus, this.clientInboundChannel);
    }

    public String toString() {
        return "SubProtocolWebSocketHandler" + this.protocolHandlers;
    }

    private class DefaultStats
    implements Stats {
        private final AtomicInteger total = new AtomicInteger();
        private final AtomicInteger webSocket = new AtomicInteger();
        private final AtomicInteger httpStreaming = new AtomicInteger();
        private final AtomicInteger httpPolling = new AtomicInteger();
        private final AtomicInteger limitExceeded = new AtomicInteger();
        private final AtomicInteger noMessagesReceived = new AtomicInteger();
        private final AtomicInteger transportError = new AtomicInteger();

        private DefaultStats() {
        }

        @Override
        public int getTotalSessions() {
            return this.total.get();
        }

        @Override
        public int getWebSocketSessions() {
            return this.webSocket.get();
        }

        @Override
        public int getHttpStreamingSessions() {
            return this.httpStreaming.get();
        }

        @Override
        public int getHttpPollingSessions() {
            return this.httpPolling.get();
        }

        @Override
        public int getLimitExceededSessions() {
            return this.limitExceeded.get();
        }

        @Override
        public int getNoMessagesReceivedSessions() {
            return this.noMessagesReceived.get();
        }

        @Override
        public int getTransportErrorSessions() {
            return this.transportError.get();
        }

        void incrementSessionCount(WebSocketSession session) {
            this.getCountFor(session).incrementAndGet();
            this.total.incrementAndGet();
        }

        void decrementSessionCount(WebSocketSession session) {
            this.getCountFor(session).decrementAndGet();
        }

        void incrementLimitExceededCount() {
            this.limitExceeded.incrementAndGet();
        }

        void incrementNoMessagesReceivedCount() {
            this.noMessagesReceived.incrementAndGet();
        }

        void incrementTransportError() {
            this.transportError.incrementAndGet();
        }

        AtomicInteger getCountFor(WebSocketSession session) {
            if ((session = WebSocketSessionDecorator.unwrap(session)) instanceof PollingSockJsSession) {
                return this.httpPolling;
            }
            if (session instanceof StreamingSockJsSession) {
                return this.httpStreaming;
            }
            return this.webSocket;
        }

        public String toString() {
            return SubProtocolWebSocketHandler.this.sessions.size() + " current WS(" + this.webSocket.get() + ")-HttpStream(" + this.httpStreaming.get() + ")-HttpPoll(" + this.httpPolling.get() + "), " + this.total.get() + " total, " + (this.limitExceeded.get() + this.noMessagesReceived.get()) + " closed abnormally (" + this.noMessagesReceived.get() + " connect failure, " + this.limitExceeded.get() + " send limit, " + this.transportError.get() + " transport error)";
        }
    }

    public static interface Stats {
        public int getTotalSessions();

        public int getWebSocketSessions();

        public int getHttpStreamingSessions();

        public int getHttpPollingSessions();

        public int getLimitExceededSessions();

        public int getNoMessagesReceivedSessions();

        public int getTransportErrorSessions();
    }

    private static class WebSocketSessionHolder {
        private final WebSocketSession session;
        private final long createTime;
        private volatile boolean hasHandledMessages;

        public WebSocketSessionHolder(WebSocketSession session) {
            this.session = session;
            this.createTime = System.currentTimeMillis();
        }

        public WebSocketSession getSession() {
            return this.session;
        }

        public long getCreateTime() {
            return this.createTime;
        }

        public void setHasHandledMessages() {
            this.hasHandledMessages = true;
        }

        public boolean hasHandledMessages() {
            return this.hasHandledMessages;
        }

        public String toString() {
            return "WebSocketSessionHolder[session=" + this.session + ", createTime=" + this.createTime + ", hasHandledMessages=" + this.hasHandledMessages + "]";
        }
    }
}


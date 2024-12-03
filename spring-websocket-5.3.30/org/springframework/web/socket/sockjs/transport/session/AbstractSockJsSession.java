/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.core.NestedExceptionUtils
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.web.socket.sockjs.transport.session;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.sockjs.SockJsMessageDeliveryException;
import org.springframework.web.socket.sockjs.SockJsTransportFailureException;
import org.springframework.web.socket.sockjs.frame.SockJsFrame;
import org.springframework.web.socket.sockjs.frame.SockJsMessageCodec;
import org.springframework.web.socket.sockjs.transport.SockJsServiceConfig;
import org.springframework.web.socket.sockjs.transport.SockJsSession;

public abstract class AbstractSockJsSession
implements SockJsSession {
    public static final String DISCONNECTED_CLIENT_LOG_CATEGORY = "org.springframework.web.socket.sockjs.DisconnectedClient";
    private static final Set<String> DISCONNECTED_CLIENT_EXCEPTIONS = new HashSet<String>(Arrays.asList("ClientAbortException", "EOFException", "EofException"));
    protected static final Log disconnectedClientLogger = LogFactory.getLog((String)"org.springframework.web.socket.sockjs.DisconnectedClient");
    protected final Log logger = LogFactory.getLog(this.getClass());
    protected final Object responseLock = new Object();
    private final String id;
    private final SockJsServiceConfig config;
    private final WebSocketHandler handler;
    private final Map<String, Object> attributes = new ConcurrentHashMap<String, Object>();
    private volatile State state = State.NEW;
    private final long timeCreated;
    private volatile long timeLastActive = this.timeCreated = System.currentTimeMillis();
    @Nullable
    private ScheduledFuture<?> heartbeatFuture;
    @Nullable
    private HeartbeatTask heartbeatTask;
    private volatile boolean heartbeatDisabled;

    public AbstractSockJsSession(String id, SockJsServiceConfig config, WebSocketHandler handler, @Nullable Map<String, Object> attributes) {
        Assert.notNull((Object)id, (String)"Session id must not be null");
        Assert.notNull((Object)config, (String)"SockJsServiceConfig must not be null");
        Assert.notNull((Object)handler, (String)"WebSocketHandler must not be null");
        this.id = id;
        this.config = config;
        this.handler = handler;
        if (attributes != null) {
            this.attributes.putAll(attributes);
        }
    }

    @Override
    public String getId() {
        return this.id;
    }

    protected SockJsMessageCodec getMessageCodec() {
        return this.config.getMessageCodec();
    }

    public SockJsServiceConfig getSockJsServiceConfig() {
        return this.config;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public final void sendMessage(WebSocketMessage<?> message) throws IOException {
        Assert.state((!this.isClosed() ? 1 : 0) != 0, (String)"Cannot send a message when session is closed");
        Assert.isInstanceOf(TextMessage.class, message, (String)"SockJS supports text messages only");
        this.sendMessageInternal((String)((TextMessage)message).getPayload());
    }

    protected abstract void sendMessageInternal(String var1) throws IOException;

    public boolean isNew() {
        return State.NEW.equals((Object)this.state);
    }

    @Override
    public boolean isOpen() {
        return State.OPEN.equals((Object)this.state);
    }

    public boolean isClosed() {
        return State.CLOSED.equals((Object)this.state);
    }

    @Override
    public final void close() throws IOException {
        this.close(new CloseStatus(3000, "Go away!"));
    }

    @Override
    public final void close(CloseStatus status) throws IOException {
        if (this.isOpen()) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Closing SockJS session " + this.getId() + " with " + status));
            }
            this.state = State.CLOSED;
            try {
                if (this.isActive() && !CloseStatus.SESSION_NOT_RELIABLE.equals(status)) {
                    try {
                        this.writeFrameInternal(SockJsFrame.closeFrame(status.getCode(), status.getReason()));
                    }
                    catch (Throwable ex) {
                        this.logger.debug((Object)"Failure while sending SockJS close frame", ex);
                    }
                }
                this.updateLastActiveTime();
                this.cancelHeartbeat();
                this.disconnect(status);
            }
            finally {
                try {
                    this.handler.afterConnectionClosed(this, status);
                }
                catch (Throwable ex) {
                    this.logger.debug((Object)("Error from WebSocketHandler.afterConnectionClosed in " + this), ex);
                }
            }
        }
    }

    @Override
    public long getTimeSinceLastActive() {
        if (this.isNew()) {
            return System.currentTimeMillis() - this.timeCreated;
        }
        return this.isActive() ? 0L : System.currentTimeMillis() - this.timeLastActive;
    }

    protected void updateLastActiveTime() {
        this.timeLastActive = System.currentTimeMillis();
    }

    @Override
    public void disableHeartbeat() {
        this.heartbeatDisabled = true;
        this.cancelHeartbeat();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void sendHeartbeat() throws SockJsTransportFailureException {
        Object object = this.responseLock;
        synchronized (object) {
            if (this.isActive() && !this.heartbeatDisabled) {
                this.writeFrame(SockJsFrame.heartbeatFrame());
                this.scheduleHeartbeat();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void scheduleHeartbeat() {
        if (this.heartbeatDisabled) {
            return;
        }
        Object object = this.responseLock;
        synchronized (object) {
            this.cancelHeartbeat();
            if (!this.isActive()) {
                return;
            }
            Date time = new Date(System.currentTimeMillis() + this.config.getHeartbeatTime());
            this.heartbeatTask = new HeartbeatTask();
            this.heartbeatFuture = this.config.getTaskScheduler().schedule((Runnable)this.heartbeatTask, time);
            if (this.logger.isTraceEnabled()) {
                this.logger.trace((Object)("Scheduled heartbeat in session " + this.getId()));
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void cancelHeartbeat() {
        Object object = this.responseLock;
        synchronized (object) {
            if (this.heartbeatFuture != null) {
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace((Object)("Cancelling heartbeat in session " + this.getId()));
                }
                this.heartbeatFuture.cancel(false);
                this.heartbeatFuture = null;
            }
            if (this.heartbeatTask != null) {
                this.heartbeatTask.cancel();
                this.heartbeatTask = null;
            }
        }
    }

    public abstract boolean isActive();

    protected abstract void disconnect(CloseStatus var1) throws IOException;

    protected void writeFrame(SockJsFrame frame) throws SockJsTransportFailureException {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace((Object)("Preparing to write " + frame));
        }
        try {
            this.writeFrameInternal(frame);
        }
        catch (Exception ex) {
            this.logWriteFrameFailure(ex);
            try {
                this.disconnect(CloseStatus.SERVER_ERROR);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            try {
                this.close(CloseStatus.SERVER_ERROR);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            throw new SockJsTransportFailureException("Failed to write " + frame, this.getId(), ex);
        }
    }

    protected abstract void writeFrameInternal(SockJsFrame var1) throws IOException;

    private void logWriteFrameFailure(Throwable ex) {
        if (this.indicatesDisconnectedClient(ex)) {
            if (disconnectedClientLogger.isTraceEnabled()) {
                disconnectedClientLogger.trace((Object)"Looks like the client has gone away", ex);
            } else if (disconnectedClientLogger.isDebugEnabled()) {
                disconnectedClientLogger.debug((Object)("Looks like the client has gone away: " + ex + " (For a full stack trace, set the log category '" + DISCONNECTED_CLIENT_LOG_CATEGORY + "' to TRACE level.)"));
            }
        } else {
            this.logger.debug((Object)"Terminating connection after failure to send message to client", ex);
        }
    }

    private boolean indicatesDisconnectedClient(Throwable ex) {
        String message = NestedExceptionUtils.getMostSpecificCause((Throwable)ex).getMessage();
        message = message != null ? message.toLowerCase() : "";
        String className = ex.getClass().getSimpleName();
        return message.contains("broken pipe") || DISCONNECTED_CLIENT_EXCEPTIONS.contains(className);
    }

    public void delegateConnectionEstablished() throws Exception {
        this.state = State.OPEN;
        this.handler.afterConnectionEstablished(this);
    }

    public void delegateMessages(String ... messages) throws SockJsMessageDeliveryException {
        for (int i = 0; i < messages.length; ++i) {
            try {
                if (this.isClosed()) {
                    this.logUndeliveredMessages(i, messages);
                    return;
                }
                this.handler.handleMessage(this, new TextMessage(messages[i]));
                continue;
            }
            catch (Exception ex) {
                if (this.isClosed()) {
                    if (this.logger.isTraceEnabled()) {
                        this.logger.trace((Object)("Failed to handle message '" + messages[i] + "'"), (Throwable)ex);
                    }
                    this.logUndeliveredMessages(i, messages);
                    return;
                }
                throw new SockJsMessageDeliveryException(this.id, AbstractSockJsSession.getUndelivered(messages, i), (Throwable)ex);
            }
        }
    }

    private void logUndeliveredMessages(int index, String[] messages) {
        List<String> undelivered = AbstractSockJsSession.getUndelivered(messages, index);
        if (this.logger.isTraceEnabled() && !undelivered.isEmpty()) {
            this.logger.trace((Object)("Dropped inbound message(s) due to closed session: " + undelivered));
        }
    }

    private static List<String> getUndelivered(String[] messages, int i) {
        switch (messages.length - i) {
            case 0: {
                return Collections.emptyList();
            }
            case 1: {
                return messages[i].trim().isEmpty() ? Collections.emptyList() : Collections.singletonList(messages[i]);
            }
        }
        return Arrays.stream(Arrays.copyOfRange(messages, i, messages.length)).filter(message -> !message.trim().isEmpty()).collect(Collectors.toList());
    }

    public final void delegateConnectionClosed(CloseStatus status) throws Exception {
        if (!this.isClosed()) {
            try {
                this.updateLastActiveTime();
                ScheduledFuture<?> future = this.heartbeatFuture;
                if (future != null) {
                    this.heartbeatFuture = null;
                    future.cancel(false);
                }
            }
            finally {
                this.state = State.CLOSED;
                this.handler.afterConnectionClosed(this, status);
            }
        }
    }

    public void tryCloseWithSockJsTransportError(Throwable error, CloseStatus closeStatus) {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Closing due to transport error for " + this));
        }
        try {
            this.delegateError(error);
        }
        catch (Throwable delegateException) {
            this.logger.debug((Object)"Exception from error handling delegate", delegateException);
        }
        try {
            this.close(closeStatus);
        }
        catch (Throwable closeException) {
            this.logger.debug((Object)("Failure while closing " + this), closeException);
        }
    }

    public void delegateError(Throwable ex) throws Exception {
        this.handler.handleTransportError(this, ex);
    }

    public String toString() {
        return this.getClass().getSimpleName() + "[id=" + this.getId() + "]";
    }

    private class HeartbeatTask
    implements Runnable {
        private boolean expired;

        private HeartbeatTask() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            Object object = AbstractSockJsSession.this.responseLock;
            synchronized (object) {
                if (!this.expired && !AbstractSockJsSession.this.isClosed()) {
                    try {
                        AbstractSockJsSession.this.sendHeartbeat();
                    }
                    catch (Throwable throwable) {
                    }
                    finally {
                        this.expired = true;
                    }
                }
            }
        }

        void cancel() {
            this.expired = true;
        }
    }

    private static enum State {
        NEW,
        OPEN,
        CLOSED;

    }
}


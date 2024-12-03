/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.lang.Nullable
 */
package org.springframework.web.socket.handler;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.SessionLimitExceededException;
import org.springframework.web.socket.handler.WebSocketSessionDecorator;

public class ConcurrentWebSocketSessionDecorator
extends WebSocketSessionDecorator {
    private static final Log logger = LogFactory.getLog(ConcurrentWebSocketSessionDecorator.class);
    private final int sendTimeLimit;
    private final int bufferSizeLimit;
    private final OverflowStrategy overflowStrategy;
    @Nullable
    private Consumer<WebSocketMessage<?>> preSendCallback;
    private final Queue<WebSocketMessage<?>> buffer = new LinkedBlockingQueue();
    private final AtomicInteger bufferSize = new AtomicInteger();
    private volatile long sendStartTime;
    private volatile boolean limitExceeded;
    private volatile boolean closeInProgress;
    private final Lock flushLock = new ReentrantLock();
    private final Lock closeLock = new ReentrantLock();

    public ConcurrentWebSocketSessionDecorator(WebSocketSession delegate, int sendTimeLimit, int bufferSizeLimit) {
        this(delegate, sendTimeLimit, bufferSizeLimit, OverflowStrategy.TERMINATE);
    }

    public ConcurrentWebSocketSessionDecorator(WebSocketSession delegate, int sendTimeLimit, int bufferSizeLimit, OverflowStrategy overflowStrategy) {
        super(delegate);
        this.sendTimeLimit = sendTimeLimit;
        this.bufferSizeLimit = bufferSizeLimit;
        this.overflowStrategy = overflowStrategy;
    }

    public int getSendTimeLimit() {
        return this.sendTimeLimit;
    }

    public int getBufferSizeLimit() {
        return this.bufferSizeLimit;
    }

    public int getBufferSize() {
        return this.bufferSize.get();
    }

    public long getTimeSinceSendStarted() {
        long start = this.sendStartTime;
        return start > 0L ? System.currentTimeMillis() - start : 0L;
    }

    public void setMessageCallback(Consumer<WebSocketMessage<?>> callback) {
        this.preSendCallback = callback;
    }

    @Override
    public void sendMessage(WebSocketMessage<?> message) throws IOException {
        if (this.shouldNotSend()) {
            return;
        }
        this.buffer.add(message);
        this.bufferSize.addAndGet(message.getPayloadLength());
        if (this.preSendCallback != null) {
            this.preSendCallback.accept(message);
        }
        do {
            if (this.tryFlushMessageBuffer()) continue;
            if (logger.isTraceEnabled()) {
                logger.trace((Object)String.format("Another send already in progress: session id '%s':, \"in-progress\" send time %d (ms), buffer size %d bytes", this.getId(), this.getTimeSinceSendStarted(), this.getBufferSize()));
            }
            this.checkSessionLimits();
            break;
        } while (!this.buffer.isEmpty() && !this.shouldNotSend());
    }

    private boolean shouldNotSend() {
        return this.limitExceeded || this.closeInProgress;
    }

    private boolean tryFlushMessageBuffer() throws IOException {
        if (this.flushLock.tryLock()) {
            try {
                WebSocketMessage<?> message;
                while ((message = this.buffer.poll()) != null) {
                    if (this.shouldNotSend()) {
                        break;
                    }
                    this.bufferSize.addAndGet(-message.getPayloadLength());
                    this.sendStartTime = System.currentTimeMillis();
                    this.getDelegate().sendMessage(message);
                    this.sendStartTime = 0L;
                }
            }
            finally {
                this.sendStartTime = 0L;
                this.flushLock.unlock();
            }
            return true;
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private void checkSessionLimits() {
        if (this.shouldNotSend() || !this.closeLock.tryLock()) return;
        try {
            if (this.getTimeSinceSendStarted() > (long)this.getSendTimeLimit()) {
                String format = "Send time %d (ms) for session '%s' exceeded the allowed limit %d";
                String reason = String.format(format, this.getTimeSinceSendStarted(), this.getId(), this.getSendTimeLimit());
                this.limitExceeded(reason);
                return;
            }
            if (this.getBufferSize() <= this.getBufferSizeLimit()) return;
            switch (this.overflowStrategy) {
                case TERMINATE: {
                    String format = "Buffer size %d bytes for session '%s' exceeds the allowed limit %d";
                    String reason = String.format(format, this.getBufferSize(), this.getId(), this.getBufferSizeLimit());
                    this.limitExceeded(reason);
                    return;
                }
                case DROP: {
                    WebSocketMessage<?> message;
                    int i = 0;
                    while (this.getBufferSize() > this.getBufferSizeLimit() && (message = this.buffer.poll()) != null) {
                        this.bufferSize.addAndGet(-message.getPayloadLength());
                        ++i;
                    }
                    if (!logger.isDebugEnabled()) return;
                    logger.debug((Object)("Dropped " + i + " messages, buffer size: " + this.getBufferSize()));
                    return;
                }
                default: {
                    throw new IllegalStateException("Unexpected OverflowStrategy: " + (Object)((Object)this.overflowStrategy));
                }
            }
        }
        finally {
            this.closeLock.unlock();
        }
    }

    private void limitExceeded(String reason) {
        this.limitExceeded = true;
        throw new SessionLimitExceededException(reason, CloseStatus.SESSION_NOT_RELIABLE);
    }

    @Override
    public void close(CloseStatus status) throws IOException {
        this.closeLock.lock();
        try {
            if (this.closeInProgress) {
                return;
            }
            if (!CloseStatus.SESSION_NOT_RELIABLE.equals(status)) {
                try {
                    this.checkSessionLimits();
                }
                catch (SessionLimitExceededException sessionLimitExceededException) {
                    // empty catch block
                }
                if (this.limitExceeded) {
                    if (logger.isDebugEnabled()) {
                        logger.debug((Object)("Changing close status " + status + " to SESSION_NOT_RELIABLE."));
                    }
                    status = CloseStatus.SESSION_NOT_RELIABLE;
                }
            }
            this.closeInProgress = true;
            super.close(status);
        }
        finally {
            this.closeLock.unlock();
        }
    }

    @Override
    public String toString() {
        return this.getDelegate().toString();
    }

    public static enum OverflowStrategy {
        TERMINATE,
        DROP;

    }
}


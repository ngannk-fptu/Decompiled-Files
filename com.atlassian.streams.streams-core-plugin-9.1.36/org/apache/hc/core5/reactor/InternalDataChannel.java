/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.reactor;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import javax.net.ssl.SSLContext;
import org.apache.hc.core5.function.Callback;
import org.apache.hc.core5.function.Decorator;
import org.apache.hc.core5.io.CloseMode;
import org.apache.hc.core5.net.NamedEndpoint;
import org.apache.hc.core5.reactor.Command;
import org.apache.hc.core5.reactor.IOEventHandler;
import org.apache.hc.core5.reactor.IOSession;
import org.apache.hc.core5.reactor.IOSessionListener;
import org.apache.hc.core5.reactor.InternalChannel;
import org.apache.hc.core5.reactor.ProtocolIOSession;
import org.apache.hc.core5.reactor.ssl.SSLBufferMode;
import org.apache.hc.core5.reactor.ssl.SSLIOSession;
import org.apache.hc.core5.reactor.ssl.SSLMode;
import org.apache.hc.core5.reactor.ssl.SSLSessionInitializer;
import org.apache.hc.core5.reactor.ssl.SSLSessionVerifier;
import org.apache.hc.core5.reactor.ssl.TlsDetails;
import org.apache.hc.core5.util.Asserts;
import org.apache.hc.core5.util.Timeout;

final class InternalDataChannel
extends InternalChannel
implements ProtocolIOSession {
    private final IOSession ioSession;
    private final NamedEndpoint initialEndpoint;
    private final Decorator<IOSession> ioSessionDecorator;
    private final IOSessionListener sessionListener;
    private final Queue<InternalDataChannel> closedSessions;
    private final AtomicReference<SSLIOSession> tlsSessionRef;
    private final AtomicReference<IOSession> currentSessionRef;
    private final AtomicBoolean closed;

    InternalDataChannel(IOSession ioSession, NamedEndpoint initialEndpoint, Decorator<IOSession> ioSessionDecorator, IOSessionListener sessionListener, Queue<InternalDataChannel> closedSessions) {
        this.ioSession = ioSession;
        this.initialEndpoint = initialEndpoint;
        this.closedSessions = closedSessions;
        this.ioSessionDecorator = ioSessionDecorator;
        this.sessionListener = sessionListener;
        this.tlsSessionRef = new AtomicReference();
        this.currentSessionRef = new AtomicReference<IOSession>(ioSessionDecorator != null ? ioSessionDecorator.decorate(ioSession) : ioSession);
        this.closed = new AtomicBoolean(false);
    }

    @Override
    public String getId() {
        return this.ioSession.getId();
    }

    @Override
    public NamedEndpoint getInitialEndpoint() {
        return this.initialEndpoint;
    }

    @Override
    public IOEventHandler getHandler() {
        IOSession currentSession = this.currentSessionRef.get();
        return currentSession.getHandler();
    }

    @Override
    public void upgrade(IOEventHandler handler) {
        IOSession currentSession = this.currentSessionRef.get();
        currentSession.upgrade(handler);
    }

    private IOEventHandler ensureHandler(IOSession session) {
        IOEventHandler handler = session.getHandler();
        Asserts.notNull(handler, "IO event handler");
        return handler;
    }

    @Override
    void onIOEvent(int readyOps) throws IOException {
        IOEventHandler handler;
        IOSession currentSession;
        if ((readyOps & 8) != 0) {
            currentSession = this.currentSessionRef.get();
            currentSession.clearEvent(8);
            if (this.tlsSessionRef.get() == null) {
                if (this.sessionListener != null) {
                    this.sessionListener.connected(currentSession);
                }
                handler = this.ensureHandler(currentSession);
                handler.connected(currentSession);
            }
        }
        if ((readyOps & 1) != 0) {
            currentSession = this.currentSessionRef.get();
            currentSession.updateReadTime();
            if (this.sessionListener != null) {
                this.sessionListener.inputReady(currentSession);
            }
            handler = this.ensureHandler(currentSession);
            handler.inputReady(currentSession, null);
        }
        if ((readyOps & 4) != 0 || (this.ioSession.getEventMask() & 4) != 0) {
            currentSession = this.currentSessionRef.get();
            currentSession.updateWriteTime();
            if (this.sessionListener != null) {
                this.sessionListener.outputReady(currentSession);
            }
            handler = this.ensureHandler(currentSession);
            handler.outputReady(currentSession);
        }
    }

    @Override
    Timeout getTimeout() {
        IOSession currentSession = this.currentSessionRef.get();
        return currentSession.getSocketTimeout();
    }

    @Override
    void onTimeout(Timeout timeout) throws IOException {
        IOSession currentSession = this.currentSessionRef.get();
        if (this.sessionListener != null) {
            this.sessionListener.timeout(currentSession);
        }
        IOEventHandler handler = this.ensureHandler(currentSession);
        handler.timeout(currentSession, timeout);
    }

    @Override
    void onException(Exception cause) {
        IOEventHandler handler;
        IOSession currentSession = this.currentSessionRef.get();
        if (this.sessionListener != null) {
            this.sessionListener.exception(currentSession, cause);
        }
        if ((handler = currentSession.getHandler()) != null) {
            handler.exception(currentSession, cause);
        }
    }

    void onTLSSessionStart(SSLIOSession sslSession) {
        IOSession currentSession = this.currentSessionRef.get();
        if (this.sessionListener != null) {
            this.sessionListener.connected(currentSession);
        }
    }

    void onTLSSessionEnd() {
        if (this.closed.compareAndSet(false, true)) {
            this.closedSessions.add(this);
        }
    }

    void disconnected() {
        IOEventHandler handler;
        IOSession currentSession = this.currentSessionRef.get();
        if (this.sessionListener != null) {
            this.sessionListener.disconnected(currentSession);
        }
        if ((handler = currentSession.getHandler()) != null) {
            handler.disconnected(currentSession);
        }
    }

    @Override
    public void startTls(SSLContext sslContext, NamedEndpoint endpoint, SSLBufferMode sslBufferMode, SSLSessionInitializer initializer, SSLSessionVerifier verifier, Timeout handshakeTimeout) {
        SSLIOSession sslioSession = new SSLIOSession(endpoint != null ? endpoint : this.initialEndpoint, this.ioSession, this.initialEndpoint != null ? SSLMode.CLIENT : SSLMode.SERVER, sslContext, sslBufferMode, initializer, verifier, new Callback<SSLIOSession>(){

            @Override
            public void execute(SSLIOSession sslSession) {
                InternalDataChannel.this.onTLSSessionStart(sslSession);
            }
        }, new Callback<SSLIOSession>(){

            @Override
            public void execute(SSLIOSession sslSession) {
                InternalDataChannel.this.onTLSSessionEnd();
            }
        }, handshakeTimeout);
        if (this.tlsSessionRef.compareAndSet(null, sslioSession)) {
            this.currentSessionRef.set(this.ioSessionDecorator != null ? this.ioSessionDecorator.decorate(sslioSession) : sslioSession);
            if (this.sessionListener != null) {
                this.sessionListener.startTls(sslioSession);
            }
        } else {
            throw new IllegalStateException("TLS already activated");
        }
    }

    @Override
    public TlsDetails getTlsDetails() {
        SSLIOSession sslIoSession = this.tlsSessionRef.get();
        return sslIoSession != null ? sslIoSession.getTlsDetails() : null;
    }

    @Override
    public Lock getLock() {
        return this.ioSession.getLock();
    }

    @Override
    public void close() {
        this.close(CloseMode.GRACEFUL);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void close(CloseMode closeMode) {
        IOSession currentSession = this.currentSessionRef.get();
        if (closeMode == CloseMode.IMMEDIATE) {
            this.closed.set(true);
            currentSession.close(closeMode);
        } else if (this.closed.compareAndSet(false, true)) {
            try {
                currentSession.close(closeMode);
            }
            finally {
                this.closedSessions.add(this);
            }
        }
    }

    @Override
    public IOSession.Status getStatus() {
        IOSession currentSession = this.currentSessionRef.get();
        return currentSession.getStatus();
    }

    @Override
    public boolean isOpen() {
        IOSession currentSession = this.currentSessionRef.get();
        return currentSession.isOpen();
    }

    @Override
    public void enqueue(Command command, Command.Priority priority) {
        IOSession currentSession = this.currentSessionRef.get();
        currentSession.enqueue(command, priority);
    }

    @Override
    public boolean hasCommands() {
        IOSession currentSession = this.currentSessionRef.get();
        return currentSession.hasCommands();
    }

    @Override
    public Command poll() {
        IOSession currentSession = this.currentSessionRef.get();
        return currentSession.poll();
    }

    @Override
    public ByteChannel channel() {
        IOSession currentSession = this.currentSessionRef.get();
        return currentSession.channel();
    }

    @Override
    public SocketAddress getRemoteAddress() {
        return this.ioSession.getRemoteAddress();
    }

    @Override
    public SocketAddress getLocalAddress() {
        return this.ioSession.getLocalAddress();
    }

    @Override
    public int getEventMask() {
        IOSession currentSession = this.currentSessionRef.get();
        return currentSession.getEventMask();
    }

    @Override
    public void setEventMask(int ops) {
        IOSession currentSession = this.currentSessionRef.get();
        currentSession.setEventMask(ops);
    }

    @Override
    public void setEvent(int op) {
        IOSession currentSession = this.currentSessionRef.get();
        currentSession.setEvent(op);
    }

    @Override
    public void clearEvent(int op) {
        IOSession currentSession = this.currentSessionRef.get();
        currentSession.clearEvent(op);
    }

    @Override
    public Timeout getSocketTimeout() {
        return this.ioSession.getSocketTimeout();
    }

    @Override
    public void setSocketTimeout(Timeout timeout) {
        this.ioSession.setSocketTimeout(timeout);
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        IOSession currentSession = this.currentSessionRef.get();
        return currentSession.read(dst);
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        IOSession currentSession = this.currentSessionRef.get();
        return currentSession.write(src);
    }

    @Override
    public void updateReadTime() {
        this.ioSession.updateReadTime();
    }

    @Override
    public void updateWriteTime() {
        this.ioSession.updateWriteTime();
    }

    @Override
    public long getLastReadTime() {
        return this.ioSession.getLastReadTime();
    }

    @Override
    public long getLastWriteTime() {
        return this.ioSession.getLastWriteTime();
    }

    @Override
    public long getLastEventTime() {
        return this.ioSession.getLastEventTime();
    }

    public String toString() {
        IOSession currentSession = this.currentSessionRef.get();
        if (currentSession != null) {
            return currentSession.toString();
        }
        return this.ioSession.toString();
    }
}


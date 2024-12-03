/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http2.impl.nio;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javax.net.ssl.SSLSession;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.ConnectionClosedException;
import org.apache.hc.core5.http.EndpointDetails;
import org.apache.hc.core5.http.HttpVersion;
import org.apache.hc.core5.http.ProtocolVersion;
import org.apache.hc.core5.http.impl.nio.HttpConnectionEventHandler;
import org.apache.hc.core5.http.nio.command.CommandSupport;
import org.apache.hc.core5.io.CloseMode;
import org.apache.hc.core5.io.SocketTimeoutExceptionFactory;
import org.apache.hc.core5.reactor.IOSession;
import org.apache.hc.core5.reactor.ProtocolIOSession;
import org.apache.hc.core5.reactor.ssl.TlsDetails;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.Timeout;

abstract class PrefaceHandlerBase
implements HttpConnectionEventHandler {
    final ProtocolIOSession ioSession;
    private final AtomicReference<HttpConnectionEventHandler> protocolHandlerRef;
    private final FutureCallback<ProtocolIOSession> resultCallback;
    private final AtomicBoolean completed;

    PrefaceHandlerBase(ProtocolIOSession ioSession, FutureCallback<ProtocolIOSession> resultCallback) {
        this.ioSession = Args.notNull(ioSession, "I/O session");
        this.protocolHandlerRef = new AtomicReference();
        this.resultCallback = resultCallback;
        this.completed = new AtomicBoolean();
    }

    void startProtocol(HttpConnectionEventHandler protocolHandler, ByteBuffer data) throws IOException {
        this.protocolHandlerRef.set(protocolHandler);
        this.ioSession.upgrade(protocolHandler);
        protocolHandler.connected(this.ioSession);
        if (data != null && data.hasRemaining()) {
            protocolHandler.inputReady(this.ioSession, data);
        }
        if (this.completed.compareAndSet(false, true) && this.resultCallback != null) {
            this.resultCallback.completed(this.ioSession);
        }
    }

    @Override
    public void timeout(IOSession session, Timeout timeout) {
        this.exception(session, SocketTimeoutExceptionFactory.create(timeout));
    }

    @Override
    public void exception(IOSession session, Exception cause) {
        block4: {
            HttpConnectionEventHandler protocolHandler = this.protocolHandlerRef.get();
            try {
                session.close(CloseMode.IMMEDIATE);
                if (protocolHandler != null) {
                    protocolHandler.exception(session, cause);
                } else {
                    CommandSupport.failCommands(session, cause);
                }
            }
            catch (Exception ex) {
                if (!this.completed.compareAndSet(false, true) || this.resultCallback == null) break block4;
                this.resultCallback.failed(ex);
            }
        }
    }

    @Override
    public void disconnected(IOSession session) {
        HttpConnectionEventHandler protocolHandler = this.protocolHandlerRef.getAndSet(null);
        try {
            if (protocolHandler != null) {
                protocolHandler.disconnected(this.ioSession);
            } else {
                CommandSupport.cancelCommands(session);
            }
        }
        finally {
            if (this.completed.compareAndSet(false, true) && this.resultCallback != null) {
                this.resultCallback.failed(new ConnectionClosedException());
            }
        }
    }

    @Override
    public SSLSession getSSLSession() {
        TlsDetails tlsDetails = this.ioSession.getTlsDetails();
        return tlsDetails != null ? tlsDetails.getSSLSession() : null;
    }

    @Override
    public EndpointDetails getEndpointDetails() {
        return null;
    }

    @Override
    public void setSocketTimeout(Timeout timeout) {
        this.ioSession.setSocketTimeout(timeout);
    }

    @Override
    public Timeout getSocketTimeout() {
        return this.ioSession.getSocketTimeout();
    }

    @Override
    public ProtocolVersion getProtocolVersion() {
        return HttpVersion.HTTP_2;
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
    public boolean isOpen() {
        return this.ioSession.isOpen();
    }

    @Override
    public void close() throws IOException {
        this.ioSession.close();
    }

    @Override
    public void close(CloseMode closeMode) {
        this.ioSession.close(closeMode);
    }
}


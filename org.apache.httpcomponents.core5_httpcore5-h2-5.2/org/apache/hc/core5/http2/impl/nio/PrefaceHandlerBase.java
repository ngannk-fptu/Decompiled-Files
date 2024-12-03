/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.concurrent.FutureCallback
 *  org.apache.hc.core5.http.ConnectionClosedException
 *  org.apache.hc.core5.http.EndpointDetails
 *  org.apache.hc.core5.http.HttpVersion
 *  org.apache.hc.core5.http.ProtocolVersion
 *  org.apache.hc.core5.http.impl.nio.HttpConnectionEventHandler
 *  org.apache.hc.core5.http.nio.command.CommandSupport
 *  org.apache.hc.core5.io.CloseMode
 *  org.apache.hc.core5.io.SocketTimeoutExceptionFactory
 *  org.apache.hc.core5.reactor.IOEventHandler
 *  org.apache.hc.core5.reactor.IOSession
 *  org.apache.hc.core5.reactor.ProtocolIOSession
 *  org.apache.hc.core5.reactor.ssl.TlsDetails
 *  org.apache.hc.core5.util.Args
 *  org.apache.hc.core5.util.Timeout
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
import org.apache.hc.core5.reactor.IOEventHandler;
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
        this.ioSession = (ProtocolIOSession)Args.notNull((Object)ioSession, (String)"I/O session");
        this.protocolHandlerRef = new AtomicReference();
        this.resultCallback = resultCallback;
        this.completed = new AtomicBoolean();
    }

    void startProtocol(HttpConnectionEventHandler protocolHandler, ByteBuffer data) throws IOException {
        this.protocolHandlerRef.set(protocolHandler);
        this.ioSession.upgrade((IOEventHandler)protocolHandler);
        protocolHandler.connected((IOSession)this.ioSession);
        if (data != null && data.hasRemaining()) {
            protocolHandler.inputReady((IOSession)this.ioSession, data);
        }
        if (this.completed.compareAndSet(false, true) && this.resultCallback != null) {
            this.resultCallback.completed((Object)this.ioSession);
        }
    }

    public void timeout(IOSession session, Timeout timeout) {
        this.exception(session, SocketTimeoutExceptionFactory.create((Timeout)timeout));
    }

    public void exception(IOSession session, Exception cause) {
        block4: {
            HttpConnectionEventHandler protocolHandler = this.protocolHandlerRef.get();
            try {
                session.close(CloseMode.IMMEDIATE);
                if (protocolHandler != null) {
                    protocolHandler.exception(session, cause);
                } else {
                    CommandSupport.failCommands((IOSession)session, (Exception)cause);
                }
            }
            catch (Exception ex) {
                if (!this.completed.compareAndSet(false, true) || this.resultCallback == null) break block4;
                this.resultCallback.failed(ex);
            }
        }
    }

    public void disconnected(IOSession session) {
        HttpConnectionEventHandler protocolHandler = this.protocolHandlerRef.getAndSet(null);
        try {
            if (protocolHandler != null) {
                protocolHandler.disconnected((IOSession)this.ioSession);
            } else {
                CommandSupport.cancelCommands((IOSession)session);
            }
        }
        finally {
            if (this.completed.compareAndSet(false, true) && this.resultCallback != null) {
                this.resultCallback.failed((Exception)new ConnectionClosedException());
            }
        }
    }

    public SSLSession getSSLSession() {
        TlsDetails tlsDetails = this.ioSession.getTlsDetails();
        return tlsDetails != null ? tlsDetails.getSSLSession() : null;
    }

    public EndpointDetails getEndpointDetails() {
        return null;
    }

    public void setSocketTimeout(Timeout timeout) {
        this.ioSession.setSocketTimeout(timeout);
    }

    public Timeout getSocketTimeout() {
        return this.ioSession.getSocketTimeout();
    }

    public ProtocolVersion getProtocolVersion() {
        return HttpVersion.HTTP_2;
    }

    public SocketAddress getRemoteAddress() {
        return this.ioSession.getRemoteAddress();
    }

    public SocketAddress getLocalAddress() {
        return this.ioSession.getLocalAddress();
    }

    public boolean isOpen() {
        return this.ioSession.isOpen();
    }

    public void close() throws IOException {
        this.ioSession.close();
    }

    public void close(CloseMode closeMode) {
        this.ioSession.close(closeMode);
    }
}


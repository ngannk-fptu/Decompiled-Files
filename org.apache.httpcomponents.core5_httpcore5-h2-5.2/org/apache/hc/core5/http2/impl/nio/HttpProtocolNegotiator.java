/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Internal
 *  org.apache.hc.core5.concurrent.FutureCallback
 *  org.apache.hc.core5.http.ConnectionClosedException
 *  org.apache.hc.core5.http.EndpointDetails
 *  org.apache.hc.core5.http.HttpVersion
 *  org.apache.hc.core5.http.ProtocolVersion
 *  org.apache.hc.core5.http.impl.nio.HttpConnectionEventHandler
 *  org.apache.hc.core5.http.nio.command.CommandSupport
 *  org.apache.hc.core5.io.CloseMode
 *  org.apache.hc.core5.io.SocketTimeoutExceptionFactory
 *  org.apache.hc.core5.reactor.IOSession
 *  org.apache.hc.core5.reactor.ProtocolIOSession
 *  org.apache.hc.core5.reactor.ssl.TlsDetails
 *  org.apache.hc.core5.util.Args
 *  org.apache.hc.core5.util.TextUtils
 *  org.apache.hc.core5.util.Timeout
 */
package org.apache.hc.core5.http2.impl.nio;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javax.net.ssl.SSLSession;
import org.apache.hc.core5.annotation.Internal;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.ConnectionClosedException;
import org.apache.hc.core5.http.EndpointDetails;
import org.apache.hc.core5.http.HttpVersion;
import org.apache.hc.core5.http.ProtocolVersion;
import org.apache.hc.core5.http.impl.nio.HttpConnectionEventHandler;
import org.apache.hc.core5.http.nio.command.CommandSupport;
import org.apache.hc.core5.http2.impl.nio.ProtocolNegotiationException;
import org.apache.hc.core5.http2.ssl.ApplicationProtocol;
import org.apache.hc.core5.io.CloseMode;
import org.apache.hc.core5.io.SocketTimeoutExceptionFactory;
import org.apache.hc.core5.reactor.IOSession;
import org.apache.hc.core5.reactor.ProtocolIOSession;
import org.apache.hc.core5.reactor.ssl.TlsDetails;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.TextUtils;
import org.apache.hc.core5.util.Timeout;

@Internal
public class HttpProtocolNegotiator
implements HttpConnectionEventHandler {
    private final ProtocolIOSession ioSession;
    private final FutureCallback<ProtocolIOSession> resultCallback;
    private final AtomicBoolean completed;
    private final AtomicReference<ProtocolVersion> negotiatedProtocolRef;

    public HttpProtocolNegotiator(ProtocolIOSession ioSession, FutureCallback<ProtocolIOSession> resultCallback) {
        this.ioSession = (ProtocolIOSession)Args.notNull((Object)ioSession, (String)"I/O session");
        this.resultCallback = resultCallback;
        this.completed = new AtomicBoolean();
        this.negotiatedProtocolRef = new AtomicReference();
    }

    void startProtocol(HttpVersion httpVersion) {
        this.ioSession.switchProtocol(httpVersion == HttpVersion.HTTP_2 ? ApplicationProtocol.HTTP_2.id : ApplicationProtocol.HTTP_1_1.id, this.resultCallback);
        this.negotiatedProtocolRef.set((ProtocolVersion)httpVersion);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public void connected(IOSession session) throws IOException {
        HttpVersion httpVersion;
        TlsDetails tlsDetails = this.ioSession.getTlsDetails();
        if (tlsDetails != null) {
            String appProtocol = tlsDetails.getApplicationProtocol();
            if (TextUtils.isEmpty((CharSequence)appProtocol)) {
                httpVersion = HttpVersion.HTTP_1_1;
            } else if (appProtocol.equals(ApplicationProtocol.HTTP_1_1.id)) {
                httpVersion = HttpVersion.HTTP_1_1;
            } else {
                if (!appProtocol.equals(ApplicationProtocol.HTTP_2.id)) throw new ProtocolNegotiationException("Unsupported application protocol: " + appProtocol);
                httpVersion = HttpVersion.HTTP_2;
            }
        } else {
            httpVersion = HttpVersion.HTTP_1_1;
        }
        this.startProtocol(httpVersion);
    }

    public void inputReady(IOSession session, ByteBuffer src) throws IOException {
        throw new ProtocolNegotiationException("Unexpected input");
    }

    public void outputReady(IOSession session) throws IOException {
        throw new ProtocolNegotiationException("Unexpected output");
    }

    public void timeout(IOSession session, Timeout timeout) {
        this.exception(session, SocketTimeoutExceptionFactory.create((Timeout)timeout));
    }

    public void exception(IOSession session, Exception cause) {
        block2: {
            try {
                session.close(CloseMode.IMMEDIATE);
                CommandSupport.failCommands((IOSession)session, (Exception)cause);
            }
            catch (Exception ex) {
                if (!this.completed.compareAndSet(false, true) || this.resultCallback == null) break block2;
                this.resultCallback.failed(ex);
            }
        }
    }

    public void disconnected(IOSession session) {
        try {
            CommandSupport.cancelCommands((IOSession)session);
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
        return this.negotiatedProtocolRef.get();
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

    public String toString() {
        return this.getClass().getName();
    }
}


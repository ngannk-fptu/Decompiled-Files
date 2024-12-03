/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.concurrent.FutureCallback
 *  org.apache.hc.core5.http.EndpointDetails
 *  org.apache.hc.core5.http.HttpConnection
 *  org.apache.hc.core5.http.HttpVersion
 *  org.apache.hc.core5.http.ProtocolVersion
 *  org.apache.hc.core5.http.nio.command.ShutdownCommand
 *  org.apache.hc.core5.io.CloseMode
 *  org.apache.hc.core5.net.NamedEndpoint
 *  org.apache.hc.core5.reactor.Command
 *  org.apache.hc.core5.reactor.Command$Priority
 *  org.apache.hc.core5.reactor.IOEventHandler
 *  org.apache.hc.core5.reactor.IOSession
 *  org.apache.hc.core5.reactor.ProtocolIOSession
 *  org.apache.hc.core5.reactor.ssl.SSLBufferMode
 *  org.apache.hc.core5.reactor.ssl.SSLSessionInitializer
 *  org.apache.hc.core5.reactor.ssl.SSLSessionVerifier
 *  org.apache.hc.core5.reactor.ssl.TlsDetails
 *  org.apache.hc.core5.reactor.ssl.TransportSecurityLayer
 *  org.apache.hc.core5.util.Identifiable
 *  org.apache.hc.core5.util.Timeout
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.hc.client5.http.impl.nio;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import org.apache.hc.client5.http.nio.ManagedAsyncClientConnection;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.EndpointDetails;
import org.apache.hc.core5.http.HttpConnection;
import org.apache.hc.core5.http.HttpVersion;
import org.apache.hc.core5.http.ProtocolVersion;
import org.apache.hc.core5.http.nio.command.ShutdownCommand;
import org.apache.hc.core5.io.CloseMode;
import org.apache.hc.core5.net.NamedEndpoint;
import org.apache.hc.core5.reactor.Command;
import org.apache.hc.core5.reactor.IOEventHandler;
import org.apache.hc.core5.reactor.IOSession;
import org.apache.hc.core5.reactor.ProtocolIOSession;
import org.apache.hc.core5.reactor.ssl.SSLBufferMode;
import org.apache.hc.core5.reactor.ssl.SSLSessionInitializer;
import org.apache.hc.core5.reactor.ssl.SSLSessionVerifier;
import org.apache.hc.core5.reactor.ssl.TlsDetails;
import org.apache.hc.core5.reactor.ssl.TransportSecurityLayer;
import org.apache.hc.core5.util.Identifiable;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class DefaultManagedAsyncClientConnection
implements ManagedAsyncClientConnection,
Identifiable {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultManagedAsyncClientConnection.class);
    private final IOSession ioSession;
    private final Timeout socketTimeout;
    private final AtomicBoolean closed;

    public DefaultManagedAsyncClientConnection(IOSession ioSession) {
        this.ioSession = ioSession;
        this.socketTimeout = ioSession.getSocketTimeout();
        this.closed = new AtomicBoolean();
    }

    public String getId() {
        return this.ioSession.getId();
    }

    public void close(CloseMode closeMode) {
        if (this.closed.compareAndSet(false, true)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("{} Shutdown connection {}", (Object)this.getId(), (Object)closeMode);
            }
            this.ioSession.close(closeMode);
        }
    }

    public void close() throws IOException {
        if (this.closed.compareAndSet(false, true)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("{} Close connection", (Object)this.getId());
            }
            this.ioSession.enqueue((Command)new ShutdownCommand(CloseMode.GRACEFUL), Command.Priority.IMMEDIATE);
        }
    }

    public boolean isOpen() {
        return this.ioSession.isOpen();
    }

    public void setSocketTimeout(Timeout timeout) {
        this.ioSession.setSocketTimeout(timeout);
    }

    public Timeout getSocketTimeout() {
        return this.ioSession.getSocketTimeout();
    }

    public SocketAddress getRemoteAddress() {
        return this.ioSession.getRemoteAddress();
    }

    public SocketAddress getLocalAddress() {
        return this.ioSession.getLocalAddress();
    }

    public EndpointDetails getEndpointDetails() {
        IOEventHandler handler = this.ioSession.getHandler();
        if (handler instanceof HttpConnection) {
            return ((HttpConnection)handler).getEndpointDetails();
        }
        return null;
    }

    public ProtocolVersion getProtocolVersion() {
        ProtocolVersion version;
        IOEventHandler handler = this.ioSession.getHandler();
        if (handler instanceof HttpConnection && (version = ((HttpConnection)handler).getProtocolVersion()) != null) {
            return version;
        }
        return HttpVersion.DEFAULT;
    }

    public void startTls(SSLContext sslContext, NamedEndpoint endpoint, SSLBufferMode sslBufferMode, SSLSessionInitializer initializer, SSLSessionVerifier verifier, Timeout handshakeTimeout, FutureCallback<TransportSecurityLayer> callback) throws UnsupportedOperationException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("{} start TLS", (Object)this.getId());
        }
        if (!(this.ioSession instanceof TransportSecurityLayer)) {
            throw new UnsupportedOperationException("TLS upgrade not supported");
        }
        ((TransportSecurityLayer)this.ioSession).startTls(sslContext, endpoint, sslBufferMode, initializer, verifier, handshakeTimeout, callback);
    }

    public void startTls(SSLContext sslContext, NamedEndpoint endpoint, SSLBufferMode sslBufferMode, SSLSessionInitializer initializer, SSLSessionVerifier verifier, Timeout handshakeTimeout) throws UnsupportedOperationException {
        this.startTls(sslContext, endpoint, sslBufferMode, initializer, verifier, handshakeTimeout, null);
    }

    public TlsDetails getTlsDetails() {
        return this.ioSession instanceof TransportSecurityLayer ? ((TransportSecurityLayer)this.ioSession).getTlsDetails() : null;
    }

    public SSLSession getSSLSession() {
        TlsDetails tlsDetails = this.getTlsDetails();
        return tlsDetails != null ? tlsDetails.getSSLSession() : null;
    }

    @Override
    public void submitCommand(Command command, Command.Priority priority) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("{} {} with {} priority", new Object[]{this.getId(), command.getClass().getSimpleName(), priority});
        }
        this.ioSession.enqueue(command, Command.Priority.IMMEDIATE);
    }

    @Override
    public void passivate() {
        this.ioSession.setSocketTimeout(Timeout.ZERO_MILLISECONDS);
    }

    @Override
    public void activate() {
        this.ioSession.setSocketTimeout(this.socketTimeout);
    }

    @Override
    public void switchProtocol(String protocolId, FutureCallback<ProtocolIOSession> callback) throws UnsupportedOperationException {
        if (!(this.ioSession instanceof ProtocolIOSession)) {
            throw new UnsupportedOperationException("Protocol switch not supported");
        }
        ((ProtocolIOSession)this.ioSession).switchProtocol(protocolId, callback);
    }
}


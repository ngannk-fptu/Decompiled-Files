/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http2.impl.nio;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import javax.net.ssl.SSLSession;
import org.apache.hc.core5.http.EndpointDetails;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.ProtocolVersion;
import org.apache.hc.core5.http.impl.nio.HttpConnectionEventHandler;
import org.apache.hc.core5.http2.impl.nio.AbstractH2StreamMultiplexer;
import org.apache.hc.core5.io.CloseMode;
import org.apache.hc.core5.reactor.IOSession;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.Timeout;

class AbstractH2IOEventHandler
implements HttpConnectionEventHandler {
    final AbstractH2StreamMultiplexer streamMultiplexer;

    AbstractH2IOEventHandler(AbstractH2StreamMultiplexer streamMultiplexer) {
        this.streamMultiplexer = Args.notNull(streamMultiplexer, "Stream multiplexer");
    }

    @Override
    public void connected(IOSession session) throws IOException {
        try {
            this.streamMultiplexer.onConnect();
        }
        catch (HttpException ex) {
            this.streamMultiplexer.onException(ex);
        }
    }

    @Override
    public void inputReady(IOSession session, ByteBuffer src) throws IOException {
        try {
            this.streamMultiplexer.onInput(src);
        }
        catch (HttpException ex) {
            this.streamMultiplexer.onException(ex);
        }
    }

    @Override
    public void outputReady(IOSession session) throws IOException {
        try {
            this.streamMultiplexer.onOutput();
        }
        catch (HttpException ex) {
            this.streamMultiplexer.onException(ex);
        }
    }

    @Override
    public void timeout(IOSession session, Timeout timeout) throws IOException {
        try {
            this.streamMultiplexer.onTimeout(timeout);
        }
        catch (HttpException ex) {
            this.streamMultiplexer.onException(ex);
        }
    }

    @Override
    public void exception(IOSession session, Exception cause) {
        this.streamMultiplexer.onException(cause);
    }

    @Override
    public void disconnected(IOSession session) {
        this.streamMultiplexer.onDisconnect();
    }

    @Override
    public void close() throws IOException {
        this.streamMultiplexer.close();
    }

    @Override
    public void close(CloseMode closeMode) {
        this.streamMultiplexer.close(closeMode);
    }

    @Override
    public boolean isOpen() {
        return this.streamMultiplexer.isOpen();
    }

    @Override
    public void setSocketTimeout(Timeout timeout) {
        this.streamMultiplexer.setSocketTimeout(timeout);
    }

    @Override
    public SSLSession getSSLSession() {
        return this.streamMultiplexer.getSSLSession();
    }

    @Override
    public EndpointDetails getEndpointDetails() {
        return this.streamMultiplexer.getEndpointDetails();
    }

    @Override
    public Timeout getSocketTimeout() {
        return this.streamMultiplexer.getSocketTimeout();
    }

    @Override
    public ProtocolVersion getProtocolVersion() {
        return this.streamMultiplexer.getProtocolVersion();
    }

    @Override
    public SocketAddress getRemoteAddress() {
        return this.streamMultiplexer.getRemoteAddress();
    }

    @Override
    public SocketAddress getLocalAddress() {
        return this.streamMultiplexer.getLocalAddress();
    }
}


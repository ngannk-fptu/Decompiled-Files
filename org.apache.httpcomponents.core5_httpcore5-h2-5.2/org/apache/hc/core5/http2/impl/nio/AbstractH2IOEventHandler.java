/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.http.EndpointDetails
 *  org.apache.hc.core5.http.HttpException
 *  org.apache.hc.core5.http.ProtocolVersion
 *  org.apache.hc.core5.http.impl.nio.HttpConnectionEventHandler
 *  org.apache.hc.core5.io.CloseMode
 *  org.apache.hc.core5.reactor.IOSession
 *  org.apache.hc.core5.util.Args
 *  org.apache.hc.core5.util.Timeout
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
        this.streamMultiplexer = (AbstractH2StreamMultiplexer)Args.notNull((Object)streamMultiplexer, (String)"Stream multiplexer");
    }

    public void connected(IOSession session) throws IOException {
        try {
            this.streamMultiplexer.onConnect();
        }
        catch (HttpException ex) {
            this.streamMultiplexer.onException((Exception)((Object)ex));
        }
    }

    public void inputReady(IOSession session, ByteBuffer src) throws IOException {
        try {
            this.streamMultiplexer.onInput(src);
        }
        catch (HttpException ex) {
            this.streamMultiplexer.onException((Exception)((Object)ex));
        }
    }

    public void outputReady(IOSession session) throws IOException {
        try {
            this.streamMultiplexer.onOutput();
        }
        catch (HttpException ex) {
            this.streamMultiplexer.onException((Exception)((Object)ex));
        }
    }

    public void timeout(IOSession session, Timeout timeout) throws IOException {
        try {
            this.streamMultiplexer.onTimeout(timeout);
        }
        catch (HttpException ex) {
            this.streamMultiplexer.onException((Exception)((Object)ex));
        }
    }

    public void exception(IOSession session, Exception cause) {
        this.streamMultiplexer.onException(cause);
    }

    public void disconnected(IOSession session) {
        this.streamMultiplexer.onDisconnect();
    }

    public void close() throws IOException {
        this.streamMultiplexer.close();
    }

    public void close(CloseMode closeMode) {
        this.streamMultiplexer.close(closeMode);
    }

    public boolean isOpen() {
        return this.streamMultiplexer.isOpen();
    }

    public void setSocketTimeout(Timeout timeout) {
        this.streamMultiplexer.setSocketTimeout(timeout);
    }

    public SSLSession getSSLSession() {
        return this.streamMultiplexer.getSSLSession();
    }

    public EndpointDetails getEndpointDetails() {
        return this.streamMultiplexer.getEndpointDetails();
    }

    public Timeout getSocketTimeout() {
        return this.streamMultiplexer.getSocketTimeout();
    }

    public ProtocolVersion getProtocolVersion() {
        return this.streamMultiplexer.getProtocolVersion();
    }

    public SocketAddress getRemoteAddress() {
        return this.streamMultiplexer.getRemoteAddress();
    }

    public SocketAddress getLocalAddress() {
        return this.streamMultiplexer.getLocalAddress();
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http2.impl.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.hc.core5.annotation.Internal;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.ConnectionClosedException;
import org.apache.hc.core5.http.URIScheme;
import org.apache.hc.core5.http.impl.nio.BufferedData;
import org.apache.hc.core5.http.impl.nio.ServerHttp1IOEventHandler;
import org.apache.hc.core5.http.impl.nio.ServerHttp1StreamDuplexer;
import org.apache.hc.core5.http.impl.nio.ServerHttp1StreamDuplexerFactory;
import org.apache.hc.core5.http2.HttpVersionPolicy;
import org.apache.hc.core5.http2.impl.nio.ClientHttpProtocolNegotiator;
import org.apache.hc.core5.http2.impl.nio.ProtocolNegotiationException;
import org.apache.hc.core5.http2.impl.nio.ProtocolNegotiatorBase;
import org.apache.hc.core5.http2.impl.nio.ServerH2IOEventHandler;
import org.apache.hc.core5.http2.impl.nio.ServerH2StreamMultiplexerFactory;
import org.apache.hc.core5.http2.ssl.ApplicationProtocol;
import org.apache.hc.core5.reactor.IOSession;
import org.apache.hc.core5.reactor.ProtocolIOSession;
import org.apache.hc.core5.reactor.ssl.TlsDetails;
import org.apache.hc.core5.util.Args;

@Internal
public class ServerHttpProtocolNegotiator
extends ProtocolNegotiatorBase {
    static final byte[] PREFACE = ClientHttpProtocolNegotiator.PREFACE;
    private final ServerHttp1StreamDuplexerFactory http1StreamHandlerFactory;
    private final ServerH2StreamMultiplexerFactory http2StreamHandlerFactory;
    private final HttpVersionPolicy versionPolicy;
    private final BufferedData inBuf;
    private final AtomicBoolean initialized;
    private volatile boolean expectValidH2Preface;

    public ServerHttpProtocolNegotiator(ProtocolIOSession ioSession, ServerHttp1StreamDuplexerFactory http1StreamHandlerFactory, ServerH2StreamMultiplexerFactory http2StreamHandlerFactory, HttpVersionPolicy versionPolicy) {
        this(ioSession, http1StreamHandlerFactory, http2StreamHandlerFactory, versionPolicy, null);
    }

    public ServerHttpProtocolNegotiator(ProtocolIOSession ioSession, ServerHttp1StreamDuplexerFactory http1StreamHandlerFactory, ServerH2StreamMultiplexerFactory http2StreamHandlerFactory, HttpVersionPolicy versionPolicy, FutureCallback<ProtocolIOSession> resultCallback) {
        super(ioSession, resultCallback);
        this.http1StreamHandlerFactory = Args.notNull(http1StreamHandlerFactory, "HTTP/1.1 stream handler factory");
        this.http2StreamHandlerFactory = Args.notNull(http2StreamHandlerFactory, "HTTP/2 stream handler factory");
        this.versionPolicy = versionPolicy != null ? versionPolicy : HttpVersionPolicy.NEGOTIATE;
        this.inBuf = BufferedData.allocate(1024);
        this.initialized = new AtomicBoolean();
    }

    private void startHttp1(TlsDetails tlsDetails, ByteBuffer data) throws IOException {
        ServerHttp1StreamDuplexer http1StreamHandler = this.http1StreamHandlerFactory.create(tlsDetails != null ? URIScheme.HTTPS.id : URIScheme.HTTP.id, this.ioSession);
        this.startProtocol(new ServerHttp1IOEventHandler(http1StreamHandler), data);
    }

    private void startHttp2(ByteBuffer data) throws IOException {
        this.startProtocol(new ServerH2IOEventHandler(this.http2StreamHandlerFactory.create(this.ioSession)), data);
    }

    private void initialize() throws IOException {
        TlsDetails tlsDetails = this.ioSession.getTlsDetails();
        switch (this.versionPolicy) {
            case NEGOTIATE: {
                if (tlsDetails == null || !ApplicationProtocol.HTTP_2.id.equals(tlsDetails.getApplicationProtocol())) break;
                this.expectValidH2Preface = true;
                break;
            }
            case FORCE_HTTP_2: {
                if (tlsDetails != null && ApplicationProtocol.HTTP_1_1.id.equals(tlsDetails.getApplicationProtocol())) break;
                this.expectValidH2Preface = true;
                break;
            }
            case FORCE_HTTP_1: {
                this.startHttp1(tlsDetails, null);
            }
        }
    }

    @Override
    public void connected(IOSession session) throws IOException {
        if (this.initialized.compareAndSet(false, true)) {
            this.initialize();
        }
    }

    @Override
    public void inputReady(IOSession session, ByteBuffer src) throws IOException {
        ByteBuffer data;
        int bytesRead;
        if (src != null) {
            this.inBuf.put(src);
        }
        boolean endOfStream = false;
        if (this.inBuf.length() < PREFACE.length && (bytesRead = this.inBuf.readFrom(session)) == -1) {
            endOfStream = true;
        }
        if ((data = this.inBuf.data()).remaining() >= PREFACE.length) {
            boolean validH2Preface = true;
            for (int i = 0; i < PREFACE.length; ++i) {
                if (data.get() == PREFACE[i]) continue;
                if (this.expectValidH2Preface) {
                    throw new ProtocolNegotiationException("Unexpected HTTP/2 preface");
                }
                validH2Preface = false;
            }
            if (validH2Preface) {
                this.startHttp2(data.hasRemaining() ? data : null);
            } else {
                data.rewind();
                this.startHttp1(this.ioSession.getTlsDetails(), data);
            }
        } else if (endOfStream) {
            throw new ConnectionClosedException();
        }
    }

    @Override
    public void outputReady(IOSession session) throws IOException {
        if (this.initialized.compareAndSet(false, true)) {
            this.initialize();
        }
    }

    public String toString() {
        return this.getClass().getName() + "/" + (Object)((Object)this.versionPolicy);
    }
}


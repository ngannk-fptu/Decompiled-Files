/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http2.impl.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.hc.core5.annotation.Internal;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.impl.nio.BufferedData;
import org.apache.hc.core5.http.impl.nio.ClientHttp1IOEventHandler;
import org.apache.hc.core5.http.impl.nio.ClientHttp1StreamDuplexerFactory;
import org.apache.hc.core5.http2.HttpVersionPolicy;
import org.apache.hc.core5.http2.impl.nio.ClientH2IOEventHandler;
import org.apache.hc.core5.http2.impl.nio.ClientH2StreamMultiplexerFactory;
import org.apache.hc.core5.http2.impl.nio.ProtocolNegotiationException;
import org.apache.hc.core5.http2.impl.nio.ProtocolNegotiatorBase;
import org.apache.hc.core5.http2.ssl.ApplicationProtocol;
import org.apache.hc.core5.reactor.IOSession;
import org.apache.hc.core5.reactor.ProtocolIOSession;
import org.apache.hc.core5.reactor.ssl.TlsDetails;
import org.apache.hc.core5.util.Args;

@Internal
public class ClientHttpProtocolNegotiator
extends ProtocolNegotiatorBase {
    static final byte[] PREFACE = new byte[]{80, 82, 73, 32, 42, 32, 72, 84, 84, 80, 47, 50, 46, 48, 13, 10, 13, 10, 83, 77, 13, 10, 13, 10};
    private final ClientHttp1StreamDuplexerFactory http1StreamHandlerFactory;
    private final ClientH2StreamMultiplexerFactory http2StreamHandlerFactory;
    private final HttpVersionPolicy versionPolicy;
    private final AtomicBoolean initialized;
    private volatile ByteBuffer preface;
    private volatile BufferedData inBuf;

    public ClientHttpProtocolNegotiator(ProtocolIOSession ioSession, ClientHttp1StreamDuplexerFactory http1StreamHandlerFactory, ClientH2StreamMultiplexerFactory http2StreamHandlerFactory, HttpVersionPolicy versionPolicy) {
        this(ioSession, http1StreamHandlerFactory, http2StreamHandlerFactory, versionPolicy, null);
    }

    public ClientHttpProtocolNegotiator(ProtocolIOSession ioSession, ClientHttp1StreamDuplexerFactory http1StreamHandlerFactory, ClientH2StreamMultiplexerFactory http2StreamHandlerFactory, HttpVersionPolicy versionPolicy, FutureCallback<ProtocolIOSession> resultCallback) {
        super(ioSession, resultCallback);
        this.http1StreamHandlerFactory = Args.notNull(http1StreamHandlerFactory, "HTTP/1.1 stream handler factory");
        this.http2StreamHandlerFactory = Args.notNull(http2StreamHandlerFactory, "HTTP/2 stream handler factory");
        this.versionPolicy = versionPolicy != null ? versionPolicy : HttpVersionPolicy.NEGOTIATE;
        this.initialized = new AtomicBoolean();
    }

    private void startHttp1() throws IOException {
        ByteBuffer data = this.inBuf != null ? this.inBuf.data() : null;
        this.startProtocol(new ClientHttp1IOEventHandler(this.http1StreamHandlerFactory.create(this.ioSession)), data);
        if (this.inBuf != null) {
            this.inBuf.clear();
        }
    }

    private void startHttp2() throws IOException {
        ByteBuffer data = this.inBuf != null ? this.inBuf.data() : null;
        this.startProtocol(new ClientH2IOEventHandler(this.http2StreamHandlerFactory.create(this.ioSession)), data);
        if (this.inBuf != null) {
            this.inBuf.clear();
        }
    }

    private void initialize() throws IOException {
        switch (this.versionPolicy) {
            case NEGOTIATE: {
                TlsDetails tlsDetails = this.ioSession.getTlsDetails();
                if (tlsDetails == null || !ApplicationProtocol.HTTP_2.id.equals(tlsDetails.getApplicationProtocol())) break;
                this.preface = ByteBuffer.wrap(PREFACE);
                break;
            }
            case FORCE_HTTP_2: {
                this.preface = ByteBuffer.wrap(PREFACE);
            }
        }
        if (this.preface == null) {
            this.startHttp1();
        } else {
            this.ioSession.setEvent(4);
        }
    }

    private void writeOutPreface(IOSession session) throws IOException {
        if (this.preface.hasRemaining()) {
            session.write(this.preface);
        }
        if (!this.preface.hasRemaining()) {
            session.clearEvent(4);
            this.startHttp2();
            this.preface = null;
        }
    }

    @Override
    public void connected(IOSession session) throws IOException {
        if (this.initialized.compareAndSet(false, true)) {
            this.initialize();
        }
        if (this.preface != null) {
            this.writeOutPreface(session);
        }
    }

    @Override
    public void inputReady(IOSession session, ByteBuffer src) throws IOException {
        if (src != null) {
            if (this.inBuf == null) {
                this.inBuf = BufferedData.allocate(src.remaining());
            }
            this.inBuf.put(src);
        }
        if (this.preface == null) {
            throw new ProtocolNegotiationException("Unexpected input");
        }
        this.writeOutPreface(session);
    }

    @Override
    public void outputReady(IOSession session) throws IOException {
        if (this.initialized.compareAndSet(false, true)) {
            this.initialize();
        }
        if (this.preface == null) {
            throw new ProtocolNegotiationException("Unexpected output");
        }
        this.writeOutPreface(session);
    }

    public String toString() {
        return this.getClass().getName() + "/" + (Object)((Object)this.versionPolicy);
    }
}


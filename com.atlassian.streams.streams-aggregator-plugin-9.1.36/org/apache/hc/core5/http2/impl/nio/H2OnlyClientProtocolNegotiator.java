/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http2.impl.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.hc.core5.annotation.Internal;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http2.impl.nio.ClientH2IOEventHandler;
import org.apache.hc.core5.http2.impl.nio.ClientH2StreamMultiplexer;
import org.apache.hc.core5.http2.impl.nio.ClientH2StreamMultiplexerFactory;
import org.apache.hc.core5.http2.impl.nio.ClientHttpProtocolNegotiator;
import org.apache.hc.core5.http2.impl.nio.ProtocolNegotiationException;
import org.apache.hc.core5.http2.impl.nio.ProtocolNegotiatorBase;
import org.apache.hc.core5.http2.ssl.ApplicationProtocol;
import org.apache.hc.core5.reactor.IOSession;
import org.apache.hc.core5.reactor.ProtocolIOSession;
import org.apache.hc.core5.reactor.ssl.TlsDetails;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.TextUtils;

@Internal
public class H2OnlyClientProtocolNegotiator
extends ProtocolNegotiatorBase {
    private final ClientH2StreamMultiplexerFactory http2StreamHandlerFactory;
    private final boolean strictALPNHandshake;
    private final AtomicBoolean initialized;
    private volatile ByteBuffer preface;

    public H2OnlyClientProtocolNegotiator(ProtocolIOSession ioSession, ClientH2StreamMultiplexerFactory http2StreamHandlerFactory, boolean strictALPNHandshake) {
        this(ioSession, http2StreamHandlerFactory, strictALPNHandshake, null);
    }

    public H2OnlyClientProtocolNegotiator(ProtocolIOSession ioSession, ClientH2StreamMultiplexerFactory http2StreamHandlerFactory, boolean strictALPNHandshake, FutureCallback<ProtocolIOSession> resultCallback) {
        super(ioSession, resultCallback);
        this.http2StreamHandlerFactory = Args.notNull(http2StreamHandlerFactory, "HTTP/2 stream handler factory");
        this.strictALPNHandshake = strictALPNHandshake;
        this.initialized = new AtomicBoolean();
    }

    private void initialize() throws IOException {
        TlsDetails tlsDetails = this.ioSession.getTlsDetails();
        if (tlsDetails != null) {
            String applicationProtocol = tlsDetails.getApplicationProtocol();
            if (TextUtils.isEmpty(applicationProtocol)) {
                if (this.strictALPNHandshake) {
                    throw new ProtocolNegotiationException("ALPN: missing application protocol");
                }
            } else if (!ApplicationProtocol.HTTP_2.id.equals(applicationProtocol)) {
                throw new ProtocolNegotiationException("ALPN: unexpected application protocol '" + applicationProtocol + "'");
            }
        }
        this.preface = ByteBuffer.wrap(ClientHttpProtocolNegotiator.PREFACE);
        this.ioSession.setEvent(4);
    }

    private void writeOutPreface(IOSession session) throws IOException {
        if (this.preface.hasRemaining()) {
            session.write(this.preface);
        }
        if (!this.preface.hasRemaining()) {
            session.clearEvent(4);
            ClientH2StreamMultiplexer streamMultiplexer = this.http2StreamHandlerFactory.create(this.ioSession);
            this.startProtocol(new ClientH2IOEventHandler(streamMultiplexer), null);
            this.preface = null;
        }
    }

    @Override
    public void connected(IOSession session) throws IOException {
        if (this.initialized.compareAndSet(false, true)) {
            this.initialize();
        }
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

    @Override
    public void inputReady(IOSession session, ByteBuffer src) throws IOException {
        if (src != null) {
            throw new ProtocolNegotiationException("Unexpected input");
        }
        if (this.preface == null) {
            throw new ProtocolNegotiationException("Unexpected input");
        }
        this.writeOutPreface(session);
    }

    public String toString() {
        return this.getClass().getName();
    }
}


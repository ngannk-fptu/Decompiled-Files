/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Internal
 *  org.apache.hc.core5.concurrent.FutureCallback
 *  org.apache.hc.core5.http.ConnectionClosedException
 *  org.apache.hc.core5.http.impl.nio.BufferedData
 *  org.apache.hc.core5.reactor.IOSession
 *  org.apache.hc.core5.reactor.ProtocolIOSession
 *  org.apache.hc.core5.util.Args
 */
package org.apache.hc.core5.http2.impl.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import org.apache.hc.core5.annotation.Internal;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.ConnectionClosedException;
import org.apache.hc.core5.http.impl.nio.BufferedData;
import org.apache.hc.core5.http2.impl.nio.ClientH2PrefaceHandler;
import org.apache.hc.core5.http2.impl.nio.PrefaceHandlerBase;
import org.apache.hc.core5.http2.impl.nio.ProtocolNegotiationException;
import org.apache.hc.core5.http2.impl.nio.ServerH2IOEventHandler;
import org.apache.hc.core5.http2.impl.nio.ServerH2StreamMultiplexerFactory;
import org.apache.hc.core5.reactor.IOSession;
import org.apache.hc.core5.reactor.ProtocolIOSession;
import org.apache.hc.core5.util.Args;

@Internal
public class ServerH2PrefaceHandler
extends PrefaceHandlerBase {
    static final byte[] PREFACE = ClientH2PrefaceHandler.PREFACE;
    private final ServerH2StreamMultiplexerFactory http2StreamHandlerFactory;
    private final BufferedData inBuf;

    public ServerH2PrefaceHandler(ProtocolIOSession ioSession, ServerH2StreamMultiplexerFactory http2StreamHandlerFactory) {
        this(ioSession, http2StreamHandlerFactory, null);
    }

    public ServerH2PrefaceHandler(ProtocolIOSession ioSession, ServerH2StreamMultiplexerFactory http2StreamHandlerFactory, FutureCallback<ProtocolIOSession> resultCallback) {
        super(ioSession, resultCallback);
        this.http2StreamHandlerFactory = (ServerH2StreamMultiplexerFactory)Args.notNull((Object)http2StreamHandlerFactory, (String)"HTTP/2 stream handler factory");
        this.inBuf = BufferedData.allocate((int)1024);
    }

    public void connected(IOSession session) throws IOException {
    }

    public void inputReady(IOSession session, ByteBuffer src) throws IOException {
        ByteBuffer data;
        int bytesRead;
        if (src != null) {
            this.inBuf.put(src);
        }
        boolean endOfStream = false;
        if (this.inBuf.length() < PREFACE.length && (bytesRead = this.inBuf.readFrom((ReadableByteChannel)session)) == -1) {
            endOfStream = true;
        }
        if ((data = this.inBuf.data()).remaining() >= PREFACE.length) {
            for (int i = 0; i < PREFACE.length; ++i) {
                if (data.get() == PREFACE[i]) continue;
                throw new ProtocolNegotiationException("Unexpected HTTP/2 preface");
            }
            this.startProtocol(new ServerH2IOEventHandler(this.http2StreamHandlerFactory.create(this.ioSession)), data.hasRemaining() ? data : null);
        } else if (endOfStream) {
            throw new ConnectionClosedException();
        }
    }

    public void outputReady(IOSession session) throws IOException {
    }

    public String toString() {
        return this.getClass().getName();
    }
}


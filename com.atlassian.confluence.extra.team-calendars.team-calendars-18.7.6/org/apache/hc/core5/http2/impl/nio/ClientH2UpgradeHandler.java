/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http2.impl.nio;

import java.io.IOException;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.Internal;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http2.impl.nio.ClientH2PrefaceHandler;
import org.apache.hc.core5.http2.impl.nio.ClientH2StreamMultiplexerFactory;
import org.apache.hc.core5.reactor.ProtocolIOSession;
import org.apache.hc.core5.reactor.ProtocolUpgradeHandler;
import org.apache.hc.core5.util.Args;

@Contract(threading=ThreadingBehavior.IMMUTABLE_CONDITIONAL)
@Internal
public class ClientH2UpgradeHandler
implements ProtocolUpgradeHandler {
    private final ClientH2StreamMultiplexerFactory http2StreamHandlerFactory;

    public ClientH2UpgradeHandler(ClientH2StreamMultiplexerFactory http2StreamHandlerFactory) {
        this.http2StreamHandlerFactory = Args.notNull(http2StreamHandlerFactory, "HTTP/2 stream handler factory");
    }

    @Override
    public void upgrade(ProtocolIOSession ioSession, FutureCallback<ProtocolIOSession> callback) {
        ClientH2PrefaceHandler protocolNegotiator = new ClientH2PrefaceHandler(ioSession, this.http2StreamHandlerFactory, true, callback);
        ioSession.upgrade(protocolNegotiator);
        try {
            protocolNegotiator.connected(ioSession);
        }
        catch (IOException ex) {
            protocolNegotiator.exception(ioSession, ex);
        }
    }
}


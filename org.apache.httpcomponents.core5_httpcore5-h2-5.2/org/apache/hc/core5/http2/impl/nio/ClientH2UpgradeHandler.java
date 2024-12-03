/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Contract
 *  org.apache.hc.core5.annotation.Internal
 *  org.apache.hc.core5.annotation.ThreadingBehavior
 *  org.apache.hc.core5.concurrent.FutureCallback
 *  org.apache.hc.core5.reactor.IOEventHandler
 *  org.apache.hc.core5.reactor.IOSession
 *  org.apache.hc.core5.reactor.ProtocolIOSession
 *  org.apache.hc.core5.reactor.ProtocolUpgradeHandler
 *  org.apache.hc.core5.util.Args
 */
package org.apache.hc.core5.http2.impl.nio;

import java.io.IOException;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.Internal;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http2.impl.nio.ClientH2PrefaceHandler;
import org.apache.hc.core5.http2.impl.nio.ClientH2StreamMultiplexerFactory;
import org.apache.hc.core5.reactor.IOEventHandler;
import org.apache.hc.core5.reactor.IOSession;
import org.apache.hc.core5.reactor.ProtocolIOSession;
import org.apache.hc.core5.reactor.ProtocolUpgradeHandler;
import org.apache.hc.core5.util.Args;

@Contract(threading=ThreadingBehavior.IMMUTABLE_CONDITIONAL)
@Internal
public class ClientH2UpgradeHandler
implements ProtocolUpgradeHandler {
    private final ClientH2StreamMultiplexerFactory http2StreamHandlerFactory;

    public ClientH2UpgradeHandler(ClientH2StreamMultiplexerFactory http2StreamHandlerFactory) {
        this.http2StreamHandlerFactory = (ClientH2StreamMultiplexerFactory)Args.notNull((Object)http2StreamHandlerFactory, (String)"HTTP/2 stream handler factory");
    }

    public void upgrade(ProtocolIOSession ioSession, FutureCallback<ProtocolIOSession> callback) {
        ClientH2PrefaceHandler protocolNegotiator = new ClientH2PrefaceHandler(ioSession, this.http2StreamHandlerFactory, true, callback);
        ioSession.upgrade((IOEventHandler)protocolNegotiator);
        try {
            protocolNegotiator.connected((IOSession)ioSession);
        }
        catch (IOException ex) {
            protocolNegotiator.exception((IOSession)ioSession, ex);
        }
    }
}


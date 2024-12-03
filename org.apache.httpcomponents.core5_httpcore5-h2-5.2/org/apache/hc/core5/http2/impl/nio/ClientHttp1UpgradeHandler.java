/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Contract
 *  org.apache.hc.core5.annotation.Internal
 *  org.apache.hc.core5.annotation.ThreadingBehavior
 *  org.apache.hc.core5.concurrent.FutureCallback
 *  org.apache.hc.core5.http.impl.nio.ClientHttp1IOEventHandler
 *  org.apache.hc.core5.http.impl.nio.ClientHttp1StreamDuplexerFactory
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
import org.apache.hc.core5.http.impl.nio.ClientHttp1IOEventHandler;
import org.apache.hc.core5.http.impl.nio.ClientHttp1StreamDuplexerFactory;
import org.apache.hc.core5.reactor.IOEventHandler;
import org.apache.hc.core5.reactor.IOSession;
import org.apache.hc.core5.reactor.ProtocolIOSession;
import org.apache.hc.core5.reactor.ProtocolUpgradeHandler;
import org.apache.hc.core5.util.Args;

@Contract(threading=ThreadingBehavior.IMMUTABLE_CONDITIONAL)
@Internal
public class ClientHttp1UpgradeHandler
implements ProtocolUpgradeHandler {
    private final ClientHttp1StreamDuplexerFactory http1StreamHandlerFactory;

    public ClientHttp1UpgradeHandler(ClientHttp1StreamDuplexerFactory http1StreamHandlerFactory) {
        this.http1StreamHandlerFactory = (ClientHttp1StreamDuplexerFactory)Args.notNull((Object)http1StreamHandlerFactory, (String)"HTTP/1.1 stream handler factory");
    }

    public void upgrade(ProtocolIOSession ioSession, FutureCallback<ProtocolIOSession> callback) {
        ClientHttp1IOEventHandler eventHandler = new ClientHttp1IOEventHandler(this.http1StreamHandlerFactory.create(ioSession));
        ioSession.upgrade((IOEventHandler)eventHandler);
        try {
            eventHandler.connected((IOSession)ioSession);
            if (callback != null) {
                callback.completed((Object)ioSession);
            }
        }
        catch (IOException ex) {
            eventHandler.exception((IOSession)ioSession, (Exception)ex);
        }
    }
}


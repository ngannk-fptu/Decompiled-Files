/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Contract
 *  org.apache.hc.core5.annotation.Internal
 *  org.apache.hc.core5.annotation.ThreadingBehavior
 *  org.apache.hc.core5.concurrent.FutureCallback
 *  org.apache.hc.core5.http.URIScheme
 *  org.apache.hc.core5.http.impl.nio.ServerHttp1IOEventHandler
 *  org.apache.hc.core5.http.impl.nio.ServerHttp1StreamDuplexerFactory
 *  org.apache.hc.core5.reactor.IOEventHandler
 *  org.apache.hc.core5.reactor.IOSession
 *  org.apache.hc.core5.reactor.ProtocolIOSession
 *  org.apache.hc.core5.reactor.ProtocolUpgradeHandler
 *  org.apache.hc.core5.reactor.ssl.TlsDetails
 *  org.apache.hc.core5.util.Args
 */
package org.apache.hc.core5.http2.impl.nio;

import java.io.IOException;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.Internal;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.URIScheme;
import org.apache.hc.core5.http.impl.nio.ServerHttp1IOEventHandler;
import org.apache.hc.core5.http.impl.nio.ServerHttp1StreamDuplexerFactory;
import org.apache.hc.core5.reactor.IOEventHandler;
import org.apache.hc.core5.reactor.IOSession;
import org.apache.hc.core5.reactor.ProtocolIOSession;
import org.apache.hc.core5.reactor.ProtocolUpgradeHandler;
import org.apache.hc.core5.reactor.ssl.TlsDetails;
import org.apache.hc.core5.util.Args;

@Contract(threading=ThreadingBehavior.IMMUTABLE_CONDITIONAL)
@Internal
public class ServerHttp1UpgradeHandler
implements ProtocolUpgradeHandler {
    private final ServerHttp1StreamDuplexerFactory http1StreamHandlerFactory;

    public ServerHttp1UpgradeHandler(ServerHttp1StreamDuplexerFactory http1StreamHandlerFactory) {
        this.http1StreamHandlerFactory = (ServerHttp1StreamDuplexerFactory)Args.notNull((Object)http1StreamHandlerFactory, (String)"HTTP/1.1 stream handler factory");
    }

    public void upgrade(ProtocolIOSession ioSession, FutureCallback<ProtocolIOSession> callback) {
        TlsDetails tlsDetails = ioSession.getTlsDetails();
        ServerHttp1IOEventHandler eventHandler = new ServerHttp1IOEventHandler(this.http1StreamHandlerFactory.create(tlsDetails != null ? URIScheme.HTTPS.id : URIScheme.HTTP.id, ioSession));
        ioSession.upgrade((IOEventHandler)eventHandler);
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


/*
 * Decompiled with CFR 0.152.
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
        this.http1StreamHandlerFactory = Args.notNull(http1StreamHandlerFactory, "HTTP/1.1 stream handler factory");
    }

    @Override
    public void upgrade(ProtocolIOSession ioSession, FutureCallback<ProtocolIOSession> callback) {
        TlsDetails tlsDetails = ioSession.getTlsDetails();
        ServerHttp1IOEventHandler eventHandler = new ServerHttp1IOEventHandler(this.http1StreamHandlerFactory.create(tlsDetails != null ? URIScheme.HTTPS.id : URIScheme.HTTP.id, ioSession));
        ioSession.upgrade(eventHandler);
        ioSession.upgrade(eventHandler);
        try {
            eventHandler.connected(ioSession);
            if (callback != null) {
                callback.completed(ioSession);
            }
        }
        catch (IOException ex) {
            eventHandler.exception(ioSession, ex);
        }
    }
}


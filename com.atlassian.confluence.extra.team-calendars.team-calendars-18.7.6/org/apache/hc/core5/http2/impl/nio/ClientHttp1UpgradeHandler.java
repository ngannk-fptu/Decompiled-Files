/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http2.impl.nio;

import java.io.IOException;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.Internal;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.impl.nio.ClientHttp1IOEventHandler;
import org.apache.hc.core5.http.impl.nio.ClientHttp1StreamDuplexerFactory;
import org.apache.hc.core5.reactor.ProtocolIOSession;
import org.apache.hc.core5.reactor.ProtocolUpgradeHandler;
import org.apache.hc.core5.util.Args;

@Contract(threading=ThreadingBehavior.IMMUTABLE_CONDITIONAL)
@Internal
public class ClientHttp1UpgradeHandler
implements ProtocolUpgradeHandler {
    private final ClientHttp1StreamDuplexerFactory http1StreamHandlerFactory;

    public ClientHttp1UpgradeHandler(ClientHttp1StreamDuplexerFactory http1StreamHandlerFactory) {
        this.http1StreamHandlerFactory = Args.notNull(http1StreamHandlerFactory, "HTTP/1.1 stream handler factory");
    }

    @Override
    public void upgrade(ProtocolIOSession ioSession, FutureCallback<ProtocolIOSession> callback) {
        ClientHttp1IOEventHandler eventHandler = new ClientHttp1IOEventHandler(this.http1StreamHandlerFactory.create(ioSession));
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


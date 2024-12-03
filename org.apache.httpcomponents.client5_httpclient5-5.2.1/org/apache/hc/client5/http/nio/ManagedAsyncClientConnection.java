/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Internal
 *  org.apache.hc.core5.concurrent.FutureCallback
 *  org.apache.hc.core5.http.HttpConnection
 *  org.apache.hc.core5.reactor.Command
 *  org.apache.hc.core5.reactor.Command$Priority
 *  org.apache.hc.core5.reactor.ProtocolIOSession
 *  org.apache.hc.core5.reactor.ssl.TransportSecurityLayer
 */
package org.apache.hc.client5.http.nio;

import org.apache.hc.core5.annotation.Internal;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.HttpConnection;
import org.apache.hc.core5.reactor.Command;
import org.apache.hc.core5.reactor.ProtocolIOSession;
import org.apache.hc.core5.reactor.ssl.TransportSecurityLayer;

@Internal
public interface ManagedAsyncClientConnection
extends HttpConnection,
TransportSecurityLayer {
    public void submitCommand(Command var1, Command.Priority var2);

    public void passivate();

    public void activate();

    default public void switchProtocol(String protocolId, FutureCallback<ProtocolIOSession> callback) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Protocol switch not supported");
    }
}


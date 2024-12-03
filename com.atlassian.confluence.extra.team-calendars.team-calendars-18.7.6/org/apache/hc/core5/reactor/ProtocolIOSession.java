/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.reactor;

import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.net.NamedEndpoint;
import org.apache.hc.core5.reactor.IOSession;
import org.apache.hc.core5.reactor.ProtocolUpgradeHandler;
import org.apache.hc.core5.reactor.ssl.TransportSecurityLayer;

public interface ProtocolIOSession
extends IOSession,
TransportSecurityLayer {
    default public void switchProtocol(String protocolId, FutureCallback<ProtocolIOSession> callback) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Protocol switch not supported");
    }

    default public void registerProtocol(String protocolId, ProtocolUpgradeHandler upgradeHandler) {
    }

    public NamedEndpoint getInitialEndpoint();
}


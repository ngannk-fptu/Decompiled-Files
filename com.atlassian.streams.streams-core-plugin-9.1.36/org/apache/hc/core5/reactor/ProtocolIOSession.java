/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.reactor;

import org.apache.hc.core5.net.NamedEndpoint;
import org.apache.hc.core5.reactor.IOSession;
import org.apache.hc.core5.reactor.ssl.TransportSecurityLayer;

public interface ProtocolIOSession
extends IOSession,
TransportSecurityLayer {
    public NamedEndpoint getInitialEndpoint();
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http.nio.ssl;

import org.apache.hc.core5.annotation.Internal;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.net.NamedEndpoint;
import org.apache.hc.core5.reactor.ProtocolIOSession;

@Internal
public interface TlsUpgradeCapable {
    public void tlsUpgrade(NamedEndpoint var1, FutureCallback<ProtocolIOSession> var2);
}


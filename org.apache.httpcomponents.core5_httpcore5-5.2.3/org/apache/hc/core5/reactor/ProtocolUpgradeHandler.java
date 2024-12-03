/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.reactor;

import org.apache.hc.core5.annotation.Internal;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.reactor.ProtocolIOSession;

@Internal
public interface ProtocolUpgradeHandler {
    public void upgrade(ProtocolIOSession var1, FutureCallback<ProtocolIOSession> var2);
}


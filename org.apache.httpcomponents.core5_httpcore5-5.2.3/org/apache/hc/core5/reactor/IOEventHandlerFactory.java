/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.reactor;

import org.apache.hc.core5.annotation.Internal;
import org.apache.hc.core5.reactor.IOEventHandler;
import org.apache.hc.core5.reactor.ProtocolIOSession;

@Internal
public interface IOEventHandlerFactory {
    public IOEventHandler createHandler(ProtocolIOSession var1, Object var2);
}


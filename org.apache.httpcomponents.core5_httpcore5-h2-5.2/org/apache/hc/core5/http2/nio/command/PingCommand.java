/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Internal
 *  org.apache.hc.core5.reactor.Command
 *  org.apache.hc.core5.util.Args
 */
package org.apache.hc.core5.http2.nio.command;

import org.apache.hc.core5.annotation.Internal;
import org.apache.hc.core5.http2.nio.AsyncPingHandler;
import org.apache.hc.core5.reactor.Command;
import org.apache.hc.core5.util.Args;

@Internal
public final class PingCommand
implements Command {
    private final AsyncPingHandler handler;

    public PingCommand(AsyncPingHandler handler) {
        this.handler = (AsyncPingHandler)Args.notNull((Object)handler, (String)"Handler");
    }

    public AsyncPingHandler getHandler() {
        return this.handler;
    }

    public boolean cancel() {
        this.handler.cancel();
        return true;
    }
}


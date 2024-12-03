/*
 * Decompiled with CFR 0.152.
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
        this.handler = Args.notNull(handler, "Handler");
    }

    public AsyncPingHandler getHandler() {
        return this.handler;
    }

    @Override
    public boolean cancel() {
        this.handler.cancel();
        return true;
    }
}


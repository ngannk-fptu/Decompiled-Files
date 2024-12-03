/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ch.qos.logback.core.net.server.ConcurrentServerRunner
 *  ch.qos.logback.core.net.server.ServerListener
 */
package ch.qos.logback.classic.net.server;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.net.server.RemoteAppenderClient;
import ch.qos.logback.core.net.server.ConcurrentServerRunner;
import ch.qos.logback.core.net.server.ServerListener;
import java.util.concurrent.Executor;

class RemoteAppenderServerRunner
extends ConcurrentServerRunner<RemoteAppenderClient> {
    public RemoteAppenderServerRunner(ServerListener<RemoteAppenderClient> listener, Executor executor) {
        super(listener, executor);
    }

    protected boolean configureClient(RemoteAppenderClient client) {
        client.setLoggerContext((LoggerContext)this.getContext());
        return true;
    }
}


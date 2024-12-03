/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.util.WaitUntil
 *  com.atlassian.plugin.util.WaitUntil$WaitCondition
 *  com.atlassian.util.concurrent.LazyReference
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Supplier
 *  com.google.common.base.Throwables
 *  javax.annotation.concurrent.ThreadSafe
 *  org.hsqldb.server.Server
 */
package com.atlassian.hsqldb;

import com.atlassian.hsqldb.ServerState;
import com.atlassian.hsqldb.ServerView;
import com.atlassian.plugin.util.WaitUntil;
import com.atlassian.util.concurrent.LazyReference;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import java.net.URI;
import java.net.URISyntaxException;
import javax.annotation.concurrent.ThreadSafe;
import org.hsqldb.server.Server;

@ThreadSafe
public class ServerLifecycle {
    private final Supplier<Server> server;
    private final Supplier<ServerView> serverView = new LazyReference<ServerView>(){

        protected ServerView create() throws Exception {
            return new ServerView(){

                @Override
                public ServerState getState() {
                    return ServerState.forServer((Server)ServerLifecycle.this.server.get());
                }

                @Override
                public URI getUri() {
                    try {
                        return new URI("hsql://localhost:" + ((Server)ServerLifecycle.this.server.get()).getPort());
                    }
                    catch (URISyntaxException e) {
                        throw Throwables.propagate((Throwable)e);
                    }
                }

                public String toString() {
                    return ServerLifecycle.this.server.toString();
                }
            };
        }
    };

    public ServerLifecycle(final Supplier<Server> serverFactory) {
        this.server = new LazyReference<Server>(){

            protected Server create() throws Exception {
                return (Server)serverFactory.get();
            }
        };
    }

    public ServerView view() {
        return (ServerView)this.serverView.get();
    }

    public synchronized ServerView start() {
        ((Server)this.server.get()).start();
        this.waitForState(ServerState.ONLINE);
        return this.view();
    }

    public synchronized ServerView stop() {
        ((Server)this.server.get()).stop();
        this.waitForState(ServerState.SHUTDOWN);
        return this.view();
    }

    private void waitForState(final ServerState desiredState) throws IllegalStateException {
        final Server server = (Server)this.server.get();
        boolean finished = WaitUntil.invoke((WaitUntil.WaitCondition)new WaitUntil.WaitCondition(){

            public boolean isFinished() {
                return desiredState == ServerState.forServer(server);
            }

            public String getWaitMessage() {
                return String.format("waiting for %s to start", server);
            }
        });
        Preconditions.checkState((boolean)finished, (String)"timeout whilst waiting for %s to transition into %s, left with %s", (Object[])new Object[]{server, desiredState, ServerState.forServer(server)});
    }
}


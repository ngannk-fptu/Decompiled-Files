/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.util.concurrent.LazyReference
 *  javax.annotation.Nonnull
 *  javax.annotation.concurrent.ThreadSafe
 *  org.h2.tools.Server
 */
package com.atlassian.h2;

import com.atlassian.h2.ServerView;
import io.atlassian.util.concurrent.LazyReference;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import org.h2.tools.Server;

@ThreadSafe
public class ServerLifecycle {
    private final Supplier<Server> server;
    private final Supplier<ServerView> serverView = new LazyReference<ServerView>(){

        protected ServerView create() {
            return new ServerView(){

                @Override
                public boolean isRunning() {
                    return ((Server)ServerLifecycle.this.server.get()).isRunning(false);
                }

                @Override
                public URI getUri() {
                    try {
                        return new URI("jdbc:h2:tcp://localhost:" + ((Server)ServerLifecycle.this.server.get()).getPort());
                    }
                    catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }

                public String toString() {
                    return ServerLifecycle.this.server.toString();
                }
            };
        }
    };

    public ServerLifecycle(final @Nonnull Supplier<Server> serverFactory) {
        Objects.requireNonNull(serverFactory);
        this.server = new LazyReference<Server>(){

            protected Server create() {
                return (Server)serverFactory.get();
            }
        };
    }

    @Nonnull
    public ServerView view() {
        return this.serverView.get();
    }

    @Nonnull
    public synchronized ServerView start() {
        ServerView view = this.view();
        if (!view.isRunning()) {
            try {
                this.server.get().start();
            }
            catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return this.view();
    }

    @Nonnull
    public synchronized ServerView stop() {
        ServerView view = this.view();
        if (view.isRunning()) {
            this.server.get().stop();
        }
        return this.view();
    }
}


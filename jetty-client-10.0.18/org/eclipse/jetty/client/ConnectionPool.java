/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jetty.client;

import java.io.Closeable;
import java.util.concurrent.CompletableFuture;
import org.eclipse.jetty.client.HttpDestination;
import org.eclipse.jetty.client.api.Connection;

public interface ConnectionPool
extends Closeable {
    default public CompletableFuture<Void> preCreateConnections(int connectionCount) {
        return CompletableFuture.completedFuture(null);
    }

    public boolean isActive(Connection var1);

    public boolean isEmpty();

    public boolean isClosed();

    public Connection acquire(boolean var1);

    public boolean accept(Connection var1);

    public boolean release(Connection var1);

    public boolean remove(Connection var1);

    @Override
    public void close();

    public static interface MaxUsable {
        public int getMaxUsageCount();
    }

    public static interface Multiplexable {
        public int getMaxMultiplex();

        @Deprecated
        default public void setMaxMultiplex(int maxMultiplex) {
        }
    }

    public static interface Factory {
        public ConnectionPool newConnectionPool(HttpDestination var1);
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.util.Callback
 *  org.eclipse.jetty.util.Pool
 *  org.eclipse.jetty.util.Pool$StrategyType
 *  org.eclipse.jetty.util.annotation.ManagedAttribute
 *  org.eclipse.jetty.util.annotation.ManagedObject
 */
package org.eclipse.jetty.client;

import org.eclipse.jetty.client.AbstractConnectionPool;
import org.eclipse.jetty.client.HttpDestination;
import org.eclipse.jetty.client.api.Connection;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.Pool;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;

@ManagedObject
public class DuplexConnectionPool
extends AbstractConnectionPool {
    public DuplexConnectionPool(HttpDestination destination, int maxConnections, Callback requester) {
        this(destination, maxConnections, false, requester);
    }

    public DuplexConnectionPool(HttpDestination destination, int maxConnections, boolean cache, Callback requester) {
        super(destination, Pool.StrategyType.FIRST, maxConnections, cache, requester);
    }

    @Deprecated
    public DuplexConnectionPool(HttpDestination destination, Pool<Connection> pool, Callback requester) {
        super(destination, pool, requester);
    }

    @Override
    @ManagedAttribute(value="The maximum amount of times a connection is used before it gets closed")
    public int getMaxUsageCount() {
        return super.getMaxUsageCount();
    }

    @Override
    public void setMaxUsageCount(int maxUsageCount) {
        super.setMaxUsageCount(maxUsageCount);
    }
}


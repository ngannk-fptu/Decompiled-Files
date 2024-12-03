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
import org.eclipse.jetty.client.ConnectionPool;
import org.eclipse.jetty.client.HttpDestination;
import org.eclipse.jetty.client.api.Connection;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.Pool;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;

@ManagedObject
public class MultiplexConnectionPool
extends AbstractConnectionPool {
    public MultiplexConnectionPool(HttpDestination destination, int maxConnections, Callback requester, int maxMultiplex) {
        this(destination, maxConnections, false, requester, maxMultiplex);
    }

    public MultiplexConnectionPool(HttpDestination destination, int maxConnections, boolean cache, Callback requester, int maxMultiplex) {
        this(destination, Pool.StrategyType.FIRST, maxConnections, cache, requester, maxMultiplex);
    }

    public MultiplexConnectionPool(HttpDestination destination, Pool.StrategyType strategy, int maxConnections, boolean cache, Callback requester, int maxMultiplex) {
        super(destination, new Pool<Connection>(strategy, maxConnections, cache){

            protected int getMaxUsageCount(Connection connection) {
                int maxUsage = connection instanceof ConnectionPool.MaxUsable ? ((ConnectionPool.MaxUsable)((Object)connection)).getMaxUsageCount() : super.getMaxUsageCount((Object)connection);
                return maxUsage > 0 ? maxUsage : -1;
            }

            protected int getMaxMultiplex(Connection connection) {
                int multiplex = connection instanceof ConnectionPool.Multiplexable ? ((ConnectionPool.Multiplexable)((Object)connection)).getMaxMultiplex() : super.getMaxMultiplex((Object)connection);
                return multiplex > 0 ? multiplex : 1;
            }
        }, requester);
        this.setMaxMultiplex(maxMultiplex);
    }

    @Deprecated
    public MultiplexConnectionPool(HttpDestination destination, Pool<Connection> pool, Callback requester, int maxMultiplex) {
        super(destination, pool, requester);
        this.setMaxMultiplex(maxMultiplex);
    }

    @Override
    @ManagedAttribute(value="The multiplexing factor of connections")
    public int getMaxMultiplex() {
        return super.getMaxMultiplex();
    }

    @Override
    public void setMaxMultiplex(int maxMultiplex) {
        super.setMaxMultiplex(maxMultiplex);
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


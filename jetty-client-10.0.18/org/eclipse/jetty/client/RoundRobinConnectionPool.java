/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.util.Callback
 *  org.eclipse.jetty.util.Pool$StrategyType
 *  org.eclipse.jetty.util.annotation.ManagedObject
 */
package org.eclipse.jetty.client;

import org.eclipse.jetty.client.HttpDestination;
import org.eclipse.jetty.client.MultiplexConnectionPool;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.Pool;
import org.eclipse.jetty.util.annotation.ManagedObject;

@ManagedObject
public class RoundRobinConnectionPool
extends MultiplexConnectionPool {
    public RoundRobinConnectionPool(HttpDestination destination, int maxConnections, Callback requester) {
        this(destination, maxConnections, requester, 1);
    }

    public RoundRobinConnectionPool(HttpDestination destination, int maxConnections, Callback requester, int maxMultiplex) {
        super(destination, Pool.StrategyType.ROUND_ROBIN, maxConnections, false, requester, maxMultiplex);
        this.setMaximizeConnections(true);
    }
}


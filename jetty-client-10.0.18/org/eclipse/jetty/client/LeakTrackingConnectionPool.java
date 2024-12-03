/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.util.Callback
 *  org.eclipse.jetty.util.LeakDetector
 *  org.eclipse.jetty.util.LeakDetector$LeakInfo
 *  org.eclipse.jetty.util.component.LifeCycle
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.client;

import org.eclipse.jetty.client.DuplexConnectionPool;
import org.eclipse.jetty.client.HttpDestination;
import org.eclipse.jetty.client.api.Connection;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.LeakDetector;
import org.eclipse.jetty.util.component.LifeCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LeakTrackingConnectionPool
extends DuplexConnectionPool {
    private static final Logger LOG = LoggerFactory.getLogger(LeakTrackingConnectionPool.class);
    private final LeakDetector<Connection> leakDetector = new LeakDetector<Connection>(){

        protected void leaked(LeakDetector.LeakInfo leakInfo) {
            LeakTrackingConnectionPool.this.leaked(leakInfo);
        }
    };

    public LeakTrackingConnectionPool(HttpDestination destination, int maxConnections, Callback requester) {
        super(destination, maxConnections, requester);
        this.addBean(this.leakDetector);
    }

    @Override
    public void close() {
        super.close();
        LifeCycle.stop((Object)this);
    }

    @Override
    protected void acquired(Connection connection) {
        if (!this.leakDetector.acquired((Object)connection)) {
            LOG.info("Connection {}@{} not tracked", (Object)connection, (Object)this.leakDetector.id((Object)connection));
        }
    }

    @Override
    protected void released(Connection connection) {
        if (!this.leakDetector.released((Object)connection)) {
            LOG.info("Connection {}@{} released but not acquired", (Object)connection, (Object)this.leakDetector.id((Object)connection));
        }
    }

    protected void leaked(LeakDetector.LeakInfo leakInfo) {
        LOG.info("Connection {} leaked at:", (Object)leakInfo.getResourceDescription(), (Object)leakInfo.getStackFrames());
    }
}


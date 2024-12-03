/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.ComponentMonitor
 *  com.hazelcast.core.LifecycleEvent
 *  com.hazelcast.core.LifecycleListener
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.cluster.hazelcast.monitoring;

import com.atlassian.diagnostics.ComponentMonitor;
import com.hazelcast.core.LifecycleEvent;
import com.hazelcast.core.LifecycleListener;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class HazelcastLifecycleListener
implements LifecycleListener {
    private static final Logger log = LoggerFactory.getLogger(HazelcastLifecycleListener.class);
    private final ComponentMonitor monitor;

    HazelcastLifecycleListener(ComponentMonitor monitor) {
        this.monitor = Objects.requireNonNull(monitor);
    }

    public void stateChanged(LifecycleEvent event) {
        log.debug("stateChanged: state {}", (Object)event.getState());
    }
}


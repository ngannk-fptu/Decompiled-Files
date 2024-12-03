/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.util.component;

import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.util.component.LifeCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StopLifeCycle
extends AbstractLifeCycle
implements LifeCycle.Listener {
    private static final Logger LOG = LoggerFactory.getLogger(StopLifeCycle.class);
    private final LifeCycle _lifecycle;

    public StopLifeCycle(LifeCycle lifecycle) {
        this._lifecycle = lifecycle;
        this.addEventListener(this);
    }

    @Override
    public void lifeCycleStarted(LifeCycle lifecycle) {
        try {
            this._lifecycle.stop();
        }
        catch (Exception e) {
            LOG.warn("Unable to stop", (Throwable)e);
        }
    }
}


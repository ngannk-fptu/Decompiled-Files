/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 */
package com.atlassian.healthcheck.core.impl;

import com.atlassian.sal.api.lifecycle.LifecycleAware;

public class HostStateMonitor
implements LifecycleAware {
    private boolean isHostAppReady = false;

    public void onStart() {
        this.isHostAppReady = true;
    }

    public void onStop() {
        this.isHostAppReady = false;
    }

    public boolean isHostAppReady() {
        return this.isHostAppReady;
    }
}


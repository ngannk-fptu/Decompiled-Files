/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.util.concurrent.Promise
 */
package com.atlassian.confluence.plugins.synchrony.api;

import io.atlassian.util.concurrent.Promise;

public interface SynchronyMonitor {
    public boolean isSynchronyUp();

    public Promise<Boolean> pollHeartbeat();

    public void cancelHeartbeat();
}


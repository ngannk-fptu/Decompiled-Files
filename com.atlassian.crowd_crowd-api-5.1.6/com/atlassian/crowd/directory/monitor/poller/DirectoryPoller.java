/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.directory.monitor.poller;

import com.atlassian.crowd.directory.monitor.DirectoryMonitor;
import com.atlassian.crowd.manager.directory.SynchronisationMode;

public interface DirectoryPoller
extends DirectoryMonitor {
    public static final int DEFAULT_CACHE_SYNCHRONISE_INTERVAL = 3600;

    public void pollChanges(SynchronisationMode var1);

    public long getPollingInterval();
}


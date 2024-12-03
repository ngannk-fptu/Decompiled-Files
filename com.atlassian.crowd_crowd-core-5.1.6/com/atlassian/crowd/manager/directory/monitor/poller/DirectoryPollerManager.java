/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.manager.directory.SynchronisationMode
 */
package com.atlassian.crowd.manager.directory.monitor.poller;

import com.atlassian.crowd.manager.directory.SynchronisationMode;

public interface DirectoryPollerManager {
    public void triggerPoll(long var1, SynchronisationMode var3);
}


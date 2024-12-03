/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.concurrent.atomic.AtomicReference
 */
package com.atlassian.confluence.extra.calendar3.upgrade.task.aomigration.impl;

import com.atlassian.confluence.extra.calendar3.upgrade.task.aomigration.StatusProvider;
import com.atlassian.util.concurrent.atomic.AtomicReference;

public class AbstractStatusProvider
implements StatusProvider {
    protected AtomicReference<StatusProvider.RunningStatus> status = new AtomicReference((Object)StatusProvider.RunningStatus.NOT_RUNNING);

    @Override
    public StatusProvider.RunningStatus getStatus() {
        return (StatusProvider.RunningStatus)((Object)this.status.get());
    }
}


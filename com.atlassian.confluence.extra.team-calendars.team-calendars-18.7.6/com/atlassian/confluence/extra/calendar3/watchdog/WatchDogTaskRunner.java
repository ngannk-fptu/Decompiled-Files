/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.watchdog;

import com.atlassian.confluence.extra.calendar3.watchdog.WatchDogServiceState;
import com.atlassian.confluence.extra.calendar3.watchdog.WatchDogStatusReporter;
import com.atlassian.confluence.extra.calendar3.watchdog.WatchDogTask;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

public interface WatchDogTaskRunner {
    public AtomicReference<WatchDogServiceState> getState();

    public void runTasks(Collection<WatchDogTask> var1, WatchDogStatusReporter var2);
}


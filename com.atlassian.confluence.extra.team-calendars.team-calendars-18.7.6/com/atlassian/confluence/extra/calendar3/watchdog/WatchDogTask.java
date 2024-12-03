/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.watchdog;

import com.atlassian.confluence.extra.calendar3.watchdog.WatchDogStatusReporter;

public interface WatchDogTask {
    public boolean shouldRun();

    public void run(WatchDogStatusReporter var1);
}


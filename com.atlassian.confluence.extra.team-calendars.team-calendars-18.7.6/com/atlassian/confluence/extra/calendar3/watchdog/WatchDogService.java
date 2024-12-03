/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.watchdog;

import com.atlassian.confluence.extra.calendar3.watchdog.WatchDogServiceStatus;

public interface WatchDogService {
    public WatchDogServiceStatus startService();

    public WatchDogServiceStatus getStatus();
}


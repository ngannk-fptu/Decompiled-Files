/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.SchedulerService
 */
package com.atlassian.scheduler.core;

import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.core.SchedulerServiceController;

public interface LifecycleAwareSchedulerService
extends SchedulerService,
SchedulerServiceController {

    public static enum State {
        STANDBY,
        STARTED,
        SHUTDOWN;

    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.ActivityId;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

final class ActivityCorrelator {
    private static ActivityId activityId;
    private static Lock lockObject;

    static ActivityId getCurrent() {
        if (activityId == null) {
            lockObject.lock();
            if (activityId == null) {
                activityId = new ActivityId();
            }
            lockObject.unlock();
        }
        return activityId;
    }

    static ActivityId getNext() {
        ActivityId activityId = ActivityCorrelator.getCurrent();
        activityId.increment();
        return activityId;
    }

    private ActivityCorrelator() {
    }

    static {
        lockObject = new ReentrantLock();
    }
}


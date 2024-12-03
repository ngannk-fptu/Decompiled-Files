/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.mywork.client.schedule;

public interface Scheduler {
    public void schedule(ScheduleRunnable var1);

    public void rescheduleAll();

    public static interface ScheduleCallback {
        public void pass();

        public void failed();
    }

    public static interface ScheduleRunnable {
        public void run(ScheduleCallback var1);
    }
}


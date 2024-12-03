/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  com.atlassian.util.concurrent.ThreadFactories
 */
package com.atlassian.mywork.client.schedule;

import com.atlassian.mywork.client.schedule.Scheduler;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.util.concurrent.ThreadFactories;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BackOffScheduler
implements Scheduler,
LifecycleAware {
    private static final int DELAY_MULTIPLIER = 2;
    private static final int DELAY_PERIOD = 60000;
    private final ScheduledThreadPoolExecutor scheduler = (ScheduledThreadPoolExecutor)Executors.newScheduledThreadPool(3, ThreadFactories.namedThreadFactory((String)"MyworkPlugin-BackOffScheduler"));
    private final int delayPeriod;
    private final int maxDelay;

    public BackOffScheduler() {
        this(60000);
    }

    public BackOffScheduler(int delay) {
        this.delayPeriod = delay;
        this.maxDelay = 3600000;
    }

    @Override
    public void schedule(Scheduler.ScheduleRunnable run) {
        this.schedule(run, 0);
    }

    @Override
    public void rescheduleAll() {
        for (Runnable r : this.scheduler.getQueue()) {
            if (!this.scheduler.remove(r)) continue;
            this.scheduler.schedule(r, 1000L, TimeUnit.MILLISECONDS);
        }
    }

    private void schedule(final Scheduler.ScheduleRunnable run, final int delay) {
        this.scheduler.schedule(new Runnable(){

            @Override
            public void run() {
                run.run(new Scheduler.ScheduleCallback(){

                    @Override
                    public void pass() {
                        BackOffScheduler.this.schedule(run, 0);
                    }

                    @Override
                    public void failed() {
                        int newDelay = Math.max(BackOffScheduler.this.delayPeriod, delay * 2);
                        BackOffScheduler.this.schedule(run, Math.min(BackOffScheduler.this.maxDelay, newDelay));
                    }
                });
            }
        }, (long)delay, TimeUnit.MILLISECONDS);
    }

    public void onStart() {
    }

    public void onStop() {
        this.scheduler.shutdownNow();
    }
}


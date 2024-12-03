/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.util;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class FailSafeTimer {
    private final Timer timer;
    private final boolean timerThreadRunning;

    public FailSafeTimer(String name) {
        boolean threadRunning;
        Timer localTimer = null;
        try {
            localTimer = new Timer(name, true);
            threadRunning = true;
        }
        catch (Exception e) {
            localTimer = null;
            threadRunning = false;
        }
        this.timerThreadRunning = threadRunning;
        this.timer = localTimer;
    }

    public void cancel() {
        if (this.timerThreadRunning) {
            this.timer.cancel();
        }
    }

    public int purge() {
        if (this.timerThreadRunning) {
            return this.timer.purge();
        }
        return 0;
    }

    public void schedule(TimerTask task, Date firstTime, long period) {
        if (this.timerThreadRunning) {
            this.timer.schedule(task, firstTime, period);
        } else {
            task.run();
        }
    }

    public void schedule(TimerTask task, Date time) {
        if (this.timerThreadRunning) {
            this.timer.schedule(task, time);
        } else {
            task.run();
        }
    }

    public void schedule(TimerTask task, long delay, long period) {
        if (this.timerThreadRunning) {
            this.timer.schedule(task, delay, period);
        } else {
            task.run();
        }
    }

    public void schedule(TimerTask task, long delay) {
        if (this.timerThreadRunning) {
            this.timer.schedule(task, delay);
        } else {
            task.run();
        }
    }

    public void scheduleAtFixedRate(TimerTask task, Date firstTime, long period) {
        if (this.timerThreadRunning) {
            this.timer.scheduleAtFixedRate(task, firstTime, period);
        } else {
            task.run();
        }
    }

    public void scheduleAtFixedRate(TimerTask task, long delay, long period) {
        if (this.timerThreadRunning) {
            this.timer.scheduleAtFixedRate(task, delay, period);
        } else {
            task.run();
        }
    }
}


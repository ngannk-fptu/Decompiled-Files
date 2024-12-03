/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.util;

import java.util.TimerTask;

public class Timer {
    static final int IDLE_TIME = 3000;
    static final int CHECKER_INTERVAL = 1000;
    private java.util.Timer delegatee;
    private final boolean runAsDeamon;
    private int numScheduledTasks = 0;
    private long lastTaskScheduled;

    public Timer(boolean isDeamon) {
        this.runAsDeamon = isDeamon;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void schedule(Task task, long delay, long period) {
        if (delay < 0L) {
            throw new IllegalArgumentException("Negative delay.");
        }
        if (period <= 0L) {
            throw new IllegalArgumentException("Non-positive period.");
        }
        Timer timer = this;
        synchronized (timer) {
            if (this.delegatee == null) {
                this.delegatee = new java.util.Timer(this.runAsDeamon);
                IdleCheckerTask idleChecker = new IdleCheckerTask();
                ((Task)idleChecker).setTimer(this);
                this.delegatee.schedule((TimerTask)idleChecker, 3000L, 1000L);
            }
            this.delegatee.schedule((TimerTask)task, delay, period);
            task.setTimer(this);
            ++this.numScheduledTasks;
            this.lastTaskScheduled = System.currentTimeMillis();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void cancel() {
        Timer timer = this;
        synchronized (timer) {
            if (this.delegatee != null) {
                this.delegatee.cancel();
                this.numScheduledTasks = 0;
                this.delegatee = null;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    boolean isRunning() {
        Timer timer = this;
        synchronized (timer) {
            return this.delegatee != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void taskCanceled() {
        Timer timer = this;
        synchronized (timer) {
            --this.numScheduledTasks;
        }
    }

    private class IdleCheckerTask
    extends Task {
        private IdleCheckerTask() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            Timer timer = Timer.this;
            synchronized (timer) {
                if (Timer.this.numScheduledTasks == 0 && System.currentTimeMillis() > Timer.this.lastTaskScheduled + 3000L && Timer.this.delegatee != null) {
                    Timer.this.delegatee.cancel();
                    Timer.this.delegatee = null;
                }
            }
        }
    }

    public static abstract class Task
    extends TimerTask {
        private Timer timer;

        private void setTimer(Timer timer) {
            this.timer = timer;
        }

        @Override
        public final boolean cancel() {
            if (this.timer != null) {
                this.timer.taskCanceled();
                this.timer = null;
            }
            return super.cancel();
        }
    }
}


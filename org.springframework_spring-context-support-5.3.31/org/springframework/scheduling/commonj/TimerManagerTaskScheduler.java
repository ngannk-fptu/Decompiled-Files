/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  commonj.timers.Timer
 *  commonj.timers.TimerListener
 *  org.springframework.lang.Nullable
 *  org.springframework.scheduling.TaskScheduler
 *  org.springframework.scheduling.Trigger
 *  org.springframework.scheduling.TriggerContext
 *  org.springframework.scheduling.support.SimpleTriggerContext
 *  org.springframework.scheduling.support.TaskUtils
 *  org.springframework.util.Assert
 *  org.springframework.util.ErrorHandler
 */
package org.springframework.scheduling.commonj;

import commonj.timers.Timer;
import commonj.timers.TimerListener;
import java.util.Date;
import java.util.concurrent.Delayed;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.commonj.TimerManagerAccessor;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.scheduling.support.TaskUtils;
import org.springframework.util.Assert;
import org.springframework.util.ErrorHandler;

@Deprecated
public class TimerManagerTaskScheduler
extends TimerManagerAccessor
implements TaskScheduler {
    @Nullable
    private volatile ErrorHandler errorHandler;

    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    @Nullable
    public ScheduledFuture<?> schedule(Runnable task, Trigger trigger) {
        return new ReschedulingTimerListener(this.errorHandlingTask(task, true), trigger).schedule();
    }

    public ScheduledFuture<?> schedule(Runnable task, Date startTime) {
        TimerScheduledFuture futureTask = new TimerScheduledFuture(this.errorHandlingTask(task, false));
        Timer timer = this.obtainTimerManager().schedule((TimerListener)futureTask, startTime);
        futureTask.setTimer(timer);
        return futureTask;
    }

    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Date startTime, long period) {
        TimerScheduledFuture futureTask = new TimerScheduledFuture(this.errorHandlingTask(task, true));
        Timer timer = this.obtainTimerManager().scheduleAtFixedRate((TimerListener)futureTask, startTime, period);
        futureTask.setTimer(timer);
        return futureTask;
    }

    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long period) {
        TimerScheduledFuture futureTask = new TimerScheduledFuture(this.errorHandlingTask(task, true));
        Timer timer = this.obtainTimerManager().scheduleAtFixedRate((TimerListener)futureTask, 0L, period);
        futureTask.setTimer(timer);
        return futureTask;
    }

    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, Date startTime, long delay) {
        TimerScheduledFuture futureTask = new TimerScheduledFuture(this.errorHandlingTask(task, true));
        Timer timer = this.obtainTimerManager().schedule((TimerListener)futureTask, startTime, delay);
        futureTask.setTimer(timer);
        return futureTask;
    }

    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, long delay) {
        TimerScheduledFuture futureTask = new TimerScheduledFuture(this.errorHandlingTask(task, true));
        Timer timer = this.obtainTimerManager().schedule((TimerListener)futureTask, 0L, delay);
        futureTask.setTimer(timer);
        return futureTask;
    }

    private Runnable errorHandlingTask(Runnable delegate, boolean isRepeatingTask) {
        return TaskUtils.decorateTaskWithErrorHandler((Runnable)delegate, (ErrorHandler)this.errorHandler, (boolean)isRepeatingTask);
    }

    private class ReschedulingTimerListener
    extends TimerScheduledFuture {
        private final Trigger trigger;
        private final SimpleTriggerContext triggerContext;
        private volatile Date scheduledExecutionTime;

        public ReschedulingTimerListener(Runnable runnable, Trigger trigger) {
            super(runnable);
            this.triggerContext = new SimpleTriggerContext();
            this.scheduledExecutionTime = new Date();
            this.trigger = trigger;
        }

        @Nullable
        public ScheduledFuture<?> schedule() {
            Date nextExecutionTime = this.trigger.nextExecutionTime((TriggerContext)this.triggerContext);
            if (nextExecutionTime == null) {
                return null;
            }
            this.scheduledExecutionTime = nextExecutionTime;
            this.setTimer(TimerManagerTaskScheduler.this.obtainTimerManager().schedule((TimerListener)this, this.scheduledExecutionTime));
            return this;
        }

        @Override
        public void timerExpired(Timer timer) {
            Date actualExecutionTime = new Date();
            super.timerExpired(timer);
            Date completionTime = new Date();
            this.triggerContext.update(this.scheduledExecutionTime, actualExecutionTime, completionTime);
            if (!this.cancelled) {
                this.schedule();
            }
        }
    }

    private static class TimerScheduledFuture
    extends FutureTask<Object>
    implements TimerListener,
    ScheduledFuture<Object> {
        @Nullable
        protected transient Timer timer;
        protected transient boolean cancelled = false;

        public TimerScheduledFuture(Runnable runnable) {
            super(runnable, null);
        }

        public void setTimer(Timer timer) {
            this.timer = timer;
        }

        public void timerExpired(Timer timer) {
            this.runAndReset();
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            boolean result = super.cancel(mayInterruptIfRunning);
            if (this.timer != null) {
                this.timer.cancel();
            }
            this.cancelled = true;
            return result;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            Assert.state((this.timer != null ? 1 : 0) != 0, (String)"No Timer available");
            return unit.convert(this.timer.getScheduledExecutionTime() - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed other) {
            if (this == other) {
                return 0;
            }
            long diff = this.getDelay(TimeUnit.MILLISECONDS) - other.getDelay(TimeUnit.MILLISECONDS);
            return diff == 0L ? 0 : (diff < 0L ? -1 : 1);
        }
    }
}


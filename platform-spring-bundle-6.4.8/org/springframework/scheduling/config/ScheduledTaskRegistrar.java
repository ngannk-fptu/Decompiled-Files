/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.scheduling.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.FixedDelayTask;
import org.springframework.scheduling.config.FixedRateTask;
import org.springframework.scheduling.config.IntervalTask;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskHolder;
import org.springframework.scheduling.config.Task;
import org.springframework.scheduling.config.TriggerTask;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

public class ScheduledTaskRegistrar
implements ScheduledTaskHolder,
InitializingBean,
DisposableBean {
    public static final String CRON_DISABLED = "-";
    @Nullable
    private TaskScheduler taskScheduler;
    @Nullable
    private ScheduledExecutorService localExecutor;
    @Nullable
    private List<TriggerTask> triggerTasks;
    @Nullable
    private List<CronTask> cronTasks;
    @Nullable
    private List<IntervalTask> fixedRateTasks;
    @Nullable
    private List<IntervalTask> fixedDelayTasks;
    private final Map<Task, ScheduledTask> unresolvedTasks = new HashMap<Task, ScheduledTask>(16);
    private final Set<ScheduledTask> scheduledTasks = new LinkedHashSet<ScheduledTask>(16);

    public void setTaskScheduler(TaskScheduler taskScheduler) {
        Assert.notNull((Object)taskScheduler, "TaskScheduler must not be null");
        this.taskScheduler = taskScheduler;
    }

    public void setScheduler(@Nullable Object scheduler) {
        if (scheduler == null) {
            this.taskScheduler = null;
        } else if (scheduler instanceof TaskScheduler) {
            this.taskScheduler = (TaskScheduler)scheduler;
        } else if (scheduler instanceof ScheduledExecutorService) {
            this.taskScheduler = new ConcurrentTaskScheduler((ScheduledExecutorService)scheduler);
        } else {
            throw new IllegalArgumentException("Unsupported scheduler type: " + scheduler.getClass());
        }
    }

    @Nullable
    public TaskScheduler getScheduler() {
        return this.taskScheduler;
    }

    public void setTriggerTasks(Map<Runnable, Trigger> triggerTasks) {
        this.triggerTasks = new ArrayList<TriggerTask>();
        triggerTasks.forEach((task, trigger) -> this.addTriggerTask(new TriggerTask((Runnable)task, (Trigger)trigger)));
    }

    public void setTriggerTasksList(List<TriggerTask> triggerTasks) {
        this.triggerTasks = triggerTasks;
    }

    public List<TriggerTask> getTriggerTaskList() {
        return this.triggerTasks != null ? Collections.unmodifiableList(this.triggerTasks) : Collections.emptyList();
    }

    public void setCronTasks(Map<Runnable, String> cronTasks) {
        this.cronTasks = new ArrayList<CronTask>();
        cronTasks.forEach(this::addCronTask);
    }

    public void setCronTasksList(List<CronTask> cronTasks) {
        this.cronTasks = cronTasks;
    }

    public List<CronTask> getCronTaskList() {
        return this.cronTasks != null ? Collections.unmodifiableList(this.cronTasks) : Collections.emptyList();
    }

    public void setFixedRateTasks(Map<Runnable, Long> fixedRateTasks) {
        this.fixedRateTasks = new ArrayList<IntervalTask>();
        fixedRateTasks.forEach(this::addFixedRateTask);
    }

    public void setFixedRateTasksList(List<IntervalTask> fixedRateTasks) {
        this.fixedRateTasks = fixedRateTasks;
    }

    public List<IntervalTask> getFixedRateTaskList() {
        return this.fixedRateTasks != null ? Collections.unmodifiableList(this.fixedRateTasks) : Collections.emptyList();
    }

    public void setFixedDelayTasks(Map<Runnable, Long> fixedDelayTasks) {
        this.fixedDelayTasks = new ArrayList<IntervalTask>();
        fixedDelayTasks.forEach(this::addFixedDelayTask);
    }

    public void setFixedDelayTasksList(List<IntervalTask> fixedDelayTasks) {
        this.fixedDelayTasks = fixedDelayTasks;
    }

    public List<IntervalTask> getFixedDelayTaskList() {
        return this.fixedDelayTasks != null ? Collections.unmodifiableList(this.fixedDelayTasks) : Collections.emptyList();
    }

    public void addTriggerTask(Runnable task, Trigger trigger) {
        this.addTriggerTask(new TriggerTask(task, trigger));
    }

    public void addTriggerTask(TriggerTask task) {
        if (this.triggerTasks == null) {
            this.triggerTasks = new ArrayList<TriggerTask>();
        }
        this.triggerTasks.add(task);
    }

    public void addCronTask(Runnable task, String expression) {
        if (!CRON_DISABLED.equals(expression)) {
            this.addCronTask(new CronTask(task, expression));
        }
    }

    public void addCronTask(CronTask task) {
        if (this.cronTasks == null) {
            this.cronTasks = new ArrayList<CronTask>();
        }
        this.cronTasks.add(task);
    }

    public void addFixedRateTask(Runnable task, long interval) {
        this.addFixedRateTask(new IntervalTask(task, interval, 0L));
    }

    public void addFixedRateTask(IntervalTask task) {
        if (this.fixedRateTasks == null) {
            this.fixedRateTasks = new ArrayList<IntervalTask>();
        }
        this.fixedRateTasks.add(task);
    }

    public void addFixedDelayTask(Runnable task, long delay) {
        this.addFixedDelayTask(new IntervalTask(task, delay, 0L));
    }

    public void addFixedDelayTask(IntervalTask task) {
        if (this.fixedDelayTasks == null) {
            this.fixedDelayTasks = new ArrayList<IntervalTask>();
        }
        this.fixedDelayTasks.add(task);
    }

    public boolean hasTasks() {
        return !CollectionUtils.isEmpty(this.triggerTasks) || !CollectionUtils.isEmpty(this.cronTasks) || !CollectionUtils.isEmpty(this.fixedRateTasks) || !CollectionUtils.isEmpty(this.fixedDelayTasks);
    }

    @Override
    public void afterPropertiesSet() {
        this.scheduleTasks();
    }

    protected void scheduleTasks() {
        if (this.taskScheduler == null) {
            this.localExecutor = Executors.newSingleThreadScheduledExecutor();
            this.taskScheduler = new ConcurrentTaskScheduler(this.localExecutor);
        }
        if (this.triggerTasks != null) {
            for (TriggerTask triggerTask : this.triggerTasks) {
                this.addScheduledTask(this.scheduleTriggerTask(triggerTask));
            }
        }
        if (this.cronTasks != null) {
            for (CronTask cronTask : this.cronTasks) {
                this.addScheduledTask(this.scheduleCronTask(cronTask));
            }
        }
        if (this.fixedRateTasks != null) {
            for (IntervalTask intervalTask : this.fixedRateTasks) {
                this.addScheduledTask(this.scheduleFixedRateTask(intervalTask));
            }
        }
        if (this.fixedDelayTasks != null) {
            for (IntervalTask intervalTask : this.fixedDelayTasks) {
                this.addScheduledTask(this.scheduleFixedDelayTask(intervalTask));
            }
        }
    }

    private void addScheduledTask(@Nullable ScheduledTask task) {
        if (task != null) {
            this.scheduledTasks.add(task);
        }
    }

    @Nullable
    public ScheduledTask scheduleTriggerTask(TriggerTask task) {
        ScheduledTask scheduledTask = this.unresolvedTasks.remove(task);
        boolean newTask = false;
        if (scheduledTask == null) {
            scheduledTask = new ScheduledTask(task);
            newTask = true;
        }
        if (this.taskScheduler != null) {
            scheduledTask.future = this.taskScheduler.schedule(task.getRunnable(), task.getTrigger());
        } else {
            this.addTriggerTask(task);
            this.unresolvedTasks.put(task, scheduledTask);
        }
        return newTask ? scheduledTask : null;
    }

    @Nullable
    public ScheduledTask scheduleCronTask(CronTask task) {
        ScheduledTask scheduledTask = this.unresolvedTasks.remove(task);
        boolean newTask = false;
        if (scheduledTask == null) {
            scheduledTask = new ScheduledTask(task);
            newTask = true;
        }
        if (this.taskScheduler != null) {
            scheduledTask.future = this.taskScheduler.schedule(task.getRunnable(), task.getTrigger());
        } else {
            this.addCronTask(task);
            this.unresolvedTasks.put(task, scheduledTask);
        }
        return newTask ? scheduledTask : null;
    }

    @Deprecated
    @Nullable
    public ScheduledTask scheduleFixedRateTask(IntervalTask task) {
        FixedRateTask taskToUse = task instanceof FixedRateTask ? (FixedRateTask)task : new FixedRateTask(task.getRunnable(), task.getInterval(), task.getInitialDelay());
        return this.scheduleFixedRateTask(taskToUse);
    }

    @Nullable
    public ScheduledTask scheduleFixedRateTask(FixedRateTask task) {
        ScheduledTask scheduledTask = this.unresolvedTasks.remove(task);
        boolean newTask = false;
        if (scheduledTask == null) {
            scheduledTask = new ScheduledTask(task);
            newTask = true;
        }
        if (this.taskScheduler != null) {
            if (task.getInitialDelay() > 0L) {
                Date startTime = new Date(this.taskScheduler.getClock().millis() + task.getInitialDelay());
                scheduledTask.future = this.taskScheduler.scheduleAtFixedRate(task.getRunnable(), startTime, task.getInterval());
            } else {
                scheduledTask.future = this.taskScheduler.scheduleAtFixedRate(task.getRunnable(), task.getInterval());
            }
        } else {
            this.addFixedRateTask(task);
            this.unresolvedTasks.put(task, scheduledTask);
        }
        return newTask ? scheduledTask : null;
    }

    @Deprecated
    @Nullable
    public ScheduledTask scheduleFixedDelayTask(IntervalTask task) {
        FixedDelayTask taskToUse = task instanceof FixedDelayTask ? (FixedDelayTask)task : new FixedDelayTask(task.getRunnable(), task.getInterval(), task.getInitialDelay());
        return this.scheduleFixedDelayTask(taskToUse);
    }

    @Nullable
    public ScheduledTask scheduleFixedDelayTask(FixedDelayTask task) {
        ScheduledTask scheduledTask = this.unresolvedTasks.remove(task);
        boolean newTask = false;
        if (scheduledTask == null) {
            scheduledTask = new ScheduledTask(task);
            newTask = true;
        }
        if (this.taskScheduler != null) {
            if (task.getInitialDelay() > 0L) {
                Date startTime = new Date(this.taskScheduler.getClock().millis() + task.getInitialDelay());
                scheduledTask.future = this.taskScheduler.scheduleWithFixedDelay(task.getRunnable(), startTime, task.getInterval());
            } else {
                scheduledTask.future = this.taskScheduler.scheduleWithFixedDelay(task.getRunnable(), task.getInterval());
            }
        } else {
            this.addFixedDelayTask(task);
            this.unresolvedTasks.put(task, scheduledTask);
        }
        return newTask ? scheduledTask : null;
    }

    @Override
    public Set<ScheduledTask> getScheduledTasks() {
        return Collections.unmodifiableSet(this.scheduledTasks);
    }

    @Override
    public void destroy() {
        for (ScheduledTask task : this.scheduledTasks) {
            task.cancel(false);
        }
        if (this.localExecutor != null) {
            this.localExecutor.shutdownNow();
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.scheduling.PluginJob
 *  com.atlassian.sal.api.scheduling.PluginScheduler
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 */
package com.atlassian.sal.core.scheduling;

import com.atlassian.sal.api.scheduling.PluginJob;
import com.atlassian.sal.api.scheduling.PluginScheduler;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

public class TimerPluginScheduler
implements PluginScheduler,
DisposableBean {
    private final Map<String, Timer> tasks;
    private final boolean useDaemons;

    public TimerPluginScheduler() {
        this(Collections.synchronizedMap(new HashMap()), false);
    }

    protected TimerPluginScheduler(Map<String, Timer> tasks, boolean useDaemons) {
        this.tasks = tasks;
        this.useDaemons = useDaemons;
    }

    public synchronized void scheduleJob(String name, Class<? extends PluginJob> job, Map<String, Object> jobDataMap, Date startTime, long repeatInterval) {
        Timer timer = this.tasks.get(name);
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer("PluginSchedulerTask-" + name, this.useDaemons);
        this.tasks.put(name, timer);
        PluginTimerTask task = new PluginTimerTask();
        task.setJobClass(job);
        task.setJobDataMap(jobDataMap);
        timer.scheduleAtFixedRate((TimerTask)task, startTime, repeatInterval);
    }

    public void destroy() throws Exception {
        for (Timer timer : this.tasks.values()) {
            timer.cancel();
        }
    }

    public void unscheduleJob(String name) {
        Timer timer = this.tasks.remove(name);
        if (timer == null) {
            throw new IllegalArgumentException("Attempted to unschedule unknown job: " + name);
        }
        timer.cancel();
    }

    private static class PluginTimerTask
    extends TimerTask {
        private Class<? extends PluginJob> jobClass;
        private Map<String, Object> jobDataMap;
        private static final Logger log = LoggerFactory.getLogger(PluginTimerTask.class);

        private PluginTimerTask() {
        }

        @Override
        public void run() {
            PluginJob job;
            try {
                job = this.jobClass.newInstance();
            }
            catch (InstantiationException ie) {
                log.error("Error instantiating job", (Throwable)ie);
                return;
            }
            catch (IllegalAccessException iae) {
                log.error("Cannot access job class", (Throwable)iae);
                return;
            }
            job.execute(this.jobDataMap);
        }

        public Class<? extends PluginJob> getJobClass() {
            return this.jobClass;
        }

        public void setJobClass(Class<? extends PluginJob> jobClass) {
            this.jobClass = jobClass;
        }

        public Map<String, Object> getJobDataMap() {
            return this.jobDataMap;
        }

        public void setJobDataMap(Map<String, Object> jobDataMap) {
            this.jobDataMap = jobDataMap;
        }
    }
}


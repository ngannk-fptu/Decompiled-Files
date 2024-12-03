/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.quartz.Job
 *  org.quartz.Scheduler
 *  org.quartz.SchedulerException
 *  org.quartz.spi.JobFactory
 *  org.quartz.spi.TriggerFiredBundle
 */
package org.springframework.scheduling.quartz;

import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.scheduling.quartz.DelegatingJob;
import org.springframework.util.ReflectionUtils;

public class AdaptableJobFactory
implements JobFactory {
    public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) throws SchedulerException {
        try {
            Object jobObject = this.createJobInstance(bundle);
            return this.adaptJob(jobObject);
        }
        catch (Throwable ex) {
            throw new SchedulerException("Job instantiation failed", ex);
        }
    }

    protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
        Class jobClass = bundle.getJobDetail().getJobClass();
        return ReflectionUtils.accessibleConstructor(jobClass, new Class[0]).newInstance(new Object[0]);
    }

    protected Job adaptJob(Object jobObject) throws Exception {
        if (jobObject instanceof Job) {
            return (Job)jobObject;
        }
        if (jobObject instanceof Runnable) {
            return new DelegatingJob((Runnable)jobObject);
        }
        throw new IllegalArgumentException("Unable to execute job class [" + jobObject.getClass().getName() + "]: only [org.quartz.Job] and [java.lang.Runnable] supported.");
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.quartz.Job
 *  org.quartz.JobExecutionContext
 *  org.quartz.JobExecutionException
 *  org.quartz.SchedulerException
 *  org.springframework.beans.BeanWrapper
 *  org.springframework.beans.MutablePropertyValues
 *  org.springframework.beans.PropertyAccessorFactory
 *  org.springframework.beans.PropertyValues
 */
package org.springframework.scheduling.quartz;

import java.util.Map;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.PropertyValues;

public abstract class QuartzJobBean
implements Job {
    public final void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess((Object)this);
            MutablePropertyValues pvs = new MutablePropertyValues();
            pvs.addPropertyValues((Map)context.getScheduler().getContext());
            pvs.addPropertyValues((Map)context.getMergedJobDataMap());
            bw.setPropertyValues((PropertyValues)pvs, true);
        }
        catch (SchedulerException ex) {
            throw new JobExecutionException((Throwable)ex);
        }
        this.executeInternal(context);
    }

    protected abstract void executeInternal(JobExecutionContext var1) throws JobExecutionException;
}


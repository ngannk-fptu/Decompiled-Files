/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.quartz.SchedulerContext
 *  org.quartz.spi.TriggerFiredBundle
 */
package org.springframework.scheduling.quartz;

import java.util.Map;
import org.quartz.SchedulerContext;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.quartz.AdaptableJobFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerContextAware;

public class SpringBeanJobFactory
extends AdaptableJobFactory
implements ApplicationContextAware,
SchedulerContextAware {
    @Nullable
    private String[] ignoredUnknownProperties;
    @Nullable
    private ApplicationContext applicationContext;
    @Nullable
    private SchedulerContext schedulerContext;

    public void setIgnoredUnknownProperties(String ... ignoredUnknownProperties) {
        this.ignoredUnknownProperties = ignoredUnknownProperties;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setSchedulerContext(SchedulerContext schedulerContext) {
        this.schedulerContext = schedulerContext;
    }

    @Override
    protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
        Object job;
        Object object = job = this.applicationContext != null ? this.applicationContext.getAutowireCapableBeanFactory().createBean(bundle.getJobDetail().getJobClass(), 3, false) : super.createJobInstance(bundle);
        if (this.isEligibleForPropertyPopulation(job)) {
            BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(job);
            MutablePropertyValues pvs = new MutablePropertyValues();
            if (this.schedulerContext != null) {
                pvs.addPropertyValues((Map<?, ?>)this.schedulerContext);
            }
            pvs.addPropertyValues((Map<?, ?>)bundle.getJobDetail().getJobDataMap());
            pvs.addPropertyValues((Map<?, ?>)bundle.getTrigger().getJobDataMap());
            if (this.ignoredUnknownProperties != null) {
                for (String propName : this.ignoredUnknownProperties) {
                    if (!pvs.contains(propName) || bw.isWritableProperty(propName)) continue;
                    pvs.removePropertyValue(propName);
                }
                bw.setPropertyValues(pvs);
            } else {
                bw.setPropertyValues(pvs, true);
            }
        }
        return job;
    }

    protected boolean isEligibleForPropertyPopulation(Object jobObject) {
        return !(jobObject instanceof QuartzJobBean);
    }
}


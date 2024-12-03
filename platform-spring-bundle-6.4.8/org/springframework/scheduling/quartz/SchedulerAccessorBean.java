/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.quartz.Scheduler
 *  org.quartz.SchedulerException
 *  org.quartz.impl.SchedulerRepository
 */
package org.springframework.scheduling.quartz;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.SchedulerRepository;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.quartz.SchedulerAccessor;
import org.springframework.util.Assert;

public class SchedulerAccessorBean
extends SchedulerAccessor
implements BeanFactoryAware,
InitializingBean {
    @Nullable
    private String schedulerName;
    @Nullable
    private Scheduler scheduler;
    @Nullable
    private BeanFactory beanFactory;

    public void setSchedulerName(String schedulerName) {
        this.schedulerName = schedulerName;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public Scheduler getScheduler() {
        Assert.state(this.scheduler != null, "No Scheduler set");
        return this.scheduler;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public void afterPropertiesSet() throws SchedulerException {
        if (this.scheduler == null) {
            this.scheduler = this.schedulerName != null ? this.findScheduler(this.schedulerName) : this.findDefaultScheduler();
        }
        this.registerListeners();
        this.registerJobsAndTriggers();
    }

    protected Scheduler findScheduler(String schedulerName) throws SchedulerException {
        Scheduler schedulerInRepo;
        if (this.beanFactory instanceof ListableBeanFactory) {
            String[] beanNames;
            ListableBeanFactory lbf = (ListableBeanFactory)this.beanFactory;
            for (String beanName : beanNames = lbf.getBeanNamesForType(Scheduler.class)) {
                Scheduler schedulerBean = (Scheduler)lbf.getBean(beanName);
                if (!schedulerName.equals(schedulerBean.getSchedulerName())) continue;
                return schedulerBean;
            }
        }
        if ((schedulerInRepo = SchedulerRepository.getInstance().lookup(schedulerName)) == null) {
            throw new IllegalStateException("No Scheduler named '" + schedulerName + "' found");
        }
        return schedulerInRepo;
    }

    protected Scheduler findDefaultScheduler() {
        if (this.beanFactory != null) {
            return this.beanFactory.getBean(Scheduler.class);
        }
        throw new IllegalStateException("No Scheduler specified, and cannot find a default Scheduler without a BeanFactory");
    }
}


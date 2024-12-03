/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.quartz.JobDataMap
 *  org.quartz.JobDetail
 *  org.quartz.SimpleTrigger
 *  org.quartz.impl.triggers.SimpleTriggerImpl
 */
package org.springframework.scheduling.quartz;

import java.util.Date;
import java.util.Map;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.Constants;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class SimpleTriggerFactoryBean
implements FactoryBean<SimpleTrigger>,
BeanNameAware,
InitializingBean {
    private static final Constants constants = new Constants(SimpleTrigger.class);
    @Nullable
    private String name;
    @Nullable
    private String group;
    @Nullable
    private JobDetail jobDetail;
    private JobDataMap jobDataMap = new JobDataMap();
    @Nullable
    private Date startTime;
    private long startDelay;
    private long repeatInterval;
    private int repeatCount = -1;
    private int priority;
    private int misfireInstruction;
    @Nullable
    private String description;
    @Nullable
    private String beanName;
    @Nullable
    private SimpleTrigger simpleTrigger;

    public void setName(String name) {
        this.name = name;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setJobDetail(JobDetail jobDetail) {
        this.jobDetail = jobDetail;
    }

    public void setJobDataMap(JobDataMap jobDataMap) {
        this.jobDataMap = jobDataMap;
    }

    public JobDataMap getJobDataMap() {
        return this.jobDataMap;
    }

    public void setJobDataAsMap(Map<String, ?> jobDataAsMap) {
        this.jobDataMap.putAll(jobDataAsMap);
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public void setStartDelay(long startDelay) {
        Assert.isTrue(startDelay >= 0L, "Start delay cannot be negative");
        this.startDelay = startDelay;
    }

    public void setRepeatInterval(long repeatInterval) {
        this.repeatInterval = repeatInterval;
    }

    public void setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setMisfireInstruction(int misfireInstruction) {
        this.misfireInstruction = misfireInstruction;
    }

    public void setMisfireInstructionName(String constantName) {
        this.misfireInstruction = constants.asNumber(constantName).intValue();
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    @Override
    public void afterPropertiesSet() {
        if (this.name == null) {
            this.name = this.beanName;
        }
        if (this.group == null) {
            this.group = "DEFAULT";
        }
        if (this.jobDetail != null) {
            this.jobDataMap.put("jobDetail", (Object)this.jobDetail);
        }
        if (this.startDelay > 0L || this.startTime == null) {
            this.startTime = new Date(System.currentTimeMillis() + this.startDelay);
        }
        SimpleTriggerImpl sti = new SimpleTriggerImpl();
        sti.setName(this.name != null ? this.name : this.toString());
        sti.setGroup(this.group);
        if (this.jobDetail != null) {
            sti.setJobKey(this.jobDetail.getKey());
        }
        sti.setJobDataMap(this.jobDataMap);
        sti.setStartTime(this.startTime);
        sti.setRepeatInterval(this.repeatInterval);
        sti.setRepeatCount(this.repeatCount);
        sti.setPriority(this.priority);
        sti.setMisfireInstruction(this.misfireInstruction);
        sti.setDescription(this.description);
        this.simpleTrigger = sti;
    }

    @Override
    @Nullable
    public SimpleTrigger getObject() {
        return this.simpleTrigger;
    }

    @Override
    public Class<?> getObjectType() {
        return SimpleTrigger.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}


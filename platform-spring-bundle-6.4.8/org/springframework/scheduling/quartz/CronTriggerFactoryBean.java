/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.quartz.CronTrigger
 *  org.quartz.JobDataMap
 *  org.quartz.JobDetail
 *  org.quartz.impl.triggers.CronTriggerImpl
 */
package org.springframework.scheduling.quartz;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.Constants;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class CronTriggerFactoryBean
implements FactoryBean<CronTrigger>,
BeanNameAware,
InitializingBean {
    private static final Constants constants = new Constants(CronTrigger.class);
    @Nullable
    private String name;
    @Nullable
    private String group;
    @Nullable
    private JobDetail jobDetail;
    private JobDataMap jobDataMap = new JobDataMap();
    @Nullable
    private Date startTime;
    private long startDelay = 0L;
    @Nullable
    private String cronExpression;
    @Nullable
    private TimeZone timeZone;
    @Nullable
    private String calendarName;
    private int priority;
    private int misfireInstruction;
    @Nullable
    private String description;
    @Nullable
    private String beanName;
    @Nullable
    private CronTrigger cronTrigger;

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

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    public void setCalendarName(String calendarName) {
        this.calendarName = calendarName;
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
    public void afterPropertiesSet() throws ParseException {
        Assert.notNull((Object)this.cronExpression, "Property 'cronExpression' is required");
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
        if (this.timeZone == null) {
            this.timeZone = TimeZone.getDefault();
        }
        CronTriggerImpl cti = new CronTriggerImpl();
        cti.setName(this.name != null ? this.name : this.toString());
        cti.setGroup(this.group);
        if (this.jobDetail != null) {
            cti.setJobKey(this.jobDetail.getKey());
        }
        cti.setJobDataMap(this.jobDataMap);
        cti.setStartTime(this.startTime);
        cti.setCronExpression(this.cronExpression);
        cti.setTimeZone(this.timeZone);
        cti.setCalendarName(this.calendarName);
        cti.setPriority(this.priority);
        cti.setMisfireInstruction(this.misfireInstruction);
        cti.setDescription(this.description);
        this.cronTrigger = cti;
    }

    @Override
    @Nullable
    public CronTrigger getObject() {
        return this.cronTrigger;
    }

    @Override
    public Class<?> getObjectType() {
        return CronTrigger.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}


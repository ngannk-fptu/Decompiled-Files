/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.quartz.Calendar
 *  org.quartz.JobDetail
 *  org.quartz.JobListener
 *  org.quartz.ListenerManager
 *  org.quartz.ObjectAlreadyExistsException
 *  org.quartz.Scheduler
 *  org.quartz.SchedulerException
 *  org.quartz.SchedulerListener
 *  org.quartz.Trigger
 *  org.quartz.TriggerListener
 *  org.quartz.spi.ClassLoadHelper
 *  org.quartz.xml.XMLSchedulingDataProcessor
 *  org.springframework.context.ResourceLoaderAware
 *  org.springframework.core.io.ResourceLoader
 *  org.springframework.lang.Nullable
 *  org.springframework.transaction.PlatformTransactionManager
 *  org.springframework.transaction.TransactionDefinition
 *  org.springframework.transaction.TransactionException
 *  org.springframework.transaction.TransactionStatus
 */
package org.springframework.scheduling.quartz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Calendar;
import org.quartz.JobDetail;
import org.quartz.JobListener;
import org.quartz.ListenerManager;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerListener;
import org.quartz.Trigger;
import org.quartz.TriggerListener;
import org.quartz.spi.ClassLoadHelper;
import org.quartz.xml.XMLSchedulingDataProcessor;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.quartz.ResourceLoaderClassLoadHelper;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;

public abstract class SchedulerAccessor
implements ResourceLoaderAware {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private boolean overwriteExistingJobs = false;
    @Nullable
    private String[] jobSchedulingDataLocations;
    @Nullable
    private List<JobDetail> jobDetails;
    @Nullable
    private Map<String, Calendar> calendars;
    @Nullable
    private List<Trigger> triggers;
    @Nullable
    private SchedulerListener[] schedulerListeners;
    @Nullable
    private JobListener[] globalJobListeners;
    @Nullable
    private TriggerListener[] globalTriggerListeners;
    @Nullable
    private PlatformTransactionManager transactionManager;
    @Nullable
    protected ResourceLoader resourceLoader;

    public void setOverwriteExistingJobs(boolean overwriteExistingJobs) {
        this.overwriteExistingJobs = overwriteExistingJobs;
    }

    public void setJobSchedulingDataLocation(String jobSchedulingDataLocation) {
        this.jobSchedulingDataLocations = new String[]{jobSchedulingDataLocation};
    }

    public void setJobSchedulingDataLocations(String ... jobSchedulingDataLocations) {
        this.jobSchedulingDataLocations = jobSchedulingDataLocations;
    }

    public void setJobDetails(JobDetail ... jobDetails) {
        this.jobDetails = new ArrayList<JobDetail>(Arrays.asList(jobDetails));
    }

    public void setCalendars(Map<String, Calendar> calendars) {
        this.calendars = calendars;
    }

    public void setTriggers(Trigger ... triggers) {
        this.triggers = Arrays.asList(triggers);
    }

    public void setSchedulerListeners(SchedulerListener ... schedulerListeners) {
        this.schedulerListeners = schedulerListeners;
    }

    public void setGlobalJobListeners(JobListener ... globalJobListeners) {
        this.globalJobListeners = globalJobListeners;
    }

    public void setGlobalTriggerListeners(TriggerListener ... globalTriggerListeners) {
        this.globalTriggerListeners = globalTriggerListeners;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    protected void registerJobsAndTriggers() throws SchedulerException {
        TransactionStatus transactionStatus = null;
        if (this.transactionManager != null) {
            transactionStatus = this.transactionManager.getTransaction(TransactionDefinition.withDefaults());
        }
        try {
            if (this.jobSchedulingDataLocations != null) {
                Iterator<String> clh = new ResourceLoaderClassLoadHelper(this.resourceLoader);
                clh.initialize();
                XMLSchedulingDataProcessor dataProcessor = new XMLSchedulingDataProcessor((ClassLoadHelper)clh);
                for (String location : this.jobSchedulingDataLocations) {
                    dataProcessor.processFileAndScheduleJobs(location, this.getScheduler());
                }
            }
            if (this.jobDetails != null) {
                for (JobDetail jobDetail : this.jobDetails) {
                    this.addJobToScheduler(jobDetail);
                }
            } else {
                this.jobDetails = new ArrayList<JobDetail>();
            }
            if (this.calendars != null) {
                for (String calendarName : this.calendars.keySet()) {
                    Calendar calendar = this.calendars.get(calendarName);
                    this.getScheduler().addCalendar(calendarName, calendar, true, true);
                }
            }
            if (this.triggers != null) {
                for (Trigger trigger : this.triggers) {
                    this.addTriggerToScheduler(trigger);
                }
            }
        }
        catch (Throwable ex) {
            if (transactionStatus != null) {
                try {
                    this.transactionManager.rollback(transactionStatus);
                }
                catch (TransactionException tex) {
                    this.logger.error((Object)"Job registration exception overridden by rollback exception", ex);
                    throw tex;
                }
            }
            if (ex instanceof SchedulerException) {
                throw (SchedulerException)ex;
            }
            if (ex instanceof Exception) {
                throw new SchedulerException("Registration of jobs and triggers failed: " + ex.getMessage(), ex);
            }
            throw new SchedulerException("Registration of jobs and triggers failed: " + ex.getMessage());
        }
        if (transactionStatus != null) {
            this.transactionManager.commit(transactionStatus);
        }
    }

    private boolean addJobToScheduler(JobDetail jobDetail) throws SchedulerException {
        if (this.overwriteExistingJobs || this.getScheduler().getJobDetail(jobDetail.getKey()) == null) {
            this.getScheduler().addJob(jobDetail, true);
            return true;
        }
        return false;
    }

    private boolean addTriggerToScheduler(Trigger trigger) throws SchedulerException {
        block11: {
            boolean triggerExists;
            boolean bl = triggerExists = this.getScheduler().getTrigger(trigger.getKey()) != null;
            if (triggerExists && !this.overwriteExistingJobs) {
                return false;
            }
            JobDetail jobDetail = (JobDetail)trigger.getJobDataMap().remove((Object)"jobDetail");
            if (triggerExists) {
                if (jobDetail != null && this.jobDetails != null && !this.jobDetails.contains(jobDetail) && this.addJobToScheduler(jobDetail)) {
                    this.jobDetails.add(jobDetail);
                }
                try {
                    this.getScheduler().rescheduleJob(trigger.getKey(), trigger);
                }
                catch (ObjectAlreadyExistsException ex) {
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug((Object)("Unexpectedly encountered existing trigger on rescheduling, assumably due to cluster race condition: " + ex.getMessage() + " - can safely be ignored"));
                    }
                    break block11;
                }
            }
            try {
                if (jobDetail != null && this.jobDetails != null && !this.jobDetails.contains(jobDetail) && (this.overwriteExistingJobs || this.getScheduler().getJobDetail(jobDetail.getKey()) == null)) {
                    this.getScheduler().scheduleJob(jobDetail, trigger);
                    this.jobDetails.add(jobDetail);
                } else {
                    this.getScheduler().scheduleJob(trigger);
                }
            }
            catch (ObjectAlreadyExistsException ex) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug((Object)("Unexpectedly encountered existing trigger on job scheduling, assumably due to cluster race condition: " + ex.getMessage() + " - can safely be ignored"));
                }
                if (!this.overwriteExistingJobs) break block11;
                this.getScheduler().rescheduleJob(trigger.getKey(), trigger);
            }
        }
        return true;
    }

    protected void registerListeners() throws SchedulerException {
        ListenerManager listenerManager = this.getScheduler().getListenerManager();
        if (this.schedulerListeners != null) {
            for (SchedulerListener schedulerListener : this.schedulerListeners) {
                listenerManager.addSchedulerListener(schedulerListener);
            }
        }
        if (this.globalJobListeners != null) {
            for (SchedulerListener schedulerListener : this.globalJobListeners) {
                listenerManager.addJobListener((JobListener)schedulerListener);
            }
        }
        if (this.globalTriggerListeners != null) {
            for (SchedulerListener schedulerListener : this.globalTriggerListeners) {
                listenerManager.addTriggerListener((TriggerListener)schedulerListener);
            }
        }
    }

    protected abstract Scheduler getScheduler();
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.config.lifecycle.events.ApplicationStartedEvent
 *  com.atlassian.confluence.cluster.ClusterManager
 *  com.atlassian.confluence.schedule.ManagedScheduledCronJob
 *  com.atlassian.confluence.schedule.ManagedScheduledJob
 *  com.atlassian.confluence.schedule.ManagedScheduledJobRegistrationService
 *  com.atlassian.confluence.schedule.ManagedScheduledSimpleJob
 *  com.atlassian.confluence.schedule.ScheduleUtil
 *  com.atlassian.confluence.schedule.ScheduledCronJob
 *  com.atlassian.confluence.schedule.ScheduledSimpleJob
 *  com.atlassian.confluence.schedule.TenantAwareJobRescheduler
 *  com.atlassian.confluence.util.OsgiUtils
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventListenerRegistrar
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.lifecycle.LifecycleManager
 *  com.atlassian.sal.api.timezone.TimeZoneManager
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.config.JobConfig
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.atlassian.scheduler.status.JobDetails
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.PostConstruct
 *  org.apache.commons.lang3.BooleanUtils
 *  org.dom4j.Element
 *  org.osgi.framework.BundleContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.BeanNotOfRequiredTypeException
 *  org.springframework.beans.factory.NoSuchBeanDefinitionException
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.context.ApplicationContext
 */
package com.atlassian.confluence.plugins.scheduler.spi.descriptor;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.config.lifecycle.events.ApplicationStartedEvent;
import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.schedule.ManagedScheduledCronJob;
import com.atlassian.confluence.schedule.ManagedScheduledJob;
import com.atlassian.confluence.schedule.ManagedScheduledJobRegistrationService;
import com.atlassian.confluence.schedule.ManagedScheduledSimpleJob;
import com.atlassian.confluence.schedule.ScheduleUtil;
import com.atlassian.confluence.schedule.ScheduledCronJob;
import com.atlassian.confluence.schedule.ScheduledSimpleJob;
import com.atlassian.confluence.schedule.TenantAwareJobRescheduler;
import com.atlassian.confluence.util.OsgiUtils;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventListenerRegistrar;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.lifecycle.LifecycleManager;
import com.atlassian.sal.api.timezone.TimeZoneManager;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.status.JobDetails;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TimeZone;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.BooleanUtils;
import org.dom4j.Element;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

@ParametersAreNonnullByDefault
public class JobConfigModuleDescriptor
extends AbstractModuleDescriptor<Void> {
    private static final Logger log = LoggerFactory.getLogger(JobConfigModuleDescriptor.class);
    private final ManagedScheduledJobRegistrationService jobRegistrationService;
    private final TimeZoneManager timeZoneManager;
    private final ClusterManager clusterManager;
    private final SchedulerService schedulerService;
    private final EventListenerRegistrar eventListenerRegistrar;
    private final LifecycleManager lifecycleManager;
    private final BundleContext bundleContext;
    private String jobKey;
    private boolean perClusterJob = false;
    private boolean clusteredOnly = false;
    private boolean managed = false;
    private boolean editable = false;
    private boolean keepingHistory = false;
    private boolean canRunAdhoc = false;
    private boolean canDisable = false;
    private boolean disabledByDefault = false;
    private boolean isCronJob;
    private String defaultCronExpression;
    private int jitterSecs = -1;
    private long repeatInterval;
    private int repeatCount = -1;
    private ManagedScheduledJob managedScheduledJob;

    @Autowired
    public JobConfigModuleDescriptor(@ComponentImport ModuleFactory moduleFactory, @ComponentImport ManagedScheduledJobRegistrationService jobRegistrationService, @ComponentImport TimeZoneManager timeZoneManager, @ComponentImport ClusterManager clusterManager, @ComponentImport SchedulerService schedulerService, @ComponentImport EventListenerRegistrar eventListenerRegistrar, @ComponentImport LifecycleManager lifecycleManager, BundleContext bundleContext) {
        super(moduleFactory);
        this.jobRegistrationService = Objects.requireNonNull(jobRegistrationService);
        this.timeZoneManager = Objects.requireNonNull(timeZoneManager);
        this.clusterManager = Objects.requireNonNull(clusterManager);
        this.schedulerService = Objects.requireNonNull(schedulerService);
        this.eventListenerRegistrar = Objects.requireNonNull(eventListenerRegistrar);
        this.lifecycleManager = Objects.requireNonNull(lifecycleManager);
        this.bundleContext = Objects.requireNonNull(bundleContext);
    }

    @PostConstruct
    public void listenApplicationStartedEvent() {
        this.eventListenerRegistrar.register((Object)this);
    }

    public Void getModule() {
        return null;
    }

    public void init(Plugin plugin, Element element) throws PluginParseException {
        super.init(plugin, element);
        this.parseJobElement(element);
        this.parseManagedElement(element);
        this.parseScheduleElement(element);
    }

    private void parseJobElement(Element element) {
        Element jobEl = element.element("job");
        if (jobEl == null) {
            throw new PluginParseException("You must specify a <job /> for job-config plugin modules");
        }
        if (jobEl.attribute("key") == null) {
            throw new PluginParseException("You must specify key in <job /> for job-config plugin modules");
        }
        this.jobKey = jobEl.attributeValue("key");
        if (jobEl.attribute("perClusterJob") != null) {
            this.perClusterJob = BooleanUtils.toBoolean((String)jobEl.attributeValue("perClusterJob"));
        }
        if (jobEl.attribute("clusteredOnly") != null) {
            this.clusteredOnly = BooleanUtils.toBoolean((String)jobEl.attributeValue("clusteredOnly"));
        }
    }

    private void parseScheduleElement(Element element) {
        Element scheduleEl = element.element("schedule");
        if (scheduleEl == null) {
            throw new PluginParseException("You must specify a <schedule /> for job-config plugin modules.");
        }
        if (scheduleEl.attribute("cron-expression") != null) {
            this.isCronJob = true;
            this.defaultCronExpression = scheduleEl.attributeValue("cron-expression");
            this.jitterSecs = Integer.parseInt(scheduleEl.attributeValue("jitterSecs", "-1"));
        } else if (scheduleEl.attribute("repeat-interval") != null) {
            this.isCronJob = false;
            this.repeatInterval = Long.parseLong(scheduleEl.attributeValue("repeat-interval"));
            this.repeatCount = Integer.parseInt(scheduleEl.attributeValue("repeat-count", "-1"));
            if (this.repeatCount != -1) {
                this.editable = false;
            }
        } else {
            throw new PluginParseException("You must specify cron-expression or repeat-interval in <schedule /> for job-config plugin modules.");
        }
    }

    private void parseManagedElement(Element element) {
        Element managedElement = element.element("managed");
        if (managedElement != null) {
            this.managed = true;
            this.editable = BooleanUtils.toBoolean((String)managedElement.attributeValue("editable"));
            this.keepingHistory = BooleanUtils.toBoolean((String)managedElement.attributeValue("keepingHistory"));
            this.canRunAdhoc = BooleanUtils.toBoolean((String)managedElement.attributeValue("canRunAdhoc"));
            this.canDisable = BooleanUtils.toBoolean((String)managedElement.attributeValue("canDisable"));
            this.disabledByDefault = BooleanUtils.toBoolean((String)managedElement.attributeValue("disabledByDefault"));
        }
    }

    @EventListener
    public void onApplicationStartedEvent(ApplicationStartedEvent event) {
        this.eventListenerRegistrar.unregister((Object)this);
        if (!this.managed && this.isEnabled()) {
            this.scheduleJob();
        }
    }

    public void enabled() {
        super.enabled();
        if (this.managed || this.lifecycleManager.isApplicationSetUp()) {
            this.scheduleJob();
        }
    }

    public void disabled() {
        JobDetails existingJob = this.schedulerService.getJobDetails(this.getJobId());
        if (existingJob != null && existingJob.getJobRunnerKey().equals((Object)this.getJobRunnerKey())) {
            if (this.managed) {
                this.jobRegistrationService.unregisterManagedScheduledJob(this.managedScheduledJob);
            } else {
                this.unscheduleUnmanagedJob();
            }
        }
        super.disabled();
    }

    private void scheduleJob() {
        if (this.clusteredOnly && !this.clusterManager.isClustered()) {
            log.info("Job with ID {} will not be scheduled in this non-clustered environment.", (Object)this.getJobId());
            return;
        }
        Optional<JobRunner> jobRunner = this.getJobRunner();
        if (!jobRunner.isPresent()) {
            return;
        }
        JobDetails existingJob = this.schedulerService.getJobDetails(this.getJobId());
        if (existingJob != null && !existingJob.getJobRunnerKey().equals((Object)this.getJobRunnerKey())) {
            log.error("Job with ID {} already exists, cannot override it. Consider using a different module key.", (Object)this.getJobId());
            return;
        }
        if (this.managed) {
            this.scheduleManagedJob(jobRunner.get());
        } else {
            this.scheduleUnmanagedJob(jobRunner.get());
        }
    }

    private void setManagedScheduledJob(JobRunner jobRunner) {
        if (!this.managed) {
            return;
        }
        this.managedScheduledJob = this.isCronJob ? new ManagedScheduledCronJob(this.getJobId(), jobRunner, this.createJobConfig(), this.editable, this.keepingHistory, this.canRunAdhoc, this.canDisable, this.clusteredOnly, this.disabledByDefault) : new ManagedScheduledSimpleJob(this.getJobId(), jobRunner, this.createJobConfig(), this.editable, this.keepingHistory, this.canRunAdhoc, this.canDisable, this.clusteredOnly, this.disabledByDefault);
    }

    private void scheduleManagedJob(JobRunner jobRunner) {
        this.setManagedScheduledJob(jobRunner);
        this.jobRegistrationService.registerManagedScheduledJob(this.managedScheduledJob);
    }

    private void scheduleUnmanagedJob(JobRunner jobRunner) {
        this.schedulerService.registerJobRunner(this.getJobRunnerKey(), jobRunner);
        try {
            this.schedulerService.scheduleJob(this.getJobId(), this.createJobConfig());
        }
        catch (SchedulerServiceException e) {
            log.error("Could not schedule job, jobId: {}", (Object)this.getJobId(), (Object)e);
        }
    }

    private void unscheduleUnmanagedJob() {
        this.schedulerService.unscheduleJob(this.getJobId());
        this.schedulerService.unregisterJobRunner(this.getJobRunnerKey());
    }

    private Optional<JobRunner> getJobRunner() {
        Optional<JobRunner> jobRunner = this.getJobRunnerFromOsgiBundleContext();
        if (!jobRunner.isPresent()) {
            jobRunner = this.getJobRunnerFromModuleDescriptor();
        }
        return jobRunner;
    }

    private Optional<JobRunner> getJobRunnerFromOsgiBundleContext() {
        Plugin plugin = this.getPlugin();
        try {
            Optional applicationContext = OsgiUtils.findApplicationContextInOsgiBundle((Plugin)plugin);
            return applicationContext.map(context -> (JobRunner)((ApplicationContext)context).getBean(this.jobKey, JobRunner.class));
        }
        catch (NoSuchBeanDefinitionException e) {
            log.debug("Job key {} is not a bean in plugin {}. Assuming it's a legacy component module", (Object)this.jobKey, (Object)plugin.getKey());
        }
        catch (BeanNotOfRequiredTypeException e) {
            log.error("Job key {} does not reference a com.atlassian.scheduler.JobRunner bean in plugin {}", new Object[]{this.jobKey, plugin.getKey(), e});
        }
        catch (BeansException e) {
            log.error("Bean with name job key {} in plugin {} could not be created", new Object[]{this.jobKey, plugin.getKey(), e});
        }
        return Optional.empty();
    }

    private Optional<JobRunner> getJobRunnerFromModuleDescriptor() {
        Optional<JobRunner> jobRunner = Optional.empty();
        Optional<ModuleDescriptor> jobRunnerModuleDescriptor = Optional.ofNullable(this.plugin.getModuleDescriptor(this.jobKey));
        if (!jobRunnerModuleDescriptor.isPresent()) {
            log.error("Job key {} is not a bean or a component in plugin {}", (Object)this.jobKey, (Object)this.plugin.getKey());
        } else {
            Object o = jobRunnerModuleDescriptor.get().getModule();
            if (o instanceof JobRunner) {
                jobRunner = Optional.of((JobRunner)o);
            } else {
                log.error("Job key {} does not reference a com.atlassian.scheduler.JobRunner component in plugin {}", (Object)this.jobKey, (Object)this.plugin.getKey());
            }
        }
        return jobRunner;
    }

    private JobConfig createJobConfig() {
        if (this.isCronJob) {
            JobConfig jobConfig = ScheduledCronJob.toJobConfig((String)this.getJobRunnerKey().toString(), (boolean)this.perClusterJob, (String)this.defaultCronExpression, (int)this.jitterSecs);
            jobConfig = ScheduleUtil.withTimeZone((JobConfig)jobConfig, (TimeZone)this.timeZoneManager.getDefaultTimeZone());
            ImmutableMap parametersWithTzSensitiveJobKey = ImmutableMap.builder().putAll(jobConfig.getParameters()).put((Object)TenantAwareJobRescheduler.TZ_SENSITIVE_JOB_KEY, (Object)true).build();
            return jobConfig.withParameters((Map)parametersWithTzSensitiveJobKey);
        }
        return ScheduledSimpleJob.toJobConfig((String)this.getJobRunnerKey().toString(), (boolean)this.perClusterJob, (long)this.repeatInterval, (int)this.repeatCount, (int)this.jitterSecs);
    }

    private JobRunnerKey getJobRunnerKey() {
        return JobRunnerKey.of((String)(this.plugin.getKey() + ":" + this.jobKey));
    }

    private JobId getJobId() {
        return JobId.of((String)this.getKey());
    }
}


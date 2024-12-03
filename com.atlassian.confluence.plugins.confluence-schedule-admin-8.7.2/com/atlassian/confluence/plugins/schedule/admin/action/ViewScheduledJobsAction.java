/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.schedule.ManagedScheduledCronJob
 *  com.atlassian.confluence.schedule.ManagedScheduledJob
 *  com.atlassian.confluence.schedule.ManagedScheduledSimpleJob
 *  com.atlassian.confluence.schedule.ScheduledJobStatus
 *  com.atlassian.confluence.schedule.managers.ManagedScheduledJobRegistry
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.plugins.schedule.admin.action;

import com.atlassian.confluence.plugins.schedule.admin.action.AbstractViewAction;
import com.atlassian.confluence.schedule.ManagedScheduledCronJob;
import com.atlassian.confluence.schedule.ManagedScheduledJob;
import com.atlassian.confluence.schedule.ManagedScheduledSimpleJob;
import com.atlassian.confluence.schedule.ScheduledJobStatus;
import com.atlassian.confluence.schedule.managers.ManagedScheduledJobRegistry;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewScheduledJobsAction
extends AbstractViewAction {
    private static final long serialVersionUID = 1L;
    private List<ScheduledJobStatus> jobs;
    private ManagedScheduledJobRegistry jobRegistry;

    public List<ScheduledJobStatus> getJobs() {
        return this.jobs;
    }

    public ManagedScheduledJob getManagedScheduledJob(JobId jobId) {
        return this.jobRegistry.getManagedScheduledJob(jobId);
    }

    public boolean isCronJob(ManagedScheduledJob job) {
        return ManagedScheduledJob.isCronJob((ManagedScheduledJob)job);
    }

    public <T extends ManagedScheduledJob> T getManagedScheduledJob(JobId jobId, Class<T> type) {
        ManagedScheduledJob managedJob = this.jobRegistry.getManagedScheduledJob(jobId);
        if (type.isInstance(managedJob)) {
            return (T)managedJob;
        }
        return null;
    }

    public ManagedScheduledCronJob getManagedScheduledCronJob(JobId jobId) {
        return this.getManagedScheduledJob(jobId, ManagedScheduledCronJob.class);
    }

    public ManagedScheduledSimpleJob getManagedScheduledSimpleJob(JobId jobId) {
        return this.getManagedScheduledJob(jobId, ManagedScheduledSimpleJob.class);
    }

    public String getCronExpression(JobId jobId) {
        return this.scheduledJobManager.getCronExpression(jobId);
    }

    public Long getRepeatInterval(JobId jobId) {
        return this.scheduledJobManager.getRepeatInterval(jobId);
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        this.jobs = this.scheduledJobManager.getScheduledJobs();
        Collections.sort(this.jobs, new ScheduledJobStatusComparator());
        return "success";
    }

    public void setManagedScheduledJobRegistry(ManagedScheduledJobRegistry jobRegistry) {
        this.jobRegistry = jobRegistry;
    }

    private class ScheduledJobStatusComparator
    implements Comparator<ScheduledJobStatus> {
        private final Map<String, String> translations = new HashMap<String, String>();

        private ScheduledJobStatusComparator() {
        }

        @Override
        public int compare(ScheduledJobStatus s1, ScheduledJobStatus s2) {
            return this.getTranslatedName(s1).compareToIgnoreCase(this.getTranslatedName(s2));
        }

        private String getTranslatedName(ScheduledJobStatus s1) {
            if (s1 == null) {
                return "null";
            }
            String jobId = String.valueOf(s1.getJobId());
            String translation = this.translations.get(jobId);
            if (translation == null) {
                translation = ViewScheduledJobsAction.this.getText("scheduledjob.desc." + jobId);
                if (translation == null) {
                    translation = jobId;
                }
                this.translations.put(jobId, translation);
            }
            return translation;
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.schedule.ScheduledJobHistory
 *  com.atlassian.confluence.schedule.ScheduledJobHistory$NaturalComparator
 *  com.atlassian.confluence.schedule.ScheduledJobStatus
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.apache.commons.collections4.comparators.ReverseComparator
 */
package com.atlassian.confluence.plugins.schedule.admin.action;

import com.atlassian.confluence.plugins.schedule.admin.action.AbstractViewAction;
import com.atlassian.confluence.schedule.ScheduledJobHistory;
import com.atlassian.confluence.schedule.ScheduledJobStatus;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.collections4.comparators.ReverseComparator;

public class ViewScheduledJobsHistoryAction
extends AbstractViewAction {
    private static final long serialVersionUID = 1L;
    private ScheduledJobStatus job;
    private String id;
    private List<ScheduledJobHistory> history;

    public ScheduledJobStatus getJob() {
        return this.job;
    }

    public List<ScheduledJobHistory> getHistory() {
        return this.history;
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        this.job = this.scheduledJobManager.getScheduledJob(JobId.of((String)this.id));
        this.history = this.job.getHistory();
        Collections.sort(this.history, new ReverseComparator((Comparator)new ScheduledJobHistory.NaturalComparator()));
        return "success";
    }

    public void setId(String id) {
        this.id = id;
    }
}


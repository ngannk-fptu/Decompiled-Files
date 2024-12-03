/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.transaction.TransactionCallback
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.webdav.job;

import com.atlassian.confluence.extra.webdav.job.ContentJob;
import com.atlassian.confluence.extra.webdav.job.ContentJobQueueTransactionCallback;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ContentJobQueue {
    private final Object mutex = new Object();
    private final List<ContentJob> jobs = new ArrayList<ContentJob>();
    private final TransactionTemplate transactionTemplate;

    @Autowired
    public ContentJobQueue(@ComponentImport TransactionTemplate template) {
        this.transactionTemplate = template;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void enque(ContentJob contentJob) {
        Object object = this.mutex;
        synchronized (object) {
            this.jobs.add(contentJob);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void remove(ContentJob contentJob) {
        Object object = this.mutex;
        synchronized (object) {
            this.jobs.remove(contentJob);
        }
    }

    protected boolean isJobDueForExecution(ContentJob job) {
        return System.currentTimeMillis() - job.getCreationTime() >= job.getMinimumAgeForExecution();
    }

    protected ContentJob executeTask(ContentJob job) {
        return (ContentJob)this.transactionTemplate.execute((TransactionCallback)new ContentJobQueueTransactionCallback(job));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void executeTasks() {
        ArrayList<ContentJob> jobsCopy = new ArrayList<ContentJob>();
        ArrayList<ContentJob> jobsExecuted = new ArrayList<ContentJob>();
        Object object = this.mutex;
        synchronized (object) {
            jobsCopy.addAll(this.jobs);
        }
        for (ContentJob job : jobsCopy) {
            ContentJob jobExecuted;
            if (!this.isJobDueForExecution(job) || null == (jobExecuted = this.executeTask(job))) continue;
            jobsExecuted.add(jobExecuted);
        }
        object = this.jobs;
        synchronized (object) {
            this.jobs.removeAll(jobsExecuted);
        }
    }
}


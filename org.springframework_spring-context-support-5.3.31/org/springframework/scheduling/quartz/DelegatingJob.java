/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.quartz.Job
 *  org.quartz.JobExecutionContext
 *  org.quartz.JobExecutionException
 *  org.springframework.util.Assert
 */
package org.springframework.scheduling.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.util.Assert;

public class DelegatingJob
implements Job {
    private final Runnable delegate;

    public DelegatingJob(Runnable delegate) {
        Assert.notNull((Object)delegate, (String)"Delegate must not be null");
        this.delegate = delegate;
    }

    public final Runnable getDelegate() {
        return this.delegate;
    }

    public void execute(JobExecutionContext context) throws JobExecutionException {
        this.delegate.run();
    }
}


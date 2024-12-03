/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.mail.archive.jobs;

import com.atlassian.confluence.mail.archive.MailAccountManager;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import org.springframework.stereotype.Component;

@Component
public class MailPollJob
implements JobRunner {
    private static final String SYSTEM_PROPERTY_DISABLE_POLLING = "confluence.disable.mailpolling";
    private final MailAccountManager mailAccountManager;
    private final TransactionTemplate transactionTemplate;

    public MailPollJob(MailAccountManager mailAccountManager, TransactionTemplate transactionTemplate) {
        this.mailAccountManager = mailAccountManager;
        this.transactionTemplate = transactionTemplate;
    }

    public JobRunnerResponse runJob(JobRunnerRequest request) {
        if (Boolean.getBoolean(SYSTEM_PROPERTY_DISABLE_POLLING)) {
            return JobRunnerResponse.aborted((String)"Mail polling disabled.");
        }
        this.transactionTemplate.execute(() -> {
            this.mailAccountManager.pollAllSpaces();
            return null;
        });
        return JobRunnerResponse.success();
    }
}


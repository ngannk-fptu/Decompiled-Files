/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.transaction.TransactionCallback
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.webdav.job;

import com.atlassian.confluence.extra.webdav.job.ContentJob;
import com.atlassian.confluence.extra.webdav.job.ContentJobQueue;
import com.atlassian.sal.api.transaction.TransactionCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ContentJobQueueTransactionCallback
implements TransactionCallback<ContentJob> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContentJobQueue.class);
    private final ContentJob job;

    public ContentJobQueueTransactionCallback(ContentJob job) {
        this.job = job;
    }

    public ContentJob doInTransaction() {
        if (null != this.job) {
            try {
                this.job.execute();
            }
            catch (Exception e) {
                LOGGER.error("Error executing content job: " + this.job, (Throwable)e);
            }
        }
        return this.job;
    }
}


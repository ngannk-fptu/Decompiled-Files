/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.confluence.notifications.DispatchService
 *  com.atlassian.confluence.notifications.impl.ObjectMapperFactory
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  javax.annotation.Nullable
 *  net.java.ao.EntityStreamCallback
 *  net.java.ao.Query
 */
package com.atlassian.confluence.notifications.batch.job;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.confluence.notifications.DispatchService;
import com.atlassian.confluence.notifications.batch.ao.NotificationStoreAo;
import com.atlassian.confluence.notifications.batch.job.NotificationStoreCallback;
import com.atlassian.confluence.notifications.impl.ObjectMapperFactory;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import javax.annotation.Nullable;
import net.java.ao.EntityStreamCallback;
import net.java.ao.Query;

public class NotificationBatchingJob
implements JobRunner {
    private final TransactionTemplate transactionTemplate;
    private final ActiveObjects ao;
    private final ObjectMapperFactory objectMapperFactory;
    private final PluginAccessor pluginAccessor;
    private final DispatchService dispatchService;

    public NotificationBatchingJob(TransactionTemplate transactionTemplate, ActiveObjects ao, ObjectMapperFactory objectMapperFactory, PluginAccessor pluginAccessor, DispatchService dispatchService) {
        this.transactionTemplate = transactionTemplate;
        this.ao = ao;
        this.objectMapperFactory = objectMapperFactory;
        this.pluginAccessor = pluginAccessor;
        this.dispatchService = dispatchService;
    }

    @Nullable
    public JobRunnerResponse runJob(JobRunnerRequest request) {
        NotificationStoreCallback notificationStoreCallback = new NotificationStoreCallback(this.objectMapperFactory, this.pluginAccessor, this.dispatchService);
        this.transactionTemplate.execute(() -> {
            this.ao.stream(NotificationStoreAo.class, Query.select((String)"ID, BATCHING_COLUMN, CONTENT_TYPE, NOTIFICATION_KEY, PAYLOAD").order("BATCHING_COLUMN asc, CONTENT_TYPE asc, NOTIFICATION_KEY asc"), (EntityStreamCallback)notificationStoreCallback);
            notificationStoreCallback.sendRemainingBatch();
            this.ao.deleteWithSQL(NotificationStoreAo.class, null, new Object[0]);
            return null;
        });
        return null;
    }
}


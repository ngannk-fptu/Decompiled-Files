/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.notifications.NotificationPayload
 *  com.atlassian.confluence.notifications.PayloadProcessor
 *  com.atlassian.confluence.notifications.PayloadTransformer
 *  com.atlassian.confluence.notifications.ProductionAwareLoggerSwitch
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 */
package com.atlassian.confluence.notifications.batch.payload;

import com.atlassian.confluence.notifications.NotificationPayload;
import com.atlassian.confluence.notifications.PayloadProcessor;
import com.atlassian.confluence.notifications.PayloadTransformer;
import com.atlassian.confluence.notifications.ProductionAwareLoggerSwitch;
import com.atlassian.confluence.notifications.batch.payload.BatchingPayloadTransformer;
import com.atlassian.confluence.notifications.batch.service.BatchingKey;
import com.atlassian.confluence.notifications.batch.service.NotificationStoreService;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import java.io.IOException;

public class BatchingPayloadProcessor
implements PayloadProcessor {
    private final TransactionTemplate transactionTemplate;
    private final DarkFeatureManager darkFeatureManager;
    private final NotificationStoreService notificationStoreService;
    private final ProductionAwareLoggerSwitch log;

    public BatchingPayloadProcessor(TransactionTemplate transactionTemplate, DarkFeatureManager darkFeatureManager, NotificationStoreService notificationStoreService) {
        this.transactionTemplate = transactionTemplate;
        this.darkFeatureManager = darkFeatureManager;
        this.notificationStoreService = notificationStoreService;
        this.log = ProductionAwareLoggerSwitch.forCaller();
    }

    public <SOURCE, PAYLOAD extends NotificationPayload> boolean process(PAYLOAD payload, PayloadTransformer<SOURCE, PAYLOAD> payloadTransformer, ModuleCompleteKey forNotificationKey) {
        if (this.darkFeatureManager == null || !this.darkFeatureManager.isEnabledForAllUsers("notification.batch").orElse(false).booleanValue()) {
            return false;
        }
        if (!(payloadTransformer instanceof BatchingPayloadTransformer)) {
            return false;
        }
        return (Boolean)this.transactionTemplate.execute(() -> {
            try {
                return this.storeNotification(payload, (BatchingPayloadTransformer)payloadTransformer, forNotificationKey);
            }
            catch (IOException e) {
                this.log.errorOrDebug((Throwable)e, "Could not store batched notification", new Object[0]);
                return false;
            }
        });
    }

    private <PAYLOAD extends NotificationPayload> boolean storeNotification(PAYLOAD payload, BatchingPayloadTransformer<PAYLOAD> payloadTransformer, ModuleCompleteKey forNotificationKey) throws IOException {
        BatchingKey batchingColumnValue = payloadTransformer.getBatchingColumnValue(payload);
        if (batchingColumnValue == null || batchingColumnValue.equals(BatchingKey.NO_BATCHING)) {
            return false;
        }
        this.notificationStoreService.storeNotification(payload, forNotificationKey, batchingColumnValue);
        return true;
    }
}


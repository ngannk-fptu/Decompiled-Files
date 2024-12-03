/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalSpi
 *  com.atlassian.plugin.notifications.api.medium.ServerConfiguration
 *  com.atlassian.plugin.notifications.api.notification.UserNotificationPreferences
 *  com.atlassian.plugin.notifications.api.notification.UserNotificationPreferencesManager
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.confluence.notifications.batch.service;

import com.atlassian.annotations.ExperimentalSpi;
import com.atlassian.confluence.notifications.batch.service.BatchContext;
import com.atlassian.confluence.notifications.batch.service.BatchSectionProvider;
import com.atlassian.confluence.notifications.batch.service.BatchingRoleRecipient;
import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.api.notification.UserNotificationPreferences;
import com.atlassian.plugin.notifications.api.notification.UserNotificationPreferencesManager;
import com.atlassian.sal.api.user.UserKey;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ExperimentalSpi
public abstract class AbstractBatchSectionProvider<CONTEXT extends BatchContext>
implements BatchSectionProvider<List<CONTEXT>> {
    private final UserNotificationPreferencesManager preferencesManager;

    public AbstractBatchSectionProvider(UserNotificationPreferencesManager preferencesManager) {
        this.preferencesManager = preferencesManager;
    }

    @Override
    public final BatchSectionProvider.BatchOutput handle(BatchingRoleRecipient recipient, List<List<CONTEXT>> contexts, ServerConfiguration serverConfiguration) {
        if (contexts == null || contexts.isEmpty()) {
            return new BatchSectionProvider.BatchOutput();
        }
        List flattened = contexts.stream().filter(x -> x != null).flatMap(x -> x.stream()).collect(Collectors.toList());
        Set<UserKey> contributorKeys = flattened.stream().map(x -> x.getOriginator()).collect(Collectors.toSet());
        UserNotificationPreferences preferences = this.preferencesManager.getPreferences(recipient.getUserKey());
        boolean watchOwnActions = preferences.isOwnEventNotificationsEnabled(serverConfiguration);
        List batchContexts = flattened;
        if (!watchOwnActions) {
            batchContexts = flattened.stream().filter(x -> !recipient.getUserKey().equals((Object)x.getOriginator())).collect(Collectors.toList());
            contributorKeys.remove(recipient.getUserKey());
        }
        if (batchContexts.isEmpty()) {
            return new BatchSectionProvider.BatchOutput();
        }
        return this.processBatch(recipient, batchContexts, contributorKeys);
    }

    public BatchSectionProvider.BatchOutput processBatch(BatchingRoleRecipient recipient, List<CONTEXT> context, Set<UserKey> contributorKeys) {
        return new BatchSectionProvider.BatchOutput();
    }
}


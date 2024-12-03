/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.notifications.Notification
 *  com.atlassian.confluence.notifications.NotificationPayload
 *  com.atlassian.confluence.notifications.RecipientsProvider
 *  com.atlassian.confluence.notifications.RecipientsProviderTemplate
 *  com.atlassian.confluence.notifications.impl.descriptors.NotificationDescriptor
 *  com.atlassian.confluence.notifications.impl.descriptors.RecipientProviderDescriptor
 *  com.atlassian.fugue.Iterables
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient
 *  com.atlassian.plugin.notifications.spi.UserRole
 *  com.atlassian.plugin.tracker.DefaultPluginModuleTracker
 *  com.atlassian.plugin.tracker.PluginModuleTracker
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.collect.ImmutableList
 *  org.codehaus.jackson.type.TypeReference
 */
package com.atlassian.confluence.notifications.batch.recipients;

import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.NotificationPayload;
import com.atlassian.confluence.notifications.RecipientsProvider;
import com.atlassian.confluence.notifications.RecipientsProviderTemplate;
import com.atlassian.confluence.notifications.batch.content.BatchingPayload;
import com.atlassian.confluence.notifications.batch.service.BatchingRecipientsProvider;
import com.atlassian.confluence.notifications.batch.service.BatchingRoleRecipient;
import com.atlassian.confluence.notifications.impl.descriptors.NotificationDescriptor;
import com.atlassian.confluence.notifications.impl.descriptors.RecipientProviderDescriptor;
import com.atlassian.fugue.Iterables;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient;
import com.atlassian.plugin.notifications.spi.UserRole;
import com.atlassian.plugin.tracker.DefaultPluginModuleTracker;
import com.atlassian.plugin.tracker.PluginModuleTracker;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.collect.ImmutableList;
import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import java.util.LinkedHashMap;
import org.codehaus.jackson.type.TypeReference;

public class BatchingRecipientsProviderImpl
extends RecipientsProviderTemplate<BatchingPayload> {
    private final PluginModuleTracker<NotificationPayload, NotificationDescriptor<NotificationPayload>> notificationProviderTracker;
    private final PluginModuleTracker<RecipientsProvider, RecipientProviderDescriptor> recipientsProviderTracker;

    public BatchingRecipientsProviderImpl(PluginAccessor pluginAccessor, PluginEventManager pluginEventManager) throws ClassNotFoundException {
        this.recipientsProviderTracker = new DefaultPluginModuleTracker(pluginAccessor, pluginEventManager, RecipientProviderDescriptor.class);
        TypeReference<NotificationDescriptor<NotificationPayload>> notificationDescriptorTypeToken = new TypeReference<NotificationDescriptor<NotificationPayload>>(){};
        this.notificationProviderTracker = new DefaultPluginModuleTracker(pluginAccessor, pluginEventManager, (Class)((ParameterizedType)notificationDescriptorTypeToken.getType()).getRawType());
    }

    public Iterable<UserRole> getUserRoles() {
        return Collections.emptyList();
    }

    protected Iterable<RoleRecipient> computeUserBasedRecipients(Notification<BatchingPayload> notification) {
        LinkedHashMap<UserKey, BatchingRoleRecipient> resultMap = new LinkedHashMap<UserKey, BatchingRoleRecipient>();
        BatchingPayload payload = (BatchingPayload)notification.getPayload();
        LinkedHashMap<ModuleCompleteKey, Object> innerPayloads = payload.getPayloads();
        int payloadIdx = 0;
        Class<NotificationPayload> payloadType = null;
        ModuleCompleteKey lastPayloadType = null;
        for (ModuleCompleteKey key : innerPayloads.keySet()) {
            if (!key.equals(lastPayloadType)) {
                lastPayloadType = key;
                payloadType = this.findDescriptorPayloadType(key);
            }
            this.addRecipientsFromProviders(resultMap, payload, payloadIdx, payloadType);
            ++payloadIdx;
        }
        return ImmutableList.copyOf(resultMap.values());
    }

    private Class<NotificationPayload> findDescriptorPayloadType(ModuleCompleteKey key) {
        for (NotificationDescriptor descriptor : this.notificationProviderTracker.getModuleDescriptors()) {
            if (!new ModuleCompleteKey(descriptor.getCompleteKey()).equals((Object)key)) continue;
            return descriptor.getPayloadType();
        }
        return null;
    }

    private void addRecipientsFromProviders(LinkedHashMap<UserKey, BatchingRoleRecipient> resultMap, BatchingPayload payload, int payloadIdx, Class<NotificationPayload> payloadType) {
        for (RecipientsProvider recipientsProvider : this.recipientsProviderTracker.getModules()) {
            Option random;
            Iterable<RoleRecipient> recipients;
            BatchingRecipientsProvider provider;
            if (!(recipientsProvider instanceof BatchingRecipientsProvider) || !(provider = (BatchingRecipientsProvider)recipientsProvider).getPayloadType().isAssignableFrom(payloadType) || (recipients = provider.batchUserBasedRecipientsFor((random = Iterables.first(payload.getOriginators())).isDefined() ? ((UserKey)random.get()).getStringValue() : null, payload.getBatchingId(), payload.getContentType())) == null) continue;
            this.markRecipientPayloads(resultMap, payloadIdx, recipients);
        }
    }

    private void markRecipientPayloads(LinkedHashMap<UserKey, BatchingRoleRecipient> resultMap, int payloadIdx, Iterable<RoleRecipient> recipients) {
        for (RoleRecipient roleRecipient : recipients) {
            UserKey userKey = roleRecipient.getUserKey();
            BatchingRoleRecipient batchingRoleRecipient = resultMap.get(userKey);
            if (batchingRoleRecipient == null) {
                batchingRoleRecipient = new BatchingRoleRecipient(roleRecipient.getRole(), userKey);
                resultMap.put(userKey, batchingRoleRecipient);
            }
            batchingRoleRecipient.addUserRole(roleRecipient.getRole());
            batchingRoleRecipient.setPayloadIdx(payloadIdx);
        }
    }
}


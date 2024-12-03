/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.notifications.DispatchService
 *  com.atlassian.confluence.notifications.NotificationPayload
 *  com.atlassian.confluence.notifications.ProductionAwareLoggerSwitch
 *  com.atlassian.confluence.notifications.impl.ObjectMapperFactory
 *  com.atlassian.confluence.notifications.impl.descriptors.NotificationDescriptor
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.collect.ImmutableSet
 *  net.java.ao.EntityStreamCallback
 *  org.apache.commons.lang3.StringUtils
 *  org.codehaus.jackson.map.ObjectReader
 */
package com.atlassian.confluence.notifications.batch.job;

import com.atlassian.confluence.notifications.DispatchService;
import com.atlassian.confluence.notifications.NotificationPayload;
import com.atlassian.confluence.notifications.ProductionAwareLoggerSwitch;
import com.atlassian.confluence.notifications.batch.ao.NotificationStoreAo;
import com.atlassian.confluence.notifications.batch.content.SimpleBatchingPayload;
import com.atlassian.confluence.notifications.batch.descriptor.NotificationBatchingDescriptor;
import com.atlassian.confluence.notifications.batch.service.BatchingProcessor;
import com.atlassian.confluence.notifications.impl.ObjectMapperFactory;
import com.atlassian.confluence.notifications.impl.descriptors.NotificationDescriptor;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.java.ao.EntityStreamCallback;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectReader;

class NotificationStoreCallback
implements EntityStreamCallback<NotificationStoreAo, Integer> {
    private final ObjectMapperFactory objectMapperFactory;
    private final NotificationDescriptor<SimpleBatchingPayload> batchingNotificationDescriptor;
    private final DispatchService dispatchService;
    private final PluginAccessor pluginAccessor;
    private final ProductionAwareLoggerSwitch log;
    private String prevBatchingId = null;
    private Set<UserKey> originators = new HashSet<UserKey>();
    private String prevContentType = null;
    private ModuleCompleteKey prevNotificationKey = null;
    private Object context = null;
    private Map<ModuleCompleteKey, Object> newNotification = new HashMap<ModuleCompleteKey, Object>();
    private BatchingProcessor processor = null;

    public NotificationStoreCallback(ObjectMapperFactory objectMapperFactory, PluginAccessor pluginAccessor, DispatchService dispatchService) {
        this.objectMapperFactory = objectMapperFactory;
        this.dispatchService = dispatchService;
        this.pluginAccessor = pluginAccessor;
        this.batchingNotificationDescriptor = (NotificationDescriptor)pluginAccessor.getPluginModule("com.atlassian.confluence.plugins.confluence-notifications-batch-plugin:batching-notification");
        this.log = ProductionAwareLoggerSwitch.forCaller();
    }

    public void onRowRead(NotificationStoreAo notificationStoreAo) {
        String batchingId = notificationStoreAo.getBatchingColumn();
        String contentType = notificationStoreAo.getContentType();
        String strNotificationKey = notificationStoreAo.getNotificationKey();
        ModuleCompleteKey notificationKey = StringUtils.isBlank((CharSequence)strNotificationKey) ? null : new ModuleCompleteKey(strNotificationKey);
        String payloadStr = notificationStoreAo.getPayload();
        if (batchingId == null || contentType == null || notificationKey == null) {
            return;
        }
        this.checkForChangeOfBatch(batchingId, contentType);
        this.checkForChangeOfModule(notificationKey);
        if (this.processor == null) {
            return;
        }
        this.processBatch(payloadStr, strNotificationKey);
    }

    private void checkForChangeOfBatch(String batchingId, String contentType) {
        if (!batchingId.equals(this.prevBatchingId) || !contentType.equals(this.prevContentType)) {
            this.startNewBatch();
            if (this.prevBatchingId != null) {
                this.sendAndClear(this.originators, this.prevBatchingId, this.prevContentType, this.newNotification);
            }
            this.prevBatchingId = batchingId;
            this.prevContentType = contentType;
            this.originators.clear();
        }
    }

    private void checkForChangeOfModule(ModuleCompleteKey notificationKey) {
        if (!notificationKey.equals((Object)this.prevNotificationKey)) {
            this.startNewBatch();
            this.prevNotificationKey = notificationKey;
            this.processor = this.findBatchingProcessor(notificationKey);
        }
    }

    private void startNewBatch() {
        if (this.context != null) {
            this.newNotification.put(this.prevNotificationKey, this.context);
        }
        this.context = null;
    }

    private void processBatch(String payloadStr, String notificationKey) {
        try {
            Object payload = this.readNotification(payloadStr, this.processor.getPayloadTypeImpl());
            payload.setNotificationKey(notificationKey);
            Optional originator = payload.getOriginatorUserKey();
            if (originator.isPresent()) {
                this.originators.add((UserKey)originator.get());
            }
            this.context = this.processor.process(payload, this.context);
        }
        catch (IOException e) {
            this.log.errorOrDebug((Throwable)e, "Could not process notification batch", new Object[0]);
        }
    }

    public void sendRemainingBatch() {
        if (this.prevBatchingId != null && this.context != null) {
            this.newNotification.put(this.prevNotificationKey, this.context);
            this.sendAndClear(this.originators, this.prevBatchingId, this.prevContentType, this.newNotification);
        }
    }

    private BatchingProcessor findBatchingProcessor(ModuleCompleteKey notificationKey) {
        for (NotificationBatchingDescriptor batchingDescriptor : this.pluginAccessor.getEnabledModuleDescriptorsByClass(NotificationBatchingDescriptor.class)) {
            if (!batchingDescriptor.getNotificationKey().equals((Object)notificationKey)) continue;
            return (BatchingProcessor)batchingDescriptor.getModule();
        }
        return null;
    }

    private void sendAndClear(Set<UserKey> originators, String batchingId, String contentType, Map<ModuleCompleteKey, Object> newNotification) {
        if (!newNotification.isEmpty()) {
            this.dispatchService.dispatch(this.batchingNotificationDescriptor.getNotificationFactory().create((NotificationPayload)new SimpleBatchingPayload((Set<UserKey>)ImmutableSet.copyOf(originators), batchingId, contentType, new LinkedHashMap<ModuleCompleteKey, Object>(newNotification))));
            newNotification.clear();
            originators.clear();
        }
    }

    public <T extends NotificationPayload> T readNotification(String payload, Class<T> payloadClass) throws IOException {
        ObjectReader mapperReader = this.objectMapperFactory.buildObjectMapper().reader(payloadClass);
        return (T)((NotificationPayload)mapperReader.readValue(payload));
    }
}


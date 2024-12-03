/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.confluence.notifications.ProductionAwareLoggerSwitch
 *  com.atlassian.confluence.notifications.impl.ObjectMapperFactory
 *  com.atlassian.plugin.ModuleCompleteKey
 *  net.java.ao.DBParam
 *  org.codehaus.jackson.map.ObjectWriter
 */
package com.atlassian.confluence.notifications.batch.service;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.confluence.notifications.ProductionAwareLoggerSwitch;
import com.atlassian.confluence.notifications.batch.ao.NotificationStoreAo;
import com.atlassian.confluence.notifications.batch.service.BatchingKey;
import com.atlassian.confluence.notifications.batch.service.NotificationStoreService;
import com.atlassian.confluence.notifications.impl.ObjectMapperFactory;
import com.atlassian.plugin.ModuleCompleteKey;
import java.io.IOException;
import net.java.ao.DBParam;
import org.codehaus.jackson.map.ObjectWriter;

public class DefaultNotificationStoreService
implements NotificationStoreService {
    private final ObjectMapperFactory objectMapperFactory;
    private final ActiveObjects ao;
    private final ProductionAwareLoggerSwitch log = ProductionAwareLoggerSwitch.forCaller();

    public DefaultNotificationStoreService(ObjectMapperFactory objectMapperFactory, ActiveObjects ao) {
        this.objectMapperFactory = objectMapperFactory;
        this.ao = ao;
    }

    @Override
    public void storeNotification(Object payload, ModuleCompleteKey notificationKey, BatchingKey batchingKey) throws IOException {
        this.ao.executeInTransaction(() -> {
            ObjectWriter mapperWriter = this.objectMapperFactory.buildObjectMapper().writerWithType(payload.getClass());
            try {
                return (NotificationStoreAo)this.ao.create(NotificationStoreAo.class, new DBParam[]{new DBParam("PAYLOAD", (Object)mapperWriter.writeValueAsString(payload)), new DBParam("BATCHING_COLUMN", (Object)batchingKey.getKey()), new DBParam("NOTIFICATION_KEY", (Object)notificationKey.toString()), new DBParam("CONTENT_TYPE", (Object)batchingKey.getContentType())});
            }
            catch (IOException e) {
                this.log.errorOrDebug((Throwable)e, "Could not store batched notification as AO", new Object[0]);
                return null;
            }
        });
    }
}


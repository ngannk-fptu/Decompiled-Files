/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleCompleteKey
 */
package com.atlassian.confluence.notifications.batch.service;

import com.atlassian.confluence.notifications.batch.service.BatchingKey;
import com.atlassian.plugin.ModuleCompleteKey;
import java.io.IOException;

public interface NotificationStoreService {
    public void storeNotification(Object var1, ModuleCompleteKey var2, BatchingKey var3) throws IOException;
}


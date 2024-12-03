/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
 */
package com.atlassian.confluence.plugins.mobile.service.executor;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class PushThreadPoolTaskExecutor
extends ThreadPoolTaskExecutor {
    private static final String PUSH_NOTIFICATION_CORE_POOL_SIZE_PROPERTY = "push.notification.core.pool.size";
    private static final String PUSH_NOTIFICATION_MAX_POOL_SIZE_PROPERTY = "push.notification.max.pool.size";
    private static final String PUSH_NOTIFICATION_QUEUE_SIZE_PROPERTY = "push.notification.queue.size";

    public void setCorePoolSize(int corePoolSize) {
        super.setCorePoolSize(Integer.getInteger(PUSH_NOTIFICATION_CORE_POOL_SIZE_PROPERTY, corePoolSize).intValue());
    }

    public void setMaxPoolSize(int maxPoolSize) {
        super.setMaxPoolSize(Integer.getInteger(PUSH_NOTIFICATION_MAX_POOL_SIZE_PROPERTY, maxPoolSize).intValue());
    }

    public void setQueueCapacity(int queueCapacity) {
        super.setQueueCapacity(Integer.getInteger(PUSH_NOTIFICATION_QUEUE_SIZE_PROPERTY, queueCapacity).intValue());
    }
}


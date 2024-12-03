/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.eventservice.impl;

import com.hazelcast.spi.EventPublishingService;
import com.hazelcast.spi.impl.eventservice.impl.EventServiceImpl;
import com.hazelcast.util.executor.StripedRunnable;
import com.hazelcast.util.executor.TimeoutRunnable;
import java.util.concurrent.TimeUnit;

public final class LocalEventDispatcher
implements StripedRunnable,
TimeoutRunnable {
    private final EventServiceImpl eventService;
    private final String serviceName;
    private final Object event;
    private final Object listener;
    private final int orderKey;
    private final long timeoutMs;

    public LocalEventDispatcher(EventServiceImpl eventService, String serviceName, Object event, Object listener, int orderKey, long timeoutMs) {
        this.eventService = eventService;
        this.serviceName = serviceName;
        this.event = event;
        this.listener = listener;
        this.orderKey = orderKey;
        this.timeoutMs = timeoutMs;
    }

    @Override
    public long getTimeout() {
        return this.timeoutMs;
    }

    @Override
    public TimeUnit getTimeUnit() {
        return TimeUnit.MILLISECONDS;
    }

    @Override
    public void run() {
        EventPublishingService service = (EventPublishingService)this.eventService.nodeEngine.getService(this.serviceName);
        service.dispatchEvent(this.event, this.listener);
    }

    @Override
    public int getKey() {
        return this.orderKey;
    }

    public String getServiceName() {
        return this.serviceName;
    }

    public Object getEvent() {
        return this.event;
    }
}


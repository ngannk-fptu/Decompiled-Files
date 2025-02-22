/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.eventservice.impl;

import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.EventPublishingService;
import com.hazelcast.spi.impl.eventservice.impl.EventEnvelope;
import com.hazelcast.spi.impl.eventservice.impl.EventServiceImpl;
import com.hazelcast.spi.impl.eventservice.impl.EventServiceSegment;
import com.hazelcast.spi.impl.eventservice.impl.Registration;
import com.hazelcast.util.executor.StripedRunnable;

public class EventProcessor
implements StripedRunnable {
    private final EventServiceImpl eventService;
    private final int orderKey;
    private final EventEnvelope envelope;

    public EventProcessor(EventServiceImpl eventService, EventEnvelope envelope, int orderKey) {
        this.eventService = eventService;
        this.envelope = envelope;
        this.orderKey = orderKey;
    }

    @Override
    public void run() {
        this.process(this.envelope);
    }

    void process(EventEnvelope envelope) {
        Object event = this.getEvent(envelope);
        String serviceName = envelope.getServiceName();
        EventPublishingService service = (EventPublishingService)this.eventService.nodeEngine.getService(serviceName);
        Registration registration = this.getRegistration(envelope, serviceName);
        if (registration == null) {
            return;
        }
        service.dispatchEvent(event, registration.getListener());
    }

    private Registration getRegistration(EventEnvelope eventEnvelope, String serviceName) {
        EventServiceSegment segment = this.eventService.getSegment(serviceName, false);
        if (segment == null) {
            if (this.eventService.nodeEngine.isRunning()) {
                this.eventService.logger.warning("No service registration found for " + serviceName);
            }
            return null;
        }
        String id = eventEnvelope.getEventId();
        Registration registration = (Registration)segment.getRegistrationIdMap().get(id);
        if (registration == null) {
            if (this.eventService.nodeEngine.isRunning() && this.eventService.logger.isFinestEnabled()) {
                this.eventService.logger.finest("No registration found for " + serviceName + " / " + id);
            }
            return null;
        }
        if (!this.eventService.isLocal(registration)) {
            this.eventService.logger.severe("Invalid target for  " + registration);
            return null;
        }
        if (registration.getListener() == null) {
            this.eventService.logger.warning("Something seems wrong! Subscriber is local but listener instance is null! -> " + registration);
            return null;
        }
        return registration;
    }

    private Object getEvent(EventEnvelope eventEnvelope) {
        Object event = eventEnvelope.getEvent();
        if (event instanceof Data) {
            event = this.eventService.nodeEngine.toObject(event);
        }
        return event;
    }

    @Override
    public int getKey() {
        return this.orderKey;
    }

    public String toString() {
        return "EventProcessor{envelope=" + this.envelope + '}';
    }
}


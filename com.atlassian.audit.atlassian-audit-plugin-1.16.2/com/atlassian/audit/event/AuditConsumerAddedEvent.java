/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditConsumer
 */
package com.atlassian.audit.event;

import com.atlassian.audit.api.AuditConsumer;

public class AuditConsumerAddedEvent {
    private final AuditConsumer consumerService;

    public AuditConsumerAddedEvent(AuditConsumer consumerService) {
        this.consumerService = consumerService;
    }

    public AuditConsumer getConsumerService() {
        return this.consumerService;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditConsumer
 *  com.atlassian.audit.entity.AuditEntity
 */
package com.atlassian.audit.broker;

import com.atlassian.audit.api.AuditConsumer;
import com.atlassian.audit.broker.AuditEntityRejectionHandler;
import com.atlassian.audit.broker.InternalAuditBroker;
import com.atlassian.audit.entity.AuditEntity;
import java.util.Arrays;
import java.util.List;

public class CompositeRejectionHandler
implements AuditEntityRejectionHandler {
    private List<AuditEntityRejectionHandler> handlers;

    public CompositeRejectionHandler(AuditEntityRejectionHandler ... handlers) {
        this.handlers = Arrays.asList(handlers);
    }

    @Override
    public void reject(InternalAuditBroker broker, AuditConsumer consumer, List<AuditEntity> batch) {
        this.handlers.forEach(x -> x.reject(broker, consumer, batch));
    }
}


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
import java.util.List;

public class SilentRejectionHandler
implements AuditEntityRejectionHandler {
    @Override
    public void reject(InternalAuditBroker broker, AuditConsumer consumer, List<AuditEntity> batch) {
    }
}


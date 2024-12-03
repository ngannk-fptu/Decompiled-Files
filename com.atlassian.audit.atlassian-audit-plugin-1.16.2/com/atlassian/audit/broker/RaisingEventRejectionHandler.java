/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditConsumer
 *  com.atlassian.audit.entity.AuditAttribute
 *  com.atlassian.audit.entity.AuditEntity
 *  com.atlassian.audit.entity.AuditType
 *  com.atlassian.audit.entity.CoverageArea
 *  com.atlassian.audit.entity.CoverageLevel
 */
package com.atlassian.audit.broker;

import com.atlassian.audit.api.AuditConsumer;
import com.atlassian.audit.broker.AuditEntityRejectionHandler;
import com.atlassian.audit.broker.InternalAuditBroker;
import com.atlassian.audit.entity.AuditAttribute;
import com.atlassian.audit.entity.AuditEntity;
import com.atlassian.audit.entity.AuditType;
import com.atlassian.audit.entity.CoverageArea;
import com.atlassian.audit.entity.CoverageLevel;
import java.util.List;

public class RaisingEventRejectionHandler
implements AuditEntityRejectionHandler {
    static final AuditType AUDIT_EVENT_DROPPED = new AuditType(CoverageArea.AUDIT_LOG, "Auditing", "Audit events discarded", CoverageLevel.BASE);
    static final String NUM_EVENTS = "number of events";
    static final String CONSUMER_NAME = "audit consumer";

    @Override
    public void reject(InternalAuditBroker broker, AuditConsumer consumer, List<AuditEntity> batch) {
        broker.audit(AuditEntity.builder((AuditType)AUDIT_EVENT_DROPPED).extraAttribute(new AuditAttribute(NUM_EVENTS, String.valueOf(batch.size()))).extraAttribute(new AuditAttribute(CONSUMER_NAME, consumer.getClass().getSimpleName())).build());
    }
}


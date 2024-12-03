/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditConsumer
 *  com.atlassian.audit.entity.AuditEntity
 */
package com.atlassian.audit.broker;

import com.atlassian.audit.api.AuditConsumer;
import com.atlassian.audit.broker.InternalAuditBroker;
import com.atlassian.audit.entity.AuditEntity;
import java.util.List;

public interface AuditEntityRejectionHandler {
    public void reject(InternalAuditBroker var1, AuditConsumer var2, List<AuditEntity> var3);
}


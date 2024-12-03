/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditConsumer
 *  com.atlassian.audit.entity.AuditEntity
 */
package com.atlassian.audit.broker;

import com.atlassian.audit.api.AuditConsumer;
import com.atlassian.audit.broker.AuditConsumerExceptionHandler;
import com.atlassian.audit.entity.AuditEntity;
import java.util.List;

public class SilentAuditConsumerExceptionHandler
implements AuditConsumerExceptionHandler {
    @Override
    public void handle(AuditConsumer auditConsumer, RuntimeException exception, List<AuditEntity> batch) {
    }
}


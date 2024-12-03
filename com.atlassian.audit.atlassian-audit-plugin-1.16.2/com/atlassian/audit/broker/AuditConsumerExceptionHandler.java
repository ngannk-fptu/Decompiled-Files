/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditConsumer
 *  com.atlassian.audit.entity.AuditEntity
 */
package com.atlassian.audit.broker;

import com.atlassian.audit.api.AuditConsumer;
import com.atlassian.audit.entity.AuditEntity;
import java.util.List;

public interface AuditConsumerExceptionHandler {
    public void handle(AuditConsumer var1, RuntimeException var2, List<AuditEntity> var3);
}


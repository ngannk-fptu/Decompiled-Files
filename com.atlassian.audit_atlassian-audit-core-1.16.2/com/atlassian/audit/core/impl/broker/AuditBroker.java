/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.entity.AuditEntity
 *  javax.annotation.Nonnull
 */
package com.atlassian.audit.core.impl.broker;

import com.atlassian.audit.entity.AuditEntity;
import javax.annotation.Nonnull;

public interface AuditBroker {
    public void audit(@Nonnull AuditEntity var1);
}


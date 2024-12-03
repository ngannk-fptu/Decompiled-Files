/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.entity.ChangedValue
 */
package com.atlassian.confluence.impl.audit.handler;

import com.atlassian.audit.entity.ChangedValue;
import com.atlassian.confluence.impl.audit.handler.AuditAction;
import java.util.List;
import java.util.Optional;

public interface AuditHandlerService {
    public <T> List<ChangedValue> handle(T var1, AuditAction var2);

    public <T> List<ChangedValue> handle(Optional<T> var1, Optional<T> var2);

    public <T> List<ChangedValue> handle(T var1, T var2);
}


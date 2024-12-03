/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.entity.ChangedValue
 */
package com.atlassian.confluence.impl.audit.handler;

import com.atlassian.audit.entity.ChangedValue;
import com.atlassian.confluence.impl.audit.handler.AuditAction;
import com.atlassian.confluence.impl.audit.handler.AuditHandlerDescription;
import com.atlassian.confluence.impl.audit.handler.Handler;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

interface AuditHandler<T>
extends Handler<T>,
AuditHandlerDescription {
    default public List<ChangedValue> handle(T ref, AuditAction action) {
        return this.handle(Optional.empty(), ref, action).collect(Collectors.toList());
    }

    default public List<ChangedValue> handle(T oldT, T newT) {
        return this.handle(Optional.empty(), oldT, newT).collect(Collectors.toList());
    }

    public Handler<T> reference();

    public Class<T> getHandledClass();
}


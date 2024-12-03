/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.entity.AuditEntity
 *  javax.annotation.Nonnull
 */
package com.atlassian.audit.core;

import com.atlassian.audit.core.impl.broker.AuditBroker;
import com.atlassian.audit.entity.AuditEntity;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.annotation.Nonnull;

public class ReflectionAuditBroker
implements AuditBroker {
    private final Object delegate;
    private final Method auditMethod;

    public ReflectionAuditBroker(Object delegate) {
        this.delegate = delegate;
        try {
            this.auditMethod = delegate.getClass().getMethod("audit", AuditEntity.class);
        }
        catch (NoSuchMethodException e) {
            throw new RuntimeException("Delegate object should have a public 'audit' method with single AuditEntity parameter", e);
        }
    }

    @Override
    public void audit(@Nonnull AuditEntity entity) {
        try {
            this.auditMethod.invoke(this.delegate, entity);
        }
        catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.entity.ChangedValue
 *  org.apache.commons.lang3.ClassUtils
 *  org.apache.commons.lang3.ClassUtils$Interfaces
 */
package com.atlassian.confluence.impl.audit.handler;

import com.atlassian.audit.entity.ChangedValue;
import com.atlassian.confluence.impl.audit.handler.AuditAction;
import com.atlassian.confluence.impl.audit.handler.AuditHandler;
import com.atlassian.confluence.impl.audit.handler.AuditHandlerService;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ClassUtils;

class DefaultAuditHandlerService
implements AuditHandlerService {
    private final Map<Class<?>, AuditHandler<?>> auditHandlers;

    public DefaultAuditHandlerService(Collection<AuditHandler<?>> auditHandlers) {
        this.auditHandlers = auditHandlers.stream().collect(Collectors.toMap(AuditHandler::getHandledClass, Function.identity()));
    }

    @Override
    public <T> List<ChangedValue> handle(T object, AuditAction action) {
        return this.getHandler(object).handle((AuditAction)((Object)object), action);
    }

    @Override
    public <T> List<ChangedValue> handle(Optional<T> oldT, Optional<T> newT) {
        if (!oldT.isPresent() && !newT.isPresent()) {
            return Collections.emptyList();
        }
        if (!oldT.isPresent()) {
            return this.handle(newT.get(), (T)((Object)AuditAction.ADD));
        }
        if (!newT.isPresent()) {
            return this.handle(oldT.get(), (T)((Object)AuditAction.REMOVE));
        }
        return this.handle(oldT.get(), newT.get());
    }

    @Override
    public <T> List<ChangedValue> handle(T oldT, T newT) {
        return this.getHandler(oldT).handle(oldT, newT);
    }

    private <T> AuditHandler<? super T> getHandler(T object) {
        for (Class clazz : ClassUtils.hierarchy(object.getClass(), (ClassUtils.Interfaces)ClassUtils.Interfaces.INCLUDE)) {
            AuditHandler<?> handler = this.auditHandlers.get(clazz);
            if (handler == null) continue;
            return handler;
        }
        throw new IllegalArgumentException("No handler for class " + object.getClass());
    }
}


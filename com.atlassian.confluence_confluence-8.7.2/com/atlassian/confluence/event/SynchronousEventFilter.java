/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.db.HibernateConfig
 *  com.atlassian.event.internal.AsynchronousEventResolver
 *  com.atlassian.tenancy.api.event.TenantArrivedEvent
 *  com.google.common.base.Predicate
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.confluence.event;

import com.atlassian.config.db.HibernateConfig;
import com.atlassian.confluence.event.TypeWhitelist;
import com.atlassian.event.internal.AsynchronousEventResolver;
import com.atlassian.tenancy.api.event.TenantArrivedEvent;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import java.util.Set;

public class SynchronousEventFilter
implements AsynchronousEventResolver {
    private final AsynchronousEventResolver delegate;
    private final Predicate<Object> synchronousEventPredicate;

    public SynchronousEventFilter(AsynchronousEventResolver delegate, Predicate<Object> synchronousEventPredicate) {
        this.delegate = delegate;
        this.synchronousEventPredicate = synchronousEventPredicate;
    }

    public boolean isAsynchronousEvent(Object event) {
        if (this.synchronousEventPredicate.apply(event)) {
            return false;
        }
        return this.delegate.isAsynchronousEvent(event);
    }

    public static AsynchronousEventResolver create(AsynchronousEventResolver delegate, HibernateConfig hibernateConfig) {
        boolean isHsql = hibernateConfig.isHSQL();
        TypeWhitelist filter = new TypeWhitelist((Set<Class>)ImmutableSet.of(TenantArrivedEvent.class));
        return new SynchronousEventFilter(delegate, (Predicate<Object>)((Predicate)event -> isHsql && filter.apply(event)));
    }
}


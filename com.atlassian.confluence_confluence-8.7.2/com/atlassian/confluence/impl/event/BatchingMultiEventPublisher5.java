/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault
 *  com.atlassian.confluence.impl.hibernate.HibernateSessionManager5
 *  com.atlassian.crowd.core.event.MultiEventPublisher
 *  com.atlassian.event.api.EventPublisher
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.hibernate.SessionFactory
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.event;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault;
import com.atlassian.confluence.impl.hibernate.HibernateSessionManager5;
import com.atlassian.crowd.core.event.MultiEventPublisher;
import com.atlassian.event.api.EventPublisher;
import java.util.Collection;
import java.util.function.Function;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
public class BatchingMultiEventPublisher5
implements MultiEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(BatchingMultiEventPublisher5.class);
    private static final int DEFAULT_EVENT_BATCH_SIZE = 50;
    private final SessionFactory sessionFactory;
    private final HibernateSessionManager5 hibernateSessionManager;
    private final EventPublisher delegate;

    public BatchingMultiEventPublisher5(HibernateSessionManager5 hibernateSessionManager, SessionFactory sessionFactory, EventPublisher delegate) {
        this.hibernateSessionManager = hibernateSessionManager;
        this.sessionFactory = sessionFactory;
        this.delegate = delegate;
    }

    public void publishAll(Collection<Object> events) {
        try {
            this.sessionFactory.getCurrentSession().flush();
            this.hibernateSessionManager.executeThenFlushAndClearSession(events, 50, events.size(), (Function)new Function<Object, Void>(){

                @Override
                public @Nullable Void apply(Object event) {
                    BatchingMultiEventPublisher5.this.publish(event);
                    return null;
                }

                public String toString() {
                    return "BatchingMultiEventPublisher publishAll batch";
                }
            });
        }
        catch (IllegalStateException e) {
            log.error("Failed to flush existing session. Unable to publish events", (Throwable)e);
        }
    }

    public void publish(Object event) {
        this.delegate.publish(event);
    }

    public void register(Object listener) {
        this.delegate.register(listener);
    }

    public void unregister(Object listener) {
        this.delegate.unregister(listener);
    }

    public void unregisterAll() {
        this.delegate.unregisterAll();
    }
}


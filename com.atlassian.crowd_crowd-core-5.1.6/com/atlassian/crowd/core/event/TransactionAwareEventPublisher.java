/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.event.TransactionAwareImmediateEvent
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.event.events.PluginEvent
 *  com.atlassian.plugin.event.events.PluginFrameworkEvent
 *  org.springframework.transaction.support.TransactionSynchronization
 *  org.springframework.transaction.support.TransactionSynchronizationAdapter
 *  org.springframework.transaction.support.TransactionSynchronizationManager
 */
package com.atlassian.crowd.core.event;

import com.atlassian.crowd.core.event.DelegatingMultiEventPublisher;
import com.atlassian.crowd.core.event.MultiEventPublisher;
import com.atlassian.crowd.event.TransactionAwareImmediateEvent;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.event.events.PluginEvent;
import com.atlassian.plugin.event.events.PluginFrameworkEvent;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class TransactionAwareEventPublisher
extends DelegatingMultiEventPublisher
implements EventPublisher,
MultiEventPublisher {
    TransactionAwareEventPublisher(EventPublisher delegate) {
        super(delegate);
    }

    @Override
    public void publish(Object event) {
        if (this.shouldPostponeEvent(event)) {
            TransactionSynchronizationManager.registerSynchronization((TransactionSynchronization)this.createSynchronization(event));
        } else {
            this.delegate.publish(event);
        }
    }

    @Override
    public void publishAll(Collection<Object> events) {
        Map<Boolean, List<Object>> partitionedEvents = events.stream().collect(Collectors.partitioningBy(this::shouldPostponeEvent));
        List<Object> postponedEvents = partitionedEvents.get(true);
        List<Object> immediateEvents = partitionedEvents.get(false);
        if (postponedEvents != null && !postponedEvents.isEmpty()) {
            TransactionSynchronizationManager.registerSynchronization((TransactionSynchronization)this.createSynchronization(postponedEvents));
        }
        if (immediateEvents != null) {
            immediateEvents.forEach(arg_0 -> ((EventPublisher)this.delegate).publish(arg_0));
        }
    }

    private boolean shouldPostponeEvent(Object event) {
        if (event instanceof PluginFrameworkEvent || event instanceof PluginEvent) {
            return false;
        }
        return !(event instanceof TransactionAwareImmediateEvent) && TransactionSynchronizationManager.isActualTransactionActive() && TransactionSynchronizationManager.isSynchronizationActive();
    }

    private TransactionSynchronizationAdapter createSynchronization(final Object event) {
        return new TransactionSynchronizationAdapter(){

            public void afterCommit() {
                TransactionAwareEventPublisher.this.delegate.publish(event);
            }
        };
    }

    private TransactionSynchronizationAdapter createSynchronization(final Collection<Object> events) {
        return new TransactionSynchronizationAdapter(){

            public void afterCommit() {
                events.forEach(arg_0 -> ((EventPublisher)TransactionAwareEventPublisher.this.delegate).publish(arg_0));
            }
        };
    }
}


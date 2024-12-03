/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.AsynchronousPreferred
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.annotations.VisibleForTesting
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.support.TransactionSynchronization
 *  org.springframework.transaction.support.TransactionSynchronizationAdapter
 */
package com.atlassian.confluence.impl.search;

import com.atlassian.confluence.core.SynchronizationManager;
import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEvent;
import com.atlassian.confluence.search.IndexFlushRequester;
import com.atlassian.confluence.search.v2.lucene.SearchIndex;
import com.atlassian.event.api.AsynchronousPreferred;
import com.atlassian.event.api.EventPublisher;
import com.google.common.annotations.VisibleForTesting;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;

public class DefaultIndexFlushRequester
implements IndexFlushRequester {
    private static final Logger log = LoggerFactory.getLogger(DefaultIndexFlushRequester.class);
    private final ThreadLocal<Boolean> scheduled = ThreadLocal.withInitial(() -> false);
    private final SynchronizationManager synchronizationManager;
    private final EventPublisher eventPublisher;
    private final SearchIndex searchIndex;
    private volatile boolean enabled = true;

    public DefaultIndexFlushRequester(SynchronizationManager synchronizationManager, EventPublisher eventPublisher, SearchIndex searchIndex) {
        this.synchronizationManager = Objects.requireNonNull(synchronizationManager);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.searchIndex = Objects.requireNonNull(searchIndex);
    }

    @Override
    public void requestFlush() {
        if (!this.enabled) {
            return;
        }
        if (!this.synchronizationManager.isTransactionActive()) {
            this.publishRequestIndexFlushEvent();
            return;
        }
        if (this.scheduled.get().booleanValue()) {
            log.trace("Index flush on successful commit has already been requested");
            return;
        }
        log.debug("Scheduling index flush request on successful commit");
        this.scheduled.set(true);
        this.synchronizationManager.registerSynchronization((TransactionSynchronization)new TransactionSynchronizationAdapter(){

            public void afterCompletion(int status) {
                log.debug("Transaction completed with status: {}", (Object)status);
                DefaultIndexFlushRequester.this.scheduled.remove();
                if (DefaultIndexFlushRequester.this.enabled && status == 0) {
                    DefaultIndexFlushRequester.this.publishRequestIndexFlushEvent();
                }
            }
        });
    }

    @Override
    public void resume() {
        this.enabled = true;
    }

    @Override
    public IndexFlushRequester.Resumer pause() {
        this.enabled = false;
        return this::resume;
    }

    private void publishRequestIndexFlushEvent() {
        this.eventPublisher.publish((Object)new RequestIndexFlushEvent(this, this.searchIndex));
    }

    @AsynchronousPreferred
    @VisibleForTesting
    public static class RequestIndexFlushEvent
    extends ConfluenceEvent
    implements ClusterEvent {
        private static final long serialVersionUID = 5840660416914484815L;
        private final SearchIndex affectedSearchIndex;

        public RequestIndexFlushEvent(Object src, SearchIndex affectedSearchIndex) {
            super(src);
            this.affectedSearchIndex = Objects.requireNonNull(affectedSearchIndex);
        }

        public SearchIndex getAffectedSearchIndex() {
            return this.affectedSearchIndex;
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.PlatformTransactionManager
 *  org.springframework.transaction.TransactionDefinition
 *  org.springframework.transaction.interceptor.DefaultTransactionAttribute
 *  org.springframework.transaction.support.TransactionTemplate
 */
package com.atlassian.confluence.internal.content.collab;

import com.atlassian.confluence.event.events.content.page.synchrony.ContentUpdatedEvent;
import com.atlassian.confluence.internal.content.collab.ReconcileContentRegisterTask;
import com.atlassian.event.api.EventPublisher;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.support.TransactionTemplate;

public class ReconcileContentTask
implements ReconcileContentRegisterTask<ContentUpdatedEvent> {
    private static Logger logger = LoggerFactory.getLogger(ReconcileContentTask.class);
    private final Set<ContentUpdatedEvent> contentToReconcileEvents;
    private final PlatformTransactionManager transactionManager;
    private final EventPublisher eventPublisher;

    public ReconcileContentTask(EventPublisher eventPublisher, PlatformTransactionManager transactionManager) {
        this.eventPublisher = eventPublisher;
        this.contentToReconcileEvents = new HashSet<ContentUpdatedEvent>();
        this.transactionManager = transactionManager;
    }

    @Override
    public void run() {
        logger.info("Processing reconcile for {} events", (Object)this.contentToReconcileEvents.size());
        TransactionTemplate transactionTemplate = new TransactionTemplate(this.transactionManager, (TransactionDefinition)new DefaultTransactionAttribute(3));
        transactionTemplate.execute(status -> {
            for (ContentUpdatedEvent contentUpdatedEvent : this.contentToReconcileEvents) {
                this.eventPublisher.publish((Object)contentUpdatedEvent);
            }
            return null;
        });
        logger.info("Processing reconcile for {} events ===> DONE", (Object)this.contentToReconcileEvents.size());
    }

    @Override
    public void registerReconcileContent(ContentUpdatedEvent contentUpdatedEvent) {
        if (contentUpdatedEvent == null) {
            return;
        }
        this.contentToReconcileEvents.add(contentUpdatedEvent);
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.event.api.EventListener
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.PlatformTransactionManager
 */
package com.atlassian.confluence.internal.content.collab;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SynchronizationManager;
import com.atlassian.confluence.event.events.content.attachment.AttachmentCreateEvent;
import com.atlassian.confluence.event.events.content.attachment.AttachmentTrashedEvent;
import com.atlassian.confluence.event.events.content.attachment.GeneralAttachmentRestoreEvent;
import com.atlassian.confluence.event.events.content.attachment.GeneralAttachmentUpdateEvent;
import com.atlassian.confluence.internal.ContentEntityManagerInternal;
import com.atlassian.confluence.internal.content.collab.ContentReconciliationManager;
import com.atlassian.confluence.internal.content.collab.IncludeOwnContentEnum;
import com.atlassian.confluence.internal.content.collab.OwningContent;
import com.atlassian.confluence.internal.content.collab.ReconcileContentRegisterTask;
import com.atlassian.confluence.internal.content.collab.ReconcileReferringContentTask;
import com.atlassian.confluence.internal.content.collab.ThreadLocalCleanUpSynchronization;
import com.atlassian.confluence.internal.persistence.ContentEntityObjectDaoInternal;
import com.atlassian.confluence.links.LinkManager;
import com.atlassian.event.api.EventListener;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;

public class AttachmentRelatedContentReconciliationListener {
    private static final Logger log = LoggerFactory.getLogger(AttachmentRelatedContentReconciliationListener.class);
    private final LinkManager linkManager;
    private final ContentReconciliationManager reconciliationManager;
    private final SynchronizationManager synchronizationManager;
    private final PlatformTransactionManager transactionManager;
    private final ContentEntityManagerInternal contentEntityManager;
    private final ContentEntityObjectDaoInternal contentEntityObjectDao;
    private final ThreadLocal<ReconcileContentRegisterTask<OwningContent>> successfulCommitTaskThreadLocal;

    public AttachmentRelatedContentReconciliationListener(LinkManager linkManager, ContentReconciliationManager reconciliationManager, SynchronizationManager synchronizationManager, PlatformTransactionManager transactionManager, ContentEntityManagerInternal contentEntityManager, ContentEntityObjectDaoInternal contentEntityObjectDao) {
        this.linkManager = linkManager;
        this.reconciliationManager = reconciliationManager;
        this.synchronizationManager = synchronizationManager;
        this.transactionManager = transactionManager;
        this.contentEntityManager = contentEntityManager;
        this.contentEntityObjectDao = contentEntityObjectDao;
        this.successfulCommitTaskThreadLocal = new ThreadLocal();
    }

    @EventListener
    public void handleEvent(AttachmentCreateEvent event) {
        this.reconcileReferringContent(event.getAttachedTo(), IncludeOwnContentEnum.EXCLUDE);
    }

    @EventListener
    public void handleEvent(GeneralAttachmentUpdateEvent event) {
        this.reconcileReferringContent(event.getAttachedTo(), IncludeOwnContentEnum.INCLUDE);
    }

    @EventListener
    public void handleEvent(AttachmentTrashedEvent event) {
        this.reconcileReferringContent(event.getAttachedTo(), IncludeOwnContentEnum.INCLUDE);
    }

    @EventListener
    public void handleEvent(GeneralAttachmentRestoreEvent event) {
        this.reconcileReferringContent(event.getAttachedTo(), IncludeOwnContentEnum.INCLUDE);
    }

    private void reconcileReferringContent(ContentEntityObject attachedTo, IncludeOwnContentEnum includeOwnContentEnum) {
        long id = attachedTo.getId();
        ReconcileReferringContentTask reconcileContentRegisterTask = this.successfulCommitTaskThreadLocal.get();
        if (reconcileContentRegisterTask == null) {
            log.info("No ReconcileContentRegisterTask in current Thread we will create one");
            reconcileContentRegisterTask = new ReconcileReferringContentTask(this.transactionManager, this.contentEntityManager, this.reconciliationManager, this.contentEntityObjectDao, this.linkManager);
            this.successfulCommitTaskThreadLocal.set(reconcileContentRegisterTask);
            this.synchronizationManager.registerSynchronization(new ThreadLocalCleanUpSynchronization(this.successfulCommitTaskThreadLocal));
        }
        reconcileContentRegisterTask.registerReconcileContent((OwningContent)new OwningContent(id, includeOwnContentEnum));
    }

    @VisibleForTesting
    ThreadLocal<ReconcileContentRegisterTask<OwningContent>> getSuccessfulCommitTaskThreadLocal() {
        return this.successfulCommitTaskThreadLocal;
    }

    public static void updateCEOWithAttachmentChange(ContentEntityObject ceo, ContentEntityObjectDaoInternal contentEntityObjectDao) {
        String attachmentChangePropertyPrefix = "attachmentChanged-";
        List propertiesToRemove = ceo.getProperties().asList().stream().filter(contentProperty -> contentProperty.getName().contains(attachmentChangePropertyPrefix)).collect(Collectors.toList());
        propertiesToRemove.stream().forEach(removeProperty -> ceo.getProperties().removeProperty(removeProperty.getName()));
        ceo.getProperties().setStringProperty(attachmentChangePropertyPrefix + System.currentTimeMillis(), "" + System.currentTimeMillis());
        contentEntityObjectDao.saveRawWithoutReindex(ceo);
    }
}


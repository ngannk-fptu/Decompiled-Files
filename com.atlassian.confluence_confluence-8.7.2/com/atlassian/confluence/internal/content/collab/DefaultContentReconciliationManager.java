/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.PlatformTransactionManager
 */
package com.atlassian.confluence.internal.content.collab;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.DefaultSaveContext;
import com.atlassian.confluence.core.SaveContext;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.core.SynchronizationManager;
import com.atlassian.confluence.event.events.content.page.synchrony.ContentUpdatedEvent;
import com.atlassian.confluence.event.events.content.page.synchrony.SynchronyRecoveryEvent;
import com.atlassian.confluence.internal.ContentEntityManagerInternal;
import com.atlassian.confluence.internal.content.collab.ContentReconciliationManager;
import com.atlassian.confluence.internal.content.collab.ReconcileContentRegisterTask;
import com.atlassian.confluence.internal.content.collab.ReconcileContentTask;
import com.atlassian.confluence.internal.content.collab.ThreadLocalCleanUpSynchronization;
import com.atlassian.confluence.pages.PageUpdateTrigger;
import com.atlassian.confluence.pages.exceptions.ExternalChangesException;
import com.atlassian.confluence.setup.settings.CollaborativeEditingHelper;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.event.api.EventPublisher;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;

public class DefaultContentReconciliationManager
implements ContentReconciliationManager {
    private static final Logger log = LoggerFactory.getLogger(DefaultContentReconciliationManager.class);
    private final CollaborativeEditingHelper collaborativeEditingHelper;
    private final EventPublisher eventPublisher;
    private final SynchronizationManager synchronizationManager;
    private final PlatformTransactionManager transactionManager;
    private final Supplier<? extends ContentEntityManagerInternal> contentEntityManager;
    private final ThreadLocal<ReconcileContentRegisterTask<ContentUpdatedEvent>> successfulCommitTaskThreadLocal;

    public DefaultContentReconciliationManager(CollaborativeEditingHelper collaborativeEditingHelper, EventPublisher eventPublisher, SynchronizationManager synchronizationManager, PlatformTransactionManager transactionManager, Supplier<? extends ContentEntityManagerInternal> contentEntityManager) {
        this.collaborativeEditingHelper = Objects.requireNonNull(collaborativeEditingHelper);
        this.eventPublisher = eventPublisher;
        this.synchronizationManager = synchronizationManager;
        this.transactionManager = transactionManager;
        this.contentEntityManager = contentEntityManager;
        this.successfulCommitTaskThreadLocal = new ThreadLocal();
    }

    @Override
    public void handleContentUpdateBeforeSave(@NonNull ContentEntityObject content, @Nullable SaveContext saveContext) {
        if (this.shouldSkipCollabReconciliation(content)) {
            return;
        }
        String syncRev = this.ensureNonDummySyncRev(content);
        boolean isReconciled = this.isReconciled(content);
        this.reconcileIfNeeded(content, saveContext);
        if (isReconciled && StringUtils.isNotBlank((CharSequence)syncRev)) {
            content.setSynchronyRevisionSource("synchrony");
        }
    }

    @Override
    public void handleEditorOnlyContentUpdateBeforeSave(@NonNull ContentEntityObject content, @Nullable SaveContext saveContext) {
        this.handleContentUpdateBeforeSave(content, saveContext);
    }

    @Override
    public void handleContentUpdateAfterSave(@NonNull ContentEntityObject content, @Nullable SaveContext saveContext, @NonNull Optional<Date> lastUpdateDate) {
        if (this.shouldSkipCollabReconciliation(content)) {
            return;
        }
        this.synchronizeSharedDraftIfNeeded(content, lastUpdateDate);
        boolean reconciled = this.isReconciled(content);
        if (!reconciled) {
            return;
        }
        PageUpdateTrigger updateTrigger = saveContext != null ? (PageUpdateTrigger)saveContext.getUpdateTrigger() : PageUpdateTrigger.UNKNOWN;
        ReconcileContentTask task = this.successfulCommitTaskThreadLocal.get();
        if (task == null) {
            log.info("No ReconcileContentTask in current Thread we will create one");
            task = new ReconcileContentTask(this.eventPublisher, this.transactionManager);
            this.successfulCommitTaskThreadLocal.set(task);
            this.synchronizationManager.registerSynchronization(new ThreadLocalCleanUpSynchronization(this.successfulCommitTaskThreadLocal));
        }
        task.registerReconcileContent((ContentUpdatedEvent)this.createContentUpdatedEvent(content, updateTrigger));
    }

    @Override
    public void handleEditorOnlyContentUpdateAfterSave(@NonNull ContentEntityObject content, @Nullable SaveContext saveContext, @NonNull Optional<Date> lastUpdateDate) {
        log.debug("Initiating editor content sync with synchrony for content: {}", (Object)content.getId());
        content.setSynchronyRevisionSource("synchrony");
        this.handleContentUpdateAfterSave(content, saveContext, lastUpdateDate);
    }

    @Override
    public void reconcileIfNeeded(@NonNull ContentEntityObject content, @Nullable SaveContext saveContext) {
        if (this.shouldSkipCollabReconciliation(content)) {
            return;
        }
        boolean reconciled = this.isReconciled(content);
        if (!(reconciled || saveContext != null && this.pageUpdateTriggerAllowedInUnreconciledPages((PageUpdateTrigger)saveContext.getUpdateTrigger()))) {
            log.debug("Reconciling a content {}", (Object)content.getContentId());
            try {
                this.eventPublisher.publish((Object)new SynchronyRecoveryEvent(AuthenticatedUserThreadLocal.get(), content.getContentId(), (String)StringUtils.firstNonBlank((CharSequence[])new String[]{content.getSynchronyRevisionSource(), "restored"})));
            }
            catch (Exception e) {
                log.debug("Content recovery error for {}", (Object)content.getId(), (Object)e);
                throw new ExternalChangesException("Unable to save changes to unreconciled page " + content.getContentId());
            }
            if (!this.isReconciled(content)) {
                throw new ExternalChangesException("Unable to save changes to unreconciled page " + content.getContentId());
            }
        }
    }

    @Override
    public boolean isReconciled(@NonNull ContentEntityObject ceo) {
        String syncRevSource = ceo.getSynchronyRevisionSource();
        return StringUtils.isBlank((CharSequence)syncRevSource) || "synchrony".equals(syncRevSource) || "synchrony-ack".equals(syncRevSource);
    }

    @Override
    public void reconcileDraft(@NonNull SpaceContentEntityObject page, @NonNull SpaceContentEntityObject draft) {
        boolean isRestoredDraft;
        boolean bl = isRestoredDraft = StringUtils.isBlank((CharSequence)draft.getSynchronyRevisionSource()) && !draft.isUnpublished();
        if ((!this.isReconciled(page) || !this.isReconciled(draft) || isRestoredDraft) && this.collaborativeEditingHelper.isSharedDraftsFeatureEnabled(page.getSpaceKey()) && this.collaborativeEditingHelper.isUpgraded()) {
            SpaceContentEntityObject unreconciled = this.isReconciled(page) ? draft : page;
            String synchronyRevisionSource = StringUtils.isNotBlank((CharSequence)unreconciled.getSynchronyRevisionSource()) ? unreconciled.getSynchronyRevisionSource() : "restored";
            this.eventPublisher.publish((Object)new SynchronyRecoveryEvent(AuthenticatedUserThreadLocal.get(), unreconciled.getContentId(), synchronyRevisionSource));
            if (!this.isReconciled(draft)) {
                throw new ExternalChangesException("Trying to access an unreconciled draft. Retry in a few moments");
            }
        }
    }

    @Override
    public void markDraftSynchronised(@NonNull SpaceContentEntityObject draft) {
        if (this.collaborativeEditingHelper.isSharedDraftsFeatureEnabled(draft.getSpaceKey())) {
            draft.setSynchronyRevisionSource("synchrony");
        }
    }

    private void synchronizeSharedDraftIfNeeded(@NonNull ContentEntityObject content, @NonNull Optional<Date> lastUpdateDate) {
        Date sharedDraftDate;
        if (!(content instanceof SpaceContentEntityObject)) {
            return;
        }
        SpaceContentEntityObject spaceEntity = (SpaceContentEntityObject)content;
        SpaceContentEntityObject sharedDraft = this.contentEntityManager.get().findDraftFor(spaceEntity);
        if (sharedDraft != null && (sharedDraftDate = sharedDraft.getLastModificationDate()) != null && lastUpdateDate.isPresent() && sharedDraftDate.after(lastUpdateDate.get())) {
            log.debug("Synchronizing shared draft for {}", (Object)content.getId());
            this.contentEntityManager.get().saveContentEntity(sharedDraft, DefaultSaveContext.DRAFT);
        }
    }

    private boolean shouldSkipCollabReconciliation(@NonNull ContentEntityObject content) {
        return !this.collaborativeEditingHelper.isUpgraded() || content instanceof SpaceContentEntityObject && !this.collaborativeEditingHelper.isSharedDraftsFeatureEnabled(((SpaceContentEntityObject)content).getSpaceKey()) || !content.isCurrent() || Objects.isNull(content.getContentId());
    }

    private boolean pageUpdateTriggerAllowedInUnreconciledPages(PageUpdateTrigger trigger) {
        return PageUpdateTrigger.LINK_REFACTORING.equals(trigger);
    }

    private ContentUpdatedEvent createContentUpdatedEvent(@NonNull ContentEntityObject content, @NonNull PageUpdateTrigger updateTrigger) {
        return new ContentUpdatedEvent(AuthenticatedUserThreadLocal.get(), content.getContentId(), content.getContentStatusObject(), content instanceof SpaceContentEntityObject ? ((SpaceContentEntityObject)content).getSpaceKey() : null, content.getSynchronyRevision(), updateTrigger);
    }

    private String ensureNonDummySyncRev(ContentEntityObject ceo) {
        if ("dummy-sync-rev".equals(ceo.getSynchronyRevision())) {
            ceo.getProperties().removeProperty("sync-rev");
        }
        return ceo.getSynchronyRevision();
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.beehive.ClusterLock
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.confluence.api.model.Depth
 *  com.atlassian.confluence.api.model.pagination.LimitedRequestImpl
 *  com.atlassian.confluence.api.service.content.ContentPropertyService
 *  com.atlassian.confluence.api.service.exceptions.PermissionException
 *  com.atlassian.confluence.impl.hibernate.bulk.BulkTransaction
 *  com.atlassian.confluence.impl.hibernate.bulk.HibernateBulkTransaction
 *  com.atlassian.core.util.ProgressMeter
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.fugue.Option
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.user.User
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Iterables
 *  com.google.common.io.Closer
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.hibernate.LockMode
 *  org.hibernate.SessionFactory
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.pages.persistence.dao.bulk.copy;

import com.atlassian.beehive.ClusterLock;
import com.atlassian.beehive.ClusterLockService;
import com.atlassian.confluence.api.model.Depth;
import com.atlassian.confluence.api.model.pagination.LimitedRequestImpl;
import com.atlassian.confluence.api.service.content.ContentPropertyService;
import com.atlassian.confluence.api.service.exceptions.PermissionException;
import com.atlassian.confluence.content.render.xhtml.links.LinksUpdater;
import com.atlassian.confluence.core.ContentPermissionManager;
import com.atlassian.confluence.core.ContentPropertyManager;
import com.atlassian.confluence.core.DefaultSaveContext;
import com.atlassian.confluence.core.SaveContext;
import com.atlassian.confluence.event.events.content.page.PageCopyEvent;
import com.atlassian.confluence.impl.hibernate.bulk.BulkAction;
import com.atlassian.confluence.impl.hibernate.bulk.BulkStatusReport;
import com.atlassian.confluence.impl.hibernate.bulk.BulkStatusReportEnum;
import com.atlassian.confluence.impl.hibernate.bulk.BulkTransaction;
import com.atlassian.confluence.impl.hibernate.bulk.HibernateBulkTransaction;
import com.atlassian.confluence.impl.hibernate.bulk.RecursiveHibernateBulkAction;
import com.atlassian.confluence.internal.pages.PageManagerInternal;
import com.atlassian.confluence.labels.EditableLabelable;
import com.atlassian.confluence.labels.Labelling;
import com.atlassian.confluence.labels.Namespace;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.DuplicateDataRuntimeException;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.SavableAttachment;
import com.atlassian.confluence.pages.persistence.dao.bulk.AbstractBulkPageAction;
import com.atlassian.confluence.pages.persistence.dao.bulk.PageNameConflictResolver;
import com.atlassian.confluence.pages.persistence.dao.bulk.copy.BulkPageCopy;
import com.atlassian.confluence.pages.persistence.dao.bulk.copy.BulkPageCopyExecutionContext;
import com.atlassian.confluence.pages.persistence.dao.bulk.copy.PageCopyOptions;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.LogProgressMeterWrapper;
import com.atlassian.confluence.util.SubProgressMeter;
import com.atlassian.core.util.ProgressMeter;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.fugue.Option;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.user.User;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.io.Closer;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultBulkPageCopy
implements BulkPageCopy {
    private static final String LOCK_PREFIX = BulkAction.LOCK_PREFIX;
    private static final float COPY_PAGE_PERCENTAGE = 0.8f;
    private static final float RELINK_PAGE_PERCENTAGE = 0.2f;
    private static final int PERCENTAGE_COMPLETE = 100;
    private static final int DEFAULT_PAGE_LOAD_BATCH = 50;
    private final Logger log = LoggerFactory.getLogger(DefaultBulkPageCopy.class);
    private final SessionFactory sessionFactory;
    private final ContentPermissionManager contentPermissionManager;
    private final PermissionManager permissionManager;
    private final SpacePermissionManager spacePermissionManager;
    private final ContentPropertyManager contentPropertyManager;
    private final AttachmentManager attachmentManager;
    private final PageManagerInternal pageManager;
    private final LinksUpdater linksUpdater;
    private final ObjectMapper objectMapper;
    private final ClusterLockService lockService;
    private final EventPublisher eventPublisher;
    private final ContentPropertyService contentPropertyService;
    private final BulkStatusReport.Builder statusReportBuilder;
    private final Map<Long, PartialReferenceDetailMapping> newToOldPageMap;

    public DefaultBulkPageCopy(SessionFactory sessionFactory5, ContentPermissionManager contentPermissionManager, PermissionManager permissionManager, ContentPropertyManager contentPropertyManager, AttachmentManager attachmentManager, LinksUpdater linksUpdater, ClusterLockService lockService, PageManagerInternal pageManager, SpacePermissionManager spacePermissionManager, EventPublisher eventPublisher) {
        this.sessionFactory = Objects.requireNonNull(sessionFactory5);
        this.contentPermissionManager = Objects.requireNonNull(contentPermissionManager);
        this.permissionManager = Objects.requireNonNull(permissionManager);
        this.contentPropertyManager = Objects.requireNonNull(contentPropertyManager);
        this.attachmentManager = Objects.requireNonNull(attachmentManager);
        this.linksUpdater = Objects.requireNonNull(linksUpdater);
        this.lockService = Objects.requireNonNull(lockService);
        this.pageManager = Objects.requireNonNull(pageManager);
        this.spacePermissionManager = Objects.requireNonNull(spacePermissionManager);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.newToOldPageMap = new HashMap<Long, PartialReferenceDetailMapping>();
        this.objectMapper = new ObjectMapper();
        this.statusReportBuilder = BulkStatusReport.getBuilder();
        this.contentPropertyService = (ContentPropertyService)ContainerManager.getComponent((String)"contentPropertyService");
    }

    public DefaultBulkPageCopy(SessionFactory sessionFactory5, ContentPermissionManager contentPermissionManager, PermissionManager permissionManager, ContentPropertyManager contentPropertyManager, AttachmentManager attachmentManager, LinksUpdater linksUpdater, ClusterLockService lockService, PageManagerInternal pageManager, SpacePermissionManager spacePermissionManager, EventPublisher eventPublisher, ContentPropertyService contentPropertyService) {
        this.sessionFactory = Objects.requireNonNull(sessionFactory5);
        this.contentPermissionManager = Objects.requireNonNull(contentPermissionManager);
        this.permissionManager = Objects.requireNonNull(permissionManager);
        this.contentPropertyManager = Objects.requireNonNull(contentPropertyManager);
        this.attachmentManager = Objects.requireNonNull(attachmentManager);
        this.linksUpdater = Objects.requireNonNull(linksUpdater);
        this.lockService = Objects.requireNonNull(lockService);
        this.pageManager = Objects.requireNonNull(pageManager);
        this.spacePermissionManager = Objects.requireNonNull(spacePermissionManager);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.newToOldPageMap = new HashMap<Long, PartialReferenceDetailMapping>();
        this.objectMapper = new ObjectMapper();
        this.statusReportBuilder = BulkStatusReport.getBuilder();
        this.contentPropertyService = contentPropertyService;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void deepCopy(PageCopyOptions pageCopyOptions, Page originalPage, Page destinationPage) {
        Preconditions.checkNotNull((Object)pageCopyOptions, (Object)"PageCopyOptions should not be null");
        Preconditions.checkNotNull((Object)originalPage, (Object)"Source page should not be null");
        Preconditions.checkNotNull((Object)destinationPage, (Object)"Destination page should not be null");
        ConfluenceUser user = pageCopyOptions.getUser();
        ConfluenceUser lastLoggedInUser = AuthenticatedUserThreadLocal.get();
        try {
            AuthenticatedUserThreadLocal.set(user);
            PageCopyOptions pageCopyOptionsInternal = this.validatePermissions(pageCopyOptions, originalPage, destinationPage, user);
            this.deepCopyInternal(pageCopyOptionsInternal, originalPage, destinationPage);
        }
        finally {
            AuthenticatedUserThreadLocal.set(lastLoggedInUser);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void deepCopyInternal(PageCopyOptions pageCopyOptions, Page originalPage, Page destinationPage) {
        int processedEntities;
        LogProgressMeterWrapper logProgressMeter;
        block8: {
            ClusterLock lock;
            block6: {
                block7: {
                    ProgressMeter progressMeter = pageCopyOptions.getProgressMeter();
                    logProgressMeter = new LogProgressMeterWrapper(progressMeter);
                    String destinationSpaceKey = destinationPage.getSpaceKey();
                    lock = this.lockService.getLockForName(LOCK_PREFIX + "." + destinationSpaceKey);
                    processedEntities = 0;
                    if (lock != null && lock.tryLock()) break block6;
                    this.log.error("Failed to acquire lock for copy page hierarchy: [{}]", (Object)lock);
                    this.statusReportBuilder.addErrorMessage(BulkStatusReportEnum.ERROR_LOCK_FAILED.name(), destinationSpaceKey);
                    logProgressMeter.setCompletedSuccessfully(false);
                    lock = null;
                    if (lock == null) break block7;
                    lock.unlock();
                }
                logProgressMeter.setStatus(this.getStatusString(this.statusReportBuilder.withMessageKey(BulkStatusReportEnum.STATUS_COPIED_PAGES.name(), String.valueOf(processedEntities)).build()));
                return;
            }
            try {
                int totalPageNeedToCopy = this.pageManager.countPagesInSubtree(originalPage);
                this.statusReportBuilder.withTotalPageNeedToCopy(totalPageNeedToCopy);
                SubProgressMeter copyPageProgressMeter = new SubProgressMeter(logProgressMeter, 0.8f, totalPageNeedToCopy);
                HibernateBulkTransaction bulkTransaction = new HibernateBulkTransaction(this.sessionFactory);
                BulkPageCopyExecutionContext context = new BulkPageCopyExecutionContext(copyPageProgressMeter, destinationPage);
                RecursiveHibernateBulkAction<BulkPageCopyExecutionContext, Page> bulkAction = new RecursiveHibernateBulkAction<BulkPageCopyExecutionContext, Page>((BulkTransaction)bulkTransaction, copyPageProgressMeter, pageCopyOptions.getBatchSize(), pageCopyOptions.getMaxProcessedEntries());
                logProgressMeter.setStatus(this.getStatusString(BulkStatusReport.getBuilder().withMessageKey(BulkStatusReportEnum.STATUS_COPYING_PAGES.name(), new String[0]).build()));
                processedEntities = bulkAction.execute(context, originalPage, new BulkPageCopyAction(pageCopyOptions));
                copyPageProgressMeter.setPercentage(100);
                this.updatePageReference(logProgressMeter, pageCopyOptions);
                logProgressMeter.setCompletedSuccessfully(true);
                if (lock == null) break block8;
            }
            catch (Exception exception) {
                block9: {
                    try {
                        this.log.error(exception.getMessage(), (Throwable)exception);
                        String errorMsg = this.getStatusString(BulkStatusReport.getBuilder().withMessageKey(BulkStatusReportEnum.ERROR_UNKNOWN.name(), new String[0]).build());
                        logProgressMeter.setStatus(errorMsg);
                        logProgressMeter.setCompletedSuccessfully(false);
                        if (lock == null) break block9;
                    }
                    catch (Throwable throwable) {
                        if (lock != null) {
                            lock.unlock();
                        }
                        logProgressMeter.setStatus(this.getStatusString(this.statusReportBuilder.withMessageKey(BulkStatusReportEnum.STATUS_COPIED_PAGES.name(), String.valueOf(processedEntities)).build()));
                        throw throwable;
                    }
                    lock.unlock();
                }
                logProgressMeter.setStatus(this.getStatusString(this.statusReportBuilder.withMessageKey(BulkStatusReportEnum.STATUS_COPIED_PAGES.name(), String.valueOf(processedEntities)).build()));
            }
            lock.unlock();
        }
        logProgressMeter.setStatus(this.getStatusString(this.statusReportBuilder.withMessageKey(BulkStatusReportEnum.STATUS_COPIED_PAGES.name(), String.valueOf(processedEntities)).build()));
    }

    private void publishCopyEvent(Page origin, Page destination, PageCopyOptions pageCopyOptions) {
        PageCopyEvent event = new PageCopyEvent((Object)this, origin, destination, true, pageCopyOptions);
        this.eventPublisher.publish((Object)event);
    }

    private PageCopyOptions validatePermissions(PageCopyOptions pageCopyOptions, Page originalPage, Page destinationPage, ConfluenceUser copier) {
        Space destinationSpace = destinationPage.getSpace();
        if (!this.permissionManager.hasPermission((User)copier, Permission.VIEW, destinationPage) || !this.permissionManager.hasCreatePermission((User)copier, (Object)destinationSpace, destinationPage)) {
            throw new PermissionException(String.format("Could not execute deep page copy because the user %s doesn't have EDIT permission on destination page", copier == null ? "Anonymous" : copier.getName()));
        }
        if (!this.permissionManager.hasPermission((User)copier, Permission.VIEW, originalPage)) {
            throw new PermissionException(String.format("Could not execute deep page copy because the user %s doesn't have VIEW permission on original page", copier == null ? "Anonymous" : copier.getName()));
        }
        PageCopyOptions.Builder pageCopyOptionBuilder = PageCopyOptions.builder();
        pageCopyOptionBuilder.withPageCopyOptions(pageCopyOptions);
        if (pageCopyOptions.shouldCopyPermissions() && !this.spacePermissionManager.hasPermission("SETPAGEPERMISSIONS", destinationPage.getSpace(), copier)) {
            this.statusReportBuilder.addWarnMessage(BulkStatusReportEnum.WARN_IGNORE_COPY_PERMISSION.name(), new String[0]);
            pageCopyOptionBuilder.withCopyPermission(false);
        }
        if (pageCopyOptions.shouldCopyAttachments() && !this.spacePermissionManager.hasPermission("CREATEATTACHMENT", destinationPage.getSpace(), copier)) {
            this.statusReportBuilder.addWarnMessage(BulkStatusReportEnum.WARN_IGNORE_COPY_ATTACHMENT.name(), new String[0]);
            pageCopyOptionBuilder.withCopyAttachment(false);
        }
        return pageCopyOptionBuilder.build();
    }

    private void updatePageReference(ProgressMeter logProgressMeter, PageCopyOptions pageCopyOptions) {
        if (pageCopyOptions.shouldSkipLinkUpdates()) {
            this.log.debug("Skip page reference update.");
            return;
        }
        logProgressMeter.setStatus(this.getStatusString(BulkStatusReport.getBuilder().withMessageKey(BulkStatusReportEnum.STATUS_RELINK_PAGES.name(), new String[0]).build()));
        SubProgressMeter relinkPageProgressMeter = new SubProgressMeter(logProgressMeter, 0.2f, pageCopyOptions.getBatchSize());
        try {
            Set<Long> newIds = this.newToOldPageMap.keySet();
            Map<LinksUpdater.PartialReferenceDetails, LinksUpdater.PartialReferenceDetails> referenceMap = this.getReferenceMap(newIds);
            Iterable partitionNewIds = Iterables.partition(newIds, (int)50);
            int totalNumberOfBatch = Iterables.size((Iterable)partitionNewIds);
            relinkPageProgressMeter.setTotalObjects(totalNumberOfBatch);
            int counter = 0;
            for (List batchIds : partitionNewIds) {
                new BulkTransactionTemplate().executeInTransaction(() -> {
                    List<Page> newPages = this.pageManager.getPages(batchIds);
                    for (Page newPage : newPages) {
                        String newBody = this.linksUpdater.updateReferencesInContent(newPage.getBodyAsString(), referenceMap);
                        newPage.setBodyAsString(newBody);
                        this.updateOutgoingLinks(newPage);
                    }
                });
                relinkPageProgressMeter.setPercentage(++counter, totalNumberOfBatch);
            }
            relinkPageProgressMeter.setCompletedSuccessfully(true);
        }
        catch (Exception exception) {
            this.log.error(exception.getMessage(), (Throwable)exception);
            String errorMsg = this.getStatusString(BulkStatusReport.getBuilder().withMessageKey(BulkStatusReportEnum.ERROR_RELINK.name(), new String[0]).build());
            this.statusReportBuilder.addErrorMessage(BulkStatusReportEnum.ERROR_RELINK.name(), new String[0]);
            relinkPageProgressMeter.setStatus(errorMsg);
            relinkPageProgressMeter.setCompletedSuccessfully(false);
        }
        relinkPageProgressMeter.setPercentage(100);
    }

    private void updateOutgoingLinks(Page newPage) {
        this.eventPublisher.publish(linkManager -> linkManager.updateOutgoingLinks(newPage));
    }

    private Map<LinksUpdater.PartialReferenceDetails, LinksUpdater.PartialReferenceDetails> getReferenceMap(Collection<Long> newIds) {
        HashMap<LinksUpdater.PartialReferenceDetails, LinksUpdater.PartialReferenceDetails> referenceMap = new HashMap<LinksUpdater.PartialReferenceDetails, LinksUpdater.PartialReferenceDetails>(this.newToOldPageMap.size());
        for (long newPageId : newIds) {
            PartialReferenceDetailMapping partialReferenceDetailMapping = this.newToOldPageMap.get(newPageId);
            referenceMap.put(partialReferenceDetailMapping.getOldReference(), partialReferenceDetailMapping.getNewReference());
        }
        return referenceMap;
    }

    private void safeAction(Runnable action) {
        try {
            action.run();
        }
        catch (Exception exception) {
            this.log.error("An exception occurred while trying to do a bulk page copy operation", (Throwable)exception);
        }
    }

    private Option<Page> copyPage(PageCopyOptions pageCopyOptions, Page originalPage, Page newParentPage) {
        if (this.log.isDebugEnabled()) {
            this.log.debug("Copying page [{}] to new parent page [{}]", (Object)originalPage.toString(), (Object)newParentPage.toString());
        }
        ConfluenceUser copier = pageCopyOptions.getUser();
        SaveContext saveContext = DefaultSaveContext.BULK_OPERATION;
        PageNameConflictResolver pageNameConflictResolver = pageCopyOptions.getPageNameConflictResolver();
        Page copy = originalPage.copyLatestVersion();
        String oldPageTitle = originalPage.getTitle();
        String newPageTitle = pageNameConflictResolver.couldProvideNewName() ? pageNameConflictResolver.resolveConflict(0, oldPageTitle) : oldPageTitle;
        copy.setCreationDate(null);
        copy.setCreator(copier);
        copy.setSpace(newParentPage.getSpace());
        copy.setParentPage(newParentPage);
        copy.setTitle(newPageTitle);
        copy.setBodyAsString(pageCopyOptions.getPageContentTransformer().transform(this.linksUpdater.expandRelativeReferencesInContent(originalPage), originalPage, copy));
        newParentPage.addChild(copy);
        if (!this.savePage(pageCopyOptions, copy)) {
            this.log.error("Could not copy page [{}] to page [{}]", (Object)originalPage.toString(), (Object)copy.toString());
            this.statusReportBuilder.addErrorMessage(BulkStatusReportEnum.ERROR_COPY_PAGE.name(), originalPage.getTitle(), "" + originalPage.getId(), originalPage.getUrlPath());
            return Option.none();
        }
        if (pageCopyOptions.shouldCopyLabels()) {
            originalPage.getLabellings().stream().filter(labelling -> !Namespace.PERSONAL.equals(labelling.getLabel().getNamespace())).forEach(labelling -> copy.addLabelling(new Labelling(labelling.getLabel(), (EditableLabelable)copy, pageCopyOptions.getUser())));
        }
        if (pageCopyOptions.shouldCopyPermissions()) {
            this.safeAction(() -> {
                this.log.debug("Copying page permissions from page [{}] to page [{}]", (Object)originalPage, (Object)copy);
                this.contentPermissionManager.copyContentPermissions(originalPage, copy);
            });
        }
        if (pageCopyOptions.shouldCopyAttachments()) {
            this.safeAction(() -> {
                this.log.debug("Setting page watching status based on user preference from page [{}] to page [{}]", (Object)originalPage, (Object)copy);
                try {
                    this.copyAttachments(originalPage, copy, saveContext, pageCopyOptions);
                }
                catch (IOException exception) {
                    throw new RuntimeException(exception);
                }
            });
        }
        if (pageCopyOptions.shouldCopyContentProperties()) {
            this.safeAction(() -> {
                this.log.debug("Copying page properties from page [{}] to page [{}]", (Object)originalPage, (Object)copy);
                this.contentPropertyManager.transferProperties(originalPage, copy);
                this.contentPropertyService.copyAllJsonContentProperties(originalPage.getSelector(), copy.getSelector());
            });
        }
        this.safeAction(() -> {
            this.log.debug("Copying page jsonproperties from page [{}] to page [{}]", (Object)originalPage, (Object)copy);
            this.contentPropertyService.copyAllJsonContentProperties(originalPage.getSelector(), copy.getSelector());
        });
        if (this.newToOldPageMap.size() == 0) {
            this.statusReportBuilder.withDestinationUrl(copy.getUrlPath());
        }
        this.newToOldPageMap.put(copy.getId(), new PartialReferenceDetailMapping(LinksUpdater.PartialReferenceDetails.createReference(originalPage), LinksUpdater.PartialReferenceDetails.createReference(copy)));
        this.publishCopyEvent(originalPage, copy, pageCopyOptions);
        return Option.option((Object)copy);
    }

    private void copyAttachments(Page originalPage, Page copy, SaveContext saveContext, PageCopyOptions pageCopyOptions) throws IOException {
        List<Attachment> latestVersionAttachments = this.attachmentManager.getLatestVersionsOfAttachments(originalPage);
        if (latestVersionAttachments == null) {
            return;
        }
        HashMap<LinksUpdater.AttachmentReferenceDetails, LinksUpdater.AttachmentReferenceDetails> oldToNewMap = new HashMap<LinksUpdater.AttachmentReferenceDetails, LinksUpdater.AttachmentReferenceDetails>(latestVersionAttachments.size());
        ArrayList<SavableAttachment> copies = new ArrayList<SavableAttachment>(latestVersionAttachments.size());
        try (Closer closer = Closer.create();){
            for (Attachment attachment : latestVersionAttachments) {
                Attachment attachmentCopy = attachment.copyLatestVersion();
                attachmentCopy.setVersion(1);
                copy.addAttachment(attachmentCopy);
                InputStream data = this.attachmentManager.getAttachmentData(attachment);
                closer.register((Closeable)data);
                if (data == null) {
                    this.log.error("No data found for {}", (Object)attachment);
                    continue;
                }
                copies.add(new SavableAttachment(attachmentCopy, null, data));
                oldToNewMap.put(LinksUpdater.AttachmentReferenceDetails.createReference(attachment), LinksUpdater.AttachmentReferenceDetails.createReference(attachmentCopy));
            }
            this.attachmentManager.saveAttachments(copies, saveContext);
            if (pageCopyOptions.shouldSkipLinkUpdates()) {
                this.log.debug("Skipping attachment reference update.");
            } else {
                String updatedBody = this.linksUpdater.updateAttachmentReferencesInContent(copy.getBodyAsString(), oldToNewMap);
                copy.setBodyAsString(updatedBody);
            }
        }
    }

    private boolean savePage(PageCopyOptions pageCopyOptions, Page copy) {
        PageNameConflictResolver nameConflictResolver = pageCopyOptions.getPageNameConflictResolver();
        String oldPageTitle = copy.getTitle();
        DuplicateDataRuntimeException lastException = null;
        for (int i = 0; i < nameConflictResolver.getMaxRetryNumber(); ++i) {
            try {
                this.pageManager.saveContentEntity(copy, DefaultSaveContext.BULK_OPERATION);
                this.pageManager.updatePageInAncestorCollections(copy, copy.getParent());
                if (lastException != null) {
                    this.statusReportBuilder.addWarnMessage(BulkStatusReportEnum.WARN_RENAME_PAGE.name(), oldPageTitle, copy.getTitle());
                }
                lastException = null;
                break;
            }
            catch (DuplicateDataRuntimeException exception) {
                lastException = exception;
                copy.setTitle(pageCopyOptions.getPageNameConflictResolver().resolveConflict(i + 1, copy.getTitle()));
                continue;
            }
        }
        return lastException == null;
    }

    private String getStatusString(BulkStatusReport bulkStatusReport) {
        String message = "";
        try {
            message = this.objectMapper.writeValueAsString((Object)bulkStatusReport);
        }
        catch (IOException ex) {
            this.log.error("Could not report status for bulk copy action due could not serialize JSON status", (Throwable)ex);
        }
        return message;
    }

    private static class PartialReferenceDetailMapping {
        private LinksUpdater.PartialReferenceDetails oldReference;
        private LinksUpdater.PartialReferenceDetails newReference;

        public PartialReferenceDetailMapping(LinksUpdater.PartialReferenceDetails oldReference, LinksUpdater.PartialReferenceDetails newReference) {
            this.oldReference = oldReference;
            this.newReference = newReference;
        }

        public LinksUpdater.PartialReferenceDetails getOldReference() {
            return this.oldReference;
        }

        public LinksUpdater.PartialReferenceDetails getNewReference() {
            return this.newReference;
        }
    }

    @VisibleForTesting
    class BulkPageCopyAction
    extends AbstractBulkPageAction<PageCopyOptions, BulkPageCopyExecutionContext> {
        public BulkPageCopyAction(PageCopyOptions options) {
            super(options);
        }

        @Override
        public BulkAction.Result<BulkPageCopyExecutionContext, Page> process(BulkPageCopyExecutionContext context, Page target) {
            DefaultBulkPageCopy.this.log.info("Bulk copy pages executing...");
            BulkPageCopyExecutionContext innerContext = context;
            DefaultBulkPageCopy.this.sessionFactory.getCurrentSession().refresh((Object)target, LockMode.NONE);
            Page newParentPage = DefaultBulkPageCopy.this.pageManager.getPage(innerContext.getParentPageId());
            Option<Page> newPageOption = DefaultBulkPageCopy.this.copyPage((PageCopyOptions)this.options, target, newParentPage);
            Page newPage = (Page)newPageOption.getOrNull();
            if (newPage == null) {
                DefaultBulkPageCopy.this.log.error("Failed to copy page {}. Stopping bulk copy pages action.", (Object)target);
                return new BulkAction.Result<BulkPageCopyExecutionContext, Page>(innerContext, Collections.emptyList(), false);
            }
            List childrenPages = DefaultBulkPageCopy.this.pageManager.getChildren(target, LimitedRequestImpl.create((int)0x7FFFFFFE), Depth.ROOT).getResults();
            if (childrenPages != null && !(childrenPages = childrenPages.stream().filter(page -> !DefaultBulkPageCopy.this.newToOldPageMap.containsKey(page.getId())).collect(Collectors.toList())).isEmpty()) {
                innerContext = new BulkPageCopyExecutionContext(context, newPage);
            }
            return new BulkAction.Result<BulkPageCopyExecutionContext, Page>(innerContext, childrenPages, true);
        }

        @Override
        public void report(ProgressMeter innerProgressMeter, int processedEntities, int actionedEntities, int maxProcessedDepthLevel) {
            ProgressMeter progressMeter = ((PageCopyOptions)this.options).getProgressMeter();
            progressMeter.setStatus(DefaultBulkPageCopy.this.getStatusString(BulkStatusReport.getBuilder().withMessageKey(BulkStatusReportEnum.STATUS_COPIED_PAGES.name(), String.valueOf(processedEntities), String.valueOf(innerProgressMeter.getTotal())).build()));
        }
    }

    private class BulkTransactionTemplate {
        private BulkTransactionTemplate() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void executeInTransaction(Runnable action) {
            if (action == null) {
                return;
            }
            HibernateBulkTransaction bulkTransaction = new HibernateBulkTransaction(DefaultBulkPageCopy.this.sessionFactory);
            try {
                bulkTransaction.beginTransaction(new Object[0]);
                action.run();
            }
            catch (Exception exception) {
                DefaultBulkPageCopy.this.log.error(exception.getMessage(), (Throwable)exception);
                bulkTransaction.rollbackTransaciton();
            }
            finally {
                bulkTransaction.commitTransaciton();
            }
        }
    }
}


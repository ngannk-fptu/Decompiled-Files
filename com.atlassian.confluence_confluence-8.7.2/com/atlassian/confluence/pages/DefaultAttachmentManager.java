/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.relations.CumulativeContributorRelationDescriptor
 *  com.atlassian.confluence.api.model.relations.RelationDescriptor
 *  com.atlassian.confluence.api.service.retention.RetentionFeatureChecker
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.user.User
 *  org.apache.commons.io.input.AutoCloseInputStream
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.hibernate.SessionFactory
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.util.Assert
 */
package com.atlassian.confluence.pages;

import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.relations.CumulativeContributorRelationDescriptor;
import com.atlassian.confluence.api.model.relations.RelationDescriptor;
import com.atlassian.confluence.api.service.retention.RetentionFeatureChecker;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.DefaultSaveContext;
import com.atlassian.confluence.core.MaybeNot;
import com.atlassian.confluence.core.SaveContext;
import com.atlassian.confluence.core.persistence.ContentEntityObjectDao;
import com.atlassian.confluence.event.events.content.attachment.AttachmentBatchUploadCompletedEvent;
import com.atlassian.confluence.event.events.content.attachment.AttachmentCreateEvent;
import com.atlassian.confluence.event.events.content.attachment.AttachmentRemoveEvent;
import com.atlassian.confluence.event.events.content.attachment.AttachmentTrashedEvent;
import com.atlassian.confluence.event.events.content.attachment.AttachmentUpdateEvent;
import com.atlassian.confluence.event.events.content.attachment.AttachmentVersionRemoveEvent;
import com.atlassian.confluence.event.events.content.attachment.GeneralAttachmentCreateEvent;
import com.atlassian.confluence.event.events.content.attachment.GeneralAttachmentRestoreEvent;
import com.atlassian.confluence.event.events.content.attachment.GeneralAttachmentUpdateEvent;
import com.atlassian.confluence.event.events.content.attachment.HiddenAttachmentBatchUploadCompletedEvent;
import com.atlassian.confluence.event.events.content.attachment.HiddenAttachmentCreateEvent;
import com.atlassian.confluence.event.events.content.attachment.HiddenAttachmentRemoveEvent;
import com.atlassian.confluence.event.events.content.attachment.HiddenAttachmentRestoreEvent;
import com.atlassian.confluence.event.events.content.attachment.HiddenAttachmentUpdateEvent;
import com.atlassian.confluence.event.events.content.attachment.HiddenAttachmentVersionRemoveEvent;
import com.atlassian.confluence.event.events.internal.attachment.AttachmentCreatedAuditingEvent;
import com.atlassian.confluence.impl.event.AttachmentRemovedEvent;
import com.atlassian.confluence.impl.search.IndexerEventPublisher;
import com.atlassian.confluence.internal.content.collab.AttachmentRelatedContentReconciliationListener;
import com.atlassian.confluence.internal.content.collab.ContentReconciliationManager;
import com.atlassian.confluence.internal.pages.AttachmentManagerInternal;
import com.atlassian.confluence.internal.pages.persistence.AttachmentDaoInternal;
import com.atlassian.confluence.internal.persistence.ContentEntityObjectDaoInternal;
import com.atlassian.confluence.internal.relations.RelationManager;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentDataExistsException;
import com.atlassian.confluence.pages.AttachmentDataNotFoundException;
import com.atlassian.confluence.pages.AttachmentDataStorageType;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.AttachmentStatisticsDTO;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.DelegatingAttachmentManager;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.SavableAttachment;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDao;
import com.atlassian.confluence.pages.persistence.dao.FlushableCachingDao;
import com.atlassian.confluence.pages.persistence.dao.GeneralAttachmentCopier;
import com.atlassian.confluence.pages.persistence.dao.GeneralAttachmentMigrator;
import com.atlassian.confluence.pages.persistence.dao.bulk.attachment.delete.AttachmentDeleteOptions;
import com.atlassian.confluence.pages.persistence.dao.bulk.attachment.delete.DefaultBulkAttachmentDelete;
import com.atlassian.confluence.search.ChangeIndexer;
import com.atlassian.confluence.search.ConfluenceIndexer;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.UserPreferencesAccessor;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.web.rangerequest.RangeRequest;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.user.User;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.io.input.AutoCloseInputStream;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

public class DefaultAttachmentManager
implements AttachmentManagerInternal {
    private static final Logger log = LoggerFactory.getLogger(DefaultAttachmentManager.class);
    private static final Pattern ATTACHMENT_DOWNLOAD_PATH_PATTERN = Pattern.compile(Attachment.DOWNLOAD_PATH_BASE + "([0-9]*)/(.*)[\\?$]");
    private EventPublisher eventPublisher;
    private AttachmentDaoInternal attachmentDao;
    private ContentEntityObjectDaoInternal<?> contentEntityObjectDao;
    private RelationManager relationManager;
    private RetentionFeatureChecker retentionFeatureChecker;
    private UserPreferencesAccessor userPreferencesAccessor;
    private NotificationManager notificationManager;
    private final ContentReconciliationManager reconciliationManager;

    public DefaultAttachmentManager(AttachmentDaoInternal attachmentDao, ContentEntityObjectDaoInternal<?> contentEntityObjectDao, EventPublisher eventPublisher, UserPreferencesAccessor userPreferencesAccessor, NotificationManager notificationManager, RelationManager relationManager, RetentionFeatureChecker retentionFeatureChecker, ContentReconciliationManager reconciliationManager) {
        this.eventPublisher = eventPublisher;
        this.attachmentDao = attachmentDao;
        this.contentEntityObjectDao = contentEntityObjectDao;
        this.userPreferencesAccessor = userPreferencesAccessor;
        this.notificationManager = notificationManager;
        this.relationManager = relationManager;
        this.retentionFeatureChecker = retentionFeatureChecker;
        this.reconciliationManager = reconciliationManager;
    }

    @Deprecated
    public DefaultAttachmentManager() {
        this.reconciliationManager = (ContentReconciliationManager)ContainerManager.getComponent((String)"contentReconciliationManager");
    }

    @Override
    public Attachment getAttachment(ContentEntityObject content, String attachmentFileName) {
        return this.getAttachment(content, attachmentFileName, 0);
    }

    @Override
    public String getAttachmentDownloadPath(ContentEntityObject content, String attachmentFileName) {
        Attachment attachment = this.getAttachment(content, attachmentFileName);
        if (attachment != null) {
            return attachment.getDownloadPath();
        }
        return null;
    }

    @Override
    public Attachment getAttachment(ContentEntityObject content, String attachmentFileName, int version) {
        ContentEntityObject holder = null;
        while (holder == null && content != null) {
            if (content instanceof Comment) {
                content = ((Comment)content).getContainer();
                continue;
            }
            holder = content;
        }
        if (version <= 0) {
            return this.attachmentDao.getLatestAttachment(holder, attachmentFileName);
        }
        return this.attachmentDao.getAttachment(holder, attachmentFileName, version);
    }

    @Override
    public void saveAttachment(Attachment attachment, Attachment previousVersion, InputStream attachmentData) throws IOException {
        this.saveAttachment(attachment, previousVersion, attachmentData, DefaultSaveContext.DEFAULT);
    }

    @Override
    public void saveAttachment(Attachment attachment, Attachment previousVersion, InputStream attachmentData, SaveContext saveContext) throws IOException {
        this.saveAttachments(Collections.singletonList(new SavableAttachment(attachment, previousVersion, attachmentData)), saveContext);
    }

    @Override
    public void saveAttachments(List<SavableAttachment> savableAttachments) throws IOException {
        this.saveAttachments(savableAttachments, DefaultSaveContext.DEFAULT);
    }

    @Override
    public void saveAttachments(List<SavableAttachment> savableAttachments, SaveContext saveContext) throws IOException {
        ArrayList<Attachment> visibleAttachments = new ArrayList<Attachment>();
        ArrayList<Attachment> hiddenAttachments = new ArrayList<Attachment>();
        Map<ContentEntityObject, Optional<Date>> containers = this.extractBackingContainers(savableAttachments);
        containers.keySet().forEach(ceo -> {
            AttachmentRelatedContentReconciliationListener.updateCEOWithAttachmentChange(ceo, this.contentEntityObjectDao);
            this.reconciliationManager.handleEditorOnlyContentUpdateBeforeSave((ContentEntityObject)ceo, saveContext);
        });
        for (SavableAttachment savableAttachment : savableAttachments) {
            Attachment attachment = savableAttachment.getAttachment();
            Attachment previousVersion = savableAttachment.getPreviousVersion();
            InputStream attachmentData = savableAttachment.getAttachmentData();
            Assert.notNull((Object)attachment, (String)"Attachment");
            ContentEntityObject content = attachment.getContainer();
            Assert.notNull((Object)content, (String)"Attachment content");
            Assert.notNull((Object)attachment.getMediaType(), (String)"Attachment content type");
            if (!content.isLatestVersion()) {
                attachment.setContainer((ContentEntityObject)content.getLatestVersion());
            }
            if (previousVersion == null) {
                this.saveNewAttachment(attachment, attachmentData, saveContext);
            } else {
                this.saveNewAttachmentVersion(attachment, previousVersion, attachmentData, saveContext);
            }
            if (!attachment.isHidden()) {
                visibleAttachments.add(attachment);
                continue;
            }
            hiddenAttachments.add(attachment);
        }
        containers.entrySet().forEach(entry -> this.reconciliationManager.handleEditorOnlyContentUpdateAfterSave((ContentEntityObject)entry.getKey(), saveContext, (Optional)entry.getValue()));
        if (!visibleAttachments.isEmpty()) {
            this.eventPublisher.publish((Object)new AttachmentBatchUploadCompletedEvent((Object)this, visibleAttachments, saveContext.isSuppressNotifications()));
        }
        if (!hiddenAttachments.isEmpty()) {
            this.eventPublisher.publish((Object)new HiddenAttachmentBatchUploadCompletedEvent((Object)this, hiddenAttachments));
        }
    }

    protected final void saveNewAttachment(Attachment attachment, InputStream attachmentData, SaveContext saveContext) {
        this.attachmentDao.saveNewAttachment(attachment, attachmentData);
        this.eventPublisher.publish((Object)new AttachmentCreatedAuditingEvent(attachment, saveContext));
        if (saveContext.isEventSuppressed()) {
            return;
        }
        this.autowatchContainerIfNeeded(AuthenticatedUserThreadLocal.get(), attachment, saveContext);
        GeneralAttachmentCreateEvent attachmentCreateEvent = attachment.isHidden() ? new HiddenAttachmentCreateEvent((Object)this, attachment) : new AttachmentCreateEvent((Object)this, attachment, saveContext.isSuppressNotifications());
        this.eventPublisher.publish((Object)attachmentCreateEvent);
    }

    protected final void saveNewAttachmentVersion(Attachment attachment, Attachment previousVersion, InputStream attachmentData, SaveContext saveContext) throws IOException {
        this.attachmentDao.saveNewAttachmentVersion(attachment, previousVersion, attachmentData);
        this.eventPublisher.publish((Object)new AttachmentCreatedAuditingEvent(attachment, saveContext));
        if (saveContext.isEventSuppressed()) {
            return;
        }
        this.autowatchContainerIfNeeded(AuthenticatedUserThreadLocal.get(), attachment, saveContext);
        GeneralAttachmentUpdateEvent attachmentUpdateEvent = attachment.isHidden() ? new HiddenAttachmentUpdateEvent((Object)this, attachment, previousVersion) : new AttachmentUpdateEvent(this, attachment, previousVersion, saveContext.isSuppressNotifications());
        this.eventPublisher.publish((Object)attachmentUpdateEvent);
    }

    @Override
    public List<Attachment> getPreviousVersions(Attachment attachment) {
        List<Attachment> allVersions = this.getAllVersions(attachment);
        allVersions.remove(0);
        return allVersions;
    }

    @Override
    public List<Attachment> getLastAddedVersionsOf(Attachment attachment) {
        return this.attachmentDao.getLastAddedVersionsOf(attachment);
    }

    @Override
    public InputStream getAttachmentData(Attachment attachment) {
        return this.getAttachmentData(attachment, Optional.empty());
    }

    @Override
    public InputStream getAttachmentData(Attachment attachment, Optional<RangeRequest> range) {
        try {
            return new AutoCloseInputStream(this.attachmentDao.getAttachmentData(attachment, range));
        }
        catch (AttachmentDataNotFoundException e) {
            String cause = e.getMessage();
            if (e.getCause() != null) {
                cause = e.getCause().toString();
            }
            log.warn("Could not find data for attachment: {} - {}", (Object)attachment, (Object)cause);
            return null;
        }
    }

    @Override
    public List<Attachment> getLatestVersionsOfAttachments(ContentEntityObject content) {
        return this.attachmentDao.getLatestVersionsOfAttachments(content);
    }

    @Override
    public List<Attachment> getLatestVersionsOfAttachmentsForMultipleCeos(Iterable<? extends ContentEntityObject> contentEntityObjects) {
        return this.attachmentDao.getLatestVersionsOfAttachmentsForMultipleCeos(contentEntityObjects);
    }

    @Override
    public List<Attachment> getLatestVersionsOfAttachmentsWithAnyStatusForContainers(Iterable<? extends ContentEntityObject> contentEntityObjects) {
        return this.attachmentDao.getLatestVersionsOfAttachmentsWithAnyStatusForContainers(contentEntityObjects);
    }

    @Override
    public List<Attachment> getLatestVersionsOfAttachmentsWithAnyStatus(ContentEntityObject content) {
        return this.attachmentDao.getLatestVersionsOfAttachmentsWithAnyStatus(content);
    }

    @Override
    public PageResponse<Attachment> getFilteredAttachments(ContentEntityObject content, LimitedRequest pageRequest, Predicate<? super Attachment> filterPredicate) {
        return this.attachmentDao.getFilteredLatestVersionsOfAttachments(content, pageRequest, filterPredicate);
    }

    @Override
    public int countLatestVersionsOfAttachments(ContentEntityObject content) {
        return this.attachmentDao.countLatestVersionsOfAttachments(content);
    }

    @Override
    public int countLatestVersionsOfAttachmentsWithAnyStatus(ContentEntityObject content) {
        return this.attachmentDao.countLatestVersionsOfAttachmentsWithAnyStatus(content);
    }

    @Override
    public int countLatestVersionsOfAttachmentsOnPageSince(ContentEntityObject content, Date since) {
        return this.attachmentDao.countLatestVersionsOfAttachmentsOnPageSince(content, since);
    }

    @Override
    public Optional<AttachmentStatisticsDTO> getAttachmentStatistics() {
        return this.attachmentDao.getAttachmentStatistics();
    }

    @Override
    public void deepAttachmentDelete(AttachmentDeleteOptions attachmentDeleteOptions) {
        DefaultBulkAttachmentDelete defaultBulkAttachmentDeleteAction = new DefaultBulkAttachmentDelete((PageManager)ContainerManager.getComponent((String)"pageManager", PageManager.class), this, (SessionFactory)ContainerManager.getComponent((String)"sessionFactory", SessionFactory.class), (PermissionManager)ContainerManager.getComponent((String)"permissionManager", PermissionManager.class));
        defaultBulkAttachmentDeleteAction.deepDelete(attachmentDeleteOptions);
    }

    @Override
    public Attachment getAttachment(long id) {
        return this.attachmentDao.getById(id);
    }

    @Override
    public List<Attachment> getAttachments(List<Long> ids) {
        return this.attachmentDao.getByIds(ids);
    }

    @Override
    public List<Attachment> getAllVersionsOfAttachments(ContentEntityObject content) {
        return Collections.unmodifiableList(content.getAttachments());
    }

    @Override
    public List<Attachment> getAllVersions(Attachment attachment) {
        return this.attachmentDao.findAllVersions(attachment);
    }

    @Override
    public void removeAttachments(List<? extends Attachment> attachments) {
        attachments.forEach(this::removeAttachmentFromServer);
    }

    @Override
    public void removeAttachmentFromServer(Attachment latestVersion) {
        this.publishAttachmentRemoveEvent(latestVersion, false);
        if (this.retentionFeatureChecker != null && this.retentionFeatureChecker.isFeatureAvailable()) {
            this.relationManager.removeAllRelationsFromEntityWithType((RelationDescriptor)CumulativeContributorRelationDescriptor.CUMULATIVE_CONTRIBUTOR, latestVersion);
        }
        List<Attachment> removedVersions = this.attachmentDao.removeAllVersionsFromServer(latestVersion);
        this.publishAttachmentRemovedEvent(removedVersions);
    }

    @Override
    public void removeAttachmentWithoutNotifications(Attachment latestVersion) {
        this.publishAttachmentRemoveEvent(latestVersion, true);
        if (this.retentionFeatureChecker != null && this.retentionFeatureChecker.isFeatureAvailable()) {
            this.relationManager.removeAllRelationsFromEntityWithType((RelationDescriptor)CumulativeContributorRelationDescriptor.CUMULATIVE_CONTRIBUTOR, latestVersion);
        }
        List<Attachment> removedVersions = this.attachmentDao.removeAllVersionsFromServer(latestVersion);
        this.publishAttachmentRemovedEvent(removedVersions);
    }

    @Override
    public void removeAttachmentVersionFromServer(Attachment attachment) {
        this.removeAttachmentVersionFromServer(attachment, false);
    }

    @Override
    public void removeAttachmentVersionFromServerWithoutNotifications(Attachment attachment) {
        this.removeAttachmentVersionFromServer(attachment, true);
    }

    private void removeAttachmentVersionFromServer(Attachment attachment, boolean shouldSuppressNotifications) {
        if (attachment.isHidden()) {
            this.eventPublisher.publish((Object)new HiddenAttachmentVersionRemoveEvent((Object)this, attachment, AuthenticatedUserThreadLocal.get()));
        } else {
            this.eventPublisher.publish((Object)new AttachmentVersionRemoveEvent(this, attachment, AuthenticatedUserThreadLocal.get(), shouldSuppressNotifications));
        }
        ContentEntityObject container = attachment.getContainer();
        Optional<Date> lastModificationDate = this.handleEditorOnlyContentUpdateBeforeSave(container, null);
        if (this.retentionFeatureChecker != null && this.retentionFeatureChecker.isFeatureAvailable()) {
            Attachment original = (Attachment)attachment.getLatestVersion();
            List<Attachment> historicalVersions = this.attachmentDao.findAllVersions(original);
            if (historicalVersions.size() == 1) {
                this.relationManager.removeAllRelationsFromEntityWithType((RelationDescriptor)CumulativeContributorRelationDescriptor.CUMULATIVE_CONTRIBUTOR, attachment);
            } else if (attachment.getLastModifier() != null) {
                this.relationManager.addRelation(attachment.getLastModifier(), original, (RelationDescriptor)CumulativeContributorRelationDescriptor.CUMULATIVE_CONTRIBUTOR);
            }
        }
        this.attachmentDao.removeAttachmentVersionFromServer(attachment);
        this.handleEditorOnlyContentUpdateAfterSave(container, null, lastModificationDate);
    }

    @Override
    public void moveAttachment(Attachment latestVersion, String newFileName, ContentEntityObject newContent) {
        if (!this.attachmentDao.isAttachmentPresent(latestVersion)) {
            log.warn("Attachment not found for {}", (Object)latestVersion.getFileName());
            return;
        }
        ContentEntityObject oldContent = latestVersion.getContainer();
        Optional.ofNullable(oldContent).ifPresent(ceo -> AttachmentRelatedContentReconciliationListener.updateCEOWithAttachmentChange(ceo, this.contentEntityObjectDao));
        Optional<Date> oldContentLastModificationDate = this.handleEditorOnlyContentUpdateBeforeSave(oldContent, null);
        Optional.ofNullable(newContent).ifPresent(ceo -> AttachmentRelatedContentReconciliationListener.updateCEOWithAttachmentChange(ceo, this.contentEntityObjectDao));
        Optional<Date> newContentsLastModificationDate = this.handleEditorOnlyContentUpdateBeforeSave(newContent, null);
        Attachment oldAttachment = (Attachment)latestVersion.clone();
        if (oldContent != null) {
            oldContent.removeAttachment(latestVersion);
        }
        newContent.addAttachment(latestVersion);
        if (newFileName != null) {
            latestVersion.setFileName(newFileName);
        }
        for (Attachment previousVersion : this.getAllVersions(latestVersion)) {
            if (newFileName != null) {
                previousVersion.setFileName(newFileName);
            }
            if (oldContent != null) {
                oldContent.removeAttachment(previousVersion);
            }
            newContent.addAttachment(previousVersion);
        }
        this.attachmentDao.moveAttachment(latestVersion, oldAttachment, newContent);
        this.autowatchContainerIfNeeded(AuthenticatedUserThreadLocal.get(), latestVersion, DefaultSaveContext.DEFAULT);
        GeneralAttachmentUpdateEvent attachmentUpdateEvent = latestVersion.isHidden() ? new HiddenAttachmentUpdateEvent((Object)this, latestVersion, oldAttachment) : new AttachmentUpdateEvent((Object)this, latestVersion, oldAttachment);
        this.eventPublisher.publish((Object)attachmentUpdateEvent);
        this.handleEditorOnlyContentUpdateAfterSave(oldContent, null, oldContentLastModificationDate);
        this.handleEditorOnlyContentUpdateAfterSave(newContent, null, newContentsLastModificationDate);
    }

    @Override
    public void moveAttachment(Attachment attachment, ContentEntityObject newContainer) {
        this.moveAttachment(attachment, null, newContainer);
    }

    @Override
    public void copyAttachments(ContentEntityObject sourceContent, ContentEntityObject destinationContent, SaveContext saveContext) throws IOException {
        List<Attachment> attachments = this.getLatestVersionsOfAttachments(sourceContent);
        if (attachments.isEmpty()) {
            return;
        }
        for (Attachment attachment : attachments) {
            Attachment attachmentCopy = attachment.copyLatestVersion();
            destinationContent.addAttachment(attachmentCopy);
            InputStream data = this.getAttachmentData(attachment);
            try {
                if (data == null) {
                    log.error("No data found for {}", (Object)attachment);
                    continue;
                }
                this.saveAttachments(Collections.singletonList(new SavableAttachment(attachmentCopy, null, data)), saveContext);
            }
            finally {
                if (data == null) continue;
                data.close();
            }
        }
    }

    @Override
    public void copyAttachments(ContentEntityObject sourceContent, ContentEntityObject destinationContent) throws IOException {
        List<Attachment> attachments = this.getLatestVersionsOfAttachments(sourceContent);
        for (Attachment attachment : attachments) {
            this.copyAttachment(attachment, destinationContent);
        }
    }

    @Override
    public void copyAttachment(Attachment attachment, ContentEntityObject destinationContent) throws IOException {
        Attachment attachmentCopy = new Attachment();
        attachmentCopy.setFileName(attachment.getFileName());
        attachmentCopy.setFileSize(attachment.getFileSize());
        attachmentCopy.setMediaType(attachment.getMediaType());
        attachmentCopy.setVersionComment(attachment.getVersionComment());
        attachmentCopy.setVersion(1);
        destinationContent.addAttachment(attachmentCopy);
        try (InputStream data = this.getAttachmentData(attachment);){
            this.saveAttachment(attachmentCopy, null, data);
        }
    }

    @Override
    public void setAttachmentData(Attachment attachment, InputStream attachmentData) throws AttachmentDataExistsException {
        try {
            this.attachmentDao.getAttachmentData(attachment);
            throw new AttachmentDataExistsException("Attachment data was found for attachment '" + attachment + "'. Cannot set new data.");
        }
        catch (AttachmentDataNotFoundException attachmentDataNotFoundException) {
            this.attachmentDao.replaceAttachmentData(attachment, attachmentData);
            return;
        }
    }

    @Override
    public AttachmentDao.AttachmentMigrator getMigrator(AttachmentManager destination) {
        if (destination instanceof DelegatingAttachmentManager) {
            destination = ((DelegatingAttachmentManager)((Object)destination)).getAttachmentManager();
        }
        if (this.getClass().equals(destination.getClass())) {
            return this.attachmentDao.getMigrator(destination.getAttachmentDao());
        }
        return new GeneralAttachmentMigrator(this, destination);
    }

    @Override
    public AttachmentDao.AttachmentCopier getCopier(AttachmentManager destination) {
        if (destination instanceof DelegatingAttachmentManager) {
            destination = ((DelegatingAttachmentManager)((Object)destination)).getAttachmentManager();
        }
        if (this.getClass().equals(destination.getClass())) {
            return this.attachmentDao.getCopier(destination.getAttachmentDao());
        }
        return new GeneralAttachmentCopier(this, destination);
    }

    @Override
    public AttachmentDao getAttachmentDao() {
        return this.attachmentDao;
    }

    public void setAttachmentDao(AttachmentDaoInternal attachmentDao) {
        this.attachmentDao = attachmentDao;
    }

    @Override
    public AttachmentDataStorageType getBackingStorageType() {
        return this.attachmentDao.getBackingStorageType();
    }

    @Override
    public Optional<Attachment> findAttachmentForDownloadPath(String downloadPath) {
        Matcher downloadPathMatcher = ATTACHMENT_DOWNLOAD_PATH_PATTERN.matcher(downloadPath);
        String pattern = ATTACHMENT_DOWNLOAD_PATH_PATTERN.pattern();
        if (!downloadPathMatcher.find()) {
            log.info("Could not find pattern [{}] in given download path [{}].", (Object)pattern, (Object)downloadPath);
            return Optional.empty();
        }
        long contentId = Long.parseLong(downloadPathMatcher.group(1));
        String fileName = HtmlUtil.urlDecode(downloadPathMatcher.group(2));
        Maybe<ContentEntityObjectDao<?>> entityObjectDao = this.getContentEntityObjectDao();
        if (entityObjectDao.isEmpty()) {
            return Optional.empty();
        }
        Object contentEntityObject = ((ContentEntityObjectDao)entityObjectDao.get()).getById(contentId);
        if (contentEntityObject == null) {
            log.info("Could not find the [{}] instance with id [{}] derived from the given download path [{}] with pattern [{}].", new Object[]{ContentEntityObject.class.getSimpleName(), contentId, downloadPath, pattern});
            return Optional.empty();
        }
        Attachment attachment = this.getAttachment((ContentEntityObject)contentEntityObject, fileName);
        if (attachment == null) {
            log.info("Could not find the [{}] instance for file name [{}] and [{}] derived from the given download path [{}] with pattern [{}].", new Object[]{Attachment.class.getSimpleName(), fileName, contentEntityObject, downloadPath, pattern});
            return Optional.empty();
        }
        return Optional.of(attachment);
    }

    @Override
    public Map<Long, Long> getRemappedAttachmentIds() {
        return this.attachmentDao.getRemappedAttachmentIds();
    }

    @Override
    public void trash(Attachment attachment) {
        ContentEntityObject container = attachment.getContainer();
        Optional<Date> lastModificationDate = this.handleEditorOnlyContentUpdateBeforeSave(container, null);
        attachment.trash();
        this.withIndexers((indexer, changeIndexer) -> this.getAllVersions(attachment).forEach(indexer::unIndexIncludingDependents));
        this.eventPublisher.publish((Object)new AttachmentTrashedEvent(this, attachment, AuthenticatedUserThreadLocal.get(), false));
        if (this.attachmentDao instanceof FlushableCachingDao) {
            ((FlushableCachingDao)((Object)this.attachmentDao)).flush();
        }
        this.handleEditorOnlyContentUpdateAfterSave(container, null, lastModificationDate);
    }

    private void withIndexers(BiConsumer<ConfluenceIndexer, ChangeIndexer> task) {
        new IndexerEventPublisher(this.eventPublisher).publishCallbackEvent(task);
    }

    @Override
    public void restore(Attachment attachment) {
        ContentEntityObject container = attachment.getContainer();
        Optional<Date> lastModificationDate = this.handleEditorOnlyContentUpdateBeforeSave(container, null);
        attachment.restore();
        this.withIndexers((indexer, changeIndexer) -> {
            this.getAllVersions(attachment).forEach(indexer::indexIncludingDependents);
            changeIndexer.reIndexAllVersions(attachment);
        });
        if (attachment.isHidden()) {
            this.eventPublisher.publish((Object)new HiddenAttachmentRestoreEvent((Object)this, attachment, AuthenticatedUserThreadLocal.get()));
        } else {
            this.eventPublisher.publish((Object)new GeneralAttachmentRestoreEvent(this, attachment, AuthenticatedUserThreadLocal.get(), false));
        }
        this.handleEditorOnlyContentUpdateAfterSave(container, null, lastModificationDate);
    }

    private Maybe<ContentEntityObjectDao<?>> getContentEntityObjectDao() {
        if (this.contentEntityObjectDao == null) {
            return MaybeNot.becauseOf("The [%s] is missing a reference to a [%s] due to being improperly initialized via a legacy constructor.", DefaultAttachmentManager.class.getSimpleName(), ContentEntityObjectDao.class.getSimpleName());
        }
        return Option.some(this.contentEntityObjectDao);
    }

    private void publishAttachmentRemoveEvent(Attachment attachment, boolean suppressNotifications) {
        if (attachment.isHidden()) {
            this.eventPublisher.publish((Object)new HiddenAttachmentRemoveEvent((Object)this, attachment, AuthenticatedUserThreadLocal.get()));
        } else {
            this.eventPublisher.publish((Object)new AttachmentRemoveEvent(this, attachment, AuthenticatedUserThreadLocal.get(), suppressNotifications));
        }
    }

    private void publishAttachmentRemovedEvent(List<Attachment> removedVersions) {
        this.eventPublisher.publish((Object)new AttachmentRemovedEvent(removedVersions));
    }

    private void autowatchContainerIfNeeded(@Nullable User user, Attachment attachment, SaveContext saveContext) {
        if (saveContext.isSuppressAutowatch()) {
            return;
        }
        if (user == null) {
            return;
        }
        if (attachment.isHidden() || attachment.getContainer() instanceof Draft) {
            return;
        }
        if (!this.userPreferencesAccessor.getConfluenceUserPreferences(user).isWatchingOwnContent()) {
            return;
        }
        this.notificationManager.addContentNotification(user, attachment.getContainer());
    }

    private Map<ContentEntityObject, Optional<Date>> extractBackingContainers(List<SavableAttachment> attachments) {
        return attachments.stream().map(savableAttachment -> savableAttachment.getAttachment()).filter(attachment -> !Objects.isNull(attachment)).map(attachment -> attachment.getContainer()).filter(container -> !Objects.isNull(container)).collect(Collectors.toMap(Function.identity(), container -> Optional.ofNullable(container.getLastModificationDate()), (o1, o2) -> o1));
    }

    private Optional<Date> handleEditorOnlyContentUpdateBeforeSave(ContentEntityObject ceo, SaveContext context) {
        Optional<Date> lastModificationDate = Optional.empty();
        if (ceo != null) {
            lastModificationDate = Optional.ofNullable(ceo.getLastModificationDate());
            this.reconciliationManager.handleEditorOnlyContentUpdateBeforeSave(ceo, context);
        }
        return lastModificationDate;
    }

    private void handleEditorOnlyContentUpdateAfterSave(ContentEntityObject ceo, SaveContext saveContext, Optional<Date> lastModificationDate) {
        if (ceo != null) {
            this.reconciliationManager.handleEditorOnlyContentUpdateAfterSave(ceo, saveContext, lastModificationDate);
        }
    }
}


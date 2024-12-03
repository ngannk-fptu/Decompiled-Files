/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.pagination.ContentCursor
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 *  com.atlassian.confluence.api.model.relations.CollaboratorRelationDescriptor
 *  com.atlassian.confluence.api.model.relations.CumulativeContributorRelationDescriptor
 *  com.atlassian.confluence.api.model.relations.RelationDescriptor
 *  com.atlassian.confluence.api.service.retention.RetentionFeatureChecker
 *  com.atlassian.core.bean.EntityObject
 *  com.atlassian.core.exception.InfrastructureException
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.hibernate.HibernateException
 *  org.hibernate.SessionFactory
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.content;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.pagination.ContentCursor;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.api.model.relations.CollaboratorRelationDescriptor;
import com.atlassian.confluence.api.model.relations.CumulativeContributorRelationDescriptor;
import com.atlassian.confluence.api.model.relations.RelationDescriptor;
import com.atlassian.confluence.api.service.retention.RetentionFeatureChecker;
import com.atlassian.confluence.audit.AuditingContext;
import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.core.BodyContent;
import com.atlassian.confluence.core.BodyType;
import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.ContributionStatus;
import com.atlassian.confluence.core.DefaultSaveContext;
import com.atlassian.confluence.core.Modification;
import com.atlassian.confluence.core.SaveContext;
import com.atlassian.confluence.core.VersionHistorySummary;
import com.atlassian.confluence.core.persistence.ContentEntityObjectDao;
import com.atlassian.confluence.core.persistence.confluence.StaleObjectStateException;
import com.atlassian.confluence.event.events.content.ContentHistoricalVersionRemoveEvent;
import com.atlassian.confluence.event.events.content.ContentRevertedEvent;
import com.atlassian.confluence.event.events.content.Contented;
import com.atlassian.confluence.event.events.types.Removed;
import com.atlassian.confluence.impl.notifications.ContentEntityAutoWatcher;
import com.atlassian.confluence.internal.ContentEntityManagerInternal;
import com.atlassian.confluence.internal.pages.AttachmentManagerInternal;
import com.atlassian.confluence.internal.relations.RelationManager;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.links.LinkManager;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.setup.settings.CollaborativeEditingHelper;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.xhtml.api.WikiToStorageConverter;
import com.atlassian.core.bean.EntityObject;
import com.atlassian.core.exception.InfrastructureException;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class DefaultContentEntityManager
implements ContentEntityManagerInternal {
    private static final Logger log = LoggerFactory.getLogger(DefaultContentEntityManager.class);
    private final ContentEntityObjectDao<? extends ContentEntityObject> contentEntityObjectDao;
    private final SessionFactory sessionFactory;
    private final WikiToStorageConverter wikiToStorageConverter;
    private final EventPublisher eventPublisher;
    private final AuditingContext auditingContext;
    private final RelationManager relationManager;
    private final RetentionFeatureChecker retentionFeatureChecker;
    private final CollaborativeEditingHelper collaborativeEditingHelper;
    private final EventFactory eventFactory;

    DefaultContentEntityManager(ContentEntityObjectDao<? extends ContentEntityObject> contentEntityObjectDao, SessionFactory sessionFactory, WikiToStorageConverter wikiToStorageConverter, EventPublisher eventPublisher, RelationManager relationManager, CollaborativeEditingHelper collaborativeEditingHelper, AuditingContext auditingContext, RetentionFeatureChecker retentionFeatureChecker, EventFactory eventFactory) {
        this.contentEntityObjectDao = Objects.requireNonNull(contentEntityObjectDao);
        this.sessionFactory = Objects.requireNonNull(sessionFactory);
        this.wikiToStorageConverter = Objects.requireNonNull(wikiToStorageConverter);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.auditingContext = Objects.requireNonNull(auditingContext);
        this.relationManager = Objects.requireNonNull(relationManager);
        this.collaborativeEditingHelper = Objects.requireNonNull(collaborativeEditingHelper);
        this.retentionFeatureChecker = Objects.requireNonNull(retentionFeatureChecker);
        this.eventFactory = Objects.requireNonNull(eventFactory);
    }

    public DefaultContentEntityManager(ContentEntityObjectDao<? extends ContentEntityObject> contentEntityObjectDao, SessionFactory sessionFactory, WikiToStorageConverter wikiToStorageConverter, EventPublisher eventPublisher, RelationManager relationManager, CollaborativeEditingHelper collaborativeEditingHelper, AuditingContext auditingContext, RetentionFeatureChecker retentionFeatureChecker) {
        this(contentEntityObjectDao, sessionFactory, wikiToStorageConverter, eventPublisher, relationManager, collaborativeEditingHelper, auditingContext, retentionFeatureChecker, DefaultContentEntityManager.warningEventFactory());
    }

    public static <T extends ConfluenceEntityObject> PageResponse<T> filteredResponseWithCursor(LimitedRequest originalRequest, @Nullable Predicate<? super T> filter, List<T> pages) {
        return PageResponseImpl.filteredResponseWithCursor((LimitedRequest)originalRequest, pages, filter, (page, isReverse) -> ContentCursor.createCursor((boolean)isReverse, (long)page.getId()), Comparator.comparing(EntityObject::getId));
    }

    @Override
    public void refreshContentEntity(ContentEntityObject obj) {
        this.contentEntityObjectDao.refresh(obj);
        try {
            this.sessionFactory.getCurrentSession().refresh((Object)obj.getBodyContents().get(0));
        }
        catch (HibernateException e) {
            throw new InfrastructureException("Couldn't refresh Body Content while refreshing Content Entity", (Throwable)e);
        }
    }

    @Override
    public <T extends ContentEntityObject> T createDraft(T draft, SaveContext saveContext) {
        draft.setContentStatus("draft");
        if (draft.getCreator() == null) {
            draft.setCreator(AuthenticatedUserThreadLocal.get());
        }
        if (draft.getShareId() == null) {
            draft.setShareId(UUID.randomUUID().toString());
        }
        this.saveContentEntity(draft, saveContext);
        return draft;
    }

    @Override
    public <T extends ContentEntityObject> T findDraftFor(@NonNull T ceo) {
        return (T)(ceo.isDraft() ? ceo : this.contentEntityObjectDao.findDraftFor(ceo.getId()));
    }

    @Override
    public <T extends ContentEntityObject> T findDraftFor(long contentId) {
        return (T)this.contentEntityObjectDao.findDraftFor(contentId);
    }

    @Override
    public <T extends ContentEntityObject> List<T> findAllDraftsFor(long contentId) {
        return this.contentEntityObjectDao.findAllDraftsFor(contentId);
    }

    @Override
    public List<ContentEntityObject> findUnpublishedContentWithUserContributions(String username) {
        return this.contentEntityObjectDao.findUnpublishedContentWithUserContributions(username);
    }

    @Override
    public List<ContentEntityObject> findAllDraftsWithUnpublishedChangesForUser(String creatorName) {
        return this.contentEntityObjectDao.findDraftsWithUnpublishedChangesForUser(creatorName);
    }

    @Override
    public void saveContentEntity(ContentEntityObject obj, @Nullable SaveContext saveContext) {
        boolean isNewContentObject = true;
        if (obj.getId() != 0L) {
            boolean bl = isNewContentObject = this.contentEntityObjectDao.getById(obj.getId()) == null;
        }
        if (saveContext != null && !saveContext.doUpdateLastModifier()) {
            this.contentEntityObjectDao.saveRaw(obj);
        } else {
            this.contentEntityObjectDao.save(obj);
        }
        if (saveContext == null || !saveContext.isEventSuppressed()) {
            if (isNewContentObject) {
                this.publishCreateEvent(obj, saveContext);
            } else {
                this.publishUpdateEvent(obj, null, saveContext);
            }
        }
        this.autowatchIfRequired(obj, saveContext);
        this.updateOutgoingLinks(obj);
    }

    @Override
    public void saveContentEntity(ContentEntityObject obj, @Nullable ContentEntityObject origObj, @Nullable SaveContext saveContext) {
        if (saveContext != null && !saveContext.doUpdateLastModifier()) {
            this.contentEntityObjectDao.saveRaw(obj);
        } else {
            this.contentEntityObjectDao.save(obj, origObj);
        }
        if (saveContext == null || !saveContext.isEventSuppressed()) {
            this.publishUpdateEvent(obj, origObj, saveContext);
        }
        this.autowatchIfRequired(obj, saveContext);
        this.updateOutgoingLinks(obj);
    }

    @Override
    public <T extends ContentEntityObject> void saveNewVersion(T current, Modification<T> modification) {
        this.saveNewVersion(current, modification, null);
    }

    @Override
    public <T extends ContentEntityObject> void saveNewVersion(T current, Modification<T> modification, @Nullable SaveContext saveContext) {
        ContentEntityObject newHistoricalVersion = (ContentEntityObject)current.clone();
        current.setVersionComment("");
        modification.modify(current);
        this.saveContentEntity(current, newHistoricalVersion, saveContext);
    }

    protected void removeContentEntities(Iterable<? extends ContentEntityObject> contentEntityObjects) {
        this.withAttachmentManager(attachmentManager -> {
            List<Attachment> latestVersionsOfAttachments = ((AttachmentManagerInternal)attachmentManager).getLatestVersionsOfAttachmentsWithAnyStatusForContainers(contentEntityObjects);
            attachmentManager.removeAttachments(latestVersionsOfAttachments);
        });
        this.relationManager.removeAllRelationsFromCurrentAndHistoricalEntities(contentEntityObjects);
        for (ContentEntityObject contentEntityObject : contentEntityObjects) {
            this.removeContentEntityInternal(contentEntityObject);
        }
    }

    private void withAttachmentManager(Consumer<AttachmentManager> task) {
        this.eventPublisher.publish(task::accept);
    }

    private void removeContentEntityInternal(ContentEntityObject obj) {
        if (obj.isLatestVersion() || obj.isDraft()) {
            this.publishRemoveEvent(obj);
            this.removeAllLabels(obj);
            this.removeOutgoingLinks(obj);
        }
        if (!obj.isDraft()) {
            Stream.of(this.findAllDraftsFor(obj.getId()), this.contentEntityObjectDao.findAllLegacyDraftsFor(obj.getId())).filter(Objects::nonNull).flatMap(Collection::stream).filter(Objects::nonNull).forEach(this::removeContentEntity);
        } else {
            this.eventPublisher.publish((Object)new DraftRemovalEvent(obj));
        }
        this.contentEntityObjectDao.remove(obj);
    }

    private void removeAllLabels(ContentEntityObject obj) {
        this.withLabelManager(labelManager -> labelManager.removeAllLabels(obj));
    }

    private void updateOutgoingLinks(ContentEntityObject obj) {
        this.withLinkManager(linkManager -> linkManager.updateOutgoingLinks(obj));
    }

    private void removeOutgoingLinks(ContentEntityObject obj) {
        ImmutableList links = ImmutableList.copyOf(obj.getOutgoingLinks());
        this.withLinkManager(arg_0 -> DefaultContentEntityManager.lambda$removeOutgoingLinks$4((Collection)links, arg_0));
    }

    private void withLinkManager(Consumer<LinkManager> task) {
        this.eventPublisher.publish(task::accept);
    }

    private void withLabelManager(Consumer<LabelManager> task) {
        this.eventPublisher.publish(task::accept);
    }

    @Override
    public void removeContentEntity(ContentEntityObject obj) {
        Page page;
        if (obj instanceof Page && (page = (Page)obj).isDraft()) {
            page.getChildren().forEach(child -> {
                child.setParentPage(null);
                child.setAncestors(new ArrayList<Page>());
            });
            page.setChildren((List<Page>)new ArrayList<Page>());
        }
        this.withAttachmentManager(attachmentManager -> this.auditingContext.executeWithoutAuditing(() -> {
            List<Attachment> latestVersionsOfAttachments = attachmentManager.getLatestVersionsOfAttachmentsWithAnyStatus(obj);
            attachmentManager.removeAttachments(latestVersionsOfAttachments);
        }));
        this.relationManager.removeAllRelationsFromCurrentAndHistoricalEntities(obj);
        this.removeContentEntityInternal(obj);
    }

    @Override
    public @Nullable ContentEntityObject getById(long id) {
        return this.contentEntityObjectDao.getById(id);
    }

    @Override
    public @Nullable ContentEntityObject getById(ContentId id) {
        ContentEntityObject entity = this.getById(id.asLong());
        if (entity == null || entity instanceof CustomContentEntityObject) {
            return entity;
        }
        ContentType contentType = ContentType.valueOf((String)entity.getType());
        if (ContentType.BUILT_IN.contains(contentType)) {
            return entity;
        }
        return null;
    }

    @Override
    public @Nullable ContentEntityObject getById(ContentId id, int version) {
        ContentEntityObject entity = this.getById(id);
        if (entity == null) {
            return null;
        }
        if (!entity.isLatestVersion()) {
            return null;
        }
        return version > 0 ? this.getOtherVersion(entity, version) : entity;
    }

    @Override
    @Deprecated
    public PageResponse<ContentEntityObject> getByIds(List<ContentId> contentIds, LimitedRequest limitedRequest, com.google.common.base.Predicate<? super ContentEntityObject> ... filters) {
        return this.contentEntityObjectDao.findByClassIds(Iterables.transform(contentIds, ContentId::asLong), limitedRequest, Predicates.and(filters));
    }

    @Override
    public @NonNull PageResponse<VersionHistorySummary> getVersionHistorySummaries(ContentId contentId, LimitedRequest limitedRequest) {
        return this.contentEntityObjectDao.getVersionHistorySummary(contentId.asLong(), limitedRequest);
    }

    @Override
    public void revertContentEntityBackToVersion(ContentEntityObject entity, int version, @Nullable String revertComment, boolean revertTitle) {
        this.revertContentEntityBackToVersion(entity, version, revertComment, revertTitle, this::convertFromWikiToStorageFormatIfRequired);
    }

    public void revertContentEntityBackToVersion(ContentEntityObject entity, int version, @Nullable String revertComment, boolean revertTitle, Function<ContentEntityObject, BodyContent> revertBodyContentFactory) {
        if (!entity.isLatestVersion()) {
            throw new StaleObjectStateException("Page revert back can not be done for the non-latest version!");
        }
        if (entity.getVersion() == version) {
            throw new StaleObjectStateException("Can't revert a page back to itself");
        }
        ContentEntityObject currentVersion = (ContentEntityObject)entity.clone();
        ContentEntityObject historicalVersion = this.getOtherVersion(entity, version);
        if (historicalVersion == null) {
            throw new InfrastructureException("The specified version not found to revert back!");
        }
        entity.setBodyContent(revertBodyContentFactory.apply(historicalVersion));
        if (revertTitle) {
            entity.setTitle(historicalVersion.getTitle());
        }
        entity.setVersionComment(revertComment);
        this.saveContentEntity(entity, currentVersion, DefaultSaveContext.REVERT);
        this.relationManager.moveRelationsToContent(entity, currentVersion, (RelationDescriptor)CollaboratorRelationDescriptor.COLLABORATOR);
        if (!AuthenticatedUserThreadLocal.isAnonymousUser()) {
            this.relationManager.addRelation(AuthenticatedUserThreadLocal.get(), entity, (RelationDescriptor)CollaboratorRelationDescriptor.COLLABORATOR);
        }
        this.eventPublisher.publish((Object)new ContentRevertedEvent(entity, version, revertComment));
    }

    protected @NonNull BodyContent convertFromWikiToStorageFormatIfRequired(ContentEntityObject historicalVersion) {
        if (historicalVersion.getBodyContent().getBodyType() == BodyType.WIKI) {
            return this.wikiToStorageConverter.convertWikiBodyToStorage(historicalVersion).getBodyContent();
        }
        return historicalVersion.getBodyContent();
    }

    @Override
    public @NonNull Iterator getRecentlyAddedEntities(@Nullable String spaceKey, int maxResults) {
        return this.contentEntityObjectDao.getRecentlyAddedEntities(spaceKey, maxResults);
    }

    @Override
    public @NonNull Iterator getRecentlyModifiedEntities(String spaceKey, int maxResults) {
        return this.contentEntityObjectDao.getRecentlyModifiedEntities(spaceKey, maxResults);
    }

    @Override
    public @NonNull PageResponse<AbstractPage> getPageAndBlogPostsVersionsLastEditedByUser(UserKey userKey, LimitedRequest request) {
        return this.contentEntityObjectDao.getPageAndBlogPostsVersionsLastEditedByUser(userKey, request);
    }

    @Override
    public @NonNull PageResponse<AbstractPage> getPageAndBlogPostsVersionsLastEditedByUserIncludingDrafts(@Nullable UserKey userKey, LimitedRequest request) {
        return this.contentEntityObjectDao.getPageAndBlogPostsVersionsLastEditedByUserIncludingDrafts(userKey, request);
    }

    @Override
    public @NonNull Iterator getRecentlyModifiedEntitiesForUser(String username) {
        return this.contentEntityObjectDao.getRecentlyModifiedEntitiesForUser(username);
    }

    @Override
    public @NonNull List getRecentlyModifiedForChangeDigest(Date fromDate) {
        return this.contentEntityObjectDao.getRecentlyModifiedForChangeDigest(fromDate);
    }

    @Override
    public @Nullable ContentEntityObject getPreviousVersion(ContentEntityObject ceo) {
        if (ceo.isDraft()) {
            return null;
        }
        ContentEntityObject latestVersion = (ContentEntityObject)ceo.getLatestVersion();
        return this.contentEntityObjectDao.getFirstVersionBefore(latestVersion.getId(), ceo.getVersion());
    }

    @Override
    public @Nullable ContentEntityObject getNextVersion(ContentEntityObject ceo) {
        if (ceo.isDraft()) {
            return null;
        }
        ContentEntityObject latestVersion = (ContentEntityObject)ceo.getLatestVersion();
        return this.contentEntityObjectDao.getFirstVersionAfter(latestVersion.getId(), ceo.getVersion());
    }

    @Override
    public @Nullable ContentEntityObject getOtherVersion(ContentEntityObject ceo, int version) {
        if (ceo.getVersion() == version) {
            return ceo;
        }
        ContentEntityObject latestVersion = (ContentEntityObject)ceo.getLatestVersion();
        if (latestVersion.getVersion() == version) {
            return latestVersion;
        }
        return this.contentEntityObjectDao.getVersion(latestVersion.getId(), version);
    }

    @Override
    public @NonNull Map<Long, ContentEntityObject> getVersionsLastEditedByUser(@NonNull Collection<ContentId> contentIds, @Nullable UserKey userKey) {
        if (this.collaborativeEditingHelper.getEditMode("").equals("legacy")) {
            return this.contentEntityObjectDao.getVersionsLastEditedByUser(contentIds.stream().map(ContentId::asLong).collect(Collectors.toList()), userKey);
        }
        return this.contentEntityObjectDao.getVersionsLastEditedByUserNew(contentIds.stream().map(ContentId::asLong).collect(Collectors.toList()), userKey);
    }

    @Override
    public Map<Long, ContributionStatus> getContributionStatusByUser(@NonNull Collection<ContentId> contentIds, @Nullable UserKey userKey) {
        return this.contentEntityObjectDao.getContributionStatusByUser(contentIds, userKey);
    }

    @Override
    public @NonNull List<VersionHistorySummary> getVersionHistorySummaries(ContentEntityObject ceo) {
        return this.contentEntityObjectDao.getVersionHistorySummary(((ContentEntityObject)ceo.getLatestVersion()).getId());
    }

    @Override
    public void removeHistoricalVersion(ContentEntityObject historicalVersion) {
        if (this.shouldHistoricalVersionBeRemoved(historicalVersion)) {
            this.relationManager.removeAllRelations(historicalVersion);
            if (this.retentionFeatureChecker != null && this.retentionFeatureChecker.isFeatureAvailable()) {
                this.addCumulativeContributorRelation(historicalVersion);
            }
            this.eventPublisher.publish((Object)new ContentHistoricalVersionRemoveEvent((Object)this, historicalVersion));
            this.contentEntityObjectDao.remove(historicalVersion);
            this.sequenceHistoricalVersions(historicalVersion);
        }
    }

    private void addCumulativeContributorRelation(ContentEntityObject historicalVersion) {
        if (historicalVersion.getLastModifier() != null) {
            this.relationManager.addRelation(historicalVersion.getLastModifier(), (ContentEntityObject)historicalVersion.getLatestVersion(), (RelationDescriptor)CumulativeContributorRelationDescriptor.CUMULATIVE_CONTRIBUTOR);
        }
    }

    private void sequenceHistoricalVersions(ContentEntityObject historicalVersion) {
        if (this.retentionFeatureChecker != null && !this.retentionFeatureChecker.isFeatureAvailable()) {
            ContentEntityObject historicalVersionObject = (ContentEntityObject)historicalVersion.getLatestVersion();
            int versionToRemove = historicalVersion.getVersion();
            List<ContentEntityObject> subsequentHistoricalVersions = this.contentEntityObjectDao.findHistoricalVersionsAfterVersion(historicalVersionObject.getId(), versionToRemove);
            subsequentHistoricalVersions.add(historicalVersionObject);
            for (ContentEntityObject contentEntity : subsequentHistoricalVersions) {
                contentEntity.setVersion(versionToRemove++);
            }
        }
    }

    private boolean shouldHistoricalVersionBeRemoved(ContentEntityObject historicalVersion) {
        return !historicalVersion.isLatestVersion() && historicalVersion.isPersistent() && historicalVersion.getLatestVersion() instanceof ContentEntityObject && this.findAllDraftsFor(historicalVersion.getId()).isEmpty();
    }

    protected void publishCreateEvent(ContentEntityObject obj, @Nullable SaveContext saveContext) {
        this.eventFactory.newCreateEvent(this, obj, saveContext).ifPresent(arg_0 -> ((EventPublisher)this.eventPublisher).publish(arg_0));
    }

    protected void publishUpdateEvent(ContentEntityObject obj, @Nullable ContentEntityObject origObj, @Nullable SaveContext saveContext) {
        this.eventFactory.newUpdateEvent(this, obj, origObj, saveContext).ifPresent(arg_0 -> ((EventPublisher)this.eventPublisher).publish(arg_0));
    }

    protected void publishRemoveEvent(ContentEntityObject obj) {
        this.eventFactory.newRemoveEvent(this, obj).ifPresent(arg_0 -> ((EventPublisher)this.eventPublisher).publish(arg_0));
    }

    private void autowatchIfRequired(ContentEntityObject ceo, @Nullable SaveContext nullableSaveContext) {
        SaveContext saveContext = nullableSaveContext != null ? nullableSaveContext : DefaultSaveContext.DEFAULT;
        this.eventPublisher.publish((Object)new ContentEntityAutoWatcher.AutowatchIfRequiredEvent(ceo, saveContext));
    }

    private static EventFactory warningEventFactory() {
        return new EventFactory(){

            @Override
            public Optional<?> newCreateEvent(Object source, ContentEntityObject obj, @Nullable SaveContext saveContext) {
                log.warn("Tried to publish a create event from generic content manager", (Throwable)new UnsupportedOperationException());
                return Optional.empty();
            }

            @Override
            public Optional<?> newUpdateEvent(Object source, ContentEntityObject obj, @Nullable ContentEntityObject origObj, @Nullable SaveContext saveContext) {
                log.warn("Tried to publish an update event from generic content manager", (Throwable)new UnsupportedOperationException());
                return Optional.empty();
            }

            @Override
            public Optional<?> newRemoveEvent(Object source, ContentEntityObject obj) {
                log.warn("Tried to publish a remove event from generic content manager", (Throwable)new UnsupportedOperationException());
                return Optional.empty();
            }
        };
    }

    private static /* synthetic */ void lambda$removeOutgoingLinks$4(Collection links, LinkManager linkManager) {
        links.forEach(linkManager::removeLink);
    }

    static interface EventFactory {
        default public Optional<?> newCreateEvent(Object source, ContentEntityObject obj, @Nullable SaveContext saveContext) {
            return Optional.empty();
        }

        default public Optional<?> newUpdateEvent(Object source, ContentEntityObject obj, @Nullable ContentEntityObject origObj, @Nullable SaveContext saveContext) {
            return Optional.empty();
        }

        default public Optional<?> newRemoveEvent(Object source, ContentEntityObject obj) {
            return Optional.empty();
        }

        public static EventFactory noEvents() {
            return new EventFactory(){};
        }
    }

    static class DraftRemovalEvent
    implements Removed,
    Contented {
        private final ContentEntityObject draft;

        DraftRemovalEvent(ContentEntityObject draft) {
            this.draft = draft;
        }

        @Override
        public @NonNull ContentEntityObject getContent() {
            return this.draft;
        }
    }
}


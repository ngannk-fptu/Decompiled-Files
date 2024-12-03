/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.confluence.api.model.Depth
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 *  com.atlassian.confluence.api.service.exceptions.unchecked.NotImplementedServiceException
 *  com.atlassian.confluence.api.service.retention.RetentionFeatureChecker
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Iterators
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.hibernate.SessionFactory
 */
package com.atlassian.confluence.impl.content;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.api.impl.service.content.factory.ContentFactory;
import com.atlassian.confluence.api.model.Depth;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.api.service.exceptions.unchecked.NotImplementedServiceException;
import com.atlassian.confluence.api.service.retention.RetentionFeatureChecker;
import com.atlassian.confluence.audit.AuditingContext;
import com.atlassian.confluence.content.ContentQuery;
import com.atlassian.confluence.content.ContentTypeManager;
import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.content.CustomContentManager;
import com.atlassian.confluence.content.event.PluginContentCreatedEvent;
import com.atlassian.confluence.content.event.PluginContentRemovedEvent;
import com.atlassian.confluence.content.event.PluginContentUpdatedEvent;
import com.atlassian.confluence.content.event.PluginContentWillBeRemovedForSpaceEvent;
import com.atlassian.confluence.content.persistence.CustomContentDao;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SaveContext;
import com.atlassian.confluence.impl.content.DefaultContentEntityManager;
import com.atlassian.confluence.internal.relations.RelationManager;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.setup.settings.CollaborativeEditingHelper;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.Spaced;
import com.atlassian.confluence.xhtml.api.WikiToStorageConverter;
import com.atlassian.event.api.EventPublisher;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.hibernate.SessionFactory;

@ParametersAreNonnullByDefault
public class DefaultCustomContentManager
extends DefaultContentEntityManager
implements CustomContentManager {
    private final CustomContentDao customContentDao;
    private final EventPublisher eventPublisher;
    private ContentTypeManager contentTypeManager;
    private ContentFactory contentFactory;

    public DefaultCustomContentManager(CustomContentDao customContentDao, SessionFactory sessionFactory, WikiToStorageConverter wikiToStorageConverter, EventPublisher eventPublisher, ContentTypeManager contentTypeManager, RelationManager relationManager, ContentFactory contentFactory, CollaborativeEditingHelper collaborativeEditingHelper, AuditingContext auditingContext, RetentionFeatureChecker retentionFeatureChecker) {
        super(customContentDao, sessionFactory, wikiToStorageConverter, eventPublisher, relationManager, collaborativeEditingHelper, auditingContext, retentionFeatureChecker, DefaultCustomContentManager.eventFactory());
        this.customContentDao = (CustomContentDao)Preconditions.checkNotNull((Object)customContentDao);
        this.eventPublisher = (EventPublisher)Preconditions.checkNotNull((Object)eventPublisher);
        this.contentTypeManager = (ContentTypeManager)Preconditions.checkNotNull((Object)contentTypeManager);
        this.contentFactory = (ContentFactory)Preconditions.checkNotNull((Object)contentFactory);
    }

    @Override
    public @NonNull CustomContentEntityObject newPluginContentEntityObject(String contentModuleKey) {
        CustomContentEntityObject obj = new CustomContentEntityObject();
        obj.setPluginModuleKey(contentModuleKey);
        obj.setAdapter(this.contentTypeManager.getContentType(contentModuleKey).getContentAdapter());
        obj.setPluginVersion(this.contentTypeManager.getImplementingPluginVersion(contentModuleKey));
        return obj;
    }

    @Override
    public @NonNull CustomContentEntityObject updatePluginModuleKey(CustomContentEntityObject content, String pluginModuleKey) {
        content.setPluginModuleKey(pluginModuleKey);
        content.setAdapter(this.contentTypeManager.getContentType(pluginModuleKey).getContentAdapter());
        content.setPluginVersion(this.contentTypeManager.getImplementingPluginVersion(pluginModuleKey));
        return content;
    }

    @Override
    public @Nullable CustomContentEntityObject getById(long id) {
        return (CustomContentEntityObject)this.customContentDao.getById(id);
    }

    @Override
    public <T> @NonNull Iterator<T> findByQuery(ContentQuery<T> query, int offset, int maxResults) {
        return this.customContentDao.findByQuery(query, offset, maxResults);
    }

    @Override
    public <T> @NonNull List<T> queryForList(ContentQuery<T> query, int offset, int maxResults) {
        return this.customContentDao.queryForList(query, offset, maxResults);
    }

    @Override
    public <T> @NonNull List<T> queryForList(ContentQuery<T> query) {
        return this.customContentDao.queryForList(query);
    }

    @Override
    public <T> @NonNull PageResponse<T> findByQueryAndFilter(ContentQuery<T> query, boolean cacheable, LimitedRequest request, Predicate<T> predicate) {
        return PageResponseImpl.filteredResponse((LimitedRequest)request, this.customContentDao.findByQuery(query, cacheable, request), predicate);
    }

    @Override
    public <T> @Nullable T findFirstObjectByQuery(ContentQuery<T> query) {
        Iterator<T> it = this.findByQuery(query, 0, 1);
        return it.hasNext() ? (T)it.next() : null;
    }

    @Override
    public int findTotalInSpace(Space space, String pluginContentKey) {
        return this.customContentDao.findTotalInSpace(space.getId(), pluginContentKey);
    }

    @Override
    public @NonNull Iterator<CustomContentEntityObject> findCurrentInSpace(Space space, String pluginContentKey, int offset, int maxResults, CustomContentManager.SortField sortField, CustomContentManager.SortOrder sortOrder) {
        return this.customContentDao.findCurrentInSpace(space.getId(), pluginContentKey, offset, maxResults, sortField, sortOrder);
    }

    @Override
    public long countChildrenOfType(CustomContentEntityObject content, String contentModuleKey) {
        return this.customContentDao.countChildrenOfType(content.getId(), contentModuleKey);
    }

    @Override
    public @NonNull Iterator<CustomContentEntityObject> findChildrenOfType(CustomContentEntityObject content, String pluginContentKey, int offset, int maxResults, CustomContentManager.SortField sortField, CustomContentManager.SortOrder sortOrder) {
        return this.customContentDao.findChildrenOfType(content.getId(), pluginContentKey, offset, maxResults, sortField, sortOrder);
    }

    @Override
    public @NonNull PageResponse<Content> getChildrenOfTypeAndFilter(ContentEntityObject entity, String contentModuleKey, LimitedRequest limitedRequest, Expansions expansions, Depth depth, Predicate<? super CustomContentEntityObject> predicate) {
        LinkedList children = new LinkedList();
        if (depth == Depth.ALL) {
            throw new NotImplementedServiceException("Custom content type children is currently only supported for direct children");
        }
        Iterator<CustomContentEntityObject> iterator = entity instanceof CustomContentEntityObject && ((CustomContentEntityObject)entity).getPluginModuleKey().equals(contentModuleKey) ? this.customContentDao.findChildrenOfType(entity.getId(), contentModuleKey, limitedRequest.getStart(), limitedRequest.getLimit(), CustomContentManager.SortField.CREATED, CustomContentManager.SortOrder.DESC) : this.customContentDao.findAllContainedOfType(entity.getId(), contentModuleKey);
        Iterators.addAll(children, iterator);
        PageResponse customContentEntityPageResponse = PageResponseImpl.filteredResponse((LimitedRequest)limitedRequest, children, predicate);
        List results = customContentEntityPageResponse.getResults();
        Iterable<Content> contentList = this.contentFactory.buildFrom(results, expansions);
        return PageResponseImpl.from(contentList, (boolean)customContentEntityPageResponse.hasMore()).build();
    }

    @Override
    public @NonNull Iterator<CustomContentEntityObject> findAllChildren(CustomContentEntityObject content) {
        return this.customContentDao.findAllChildren(content.getId());
    }

    @Override
    public @NonNull Iterator<CustomContentEntityObject> findAllContainedOfType(long containerContentId, String pluginModuleKey) {
        return this.customContentDao.findAllContainedOfType(containerContentId, pluginModuleKey);
    }

    @Override
    public void removeAllInSpace(String pluginContentKey, Space space) {
        Iterator<CustomContentEntityObject> contentWithAttachmentsIterator = this.customContentDao.findAllInSpaceWithAttachments(pluginContentKey, space.getId());
        while (contentWithAttachmentsIterator.hasNext()) {
            CustomContentEntityObject entityWithAttachments = contentWithAttachmentsIterator.next();
            this.withAttachmentManager(attachmentManager -> attachmentManager.removeAttachments(new ArrayList<Attachment>(entityWithAttachments.getAttachments())));
        }
        Iterator<CustomContentEntityObject> contentIterator = this.customContentDao.findAllInSpace(pluginContentKey, space.getId());
        while (contentIterator.hasNext()) {
            this.removeContentEntity(contentIterator.next());
        }
    }

    private static DefaultContentEntityManager.EventFactory eventFactory() {
        return new DefaultContentEntityManager.EventFactory(){

            @Override
            public Optional<?> newCreateEvent(Object source, ContentEntityObject obj, @Nullable SaveContext saveContext) {
                return Optional.of(new PluginContentCreatedEvent(source, (CustomContentEntityObject)obj, saveContext));
            }

            @Override
            public Optional<?> newUpdateEvent(Object source, ContentEntityObject obj, @Nullable ContentEntityObject origObj, @Nullable SaveContext saveContext) {
                return Optional.of(new PluginContentUpdatedEvent(source, (CustomContentEntityObject)obj, (CustomContentEntityObject)origObj, saveContext));
            }

            @Override
            public Optional<?> newRemoveEvent(Object source, ContentEntityObject obj) {
                return Optional.of(new PluginContentRemovedEvent(source, (CustomContentEntityObject)obj));
            }
        };
    }

    @Override
    public void removeAllPluginContentInSpace(Space space) {
        this.eventPublisher.publish((Object)new PluginContentWillBeRemovedForSpaceEvent(this, space));
        Iterator<CustomContentEntityObject> contentWithAttachmentsIterator = this.customContentDao.findAllInSpaceWithAttachments(space.getId());
        while (contentWithAttachmentsIterator.hasNext()) {
            CustomContentEntityObject entityWithAttachments = contentWithAttachmentsIterator.next();
            this.withAttachmentManager(attachmentManager -> attachmentManager.removeAttachments(new ArrayList<Attachment>(entityWithAttachments.getAttachments())));
        }
        Iterator<CustomContentEntityObject> contentIterator = this.customContentDao.findAllInSpace(space.getId());
        while (contentIterator.hasNext()) {
            CustomContentEntityObject cceo = contentIterator.next();
            ContentEntityObject container = cceo.getContainer();
            if (container instanceof Spaced && space.equals(((Spaced)((Object)container)).getSpace())) continue;
            this.removeContentEntity(cceo);
        }
    }

    @Override
    public void removeAllPluginContent(String contentModuleKey) {
        Iterator<CustomContentEntityObject> contentWithAttachmentsIterator = this.customContentDao.findAllWithAttachments(contentModuleKey);
        while (contentWithAttachmentsIterator.hasNext()) {
            CustomContentEntityObject entityWithAttachments = contentWithAttachmentsIterator.next();
            this.withAttachmentManager(attachmentManager -> attachmentManager.removeAttachments(new ArrayList<Attachment>(entityWithAttachments.getAttachments())));
        }
        Iterator<CustomContentEntityObject> contentIterator = this.customContentDao.findAll(contentModuleKey);
        while (contentIterator.hasNext()) {
            this.removeContentEntity(contentIterator.next());
        }
    }

    @Override
    public void removeContentEntity(ContentEntityObject obj) {
        Iterator<CustomContentEntityObject> children = this.customContentDao.findAllChildren(obj.getId());
        while (children.hasNext()) {
            CustomContentEntityObject child = children.next();
            this.removeContentEntity(child);
        }
        if (!(obj instanceof CustomContentEntityObject)) {
            throw new IllegalArgumentException("<" + obj + "> is not an instance of CustomContentEntityObject. You need to use the correct Manager for this object");
        }
        CustomContentEntityObject customContentEntityObject = (CustomContentEntityObject)obj;
        ContentEntityObject container = customContentEntityObject.getContainer();
        if (container != null) {
            container.removeCustomContent(customContentEntityObject);
        }
        super.removeContentEntity(obj);
    }

    @Override
    public @NonNull Collection<CustomContentEntityObject> findAllInSpace(Space space) {
        ArrayList<CustomContentEntityObject> content = new ArrayList<CustomContentEntityObject>();
        Iterator<CustomContentEntityObject> it = this.customContentDao.findAllInSpace(space.getId());
        while (it.hasNext()) {
            content.add(it.next());
        }
        return content;
    }

    @VisibleForTesting
    public void setContentTypeManager(ContentTypeManager contentTypeManager) {
        this.contentTypeManager = (ContentTypeManager)Preconditions.checkNotNull((Object)contentTypeManager);
    }

    @VisibleForTesting
    public void setContentFactory(ContentFactory contentFactory) {
        this.contentFactory = (ContentFactory)Preconditions.checkNotNull((Object)contentFactory);
    }

    private void withAttachmentManager(Consumer<AttachmentManager> task) {
        this.eventPublisher.publish(task::accept);
    }
}


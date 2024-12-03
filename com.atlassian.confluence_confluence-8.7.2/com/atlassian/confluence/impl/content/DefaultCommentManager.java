/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.confluence.api.model.Depth
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.service.retention.RetentionFeatureChecker
 *  com.atlassian.confluence.util.collections.GuavaConversionUtil
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.base.Preconditions
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.hibernate.SessionFactory
 */
package com.atlassian.confluence.impl.content;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.api.model.Depth;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.service.retention.RetentionFeatureChecker;
import com.atlassian.confluence.audit.AuditingContext;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SaveContext;
import com.atlassian.confluence.core.persistence.ContentEntityObjectDao;
import com.atlassian.confluence.event.events.content.comment.CommentCreateEvent;
import com.atlassian.confluence.event.events.content.comment.CommentRemoveEvent;
import com.atlassian.confluence.event.events.content.comment.CommentUpdateEvent;
import com.atlassian.confluence.impl.content.DefaultContentEntityManager;
import com.atlassian.confluence.internal.pages.CommentManagerInternal;
import com.atlassian.confluence.internal.pages.persistence.CommentDaoInternal;
import com.atlassian.confluence.internal.relations.RelationManager;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.NewCommentDeduplicator;
import com.atlassian.confluence.setup.settings.CollaborativeEditingHelper;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.collections.GuavaConversionUtil;
import com.atlassian.confluence.xhtml.api.WikiToStorageConverter;
import com.atlassian.event.api.EventPublisher;
import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.hibernate.SessionFactory;

@ParametersAreNonnullByDefault
public class DefaultCommentManager
extends DefaultContentEntityManager
implements CommentManagerInternal {
    private final EventPublisher eventPublisher;
    private final CommentDaoInternal commentDao;

    public DefaultCommentManager(ContentEntityObjectDao<ContentEntityObject> contentEntityObjectDao, SessionFactory sessionFactory, WikiToStorageConverter wikiToStorageConverter, EventPublisher eventPublisher, CommentDaoInternal commentDao, RelationManager relationManager, CollaborativeEditingHelper collaborativeEditingHelper, AuditingContext auditingContext, RetentionFeatureChecker retentionFeatureChecker) {
        super(contentEntityObjectDao, sessionFactory, wikiToStorageConverter, eventPublisher, relationManager, collaborativeEditingHelper, auditingContext, retentionFeatureChecker, DefaultCommentManager.eventFactory());
        this.eventPublisher = (EventPublisher)Preconditions.checkNotNull((Object)eventPublisher);
        this.commentDao = (CommentDaoInternal)Preconditions.checkNotNull((Object)commentDao);
    }

    @Override
    public @Nullable Comment getComment(long id) {
        return this.commentDao.getById(id);
    }

    @Override
    public @NonNull Comment addCommentToPage(AbstractPage page, Comment parent, String content) {
        return this.addCommentToObject(page, parent, content);
    }

    @Override
    public @NonNull Comment addCommentToObject(ContentEntityObject ceo, Comment parent, String content) {
        Comment comment = this.saveComment(ceo, parent, content, null);
        this.publishCreateEvent(comment, null);
        return comment;
    }

    @Override
    public @NonNull Comment addCommentToObject(ContentEntityObject ceo, Comment parent, String content, @Nullable NewCommentDeduplicator commentDeduplicator) {
        Optional<Comment> existingComment;
        if (commentDeduplicator != null && (existingComment = commentDeduplicator.getDuplicateComment(ceo.getComments())).isPresent()) {
            return existingComment.get();
        }
        return this.saveComment(ceo, parent, content, commentDeduplicator);
    }

    private @NonNull Comment saveComment(ContentEntityObject ceo, @Nullable Comment parent, String content, @Nullable NewCommentDeduplicator commentDeduplicator) {
        Comment comment = new Comment();
        comment.setBodyAsString(content);
        this.commentDao.save(comment);
        ceo.addComment(comment);
        if (commentDeduplicator != null) {
            commentDeduplicator.newCommentSaved(comment);
        }
        if (parent != null) {
            parent.addChild(comment);
        }
        this.updateOutgoingLinks(comment);
        return comment;
    }

    private void updateOutgoingLinks(Comment comment) {
        this.eventPublisher.publish(linkManager -> linkManager.updateOutgoingLinks(comment));
    }

    @Override
    public void updateCommentContent(Comment comment, String content) {
        this.saveNewVersion(comment, comment1 -> comment1.setBodyAsString(content));
    }

    @Override
    public void removeCommentFromPage(long id) {
        this.removeCommentFromObject(id);
    }

    @Override
    public void removeCommentFromObject(long id) {
        Comment comment = this.getComment(id);
        if (comment == null) {
            return;
        }
        ContentEntityObject container = comment.getContainer();
        if (container != null) {
            container.removeComment(comment);
        }
        this.removeContentEntity(comment);
    }

    @Override
    public @NonNull Iterator getRecentlyUpdatedComments(Space space, int maxResults) {
        return this.commentDao.getRecentlyUpdatedComments(space.getId(), maxResults);
    }

    @Override
    public @NonNull List<Comment> getPageComments(long pageId, Date since) {
        return this.commentDao.getContainerComments(pageId, since);
    }

    @Override
    public @NonNull List<Comment> getPageComments(long pageId, Date since, String ignoreUsername) {
        return this.commentDao.getContainerComments(pageId, since, ignoreUsername);
    }

    @Override
    public @NonNull PageResponse<Comment> getFilteredContainerComments(long containerId, LimitedRequest pageRequest, Depth depth, Predicate<? super Comment> ... predicates) {
        return this.commentDao.getContainerComments(containerId, pageRequest, depth, GuavaConversionUtil.toGuavaPredicates((Predicate[])predicates));
    }

    @Override
    public @NonNull Map<Searchable, Integer> countComments(Collection<? extends Searchable> searchables) {
        return this.commentDao.countComments(searchables);
    }

    @Override
    public int countComments(Searchable searchable) {
        return this.commentDao.countComments(searchable);
    }

    @Override
    public int countAllCommentVersions() {
        return this.commentDao.countAllCommentVersions();
    }

    @Override
    public @NonNull Map<Long, Integer> countUnresolvedComments(Collection<Long> containerIds) {
        return this.commentDao.countUnresolvedComments(containerIds);
    }

    @Override
    public @NonNull PageResponse<Comment> getFilteredChildren(Comment comment, LimitedRequest pageRequest, Depth depth, Predicate<? super Comment> ... predicates) {
        return this.commentDao.getChildren(comment, pageRequest, depth, GuavaConversionUtil.toGuavaPredicates((Predicate[])predicates));
    }

    private static DefaultContentEntityManager.EventFactory eventFactory() {
        return new DefaultContentEntityManager.EventFactory(){

            @Override
            public Optional<?> newCreateEvent(Object source, ContentEntityObject obj, @Nullable SaveContext saveContext) {
                return Optional.of(new CommentCreateEvent(source, (Comment)obj, saveContext));
            }

            @Override
            public Optional<?> newUpdateEvent(Object source, ContentEntityObject obj, @Nullable ContentEntityObject origObj, @Nullable SaveContext saveContext) {
                return Optional.of(new CommentUpdateEvent(source, (Comment)obj, (Comment)origObj, saveContext));
            }

            @Override
            public Optional<?> newRemoveEvent(Object source, ContentEntityObject obj) {
                return Optional.of(new CommentRemoveEvent(source, (Comment)obj, AuthenticatedUserThreadLocal.get()));
            }
        };
    }

    @Override
    public @NonNull List<Comment> getPageLevelComments(long pageId, Date since) {
        return this.getPageLevelComments(this.commentDao.getContainerComments(pageId, since));
    }

    private @NonNull List<Comment> getPageLevelComments(List<Comment> comments) {
        ArrayList<Comment> pageLevelComments = new ArrayList<Comment>();
        for (Comment comment : comments) {
            if (comment.isInlineComment()) continue;
            pageLevelComments.add(comment);
        }
        return pageLevelComments;
    }
}


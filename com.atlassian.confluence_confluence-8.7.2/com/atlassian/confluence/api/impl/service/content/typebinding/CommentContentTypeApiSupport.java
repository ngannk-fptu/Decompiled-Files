/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.confluence.api.model.Depth
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.Container
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentBody
 *  com.atlassian.confluence.api.model.content.ContentRepresentation
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 *  com.atlassian.confluence.api.model.pagination.PaginationBatch
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult$Builder
 *  com.atlassian.confluence.api.model.validation.ValidationResult
 *  com.atlassian.confluence.api.service.content.ContentBodyConversionService
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.api.service.exceptions.PermissionException
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.api.service.pagination.PaginationService
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.user.User
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.ImmutableListMultimap
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Multimaps
 *  com.google.common.collect.Sets
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.api.impl.service.content.typebinding;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.api.impl.service.content.factory.ContentFactory;
import com.atlassian.confluence.api.impl.service.content.typebinding.CommandValidationHelper;
import com.atlassian.confluence.api.impl.service.content.typebinding.TreeSorter;
import com.atlassian.confluence.api.model.Depth;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.Container;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentBody;
import com.atlassian.confluence.api.model.content.ContentRepresentation;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.api.model.pagination.PaginationBatch;
import com.atlassian.confluence.api.model.validation.SimpleValidationResult;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.service.content.ContentBodyConversionService;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.exceptions.PermissionException;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.api.service.pagination.PaginationService;
import com.atlassian.confluence.content.apisupport.ApiSupportProvider;
import com.atlassian.confluence.content.apisupport.BaseContentTypeApiSupport;
import com.atlassian.confluence.content.apisupport.CommentExtensionsSupport;
import com.atlassian.confluence.content.service.CommentService;
import com.atlassian.confluence.content.service.comment.CreateCommentCommand;
import com.atlassian.confluence.content.service.comment.EditCommentCommand;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.DefaultSaveContext;
import com.atlassian.confluence.core.service.NotAuthorizedException;
import com.atlassian.confluence.core.service.NotValidException;
import com.atlassian.confluence.core.service.ServiceCommand;
import com.atlassian.confluence.event.events.content.comment.CommentCreateEvent;
import com.atlassian.confluence.internal.pages.CommentManagerInternal;
import com.atlassian.confluence.internal.pagination.SubListResponse;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.ContentConvertible;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.user.User;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.checkerframework.checker.nullness.qual.NonNull;

public class CommentContentTypeApiSupport
extends BaseContentTypeApiSupport<Comment> {
    private static final Set<ContentType> EXCLUDED_PARENT_TYPES = Sets.newHashSet();
    private final CommentManagerInternal commentManager;
    private final PaginationService paginationService;
    private final ContentFactory contentFactory;
    private final PermissionManager permissionManager;
    private final ContentEntityManager contentEntityManager;
    private final CommentService commentService;
    private final ContentBodyConversionService contentBodyConversionService;
    private final PluginAccessor pluginAccessor;
    private final EventPublisher eventPublisher;
    @VisibleForTesting
    static final Function<Comment, @NonNull List<Comment>> ANCESTORS_GETTER = input -> {
        ArrayList<Comment> ancestors = new ArrayList<Comment>();
        while (input.getParent() != null) {
            ancestors.add(0, input.getParent());
            input = input.getParent();
        }
        return ancestors;
    };

    public CommentContentTypeApiSupport(CommentManagerInternal commentManager, PaginationService paginationService, ContentFactory contentFactory, PermissionManager permissionManager, ContentEntityManager contentEntityManager, ApiSupportProvider apiSupportProvider, CommentService commentService, ContentBodyConversionService contentBodyConversionService, PluginAccessor pluginAccessor, EventPublisher eventPublisher) {
        super(apiSupportProvider);
        this.commentManager = commentManager;
        this.paginationService = paginationService;
        this.contentFactory = contentFactory;
        this.permissionManager = permissionManager;
        this.contentEntityManager = contentEntityManager;
        this.commentService = commentService;
        this.contentBodyConversionService = contentBodyConversionService;
        this.pluginAccessor = pluginAccessor;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public ContentType getHandledType() {
        return ContentType.COMMENT;
    }

    @Override
    protected PageResponse<Content> getChildrenForThisType(Comment content, LimitedRequest limitedRequest, Expansions expansions, Depth depth) {
        return this.getFilteredChildrenForThisType(content, limitedRequest, expansions, depth, t -> true);
    }

    @Override
    protected PageResponse<Content> getChildrenForThisType(Comment content, LimitedRequest limitedRequest, Expansions expansions, Depth depth, com.google.common.base.Predicate<? super ContentEntityObject> predicate) {
        return this.getChildrenInternal(content, limitedRequest, expansions, depth, predicate);
    }

    @Override
    public boolean supportsChildrenOfType(ContentType otherType) {
        return !otherType.equals((Object)ContentType.BLOG_POST) && !otherType.equals((Object)ContentType.ATTACHMENT) && !otherType.equals((Object)ContentType.PAGE);
    }

    @Override
    protected PageResponse<Content> getChildrenOfThisTypeForOtherType(ContentConvertible otherTypeParent, LimitedRequest limitedRequest, Expansions expansions, Depth depth) {
        return this.getFilteredChildrenOfThisTypeForOtherType(otherTypeParent, limitedRequest, expansions, depth, t -> true);
    }

    @Override
    protected PageResponse<Content> getChildrenOfThisTypeForOtherType(ContentConvertible otherTypeParent, LimitedRequest limitedRequest, Expansions expansions, Depth depth, com.google.common.base.Predicate<? super ContentEntityObject> predicate) {
        ContentEntityObject other = this.contentEntityManager.getById(otherTypeParent.getContentId().asLong());
        return this.getChildrenInternal(other, limitedRequest, expansions, depth, predicate);
    }

    private PageResponse<Content> getChildrenInternal(ContentEntityObject parentCeo, LimitedRequest limitedRequest, Expansions expansions, Depth depth, com.google.common.base.Predicate<? super ContentEntityObject> predicate) {
        if (!this.canView(parentCeo)) {
            return PageResponseImpl.empty((boolean)false);
        }
        PaginationBatch fetchPage = parentCeo instanceof Comment ? nextRequest -> {
            Predicate[] predicateArray = new Predicate[1];
            predicateArray[0] = arg_0 -> ((com.google.common.base.Predicate)predicate).apply(arg_0);
            return this.commentManager.getFilteredChildren((Comment)parentCeo, (LimitedRequest)nextRequest, depth, predicateArray);
        } : nextRequest -> {
            if (depth == Depth.ALL) {
                return this.getCommentDescendantsOfContainer(parentCeo, (LimitedRequest)nextRequest, predicate);
            }
            Predicate[] predicateArray = new Predicate[1];
            predicateArray[0] = arg_0 -> ((com.google.common.base.Predicate)predicate).apply(arg_0);
            return this.commentManager.getFilteredContainerComments(parentCeo.getId(), (LimitedRequest)nextRequest, depth, predicateArray);
        };
        return this.paginationService.performPaginationListRequest(limitedRequest, fetchPage, items -> this.contentFactory.buildFrom(items, expansions));
    }

    @Override
    public boolean supportsChildrenForParentType(ContentType parentType) {
        return !EXCLUDED_PARENT_TYPES.contains(parentType);
    }

    @Override
    public Map<ContentId, Map<String, Object>> getExtensions(Iterable<Comment> contentEntities, Expansions expansions) {
        ImmutableListMultimap groupByContainerClass = Multimaps.index(contentEntities, input -> {
            ContentEntityObject container;
            if (input == null) {
                return null;
            }
            ContentEntityObject contentEntityObject = container = input.isLatestVersion() ? input.getContainer() : ((Comment)input.getLatestVersion()).getContainer();
            if (container == null) {
                return null;
            }
            if (container instanceof ContentConvertible) {
                return ((ContentConvertible)((Object)container)).getContentTypeObject();
            }
            throw new IllegalStateException("Comment must have a content convertible container : " + container.getClass());
        });
        ImmutableMap.Builder result = ImmutableMap.builder();
        List extensionSupports = this.pluginAccessor.getEnabledModulesByClass(CommentExtensionsSupport.class);
        for (CommentExtensionsSupport extensionsSupport : extensionSupports) {
            Iterable<Comment> entities = Collections.emptyList();
            for (ContentType type : extensionsSupport.getCommentContainerType()) {
                entities = Iterables.concat(entities, (Iterable)Iterables.filter((Iterable)groupByContainerClass.get((Object)type), (com.google.common.base.Predicate)Predicates.notNull()));
            }
            result.putAll(extensionsSupport.getExtensions(entities, expansions));
        }
        return result.build();
    }

    private CommentExtensionsSupport getExtensionSupportForContainerType(ContentType type) {
        List extensionsSupports = this.pluginAccessor.getEnabledModulesByClass(CommentExtensionsSupport.class);
        for (CommentExtensionsSupport extensionsSupport : extensionsSupports) {
            if (!Iterables.contains(extensionsSupport.getCommentContainerType(), (Object)type)) continue;
            return extensionsSupport;
        }
        return CommentExtensionsSupport.NULL_OBJECT;
    }

    @Override
    public Class<Comment> getEntityClass() {
        return Comment.class;
    }

    @Override
    public Comment create(Content newContent) {
        CreateCommentCommand command = this.newCreateCommentCommand(newContent);
        this.execute(command);
        Comment comment = command.getComment();
        ContentEntityObject container = comment.getContainer();
        Preconditions.checkArgument((boolean)(container instanceof ContentConvertible));
        ContentConvertible convertible = (ContentConvertible)((Object)container);
        CommentExtensionsSupport support = this.getExtensionSupportForContainerType(convertible.getContentTypeObject());
        support.updateExtensionsOnEntity(comment, newContent.getExtensions());
        this.eventPublisher.publish((Object)new CommentCreateEvent(this, comment, DefaultSaveContext.DEFAULT));
        return comment;
    }

    @Override
    public ValidationResult validateCreate(Content newContent) {
        Container container = newContent.getContainer();
        if (!(container instanceof Content)) {
            return SimpleValidationResult.builder().addError("The container property is required when creating a Comment, and it must be another Content object", new Object[0]).build();
        }
        CreateCommentCommand command = this.newCreateCommentCommand(newContent);
        SimpleValidationResult.Builder validationResultBuilder = CommandValidationHelper.validateCommand(command);
        ValidationResult result = this.getExtensionSupportForContainerType(((Content)container).getType()).validateExtensionsForCreate(newContent.getExtensions(), validationResultBuilder);
        return result;
    }

    @Override
    public ValidationResult validateUpdate(Content updatedContent, Comment existingEntity) {
        ContentEntityObject containerObj = existingEntity.getContainer();
        if (!(containerObj instanceof ContentConvertible)) {
            return SimpleValidationResult.builder().addError("Container must be a ContentConvertible entity" + containerObj, new Object[0]).build();
        }
        ContentConvertible container = (ContentConvertible)((Object)containerObj);
        EditCommentCommand command = this.newEditCommentCommand(updatedContent);
        SimpleValidationResult.Builder validationResultBuilder = CommandValidationHelper.validateCommand(command);
        return this.getExtensionSupportForContainerType(container.getContentTypeObject()).validateExtensionsForUpdate(existingEntity, updatedContent.getExtensions(), validationResultBuilder);
    }

    @Override
    public Comment update(Content contentToUpdate, Comment entity) {
        EditCommentCommand command = this.newEditCommentCommand(contentToUpdate);
        this.execute(command);
        Comment comment = command.getComment();
        ContentEntityObject container = comment.getContainer();
        Preconditions.checkArgument((boolean)(container instanceof ContentConvertible));
        ContentConvertible convertible = (ContentConvertible)((Object)container);
        CommentExtensionsSupport support = this.getExtensionSupportForContainerType(convertible.getContentTypeObject());
        support.updateExtensionsOnEntity(comment, contentToUpdate.getExtensions());
        return comment;
    }

    protected void execute(ServiceCommand command) throws PermissionException, BadRequestException {
        try {
            command.execute();
        }
        catch (NotAuthorizedException e) {
            throw new PermissionException((Throwable)e);
        }
        catch (NotValidException e) {
            throw new BadRequestException((Throwable)e);
        }
    }

    private EditCommentCommand newEditCommentCommand(Content newContent) throws ServiceException {
        long commentId = newContent.getId().asLong();
        Map bodyMap = newContent.getBody();
        if (bodyMap.containsKey(ContentRepresentation.STORAGE)) {
            String content = ((ContentBody)bodyMap.get(ContentRepresentation.STORAGE)).getValue();
            return this.commentService.newEditCommentCommand(commentId, content);
        }
        if (bodyMap.containsKey(ContentRepresentation.EDITOR)) {
            String content = ((ContentBody)bodyMap.get(ContentRepresentation.EDITOR)).getValue();
            return this.commentService.newEditCommentFromEditorCommand(commentId, content);
        }
        if (bodyMap.containsKey(ContentRepresentation.WIKI)) {
            String content = this.contentBodyConversionService.convert((ContentBody)bodyMap.get(ContentRepresentation.WIKI), ContentRepresentation.STORAGE).getValue();
            return this.commentService.newEditCommentCommand(commentId, content);
        }
        throw new BadRequestException("Comment to edit must include EDITOR or STORAGE content body.");
    }

    private CreateCommentCommand newCreateCommentCommand(Content newContent) throws ServiceException {
        long parentId = ((Content)newContent.getContainer()).getId().asLong();
        ContentId parentCommentId = newContent.getParentId();
        Map bodyMap = newContent.getBody();
        if (bodyMap.containsKey(ContentRepresentation.STORAGE)) {
            String content = ((ContentBody)bodyMap.get(ContentRepresentation.STORAGE)).getValue();
            return this.commentService.newCreateCommentCommand(parentId, parentCommentId.asLong(), content, UUID.randomUUID());
        }
        if (bodyMap.containsKey(ContentRepresentation.EDITOR)) {
            String content = ((ContentBody)bodyMap.get(ContentRepresentation.EDITOR)).getValue();
            return this.commentService.newCreateCommentFromEditorCommand(parentId, parentCommentId.asLong(), content, UUID.randomUUID());
        }
        if (bodyMap.containsKey(ContentRepresentation.WIKI)) {
            String content = this.contentBodyConversionService.convert((ContentBody)bodyMap.get(ContentRepresentation.WIKI), ContentRepresentation.STORAGE).getValue();
            return this.commentService.newCreateCommentCommand(parentId, parentCommentId.asLong(), content, UUID.randomUUID());
        }
        throw new BadRequestException("Comment to create must include EDITOR or STORAGE content body.");
    }

    private PageResponse<Comment> getCommentDescendantsOfContainer(ContentEntityObject parentCeo, LimitedRequest nextRequest, com.google.common.base.Predicate<? super ContentEntityObject> predicate) {
        List<Comment> allComments = parentCeo.getComments();
        List<Comment> creationDateSortedComments = this.filterComments(allComments, predicate);
        Comparator commentComparator = (o1, o2) -> {
            int result = o1.getCreationDate().compareTo(o2.getCreationDate());
            if (result == 0) {
                result = o1.getContentId().compareTo(o2.getContentId());
            }
            return result;
        };
        List<Comment> treeSortedComments = TreeSorter.depthFirstPreOrdered(creationDateSortedComments, ANCESTORS_GETTER, commentComparator);
        return SubListResponse.from(treeSortedComments, nextRequest);
    }

    private List<Comment> filterComments(List<Comment> comments, com.google.common.base.Predicate<? super ContentEntityObject> predicate) {
        if (predicate == null) {
            return comments;
        }
        return Collections.unmodifiableList(comments.stream().filter(arg_0 -> predicate.apply(arg_0)).collect(Collectors.toList()));
    }

    private boolean canView(ContentEntityObject entity) {
        return this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, entity);
    }
}


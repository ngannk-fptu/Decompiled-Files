/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentBody
 *  com.atlassian.confluence.api.model.content.ContentRepresentation
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.api.model.people.Person
 *  com.atlassian.confluence.api.service.content.ChildContentService
 *  com.atlassian.confluence.api.service.content.ContentService
 *  com.atlassian.confluence.content.render.xhtml.FormatConverter
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.DefaultSaveContext
 *  com.atlassian.confluence.core.OperationContext
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.pages.CommentManager
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.rest.api.model.ExpansionsParser
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 */
package com.atlassian.confluence.plugins.inlinecomments.service;

import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentBody;
import com.atlassian.confluence.api.model.content.ContentRepresentation;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.api.service.content.ChildContentService;
import com.atlassian.confluence.api.service.content.ContentService;
import com.atlassian.confluence.content.render.xhtml.FormatConverter;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.DefaultSaveContext;
import com.atlassian.confluence.core.OperationContext;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.CommentManager;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.inlinecomments.entities.InlineCommentResult;
import com.atlassian.confluence.plugins.inlinecomments.entities.Reply;
import com.atlassian.confluence.plugins.inlinecomments.events.InlineCommentReplyEvent;
import com.atlassian.confluence.plugins.inlinecomments.helper.InlineCommentDateTimeHelper;
import com.atlassian.confluence.plugins.inlinecomments.helper.InlineCommentPermissionHelper;
import com.atlassian.confluence.plugins.inlinecomments.service.InlineCommentAutoWatchManager;
import com.atlassian.confluence.plugins.inlinecomments.service.InlineCommentPropertyManager;
import com.atlassian.confluence.plugins.inlinecomments.service.ReplyCommentService;
import com.atlassian.confluence.plugins.inlinecomments.utils.InlineCommentUtils;
import com.atlassian.confluence.rest.api.model.ExpansionsParser;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.renderer.RenderContext;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReplyCommentServiceImpl
implements ReplyCommentService {
    private final ChildContentService childContentService;
    private final ContentService contentService;
    private final TransactionTemplate transactionTemplate;
    private final InlineCommentPropertyManager propertyManager;
    private final CommentManager commentManager;
    private final InlineCommentPermissionHelper permissionHelper;
    private final InlineCommentAutoWatchManager autoWatchManager;
    private final FormatConverter formatConverter;
    private final InlineCommentDateTimeHelper dateTimeHelper;
    private final PageManager pageManager;
    private final EventPublisher eventPublisher;

    public ReplyCommentServiceImpl(ContentService contentService, ChildContentService childContentService, TransactionTemplate transactionTemplate, InlineCommentPropertyManager propertyManager, CommentManager commentManager, InlineCommentPermissionHelper permissionHelper, InlineCommentAutoWatchManager autoWatchManager, FormatConverter formatConverter, InlineCommentDateTimeHelper dateTimeHelper, PageManager pageManager, EventPublisher eventPublisher) {
        this.contentService = contentService;
        this.childContentService = childContentService;
        this.transactionTemplate = transactionTemplate;
        this.propertyManager = propertyManager;
        this.commentManager = commentManager;
        this.permissionHelper = permissionHelper;
        this.autoWatchManager = autoWatchManager;
        this.formatConverter = formatConverter;
        this.dateTimeHelper = dateTimeHelper;
        this.pageManager = pageManager;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public InlineCommentResult<List<Reply>> getReplies(long commentId) {
        try {
            PageResponse pageResponse = this.childContentService.findContent(InlineCommentUtils.buildContentId(ContentType.COMMENT, commentId), ExpansionsParser.parse((String)"history,body.view,version")).fetchMany(ContentType.COMMENT, (PageRequest)new SimplePageRequest(0, 100));
            List contents = pageResponse.getResults();
            if (contents.isEmpty()) {
                return new InlineCommentResult<List<Reply>>(InlineCommentResult.Status.SUCCESS, new ArrayList());
            }
            Comment firstComment = this.commentManager.getComment(((Content)contents.get(0)).getId().asLong());
            String pageUrl = GeneralUtil.customGetPageUrl((AbstractPage)((AbstractPage)firstComment.getContainer()));
            List replies = contents.stream().map(content -> {
                Reply reply = new Reply();
                if (content != null) {
                    reply.setId(content.getId().asLong());
                    reply.setBody(((ContentBody)content.getBody().get(ContentRepresentation.VIEW)).getValue());
                    Person person = content.getHistory().getCreatedBy();
                    reply.setAuthorInformation(person);
                    reply.setCommentId(commentId);
                    reply.setLastModificationDate(this.dateTimeHelper.formatFriendlyDate(content.getVersion().getWhen().getMillis()));
                    reply.setCommentDateUrl(InlineCommentUtils.getCommentDateUrl(pageUrl, reply.getId()));
                }
                return reply;
            }).collect(Collectors.toList());
            this.permissionHelper.setupPermission(replies, firstComment);
            return new InlineCommentResult<List<Reply>>(InlineCommentResult.Status.SUCCESS, replies);
        }
        catch (Exception e) {
            return InlineCommentResult.getResultFromServiceException(e);
        }
    }

    @Override
    public InlineCommentResult<Reply> createReply(Reply replyComment, Long containerId) {
        if (replyComment == null || !replyComment.isCreationBeanValid(containerId)) {
            return new InlineCommentResult<Reply>(InlineCommentResult.Status.REQUEST_DATA_INCORRECT);
        }
        if (!this.permissionHelper.hasCreateCommentPermission(containerId)) {
            return new InlineCommentResult<Reply>(InlineCommentResult.Status.NOT_PERMITTED);
        }
        try {
            Comment comment = new Comment();
            AbstractPage pageContainer = (AbstractPage)this.pageManager.getById(containerId.longValue());
            comment.setBodyAsString(this.formatConverter.convertToStorageFormat(replyComment.getBody(), (RenderContext)pageContainer.toPageContext()));
            comment.setInlineComment(true);
            pageContainer.addComment(comment);
            long parentCommentId = replyComment.getCommentId();
            if (parentCommentId != 0L) {
                this.commentManager.getComment(parentCommentId).addChild(comment);
            }
            Content savedComment = (Content)this.transactionTemplate.execute(() -> {
                this.commentManager.saveContentEntity((ContentEntityObject)comment, DefaultSaveContext.SUPPRESS_NOTIFICATIONS);
                this.autoWatchManager.watchContentRespectingUserAutoWatchPreference(containerId);
                this.eventPublisher.publish((Object)new InlineCommentReplyEvent(this, comment, (OperationContext<?>)DefaultSaveContext.DEFAULT));
                return (Content)this.contentService.find(ExpansionsParser.parse((String)"history,version")).withId(ContentId.of((ContentType)ContentType.COMMENT, (long)comment.getId())).fetchOneOrNull();
            });
            replyComment.setId(savedComment.getId().asLong());
            replyComment.setAuthorInformation(savedComment.getHistory().getCreatedBy());
            replyComment.setBody(this.formatConverter.convertToEditorFormat(comment.getBodyAsString(), (RenderContext)pageContainer.toPageContext()));
            replyComment.setLastModificationDate(this.dateTimeHelper.formatFriendlyDate(savedComment.getVersion().getWhen().getMillis()));
            String pageUrl = GeneralUtil.customGetPageUrl((AbstractPage)pageContainer);
            replyComment.setCommentDateUrl(InlineCommentUtils.getCommentDateUrl(pageUrl, replyComment.getId()));
            return new InlineCommentResult<Reply>(InlineCommentResult.Status.SUCCESS, replyComment);
        }
        catch (Exception e) {
            return InlineCommentResult.getResultFromServiceException(e);
        }
    }

    @Override
    public InlineCommentResult deleteReply(Long replyId) {
        Comment comment = this.commentManager.getComment(replyId.longValue());
        if (comment == null) {
            return new InlineCommentResult(InlineCommentResult.Status.REQUEST_DATA_INCORRECT);
        }
        if (!this.permissionHelper.hasDeleteCommentPermission(comment)) {
            return new InlineCommentResult(InlineCommentResult.Status.NOT_PERMITTED);
        }
        try {
            return (InlineCommentResult)this.transactionTemplate.execute(() -> {
                Content reply = Content.builder((ContentType)ContentType.COMMENT, (long)replyId).build();
                this.contentService.delete(reply);
                return new InlineCommentResult(InlineCommentResult.Status.SUCCESS);
            });
        }
        catch (Exception e) {
            return InlineCommentResult.getResultFromServiceException(e);
        }
    }

    @Override
    public InlineCommentResult<Reply> updateReply(Reply reply) {
        Comment comment = this.commentManager.getComment(reply.getId());
        if (comment == null) {
            return new InlineCommentResult<Reply>(InlineCommentResult.Status.REQUEST_DATA_INCORRECT);
        }
        if (!this.permissionHelper.hasEditCommentPermission(comment)) {
            return new InlineCommentResult<Reply>(InlineCommentResult.Status.NOT_PERMITTED);
        }
        try {
            this.commentManager.updateCommentContent(comment, this.formatConverter.convertToStorageFormat(reply.getBody(), (RenderContext)comment.toPageContext()));
            reply.setBody(this.formatConverter.convertToViewFormat(comment.getBodyAsString(), (RenderContext)comment.toPageContext()));
            reply.setLastModificationDate(this.dateTimeHelper.formatFriendlyDate(comment.getLastModificationDate()));
            reply.setCommentDateUrl(comment.getUrlPath());
            return new InlineCommentResult<Reply>(InlineCommentResult.Status.SUCCESS, reply);
        }
        catch (Exception e) {
            return InlineCommentResult.getResultFromServiceException(e);
        }
    }
}


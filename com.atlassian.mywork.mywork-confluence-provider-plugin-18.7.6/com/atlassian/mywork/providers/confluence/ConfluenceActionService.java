/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.service.CommentService
 *  com.atlassian.confluence.content.service.comment.CreateCommentCommand
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.service.ValidationError
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.like.LikeManager
 *  com.atlassian.confluence.mail.notification.Notification
 *  com.atlassian.confluence.mail.notification.NotificationManager
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.pages.PageUpdateTrigger
 *  com.atlassian.confluence.plugins.tasklist.TaskStatus
 *  com.atlassian.confluence.plugins.tasklist.service.InlineTaskService
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.confluence.rpc.NotPermittedException
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.confluence.xhtml.api.XhtmlContent
 *  com.atlassian.mywork.service.ActionResult
 *  com.atlassian.mywork.service.ActionService
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.user.User
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  org.codehaus.jackson.JsonNode
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.mywork.providers.confluence;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.service.CommentService;
import com.atlassian.confluence.content.service.comment.CreateCommentCommand;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.service.ValidationError;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.like.LikeManager;
import com.atlassian.confluence.mail.notification.Notification;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.PageUpdateTrigger;
import com.atlassian.confluence.plugins.tasklist.TaskStatus;
import com.atlassian.confluence.plugins.tasklist.service.InlineTaskService;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.rpc.NotPermittedException;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.mywork.providers.confluence.ConfluenceRegistrationProvider;
import com.atlassian.mywork.service.ActionResult;
import com.atlassian.mywork.service.ActionService;
import com.atlassian.renderer.RenderContext;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.user.User;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Map;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceActionService
implements ActionService {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceActionService.class);
    private final ContentEntityManager contentEntityManager;
    private final TransactionTemplate transactionTemplate;
    private final LikeManager likeManager;
    private final CommentService commentService;
    private final UserAccessor userAccessor;
    private final NotificationManager notificationManager;
    private final I18NBeanFactory i18NBeanFactory;
    private final LocaleManager localeManager;
    private final XhtmlContent xhtmlContent;
    private final InlineTaskService inlineTaskService;
    private final ActionExecutor likeAction = new ActionExecutor(){

        @Override
        public ActionResult execute(String username, JsonNode action) {
            return (ActionResult)ConfluenceActionService.this.transactionTemplate.execute(() -> {
                ContentEntityObject content = ConfluenceActionService.this.getContent(action);
                if (content == null) {
                    log.info("Could not find content for action \"" + action + "\" with content id " + ConfluenceActionService.this.getContentId(action) + ".");
                    return ActionResult.FAILED;
                }
                ConfluenceActionService.this.likeManager.addLike(content, (User)ConfluenceActionService.this.userAccessor.getUserByName(username));
                Object url = content.getUrlPath();
                if (!(content instanceof Comment)) {
                    url = (String)url + "#like-section";
                }
                return new ActionResult(true, (String)url);
            });
        }
    };
    private final ActionExecutor unlikeAction = new ActionExecutor(){

        @Override
        public ActionResult execute(String username, JsonNode action) {
            return (ActionResult)ConfluenceActionService.this.transactionTemplate.execute(() -> {
                ContentEntityObject content = ConfluenceActionService.this.getContent(action);
                if (content == null) {
                    log.info("Could not find content for action \"" + action + "\" with content id " + ConfluenceActionService.this.getContentId(action) + ".");
                    return ActionResult.FAILED;
                }
                ConfluenceActionService.this.likeManager.removeLike(content, (User)ConfluenceActionService.this.userAccessor.getUserByName(username));
                return ActionResult.SUCCESS;
            });
        }
    };
    private final ActionExecutor commentAction = new ActionExecutor(){

        @Override
        public ActionResult execute(String username, JsonNode action) {
            CreateCommentCommand command;
            String content = action.path("comment").getTextValue();
            long parentCommentId = ConfluenceActionService.this.isObject(action) ? 0L : ConfluenceActionService.this.getContentId(action);
            long pageId = ConfluenceActionService.this.getPageId(action);
            parentCommentId = pageId == parentCommentId ? 0L : parentCommentId;
            parentCommentId = ConfluenceActionService.this.correctParentCommentIdForInlineComment(parentCommentId);
            ArrayList conversionErrors = new ArrayList();
            String newContent = ConfluenceActionService.this.xhtmlContent.convertWikiToStorage(content, (ConversionContext)new DefaultConversionContext((RenderContext)new PageContext()), conversionErrors);
            if (!conversionErrors.isEmpty()) {
                log.info("Invalid markup: " + conversionErrors.toString());
            }
            if (!(command = ConfluenceActionService.this.commentService.newCreateCommentCommand(ConfluenceActionService.this.getPageId(action), parentCommentId, newContent, null)).isAuthorized()) {
                return new ActionResult(false, "", "permission.denied");
            }
            if (!command.isValid()) {
                ConfluenceUser user = AuthenticatedUserThreadLocal.get();
                ArrayList errors = Lists.newArrayListWithExpectedSize((int)command.getValidationErrors().size());
                for (ValidationError error : command.getValidationErrors()) {
                    errors.add(ConfluenceActionService.this.i18NBeanFactory.getI18NBean(ConfluenceActionService.this.localeManager.getLocale((User)user)).getText(error.getMessageKey(), error.getArgs()));
                }
                log.info("Failed to create new comment: " + errors);
                return ActionResult.FAILED;
            }
            command.execute();
            Comment comment = command.getComment();
            if (comment.getParent() != null && comment.getParent().isInlineComment()) {
                comment.getProperties().setStringProperty("inline-comment", Boolean.TRUE.toString());
            }
            return new ActionResult(true, comment.getUrlPath());
        }
    };
    private final ActionExecutor watchAction = new ActionExecutor(){

        @Override
        public ActionResult execute(String username, JsonNode action) {
            ContentEntityObject content;
            ConfluenceUser user = ConfluenceActionService.this.userAccessor.getUserByName(username);
            if (ConfluenceActionService.this.userAccessor.getConfluenceUserPreferences((User)user).isWatchingOwnContent() && (content = ConfluenceActionService.this.contentEntityManager.getById(ConfluenceActionService.this.getPageId(action))) != null) {
                ConfluenceActionService.this.notificationManager.addContentNotification((User)user, content);
            }
            return ActionResult.SUCCESS;
        }
    };
    private final ActionExecutor unwatchAction = new ActionExecutor(){

        @Override
        public ActionResult execute(String username, JsonNode action) {
            Notification notification;
            ContentEntityObject content;
            ConfluenceUser user = ConfluenceActionService.this.userAccessor.getUserByName(username);
            if (ConfluenceActionService.this.userAccessor.getConfluenceUserPreferences((User)user).isWatchingOwnContent() && (content = ConfluenceActionService.this.contentEntityManager.getById(ConfluenceActionService.this.getPageId(action))) != null && (notification = ConfluenceActionService.this.notificationManager.getNotificationByUserAndContent((User)user, content)) != null) {
                ConfluenceActionService.this.notificationManager.removeNotification(notification);
            }
            return ActionResult.SUCCESS;
        }
    };
    private final ActionExecutor commentAndWatchAction = (username, action) -> {
        ActionResult commentResult = this.commentAction.execute(username, action);
        try {
            this.watchAction.execute(username, action);
        }
        catch (Exception e) {
            log.info("Failed to start watching a page", (Throwable)e);
        }
        return commentResult;
    };
    private final ActionExecutor completeTaskAction = new ActionExecutor(){

        @Override
        public ActionResult execute(String username, JsonNode action) {
            ContentEntityObject content = ConfluenceActionService.this.getContent(action);
            String task = action.get("metadata").get("taskId").asText();
            TaskStatus checked = action.get("status").getTextValue().equals("DONE") ? TaskStatus.CHECKED : TaskStatus.UNCHECKED;
            return (ActionResult)ConfluenceActionService.this.transactionTemplate.execute(() -> {
                try {
                    ConfluenceActionService.this.inlineTaskService.setTaskStatus(content, task, checked, PageUpdateTrigger.PERSONAL_TASKLIST);
                    return ActionResult.SUCCESS;
                }
                catch (NotPermittedException e) {
                    log.debug("Not permitted to update: " + task, (Throwable)e);
                    return ActionResult.FAILED;
                }
            });
        }
    };
    private final Map<String, ActionExecutor> actionExecutors = ImmutableMap.builder().put((Object)"com.atlassian.mywork.providers.confluence.page.comment", (Object)this.commentAndWatchAction).put((Object)"com.atlassian.mywork.providers.confluence.blog.comment", (Object)this.commentAndWatchAction).put((Object)"com.atlassian.mywork.providers.confluence.comment.comment", (Object)this.commentAndWatchAction).put((Object)"com.atlassian.mywork.providers.confluence.page.reply", (Object)this.commentAndWatchAction).put((Object)"com.atlassian.mywork.providers.confluence.blog.reply", (Object)this.commentAndWatchAction).put((Object)"com.atlassian.mywork.providers.confluence.comment.reply", (Object)this.commentAndWatchAction).put((Object)"com.atlassian.mywork.providers.confluence.page.watch", (Object)this.watchAction).put((Object)"com.atlassian.mywork.providers.confluence.blog.watch", (Object)this.watchAction).put((Object)"com.atlassian.mywork.providers.confluence.page.unwatch", (Object)this.unwatchAction).put((Object)"com.atlassian.mywork.providers.confluence.blog.unwatch", (Object)this.unwatchAction).put((Object)"com.atlassian.mywork.providers.confluence.page.like", (Object)this.likeAction).put((Object)"com.atlassian.mywork.providers.confluence.blog.like", (Object)this.likeAction).put((Object)"com.atlassian.mywork.providers.confluence.comment.like", (Object)this.likeAction).put((Object)"com.atlassian.mywork.providers.confluence.page.likeComment", (Object)this.likeAction).put((Object)"com.atlassian.mywork.providers.confluence.blog.likeComment", (Object)this.likeAction).put((Object)"com.atlassian.mywork.providers.confluence.comment.likeComment", (Object)this.likeAction).put((Object)"com.atlassian.mywork.providers.confluence.page.unlike", (Object)this.unlikeAction).put((Object)"com.atlassian.mywork.providers.confluence.blog.unlike", (Object)this.unlikeAction).put((Object)"com.atlassian.mywork.providers.confluence.comment.unlike", (Object)this.unlikeAction).put((Object)"com.atlassian.mywork.providers.confluence.page.unlikeComment", (Object)this.unlikeAction).put((Object)"com.atlassian.mywork.providers.confluence.blog.unlikeComment", (Object)this.unlikeAction).put((Object)"com.atlassian.mywork.providers.confluence.comment.unlikeComment", (Object)this.unlikeAction).put((Object)"com.atlassian.mywork.providers.confluence.inline-task", (Object)this.completeTaskAction).build();

    public ConfluenceActionService(ContentEntityManager contentEntityManager, TransactionTemplate transactionTemplate, LikeManager likeManager, CommentService commentService, UserAccessor userAccessor, NotificationManager notificationManager, I18NBeanFactory i18NBeanFactory, LocaleManager localeManager, XhtmlContent xhtmlContent, InlineTaskService inlineTaskService) {
        this.contentEntityManager = contentEntityManager;
        this.transactionTemplate = transactionTemplate;
        this.likeManager = likeManager;
        this.commentService = commentService;
        this.userAccessor = userAccessor;
        this.notificationManager = notificationManager;
        this.i18NBeanFactory = i18NBeanFactory;
        this.localeManager = localeManager;
        this.xhtmlContent = xhtmlContent;
        this.inlineTaskService = inlineTaskService;
    }

    public String getApplication() {
        return new ConfluenceRegistrationProvider().getApplication();
    }

    public ActionResult execute(String username, JsonNode action) {
        String qualifiedAction = action.path("qualifiedAction").getTextValue();
        ActionExecutor executor = this.actionExecutors.get(qualifiedAction);
        return executor.execute(username, action);
    }

    private ContentEntityObject getContent(JsonNode action) {
        return this.contentEntityManager.getById(this.getContentId(action));
    }

    private long getContentId(JsonNode action) {
        long contentId = this.isObject(action) ? action.path("metadata").path("itemContentId").getLongValue() : action.path("metadata").path("contentId").getLongValue();
        return contentId;
    }

    private long getPageId(JsonNode action) {
        return action.path("metadata").path("pageId").getLongValue();
    }

    private boolean isObject(JsonNode action) {
        return "object".equals(action.path("target").getTextValue());
    }

    long correctParentCommentIdForInlineComment(long parentCommentId) {
        if (parentCommentId == 0L) {
            return parentCommentId;
        }
        ContentEntityObject ceo = this.contentEntityManager.getById(parentCommentId);
        if (ceo instanceof Comment) {
            Comment parentComment = (Comment)ceo;
            Comment topLevelComment = parentComment.getParent();
            if (parentComment.isInlineComment() && topLevelComment != null && topLevelComment.isInlineComment()) {
                parentCommentId = topLevelComment.getId();
            }
        }
        return parentCommentId;
    }

    private static interface ActionExecutor {
        public ActionResult execute(String var1, JsonNode var2);
    }
}


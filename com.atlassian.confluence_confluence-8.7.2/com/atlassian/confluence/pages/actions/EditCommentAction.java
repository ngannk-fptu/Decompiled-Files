/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.content.service.CommentService;
import com.atlassian.confluence.content.service.comment.EditCommentCommand;
import com.atlassian.confluence.core.actions.ServiceBackedActionHelper;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.actions.AbstractPreviewPageAction;
import com.atlassian.confluence.pages.actions.AddCommentAction;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.security.CaptchaAware;
import com.atlassian.confluence.security.CaptchaManager;
import java.util.List;

public class EditCommentAction
extends AbstractPreviewPageAction
implements CaptchaAware {
    private CommentService commentService;
    private CaptchaManager captchaManager;
    private NotificationManager notificationManager;
    private long commentId;
    private boolean watchPageAfterComment;

    @Override
    public void validate() {
        if (!this.getEditCommentCommand().isValid()) {
            new ServiceBackedActionHelper(this.getEditCommentCommand()).addValidationErrors(this);
        }
    }

    @Override
    public String doDefault() throws Exception {
        return "input";
    }

    public String doEdit() throws Exception {
        this.updateXHtmlContent();
        this.getEditCommentCommand().execute();
        if (this.watchPageAfterComment) {
            this.notificationManager.addContentNotification(this.getAuthenticatedUser(), this.getPage());
        }
        return "success";
    }

    public Comment getComment() {
        return this.getEditCommentCommand().getComment();
    }

    @Override
    public String getWysiwygContent() {
        if (this.wysiwygContent == null) {
            Comment comment = this.getComment();
            this.wysiwygContent = comment == null ? "" : this.getEditorFormattedContent(comment.getBodyAsString());
        }
        return this.wysiwygContent;
    }

    @Override
    public boolean isPermitted() {
        return this.getEditCommentCommand().isAuthorized();
    }

    public long getCommentId() {
        return this.commentId;
    }

    public void setCommentId(long commentId) {
        this.commentId = commentId;
    }

    @Override
    public List<Page> getPermittedChildren() {
        return super.getPermittedChildren();
    }

    public void setCommentService(CommentService commentService) {
        this.commentService = commentService;
    }

    public void setNotificationManager(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    public void setWatchPageAfterComment(boolean watchPageAfterComment) {
        this.watchPageAfterComment = watchPageAfterComment;
    }

    private EditCommentCommand getEditCommentCommand() {
        return this.commentService.newEditCommentFromEditorCommand(this.getCommentId(), this.wysiwygContent);
    }

    public CaptchaManager getCaptchaManager() {
        return this.captchaManager;
    }

    public void setCaptchaManager(CaptchaManager captchaManager) {
        this.captchaManager = captchaManager;
    }

    public WebInterfaceContext getWebInterfaceContext(Comment comment) {
        return AddCommentAction.createWebInterfaceContextWithComment(super.getWebInterfaceContext(), comment);
    }
}


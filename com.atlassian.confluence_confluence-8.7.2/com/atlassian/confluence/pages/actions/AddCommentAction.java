/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.content.service.CommentService;
import com.atlassian.confluence.content.service.comment.CreateCommentCommand;
import com.atlassian.confluence.core.actions.ServiceBackedActionHelper;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.actions.AbstractPreviewPageAction;
import com.atlassian.confluence.plugin.descriptor.web.DefaultWebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.security.CaptchaAware;
import com.atlassian.confluence.security.CaptchaManager;
import com.atlassian.confluence.util.HtmlUtil;
import java.util.List;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;

public class AddCommentAction
extends AbstractPreviewPageAction
implements CaptchaAware {
    private CaptchaManager captchaManager;
    private CommentService commentService;
    private NotificationManager notificationManager;
    private CreateCommentCommand createCommentCommand;
    private long parentId;
    private boolean watchPageAfterComment;
    private String textContent;

    @Override
    public void validate() {
        if (!this.getAddCommentCommand().isValid()) {
            new ServiceBackedActionHelper(this.getAddCommentCommand()).addValidationErrors(this);
        }
    }

    public String execute() throws Exception {
        if (StringUtils.isNotEmpty((CharSequence)this.back)) {
            return "input";
        }
        if (StringUtils.isNotEmpty((CharSequence)this.preview)) {
            this.updateXHtmlContent();
            this.setInPreview(true);
            return "preview";
        }
        this.updateXHtmlContent();
        this.getAddCommentCommand().execute();
        if (this.watchPageAfterComment) {
            this.notificationManager.addContentNotification(this.getAuthenticatedUser(), this.getPage());
        }
        return "success";
    }

    @Override
    public String getWysiwygContent() {
        if (this.wysiwygContent == null) {
            Comment comment = this.getComment();
            this.wysiwygContent = comment == null ? "" : this.getEditorFormattedContent(comment.getBodyAsString());
        }
        return this.wysiwygContent;
    }

    public Comment getComment() {
        return this.getAddCommentCommand().getComment();
    }

    @Override
    public boolean isPermitted() {
        return this.getAddCommentCommand().isAuthorized();
    }

    public List getPermittedChildren() {
        return super.getPermittedChildren();
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public long getParentId() {
        return this.parentId;
    }

    public String getTextContent() {
        return this.textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
        if (StringUtils.isNotBlank((CharSequence)textContent)) {
            this.setWysiwygContent(HtmlUtil.htmlEncode(textContent));
        }
    }

    @Override
    public String getCancelResult() {
        if (this.parentId != 0L) {
            return "cancel-to-parent";
        }
        return super.getCancelResult();
    }

    private CreateCommentCommand getAddCommentCommand() {
        if (this.createCommentCommand == null) {
            this.createCommentCommand = this.commentService.newCreateCommentFromEditorCommand(this.getPageId(), this.parentId, this.wysiwygContent, UUID.randomUUID());
        }
        return this.createCommentCommand;
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

    public CaptchaManager getCaptchaManager() {
        return this.captchaManager;
    }

    public void setCaptchaManager(CaptchaManager captchaManager) {
        this.captchaManager = captchaManager;
    }

    public WebInterfaceContext getWebInterfaceContext(Comment comment) {
        return AddCommentAction.createWebInterfaceContextWithComment(super.getWebInterfaceContext(), comment);
    }

    public static WebInterfaceContext createWebInterfaceContextWithComment(WebInterfaceContext context, Comment comment) {
        DefaultWebInterfaceContext webInterfaceContext = DefaultWebInterfaceContext.copyOf(context);
        webInterfaceContext.setComment(comment);
        return webInterfaceContext;
    }
}


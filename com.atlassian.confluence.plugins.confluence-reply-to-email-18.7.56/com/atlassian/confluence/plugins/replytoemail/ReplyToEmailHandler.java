/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceSidManager
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.pages.CommentManager
 *  com.atlassian.confluence.plugins.emailgateway.api.AttachmentConverterService
 *  com.atlassian.confluence.plugins.emailgateway.api.CreateCommentAttachmentHandleException
 *  com.atlassian.confluence.plugins.emailgateway.api.EmailContentParser
 *  com.atlassian.confluence.plugins.emailgateway.api.EmailGatewaySettingsManager
 *  com.atlassian.confluence.plugins.emailgateway.api.EmailHandler
 *  com.atlassian.confluence.plugins.emailgateway.api.InboundMailServerManager
 *  com.atlassian.confluence.plugins.emailgateway.api.NoMatchingUserToCommentException
 *  com.atlassian.confluence.plugins.emailgateway.api.NotificationEmailHelper
 *  com.atlassian.confluence.plugins.emailgateway.api.ReceivedEmail
 *  com.atlassian.confluence.plugins.emailgateway.api.SerializableAttachment
 *  com.atlassian.confluence.plugins.emailgateway.api.UsersByEmailService
 *  com.atlassian.confluence.plugins.emailgateway.api.analytics.ReplyToCommentByEmailAnalytics$CreateComment
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.spaces.Spaced
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.user.EntityException
 *  com.atlassian.user.User
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.plugins.replytoemail;

import com.atlassian.confluence.core.ConfluenceSidManager;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.CommentManager;
import com.atlassian.confluence.plugins.emailgateway.api.AttachmentConverterService;
import com.atlassian.confluence.plugins.emailgateway.api.CreateCommentAttachmentHandleException;
import com.atlassian.confluence.plugins.emailgateway.api.EmailContentParser;
import com.atlassian.confluence.plugins.emailgateway.api.EmailGatewaySettingsManager;
import com.atlassian.confluence.plugins.emailgateway.api.EmailHandler;
import com.atlassian.confluence.plugins.emailgateway.api.InboundMailServerManager;
import com.atlassian.confluence.plugins.emailgateway.api.NoMatchingUserToCommentException;
import com.atlassian.confluence.plugins.emailgateway.api.NotificationEmailHelper;
import com.atlassian.confluence.plugins.emailgateway.api.ReceivedEmail;
import com.atlassian.confluence.plugins.emailgateway.api.SerializableAttachment;
import com.atlassian.confluence.plugins.emailgateway.api.UsersByEmailService;
import com.atlassian.confluence.plugins.emailgateway.api.analytics.ReplyToCommentByEmailAnalytics;
import com.atlassian.confluence.plugins.replytoemail.EmailQuoteRegex;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.Spaced;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.user.EntityException;
import com.atlassian.user.User;
import java.util.List;
import java.util.regex.Matcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

public class ReplyToEmailHandler
implements EmailHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ReplyToEmailHandler.class);
    private final ContentEntityManager contentEntityManager;
    private final CommentManager commentManager;
    private final TransactionTemplate transactionTemplate;
    private final UsersByEmailService usersByEmailService;
    private final EmailContentParser emailContentParser;
    private final AttachmentConverterService attachmentConverterService;
    private final NotificationEmailHelper notificationEmailHelper;
    private final EmailGatewaySettingsManager emailGatewaySettingsManager;
    private final SettingsManager settingsManager;
    private final EventPublisher eventPublisher;

    public ReplyToEmailHandler(@Qualifier(value="contentEntityManager") ContentEntityManager contentEntityManager, CommentManager commentManager, TransactionTemplate transactionTemplate, UsersByEmailService usersByEmailService, EmailContentParser emailContentParser, AttachmentConverterService attachmentConverterService, InboundMailServerManager inboundMailServerManager, EmailGatewaySettingsManager emailGatewaySettingsManager, SettingsManager settingsManager, EventPublisher eventPublisher, ConfluenceSidManager confluenceSidManager) {
        this.contentEntityManager = contentEntityManager;
        this.commentManager = commentManager;
        this.transactionTemplate = transactionTemplate;
        this.usersByEmailService = usersByEmailService;
        this.emailContentParser = emailContentParser;
        this.attachmentConverterService = attachmentConverterService;
        this.notificationEmailHelper = new NotificationEmailHelper(confluenceSidManager, inboundMailServerManager);
        this.emailGatewaySettingsManager = emailGatewaySettingsManager;
        this.settingsManager = settingsManager;
        this.eventPublisher = eventPublisher;
    }

    public boolean handle(ReceivedEmail email) {
        if (!this.emailGatewaySettingsManager.isAllowToCreateCommentByEmail()) {
            return false;
        }
        return (Boolean)this.transactionTemplate.execute(() -> this.handleEmail(email));
    }

    private boolean handleEmail(ReceivedEmail email) {
        LOG.debug("Checking inbound message from {} to see if it's a reply to an earlier notification", (Object)email.getSender());
        ContentEntityObject target = this.notificationEmailHelper.extractTargetContentFromEmailReply(email.getHeaders().getAllHeaders(), this::findContentObject);
        if (target != null) {
            String userAddress = email.getSender().getAddress();
            try {
                User user = this.usersByEmailService.getUniqueUserByEmail(userAddress);
                if (user == null) {
                    throw new EntityException("No user with email address: " + userAddress);
                }
            }
            catch (EntityException e) {
                throw new NoMatchingUserToCommentException(userAddress);
            }
            for (SerializableAttachment attachment : email.getAttachments()) {
                if ((long)attachment.getContents().length <= this.settingsManager.getGlobalSettings().getAttachmentMaxSize()) continue;
                throw new CreateCommentAttachmentHandleException("Cannot create comment due to big attachment");
            }
            try {
                LOG.debug("Adding email as a comment to {} {}", (Object)target.getClass().getSimpleName(), (Object)target.getId());
                this.attachEmailAsComment(email, target);
                LOG.info("Comment added successfully to {} {}", (Object)target.getClass().getSimpleName(), (Object)target.getId());
            }
            catch (EntityException e) {
                LOG.error("Failed to persist email reply comment", (Throwable)e);
            }
            return true;
        }
        LOG.debug("Email was not a reply to notification, skipping");
        return false;
    }

    private void attachEmailAsComment(ReceivedEmail email, ContentEntityObject target) throws EntityException {
        ContentEntityObject targetOwner;
        Comment parentComment;
        User user = this.usersByEmailService.getUniqueUserByEmail(email.getSender());
        if (user == null) {
            LOG.debug("Received email from address which does not correspond to a Confluence user. Ignoring it.");
            return;
        }
        if (target instanceof Comment) {
            parentComment = (Comment)target;
            targetOwner = parentComment.getContainer();
        } else {
            parentComment = null;
            targetOwner = target;
        }
        String content = this.prepareCommentContent(email);
        List attachments = email.getAttachments();
        this.attachmentConverterService.attachTo(targetOwner, attachments);
        this.persistNewComment(user, parentComment, targetOwner, content);
    }

    private String prepareCommentContent(ReceivedEmail email) {
        switch (email.getBodyType()) {
            case HTML: {
                String removeQuotedContent = this.removeQuotedResponseSection(email.getBodyContentAsString());
                ReceivedEmail processingEmail = new ReceivedEmail(email.getSender(), email.getRecipientAddress(), email.getParticipants(), email.getHeaders(), email.getSubject(), email.getBodyType(), removeQuotedContent, email.getAttachments(), email.getContext());
                return this.emailContentParser.parseContent(processingEmail);
            }
        }
        return this.removeQuotedResponseSection(this.emailContentParser.parseContent(email));
    }

    private String removeQuotedResponseSection(String htmlContent) {
        Matcher matcher = EmailQuoteRegex.REPLY_EMAIL_HEADING.matcher(htmlContent);
        if (matcher.find()) {
            int quoteStartIndex = matcher.start();
            String strippedContent = htmlContent.substring(0, quoteStartIndex);
            return strippedContent;
        }
        LOG.warn("Failed to clean HTML, returning as-is");
        return htmlContent;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void persistNewComment(User user, Comment parentComment, ContentEntityObject targetOwner, String content) {
        AuthenticatedUserThreadLocal.setUser((User)user);
        try {
            Comment comment = this.commentManager.addCommentToObject(targetOwner, parentComment, content);
            ContentEntityObject container = comment.getContainer();
            String spaceKey = container instanceof Spaced ? ((Spaced)container).getSpace().getKey() : "";
            this.eventPublisher.publish((Object)new ReplyToCommentByEmailAnalytics.CreateComment(comment.getContainer().getId(), comment.getContainer().getType(), spaceKey, comment.getId()));
        }
        finally {
            AuthenticatedUserThreadLocal.reset();
        }
    }

    private ContentEntityObject findContentObject(long contentId) {
        return this.contentEntityManager.getById(contentId);
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.ContentProperties
 *  com.atlassian.confluence.content.render.xhtml.Renderer
 *  com.atlassian.confluence.notifications.batch.template.BatchTemplateActions
 *  com.atlassian.confluence.notifications.batch.template.BatchTemplateCommentPattern$Builder
 *  com.atlassian.confluence.notifications.batch.template.BatchTemplateLozenge
 *  com.atlassian.confluence.notifications.batch.template.BatchTemplateLozenge$Status
 *  com.atlassian.confluence.notifications.content.SimpleCommentPayload
 *  com.atlassian.confluence.notifications.content.batching.CommentContext
 *  com.atlassian.confluence.notifications.content.context.email.batch.EmailCommentBatchSectionProvider
 *  com.atlassian.confluence.notifications.content.context.email.batch.EmailCommentBatchSectionProvider$CommentNode
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.pages.CommentManager
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.plugin.notifications.api.notification.UserNotificationPreferencesManager
 *  com.atlassian.sal.api.message.I18nResolver
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.inlinecomments.notifications.batch;

import com.atlassian.confluence.content.ContentProperties;
import com.atlassian.confluence.content.render.xhtml.Renderer;
import com.atlassian.confluence.notifications.batch.template.BatchTemplateActions;
import com.atlassian.confluence.notifications.batch.template.BatchTemplateCommentPattern;
import com.atlassian.confluence.notifications.batch.template.BatchTemplateLozenge;
import com.atlassian.confluence.notifications.content.SimpleCommentPayload;
import com.atlassian.confluence.notifications.content.batching.CommentContext;
import com.atlassian.confluence.notifications.content.context.email.batch.EmailCommentBatchSectionProvider;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.CommentManager;
import com.atlassian.confluence.plugins.inlinecomments.utils.ResolveCommentConverter;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.notifications.api.notification.UserNotificationPreferencesManager;
import com.atlassian.sal.api.message.I18nResolver;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

public class EmailInlineCommentBatchingSectionProvider
extends EmailCommentBatchSectionProvider {
    private static final String INLINE_COMMENT_SECTION_NAME = "notifications.batch.inline.comment.%s.section.name";
    private static final String INLINE_COMMENT_CREATED_SECTION_HEADER = "notifications.batch.inline.comment.created.section.header";
    private static final String INLINE_COMMENT_STATUS_LOZENGE = "notifications.batch.inline.comment.%s.lozenge";
    private static final String COMMENT_WEBITEMS_SECTION_ID = "email.batch.inline.comment.action.links";
    private static final String NOTIFICATION_KEY = "notificationKey";
    private final Set<String> statuses = new HashSet<String>();

    public EmailInlineCommentBatchingSectionProvider(I18nResolver i18nResolver, Renderer xhtmlRenderer, CommentManager commentManager, UserNotificationPreferencesManager preferencesManager) {
        super(commentManager, xhtmlRenderer, i18nResolver, preferencesManager);
    }

    protected List<EmailCommentBatchSectionProvider.CommentNode> filterAndTransformContexts(List<CommentContext> commentContexts) {
        this.statuses.clear();
        List commentNodes = super.filterAndTransformContexts(commentContexts);
        HashSet<Long> commentIds = new HashSet<Long>();
        ArrayList<EmailCommentBatchSectionProvider.CommentNode> result = new ArrayList<EmailCommentBatchSectionProvider.CommentNode>();
        for (EmailCommentBatchSectionProvider.CommentNode commentNode : commentNodes) {
            if (commentIds.contains(commentNode.getContext().getCommentId())) continue;
            result.add(commentNode);
            commentIds.add(commentNode.getContext().getCommentId());
        }
        return result;
    }

    protected BatchTemplateCommentPattern.Builder commentThread(EmailCommentBatchSectionProvider.CommentNode commentNode, Set<Long> comments) {
        BatchTemplateCommentPattern.Builder thread = super.commentThread(commentNode, comments);
        CommentContext context = commentNode.getContext();
        Comment comment = commentNode.getComment();
        ContentProperties properties = comment.getProperties();
        thread.inlineContext(properties.getStringProperty("inline-original-selection"));
        if (!commentNode.isContextual()) {
            String statusProperty = properties.getStringProperty("status");
            String status = StringUtils.isEmpty((CharSequence)statusProperty) ? "new" : (statusProperty.equals("dangling") ? "resolved" : statusProperty);
            this.statuses.add(status);
            boolean resolved = ResolveCommentConverter.isResolved(status);
            if (statusProperty != null) {
                thread.status(new BatchTemplateLozenge(this.i18nResolver.getText(String.format(INLINE_COMMENT_STATUS_LOZENGE, status)), resolved ? BatchTemplateLozenge.Status.SUCCESS : BatchTemplateLozenge.Status.MOVED, false));
            }
            BatchTemplateActions actions = new BatchTemplateActions(comment.getContentId(), COMMENT_WEBITEMS_SECTION_ID);
            if (context.getNotificationKey().isDefined()) {
                actions.getContext().put(NOTIFICATION_KEY, new ModuleCompleteKey((String)context.getNotificationKey().get()));
            }
            thread.actions(actions);
        }
        thread.singleAuthor(commentNode.getContext().getOriginator());
        return thread;
    }

    protected String getActionsSectionId() {
        return COMMENT_WEBITEMS_SECTION_ID;
    }

    protected String getSectionHeaderKey() {
        return INLINE_COMMENT_CREATED_SECTION_HEADER;
    }

    protected String getSectionNameKey() {
        return String.format(INLINE_COMMENT_SECTION_NAME, this.statuses.size() == 1 ? this.statuses.iterator().next() : "update");
    }

    public Class getPayloadType() {
        return SimpleCommentPayload.class;
    }
}


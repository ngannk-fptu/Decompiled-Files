/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalSpi
 *  com.atlassian.confluence.content.render.xhtml.Renderer
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.notifications.batch.service.AbstractBatchSectionProvider
 *  com.atlassian.confluence.notifications.batch.service.BatchSectionProvider$BatchOutput
 *  com.atlassian.confluence.notifications.batch.service.BatchTarget
 *  com.atlassian.confluence.notifications.batch.service.BatchingRoleRecipient
 *  com.atlassian.confluence.notifications.batch.template.BatchSection
 *  com.atlassian.confluence.notifications.batch.template.BatchTemplateActions
 *  com.atlassian.confluence.notifications.batch.template.BatchTemplateCommentPattern$Builder
 *  com.atlassian.confluence.notifications.batch.template.BatchTemplateElement
 *  com.atlassian.confluence.notifications.batch.template.BatchTemplateGroup$Builder
 *  com.atlassian.confluence.notifications.batch.template.BatchTemplateHtml
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.pages.CommentManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.notifications.api.notification.UserNotificationPreferencesManager
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.confluence.notifications.content.context.email.batch;

import com.atlassian.annotations.ExperimentalSpi;
import com.atlassian.confluence.content.render.xhtml.Renderer;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.notifications.batch.service.AbstractBatchSectionProvider;
import com.atlassian.confluence.notifications.batch.service.BatchSectionProvider;
import com.atlassian.confluence.notifications.batch.service.BatchTarget;
import com.atlassian.confluence.notifications.batch.service.BatchingRoleRecipient;
import com.atlassian.confluence.notifications.batch.template.BatchSection;
import com.atlassian.confluence.notifications.batch.template.BatchTemplateActions;
import com.atlassian.confluence.notifications.batch.template.BatchTemplateCommentPattern;
import com.atlassian.confluence.notifications.batch.template.BatchTemplateElement;
import com.atlassian.confluence.notifications.batch.template.BatchTemplateGroup;
import com.atlassian.confluence.notifications.batch.template.BatchTemplateHtml;
import com.atlassian.confluence.notifications.content.SimpleCommentPayload;
import com.atlassian.confluence.notifications.content.batching.CommentContext;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.CommentManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.notifications.api.notification.UserNotificationPreferencesManager;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserKey;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

@ExperimentalSpi
public class EmailCommentBatchSectionProvider
extends AbstractBatchSectionProvider<CommentContext> {
    private static final String COMMENT_CREATED_SECTION_HEADER = "notifications.batch.comment.created.section.header";
    private static final String COMMENT_CREATED_SECTION_NAME = "notifications.batch.comment.created.section.name";
    private static final String COMMENT_WEBITEMS_SECTION_ID = "email.batch.content.action.links";
    protected final CommentManager commentManager;
    protected final Renderer xhtmlRenderer;
    protected final I18nResolver i18nResolver;

    public EmailCommentBatchSectionProvider(CommentManager commentManager, Renderer xhtmlRenderer, I18nResolver i18nResolver, UserNotificationPreferencesManager preferencesManager) {
        super(preferencesManager);
        this.commentManager = commentManager;
        this.xhtmlRenderer = xhtmlRenderer;
        this.i18nResolver = i18nResolver;
    }

    public BatchSectionProvider.BatchOutput processBatch(BatchingRoleRecipient recipient, List<CommentContext> commentContexts, Set<UserKey> userKeys) {
        List<CommentNode> filtered = this.filterAndTransformContexts(commentContexts);
        if (filtered.isEmpty()) {
            return new BatchSectionProvider.BatchOutput();
        }
        int count = filtered.size();
        CommentNode firstComment = filtered.get(0);
        Map<Long, CommentNode> commentThreads = this.buildCommentThreads(filtered);
        BatchTemplateGroup.Builder group = new BatchTemplateGroup.Builder();
        this.generateCommentThreads(group, new HashSet<Long>(), commentThreads.values());
        BatchTarget batchTarget = count == 1 ? new BatchTarget(Long.toString(firstComment.getContext().getCommentId()), 1) : new BatchTarget(Long.toString(firstComment.getContext().getPageId()), 0);
        return new BatchSectionProvider.BatchOutput(new BatchSection(count, this.i18nResolver.getText(this.getSectionHeaderKey(), new Serializable[]{Integer.valueOf(count)}), this.i18nResolver.getText(this.getSectionNameKey(), new Serializable[]{Integer.valueOf(count)}), Collections.singletonList(group.build())), batchTarget);
    }

    protected List<CommentNode> filterAndTransformContexts(List<CommentContext> commentContexts) {
        return commentContexts.stream().map(context -> new CommentNode(this.commentManager.getComment(context.getCommentId()), (CommentContext)context, false)).filter(node -> node.comment != null).collect(Collectors.toList());
    }

    private Map<Long, CommentNode> buildCommentThreads(List<CommentNode> commentNodes) {
        TreeMap<Long, CommentNode> commentThreads = new TreeMap<Long, CommentNode>();
        commentNodes.forEach(node -> commentThreads.put(node.getContext().getCommentId(), (CommentNode)node));
        HashMap parents = new HashMap();
        commentNodes.forEach(node -> this.resolveParent((CommentNode)node, (Map<Long, CommentNode>)commentThreads, parents));
        commentThreads.putAll(parents);
        return commentThreads;
    }

    private void resolveParent(CommentNode node, Map<Long, CommentNode> commentThreads, Map<Long, CommentNode> contextParents) {
        Option<Long> parentId = node.getContext().getParentCommentId();
        if (parentId.isDefined()) {
            long parentCommentId = (Long)parentId.get();
            long pageId = node.getContext().getPageId();
            CommentNode parent = commentThreads.get(parentCommentId);
            CommentNode commentNode = parent = parent == null ? contextParents.get(parentCommentId) : parent;
            if (parent == null) {
                Comment parentComment = this.commentManager.getComment(parentCommentId);
                UserKey parentAuthor = parentComment != null && parentComment.getCreator() != null ? parentComment.getCreator().getKey() : null;
                parent = new CommentNode(parentComment, new CommentContext(parentAuthor, parentCommentId, (Option<Long>)Option.none(), pageId), true);
                contextParents.put(parentCommentId, parent);
            }
            parent.addChild(node);
        }
    }

    private void generateCommentThreads(BatchTemplateGroup.Builder group, Set<Long> commentsToIgnore, Collection<CommentNode> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            return;
        }
        ArrayList<BatchTemplateCommentPattern.Builder> threads = new ArrayList<BatchTemplateCommentPattern.Builder>();
        for (CommentNode commentNode : nodes) {
            long commentId = commentNode.getContext().getCommentId();
            if (commentsToIgnore.contains(commentId) || commentNode.getComment() == null) continue;
            BatchTemplateCommentPattern.Builder thread = this.commentThread(commentNode, commentsToIgnore);
            threads.add(thread);
        }
        int count = threads.size() - 1;
        for (int index = 0; index <= count; ++index) {
            BatchTemplateCommentPattern.Builder thread = (BatchTemplateCommentPattern.Builder)threads.get(index);
            if (index < count) {
                thread.split(true);
            }
            group.line().element((BatchTemplateElement)thread.build()).end();
        }
    }

    protected BatchTemplateCommentPattern.Builder commentThread(CommentNode commentNode, Set<Long> commentsToIgnore) {
        BatchTemplateCommentPattern.Builder thread = new BatchTemplateCommentPattern.Builder();
        ConfluenceUser author = commentNode.getComment().getCreator();
        thread.author(author != null ? author.getKey() : null);
        thread.commentBody(new BatchTemplateHtml(this.xhtmlRenderer.render((ContentEntityObject)commentNode.getComment()), false));
        thread.contextual(commentNode.isContextual());
        if (!commentNode.isContextual()) {
            thread.actions(new BatchTemplateActions(commentNode.getComment().getContentId(), this.getActionsSectionId()));
        }
        commentsToIgnore.add(commentNode.getContext().getCommentId());
        if (commentNode.children != null) {
            commentNode.children.stream().filter(child -> !commentsToIgnore.contains(child.getContext().getCommentId())).forEach(child -> thread.reply(this.commentThread((CommentNode)child, commentsToIgnore)));
        }
        return thread;
    }

    protected String getActionsSectionId() {
        return COMMENT_WEBITEMS_SECTION_ID;
    }

    protected String getSectionHeaderKey() {
        return COMMENT_CREATED_SECTION_HEADER;
    }

    protected String getSectionNameKey() {
        return COMMENT_CREATED_SECTION_NAME;
    }

    public Class getPayloadType() {
        return SimpleCommentPayload.class;
    }

    public static class CommentNode
    implements Comparable<CommentNode> {
        private final Comment comment;
        private final CommentContext context;
        private final boolean contextual;
        private TreeSet<CommentNode> children;

        public CommentNode(Comment comment, CommentContext context, boolean contextual) {
            this.comment = comment;
            this.context = context;
            this.contextual = contextual;
        }

        public void addChild(CommentNode child) {
            if (this.children == null) {
                this.children = new TreeSet();
            }
            this.children.add(child);
        }

        public CommentContext getContext() {
            return this.context;
        }

        public boolean isContextual() {
            return this.contextual;
        }

        public Comment getComment() {
            return this.comment;
        }

        @Override
        public int compareTo(CommentNode o) {
            return (int)(this.context.getCommentId() - o.context.getCommentId());
        }
    }
}


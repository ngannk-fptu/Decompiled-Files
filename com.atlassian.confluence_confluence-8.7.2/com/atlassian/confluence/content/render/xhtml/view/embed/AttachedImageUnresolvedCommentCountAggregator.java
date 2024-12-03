/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 */
package com.atlassian.confluence.content.render.xhtml.view.embed;

import com.atlassian.confluence.pages.CommentManager;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;

public class AttachedImageUnresolvedCommentCountAggregator {
    public static final String UNRESOLVED_COMMENT_COUNT_AGGREGATOR_PROP = "UnresolvedCommentCountAggregator";
    public static final String COMMENT_STATUS_PROP = "status";
    public static final String COMMENT_STATUS_OPEN = "open";
    public static final String COMMENT_STATUS_REOPENED = "reopened";
    private final CommentManager commentManager;
    private final Set<Long> attachmentIds = Sets.newHashSet();
    private final Map<Long, Integer> unresolvedCommentCountMap = Maps.newHashMap();

    public AttachedImageUnresolvedCommentCountAggregator(CommentManager commentManager) {
        this.commentManager = commentManager;
    }

    public void addAttachedImageId(Long contentId) {
        if (!this.unresolvedCommentCountMap.containsKey(contentId)) {
            this.attachmentIds.add(contentId);
        }
    }

    public Integer getUnresolvedCommentCount(Long contentId) {
        Integer unresolvedCommentCount = this.unresolvedCommentCountMap.get(contentId);
        if (unresolvedCommentCount == null && !this.attachmentIds.isEmpty()) {
            this.loadUnresolvedCommentCount();
            unresolvedCommentCount = this.unresolvedCommentCountMap.get(contentId);
        }
        return unresolvedCommentCount != null ? unresolvedCommentCount : 0;
    }

    private void loadUnresolvedCommentCount() {
        this.unresolvedCommentCountMap.putAll(this.commentManager.countUnresolvedComments(this.attachmentIds));
        this.attachmentIds.clear();
    }
}


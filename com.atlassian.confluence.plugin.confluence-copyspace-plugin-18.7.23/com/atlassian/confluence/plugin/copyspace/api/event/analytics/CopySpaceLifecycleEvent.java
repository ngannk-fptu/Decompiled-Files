/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugin.copyspace.api.event.analytics;

public abstract class CopySpaceLifecycleEvent {
    private final String operationUUID;
    private final long originalSpaceId;
    private final String originalSpaceKey;
    private final boolean copyComments;
    private final boolean copyLabels;
    private final boolean copyAttachments;
    private final boolean keepMetaData;
    private final boolean preserveWatchers;
    private final boolean copyBlogposts;
    private final boolean copyPages;
    private final int pagesCount;
    private final int commentsCount;
    private final int blogPostsCount;
    private final int attachmentsCount;

    protected CopySpaceLifecycleEvent(String operationUUID, long originalSpaceId, String originalSpaceKey, boolean copyComments, boolean copyLabels, boolean copyAttachments, boolean keepMetaData, boolean preserveWatchers, boolean copyBlogposts, boolean copyPages, int pagesCount, int commentsCount, int blogPostsCount, int attachmentsCount) {
        this.operationUUID = operationUUID;
        this.originalSpaceId = originalSpaceId;
        this.originalSpaceKey = originalSpaceKey;
        this.copyComments = copyComments;
        this.copyLabels = copyLabels;
        this.copyAttachments = copyAttachments;
        this.keepMetaData = keepMetaData;
        this.preserveWatchers = preserveWatchers;
        this.copyBlogposts = copyBlogposts;
        this.copyPages = copyPages;
        this.pagesCount = pagesCount;
        this.blogPostsCount = blogPostsCount;
        this.commentsCount = commentsCount;
        this.attachmentsCount = attachmentsCount;
    }

    public String getOperationUUID() {
        return this.operationUUID;
    }

    public long getOriginalSpaceId() {
        return this.originalSpaceId;
    }

    public String getOriginalSpaceKey() {
        return this.originalSpaceKey;
    }

    public boolean isCopyComments() {
        return this.copyComments;
    }

    public boolean isCopyLabels() {
        return this.copyLabels;
    }

    public boolean isCopyAttachments() {
        return this.copyAttachments;
    }

    public boolean isKeepMetaData() {
        return this.keepMetaData;
    }

    public boolean isPreserveWatchers() {
        return this.preserveWatchers;
    }

    public boolean isCopyBlogposts() {
        return this.copyBlogposts;
    }

    public boolean isCopyPages() {
        return this.copyPages;
    }

    public int getPagesCount() {
        return this.pagesCount;
    }

    public int getCommentsCount() {
        return this.commentsCount;
    }

    public int getBlogPostsCount() {
        return this.blogPostsCount;
    }

    public int getAttachmentsCount() {
        return this.attachmentsCount;
    }
}


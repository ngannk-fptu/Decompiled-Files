/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.ProgressMeter
 */
package com.atlassian.confluence.plugin.copyspace.context;

import com.atlassian.confluence.plugin.copyspace.rest.CopySpaceRequest;
import com.atlassian.core.util.ProgressMeter;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class CopySpaceContext {
    private final String uuid;
    private final long originalSpaceId;
    private final String originalSpaceKey;
    private final String targetSpaceKey;
    private final String targetSpaceName;
    private final String targetSpaceDescription;
    private final Instant startTimestamp;
    private final boolean copyAttachments;
    private final boolean copyMetadata;
    private final boolean preserveWatchers;
    private final boolean copyLabels;
    private final boolean copyComments;
    private final boolean copyBlogPosts;
    private final boolean copyPages;
    private final int pagesCount;
    private final int blogPostsCount;
    private final int attachmentsCount;
    private final int commentsCount;
    private final ProgressMeter progressMeter;

    private CopySpaceContext(String uuid, long originalSpaceId, String originalSpaceKey, String targetSpaceKey, String targetSpaceName, String targetSpaceDescription, Instant startTimestamp, boolean copyAttachments, boolean copyMetadata, boolean preserveWatchers, boolean copyLabels, boolean copyComments, boolean copyBlogPosts, boolean copyPages, int pagesCount, int blogPostsCount, int attachmentsCount, int commentsCount, ProgressMeter progressMeter) {
        this.uuid = uuid;
        this.originalSpaceId = originalSpaceId;
        this.originalSpaceKey = originalSpaceKey;
        this.targetSpaceKey = targetSpaceKey;
        this.targetSpaceName = targetSpaceName;
        this.targetSpaceDescription = targetSpaceDescription;
        this.startTimestamp = startTimestamp;
        this.copyAttachments = copyAttachments;
        this.copyMetadata = copyMetadata;
        this.preserveWatchers = preserveWatchers;
        this.copyLabels = copyLabels;
        this.copyComments = copyComments;
        this.copyBlogPosts = copyBlogPosts;
        this.copyPages = copyPages;
        this.pagesCount = pagesCount;
        this.blogPostsCount = blogPostsCount;
        this.attachmentsCount = attachmentsCount;
        this.commentsCount = commentsCount;
        this.progressMeter = progressMeter;
    }

    public String getUuid() {
        return this.uuid;
    }

    public long getOriginalSpaceId() {
        return this.originalSpaceId;
    }

    public String getOriginalSpaceKey() {
        return this.originalSpaceKey;
    }

    public String getTargetSpaceKey() {
        return this.targetSpaceKey;
    }

    public Instant getStartTimestamp() {
        return this.startTimestamp;
    }

    public boolean isCopyAttachments() {
        return this.copyAttachments;
    }

    public boolean isCopyMetadata() {
        return this.copyMetadata;
    }

    public boolean isCopyLabels() {
        return this.copyLabels;
    }

    public boolean isPreserveWatchers() {
        return this.preserveWatchers;
    }

    public boolean isCopyComments() {
        return this.copyComments;
    }

    public boolean isCopyBlogPosts() {
        return this.copyBlogPosts;
    }

    public boolean isCopyPages() {
        return this.copyPages;
    }

    public int getPagesCount() {
        return this.pagesCount;
    }

    public int getBlogPostsCount() {
        return this.blogPostsCount;
    }

    public int getAttachmentsCount() {
        return this.attachmentsCount;
    }

    public int getCommentsCount() {
        return this.commentsCount;
    }

    public String getTargetSpaceName() {
        return this.targetSpaceName;
    }

    public String getTargetSpaceDescription() {
        return this.targetSpaceDescription;
    }

    public ProgressMeter getProgressMeter() {
        return this.progressMeter;
    }

    public static class Builder {
        private String uuid = String.valueOf(UUID.randomUUID());
        private long originalSpaceId;
        private String originalSpaceKey;
        private String targetSpaceKey;
        private String targetSpaceName;
        private String targetSpaceDescription;
        private Instant startTimestamp = Instant.now();
        private boolean copyAttachments;
        private boolean copyMetadata;
        private boolean preserveWatchers;
        private boolean copyLabels;
        private boolean copyComments;
        private boolean copyBlogPosts;
        private boolean copyPages;
        private int pagesCount;
        private int blogPostsCount;
        private int attachmentsCount;
        private int commentsCount;
        private ProgressMeter progressMeter;

        public Builder(String originalSpaceKey, String targetSpaceKey) {
            this.originalSpaceKey = Objects.requireNonNull(originalSpaceKey);
            this.targetSpaceKey = Objects.requireNonNull(targetSpaceKey);
        }

        public Builder copySpaceRequest(CopySpaceRequest request) {
            this.targetSpaceName = request.getNewName();
            this.targetSpaceDescription = request.getNewDescription();
            this.copyAttachments = request.isCopyAttachments();
            this.copyPages = request.isCopyPages();
            this.copyBlogPosts = request.isCopyBlogPosts();
            this.copyComments = request.isCopyComments();
            this.copyLabels = request.isCopyLabels();
            this.copyMetadata = request.isCopyMetadata();
            this.preserveWatchers = request.isPreserveWatchers();
            return this;
        }

        public Builder originalSpaceId(long originalSpaceId) {
            this.originalSpaceId = originalSpaceId;
            return this;
        }

        public Builder copyAttachments(boolean copyAttachments) {
            this.copyAttachments = copyAttachments;
            return this;
        }

        public Builder copyMetadata(boolean copyMetadata) {
            this.copyMetadata = copyMetadata;
            return this;
        }

        public Builder preserveWatchers(boolean preserveWatchers) {
            this.preserveWatchers = preserveWatchers;
            return this;
        }

        public Builder copyPages(boolean copyPages) {
            this.copyPages = copyPages;
            return this;
        }

        public Builder copyBlogPosts(boolean copyBlogPosts) {
            this.copyBlogPosts = copyBlogPosts;
            return this;
        }

        public Builder copyComments(boolean copyComments) {
            this.copyComments = copyComments;
            return this;
        }

        public Builder copyLabels(boolean copyLabels) {
            this.copyLabels = copyLabels;
            return this;
        }

        public Builder pagesCount(int pagesCount) {
            this.pagesCount = pagesCount;
            return this;
        }

        public Builder blogPostsCount(int blogPostsCount) {
            this.blogPostsCount = blogPostsCount;
            return this;
        }

        public Builder attachmentsCount(int attachmentsCount) {
            this.attachmentsCount = attachmentsCount;
            return this;
        }

        public Builder commentsCount(int commentsCount) {
            this.commentsCount = commentsCount;
            return this;
        }

        public Builder progressMeter(ProgressMeter progressMeter) {
            this.progressMeter = progressMeter;
            return this;
        }

        public CopySpaceContext build() {
            return new CopySpaceContext(this.uuid, this.originalSpaceId, this.originalSpaceKey, this.targetSpaceKey, this.targetSpaceName, this.targetSpaceDescription, this.startTimestamp, this.copyAttachments, this.copyMetadata, this.preserveWatchers, this.copyLabels, this.copyComments, this.copyBlogPosts, this.copyPages, this.pagesCount, this.blogPostsCount, this.attachmentsCount, this.commentsCount, this.progressMeter);
        }
    }
}


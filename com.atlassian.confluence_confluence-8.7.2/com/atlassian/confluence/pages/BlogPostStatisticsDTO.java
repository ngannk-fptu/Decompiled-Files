/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.pages;

public class BlogPostStatisticsDTO {
    private final int allBlogsCount;
    private final int currentBlogsCount;
    private final int draftBlogsCount;
    private final int blogsWithUnpublishedChangesCount;
    private final int deletedBlogsCount;

    public BlogPostStatisticsDTO(int allBlogsCount, int currentBlogsCount, int draftBlogsCount, int blogsWithUnpublishedChangesCount, int deletedBlogsCount) {
        this.allBlogsCount = allBlogsCount;
        this.currentBlogsCount = currentBlogsCount;
        this.draftBlogsCount = draftBlogsCount;
        this.blogsWithUnpublishedChangesCount = blogsWithUnpublishedChangesCount;
        this.deletedBlogsCount = deletedBlogsCount;
    }

    public int getAllBlogsCount() {
        return this.allBlogsCount;
    }

    public int getCurrentBlogsCount() {
        return this.currentBlogsCount;
    }

    public int getDraftBlogsCount() {
        return this.draftBlogsCount;
    }

    public int getBlogsWithUnpublishedChangesCount() {
        return this.blogsWithUnpublishedChangesCount;
    }

    public int getDeletedBlogsCount() {
        return this.deletedBlogsCount;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.pages;

public class PageStatisticsDTO {
    private final int allPagesCount;
    private final int currentPagesCount;
    private final int draftPagesCount;
    private final int pagesWithUnpublishedChangesCount;
    private final int deletedPagesCount;

    public PageStatisticsDTO(int allPagesCount, int currentPagesCount, int draftPagesCount, int pagesWithUnpublishedChangesCount, int deletedPagesCount) {
        this.allPagesCount = allPagesCount;
        this.currentPagesCount = currentPagesCount;
        this.draftPagesCount = draftPagesCount;
        this.pagesWithUnpublishedChangesCount = pagesWithUnpublishedChangesCount;
        this.deletedPagesCount = deletedPagesCount;
    }

    public int getAllPagesCount() {
        return this.allPagesCount;
    }

    public int getCurrentPagesCount() {
        return this.currentPagesCount;
    }

    public int getDraftPagesCount() {
        return this.draftPagesCount;
    }

    public int getPagesWithUnpublishedChangesCount() {
        return this.pagesWithUnpublishedChangesCount;
    }

    public int getDeletedPagesCount() {
        return this.deletedPagesCount;
    }
}


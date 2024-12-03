/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  javax.annotation.Nullable
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.confluence.extra.masterdetail;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.extra.masterdetail.rest.ResourceErrorType;
import com.atlassian.confluence.extra.masterdetail.rest.ResourceException;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import javax.ws.rs.core.Response;

public class DetailsSummaryParameters {
    private int pageSize;
    private int currentPage;
    private boolean countComments;
    private boolean countLikes;
    private String headingsString;
    private String sortBy;
    private boolean reverseSort;
    private List<ContentEntityObject> content;
    private Integer totalRenderedLines;
    private String id;

    public int getPageSize() {
        return this.pageSize;
    }

    public DetailsSummaryParameters setPageSize(int pageSize) {
        this.pageSize = Math.min(1000, pageSize);
        return this;
    }

    public int getCurrentPage() {
        return this.currentPage;
    }

    public DetailsSummaryParameters setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
        return this;
    }

    public Boolean isCountComments() {
        return this.countComments;
    }

    public DetailsSummaryParameters setCountComments(boolean countComments) {
        this.countComments = countComments;
        return this;
    }

    public boolean isCountLikes() {
        return this.countLikes;
    }

    public DetailsSummaryParameters setCountLikes(boolean countLikes) {
        this.countLikes = countLikes;
        return this;
    }

    @Nullable
    public String getHeadingsString() {
        return this.headingsString;
    }

    public DetailsSummaryParameters setHeadingsString(@Nullable String headingsString) {
        this.headingsString = headingsString;
        return this;
    }

    public String getSortBy() {
        return this.sortBy;
    }

    public DetailsSummaryParameters setSortBy(String sortBy) {
        this.sortBy = sortBy;
        return this;
    }

    public boolean isReverseSort() {
        return this.reverseSort;
    }

    public DetailsSummaryParameters setReverseSort(boolean reverseSort) {
        this.reverseSort = reverseSort;
        return this;
    }

    public List<ContentEntityObject> getContent() {
        return this.content;
    }

    public DetailsSummaryParameters setContent(List<ContentEntityObject> content) {
        this.content = content;
        return this;
    }

    public String getId() {
        return this.id;
    }

    public DetailsSummaryParameters setId(String id) {
        this.id = id;
        return this;
    }

    public Integer getTotalRenderedLines() {
        return this.totalRenderedLines;
    }

    public void setTotalRenderedLines(Integer totalRenderedLines) {
        this.totalRenderedLines = totalRenderedLines;
    }

    public int getTotalPages() {
        if (Objects.isNull(this.totalRenderedLines)) {
            return 0;
        }
        return (int)Math.ceil((double)this.getTotalRenderedLines().intValue() / (double)this.pageSize);
    }

    public void checkPageBounds() {
        if (this.currentPage >= this.getTotalPages() && this.getTotalPages() != 0) {
            throw new ResourceException("Requested page is outside bounds", Response.Status.BAD_REQUEST, ResourceErrorType.INVALID_INDEX_PAGE, (Object)this.currentPage);
        }
    }
}


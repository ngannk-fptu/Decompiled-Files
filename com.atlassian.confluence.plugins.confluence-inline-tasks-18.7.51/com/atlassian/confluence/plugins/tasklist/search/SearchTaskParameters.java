/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.tasklist.search;

import com.atlassian.confluence.plugins.tasklist.TaskStatus;
import com.atlassian.confluence.plugins.tasklist.search.SearchTaskSortParameter;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class SearchTaskParameters {
    private int pageIndex;
    private int pageSize;
    private int displayedPages;
    private Integer totalPages;
    private List<Long> pageIds = Collections.emptyList();
    private List<Long> spaceIds = Collections.emptyList();
    private List<Long> labelIds = Collections.emptyList();
    private List<String> assigneeUserKeys = Collections.emptyList();
    private List<String> creatorUserKeys = Collections.emptyList();
    private Date startDueDate;
    private Date endDueDate;
    private Date startCreatedDate;
    private Date endCreatedDate;
    private TaskStatus status;
    private SearchTaskSortParameter sortParameters;

    public List<Long> getPageIds() {
        return this.pageIds;
    }

    public void setPageIds(List<Long> pageIds) {
        this.pageIds = pageIds;
    }

    public List<Long> getSpaceIds() {
        return this.spaceIds;
    }

    public void setSpaceIds(List<Long> spaceIds) {
        this.spaceIds = spaceIds;
    }

    public List<Long> getLabelIds() {
        return this.labelIds;
    }

    public void setLabelIds(List<Long> labelIds) {
        this.labelIds = labelIds;
    }

    public List<String> getAssigneeUserKeys() {
        return this.assigneeUserKeys;
    }

    public void setAssigneeUserKeys(List<String> assigneeUserKeys) {
        this.assigneeUserKeys = assigneeUserKeys;
    }

    public Date getStartDueDate() {
        return this.startDueDate;
    }

    public void setStartDueDate(Date startDueDate) {
        this.startDueDate = startDueDate;
    }

    public Date getEndDueDate() {
        return this.endDueDate;
    }

    public void setEndDueDate(Date endDueDate) {
        this.endDueDate = endDueDate;
    }

    public Date getStartCreatedDate() {
        return this.startCreatedDate;
    }

    public void setStartCreatedDate(Date startCreatedDate) {
        this.startCreatedDate = startCreatedDate;
    }

    public Date getEndCreatedDate() {
        return this.endCreatedDate;
    }

    public void setEndCreatedDate(Date endCreatedDate) {
        this.endCreatedDate = endCreatedDate;
    }

    public int getPageIndex() {
        return this.pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotalPages() {
        return this.totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public List<String> getCreatorUserKeys() {
        return this.creatorUserKeys;
    }

    public void setCreatorUserKeys(List<String> creatorUserKeys) {
        this.creatorUserKeys = creatorUserKeys;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskStatus getStatus() {
        return this.status;
    }

    public int getDisplayedPages() {
        return this.displayedPages;
    }

    public void setDisplayedPages(int displayedPages) {
        this.displayedPages = displayedPages;
    }

    public SearchTaskSortParameter getSortParameters() {
        return this.sortParameters;
    }

    public void setSortParameters(SearchTaskSortParameter sortParameters) {
        this.sortParameters = sortParameters;
    }
}


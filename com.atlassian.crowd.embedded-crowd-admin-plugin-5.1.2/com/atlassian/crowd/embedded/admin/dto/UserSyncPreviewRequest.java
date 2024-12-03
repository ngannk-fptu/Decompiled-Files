/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.embedded.admin.dto;

public abstract class UserSyncPreviewRequest {
    private String filter = "";
    private int pageNumber = 1;
    private int totalUsersCount = -1;

    public String getFilter() {
        return this.filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public int getPageNumber() {
        return this.pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getTotalUsersCount() {
        return this.totalUsersCount;
    }

    public void setTotalUsersCount(int totalUsersCount) {
        this.totalUsersCount = totalUsersCount;
    }
}


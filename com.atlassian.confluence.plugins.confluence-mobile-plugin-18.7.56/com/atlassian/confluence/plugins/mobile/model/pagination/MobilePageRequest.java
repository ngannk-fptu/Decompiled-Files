/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 */
package com.atlassian.confluence.plugins.mobile.model.pagination;

import com.atlassian.confluence.api.model.pagination.PageRequest;

public class MobilePageRequest
implements PageRequest {
    private int start;
    private int limit;
    private int accept;
    private MobilePageRequest next;

    public MobilePageRequest(int start, int limit, int accept) {
        this.start = start;
        this.limit = limit;
        this.accept = accept;
    }

    public MobilePageRequest(MobilePageRequest request) {
        this(request.getStart(), request.getLimit(), request.getAccept());
    }

    public int getStart() {
        return this.start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getLimit() {
        return this.limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getAccept() {
        return this.accept;
    }

    public void setAccept(int accept) {
        this.accept = accept;
    }

    public MobilePageRequest getNext() {
        return this.next;
    }

    public void setNext(MobilePageRequest next) {
        this.next = next;
    }
}


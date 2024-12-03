/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.atlassian.ratelimiting.page;

import com.google.common.base.Preconditions;

public class PageRequest {
    public static final int MAX_PAGE_SIZE = 1000;
    private final int page;
    private final int size;

    public PageRequest(int page, int size) {
        this.page = Math.max(0, page);
        this.size = Math.max(1, size);
        Preconditions.checkArgument((this.size <= 1000 ? 1 : 0) != 0, (Object)"Page size must not be greater than 1000");
    }

    public int getOffset() {
        return this.getPage() * this.getSize();
    }

    public PageRequest next() {
        return new PageRequest(this.getPage() + 1, this.getSize());
    }

    public PageRequest previous() {
        return new PageRequest(this.getPage() - 1, this.getSize());
    }

    public int getPage() {
        return this.page;
    }

    public int getSize() {
        return this.size;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PageRequest)) {
            return false;
        }
        PageRequest other = (PageRequest)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getPage() != other.getPage()) {
            return false;
        }
        return this.getSize() == other.getSize();
    }

    protected boolean canEqual(Object other) {
        return other instanceof PageRequest;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getPage();
        result = result * 59 + this.getSize();
        return result;
    }

    public String toString() {
        return "PageRequest(page=" + this.getPage() + ", size=" + this.getSize() + ")";
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.domain;

import java.io.Serializable;
import org.springframework.data.domain.Pageable;

public abstract class AbstractPageRequest
implements Pageable,
Serializable {
    private static final long serialVersionUID = 1232825578694716871L;
    private final int page;
    private final int size;

    public AbstractPageRequest(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("Page index must not be less than zero!");
        }
        if (size < 1) {
            throw new IllegalArgumentException("Page size must not be less than one!");
        }
        this.page = page;
        this.size = size;
    }

    @Override
    public int getPageSize() {
        return this.size;
    }

    @Override
    public int getPageNumber() {
        return this.page;
    }

    @Override
    public long getOffset() {
        return (long)this.page * (long)this.size;
    }

    @Override
    public boolean hasPrevious() {
        return this.page > 0;
    }

    @Override
    public Pageable previousOrFirst() {
        return this.hasPrevious() ? this.previous() : this.first();
    }

    @Override
    public abstract Pageable next();

    public abstract Pageable previous();

    @Override
    public abstract Pageable first();

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + this.page;
        result = 31 * result + this.size;
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        AbstractPageRequest other = (AbstractPageRequest)obj;
        return this.page == other.page && this.size == other.size;
    }
}


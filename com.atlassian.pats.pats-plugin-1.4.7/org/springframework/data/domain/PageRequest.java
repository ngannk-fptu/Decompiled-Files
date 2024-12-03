/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.data.domain;

import org.springframework.data.domain.AbstractPageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class PageRequest
extends AbstractPageRequest {
    private static final long serialVersionUID = -4541509938956089562L;
    private final Sort sort;

    protected PageRequest(int page, int size, Sort sort) {
        super(page, size);
        Assert.notNull((Object)sort, (String)"Sort must not be null!");
        this.sort = sort;
    }

    public static PageRequest of(int page, int size) {
        return PageRequest.of(page, size, Sort.unsorted());
    }

    public static PageRequest of(int page, int size, Sort sort) {
        return new PageRequest(page, size, sort);
    }

    public static PageRequest of(int page, int size, Sort.Direction direction, String ... properties) {
        return PageRequest.of(page, size, Sort.by(direction, properties));
    }

    public static PageRequest ofSize(int pageSize) {
        return PageRequest.of(0, pageSize);
    }

    @Override
    public Sort getSort() {
        return this.sort;
    }

    @Override
    public PageRequest next() {
        return new PageRequest(this.getPageNumber() + 1, this.getPageSize(), this.getSort());
    }

    @Override
    public PageRequest previous() {
        return this.getPageNumber() == 0 ? this : new PageRequest(this.getPageNumber() - 1, this.getPageSize(), this.getSort());
    }

    @Override
    public PageRequest first() {
        return new PageRequest(0, this.getPageSize(), this.getSort());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof PageRequest)) {
            return false;
        }
        PageRequest that = (PageRequest)obj;
        return super.equals(that) && this.sort.equals(that.sort);
    }

    @Override
    public PageRequest withPage(int pageNumber) {
        return new PageRequest(pageNumber, this.getPageSize(), this.getSort());
    }

    public PageRequest withSort(Sort.Direction direction, String ... properties) {
        return new PageRequest(this.getPageNumber(), this.getPageSize(), Sort.by(direction, properties));
    }

    public PageRequest withSort(Sort sort) {
        return new PageRequest(this.getPageNumber(), this.getPageSize(), sort);
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + this.sort.hashCode();
    }

    public String toString() {
        return String.format("Page request [number: %d, size %d, sort: %s]", this.getPageNumber(), this.getPageSize(), this.sort);
    }
}


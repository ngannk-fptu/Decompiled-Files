/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.data.domain;

import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Unpaged;
import org.springframework.util.Assert;

public interface Pageable {
    public static Pageable unpaged() {
        return Unpaged.INSTANCE;
    }

    public static Pageable ofSize(int pageSize) {
        return PageRequest.of(0, pageSize);
    }

    default public boolean isPaged() {
        return true;
    }

    default public boolean isUnpaged() {
        return !this.isPaged();
    }

    public int getPageNumber();

    public int getPageSize();

    public long getOffset();

    public Sort getSort();

    default public Sort getSortOr(Sort sort) {
        Assert.notNull((Object)sort, (String)"Fallback Sort must not be null!");
        return this.getSort().isSorted() ? this.getSort() : sort;
    }

    public Pageable next();

    public Pageable previousOrFirst();

    public Pageable first();

    public Pageable withPage(int var1);

    public boolean hasPrevious();

    default public Optional<Pageable> toOptional() {
        return this.isUnpaged() ? Optional.empty() : Optional.of(this);
    }
}


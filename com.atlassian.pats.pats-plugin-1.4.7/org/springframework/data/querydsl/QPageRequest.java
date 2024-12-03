/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.data.querydsl;

import com.querydsl.core.types.OrderSpecifier;
import org.springframework.data.domain.AbstractPageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QSort;
import org.springframework.util.Assert;

public class QPageRequest
extends AbstractPageRequest {
    private static final long serialVersionUID = 7529171950267879273L;
    private final QSort sort;

    @Deprecated
    public QPageRequest(int page, int size) {
        this(page, size, QSort.unsorted());
    }

    @Deprecated
    public QPageRequest(int page, int size, OrderSpecifier<?> ... orderSpecifiers) {
        this(page, size, new QSort(orderSpecifiers));
    }

    @Deprecated
    public QPageRequest(int page, int size, QSort sort) {
        super(page, size);
        Assert.notNull((Object)sort, (String)"QSort must not be null!");
        this.sort = sort;
    }

    public static QPageRequest of(int page, int size) {
        return new QPageRequest(page, size, QSort.unsorted());
    }

    public static QPageRequest of(int page, int size, OrderSpecifier<?> ... orderSpecifiers) {
        return new QPageRequest(page, size, new QSort(orderSpecifiers));
    }

    public static QPageRequest of(int page, int size, QSort sort) {
        return new QPageRequest(page, size, sort);
    }

    public static QPageRequest ofSize(int pageSize) {
        return QPageRequest.of(0, pageSize);
    }

    @Override
    public Sort getSort() {
        return this.sort;
    }

    @Override
    public Pageable next() {
        return QPageRequest.of(this.getPageNumber() + 1, this.getPageSize(), this.sort);
    }

    @Override
    public Pageable previous() {
        return QPageRequest.of(this.getPageNumber() - 1, this.getPageSize(), this.sort);
    }

    @Override
    public Pageable first() {
        return QPageRequest.of(0, this.getPageSize(), this.sort);
    }

    @Override
    public QPageRequest withPage(int pageNumber) {
        return new QPageRequest(pageNumber, this.getPageSize(), this.sort);
    }

    public QPageRequest withSort(QSort sort) {
        return new QPageRequest(this.getPageNumber(), this.getPageSize(), sort);
    }
}


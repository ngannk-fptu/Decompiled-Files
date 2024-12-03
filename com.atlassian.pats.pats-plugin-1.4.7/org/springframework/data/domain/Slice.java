/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.domain;

import java.util.List;
import java.util.function.Function;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Streamable;

public interface Slice<T>
extends Streamable<T> {
    public int getNumber();

    public int getSize();

    public int getNumberOfElements();

    public List<T> getContent();

    public boolean hasContent();

    public Sort getSort();

    public boolean isFirst();

    public boolean isLast();

    public boolean hasNext();

    public boolean hasPrevious();

    default public Pageable getPageable() {
        return PageRequest.of(this.getNumber(), this.getSize(), this.getSort());
    }

    public Pageable nextPageable();

    public Pageable previousPageable();

    @Override
    public <U> Slice<U> map(Function<? super T, ? extends U> var1);

    default public Pageable nextOrLastPageable() {
        return this.hasNext() ? this.nextPageable() : this.getPageable();
    }

    default public Pageable previousOrFirstPageable() {
        return this.hasPrevious() ? this.previousPageable() : this.getPageable();
    }
}


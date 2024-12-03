/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.data.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.util.Assert;

abstract class Chunk<T>
implements Slice<T>,
Serializable {
    private static final long serialVersionUID = 867755909294344406L;
    private final List<T> content = new ArrayList<T>();
    private final Pageable pageable;

    public Chunk(List<T> content, Pageable pageable) {
        Assert.notNull(content, (String)"Content must not be null!");
        Assert.notNull((Object)pageable, (String)"Pageable must not be null!");
        this.content.addAll(content);
        this.pageable = pageable;
    }

    @Override
    public int getNumber() {
        return this.pageable.isPaged() ? this.pageable.getPageNumber() : 0;
    }

    @Override
    public int getSize() {
        return this.pageable.isPaged() ? this.pageable.getPageSize() : this.content.size();
    }

    @Override
    public int getNumberOfElements() {
        return this.content.size();
    }

    @Override
    public boolean hasPrevious() {
        return this.getNumber() > 0;
    }

    @Override
    public boolean isFirst() {
        return !this.hasPrevious();
    }

    @Override
    public boolean isLast() {
        return !this.hasNext();
    }

    @Override
    public Pageable nextPageable() {
        return this.hasNext() ? this.pageable.next() : Pageable.unpaged();
    }

    @Override
    public Pageable previousPageable() {
        return this.hasPrevious() ? this.pageable.previousOrFirst() : Pageable.unpaged();
    }

    @Override
    public boolean hasContent() {
        return !this.content.isEmpty();
    }

    @Override
    public List<T> getContent() {
        return Collections.unmodifiableList(this.content);
    }

    @Override
    public Pageable getPageable() {
        return this.pageable;
    }

    @Override
    public Sort getSort() {
        return this.pageable.getSort();
    }

    @Override
    public Iterator<T> iterator() {
        return this.content.iterator();
    }

    protected <U> List<U> getConvertedContent(Function<? super T, ? extends U> converter) {
        Assert.notNull(converter, (String)"Function must not be null!");
        return this.stream().map(converter::apply).collect(Collectors.toList());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Chunk)) {
            return false;
        }
        Chunk that = (Chunk)obj;
        boolean contentEqual = this.content.equals(that.content);
        boolean pageableEqual = this.pageable.equals(that.pageable);
        return contentEqual && pageableEqual;
    }

    public int hashCode() {
        int result = 17;
        result += 31 * this.pageable.hashCode();
        return result += 31 * this.content.hashCode();
    }
}


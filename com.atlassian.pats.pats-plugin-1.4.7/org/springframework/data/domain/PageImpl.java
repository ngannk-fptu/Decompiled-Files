/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.data.domain;

import java.util.List;
import java.util.function.Function;
import org.springframework.data.domain.Chunk;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;

public class PageImpl<T>
extends Chunk<T>
implements Page<T> {
    private static final long serialVersionUID = 867755909294344406L;
    private final long total;

    public PageImpl(List<T> content, Pageable pageable, long total) {
        super(content, pageable);
        this.total = pageable.toOptional().filter(it -> !content.isEmpty()).filter(it -> it.getOffset() + (long)it.getPageSize() > total).map((? super T it) -> it.getOffset() + (long)content.size()).orElse(total);
    }

    public PageImpl(List<T> content) {
        this(content, Pageable.unpaged(), null == content ? 0L : (long)content.size());
    }

    @Override
    public int getTotalPages() {
        return this.getSize() == 0 ? 1 : (int)Math.ceil((double)this.total / (double)this.getSize());
    }

    @Override
    public long getTotalElements() {
        return this.total;
    }

    @Override
    public boolean hasNext() {
        return this.getNumber() + 1 < this.getTotalPages();
    }

    @Override
    public boolean isLast() {
        return !this.hasNext();
    }

    @Override
    public <U> Page<U> map(Function<? super T, ? extends U> converter) {
        return new PageImpl<U>(this.getConvertedContent(converter), this.getPageable(), this.total);
    }

    public String toString() {
        String contentType = "UNKNOWN";
        List content = this.getContent();
        if (!content.isEmpty() && content.get(0) != null) {
            contentType = content.get(0).getClass().getName();
        }
        return String.format("Page %s of %d containing %s instances", this.getNumber() + 1, this.getTotalPages(), contentType);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof PageImpl)) {
            return false;
        }
        PageImpl that = (PageImpl)obj;
        return this.total == that.total && super.equals(obj);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result += 31 * (int)(this.total ^ this.total >>> 32);
        return result += 31 * super.hashCode();
    }
}


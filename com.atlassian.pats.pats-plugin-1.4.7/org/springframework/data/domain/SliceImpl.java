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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.lang.Nullable;

public class SliceImpl<T>
extends Chunk<T> {
    private static final long serialVersionUID = 867755909294344406L;
    private final boolean hasNext;
    private final Pageable pageable;

    public SliceImpl(List<T> content, Pageable pageable, boolean hasNext) {
        super(content, pageable);
        this.hasNext = hasNext;
        this.pageable = pageable;
    }

    public SliceImpl(List<T> content) {
        this(content, Pageable.unpaged(), false);
    }

    @Override
    public boolean hasNext() {
        return this.hasNext;
    }

    @Override
    public <U> Slice<U> map(Function<? super T, ? extends U> converter) {
        return new SliceImpl<U>(this.getConvertedContent(converter), this.pageable, this.hasNext);
    }

    public String toString() {
        String contentType = "UNKNOWN";
        List content = this.getContent();
        if (content.size() > 0) {
            contentType = content.get(0).getClass().getName();
        }
        return String.format("Slice %d containing %s instances", this.getNumber(), contentType);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SliceImpl)) {
            return false;
        }
        SliceImpl that = (SliceImpl)obj;
        return this.hasNext == that.hasNext && super.equals(obj);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result += 31 * (this.hasNext ? 1 : 0);
        return result += 31 * super.hashCode();
    }
}


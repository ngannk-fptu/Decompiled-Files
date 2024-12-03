/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.domain;

import java.util.Collections;
import java.util.function.Function;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface Page<T>
extends Slice<T> {
    public static <T> Page<T> empty() {
        return Page.empty(Pageable.unpaged());
    }

    public static <T> Page<T> empty(Pageable pageable) {
        return new PageImpl(Collections.emptyList(), pageable, 0L);
    }

    public int getTotalPages();

    public long getTotalElements();

    @Override
    public <U> Page<U> map(Function<? super T, ? extends U> var1);
}


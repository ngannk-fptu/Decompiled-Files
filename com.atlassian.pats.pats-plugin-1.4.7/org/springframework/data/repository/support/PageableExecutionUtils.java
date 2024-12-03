/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.repository.support;

import java.util.List;
import java.util.function.LongSupplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Deprecated
public abstract class PageableExecutionUtils {
    private PageableExecutionUtils() {
    }

    public static <T> Page<T> getPage(List<T> content, Pageable pageable, LongSupplier totalSupplier) {
        return org.springframework.data.support.PageableExecutionUtils.getPage(content, pageable, totalSupplier);
    }
}


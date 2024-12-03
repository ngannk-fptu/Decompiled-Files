/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.data.support;

import java.util.List;
import java.util.function.LongSupplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.Assert;

public abstract class PageableExecutionUtils {
    private PageableExecutionUtils() {
    }

    public static <T> Page<T> getPage(List<T> content, Pageable pageable, LongSupplier totalSupplier) {
        Assert.notNull(content, (String)"Content must not be null!");
        Assert.notNull((Object)pageable, (String)"Pageable must not be null!");
        Assert.notNull((Object)totalSupplier, (String)"TotalSupplier must not be null!");
        if (pageable.isUnpaged() || pageable.getOffset() == 0L) {
            if (pageable.isUnpaged() || pageable.getPageSize() > content.size()) {
                return new PageImpl<T>(content, pageable, content.size());
            }
            return new PageImpl<T>(content, pageable, totalSupplier.getAsLong());
        }
        if (content.size() != 0 && pageable.getPageSize() > content.size()) {
            return new PageImpl<T>(content, pageable, pageable.getOffset() + (long)content.size());
        }
        return new PageImpl<T>(content, pageable, totalSupplier.getAsLong());
    }
}


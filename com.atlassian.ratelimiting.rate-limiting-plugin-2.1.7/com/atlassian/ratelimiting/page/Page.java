/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.ratelimiting.page;

import com.atlassian.ratelimiting.page.PageRequest;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Page<T> {
    public List<T> getContent();

    public int getPageNumber();

    public int getPageSize();

    public int getNumberOfElements();

    public int getTotalElements();

    public int getTotalPages();

    public boolean isFirst();

    public boolean isLast();

    public PageRequest getPageRequest();

    public PageRequest nextPageRequest();

    public PageRequest previousPageRequest();

    public <E> Page<E> map(Function<T, E> var1);

    public Page<T> filter(Predicate<? super T> var1);
}


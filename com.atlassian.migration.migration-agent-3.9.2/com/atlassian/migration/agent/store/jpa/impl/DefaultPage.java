/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store.jpa.impl;

import com.atlassian.migration.agent.store.jpa.Page;
import com.atlassian.migration.agent.store.jpa.Pageable;
import java.util.List;
import java.util.function.Function;

class DefaultPage<T>
implements Page<T> {
    private final List<T> content;
    private final Pageable pageable;
    private final Function<Pageable, Page<T>> nextPage;

    DefaultPage(List<T> content, Pageable pageable, Function<Pageable, Page<T>> nextPage) {
        this.content = content;
        this.pageable = pageable;
        this.nextPage = nextPage;
    }

    @Override
    public List<T> getContent() {
        return this.content;
    }

    @Override
    public Page<T> next() {
        return this.nextPage.apply(this.pageable.next());
    }
}


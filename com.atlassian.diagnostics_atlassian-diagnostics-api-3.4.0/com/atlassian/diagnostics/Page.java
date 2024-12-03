/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics;

import com.atlassian.diagnostics.PageRequest;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Consumer;
import javax.annotation.Nonnull;

public class Page<T>
implements Iterable<T> {
    private final PageRequest request;
    private final List<T> values;

    public Page(@Nonnull PageRequest request, @Nonnull List<T> values) {
        this.request = request;
        this.values = values;
    }

    @Override
    @Nonnull
    public Iterator<T> iterator() {
        return this.getValues().iterator();
    }

    @Override
    public void forEach(@Nonnull Consumer<? super T> action) {
        this.getValues().forEach(action);
    }

    @Nonnull
    public Optional<PageRequest> getNextRequest() {
        int limit = this.request.getLimit();
        if (this.values.size() > limit) {
            return Optional.of(PageRequest.of(this.request.getStart() + limit, limit));
        }
        return Optional.empty();
    }

    @Nonnull
    public Optional<PageRequest> getPrevRequest() {
        int start = this.request.getStart();
        if (start > 0) {
            int limit = this.request.getLimit();
            return Optional.of(PageRequest.of(Math.max(0, start - limit), limit));
        }
        return Optional.empty();
    }

    @Nonnull
    public PageRequest getRequest() {
        return this.request;
    }

    @Nonnull
    public List<T> getValues() {
        if (this.values.size() <= this.request.getLimit()) {
            return this.values;
        }
        return this.values.subList(0, this.request.getLimit());
    }

    public boolean isEmpty() {
        return this.values.isEmpty();
    }

    public int size() {
        return Math.min(this.request.getLimit(), this.values.size());
    }

    @Override
    @Nonnull
    public Spliterator<T> spliterator() {
        return this.getValues().spliterator();
    }
}


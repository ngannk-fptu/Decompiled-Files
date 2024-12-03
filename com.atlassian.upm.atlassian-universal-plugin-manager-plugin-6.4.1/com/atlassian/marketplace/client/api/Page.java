/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  javax.annotation.Nonnull
 */
package com.atlassian.marketplace.client.api;

import com.atlassian.marketplace.client.api.PageReader;
import com.atlassian.marketplace.client.api.PageReference;
import com.atlassian.marketplace.client.util.Convert;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.util.Iterator;
import java.util.Optional;
import javax.annotation.Nonnull;

public abstract class Page<T>
implements Iterable<T> {
    private final ImmutableList<T> items;
    private final int totalSize;
    protected final PageReader<T> reader;
    private static final Page<Object> EMPTY_PAGE = new FixedPage<Object>((Iterable<Object>)ImmutableList.of());

    public static <T> Page<T> empty() {
        return EMPTY_PAGE;
    }

    public static <T> Page<T> empty(Class<T> type) {
        return EMPTY_PAGE;
    }

    public static <T> Page<T> fromItems(Iterable<T> items) {
        return Iterables.isEmpty(items) ? Page.empty() : new FixedPage<T>(items);
    }

    protected Page(Iterable<T> items, int totalSize, PageReader<T> reader) {
        this.items = ImmutableList.copyOf((Iterable)((Iterable)Preconditions.checkNotNull(items, (Object)"items")));
        this.totalSize = totalSize;
        this.reader = (PageReader)Preconditions.checkNotNull(reader);
    }

    @Override
    @Nonnull
    public Iterator<T> iterator() {
        return this.items.iterator();
    }

    public int size() {
        return this.items.size();
    }

    public int totalSize() {
        return this.totalSize;
    }

    public abstract Optional<PageReference<T>> safeGetReference();

    public abstract Optional<PageReference<T>> safeGetPrevious();

    public abstract Optional<PageReference<T>> safeGetNext();

    public int getOffset() {
        Iterator<PageReference<T>> iterator = Convert.iterableOf(this.safeGetReference()).iterator();
        if (iterator.hasNext()) {
            PageReference<T> ref = iterator.next();
            return ref.getBounds().getOffset();
        }
        return 0;
    }

    private static final class FixedPage<T>
    extends Page<T> {
        FixedPage(Iterable<T> items) {
            super(ImmutableList.copyOf(items), Iterables.size(items), PageReader.stub());
        }

        @Override
        public Optional<PageReference<T>> safeGetReference() {
            return Optional.empty();
        }

        @Override
        public Optional<PageReference<T>> safeGetPrevious() {
            return Optional.empty();
        }

        @Override
        public Optional<PageReference<T>> safeGetNext() {
            return Optional.empty();
        }
    }
}


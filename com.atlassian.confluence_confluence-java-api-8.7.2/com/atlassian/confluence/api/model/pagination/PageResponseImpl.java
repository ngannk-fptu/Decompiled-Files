/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.google.common.base.Function
 *  com.google.common.base.Predicate
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.api.model.pagination;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.api.model.pagination.Cursor;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.google.common.base.Function;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class PageResponseImpl<T>
implements PageResponse<T> {
    private final PageRequest pageRequest;
    private List<T> wrappedList;
    private final boolean hasMore;
    private final Cursor nextCursor;
    private final Cursor prevCursor;

    private PageResponseImpl(@JsonProperty(value="hasMore") boolean hasMore) {
        this.pageRequest = null;
        this.hasMore = hasMore;
        this.nextCursor = null;
        this.prevCursor = null;
    }

    protected PageResponseImpl(Builder<T, ? extends Builder> builder) {
        this.wrappedList = Collections.unmodifiableList(((Builder)builder).list);
        this.hasMore = ((Builder)builder).hasMore;
        this.pageRequest = ((Builder)builder).request;
        this.nextCursor = ((Builder)builder).nextCursor;
        this.prevCursor = ((Builder)builder).prevCursor;
    }

    private void setResults(List<T> wrappedList) {
        this.wrappedList = Collections.unmodifiableList(wrappedList);
    }

    @Override
    public PageRequest getPageRequest() {
        return this.pageRequest;
    }

    @Override
    public Iterator<T> iterator() {
        return this.wrappedList.iterator();
    }

    @Override
    public int size() {
        return this.wrappedList.size();
    }

    @Override
    public List<T> getResults() {
        return this.wrappedList;
    }

    @Override
    public boolean hasMore() {
        return this.hasMore;
    }

    @Override
    public Cursor getNextCursor() {
        return this.nextCursor;
    }

    @Override
    public Cursor getPrevCursor() {
        return this.prevCursor;
    }

    public String toString() {
        return "PageResponseImpl{pageRequest=" + this.pageRequest + ", wrappedList=" + this.wrappedList + ", hasMore=" + this.hasMore + ", nextCursor=" + this.nextCursor + ", prevCursor=" + this.prevCursor + '}';
    }

    public static <T> Builder<T, ? extends Builder> from(Iterable<T> list, boolean hasMore) {
        Builder builder = new Builder();
        builder.addAll(list);
        builder.hasMore = hasMore;
        return builder;
    }

    public static <T> Builder<T, ? extends Builder> fromSingle(T element, boolean hasMore) {
        if (element != null) {
            return PageResponseImpl.from(Collections.singletonList(element), hasMore);
        }
        return PageResponseImpl.from(Collections.emptyList(), hasMore);
    }

    @Deprecated
    public static <F, T> PageResponseImpl<T> transform(PageResponse<F> input, Function<F, T> mapper) {
        return PageResponseImpl.transformResponse(input, mapper);
    }

    public static <F, T> PageResponseImpl<T> transformResponse(PageResponse<F> input, java.util.function.Function<F, T> mapper) {
        Iterable transformedItems = input.getResults().stream().map(mapper).collect(Collectors.toList());
        return ((Builder)((Builder)PageResponseImpl.from(transformedItems, input.hasMore()).pageRequest(input.getPageRequest()).nextCursor(input.getNextCursor())).prevCursor(input.getPrevCursor())).build();
    }

    public static <T> PageResponseImpl<T> empty(boolean hasMore) {
        return PageResponseImpl.from(Collections.emptyList(), hasMore).build();
    }

    public static <T> PageResponseImpl<T> empty(boolean hasMore, PageRequest request) {
        return PageResponseImpl.from(Collections.emptyList(), hasMore).pageRequest(request).build();
    }

    public static <T> PageResponseImpl<T> empty(boolean hasMore, LimitedRequest request) {
        return PageResponseImpl.from(Collections.emptyList(), hasMore).pageRequest(request).build();
    }

    @Deprecated
    public static <T> PageResponse<T> filteredPageResponse(LimitedRequest limitedRequest, List<T> items, com.google.common.base.Predicate<? super T> predicate) {
        if (predicate != null) {
            return PageResponseImpl.filteredResponse(limitedRequest, items, arg_0 -> predicate.apply(arg_0));
        }
        return PageResponseImpl.filteredResponse(limitedRequest, items, null);
    }

    public static <T> PageResponse<T> filteredResponse(LimitedRequest limitedRequest, List<T> items, Predicate<? super T> predicate) {
        boolean hasMore;
        boolean bl = hasMore = items.size() > limitedRequest.getLimit();
        if (predicate == null) {
            predicate = T -> true;
        }
        ArrayList<T> filteredItems = new ArrayList<T>();
        if (items.size() > limitedRequest.getLimit()) {
            items = items.subList(0, limitedRequest.getLimit());
        }
        for (T item : items) {
            if (!predicate.test(item)) continue;
            filteredItems.add(item);
        }
        return PageResponseImpl.from(filteredItems, hasMore).pageRequest(limitedRequest).build();
    }

    public static <T> PageResponse<T> filteredResponseWithCursor(LimitedRequest limitedRequest, List<T> items, Predicate<? super T> predicate, BiFunction<T, Boolean, Cursor> cursorCalculator, Comparator<T> ascComparator) {
        boolean hasMore;
        boolean bl = hasMore = items.size() > limitedRequest.getLimit();
        if (predicate == null) {
            predicate = T -> true;
        }
        ArrayList<T> filteredItems = new ArrayList<T>();
        if (items.size() > limitedRequest.getLimit()) {
            items = items.subList(0, limitedRequest.getLimit());
        }
        for (T item : items) {
            if (!predicate.test(item)) continue;
            filteredItems.add(item);
        }
        return PageResponseImpl.prepareResponseWithCursor(limitedRequest, hasMore, filteredItems, items, cursorCalculator, ascComparator);
    }

    @VisibleForTesting
    static <T> PageResponse<T> prepareResponseWithCursor(LimitedRequest originalRequest, boolean hasMore, List<T> filteredItems, List<T> itemsFetchedFromDB, BiFunction<T, Boolean, Cursor> cursorCalculator, Comparator<T> ascComparator) {
        Object largestItem;
        Object smallestItem;
        Object firstItem = null;
        Object lastItem = null;
        if (!filteredItems.isEmpty()) {
            firstItem = filteredItems.get(0);
            lastItem = filteredItems.get(filteredItems.size() - 1);
        } else if (!itemsFetchedFromDB.isEmpty()) {
            firstItem = itemsFetchedFromDB.get(0);
            lastItem = itemsFetchedFromDB.get(itemsFetchedFromDB.size() - 1);
        }
        if (originalRequest.getCursor().isReverse()) {
            smallestItem = lastItem;
            largestItem = firstItem;
        } else {
            smallestItem = firstItem;
            largestItem = lastItem;
        }
        Cursor prevCursor = smallestItem == null ? null : cursorCalculator.apply(smallestItem, true);
        Cursor nextCursor = largestItem == null ? null : cursorCalculator.apply(largestItem, false);
        List<T> sortedItems = originalRequest.getCursor().isReverse() ? filteredItems.stream().sorted(ascComparator).collect(Collectors.toList()) : filteredItems;
        return ((Builder)((Builder)PageResponseImpl.from(sortedItems, hasMore).pageRequest(originalRequest).prevCursor(prevCursor)).nextCursor(nextCursor)).build();
    }

    public static <T> Builder<T, ? extends Builder<T, ?>> builder() {
        return new Builder();
    }

    public static class Builder<E, B extends Builder<E, B>> {
        private boolean hasMore;
        private final List<E> list = new ArrayList();
        private PageRequest request;
        private Cursor nextCursor;
        private Cursor prevCursor;

        public PageResponseImpl<E> build() {
            return new PageResponseImpl(this);
        }

        public B add(E add) {
            this.list.add(add);
            return (B)this;
        }

        public B addAll(Iterable<E> toAdd) {
            toAdd.forEach(this.list::add);
            return (B)this;
        }

        public B pageRequest(PageRequest request) {
            this.request = request;
            return (B)this;
        }

        public B pageRequest(LimitedRequest limitedRequest) {
            this.request = new SimplePageRequest(limitedRequest);
            return (B)this;
        }

        public B hasMore(boolean hasMore) {
            this.hasMore = hasMore;
            return (B)this;
        }

        public B nextCursor(Cursor nextCursor) {
            this.nextCursor = nextCursor;
            return (B)this;
        }

        public B prevCursor(Cursor prevCursor) {
            this.prevCursor = prevCursor;
            return (B)this;
        }
    }
}


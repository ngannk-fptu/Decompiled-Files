/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.confluence.api.model.pagination.Cursor
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.nav.Navigation$Builder
 *  com.atlassian.confluence.api.nav.NavigationAware
 *  com.atlassian.confluence.api.nav.NavigationService
 */
package com.atlassian.confluence.rest.api.model;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.pagination.Cursor;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.nav.Navigation;
import com.atlassian.confluence.api.nav.NavigationAware;
import com.atlassian.confluence.api.nav.NavigationService;
import com.atlassian.confluence.rest.api.model.RestObject;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

@ExperimentalApi
public final class RestList<T>
extends RestObject
implements PageResponse<T>,
List<T>,
NavigationAware {
    private final PageRequest pageRequest;
    private List<T> results;
    private boolean hasMore;
    private Navigation.Builder navBuilder;
    private Cursor nextCursor;
    private Cursor prevCursor;

    protected RestList() {
        this.pageRequest = null;
    }

    private RestList(Builder<T> builder) {
        this.pageRequest = ((Builder)builder).pageRequest;
        this.results = ((Builder)builder).results;
        this.hasMore = ((Builder)builder).hasMore;
        this.navBuilder = ((Builder)builder).navBuilder;
        this.nextCursor = ((Builder)builder).nextCursor;
        this.prevCursor = ((Builder)builder).prevCursor;
    }

    public static <T> Builder<T> newRestList(PageRequest pageRequest) {
        return new Builder().pageRequest(pageRequest);
    }

    public static <T> Builder<T> newRestList(PageResponse<T> response) {
        Builder<T> builder = RestList.newRestList();
        return builder.results(response);
    }

    public static <T> Builder<T> newRestList() {
        return new Builder();
    }

    @Deprecated
    public static <T> RestList<T> createRestList(PageResponse<T> pageResponse) {
        return RestList.newRestList(pageResponse).build();
    }

    @Deprecated
    public static <T> RestList<T> createRestList(PageRequest pageRequest, PageResponse<T> pageResponse) {
        return RestList.newRestList(pageResponse).pageRequest(pageRequest).build();
    }

    @Deprecated
    public static <T> RestList<T> createRestList(PageRequest request, List<T> results, boolean hasMore) {
        Builder<T> builder = RestList.newRestList(request);
        return builder.results(results, hasMore).build();
    }

    public PageRequest getPageRequest() {
        return this.pageRequest;
    }

    public PageResponse<T> getPageResponse() {
        return this;
    }

    public String toString() {
        return "RestList{pageRequest=" + this.pageRequest + ", results=" + this.results + ", hasMore=" + this.hasMore + '}';
    }

    public Navigation.Builder resolveNavigation(NavigationService navigationService) {
        return this.navBuilder;
    }

    @Override
    public Iterator<T> iterator() {
        return this.results.iterator();
    }

    @Override
    public Object[] toArray() {
        return this.results.toArray();
    }

    @Override
    public boolean add(T o) {
        return this.results.add(o);
    }

    @Override
    public boolean remove(Object o) {
        return this.results.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return this.results.containsAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return this.results.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return this.results.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return this.results.retainAll(c);
    }

    @Override
    public void clear() {
        this.results.clear();
    }

    @Override
    public T get(int index) {
        return this.results.get(index);
    }

    @Override
    public T set(int index, T element) {
        return this.results.set(index, element);
    }

    @Override
    public void add(int index, T element) {
        this.results.add(index, element);
    }

    @Override
    public T remove(int index) {
        return this.results.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return this.results.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return this.results.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return this.results.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return this.results.listIterator(index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return null;
    }

    @Override
    public <A> A[] toArray(A[] a) {
        return this.results.toArray(a);
    }

    public List<T> getResults() {
        return this.results;
    }

    @Override
    public int size() {
        return this.results.size();
    }

    @Override
    public boolean isEmpty() {
        return this.results.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.results.contains(o);
    }

    public boolean hasMore() {
        return this.hasMore;
    }

    public Cursor getNextCursor() {
        return this.nextCursor;
    }

    public Cursor getPrevCursor() {
        return this.prevCursor;
    }

    @Override
    public boolean equals(Object obj) {
        return this.results.equals(obj);
    }

    @Override
    public int hashCode() {
        return this.results.hashCode();
    }

    public static class Builder<T> {
        private PageRequest pageRequest;
        private List<T> results = Collections.emptyList();
        private boolean hasMore = false;
        private Navigation.Builder navBuilder = null;
        private Cursor nextCursor = null;
        private Cursor prevCursor = null;

        public Builder<T> pageRequest(PageRequest pageRequest) {
            this.pageRequest = pageRequest;
            return this;
        }

        public Builder<T> results(List<T> results, boolean hasMore) {
            this.results = results;
            this.hasMore = hasMore;
            this.nextCursor = null;
            this.prevCursor = null;
            return this;
        }

        public Builder<T> results(List<T> results, Cursor nextCursor, Cursor prevCursor, boolean hasMore) {
            this.results = results;
            this.hasMore = hasMore;
            this.nextCursor = nextCursor;
            this.prevCursor = prevCursor;
            return this;
        }

        public Builder<T> results(PageResponse<T> pageResponse) {
            this.results = pageResponse.getResults();
            this.pageRequest = pageResponse.getPageRequest();
            this.hasMore = pageResponse.hasMore();
            this.nextCursor = pageResponse.getNextCursor();
            this.prevCursor = pageResponse.getPrevCursor();
            return this;
        }

        public Builder<T> navigationAware(Navigation.Builder navBuilder) {
            this.navBuilder = navBuilder;
            return this;
        }

        public RestList<T> build() {
            return new RestList(this);
        }
    }
}


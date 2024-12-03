/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.business.insights.core.rest.model;

import com.atlassian.business.insights.core.rest.model.PageRequest;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class Page<T> {
    private boolean lastPage;
    private int limit;
    private PageRequest nextPageRequest;
    private int offset;
    private int size;
    private List<T> values;

    public Page() {
    }

    @JsonCreator
    public Page(@JsonProperty(value="limit") int limit, @JsonProperty(value="nextPageRequest") PageRequest nextPageRequest, @JsonProperty(value="offset") int offset, @JsonProperty(value="lastPage") boolean lastPage, @JsonProperty(value="size") int size, @JsonProperty(value="values") List<T> values) {
        this.limit = limit;
        this.nextPageRequest = nextPageRequest;
        this.offset = offset;
        this.lastPage = lastPage;
        this.size = size;
        this.values = values;
    }

    public Page(List<T> values, int offset, int limit, boolean lastPage) {
        this.values = Collections.unmodifiableList(Objects.requireNonNull(values));
        this.offset = offset;
        this.limit = limit;
        this.lastPage = lastPage;
        this.size = values.size();
        this.nextPageRequest = !lastPage ? new PageRequest(offset + this.size, limit) : null;
    }

    @JsonProperty(value="limit")
    public int getLimit() {
        return this.limit;
    }

    @JsonProperty(value="nextPageRequest")
    @Nullable
    public PageRequest getNextPageRequest() {
        return this.nextPageRequest;
    }

    @JsonProperty(value="offset")
    public int getOffset() {
        return this.offset;
    }

    @JsonProperty(value="size")
    public int getSize() {
        return this.size;
    }

    @JsonProperty(value="values")
    @Nonnull
    public List<T> getValues() {
        return this.values;
    }

    @JsonProperty(value="lastPage")
    public boolean isLastPage() {
        return this.lastPage;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Page)) {
            return false;
        }
        Page page = (Page)o;
        return this.lastPage == page.lastPage && this.limit == page.limit && this.offset == page.offset && this.size == page.size && Objects.equals(this.nextPageRequest, page.nextPageRequest) && Objects.equals(this.values, page.values);
    }

    public int hashCode() {
        return Objects.hash(this.lastPage, this.limit, this.nextPageRequest, this.offset, this.size, this.values);
    }
}


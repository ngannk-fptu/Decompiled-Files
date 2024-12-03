/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.audit.api.util.pagination;

import com.atlassian.audit.api.util.pagination.PageRequest;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;

public class Page<T, C> {
    private final List<T> values;
    private final PageRequest<C> nextPageRequest;

    private Page(Builder<T, C> builder) {
        this.values = ((Builder)builder).values;
        this.nextPageRequest = ((Builder)builder).nextPageRequest;
    }

    public boolean getIsLastPage() {
        return this.nextPageRequest == null;
    }

    @Nonnull
    public Optional<PageRequest<C>> getNextPageRequest() {
        return Optional.ofNullable(this.nextPageRequest);
    }

    public int getSize() {
        return this.values.size();
    }

    @Nonnull
    public List<T> getValues() {
        return this.values;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Page page = (Page)o;
        return this.values.equals(page.values) && Objects.equals(this.nextPageRequest, page.nextPageRequest);
    }

    public int hashCode() {
        return Objects.hash(this.values, this.nextPageRequest);
    }

    public static <T, C> Page<T, C> emptyPage() {
        return new Builder(Collections.emptyList(), true).build();
    }

    public static class Builder<T, C> {
        private PageRequest<C> nextPageRequest;
        private boolean lastPage;
        private final List<T> values;

        public Builder(@Nonnull List<T> values, boolean lastPage) {
            this.values = Collections.unmodifiableList(Objects.requireNonNull(values));
            this.lastPage = lastPage;
        }

        @Nonnull
        public Builder<T, C> nextPageRequest(PageRequest<C> nextPageRequest) {
            this.nextPageRequest = nextPageRequest;
            return this;
        }

        @Nonnull
        public Page<T, C> build() {
            if (!this.lastPage) {
                Objects.requireNonNull(this.nextPageRequest, "nextPageRequest should be non-null except for last page");
            } else {
                this.nextPageRequest = null;
            }
            return new Page(this);
        }
    }
}


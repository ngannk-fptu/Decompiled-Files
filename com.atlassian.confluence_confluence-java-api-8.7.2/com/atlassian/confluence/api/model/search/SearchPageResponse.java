/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.fugue.Option
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  org.codehaus.jackson.annotate.JsonIgnore
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.api.model.search;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.serialization.RestEnrichable;
import com.atlassian.confluence.api.util.FugueConversionUtil;
import com.atlassian.fugue.Option;
import com.google.common.collect.ImmutableList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@ExperimentalApi
@JsonIgnoreProperties(ignoreUnknown=true)
@RestEnrichable
public class SearchPageResponse<T>
implements PageResponse<T> {
    @JsonProperty
    private final List<T> results;
    @JsonProperty
    private final boolean hasMore;
    @JsonProperty
    private final String cqlQuery;
    private final PageRequest pageRequest;
    @JsonProperty
    private final int totalSize;
    @JsonProperty
    private final int searchDuration;
    @JsonProperty
    private final Optional<Integer> archivedResultCount;

    private SearchPageResponse(Builder<T> builder) {
        this.results = builder.results.build();
        this.hasMore = builder.hasMore;
        this.cqlQuery = builder.cqlQuery;
        this.pageRequest = builder.pageRequest;
        this.totalSize = builder.totalSize;
        this.searchDuration = builder.searchDuration;
        this.archivedResultCount = FugueConversionUtil.toOptional(builder.archivedResultCount);
    }

    @Override
    public List<T> getResults() {
        return this.results;
    }

    @Override
    public int size() {
        return this.results.size();
    }

    public int totalSize() {
        return this.totalSize;
    }

    public int getSearchDuration() {
        return this.searchDuration;
    }

    @Deprecated
    @JsonIgnore
    public Option<Integer> getArchivedResultCount() {
        return FugueConversionUtil.toComOption(this.archivedResultCount);
    }

    @JsonProperty(value="archivedResultCount")
    public Optional<Integer> archivedResultCount() {
        return this.archivedResultCount;
    }

    @Override
    public boolean hasMore() {
        return this.hasMore;
    }

    @Override
    public PageRequest getPageRequest() {
        return this.pageRequest;
    }

    public String getCqlQuery() {
        return this.cqlQuery;
    }

    @Override
    public Iterator<T> iterator() {
        return this.results.iterator();
    }

    public static <T> Builder<T> builder() {
        return new Builder();
    }

    public static class Builder<T> {
        @Deprecated
        public ImmutableList.Builder<T> results = ImmutableList.builder();
        public boolean hasMore;
        public String cqlQuery = "";
        public PageRequest pageRequest;
        public int totalSize;
        public int searchDuration;
        @Deprecated
        public Option<Integer> archivedResultCount = Option.none();

        private Builder() {
        }

        public SearchPageResponse<T> build() {
            return new SearchPageResponse(this);
        }

        public Builder<T> result(Iterable<T> results) {
            this.results.addAll(results);
            return this;
        }

        public Builder<T> hasMore(boolean hasMore) {
            this.hasMore = hasMore;
            return this;
        }

        public Builder<T> totalSize(int size) {
            this.totalSize = size;
            return this;
        }

        public Builder<T> cqlQuery(String query) {
            this.cqlQuery = query;
            return this;
        }

        public Builder<T> pageRequest(PageRequest pageRequest) {
            this.pageRequest = pageRequest;
            return this;
        }

        public Builder<T> searchDuration(int duration) {
            this.searchDuration = duration;
            return this;
        }

        @Deprecated
        public Builder<T> archivedResultCount(Option<Integer> archivedResultCount) {
            this.archivedResultCount = archivedResultCount;
            return this;
        }

        public Builder<T> withArchivedResultCount(Optional<Integer> archivedResultCount) {
            this.archivedResultCount = FugueConversionUtil.toComOption(archivedResultCount);
            return this;
        }
    }
}


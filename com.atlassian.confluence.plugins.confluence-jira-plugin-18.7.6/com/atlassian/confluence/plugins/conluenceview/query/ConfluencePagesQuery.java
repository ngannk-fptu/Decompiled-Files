/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.conluenceview.query;

import com.atlassian.confluence.plugins.conluenceview.query.PagingQuery;
import java.util.List;

public class ConfluencePagesQuery
extends PagingQuery {
    private List<Long> pageIds;
    private String cacheToken;
    private String searchString;
    private String spaceKey;

    private ConfluencePagesQuery(Builder builder) {
        super(builder);
        this.cacheToken = builder.cacheToken;
        this.pageIds = builder.pageIds;
        this.searchString = builder.searchString;
        this.spaceKey = builder.spaceKey;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public List<Long> getPageIds() {
        return this.pageIds;
    }

    public String getCacheToken() {
        return this.cacheToken;
    }

    public String getSearchString() {
        return this.searchString;
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }

    public static final class Builder
    extends PagingQuery.Builder {
        private String cacheToken;
        private List<Long> pageIds;
        private String searchString;
        private String spaceKey;

        private Builder() {
        }

        public Builder withCacheToken(String cacheToken) {
            this.cacheToken = cacheToken;
            return this;
        }

        public Builder withPageIds(List<Long> pageIds) {
            this.pageIds = pageIds;
            return this;
        }

        public Builder withSearchString(String searchString) {
            this.searchString = searchString;
            return this;
        }

        public Builder withSpaceKey(String spaceKey) {
            this.spaceKey = spaceKey;
            return this;
        }

        @Override
        public Builder withLimit(Integer limit) {
            super.withLimit(limit);
            return this;
        }

        @Override
        public Builder withStart(Integer start) {
            super.withStart(start);
            return this;
        }

        public ConfluencePagesQuery build() {
            return new ConfluencePagesQuery(this);
        }
    }
}


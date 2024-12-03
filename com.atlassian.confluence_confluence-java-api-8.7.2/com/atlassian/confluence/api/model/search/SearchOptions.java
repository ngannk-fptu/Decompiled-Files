/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.api.model.search;

import com.atlassian.confluence.api.model.BaseApiEnum;
import com.atlassian.confluence.api.model.search.SearchContext;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SearchOptions {
    private final SearchContext searchContext;
    private final Excerpt excerptStrategy;
    private final boolean includeArchivedSpaces;
    private final boolean fireSearchPerformed;

    public SearchOptions(Builder builder) {
        this.excerptStrategy = builder.excerptStrategy;
        this.searchContext = builder.searchContext;
        this.includeArchivedSpaces = builder.includeArchivedSpaces;
        this.fireSearchPerformed = builder.fireSearchPerformed;
    }

    public Excerpt getExcerptStrategy() {
        return this.excerptStrategy;
    }

    public SearchContext getSearchContext() {
        return this.searchContext;
    }

    public boolean isIncludeArchivedSpaces() {
        return this.includeArchivedSpaces;
    }

    public boolean isFireSearchPerformed() {
        return this.fireSearchPerformed;
    }

    public static SearchOptions buildDefault() {
        return SearchOptions.builder().searchContext(SearchContext.EMPTY).excerptStrategy(Excerpt.NONE).includeArchivedSpaces(false).build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Excerpt excerptStrategy = Excerpt.NONE;
        private SearchContext searchContext = SearchContext.EMPTY;
        private boolean includeArchivedSpaces = false;
        private boolean fireSearchPerformed = false;

        private Builder() {
        }

        public Builder excerptStrategy(Excerpt excerpt) {
            this.excerptStrategy = excerpt;
            return this;
        }

        public Builder excerptStrategy(String excerptStrategyName) {
            int indexOf = Excerpt.BUILT_IN.indexOf(new Excerpt(excerptStrategyName));
            if (indexOf >= 0) {
                this.excerptStrategy(Excerpt.BUILT_IN.get(indexOf));
            } else {
                this.excerptStrategy(Excerpt.NONE);
            }
            return this;
        }

        public Builder searchContext(SearchContext searchContext) {
            this.searchContext = searchContext;
            return this;
        }

        public Builder includeArchivedSpaces(boolean includeArchivedSpaces) {
            this.includeArchivedSpaces = includeArchivedSpaces;
            return this;
        }

        public Builder fireSearchPerformed(boolean fireIt) {
            this.fireSearchPerformed = fireIt;
            return this;
        }

        public SearchOptions build() {
            return new SearchOptions(this);
        }
    }

    public static class Excerpt
    extends BaseApiEnum {
        public static final Excerpt HIGHLIGHT = new Excerpt("highlight");
        public static final Excerpt INDEXED = new Excerpt("indexed");
        public static final Excerpt NONE = new Excerpt("none");
        public static final Excerpt HIGHLIGHT_UNESCAPED = new Excerpt("highlight_unescaped");
        public static final Excerpt INDEXED_UNESCAPED = new Excerpt("indexed_unescaped");
        public static final List<Excerpt> BUILT_IN = Collections.unmodifiableList(Arrays.asList(HIGHLIGHT, INDEXED, NONE, HIGHLIGHT_UNESCAPED, INDEXED_UNESCAPED));

        protected Excerpt(String value) {
            super(value);
        }
    }
}


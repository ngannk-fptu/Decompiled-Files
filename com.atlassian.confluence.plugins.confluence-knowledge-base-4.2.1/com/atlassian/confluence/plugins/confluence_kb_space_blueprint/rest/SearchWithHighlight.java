/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.v2.HightlightParams
 *  com.atlassian.confluence.search.v2.ISearch
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.SearchSort
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.plugins.confluence_kb_space_blueprint.rest;

import com.atlassian.confluence.search.v2.HightlightParams;
import com.atlassian.confluence.search.v2.ISearch;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SearchSort;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;

public class SearchWithHighlight
implements ISearch {
    HightlightParams hightlightParams = new HightlightParams(){

        public String getPreTag() {
            return "@@@hl@@@";
        }

        public String getPostTag() {
            return "@@@endhl@@@";
        }

        public String getEncoder() {
            return "none";
        }

        public SearchQuery getQuery() {
            return super.getQuery();
        }
    };
    private final ISearch search;
    private final boolean highlight;

    public SearchWithHighlight(ISearch search, boolean highlight) {
        this.search = search;
        this.highlight = highlight;
    }

    public @NonNull SearchQuery getQuery() {
        return this.search.getQuery();
    }

    public SearchSort getSort() {
        return this.search.getSort();
    }

    public int getStartOffset() {
        return this.search.getStartOffset();
    }

    public int getLimit() {
        return this.search.getLimit();
    }

    public Optional<HightlightParams> getHighlight() {
        if (this.highlight) {
            return Optional.of(this.hightlightParams);
        }
        return Optional.empty();
    }
}


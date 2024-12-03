/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.plugins.search.actions;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.plugins.search.api.Searcher;
import com.atlassian.confluence.plugins.search.api.model.SearchExplanation;
import com.atlassian.confluence.plugins.search.api.model.SearchQueryParameters;
import com.atlassian.confluence.plugins.search.api.model.SearchResults;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;

public class ExplainAction
extends ConfluenceActionSupport {
    static final long serialVersionUID = 1L;
    private transient Searcher searcherv3;
    private String queryString;
    private long[] contentIds;
    private transient List<SearchExplanation> explanationsForSpecifiedContentIds;
    private transient SearchResults searchResults;

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() {
        SearchQueryParameters queryParameters = new SearchQueryParameters.Builder(this.queryString).build();
        if (this.contentIds != null) {
            this.explanationsForSpecifiedContentIds = this.searcherv3.explain(queryParameters, this.contentIds);
        }
        this.searchResults = this.searcherv3.search(queryParameters, true);
        return "success";
    }

    public String fixExplanation(@Nullable String explanation) {
        return explanation == null ? "" : explanation.replaceAll("\n\\), product of", "\\), product of");
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public String getQueryString() {
        return this.queryString;
    }

    public void setSearcherv3(Searcher searcherv3) {
        this.searcherv3 = searcherv3;
    }

    public SearchResults getSearchResults() {
        return this.searchResults;
    }

    public List<SearchExplanation> getExplanationsForSpecifiedContentIds() {
        return this.explanationsForSpecifiedContentIds;
    }

    public void setContentIds(@Nullable long[] contentIds) {
        if (contentIds != null) {
            this.contentIds = Arrays.copyOf(contentIds, contentIds.length);
        }
    }
}


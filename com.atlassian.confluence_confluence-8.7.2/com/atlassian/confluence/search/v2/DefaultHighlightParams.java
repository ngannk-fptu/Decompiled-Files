/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2;

import com.atlassian.confluence.search.v2.HightlightParams;
import com.atlassian.confluence.search.v2.SearchQuery;

public class DefaultHighlightParams
implements HightlightParams {
    public static final String DEFAULT_ENCODER = "html";
    public static final String NONE_ENCODER = "none";
    private final String encoder;
    private final SearchQuery query;

    public DefaultHighlightParams(SearchQuery query) {
        this(DEFAULT_ENCODER, query);
    }

    public DefaultHighlightParams(String encoder, SearchQuery query) {
        this.encoder = encoder;
        this.query = query;
    }

    @Override
    public String getPreTag() {
        return "@@@hl@@@";
    }

    @Override
    public String getPostTag() {
        return "@@@endhl@@@";
    }

    @Override
    public String getEncoder() {
        return this.encoder;
    }

    @Override
    public SearchQuery getQuery() {
        return this.query;
    }
}


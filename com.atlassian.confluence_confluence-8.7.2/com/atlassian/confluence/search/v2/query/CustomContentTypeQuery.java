/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2.query;

import com.atlassian.confluence.internal.search.v2.CustomContentTypeQueryFactory;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.ConstantScoreQuery;
import com.atlassian.confluence.search.v2.query.TermQuery;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class CustomContentTypeQuery
implements SearchQuery {
    private static final String KEY = "customContentType";
    private final Set<String> pluginKeys;

    public CustomContentTypeQuery(String ... pluginKeys) {
        this(Arrays.asList(pluginKeys));
    }

    public CustomContentTypeQuery(Collection<String> pluginKeys) {
        if (pluginKeys == null) {
            throw new IllegalArgumentException("pluginKeys should not be null");
        }
        if (pluginKeys.isEmpty()) {
            throw new IllegalArgumentException("pluginKeys should not be an empty list");
        }
        if (pluginKeys.contains(null)) {
            throw new IllegalArgumentException("pluginKeys should not contain a null value");
        }
        this.pluginKeys = new HashSet<String>(pluginKeys);
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public List<String> getParameters() {
        return new ArrayList<String>(this.pluginKeys);
    }

    public Set<String> getPluginKeys() {
        return this.pluginKeys;
    }

    @Override
    public SearchQuery expand() {
        SearchQuery toWrap = (SearchQuery)new CustomContentTypeQueryFactory(this.pluginKeys, BooleanQuery::builder, TermQuery::builder).create();
        return new ConstantScoreQuery(toWrap);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        CustomContentTypeQuery that = (CustomContentTypeQuery)o;
        return Objects.equals(this.pluginKeys, that.pluginKeys);
    }

    public int hashCode() {
        return Objects.hash(this.pluginKeys);
    }
}


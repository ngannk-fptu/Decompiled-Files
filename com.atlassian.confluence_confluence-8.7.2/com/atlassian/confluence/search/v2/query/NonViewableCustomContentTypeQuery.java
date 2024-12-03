/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.plugin.PluginAccessor
 */
package com.atlassian.confluence.search.v2.query;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.impl.search.v2.searchfilter.NonViewableContentTypeSupplier;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.AllQuery;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.CustomContentTypeQuery;
import com.atlassian.plugin.PluginAccessor;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class NonViewableCustomContentTypeQuery
implements SearchQuery {
    private final NonViewableContentTypeSupplier nonViewableContentTypeSupplier;
    public static final String KEY = "nonViewableCustomContentType";

    public NonViewableCustomContentTypeQuery(PluginAccessor pluginAccessor) {
        this.nonViewableContentTypeSupplier = new NonViewableContentTypeSupplier(pluginAccessor);
    }

    @VisibleForTesting
    protected NonViewableCustomContentTypeQuery(NonViewableContentTypeSupplier nonViewableContentTypeSupplier) {
        this.nonViewableContentTypeSupplier = nonViewableContentTypeSupplier;
    }

    @Override
    public SearchQuery expand() {
        BooleanQuery.Builder queryBuilder = BooleanQuery.builder();
        Object nonViewableContentTypes = this.nonViewableContentTypeSupplier.get();
        if (!nonViewableContentTypes.isEmpty()) {
            queryBuilder.addMustNot(new CustomContentTypeQuery((Collection<String>)nonViewableContentTypes));
            return queryBuilder.build();
        }
        return AllQuery.getInstance();
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public List getParameters() {
        return Collections.emptyList();
    }
}


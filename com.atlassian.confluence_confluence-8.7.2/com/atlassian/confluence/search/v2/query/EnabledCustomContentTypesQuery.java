/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.plugin.PluginAccessor
 */
package com.atlassian.confluence.search.v2.query;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.impl.search.v2.searchfilter.EnabledContentTypeSupplier;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.AllQuery;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.CustomContentTypeQuery;
import com.atlassian.confluence.search.v2.query.FieldExistsQuery;
import com.atlassian.plugin.PluginAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class EnabledCustomContentTypesQuery
implements SearchQuery {
    private final Supplier<Set<String>> enabledCustomContentTypeSupplier;

    public EnabledCustomContentTypesQuery(PluginAccessor pluginAccessor) {
        this.enabledCustomContentTypeSupplier = new EnabledContentTypeSupplier(pluginAccessor);
    }

    @VisibleForTesting
    protected EnabledCustomContentTypesQuery(Supplier<Set<String>> enabledCustomContentTypeSupplier) {
        this.enabledCustomContentTypeSupplier = enabledCustomContentTypeSupplier;
    }

    @Override
    public String getKey() {
        return "enabledCustomContentTypes";
    }

    @Override
    public List getParameters() {
        return new ArrayList(this.enabledCustomContentTypeSupplier.get());
    }

    @Override
    public SearchQuery expand() {
        Set<String> enabledContentTypes = this.enabledCustomContentTypeSupplier.get();
        if (enabledContentTypes.isEmpty()) {
            return AllQuery.getInstance();
        }
        BooleanQuery.Builder boolQuery = new BooleanQuery.Builder();
        boolQuery.addShould(new CustomContentTypeQuery(enabledContentTypes));
        boolQuery.addShould(FieldExistsQuery.fieldNotExistsQuery(SearchFieldNames.CONTENT_PLUGIN_KEY));
        return boolQuery.build();
    }
}


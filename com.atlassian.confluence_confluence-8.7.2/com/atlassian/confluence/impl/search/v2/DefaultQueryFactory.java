/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 */
package com.atlassian.confluence.impl.search.v2;

import com.atlassian.confluence.impl.plugin.descriptor.search.SearchQueryModuleDescriptor;
import com.atlassian.confluence.search.v2.InvalidQueryException;
import com.atlassian.confluence.search.v2.QueryFactory;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.plugin.PluginAccessor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;

public class DefaultQueryFactory
implements QueryFactory {
    private final PluginAccessor pluginAccessor;
    private static final String BUILTIN_SEARCH_PLUGIN_KEY = "confluence.search.builtin";

    public DefaultQueryFactory(PluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;
    }

    @Override
    public SearchQuery newQuery(String queryKey) throws InvalidQueryException {
        return this.newQuery(queryKey, Collections.EMPTY_LIST);
    }

    @Override
    public SearchQuery newQuery(String queryKey, List parameters) throws InvalidQueryException {
        SearchQueryModuleDescriptor moduleDescriptor;
        if (!((String)queryKey).contains(":")) {
            queryKey = "confluence.search.builtin:" + (String)queryKey;
        }
        if ((moduleDescriptor = (SearchQueryModuleDescriptor)this.pluginAccessor.getEnabledPluginModule((String)queryKey)) == null) {
            throw new InvalidQueryException("Unable to find query plugin module: " + (String)queryKey);
        }
        Class moduleClass = moduleDescriptor.getModuleClass();
        try {
            return moduleDescriptor.newQuery(parameters);
        }
        catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            throw new InvalidQueryException("Unable to instantiate query module class: " + moduleClass.getName() + " : " + e.toString());
        }
    }
}


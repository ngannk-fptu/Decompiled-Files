/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceSystemProperties
 *  com.atlassian.confluence.core.DataSourceFactory
 *  com.atlassian.confluence.core.InputStreamSerializer
 *  com.atlassian.confluence.core.PluginDataSourceFactory
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.soy.renderer.SoyServerFunction
 *  com.google.common.collect.ImmutableSet
 *  javax.activation.DataSource
 */
package com.atlassian.confluence.plugins.soy;

import com.atlassian.confluence.core.ConfluenceSystemProperties;
import com.atlassian.confluence.core.DataSourceFactory;
import com.atlassian.confluence.core.InputStreamSerializer;
import com.atlassian.confluence.core.PluginDataSourceFactory;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.soy.renderer.SoyServerFunction;
import com.google.common.collect.ImmutableSet;
import java.util.Optional;
import java.util.Set;
import javax.activation.DataSource;

public class ImportResourceFunction
implements SoyServerFunction<String> {
    private static final ImmutableSet<Integer> VALID_ARGUMENT_SIZES = ImmutableSet.of((Object)2);
    private final DataSourceFactory dataSourceFactory;

    public ImportResourceFunction(DataSourceFactory dataSourceFactory) {
        this.dataSourceFactory = dataSourceFactory;
    }

    public String getName() {
        return "importResource";
    }

    public String apply(Object ... args) {
        ModuleCompleteKey moduleCompleteKey = new ModuleCompleteKey((String)args[0]);
        String resourceName = (String)args[1];
        Optional maybePlugin = this.dataSourceFactory.createForPlugin(moduleCompleteKey.getPluginKey());
        if (!maybePlugin.isPresent() && !ConfluenceSystemProperties.isDevMode()) {
            return "";
        }
        Optional maybeResource = ((PluginDataSourceFactory)maybePlugin.get()).getResourceFromModuleByName(moduleCompleteKey.getModuleKey(), resourceName);
        if (!maybeResource.isPresent() && !ConfluenceSystemProperties.isDevMode()) {
            return "";
        }
        return InputStreamSerializer.eagerInDevMode().addDataSource(new DataSource[]{(DataSource)maybeResource.get()}).toString();
    }

    public Set<Integer> validArgSizes() {
        return VALID_ARGUMENT_SIZES;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.botocss.Botocss
 *  com.atlassian.confluence.core.ConfluenceSystemProperties
 *  com.atlassian.confluence.core.DataSourceFactory
 *  com.atlassian.confluence.core.InputStreamSerializer
 *  com.atlassian.confluence.core.PluginDataSourceFactory
 *  com.atlassian.confluence.util.collections.Range
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.soy.renderer.SoyServerFunction
 *  com.google.common.collect.ImmutableSet
 *  javax.activation.DataSource
 */
package com.atlassian.confluence.plugins.email.soy;

import com.atlassian.botocss.Botocss;
import com.atlassian.confluence.core.ConfluenceSystemProperties;
import com.atlassian.confluence.core.DataSourceFactory;
import com.atlassian.confluence.core.InputStreamSerializer;
import com.atlassian.confluence.core.PluginDataSourceFactory;
import com.atlassian.confluence.util.collections.Range;
import com.atlassian.fugue.Maybe;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.soy.renderer.SoyServerFunction;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.activation.DataSource;

public class InjectCssInlineFunction
implements SoyServerFunction<String> {
    private static final ImmutableSet<Integer> VALID_SIZES = ImmutableSet.copyOf((Iterable)Range.range((int)2, (int)4));
    private final DataSourceFactory dataSourceFactory;

    public InjectCssInlineFunction(DataSourceFactory dataSourceFactory) {
        this.dataSourceFactory = dataSourceFactory;
    }

    public String getName() {
        return "injectCssInline";
    }

    public String apply(Object ... args) {
        Arguments arguments = this.makeArguments(args);
        InputStreamSerializer serializer = InputStreamSerializer.eagerInDevMode();
        for (Map.Entry<String, Map<String, List<String>>> pluginEntries : arguments.getResourceModuleEntries()) {
            String pluginKey = pluginEntries.getKey();
            Map<String, List<String>> modules = pluginEntries.getValue();
            for (Map.Entry<String, List<String>> module : modules.entrySet()) {
                ModuleCompleteKey moduleCompleteKey = new ModuleCompleteKey(pluginKey, module.getKey());
                for (String resourceName : module.getValue()) {
                    Maybe maybeResource;
                    Maybe maybePlugin = this.dataSourceFactory.forPlugin(moduleCompleteKey.getPluginKey());
                    if (!maybePlugin.isDefined() && !ConfluenceSystemProperties.isDevMode() || !(maybeResource = ((PluginDataSourceFactory)maybePlugin.get()).resourceFromModuleByName(moduleCompleteKey.getModuleKey(), resourceName)).isDefined() && !ConfluenceSystemProperties.isDevMode()) continue;
                    serializer.addDataSource(new DataSource[]{(DataSource)maybeResource.get()});
                }
            }
        }
        return Botocss.inject((String)arguments.getHtml(), (String[])new String[]{serializer.toString()});
    }

    private Arguments makeArguments(Object[] args) {
        return new Arguments(args);
    }

    public Set<Integer> validArgSizes() {
        return VALID_SIZES;
    }

    private static class Arguments {
        private final Map<String, Map<String, List<String>>> resourceModules;
        private String html;

        public Arguments(Object[] args) {
            this.html = args[0].toString();
            if (!(args[1] instanceof Map)) {
                throw new IllegalArgumentException("the 2nd parameter to injectCSsInline() needs to be a map literal");
            }
            this.resourceModules = (Map)args[1];
        }

        public String getHtml() {
            return this.html;
        }

        public Set<Map.Entry<String, Map<String, List<String>>>> getResourceModuleEntries() {
            return this.resourceModules.entrySet();
        }
    }
}


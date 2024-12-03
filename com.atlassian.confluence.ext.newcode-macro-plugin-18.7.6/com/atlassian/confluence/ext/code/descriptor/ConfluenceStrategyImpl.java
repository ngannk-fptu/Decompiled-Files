/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.elements.ResourceDescriptor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.ext.code.descriptor;

import com.atlassian.confluence.ext.code.descriptor.BrushDefinition;
import com.atlassian.confluence.ext.code.descriptor.ConfluenceStrategy;
import com.atlassian.confluence.ext.code.descriptor.ThemeDefinition;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.elements.ResourceDescriptor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConfluenceStrategyImpl
implements ConfluenceStrategy {
    private static final String THEME_DESCRIPTOR_PREFIX = "sh-theme-";
    private static final String LAYOUT_PREFIX = "layout-";
    private static final String LOCALIZATION_DESCRIPTOR_PREFIX = "syntaxhighlighter-lang-";
    private final PluginAccessor pluginAccessor;

    @Autowired
    public ConfluenceStrategyImpl(@ComponentImport PluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;
    }

    @Override
    public BrushDefinition[] listBuiltinBrushes() {
        ModuleDescriptor<?> descriptor = this.getDescriptor("syntaxhighlighter-brushes");
        List resources = descriptor.getResourceDescriptors();
        BrushDefinition[] result = new BrushDefinition[resources.size()];
        int i = 0;
        for (ResourceDescriptor resource : resources) {
            result[i] = new BrushDefinition(resource.getLocation(), descriptor.getCompleteKey());
            ++i;
        }
        return result;
    }

    @Override
    public ThemeDefinition[] listBuiltinThemes() {
        Plugin plugin = this.pluginAccessor.getPlugin("com.atlassian.confluence.ext.newcode-macro-plugin");
        Collection descriptors = plugin.getModuleDescriptors();
        ArrayList<ThemeDefinition> result = new ArrayList<ThemeDefinition>();
        for (ModuleDescriptor descriptor : descriptors) {
            if (!descriptor.getKey().startsWith(THEME_DESCRIPTOR_PREFIX)) continue;
            List resources = descriptor.getResourceDescriptors();
            ResourceDescriptor first = (ResourceDescriptor)resources.get(0);
            String location = first.getLocation();
            String webResourceId = descriptor.getCompleteKey();
            HashMap<String, String> panelLookAndFeel = new HashMap<String, String>();
            for (Map.Entry param : descriptor.getParams().entrySet()) {
                if (!((String)param.getKey()).startsWith(LAYOUT_PREFIX)) continue;
                panelLookAndFeel.put((String)param.getKey(), (String)param.getValue());
            }
            result.add(new ThemeDefinition(location, webResourceId, panelLookAndFeel));
        }
        return result.toArray(new ThemeDefinition[result.size()]);
    }

    @Override
    public List<String> listLocalization() {
        Plugin plugin = this.pluginAccessor.getPlugin("com.atlassian.confluence.ext.newcode-macro-plugin");
        Collection descriptors = plugin.getModuleDescriptors();
        ArrayList<String> result = new ArrayList<String>();
        for (ModuleDescriptor descriptor : descriptors) {
            String webResourceId;
            if (!descriptor.getKey().startsWith(LOCALIZATION_DESCRIPTOR_PREFIX) || !StringUtils.isNotEmpty((CharSequence)(webResourceId = descriptor.getCompleteKey()))) continue;
            String languageKey = webResourceId.substring(webResourceId.length() - 2, webResourceId.length());
            result.add(languageKey.toLowerCase());
        }
        return result;
    }

    protected ModuleDescriptor<?> getDescriptor(String key) {
        return this.pluginAccessor.getPlugin("com.atlassian.confluence.ext.newcode-macro-plugin").getModuleDescriptor(key);
    }
}


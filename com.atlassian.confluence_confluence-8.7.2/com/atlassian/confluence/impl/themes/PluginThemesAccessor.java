/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.PluginAccessor
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.themes;

import com.atlassian.confluence.plugin.descriptor.ThemeModuleDescriptor;
import com.atlassian.confluence.themes.Theme;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.PluginAccessor;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PluginThemesAccessor {
    private static final Logger log = LoggerFactory.getLogger(PluginThemesAccessor.class);
    private final PluginAccessor pluginAccessor;

    public PluginThemesAccessor(PluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;
    }

    public Theme extractTheme(String themeModuleKey) {
        if (StringUtils.isBlank((CharSequence)themeModuleKey)) {
            return null;
        }
        ModuleDescriptor moduleDesc = this.pluginAccessor.getEnabledPluginModule(themeModuleKey);
        if (moduleDesc == null) {
            log.debug("Unable to find configured theme module: {}, the plugin may be disabled", (Object)themeModuleKey);
            return null;
        }
        Object theme = moduleDesc.getModule();
        if (!(theme instanceof Theme)) {
            log.warn("Found configured theme module: " + themeModuleKey + " but it was the wrong type: " + theme.getClass().getName());
            return null;
        }
        return (Theme)theme;
    }

    public List<ThemeModuleDescriptor> getAvailableThemeDescriptors() {
        return this.pluginAccessor.getEnabledModuleDescriptorsByClass(ThemeModuleDescriptor.class);
    }
}


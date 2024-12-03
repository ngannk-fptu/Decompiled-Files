/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 */
package com.atlassian.renderer.v2.macro.code;

import com.atlassian.plugin.PluginAccessor;
import com.atlassian.renderer.v2.macro.code.SourceCodeFormatter;
import com.atlassian.renderer.v2.macro.code.SourceCodeFormatterModuleDescriptor;
import com.atlassian.renderer.v2.macro.code.SourceCodeFormatterRepository;
import java.util.Arrays;
import java.util.Collection;
import java.util.TreeSet;

public class PluggableSourceCodeFormatterRepository
implements SourceCodeFormatterRepository {
    private PluginAccessor pluginAccessor;

    public PluggableSourceCodeFormatterRepository(PluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;
    }

    @Override
    public SourceCodeFormatter getSourceCodeFormatter(String language) {
        for (SourceCodeFormatterModuleDescriptor descriptor : this.pluginAccessor.getEnabledModuleDescriptorsByClass(SourceCodeFormatterModuleDescriptor.class)) {
            SourceCodeFormatter formatter = descriptor.getFormatter();
            if (!this.supportsLanguage(formatter, language)) continue;
            return formatter;
        }
        return null;
    }

    @Override
    public Collection<String> getAvailableLanguages() {
        TreeSet<String> supportedLanguages = new TreeSet<String>();
        for (SourceCodeFormatterModuleDescriptor descriptor : this.pluginAccessor.getEnabledModuleDescriptorsByClass(SourceCodeFormatterModuleDescriptor.class)) {
            supportedLanguages.addAll(Arrays.asList(descriptor.getFormatter().getSupportedLanguages()));
        }
        return supportedLanguages;
    }

    private boolean supportsLanguage(SourceCodeFormatter formatter, String language) {
        if (formatter == null) {
            return false;
        }
        for (String supportedLanguage : formatter.getSupportedLanguages()) {
            if (!supportedLanguage.equals(language)) continue;
            return true;
        }
        return false;
    }
}


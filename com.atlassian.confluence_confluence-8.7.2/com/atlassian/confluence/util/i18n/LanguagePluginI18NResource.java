/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.util.i18n;

import com.atlassian.confluence.languages.Language;
import com.atlassian.confluence.plugin.descriptor.LanguageModuleDescriptor;
import com.atlassian.confluence.util.i18n.AbstractI18NResource;
import com.atlassian.plugin.PluginAccessor;
import java.io.InputStream;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class LanguagePluginI18NResource
extends AbstractI18NResource {
    private final PluginAccessor pluginAccessor;

    public LanguagePluginI18NResource(PluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;
    }

    @Override
    protected InputStream getPropertyResourceAsStream(String locale) {
        List languageModuleDescriptors = this.pluginAccessor.getEnabledModuleDescriptorsByClass(LanguageModuleDescriptor.class);
        for (LanguageModuleDescriptor languageModuleDescriptor : languageModuleDescriptors) {
            String languageModuleLocale = languageModuleDescriptor.getKey();
            if (!StringUtils.equals((CharSequence)locale, (CharSequence)languageModuleLocale)) continue;
            Language language = languageModuleDescriptor.getModule();
            return languageModuleDescriptor.getPlugin().getResourceAsStream(language.getResourceBundlePath());
        }
        return null;
    }
}


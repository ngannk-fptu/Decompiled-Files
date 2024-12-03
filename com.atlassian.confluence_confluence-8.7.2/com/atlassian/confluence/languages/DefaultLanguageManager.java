/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.event.PluginEventListener
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.event.events.PluginModuleDisabledEvent
 *  com.atlassian.plugin.event.events.PluginModuleEnabledEvent
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.languages;

import com.atlassian.confluence.languages.Language;
import com.atlassian.confluence.languages.LanguageComparator;
import com.atlassian.confluence.languages.LanguageManager;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.plugin.descriptor.LanguageModuleDescriptor;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.event.PluginEventListener;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.event.events.PluginModuleDisabledEvent;
import com.atlassian.plugin.event.events.PluginModuleEnabledEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultLanguageManager
implements LanguageManager {
    private static final Logger log = LoggerFactory.getLogger(DefaultLanguageManager.class);
    private PluginAccessor pluginAccessor;
    private volatile List<Language> languages;
    private volatile Map<String, Language> languageMap;
    private volatile Set<String> duplicateLanguages;
    private SettingsManager settingsManager;

    public static Language getDefaultEnglishLanguage() {
        Language language = new Language(LocaleManager.DEFAULT_LOCALE);
        language.setFlagUrl("/images/en_GB.gif");
        return language;
    }

    public List<LanguageModuleDescriptor> getAvailableLanguageDescriptors() {
        return this.pluginAccessor.getEnabledModuleDescriptorsByClass(LanguageModuleDescriptor.class);
    }

    @Override
    public Language getLanguage(String languageKey) {
        Language language = this.getLanguageMap().get(languageKey);
        if (language != null) {
            return language;
        }
        log.warn("Unable to find configured language module: " + languageKey);
        return null;
    }

    public Map<String, Language> getLanguageMap() {
        if (this.languageMap == null) {
            List<LanguageModuleDescriptor> installedLanguagePacks = this.getAvailableLanguageDescriptors();
            HashMap<String, Language> newLanguageMap = new HashMap<String, Language>();
            Language defaultLanguage = DefaultLanguageManager.getDefaultEnglishLanguage();
            newLanguageMap.put(defaultLanguage.getName(), defaultLanguage);
            for (LanguageModuleDescriptor descriptor : installedLanguagePacks) {
                Language language = new Language(descriptor);
                newLanguageMap.put(language.getName(), language);
            }
            this.languageMap = newLanguageMap;
        }
        return this.languageMap;
    }

    @Override
    public List<Language> getLanguages() {
        if (this.languages == null) {
            ArrayList<Language> languageList = new ArrayList<Language>(this.getLanguageMap().values());
            Collections.sort(languageList, new LanguageComparator());
            this.languages = languageList;
        }
        return this.languages;
    }

    @Override
    public final Language getGlobalDefaultLanguage() {
        return this.getGlobalSettings().map(Settings::getGlobalDefaultLocale).filter(defaultLocale -> !StringUtils.isEmpty((CharSequence)defaultLocale) && this.getLanguage((String)defaultLocale) != null).map(this::getLanguage).orElse(this.getLanguage("en_GB"));
    }

    private Optional<Settings> getGlobalSettings() {
        if (this.settingsManager == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(this.settingsManager.getGlobalSettings());
    }

    public void setPluginAccessor(PluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;
    }

    @PluginEventListener
    public void handlePluginModuleDisabled(PluginModuleDisabledEvent event) {
        if (event.getModule() instanceof LanguageModuleDescriptor) {
            this.resetCache();
        }
    }

    @PluginEventListener
    public void handlePluginModuleEnabled(PluginModuleEnabledEvent event) {
        if (event.getModule() instanceof LanguageModuleDescriptor) {
            this.resetCache();
        }
    }

    private void resetCache() {
        this.languageMap = null;
        this.languages = null;
        this.duplicateLanguages = null;
    }

    public boolean isDuplicateLanguage(Language language) {
        if (this.duplicateLanguages == null) {
            HashSet seenLanguages = new HashSet();
            this.duplicateLanguages = this.getLanguages().stream().map(Language::getLanguage).filter(otherLanguage -> !seenLanguages.add(otherLanguage)).collect(Collectors.toSet());
        }
        return this.duplicateLanguages.contains(language.getLanguage());
    }

    public void setPluginEventManager(PluginEventManager pluginEventManager) {
        pluginEventManager.register((Object)this);
    }

    public String getDisplayName(Language language) {
        if (this.isDuplicateLanguage(language)) {
            return language.getCapitalDisplayName();
        }
        return language.getCapitalDisplayLanguage();
    }

    public void setSettingsManager(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.velocity.htmlsafe.HtmlSafe
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.tracker.PluginModuleTracker
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Supplier
 *  com.google.common.base.Suppliers
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  com.google.common.collect.Lists
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.util.i18n;

import com.atlassian.confluence.languages.TranslationTransform;
import com.atlassian.confluence.plugin.descriptor.TranslationTransformModuleDescriptor;
import com.atlassian.confluence.util.i18n.CombinedResourceBundleFactory;
import com.atlassian.confluence.util.i18n.DefaultI18NBeanFactory;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18nModeManager;
import com.atlassian.confluence.util.i18n.Message;
import com.atlassian.confluence.util.i18n.TranslationMode;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.tracker.PluginModuleTracker;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultI18NBean
implements I18NBean {
    private static final Logger log = LoggerFactory.getLogger(I18NBean.class);
    private final List<ResourceBundle> bundles;
    private final Locale locale;
    private PluginModuleTracker<TranslationTransform, TranslationTransformModuleDescriptor> pluginModuleTracker;
    private final I18nModeManager i18nModeManager;
    private final Supplier<ResourceBundle> resourceBundleLazyReference;
    private final LoadingCache<String, Map<String, String>> prefixTranslationCache = CacheBuilder.newBuilder().expireAfterAccess(30L, TimeUnit.MINUTES).build(CacheLoader.from((Function)new Function<String, Map<String, String>>(){

        public Map<String, String> apply(String prefix) {
            return DefaultI18NBean.this.getTranslationsForPrefixInternal(prefix);
        }
    }));

    @Deprecated
    public DefaultI18NBean(Locale locale, PluginAccessor pluginAccessor, I18nModeManager modeManager) {
        this(locale, null, modeManager, DefaultI18NBeanFactory.buildBundles(locale, pluginAccessor));
    }

    @Deprecated
    public DefaultI18NBean(Locale locale, PluginModuleTracker<TranslationTransform, TranslationTransformModuleDescriptor> pluginModuleTracker, I18nModeManager modeManager, List<ResourceBundle> resourceBundles) {
        this.locale = locale;
        this.pluginModuleTracker = pluginModuleTracker;
        this.i18nModeManager = modeManager;
        this.bundles = resourceBundles;
        this.resourceBundleLazyReference = Suppliers.memoize(() -> this.getBundle());
    }

    private ResourceBundle getBundle() {
        ArrayList list = Lists.newArrayListWithCapacity((int)this.bundles.size());
        for (ResourceBundle bundle : this.bundles) {
            if (bundle == null) continue;
            list.add(bundle);
        }
        return list.isEmpty() ? DefaultI18NBeanFactory.getDefaultResourceBundle() : CombinedResourceBundleFactory.createCombinedResourceBundle(list);
    }

    @Override
    @HtmlSafe
    public String getText(@Nullable String key) {
        return this.getText(key, (Object[])null);
    }

    @Override
    @HtmlSafe
    public String getText(@Nullable String key, @Nullable Object[] args) {
        return this.getText(key, args, false);
    }

    @Override
    public String getText(String key, Object[] args, boolean onlyRawValue) {
        if (key == null) {
            return "";
        }
        String foundValue = this.getI18nValue(key);
        foundValue = foundValue != null ? this.processTranslationTransforms(this.locale, key, foundValue) : key;
        String renderedValue = foundValue;
        if (renderedValue == null || onlyRawValue) {
            return renderedValue;
        }
        if (!(renderedValue.contains("'{") || renderedValue.contains("'}") || args != null && args.length != 0)) {
            renderedValue = renderedValue.replaceAll("\\{", "'{").replaceAll("\\}", "'}");
        }
        try {
            renderedValue = MessageFormat.format(renderedValue, args);
        }
        catch (IllegalArgumentException ex) {
            log.debug("Error formatting internationalized string with key: \"" + key + "\" and params: " + Arrays.toString(args));
            return key;
        }
        TranslationMode mode = this.getTranslationMode();
        return mode == null ? renderedValue : mode.getMarkedUpText(key, renderedValue, foundValue);
    }

    @Override
    public String getUntransformedRawText(String key) {
        String value = this.getI18nValue(key);
        return value != null ? value : key;
    }

    private String processTranslationTransforms(Locale locale, String key, String rawMessage) {
        if (this.pluginModuleTracker == null) {
            return rawMessage;
        }
        String result = rawMessage;
        try {
            for (TranslationTransform translationTransform : this.pluginModuleTracker.getModules()) {
                result = translationTransform.apply(locale, key, result);
            }
        }
        catch (Exception e) {
            log.warn("Failed to process translation transforms", (Throwable)e);
        }
        return result;
    }

    private TranslationMode getTranslationMode() {
        if (this.i18nModeManager != null) {
            return this.i18nModeManager.getTranslationMode();
        }
        return null;
    }

    @Override
    @HtmlSafe
    public String getText(String key, List list) {
        if (list != null) {
            return this.getText(key, list.toArray());
        }
        return this.getText(key, new Object[0]);
    }

    @Override
    @HtmlSafe
    public String getText(Message message) {
        return this.getText(message.getKey(), message.getArguments());
    }

    @Override
    public String getTextStrict(String i18nKey) {
        String value = this.getI18nValue(i18nKey);
        return value != null ? this.processTranslationTransforms(this.locale, i18nKey, value) : i18nKey;
    }

    @Override
    public ResourceBundle getResourceBundle() {
        return (ResourceBundle)this.resourceBundleLazyReference.get();
    }

    @Override
    public Map<String, String> getTranslationsForPrefix(String prefix) {
        Preconditions.checkNotNull((Object)prefix);
        return (Map)this.prefixTranslationCache.getUnchecked((Object)prefix);
    }

    private Set<String> getKeysForPrefix(String prefix) {
        Preconditions.checkNotNull((Object)prefix);
        ResourceBundle bundle = this.getResourceBundle();
        ImmutableSet.Builder builder = ImmutableSet.builder();
        for (String key : bundle.keySet()) {
            if (!key.startsWith(prefix)) continue;
            builder.add((Object)key);
        }
        return builder.build();
    }

    private Map<String, String> getTranslationsForPrefixInternal(String prefix) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        for (String key : this.getKeysForPrefix(prefix)) {
            builder.put((Object)key, (Object)this.getI18nValue(key));
        }
        return builder.build();
    }

    private String getI18nValue(String i18nKey) {
        for (ResourceBundle bundle : this.bundles) {
            try {
                if (i18nKey.startsWith("'") && i18nKey.endsWith("'")) {
                    i18nKey = i18nKey.substring(1, i18nKey.length() - 1);
                }
                if (!bundle.containsKey(i18nKey)) continue;
                return bundle.getString(i18nKey);
            }
            catch (Exception e) {
                if (!log.isDebugEnabled()) continue;
                log.debug("Error getting text for key: (" + i18nKey + ") against resource bundle: (" + bundle + ") for locale: (" + this.locale + ")", (Throwable)e);
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("Error getting text for key: (" + i18nKey + ")");
        }
        return null;
    }
}


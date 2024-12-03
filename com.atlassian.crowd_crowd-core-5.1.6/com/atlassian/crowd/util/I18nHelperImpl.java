/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.util.I18nHelper
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.util;

import com.atlassian.crowd.util.I18nHelper;
import com.atlassian.crowd.util.ResourceBundleProvider;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class I18nHelperImpl
implements I18nHelper {
    private static final Logger LOG = LoggerFactory.getLogger(I18nHelperImpl.class);
    private final Iterable<ResourceBundleProvider> resourceBundleProviders;
    private final boolean fallbackToKey;

    public I18nHelperImpl(List<ResourceBundleProvider> providers) {
        this(providers, true);
    }

    public I18nHelperImpl(List<ResourceBundleProvider> providers, boolean fallbackToKey) {
        Preconditions.checkNotNull(providers);
        this.resourceBundleProviders = ImmutableList.copyOf(providers);
        this.fallbackToKey = fallbackToKey;
    }

    public String getText(String key) {
        return this.getText(key, new Object[0]);
    }

    public String getText(String key, String value1) {
        return this.getText(key, Arrays.asList(value1));
    }

    public String getText(String key, String value1, String value2) {
        return this.getText(key, Arrays.asList(value1, value2));
    }

    public String getText(String key, Object parameters) {
        Object[] params = parameters instanceof List ? ((List)parameters).toArray() : (parameters instanceof Object[] ? (Object[])parameters : new Object[]{parameters});
        return new MessageFormat(this.getUnescapedText(key)).format(params);
    }

    public String getText(Locale locale, String key, Serializable ... arguments) {
        return this.getText(key, arguments);
    }

    public String getUnescapedText(String key) {
        for (ResourceBundleProvider resourceBundleProvider : this.resourceBundleProviders) {
            for (ResourceBundle i18nBundle : resourceBundleProvider.getResourceBundles()) {
                if (key.startsWith("'") && key.endsWith("'")) {
                    key = key.substring(1, key.length() - 1);
                }
                if (!i18nBundle.containsKey(key)) continue;
                try {
                    return i18nBundle.getString(key);
                }
                catch (MissingResourceException e) {
                    LOG.debug("Key <{}> not present in bundle", (Object)key);
                }
            }
        }
        if (this.fallbackToKey) {
            return key;
        }
        throw new IllegalArgumentException("Unknown key: " + key);
    }

    public String getUnescapedText(Locale locale, String key) {
        return this.getUnescapedText(key);
    }

    public Map<String, String> getAllTranslationsForPrefix(String prefix) {
        Preconditions.checkNotNull((Object)prefix);
        HashMap translations = Maps.newHashMap();
        for (ResourceBundleProvider resourceBundleProvider : this.resourceBundleProviders) {
            for (ResourceBundle resourceBundle : resourceBundleProvider.getResourceBundles()) {
                for (String key : resourceBundle.keySet()) {
                    if (!key.startsWith(prefix)) continue;
                    translations.put(key, resourceBundle.getString(key));
                }
            }
        }
        return ImmutableMap.copyOf((Map)translations);
    }
}


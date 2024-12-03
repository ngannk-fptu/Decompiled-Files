/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.features.SiteDarkFeaturesStorage
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableSet
 *  io.atlassian.util.concurrent.ResettableLazyReference
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.sal.core.features;

import com.atlassian.sal.api.features.SiteDarkFeaturesStorage;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import io.atlassian.util.concurrent.ResettableLazyReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

public class DefaultSiteDarkFeaturesStorage
implements SiteDarkFeaturesStorage {
    private static final String SITE_WIDE_DARK_FEATURES = "atlassian.sitewide.dark.features";
    private final ResettableLazyReference<ImmutableSet<String>> cache = new ResettableLazyReference<ImmutableSet<String>>(){

        protected ImmutableSet<String> create() {
            return ImmutableSet.copyOf((Collection)DefaultSiteDarkFeaturesStorage.this.load());
        }
    };
    private final PluginSettingsFactory pluginSettingsFactory;

    public DefaultSiteDarkFeaturesStorage(PluginSettingsFactory pluginSettingsFactory) {
        this.pluginSettingsFactory = pluginSettingsFactory;
    }

    public boolean contains(String featureKey) {
        String trimmedFeatureKey = (String)Preconditions.checkNotNull((Object)StringUtils.trimToNull((String)featureKey), (Object)"featureKey must not be blank");
        return ((ImmutableSet)this.cache.get()).contains((Object)trimmedFeatureKey);
    }

    public void enable(String featureKey) {
        String trimmedFeatureKey = (String)Preconditions.checkNotNull((Object)StringUtils.trimToNull((String)featureKey), (Object)"featureKey must not be blank");
        if (!((ImmutableSet)this.cache.get()).contains((Object)trimmedFeatureKey)) {
            this.update(this.addFeatureKey(trimmedFeatureKey));
            this.cache.reset();
        }
    }

    public void disable(String featureKey) {
        String trimmedFeatureKey = (String)Preconditions.checkNotNull((Object)StringUtils.trimToNull((String)featureKey), (Object)"featureKey must not be blank");
        if (((ImmutableSet)this.cache.get()).contains((Object)trimmedFeatureKey)) {
            this.update(this.removeFeatureKey(trimmedFeatureKey));
            this.cache.reset();
        }
    }

    @Deprecated
    public ImmutableSet<String> getEnabledDarkFeatures() {
        return (ImmutableSet)this.cache.get();
    }

    public Set<String> getEnabledDarkFeatureSet() {
        return this.getEnabledDarkFeatures();
    }

    private synchronized void update(Function<List<String>, List<String>> transformer) {
        List<String> storedFeatureKeys = this.load();
        List<String> updatedFeatureKeys = transformer.apply(storedFeatureKeys);
        this.store(updatedFeatureKeys);
    }

    private synchronized List<String> load() {
        PluginSettings globalSettings = this.pluginSettingsFactory.createGlobalSettings();
        Object value = globalSettings.get(SITE_WIDE_DARK_FEATURES);
        return this.extractFeatureKeys(value);
    }

    private List<String> extractFeatureKeys(@Nullable Object value) {
        LinkedList<String> storedFeatureKeys = new LinkedList<String>();
        if (value instanceof List) {
            List list = (List)List.class.cast(value);
            for (Object listItem : list) {
                if (!(listItem instanceof String)) continue;
                storedFeatureKeys.addLast((String)String.class.cast(listItem));
            }
        }
        return storedFeatureKeys;
    }

    private synchronized void store(List<String> updatedFeatureKeys) {
        PluginSettings globalSettings = this.pluginSettingsFactory.createGlobalSettings();
        globalSettings.put(SITE_WIDE_DARK_FEATURES, updatedFeatureKeys);
    }

    private Function<List<String>, List<String>> addFeatureKey(String featureKey) {
        return storedFeatureKeys -> {
            if (storedFeatureKeys == null) {
                return null;
            }
            ArrayList<String> result = new ArrayList<String>((Collection<String>)storedFeatureKeys);
            if (!storedFeatureKeys.contains(featureKey)) {
                result.add(featureKey);
            }
            return result;
        };
    }

    private Function<List<String>, List<String>> removeFeatureKey(String featureKey) {
        return storedFeatureKeys -> {
            if (storedFeatureKeys == null) {
                return storedFeatureKeys;
            }
            ArrayList result = new ArrayList(storedFeatureKeys);
            result.remove(featureKey);
            return result;
        };
    }
}


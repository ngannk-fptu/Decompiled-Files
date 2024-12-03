/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.Functions
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.google.common.collect.Sets$SetView
 */
package com.atlassian.sal.core.features;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class SystemDarkFeatureInitializer {
    public static SystemDarkFeatures getSystemStartupDarkFeatures() {
        String propertiesFile = System.getProperty("darkfeatures.properties.file", "atlassian-darkfeatures.properties");
        String disableAllPropertiesFlag = System.getProperty("atlassian.darkfeature.disabled");
        if (Boolean.parseBoolean(disableAllPropertiesFlag)) {
            return SystemDarkFeatures.disableAll();
        }
        Set<String> enabledPropertyDarkFeatures = SystemDarkFeatureInitializer.getPropertyDarkFeaturesWithValue(true);
        Set<String> enabledPropertiesFileDarkFeatures = SystemDarkFeatureInitializer.getPropertiesFileDarkFeatures(propertiesFile, true);
        Set<String> disabledPropertyDarkFeatures = SystemDarkFeatureInitializer.getPropertyDarkFeaturesWithValue(false);
        Set<String> disabledPropertiesFileDarkFeatures = SystemDarkFeatureInitializer.getPropertiesFileDarkFeatures(propertiesFile, false);
        Sets.SetView enabled = Sets.union((Set)Sets.difference(enabledPropertiesFileDarkFeatures, disabledPropertyDarkFeatures), enabledPropertyDarkFeatures);
        Sets.SetView disabled = Sets.union((Set)Sets.difference(disabledPropertiesFileDarkFeatures, enabledPropertyDarkFeatures), disabledPropertyDarkFeatures);
        return SystemDarkFeatures.darkFeatures((Set<String>)enabled, (Set<String>)disabled);
    }

    private static Set<String> getPropertiesFileDarkFeatures(String filename, boolean b) {
        File propertiesFile = new File(filename);
        Properties properties = new Properties();
        try {
            properties.load(new FileReader(propertiesFile));
        }
        catch (IOException e) {
            return Collections.emptySet();
        }
        return new HashSet<String>(SystemDarkFeatureInitializer.getKeysForBooleanValue(SystemDarkFeatureInitializer.filterOnAndStripKeyPrefix(properties), b));
    }

    private static Set<String> getPropertyDarkFeaturesWithValue(boolean b) {
        return new HashSet<String>(SystemDarkFeatureInitializer.getKeysForBooleanValue(SystemDarkFeatureInitializer.filterOnAndStripKeyPrefix(System.getProperties()), b));
    }

    private static Collection<String> getKeysForBooleanValue(Map<Object, Object> properties, final Boolean b) {
        return Collections2.transform(Maps.filterValues(properties, (Predicate)new Predicate<Object>(){

            public boolean apply(Object input) {
                return input.toString().toLowerCase().matches(b.toString());
            }
        }).keySet(), (Function)Functions.toStringFunction());
    }

    private static Map<Object, Object> filterOnAndStripKeyPrefix(Map<Object, Object> properties) {
        Set entries = Maps.filterKeys(properties, (Predicate)new Predicate<Object>(){

            public boolean apply(Object entry) {
                return entry.toString().startsWith("atlassian.darkfeature.");
            }
        }).entrySet();
        HashMap<Object, Object> prefixStrippedEntries = new HashMap<Object, Object>();
        for (Map.Entry entry : entries) {
            prefixStrippedEntries.put(entry.getKey().toString().substring("atlassian.darkfeature.".length()), entry.getValue());
        }
        return prefixStrippedEntries;
    }

    public static class SystemDarkFeatures {
        private final Set<String> enabled;
        private final Set<String> disabled;
        private final boolean disableAll;

        private SystemDarkFeatures(Set<String> enabled, Set<String> disabled, boolean disableAll) {
            this.enabled = ImmutableSet.copyOf(enabled);
            this.disabled = ImmutableSet.copyOf(disabled);
            this.disableAll = disableAll;
        }

        public static SystemDarkFeatures disableAll() {
            return new SystemDarkFeatures(Collections.emptySet(), Collections.emptySet(), true);
        }

        public static SystemDarkFeatures darkFeatures(Set<String> enabled, Set<String> disabled) {
            return new SystemDarkFeatures(enabled, disabled, false);
        }

        public Set<String> getEnabled() {
            return this.enabled;
        }

        public Set<String> getDisabled() {
            return this.disabled;
        }

        public boolean isDisableAll() {
            return this.disableAll;
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.ImmutableSet
 *  javax.annotation.Nullable
 */
package com.atlassian.sal.api.features;

import com.atlassian.sal.api.features.EnabledDarkFeatures;
import com.atlassian.sal.api.features.FeatureKeyScope;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import javax.annotation.Nullable;

public class EnabledDarkFeaturesBuilder {
    private final ImmutableMap.Builder<FeatureKeyScope, ImmutableSet<String>> builder = ImmutableMap.builder();

    public EnabledDarkFeaturesBuilder unmodifiableFeaturesEnabledForAllUsers(@Nullable Set<String> enabledFeatureKeys) {
        return this.setEnabledFeatures(FeatureKeyScope.ALL_USERS_READ_ONLY, enabledFeatureKeys);
    }

    public EnabledDarkFeaturesBuilder featuresEnabledForAllUsers(@Nullable Set<String> enabledFeatureKeys) {
        return this.setEnabledFeatures(FeatureKeyScope.ALL_USERS, enabledFeatureKeys);
    }

    public EnabledDarkFeaturesBuilder featuresEnabledForCurrentUser(@Nullable Set<String> enabledFeatureKeys) {
        return this.setEnabledFeatures(FeatureKeyScope.CURRENT_USER_ONLY, enabledFeatureKeys);
    }

    public EnabledDarkFeatures build() {
        return new EnabledDarkFeatures((ImmutableMap<FeatureKeyScope, ImmutableSet<String>>)this.builder.build());
    }

    private EnabledDarkFeaturesBuilder setEnabledFeatures(FeatureKeyScope featureKeyScope, @Nullable Set<String> enabledFeatureKeys) {
        ImmutableSet nonNullCopy = enabledFeatureKeys != null ? ImmutableSet.copyOf(enabledFeatureKeys) : ImmutableSet.of();
        this.builder.put((Object)featureKeyScope, (Object)nonNullCopy);
        return this;
    }
}


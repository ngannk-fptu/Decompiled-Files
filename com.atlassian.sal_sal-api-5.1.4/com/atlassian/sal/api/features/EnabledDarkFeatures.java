/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.google.common.base.Predicate
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Maps
 *  javax.annotation.concurrent.Immutable
 */
package com.atlassian.sal.api.features;

import com.atlassian.annotations.PublicApi;
import com.atlassian.sal.api.features.FeatureKeyScope;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.concurrent.Immutable;

@Immutable
@PublicApi
public final class EnabledDarkFeatures {
    public static final EnabledDarkFeatures NONE = new EnabledDarkFeatures(Collections.emptyMap());
    private final Map<FeatureKeyScope, Set<String>> enabledFeatures;

    @Deprecated
    public EnabledDarkFeatures(ImmutableMap<FeatureKeyScope, ImmutableSet<String>> enabledFeatures) {
        Objects.requireNonNull(enabledFeatures, "enabledFeatures");
        this.enabledFeatures = ImmutableMap.copyOf(enabledFeatures);
    }

    public EnabledDarkFeatures(Map<FeatureKeyScope, Set<String>> enabledFeatures) {
        Objects.requireNonNull(enabledFeatures, "enabledFeatures");
        this.enabledFeatures = Collections.unmodifiableMap(enabledFeatures);
    }

    @Deprecated
    public ImmutableSet<String> getFeatureKeys() {
        return ImmutableSet.copyOf((Iterable)Iterables.concat(this.enabledFeatures.values()));
    }

    public Set<String> getFeatureKeySet() {
        return Collections.unmodifiableSet(this.enabledFeatures.values().stream().flatMap(Collection::stream).collect(Collectors.toSet()));
    }

    @Deprecated
    public ImmutableSet<String> getFeatureKeys(com.google.common.base.Predicate<FeatureKeyScope> criteria) {
        Objects.requireNonNull(criteria, "criteria");
        return ImmutableSet.copyOf((Iterable)Iterables.concat(Maps.filterKeys(this.enabledFeatures, criteria).values()));
    }

    public Set<String> getFeatureKeys(Predicate<FeatureKeyScope> criteria) {
        Objects.requireNonNull(criteria, "criteria");
        return Collections.unmodifiableSet(this.enabledFeatures.entrySet().stream().filter(entry -> criteria.test((FeatureKeyScope)((Object)entry.getKey()))).map(Map.Entry::getValue).flatMap(Collection::stream).collect(Collectors.toSet()));
    }

    public boolean isFeatureEnabled(String featureKey) {
        Objects.requireNonNull(featureKey, "featureKey");
        return Iterables.contains((Iterable)Iterables.concat(this.enabledFeatures.values()), (Object)featureKey);
    }

    public String toString() {
        return "EnabledDarkFeatures{enabledFeatures=" + this.enabledFeatures + '}';
    }
}


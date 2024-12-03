/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicSpi
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.sal.api.features;

import com.atlassian.annotations.PublicSpi;
import com.google.common.collect.ImmutableSet;
import java.util.Set;

@PublicSpi
public interface SiteDarkFeaturesStorage {
    public boolean contains(String var1);

    public void enable(String var1);

    public void disable(String var1);

    @Deprecated
    public ImmutableSet<String> getEnabledDarkFeatures();

    default public Set<String> getEnabledDarkFeatureSet() {
        return this.getEnabledDarkFeatures();
    }
}


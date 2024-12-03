/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  javax.annotation.Nonnull
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.applinks.internal.rest.applink.data;

import com.atlassian.applinks.internal.rest.applink.data.RestApplinkDataProvider;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;

public class RestApplinkDataProviders {
    final Map<String, RestApplinkDataProvider> providers;

    @Autowired
    public RestApplinkDataProviders(List<RestApplinkDataProvider> providers) {
        this.providers = this.mapProviders(providers);
    }

    private Map<String, RestApplinkDataProvider> mapProviders(Iterable<RestApplinkDataProvider> providers) {
        ImmutableMap.Builder mappedProviders = ImmutableMap.builder();
        for (RestApplinkDataProvider provider : providers) {
            for (String key : provider.getSupportedKeys()) {
                mappedProviders.put((Object)key, (Object)provider);
            }
        }
        return mappedProviders.build();
    }

    public RestApplinkDataProvider getProvider(@Nonnull String key) {
        return this.providers.get(key);
    }
}


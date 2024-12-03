/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.AliasedDiscoveryConfig;

public class KubernetesConfig
extends AliasedDiscoveryConfig<KubernetesConfig> {
    public KubernetesConfig() {
        super("kubernetes");
    }

    public KubernetesConfig(KubernetesConfig kubernetesConfig) {
        super(kubernetesConfig);
    }

    @Override
    public int getId() {
        return 56;
    }
}


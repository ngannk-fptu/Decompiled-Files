/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.monitoring;

import com.amazonaws.monitoring.CsmConfiguration;
import com.amazonaws.monitoring.CsmConfigurationProvider;
import com.amazonaws.util.ValidationUtils;

public class StaticCsmConfigurationProvider
implements CsmConfigurationProvider {
    private final CsmConfiguration csmConfig;

    public StaticCsmConfigurationProvider(CsmConfiguration csmConfig) {
        this.csmConfig = ValidationUtils.assertNotNull(csmConfig, "csmConfig");
    }

    @Override
    public CsmConfiguration getConfiguration() {
        return this.csmConfig;
    }
}


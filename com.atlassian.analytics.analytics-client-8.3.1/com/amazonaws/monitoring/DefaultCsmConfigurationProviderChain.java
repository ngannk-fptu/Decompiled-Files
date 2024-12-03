/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.monitoring;

import com.amazonaws.monitoring.CsmConfigurationProviderChain;
import com.amazonaws.monitoring.EnvironmentVariableCsmConfigurationProvider;
import com.amazonaws.monitoring.ProfileCsmConfigurationProvider;
import com.amazonaws.monitoring.SystemPropertyCsmConfigurationProvider;

public final class DefaultCsmConfigurationProviderChain
extends CsmConfigurationProviderChain {
    private static final DefaultCsmConfigurationProviderChain INSTANCE = new DefaultCsmConfigurationProviderChain();

    private DefaultCsmConfigurationProviderChain() {
        super(new EnvironmentVariableCsmConfigurationProvider(), new SystemPropertyCsmConfigurationProvider(), new ProfileCsmConfigurationProvider());
    }

    public static DefaultCsmConfigurationProviderChain getInstance() {
        return INSTANCE;
    }
}


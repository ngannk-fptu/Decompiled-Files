/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.SDKGlobalConfiguration;
import com.amazonaws.annotation.SdkProtectedApi;

@SdkProtectedApi
public class ClientConfigurationFactory {
    public final ClientConfiguration getConfig() {
        return SDKGlobalConfiguration.isInRegionOptimizedModeEnabled() ? this.getInRegionOptimizedConfig() : this.getDefaultConfig();
    }

    protected ClientConfiguration getDefaultConfig() {
        return new ClientConfiguration();
    }

    protected ClientConfiguration getInRegionOptimizedConfig() {
        return new ClientConfiguration().withConnectionTimeout(1000);
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.instrumentation;

import com.atlassian.instrumentation.RegistryConfiguration;
import java.io.File;

class DefaultRegistryConfiguration
implements RegistryConfiguration {
    DefaultRegistryConfiguration() {
    }

    @Override
    public String getRegistryName() {
        return "default-registry";
    }

    @Override
    public boolean isCPUCostCollected() {
        return true;
    }

    @Override
    public File getRegistryHomeDirectory() {
        return new File(System.getProperty("java.io.tmpdir"));
    }
}


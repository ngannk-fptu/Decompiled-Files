/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util.profiling;

import com.atlassian.confluence.util.profiling.MutableRegistryConfiguration;
import java.io.File;

public class ConfluenceInstrumentRegistryConfiguration
implements MutableRegistryConfiguration {
    private boolean cpuCostCollected;

    public String getRegistryName() {
        return "confluence";
    }

    public boolean isCPUCostCollected() {
        return this.cpuCostCollected;
    }

    @Override
    public void setCpuCostCollected(boolean cpuCostCollected) {
        this.cpuCostCollected = cpuCostCollected;
    }

    public File getRegistryHomeDirectory() {
        throw new UnsupportedOperationException("do not support writing to the file system");
    }
}


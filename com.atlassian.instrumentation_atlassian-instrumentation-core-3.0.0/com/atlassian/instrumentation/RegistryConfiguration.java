/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.instrumentation;

import java.io.File;

public interface RegistryConfiguration {
    public String getRegistryName();

    public boolean isCPUCostCollected();

    public File getRegistryHomeDirectory();
}


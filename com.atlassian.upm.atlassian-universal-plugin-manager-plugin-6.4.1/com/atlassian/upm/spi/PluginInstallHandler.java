/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.spi;

import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.spi.PluginInstallException;
import com.atlassian.upm.spi.PluginInstallResult;
import java.io.File;

public interface PluginInstallHandler {
    public boolean canInstallPlugin(File var1, Option<String> var2);

    public PluginInstallResult installPlugin(File var1, Option<String> var2) throws PluginInstallException;
}


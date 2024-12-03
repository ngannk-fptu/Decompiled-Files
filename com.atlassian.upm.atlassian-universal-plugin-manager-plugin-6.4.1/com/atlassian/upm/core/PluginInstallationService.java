/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.core;

import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.PluginWithDependenciesInstallResult;
import java.io.File;

public interface PluginInstallationService {
    public PluginWithDependenciesInstallResult install(File var1, String var2, Option<String> var3, boolean var4);

    public PluginWithDependenciesInstallResult update(File var1, String var2, Option<String> var3, boolean var4);

    public void uninstall(Plugin var1);
}


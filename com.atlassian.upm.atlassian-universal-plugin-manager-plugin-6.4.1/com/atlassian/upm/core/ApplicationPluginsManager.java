/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 */
package com.atlassian.upm.core;

import com.atlassian.plugin.Plugin;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.impl.UpmAppManager;
import java.util.Map;
import java.util.Set;

public interface ApplicationPluginsManager {
    public Map<String, UpmAppManager.ApplicationDescriptorModuleInfo> getApplicationRelatedPlugins(Iterable<Plugin> var1);

    public boolean isApplication(Plugin var1);

    public Option<String> getApplicationKey(Plugin var1);

    public Set<String> getApplicationRelatedPluginKeys();

    public boolean isUninstallable(String var1);
}


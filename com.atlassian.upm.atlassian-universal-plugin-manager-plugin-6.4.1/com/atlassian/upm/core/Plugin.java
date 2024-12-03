/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginInformation
 *  com.atlassian.plugin.PluginRestartState
 *  com.atlassian.plugin.PluginState
 */
package com.atlassian.upm.core;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.PluginInformation;
import com.atlassian.plugin.PluginRestartState;
import com.atlassian.plugin.PluginState;
import com.atlassian.upm.api.util.Option;

public interface Plugin {
    public com.atlassian.plugin.Plugin getPlugin();

    public String getName();

    public String getKey();

    public Iterable<Module> getModules();

    public Option<Module> getModule(String var1);

    public boolean isConnect();

    public boolean isEnabledByDefault();

    public boolean isEnabled();

    public PluginInformation getPluginInformation();

    public PluginState getPluginState();

    public boolean isStaticPlugin();

    public boolean isUserInstalled();

    public boolean isUpmPlugin();

    public boolean isBundledPlugin();

    public boolean isUninstallable();

    public boolean isUnloadable();

    public String getVersion();

    public Option<Boolean> isUpdateAvailable();

    public boolean hasUnrecognisedModuleTypes();

    public PluginRestartState getRestartState();

    public static interface Module {
        public ModuleDescriptor<?> getModuleDescriptor();

        public String getCompleteKey();

        public String getPluginKey();

        public String getKey();

        public String getName();

        public String getDescription();

        public Plugin getPlugin();

        public boolean canNotBeDisabled();

        public boolean hasRecognisableType();

        public boolean isBroken();
    }
}


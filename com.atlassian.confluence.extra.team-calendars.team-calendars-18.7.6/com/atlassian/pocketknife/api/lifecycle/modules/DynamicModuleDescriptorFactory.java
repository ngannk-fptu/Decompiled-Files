/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  org.dom4j.Element
 */
package com.atlassian.pocketknife.api.lifecycle.modules;

import com.atlassian.plugin.Plugin;
import com.atlassian.pocketknife.api.lifecycle.modules.LoaderConfiguration;
import com.atlassian.pocketknife.api.lifecycle.modules.ModuleRegistrationHandle;
import com.atlassian.pocketknife.api.lifecycle.modules.ResourceLoader;
import org.dom4j.Element;

public interface DynamicModuleDescriptorFactory {
    @Deprecated
    public ModuleRegistrationHandle loadModules(Plugin var1, String ... var2);

    @Deprecated
    public ModuleRegistrationHandle loadModules(Plugin var1, ResourceLoader var2, String ... var3);

    public ModuleRegistrationHandle loadModules(LoaderConfiguration var1);

    public ModuleRegistrationHandle loadModules(Plugin var1, Element var2);
}


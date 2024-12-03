/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.plugin;

import com.atlassian.annotations.Internal;
import com.atlassian.plugin.InstallationMode;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.PluginArtifact;
import com.atlassian.plugin.PluginDependencies;
import com.atlassian.plugin.PluginInformation;
import com.atlassian.plugin.PluginState;
import com.atlassian.plugin.Resourced;
import com.atlassian.plugin.ScopeAware;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface Plugin
extends ScopeAware,
Resourced,
Comparable<Plugin> {
    public static final int VERSION_1 = 1;
    public static final int VERSION_2 = 2;
    public static final int VERSION_3 = 3;

    public int getPluginsVersion();

    public void setPluginsVersion(int var1);

    public String getName();

    public void setName(String var1);

    public String getI18nNameKey();

    public void setI18nNameKey(String var1);

    public String getKey();

    public void setKey(String var1);

    public void addModuleDescriptor(ModuleDescriptor<?> var1);

    public Collection<ModuleDescriptor<?>> getModuleDescriptors();

    public ModuleDescriptor<?> getModuleDescriptor(String var1);

    public <M> List<ModuleDescriptor<M>> getModuleDescriptorsByModuleClass(Class<M> var1);

    public InstallationMode getInstallationMode();

    public boolean isEnabledByDefault();

    public void setEnabledByDefault(boolean var1);

    public PluginInformation getPluginInformation();

    public void setPluginInformation(PluginInformation var1);

    public void setResources(Resourced var1);

    public PluginState getPluginState();

    @Deprecated
    public boolean isSystemPlugin();

    @Deprecated
    public void setSystemPlugin(boolean var1);

    public boolean containsSystemModule();

    public boolean isBundledPlugin();

    public Date getDateLoaded();

    public Date getDateInstalled();

    public boolean isUninstallable();

    public boolean isDeleteable();

    public boolean isDynamicallyLoaded();

    public <T> Class<T> loadClass(String var1, Class<?> var2) throws ClassNotFoundException;

    public ClassLoader getClassLoader();

    public URL getResource(String var1);

    public InputStream getResourceAsStream(String var1);

    public void install();

    public void uninstall();

    public void enable();

    public void disable();

    @Nonnull
    public PluginDependencies getDependencies();

    public Set<String> getActivePermissions();

    public boolean hasAllPermissions();

    public void resolve();

    @Nullable
    public Date getDateEnabling();

    @Nullable
    public Date getDateEnabled();

    @Internal
    public PluginArtifact getPluginArtifact();
}


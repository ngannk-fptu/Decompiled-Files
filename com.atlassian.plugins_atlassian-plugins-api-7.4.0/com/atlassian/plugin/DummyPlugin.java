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
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginArtifact;
import com.atlassian.plugin.PluginDependencies;
import com.atlassian.plugin.PluginInformation;
import com.atlassian.plugin.PluginState;
import com.atlassian.plugin.Resourced;
import com.atlassian.plugin.elements.ResourceDescriptor;
import com.atlassian.plugin.elements.ResourceLocation;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Internal
public class DummyPlugin
implements Plugin {
    @Override
    public int getPluginsVersion() {
        return 0;
    }

    @Override
    public void setPluginsVersion(int version) {
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void setName(String name) {
    }

    @Override
    public String getI18nNameKey() {
        return null;
    }

    @Override
    public void setI18nNameKey(String i18nNameKey) {
    }

    @Override
    public String getKey() {
        return null;
    }

    @Override
    public void setKey(String aPackage) {
    }

    @Override
    public void addModuleDescriptor(ModuleDescriptor<?> moduleDescriptor) {
    }

    @Override
    public Collection<ModuleDescriptor<?>> getModuleDescriptors() {
        return null;
    }

    @Override
    public ModuleDescriptor<?> getModuleDescriptor(String key) {
        return null;
    }

    @Override
    public <M> List<ModuleDescriptor<M>> getModuleDescriptorsByModuleClass(Class<M> moduleClass) {
        return null;
    }

    @Override
    public InstallationMode getInstallationMode() {
        return null;
    }

    @Override
    public boolean isEnabledByDefault() {
        return false;
    }

    @Override
    public void setEnabledByDefault(boolean enabledByDefault) {
    }

    @Override
    public PluginInformation getPluginInformation() {
        return null;
    }

    @Override
    public void setPluginInformation(PluginInformation pluginInformation) {
    }

    @Override
    public void setResources(Resourced resources) {
    }

    @Override
    public PluginState getPluginState() {
        return null;
    }

    @Override
    public boolean isSystemPlugin() {
        return false;
    }

    @Override
    public void setSystemPlugin(boolean system) {
    }

    @Override
    public boolean containsSystemModule() {
        return false;
    }

    @Override
    public boolean isBundledPlugin() {
        return false;
    }

    @Override
    public Date getDateLoaded() {
        return null;
    }

    @Override
    public Date getDateInstalled() {
        return null;
    }

    @Override
    public boolean isUninstallable() {
        return false;
    }

    @Override
    public boolean isDeleteable() {
        return false;
    }

    @Override
    public boolean isDynamicallyLoaded() {
        return false;
    }

    @Override
    public <T> Class<T> loadClass(String clazz, Class<?> callingClass) throws ClassNotFoundException {
        return null;
    }

    @Override
    public ClassLoader getClassLoader() {
        return null;
    }

    @Override
    public URL getResource(String path) {
        return null;
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        return null;
    }

    @Override
    public void install() {
    }

    @Override
    public void uninstall() {
    }

    @Override
    public void enable() {
    }

    @Override
    public void disable() {
    }

    @Override
    @Nonnull
    public PluginDependencies getDependencies() {
        return new PluginDependencies();
    }

    @Override
    public Set<String> getActivePermissions() {
        return null;
    }

    @Override
    public boolean hasAllPermissions() {
        return false;
    }

    @Override
    public void resolve() {
    }

    @Override
    @Nullable
    public Date getDateEnabling() {
        return null;
    }

    @Override
    @Nullable
    public Date getDateEnabled() {
        return null;
    }

    @Override
    public PluginArtifact getPluginArtifact() {
        return null;
    }

    @Override
    public int compareTo(Plugin o) {
        return 0;
    }

    @Override
    public List<ResourceDescriptor> getResourceDescriptors() {
        return null;
    }

    @Override
    public ResourceDescriptor getResourceDescriptor(String type, String name) {
        return null;
    }

    @Override
    public ResourceLocation getResourceLocation(String type, String name) {
        return null;
    }
}


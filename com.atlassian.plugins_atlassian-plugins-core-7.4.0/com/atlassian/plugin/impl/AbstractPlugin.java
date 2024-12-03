/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.annotations.Internal
 *  com.atlassian.plugin.InstallationMode
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginArtifact
 *  com.atlassian.plugin.PluginDependencies
 *  com.atlassian.plugin.PluginException
 *  com.atlassian.plugin.PluginInformation
 *  com.atlassian.plugin.PluginPermission
 *  com.atlassian.plugin.PluginState
 *  com.atlassian.plugin.Resourced
 *  com.atlassian.plugin.Resources
 *  com.atlassian.plugin.elements.ResourceDescriptor
 *  com.atlassian.plugin.elements.ResourceLocation
 *  com.google.common.base.Suppliers
 *  io.atlassian.util.concurrent.CopyOnWriteMap
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.impl;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.annotations.Internal;
import com.atlassian.plugin.InstallationMode;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginArtifact;
import com.atlassian.plugin.PluginDependencies;
import com.atlassian.plugin.PluginException;
import com.atlassian.plugin.PluginInformation;
import com.atlassian.plugin.PluginInternal;
import com.atlassian.plugin.PluginPermission;
import com.atlassian.plugin.PluginState;
import com.atlassian.plugin.Resourced;
import com.atlassian.plugin.Resources;
import com.atlassian.plugin.elements.ResourceDescriptor;
import com.atlassian.plugin.elements.ResourceLocation;
import com.atlassian.plugin.util.VersionStringComparator;
import com.google.common.base.Suppliers;
import io.atlassian.util.concurrent.CopyOnWriteMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractPlugin
implements PluginInternal,
Comparable<Plugin> {
    private static final Logger log = LoggerFactory.getLogger(AbstractPlugin.class);
    private final Map<String, ModuleDescriptor<?>> modules = CopyOnWriteMap.builder().stableViews().newLinkedMap();
    private final Set<ModuleDescriptor<?>> dynamicModules = new CopyOnWriteArraySet();
    private String name;
    private String i18nNameKey;
    private String key;
    private boolean enabledByDefault = true;
    private PluginInformation pluginInformation = new PluginInformation();
    private boolean system;
    private Resourced resources = Resources.EMPTY_RESOURCES;
    private int pluginsVersion = 1;
    private final Date dateLoaded = new Date();
    private volatile Date dateEnabling;
    private volatile Date dateEnabled;
    private final AtomicReference<PluginState> pluginState = new AtomicReference<PluginState>(PluginState.UNINSTALLED);
    private final Supplier<Set<String>> permissions;
    private volatile boolean bundledPlugin = false;
    protected final PluginArtifact pluginArtifact;

    public AbstractPlugin(PluginArtifact pluginArtifact) {
        this.pluginArtifact = pluginArtifact;
        this.permissions = Suppliers.memoize(this::getPermissionsInternal);
    }

    public String getName() {
        if (StringUtils.isNotBlank((CharSequence)this.name)) {
            return this.name;
        }
        if (StringUtils.isNotBlank((CharSequence)this.i18nNameKey)) {
            return "";
        }
        return this.getKey();
    }

    public void setName(String name) {
        this.name = name;
    }

    protected Logger getLog() {
        return log;
    }

    public String getI18nNameKey() {
        return this.i18nNameKey;
    }

    public void setI18nNameKey(String i18nNameKey) {
        this.i18nNameKey = i18nNameKey;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void addModuleDescriptor(ModuleDescriptor<?> moduleDescriptor) {
        this.modules.put(moduleDescriptor.getKey(), moduleDescriptor);
    }

    protected void removeModuleDescriptor(String key) {
        this.modules.remove(key);
    }

    public Collection<ModuleDescriptor<?>> getModuleDescriptors() {
        return this.modules.values();
    }

    public ModuleDescriptor<?> getModuleDescriptor(String key) {
        return this.modules.get(key);
    }

    public <T> List<ModuleDescriptor<T>> getModuleDescriptorsByModuleClass(Class<T> aClass) {
        ArrayList<ModuleDescriptor<T>> result = new ArrayList<ModuleDescriptor<T>>();
        for (ModuleDescriptor<?> moduleDescriptor : this.modules.values()) {
            Class moduleClass = moduleDescriptor.getModuleClass();
            if (moduleClass == null || !aClass.isAssignableFrom(moduleClass)) continue;
            ModuleDescriptor<?> typedModuleDescriptor = moduleDescriptor;
            result.add(typedModuleDescriptor);
        }
        return result;
    }

    public PluginState getPluginState() {
        return this.pluginState.get();
    }

    protected void setPluginState(PluginState state) {
        if (log.isDebugEnabled()) {
            log.debug("Plugin {} going from {} to {}", new Object[]{this.getKey(), this.getPluginState(), state});
        }
        this.pluginState.set(state);
        this.updateEnableTimes(state);
    }

    protected boolean compareAndSetPluginState(PluginState requiredExistingState, PluginState desiredState) {
        boolean changed;
        if (log.isDebugEnabled()) {
            log.debug("Plugin {} trying to go from {} to {} but only if in {}", new Object[]{this.getKey(), this.getPluginState(), desiredState, requiredExistingState});
        }
        if (changed = this.pluginState.compareAndSet(requiredExistingState, desiredState)) {
            this.updateEnableTimes(desiredState);
        }
        return changed;
    }

    private void updateEnableTimes(PluginState state) {
        Date now = new Date();
        if (PluginState.ENABLING == state) {
            this.dateEnabling = now;
            this.dateEnabled = null;
        } else if (PluginState.ENABLED == state) {
            if (this.dateEnabling == null) {
                this.dateEnabling = now;
            }
            this.dateEnabled = now;
        }
    }

    public boolean isEnabledByDefault() {
        return this.enabledByDefault && (this.pluginInformation == null || this.pluginInformation.satisfiesMinJavaVersion());
    }

    public void setEnabledByDefault(boolean enabledByDefault) {
        this.enabledByDefault = enabledByDefault;
    }

    public int getPluginsVersion() {
        return this.pluginsVersion;
    }

    public void setPluginsVersion(int pluginsVersion) {
        this.pluginsVersion = pluginsVersion;
    }

    public PluginInformation getPluginInformation() {
        return this.pluginInformation;
    }

    public void setPluginInformation(PluginInformation pluginInformation) {
        this.pluginInformation = pluginInformation;
    }

    public void setResources(Resourced resources) {
        this.resources = resources != null ? resources : Resources.EMPTY_RESOURCES;
    }

    public List<ResourceDescriptor> getResourceDescriptors() {
        return this.resources.getResourceDescriptors();
    }

    public ResourceLocation getResourceLocation(String type, String name) {
        return this.resources.getResourceLocation(type, name);
    }

    public ResourceDescriptor getResourceDescriptor(String type, String name) {
        return this.resources.getResourceDescriptor(type, name);
    }

    public void enable() {
        log.debug("Enabling plugin '{}'", (Object)this.getKey());
        PluginState state = this.pluginState.get();
        if (state == PluginState.ENABLED || state == PluginState.ENABLING) {
            log.debug("Plugin '{}' is already enabled, not doing anything.", (Object)this.getKey());
            return;
        }
        try {
            log.debug("Plugin '{}' is NOT already enabled, actually enabling.", (Object)this.getKey());
            PluginState desiredState = this.enableInternal();
            if (desiredState != PluginState.PENDING) {
                if (desiredState != PluginState.ENABLED && desiredState != PluginState.ENABLING) {
                    log.warn("Illegal state transition to {} for plugin '{}' on enable()", (Object)desiredState, (Object)this.getKey());
                }
                this.setPluginState(desiredState);
            }
        }
        catch (PluginException ex) {
            log.warn("Unable to enable plugin '{}'", (Object)this.getKey());
            log.warn("Because of this exception", (Throwable)ex);
            throw ex;
        }
        log.debug("Enabled plugin '{}'", (Object)this.getKey());
    }

    protected PluginState enableInternal() {
        return PluginState.ENABLED;
    }

    public final void disable() {
        if (this.pluginState.get() == PluginState.DISABLED) {
            return;
        }
        log.debug("Disabling plugin '{}'", (Object)this.getKey());
        try {
            this.setPluginState(PluginState.DISABLING);
            this.disableInternal();
            this.setPluginState(PluginState.DISABLED);
        }
        catch (PluginException ex) {
            this.setPluginState(PluginState.ENABLED);
            log.warn("Unable to disable plugin '" + this.getKey() + "'", (Throwable)ex);
            throw ex;
        }
        log.debug("Disabled plugin '{}'", (Object)this.getKey());
    }

    protected void disableInternal() {
    }

    public Set<String> getRequiredPlugins() {
        return this.getDependencies().getAll();
    }

    @Nonnull
    public PluginDependencies getDependencies() {
        return new PluginDependencies();
    }

    public final Set<String> getActivePermissions() {
        return this.permissions.get();
    }

    private Set<String> getPermissionsInternal() {
        return Collections.unmodifiableSet(StreamSupport.stream(this.getPermissionsForCurrentInstallationMode().spliterator(), false).map(PluginPermission::getName).collect(Collectors.toSet()));
    }

    private Iterable<PluginPermission> getPermissionsForCurrentInstallationMode() {
        InstallationMode currentMode = this.getInstallationMode();
        return this.getPluginInformation().getPermissions().stream().filter(permission -> permission.getInstallationMode().map(arg_0 -> currentMode.equals(arg_0)).orElse(true)).collect(Collectors.toList());
    }

    public final boolean hasAllPermissions() {
        return this.getActivePermissions().contains("all_permissions");
    }

    public InstallationMode getInstallationMode() {
        return InstallationMode.LOCAL;
    }

    public void close() {
        this.uninstall();
    }

    public final void install() {
        log.debug("Installing plugin '{}'.", (Object)this.getKey());
        if (this.pluginState.get() == PluginState.INSTALLED) {
            log.debug("Plugin '{}' is already installed, not doing anything.", (Object)this.getKey());
            return;
        }
        try {
            this.installInternal();
            this.setPluginState(PluginState.INSTALLED);
        }
        catch (PluginException ex) {
            log.warn("Unable to install plugin '" + this.getKey() + "'.", (Throwable)ex);
            throw ex;
        }
        log.debug("Installed plugin '{}'.", (Object)this.getKey());
    }

    protected void installInternal() {
        log.debug("Actually installing plugin '{}'.", (Object)this.getKey());
    }

    public final void uninstall() {
        if (this.pluginState.get() == PluginState.UNINSTALLED) {
            return;
        }
        log.debug("Uninstalling plugin '{}'", (Object)this.getKey());
        try {
            this.uninstallInternal();
            this.setPluginState(PluginState.UNINSTALLED);
        }
        catch (PluginException ex) {
            log.warn("Unable to uninstall plugin '" + this.getKey() + "'", (Throwable)ex);
            throw ex;
        }
        log.debug("Uninstalled plugin '{}'", (Object)this.getKey());
    }

    protected void uninstallInternal() {
    }

    public boolean isSystemPlugin() {
        return this.system;
    }

    public boolean containsSystemModule() {
        for (ModuleDescriptor<?> moduleDescriptor : this.modules.values()) {
            if (!moduleDescriptor.isSystemModule()) continue;
            return true;
        }
        return false;
    }

    public void setSystemPlugin(boolean system) {
        this.system = system;
    }

    public void resolve() {
    }

    public Date getDateLoaded() {
        return this.dateLoaded;
    }

    public Date getDateInstalled() {
        return new Date(this.dateLoaded.getTime());
    }

    @ExperimentalApi
    public Date getDateEnabling() {
        return this.dateEnabling;
    }

    @ExperimentalApi
    public Date getDateEnabled() {
        return this.dateEnabled;
    }

    public boolean isBundledPlugin() {
        return this.bundledPlugin;
    }

    @Override
    public void setBundledPlugin(boolean bundledPlugin) {
        this.bundledPlugin = bundledPlugin;
    }

    public PluginArtifact getPluginArtifact() {
        return this.pluginArtifact;
    }

    public Optional<String> getScopeKey() {
        return this.pluginInformation.getScopeKey();
    }

    @Override
    public Iterable<ModuleDescriptor<?>> getDynamicModuleDescriptors() {
        return Collections.unmodifiableSet(new HashSet(this.dynamicModules));
    }

    @Override
    public boolean addDynamicModuleDescriptor(ModuleDescriptor<?> module) {
        this.addModuleDescriptor(module);
        return this.dynamicModules.add(module);
    }

    @Override
    public boolean removeDynamicModuleDescriptor(ModuleDescriptor<?> module) {
        this.removeModuleDescriptor(module.getKey());
        return this.dynamicModules.remove(module);
    }

    @Override
    public int compareTo(@Nonnull Plugin otherPlugin) {
        if (otherPlugin.getKey() == null) {
            if (this.getKey() == null) {
                return 0;
            }
            return 1;
        }
        if (this.getKey() == null) {
            return -1;
        }
        if (!otherPlugin.getKey().equals(this.getKey())) {
            return this.getKey().compareTo(otherPlugin.getKey());
        }
        String thisVersion = AbstractPlugin.cleanVersionString(this.getPluginInformation() != null ? this.getPluginInformation().getVersion() : null);
        String otherVersion = AbstractPlugin.cleanVersionString(otherPlugin.getPluginInformation() != null ? otherPlugin.getPluginInformation().getVersion() : null);
        if (!VersionStringComparator.isValidVersionString(thisVersion)) {
            if (!VersionStringComparator.isValidVersionString(otherVersion)) {
                return 0;
            }
            return -1;
        }
        if (!VersionStringComparator.isValidVersionString(otherVersion)) {
            return 1;
        }
        if (VersionStringComparator.isSnapshotVersion(thisVersion) && VersionStringComparator.isSnapshotVersion(otherVersion)) {
            int comparison = new VersionStringComparator().compare(thisVersion, otherVersion);
            if (comparison == 0) {
                return this.getDateInstalled().compareTo(otherPlugin.getDateInstalled());
            }
            return comparison;
        }
        return new VersionStringComparator().compare(thisVersion, otherVersion);
    }

    @Internal
    public static String cleanVersionString(String version) {
        if (version == null || version.trim().equals("")) {
            return "0";
        }
        return version.replaceAll(" ", "");
    }

    public String toString() {
        PluginInformation info = this.getPluginInformation();
        return this.getKey() + ":" + (info == null ? "?" : info.getVersion());
    }
}


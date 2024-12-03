/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.upm.core.rest.representations;

import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.ApplicationPluginsManager;
import com.atlassian.upm.core.Change;
import com.atlassian.upm.core.LicensingUsageVerifier;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.PluginMetadataAccessor;
import com.atlassian.upm.core.PluginRestartRequiredService;
import com.atlassian.upm.core.PluginRetriever;
import com.atlassian.upm.core.impl.UpmAppManager;
import com.atlassian.upm.core.permission.Permission;
import com.atlassian.upm.core.rest.BaseUriBuilder;
import com.atlassian.upm.core.rest.representations.BasePluginRepresentationFactory;
import com.atlassian.upm.core.rest.representations.ChangesRequiringRestartRepresentation;
import com.atlassian.upm.core.rest.representations.DefaultLinkBuilder;
import com.atlassian.upm.core.rest.representations.ErrorRepresentation;
import com.atlassian.upm.core.rest.representations.LinksMapBuilder;
import com.atlassian.upm.core.rest.representations.PluginCollectionRepresentation;
import com.atlassian.upm.core.rest.representations.PluginModuleRepresentation;
import com.atlassian.upm.core.rest.representations.PluginRepresentation;
import com.atlassian.upm.core.rest.representations.PluginSummaryRepresentation;
import com.atlassian.upm.core.rest.representations.RestartState;
import com.atlassian.upm.core.rest.representations.VendorRepresentation;
import com.atlassian.upm.core.rest.resources.RequestContext;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.license.LicensedPlugins;
import com.google.common.collect.ImmutableList;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.commons.lang3.StringUtils;

public class DefaultRepresentationFactory
implements BasePluginRepresentationFactory {
    private final PluginRetriever pluginRetriever;
    private final PluginMetadataAccessor metadata;
    private final BaseUriBuilder uriBuilder;
    private final DefaultLinkBuilder linkBuilder;
    private final PermissionEnforcer permissionEnforcer;
    private final PluginRestartRequiredService restartRequiredService;
    private final LicensingUsageVerifier licensingUsageVerifier;
    private final ApplicationPluginsManager applicationPluginsManager;
    protected final UpmAppManager appManager;

    public DefaultRepresentationFactory(PluginRetriever pluginRetriever, PluginMetadataAccessor metadata, BaseUriBuilder uriBuilder, DefaultLinkBuilder linkBuilder, PermissionEnforcer permissionEnforcer, PluginRestartRequiredService restartRequiredService, UpmAppManager appManager, LicensingUsageVerifier licensingUsageVerifier, ApplicationPluginsManager applicationPluginsManager) {
        this.pluginRetriever = Objects.requireNonNull(pluginRetriever, "pluginRetriever");
        this.metadata = Objects.requireNonNull(metadata, "metadata");
        this.uriBuilder = Objects.requireNonNull(uriBuilder, "uriBuilder");
        this.linkBuilder = Objects.requireNonNull(linkBuilder, "linkBuilder");
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
        this.restartRequiredService = Objects.requireNonNull(restartRequiredService, "restartRequiredService");
        this.appManager = Objects.requireNonNull(appManager, "appManager");
        this.licensingUsageVerifier = Objects.requireNonNull(licensingUsageVerifier, "licensingUsageVerifier");
        this.applicationPluginsManager = Objects.requireNonNull(applicationPluginsManager, "applicationPluginsManager");
    }

    @Override
    public ErrorRepresentation createErrorRepresentation(String message) {
        return new ErrorRepresentation(Objects.requireNonNull(message, "message"), null);
    }

    @Override
    public ErrorRepresentation createErrorRepresentation(String message, String subCode) {
        return new ErrorRepresentation(Objects.requireNonNull(message, "message"), Objects.requireNonNull(subCode, "subCode"));
    }

    @Override
    public ErrorRepresentation createI18nErrorRepresentation(String i18nKey) {
        return new ErrorRepresentation(null, Objects.requireNonNull(i18nKey, "i18nKey"));
    }

    @Override
    public VendorRepresentation createVendorRepresentation(Plugin plugin) {
        String name = plugin.getPluginInformation().getVendorName();
        String url = plugin.getPluginInformation().getVendorUrl();
        if (StringUtils.isEmpty((CharSequence)name)) {
            return null;
        }
        if (StringUtils.isEmpty((CharSequence)url)) {
            return new VendorRepresentation(name, null, null);
        }
        try {
            URI vendorUri = URI.create(url);
            return new VendorRepresentation(name, vendorUri, null);
        }
        catch (IllegalArgumentException iae) {
            return new VendorRepresentation(name, null, null);
        }
    }

    @Override
    public PluginCollectionRepresentation createInstalledPluginCollectionRepresentation(Locale locale, List<Plugin> plugins, Map<String, UpmAppManager.ApplicationDescriptorModuleInfo> appPlugins, RequestContext context) {
        return new PluginCollectionRepresentation(this, this.uriBuilder, this.linkBuilder, locale, plugins, appPlugins);
    }

    @Override
    public PluginRepresentation createPluginRepresentation(Plugin plugin) {
        Objects.requireNonNull(plugin, "plugin");
        ImmutableList modules = null;
        if (this.permissionEnforcer.hasPermission(Permission.GET_PLUGIN_MODULES, plugin)) {
            modules = ImmutableList.copyOf((Collection)StreamSupport.stream(plugin.getModules().spliterator(), false).map(this::createPluginModuleRepresentation).collect(Collectors.toList()));
        }
        return new PluginRepresentation(this.linkBuilder.buildLinksForInstalledPlugin(plugin).build(), plugin.getKey(), plugin.isEnabled(), plugin.isEnabledByDefault(), plugin.getPluginInformation().getVersion(), plugin.getPluginInformation().getDescription(), plugin.getName(), (Collection<PluginModuleRepresentation>)modules, this.metadata.isUserInstalled(plugin), this.metadata.isOptional(plugin), plugin.hasUnrecognisedModuleTypes(), plugin.isUnloadable(), RestartState.toString(plugin.getRestartState()), plugin.isStaticPlugin(), this.isPluginLicenseManageable(plugin), plugin.isConnect(), this.createVendorRepresentation(plugin), this.applicationPluginsManager.getApplicationKey(plugin.getPlugin()).getOrElse((String)null), null, this.applicationPluginsManager.isUninstallable(plugin.getPlugin().getKey()));
    }

    @Override
    public PluginSummaryRepresentation createPluginSummaryRepresentation(Plugin plugin, Option<UpmAppManager.ApplicationDescriptorModuleInfo> appPluginInfo) {
        Objects.requireNonNull(plugin, "plugin");
        return new PluginSummaryRepresentation(this.pluginRetriever.isPluginEnabled(plugin.getKey()), this.linkBuilder.buildLinksForInstalledPlugin(plugin).build(), plugin.getName(), plugin.getVersion(), this.metadata.isUserInstalled(plugin), this.metadata.isOptional(plugin), plugin.isStaticPlugin(), plugin.isUnloadable(), RestartState.toString(plugin.getRestartState()), plugin.getPluginInformation().getDescription(), plugin.getKey(), this.isPluginLicenseManageable(plugin), plugin.isConnect(), this.createVendorRepresentation(plugin), appPluginInfo.map(this.appManager.applicationPluginAppKey()).getOrElse((String)null), appPluginInfo.map(this.appManager.applicationPluginTypeString()).getOrElse((String)null), this.applicationPluginsManager.isUninstallable(plugin.getPlugin().getKey()));
    }

    private boolean isPluginLicenseManageable(Plugin plugin) {
        return LicensedPlugins.usesLicensing(plugin.getPlugin(), this.licensingUsageVerifier) && this.permissionEnforcer.hasPermission(Permission.MANAGE_PLUGIN_LICENSE, plugin);
    }

    @Override
    public PluginModuleRepresentation createPluginModuleRepresentation(Plugin.Module module) {
        LinksMapBuilder links = this.linkBuilder.buildLinkForSelf(this.uriBuilder.buildPluginModuleUri(module.getPluginKey(), module.getKey())).putIfPermittedForModule(Permission.MANAGE_PLUGIN_MODULE_ENABLEMENT, Option.some(module), "modify", this.uriBuilder.buildPluginModuleUri(module.getPluginKey(), module.getKey())).put("plugin", this.uriBuilder.buildPluginUri(module.getPluginKey()));
        String name = null;
        String description = null;
        if (((Boolean)this.restartRequiredService.getRestartRequiredChange(module.getPlugin()).map(change -> !change.getAction().equals("install")).getOrElse(true)).booleanValue()) {
            name = module.getName();
            description = module.getDescription();
        }
        return new PluginModuleRepresentation(module.getKey(), module.getCompleteKey(), links.build(), this.pluginRetriever.isPluginModuleEnabled(module.getCompleteKey()), this.metadata.isOptional(module), name, description, module.hasRecognisableType(), module.isBroken());
    }

    @Override
    public ChangesRequiringRestartRepresentation createChangesRequiringRestartRepresentation(Iterable<Change> restartChanges) {
        return new ChangesRequiringRestartRepresentation(restartChanges, this.uriBuilder, this.linkBuilder);
    }
}


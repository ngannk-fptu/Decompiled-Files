/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.core.rest.representations;

import com.atlassian.upm.core.rest.representations.AbstractPluginRepresentation;
import com.atlassian.upm.core.rest.representations.PluginModuleRepresentation;
import com.atlassian.upm.core.rest.representations.VendorRepresentation;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.net.URI;
import java.util.Collection;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class PluginRepresentation
extends AbstractPluginRepresentation {
    @JsonProperty
    private final boolean enabledByDefault;
    @JsonProperty
    private final Collection<PluginModuleRepresentation> modules;
    @JsonProperty
    private final boolean unrecognisedModuleTypes;

    @JsonCreator
    public PluginRepresentation(@JsonProperty(value="links") Map<String, URI> links, @JsonProperty(value="key") String key, @JsonProperty(value="enabled") boolean enabled, @JsonProperty(value="enabledByDefault") boolean enabledByDefault, @JsonProperty(value="version") String version, @JsonProperty(value="description") String description, @JsonProperty(value="name") String name, @JsonProperty(value="modules") Collection<PluginModuleRepresentation> modules, @JsonProperty(value="userInstalled") boolean userInstalled, @JsonProperty(value="optional") boolean optional, @JsonProperty(value="unrecognisedModuleTypes") boolean unrecognisedModuleTypes, @JsonProperty(value="unloadable") boolean unloadable, @JsonProperty(value="restartState") String restartState, @JsonProperty(value="static") boolean staticPlugin, @JsonProperty(value="usesLicensing") boolean usesLicensing, @JsonProperty(value="remotable") boolean remotable, @JsonProperty(value="vendor") VendorRepresentation vendor, @JsonProperty(value="applicationKey") String applicationKey, @JsonProperty(value="applicationPluginType") String applicationPluginType, @JsonProperty(value="uninstallable") boolean uninstallable) {
        super(enabled, (Map<String, URI>)ImmutableMap.copyOf(links), name, version, userInstalled, optional, staticPlugin, unloadable, restartState, description, key, usesLicensing, remotable, vendor, applicationKey, applicationPluginType, uninstallable);
        this.enabledByDefault = enabledByDefault;
        this.modules = modules != null ? ImmutableList.copyOf(modules) : null;
        this.unrecognisedModuleTypes = unrecognisedModuleTypes;
    }

    public boolean isEnabledByDefault() {
        return this.enabledByDefault;
    }

    public boolean hasUnrecognisedModuleTypes() {
        return this.unrecognisedModuleTypes;
    }

    public URI getConfigureUrl() {
        return this.getLinks().get("configure");
    }

    public Collection<PluginModuleRepresentation> getModules() {
        return this.modules;
    }

    public URI getChangeRequiringRestartLink() {
        return this.getLinks().get("change-requiring-restart");
    }

    @Override
    public URI getPluginIconLink() {
        return this.getLinks().get("plugin-icon");
    }

    @Override
    public URI getPluginLogoLink() {
        return this.getLinks().get("plugin-logo");
    }

    public URI getUninstallLink() {
        return this.getLinks().get("delete");
    }
}


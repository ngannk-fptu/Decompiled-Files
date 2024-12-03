/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.core.rest.representations;

import com.atlassian.upm.core.rest.representations.VendorRepresentation;
import com.google.common.collect.ImmutableMap;
import java.net.URI;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonProperty;

public abstract class AbstractPluginRepresentation {
    @JsonProperty
    private final boolean enabled;
    @JsonProperty
    private final Map<String, URI> links;
    @JsonProperty
    private final String name;
    @JsonProperty
    private final String version;
    @JsonProperty
    private final String key;
    @JsonProperty
    private final boolean userInstalled;
    @JsonProperty
    private final boolean optional;
    @JsonProperty(value="static")
    private final boolean staticPlugin;
    @JsonProperty
    private final boolean unloadable;
    @JsonProperty
    private final String restartState;
    @JsonProperty
    private final String description;
    @JsonProperty
    private final boolean usesLicensing;
    @JsonProperty
    private final boolean remotable;
    @JsonProperty
    private final VendorRepresentation vendor;
    @JsonProperty
    private final String applicationKey;
    @JsonProperty
    private final String applicationPluginType;
    @JsonProperty
    private final boolean uninstallable;

    public AbstractPluginRepresentation(boolean enabled, Map<String, URI> links, String name, String version, boolean userInstalled, boolean optional, boolean staticPlugin, boolean unloadable, String restartState, String description, String key, boolean usesLicensing, boolean remotable, VendorRepresentation vendor, String applicationKey, String applicationPluginType, boolean uninstallable) {
        this.enabled = enabled;
        this.links = ImmutableMap.copyOf(links);
        this.name = name;
        this.version = version;
        this.userInstalled = userInstalled;
        this.optional = optional;
        this.staticPlugin = staticPlugin;
        this.unloadable = unloadable;
        this.restartState = restartState;
        this.description = description;
        this.key = key;
        this.usesLicensing = usesLicensing;
        this.remotable = remotable;
        this.vendor = vendor;
        this.applicationKey = applicationKey;
        this.applicationPluginType = applicationPluginType;
        this.uninstallable = uninstallable;
    }

    public Map<String, URI> getLinks() {
        return this.links;
    }

    public URI getSelfLink() {
        return this.links.get("self");
    }

    public boolean isUserInstalled() {
        return this.userInstalled;
    }

    public boolean isOptional() {
        return this.optional;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public boolean isStatic() {
        return this.staticPlugin;
    }

    public boolean isUnloadable() {
        return this.unloadable;
    }

    public boolean isRemotable() {
        return this.remotable;
    }

    public String getName() {
        return this.name;
    }

    public String getVersion() {
        return this.version;
    }

    public String getRestartState() {
        return this.restartState;
    }

    public String getDescription() {
        return this.description;
    }

    public String getKey() {
        return this.key;
    }

    public boolean usesLicensing() {
        return this.usesLicensing;
    }

    public URI getPluginIconLink() {
        return this.links.get("plugin-icon");
    }

    public URI getPluginLogoLink() {
        return this.links.get("plugin-logo");
    }

    public VendorRepresentation getVendor() {
        return this.vendor;
    }

    public String getApplicationKey() {
        return this.applicationKey;
    }

    public String getApplicationPluginType() {
        return this.applicationPluginType;
    }

    public boolean getUninstallable() {
        return this.uninstallable;
    }
}


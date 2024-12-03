/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.core.rest.representations;

import com.atlassian.upm.core.rest.representations.AbstractPluginRepresentation;
import com.atlassian.upm.core.rest.representations.VendorRepresentation;
import com.google.common.collect.ImmutableMap;
import java.net.URI;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class PluginSummaryRepresentation
extends AbstractPluginRepresentation {
    @JsonCreator
    public PluginSummaryRepresentation(@JsonProperty(value="enabled") boolean enabled, @JsonProperty(value="links") Map<String, URI> links, @JsonProperty(value="name") String name, @JsonProperty(value="version") String version, @JsonProperty(value="userInstalled") boolean userInstalled, @JsonProperty(value="optional") boolean optional, @JsonProperty(value="static") boolean staticPlugin, @JsonProperty(value="unloadable") boolean unloadable, @JsonProperty(value="restartState") String restartState, @JsonProperty(value="description") String description, @JsonProperty(value="key") String key, @JsonProperty(value="usesLicensing") boolean usesLicensing, @JsonProperty(value="remotable") boolean remotable, @JsonProperty(value="vendor") VendorRepresentation vendor, @JsonProperty(value="applicationKey") String applicationKey, @JsonProperty(value="applicationPluginType") String applicationPluginType, @JsonProperty(value="uninstallable") boolean uninstallable) {
        super(enabled, (Map<String, URI>)ImmutableMap.copyOf(links), name, version, userInstalled, optional, staticPlugin, unloadable, restartState, description, key, usesLicensing, remotable, vendor, applicationKey, applicationPluginType, uninstallable);
    }
}


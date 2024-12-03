/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.rest.monitor.representations;

import java.util.Objects;
import org.codehaus.jackson.annotate.JsonProperty;

public class PluginStateRep {
    @JsonProperty
    public final String key;
    @JsonProperty
    public final String name;
    @JsonProperty
    public final String version;
    @JsonProperty
    public final boolean enabled;
    @JsonProperty
    public final boolean adminCanDisable;
    @JsonProperty
    public final boolean atlassianConnect;

    public PluginStateRep(String key, String name, String version, boolean enabled, boolean adminCanDisable, boolean atlassianConnect) {
        this.key = Objects.requireNonNull(key, "key");
        this.name = Objects.requireNonNull(name, "name");
        this.version = Objects.requireNonNull(version, "version");
        this.enabled = enabled;
        this.adminCanDisable = adminCanDisable;
        this.atlassianConnect = atlassianConnect;
    }
}


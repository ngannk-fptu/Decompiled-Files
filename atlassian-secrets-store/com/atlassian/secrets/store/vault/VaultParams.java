/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonProperty
 */
package com.atlassian.secrets.store.vault;

import com.atlassian.secrets.store.vault.auth.VaultAuthenticationMethod;
import com.fasterxml.jackson.annotation.JsonProperty;

public class VaultParams {
    private final String mount;
    private final String path;
    private final String key;
    private final String endpoint;
    private final VaultAuthenticationMethod authenticationType;

    public VaultParams(@JsonProperty(value="mount", required=true) String mount, @JsonProperty(value="path", required=true) String path, @JsonProperty(value="key", required=true) String key, @JsonProperty(value="endpoint", required=true) String endpoint, @JsonProperty(value="authenticationType") VaultAuthenticationMethod authenticationType) {
        this.mount = mount;
        this.path = path;
        this.key = key;
        this.endpoint = endpoint;
        this.authenticationType = authenticationType == null ? VaultAuthenticationMethod.TOKEN : authenticationType;
    }

    public String getMount() {
        return this.mount;
    }

    public String getPath() {
        return this.path;
    }

    public String getKey() {
        return this.key;
    }

    public String getEndpoint() {
        return this.endpoint;
    }

    public VaultAuthenticationMethod getAuthenticationType() {
        return this.authenticationType;
    }

    public String toString() {
        return "VaultParams{mount='" + this.mount + '\'' + ", path='" + this.path + '\'' + ", key='" + this.key + '\'' + ", endpoint='" + this.endpoint + '\'' + ", authenticationType='" + (Object)((Object)this.authenticationType) + '\'' + '\'' + '}';
    }
}


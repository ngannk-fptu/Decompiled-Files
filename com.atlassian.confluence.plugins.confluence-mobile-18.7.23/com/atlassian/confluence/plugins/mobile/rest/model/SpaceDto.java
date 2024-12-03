/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.mobile.rest.model;

import java.util.List;
import org.codehaus.jackson.annotate.JsonProperty;

public class SpaceDto {
    @JsonProperty
    private String key;
    @JsonProperty
    private List<String> permissions;

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<String> getPermissions() {
        return this.permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }
}


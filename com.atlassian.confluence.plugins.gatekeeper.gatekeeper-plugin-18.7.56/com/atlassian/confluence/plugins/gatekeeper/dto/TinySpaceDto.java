/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonAutoDetect
 *  com.fasterxml.jackson.annotation.JsonProperty
 */
package com.atlassian.confluence.plugins.gatekeeper.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect
public class TinySpaceDto {
    @JsonProperty(value="k")
    private String key;
    @JsonProperty(value="n")
    private String name;
    @JsonProperty(value="u")
    private String logoUrl;

    public TinySpaceDto(String key, String name) {
        this.key = key;
        this.name = name;
    }

    public String getKey() {
        return this.key;
    }

    public String getName() {
        return this.name;
    }

    public String getLogoUrl() {
        return this.logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }
}


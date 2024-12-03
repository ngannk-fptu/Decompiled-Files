/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.efi.rest.beans;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class SpaceBean {
    @JsonProperty
    private String key;
    @JsonProperty
    private String name;
    @JsonProperty
    private boolean temporary;

    public SpaceBean() {
    }

    public SpaceBean(String key, String name, boolean temporary) {
        this.key = key;
        this.name = name;
        this.temporary = temporary;
    }

    public SpaceBean(String key, String name) {
        this(key, name, false);
    }

    public String getKey() {
        return this.key;
    }

    public String getName() {
        return this.name;
    }

    public boolean isTemporary() {
        return this.temporary;
    }
}


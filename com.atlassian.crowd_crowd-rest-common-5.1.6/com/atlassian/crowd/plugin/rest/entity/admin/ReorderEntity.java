/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.crowd.plugin.rest.entity.admin;

import org.codehaus.jackson.annotate.JsonProperty;

public class ReorderEntity {
    @JsonProperty(value="after")
    private String after;
    @JsonProperty(value="position")
    private String position;

    public String getAfter() {
        return this.after;
    }

    public String getPosition() {
        return this.position;
    }
}


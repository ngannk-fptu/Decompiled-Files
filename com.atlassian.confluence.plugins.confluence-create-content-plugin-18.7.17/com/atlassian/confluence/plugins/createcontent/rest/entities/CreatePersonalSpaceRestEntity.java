/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.createcontent.rest.entities;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class CreatePersonalSpaceRestEntity {
    @JsonProperty
    private final String spaceUserKey;
    @JsonProperty
    private final String spacePermission;

    @JsonCreator
    public CreatePersonalSpaceRestEntity(@JsonProperty(value="spaceUserKey") String spaceUserKey, @JsonProperty(value="spacePermission") String spacePermission) {
        this.spaceUserKey = spaceUserKey;
        this.spacePermission = spacePermission;
    }

    public String getSpaceUserKey() {
        return this.spaceUserKey;
    }

    public String isSpacePermission() {
        return this.spacePermission;
    }
}


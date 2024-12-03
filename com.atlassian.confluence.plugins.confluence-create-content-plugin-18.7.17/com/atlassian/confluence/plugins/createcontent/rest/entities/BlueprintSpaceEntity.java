/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.spaces.Space
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.createcontent.rest.entities;

import com.atlassian.confluence.plugins.createcontent.services.model.BlueprintSpace;
import com.atlassian.confluence.spaces.Space;
import org.codehaus.jackson.annotate.JsonProperty;

public class BlueprintSpaceEntity {
    @JsonProperty
    private String key;
    @JsonProperty
    private String name;
    @JsonProperty
    private String description;
    @JsonProperty
    private String url;

    public BlueprintSpaceEntity() {
    }

    public BlueprintSpaceEntity(BlueprintSpace blueprintSpace, String baseUrl) {
        Space space = blueprintSpace.getSpace();
        this.key = space.getKey();
        this.name = space.getName();
        this.description = space.getDescription().getBodyAsString();
        this.url = baseUrl + space.getUrlPath();
    }

    public String getKey() {
        return this.key;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public String getUrl() {
        return this.url;
    }
}


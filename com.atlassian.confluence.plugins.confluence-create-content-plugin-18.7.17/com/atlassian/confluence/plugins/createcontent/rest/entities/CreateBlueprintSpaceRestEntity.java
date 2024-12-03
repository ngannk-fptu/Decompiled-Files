/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.createcontent.rest.entities;

import com.atlassian.confluence.plugins.createcontent.services.model.CreateBlueprintSpaceEntity;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class CreateBlueprintSpaceRestEntity
implements CreateBlueprintSpaceEntity {
    @JsonProperty
    private final String spaceKey;
    @JsonProperty
    private final String name;
    @JsonProperty
    private final String description;
    @JsonProperty
    private final String permissions;
    @JsonProperty
    private final String spaceBlueprintId;
    @JsonProperty
    private final Map<String, Object> context;

    @JsonCreator
    public CreateBlueprintSpaceRestEntity(@JsonProperty(value="spaceKey") String spaceKey, @JsonProperty(value="name") String name, @JsonProperty(value="description") String description, @JsonProperty(value="permissions") String permissions, @JsonProperty(value="spaceBlueprintId") String spaceBlueprintId, @JsonProperty(value="context") Map<String, Object> context) {
        this.spaceKey = spaceKey;
        this.name = name;
        this.description = description;
        this.permissions = permissions;
        this.spaceBlueprintId = spaceBlueprintId;
        this.context = context;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public String getSpaceKey() {
        return this.spaceKey;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getPermissions() {
        return this.permissions;
    }

    @Override
    public String getSpaceBlueprintId() {
        return this.spaceBlueprintId;
    }

    @Override
    public Map<String, Object> getContext() {
        return this.context;
    }
}


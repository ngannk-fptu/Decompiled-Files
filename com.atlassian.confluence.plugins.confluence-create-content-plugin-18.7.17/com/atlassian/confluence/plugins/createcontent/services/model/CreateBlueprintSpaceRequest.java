/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.createcontent.services.model;

import com.atlassian.confluence.plugins.createcontent.impl.SpaceBlueprint;
import com.atlassian.confluence.plugins.createcontent.services.model.CreateBlueprintSpaceEntity;
import java.util.Map;

public class CreateBlueprintSpaceRequest {
    private final SpaceBlueprint blueprint;
    private String spaceKey;
    private String name;
    private String description;
    private Map<String, Object> context;

    public CreateBlueprintSpaceRequest(SpaceBlueprint blueprint, CreateBlueprintSpaceEntity entity) {
        this.blueprint = blueprint;
        this.spaceKey = entity.getSpaceKey();
        this.name = entity.getName();
        this.description = entity.getDescription();
        this.context = entity.getContext();
    }

    public SpaceBlueprint getBlueprint() {
        return this.blueprint;
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public Map<String, Object> getContext() {
        return this.context;
    }
}


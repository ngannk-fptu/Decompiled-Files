/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.plugin.ModuleCompleteKey
 */
package com.atlassian.confluence.plugins.createcontent.model;

import com.atlassian.confluence.spaces.Space;
import com.atlassian.plugin.ModuleCompleteKey;
import java.util.UUID;

public class BlueprintIdBundle {
    private final UUID blueprintId;
    private final ModuleCompleteKey blueprintModuleKey;
    private final String spaceKey;
    private final Space space;

    public BlueprintIdBundle(UUID blueprintId, ModuleCompleteKey blueprintModuleKey, Space space) {
        this.blueprintId = blueprintId;
        this.blueprintModuleKey = blueprintModuleKey;
        this.space = space;
        this.spaceKey = space.getKey();
    }

    public BlueprintIdBundle(UUID blueprintId, ModuleCompleteKey blueprintModuleKey, String spaceKey) {
        this.blueprintId = blueprintId;
        this.blueprintModuleKey = blueprintModuleKey;
        this.spaceKey = spaceKey;
        this.space = null;
    }

    public UUID getBlueprintId() {
        return this.blueprintId;
    }

    public ModuleCompleteKey getBlueprintModuleKey() {
        return this.blueprintModuleKey;
    }

    public Space getSpace() {
        return this.space;
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }

    public String toString() {
        return "Blueprint id: " + this.blueprintId + ", module key: " + this.blueprintModuleKey + ", space: " + this.space;
    }
}


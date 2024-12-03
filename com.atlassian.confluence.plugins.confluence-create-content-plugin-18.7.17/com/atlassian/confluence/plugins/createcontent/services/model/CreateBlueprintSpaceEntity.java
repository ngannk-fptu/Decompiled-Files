/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.createcontent.services.model;

import java.util.Map;

public interface CreateBlueprintSpaceEntity {
    public String getSpaceKey();

    public String getName();

    public String getDescription();

    public String getPermissions();

    public String getSpaceBlueprintId();

    public Map<String, Object> getContext();
}


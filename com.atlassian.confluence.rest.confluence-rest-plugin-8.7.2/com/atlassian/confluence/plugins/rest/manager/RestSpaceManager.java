/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.rest.manager;

import com.atlassian.confluence.plugins.rest.entities.SpaceEntity;
import com.atlassian.confluence.plugins.rest.entities.SpaceEntityList;
import com.atlassian.confluence.plugins.rest.entities.SpaceEntityListContext;

public interface RestSpaceManager {
    public SpaceEntity getSpaceEntity(String var1, boolean var2);

    public SpaceEntityList getSpaceEntityList(SpaceEntityListContext var1);

    public SpaceEntity expand(SpaceEntity var1);
}


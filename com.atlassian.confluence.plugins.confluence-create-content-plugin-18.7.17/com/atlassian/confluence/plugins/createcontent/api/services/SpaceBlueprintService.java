/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.plugins.createcontent.api.services;

import com.atlassian.annotations.PublicApi;
import com.atlassian.confluence.plugins.createcontent.api.exceptions.BlueprintIllegalArgumentException;
import com.atlassian.confluence.plugins.createcontent.rest.entities.CreatePersonalSpaceRestEntity;
import com.atlassian.confluence.plugins.createcontent.services.model.BlueprintSpace;
import com.atlassian.confluence.plugins.createcontent.services.model.CreateBlueprintSpaceEntity;
import com.atlassian.confluence.user.ConfluenceUser;

@PublicApi
public interface SpaceBlueprintService {
    public BlueprintSpace createSpace(CreateBlueprintSpaceEntity var1, ConfluenceUser var2) throws BlueprintIllegalArgumentException;

    public BlueprintSpace createPersonalSpace(CreatePersonalSpaceRestEntity var1, ConfluenceUser var2);
}


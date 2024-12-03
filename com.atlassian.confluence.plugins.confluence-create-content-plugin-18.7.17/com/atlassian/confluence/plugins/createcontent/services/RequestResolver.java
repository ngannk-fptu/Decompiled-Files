/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.plugins.createcontent.services;

import com.atlassian.confluence.plugins.createcontent.api.exceptions.BlueprintIllegalArgumentException;
import com.atlassian.confluence.plugins.createcontent.rest.entities.CreatePersonalSpaceRestEntity;
import com.atlassian.confluence.plugins.createcontent.services.model.CreateBlueprintPageEntity;
import com.atlassian.confluence.plugins.createcontent.services.model.CreateBlueprintPageRequest;
import com.atlassian.confluence.plugins.createcontent.services.model.CreateBlueprintSpaceEntity;
import com.atlassian.confluence.plugins.createcontent.services.model.CreateBlueprintSpaceRequest;
import com.atlassian.confluence.plugins.createcontent.services.model.CreatePersonalSpaceRequest;
import com.atlassian.confluence.user.ConfluenceUser;

public interface RequestResolver {
    public CreateBlueprintPageRequest resolve(CreateBlueprintPageEntity var1, ConfluenceUser var2) throws BlueprintIllegalArgumentException;

    public CreateBlueprintSpaceRequest resolve(CreateBlueprintSpaceEntity var1, ConfluenceUser var2) throws BlueprintIllegalArgumentException;

    public CreatePersonalSpaceRequest resolve(CreatePersonalSpaceRestEntity var1, ConfluenceUser var2);
}


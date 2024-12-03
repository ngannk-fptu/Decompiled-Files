/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 */
package com.atlassian.confluence.plugins.createcontent.services;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.plugins.createcontent.services.model.CreateBlueprintPageEntity;

public interface RequestStorage {
    public void storeCreateRequest(CreateBlueprintPageEntity var1, ContentEntityObject var2);

    public CreateBlueprintPageEntity retrieveRequest(ContentEntityObject var1);

    public void clear(ContentEntityObject var1);
}


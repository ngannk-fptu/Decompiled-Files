/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.createcontent.api.services;

import com.atlassian.confluence.plugins.createcontent.services.model.CreateBlueprintPageEntity;

public interface ContentBlueprintSanitiserManager {
    public CreateBlueprintPageEntity sanitise(CreateBlueprintPageEntity var1);

    public CreateBlueprintPageEntity unsanitise(CreateBlueprintPageEntity var1);
}


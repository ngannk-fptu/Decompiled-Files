/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.createcontent.services;

import com.atlassian.confluence.plugins.createcontent.rest.entities.CreateDialogWebItemEntity;
import java.util.List;

public interface BlueprintDiscoveryService {
    public List<CreateDialogWebItemEntity> discoverRecentlyInstalled(List<CreateDialogWebItemEntity> var1);
}


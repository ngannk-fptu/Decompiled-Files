/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.createcontent.services;

import com.atlassian.confluence.plugins.createcontent.rest.SpaceResultsEntity;
import java.util.List;
import java.util.Map;

public interface SpaceCollectionService {
    public Map<String, SpaceResultsEntity> getSpaces(List<String> var1, int var2, int var3, String var4);
}


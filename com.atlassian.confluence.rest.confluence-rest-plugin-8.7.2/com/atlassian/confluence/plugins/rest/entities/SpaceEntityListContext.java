/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.rest.entities;

import java.util.Set;

public class SpaceEntityListContext {
    private final String spaceType;
    private final Integer startIndex;
    private final Integer maxResults;
    private Set<String> spaceKeys;

    public SpaceEntityListContext(String spaceType, Integer startIndex, Integer maxResults, Set<String> spaceKeys) {
        this.spaceType = spaceType;
        this.startIndex = startIndex;
        this.maxResults = maxResults;
        this.spaceKeys = spaceKeys;
    }

    public String getSpaceType() {
        return this.spaceType;
    }

    public Integer getStartIndex() {
        return this.startIndex;
    }

    public Integer getMaxResults() {
        return this.maxResults;
    }

    public Set<String> getSpaceKeys() {
        return this.spaceKeys;
    }
}


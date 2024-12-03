/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ListBuilder
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.spaces.SpaceType
 *  com.atlassian.confluence.spaces.SpacesQuery
 *  com.atlassian.sal.api.project.ProjectManager
 */
package com.atlassian.sal.confluence.project;

import com.atlassian.confluence.core.ListBuilder;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.SpaceType;
import com.atlassian.confluence.spaces.SpacesQuery;
import com.atlassian.sal.api.project.ProjectManager;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class ConfluenceProjectManager
implements ProjectManager {
    private final SpaceManager spaceManager;

    public ConfluenceProjectManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }

    public Collection<String> getAllProjectKeys() {
        HashSet<String> results = new HashSet<String>();
        ListBuilder listBuilder = this.spaceManager.getSpaces(SpacesQuery.newQuery().withSpaceType(SpaceType.GLOBAL).build());
        for (List list : listBuilder) {
            for (Space space : list) {
                results.add(space.getKey());
            }
        }
        return results;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.spaces;

import com.atlassian.confluence.spaces.SpaceGroup;
import com.atlassian.confluence.spaces.SpaceGroupManager;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Deprecated
public class DefaultSpaceGroupManager
implements SpaceGroupManager {
    @Override
    public SpaceGroup createSpaceGroup(String key, String name) {
        return null;
    }

    @Override
    public SpaceGroup createSpaceGroup(String key, String name, String creatorName) {
        return null;
    }

    @Override
    public void saveSpaceGroup(SpaceGroup spaceGroup) {
    }

    @Override
    public void removeSpaceGroup(SpaceGroup spaceGroup, boolean removeSpaces) {
    }

    @Override
    public SpaceGroup getSpaceGroup(long id) {
        return null;
    }

    @Override
    public SpaceGroup getSpaceGroup(String spaceGroupKey) {
        return null;
    }

    @Override
    public List getSpaceGroups() {
        return Collections.emptyList();
    }

    @Override
    public Set<SpaceGroup> getSpaceGroupsForUser(String username) {
        return Collections.emptySet();
    }
}


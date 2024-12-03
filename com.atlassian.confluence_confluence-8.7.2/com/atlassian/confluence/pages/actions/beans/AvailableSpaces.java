/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.pages.actions.beans;

import com.atlassian.confluence.core.ListBuilder;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.SpaceType;
import com.atlassian.confluence.spaces.SpacesQuery;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.ArrayList;
import java.util.List;

public class AvailableSpaces {
    private final SpaceManager spaceManager;

    public AvailableSpaces(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }

    public List<Space> getAvailableSpaces(Space currentSpace, ConfluenceUser user) {
        ListBuilder<Space> spacesListBuilder = this.spaceManager.getSpaces(SpacesQuery.newQuery().forUser(user).withPermission("EDITSPACE").withSpaceType(SpaceType.GLOBAL).build());
        ArrayList<Space> availableSpaces = new ArrayList<Space>(spacesListBuilder.getAvailableSize() + 2);
        for (List list : spacesListBuilder) {
            availableSpaces.addAll(list);
        }
        this.addPersonalAndCurrentSpace(availableSpaces, currentSpace, user);
        return availableSpaces;
    }

    private void addPersonalAndCurrentSpace(List<Space> spaces, Space currentSpace, ConfluenceUser user) {
        Space personalSpace = this.spaceManager.getPersonalSpace(user);
        if (personalSpace != null && !spaces.contains(personalSpace)) {
            spaces.add(0, personalSpace);
        }
        if (currentSpace != null && !spaces.contains(currentSpace)) {
            spaces.add(currentSpace);
        }
    }
}


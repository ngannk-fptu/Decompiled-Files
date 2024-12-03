/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.user;

import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserContentManager;
import com.atlassian.user.User;
import java.util.List;

public class DefaultUserContentManager
implements UserContentManager {
    private SpaceManager spaceManager;

    @Override
    @Deprecated
    public boolean hasAuthoredContent(User user) {
        return this.hasAuthoredContent((ConfluenceUser)user);
    }

    @Override
    public boolean hasAuthoredContent(ConfluenceUser user) {
        return user != null && (!this.getUserEditedSpaces(user).isEmpty() || !this.getUserOwnedSpaces(user).isEmpty() || !this.getUserCommentedSpaces(user).isEmpty());
    }

    private List getUserOwnedSpaces(ConfluenceUser user) {
        List userOwnedSpaces = this.spaceManager.getAuthoredSpacesByUser(user.getName());
        userOwnedSpaces.remove(this.spaceManager.getPersonalSpace(user));
        return userOwnedSpaces;
    }

    private List getUserEditedSpaces(ConfluenceUser user) {
        List userEditedSpaces = this.spaceManager.getSpacesContainingPagesEditedBy(user.getName());
        userEditedSpaces.remove(this.spaceManager.getPersonalSpace(user));
        return userEditedSpaces;
    }

    private List getUserCommentedSpaces(ConfluenceUser user) {
        List userCommentedSpaces = this.spaceManager.getSpacesContainingCommentsBy(user.getName());
        userCommentedSpaces.remove(this.spaceManager.getPersonalSpace(user));
        return userCommentedSpaces;
    }

    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugin.descriptor.web.conditions.user;

import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.userstatus.FavouriteManager;
import com.atlassian.user.User;

public class UserFavouritingTargetUserPersonalSpaceCondition
extends BaseConfluenceCondition {
    private FavouriteManager favouriteManager;
    private SpaceManager spaceManager;

    @Override
    public boolean shouldDisplay(WebInterfaceContext context) {
        Space personalSpace = this.spaceManager.getPersonalSpace(context.getTargetedUser());
        return this.favouriteManager.isUserFavourite((User)context.getCurrentUser(), personalSpace);
    }

    public void setFavouriteManager(FavouriteManager favouriteManager) {
        this.favouriteManager = favouriteManager;
    }

    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }
}


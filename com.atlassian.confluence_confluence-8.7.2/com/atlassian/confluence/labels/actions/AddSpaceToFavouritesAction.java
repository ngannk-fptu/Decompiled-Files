/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.labels.actions;

import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.actions.AbstractSpaceAction;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.persistence.dao.compatibility.FindUserHelper;
import com.atlassian.confluence.userstatus.FavouriteManager;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import org.apache.commons.lang3.StringUtils;

public class AddSpaceToFavouritesAction
extends AbstractSpaceAction {
    private FavouriteManager favouriteManager;
    private String mode;
    private String username;

    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    public String execute() throws Exception {
        ConfluenceUser user;
        Space personalSpace;
        if (this.space == null && StringUtils.isNotBlank((CharSequence)this.username) && (personalSpace = this.spaceManager.getPersonalSpace(user = FindUserHelper.getUserByUsername(this.username))) != null) {
            this.setKey(personalSpace.getKey());
            this.setSpace(personalSpace);
        }
        if (this.getAuthenticatedUser() == null || this.getSpace() == null) {
            return "error";
        }
        this.favouriteManager.addSpaceToFavourites(this.getAuthenticatedUser(), this.getSpace());
        return StringUtils.isBlank((CharSequence)this.mode) ? "success" : this.mode;
    }

    public void setFavouriteManager(FavouriteManager favouriteManager) {
        this.favouriteManager = favouriteManager;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }

    public String getUrlEncodedUsername() {
        return HtmlUtil.urlEncode(this.username);
    }
}


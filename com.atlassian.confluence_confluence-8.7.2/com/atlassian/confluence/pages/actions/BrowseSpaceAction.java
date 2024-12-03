/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.core.actions.RedirectActionHelper;
import com.atlassian.confluence.security.access.annotations.RequiresAnyConfluenceAccess;
import com.atlassian.confluence.spaces.actions.AbstractSpaceAction;
import com.atlassian.confluence.themes.Theme;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;

@RequiresAnyConfluenceAccess
public class BrowseSpaceAction
extends AbstractSpaceAction {
    public static final String BROWSE_SPACE_COOKIE = "confluence.browse.space.cookie";
    public static final String WEB_ITEM_LOCATION = "system.space";
    private String redirectUrl;

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        Theme theme;
        RedirectActionHelper redirectHelper = new RedirectActionHelper(this.webInterfaceManager);
        if (this.themeManager != null && this.space.getKey() != null && (theme = this.themeManager.getSpaceTheme(this.space.getKey())) != null && theme.hasSpaceSideBar()) {
            return "spacetools";
        }
        this.redirectUrl = redirectHelper.getRedirectUrlAndUpdateCookies(BROWSE_SPACE_COOKIE, WEB_ITEM_LOCATION, this.getWebInterfaceContext());
        return "success";
    }

    public String getRedirectUrl() {
        return this.redirectUrl;
    }
}


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
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;

@RequiresAnyConfluenceAccess
public class ListPagesAction
extends AbstractSpaceAction {
    private static final String PLUGIN_KEY = "space-pages";
    public static final String LIST_PAGE_COOKIE = "confluence.list.pages.cookie";
    public static final String LIST_PAGE_LOCATION = "system.space.pages";
    private String redirectUrl;

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        GeneralUtil.setCookie("confluence.browse.space.cookie", PLUGIN_KEY);
        RedirectActionHelper helper = new RedirectActionHelper(this.webInterfaceManager);
        this.redirectUrl = helper.getRedirectUrlAndUpdateCookies(LIST_PAGE_COOKIE, LIST_PAGE_LOCATION, this.getWebInterfaceContext());
        return "success";
    }

    public String getRedirectUrl() {
        return this.redirectUrl;
    }
}


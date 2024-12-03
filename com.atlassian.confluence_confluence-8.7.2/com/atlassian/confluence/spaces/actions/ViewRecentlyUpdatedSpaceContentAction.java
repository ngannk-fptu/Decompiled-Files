/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.spaces.actions;

import com.atlassian.confluence.core.actions.RssDescriptor;
import com.atlassian.confluence.security.access.annotations.RequiresAnyConfluenceAccess;
import com.atlassian.confluence.spaces.actions.ViewSpaceSummaryAction;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;

@RequiresAnyConfluenceAccess
public class ViewRecentlyUpdatedSpaceContentAction
extends ViewSpaceSummaryAction {
    private static final String PLUGIN_KEY = "list-recently-updated";

    @Override
    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        String result = super.execute();
        GeneralUtil.setCookie("confluence.list.pages.cookie", PLUGIN_KEY);
        return result;
    }

    public RssDescriptor getRssDescriptor() {
        String title = this.getSpace().getName() + " Recent Changes";
        return new RssDescriptor("/spaces/createrssfeed.action?types=page&spaces=" + HtmlUtil.urlEncode(this.getKey()) + "&sort=modified&title=" + HtmlUtil.urlEncode(title) + "&maxResults=15", title, this.getAuthenticatedUser() != null);
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.dashboard.actions;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.security.access.annotations.RequiresLicensedOrAnonymousConfluenceAccess;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;

@RequiresLicensedOrAnonymousConfluenceAccess
public class GlobalRssFeedAction
extends ConfluenceActionSupport {
    private String rssType;

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        return "success";
    }

    public String getRssParameters() {
        StringBuilder sb = new StringBuilder();
        sb.append("spaces=&types=page&types=blogpost&types=comment&sort=modified&maxResults=100").append("&publicFeed=true&rssType=").append(this.getRssType());
        return sb.toString();
    }

    public String getRssType() {
        if (this.rssType == null) {
            return "rss";
        }
        return this.rssType;
    }

    public void setRssType(String rssType) {
        this.rssType = rssType;
    }
}


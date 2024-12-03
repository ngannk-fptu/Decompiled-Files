/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.atlassian.xwork.RequireSecurityToken
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.labels.actions;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.follow.FollowManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.atlassian.xwork.RequireSecurityToken;
import org.apache.commons.lang3.StringUtils;

public class UnFollowUserAction
extends ConfluenceActionSupport {
    private FollowManager followManager;
    private String username;
    private String mode;

    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    @RequireSecurityToken(value=true)
    public String execute() {
        ConfluenceUser user = this.getUserByName(this.username);
        this.followManager.unfollowUser(this.getAuthenticatedUser(), user);
        return StringUtils.isBlank((CharSequence)this.mode) ? "success" : this.mode;
    }

    public void setFollowManager(FollowManager followManager) {
        this.followManager = followManager;
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

    public void setMode(String mode) {
        this.mode = mode;
    }
}


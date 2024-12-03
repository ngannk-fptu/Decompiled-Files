/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.user.actions;

import com.atlassian.confluence.user.actions.AbstractUserProfileAction;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import org.apache.commons.lang3.StringUtils;

public class ViewUserActivityAction
extends AbstractUserProfileAction {
    String username;

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() {
        return "success";
    }

    @Override
    public String getUsername() {
        if ((this.username == null || StringUtils.isEmpty((CharSequence)this.username)) && this.getAuthenticatedUser() != null) {
            this.username = this.getAuthenticatedUser().getName();
        }
        return this.username;
    }

    public void setUsername(String username) {
        if (HtmlUtil.shouldUrlDecode(username)) {
            username = HtmlUtil.urlDecode(username);
        }
        this.username = username;
    }
}


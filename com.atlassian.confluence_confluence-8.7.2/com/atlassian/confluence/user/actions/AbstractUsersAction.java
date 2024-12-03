/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.user.actions;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public abstract class AbstractUsersAction
extends ConfluenceActionSupport {
    private static final Pattern URL_ENCODED_STRING_PATTERN = Pattern.compile("%[a-fA-F0-9]{2}");
    protected UserKey userKey;
    protected String username;
    @Deprecated
    public ConfluenceUser user;

    @Override
    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION);
    }

    public boolean isLicensedToAddMoreUsers() {
        return this.userAccessor.isLicensedToAddMoreUsers();
    }

    public ConfluenceUser getUser() {
        if (this.userKey == null && this.username == null) {
            return null;
        }
        if (this.user == null) {
            this.user = this.userAccessor.getExistingUserByKey(this.userKey);
        }
        if (this.user == null) {
            this.user = this.userAccessor.getUserByName(this.username);
        }
        if (this.user == null) {
            this.user = this.userAccessor.getUserByName(HtmlUtil.urlDecode(this.username));
        }
        return this.user;
    }

    public UserKey getUserKey() {
        return this.userKey;
    }

    public void setUserKey(UserKey userKey) {
        this.userKey = userKey;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        if (this.shouldUrlDecode(username)) {
            username = HtmlUtil.urlDecode(username);
        }
        this.username = StringUtils.isNotEmpty((CharSequence)username) ? username.trim() : username;
    }

    public String getRemoteUsername() {
        return this.getAuthenticatedUser() == null ? null : this.getAuthenticatedUser().getName();
    }

    private boolean shouldUrlDecode(String str) {
        return str != null && URL_ENCODED_STRING_PATTERN.matcher(str).find();
    }

    public String getUrlEncodeUsername() {
        return HtmlUtil.urlEncode(HtmlUtil.urlEncode(this.getUsername()));
    }
}


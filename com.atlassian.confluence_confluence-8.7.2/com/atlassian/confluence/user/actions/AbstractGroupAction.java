/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.Group
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.user.actions;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.security.ExternalUserManagementAware;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.user.Group;
import com.atlassian.user.User;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class AbstractGroupAction
extends ConfluenceActionSupport
implements ExternalUserManagementAware {
    protected String name;
    private Group group;
    protected List groups;

    @Override
    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION);
    }

    public @Nullable Group getGroup() {
        if (this.group == null && StringUtils.isNotEmpty((CharSequence)this.name)) {
            this.group = this.userAccessor.getGroup(this.name);
        }
        return this.group;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = HtmlUtil.urlDecode(name);
    }
}


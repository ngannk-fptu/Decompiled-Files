/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.admin.actions.tasks;

import com.atlassian.confluence.admin.AdminTasklistManager;
import com.atlassian.confluence.admin.tasks.AdminTask;
import com.atlassian.confluence.core.Beanable;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.impl.security.AdminOnly;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;

@WebSudoRequired
@AdminOnly
public class ManageAdminTaskAction
extends ConfluenceActionSupport
implements Beanable {
    private String key;
    private AdminTasklistManager adminTasklistManager;

    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    public String doIgnore() {
        String result = "error";
        AdminTask task = this.getBean();
        if (task != null && task.isIgnorable()) {
            task.setIgnored(true);
            result = "success";
        }
        return result;
    }

    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    public String doUnignore() {
        String result = "error";
        AdminTask task = this.getBean();
        if (task != null && task.isIgnorable()) {
            task.setIgnored(false);
            result = "success";
        }
        return result;
    }

    @Override
    public AdminTask getBean() {
        AdminTask task = null;
        if (this.adminTasklistManager != null) {
            for (AdminTask entry : this.adminTasklistManager.getAllTasks()) {
                if (!entry.getKey().equals(this.key)) continue;
                task = entry;
                break;
            }
        }
        return task;
    }

    @Override
    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION);
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setAdminTasklistManager(AdminTasklistManager adminTasklistManager) {
        this.adminTasklistManager = adminTasklistManager;
    }
}


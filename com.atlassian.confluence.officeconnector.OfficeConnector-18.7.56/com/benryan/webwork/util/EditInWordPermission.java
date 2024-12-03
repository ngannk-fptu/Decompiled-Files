/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.web.Condition
 *  com.atlassian.user.User
 *  com.benryan.components.OcSettingsManager
 */
package com.benryan.webwork.util;

import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.web.Condition;
import com.atlassian.user.User;
import com.benryan.components.OcSettingsManager;
import java.util.Map;

public abstract class EditInWordPermission
implements Condition {
    final PermissionManager permissionManager;
    final OcSettingsManager ocSettingsManager;

    public EditInWordPermission(@ComponentImport PermissionManager permissionManager, OcSettingsManager ocSettingsManager) {
        this.permissionManager = permissionManager;
        this.ocSettingsManager = ocSettingsManager;
    }

    public boolean shouldDisplay(Map<String, Object> map) {
        AbstractPage page = this.getCurrentPage(map);
        User user = (User)map.get("user");
        if (this.permissionManager.hasPermission(user, Permission.EDIT, (Object)page)) {
            return this.shouldDisplay(page);
        }
        return false;
    }

    protected abstract boolean shouldDisplay(AbstractPage var1);

    private AbstractPage getCurrentPage(Map<String, Object> map) {
        return (AbstractPage)map.get("page");
    }
}


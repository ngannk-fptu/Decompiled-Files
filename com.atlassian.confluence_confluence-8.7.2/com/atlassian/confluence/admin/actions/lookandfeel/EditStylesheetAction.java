/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.admin.actions.lookandfeel;

import com.atlassian.confluence.admin.actions.LookAndFeel;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.impl.security.SystemAdminOnly;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.themes.StylesheetManager;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.user.User;

@WebSudoRequired
@SystemAdminOnly
public class EditStylesheetAction
extends ConfluenceActionSupport
implements LookAndFeel {
    String style;
    private PermissionManager permissionManager;
    private StylesheetManager stylesheetManager;

    @Override
    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_SYSTEM);
    }

    @Override
    public String doDefault() throws Exception {
        this.style = this.stylesheetManager.getGlobalStylesheet();
        return super.doDefault();
    }

    public String doReset() {
        this.stylesheetManager.removeGlobalStylesheet();
        return "success";
    }

    public String execute() throws Exception {
        this.stylesheetManager.addGlobalStylesheet(this.style);
        return super.execute();
    }

    public String getStyle() {
        return this.style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    @Override
    public void setPermissionManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    public boolean isSpaceRequired() {
        return true;
    }

    public boolean isViewPermissionRequired() {
        return true;
    }

    public void setStylesheetManager(StylesheetManager stylesheetManager) {
        this.stylesheetManager = stylesheetManager;
    }
}


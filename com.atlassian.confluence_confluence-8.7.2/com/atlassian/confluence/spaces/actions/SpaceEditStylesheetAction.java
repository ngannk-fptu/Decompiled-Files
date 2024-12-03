/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.spaces.actions;

import com.atlassian.confluence.admin.actions.LookAndFeel;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.actions.AbstractSpaceAdminAction;
import com.atlassian.confluence.spaces.actions.SpaceAware;
import com.atlassian.confluence.themes.StylesheetManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.user.User;

public class SpaceEditStylesheetAction
extends AbstractSpaceAdminAction
implements LookAndFeel,
SpaceAware {
    String style;
    private PermissionManager permissionManager;
    private StylesheetManager stylesheetManager;

    @Override
    public boolean isPermitted() {
        ConfluenceUser user = this.getAuthenticatedUser();
        if (this.permissionManager.hasPermission((User)user, Permission.ADMINISTER, PermissionManager.TARGET_SYSTEM)) {
            return true;
        }
        return this.settingsManager.getGlobalSettings().isEnableSpaceStyles() && this.permissionManager.hasPermission((User)user, Permission.ADMINISTER, this.getSpace());
    }

    @Override
    public String doDefault() throws Exception {
        this.style = this.stylesheetManager.getSpaceStylesheet(this.getSpaceKey(), false);
        return super.doDefault();
    }

    public String doReset() {
        this.stylesheetManager.removeSpaceStylesheet(this.getSpaceKey());
        return "success";
    }

    public String execute() throws Exception {
        this.stylesheetManager.addSpaceStylesheet(this.getSpaceKey(), this.style);
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

    @Override
    public boolean isSpaceRequired() {
        return true;
    }

    @Override
    public boolean isViewPermissionRequired() {
        return true;
    }

    public void setStylesheetManager(StylesheetManager stylesheetManager) {
        this.stylesheetManager = stylesheetManager;
    }
}


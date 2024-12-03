/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.spaces.actions;

import com.atlassian.confluence.core.CustomPageSettings;
import com.atlassian.confluence.core.CustomPageSettingsManager;
import com.atlassian.confluence.core.FormAware;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.spaces.actions.AbstractSpaceAction;
import com.atlassian.confluence.spaces.actions.SpaceAware;
import com.atlassian.user.User;

public class CustomPageContentAction
extends AbstractSpaceAction
implements FormAware,
SpaceAware {
    private String headerText;
    private String footerText;
    private String sidebarText;
    private CustomPageSettingsManager customPageSettingsManager;

    @Override
    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, this.space);
    }

    @Override
    public String doDefault() {
        if (this.space == null) {
            return "error";
        }
        CustomPageSettings settings = this.customPageSettingsManager.retrieveSettings(this.space.getKey());
        this.headerText = settings.getHeader();
        this.footerText = settings.getFooter();
        this.sidebarText = settings.getSidebar();
        return "input";
    }

    public String execute() throws Exception {
        CustomPageSettings settings = new CustomPageSettings(this.headerText, this.footerText, this.sidebarText);
        this.customPageSettingsManager.saveSettings(this.space.getKey(), settings);
        return super.execute();
    }

    public String getFooterText() {
        return this.footerText;
    }

    public String getHeaderText() {
        return this.headerText;
    }

    public String getSidebarText() {
        return this.sidebarText;
    }

    public void setHeaderText(String headerText) {
        this.headerText = headerText;
    }

    public void setFooterText(String footerText) {
        this.footerText = footerText;
    }

    public void setSidebarText(String sidebarText) {
        this.sidebarText = sidebarText;
    }

    public void setCustomPageSettingsManager(CustomPageSettingsManager customPageSettingsManager) {
        this.customPageSettingsManager = customPageSettingsManager;
    }

    @Override
    public boolean isEditMode() {
        return true;
    }

    @Override
    public boolean isSpaceRequired() {
        return true;
    }

    @Override
    public boolean isViewPermissionRequired() {
        return true;
    }
}


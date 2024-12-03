/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.admin.actions.lookandfeel;

import com.atlassian.confluence.admin.actions.LookAndFeel;
import com.atlassian.confluence.admin.actions.lookandfeel.AbstractLookAndFeelAction;
import com.atlassian.confluence.core.CustomPageSettings;
import com.atlassian.confluence.core.CustomPageSettingsManager;
import com.atlassian.confluence.impl.security.AdminOnly;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.user.User;

@WebSudoRequired
@AdminOnly
public class CustomPageContentAction
extends AbstractLookAndFeelAction
implements LookAndFeel {
    private String headerText;
    private String footerText;
    private String sidebarText;
    private CustomPageSettingsManager customPageSettingsManager;

    @Override
    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION);
    }

    @Override
    public String doDefault() {
        CustomPageSettings settings = this.customPageSettingsManager.retrieveSettings();
        this.headerText = settings.getHeader();
        this.footerText = settings.getFooter();
        this.sidebarText = settings.getSidebar();
        return "input";
    }

    public String execute() throws Exception {
        CustomPageSettings settings = new CustomPageSettings(this.headerText, this.footerText, this.sidebarText);
        this.customPageSettingsManager.saveSettings(settings);
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
}


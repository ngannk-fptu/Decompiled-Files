/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.actions.SpaceAware
 *  com.atlassian.confluence.themes.ThemeManager
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.extra.flyingpdf.config;

import com.atlassian.confluence.extra.flyingpdf.config.GlobalConfigurePdfExportLayout;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.actions.SpaceAware;
import com.atlassian.confluence.themes.ThemeManager;
import com.atlassian.user.User;

public class ConfigurePdfExportLayoutAction
extends GlobalConfigurePdfExportLayout
implements SpaceAware {
    private Space space;
    private ThemeManager themeManager;

    public String getKey() {
        return this.getSpace().getKey();
    }

    public boolean isSpaceRequired() {
        return true;
    }

    public boolean isViewPermissionRequired() {
        return true;
    }

    @Override
    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, (Object)this.getSpace());
    }

    public void setSpace(Space space) {
        this.space = space;
        this.context = new ConfluenceBandanaContext(space);
    }

    public Space getSpace() {
        return this.space;
    }

    public ThemeManager getThemeManager() {
        return this.themeManager;
    }

    public void setThemeManager(ThemeManager themeManager) {
        this.themeManager = themeManager;
    }
}


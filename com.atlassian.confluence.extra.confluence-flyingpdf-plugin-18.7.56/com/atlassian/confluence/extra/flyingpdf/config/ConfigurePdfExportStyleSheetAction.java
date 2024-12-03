/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.actions.SpaceAware
 *  com.atlassian.confluence.themes.ThemeManager
 */
package com.atlassian.confluence.extra.flyingpdf.config;

import com.atlassian.confluence.extra.flyingpdf.config.GlobalConfigurePdfExportStyleSheet;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.actions.SpaceAware;
import com.atlassian.confluence.themes.ThemeManager;

public class ConfigurePdfExportStyleSheetAction
extends GlobalConfigurePdfExportStyleSheet
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
        return this.permissionManager.hasPermission(this.getRemoteUser(), Permission.ADMINISTER, (Object)this.getSpace());
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


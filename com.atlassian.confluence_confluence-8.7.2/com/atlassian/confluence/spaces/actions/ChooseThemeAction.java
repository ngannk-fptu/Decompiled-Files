/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.spaces.actions;

import com.atlassian.confluence.admin.actions.lookandfeel.AbstractThemeAction;
import com.atlassian.confluence.audit.AuditingContext;
import com.atlassian.confluence.plugin.descriptor.ThemeModuleDescriptor;
import com.atlassian.confluence.spaces.actions.SpaceAdministrative;
import com.atlassian.confluence.spaces.actions.SpaceAware;
import com.atlassian.confluence.themes.Theme;

public class ChooseThemeAction
extends AbstractThemeAction
implements SpaceAdministrative,
SpaceAware {
    private AuditingContext auditingContext;

    @Override
    protected void setTheme(String themeKey) {
        if (this.getSpace() == null) {
            return;
        }
        this.themeManager.setSpaceTheme(this.getSpace().getKey(), themeKey);
        this.auditingContext.executeWithoutAuditing(() -> {
            Theme theme = this.themeManager.getSpaceTheme(this.getSpace().getKey());
            String setting = theme != null && theme.getColourScheme() != null ? "theme" : "custom";
            this.colourSchemeManager.setColourSchemeSetting(this.getSpace(), setting);
        });
    }

    @Override
    protected String getCurrentThemeKey() {
        if (this.getSpace() != null) {
            return this.themeManager.getSpaceThemeKey(this.getSpace().getKey());
        }
        return null;
    }

    @Override
    public String getConfigPath(ThemeModuleDescriptor descriptor) {
        return this.layoutHelper.getConfigPath(descriptor, "space-config-path");
    }

    @Override
    public boolean isSpaceRequired() {
        return true;
    }

    @Override
    public boolean isViewPermissionRequired() {
        return true;
    }

    public void setAuditingContext(AuditingContext auditingContext) {
        this.auditingContext = auditingContext;
    }
}


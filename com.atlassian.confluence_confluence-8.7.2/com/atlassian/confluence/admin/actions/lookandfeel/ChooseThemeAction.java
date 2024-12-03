/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.user.User
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.admin.actions.lookandfeel;

import com.atlassian.confluence.admin.actions.LookAndFeel;
import com.atlassian.confluence.admin.actions.lookandfeel.AbstractThemeAction;
import com.atlassian.confluence.audit.AuditingContext;
import com.atlassian.confluence.impl.security.AdminOnly;
import com.atlassian.confluence.plugin.descriptor.ThemeModuleDescriptor;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.themes.Theme;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebSudoRequired
@AdminOnly
public class ChooseThemeAction
extends AbstractThemeAction
implements LookAndFeel {
    private static final Logger logger = LoggerFactory.getLogger(ChooseThemeAction.class);
    private AuditingContext auditingContext;

    @Override
    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION);
    }

    @Override
    public String getConfigPath(ThemeModuleDescriptor descriptor) {
        return this.layoutHelper.getConfigPath(descriptor, "global-config-path");
    }

    @Override
    protected void setTheme(String themeKey) {
        if (logger.isInfoEnabled()) {
            logger.info("Global theme change to \"" + themeKey + "\" attempted by \"" + this.getAuthenticatedUser() + "\".");
        }
        this.themeManager.setGlobalTheme(themeKey);
        this.auditingContext.executeWithoutAuditing(() -> {
            Theme theme = this.themeManager.getGlobalTheme();
            String setting = theme != null && theme.getColourScheme() != null ? "theme" : "custom";
            this.colourSchemeManager.setColourSchemeSetting(null, setting);
        });
    }

    public void setAuditingContext(AuditingContext auditingContext) {
        this.auditingContext = auditingContext;
    }

    @Override
    protected String getCurrentThemeKey() {
        return this.themeManager.getGlobalThemeKey();
    }
}


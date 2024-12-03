/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.admin.actions;

import com.atlassian.confluence.admin.actions.LookAndFeel;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.impl.security.AdminOnly;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.settings.GlobalDescription;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.spaces.actions.AbstractLogoAction;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebSudoRequired
@AdminOnly
public class ConfigureDefaultSpaceLogoAction
extends AbstractLogoAction
implements LookAndFeel {
    public static final String DEFAULT_SPACE_LOGO = "global.logo";
    private static final Logger log = LoggerFactory.getLogger(ConfigureDefaultSpaceLogoAction.class);

    @Override
    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION);
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() {
        return "success";
    }

    @Override
    public boolean isLogoDisabled() {
        return this.settingsManager.getGlobalSettings().isDisableLogo();
    }

    @Override
    public String getActionDescriminator() {
        return "defaultspace";
    }

    @Override
    public String doDisableLogo() {
        Settings settings = this.settingsManager.getGlobalSettings();
        settings.setDisableLogo(true);
        this.settingsManager.updateGlobalSettings(settings);
        return "success";
    }

    @Override
    public String doEnableLogo() {
        Settings settings = this.settingsManager.getGlobalSettings();
        settings.setDisableLogo(false);
        this.settingsManager.updateGlobalSettings(settings);
        return "success";
    }

    @Override
    protected ContentEntityObject getContentToAttachLogoTo() {
        GlobalDescription globalDescription = this.settingsManager.getGlobalDescription();
        if (globalDescription == null) {
            log.warn("Global description was null, even though it should always exist.");
            this.settingsManager.updateGlobalDescription(new GlobalDescription());
            globalDescription = this.settingsManager.getGlobalDescription();
            if (globalDescription == null) {
                log.error("Global Description null after attempt to create one");
            }
        }
        return globalDescription;
    }

    @Override
    protected String getAttachmentName() {
        return DEFAULT_SPACE_LOGO;
    }
}


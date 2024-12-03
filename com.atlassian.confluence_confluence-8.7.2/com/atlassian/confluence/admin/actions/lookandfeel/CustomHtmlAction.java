/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.Event
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.admin.actions.lookandfeel;

import com.atlassian.confluence.admin.actions.LookAndFeel;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.event.events.admin.GlobalSettingsChangedEvent;
import com.atlassian.confluence.impl.security.SystemAdminOnly;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.settings.CustomHtmlSettings;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.event.Event;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.user.User;

@WebSudoRequired
@SystemAdminOnly
public class CustomHtmlAction
extends ConfluenceActionSupport
implements LookAndFeel {
    private CustomHtmlSettings customHtmlSettings = new CustomHtmlSettings();

    @Override
    public String doDefault() throws Exception {
        this.customHtmlSettings = new CustomHtmlSettings(this.settingsManager.getGlobalSettings().getCustomHtmlSettings());
        return super.doDefault();
    }

    public String execute() throws Exception {
        Settings originalSettings = this.settingsManager.getGlobalSettings();
        Settings settings = new Settings(originalSettings);
        settings.setCustomHtmlSettings(this.customHtmlSettings);
        this.settingsManager.updateGlobalSettings(settings);
        this.makeSettingsChangedEvent(originalSettings);
        return super.execute();
    }

    public String getBeforeHeadEnd() {
        return this.customHtmlSettings.getBeforeHeadEnd();
    }

    public void setBeforeHeadEnd(String beforeHeadEnd) {
        this.customHtmlSettings.setBeforeHeadEnd(beforeHeadEnd);
    }

    public String getAfterBodyStart() {
        return this.customHtmlSettings.getAfterBodyStart();
    }

    public void setAfterBodyStart(String afterBodyStart) {
        this.customHtmlSettings.setAfterBodyStart(afterBodyStart);
    }

    public String getBeforeBodyEnd() {
        return this.customHtmlSettings.getBeforeBodyEnd();
    }

    public void setBeforeBodyEnd(String beforeBodyEnd) {
        this.customHtmlSettings.setBeforeBodyEnd(beforeBodyEnd);
    }

    private void makeSettingsChangedEvent(Settings originalSettings) {
        this.eventManager.publishEvent((Event)new GlobalSettingsChangedEvent(this, originalSettings, this.settingsManager.getGlobalSettings(), this.settingsManager.getGlobalSettings().getBaseUrl(), this.settingsManager.getGlobalSettings().getBaseUrl()));
    }

    @Override
    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_SYSTEM);
    }
}


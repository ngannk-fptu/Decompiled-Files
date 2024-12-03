/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.Event
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.opensymphony.xwork2.Action
 */
package com.atlassian.confluence.spaces.actions;

import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.event.events.space.SpaceLogoUpdateEvent;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.setup.settings.SpaceSettings;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceDescription;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.Spaced;
import com.atlassian.confluence.spaces.actions.AbstractLogoAction;
import com.atlassian.confluence.spaces.actions.SpaceAware;
import com.atlassian.confluence.themes.ThemeManager;
import com.atlassian.confluence.util.breadcrumbs.Breadcrumb;
import com.atlassian.confluence.util.breadcrumbs.BreadcrumbAware;
import com.atlassian.confluence.util.breadcrumbs.BreadcrumbGenerator;
import com.atlassian.event.Event;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.opensymphony.xwork2.Action;

public class ConfigureSpaceLogoAction
extends AbstractLogoAction
implements SpaceAware,
Spaced,
BreadcrumbAware {
    private Space space;
    private String key;
    private SpaceManager spaceManager;
    protected ThemeManager themeManager;
    private BreadcrumbGenerator breadcrumbGenerator;

    private void publishEvent(ConfluenceEvent event) {
        if (event != null) {
            this.eventManager.publishEvent((Event)event);
        }
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() {
        return "success";
    }

    @Override
    public String doDisableLogo() {
        SpaceSettings spaceSettings = this.settingsManager.getSpaceSettings(this.getSpace().getKey());
        spaceSettings.setDisableLogo(true);
        this.settingsManager.updateSpaceSettings(spaceSettings);
        this.publishEvent(new SpaceLogoUpdateEvent((Object)this, this.getSpace(), SpaceLogoUpdateEvent.SpaceLogoActions.DISABLE, null));
        return "success";
    }

    @Override
    public String doEnableLogo() {
        SpaceSettings spaceSettings = this.settingsManager.getSpaceSettings(this.getSpace().getKey());
        spaceSettings.setDisableLogo(false);
        this.settingsManager.updateSpaceSettings(spaceSettings);
        this.publishEvent(new SpaceLogoUpdateEvent((Object)this, this.getSpace(), SpaceLogoUpdateEvent.SpaceLogoActions.ENABLE, null));
        return "success";
    }

    @Override
    public String doDelete() {
        String result = super.doDelete();
        if ("success".equals(result)) {
            this.publishEvent(new SpaceLogoUpdateEvent((Object)this, this.getSpace(), SpaceLogoUpdateEvent.SpaceLogoActions.DELETE, null));
        }
        return result;
    }

    @Override
    public String doUpload() {
        String result = super.doUpload();
        if ("success".equals(result)) {
            this.publishEvent(new SpaceLogoUpdateEvent((Object)this, this.getSpace(), SpaceLogoUpdateEvent.SpaceLogoActions.UPLOAD, this.getLogo()));
        }
        return result;
    }

    @Override
    public boolean isLogoDisabled() {
        return this.settingsManager.getSpaceSettings(this.getSpace().getKey()).isDisableLogo();
    }

    @Override
    public String getActionDescriminator() {
        return "space";
    }

    @Override
    public void setSpace(Space space) {
        this.space = space;
    }

    @Override
    public Space getSpace() {
        if (this.space == null) {
            this.space = this.spaceManager.getSpace(this.key);
        }
        return this.space;
    }

    @Override
    public boolean isSpaceRequired() {
        return true;
    }

    @Override
    public boolean isViewPermissionRequired() {
        return false;
    }

    @Override
    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, this.getSpace());
    }

    @Override
    protected SpaceDescription getContentToAttachLogoTo() {
        return this.getSpace().getDescription();
    }

    @Override
    protected String getAttachmentName() {
        return this.getSpace().getKey();
    }

    public void setKey(String key) {
        this.key = key.trim();
    }

    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }

    public void setThemeManager(ThemeManager themeManager) {
        this.themeManager = themeManager;
    }

    public void setBreadcrumbGenerator(BreadcrumbGenerator breadcrumbGenerator) {
        this.breadcrumbGenerator = breadcrumbGenerator;
    }

    public ThemeManager getThemeManager() {
        return this.themeManager;
    }

    @Override
    public Breadcrumb getBreadcrumb() {
        return this.breadcrumbGenerator.getSpaceAdminBreadcrumb((Action)this, this.getSpace());
    }
}


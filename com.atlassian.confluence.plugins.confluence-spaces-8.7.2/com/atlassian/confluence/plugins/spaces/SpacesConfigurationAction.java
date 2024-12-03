/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.core.FormAware
 *  com.atlassian.confluence.event.events.admin.GlobalSettingsChangedEvent
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.setup.settings.Settings
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.spaces.SpaceType
 *  com.atlassian.confluence.spaces.SpacesQuery
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.core.util.PairType
 *  com.atlassian.event.Event
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.spaces;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.core.FormAware;
import com.atlassian.confluence.event.events.admin.GlobalSettingsChangedEvent;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.SpaceType;
import com.atlassian.confluence.spaces.SpacesQuery;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.core.util.PairType;
import com.atlassian.event.Event;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpacesConfigurationAction
extends ConfluenceActionSupport
implements FormAware {
    private static final Logger log = LoggerFactory.getLogger(SpacesConfigurationAction.class);
    private SpaceManager spaceManager;
    private boolean allowThreadedComments;
    private boolean allowRemoteApi;
    private boolean enableQuickNav;
    private boolean enableOpenSearch;
    private int maxSimultaneousQuickNavRequests;
    private int draftSaveIntervalMinutes;
    private int draftSaveIntervalSeconds;
    private static final int DRAFT_SAVE_MIN = 10000;
    private String siteHomePage;
    private boolean editMode = true;

    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION);
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String doView() throws Exception {
        this.editMode = false;
        return this.doDefault();
    }

    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    public String doDefault() throws Exception {
        Settings globalSettings = this.getGlobalSettings();
        this.allowRemoteApi = globalSettings.isAllowRemoteApi();
        this.allowThreadedComments = globalSettings.isAllowThreadedComments();
        this.enableQuickNav = globalSettings.isEnableQuickNav();
        this.enableOpenSearch = globalSettings.isEnableOpenSearch();
        this.maxSimultaneousQuickNavRequests = globalSettings.getMaxSimultaneousQuickNavRequests();
        this.siteHomePage = globalSettings.getSiteHomePage();
        int draftSaveInterval = globalSettings.getDraftSaveInterval();
        this.draftSaveIntervalMinutes = draftSaveInterval / 1000 / 60;
        this.draftSaveIntervalSeconds = draftSaveInterval / 1000 - this.draftSaveIntervalMinutes * 60;
        return super.doDefault();
    }

    private void validation() {
        if (this.getDraftSaveInterval() < 10000) {
            Object[] args = new String[]{GeneralUtil.getNiceDuration((int)0, (int)10)};
            this.addFieldError("draftSaveIntervalSeconds", "error.minimum.draft.interval", args);
        }
        if (this.isEnableQuickNav() && this.getMaxSimultaneousQuickNavRequests() <= 0) {
            this.addFieldError("maxSimultaneousQuickNavRequests", this.getText("quick.nav.validation.error.max.requests"));
        }
        if (!"dashboard".equals(this.siteHomePage) && !StringUtils.isBlank((CharSequence)this.siteHomePage)) {
            Space siteHomePageParentSpace = this.spaceManager.getSpace(this.siteHomePage);
            if (siteHomePageParentSpace == null) {
                this.addActionError("site.homepage.invalid.space.error", new Object[]{this.siteHomePage});
            } else {
                boolean isGlobalAnonymousAccessEnabled = this.spacePermissionManager.hasPermission("USECONFLUENCE", null, null);
                String defaultUsersGroup = this.settingsManager.getGlobalSettings().getDefaultUsersGroup();
                if (isGlobalAnonymousAccessEnabled) {
                    if (!this.spacePermissionManager.hasPermission("VIEWSPACE", siteHomePageParentSpace, null) && !this.spacePermissionManager.groupHasPermission("VIEWSPACE", siteHomePageParentSpace, defaultUsersGroup)) {
                        this.addActionError("site.homepage.invalid.permissions.error", new Object[]{siteHomePageParentSpace.getName(), defaultUsersGroup});
                    }
                } else if (!this.spacePermissionManager.groupHasPermission("VIEWSPACE", siteHomePageParentSpace, defaultUsersGroup)) {
                    this.addActionError("site.homepage.invalid.permissions.error", new Object[]{siteHomePageParentSpace.getName(), defaultUsersGroup});
                }
            }
        }
    }

    public String execute() throws Exception {
        this.validation();
        if (this.hasErrors()) {
            return "error";
        }
        Settings originalSettings = this.settingsManager.getGlobalSettings();
        String oldDomainName = this.settingsManager.getGlobalSettings().getBaseUrl();
        this.saveSetupOptions();
        GlobalSettingsChangedEvent event = new GlobalSettingsChangedEvent((Object)this, originalSettings, this.settingsManager.getGlobalSettings(), oldDomainName, this.settingsManager.getGlobalSettings().getBaseUrl());
        this.eventManager.publishEvent((Event)event);
        return "success";
    }

    private void saveSetupOptions() {
        Settings settings = new Settings(this.getGlobalSettings());
        settings.setAllowThreadedComments(this.allowThreadedComments);
        settings.setEnableQuickNav(this.enableQuickNav);
        settings.setEnableOpenSearch(this.enableOpenSearch);
        settings.setMaxSimultaneousQuickNavRequests(this.maxSimultaneousQuickNavRequests);
        settings.setDraftSaveInterval(this.getDraftSaveInterval());
        settings.setSiteHomePage("dashboard".equals(this.siteHomePage) ? null : this.siteHomePage);
        if (this.isSystemAdmin()) {
            settings.setAllowRemoteApi(this.allowRemoteApi);
        }
        this.settingsManager.updateGlobalSettings(settings);
    }

    public boolean isSystemAdmin() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_SYSTEM);
    }

    public SpaceManager getSpaceManager() {
        return this.spaceManager;
    }

    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }

    public List<PairType> getSiteHomePages() {
        if (!this.editMode) {
            if (StringUtils.isBlank((CharSequence)this.siteHomePage)) {
                return Collections.singletonList(new PairType((Serializable)((Object)this.getText("dashboard")), (Serializable)((Object)"dashboard")));
            }
            return Collections.singletonList(new PairType((Serializable)((Object)this.siteHomePage), (Serializable)((Object)this.siteHomePage)));
        }
        List allSpaces = this.spaceManager.getAllSpaces(SpacesQuery.newQuery().withSpaceType(SpaceType.GLOBAL).build());
        ArrayList<PairType> siteHomePages = new ArrayList<PairType>(allSpaces.size() + 1);
        siteHomePages.add(new PairType((Serializable)((Object)this.getText("dashboard")), (Serializable)((Object)"dashboard")));
        for (Space space : allSpaces) {
            siteHomePages.add(new PairType((Serializable)((Object)space.getKey()), (Serializable)((Object)space.getKey())));
        }
        return siteHomePages;
    }

    public String getSiteHomePage() {
        return this.siteHomePage;
    }

    public void setSiteHomePage(String siteHomePage) {
        this.siteHomePage = siteHomePage;
    }

    public int getDraftSaveIntervalMinutes() {
        return this.draftSaveIntervalMinutes;
    }

    public void setDraftSaveIntervalMinutes(int draftSaveIntervalMinutes) {
        this.draftSaveIntervalMinutes = draftSaveIntervalMinutes;
    }

    public int getDraftSaveIntervalSeconds() {
        return this.draftSaveIntervalSeconds;
    }

    public void setDraftSaveIntervalSeconds(int draftSaveIntervalSeconds) {
        this.draftSaveIntervalSeconds = draftSaveIntervalSeconds;
    }

    public String getCurrentTime(String dateFormatPattern) {
        return new SimpleDateFormat(dateFormatPattern).format(new Date());
    }

    public String getNiceDraftSaveInterval() {
        return GeneralUtil.getNiceDuration((int)this.draftSaveIntervalMinutes, (int)this.draftSaveIntervalSeconds);
    }

    public boolean isAllowThreadedComments() {
        return this.allowThreadedComments;
    }

    public void setAllowThreadedComments(boolean allowThreadedComments) {
        this.allowThreadedComments = allowThreadedComments;
    }

    public boolean isAllowRemoteApi() {
        return this.allowRemoteApi;
    }

    public void setAllowRemoteApi(boolean allowRemoteApi) {
        this.allowRemoteApi = allowRemoteApi;
    }

    public int getDraftSaveInterval() {
        return (this.draftSaveIntervalMinutes * 60 + this.draftSaveIntervalSeconds) * 1000;
    }

    public boolean isEnableQuickNav() {
        return this.enableQuickNav;
    }

    public void setEnableQuickNav(boolean enableQuickNav) {
        this.enableQuickNav = enableQuickNav;
    }

    public int getMaxSimultaneousQuickNavRequests() {
        return this.maxSimultaneousQuickNavRequests;
    }

    public void setMaxSimultaneousQuickNavRequests(int maxSimultaneousQuickNavRequests) {
        this.maxSimultaneousQuickNavRequests = maxSimultaneousQuickNavRequests;
    }

    public boolean isEnableOpenSearch() {
        return this.enableOpenSearch;
    }

    public void setEnableOpenSearch(boolean enableOpenSearch) {
        this.enableOpenSearch = enableOpenSearch;
    }

    public boolean isEditMode() {
        return this.editMode;
    }
}


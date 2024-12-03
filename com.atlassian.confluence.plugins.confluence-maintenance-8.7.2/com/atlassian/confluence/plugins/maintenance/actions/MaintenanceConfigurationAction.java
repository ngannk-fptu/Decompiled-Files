/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.accessmode.AccessMode
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.event.events.analytics.MaintenanceBannerEvent
 *  com.atlassian.confluence.event.events.cluster.ClusterMaintenanceBannerEvent
 *  com.atlassian.confluence.license.LicenseService
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.maintenance.actions;

import com.atlassian.confluence.api.model.accessmode.AccessMode;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.event.events.analytics.MaintenanceBannerEvent;
import com.atlassian.confluence.event.events.cluster.ClusterMaintenanceBannerEvent;
import com.atlassian.confluence.license.LicenseService;
import com.atlassian.confluence.plugins.maintenance.model.Addon;
import com.atlassian.confluence.plugins.maintenance.model.MaintenanceInfo;
import com.atlassian.confluence.plugins.maintenance.service.MaintenanceService;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MaintenanceConfigurationAction
extends ConfluenceActionSupport {
    private boolean isReadOnlyModeEnabled;
    private boolean bannerMessageOn;
    private String bannerMessage;
    private boolean editMode = true;
    private List<Addon> addons;
    @ComponentImport
    private EventPublisher eventPublisher;
    private MaintenanceService maintenanceService;
    @ComponentImport
    private LicenseService licenseService;
    private static final Logger logger = LoggerFactory.getLogger(MaintenanceConfigurationAction.class);

    public boolean isPermitted() {
        return this.licenseService.isLicensedForDataCenterOrExempt() && this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION);
    }

    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    public String doDefault() throws Exception {
        this.isReadOnlyModeEnabled = this.accessModeService.isReadOnlyAccessModeEnabled();
        this.addons = this.maintenanceService.getUserInstalledAddons();
        MaintenanceInfo maintenanceInfo = this.maintenanceService.getMaintenanceInfo();
        this.bannerMessageOn = maintenanceInfo.isBannerMessageOn();
        this.bannerMessage = maintenanceInfo.getBannerMessage();
        return super.doDefault();
    }

    public String execute() {
        try {
            MaintenanceInfo lastMaintenanceInfo;
            boolean isBannerFlagChanged;
            boolean isReadOnlyModeChanged;
            boolean bl = isReadOnlyModeChanged = this.isReadOnlyModeEnabled != this.accessModeService.isReadOnlyAccessModeEnabled();
            if (this.isReadOnlyModeEnabled) {
                this.bannerMessageOn = true;
            }
            boolean bl2 = isBannerFlagChanged = this.bannerMessageOn != (lastMaintenanceInfo = this.maintenanceService.getMaintenanceInfo()).isBannerMessageOn();
            if (isReadOnlyModeChanged) {
                this.accessModeService.updateAccessMode(this.isReadOnlyModeEnabled ? AccessMode.READ_ONLY : AccessMode.READ_WRITE);
                logger.info("Read-only mode has been " + (this.isReadOnlyModeEnabled ? "enabled." : "disabled."));
            }
            MaintenanceInfo.Builder maintenanceInfoBuilder = new MaintenanceInfo.Builder();
            maintenanceInfoBuilder.bannerMessageEnabled(this.bannerMessageOn).bannerMessage(this.bannerMessage);
            this.maintenanceService.updateMaintenanceInfo(maintenanceInfoBuilder.build());
            if (isBannerFlagChanged || !StringUtils.equals((CharSequence)this.bannerMessage, (CharSequence)lastMaintenanceInfo.getBannerMessage())) {
                logger.info("The site-wide maintenance banner has been " + (this.bannerMessageOn ? "enabled." : "disabled."));
                MaintenanceInfo updatedMaintenanceInfo = this.maintenanceService.getMaintenanceInfo();
                this.eventPublisher.publish((Object)new ClusterMaintenanceBannerEvent((Object)this, updatedMaintenanceInfo.isBannerMessageOn(), updatedMaintenanceInfo.getBannerMessage(), lastMaintenanceInfo.isBannerMessageOn(), lastMaintenanceInfo.getBannerMessage()));
            }
            if (!isReadOnlyModeChanged || !isBannerFlagChanged) {
                boolean enabled = !this.isReadOnlyModeEnabled && this.bannerMessageOn;
                this.eventPublisher.publish((Object)new MaintenanceBannerEvent(enabled));
            }
        }
        catch (ServiceException e) {
            this.addActionError(this.getText("confluence.maintenance.access.mode.update.error"));
            logger.error("Error occurred while updating the access mode and banner {}", (Throwable)e);
        }
        if (this.getActionErrors().size() != 0) {
            return "error";
        }
        return "success";
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String doView() throws Exception {
        this.editMode = false;
        return this.doDefault();
    }

    public boolean isReadOnlyModeEnabled() {
        return this.isReadOnlyModeEnabled;
    }

    public void setReadOnlyModeEnabled(boolean readOnlyModeEnabled) {
        this.isReadOnlyModeEnabled = readOnlyModeEnabled;
    }

    public boolean isEditMode() {
        return this.editMode;
    }

    public List<Addon> getAddons() {
        return this.addons;
    }

    public boolean isBannerMessageOn() {
        return this.bannerMessageOn;
    }

    public void setBannerMessageOn(boolean bannerMessageOn) {
        this.bannerMessageOn = bannerMessageOn;
    }

    public void setMaintenanceService(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    public String getBannerMessage() {
        return this.bannerMessage;
    }

    public void setBannerMessage(String bannerMessage) {
        this.bannerMessage = bannerMessage;
    }

    public void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void setLicenseService(LicenseService licenseService) {
        this.licenseService = licenseService;
    }
}


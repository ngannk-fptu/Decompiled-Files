/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.security.XsrfProtectionExcluded
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.upm.test.rest.resources;

import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.atlassian.upm.UpmSys;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.notification.PluginLicenseNotificationChecker;
import com.atlassian.upm.notification.PluginRequestNotificationChecker;
import com.atlassian.upm.notification.PluginUpdateChecker;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path(value="/test/notifications")
public class NotificationRefreshResource {
    private final PermissionEnforcer permissionEnforcer;
    private final PluginLicenseNotificationChecker licenseChecker;
    private final PluginRequestNotificationChecker requestChecker;
    private final PluginUpdateChecker updateChecker;

    public NotificationRefreshResource(PermissionEnforcer permissionEnforcer, PluginLicenseNotificationChecker licenseChecker, PluginRequestNotificationChecker requestChecker, PluginUpdateChecker updateChecker) {
        this.permissionEnforcer = permissionEnforcer;
        this.licenseChecker = licenseChecker;
        this.requestChecker = requestChecker;
        this.updateChecker = updateChecker;
    }

    @Path(value="/refresh")
    @POST
    @XsrfProtectionExcluded
    public Response refreshNotifications() {
        this.permissionEnforcer.enforceSystemAdmin();
        if (!UpmSys.isUpmDebugModeEnabled()) {
            return Response.status((Response.Status)Response.Status.PRECONDITION_FAILED).build();
        }
        this.licenseChecker.updateLocalPluginLicenseNotifications();
        this.requestChecker.updatePluginRequestNotifications();
        this.updateChecker.checkForUpdates(PluginUpdateChecker.UpdateCheckOptions.options().updateNotifications(true));
        return Response.ok().build();
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.permission;

import com.atlassian.upm.SysPersisted;
import com.atlassian.upm.UpmHostApplicationInformation;
import com.atlassian.upm.UpmSettings;
import com.atlassian.upm.UpmSys;
import com.atlassian.upm.core.permission.Permission;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.pac.PacClient;
import com.atlassian.upm.permission.UpmVisibility;
import java.util.Objects;

public class UpmVisibilityImpl
implements UpmVisibility {
    private final PermissionEnforcer permissionEnforcer;
    private final SysPersisted sysPersisted;
    private final PacClient pacClient;
    private final UpmHostApplicationInformation hostApplicationInformation;

    public UpmVisibilityImpl(PermissionEnforcer permissionEnforcer, SysPersisted sysPersisted, PacClient pacClient, UpmHostApplicationInformation hostApplicationInformation) {
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
        this.sysPersisted = Objects.requireNonNull(sysPersisted, "sysPersisted");
        this.pacClient = Objects.requireNonNull(pacClient, "pacClient");
        this.hostApplicationInformation = Objects.requireNonNull(hostApplicationInformation, "hostApplicationInformation");
    }

    @Override
    public boolean isManageExistingVisible() {
        return this.permissionEnforcer.hasPermission(Permission.GET_INSTALLED_PLUGINS);
    }

    @Override
    public boolean isInstallVisible() {
        return this.permissionEnforcer.hasPermission(Permission.GET_AVAILABLE_PLUGINS) && !this.sysPersisted.is(UpmSettings.PAC_DISABLED) && this.isKnownOrDevelopmentVersion();
    }

    @Override
    public boolean isOsgiVisible() {
        return this.permissionEnforcer.hasPermission(Permission.GET_OSGI_STATE);
    }

    @Override
    public boolean isPurchasedAddonsVisible() {
        return UpmSys.isPurchasedAddonsEnabled() && this.permissionEnforcer.hasPermission(Permission.GET_PURCHASED_PLUGINS);
    }

    @Override
    public boolean isNotificationDropdownVisible() {
        return this.permissionEnforcer.hasPermission(Permission.GET_NOTIFICATIONS);
    }

    private boolean isKnownOrDevelopmentVersion() {
        return this.pacClient.isUnknownProductVersion().getOrElse(false) == false || this.hostApplicationInformation.isDevelopmentProductVersion();
    }
}


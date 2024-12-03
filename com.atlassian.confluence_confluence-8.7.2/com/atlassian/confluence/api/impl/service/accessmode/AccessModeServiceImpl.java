/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ConfigurationException
 *  com.atlassian.confluence.api.model.accessmode.AccessMode
 *  com.atlassian.confluence.api.service.accessmode.AccessModeService
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.user.User
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.api.impl.service.accessmode;

import com.atlassian.config.ConfigurationException;
import com.atlassian.confluence.api.model.accessmode.AccessMode;
import com.atlassian.confluence.api.service.accessmode.AccessModeService;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.event.events.analytics.MaintenanceReadOnlyEvent;
import com.atlassian.confluence.event.events.cluster.ClusterAccessModeEvent;
import com.atlassian.confluence.internal.accessmode.AccessModeManager;
import com.atlassian.confluence.internal.accessmode.ThreadLocalReadOnlyAccessCacheInternal;
import com.atlassian.confluence.license.LicenseService;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.user.User;
import java.util.concurrent.Callable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccessModeServiceImpl
implements AccessModeService {
    private final AccessModeManager accessModeManager;
    private final PermissionManager permissionManager;
    private final EventPublisher eventPublisher;
    private final LicenseService licenseService;

    @Autowired
    public AccessModeServiceImpl(AccessModeManager accessModeManager, PermissionManager permissionManager, EventPublisher eventPublisher, LicenseService licenseService) {
        this.accessModeManager = accessModeManager;
        this.permissionManager = permissionManager;
        this.eventPublisher = eventPublisher;
        this.licenseService = licenseService;
    }

    public AccessMode getAccessMode() {
        if (this.licenseService.isLicensedForDataCenterOrExempt()) {
            return this.accessModeManager.getAccessMode();
        }
        return AccessMode.READ_WRITE;
    }

    public void updateAccessMode(AccessMode accessMode) throws ServiceException {
        if (!this.licenseService.isLicensedForDataCenterOrExempt()) {
            throw new ServiceException("The instance is not a Data Center");
        }
        AccessMode currentAccessMode = this.accessModeManager.getAccessMode();
        if (accessMode.equals((Object)currentAccessMode)) {
            return;
        }
        if (this.isAdmin()) {
            try {
                this.accessModeManager.updateAccessMode(accessMode);
                this.eventPublisher.publish((Object)new ClusterAccessModeEvent(this, accessMode));
                this.eventPublisher.publish((Object)new MaintenanceReadOnlyEvent(accessMode == AccessMode.READ_ONLY));
            }
            catch (ConfigurationException e) {
                throw new ServiceException("Cannot update the access mode", e.getCause());
            }
        }
    }

    public boolean isReadOnlyAccessModeEnabled() {
        return this.licenseService.isLicensedForDataCenterOrExempt() && this.accessModeManager.isReadOnlyAccessModeEnabled();
    }

    public boolean shouldEnforceReadOnlyAccess() {
        return this.licenseService.isLicensedForDataCenterOrExempt() && this.accessModeManager.shouldEnforceReadOnlyAccess();
    }

    public <T> T withReadOnlyAccessExemption(Callable<T> callable) throws ServiceException {
        if (ThreadLocalReadOnlyAccessCacheInternal.hasReadOnlyAccessExemption()) {
            try {
                return callable.call();
            }
            catch (Exception e) {
                throw new ServiceException("Cannot run the callable", (Throwable)e);
            }
        }
        ThreadLocalReadOnlyAccessCacheInternal.enableReadOnlyAccessExemption();
        try {
            T e = callable.call();
            return e;
        }
        catch (Exception e) {
            throw new ServiceException("Cannot run the callable", (Throwable)e);
        }
        finally {
            ThreadLocalReadOnlyAccessCacheInternal.disableReadOnlyAccessExemption();
        }
    }

    private boolean isAdmin() {
        return this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION);
    }
}


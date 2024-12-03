/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.upgrade.AbstractUpgradeTask
 *  com.atlassian.confluence.upgrade.DatabaseUpgradeTask
 */
package com.atlassian.confluence.upgrade.upgradetask;

import com.atlassian.confluence.security.denormalisedpermissions.DenormalisedPermissionServiceState;
import com.atlassian.confluence.security.denormalisedpermissions.impl.manage.DenormalisedPermissionStateLogService;
import com.atlassian.confluence.security.denormalisedpermissions.impl.manage.domain.DenormalisedServiceStateRecord;
import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.DenormalisedPermissionsDdlExecutor;
import com.atlassian.confluence.upgrade.AbstractUpgradeTask;
import com.atlassian.confluence.upgrade.DatabaseUpgradeTask;

public class DenormalisedPermissionsTriggersUpgradeTask
extends AbstractUpgradeTask
implements DatabaseUpgradeTask {
    private static final String BUILD_NUMBER = "8901";
    final DenormalisedPermissionStateLogService denormalisedPermissionStateLogService;
    final DenormalisedPermissionsDdlExecutor ddlExecutor;

    public DenormalisedPermissionsTriggersUpgradeTask(DenormalisedPermissionsDdlExecutor denormalisedPermissionsDdlExecutor, DenormalisedPermissionStateLogService denormalisedPermissionStateLogService) {
        this.denormalisedPermissionStateLogService = denormalisedPermissionStateLogService;
        this.ddlExecutor = denormalisedPermissionsDdlExecutor;
    }

    public boolean runOnSpaceImport() {
        return false;
    }

    public boolean breaksBackwardCompatibility() {
        return false;
    }

    public String getBuildNumber() {
        return BUILD_NUMBER;
    }

    public void doUpgrade() throws Exception {
        DenormalisedPermissionServiceState spaceServiceState = this.denormalisedPermissionStateLogService.getServiceState(DenormalisedServiceStateRecord.ServiceType.SPACE);
        DenormalisedPermissionServiceState contentServiceState = this.denormalisedPermissionStateLogService.getServiceState(DenormalisedServiceStateRecord.ServiceType.CONTENT);
        boolean enableSpaceService = this.isServiceEnabled(spaceServiceState);
        boolean enableContentService = this.isServiceEnabled(contentServiceState);
        this.ddlExecutor.dropSpaceDatabaseObjects();
        this.ddlExecutor.createSpaceDatabaseObjects(enableSpaceService);
        this.ddlExecutor.dropContentDatabaseObjects();
        this.ddlExecutor.createContentDatabaseObjects(enableContentService);
    }

    private boolean isServiceEnabled(DenormalisedPermissionServiceState serviceState) {
        return serviceState != DenormalisedPermissionServiceState.DISABLED && serviceState != DenormalisedPermissionServiceState.SHUTTING_DOWN;
    }
}


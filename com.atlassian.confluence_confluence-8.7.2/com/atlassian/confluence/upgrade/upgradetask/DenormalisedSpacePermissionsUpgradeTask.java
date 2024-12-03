/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.upgrade.AbstractUpgradeTask
 *  com.atlassian.confluence.upgrade.DatabaseUpgradeTask
 */
package com.atlassian.confluence.upgrade.upgradetask;

import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.DenormalisedPermissionsDdlExecutor;
import com.atlassian.confluence.upgrade.AbstractUpgradeTask;
import com.atlassian.confluence.upgrade.DatabaseUpgradeTask;
import com.atlassian.confluence.upgrade.ddl.DdlExecutor;

public class DenormalisedSpacePermissionsUpgradeTask
extends AbstractUpgradeTask
implements DatabaseUpgradeTask {
    private static final String BUILD_NUMBER = "8507";
    private final DdlExecutor ddlExecutor;
    private final DenormalisedPermissionsDdlExecutor denormalisedPermissionsDdlExecutor;

    public DenormalisedSpacePermissionsUpgradeTask(DdlExecutor ddlExecutor, DenormalisedPermissionsDdlExecutor denormalisedPermissionsDdlExecutor) {
        this.ddlExecutor = ddlExecutor;
        this.denormalisedPermissionsDdlExecutor = denormalisedPermissionsDdlExecutor;
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
        this.denormalisedPermissionsDdlExecutor.dropSpaceDatabaseObjects();
        this.denormalisedPermissionsDdlExecutor.createSpaceDatabaseObjects(false);
        this.denormalisedPermissionsDdlExecutor.dropAdditionalSpaceIndexes();
        this.ddlExecutor.executeDdl(this.denormalisedPermissionsDdlExecutor.getAdditionalSpaceIndexes(this.ddlExecutor));
    }
}


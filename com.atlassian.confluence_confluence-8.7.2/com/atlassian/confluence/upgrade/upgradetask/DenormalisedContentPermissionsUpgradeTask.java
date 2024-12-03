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

public class DenormalisedContentPermissionsUpgradeTask
extends AbstractUpgradeTask
implements DatabaseUpgradeTask {
    private static final String BUILD_NUMBER = "8701";
    private final DdlExecutor ddlExecutor;
    private final DenormalisedPermissionsDdlExecutor denormalisedPermissionsDdlExecutor;

    public DenormalisedContentPermissionsUpgradeTask(DdlExecutor ddlExecutor, DenormalisedPermissionsDdlExecutor denormalisedPermissionsDdlExecutor) {
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
        this.denormalisedPermissionsDdlExecutor.dropContentDatabaseObjects();
        this.denormalisedPermissionsDdlExecutor.createContentDatabaseObjects(false);
        this.denormalisedPermissionsDdlExecutor.dropAdditionalContentIndexes();
        this.ddlExecutor.executeDdl(this.denormalisedPermissionsDdlExecutor.getAdditionalContentIndexes(this.ddlExecutor));
    }
}


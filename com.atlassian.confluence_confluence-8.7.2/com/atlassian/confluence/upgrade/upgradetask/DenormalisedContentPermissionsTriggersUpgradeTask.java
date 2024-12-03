/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.upgrade.AbstractUpgradeTask
 *  com.atlassian.confluence.upgrade.DatabaseUpgradeTask
 *  org.springframework.transaction.PlatformTransactionManager
 *  org.springframework.transaction.TransactionDefinition
 *  org.springframework.transaction.interceptor.DefaultTransactionAttribute
 *  org.springframework.transaction.support.TransactionTemplate
 */
package com.atlassian.confluence.upgrade.upgradetask;

import com.atlassian.confluence.security.denormalisedpermissions.DenormalisedPermissionServiceState;
import com.atlassian.confluence.security.denormalisedpermissions.StateChangeInformation;
import com.atlassian.confluence.security.denormalisedpermissions.impl.manage.DenormalisedPermissionStateLogService;
import com.atlassian.confluence.security.denormalisedpermissions.impl.manage.domain.DenormalisedServiceStateRecord;
import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.DenormalisedPermissionsDdlExecutor;
import com.atlassian.confluence.upgrade.AbstractUpgradeTask;
import com.atlassian.confluence.upgrade.DatabaseUpgradeTask;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.support.TransactionTemplate;

public class DenormalisedContentPermissionsTriggersUpgradeTask
extends AbstractUpgradeTask
implements DatabaseUpgradeTask {
    private static final String BUILD_NUMBER = "8703";
    private final PlatformTransactionManager txManager;
    private final DenormalisedPermissionStateLogService denormalisedPermissionStateLogService;
    private final DenormalisedPermissionsDdlExecutor denormalisedPermissionsDdlExecutor;

    public DenormalisedContentPermissionsTriggersUpgradeTask(PlatformTransactionManager txManager, DenormalisedPermissionStateLogService denormalisedPermissionStateLogService, DenormalisedPermissionsDdlExecutor denormalisedPermissionsDdlExecutor) {
        this.txManager = txManager;
        this.denormalisedPermissionStateLogService = denormalisedPermissionStateLogService;
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
        DenormalisedPermissionServiceState contentServiceState = this.denormalisedPermissionStateLogService.getServiceState(DenormalisedServiceStateRecord.ServiceType.CONTENT);
        boolean enableService = contentServiceState == DenormalisedPermissionServiceState.SERVICE_READY || contentServiceState == DenormalisedPermissionServiceState.INITIALISING;
        this.denormalisedPermissionsDdlExecutor.dropContentDatabaseObjects();
        this.denormalisedPermissionsDdlExecutor.createContentDatabaseObjects(enableService);
        if (contentServiceState == DenormalisedPermissionServiceState.SERVICE_READY) {
            DefaultTransactionAttribute transactionDefinition = new DefaultTransactionAttribute(0);
            new TransactionTemplate(this.txManager, (TransactionDefinition)transactionDefinition).execute(status -> {
                this.denormalisedPermissionStateLogService.changeState(DenormalisedServiceStateRecord.ServiceType.CONTENT, DenormalisedPermissionServiceState.INITIALISING, StateChangeInformation.MessageLevel.INFO, DenormalisedServiceStateRecord.ServiceType.CONTENT.getDisplayName() + " service will be re-initialised because of upgrade task. Initialisation started. Previous state was: " + contentServiceState, null);
                return null;
            });
        }
    }
}


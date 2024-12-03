/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.security.XsrfProtectionExcluded
 *  com.atlassian.config.lifecycle.LifecycleManager
 *  com.atlassian.confluence.upgrade.UpgradeFinalizationManager
 *  com.atlassian.confluence.upgrade.UpgradeManager
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.apache.struts2.ServletActionContext
 */
package com.atlassian.confluence.setup.actions;

import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.atlassian.config.lifecycle.LifecycleManager;
import com.atlassian.confluence.core.ListBuilder;
import com.atlassian.confluence.impl.cluster.ClusterConfigurationHelperInternal;
import com.atlassian.confluence.impl.security.SystemAdminOnly;
import com.atlassian.confluence.schedule.managers.ScheduledJobManager;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.setup.SetupCompleteEvent;
import com.atlassian.confluence.setup.actions.AbstractSetupAction;
import com.atlassian.confluence.setup.settings.GlobalDescription;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.SpaceType;
import com.atlassian.confluence.spaces.SpacesQuery;
import com.atlassian.confluence.upgrade.UpgradeFinalizationManager;
import com.atlassian.confluence.upgrade.UpgradeGate;
import com.atlassian.confluence.upgrade.UpgradeManager;
import com.atlassian.confluence.upgrade.upgradetask.DisableBackupJobUpgradeTask;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.util.ArrayList;
import java.util.List;
import org.apache.struts2.ServletActionContext;

@WebSudoRequired
@SystemAdminOnly
public class FinishSetupAction
extends AbstractSetupAction {
    private String installationMethod;
    private LifecycleManager lifecycleManager;
    private UpgradeGate upgradeGate;
    private EventPublisher eventPublisher;
    private UpgradeManager upgradeManager;
    private UpgradeFinalizationManager upgradeFinalizationManager;
    private ClusterConfigurationHelperInternal clusterConfigurationHelper;
    private ScheduledJobManager scheduledJobManager;

    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    @XsrfProtectionExcluded
    public String execute() throws Exception {
        this.getSetupPersister().progessSetupStep();
        if (this.getSetupPersister().isDemonstrationContentInstalled()) {
            this.fixOwnershipAndPermissionsOfDemoSpace();
        }
        this.spacePermissionManager.flushCaches();
        this.installationMethod = this.getSetupPersister().getSetupType();
        Settings globalSettings = this.settingsManager.getGlobalSettings();
        globalSettings.setGlobalDefaultLocale(this.getLocale().toString());
        this.settingsManager.updateGlobalSettings(globalSettings);
        if (this.settingsManager.getGlobalDescription() == null) {
            this.settingsManager.updateGlobalDescription(new GlobalDescription());
        }
        this.bootstrapConfigurer().publishConfiguration();
        if (this.upgradeGate == null) {
            this.upgradeGate = (UpgradeGate)ContainerManager.getComponent((String)"upgradeGate");
        }
        this.upgradeGate.setUpgradeRequired(false);
        this.upgradeManager.setDatabaseBuildNumber();
        this.upgradeFinalizationManager.markAsFullyFinalized(true);
        this.getSetupPersister().finishSetup();
        this.clusterConfigurationHelper.saveSetupConfigIntoSharedHome();
        this.eventPublisher.publish((Object)new SetupCompleteEvent(this));
        this.lifecycleManager.startUp(ServletActionContext.getServletContext());
        DisableBackupJobUpgradeTask.disableAutomaticBackup(this.scheduledJobManager, this.clusterConfigurationHelper);
        return "success";
    }

    private void fixOwnershipAndPermissionsOfDemoSpace() {
        SpaceManager spaceManager = (SpaceManager)ContainerManager.getComponent((String)"spaceManager");
        SpacePermissionManager spacePermissionManager = (SpacePermissionManager)ContainerManager.getComponent((String)"spacePermissionManager");
        ConfluenceUser user = this.getAuthenticatedUser();
        ListBuilder<Space> listBuilder = spaceManager.getSpaces(SpacesQuery.newQuery().withSpaceType(SpaceType.GLOBAL).build());
        if (listBuilder.getAvailableSize() == 1) {
            Space space = (Space)((List)listBuilder.iterator().next()).iterator().next();
            if (user != null) {
                space.setCreator(user);
            }
            ArrayList<SpacePermission> oldSpacePermissions = new ArrayList<SpacePermission>(space.getPermissions());
            for (SpacePermission oldPermission : oldSpacePermissions) {
                spacePermissionManager.removePermission(oldPermission);
            }
            for (String permissionStr : SpacePermission.GENERIC_SPACE_PERMISSIONS) {
                spacePermissionManager.savePermission(SpacePermission.createGroupSpacePermission(permissionStr, space, "confluence-administrators"));
                if (user != null) {
                    spacePermissionManager.savePermission(SpacePermission.createUserSpacePermission(permissionStr, space, user));
                }
                if ("SETSPACEPERMISSIONS".equals(permissionStr)) continue;
                spacePermissionManager.savePermission(SpacePermission.createGroupSpacePermission(permissionStr, space, this.userAccessor.getNewUserDefaultGroupName()));
                if (SpacePermission.INVALID_ANONYMOUS_PERMISSIONS.contains(permissionStr)) continue;
                spacePermissionManager.savePermission(SpacePermission.createAnonymousSpacePermission(permissionStr, space));
            }
        }
    }

    public void setClusterConfigurationHelper(ClusterConfigurationHelperInternal clusterConfigurationHelper) {
        this.clusterConfigurationHelper = clusterConfigurationHelper;
    }

    public String getInstallationMethod() {
        return this.installationMethod;
    }

    public void setInstallationMethod(String installationMethod) {
        this.installationMethod = installationMethod;
    }

    public void setLifecycleManager(LifecycleManager lifecycleManager) {
        this.lifecycleManager = lifecycleManager;
    }

    public void setUpgradeGate(UpgradeGate upgradeGate) {
        this.upgradeGate = upgradeGate;
    }

    public void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void setUpgradeManager(UpgradeManager upgradeManager) {
        this.upgradeManager = upgradeManager;
    }

    public void setUpgradeFinalizationManager(UpgradeFinalizationManager upgradeFinalizationManager) {
        this.upgradeFinalizationManager = upgradeFinalizationManager;
    }

    public void setScheduledJobManager(ScheduledJobManager scheduledJobManager) {
        this.scheduledJobManager = scheduledJobManager;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.util.BootstrapUtils
 *  com.atlassian.confluence.upgrade.UpgradeManager
 *  com.atlassian.spring.container.ContainerManager
 */
package com.atlassian.confluence.impl.backuprestore.backup;

import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.impl.backuprestore.backup.container.BackupContainerWriter;
import com.atlassian.confluence.importexport.impl.ExportDescriptor;
import com.atlassian.confluence.importexport.impl.ExportScope;
import com.atlassian.confluence.license.LicenseService;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.setup.settings.GlobalSettingsManager;
import com.atlassian.confluence.upgrade.UpgradeManager;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.spring.container.ContainerManager;
import java.util.Collection;
import java.util.Map;

public class BackupDescriptorWriter {
    public ExportDescriptor writeBackupDescriptor(BackupContainerWriter backupContainerWriter, ExportScope exportScope, boolean backupAttachments, Collection<String> spaceKeys, Long objectsCount) throws BackupRestoreException {
        String buildNumber;
        ExportDescriptor exportDescriptor = new ExportDescriptor();
        exportDescriptor.setScope(exportScope);
        GlobalSettingsManager settingsManager = (GlobalSettingsManager)ContainerManager.getComponent((String)"globalSettingsManager");
        exportDescriptor.setDefaultUserGroup(settingsManager.getGlobalSettings().getDefaultUsersGroup());
        BootstrapManager bootstrapManager = (BootstrapManager)BootstrapUtils.getBootstrapManager();
        String earliestCompatibleBuildNumber = buildNumber = bootstrapManager.getBuildNumber();
        UpgradeManager upgradeManager = (UpgradeManager)ContainerManager.getComponent((String)"upgradeManager");
        if (upgradeManager != null) {
            earliestCompatibleBuildNumber = upgradeManager.getExportBuildNumber(exportScope == ExportScope.SPACE);
            Map pluginExportCompatibility = upgradeManager.getPluginExportCompatibility(exportScope == ExportScope.SPACE);
            exportDescriptor.setPluginExportCompatibility(pluginExportCompatibility);
        }
        LicenseService licenseService = (LicenseService)ContainerManager.getComponent((String)"licenseService");
        String supportEntitlementNumber = licenseService.retrieve().getSupportEntitlementNumber();
        exportDescriptor.setCreatedByBuildNumber(buildNumber);
        exportDescriptor.setBuildNumber(earliestCompatibleBuildNumber);
        exportDescriptor.setVersionNumber(GeneralUtil.getVersionNumber());
        exportDescriptor.setSource(ExportDescriptor.Source.SERVER);
        exportDescriptor.setObjectsCount(objectsCount);
        if (supportEntitlementNumber != null) {
            exportDescriptor.setSupportEntitlementNumber(supportEntitlementNumber);
        }
        this.writeSpaceKeys(exportDescriptor, spaceKeys);
        exportDescriptor.setBackupAttachments(backupAttachments);
        for (Map.Entry<Object, Object> nameAndValuePair : exportDescriptor.getProperties().entrySet()) {
            backupContainerWriter.addDescriptionProperty((String)nameAndValuePair.getKey(), (String)nameAndValuePair.getValue());
        }
        return exportDescriptor;
    }

    private void writeSpaceKeys(ExportDescriptor exportDescriptor, Collection<String> spaceKeys) {
        if (spaceKeys == null) {
            return;
        }
        exportDescriptor.setSpaceKeys(spaceKeys);
        if (spaceKeys.size() == 1) {
            exportDescriptor.setSpaceKey(spaceKeys.iterator().next());
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.importexport.DefaultImportContext
 *  com.atlassian.confluence.importexport.ImportContext
 *  com.atlassian.confluence.importexport.ImportExportException
 *  com.atlassian.confluence.importexport.ImportExportManager
 *  com.atlassian.confluence.importexport.ImportedObjectPreProcessor
 *  com.atlassian.confluence.importexport.actions.ImportLongRunningTask
 *  com.atlassian.confluence.search.IndexManager
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.security.SpacePermission
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.spaces.SpaceStatus
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  com.google.common.collect.Sets
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.efi.services;

import com.atlassian.confluence.efi.services.OnboardingSpaceImportObjectPreProcessor;
import com.atlassian.confluence.efi.services.SpaceImportConfig;
import com.atlassian.confluence.efi.services.SpaceService;
import com.atlassian.confluence.importexport.DefaultImportContext;
import com.atlassian.confluence.importexport.ImportContext;
import com.atlassian.confluence.importexport.ImportExportException;
import com.atlassian.confluence.importexport.ImportExportManager;
import com.atlassian.confluence.importexport.ImportedObjectPreProcessor;
import com.atlassian.confluence.importexport.actions.ImportLongRunningTask;
import com.atlassian.confluence.search.IndexManager;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.SpaceStatus;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import com.google.common.collect.Sets;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SpaceServiceImpl
implements SpaceService {
    private final SpaceManager spaceManager;
    private final ImportExportManager importExportManager;
    private final IndexManager indexManager;
    private final EventPublisher eventPublisher;
    private final SpacePermissionManager spacePermissionManager;
    private final SettingsManager settingsManager;
    private final PermissionManager permissionManager;

    @Autowired
    public SpaceServiceImpl(@ComponentImport SpaceManager spaceManager, @ComponentImport ImportExportManager importExportManager, @ComponentImport IndexManager indexManager, @ComponentImport EventPublisher eventPublisher, @ComponentImport SpacePermissionManager spacePermissionManager, @ComponentImport SettingsManager settingsManager, @ComponentImport PermissionManager permissionManager) {
        this.spaceManager = spaceManager;
        this.importExportManager = importExportManager;
        this.indexManager = indexManager;
        this.eventPublisher = eventPublisher;
        this.spacePermissionManager = spacePermissionManager;
        this.settingsManager = settingsManager;
        this.permissionManager = permissionManager;
    }

    @Override
    public URL getOnboardingSpaceZipURL() {
        return this.getClass().getResource("/onboarding-space.zip");
    }

    @Override
    public String createUniqueSpaceKey(String key) {
        Collection spaceKeys = this.spaceManager.getAllSpaceKeys(SpaceStatus.CURRENT);
        spaceKeys.addAll(this.spaceManager.getAllSpaceKeys(SpaceStatus.ARCHIVED));
        HashSet keyMap = Sets.newHashSet();
        for (String spaceKey : spaceKeys) {
            keyMap.add(spaceKey.toUpperCase());
        }
        return this.findUniqueKey(keyMap, key.toUpperCase());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void importAndReindex(URL onboardingSpaceZipUrl, SpaceImportConfig importConfig) throws ImportExportException {
        ConfluenceUser authenticated = AuthenticatedUserThreadLocal.get();
        try {
            DefaultImportContext importContext = this.createImportContext(onboardingSpaceZipUrl, importConfig);
            ImportLongRunningTask task = new ImportLongRunningTask(this.eventPublisher, this.indexManager, this.importExportManager, (ImportContext)importContext);
            task.run();
            if (!task.isSuccessful()) {
                throw new ImportExportException("Import onboarding space task failed");
            }
        }
        finally {
            AuthenticatedUserThreadLocal.set((ConfluenceUser)authenticated);
        }
        this.updateSpaceCreatorAndPermissions(importConfig.getSpaceKey(), importConfig.getActor(), importConfig.isTemporary());
    }

    private DefaultImportContext createImportContext(URL onboardingSpaceZipUrl, SpaceImportConfig importConfig) {
        DefaultImportContext importContext = new DefaultImportContext(onboardingSpaceZipUrl, importConfig.getActor());
        importContext.setPreProcessor((ImportedObjectPreProcessor)new OnboardingSpaceImportObjectPreProcessor(importConfig));
        importContext.setDefaultUsersGroup("confluence-users");
        importContext.setSpaceKeyOfSpaceImport(importConfig.getSpaceKey());
        importContext.setIncrementalImport(true);
        importContext.setRebuildIndex(false);
        return importContext;
    }

    private void updateSpaceCreatorAndPermissions(String spaceKey, ConfluenceUser user, boolean temporary) {
        Space sp = this.spaceManager.getSpace(spaceKey);
        if (sp == null) {
            return;
        }
        sp.setCreator(user);
        if (temporary && sp.getDescription() != null) {
            sp.getDescription().setBodyAsString("efionboardingspace");
        }
        this.spaceManager.saveSpace(sp);
        if (this.permissionManager.isConfluenceAdministrator((User)user)) {
            this.fixPermissionsOfSpace(sp, user);
        }
    }

    private void fixPermissionsOfSpace(Space space, ConfluenceUser user) {
        ArrayList oldSpacePermissions = new ArrayList(space.getPermissions());
        for (SpacePermission oldPermission : oldSpacePermissions) {
            this.spacePermissionManager.removePermission(oldPermission);
        }
        for (String permissionStr : SpacePermission.GENERIC_SPACE_PERMISSIONS) {
            this.spacePermissionManager.savePermission(new SpacePermission(permissionStr, space, "confluence-administrators"));
            if (user != null) {
                this.spacePermissionManager.savePermission(SpacePermission.createUserSpacePermission((String)permissionStr, (Space)space, (ConfluenceUser)user));
            }
            if (!"SETSPACEPERMISSIONS".equals(permissionStr)) {
                this.spacePermissionManager.savePermission(new SpacePermission(permissionStr, space, this.settingsManager.getGlobalSettings().getDefaultUsersGroup()));
            }
            if (SpacePermission.INVALID_ANONYMOUS_PERMISSIONS.contains(permissionStr)) continue;
            this.spacePermissionManager.savePermission(new SpacePermission(permissionStr, space));
        }
    }

    private String findUniqueKey(Collection<String> keyMap, String currentKey) {
        if (!keyMap.contains(currentKey)) {
            return currentKey;
        }
        for (int i = 1; i < 999; ++i) {
            String key = currentKey + i;
            if (keyMap.contains(key)) continue;
            return key;
        }
        return null;
    }
}


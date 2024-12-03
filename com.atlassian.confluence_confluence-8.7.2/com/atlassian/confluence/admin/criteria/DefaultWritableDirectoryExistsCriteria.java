/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.ApplicationFactory
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.manager.permission.PermissionManager
 */
package com.atlassian.confluence.admin.criteria;

import com.atlassian.confluence.admin.criteria.AdminConfigurationCriteria;
import com.atlassian.confluence.admin.criteria.DirectoryUtil;
import com.atlassian.confluence.admin.criteria.WritableDirectoryExistsCriteria;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.crowd.embedded.api.ApplicationFactory;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.manager.permission.PermissionManager;

public class DefaultWritableDirectoryExistsCriteria
implements AdminConfigurationCriteria,
WritableDirectoryExistsCriteria {
    private final SettingsManager settingsManager;
    private final ApplicationFactory applicationFactory;
    private final PermissionManager permissionManager;

    public DefaultWritableDirectoryExistsCriteria(SettingsManager settingsManager, ApplicationFactory applicationFactory, PermissionManager permissionManager) {
        this.settingsManager = settingsManager;
        this.applicationFactory = applicationFactory;
        this.permissionManager = permissionManager;
    }

    @Override
    public boolean isMet() {
        return !this.settingsManager.getGlobalSettings().isExternalUserManagement() && DirectoryUtil.findFirstDirectoryWithCreateUserPermission(this.applicationFactory.getApplication(), this.permissionManager) != null;
    }

    @Override
    public String getValue() {
        Directory firstWritableDirectory = DirectoryUtil.findFirstDirectoryWithCreateUserPermission(this.applicationFactory.getApplication(), this.permissionManager);
        return this.isMet() ? firstWritableDirectory.getName() : "External";
    }

    @Override
    public boolean hasValue() {
        return true;
    }

    @Override
    public boolean hasLiveValue() {
        return true;
    }

    @Override
    public boolean getIgnored() {
        return false;
    }

    @Override
    public void setIgnored(boolean ignored) {
    }
}


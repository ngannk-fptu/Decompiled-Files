/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.user.UserManager
 */
package com.atlassian.audit.plugin.onboarding;

import com.atlassian.audit.permission.PermissionChecker;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.user.UserManager;

public class OnboardingSeenService {
    private static final String ONBOARDING_SEEN_PREFIX = "com.atlassian.audit.plugin:audit-config:onboarding:%d:isseen";
    private static final String ONBOARDING_DARKFEATURE_FLAG = "atlassian.darkfeature.audit.onboarding.feature.disabled";
    private final UserManager userManager;
    private final PluginSettingsFactory pluginSettingsFactory;
    private final PermissionChecker permissionChecker;

    public OnboardingSeenService(UserManager userManager, PluginSettingsFactory pluginSettingsFactory, PermissionChecker permissionChecker) {
        this.userManager = userManager;
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.permissionChecker = permissionChecker;
    }

    public boolean shouldDisplay() {
        return !this.isOnboardingDisable() && this.isOnboardingTargetUser() && !this.getSeenValue();
    }

    public void seenAndDismissed() {
        if (this.isOnboardingTargetUser()) {
            this.updateSeenValue(true);
        }
    }

    private boolean isOnboardingTargetUser() {
        return this.permissionChecker.hasUnrestrictedAuditViewPermission();
    }

    private String getKey() {
        int usernameCache = this.userManager.getRemoteUserKey().hashCode();
        return String.format(ONBOARDING_SEEN_PREFIX, usernameCache);
    }

    private boolean getSeenValue() {
        PluginSettings settings = this.pluginSettingsFactory.createGlobalSettings();
        String isSeen = (String)settings.get(this.getKey());
        if (isSeen != null) {
            return Boolean.parseBoolean(isSeen);
        }
        return false;
    }

    private void updateSeenValue(boolean isSeen) {
        PluginSettings settings = this.pluginSettingsFactory.createGlobalSettings();
        settings.put(this.getKey(), (Object)String.valueOf(isSeen));
    }

    private boolean isOnboardingDisable() {
        String disableFlag = System.getProperty(ONBOARDING_DARKFEATURE_FLAG);
        return Boolean.parseBoolean(disableFlag);
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.admin.criteria.MailServerExistsCriteria
 *  com.atlassian.confluence.core.ConfluenceSystemProperties
 *  com.atlassian.confluence.core.HeartbeatManager
 *  com.atlassian.confluence.license.LicenseService
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.setup.settings.GlobalSettingsManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.core.user.preferences.UserPreferences
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.ContextProvider
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.tinymceplugin.web;

import com.atlassian.confluence.admin.criteria.MailServerExistsCriteria;
import com.atlassian.confluence.core.ConfluenceSystemProperties;
import com.atlassian.confluence.core.HeartbeatManager;
import com.atlassian.confluence.license.LicenseService;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.settings.GlobalSettingsManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.core.user.preferences.UserPreferences;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.ContextProvider;
import com.atlassian.user.User;
import java.util.LinkedHashMap;
import java.util.Map;

public class EditorSettingsContextProvider
implements ContextProvider {
    private final GlobalSettingsManager settingsManager;
    private final PermissionManager permissionManager;
    private final HeartbeatManager heartbeatManager;
    private final UserAccessor userAccessor;
    private final MailServerExistsCriteria mailServerExistsCriteria;
    private final LicenseService licenseService;

    public EditorSettingsContextProvider(GlobalSettingsManager settingsManager, PermissionManager permissionManager, UserAccessor userAccessor, HeartbeatManager heartbeatManager, MailServerExistsCriteria mailServerExistsCriteria, LicenseService licenseService) {
        this.settingsManager = settingsManager;
        this.permissionManager = permissionManager;
        this.userAccessor = userAccessor;
        this.heartbeatManager = heartbeatManager;
        this.mailServerExistsCriteria = mailServerExistsCriteria;
        this.licenseService = licenseService;
    }

    public void init(Map<String, String> params) throws PluginParseException {
    }

    public Map<String, Object> getContextMap(Map<String, Object> context) {
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        LinkedHashMap<String, Object> settings = new LinkedHashMap<String, Object>();
        settings.put("max-thumb-width", this.settingsManager.getGlobalSettings().getMaxThumbWidth());
        settings.put("max-thumb-height", this.settingsManager.getGlobalSettings().getMaxThumbHeight());
        settings.put("can-send-email", this.mailServerExistsCriteria.isMet());
        settings.put("is-dev-mode", ConfluenceSystemProperties.isDevMode());
        settings.put("draft-save-interval", this.settingsManager.getGlobalSettings().getDraftSaveInterval());
        settings.put("show-hidden-user-macros", this.permissionManager.hasPermission((User)currentUser, Permission.ADMINISTER, PermissionManager.TARGET_SYSTEM));
        settings.put("can-view-profile", this.permissionManager.hasPermission((User)currentUser, Permission.VIEW, User.class));
        settings.put("is-admin", this.permissionManager.isConfluenceAdministrator((User)currentUser));
        settings.put("is-dc-license", this.licenseService.isLicensedForDataCenter());
        if (currentUser != null) {
            UserPreferences userPreferences = this.userAccessor.getUserPreferences((User)currentUser);
            this.addUserPreference("confluence.prefs.editor.disable.autocomplete", settings, userPreferences);
            this.addUserPreference("confluence.prefs.editor.disable.autoformat", settings, userPreferences);
        }
        settings.put("heartbeat-interval", this.heartbeatManager.getHeartbeatInterval());
        context.put("settings", settings.entrySet());
        return context;
    }

    private void addUserPreference(String property, Map<String, Object> settings, UserPreferences userPreferences) {
        settings.put(property, userPreferences.getBoolean(property));
    }
}


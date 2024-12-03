/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.user.UserPreferencesKeys
 *  com.atlassian.core.user.preferences.UserPreferences
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.highlight.service;

import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.user.UserPreferencesKeys;
import com.atlassian.core.user.preferences.UserPreferences;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HighlightOptionPanelConfigService {
    private static final String USER_HIGHLIGHT_OPTION_PANEL_CONFIG_FIELD = "PROPERTY_USER_HIGHLIGHT_OPTION_PANEL_ENABLED";
    private final UserAccessor userAccessor;

    @Autowired
    public HighlightOptionPanelConfigService(@ComponentImport UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }

    public boolean isSupported() {
        Class<UserPreferencesKeys> userPreferencesKeysClass = UserPreferencesKeys.class;
        try {
            userPreferencesKeysClass.getDeclaredField(USER_HIGHLIGHT_OPTION_PANEL_CONFIG_FIELD);
            return true;
        }
        catch (SecurityException e) {
            return false;
        }
        catch (NoSuchFieldException e) {
            return false;
        }
    }

    public boolean isEnabled() {
        return this.getUserPreferences().getBoolean("confluence.user.highlight.option.panel.enabled");
    }

    protected UserPreferences getUserPreferences() {
        return new UserPreferences(this.userAccessor.getPropertySet(this.getUser()));
    }

    private ConfluenceUser getUser() {
        return AuthenticatedUserThreadLocal.get();
    }
}


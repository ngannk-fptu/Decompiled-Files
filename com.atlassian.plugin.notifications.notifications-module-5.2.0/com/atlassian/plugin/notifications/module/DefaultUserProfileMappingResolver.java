/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.user.UserProfile
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.plugin.notifications.module;

import com.atlassian.plugin.notifications.api.event.EventContextBuilder;
import com.atlassian.plugin.notifications.api.macros.MacroResolver;
import com.atlassian.plugin.notifications.api.macros.UserProfileMappingResolver;
import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.api.notification.UserNotificationPreferencesManager;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserProfile;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;

public class DefaultUserProfileMappingResolver
implements UserProfileMappingResolver {
    private final MacroResolver macroResolver;
    private final I18nResolver i18n;
    private final UserNotificationPreferencesManager prefManager;

    public DefaultUserProfileMappingResolver(MacroResolver macroResolver, @Qualifier(value="i18nResolver") I18nResolver i18n, UserNotificationPreferencesManager prefManager) {
        this.macroResolver = macroResolver;
        this.i18n = i18n;
        this.prefManager = prefManager;
    }

    @Override
    public String resolveMapping(UserProfile profileUser, ServerConfiguration server) {
        String serverMapping = this.prefManager.getPreferences(profileUser.getUserKey()).getServerMapping(server);
        Map<String, Object> context = EventContextBuilder.buildContext(null, this.i18n, profileUser.getUserKey(), null, server);
        return this.macroResolver.resolveAll(serverMapping, context);
    }
}


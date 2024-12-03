/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.notifications.api.medium.ServerConfiguration
 *  com.atlassian.plugin.notifications.api.notification.UserNotificationPreferences
 *  com.atlassian.plugin.notifications.spi.UserRole
 *  com.atlassian.plugin.web.ContextProvider
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserProfile
 *  com.google.common.base.Function
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Ordering
 */
package com.atlassian.confluence.notifications.ui;

import com.atlassian.confluence.notifications.impl.RoutesProvider;
import com.atlassian.confluence.notifications.ui.UserPreferenceRoleEntry;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.api.notification.UserNotificationPreferences;
import com.atlassian.plugin.notifications.spi.UserRole;
import com.atlassian.plugin.web.ContextProvider;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserProfile;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class UserSettingsWebPanelContextProvider
implements ContextProvider {
    private final RoutesProvider routesProvider;
    private Comparator<ServerConfiguration> serverComparator = (o1, o2) -> o1.getServerName().compareTo(o2.getServerName());

    public UserSettingsWebPanelContextProvider(RoutesProvider routesProvider) {
        this.routesProvider = routesProvider;
    }

    public void init(Map<String, String> params) throws PluginParseException {
    }

    public Map<String, Object> getContextMap(Map<String, Object> context) {
        Iterable servers = (Iterable)context.get("servers");
        Iterable userRoles = (Iterable)context.get("userRoles");
        UserNotificationPreferences userPrefs = (UserNotificationPreferences)context.get("userPrefs");
        UserProfile userProfile = (UserProfile)context.get("profileUser");
        List configuredServers = Ordering.from(this.serverComparator).sortedCopy(Iterables.filter((Iterable)servers, this.configuredServerPredicate(userProfile.getUserKey())));
        context.put("userPreferencesData", this.generateUserPreferenceRoleEntry(userRoles, configuredServers, userPrefs));
        context.put("servers", configuredServers);
        return context;
    }

    private Iterable<UserPreferenceRoleEntry> generateUserPreferenceRoleEntry(Iterable<UserRole> userRoles, Iterable<ServerConfiguration> configuredServers, UserNotificationPreferences userPrefs) {
        return Iterables.transform(userRoles, this.toRoleEntry(configuredServers, userPrefs));
    }

    private Function<UserRole, UserPreferenceRoleEntry> toRoleEntry(Iterable<ServerConfiguration> configuredServers, UserNotificationPreferences userPrefs) {
        return userRole -> new UserPreferenceRoleEntry(userRole.getID(), Iterables.transform((Iterable)configuredServers, this.makeServerEntry((UserRole)userRole, userPrefs)));
    }

    private Predicate<ServerConfiguration> configuredServerPredicate(UserKey userKey) {
        return input -> input.getNotificationMedium().isUserConfigured(userKey);
    }

    private Function<ServerConfiguration, UserPreferenceRoleEntry.UserPreferenceServerEntry> makeServerEntry(UserRole userRole, UserNotificationPreferences userPrefs) {
        return serverConfiguration -> new UserPreferenceRoleEntry.UserPreferenceServerEntry(serverConfiguration.getId(), userPrefs.isNotificationEnabled(serverConfiguration, userRole), this.routesProvider.getStaticMediumPreference(serverConfiguration.getNotificationMedium(), userRole));
    }
}


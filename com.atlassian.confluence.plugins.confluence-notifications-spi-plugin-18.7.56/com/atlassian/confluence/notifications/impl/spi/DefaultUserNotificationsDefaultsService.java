/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.notifications.api.medium.ServerConfiguration
 *  com.atlassian.plugin.notifications.api.medium.ServerManager
 *  com.atlassian.plugin.notifications.api.notification.UserNotificationPreferences
 *  com.atlassian.plugin.notifications.api.notification.UserNotificationPreferencesManager
 *  com.atlassian.plugin.notifications.spi.UserRole
 *  com.atlassian.plugin.notifications.spi.UserRolesProvider
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.collect.ImmutableSet
 *  javax.annotation.Nonnull
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.notifications.impl.spi;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.notifications.UserNotificationsDefaultsService;
import com.atlassian.confluence.notifications.impl.analytics.UserNotificationPreferenceApplyDefaults;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.api.medium.ServerManager;
import com.atlassian.plugin.notifications.api.notification.UserNotificationPreferences;
import com.atlassian.plugin.notifications.api.notification.UserNotificationPreferencesManager;
import com.atlassian.plugin.notifications.spi.UserRole;
import com.atlassian.plugin.notifications.spi.UserRolesProvider;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import javax.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Qualifier;

@Internal
public class DefaultUserNotificationsDefaultsService
implements UserNotificationsDefaultsService {
    static final Set<ServerRoleCombination> DEFAULT_PREFERENCES_OFF = ImmutableSet.of((Object)new ServerRoleCombination("mail", "SHARE_PAGE"), (Object)new ServerRoleCombination("mail", "com.atlassian.confluence.plugins.mentions"), (Object)new ServerRoleCombination("mail", "TASK_UPDATE_NOTIFICATION"));
    private static final Set<ServerRoleCombination> DEFAULT_PREFERENCES_ON = ImmutableSet.of((Object)new ServerRoleCombination("hipchat", "SHARE_PAGE"), (Object)new ServerRoleCombination("hipchat", "com.atlassian.confluence.plugins.mentions"), (Object)new ServerRoleCombination("hipchat", "TASK_UPDATE_NOTIFICATION"));
    private final UserNotificationPreferencesManager userNotificationPreferencesManager;
    private final ServerManager serverManager;
    private final UserRolesProvider rolesProvider;
    private EventPublisher eventPublisher;

    public DefaultUserNotificationsDefaultsService(@Qualifier(value="confluenceNotificationPreferenceManager") UserNotificationPreferencesManager userNotificationPreferencesManager, ServerManager serverManager, UserRolesProvider rolesProvider, EventPublisher eventPublisher) {
        this.userNotificationPreferencesManager = userNotificationPreferencesManager;
        this.serverManager = serverManager;
        this.rolesProvider = rolesProvider;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void applyDefaultsForUser(UserKey userKey) {
        UserNotificationPreferences pref = this.userNotificationPreferencesManager.getPreferences(userKey);
        for (ServerConfiguration server : this.serverManager.getServersForUser(userKey)) {
            for (UserRole role : this.rolesProvider.getRoles()) {
                ServerRoleCombination preferenceCombination = new ServerRoleCombination(server.getNotificationMedium().getKey(), role.getID());
                if (DEFAULT_PREFERENCES_ON.contains(preferenceCombination)) {
                    pref.setNotificationEnabled(server, role, true);
                }
                if (!DEFAULT_PREFERENCES_OFF.contains(preferenceCombination)) continue;
                pref.setNotificationEnabled(server, role, false);
            }
        }
        this.eventPublisher.publish((Object)new UserNotificationPreferenceApplyDefaults());
    }

    @Override
    public boolean isUserSettingsDefaults(UserKey userKey) {
        UserNotificationPreferences pref = this.userNotificationPreferencesManager.getPreferences(userKey);
        for (ServerConfiguration server : this.serverManager.getServersForUser(userKey)) {
            for (UserRole role : this.rolesProvider.getRoles()) {
                ServerRoleCombination preferenceCombination = new ServerRoleCombination(server.getNotificationMedium().getKey(), role.getID());
                if (DEFAULT_PREFERENCES_ON.contains(preferenceCombination) && !pref.isNotificationEnabled(server, role)) {
                    return false;
                }
                if (!DEFAULT_PREFERENCES_OFF.contains(preferenceCombination) || !pref.isNotificationEnabled(server, role)) continue;
                return false;
            }
        }
        return true;
    }

    private static final class ServerRoleCombination {
        private final String mediumkey;
        private final String role;

        private ServerRoleCombination(@Nonnull String mediumkey, @Nonnull String role) {
            this.mediumkey = mediumkey;
            this.role = role;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            ServerRoleCombination that = (ServerRoleCombination)o;
            if (!this.mediumkey.equals(that.mediumkey)) {
                return false;
            }
            return this.role.equals(that.role);
        }

        public int hashCode() {
            int result = this.mediumkey.hashCode();
            result = 31 * result + this.role.hashCode();
            return result;
        }
    }
}


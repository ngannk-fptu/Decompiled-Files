/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.tx.Transactional
 *  com.atlassian.fugue.Either
 */
package com.atlassian.plugin.notifications.config;

import com.atlassian.activeobjects.tx.Transactional;
import com.atlassian.fugue.Either;
import com.atlassian.plugin.notifications.api.ErrorCollection;
import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.api.notification.NotificationStatusRepresentation;
import java.util.Map;

@Transactional
public interface ServerConfigurationManager {
    public static final String LEGACY_NOTIFICATIONS_SEND_DISABLED_FLAG = "atlassian.mail.senddisabled";
    public static final String NOTIFICATIONS_SEND_DISABLED_FLAG = "atlassian.notifications.disabled";

    public Either<ErrorCollection, ServerValidationResult> validateAddServer(CommonServerConfig var1, Map<String, String> var2);

    public ServerConfiguration addServer(ServerValidationResult var1);

    public Either<ErrorCollection, ServerValidationResult> validateUpdateServer(CommonServerConfig var1, Map<String, String> var2);

    public ServerConfiguration updateServer(ServerValidationResult var1);

    public void removeServer(int var1);

    public Iterable<ServerConfiguration> getServers();

    public Iterable<ServerConfiguration> getServersForIndividual();

    public ServerConfiguration getServer(int var1);

    public ErrorCollection validateToggleNotifications(String var1, boolean var2);

    public void toggleNotifications(boolean var1);

    public NotificationStatusRepresentation getNotificationStatus();

    public static class ServerValidationResult {
        private final CommonServerConfig commonConfig;
        private final Map<String, String> params;

        public ServerValidationResult(CommonServerConfig commonConfig, Map<String, String> params) {
            this.commonConfig = commonConfig;
            this.params = params;
        }

        public CommonServerConfig getCommonConfig() {
            return this.commonConfig;
        }

        public Map<String, String> getParams() {
            return this.params;
        }
    }

    public static class CommonServerConfig {
        private final int id;
        private final String name;
        private final String notificationMedium;
        private final String customTemplatePath;
        private final boolean enabledForAll;
        private final Iterable<String> groupsWithAccess;

        public CommonServerConfig(int id, String name, String notificationMedium, String customTemplatePath, boolean enabledForAll, Iterable<String> groupsWithAccess) {
            this.id = id;
            this.name = name;
            this.notificationMedium = notificationMedium;
            this.customTemplatePath = customTemplatePath;
            this.enabledForAll = enabledForAll;
            this.groupsWithAccess = groupsWithAccess;
        }

        public int getId() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }

        public String getNotificationMedium() {
            return this.notificationMedium;
        }

        public String getCustomTemplatePath() {
            return this.customTemplatePath;
        }

        public boolean isEnabledForAll() {
            return this.enabledForAll;
        }

        public Iterable<String> getGroupsWithAccess() {
            return this.groupsWithAccess;
        }
    }
}


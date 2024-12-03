/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.notifications.ui;

import java.net.URI;

public class UserPreferenceRoleEntry {
    private final Iterable<UserPreferenceServerEntry> serverEntries;
    private final String userRoleId;

    public UserPreferenceRoleEntry(String userRoleId, Iterable<UserPreferenceServerEntry> serverEntries) {
        this.serverEntries = serverEntries;
        this.userRoleId = userRoleId;
    }

    public String getUserRoleId() {
        return this.userRoleId;
    }

    public Iterable<UserPreferenceServerEntry> getServerEntries() {
        return this.serverEntries;
    }

    public static class UserPreferenceServerEntry {
        private final int serverId;
        private final boolean notificationEnabled;
        private final URI preferenceResourceUrl;

        public UserPreferenceServerEntry(int serverId, boolean notificationEnabled, URI preferenceResourceUrl) {
            this.serverId = serverId;
            this.notificationEnabled = notificationEnabled;
            this.preferenceResourceUrl = preferenceResourceUrl;
        }

        public int getServerId() {
            return this.serverId;
        }

        public boolean isNotificationEnabled() {
            return this.notificationEnabled;
        }

        public URI getPreferenceResourceUrl() {
            return this.preferenceResourceUrl;
        }

        public boolean isNotificationAvailable() {
            return true;
        }
    }
}


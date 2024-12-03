/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.notification;

import com.atlassian.upm.pac.AvailableAddonWithVersion;

public interface PluginUpdateChecker {
    public Iterable<AvailableAddonWithVersion> checkForUpdates(UpdateCheckOptions var1);

    public static class UpdateCheckOptions {
        private final boolean userInitiated;
        private final boolean updateNotifications;
        private final boolean installAutoUpdates;

        UpdateCheckOptions(boolean userInitiated, boolean updateNotifications, boolean installAutoUpdates) {
            this.userInitiated = userInitiated;
            this.updateNotifications = updateNotifications;
            this.installAutoUpdates = installAutoUpdates;
        }

        public UpdateCheckOptions userInitiated(boolean value) {
            return new UpdateCheckOptions(value, this.updateNotifications, this.installAutoUpdates);
        }

        public UpdateCheckOptions updateNotifications(boolean value) {
            return new UpdateCheckOptions(this.userInitiated, value, this.installAutoUpdates);
        }

        public UpdateCheckOptions installAutoUpdates(boolean value) {
            return new UpdateCheckOptions(this.userInitiated, this.updateNotifications, value);
        }

        public boolean isUserInitiated() {
            return this.userInitiated;
        }

        public boolean isUpdateNotifications() {
            return this.updateNotifications;
        }

        public boolean isInstallAutoUpdates() {
            return this.installAutoUpdates;
        }

        public static UpdateCheckOptions options() {
            return new UpdateCheckOptions(false, false, false);
        }
    }
}


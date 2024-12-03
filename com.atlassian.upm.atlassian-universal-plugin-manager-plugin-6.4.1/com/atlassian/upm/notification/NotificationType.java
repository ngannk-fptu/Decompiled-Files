/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.notification;

import com.atlassian.upm.core.permission.Permission;
import java.util.Objects;

public enum NotificationType {
    PLUGIN_REQUEST("plugin.request", Permission.GET_PLUGIN_REQUESTS),
    PLUGIN_UPDATE_AVAILABLE("update", Permission.MANAGE_IN_PROCESS_PLUGIN_INSTALL_FROM_URI),
    EXPIRED_EVALUATION_PLUGIN_LICENSE("evaluation.expired", Permission.MANAGE_PLUGIN_LICENSE),
    NEARLY_EXPIRED_EVALUATION_PLUGIN_LICENSE("evaluation.nearlyexpired", Permission.MANAGE_PLUGIN_LICENSE),
    EDITION_MISMATCH_PLUGIN_LICENSE("edition.mismatch", Permission.MANAGE_PLUGIN_LICENSE),
    MAINTENANCE_EXPIRED_PLUGIN_LICENSE("maintenance.expired", Permission.MANAGE_PLUGIN_LICENSE),
    MAINTENANCE_NEARLY_EXPIRED_PLUGIN_LICENSE("maintenance.nearlyexpired", Permission.MANAGE_PLUGIN_LICENSE),
    AUTO_UPDATED_PLUGIN("auto.updated.plugin", Permission.GET_INSTALLED_PLUGINS),
    AUTO_UPDATED_UPM("auto.updated.upm", Permission.GET_INSTALLED_PLUGINS),
    DATA_CENTER_EXPIRED_PLUGIN_LICENSE("license.expired", Permission.MANAGE_PLUGIN_LICENSE),
    DATA_CENTER_NEARLY_EXPIRED_PLUGIN_LICENSE("license.nearlyexpired", Permission.MANAGE_PLUGIN_LICENSE);

    private final String key;
    private final Permission permission;

    private NotificationType(String key, Permission permission) {
        this.key = Objects.requireNonNull(key, "key");
        this.permission = Objects.requireNonNull(permission, "permission");
    }

    public String getKey() {
        return this.key;
    }

    public Permission getRequiredPermission() {
        return this.permission;
    }

    public boolean isForInstalledPluginsOnly() {
        switch (this) {
            case PLUGIN_UPDATE_AVAILABLE: 
            case EXPIRED_EVALUATION_PLUGIN_LICENSE: 
            case NEARLY_EXPIRED_EVALUATION_PLUGIN_LICENSE: 
            case EDITION_MISMATCH_PLUGIN_LICENSE: 
            case MAINTENANCE_EXPIRED_PLUGIN_LICENSE: 
            case MAINTENANCE_NEARLY_EXPIRED_PLUGIN_LICENSE: 
            case DATA_CENTER_EXPIRED_PLUGIN_LICENSE: 
            case DATA_CENTER_NEARLY_EXPIRED_PLUGIN_LICENSE: 
            case AUTO_UPDATED_PLUGIN: {
                return true;
            }
        }
        return false;
    }

    public boolean isAlwaysDisplayedIndividually() {
        return this == AUTO_UPDATED_PLUGIN;
    }

    public boolean isDismissedOnClick() {
        return this == AUTO_UPDATED_PLUGIN || this == AUTO_UPDATED_UPM;
    }

    public static NotificationType fromKey(String key) {
        for (NotificationType type : NotificationType.values()) {
            if (!type.getKey().equals(key)) continue;
            return type;
        }
        return null;
    }
}


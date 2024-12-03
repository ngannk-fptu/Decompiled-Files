/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.license.internal;

public enum PluginLicenseDowngradeError {
    EXPIRY_DATE_DOWNGRADE("upm.message.update.app.license.warning.expiry.date"),
    MAINTENANCE_EXPIRY_DATE_DOWNGRADE("upm.message.update.app.license.warning.maintenance.expiry.date"),
    USER_DOWNGRADE("upm.message.update.app.license.warning.user.count"),
    ROLE_DOWNGRADE("upm.message.update.app.license.warning.role.count"),
    EVALUATION_DOWNGRADE("upm.message.update.app.license.warning.evaluation");

    final String messageKey;

    private PluginLicenseDowngradeError(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getKey() {
        return this.messageKey;
    }
}


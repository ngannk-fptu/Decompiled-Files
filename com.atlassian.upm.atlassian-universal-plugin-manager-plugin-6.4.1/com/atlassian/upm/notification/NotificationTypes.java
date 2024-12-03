/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 */
package com.atlassian.upm.notification;

import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.upm.notification.NotificationType;
import java.util.Objects;

public class NotificationTypes {
    private final ApplicationProperties applicationProperties;

    public NotificationTypes(ApplicationProperties applicationProperties) {
        this.applicationProperties = Objects.requireNonNull(applicationProperties, "applicationProperties");
    }

    public String getTitleI18nKey(NotificationType type) {
        return "upm.notification." + type.getKey() + ".title";
    }

    public String getSingularMessageI18nKey(NotificationType type) {
        return "upm.notification." + this.getKey(type) + ".body.singular";
    }

    public String getPluralMessageI18nKey(NotificationType type) {
        return "upm.notification." + this.getKey(type) + ".body.plural";
    }

    public String getIndividualNotificationI18nKey(NotificationType type) {
        return "upm.notification." + this.getKey(type) + ".body.individual";
    }

    private String getKey(NotificationType type) {
        switch (type) {
            case EDITION_MISMATCH_PLUGIN_LICENSE: {
                if (this.applicationProperties.getDisplayName().equalsIgnoreCase("bamboo")) {
                    return type.getKey() + ".agent";
                }
                return type.getKey() + ".user";
            }
        }
        return type.getKey();
    }
}


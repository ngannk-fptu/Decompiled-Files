/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jmx.export.metadata;

import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

public class ManagedNotification {
    @Nullable
    private String[] notificationTypes;
    @Nullable
    private String name;
    @Nullable
    private String description;

    public void setNotificationType(String notificationType) {
        this.notificationTypes = StringUtils.commaDelimitedListToStringArray(notificationType);
    }

    public void setNotificationTypes(String ... notificationTypes) {
        this.notificationTypes = notificationTypes;
    }

    @Nullable
    public String[] getNotificationTypes() {
        return this.notificationTypes;
    }

    public void setName(@Nullable String name) {
        this.name = name;
    }

    @Nullable
    public String getName() {
        return this.name;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    @Nullable
    public String getDescription() {
        return this.description;
    }
}


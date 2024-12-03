/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.notification;

import com.atlassian.upm.notification.DismissedState;
import com.atlassian.upm.notification.NotificationType;
import java.util.Objects;
import java.util.function.Function;

public class Notification {
    private final NotificationType type;
    private final String pluginKey;
    private final DismissedState dismissedState;

    Notification(NotificationType type, String pluginKey, DismissedState dismissedState) {
        this.type = Objects.requireNonNull(type, "type");
        this.pluginKey = Objects.requireNonNull(pluginKey, "pluginKey");
        this.dismissedState = Objects.requireNonNull(dismissedState, "dismissedState");
    }

    public NotificationType getType() {
        return this.type;
    }

    public String getPluginKey() {
        return this.pluginKey;
    }

    public DismissedState getDismissedState() {
        return this.dismissedState;
    }

    public static Function<Notification, String> toNotificationPluginKey() {
        return Notification::getPluginKey;
    }

    public boolean equals(Object o) {
        return o != null && o instanceof Notification && ((Notification)o).getPluginKey().equals(this.getPluginKey());
    }

    public String toString() {
        return "Notification<" + this.getPluginKey() + ">";
    }
}


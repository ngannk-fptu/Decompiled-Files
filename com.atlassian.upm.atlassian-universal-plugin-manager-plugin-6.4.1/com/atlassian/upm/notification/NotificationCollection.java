/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.notification;

import com.atlassian.upm.notification.DismissedState;
import com.atlassian.upm.notification.Notification;
import com.atlassian.upm.notification.NotificationType;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class NotificationCollection
implements Iterable<Notification> {
    private final NotificationType type;
    private final List<Notification> notifications;
    private final int count;
    private final DismissedState dismissedState;
    private final Predicate<Notification> isDismissed = notification -> notification.getDismissedState().isDismissed();

    public NotificationCollection(NotificationType type, DismissedState dismissedState, Iterable<Notification> notifications) {
        this.type = Objects.requireNonNull(type, "type");
        this.notifications = Collections.unmodifiableList(StreamSupport.stream(notifications.spliterator(), false).filter(this.isExpectedType()).collect(Collectors.toList()));
        this.count = this.notifications.size();
        this.dismissedState = Objects.requireNonNull(dismissedState, "dismissedState");
    }

    public NotificationCollection(NotificationType type, DismissedState dismissedState, int count) {
        this.type = Objects.requireNonNull(type, "type");
        this.notifications = Collections.emptyList();
        this.count = count;
        this.dismissedState = Objects.requireNonNull(dismissedState, "dismissedState");
    }

    @Override
    public Iterator<Notification> iterator() {
        return this.notifications.iterator();
    }

    public Iterable<Notification> getDisplayableNotifications() {
        return this.type.isDismissedOnClick() ? (Iterable)StreamSupport.stream(this.notifications.spliterator(), false).filter(this.isDismissed().negate()).collect(Collectors.toList()) : this.notifications;
    }

    public NotificationType getType() {
        return this.type;
    }

    public DismissedState getDismissedState() {
        return this.dismissedState;
    }

    public int getNotificationCount() {
        return this.count;
    }

    private Predicate<Notification> isExpectedType() {
        return new IsExpectedType();
    }

    private Predicate<Notification> isDismissed() {
        return this.isDismissed;
    }

    private final class IsExpectedType
    implements Predicate<Notification> {
        private IsExpectedType() {
        }

        @Override
        public boolean test(Notification notification) {
            if (!notification.getType().equals((Object)NotificationCollection.this.type)) {
                throw new IllegalArgumentException("Expected all to be of type " + (Object)((Object)NotificationCollection.this.type) + ", found type " + (Object)((Object)notification.getType()));
            }
            return true;
        }
    }
}


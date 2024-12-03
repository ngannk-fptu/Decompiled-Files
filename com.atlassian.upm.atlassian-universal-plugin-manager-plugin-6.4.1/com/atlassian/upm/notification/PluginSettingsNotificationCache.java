/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.beehive.ClusterLock
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.upm.notification;

import com.atlassian.beehive.ClusterLock;
import com.atlassian.beehive.ClusterLockService;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.api.util.Pair;
import com.atlassian.upm.core.impl.NamespacedPluginSettings;
import com.atlassian.upm.impl.Locks;
import com.atlassian.upm.notification.Notification;
import com.atlassian.upm.notification.NotificationCache;
import com.atlassian.upm.notification.NotificationCollection;
import com.atlassian.upm.notification.NotificationFactory;
import com.atlassian.upm.notification.NotificationType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class PluginSettingsNotificationCache
implements NotificationCache {
    public static final String KEY_PREFIX = "com.atlassian.upm:notifications:";
    private static final String KEY_NOTIFICATION_PREFIX = "notification-";
    private static final String KEY_DISMISSAL_PREFIX = "dismissal-";
    private final ClusterLock lock;
    private final PluginSettingsFactory pluginSettingsFactory;
    private final NotificationFactory notificationFactory;

    public PluginSettingsNotificationCache(PluginSettingsFactory pluginSettingsFactory, NotificationFactory notificationFactory, ClusterLockService lockService) {
        this.pluginSettingsFactory = Objects.requireNonNull(pluginSettingsFactory, "pluginSettingsFactory");
        this.notificationFactory = Objects.requireNonNull(notificationFactory, "notificationFactory");
        this.lock = Locks.getLock(Objects.requireNonNull(lockService, "lockService"), this.getClass());
    }

    private PluginSettings getPluginSettings() {
        return new NamespacedPluginSettings(this.pluginSettingsFactory.createGlobalSettings(), KEY_PREFIX);
    }

    @Override
    public Option<Notification> getNotification(NotificationType type, Option<UserKey> userKey, String pluginKey) {
        for (Notification notification : this.getNotifications(type, userKey, false)) {
            if (!notification.getPluginKey().equals(pluginKey)) continue;
            return Option.some(notification);
        }
        return Option.none(Notification.class);
    }

    @Override
    public List<NotificationCollection> getNotifications() {
        return this.getNotifications(Option.none(UserKey.class), false);
    }

    @Override
    public List<NotificationCollection> getNotifications(Option<UserKey> userKey, boolean hideDismissed) {
        return Locks.readWithLock(this.lock, () -> {
            ArrayList<NotificationCollection> collections = new ArrayList<NotificationCollection>();
            for (NotificationType type : NotificationType.values()) {
                NotificationCollection notifications = this.getNotificationsWithType(type, userKey, hideDismissed);
                if (notifications.getNotificationCount() <= 0) continue;
                collections.add(notifications);
            }
            return Collections.unmodifiableList(collections);
        });
    }

    @Override
    public NotificationCollection getNotifications(NotificationType type, Option<UserKey> userKey, boolean hideDismissed) {
        return Locks.readWithLock(this.lock, () -> this.getNotificationsWithType(type, userKey, hideDismissed));
    }

    @Override
    public boolean isNotificationTypeDismissed(NotificationType type, Option<UserKey> userKey) {
        return Locks.readWithLock(this.lock, () -> this.getStoredNotificationDismissedValue(PluginSettingsNotificationCache.getDismissalKey(type), userKey));
    }

    @Override
    public boolean isNotificationDismissed(NotificationType type, Option<UserKey> userKey, String pluginKey) {
        return Locks.readWithLock(this.lock, () -> this.getStoredNotificationDismissedValue(PluginSettingsNotificationCache.getDismissalKey(type, Option.some(pluginKey)), userKey));
    }

    @Override
    public void setNotifications(NotificationType type, Collection<String> pluginKeys) {
        Locks.writeWithLock(this.lock, () -> this.saveNotifications(type, pluginKeys));
    }

    @Override
    public void addNotificationForPlugin(NotificationType type, String pluginKey) {
        Locks.writeWithLock(this.lock, () -> {
            NotificationCollection previousNotifications = this.getNotificationsWithType(type, Option.none(UserKey.class), false);
            List<String> previousPluginKeys = StreamSupport.stream(previousNotifications.spliterator(), false).map(Notification.toNotificationPluginKey()).collect(Collectors.toList());
            if (previousPluginKeys.contains(pluginKey)) {
                this.removeDismissalWithType(type, Option.some(pluginKey));
            } else {
                previousPluginKeys.add(pluginKey);
                this.saveNotifications(type, previousPluginKeys);
            }
        });
    }

    @Override
    public void removeNotificationForPlugin(NotificationType type, String pluginKey) {
        Locks.writeWithLock(this.lock, () -> {
            NotificationCollection previousNotifications = this.getNotificationsWithType(type, Option.none(UserKey.class), false);
            List previousPluginKeys = StreamSupport.stream(previousNotifications.spliterator(), false).map(Notification.toNotificationPluginKey()).collect(Collectors.toList());
            if (previousPluginKeys.contains(pluginKey)) {
                this.saveNotifications(type, previousPluginKeys.stream().filter(o -> !pluginKey.equals(o)).collect(Collectors.toList()));
            }
        });
    }

    @Override
    public void setNotificationCount(NotificationType type, int count) {
        Locks.writeWithLock(this.lock, () -> {
            NotificationCollection previousNotifications = this.getNotificationsWithType(type, Option.none(UserKey.class), false);
            this.getPluginSettings().put(PluginSettingsNotificationCache.getNotificationKey(type), (Object)String.valueOf(count));
            this.removeAllDismissalsForNotificationType(type, previousNotifications);
        });
    }

    @Override
    public void setNotificationTypeDismissal(NotificationType type, UserKey userKey, boolean dismissed) {
        Locks.writeWithLock(this.lock, () -> {
            this.dismissNotification(type, Option.none(String.class), userKey, dismissed);
            if (!type.isAlwaysDisplayedIndividually()) {
                for (Notification notification : this.getNotificationsWithType(type, Option.some(userKey), false)) {
                    this.dismissNotification(type, Option.some(notification.getPluginKey()), userKey, dismissed);
                }
            }
        });
    }

    @Override
    public void setNotificationDismissal(NotificationType type, UserKey userKey, String pluginKey, boolean dismissed) {
        Locks.writeWithLock(this.lock, () -> this.dismissNotification(type, Option.some(pluginKey), userKey, dismissed));
    }

    @Override
    public void resetNotificationTypeDismissal(NotificationType type) {
        Locks.writeWithLock(this.lock, () -> this.removeDismissalWithType(type, Option.none(String.class)));
    }

    @Override
    public void resetNotificationDismissal(NotificationType type, String pluginKey) {
        Locks.writeWithLock(this.lock, () -> this.removeDismissalWithType(type, Option.some(pluginKey)));
    }

    private NotificationCollection getNotificationsWithType(NotificationType type, Option<UserKey> userKey, boolean hideDismissed) {
        boolean typeDismissed = this.getStoredNotificationDismissedValue(PluginSettingsNotificationCache.getDismissalKey(type), userKey);
        Object storedValue = this.getPluginSettings().get(PluginSettingsNotificationCache.getNotificationKey(type));
        if (typeDismissed && hideDismissed) {
            return this.notificationFactory.getNotifications(type, Collections.emptyList(), typeDismissed);
        }
        if (storedValue instanceof List) {
            List pluginKeys = (List)storedValue;
            if (hideDismissed) {
                pluginKeys = pluginKeys.stream().filter(this.getNotificationDismissalPredicate(type, userKey).negate()).collect(Collectors.toList());
            }
            List<Pair<String, Boolean>> pluginDismissalPairs = pluginKeys.stream().map(this.getPluginDismissalPairs(type, userKey, typeDismissed)).collect(Collectors.toList());
            return this.notificationFactory.getNotifications(type, pluginDismissalPairs, typeDismissed);
        }
        if (storedValue instanceof String) {
            try {
                int count = Integer.parseInt((String)storedValue);
                return this.notificationFactory.getNotifications(type, count, typeDismissed);
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        return this.notificationFactory.getNotifications(type, Collections.emptyList(), typeDismissed);
    }

    private void saveNotifications(NotificationType type, Collection<String> pluginKeys) {
        NotificationCollection previousNotifications = this.getNotificationsWithType(type, Option.none(UserKey.class), false);
        this.getPluginSettings().put(PluginSettingsNotificationCache.getNotificationKey(type), new ArrayList<String>(pluginKeys));
        List previousPluginKeys = StreamSupport.stream(previousNotifications.spliterator(), false).map(Notification.toNotificationPluginKey()).collect(Collectors.toList());
        boolean resetTypeDismissed = false;
        for (String pluginKey : pluginKeys) {
            if (previousPluginKeys.contains(pluginKey)) continue;
            resetTypeDismissed = true;
            break;
        }
        for (String previousPluginKey : previousPluginKeys) {
            if (pluginKeys.contains(previousPluginKey)) continue;
            this.removeDismissalWithType(type, Option.some(previousPluginKey));
        }
        if (resetTypeDismissed) {
            this.removeAllDismissalsForNotificationType(type, previousNotifications);
        }
    }

    private boolean getStoredNotificationDismissedValue(String pluginSettingsKey, Option<UserKey> userKey) {
        Iterator<UserKey> iterator = userKey.iterator();
        if (iterator.hasNext()) {
            UserKey uk = iterator.next();
            Object storedValue = this.getPluginSettings().get(pluginSettingsKey);
            if (storedValue instanceof List) {
                return ((List)storedValue).contains(uk.getStringValue());
            }
            return false;
        }
        return false;
    }

    private void removeDismissalWithType(NotificationType type, Option<String> pluginKey) {
        this.getPluginSettings().remove(PluginSettingsNotificationCache.getDismissalKey(type, pluginKey));
    }

    private void removeAllDismissalsForNotificationType(NotificationType type, Iterable<Notification> notifications) {
        this.removeDismissalWithType(type, Option.none(String.class));
        for (Notification previousNotification : notifications) {
            this.removeDismissalWithType(type, Option.some(previousNotification.getPluginKey()));
        }
    }

    private void dismissNotification(NotificationType type, Option<String> pluginKey, UserKey userKey, boolean dismissed) {
        List<String> userKeys;
        String pluginSettingsKey = PluginSettingsNotificationCache.getDismissalKey(type, pluginKey);
        Object storedValue = this.getPluginSettings().get(pluginSettingsKey);
        List list = userKeys = storedValue == null ? new ArrayList() : (List)storedValue;
        if (dismissed && !userKeys.contains(userKey.getStringValue())) {
            userKeys.add(userKey.getStringValue());
            this.getPluginSettings().put(pluginSettingsKey, userKeys);
        } else if (!dismissed && userKeys.contains(userKey.getStringValue())) {
            userKeys.remove(userKey.getStringValue());
            if (userKeys.isEmpty()) {
                this.getPluginSettings().remove(pluginSettingsKey);
            } else {
                this.getPluginSettings().put(pluginSettingsKey, userKeys);
            }
        }
    }

    private Function<String, Pair<String, Boolean>> getPluginDismissalPairs(NotificationType type, Option<UserKey> userKey, boolean typeDismissed) {
        Objects.requireNonNull(type, "type");
        return pluginKey -> {
            boolean notificationDismissed = this.getStoredNotificationDismissedValue(PluginSettingsNotificationCache.getDismissalKey(type, Option.some(pluginKey)), userKey);
            return Pair.pair(pluginKey, notificationDismissed || typeDismissed);
        };
    }

    private Predicate<String> getNotificationDismissalPredicate(NotificationType type, Option<UserKey> userKey) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(userKey, "userKey");
        return pluginKey -> this.getStoredNotificationDismissedValue(PluginSettingsNotificationCache.getDismissalKey(type, Option.some(pluginKey)), userKey);
    }

    public static String getNotificationKey(NotificationType type) {
        return KEY_NOTIFICATION_PREFIX + type.getKey();
    }

    public static String getDismissalKey(NotificationType type) {
        return PluginSettingsNotificationCache.getDismissalKey(type, Option.none(String.class));
    }

    public static String getDismissalKey(NotificationType type, Option<String> pluginKey) {
        Iterator<String> iterator = pluginKey.iterator();
        if (iterator.hasNext()) {
            String pk = iterator.next();
            return KEY_DISMISSAL_PREFIX + type.getKey() + ":" + pk;
        }
        return KEY_DISMISSAL_PREFIX + type.getKey();
    }
}


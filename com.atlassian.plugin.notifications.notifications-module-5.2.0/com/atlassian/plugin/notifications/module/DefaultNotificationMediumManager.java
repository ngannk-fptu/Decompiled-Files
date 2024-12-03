/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.tracker.DefaultPluginModuleTracker
 *  com.atlassian.plugin.tracker.PluginModuleTracker
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 */
package com.atlassian.plugin.notifications.module;

import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.notifications.api.medium.NotificationMedium;
import com.atlassian.plugin.notifications.module.NotificationMediumManager;
import com.atlassian.plugin.notifications.module.NotificationMediumModuleDescriptor;
import com.atlassian.plugin.tracker.DefaultPluginModuleTracker;
import com.atlassian.plugin.tracker.PluginModuleTracker;
import com.atlassian.sal.api.message.I18nResolver;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import java.util.Set;
import javax.annotation.Nullable;

public class DefaultNotificationMediumManager
implements NotificationMediumManager {
    private final PluginModuleTracker<NotificationMedium, NotificationMediumModuleDescriptor> mediumTracker;

    public DefaultNotificationMediumManager(PluginAccessor pluginAccessor, PluginEventManager pluginEventManager) {
        this.mediumTracker = DefaultPluginModuleTracker.create((PluginAccessor)pluginAccessor, (PluginEventManager)pluginEventManager, NotificationMediumModuleDescriptor.class);
    }

    @Override
    public NotificationMedium getNotificationMedium(final String key) {
        return (NotificationMedium)Iterables.find((Iterable)this.mediumTracker.getModules(), (Predicate)new Predicate<NotificationMedium>(){

            public boolean apply(@Nullable NotificationMedium notificationMedium) {
                return notificationMedium != null && notificationMedium.getKey().equals(key);
            }
        }, null);
    }

    @Override
    public NotificationMediumModuleDescriptor getNotificationMediumModuleDescriptor(final String key) {
        Iterable moduleDescriptors = this.mediumTracker.getModuleDescriptors();
        return (NotificationMediumModuleDescriptor)((Object)Iterables.find((Iterable)moduleDescriptors, (Predicate)new Predicate<NotificationMediumModuleDescriptor>(){

            public boolean apply(@Nullable NotificationMediumModuleDescriptor input) {
                return input != null && input.getKey().equals(key);
            }
        }, null));
    }

    @Override
    public String getI18nizedMediumName(I18nResolver i18n, String key) {
        NotificationMediumModuleDescriptor descriptor = this.getNotificationMediumModuleDescriptor(key);
        if (descriptor == null) {
            return key;
        }
        if (descriptor.getI18nNameKey() != null) {
            return i18n.getText(descriptor.getI18nNameKey());
        }
        if (descriptor.getName() != null) {
            return descriptor.getName();
        }
        return key;
    }

    @Override
    public Set<NotificationMedium> getNotificationMediums() {
        return Sets.newLinkedHashSet((Iterable)this.mediumTracker.getModules());
    }
}


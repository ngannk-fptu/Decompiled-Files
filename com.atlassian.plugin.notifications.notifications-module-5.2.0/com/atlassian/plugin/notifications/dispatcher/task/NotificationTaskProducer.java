/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.tracker.PluginModuleTracker
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Iterables
 */
package com.atlassian.plugin.notifications.dispatcher.task;

import com.atlassian.plugin.notifications.api.NotificationHandler;
import com.atlassian.plugin.notifications.dispatcher.NotificationHandlerModuleDescriptor;
import com.atlassian.plugin.notifications.dispatcher.TaskComponents;
import com.atlassian.plugin.notifications.dispatcher.util.MatchingHandlerPredicate;
import com.atlassian.plugin.tracker.PluginModuleTracker;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

public class NotificationTaskProducer
implements Runnable {
    private final TaskComponents components;
    private final Object event;

    public NotificationTaskProducer(TaskComponents components, Object event) {
        this.components = components;
        this.event = event;
    }

    @Override
    public void run() {
        PluginModuleTracker<NotificationHandler, NotificationHandlerModuleDescriptor> handlerTracker = this.components.getHandlerTracker();
        Iterable handlers = Iterables.filter((Iterable)handlerTracker.getModuleDescriptors(), (Predicate)new MatchingHandlerPredicate(this.event));
        for (NotificationHandlerModuleDescriptor handler : handlers) {
            NotificationHandler module = handler.getModule();
            if (module == null) continue;
            try {
                module.handle(this.event);
            }
            catch (RuntimeException e) {
                this.components.getErrorRegistry().getLogger().error((Object)("Error producing notification task with handler '" + handler.getCompleteKey() + "'."), (Throwable)e);
            }
        }
    }
}


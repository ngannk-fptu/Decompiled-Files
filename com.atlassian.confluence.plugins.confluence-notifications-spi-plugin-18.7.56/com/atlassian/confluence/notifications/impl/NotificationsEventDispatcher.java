/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.lifecycle.events.ApplicationStartedEvent
 *  com.atlassian.confluence.api.model.event.notification.NotificationEnabledEvent
 *  com.atlassian.confluence.logging.ErrorLoggedEvent
 *  com.atlassian.confluence.mail.notification.listeners.NotificationApiDarkFeature
 *  com.atlassian.confluence.server.ApplicationState
 *  com.atlassian.confluence.server.ApplicationStatusService
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.tracker.DefaultPluginModuleTracker
 *  com.atlassian.plugin.tracker.PluginModuleTracker
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.confluence.notifications.impl;

import com.atlassian.config.lifecycle.events.ApplicationStartedEvent;
import com.atlassian.confluence.api.model.event.notification.NotificationEnabledEvent;
import com.atlassian.confluence.logging.ErrorLoggedEvent;
import com.atlassian.confluence.mail.notification.listeners.NotificationApiDarkFeature;
import com.atlassian.confluence.notifications.DispatchService;
import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.NotificationPayload;
import com.atlassian.confluence.notifications.PayloadProcessor;
import com.atlassian.confluence.notifications.PayloadTransformer;
import com.atlassian.confluence.notifications.ProductionAwareLoggerSwitch;
import com.atlassian.confluence.notifications.impl.NotificationDescriptorLocator;
import com.atlassian.confluence.notifications.impl.descriptors.NotificationDescriptor;
import com.atlassian.confluence.notifications.impl.descriptors.NotificationTransformerDescriptor;
import com.atlassian.confluence.notifications.impl.descriptors.PayloadProcessorDescriptor;
import com.atlassian.confluence.server.ApplicationState;
import com.atlassian.confluence.server.ApplicationStatusService;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.tracker.DefaultPluginModuleTracker;
import com.atlassian.plugin.tracker.PluginModuleTracker;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class NotificationsEventDispatcher
implements InitializingBean,
DisposableBean {
    private static final ProductionAwareLoggerSwitch log = ProductionAwareLoggerSwitch.forCaller();
    private final EventPublisher eventPublisher;
    private final DispatchService dispatchService;
    private final NotificationDescriptorLocator notificationDescriptorLocator;
    private final ApplicationStatusService applicationStatusService;
    private final AtomicBoolean applicationStarted = new AtomicBoolean(false);
    private final PluginModuleTracker<PayloadProcessor, PayloadProcessorDescriptor> payloadProcessorTracker;

    public NotificationsEventDispatcher(EventPublisher eventPublisher, DispatchService dispatchService, NotificationDescriptorLocator notificationDescriptorLocator, PluginAccessor pluginAccessor, PluginEventManager pluginEventManager, ApplicationStatusService applicationStatusService) {
        this.eventPublisher = eventPublisher;
        this.dispatchService = dispatchService;
        this.notificationDescriptorLocator = notificationDescriptorLocator;
        this.applicationStatusService = applicationStatusService;
        this.payloadProcessorTracker = pluginAccessor != null && pluginEventManager != null ? new DefaultPluginModuleTracker(pluginAccessor, pluginEventManager, PayloadProcessorDescriptor.class) : null;
    }

    public void afterPropertiesSet() throws Exception {
        this.eventPublisher.register((Object)this);
        if (ApplicationState.RUNNING.equals((Object)this.applicationStatusService.getState())) {
            this.applicationStarted.set(true);
        }
    }

    public void destroy() throws Exception {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void handleApplicationStartedEvent(ApplicationStartedEvent event) {
        this.applicationStarted.set(true);
    }

    @EventListener
    public void handleEvent(Object event) {
        try {
            this.handleEventInternal(event);
        }
        catch (Throwable e) {
            log.errorOrDebug(e, "Error during notification dispatch : %s", e.getMessage());
        }
    }

    private void handleEventInternal(Object event) {
        ModuleCompleteKey notificationDescriptorKey;
        if (!this.applicationStarted.get() || this.shouldIgnoreEvent(event)) {
            return;
        }
        Iterable<NotificationTransformerDescriptor> transformerDescriptors = this.notificationDescriptorLocator.findPayloadTransformerDescriptors(event);
        PayloadTransformer payloadTransformer = null;
        Option maybePayload = null;
        NotificationTransformerDescriptor notificationTransformerDescriptor = null;
        Iterator<NotificationTransformerDescriptor> iterator = transformerDescriptors.iterator();
        while (iterator.hasNext()) {
            NotificationTransformerDescriptor transformerDescriptor;
            notificationTransformerDescriptor = transformerDescriptor = iterator.next();
            payloadTransformer = (PayloadTransformer)transformerDescriptor.getModule();
            maybePayload = payloadTransformer.create(event);
            if (!maybePayload.isDefined()) continue;
            boolean skip = this.payloadProcessing((NotificationPayload)maybePayload.get(), payloadTransformer, notificationTransformerDescriptor.keyForNotificationDescriptor());
            if (!skip) break;
            maybePayload = Option.none();
            break;
        }
        if (payloadTransformer == null) {
            log.onlyTrace("Unable to find a [%s] for event [%s], skipping further processing.", NotificationTransformerDescriptor.class.getName(), event.getClass().getName());
            return;
        }
        if (maybePayload.isEmpty()) {
            log.warnOrDebug("Transformer factories did not return a notification payload of [%s], aborting.", event.getClass().getName());
            return;
        }
        NotificationPayload payload = (NotificationPayload)maybePayload.get();
        Maybe<NotificationDescriptor<NotificationPayload>> maybeNotificationModuleDescriptor = this.notificationDescriptorLocator.findNotificationDescriptor(payload, notificationDescriptorKey = notificationTransformerDescriptor.keyForNotificationDescriptor());
        if (maybeNotificationModuleDescriptor.isEmpty()) {
            log.errorOrDebug(maybeNotificationModuleDescriptor, "Could not find a [%s] for payload type [%s].", NotificationDescriptor.class.getName(), payload.getClass().getName());
            return;
        }
        Notification<NotificationPayload> notification = ((NotificationDescriptor)((Object)maybeNotificationModuleDescriptor.get())).getNotificationFactory().create(payload);
        this.dispatchService.dispatch(notification);
    }

    private boolean payloadProcessing(NotificationPayload payload, PayloadTransformer payloadTransformer, ModuleCompleteKey forNotificationKey) {
        boolean skip = false;
        for (PayloadProcessor payloadProcessor : this.payloadProcessorTracker.getModules()) {
            if (!payloadProcessor.process(payload, payloadTransformer, forNotificationKey)) continue;
            skip = true;
        }
        return skip;
    }

    private boolean shouldIgnoreEvent(Object event) {
        if (event instanceof NotificationEnabledEvent) {
            return ((NotificationEnabledEvent)event).isSuppressNotifications();
        }
        return event instanceof ErrorLoggedEvent || !NotificationApiDarkFeature.NotificationPluginApi.isEnabled(event);
    }
}


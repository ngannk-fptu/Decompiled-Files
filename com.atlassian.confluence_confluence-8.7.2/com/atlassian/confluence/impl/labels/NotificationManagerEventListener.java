/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventListenerRegistrar
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 */
package com.atlassian.confluence.impl.labels;

import com.atlassian.confluence.event.events.content.Contented;
import com.atlassian.confluence.event.events.content.attachment.GeneralAttachmentVersionRemoveEvent;
import com.atlassian.confluence.event.events.types.Removed;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventListenerRegistrar;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public final class NotificationManagerEventListener {
    private final NotificationManager notificationManager;
    private final EventListenerRegistrar eventListenerRegistrar;

    NotificationManagerEventListener(NotificationManager notificationManager, EventListenerRegistrar eventListenerRegistrar) {
        this.notificationManager = notificationManager;
        this.eventListenerRegistrar = eventListenerRegistrar;
    }

    @PostConstruct
    void registerListeners() {
        this.eventListenerRegistrar.register((Object)this);
    }

    @PreDestroy
    void unregisterListeners() {
        this.eventListenerRegistrar.unregister((Object)this);
    }

    @EventListener
    public void onContentRemoved(Removed event) {
        if (event instanceof Contented && !(event instanceof GeneralAttachmentVersionRemoveEvent)) {
            this.notificationManager.getNotificationsByContent(((Contented)((Object)event)).getContent()).forEach(this.notificationManager::removeNotification);
        }
    }

    public static interface VisitorEvent {
        public void visit(LabelManager var1);
    }
}


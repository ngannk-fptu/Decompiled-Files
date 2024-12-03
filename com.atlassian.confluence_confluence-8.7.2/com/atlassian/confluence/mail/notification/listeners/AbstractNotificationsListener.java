/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.task.MultiQueueTaskManager
 *  com.atlassian.core.user.preferences.UserPreferences
 *  com.atlassian.event.Event
 *  com.atlassian.event.EventListener
 *  com.atlassian.user.User
 *  javax.activation.DataSource
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.mail.notification.listeners;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.Renderer;
import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.DataSourceFactory;
import com.atlassian.confluence.event.events.content.ContentEvent;
import com.atlassian.confluence.event.events.types.UserDriven;
import com.atlassian.confluence.mail.notification.Notification;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.mail.notification.NotificationsSender;
import com.atlassian.confluence.mail.notification.listeners.NotificationData;
import com.atlassian.confluence.plugin.descriptor.mail.NotificationContext;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.core.task.MultiQueueTaskManager;
import com.atlassian.core.user.preferences.UserPreferences;
import com.atlassian.event.Event;
import com.atlassian.event.EventListener;
import com.atlassian.user.User;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.activation.DataSource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public abstract class AbstractNotificationsListener<T extends Event>
implements EventListener {
    private static final Logger log = LoggerFactory.getLogger(AbstractNotificationsListener.class);
    protected UserAccessor userAccessor;
    protected MultiQueueTaskManager taskManager;
    protected NotificationManager notificationManager;
    protected Renderer viewRenderer;
    protected DataSourceFactory dataSourceFactory;
    protected NotificationsSender notificationsSender;

    public void handleEvent(Event event) {
        if (event instanceof ContentEvent && ((ContentEvent)event).isSuppressNotifications()) {
            return;
        }
        this.processNotifications(event);
    }

    protected abstract void processNotifications(T var1);

    protected final void sendNotification(Notification notification, NotificationData notificationData) {
        this.notificationsSender.sendNotification(notification, notificationData, this.getConversionContext(notificationData));
    }

    protected final void sendNotification(String recipient, NotificationContext context, NotificationData notificationData) {
        this.notificationsSender.sendNotification(recipient, context, notificationData, this.getConversionContext(notificationData));
    }

    protected void attachAvatar(NotificationData notificationData) {
        DataSource avatarDataSource = this.dataSourceFactory.getAvatar(notificationData.getModifier());
        notificationData.addToContext("avatarCid", (Serializable)((Object)avatarDataSource.getName()));
        notificationData.addTemplateImage(avatarDataSource);
    }

    protected abstract ContentEntityObject getContentEntityObject(Map var1);

    protected void sendNotifications(List<Notification> notifications, NotificationData notificationData) {
        this.notificationsSender.sendNotifications(notifications, notificationData, this.getConversionContext(notificationData));
    }

    protected final NotificationData getNotificationDataForEvent(Event event, ConfluenceEntityObject entity) {
        User user = this.getUserWhoTriggeredEvent(event, entity);
        NotificationData notificationData = new NotificationData(user, this.shouldNotifyOnOwnActions(user), entity);
        NotificationContext commonContext = notificationData.getCommonContext();
        if (event instanceof ContentEvent) {
            commonContext.setContent(((ContentEvent)event).getContent());
        }
        commonContext.setEvent(event);
        return notificationData;
    }

    private User getUserWhoTriggeredEvent(Event event, ConfluenceEntityObject entity) {
        if (event instanceof UserDriven) {
            return ((UserDriven)event).getOriginatingUser();
        }
        if (StringUtils.isNotEmpty((CharSequence)entity.getLastModifierName())) {
            return this.userAccessor.getUserByName(entity.getLastModifierName());
        }
        return null;
    }

    protected ConversionContext getConversionContext(NotificationData notificationData) {
        ContentEntityObject notificationObject = this.getContentEntityObject(notificationData.getCommonContext().getMap());
        if (notificationObject == null) {
            log.debug("ContentEntityObject not found in notificationData context, returning null RenderContext");
            return null;
        }
        PageContext context = notificationObject.toPageContext();
        context.setOutputType("email");
        return new DefaultConversionContext(context);
    }

    private boolean shouldNotifyOnOwnActions(User user) {
        UserPreferences preferences = new UserPreferences(this.userAccessor.getPropertySet(user));
        return preferences.getBoolean("confluence.prefs.notify.for.my.own.actions");
    }

    public void setNotificationManager(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    public void setUserAccessor(UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }

    public void setTaskManager(MultiQueueTaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public void setViewRenderer(Renderer renderer) {
        this.viewRenderer = renderer;
    }

    public void setDataSourceFactory(DataSourceFactory dataSourceFactory) {
        this.dataSourceFactory = dataSourceFactory;
    }

    public void setNotificationsSender(NotificationsSender notificationsSender) {
        this.notificationsSender = notificationsSender;
    }
}


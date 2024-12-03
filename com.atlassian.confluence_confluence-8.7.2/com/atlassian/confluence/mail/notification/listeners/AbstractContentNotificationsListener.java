/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.user.preferences.UserPreferences
 *  com.atlassian.user.User
 *  javax.activation.DataSource
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.mail.notification.listeners;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.DataSourceFactory;
import com.atlassian.confluence.event.events.content.ContentEvent;
import com.atlassian.confluence.event.events.types.UserDriven;
import com.atlassian.confluence.mail.notification.NotificationsSender;
import com.atlassian.confluence.mail.notification.listeners.NotificationData;
import com.atlassian.confluence.plugin.descriptor.mail.NotificationContext;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.core.user.preferences.UserPreferences;
import com.atlassian.user.User;
import java.io.Serializable;
import java.util.Map;
import javax.activation.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public abstract class AbstractContentNotificationsListener {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    protected final DataSourceFactory dataSourceFactory;
    protected final NotificationsSender notificationsSender;
    private final UserAccessor userAccessor;

    AbstractContentNotificationsListener(DataSourceFactory dataSourceFactory, NotificationsSender notificationsSender, UserAccessor userAccessor) {
        this.dataSourceFactory = dataSourceFactory;
        this.notificationsSender = notificationsSender;
        this.userAccessor = userAccessor;
    }

    <T extends ContentEvent> NotificationData getNotificationDataForEvent(T event, ConfluenceEntityObject entity) {
        User user = ((UserDriven)((Object)event)).getOriginatingUser();
        NotificationData notificationData = new NotificationData(user, this.shouldNotifyOnOwnActions(user), entity);
        NotificationContext commonContext = notificationData.getCommonContext();
        commonContext.setContent(event.getContent());
        commonContext.setEvent(event);
        return notificationData;
    }

    private boolean shouldNotifyOnOwnActions(User user) {
        UserPreferences preferences = new UserPreferences(this.userAccessor.getPropertySet(user));
        return preferences.getBoolean("confluence.prefs.notify.for.my.own.actions");
    }

    void attachAvatar(NotificationData notificationData) {
        DataSource avatarDataSource = this.dataSourceFactory.getAvatar(notificationData.getModifier());
        notificationData.addToContext("avatarCid", (Serializable)((Object)avatarDataSource.getName()));
        notificationData.addTemplateImage(avatarDataSource);
    }

    protected ConversionContext getConversionContext(NotificationData notificationData) {
        ContentEntityObject notificationObject = this.getContentEntityObject(notificationData.getCommonContext().getMap());
        if (notificationObject == null) {
            this.log.debug("ContentEntityObject not found in notificationData context, returning null RenderContext");
            return null;
        }
        PageContext context = notificationObject.toPageContext();
        context.setOutputType("email");
        return new DefaultConversionContext(context);
    }

    protected abstract ContentEntityObject getContentEntityObject(Map var1);
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 */
package com.atlassian.confluence.mail.notification.listeners;

import com.atlassian.confluence.content.ContentProperties;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Renderer;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.DataSourceFactory;
import com.atlassian.confluence.event.events.content.comment.CommentRemoveEvent;
import com.atlassian.confluence.mail.notification.NotificationsSender;
import com.atlassian.confluence.mail.notification.listeners.AbstractContentNotificationsListener;
import com.atlassian.confluence.mail.notification.listeners.NotificationData;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.event.api.EventListener;
import java.io.Serializable;
import java.util.Map;

@Deprecated
public class CommentNotificationsListener
extends AbstractContentNotificationsListener {
    private final Renderer viewRenderer;

    CommentNotificationsListener(DataSourceFactory dataSourceFactory, NotificationsSender notificationsSender, UserAccessor userAccessor, Renderer viewRenderer) {
        super(dataSourceFactory, notificationsSender, userAccessor);
        this.viewRenderer = viewRenderer;
    }

    @EventListener
    public void handleCommentRemove(CommentRemoveEvent commentEvent) {
        Comment comment = commentEvent.getComment();
        ContentEntityObject ceo = comment.getContainer();
        if (!(ceo instanceof AbstractPage)) {
            return;
        }
        AbstractPage entity = (AbstractPage)ceo;
        Space space = entity.getSpace();
        NotificationData notificationData = this.getNotificationDataForEvent(commentEvent, comment);
        notificationData.addToContext("page", entity);
        notificationData.addToContext("space", space);
        notificationData.addToContext("comment", comment);
        notificationData.addToContext("content", comment);
        notificationData.addToContext("contentId", Long.valueOf(comment.getId()));
        notificationData.addToContext("enableEmailReply", Boolean.valueOf(true));
        notificationData.addToContext("inlineContext", (Serializable)((Object)this.getInlineContext(comment)));
        notificationData.addToContext("contentType", (Serializable)((Object)"comment"));
        notificationData.addToContext("commentHtml", (Serializable)((Object)this.viewRenderer.render(comment, this.getConversionContext(notificationData))));
        notificationData.setSubject("$space.name > $page.title");
        notificationData.addToContext("remover", (Serializable)commentEvent.getOriginatingUser());
        notificationData.setTemplateName("Confluence.Templates.Mail.Notifications.commentRemove.soy");
        notificationData.addTemplateImage(this.dataSourceFactory.getServletContainerResource("/images/icons/contenttypes/comment_16.png", "comment-icon"));
        this.attachAvatar(notificationData);
        ConversionContext conversionContext = this.getConversionContext(notificationData);
        this.notificationsSender.sendSpaceNotifications(space, notificationData, conversionContext);
        this.notificationsSender.sendPageNotifications(entity, notificationData, conversionContext);
        this.notificationsSender.sendNetworkNotifications(notificationData, conversionContext);
    }

    @Override
    protected final ContentEntityObject getContentEntityObject(Map contextMap) {
        return (ContentEntityObject)contextMap.get("comment");
    }

    private String getInlineContext(Comment comment) {
        ContentProperties properties = comment.getProperties();
        return properties.getStringProperty("inline-original-selection");
    }
}


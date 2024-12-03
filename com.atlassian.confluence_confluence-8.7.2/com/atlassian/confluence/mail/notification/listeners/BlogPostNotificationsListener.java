/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 */
package com.atlassian.confluence.mail.notification.listeners;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.DataSourceFactory;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostTrashedEvent;
import com.atlassian.confluence.mail.notification.NotificationsSender;
import com.atlassian.confluence.mail.notification.listeners.AbstractContentNotificationsListener;
import com.atlassian.confluence.mail.notification.listeners.NotificationData;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.event.api.EventListener;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Map;

@Deprecated
public class BlogPostNotificationsListener
extends AbstractContentNotificationsListener {
    BlogPostNotificationsListener(DataSourceFactory dataSourceFactory, NotificationsSender notificationsSender, UserAccessor userAccessor) {
        super(dataSourceFactory, notificationsSender, userAccessor);
    }

    @EventListener
    public void handleBlogPostEvent(BlogPostTrashedEvent blogEvent) {
        BlogPost blog = blogEvent.getBlogPost();
        Space space = blog.getSpace();
        NotificationData notificationData = this.getNotificationDataForEvent(blogEvent, blog, space);
        LinkedHashSet<Space> spacesToBeNotified = new LinkedHashSet<Space>();
        spacesToBeNotified.add(space);
        notificationData.setTemplateName("Confluence.Templates.Mail.Notifications.blogRemove.soy");
        notificationData.addTemplateImage(this.dataSourceFactory.getServletContainerResource("/images/icons/contenttypes/blog_post_16.png", "blogpost-icon"));
        this.attachAvatar(notificationData);
        ConversionContext conversionContext = this.getConversionContext(notificationData);
        for (Space spaceToBeNotified : spacesToBeNotified) {
            this.notificationsSender.sendSpaceNotifications(spaceToBeNotified, notificationData, conversionContext);
        }
        this.notificationsSender.sendPageNotifications(blog, notificationData, conversionContext);
        this.notificationsSender.sendNetworkNotifications(notificationData, conversionContext);
    }

    private NotificationData getNotificationDataForEvent(BlogPostTrashedEvent blogEvent, BlogPost blog, Space space) {
        NotificationData notificationData = this.getNotificationDataForEvent(blogEvent, blog);
        notificationData.addToContext("content", blog);
        notificationData.addToContext("page", blog);
        notificationData.addToContext("contentType", (Serializable)((Object)"blogpost"));
        notificationData.addToContext("space", space);
        notificationData.addToContext("contentId", Long.valueOf(blog.getId()));
        notificationData.addToContext("enableEmailReply", Boolean.valueOf(true));
        notificationData.setSubject("$space.name > $content.title");
        return notificationData;
    }

    @Override
    protected final ContentEntityObject getContentEntityObject(Map contextMap) {
        return (ContentEntityObject)contextMap.get("content");
    }
}


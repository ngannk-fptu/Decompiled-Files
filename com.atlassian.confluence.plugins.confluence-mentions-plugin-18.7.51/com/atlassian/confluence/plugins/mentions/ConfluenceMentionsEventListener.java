/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.CustomContentEntityObject
 *  com.atlassian.confluence.content.event.PluginContentCreatedEvent
 *  com.atlassian.confluence.content.event.PluginContentUpdatedEvent
 *  com.atlassian.confluence.core.BodyType
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.event.events.content.blogpost.BlogPostCreateEvent
 *  com.atlassian.confluence.event.events.content.blogpost.BlogPostUpdateEvent
 *  com.atlassian.confluence.event.events.content.comment.CommentCreateEvent
 *  com.atlassian.confluence.event.events.content.comment.CommentUpdateEvent
 *  com.atlassian.confluence.event.events.content.page.PageCreateEvent
 *  com.atlassian.confluence.event.events.content.page.PageUpdateEvent
 *  com.atlassian.confluence.pages.PageUpdateTrigger
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.security.access.ConfluenceAccessManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.user.User
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 */
package com.atlassian.confluence.plugins.mentions;

import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.content.event.PluginContentCreatedEvent;
import com.atlassian.confluence.content.event.PluginContentUpdatedEvent;
import com.atlassian.confluence.core.BodyType;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostCreateEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostUpdateEvent;
import com.atlassian.confluence.event.events.content.comment.CommentCreateEvent;
import com.atlassian.confluence.event.events.content.comment.CommentUpdateEvent;
import com.atlassian.confluence.event.events.content.page.PageCreateEvent;
import com.atlassian.confluence.event.events.content.page.PageUpdateEvent;
import com.atlassian.confluence.pages.PageUpdateTrigger;
import com.atlassian.confluence.plugins.mentions.NotificationService;
import com.atlassian.confluence.plugins.mentions.api.MentionFinder;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.access.ConfluenceAccessManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.user.User;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class ConfluenceMentionsEventListener {
    private final EventPublisher eventPublisher;
    private final UserAccessor userAccessor;
    private final PermissionManager permissionManager;
    private final ConfluenceAccessManager accessManager;
    private final MentionFinder mentionFinder;
    private final NotificationService notificationService;

    public ConfluenceMentionsEventListener(EventPublisher eventPublisher, UserAccessor userAccessor, PermissionManager permissionManager, ConfluenceAccessManager accessManager, MentionFinder mentionFinder, NotificationService notificationService) {
        this.eventPublisher = eventPublisher;
        this.userAccessor = userAccessor;
        this.permissionManager = permissionManager;
        this.accessManager = accessManager;
        this.mentionFinder = mentionFinder;
        this.notificationService = notificationService;
    }

    @PostConstruct
    public void afterPropertiesSet() {
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public void destroy() {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void commentCreated(CommentCreateEvent event) {
        this.sendNotificationsForNewContent((ContentEntityObject)event.getComment());
    }

    @EventListener
    public void commentUpdated(CommentUpdateEvent event) {
        this.sendNotificationsForUpdatedContent((ContentEntityObject)event.getOriginalComment(), (ContentEntityObject)event.getComment());
    }

    @EventListener
    public void pageCreated(PageCreateEvent event) {
        if (event.isSuppressNotifications() && event.getUpdateTrigger() == PageUpdateTrigger.LINK_REFACTORING) {
            return;
        }
        this.sendNotificationsForNewContent((ContentEntityObject)event.getPage());
    }

    @EventListener
    public void pageUpdated(PageUpdateEvent event) {
        this.sendNotificationsForUpdatedContent((ContentEntityObject)event.getOriginalPage(), (ContentEntityObject)event.getPage());
    }

    @EventListener
    public void blogPostCreated(BlogPostCreateEvent event) {
        this.sendNotificationsForNewContent((ContentEntityObject)event.getBlogPost());
    }

    @EventListener
    public void blogPostUpdated(BlogPostUpdateEvent event) {
        this.sendNotificationsForUpdatedContent((ContentEntityObject)event.getOriginalBlogPost(), (ContentEntityObject)event.getBlogPost());
    }

    @EventListener
    public void pluginContentCreated(PluginContentCreatedEvent event) {
        CustomContentEntityObject content = event.getContent();
        if (content.getDefaultBodyType() == BodyType.XHTML) {
            this.sendNotificationsForNewContent((ContentEntityObject)content);
        }
    }

    @EventListener
    public void pluginContentUpdated(PluginContentUpdatedEvent event) {
        ContentEntityObject content = event.getContent();
        if (content.getDefaultBodyType() == BodyType.XHTML) {
            this.sendNotificationsForUpdatedContent((ContentEntityObject)event.getOld(), (ContentEntityObject)event.getNew());
        }
    }

    private void sendNotificationsForNewContent(ContentEntityObject content) {
        Set<String> mentionedUserNames = this.mentionFinder.getMentionedUsernames(content.getBodyContent());
        this.sendContentNotifications(mentionedUserNames, content);
    }

    private void sendNotificationsForUpdatedContent(ContentEntityObject originalEntity, ContentEntityObject newEntity) {
        if (originalEntity != null) {
            Set<String> userNamesToNotify = this.mentionFinder.getNewMentionedUsernames(originalEntity.getBodyContent(), newEntity.getBodyContent());
            this.sendContentNotifications(userNamesToNotify, newEntity);
        }
    }

    private Set<ConfluenceUser> getUsersWithViewPermission(Set<String> userNames, ContentEntityObject content, ConfluenceUser author) {
        return userNames.stream().map(arg_0 -> ((UserAccessor)this.userAccessor).getUserByName(arg_0)).filter(user -> user != null && (author == null || !author.equals(user)) && this.accessManager.getUserAccessStatus((User)user).hasLicensedAccess() && this.permissionManager.hasPermissionNoExemptions((User)user, Permission.VIEW, (Object)content)).collect(Collectors.toSet());
    }

    private void sendContentNotifications(Set<String> mentionedUserNames, ContentEntityObject content) {
        ConfluenceUser contentAuthor = AuthenticatedUserThreadLocal.get();
        Set<ConfluenceUser> usersWithPermission = this.getUsersWithViewPermission(mentionedUserNames, content, contentAuthor);
        this.notificationService.sendMentions(usersWithPermission, contentAuthor, content);
    }
}


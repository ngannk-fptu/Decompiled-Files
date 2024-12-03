/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.ConfluenceEvent
 *  com.atlassian.confluence.event.events.content.ContentEvent
 *  com.atlassian.confluence.event.events.content.attachment.AttachmentCreateEvent
 *  com.atlassian.confluence.event.events.content.attachment.AttachmentEvent
 *  com.atlassian.confluence.event.events.content.attachment.AttachmentRemoveEvent
 *  com.atlassian.confluence.event.events.content.attachment.AttachmentTrashedEvent
 *  com.atlassian.confluence.event.events.content.attachment.AttachmentUpdateEvent
 *  com.atlassian.confluence.event.events.content.attachment.GeneralAttachmentRestoreEvent
 *  com.atlassian.confluence.event.events.content.blogpost.BlogPostCreateEvent
 *  com.atlassian.confluence.event.events.content.blogpost.BlogPostEvent
 *  com.atlassian.confluence.event.events.content.blogpost.BlogPostRemoveEvent
 *  com.atlassian.confluence.event.events.content.blogpost.BlogPostRestoreEvent
 *  com.atlassian.confluence.event.events.content.blogpost.BlogPostTrashedEvent
 *  com.atlassian.confluence.event.events.content.blogpost.BlogPostUpdateEvent
 *  com.atlassian.confluence.event.events.content.comment.CommentCreateEvent
 *  com.atlassian.confluence.event.events.content.comment.CommentEvent
 *  com.atlassian.confluence.event.events.content.comment.CommentRemoveEvent
 *  com.atlassian.confluence.event.events.content.comment.CommentUpdateEvent
 *  com.atlassian.confluence.event.events.content.page.PageChildrenReorderEvent
 *  com.atlassian.confluence.event.events.content.page.PageCreateEvent
 *  com.atlassian.confluence.event.events.content.page.PageEvent
 *  com.atlassian.confluence.event.events.content.page.PageMoveEvent
 *  com.atlassian.confluence.event.events.content.page.PageRemoveEvent
 *  com.atlassian.confluence.event.events.content.page.PageRestoreEvent
 *  com.atlassian.confluence.event.events.content.page.PageTrashedEvent
 *  com.atlassian.confluence.event.events.content.page.PageUpdateEvent
 *  com.atlassian.confluence.event.events.follow.FollowEvent
 *  com.atlassian.confluence.event.events.group.GroupCreateEvent
 *  com.atlassian.confluence.event.events.group.GroupEvent
 *  com.atlassian.confluence.event.events.group.GroupRemoveEvent
 *  com.atlassian.confluence.event.events.label.LabelAddEvent
 *  com.atlassian.confluence.event.events.label.LabelCreateEvent
 *  com.atlassian.confluence.event.events.label.LabelDeleteEvent
 *  com.atlassian.confluence.event.events.label.LabelEvent
 *  com.atlassian.confluence.event.events.label.LabelRemoveEvent
 *  com.atlassian.confluence.event.events.security.ContentPermissionEvent
 *  com.atlassian.confluence.event.events.space.SpaceCreateEvent
 *  com.atlassian.confluence.event.events.space.SpaceEvent
 *  com.atlassian.confluence.event.events.space.SpaceLogoUpdateEvent
 *  com.atlassian.confluence.event.events.space.SpacePermissionsUpdateEvent
 *  com.atlassian.confluence.event.events.space.SpaceRemoveEvent
 *  com.atlassian.confluence.event.events.space.SpaceUpdateEvent
 *  com.atlassian.confluence.event.events.types.Created
 *  com.atlassian.confluence.event.events.types.Restore
 *  com.atlassian.confluence.event.events.types.Trashed
 *  com.atlassian.confluence.event.events.types.Updated
 *  com.atlassian.confluence.event.events.user.UserCreateEvent
 *  com.atlassian.confluence.event.events.user.UserDeactivateEvent
 *  com.atlassian.confluence.event.events.user.UserEvent
 *  com.atlassian.confluence.event.events.user.UserReactivateEvent
 *  com.atlassian.confluence.event.events.user.UserRemoveEvent
 *  com.atlassian.confluence.plugins.createcontent.api.events.BlueprintPageCreateEvent
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.Spaced
 *  com.atlassian.confluence.themes.events.ThemeChangedEvent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.webhooks.WebhookEvent
 *  com.atlassian.webhooks.WebhookPublishRequest
 *  com.atlassian.webhooks.WebhookScope
 *  com.atlassian.webhooks.WebhookService
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.internal.webhooks;

import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.event.events.content.ContentEvent;
import com.atlassian.confluence.event.events.content.attachment.AttachmentCreateEvent;
import com.atlassian.confluence.event.events.content.attachment.AttachmentEvent;
import com.atlassian.confluence.event.events.content.attachment.AttachmentRemoveEvent;
import com.atlassian.confluence.event.events.content.attachment.AttachmentTrashedEvent;
import com.atlassian.confluence.event.events.content.attachment.AttachmentUpdateEvent;
import com.atlassian.confluence.event.events.content.attachment.GeneralAttachmentRestoreEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostCreateEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostRemoveEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostRestoreEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostTrashedEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostUpdateEvent;
import com.atlassian.confluence.event.events.content.comment.CommentCreateEvent;
import com.atlassian.confluence.event.events.content.comment.CommentEvent;
import com.atlassian.confluence.event.events.content.comment.CommentRemoveEvent;
import com.atlassian.confluence.event.events.content.comment.CommentUpdateEvent;
import com.atlassian.confluence.event.events.content.page.PageChildrenReorderEvent;
import com.atlassian.confluence.event.events.content.page.PageCreateEvent;
import com.atlassian.confluence.event.events.content.page.PageEvent;
import com.atlassian.confluence.event.events.content.page.PageMoveEvent;
import com.atlassian.confluence.event.events.content.page.PageRemoveEvent;
import com.atlassian.confluence.event.events.content.page.PageRestoreEvent;
import com.atlassian.confluence.event.events.content.page.PageTrashedEvent;
import com.atlassian.confluence.event.events.content.page.PageUpdateEvent;
import com.atlassian.confluence.event.events.follow.FollowEvent;
import com.atlassian.confluence.event.events.group.GroupCreateEvent;
import com.atlassian.confluence.event.events.group.GroupEvent;
import com.atlassian.confluence.event.events.group.GroupRemoveEvent;
import com.atlassian.confluence.event.events.label.LabelAddEvent;
import com.atlassian.confluence.event.events.label.LabelCreateEvent;
import com.atlassian.confluence.event.events.label.LabelDeleteEvent;
import com.atlassian.confluence.event.events.label.LabelEvent;
import com.atlassian.confluence.event.events.label.LabelRemoveEvent;
import com.atlassian.confluence.event.events.security.ContentPermissionEvent;
import com.atlassian.confluence.event.events.space.SpaceCreateEvent;
import com.atlassian.confluence.event.events.space.SpaceEvent;
import com.atlassian.confluence.event.events.space.SpaceLogoUpdateEvent;
import com.atlassian.confluence.event.events.space.SpacePermissionsUpdateEvent;
import com.atlassian.confluence.event.events.space.SpaceRemoveEvent;
import com.atlassian.confluence.event.events.space.SpaceUpdateEvent;
import com.atlassian.confluence.event.events.types.Created;
import com.atlassian.confluence.event.events.types.Restore;
import com.atlassian.confluence.event.events.types.Trashed;
import com.atlassian.confluence.event.events.types.Updated;
import com.atlassian.confluence.event.events.user.UserCreateEvent;
import com.atlassian.confluence.event.events.user.UserDeactivateEvent;
import com.atlassian.confluence.event.events.user.UserEvent;
import com.atlassian.confluence.event.events.user.UserReactivateEvent;
import com.atlassian.confluence.event.events.user.UserRemoveEvent;
import com.atlassian.confluence.internal.webhooks.ApplicationWebhookEvent;
import com.atlassian.confluence.internal.webhooks.SpaceScope;
import com.atlassian.confluence.internal.webhooks.WebhookJsonEventFactory;
import com.atlassian.confluence.plugins.createcontent.api.events.BlueprintPageCreateEvent;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.Spaced;
import com.atlassian.confluence.themes.events.ThemeChangedEvent;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.webhooks.WebhookEvent;
import com.atlassian.webhooks.WebhookPublishRequest;
import com.atlassian.webhooks.WebhookScope;
import com.atlassian.webhooks.WebhookService;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ApplicationWebhookEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(ApplicationWebhookEventPublisher.class);
    private final EventPublisher eventPublisher;
    private final WebhookJsonEventFactory jsonEventFactory;
    private final WebhookService webhookService;

    @Autowired
    public ApplicationWebhookEventPublisher(@ComponentImport EventPublisher eventPublisher, WebhookJsonEventFactory jsonEventFactory, @ComponentImport WebhookService webhookService) {
        this.eventPublisher = eventPublisher;
        this.jsonEventFactory = jsonEventFactory;
        this.webhookService = webhookService;
    }

    @PreDestroy
    public void destroy() {
        this.eventPublisher.unregister((Object)this);
    }

    @PostConstruct
    public void init() {
        this.eventPublisher.register((Object)this);
    }

    @EventListener
    public void onAttachmentCreated(AttachmentCreateEvent event) {
        this.publishAttachmentWebhook(ApplicationWebhookEvent.ATTACHMENT_CREATED, (AttachmentEvent)event);
    }

    @EventListener
    public void onAttachmentRemoved(AttachmentRemoveEvent event) {
        this.publishAttachmentWebhook(ApplicationWebhookEvent.ATTACHMENT_REMOVED, (AttachmentEvent)event);
    }

    @EventListener
    public void onAttachmentRestored(GeneralAttachmentRestoreEvent event) {
        this.publishAttachmentWebhook(ApplicationWebhookEvent.ATTACHMENT_RESTORED, (AttachmentEvent)event);
    }

    @EventListener
    public void onAttachmentTrashed(AttachmentTrashedEvent event) {
        this.publishAttachmentWebhook(ApplicationWebhookEvent.ATTACHMENT_TRASHED, (AttachmentEvent)event);
    }

    @EventListener
    public void onAttachmentUpdated(AttachmentUpdateEvent event) {
        this.publishAttachmentWebhook(ApplicationWebhookEvent.ATTACHMENT_UPDATED, (AttachmentEvent)event);
    }

    @EventListener
    public void onBlogCreated(BlogPostCreateEvent event) {
        this.publishBlogWebhook(ApplicationWebhookEvent.BLOG_CREATED, (BlogPostEvent)event);
    }

    @EventListener
    public void onBlogRemoved(BlogPostRemoveEvent event) {
        this.publishBlogWebhook(ApplicationWebhookEvent.BLOG_REMOVED, (BlogPostEvent)event);
    }

    @EventListener
    public void onBlogRestored(BlogPostRestoreEvent event) {
        this.publishBlogWebhook(ApplicationWebhookEvent.BLOG_RESTORED, (BlogPostEvent)event);
    }

    @EventListener
    public void onBlogTrashed(BlogPostTrashedEvent event) {
        this.publishBlogWebhook(ApplicationWebhookEvent.BLOG_TRASHED, (BlogPostEvent)event);
    }

    @EventListener
    public void onBlogUpdated(BlogPostUpdateEvent event) {
        this.publishBlogWebhook(ApplicationWebhookEvent.BLOG_UPDATED, (BlogPostEvent)event);
    }

    @EventListener
    public void onBlueprintPageCreated(BlueprintPageCreateEvent event) {
        this.publish(ApplicationWebhookEvent.BLUEPRINT_PAGE_CREATED, event.getPage().getSpace(), this.jsonEventFactory.build(event));
    }

    @EventListener
    public void onCommentCreated(CommentCreateEvent event) {
        this.publishCommentWebhook(ApplicationWebhookEvent.COMMENT_CREATED, (CommentEvent)event);
    }

    @EventListener
    public void onCommentRemoved(CommentRemoveEvent event) {
        this.publishCommentWebhook(ApplicationWebhookEvent.COMMENT_REMOVED, (CommentEvent)event);
    }

    @EventListener
    public void onCommentUpdated(CommentUpdateEvent event) {
        this.publishCommentWebhook(ApplicationWebhookEvent.COMMENT_UPDATED, (CommentEvent)event);
    }

    @EventListener
    public void onContentEvent(ContentEvent event) {
        ApplicationWebhookEvent webhookEvent = null;
        if (event instanceof Created) {
            webhookEvent = ApplicationWebhookEvent.CONTENT_CREATED;
        } else if (event instanceof Updated) {
            webhookEvent = ApplicationWebhookEvent.CONTENT_UPDATED;
        } else if (event instanceof Restore) {
            webhookEvent = ApplicationWebhookEvent.CONTENT_RESTORED;
        } else if (event instanceof Trashed) {
            webhookEvent = ApplicationWebhookEvent.CONTENT_TRASHED;
        }
        if (webhookEvent != null && event.getContent() != null) {
            this.publish(webhookEvent, this.getSpace(event.getContent()), this.jsonEventFactory.build((WebhookEvent)webhookEvent, event));
        }
    }

    @EventListener
    public void onContentPermissionEvent(ContentPermissionEvent event) {
        if (event.getContent() != null) {
            this.publish(ApplicationWebhookEvent.CONTENT_PERMISSIONS_UPDATED, this.getSpace(event.getContent()), this.jsonEventFactory.build((WebhookEvent)ApplicationWebhookEvent.CONTENT_PERMISSIONS_UPDATED, event));
        }
    }

    @EventListener
    public void onGroupCreated(GroupCreateEvent event) {
        this.publish(ApplicationWebhookEvent.GROUP_CREATED, null, this.jsonEventFactory.build((WebhookEvent)ApplicationWebhookEvent.GROUP_CREATED, (GroupEvent)event));
    }

    @EventListener
    public void onGroupCreated(GroupRemoveEvent event) {
        this.publish(ApplicationWebhookEvent.GROUP_REMOVED, null, this.jsonEventFactory.build((WebhookEvent)ApplicationWebhookEvent.GROUP_REMOVED, (GroupEvent)event));
    }

    @EventListener
    public void onLabelAdded(LabelAddEvent event) {
        this.publishLabelWebhook(ApplicationWebhookEvent.LABEL_ADDED, (LabelEvent)event);
    }

    @EventListener
    public void onLabelCreated(LabelCreateEvent event) {
        this.publishLabelWebhook(ApplicationWebhookEvent.LABEL_CREATED, (LabelEvent)event);
    }

    @EventListener
    public void onLabelDeleted(LabelDeleteEvent event) {
        this.publishLabelWebhook(ApplicationWebhookEvent.LABEL_DELETED, (LabelEvent)event);
    }

    @EventListener
    public void onLabelRemoved(LabelRemoveEvent event) {
        this.publishLabelWebhook(ApplicationWebhookEvent.LABEL_REMOVED, (LabelEvent)event);
    }

    @EventListener
    public void onPageCreated(PageCreateEvent event) {
        this.publishPageWebhook(ApplicationWebhookEvent.PAGE_CREATED, (PageEvent)event);
    }

    @EventListener
    public void onPageChildrenReordered(PageChildrenReorderEvent event) {
        this.publish(ApplicationWebhookEvent.PAGE_CHILDREN_REORDERED, this.getSpace(event.getContent()), this.jsonEventFactory.build(event));
    }

    @EventListener
    public void onPageMoved(PageMoveEvent event) {
        this.publish(ApplicationWebhookEvent.PAGE_MOVED, this.getSpace(event.getContent()), this.jsonEventFactory.build(event));
    }

    @EventListener
    public void onPageRemoved(PageRemoveEvent event) {
        this.publishPageWebhook(ApplicationWebhookEvent.PAGE_REMOVED, (PageEvent)event);
    }

    @EventListener
    public void onPageRestored(PageRestoreEvent event) {
        this.publishPageWebhook(ApplicationWebhookEvent.PAGE_RESTORED, (PageEvent)event);
    }

    @EventListener
    public void onPageTrashed(PageTrashedEvent event) {
        this.publishPageWebhook(ApplicationWebhookEvent.PAGE_TRASHED, (PageEvent)event);
    }

    @EventListener
    public void onPageUpdated(PageUpdateEvent event) {
        this.publish(ApplicationWebhookEvent.PAGE_UPDATED, this.getSpace(event.getContent()), this.jsonEventFactory.build(event));
    }

    @EventListener
    public void onSpaceCreated(SpaceCreateEvent event) {
        this.publishSpaceWebhook(ApplicationWebhookEvent.SPACE_CREATED, (SpaceEvent)event);
    }

    @EventListener
    public void onSpaceLogoUpdated(SpaceLogoUpdateEvent event) {
        this.publishSpaceWebhook(ApplicationWebhookEvent.SPACE_LOGO_UPDATED, (SpaceEvent)event);
    }

    @EventListener
    public void onSpacePermissionsUpdated(SpacePermissionsUpdateEvent event) {
        this.publishSpaceWebhook(ApplicationWebhookEvent.SPACE_PERMISSIONS_UPDATED, (SpaceEvent)event);
    }

    @EventListener
    public void onSpaceRemoved(SpaceRemoveEvent event) {
        this.publishSpaceWebhook(ApplicationWebhookEvent.SPACE_REMOVED, (SpaceEvent)event);
    }

    @EventListener
    public void onSpaceUpdated(SpaceUpdateEvent event) {
        this.publishSpaceWebhook(ApplicationWebhookEvent.SPACE_UPDATED, (SpaceEvent)event);
    }

    @EventListener
    public void onThemeEnabled(ThemeChangedEvent event) {
        this.publish(ApplicationWebhookEvent.THEME_ENABLED, null, this.jsonEventFactory.build((WebhookEvent)ApplicationWebhookEvent.THEME_ENABLED, event));
    }

    @EventListener
    public void onUserCreated(UserCreateEvent event) {
        this.publishUserWebhook(ApplicationWebhookEvent.USER_CREATED, (UserEvent)event);
    }

    @EventListener
    public void onUserDeactivated(UserDeactivateEvent event) {
        if (event.isSuppressWebhook()) {
            return;
        }
        this.publishUserWebhook(ApplicationWebhookEvent.USER_DEACTIVATED, (UserEvent)event);
    }

    @EventListener
    public void onUserFollowed(FollowEvent event) {
        this.publish(ApplicationWebhookEvent.USER_FOLLOWED, null, this.jsonEventFactory.build((WebhookEvent)ApplicationWebhookEvent.USER_FOLLOWED, (ConfluenceEvent)event));
    }

    @EventListener
    public void onUserReactivated(UserReactivateEvent event) {
        if (event.isSuppressWebhook()) {
            return;
        }
        this.publishUserWebhook(ApplicationWebhookEvent.USER_REACTIVATED, (UserEvent)event);
    }

    @EventListener
    public void onUserRemoved(UserRemoveEvent event) {
        this.publishUserWebhook(ApplicationWebhookEvent.USER_REMOVED, (UserEvent)event);
    }

    private @Nullable Space getSpace(Object content) {
        if (content instanceof Spaced) {
            return ((Spaced)content).getSpace();
        }
        return null;
    }

    private void publishAttachmentWebhook(WebhookEvent webhookEvent, AttachmentEvent event) {
        Map<String, Object> payload = this.jsonEventFactory.build(webhookEvent, event);
        this.publish(webhookEvent, this.getSpace(event.getAttachedTo()), payload);
    }

    private void publishBlogWebhook(WebhookEvent webhookEvent, BlogPostEvent event) {
        this.publish(webhookEvent, event.getBlogPost().getSpace(), this.jsonEventFactory.build(webhookEvent, event));
    }

    private void publishCommentWebhook(WebhookEvent webhookEvent, CommentEvent event) {
        this.publish(webhookEvent, event.getComment().getSpace(), this.jsonEventFactory.build(webhookEvent, event));
    }

    private void publishLabelWebhook(WebhookEvent webhookEvent, LabelEvent event) {
        this.publish(webhookEvent, this.getSpace(event.getLabelled()), this.jsonEventFactory.build(webhookEvent, event));
    }

    private void publishPageWebhook(WebhookEvent webhookEvent, PageEvent event) {
        this.publish(webhookEvent, this.getSpace(event.getPage()), this.jsonEventFactory.build(webhookEvent, event));
    }

    private void publishSpaceWebhook(WebhookEvent webhookEvent, SpaceEvent event) {
        this.publish(webhookEvent, event.getSpace(), this.jsonEventFactory.build(webhookEvent, event));
    }

    private void publishUserWebhook(WebhookEvent webhookEvent, UserEvent event) {
        this.publish(webhookEvent, null, this.jsonEventFactory.build(webhookEvent, event));
    }

    private void publish(WebhookEvent webhookEvent, @Nullable Space space, Object payload) {
        SpaceScope spaceScope = null;
        if (space == null) {
            log.trace("Publishing webhook {}", (Object)webhookEvent.getId());
        } else {
            log.trace("Publishing webhook {} for space {}", (Object)webhookEvent.getId(), (Object)space.getKey());
            spaceScope = new SpaceScope(space.getId());
        }
        this.webhookService.publish(WebhookPublishRequest.builder((WebhookEvent)webhookEvent, (Object)payload).scopes(WebhookScope.GLOBAL, new WebhookScope[]{spaceScope}).build());
    }
}


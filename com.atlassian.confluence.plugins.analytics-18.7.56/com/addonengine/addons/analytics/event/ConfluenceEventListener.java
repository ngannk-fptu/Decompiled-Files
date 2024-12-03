/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.ConfluenceEvent
 *  com.atlassian.confluence.event.events.content.attachment.AttachmentCreateEvent
 *  com.atlassian.confluence.event.events.content.attachment.AttachmentRemoveEvent
 *  com.atlassian.confluence.event.events.content.attachment.AttachmentUpdateEvent
 *  com.atlassian.confluence.event.events.content.attachment.AttachmentViewEvent
 *  com.atlassian.confluence.event.events.content.blogpost.BlogPostCreateEvent
 *  com.atlassian.confluence.event.events.content.blogpost.BlogPostRemoveEvent
 *  com.atlassian.confluence.event.events.content.blogpost.BlogPostRestoreEvent
 *  com.atlassian.confluence.event.events.content.blogpost.BlogPostTrashedEvent
 *  com.atlassian.confluence.event.events.content.blogpost.BlogPostUpdateEvent
 *  com.atlassian.confluence.event.events.content.blogpost.BlogPostViewEvent
 *  com.atlassian.confluence.event.events.content.comment.CommentCreateEvent
 *  com.atlassian.confluence.event.events.content.comment.CommentRemoveEvent
 *  com.atlassian.confluence.event.events.content.comment.CommentUpdateEvent
 *  com.atlassian.confluence.event.events.content.page.PageCreateEvent
 *  com.atlassian.confluence.event.events.content.page.PageRemoveEvent
 *  com.atlassian.confluence.event.events.content.page.PageRestoreEvent
 *  com.atlassian.confluence.event.events.content.page.PageTrashedEvent
 *  com.atlassian.confluence.event.events.content.page.PageUpdateEvent
 *  com.atlassian.confluence.event.events.content.page.PageViewEvent
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.BlogPost
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.core.filters.ServletContextThreadLocal
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.component.ConfluenceComponent
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 *  javax.servlet.http.HttpServletRequest
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.addonengine.addons.analytics.event;

import com.addonengine.addons.analytics.event.AsyncTrackedConfluenceEvent;
import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.event.events.content.attachment.AttachmentCreateEvent;
import com.atlassian.confluence.event.events.content.attachment.AttachmentRemoveEvent;
import com.atlassian.confluence.event.events.content.attachment.AttachmentUpdateEvent;
import com.atlassian.confluence.event.events.content.attachment.AttachmentViewEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostCreateEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostRemoveEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostRestoreEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostTrashedEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostUpdateEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostViewEvent;
import com.atlassian.confluence.event.events.content.comment.CommentCreateEvent;
import com.atlassian.confluence.event.events.content.comment.CommentRemoveEvent;
import com.atlassian.confluence.event.events.content.comment.CommentUpdateEvent;
import com.atlassian.confluence.event.events.content.page.PageCreateEvent;
import com.atlassian.confluence.event.events.content.page.PageRemoveEvent;
import com.atlassian.confluence.event.events.content.page.PageRestoreEvent;
import com.atlassian.confluence.event.events.content.page.PageTrashedEvent;
import com.atlassian.confluence.event.events.content.page.PageUpdateEvent;
import com.atlassian.confluence.event.events.content.page.PageViewEvent;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.core.filters.ServletContextThreadLocal;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.component.ConfluenceComponent;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import javax.servlet.http.HttpServletRequest;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

@ConfluenceComponent
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u00a4\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u00012\u00020\u0002B\u001b\b\u0007\u0012\b\b\u0001\u0010\u0003\u001a\u00020\u0004\u0012\b\b\u0001\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\u0002\u0010\u0007J\b\u0010\f\u001a\u00020\rH\u0016J\b\u0010\u000e\u001a\u00020\rH\u0016J\u0010\u0010\u000f\u001a\u00020\r2\u0006\u0010\u0010\u001a\u00020\u0011H\u0007J\u0010\u0010\u0012\u001a\u00020\r2\u0006\u0010\u0010\u001a\u00020\u0013H\u0007J\u0010\u0010\u0014\u001a\u00020\r2\u0006\u0010\u0010\u001a\u00020\u0015H\u0007J\u0010\u0010\u0016\u001a\u00020\r2\u0006\u0010\u0010\u001a\u00020\u0017H\u0007J\u0010\u0010\u0018\u001a\u00020\r2\u0006\u0010\u0010\u001a\u00020\u0019H\u0007J\u0010\u0010\u001a\u001a\u00020\r2\u0006\u0010\u0010\u001a\u00020\u001bH\u0007J\u0010\u0010\u001c\u001a\u00020\r2\u0006\u0010\u0010\u001a\u00020\u001dH\u0007J\u0010\u0010\u001e\u001a\u00020\r2\u0006\u0010\u0010\u001a\u00020\u001fH\u0007J\u0010\u0010 \u001a\u00020\r2\u0006\u0010\u0010\u001a\u00020!H\u0007J\u0010\u0010\"\u001a\u00020\r2\u0006\u0010\u0010\u001a\u00020#H\u0007J\u0010\u0010$\u001a\u00020\r2\u0006\u0010\u0010\u001a\u00020%H\u0007J\u0010\u0010&\u001a\u00020\r2\u0006\u0010\u0010\u001a\u00020'H\u0007J\u0010\u0010(\u001a\u00020\r2\u0006\u0010\u0010\u001a\u00020)H\u0007J\u0010\u0010*\u001a\u00020\r2\u0006\u0010\u0010\u001a\u00020+H\u0007J\u0010\u0010,\u001a\u00020\r2\u0006\u0010\u0010\u001a\u00020-H\u0007J\u0010\u0010.\u001a\u00020\r2\u0006\u0010\u0010\u001a\u00020/H\u0007J\u0010\u00100\u001a\u00020\r2\u0006\u0010\u0010\u001a\u000201H\u0007J\u0010\u00102\u001a\u00020\r2\u0006\u0010\u0010\u001a\u000203H\u0007J\u0010\u00104\u001a\u00020\r2\u0006\u0010\u0010\u001a\u000205H\u0007J\u0010\u00106\u001a\u00020\r2\u0006\u0010\u0010\u001a\u000207H\u0002J\u0012\u00108\u001a\u00020\r2\b\u00109\u001a\u0004\u0018\u00010:H\u0002R\u0011\u0010\u0003\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b\u00a8\u0006;"}, d2={"Lcom/addonengine/addons/analytics/event/ConfluenceEventListener;", "Lorg/springframework/beans/factory/InitializingBean;", "Lorg/springframework/beans/factory/DisposableBean;", "eventPublisher", "Lcom/atlassian/event/api/EventPublisher;", "userManager", "Lcom/atlassian/sal/api/user/UserManager;", "(Lcom/atlassian/event/api/EventPublisher;Lcom/atlassian/sal/api/user/UserManager;)V", "getEventPublisher", "()Lcom/atlassian/event/api/EventPublisher;", "getUserManager", "()Lcom/atlassian/sal/api/user/UserManager;", "afterPropertiesSet", "", "destroy", "onAttachmentCreateEvent", "event", "Lcom/atlassian/confluence/event/events/content/attachment/AttachmentCreateEvent;", "onAttachmentRemoveEvent", "Lcom/atlassian/confluence/event/events/content/attachment/AttachmentRemoveEvent;", "onAttachmentUpdateEvent", "Lcom/atlassian/confluence/event/events/content/attachment/AttachmentUpdateEvent;", "onAttachmentViewEvent", "Lcom/atlassian/confluence/event/events/content/attachment/AttachmentViewEvent;", "onBlogPostCreateEvent", "Lcom/atlassian/confluence/event/events/content/blogpost/BlogPostCreateEvent;", "onBlogPostRemoveEvent", "Lcom/atlassian/confluence/event/events/content/blogpost/BlogPostRemoveEvent;", "onBlogPostRestoreEvent", "Lcom/atlassian/confluence/event/events/content/blogpost/BlogPostRestoreEvent;", "onBlogPostTrashedEvent", "Lcom/atlassian/confluence/event/events/content/blogpost/BlogPostTrashedEvent;", "onBlogPostUpdateEvent", "Lcom/atlassian/confluence/event/events/content/blogpost/BlogPostUpdateEvent;", "onBlogPostViewEvent", "Lcom/atlassian/confluence/event/events/content/blogpost/BlogPostViewEvent;", "onCommentCreateEvent", "Lcom/atlassian/confluence/event/events/content/comment/CommentCreateEvent;", "onCommentRemoveEvent", "Lcom/atlassian/confluence/event/events/content/comment/CommentRemoveEvent;", "onCommentUpdateEvent", "Lcom/atlassian/confluence/event/events/content/comment/CommentUpdateEvent;", "onPageCreateEvent", "Lcom/atlassian/confluence/event/events/content/page/PageCreateEvent;", "onPageRemoveEvent", "Lcom/atlassian/confluence/event/events/content/page/PageRemoveEvent;", "onPageRestoreEvent", "Lcom/atlassian/confluence/event/events/content/page/PageRestoreEvent;", "onPageTrashedEvent", "Lcom/atlassian/confluence/event/events/content/page/PageTrashedEvent;", "onPageUpdateEvent", "Lcom/atlassian/confluence/event/events/content/page/PageUpdateEvent;", "onPageViewEvent", "Lcom/atlassian/confluence/event/events/content/page/PageViewEvent;", "publishAsyncEvent", "Lcom/atlassian/confluence/event/events/ConfluenceEvent;", "unproxySpace", "space", "Lcom/atlassian/confluence/spaces/Space;", "analytics"})
public final class ConfluenceEventListener
implements InitializingBean,
DisposableBean {
    @NotNull
    private final EventPublisher eventPublisher;
    @NotNull
    private final UserManager userManager;

    @Autowired
    public ConfluenceEventListener(@ComponentImport @NotNull EventPublisher eventPublisher, @ComponentImport @NotNull UserManager userManager) {
        Intrinsics.checkNotNullParameter((Object)eventPublisher, (String)"eventPublisher");
        Intrinsics.checkNotNullParameter((Object)userManager, (String)"userManager");
        this.eventPublisher = eventPublisher;
        this.userManager = userManager;
    }

    @NotNull
    public final EventPublisher getEventPublisher() {
        return this.eventPublisher;
    }

    @NotNull
    public final UserManager getUserManager() {
        return this.userManager;
    }

    public void afterPropertiesSet() {
        this.eventPublisher.register((Object)this);
    }

    @EventListener
    public final void onAttachmentRemoveEvent(@NotNull AttachmentRemoveEvent event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        Attachment attachment = event.getAttachment();
        this.unproxySpace((Space)(attachment != null ? attachment.getSpace() : null));
        this.publishAsyncEvent((ConfluenceEvent)event);
    }

    @EventListener
    public final void onAttachmentUpdateEvent(@NotNull AttachmentUpdateEvent event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        Attachment attachment = event.getAttachment();
        this.unproxySpace((Space)(attachment != null ? attachment.getSpace() : null));
        this.publishAsyncEvent((ConfluenceEvent)event);
    }

    @EventListener
    public final void onAttachmentCreateEvent(@NotNull AttachmentCreateEvent event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        Attachment attachment = event.getAttachment();
        this.unproxySpace((Space)(attachment != null ? attachment.getSpace() : null));
        this.publishAsyncEvent((ConfluenceEvent)event);
    }

    @EventListener
    public final void onBlogPostCreateEvent(@NotNull BlogPostCreateEvent event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        BlogPost blogPost = event.getBlogPost();
        this.unproxySpace((Space)(blogPost != null ? blogPost.getSpace() : null));
        this.publishAsyncEvent((ConfluenceEvent)event);
    }

    @EventListener
    public final void onBlogPostRemoveEvent(@NotNull BlogPostRemoveEvent event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        BlogPost blogPost = event.getBlogPost();
        this.unproxySpace((Space)(blogPost != null ? blogPost.getSpace() : null));
        this.publishAsyncEvent((ConfluenceEvent)event);
    }

    @EventListener
    public final void onBlogPostTrashedEvent(@NotNull BlogPostTrashedEvent event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        BlogPost blogPost = event.getBlogPost();
        this.unproxySpace((Space)(blogPost != null ? blogPost.getSpace() : null));
        this.publishAsyncEvent((ConfluenceEvent)event);
    }

    @EventListener
    public final void onBlogPostRestoreEvent(@NotNull BlogPostRestoreEvent event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        BlogPost blogPost = event.getBlogPost();
        this.unproxySpace((Space)(blogPost != null ? blogPost.getSpace() : null));
        this.publishAsyncEvent((ConfluenceEvent)event);
    }

    @EventListener
    public final void onBlogPostUpdateEvent(@NotNull BlogPostUpdateEvent event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        BlogPost blogPost = event.getBlogPost();
        this.unproxySpace((Space)(blogPost != null ? blogPost.getSpace() : null));
        this.publishAsyncEvent((ConfluenceEvent)event);
    }

    @EventListener
    public final void onBlogPostViewEvent(@NotNull BlogPostViewEvent event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        BlogPost blogPost = event.getBlogPost();
        this.unproxySpace((Space)(blogPost != null ? blogPost.getSpace() : null));
        this.publishAsyncEvent((ConfluenceEvent)event);
    }

    @EventListener
    public final void onPageCreateEvent(@NotNull PageCreateEvent event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        Page page = event.getPage();
        this.unproxySpace((Space)(page != null ? page.getSpace() : null));
        this.publishAsyncEvent((ConfluenceEvent)event);
    }

    @EventListener
    public final void onPageRemoveEvent(@NotNull PageRemoveEvent event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        Page page = event.getPage();
        this.unproxySpace((Space)(page != null ? page.getSpace() : null));
        this.publishAsyncEvent((ConfluenceEvent)event);
    }

    @EventListener
    public final void onPageTrashedEvent(@NotNull PageTrashedEvent event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        Page page = event.getPage();
        this.unproxySpace((Space)(page != null ? page.getSpace() : null));
        this.publishAsyncEvent((ConfluenceEvent)event);
    }

    @EventListener
    public final void onPageRestoreEvent(@NotNull PageRestoreEvent event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        Page page = event.getPage();
        this.unproxySpace((Space)(page != null ? page.getSpace() : null));
        this.publishAsyncEvent((ConfluenceEvent)event);
    }

    @EventListener
    public final void onPageUpdateEvent(@NotNull PageUpdateEvent event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        Page page = event.getPage();
        this.unproxySpace((Space)(page != null ? page.getSpace() : null));
        this.publishAsyncEvent((ConfluenceEvent)event);
    }

    @EventListener
    public final void onPageViewEvent(@NotNull PageViewEvent event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        Page page = event.getPage();
        this.unproxySpace((Space)(page != null ? page.getSpace() : null));
        this.publishAsyncEvent((ConfluenceEvent)event);
    }

    @EventListener
    public final void onCommentRemoveEvent(@NotNull CommentRemoveEvent event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        Comment comment = event.getComment();
        this.unproxySpace((Space)(comment != null ? comment.getSpace() : null));
        this.publishAsyncEvent((ConfluenceEvent)event);
    }

    @EventListener
    public final void onCommentUpdateEvent(@NotNull CommentUpdateEvent event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        Comment comment = event.getComment();
        this.unproxySpace((Space)(comment != null ? comment.getSpace() : null));
        this.publishAsyncEvent((ConfluenceEvent)event);
    }

    @EventListener
    public final void onCommentCreateEvent(@NotNull CommentCreateEvent event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        Comment comment = event.getComment();
        this.unproxySpace((Space)(comment != null ? comment.getSpace() : null));
        this.publishAsyncEvent((ConfluenceEvent)event);
    }

    @EventListener
    public final void onAttachmentViewEvent(@NotNull AttachmentViewEvent event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        Attachment attachment = event.getAttachment();
        this.unproxySpace((Space)(attachment != null ? attachment.getSpace() : null));
        this.publishAsyncEvent((ConfluenceEvent)event);
    }

    private final void publishAsyncEvent(ConfluenceEvent event) {
        HttpServletRequest httpServletRequest = ServletContextThreadLocal.getRequest();
        String userAgent = httpServletRequest != null ? httpServletRequest.getHeader("user-agent") : null;
        UserProfile user = this.userManager.getRemoteUser();
        this.eventPublisher.publish((Object)new AsyncTrackedConfluenceEvent(event, user, userAgent));
    }

    private final void unproxySpace(Space space) {
        block0: {
            Space space2 = space;
            if (space2 == null) break block0;
            space2.getKey();
        }
    }

    public void destroy() {
        this.eventPublisher.unregister((Object)this);
    }
}


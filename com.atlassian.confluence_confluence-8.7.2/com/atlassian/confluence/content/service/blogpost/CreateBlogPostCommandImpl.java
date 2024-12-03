/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.content.service.blogpost;

import com.atlassian.confluence.content.service.DraftService;
import com.atlassian.confluence.content.service.blogpost.BlogPostProvider;
import com.atlassian.confluence.content.service.blogpost.CreateBlogPostCommand;
import com.atlassian.confluence.content.service.page.ContentPermissionProvider;
import com.atlassian.confluence.content.service.page.CreateAbstractPageCommandImpl;
import com.atlassian.confluence.content.service.page.CreateContextProvider;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.ContentPermissionManager;
import com.atlassian.confluence.core.ContentPropertyManager;
import com.atlassian.confluence.core.service.ServiceCommandValidator;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostCreateEvent;
import com.atlassian.confluence.event.events.types.Created;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.pages.DraftsTransitionHelper;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.user.User;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class CreateBlogPostCommandImpl
extends CreateAbstractPageCommandImpl
implements CreateBlogPostCommand {
    protected SpacePermissionManager spacePermissionManager;
    private BlogPostProvider blogPostProvider;

    @Deprecated
    public CreateBlogPostCommandImpl(PageManager pageManager, SpacePermissionManager spacePermissionManager, ContentPermissionManager contentPermissionManager, DraftService draftService, AttachmentManager attachmentManager, NotificationManager notificationManager, EventPublisher eventPublisher, BlogPostProvider blogPostProvider, ContentPermissionProvider permissionProvider, CreateContextProvider contextProvider, Draft draft, User user, boolean notifySelf, ContentPropertyManager contentPropertyManager, LabelManager labelManager, DraftsTransitionHelper draftsTransitionHelper) {
        this(pageManager, spacePermissionManager, contentPermissionManager, draftService, attachmentManager, notificationManager, eventPublisher, blogPostProvider, permissionProvider, contextProvider, (ContentEntityObject)draft, user, notifySelf, contentPropertyManager, labelManager, draftsTransitionHelper);
    }

    @Deprecated
    public CreateBlogPostCommandImpl(PageManager pageManager, SpacePermissionManager spacePermissionManager, ContentPermissionManager contentPermissionManager, DraftService draftService, AttachmentManager attachmentManager, NotificationManager notificationManager, EventPublisher eventPublisher, BlogPostProvider blogPostProvider, ContentPermissionProvider permissionProvider, CreateContextProvider contextProvider, ContentEntityObject draft, User user, boolean notifySelf, ContentPropertyManager contentPropertyManager, LabelManager labelManager, DraftsTransitionHelper draftsTransitionHelper) {
        super(pageManager, contentPermissionManager, draftService, attachmentManager, notificationManager, eventPublisher, permissionProvider, contextProvider, draft, user, notifySelf, contentPropertyManager, labelManager, draftsTransitionHelper);
        this.spacePermissionManager = spacePermissionManager;
        this.blogPostProvider = blogPostProvider;
    }

    public CreateBlogPostCommandImpl(PageManager pageManager, SpacePermissionManager spacePermissionManager, ContentPermissionManager contentPermissionManager, DraftService draftService, AttachmentManager attachmentManager, NotificationManager notificationManager, EventPublisher eventPublisher, BlogPostProvider blogPostProvider, ContentPermissionProvider permissionProvider, CreateContextProvider contextProvider, BlogPost draft, User user, boolean notifySelf, ContentPropertyManager contentPropertyManager, LabelManager labelManager, DraftsTransitionHelper draftsTransitionHelper) {
        super(pageManager, contentPermissionManager, draftService, attachmentManager, notificationManager, eventPublisher, permissionProvider, contextProvider, draft, user, notifySelf, contentPropertyManager, labelManager, draftsTransitionHelper);
        this.spacePermissionManager = spacePermissionManager;
        this.blogPostProvider = blogPostProvider;
    }

    @Override
    protected void validateInternal(ServiceCommandValidator validator) {
        BlogPost post;
        super.validateInternal(validator);
        BlogPost blog = this.blogPostProvider.getBlogPost();
        Date postingDate = blog.getPostingDate();
        if (postingDate == null) {
            postingDate = new Date();
        }
        int oneDay = 86400000;
        if (postingDate.after(new Date(System.currentTimeMillis() + (long)oneDay))) {
            validator.addValidationError("news.date.in.future", new Object[0]);
        }
        if ((post = this.pageManager.getBlogPost(blog.getSpaceKey(), blog.getTitle(), BlogPost.toCalendar(postingDate))) != null) {
            validator.addValidationError("news.title.exists", new Object[0]);
        }
    }

    @Override
    protected boolean isAuthorizedInternal() {
        return this.spacePermissionManager.hasAllPermissions(this.getPermissionTypes(), this.blogPostProvider.getBlogPost().getSpace(), this.user);
    }

    private List<String> getPermissionTypes() {
        return Arrays.asList("USECONFLUENCE", "VIEWSPACE", "EDITBLOG");
    }

    @Override
    protected AbstractPage getContent() {
        return this.blogPostProvider.getBlogPost();
    }

    @Override
    protected Created getCreateEvent() {
        return new BlogPostCreateEvent((Object)this, (BlogPost)this.createdContent, this.contextProvider.getContext());
    }

    @Override
    public BlogPost getCreatedBlogPost() {
        return (BlogPost)this.getCreatedContent();
    }
}


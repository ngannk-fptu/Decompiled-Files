/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.user.User
 *  com.google.common.base.Supplier
 */
package com.atlassian.confluence.content.service;

import com.atlassian.confluence.content.service.BlogPostService;
import com.atlassian.confluence.content.service.DraftService;
import com.atlassian.confluence.content.service.blogpost.BlogPostLocator;
import com.atlassian.confluence.content.service.blogpost.BlogPostProvider;
import com.atlassian.confluence.content.service.blogpost.CreateBlogPostCommandImpl;
import com.atlassian.confluence.content.service.blogpost.IdBlogPostLocator;
import com.atlassian.confluence.content.service.blogpost.MoveBlogPostToTopOfSpaceCommand;
import com.atlassian.confluence.content.service.blogpost.RemoveBlogPostVersionCommand;
import com.atlassian.confluence.content.service.blogpost.RevertBlogPostCommand;
import com.atlassian.confluence.content.service.page.ContentPermissionProvider;
import com.atlassian.confluence.content.service.page.CreateContextProvider;
import com.atlassian.confluence.content.service.page.DeleteBlogPostCommand;
import com.atlassian.confluence.content.service.space.SpaceLocator;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.ContentPermissionManager;
import com.atlassian.confluence.core.ContentPropertyManager;
import com.atlassian.confluence.core.service.ServiceCommand;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.pages.DraftsTransitionHelper;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.user.User;
import com.google.common.base.Supplier;

public class DefaultBlogPostService
implements BlogPostService {
    private final PageManager pageManager;
    private PermissionManager permissionManager;
    private final ContentPermissionManager contentPermissionManager;
    private final DraftService draftService;
    private final AttachmentManager attachmentManager;
    private final NotificationManager notificationManager;
    private final EventPublisher eventPublisher;
    private final SpacePermissionManager spacePermissionManager;
    private final ContentPropertyManager contentPropertyManager;
    private final LabelManager labelManager;
    private final Supplier<DraftsTransitionHelper> draftsTransitionHelperSupplier;

    public DefaultBlogPostService(PageManager pageManager, PermissionManager permissionManager, ContentPermissionManager contentPermissionManager, SpacePermissionManager spacePermissionManager, DraftService draftService, AttachmentManager attachmentManager, NotificationManager notificationManager, EventPublisher eventPublisher, ContentPropertyManager contentPropertyManager, LabelManager labelManager, Supplier<DraftsTransitionHelper> draftsTransitionHelperSupplier) {
        this.pageManager = pageManager;
        this.permissionManager = permissionManager;
        this.contentPermissionManager = contentPermissionManager;
        this.spacePermissionManager = spacePermissionManager;
        this.draftService = draftService;
        this.attachmentManager = attachmentManager;
        this.notificationManager = notificationManager;
        this.eventPublisher = eventPublisher;
        this.contentPropertyManager = contentPropertyManager;
        this.labelManager = labelManager;
        this.draftsTransitionHelperSupplier = draftsTransitionHelperSupplier;
    }

    @Override
    public ServiceCommand newDeleteBlogPostCommand(BlogPostLocator blogPostLocator) {
        return new DeleteBlogPostCommand(this.pageManager, this.permissionManager, blogPostLocator);
    }

    @Override
    public BlogPostLocator getIdBlogPostLocator(long pageId) {
        return new IdBlogPostLocator(this.pageManager, pageId);
    }

    @Override
    public ServiceCommand newRevertBlogPostCommand(BlogPostLocator blogPostToRevert, int version, String revertComment, boolean revertTitle) {
        return new RevertBlogPostCommand(this.pageManager, this.permissionManager, blogPostToRevert, revertComment, version, revertTitle);
    }

    @Override
    public ServiceCommand newCreateBlogPostCommand(BlogPostProvider provider, ContentPermissionProvider permissionProvider, CreateContextProvider contextProvider, BlogPost draft, User user, boolean notifySelf) {
        return new CreateBlogPostCommandImpl(this.pageManager, this.spacePermissionManager, this.contentPermissionManager, this.draftService, this.attachmentManager, this.notificationManager, this.eventPublisher, provider, permissionProvider, contextProvider, draft, user, notifySelf, this.contentPropertyManager, this.labelManager, this.getDraftsTransitionHelper());
    }

    @Override
    @Deprecated
    public ServiceCommand newCreateBlogPostCommand(BlogPostProvider provider, ContentPermissionProvider permissionProvider, CreateContextProvider contextProvider, ContentEntityObject draft, User user, boolean notifySelf) {
        return new CreateBlogPostCommandImpl(this.pageManager, this.spacePermissionManager, this.contentPermissionManager, this.draftService, this.attachmentManager, this.notificationManager, this.eventPublisher, provider, permissionProvider, contextProvider, draft, user, notifySelf, this.contentPropertyManager, this.labelManager, this.getDraftsTransitionHelper());
    }

    @Override
    @Deprecated
    public ServiceCommand newCreateBlogPostCommand(BlogPostProvider provider, ContentPermissionProvider permissionProvider, CreateContextProvider contextProvider, Draft draft, User user, boolean notifySelf) {
        return new CreateBlogPostCommandImpl(this.pageManager, this.spacePermissionManager, this.contentPermissionManager, this.draftService, this.attachmentManager, this.notificationManager, this.eventPublisher, provider, permissionProvider, contextProvider, draft, user, notifySelf, this.contentPropertyManager, this.labelManager, this.getDraftsTransitionHelper());
    }

    @Override
    public ServiceCommand newMoveBlogPostCommand(BlogPostLocator blogPostLocator, SpaceLocator targetSpaceLocator) {
        return new MoveBlogPostToTopOfSpaceCommand(this.pageManager, this.permissionManager, blogPostLocator, targetSpaceLocator);
    }

    @Override
    public ServiceCommand newRemoveBlogPostVersionCommand(BlogPostLocator blogPostLocator) {
        return new RemoveBlogPostVersionCommand(this.pageManager, this.permissionManager, blogPostLocator);
    }

    public void setPermissionManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    public DraftsTransitionHelper getDraftsTransitionHelper() {
        return (DraftsTransitionHelper)this.draftsTransitionHelperSupplier.get();
    }
}


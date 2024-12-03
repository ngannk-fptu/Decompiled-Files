/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.content.service.page;

import com.atlassian.confluence.content.service.DraftService;
import com.atlassian.confluence.content.service.page.ContentPermissionProvider;
import com.atlassian.confluence.content.service.page.CreateAbstractPageCommandImpl;
import com.atlassian.confluence.content.service.page.CreateContextProvider;
import com.atlassian.confluence.content.service.page.CreatePageCommand;
import com.atlassian.confluence.content.service.page.PageProvider;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.ContentPermissionManager;
import com.atlassian.confluence.core.ContentPropertyManager;
import com.atlassian.confluence.core.service.ServiceCommandValidator;
import com.atlassian.confluence.event.events.content.page.PageCreateEvent;
import com.atlassian.confluence.event.events.types.Created;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.pages.DraftsTransitionHelper;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.user.User;

public class CreatePageCommandImpl
extends CreateAbstractPageCommandImpl
implements CreatePageCommand {
    protected PermissionManager permissionManager;
    private PageProvider pageProvider;

    @Deprecated
    public CreatePageCommandImpl(PageManager pageManager, PermissionManager permissionManager, ContentPermissionManager contentPermissionManager, DraftService draftService, AttachmentManager attachmentManager, NotificationManager notificationManager, EventPublisher eventPublisher, PageProvider pageProvider, ContentPermissionProvider permissionProvider, CreateContextProvider contextProvider, Draft draft, User user, boolean notifySelf, ContentPropertyManager contentPropertyManager, LabelManager labelManager, DraftsTransitionHelper draftsTransitionHelper) {
        this(pageManager, permissionManager, contentPermissionManager, draftService, attachmentManager, notificationManager, eventPublisher, pageProvider, permissionProvider, contextProvider, (ContentEntityObject)draft, user, notifySelf, contentPropertyManager, labelManager, draftsTransitionHelper);
    }

    @Deprecated
    public CreatePageCommandImpl(PageManager pageManager, PermissionManager permissionManager, ContentPermissionManager contentPermissionManager, DraftService draftService, AttachmentManager attachmentManager, NotificationManager notificationManager, EventPublisher eventPublisher, PageProvider pageProvider, ContentPermissionProvider permissionProvider, CreateContextProvider contextProvider, ContentEntityObject draft, User user, boolean notifySelf, ContentPropertyManager contentPropertyManager, LabelManager labelManager, DraftsTransitionHelper draftsTransitionHelper) {
        super(pageManager, contentPermissionManager, draftService, attachmentManager, notificationManager, eventPublisher, permissionProvider, contextProvider, draft, user, notifySelf, contentPropertyManager, labelManager, draftsTransitionHelper);
        this.permissionManager = permissionManager;
        this.pageProvider = pageProvider;
    }

    public CreatePageCommandImpl(PageManager pageManager, PermissionManager permissionManager, ContentPermissionManager contentPermissionManager, DraftService draftService, AttachmentManager attachmentManager, NotificationManager notificationManager, EventPublisher eventPublisher, PageProvider pageProvider, ContentPermissionProvider permissionProvider, CreateContextProvider contextProvider, Page draft, User user, boolean notifySelf, ContentPropertyManager contentPropertyManager, LabelManager labelManager, DraftsTransitionHelper draftsTransitionHelper) {
        super(pageManager, contentPermissionManager, draftService, attachmentManager, notificationManager, eventPublisher, permissionProvider, contextProvider, draft, user, notifySelf, contentPropertyManager, labelManager, draftsTransitionHelper);
        this.permissionManager = permissionManager;
        this.pageProvider = pageProvider;
    }

    @Override
    protected void validateInternal(ServiceCommandValidator validator) {
        super.validateInternal(validator);
        AbstractPage page = this.getContent();
        if (this.pageManager.getPage(page.getSpaceKey(), page.getTitle()) != null) {
            validator.addValidationError("createpage.duplicate.title", page.getTitle(), page.getSpace().getName());
            this.eventPublisher.publish((Object)new DuplicatePageTitleEvent(page.getTitle()));
        }
    }

    @Override
    protected boolean isAuthorizedInternal() {
        if (this.getContent() == null) {
            return false;
        }
        return this.permissionManager.hasCreatePermission(this.user, (Object)this.getContent().getSpace(), Page.class);
    }

    @Override
    protected AbstractPage getContent() {
        return this.pageProvider.getPage();
    }

    @Override
    protected Created getCreateEvent() {
        return new PageCreateEvent((Object)this, (Page)this.createdContent, this.contextProvider.getContext());
    }

    @Override
    public Page getCreatedPage() {
        return (Page)this.getCreatedContent();
    }

    private static class DuplicatePageTitleEvent {
        private final String pageTitle;

        public DuplicatePageTitleEvent(String pageTitle) {
            this.pageTitle = pageTitle;
        }

        public String getPageTitle() {
            return this.pageTitle;
        }
    }
}


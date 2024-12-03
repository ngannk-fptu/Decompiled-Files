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
import com.atlassian.confluence.content.service.page.CreateContextProvider;
import com.atlassian.confluence.content.service.page.CreatePageCommandImpl;
import com.atlassian.confluence.content.service.page.PageProvider;
import com.atlassian.confluence.content.service.space.SpaceProvider;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.ContentPermissionManager;
import com.atlassian.confluence.core.ContentPropertyManager;
import com.atlassian.confluence.core.service.ServiceCommandValidator;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.pages.DraftsTransitionHelper;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.user.User;

public class CreatePageFromExistingCommandImpl
extends CreatePageCommandImpl {
    private final SpaceProvider spaceProvider;

    @Deprecated
    public CreatePageFromExistingCommandImpl(PageManager pageManager, PermissionManager permissionManager, ContentPermissionManager contentPermissionManager, DraftService draftService, AttachmentManager attachmentManager, NotificationManager notificationManager, EventPublisher eventPublisher, PageProvider pageProvider, ContentPermissionProvider permissionProvider, CreateContextProvider contextProvider, Draft draft, User user, boolean notifySelf, SpaceProvider spaceProvider, ContentPropertyManager contentPropertyManager, LabelManager labelManager, DraftsTransitionHelper draftsTransitionHelper) {
        this(pageManager, permissionManager, contentPermissionManager, draftService, attachmentManager, notificationManager, eventPublisher, pageProvider, permissionProvider, contextProvider, (ContentEntityObject)draft, user, notifySelf, spaceProvider, contentPropertyManager, labelManager, draftsTransitionHelper);
    }

    @Deprecated
    public CreatePageFromExistingCommandImpl(PageManager pageManager, PermissionManager permissionManager, ContentPermissionManager contentPermissionManager, DraftService draftService, AttachmentManager attachmentManager, NotificationManager notificationManager, EventPublisher eventPublisher, PageProvider pageProvider, ContentPermissionProvider permissionProvider, CreateContextProvider contextProvider, ContentEntityObject draft, User user, boolean notifySelf, SpaceProvider spaceProvider, ContentPropertyManager contentPropertyManager, LabelManager labelManager, DraftsTransitionHelper draftsTransitionHelper) {
        super(pageManager, permissionManager, contentPermissionManager, draftService, attachmentManager, notificationManager, eventPublisher, pageProvider, permissionProvider, contextProvider, draft, user, notifySelf, contentPropertyManager, labelManager, draftsTransitionHelper);
        this.spaceProvider = spaceProvider;
    }

    public CreatePageFromExistingCommandImpl(PageManager pageManager, PermissionManager permissionManager, ContentPermissionManager contentPermissionManager, DraftService draftService, AttachmentManager attachmentManager, NotificationManager notificationManager, EventPublisher eventPublisher, PageProvider pageProvider, ContentPermissionProvider permissionProvider, CreateContextProvider contextProvider, Page draft, User user, boolean notifySelf, SpaceProvider spaceProvider, ContentPropertyManager contentPropertyManager, LabelManager labelManager, DraftsTransitionHelper draftsTransitionHelper) {
        super(pageManager, permissionManager, contentPermissionManager, draftService, attachmentManager, notificationManager, eventPublisher, pageProvider, permissionProvider, contextProvider, draft, user, notifySelf, contentPropertyManager, labelManager, draftsTransitionHelper);
        this.spaceProvider = spaceProvider;
    }

    @Override
    protected void validateInternal(ServiceCommandValidator validator) {
        super.validateInternal(validator);
        if (!this.permissionManager.hasCreatePermission(this.user, (Object)this.getContent().getSpace(), Page.class)) {
            validator.addValidationError("copy.space.error.space.create.page.permission.denied", new Object[0]);
        }
    }

    @Override
    protected boolean isAuthorizedInternal() {
        if (this.getContent() != null) {
            if (this.spaceProvider != null) {
                return this.permissionManager.hasPermission(this.user, Permission.VIEW, this.getContent().getLatestVersion()) && this.permissionManager.hasCreatePermission(this.user, (Object)this.spaceProvider.getSpace(), Page.class);
            }
            return this.permissionManager.hasPermission(this.user, Permission.VIEW, this.getContent().getLatestVersion());
        }
        return false;
    }
}


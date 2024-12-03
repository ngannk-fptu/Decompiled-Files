/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.user.User
 *  com.google.common.base.Supplier
 */
package com.atlassian.confluence.content.service;

import com.atlassian.confluence.content.service.DraftService;
import com.atlassian.confluence.content.service.PageService;
import com.atlassian.confluence.content.service.page.ContentPermissionProvider;
import com.atlassian.confluence.content.service.page.CreateContextProvider;
import com.atlassian.confluence.content.service.page.CreatePageCommandImpl;
import com.atlassian.confluence.content.service.page.CreatePageFromExistingCommandImpl;
import com.atlassian.confluence.content.service.page.DeletePageCommand;
import com.atlassian.confluence.content.service.page.IdAndVersionPageLocator;
import com.atlassian.confluence.content.service.page.IdPageLocator;
import com.atlassian.confluence.content.service.page.MovePageCommand;
import com.atlassian.confluence.content.service.page.MovePageCommandHelper;
import com.atlassian.confluence.content.service.page.PageLocator;
import com.atlassian.confluence.content.service.page.PageProvider;
import com.atlassian.confluence.content.service.page.RemovePageVersionCommand;
import com.atlassian.confluence.content.service.page.RevertPageCommand;
import com.atlassian.confluence.content.service.page.RevertPageOrderCommand;
import com.atlassian.confluence.content.service.page.SetPageOrderCommand;
import com.atlassian.confluence.content.service.page.TitleAndSpaceKeyPageLocator;
import com.atlassian.confluence.content.service.space.SpaceLocator;
import com.atlassian.confluence.content.service.space.SpaceProvider;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.ContentPermissionManager;
import com.atlassian.confluence.core.ContentPropertyManager;
import com.atlassian.confluence.core.service.ServiceCommand;
import com.atlassian.confluence.internal.longrunning.LongRunningTaskManagerInternal;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.pages.DraftsTransitionHelper;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.user.User;
import com.google.common.base.Supplier;
import java.util.List;

public class DefaultPageService
implements PageService {
    private final PageManager pageManager;
    private PermissionManager permissionManager;
    private final ContentPermissionManager contentPermissionManager;
    private final DraftService draftService;
    private final AttachmentManager attachmentManager;
    private final NotificationManager notificationManager;
    private final EventPublisher eventPublisher;
    private final ContentPropertyManager contentPropertyManager;
    private final LabelManager labelManager;
    private final Supplier<DraftsTransitionHelper> draftsTransitionHelperSupplier;
    private final MovePageCommandHelper movePageCommandHelper;

    public DefaultPageService(PageManager pageManager, PermissionManager permissionManager, ContentPermissionManager contentPermissionManager, DraftService draftService, AttachmentManager attachmentManager, NotificationManager notificationManager, EventPublisher eventPublisher, ContentPropertyManager contentPropertyManager, LabelManager labelManager, Supplier<DraftsTransitionHelper> draftsTransitionHelperSupplier, MovePageCommandHelper movePageCommandHelper) {
        this.pageManager = pageManager;
        this.permissionManager = permissionManager;
        this.contentPermissionManager = contentPermissionManager;
        this.draftService = draftService;
        this.attachmentManager = attachmentManager;
        this.notificationManager = notificationManager;
        this.eventPublisher = eventPublisher;
        this.contentPropertyManager = contentPropertyManager;
        this.labelManager = labelManager;
        this.draftsTransitionHelperSupplier = draftsTransitionHelperSupplier;
        this.movePageCommandHelper = movePageCommandHelper;
    }

    @Deprecated
    public DefaultPageService(PageManager pageManager, PermissionManager permissionManager, ContentPermissionManager contentPermissionManager, DraftService draftService, AttachmentManager attachmentManager, NotificationManager notificationManager, EventPublisher eventPublisher, ContentPropertyManager contentPropertyManager, LabelManager labelManager, Supplier<DraftsTransitionHelper> draftsTransitionHelperSupplier, LongRunningTaskManagerInternal longRunningTaskManager) {
        this.pageManager = pageManager;
        this.permissionManager = permissionManager;
        this.contentPermissionManager = contentPermissionManager;
        this.draftService = draftService;
        this.attachmentManager = attachmentManager;
        this.notificationManager = notificationManager;
        this.eventPublisher = eventPublisher;
        this.contentPropertyManager = contentPropertyManager;
        this.labelManager = labelManager;
        this.draftsTransitionHelperSupplier = draftsTransitionHelperSupplier;
        this.movePageCommandHelper = (MovePageCommandHelper)ContainerManager.getComponent((String)"movePageCommandHelper", MovePageCommandHelper.class);
    }

    @Override
    public MovePageCommand newMovePageCommand(PageLocator sourcePageLocator, PageLocator targetPageLocator, String position) {
        return this.movePageCommandHelper.newMovePageCommand(sourcePageLocator, targetPageLocator, position, MovePageCommandHelper.MovePageMode.ASYNC);
    }

    @Override
    public MovePageCommand newMovePageCommand(PageLocator sourcePageLocator, SpaceLocator targetSpaceLocator) {
        return this.movePageCommandHelper.newMovePageCommand(sourcePageLocator, targetSpaceLocator, MovePageCommandHelper.MovePageMode.ASYNC);
    }

    @Override
    public ServiceCommand newSetPageOrderCommand(PageLocator parentPageLocator, List<Long> childPageIds) {
        return new SetPageOrderCommand(this.pageManager, this.permissionManager, parentPageLocator, childPageIds);
    }

    @Override
    public ServiceCommand newRevertPageOrderCommand(PageLocator parentPageLocator) {
        return new RevertPageOrderCommand(this.pageManager, this.permissionManager, parentPageLocator);
    }

    @Override
    public ServiceCommand newDeletePageCommand(PageLocator pageLocator) {
        return new DeletePageCommand(this.pageManager, this.permissionManager, pageLocator);
    }

    @Override
    public ServiceCommand newRemovePageVersionCommand(PageLocator pageLocator) {
        return new RemovePageVersionCommand(this.pageManager, this.permissionManager, pageLocator);
    }

    @Override
    public PageLocator getIdPageLocator(long pageId) {
        return new IdPageLocator(this.pageManager, pageId);
    }

    @Override
    public PageLocator getTitleAndSpaceKeyPageLocator(String spaceKey, String title) {
        return new TitleAndSpaceKeyPageLocator(this.pageManager, spaceKey, title);
    }

    @Override
    public PageLocator getPageVersionLocator(long pageId, int version) {
        return new IdAndVersionPageLocator(this.pageManager, pageId, version);
    }

    @Override
    public ServiceCommand newRevertPageCommand(PageLocator pageToRevert, int version, String revertComment, boolean revertTitle) {
        return new RevertPageCommand(this.pageManager, this.permissionManager, pageToRevert, revertComment, version, revertTitle);
    }

    @Override
    public ServiceCommand newCreatePageCommand(PageProvider provider, ContentPermissionProvider permissionProvider, CreateContextProvider contextProvider, Page draft, User user, boolean notifySelf) {
        return new CreatePageCommandImpl(this.pageManager, this.permissionManager, this.contentPermissionManager, this.draftService, this.attachmentManager, this.notificationManager, this.eventPublisher, provider, permissionProvider, contextProvider, draft, user, notifySelf, this.contentPropertyManager, this.labelManager, this.getDraftsTransitionHelper());
    }

    @Override
    public ServiceCommand newCreatePageCommand(PageProvider provider, ContentPermissionProvider permissionProvider, CreateContextProvider contextProvider, ContentEntityObject draft, User user, boolean notifySelf) {
        return new CreatePageCommandImpl(this.pageManager, this.permissionManager, this.contentPermissionManager, this.draftService, this.attachmentManager, this.notificationManager, this.eventPublisher, provider, permissionProvider, contextProvider, draft, user, notifySelf, this.contentPropertyManager, this.labelManager, this.getDraftsTransitionHelper());
    }

    @Override
    public ServiceCommand newCreatePageCommand(PageProvider provider, ContentPermissionProvider permissionProvider, CreateContextProvider contextProvider, Draft draft, User user, boolean notifySelf) {
        return new CreatePageCommandImpl(this.pageManager, this.permissionManager, this.contentPermissionManager, this.draftService, this.attachmentManager, this.notificationManager, this.eventPublisher, provider, permissionProvider, contextProvider, draft, user, notifySelf, this.contentPropertyManager, this.labelManager, this.getDraftsTransitionHelper());
    }

    @Override
    public ServiceCommand newCreatePageCommandFromExisting(PageProvider provider, ContentPermissionProvider permissionProvider, Draft draft, User user, boolean notifySelf, SpaceProvider spaceProvider) {
        return new CreatePageFromExistingCommandImpl(this.pageManager, this.permissionManager, this.contentPermissionManager, this.draftService, this.attachmentManager, this.notificationManager, this.eventPublisher, provider, permissionProvider, CreateContextProvider.EMPTY_CONTEXT_PROVIDER, draft, user, notifySelf, spaceProvider, this.contentPropertyManager, this.labelManager, this.getDraftsTransitionHelper());
    }

    @Override
    public ServiceCommand newCreatePageCommandFromExisting(PageProvider provider, ContentPermissionProvider permissionProvider, ContentEntityObject draft, User user, boolean notifySelf, SpaceProvider spaceProvider) {
        return new CreatePageFromExistingCommandImpl(this.pageManager, this.permissionManager, this.contentPermissionManager, this.draftService, this.attachmentManager, this.notificationManager, this.eventPublisher, provider, permissionProvider, CreateContextProvider.EMPTY_CONTEXT_PROVIDER, draft, user, notifySelf, spaceProvider, this.contentPropertyManager, this.labelManager, this.getDraftsTransitionHelper());
    }

    @Override
    public ServiceCommand newCreatePageCommandFromExisting(PageProvider pageProvider, ContentPermissionProvider permissionProvider, CreateContextProvider contextProvider, ContentEntityObject draft, User user, boolean notifySelf, SpaceProvider spaceProvider) {
        return new CreatePageFromExistingCommandImpl(this.pageManager, this.permissionManager, this.contentPermissionManager, this.draftService, this.attachmentManager, this.notificationManager, this.eventPublisher, pageProvider, permissionProvider, contextProvider, draft, user, notifySelf, spaceProvider, this.contentPropertyManager, this.labelManager, this.getDraftsTransitionHelper());
    }

    @Override
    public ServiceCommand newCreatePageCommandFromExisting(PageProvider pageProvider, ContentPermissionProvider permissionProvider, CreateContextProvider contextProvider, Page draft, User user, boolean notifySelf, SpaceProvider spaceProvider) {
        return new CreatePageFromExistingCommandImpl(this.pageManager, this.permissionManager, this.contentPermissionManager, this.draftService, this.attachmentManager, this.notificationManager, this.eventPublisher, pageProvider, permissionProvider, contextProvider, draft, user, notifySelf, spaceProvider, this.contentPropertyManager, this.labelManager, this.getDraftsTransitionHelper());
    }

    public void setPermissionManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    private DraftsTransitionHelper getDraftsTransitionHelper() {
        return (DraftsTransitionHelper)this.draftsTransitionHelperSupplier.get();
    }
}


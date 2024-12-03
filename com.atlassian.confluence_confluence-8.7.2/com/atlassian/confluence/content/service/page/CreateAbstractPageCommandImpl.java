/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.ContentStatus
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.content.service.page;

import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.content.Content;
import com.atlassian.confluence.content.service.DraftService;
import com.atlassian.confluence.content.service.page.ContentPermissionProvider;
import com.atlassian.confluence.content.service.page.CreateContextProvider;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.ContentPermissionManager;
import com.atlassian.confluence.core.ContentPropertyManager;
import com.atlassian.confluence.core.DefaultSaveContext;
import com.atlassian.confluence.core.SaveContext;
import com.atlassian.confluence.core.service.AbstractServiceCommand;
import com.atlassian.confluence.core.service.ServiceCommandValidator;
import com.atlassian.confluence.event.events.types.Created;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.pages.DraftsTransitionHelper;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.PageUpdateTrigger;
import com.atlassian.confluence.util.LabelUtil;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.user.User;
import java.util.Date;

public abstract class CreateAbstractPageCommandImpl
extends AbstractServiceCommand {
    private static final SaveContext SAVE_CONTEXT_SUPPRESS_EVENTS = ((DefaultSaveContext.Builder)DefaultSaveContext.builder().updateLastModifier(true).suppressEvents(true)).build();
    protected PageManager pageManager;
    protected ContentPermissionManager contentPermissionManager;
    protected AttachmentManager attachmentManager;
    protected NotificationManager notificationManager;
    protected EventPublisher eventPublisher;
    protected ContentPermissionProvider permissionProvider;
    protected final CreateContextProvider contextProvider;
    protected final DraftService draftService;
    protected final ContentPropertyManager contentPropertyManager;
    protected final LabelManager labelManager;
    protected final DraftsTransitionHelper draftsTransitionHelper;
    protected User user;
    protected boolean notifySelf;
    protected AbstractPage createdContent;
    protected final ContentEntityObject draft;

    @Deprecated
    protected CreateAbstractPageCommandImpl(PageManager pageManager, ContentPermissionManager contentPermissionManager, DraftService draftService, AttachmentManager attachmentManager, NotificationManager notificationManager, EventPublisher eventPublisher, ContentPermissionProvider permissionProvider, CreateContextProvider contextProvider, Draft draft, User user, boolean notifySelf, ContentPropertyManager contentPropertyManager, LabelManager labelManager, DraftsTransitionHelper draftsTransitionHelper) {
        this(pageManager, contentPermissionManager, draftService, attachmentManager, notificationManager, eventPublisher, permissionProvider, contextProvider, (ContentEntityObject)draft, user, notifySelf, contentPropertyManager, labelManager, draftsTransitionHelper);
    }

    @Deprecated
    protected CreateAbstractPageCommandImpl(PageManager pageManager, ContentPermissionManager contentPermissionManager, DraftService draftService, AttachmentManager attachmentManager, NotificationManager notificationManager, EventPublisher eventPublisher, ContentPermissionProvider permissionProvider, CreateContextProvider contextProvider, ContentEntityObject draft, User user, boolean notifySelf, ContentPropertyManager contentPropertyManager, LabelManager labelManager, DraftsTransitionHelper draftsTransitionHelper) {
        this.pageManager = pageManager;
        this.contentPermissionManager = contentPermissionManager;
        this.attachmentManager = attachmentManager;
        this.notificationManager = notificationManager;
        this.eventPublisher = eventPublisher;
        this.permissionProvider = permissionProvider;
        this.contextProvider = contextProvider;
        this.draft = draft;
        this.user = user;
        this.notifySelf = notifySelf;
        this.draftService = draftService;
        this.contentPropertyManager = contentPropertyManager;
        this.labelManager = labelManager;
        this.draftsTransitionHelper = draftsTransitionHelper;
    }

    protected CreateAbstractPageCommandImpl(PageManager pageManager, ContentPermissionManager contentPermissionManager, DraftService draftService, AttachmentManager attachmentManager, NotificationManager notificationManager, EventPublisher eventPublisher, ContentPermissionProvider permissionProvider, CreateContextProvider contextProvider, AbstractPage draft, User user, boolean notifySelf, ContentPropertyManager contentPropertyManager, LabelManager labelManager, DraftsTransitionHelper draftsTransitionHelper) {
        this(pageManager, contentPermissionManager, draftService, attachmentManager, notificationManager, eventPublisher, permissionProvider, contextProvider, (ContentEntityObject)draft, user, notifySelf, contentPropertyManager, labelManager, draftsTransitionHelper);
    }

    @Override
    protected void validateInternal(ServiceCommandValidator validator) {
        AbstractPage page = this.getContent();
        if (page == null) {
            validator.addValidationError("createpage.page.null", new Object[0]);
        }
    }

    @Override
    protected void executeInternal() {
        AbstractPage content = null;
        if (this.draft == null || this.draft.getId() == 0L) {
            content = this.getContent();
            this.pageManager.saveContentEntity(content, SAVE_CONTEXT_SUPPRESS_EVENTS);
            LabelUtil.syncState((String)((Object)this.contextProvider.getContext().get("labelsString")), this.labelManager, this.user, content, true);
        } else if (!DraftsTransitionHelper.isLegacyDraft(this.draft)) {
            if (this.draft.hasPermissions("Share")) {
                this.draft.getContentPermissionSet("Share").contentPermissionsCopy().forEach(this.contentPermissionManager::removeContentPermission);
            }
            AbstractPage draftClone = this.cloneNewDraft((AbstractPage)this.draft);
            this.pageManager.saveContentEntity(draftClone, DefaultSaveContext.DRAFT);
            this.draft.setContentStatus(ContentStatus.CURRENT.getValue());
            Date postingDate = (Date)this.contextProvider.getContext().get("PostingDate");
            if (postingDate != null) {
                this.draft.setCreationDate(postingDate);
            }
            DefaultSaveContext saveContextSuppressEvents = ((DefaultSaveContext.Builder)((DefaultSaveContext.Builder)DefaultSaveContext.builder().updateLastModifier(true).suppressEvents(true)).updateTrigger(PageUpdateTrigger.EDIT_PAGE)).build();
            this.pageManager.saveContentEntity(this.draft, saveContextSuppressEvents);
            content = (AbstractPage)this.draft;
        } else {
            content = this.getContent();
            this.pageManager.saveContentEntity(content, SAVE_CONTEXT_SUPPRESS_EVENTS);
            this.draftsTransitionHelper.transitionContentObjects(this.draft, content);
            this.draftService.removeDraft(Content.UNSET, this.draft.getId());
        }
        this.createdContent = content;
        this.eventPublisher.publish((Object)this.getCreateEvent());
    }

    private AbstractPage cloneNewDraft(AbstractPage draft) {
        AbstractPage clone = (AbstractPage)draft.clone();
        clone.convertToHistoricalVersion();
        clone.setContentStatus(ContentStatus.DRAFT.getValue());
        clone.setSpace(draft.getSpace());
        clone.setOriginalVersionPage(draft);
        return clone;
    }

    protected AbstractPage getCreatedContent() {
        return this.createdContent;
    }

    protected abstract AbstractPage getContent();

    protected abstract Created getCreateEvent();
}


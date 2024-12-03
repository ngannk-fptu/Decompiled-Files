/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.service.PageService
 *  com.atlassian.confluence.content.service.page.ContentPermissionProvider
 *  com.atlassian.confluence.content.service.page.CreateContextProvider
 *  com.atlassian.confluence.content.service.page.CreatePageCommand
 *  com.atlassian.confluence.content.service.page.SimpleContentPermissionProvider
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.ContentPropertyManager
 *  com.atlassian.confluence.core.DefaultSaveContext
 *  com.atlassian.confluence.core.service.NotValidException
 *  com.atlassian.confluence.core.service.ServiceCommand
 *  com.atlassian.confluence.core.service.ValidationError
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.pages.Draft
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.plugins.emailgateway.api.AttachmentConverterService
 *  com.atlassian.confluence.plugins.emailgateway.api.EmailContentParser
 *  com.atlassian.confluence.plugins.emailgateway.api.EmailToContentConverter
 *  com.atlassian.confluence.plugins.emailgateway.api.StagedEmailThread
 *  com.atlassian.confluence.plugins.emailgateway.api.UsersByEmailService
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.validation.MessageHolder
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.user.EntityException
 *  com.atlassian.user.User
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.confluence.plugins.emailtopage;

import com.atlassian.confluence.content.service.PageService;
import com.atlassian.confluence.content.service.page.ContentPermissionProvider;
import com.atlassian.confluence.content.service.page.CreateContextProvider;
import com.atlassian.confluence.content.service.page.CreatePageCommand;
import com.atlassian.confluence.content.service.page.SimpleContentPermissionProvider;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.ContentPropertyManager;
import com.atlassian.confluence.core.DefaultSaveContext;
import com.atlassian.confluence.core.service.NotValidException;
import com.atlassian.confluence.core.service.ServiceCommand;
import com.atlassian.confluence.core.service.ValidationError;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.emailgateway.api.AttachmentConverterService;
import com.atlassian.confluence.plugins.emailgateway.api.EmailContentParser;
import com.atlassian.confluence.plugins.emailgateway.api.EmailToContentConverter;
import com.atlassian.confluence.plugins.emailgateway.api.StagedEmailThread;
import com.atlassian.confluence.plugins.emailgateway.api.UsersByEmailService;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.validation.MessageHolder;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.user.EntityException;
import com.atlassian.user.User;
import com.google.common.collect.ImmutableList;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DefaultEmailToPageConverter
implements EmailToContentConverter<Page> {
    private static final String FROM_EMAIL_CONTENT_PROPERTY = "created.from.email";
    private final UserAccessor userAccessor;
    private final UsersByEmailService usersByEmailService;
    private final PageService pageService;
    private final SpaceManager spaceManager;
    private final EmailContentParser emailContentParser;
    private final ContentPropertyManager contentPropertyManager;
    private final PageManager pageManager;
    private final PermissionManager permissionManager;
    private final I18nResolver i18nResolver;
    private final AttachmentManager attachmentManager;
    private final AttachmentConverterService attachmentConverterService;

    public DefaultEmailToPageConverter(UserAccessor userAccessor, UsersByEmailService usersByEmailService, PageService pageService, SpaceManager spaceManager, EmailContentParser emailContentParser, ContentPropertyManager contentPropertyManager, PageManager pageManager, PermissionManager permissionManager, I18nResolver i18nResolver, AttachmentManager attachmentManager, AttachmentConverterService attachmentConverterService) {
        this.userAccessor = userAccessor;
        this.usersByEmailService = usersByEmailService;
        this.pageService = pageService;
        this.spaceManager = spaceManager;
        this.emailContentParser = emailContentParser;
        this.contentPropertyManager = contentPropertyManager;
        this.pageManager = pageManager;
        this.permissionManager = permissionManager;
        this.i18nResolver = i18nResolver;
        this.attachmentManager = attachmentManager;
        this.attachmentConverterService = attachmentConverterService;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public Page publish(StagedEmailThread emailThread, MessageHolder messageHolder) {
        User creator;
        try {
            creator = this.usersByEmailService.getUniqueUserByEmail(emailThread.getSender());
        }
        catch (EntityException e) {
            throw new NotValidException(e.getMessage(), (Throwable)e);
        }
        boolean notifySelf = this.userAccessor.getConfluenceUserPreferences(creator).isWatchingOwnContent();
        String spaceKey = emailThread.getSpaceKey();
        Space space = this.spaceManager.getSpace(spaceKey);
        if (space == null) {
            throw new NotValidException("Space does not exist");
        }
        String pageTitle = this.emailContentParser.parseSubject(emailThread.getSubject());
        String content = this.emailContentParser.parseContent(emailThread.getReceivedEmail());
        Page page = this.pageManager.getPage(spaceKey, pageTitle);
        if (page != null) {
            if (!Boolean.parseBoolean(this.contentPropertyManager.getStringProperty((ContentEntityObject)page, FROM_EMAIL_CONTENT_PROPERTY))) throw new NotValidException("Non-Email Page already exists with this title");
            if (!this.permissionManager.hasPermissionNoExemptions(this.getRemoteUser(), Permission.EDIT, (Object)page)) {
                throw new NotValidException(this.i18nResolver.getText("email.to.page.create.not.permitted", new Serializable[]{page.getSpace().getDisplayTitle()}));
            }
            try {
                AbstractPage existingPage = (AbstractPage)page.clone();
                this.pageManager.refreshContentEntity((ContentEntityObject)page);
                page.setBodyAsString(content);
                page.setVersionComment(this.i18nResolver.getText("email.to.page.version.comment"));
                this.attachmentManager.removeAttachments((List)ImmutableList.copyOf((Collection)page.getAttachments()));
                this.pageManager.saveContentEntity((ContentEntityObject)page, (ContentEntityObject)existingPage, DefaultSaveContext.DEFAULT);
            }
            catch (Exception e) {
                throw new NotValidException("Clone of existing page not supported", (Throwable)e);
            }
        } else {
            page = this.createPage(this.userAccessor.getUserByName(creator.getName()), content, space, pageTitle);
            page = this.savePage(creator, notifySelf, page, messageHolder);
            this.contentPropertyManager.setStringProperty((ContentEntityObject)page, FROM_EMAIL_CONTENT_PROPERTY, Boolean.TRUE.toString());
        }
        this.attachmentConverterService.attachTo((ContentEntityObject)page, emailThread.getAttachments());
        return page;
    }

    private User getRemoteUser() {
        return AuthenticatedUserThreadLocal.get();
    }

    private Page savePage(User creator, boolean notifySelf, Page page, MessageHolder messageHolder) {
        SimpleContentPermissionProvider permissionProvider = new SimpleContentPermissionProvider();
        permissionProvider.setEditPermissions(Collections.emptyList());
        permissionProvider.setViewPermissions(Collections.emptyList());
        CreatePageCommand createPageCommand = (CreatePageCommand)this.pageService.newCreatePageCommand(() -> page, (ContentPermissionProvider)permissionProvider, CreateContextProvider.EMPTY_CONTEXT_PROVIDER, (Draft)null, creator, notifySelf);
        if (!createPageCommand.isAuthorized()) {
            throw new NotValidException(this.i18nResolver.getText("email.to.page.create.not.permitted", new Serializable[]{page.getSpace().getDisplayTitle()}));
        }
        if (!this.validate(messageHolder, (ServiceCommand)createPageCommand)) {
            throw new NotValidException(this.i18nResolver.getText("email.to.page.create.not.valid", new Serializable[]{page.getSpace().getDisplayTitle()}));
        }
        createPageCommand.execute();
        return createPageCommand.getCreatedPage();
    }

    public boolean validate(MessageHolder messageHolder, ServiceCommand command) {
        if (!command.isAuthorized()) {
            messageHolder.addActionError("command.action.auth", new Object[0]);
            return false;
        }
        if (!command.isValid()) {
            Collection validationErrors = command.getValidationErrors();
            for (ValidationError e : validationErrors) {
                messageHolder.addActionError(e.getMessageKey(), e.getArgs());
            }
            return false;
        }
        return true;
    }

    private Page createPage(ConfluenceUser creator, String content, Space space, String pageTitle) {
        Page page = new Page();
        page.setTitle(pageTitle);
        page.setCreator(creator);
        page.setBodyAsString(content);
        page.setSpace(space);
        return page;
    }
}


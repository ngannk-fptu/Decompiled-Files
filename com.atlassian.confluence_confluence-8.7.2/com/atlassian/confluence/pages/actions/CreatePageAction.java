/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.exception.InfrastructureException
 *  com.atlassian.user.User
 *  org.springframework.dao.DataIntegrityViolationException
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.service.DraftService;
import com.atlassian.confluence.content.service.page.CreateContextProvider;
import com.atlassian.confluence.content.service.page.CreatePageCommand;
import com.atlassian.confluence.content.service.page.PageProvider;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.service.CommandActionHelper;
import com.atlassian.confluence.core.service.ServiceCommand;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.actions.AbstractCreatePageAction;
import com.atlassian.confluence.pages.exceptions.ExternalChangesException;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.core.exception.InfrastructureException;
import com.atlassian.user.User;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import org.springframework.dao.DataIntegrityViolationException;

public class CreatePageAction
extends AbstractCreatePageAction {
    private String linkCreation = "";
    private CommandActionHelper helper;
    protected SimplePageProvider pageProvider;

    @Override
    protected CommandActionHelper getCommandActionHelper() {
        if (this.helper == null) {
            this.helper = new CommandActionHelper(this.createCommand());
        }
        return this.helper;
    }

    @Override
    protected void initialiseProvider(AbstractPage abstractPage) {
        this.pageProvider.setPage((Page)abstractPage);
    }

    protected ServiceCommand createCommand() {
        ConfluenceUser remoteUser = this.getAuthenticatedUser();
        boolean notifySelf = false;
        if (remoteUser != null) {
            notifySelf = this.userAccessor.getConfluenceUserPreferences(remoteUser).isWatchingOwnContent();
        }
        this.pageProvider = new SimplePageProvider();
        return this.pageService.newCreatePageCommand((PageProvider)this.pageProvider, null, (CreateContextProvider)this.contextProvider, this.getDraftAsCEO(), (User)remoteUser, notifySelf);
    }

    private boolean userCanViewAndEditPage() {
        Optional<AbstractPage> targetPage = this.getTargetObject();
        return targetPage.map(page -> super.isPermitted() && this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.EDIT, page)).orElse(false);
    }

    private Optional<AbstractPage> getTargetObject() {
        Page page = this.pageProvider.getPage();
        if (page != null) {
            return Optional.of(page.getLatestVersion());
        }
        return Optional.empty();
    }

    @Override
    public void validate() {
        super.validate();
        Page titleAndSpacePage = new Page();
        try {
            titleAndSpacePage.setTitle(this.getTitle());
            titleAndSpacePage.setSpace(this.getNewSpace());
            this.getCommandActionHelper();
            this.pageProvider.setPage(titleAndSpacePage);
            if (this.userCanViewAndEditPage()) {
                this.getCommandActionHelper().validate(this);
            }
        }
        catch (InfrastructureException | DataIntegrityViolationException throwable) {
            // empty catch block
        }
    }

    @Override
    protected Draft createDraft() {
        return this.draftService.createNewContentDraft(this.getSpaceKey(), DraftService.DraftType.PAGE);
    }

    @Override
    public boolean isPermitted() {
        Page spaceOnlyPage = new Page();
        spaceOnlyPage.setSpace(this.getNewSpace());
        this.getCommandActionHelper();
        this.pageProvider.setPage(spaceOnlyPage);
        return this.getCommandActionHelper().isAuthorized() && this.hasDraftPermission();
    }

    @Override
    protected AbstractPage getCreatedAbstractPage() {
        return ((CreatePageCommand)this.getCommandActionHelper().getCommand()).getCreatedPage();
    }

    public void setLinkCreation(String linkCreation) {
        this.linkCreation = linkCreation;
    }

    public String getLinkCreation() {
        return this.linkCreation;
    }

    public void setEncodedTitle(String encodedTitle) {
        this.setTitle(GeneralUtil.base64Decode(encodedTitle));
    }

    public String doTemplateDefault() throws Exception {
        this.validateDuplicatePageTitle();
        String returnValue = this.doDefault();
        if (this.hasErrors()) {
            return "error";
        }
        return returnValue;
    }

    @Override
    public String doDefault() throws Exception {
        try {
            this.populateParentPageTitleField();
            if ((this.linkCreation.equalsIgnoreCase("yes") || this.linkCreation.equalsIgnoreCase("true")) && this.getPage() != null) {
                return "already-exists";
            }
            String result = super.doDefault();
            return result;
        }
        catch (ExternalChangesException e) {
            return "activity-unavailable";
        }
    }

    protected void populateParentPageTitleField() {
        Long parentPageId;
        Page parentPage;
        ContentEntityObject draftContentEntityObject = this.getDraftAsCEO();
        Page page = parentPage = this.collaborativeEditingHelper.isSharedDraftsFeatureEnabled(this.getSpaceKey()) && draftContentEntityObject != null ? ((Page)draftContentEntityObject).getParent() : this.getFromPage();
        if (draftContentEntityObject != null && (parentPageId = Long.valueOf(draftContentEntityObject.getProperties().getLongProperty("legacy.draft.parent.id", 0L))) != 0L && (parentPage == null || parentPage.getId() != parentPageId.longValue())) {
            parentPage = this.pageManager.getPage(parentPageId);
        }
        if (parentPage == null) {
            return;
        }
        this.setFromPage(parentPage);
        parentPage = parentPage.getOriginalVersionPage() == null ? parentPage : parentPage.getOriginalVersionPage();
        this.parentPageString = parentPage.getTitle();
    }

    @Override
    public void createPage() throws XhtmlException, IOException {
        super.createPage();
        this.assignSpace(this.getPage(), this.getNewSpace());
        this.assignParentPage(this.getPage(), this.getParentPage());
    }

    @Override
    protected AbstractPage getPageToCreate() {
        return new Page();
    }

    @Override
    protected void assignSpace(ContentEntityObject page, Space space) {
        super.assignSpace(page, space);
        if (space != null) {
            ((Page)page).setSpace(space);
        }
    }

    @Override
    protected void assignParentPage(ContentEntityObject page, Page parentPage) {
        if (parentPage != null) {
            parentPage.addChild((Page)page);
        }
    }

    public Set getInheritedViewPermissions() {
        if (this.getParentPage() != null) {
            return this.contentPermissionManager.getViewContentPermissions(this.getParentPage());
        }
        return Collections.EMPTY_SET;
    }

    protected static class SimplePageProvider
    implements PageProvider {
        private Page page;

        protected SimplePageProvider() {
        }

        void setPage(Page page) {
            this.page = page;
        }

        @Override
        public Page getPage() {
            return this.page;
        }
    }
}


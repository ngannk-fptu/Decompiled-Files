/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.security.XsrfProtectionExcluded
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.user.actions;

import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.admin.criteria.MailServerExistsCriteria;
import com.atlassian.confluence.admin.criteria.WritableDirectoryExistsCriteria;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.event.events.admin.SearchUsersEvent;
import com.atlassian.confluence.security.login.LoginInfo;
import com.atlassian.confluence.security.login.LoginManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.actions.AbstractSearchCrowdUsersAction;
import com.atlassian.confluence.util.SearchTermType;
import com.atlassian.confluence.util.breadcrumbs.Breadcrumb;
import com.atlassian.confluence.util.breadcrumbs.BreadcrumbAware;
import com.atlassian.confluence.util.breadcrumbs.UserAdminActionBreadcrumb;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;

public class SearchUsersAction
extends AbstractSearchCrowdUsersAction
implements BreadcrumbAware {
    private BandanaManager bandanaManager;
    private MailServerExistsCriteria mailServerExistsCriteria;
    private WritableDirectoryExistsCriteria writableDirectoryExistsCriteria;
    private LoginManager loginManager;
    private EventPublisher eventPublisher;

    public SearchUsersAction() {
        this.setPageResponse((PageResponse<ConfluenceUser>)PageResponseImpl.empty((boolean)false));
        this.setShowUnlicensedUsers(true);
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() {
        return "success";
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String doCreateUserForm() {
        this.setSelectedTab("create");
        return "input";
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String doInviteUserForm() {
        this.setSelectedTab("invite");
        return "input";
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String doSignUpUserForm() {
        this.setSelectedTab("signup");
        return "input";
    }

    public boolean isSendEmailDefault() {
        Boolean sendEmail = (Boolean)this.bandanaManager.getValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, "sendEmail");
        return sendEmail == null || sendEmail != false;
    }

    protected void setSendEmailDefault(boolean sendEmail) {
        this.bandanaManager.setValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, "sendEmail", (Object)sendEmail);
    }

    public boolean isMailServerConfigured() {
        return this.mailServerExistsCriteria.isMet();
    }

    public boolean canAddUsers() {
        return this.permissionManager.isConfluenceAdministrator(this.getAuthenticatedUser()) && this.writableDirectoryExistsCriteria.isMet();
    }

    @Override
    public boolean isPermitted() {
        return this.permissionManager.isConfluenceAdministrator(this.getAuthenticatedUser()) || this.permissionManager.isSystemAdministrator(this.getAuthenticatedUser());
    }

    public void setMailServerExistsCriteria(MailServerExistsCriteria mailServerExistsCriteria) {
        this.mailServerExistsCriteria = mailServerExistsCriteria;
    }

    public void setWritableDirectoryExistsCriteria(WritableDirectoryExistsCriteria writableDirectoryExistsCriteria) {
        this.writableDirectoryExistsCriteria = writableDirectoryExistsCriteria;
    }

    public void setBandanaManager(BandanaManager bandanaManager) {
        this.bandanaManager = bandanaManager;
    }

    @Override
    public Breadcrumb getBreadcrumb() {
        return new UserAdminActionBreadcrumb(this, null);
    }

    public EventPublisher getEventPublisher() {
        return this.eventPublisher;
    }

    public void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public LoginInfo getLoginInfo(String userName) {
        return this.loginManager.getLoginInfo(this.getUserByName(userName));
    }

    @Override
    public void setLoginManager(LoginManager loginManager) {
        super.setLoginManager(loginManager);
        this.loginManager = loginManager;
    }

    private SearchTermType getSearchTermType(String searchTerm) {
        if (searchTerm == null) {
            return SearchTermType.ADVANCED;
        }
        if ("*".equals(searchTerm)) {
            return SearchTermType.ALL;
        }
        return SearchTermType.FILTERED;
    }

    public void publishEvent() {
        this.eventPublisher.publish((Object)new SearchUsersEvent(!this.isShowUnlicensedUsers(), this.getSearchTermType(this.getSearchTerm())));
    }

    @Override
    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    @XsrfProtectionExcluded
    public String doUserSearch() {
        this.publishEvent();
        return super.doUserSearch();
    }
}


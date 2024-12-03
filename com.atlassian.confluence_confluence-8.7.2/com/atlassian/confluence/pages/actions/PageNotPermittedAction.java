/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.security.XsrfProtectionExcluded
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableSet
 *  javax.servlet.http.HttpServletRequest
 *  javax.ws.rs.core.UriBuilder
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.struts2.ServletActionContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.actions.AbstractCreateAndEditPageAction;
import com.atlassian.confluence.pages.actions.PageAware;
import com.atlassian.confluence.pages.actions.ViewPageAction;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.SeraphUtils;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.UriBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PageNotPermittedAction
extends ConfluenceActionSupport
implements PageAware {
    private static final Logger log = LoggerFactory.getLogger(PageNotPermittedAction.class);
    private static final String REQUEST_ACCESS_PLUGIN_KEY = "com.atlassian.confluence.plugins.confluence-request-access-plugin";
    @VisibleForTesting
    static final String NO_SPACE_PERMISSION_REDIRECT_RESULT = "noSpaceEditPermission";
    @VisibleForTesting
    static final String NO_EDIT_PERMISSION_REDIRECT_RESULT = "noEditPermission";
    @VisibleForTesting
    static final String REQUEST_EDIT_ACCESS_URL_PARAMETER = "requestEditAccess";
    @VisibleForTesting
    static final String SPACE_EDITING_RESTRICTION_URL_PARAMETER = "spaceEditingRestriction";
    @VisibleForTesting
    static final String CREATE_PAGE_ACTION = "/pages/createpage.action";
    @VisibleForTesting
    static final String CREATE_BLOG_POST_ACTION = "/pages/createblogpost.action";
    @VisibleForTesting
    static final String EDIT_PAGE_ACTION = "/pages/editpage.action";
    @VisibleForTesting
    static final String EDIT_BLOG_POST_ACTION = "/pages/editblogpost.action";
    private static final Set<String> EDIT_ACTIONS = ImmutableSet.of((Object)"/pages/createpage.action", (Object)"/pages/createblogpost.action", (Object)"/pages/editpage.action", (Object)"/pages/editblogpost.action");
    private long pageId;
    private long draftId;
    private String spaceKey;
    private String title;
    private PageManager pageManager;
    private AbstractPage page;

    @PermittedMethods(value={HttpMethod.ANY_METHOD})
    @XsrfProtectionExcluded
    public String execute() {
        boolean targetObjectIsDraft = this.getTargetObject().map(ContentEntityObject::isDraft).orElse(false);
        if (this.getAuthenticatedUser() == null) {
            return "login";
        }
        if (!targetObjectIsDraft) {
            if (this.isRequestToCreateOrEditPage() && this.userCanViewButNotEditSpace() && this.userCanViewPage()) {
                return NO_SPACE_PERMISSION_REDIRECT_RESULT;
            }
            if (this.isRequestToCreateOrEditPage() && this.userCanViewButNotEditPage()) {
                return NO_EDIT_PERMISSION_REDIRECT_RESULT;
            }
        }
        return "success";
    }

    public String getNoPageEditPermissionRedirectUrl() {
        UriBuilder uriBuilder = UriBuilder.fromUri((String)(this.page != null ? this.page.getUrlPath() : this.getTargetUrlPath()));
        if (!this.accessModeService.isReadOnlyAccessModeEnabled()) {
            uriBuilder.queryParam(REQUEST_EDIT_ACCESS_URL_PARAMETER, new Object[]{"true"});
        }
        return uriBuilder.build(new Object[0]).toString();
    }

    public String getNoSpaceEditPermissionRedirectUrl() {
        UriBuilder uriBuilder = UriBuilder.fromUri((String)(this.page != null ? this.page.getUrlPath() : this.getTargetUrlPath()));
        if (!this.accessModeService.isReadOnlyAccessModeEnabled()) {
            uriBuilder.queryParam(SPACE_EDITING_RESTRICTION_URL_PARAMETER, new Object[]{"true"});
        }
        return uriBuilder.build(new Object[0]).toString();
    }

    public long getPageId() {
        return this.pageId;
    }

    public long getDraftId() {
        return this.draftId;
    }

    public void setPageId(long pageId) {
        this.pageId = pageId;
    }

    public void setDraftId(long draftId) {
        this.draftId = draftId;
    }

    @Override
    public AbstractPage getPage() {
        return this.page;
    }

    @Override
    public void setPage(AbstractPage page) {
        this.page = page;
    }

    @Override
    public boolean isPageRequired() {
        return false;
    }

    @Override
    public boolean isLatestVersionRequired() {
        return false;
    }

    @Override
    public boolean isViewPermissionRequired() {
        return false;
    }

    @Deprecated
    public boolean isPermittedToViewCurrentPage() {
        return !this.canUserRequestAccessOnCurrentPage();
    }

    public boolean canUserRequestAccessOnCurrentPage() {
        if (this.page == null) {
            return false;
        }
        ConfluenceUser currentUser = this.getAuthenticatedUser();
        boolean result = false;
        if (this.page.hasPermissions("Edit")) {
            boolean bl = result = !this.page.getContentPermissionSet("Edit").isPermitted(currentUser);
        }
        if (this.page.hasPermissions("View")) {
            result = result || !this.page.getContentPermissionSet("View").isPermitted(currentUser);
        }
        return result;
    }

    public String getRequestAccessType() {
        return this.getActionStack().flatMap(action -> this.getRequestAccessTypeFromAction(action).stream()).findFirst().orElseGet(() -> {
            log.warn("Could not determine request action type from Struts Action stack");
            return "";
        });
    }

    private Stream<Object> getActionStack() {
        return Optional.ofNullable(ServletActionContext.getValueStack((HttpServletRequest)this.getCurrentRequest())).stream().flatMap(valueStack -> valueStack.getRoot().stream());
    }

    private Optional<String> getRequestAccessTypeFromAction(Object action) {
        if (action instanceof ViewPageAction) {
            return Optional.of("view");
        }
        if (action instanceof AbstractCreateAndEditPageAction) {
            return Optional.of("edit");
        }
        return Optional.empty();
    }

    public String getLoginUrl() {
        HttpServletRequest request = ServletActionContext.getRequest();
        String loginURL = SeraphUtils.getLoginURL(request);
        String contextPath = StringUtils.defaultString((String)request.getContextPath());
        if (log.isDebugEnabled()) {
            log.debug("Seraph login.url is " + loginURL);
        }
        if (StringUtils.isNotEmpty((CharSequence)contextPath) && StringUtils.defaultString((String)loginURL).startsWith(contextPath)) {
            loginURL = loginURL.substring(contextPath.length());
        }
        return loginURL;
    }

    public boolean isRequestAccessPluginEnabled() {
        return this.pluginAccessor.isPluginEnabled(REQUEST_ACCESS_PLUGIN_KEY);
    }

    public PageManager getPageManager() {
        return this.pageManager;
    }

    public void setPageManager(PageManager pageManager) {
        this.pageManager = pageManager;
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }

    public void setSpaceKey(String spaceKey) {
        this.spaceKey = spaceKey;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String pageTitle) {
        this.title = pageTitle;
    }

    private boolean isRequestToCreateOrEditPage() {
        return EDIT_ACTIONS.contains(this.getCurrentRequest().getServletPath());
    }

    private boolean userCanViewButNotEditSpace() {
        Optional<AbstractPage> targetObject = this.getTargetObject();
        Optional<Space> targetSpace = targetObject.map(SpaceContentEntityObject::getSpace);
        boolean isAPage = targetObject.map(page -> page instanceof Page).orElse(false);
        boolean isABlogPost = targetObject.map(page -> page instanceof BlogPost).orElse(false);
        return targetSpace.map(space -> this.spacePermissionManager.hasPermission("VIEWSPACE", (Space)space, this.getAuthenticatedUser()) && (isAPage && !this.spacePermissionManager.hasPermission("EDITSPACE", (Space)space, this.getAuthenticatedUser()) || isABlogPost && !this.spacePermissionManager.hasPermission("EDITBLOG", (Space)space, this.getAuthenticatedUser()))).orElse(false);
    }

    private boolean userCanViewPage() {
        Optional<AbstractPage> targetPage = this.getTargetObject();
        return targetPage.map(page -> this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.VIEW, page)).orElse(false);
    }

    private boolean userCanViewButNotEditPage() {
        Optional<AbstractPage> targetPage = this.getTargetObject();
        return targetPage.map(page -> this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.VIEW, page) && !this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.EDIT, page)).orElse(false);
    }

    private Optional<AbstractPage> getTargetObject() {
        AbstractPage draft;
        if (this.page != null) {
            return Optional.of(this.page.getLatestVersion());
        }
        if (this.draftId != 0L && (draft = this.pageManager.getAbstractPage(this.draftId)) != null) {
            return Optional.of(draft.getLatestVersion());
        }
        return Optional.empty();
    }

    public String getTargetUrlPath() {
        return this.getTargetObject().isPresent() ? this.getTargetObject().get().getUrlPath() : "";
    }
}


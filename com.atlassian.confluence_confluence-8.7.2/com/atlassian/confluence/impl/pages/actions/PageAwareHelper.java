/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.accessmode.AccessModeService
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.user.User
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.pages.actions;

import com.atlassian.confluence.api.service.accessmode.AccessModeService;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.internal.audit.event.RestrictedPageViewNotPermittedEvent;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.actions.AbstractCreatePageAction;
import com.atlassian.confluence.pages.actions.CreateBlogPostAction;
import com.atlassian.confluence.pages.actions.CreatePageEntryAction;
import com.atlassian.confluence.pages.actions.PageAware;
import com.atlassian.confluence.pages.actions.ViewPageAction;
import com.atlassian.confluence.pages.actions.beans.PageReference;
import com.atlassian.confluence.plugin.webresource.ConfluenceWebResourceManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.settings.CollaborativeEditingHelper;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.user.User;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PageAwareHelper {
    private static final Logger log = LoggerFactory.getLogger(PageAwareHelper.class);
    private static final long MACRO_PLACEHOLDER_TIMEOUT_DEFAULT = 5000L;
    private static final String MACRO_PLACEHOLDER_TIMEOUT = "confluence.macro.placeholder.timeoutMillis";
    private final PageManager pageManager;
    private final ContentEntityManager contentEntityManager;
    private final PermissionManager permissionManager;
    private final SpaceManager spaceManager;
    private final ConfluenceWebResourceManager webResourceManager;
    private final CollaborativeEditingHelper collaborativeEditingHelper;
    private final AccessModeService accessModeService;
    private final EventPublisher eventPublisher;

    public PageAwareHelper(PageManager pageManager, ContentEntityManager contentEntityManager, PermissionManager permissionManager, SpaceManager spaceManager, ConfluenceWebResourceManager webResourceManager, CollaborativeEditingHelper collaborativeEditingHelper, AccessModeService accessModeService, EventPublisher eventPublisher) {
        this.pageManager = Objects.requireNonNull(pageManager);
        this.contentEntityManager = Objects.requireNonNull(contentEntityManager);
        this.permissionManager = Objects.requireNonNull(permissionManager);
        this.spaceManager = Objects.requireNonNull(spaceManager);
        this.webResourceManager = Objects.requireNonNull(webResourceManager);
        this.collaborativeEditingHelper = Objects.requireNonNull(collaborativeEditingHelper);
        this.accessModeService = Objects.requireNonNull(accessModeService);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    public @NonNull Result configure(PageAware pageAware, HttpServletRequest servletRequest) {
        return this.configure(pageAware, servletRequest, arg_0 -> ((HttpServletRequest)servletRequest).getParameter(arg_0));
    }

    public @NonNull Result configure(PageAware pageAware, HttpServletRequest servletRequest, ParameterSource parameterSource) {
        Space space;
        RequestHelper helper = new RequestHelper(parameterSource);
        AbstractPage page = helper.getPage();
        AbstractPage draft = helper.getDraft();
        if (page != null && pageAware.isLatestVersionRequired()) {
            page = page.getLatestVersion();
        } else if (draft != null && pageAware.isLatestVersionRequired() && !draft.getLatestVersion().isDraft()) {
            page = draft.getLatestVersion();
        }
        if (log.isDebugEnabled()) {
            log.debug("Set page on PageAware " + pageAware.getClass().getName() + ": " + page);
        }
        if (page != null) {
            if (page.isDraft() && pageAware instanceof ViewPageAction) {
                return Result.PAGE_NOT_FOUND;
            }
            pageAware.setPage(page);
            if (page.isDeleted() && pageAware.isPageRequired()) {
                PageReference.set(servletRequest, page.getSpaceKey(), page.getTitle());
                return Result.PAGE_NOT_FOUND;
            }
        } else if (pageAware.isPageRequired()) {
            return Result.PAGE_NOT_FOUND;
        }
        if ((space = helper.getSpace(page)) == null && StringUtils.isNotEmpty((CharSequence)helper.getSpaceKeyFromParameter())) {
            return AuthenticatedUserThreadLocal.get() == null ? Result.NOT_PERMITTED : Result.PAGE_NOT_FOUND;
        }
        if (space != null && !this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, space)) {
            if (page != null && pageAware.isPageRequired()) {
                this.eventPublisher.publish((Object)new RestrictedPageViewNotPermittedEvent(page));
            }
            return AuthenticatedUserThreadLocal.get() == null ? Result.NOT_PERMITTED : Result.PAGE_NOT_FOUND;
        }
        if (page != null && pageAware.isPageRequired()) {
            if (pageAware.isViewPermissionRequired() && !this.hasPermission(page, Permission.VIEW)) {
                if (pageAware instanceof ViewPageAction) {
                    this.eventPublisher.publish((Object)new RestrictedPageViewNotPermittedEvent(page));
                }
                return Result.PAGE_NOT_PERMITTED;
            }
            if (pageAware.isEditPermissionRequired() && !this.hasPermission(page, Permission.EDIT)) {
                return Result.PAGE_NOT_PERMITTED;
            }
        }
        if ((pageAware instanceof CreatePageEntryAction || pageAware instanceof CreateBlogPostAction) && !((AbstractCreatePageAction)pageAware).isPermitted()) {
            if (this.accessModeService.isReadOnlyAccessModeEnabled()) {
                return Result.READ_ONLY;
            }
            return Result.PAGE_NOT_PERMITTED;
        }
        if (page != null && space != null) {
            this.populateMetadata(page, space);
        } else if (draft != null) {
            this.webResourceManager.putMetadata("page-id", draft.getIdAsString());
        }
        this.webResourceManager.putMetadata("max-number-editors", String.valueOf(this.collaborativeEditingHelper.getUserLimit()));
        this.webResourceManager.putMetadata("macro-placeholder-timeout", Long.getLong(MACRO_PLACEHOLDER_TIMEOUT, 5000L).toString());
        return Result.OK;
    }

    private void populateMetadata(@NonNull AbstractPage page, @NonNull Space space) {
        Page p;
        Page parent;
        boolean hasViewPermissions = this.hasPermission(page, Permission.VIEW);
        if (hasViewPermissions) {
            this.webResourceManager.putMetadata("page-title", page.getTitle());
            this.webResourceManager.putMetadata("latest-published-page-title", page.getTitle());
            this.webResourceManager.putMetadata("space-name", space.getName());
        }
        this.webResourceManager.putMetadata("page-id", page.getIdAsString());
        this.webResourceManager.putMetadata("latest-page-id", page.getLatestVersion().getIdAsString());
        this.webResourceManager.putMetadata("content-type", page.getType());
        String parentPageId = "";
        if (page instanceof Page && (parent = (p = (Page)page).getParent()) != null) {
            parentPageId = parent.getIdAsString();
            if (hasViewPermissions) {
                this.webResourceManager.putMetadata("parent-page-title", parent.getTitle());
            }
        }
        this.webResourceManager.putMetadata("parent-page-id", parentPageId);
        this.webResourceManager.putMetadata("space-key", space.getKey());
    }

    private boolean hasPermission(AbstractPage page, Permission permission) {
        return this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), permission, page.getLatestVersion());
    }

    private class RequestHelper {
        private final ParameterSource parameterSource;

        RequestHelper(ParameterSource parameterSource) {
            this.parameterSource = parameterSource;
        }

        @Nullable Space getSpace(@Nullable AbstractPage page) {
            if (page != null) {
                return page.getLatestVersion().getSpace();
            }
            String spaceKey = this.getSpaceKeyFromParameter();
            if (StringUtils.isNotEmpty((CharSequence)spaceKey)) {
                return PageAwareHelper.this.spaceManager.getSpace(spaceKey);
            }
            return null;
        }

        @Nullable AbstractPage getPage() {
            if (this.hasParameter("pageId")) {
                return this.getPageFromIdParameter("pageId");
            }
            if (this.hasParameter("spaceKey") && this.hasParameter("title")) {
                return this.getPageFromKeyAndTitle();
            }
            return null;
        }

        @Nullable AbstractPage getDraft() {
            if (this.hasParameter("draftId")) {
                return this.getPageFromIdParameter("draftId");
            }
            return null;
        }

        private @Nullable AbstractPage getPageFromIdParameter(String idName) {
            try {
                long pageId = Long.parseLong(this.getParameter(idName));
                ContentEntityObject contentEntity = PageAwareHelper.this.contentEntityManager.getById(pageId);
                if (contentEntity instanceof AbstractPage) {
                    return this.getPageVersion((AbstractPage)contentEntity);
                }
                log.debug("received a pageId [{}] which is not of contenttype PAGE ", (Object)pageId);
                return null;
            }
            catch (NumberFormatException e) {
                return null;
            }
        }

        private @Nullable AbstractPage getPageVersion(@Nullable AbstractPage page) {
            if (page == null) {
                return null;
            }
            if (!this.hasParameter("pageVersion")) {
                return page;
            }
            try {
                return (AbstractPage)PageAwareHelper.this.pageManager.getOtherVersion(page, Integer.parseInt(this.getParameter("pageVersion")));
            }
            catch (NumberFormatException e) {
                log.info("Could not parse version as integer: " + this.getParameter("pageVersion"));
                return page;
            }
        }

        private @Nullable AbstractPage getPageFromKeyAndTitle() {
            String spaceKey = this.getSpaceKeyFromParameter();
            String title = this.getParameter("title");
            String postingDay = this.getParameter("postingDay");
            if (!AbstractPage.isValidPageTitle(title)) {
                return null;
            }
            if (StringUtils.isEmpty((CharSequence)postingDay)) {
                Page page = PageAwareHelper.this.pageManager.getPageWithComments(spaceKey, title);
                if (page == null) {
                    return null;
                }
                if (this.hasParameter("pageVersion")) {
                    return this.getPageVersion(page);
                }
                return page;
            }
            Calendar postingDayCalendar = this.getPostingDayCalendar(postingDay);
            if (postingDayCalendar == null) {
                return null;
            }
            return PageAwareHelper.this.pageManager.getBlogPost(spaceKey, title, postingDayCalendar, false);
        }

        String getSpaceKeyFromParameter() {
            return this.getParameter("spaceKey");
        }

        private String getParameter(String name) {
            return this.parameterSource.getParameter(name);
        }

        private boolean hasParameter(String name) {
            return this.parameterSource.hasParameter(name);
        }

        private @Nullable Calendar getPostingDayCalendar(String postingDay) {
            try {
                Date postingDate = new SimpleDateFormat("yyyy/MM/dd").parse(postingDay);
                return BlogPost.toCalendar(postingDate);
            }
            catch (ParseException e) {
                log.info("Invalid posting date supplied: " + postingDay);
                return null;
            }
        }
    }

    public static enum Result {
        PAGE_NOT_FOUND,
        PAGE_NOT_PERMITTED,
        NOT_PERMITTED,
        OK,
        READ_ONLY;

    }

    public static interface ParameterSource {
        public String getParameter(String var1);

        default public boolean hasParameter(String name) {
            return StringUtils.isNotEmpty((CharSequence)this.getParameter(name));
        }
    }
}


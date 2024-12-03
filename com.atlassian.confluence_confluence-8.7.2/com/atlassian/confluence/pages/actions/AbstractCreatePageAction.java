/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.whitelist.OutboundWhitelist
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.user.User
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.struts2.ServletActionContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.service.page.StringParsingContextProvider;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.service.CommandActionHelper;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.DraftsTransitionHelper;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.actions.AbstractCreateAndEditPageAction;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.Spaced;
import com.atlassian.confluence.themes.ThemeManager;
import com.atlassian.confluence.util.HtmlEntityEscapeUtil;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.UrlUtils;
import com.atlassian.confluence.util.breadcrumbs.Breadcrumb;
import com.atlassian.confluence.util.breadcrumbs.BreadcrumbAware;
import com.atlassian.confluence.util.breadcrumbs.BreadcrumbGenerator;
import com.atlassian.confluence.util.breadcrumbs.spaceia.SpaceBreadcrumb;
import com.atlassian.plugins.whitelist.OutboundWhitelist;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.user.User;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractCreatePageAction
extends AbstractCreateAndEditPageAction
implements Spaced,
BreadcrumbAware {
    public static final String INPUT_VARIABLES = "inputvariables";
    private static final Logger log = LoggerFactory.getLogger(AbstractCreatePageAction.class);
    protected Space space;
    private Space newSpace;
    private String spaceKey;
    private long fromPageId;
    private Page fromPage;
    private String titleWritten = "";
    private String queryString;
    protected AttachmentManager attachmentManager;
    protected ThemeManager themeManager;
    protected BreadcrumbGenerator breadcrumbGenerator;
    private OutboundWhitelist outboundWhitelist;
    protected final StringParsingContextProvider contextProvider = new StringParsingContextProvider();
    protected String originalReferrer;

    public void setSpaceKey(String spaceKey) {
        this.spaceKey = spaceKey;
    }

    @Override
    public String getSpaceKey() {
        if (StringUtils.isNotEmpty((CharSequence)this.spaceKey)) {
            return this.spaceKey;
        }
        return super.getSpaceKey();
    }

    @Override
    public Space getSpace() {
        if (this.space == null) {
            this.space = super.getSpace();
        }
        if (this.space == null && StringUtils.isNotEmpty((CharSequence)this.spaceKey)) {
            this.space = this.spaceManager.getSpace(this.spaceKey);
        }
        return this.space;
    }

    public Space getNewSpace() {
        if (this.newSpace == null && StringUtils.isNotEmpty((CharSequence)this.newSpaceKey)) {
            this.newSpace = this.spaceManager.getSpace(this.newSpaceKey);
        }
        return this.newSpace;
    }

    @Override
    public String doDefault() throws Exception {
        this.loadTemplates();
        return super.doDefault();
    }

    protected String beforeAdd() throws Exception {
        this.loadTemplates();
        if (StringUtils.isNotEmpty((CharSequence)this.back)) {
            return "input";
        }
        if (this.templateId != null && !this.getText("no-template").equals(this.templateId)) {
            return INPUT_VARIABLES;
        }
        try {
            if (!this.getCollaborativeEditingHelper().isSharedDraftsFeatureEnabled(this.getSpaceKey())) {
                this.createPage();
            }
        }
        catch (XhtmlException e) {
            this.addActionError("content.xhtml.editor.conversion.failed");
            log.warn("XhtmlException converting editor format to storage format. Turn on debug level logging to see editor format data.", (Throwable)e);
            log.debug("The editor data that could not be converted\n: {}", (Object)this.wysiwygContent);
            return "error";
        }
        return "success";
    }

    public String doAdd() throws Exception {
        String result = this.beforeAdd();
        if (!StringUtils.equals((CharSequence)"success", (CharSequence)result)) {
            return result;
        }
        this.getCommandActionHelper();
        this.initialiseProvider(this.getPage());
        this.contextProvider.setContext(this.getQueryString());
        this.contextProvider.getContext().put("labelsString", (Serializable)((Object)this.getLabelsString()));
        this.populateContextProvider();
        ContentEntityObject draft = this.getDraftAsCEO();
        if (draft != null) {
            draft.setTitle(this.getTitle());
            try {
                draft.setBodyAsString(this.getStorageFormat());
            }
            catch (XhtmlException e) {
                throw new Error(e);
            }
            if (!DraftsTransitionHelper.isLegacyDraft(draft)) {
                this.assignSpace(draft, this.getNewSpace());
                this.assignParentPage(draft, this.getParentPage());
                draft.setSynchronyRevision(this.getSyncRev());
            }
        }
        result = this.getCommandActionHelper().execute(this);
        this.setPage(this.getCreatedAbstractPage());
        if (!StringUtils.equals((CharSequence)"success", (CharSequence)result)) {
            return result;
        }
        return this.afterAdd();
    }

    protected void populateContextProvider() {
    }

    protected void assignSpace(ContentEntityObject draft, Space space) {
    }

    protected void assignParentPage(ContentEntityObject page, Page parentPage) {
    }

    protected abstract AbstractPage getCreatedAbstractPage();

    protected abstract CommandActionHelper getCommandActionHelper();

    protected abstract void initialiseProvider(AbstractPage var1);

    protected String afterAdd() {
        if (StringUtils.isNotEmpty((CharSequence)this.getPosition())) {
            this.getMovePageCommand().execute();
        }
        if (this.getDraftAsCEO() != null) {
            try {
                this.heartbeatManager.stopActivity(this.getDraftAsCEO().getId() + this.getContentType(), this.getAuthenticatedUser());
            }
            catch (Exception e) {
                log.error("Error stopping heartbeat activity", (Throwable)e);
            }
        }
        this.bean.put("redirectUrl", this.getPage().getUrlPath());
        return "success";
    }

    protected void transferDraftAttachments() {
        ContentEntityObject draft = this.getDraftAsCEO();
        if (draft != null) {
            for (Attachment attachment : this.attachmentManager.getLatestVersionsOfAttachmentsWithAnyStatus(draft)) {
                this.attachmentManager.moveAttachment(attachment, attachment.getFileName(), this.getPage());
            }
        }
    }

    public void createPage() throws XhtmlException, IOException {
        AbstractPage page = this.getPageToCreate();
        page.setTitle(this.getTitle());
        page.setBodyAsString(this.getStorageFormat());
        Space sp = this.getNewSpace();
        if (sp == null) {
            sp = this.spaceManager.getSpace(this.getSpaceKey());
        }
        page.setSpace(sp);
        this.setPage(page);
    }

    protected abstract AbstractPage getPageToCreate();

    @Override
    public boolean isPageRequired() {
        return false;
    }

    public void setFromPageId(long fromPageId) {
        this.fromPageId = fromPageId;
    }

    @Override
    public long getFromPageId() {
        return this.fromPageId;
    }

    public Page getFromPage() {
        if (this.fromPage == null) {
            if (this.getSpace() == null || !this.getSpace().getKey().equalsIgnoreCase(this.getSpaceKey())) {
                return null;
            }
            if (this.fromPageId == 0L) {
                Page homePage = this.getSpace().getHomePage();
                if (homePage == null) {
                    return null;
                }
                this.fromPageId = homePage.getId();
            }
            this.fromPage = this.pageManager.getPage(this.fromPageId);
        }
        return this.fromPage;
    }

    public void setFromPage(Page fromPage) {
        this.fromPage = fromPage;
    }

    @Override
    public ContentEntityObject getAttachmentSourceContent() {
        return this.getDraftAsCEO();
    }

    public String getCancelRedirectUrl() {
        AbstractPage page;
        if (this.getPage() == null) {
            return "/dashboard.action#recently-worked";
        }
        if (StringUtils.isNotBlank((CharSequence)this.originalReferrer) && !UrlUtils.isEditingUrl(this.originalReferrer)) {
            return this.originalReferrer;
        }
        if (this.fromPageId != 0L && (page = this.pageManager.getAbstractPage(this.fromPageId)) != null && page.isCurrent()) {
            return page.getUrlPath();
        }
        if (this.themeManager != null && this.themeManager.getSpaceTheme(this.space.getKey()).hasSpaceSideBar()) {
            return "/collector/pages.action?key=" + HtmlUtil.urlEncode(this.space.getKey());
        }
        return "listpages.action?key=" + HtmlUtil.urlEncode(this.space.getKey());
    }

    @Override
    public String getContentType() {
        return this.getPageToCreate().getType();
    }

    @Override
    public boolean hasSetPagePermissionsPermission() {
        return this.isSpaceAdmin() || this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.SET_PERMISSIONS, this.getDraftAsCEO());
    }

    public void setTitleWritten(String titleWritten) {
        this.titleWritten = titleWritten;
    }

    public void setAttachmentManager(AttachmentManager attachmentManager) {
        this.attachmentManager = attachmentManager;
    }

    public void setThemeManager(ThemeManager themeManager) {
        this.themeManager = themeManager;
    }

    @Override
    public String getCancelResult() {
        if (this.getDraftAsCEO() != null) {
            try {
                this.heartbeatManager.stopActivity(this.getDraftAsCEO().getId() + this.getContentType(), this.getAuthenticatedUser());
            }
            catch (Exception e) {
                log.error("Error stopping heartbeat activity", (Throwable)e);
            }
        }
        String result = super.getCancelResult();
        this.bean.put("redirectUrl", this.getCancelRedirectUrl());
        return result;
    }

    public void setBreadcrumbGenerator(BreadcrumbGenerator breadcrumbGenerator) {
        this.breadcrumbGenerator = breadcrumbGenerator;
    }

    @Override
    public Breadcrumb getBreadcrumb() {
        Breadcrumb breadcrumb = new SpaceBreadcrumb(this.getSpace()).concatWith(this.getContentBreadcrumb());
        breadcrumb.setFilterTrailingBreadcrumb(false);
        return breadcrumb;
    }

    protected Breadcrumb getContentBreadcrumb() {
        Page fromPage = this.getFromPage();
        if (fromPage == null) {
            return this.breadcrumbGenerator.getContentCollectorBreadcrumb(this.getSpace(), Page.class);
        }
        return this.breadcrumbGenerator.getContentBreadcrumb(this.getSpace(), fromPage);
    }

    public void setQueryString(String queryString) {
        if (queryString != null) {
            StringBuffer buffer = new StringBuffer(queryString);
            HtmlEntityEscapeUtil.unEscapeHtmlEntities(buffer);
            this.queryString = buffer.toString();
        }
    }

    public String getQueryString() {
        if (StringUtils.isNotBlank((CharSequence)this.queryString)) {
            return this.queryString;
        }
        HttpServletRequest request = ServletActionContext.getRequest();
        return request != null ? request.getQueryString() : "";
    }

    public String getOriginalReferrer() {
        return this.originalReferrer;
    }

    public void setOriginalReferrer(String originalReferrer) {
        this.originalReferrer = originalReferrer;
        if (StringUtils.isEmpty((CharSequence)originalReferrer)) {
            return;
        }
        try {
            URI uri = URI.create(originalReferrer);
            this.outboundWhitelist = (OutboundWhitelist)ContainerManager.getComponent((String)"outboundWhitelist");
            if (this.outboundWhitelist == null || !this.outboundWhitelist.isAllowed(uri)) {
                this.originalReferrer = "";
            }
        }
        catch (IllegalArgumentException e) {
            this.originalReferrer = "";
        }
    }
}


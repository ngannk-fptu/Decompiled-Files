/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  bucket.core.actions.PaginationSupport
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.pages.actions;

import bucket.core.actions.PaginationSupport;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.TinyUrl;
import com.atlassian.confluence.pages.actions.AbstractViewAttachmentsAction;
import com.atlassian.confluence.pages.actions.PageAware;
import com.atlassian.confluence.pages.actions.TinyUrlAware;
import com.atlassian.confluence.plugin.descriptor.web.DefaultWebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.security.CaptchaAware;
import com.atlassian.confluence.security.CaptchaManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.util.breadcrumbs.spaceia.ContentDetailAction;
import com.atlassian.user.User;
import java.util.List;

public class ViewPageAttachmentsAction
extends AbstractViewAttachmentsAction
implements PageAware,
CaptchaAware,
TinyUrlAware,
ContentDetailAction {
    private AbstractPage page;
    private String pageId;
    private PaginationSupport paginationSupport;
    private int startIndex;
    private CaptchaManager captchaManager;
    private static final int DEFAULT_ATTACHMENT_PAGE_SIZE = 20;

    @Override
    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.VIEW, this.page);
    }

    @Override
    public List<Attachment> getLatestVersionsOfAttachments() {
        List<Attachment> attachments = super.getLatestVersionsOfAttachments();
        this.getPaginationSupport().setStartIndex(this.startIndex);
        this.getPaginationSupport().setItems(attachments);
        if (this.highlight != null && !this.highlight.isEmpty()) {
            String firstFilename = (String)this.highlight.get(0);
            int i = 0;
            for (Attachment attachment : attachments) {
                if (firstFilename.equals(attachment.getFileName())) {
                    int pageIndex = i / 20;
                    this.getPaginationSupport().setStartIndex(pageIndex * 20);
                    break;
                }
                ++i;
            }
        }
        return this.getPaginationSupport().getPage();
    }

    @Override
    public ContentEntityObject getContentEntityObject() {
        return this.getPage();
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
        return true;
    }

    @Override
    public boolean isLatestVersionRequired() {
        return true;
    }

    @Override
    public boolean isViewPermissionRequired() {
        return true;
    }

    public String getPageId() {
        return this.pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public int getStartIndex() {
        return this.startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public PaginationSupport getPaginationSupport() {
        if (this.paginationSupport == null) {
            this.paginationSupport = new PaginationSupport(20);
        }
        return this.paginationSupport;
    }

    public WebInterfaceContext getWebInterfaceContext(WebInterfaceContext context, Attachment attachment) {
        DefaultWebInterfaceContext defaultContext = DefaultWebInterfaceContext.copyOf(context);
        defaultContext.setAttachment(attachment);
        return defaultContext;
    }

    public CaptchaManager getCaptchaManager() {
        return this.captchaManager;
    }

    public void setCaptchaManager(CaptchaManager captchaManager) {
        this.captchaManager = captchaManager;
    }

    public Space getSpace() {
        return this.getPage() == null ? null : this.getPage().getSpace();
    }

    @Override
    public String getTinyUrl() {
        if (this.getPage() == null) {
            return null;
        }
        return new TinyUrl(this.getPage()).getIdentifier();
    }

    public String getSpaceKey() {
        if (this.getPage() == null) {
            return null;
        }
        return this.getPage().getSpaceKey();
    }
}


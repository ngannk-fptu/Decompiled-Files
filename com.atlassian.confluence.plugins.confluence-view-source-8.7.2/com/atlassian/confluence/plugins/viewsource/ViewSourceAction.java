/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.Renderer
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.access.annotations.RequiresAnyConfluenceAccess
 *  com.atlassian.confluence.velocity.htmlsafe.HtmlSafe
 *  com.atlassian.confluence.xhtml.api.XhtmlContent
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.plugins.viewsource;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.Renderer;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.access.annotations.RequiresAnyConfluenceAccess;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.renderer.RenderContext;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;

@RequiresAnyConfluenceAccess
public class ViewSourceAction
extends ConfluenceActionSupport {
    protected Renderer editRenderer;
    private String html;
    private XhtmlContent xhtmlContent;
    private ContentEntityManager contentEntityManager;
    private long pageId;

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        ContentEntityObject content = this.xhtmlContent.convertWikiBodyToStorage(this.getContent());
        String storageFormat = content.getBodyAsString();
        DefaultConversionContext context = new DefaultConversionContext((RenderContext)new PageContext(content));
        this.html = this.editRenderer.render(storageFormat, (ConversionContext)context);
        return this.getContent().getType();
    }

    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.VIEW, (Object)this.getContent());
    }

    public boolean isLatestVersionRequired() {
        return false;
    }

    @HtmlSafe
    public String getSourceHtml() {
        return this.html;
    }

    public ContentEntityObject getContent() {
        return this.contentEntityManager.getById(this.getPageId());
    }

    public void setEditRenderer(Renderer editRenderer) {
        this.editRenderer = editRenderer;
    }

    public void setXhtmlContent(XhtmlContent xhtmlContent) {
        this.xhtmlContent = xhtmlContent;
    }

    public void setContentEntityManager(ContentEntityManager contentEntityManager) {
        this.contentEntityManager = contentEntityManager;
    }

    public long getPageId() {
        return this.pageId;
    }

    public void setPageId(long pageId) {
        this.pageId = pageId;
    }
}


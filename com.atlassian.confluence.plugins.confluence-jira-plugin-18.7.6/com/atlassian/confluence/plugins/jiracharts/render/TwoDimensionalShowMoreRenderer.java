/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.Renderer
 *  com.atlassian.confluence.content.render.xhtml.StorageFormatCleaner
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.user.User
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.confluence.plugins.jiracharts.render;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.Renderer;
import com.atlassian.confluence.content.render.xhtml.StorageFormatCleaner;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.renderer.RenderContext;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.user.User;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class TwoDimensionalShowMoreRenderer
extends HttpServlet {
    private final ContentEntityManager contentEntityManager;
    private final PermissionManager permissionManager;
    private final Renderer viewRenderer;
    private final I18nResolver i18nResolver;
    private final StorageFormatCleaner storageFormatCleaner;

    public TwoDimensionalShowMoreRenderer(ContentEntityManager contentEntityManager, PermissionManager permissionManager, Renderer viewRenderer, I18nResolver i18nResolver, StorageFormatCleaner storageFormatCleaner) {
        this.contentEntityManager = contentEntityManager;
        this.permissionManager = permissionManager;
        this.viewRenderer = viewRenderer;
        this.i18nResolver = i18nResolver;
        this.storageFormatCleaner = storageFormatCleaner;
    }

    private String convertPageWikiToHtml(long id, String wiki, boolean isShowMore) throws ServletException {
        DefaultConversionContext conversionContext;
        if (id == -1L) {
            conversionContext = new DefaultConversionContext((RenderContext)new PageContext());
        } else {
            ContentEntityObject ceo = this.contentEntityManager.getById(id);
            if (ceo != null) {
                this.assertCanView(ceo);
                conversionContext = new DefaultConversionContext((RenderContext)ceo.toPageContext());
            } else {
                conversionContext = new DefaultConversionContext((RenderContext)new PageContext());
            }
        }
        conversionContext.setProperty("isShowMore", (Object)isShowMore);
        return this.viewRenderer.render(wiki, (ConversionContext)conversionContext);
    }

    private void assertCanView(ContentEntityObject ceo) throws ServletException {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (!this.permissionManager.hasPermission((User)user, Permission.VIEW, (Object)ceo)) {
            throw new ServletException(this.i18nResolver.getText("jiraissues.error.notpermitted"));
        }
    }

    protected void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, PermissionManager.TARGET_APPLICATION)) {
            throw new ServletException(this.i18nResolver.getText("jiraissues.error.notpermitted"));
        }
        String pageIdString = httpServletRequest.getParameter("pageId");
        String wikiMarkup = this.storageFormatCleaner.cleanQuietly(httpServletRequest.getParameter("wikiMarkup"));
        boolean isShowMore = Boolean.parseBoolean(httpServletRequest.getParameter("isShowMore"));
        long pageId = Long.parseLong(pageIdString);
        String result = this.convertPageWikiToHtml(pageId, wikiMarkup, isShowMore);
        httpServletResponse.setContentType("text/html");
        PrintWriter printWriter = httpServletResponse.getWriter();
        printWriter.print(result);
        printWriter.flush();
    }
}


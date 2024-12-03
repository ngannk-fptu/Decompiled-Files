/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.persistence.ContentEntityObjectDao
 *  com.atlassian.confluence.core.service.NotAuthorizedException
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.confluence.util.RequestCacheThreadLocal
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.renderer.RenderContext
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.macros.advanced;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.persistence.ContentEntityObjectDao;
import com.atlassian.confluence.core.service.NotAuthorizedException;
import com.atlassian.confluence.plugins.macros.advanced.PageProvider;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.util.RequestCacheThreadLocal;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.renderer.RenderContext;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;

public class PageIncludeServlet
extends HttpServlet {
    private PageProvider pageProvider;
    private ContentEntityObjectDao contentEntityObjectDao;
    private I18NBeanFactory i18NBeanFactory;

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long contentId;
        I18NBean i18NBean = this.i18NBeanFactory.getI18NBean();
        String location = req.getParameter("location");
        if (StringUtils.isBlank((CharSequence)location)) {
            resp.sendError(400, i18NBean.getText("confluence.macros.advanced.include.error.no.location"));
            return;
        }
        try {
            contentId = Long.parseLong(req.getParameter("contentId"));
        }
        catch (IllegalArgumentException e) {
            resp.sendError(400, i18NBean.getText("confluence.macros.advanced.include.error.invalid.content-id"));
            return;
        }
        ContentEntityObject ceo = this.contentEntityObjectDao.getById(contentId);
        if (ceo == null) {
            resp.sendError(400, i18NBean.getText("confluence.macros.advanced.include.error.no.content-entity"));
            return;
        }
        try {
            PageContext context = new PageContext(ceo);
            DefaultConversionContext conversionContext = new DefaultConversionContext((RenderContext)context);
            ContentEntityObject page = this.pageProvider.resolve(location, (ConversionContext)conversionContext);
            if (page == null) {
                resp.sendError(404);
                return;
            }
            String baseUrl = RequestCacheThreadLocal.getContextPath();
            resp.sendRedirect(baseUrl + page.getUrlPath());
        }
        catch (IllegalArgumentException e) {
            resp.sendError(404, e.getMessage());
        }
        catch (NotAuthorizedException e) {
            resp.sendError(404, i18NBean.getText("confluence.macros.advanced.include.error.content.not.found"));
        }
    }

    public void setPageProvider(PageProvider pageProvider) {
        this.pageProvider = pageProvider;
    }

    public void setContentEntityObjectDao(ContentEntityObjectDao contentEntityObjectDao) {
        this.contentEntityObjectDao = contentEntityObjectDao;
    }

    public void setUserI18NBeanFactory(I18NBeanFactory i18NBeanFactory) {
        this.i18NBeanFactory = i18NBeanFactory;
    }
}


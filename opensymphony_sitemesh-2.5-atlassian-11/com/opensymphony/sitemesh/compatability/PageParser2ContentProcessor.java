/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.opensymphony.sitemesh.compatability;

import com.opensymphony.module.sitemesh.Factory;
import com.opensymphony.module.sitemesh.HTMLPage;
import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.PageParser;
import com.opensymphony.module.sitemesh.SitemeshBuffer;
import com.opensymphony.module.sitemesh.filter.HttpContentType;
import com.opensymphony.sitemesh.Content;
import com.opensymphony.sitemesh.ContentProcessor;
import com.opensymphony.sitemesh.SiteMeshContext;
import com.opensymphony.sitemesh.compatability.HTMLPage2Content;
import com.opensymphony.sitemesh.webapp.SiteMeshWebAppContext;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;

public class PageParser2ContentProcessor
implements ContentProcessor {
    private final Factory factory;

    public PageParser2ContentProcessor(Factory factory) {
        this.factory = factory;
    }

    public boolean handles(SiteMeshContext context) {
        SiteMeshWebAppContext webAppContext = (SiteMeshWebAppContext)context;
        return !this.factory.isPathExcluded(this.extractRequestPath(webAppContext.getRequest()));
    }

    private String extractRequestPath(HttpServletRequest request) {
        String servletPath = request.getServletPath();
        String pathInfo = request.getPathInfo();
        String query = request.getQueryString();
        return (servletPath == null ? "" : servletPath) + (pathInfo == null ? "" : pathInfo) + (query == null ? "" : "?" + query);
    }

    public boolean handles(String contentType) {
        return this.factory.shouldParsePage(contentType);
    }

    public Content build(SitemeshBuffer buffer, SiteMeshContext context) throws IOException {
        HttpContentType httpContentType = new HttpContentType(context.getContentType());
        PageParser pageParser = this.factory.getPageParser(httpContentType.getType());
        Page page = pageParser.parse(buffer);
        return new HTMLPage2Content((HTMLPage)page);
    }
}


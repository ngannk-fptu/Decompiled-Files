/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpServletResponseWrapper
 */
package com.opensymphony.sitemesh.webapp;

import com.opensymphony.module.sitemesh.PageParser;
import com.opensymphony.module.sitemesh.PageParserSelector;
import com.opensymphony.module.sitemesh.SitemeshBuffer;
import com.opensymphony.module.sitemesh.filter.PageResponseWrapper;
import com.opensymphony.module.sitemesh.scalability.ScalabilitySupport;
import com.opensymphony.sitemesh.Content;
import com.opensymphony.sitemesh.ContentProcessor;
import com.opensymphony.sitemesh.webapp.SiteMeshWebAppContext;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class ContentBufferingResponse
extends HttpServletResponseWrapper {
    private final PageResponseWrapper pageResponseWrapper;
    private final ContentProcessor contentProcessor;
    private final SiteMeshWebAppContext webAppContext;

    public ContentBufferingResponse(HttpServletResponse response, ContentProcessor contentProcessor, SiteMeshWebAppContext webAppContext, ScalabilitySupport scalabilitySupport) {
        this(response, null, contentProcessor, webAppContext, scalabilitySupport);
    }

    public ContentBufferingResponse(HttpServletResponse response, HttpServletRequest request, final ContentProcessor contentProcessor, final SiteMeshWebAppContext webAppContext, ScalabilitySupport scalabilitySupport) {
        super((HttpServletResponse)new PageResponseWrapper(response, request, scalabilitySupport, new PageParserSelector(){

            public boolean shouldParsePage(String contentType) {
                return contentProcessor.handles(contentType);
            }

            public PageParser getPageParser(String contentType) {
                return null;
            }
        }){

            public void setContentType(String contentType) {
                webAppContext.setContentType(contentType);
                super.setContentType(contentType);
            }
        });
        this.contentProcessor = contentProcessor;
        this.webAppContext = webAppContext;
        this.pageResponseWrapper = (PageResponseWrapper)this.getResponse();
        String existingContentType = response.getContentType();
        if (existingContentType != null) {
            this.pageResponseWrapper.setContentType(existingContentType);
        }
    }

    public boolean isUsingStream() {
        return this.pageResponseWrapper.isUsingStream();
    }

    public Content getContent() throws IOException {
        SitemeshBuffer content = this.pageResponseWrapper.getContents();
        if (content != null) {
            return this.contentProcessor.build(content, this.webAppContext);
        }
        return null;
    }
}


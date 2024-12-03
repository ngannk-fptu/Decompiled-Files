/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.opensymphony.sitemesh.webapp;

import com.opensymphony.sitemesh.SiteMeshContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SiteMeshWebAppContext
implements SiteMeshContext {
    private static final String IS_USING_STRING_KEY = "com.opensymphony.sitemesh.USINGSTREAM";
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final ServletContext servletContext;
    private String contentType;

    public SiteMeshWebAppContext(HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) {
        this.request = request;
        this.response = response;
        this.servletContext = servletContext;
    }

    public HttpServletRequest getRequest() {
        return this.request;
    }

    public HttpServletResponse getResponse() {
        return this.response;
    }

    public ServletContext getServletContext() {
        return this.servletContext;
    }

    public boolean isUsingStream() {
        return this.request.getAttribute(IS_USING_STRING_KEY) == Boolean.TRUE;
    }

    public void setUsingStream(boolean isUsingStream) {
        this.request.setAttribute(IS_USING_STRING_KEY, (Object)(isUsingStream ? Boolean.TRUE : Boolean.FALSE));
    }

    public String getContentType() {
        return this.contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}


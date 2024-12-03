/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletRequestWrapper
 */
package com.opensymphony.module.sitemesh.parser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

class PageRequest
extends HttpServletRequestWrapper {
    private String requestURI;
    private String method;
    private String pathInfo;
    private String pathTranslated;
    private String queryString;
    private String servletPath;

    public PageRequest(HttpServletRequest request) {
        super(request);
        this.requestURI = request.getRequestURI();
        this.method = request.getMethod();
        this.pathInfo = request.getPathInfo();
        this.pathTranslated = request.getPathTranslated();
        this.queryString = request.getQueryString();
        this.servletPath = request.getServletPath();
    }

    public String getRequestURI() {
        return this.requestURI;
    }

    public String getMethod() {
        return this.method;
    }

    public String getPathInfo() {
        return this.pathInfo;
    }

    public String getPathTranslated() {
        return this.pathTranslated;
    }

    public String getQueryString() {
        return this.queryString;
    }

    public String getServletPath() {
        return this.servletPath;
    }
}


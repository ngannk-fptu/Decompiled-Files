/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.RequestDispatcher
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletRequestWrapper
 */
package com.opensymphony.module.sitemesh.filter;

import com.opensymphony.module.sitemesh.filter.RequestDispatcherWrapper;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class PageRequestWrapper
extends HttpServletRequestWrapper {
    private static final boolean SUPPRESS_IF_MODIFIED_HEADER = true;

    public PageRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    public RequestDispatcher getRequestDispatcher(String s) {
        return new RequestDispatcherWrapper(super.getRequestDispatcher(s));
    }

    public String getHeader(String string) {
        if ("IF-MODIFIED-SINCE".equalsIgnoreCase(string)) {
            return "";
        }
        return super.getHeader(string);
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletContext
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.confluence.compat.struts2.servletactioncontext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

interface ServletActionContextCompat {
    public void setRequest(HttpServletRequest var1);

    public HttpServletRequest getRequest();

    public void setResponse(HttpServletResponse var1);

    public HttpServletResponse getResponse();

    public void setServletConfig(ServletConfig var1);

    public ServletContext getServletContext();

    public ServletConfig getServletConfig();
}


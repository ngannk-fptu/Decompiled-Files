/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.filters.ServletContextThreadLocal
 *  com.opensymphony.xwork2.ActionContext
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpSession
 *  org.apache.struts2.ServletActionContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.web.context;

import com.atlassian.confluence.web.context.HttpContext;
import com.atlassian.core.filters.ServletContextThreadLocal;
import com.opensymphony.xwork2.ActionContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticHttpContext
implements HttpContext {
    private static Logger log = LoggerFactory.getLogger(StaticHttpContext.class);

    @Override
    public HttpServletRequest getRequest() {
        if (ActionContext.getContext() != null && ServletActionContext.getRequest() != null) {
            log.debug("Using ServletActionContext request");
            return ServletActionContext.getRequest();
        }
        log.debug("Using ServletContextThreadLocal request");
        return ServletContextThreadLocal.getRequest();
    }

    @Override
    public HttpServletResponse getResponse() {
        if (ActionContext.getContext() != null && ServletActionContext.getResponse() != null) {
            log.debug("Using ServletActionContext response");
            return ServletActionContext.getResponse();
        }
        log.debug("Using ServletContextThreadLocal response");
        return ServletContextThreadLocal.getResponse();
    }

    @Override
    public HttpSession getSession(boolean create) {
        HttpServletRequest request = this.getRequest();
        if (request == null) {
            return null;
        }
        return request.getSession(create);
    }
}


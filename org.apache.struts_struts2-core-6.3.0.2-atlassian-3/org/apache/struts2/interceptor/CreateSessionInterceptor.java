/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpSession
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.dispatcher.SessionMap;

public class CreateSessionInterceptor
extends AbstractInterceptor {
    private static final long serialVersionUID = -4590322556118858869L;
    private static final Logger LOG = LogManager.getLogger(CreateSessionInterceptor.class);

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        HttpServletRequest servletRequest = invocation.getInvocationContext().getServletRequest();
        HttpSession httpSession = servletRequest.getSession(false);
        if (httpSession == null) {
            LOG.debug("Creating new HttpSession and new SessionMap in ServletActionContext");
            servletRequest.getSession(true);
            invocation.getInvocationContext().withSession(new SessionMap(servletRequest));
        }
        return invocation.invoke();
    }
}


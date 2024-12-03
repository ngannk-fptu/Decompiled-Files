/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.apache.struts2.interceptor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.action.ApplicationAware;
import org.apache.struts2.action.ParametersAware;
import org.apache.struts2.action.PrincipalAware;
import org.apache.struts2.action.ServletContextAware;
import org.apache.struts2.action.ServletRequestAware;
import org.apache.struts2.action.ServletResponseAware;
import org.apache.struts2.action.SessionAware;
import org.apache.struts2.interceptor.servlet.ServletPrincipalProxy;

public class ServletConfigInterceptor
extends AbstractInterceptor
implements StrutsStatics {
    private static final long serialVersionUID = 605261777858676638L;

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        HttpServletRequest request;
        Object action = invocation.getAction();
        ActionContext context = invocation.getInvocationContext();
        if (action instanceof ServletRequestAware) {
            request = context.getServletRequest();
            ((ServletRequestAware)action).withServletRequest(request);
        }
        if (action instanceof ServletResponseAware) {
            HttpServletResponse response = context.getServletResponse();
            ((ServletResponseAware)action).withServletResponse(response);
        }
        if (action instanceof ParametersAware) {
            ((ParametersAware)action).withParameters(context.getParameters());
        }
        if (action instanceof ApplicationAware) {
            ((ApplicationAware)action).withApplication(context.getApplication());
        }
        if (action instanceof SessionAware) {
            ((SessionAware)action).withSession(context.getSession());
        }
        if (action instanceof PrincipalAware && (request = context.getServletRequest()) != null) {
            ((PrincipalAware)action).withPrincipalProxy(new ServletPrincipalProxy(request));
        }
        if (action instanceof ServletContextAware) {
            ServletContext servletContext = context.getServletContext();
            ((ServletContextAware)action).withServletContext(servletContext);
        }
        return invocation.invoke();
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.xwork2.ActionInvocation
 *  com.opensymphony.xwork2.interceptor.Interceptor
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.struts2.ServletActionContext
 *  org.apache.struts2.dispatcher.Parameter
 *  org.apache.struts2.dispatcher.Parameter$Empty
 *  org.apache.struts2.dispatcher.Parameter$Request
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.setup.struts;

import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.HtmlUtil;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractAwareInterceptor
implements Interceptor {
    private static final Logger log = LoggerFactory.getLogger(AbstractAwareInterceptor.class);

    public void destroy() {
        log.debug("Destroying XWork interceptor: " + this.getClass().getName());
    }

    public void init() {
        log.debug("Initialising XWork interceptor: " + this.getClass().getName());
    }

    public abstract String intercept(ActionInvocation var1) throws Exception;

    protected String getParameter(String parameterName) {
        Parameter o = ServletActionContext.getContext().getParameters().get((Object)parameterName);
        if (o == null && ServletActionContext.getRequest() != null) {
            return ServletActionContext.getRequest().getParameter(parameterName);
        }
        String result = null;
        if (o instanceof Parameter.Empty) {
            result = HtmlUtil.urlDecode(((Parameter.Empty)o).getValue());
        } else if (o instanceof Parameter.Request) {
            result = HtmlUtil.urlDecode(((Parameter.Request)o).getValue());
        }
        return result;
    }

    protected boolean hasParameter(String parameterName) {
        return StringUtils.isNotEmpty((CharSequence)this.getParameter(parameterName));
    }

    protected ConfluenceUser getUser() {
        return AuthenticatedUserThreadLocal.get();
    }
}


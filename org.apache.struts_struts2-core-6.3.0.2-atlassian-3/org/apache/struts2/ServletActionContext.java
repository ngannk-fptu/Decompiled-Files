/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.jsp.PageContext
 */
package org.apache.struts2;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.dispatcher.mapper.ActionMapping;

public class ServletActionContext
implements StrutsStatics {
    public static final String STRUTS_VALUESTACK_KEY = "struts.valueStack";

    private ServletActionContext() {
    }

    public static ActionContext getActionContext(HttpServletRequest req) {
        ValueStack vs = ServletActionContext.getValueStack(req);
        if (vs != null) {
            return ActionContext.of(vs.getContext()).bind();
        }
        return null;
    }

    @Deprecated
    public static ActionContext getContext() {
        return ActionContext.getContext();
    }

    public static ActionContext getActionContext() {
        return ActionContext.getContext();
    }

    public static ValueStack getValueStack(HttpServletRequest req) {
        return (ValueStack)req.getAttribute(STRUTS_VALUESTACK_KEY);
    }

    public static ActionMapping getActionMapping() {
        return ActionContext.getContext().getActionMapping();
    }

    public static PageContext getPageContext() {
        return ActionContext.getContext().getPageContext();
    }

    public static void setRequest(HttpServletRequest request) {
        ActionContext.getContext().withServletRequest(request);
    }

    public static HttpServletRequest getRequest() {
        return ActionContext.getContext().getServletRequest();
    }

    public static void setResponse(HttpServletResponse response) {
        ActionContext.getContext().withServletResponse(response);
    }

    public static HttpServletResponse getResponse() {
        return ActionContext.getContext().getServletResponse();
    }

    public static ServletContext getServletContext() {
        return ActionContext.getContext().getServletContext();
    }

    public static void setServletContext(ServletContext servletContext) {
        ActionContext.getContext().withServletContext(servletContext);
    }
}


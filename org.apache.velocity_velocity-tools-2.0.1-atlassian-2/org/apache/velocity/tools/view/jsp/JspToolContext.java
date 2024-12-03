/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.jsp.PageContext
 *  org.apache.velocity.app.VelocityEngine
 */
package org.apache.velocity.tools.view.jsp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.view.ViewToolContext;

public class JspToolContext
extends ViewToolContext {
    public static final String PAGE_CONTEXT_KEY = "pageContext";
    private final PageContext pageContext;

    public JspToolContext(VelocityEngine velocity, PageContext pageContext) {
        super(velocity, (HttpServletRequest)pageContext.getRequest(), (HttpServletResponse)pageContext.getResponse(), pageContext.getServletContext());
        this.pageContext = pageContext;
    }

    @Override
    protected void putToolProperties() {
        this.putToolProperty(PAGE_CONTEXT_KEY, this.getPageContext());
        super.putToolProperties();
    }

    public PageContext getPageContext() {
        return this.pageContext;
    }

    @Override
    protected Object getServletApi(String key) {
        if (key.equals(PAGE_CONTEXT_KEY)) {
            return this.getPageContext();
        }
        return super.getServletApi(key);
    }

    @Override
    public Object getAttribute(String key) {
        Object o = this.getPageContext().getAttribute(key);
        if (o == null) {
            o = super.getAttribute(key);
        }
        return o;
    }
}


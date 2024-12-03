/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.RequestDispatcher
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.jsp.JspException
 *  javax.servlet.jsp.tagext.SimpleTagSupport
 */
package com.sun.jersey.server.impl.container.servlet;

import com.sun.jersey.server.impl.container.servlet.Wrapper;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class Include
extends SimpleTagSupport {
    private Class<?> resolvingClass;
    private String page;

    public void setPage(String page) {
        this.page = page;
    }

    public void setResolvingClass(Class<?> resolvingClass) {
        this.resolvingClass = resolvingClass;
    }

    private Object getPageObject(String name) {
        return this.getJspContext().getAttribute(name, 1);
    }

    public void doTag() throws JspException, IOException {
        Class<?> resolvingClass;
        Class<?> oldResolvingClass = resolvingClass = (Class<?>)this.getJspContext().getAttribute("resolvingClass", 2);
        if (this.resolvingClass != null) {
            resolvingClass = this.resolvingClass;
        }
        ServletConfig cfg = (ServletConfig)this.getPageObject("javax.servlet.jsp.jspConfig");
        ServletContext sc = cfg.getServletContext();
        String basePath = (String)this.getJspContext().getAttribute("_basePath", 2);
        for (Class<?> c = resolvingClass; c != Object.class; c = c.getSuperclass()) {
            RequestDispatcher disp;
            String name = basePath + "/" + c.getName().replace('.', '/') + '/' + this.page;
            if (sc.getResource(name) == null || (disp = sc.getRequestDispatcher(name)) == null) continue;
            this.getJspContext().setAttribute("resolvingClass", resolvingClass, 2);
            try {
                HttpServletRequest request = (HttpServletRequest)this.getPageObject("javax.servlet.jsp.jspRequest");
                disp.include((ServletRequest)request, (ServletResponse)new Wrapper((HttpServletResponse)this.getPageObject("javax.servlet.jsp.jspResponse"), new PrintWriter((Writer)this.getJspContext().getOut())));
            }
            catch (ServletException e) {
                throw new JspException((Throwable)e);
            }
            finally {
                this.getJspContext().setAttribute("resolvingClass", oldResolvingClass, 2);
            }
            return;
        }
        throw new JspException("Unable to find '" + this.page + "' for " + resolvingClass);
    }
}


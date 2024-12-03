/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.ELContext
 *  javax.servlet.Servlet
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpSession
 *  javax.servlet.jsp.ErrorData
 *  javax.servlet.jsp.JspContext
 *  javax.servlet.jsp.JspWriter
 *  javax.servlet.jsp.PageContext
 *  javax.servlet.jsp.el.ExpressionEvaluator
 *  javax.servlet.jsp.el.VariableResolver
 *  javax.servlet.jsp.tagext.BodyContent
 *  org.apache.sling.api.scripting.SlingBindings
 */
package org.apache.sling.scripting.jsp;

import java.io.IOException;
import java.io.Writer;
import java.util.Enumeration;
import javax.el.ELContext;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.ErrorData;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.el.ExpressionEvaluator;
import javax.servlet.jsp.el.VariableResolver;
import javax.servlet.jsp.tagext.BodyContent;
import org.apache.sling.api.scripting.SlingBindings;

public class SlingJspPageContext
extends PageContext {
    private final PageContext wrapped;
    private final SlingBindings slingBindings;
    private final ELContext elContext;

    public SlingJspPageContext(PageContext wrapped, SlingBindings slingBindings) {
        this.wrapped = wrapped;
        this.slingBindings = slingBindings;
        this.elContext = wrapped.getELContext();
        this.elContext.putContext(JspContext.class, (Object)this);
    }

    public void initialize(Servlet servlet, ServletRequest servletRequest, ServletResponse servletResponse, String s, boolean b, int i, boolean b1) throws IOException, IllegalStateException, IllegalArgumentException {
        this.wrapped.initialize(servlet, servletRequest, servletResponse, s, b, i, b1);
    }

    public void release() {
        this.wrapped.release();
    }

    public HttpSession getSession() {
        return this.wrapped.getSession();
    }

    public Object getPage() {
        return this.wrapped.getPage();
    }

    public ServletRequest getRequest() {
        return this.wrapped.getRequest();
    }

    public ServletResponse getResponse() {
        return this.wrapped.getResponse();
    }

    public Exception getException() {
        return this.wrapped.getException();
    }

    public ServletConfig getServletConfig() {
        return this.wrapped.getServletConfig();
    }

    public ServletContext getServletContext() {
        return this.wrapped.getServletContext();
    }

    public void forward(String s) throws ServletException, IOException {
        this.wrapped.forward(s);
    }

    public void include(String s) throws ServletException, IOException {
        this.wrapped.include(s);
    }

    public void include(String s, boolean b) throws ServletException, IOException {
        this.wrapped.include(s, b);
    }

    public void handlePageException(Exception e) throws ServletException, IOException {
        this.wrapped.handlePageException(e);
    }

    public void handlePageException(Throwable throwable) throws ServletException, IOException {
        this.wrapped.handlePageException(throwable);
    }

    public void setAttribute(String s, Object o) {
        this.wrapped.setAttribute(s, o);
    }

    public void setAttribute(String s, Object o, int i) {
        this.wrapped.setAttribute(s, o, i);
    }

    public Object getAttribute(String s) {
        Object attribute = this.wrapped.getAttribute(s);
        if (attribute == null) {
            attribute = this.slingBindings.get((Object)s);
        }
        return attribute;
    }

    public Object getAttribute(String s, int i) {
        return this.wrapped.getAttribute(s, i);
    }

    public Object findAttribute(String s) {
        Object attribute = this.wrapped.findAttribute(s);
        if (attribute == null) {
            attribute = this.slingBindings.get((Object)s);
        }
        return attribute;
    }

    public void removeAttribute(String s) {
        this.wrapped.removeAttribute(s);
    }

    public void removeAttribute(String s, int i) {
        this.wrapped.removeAttribute(s, i);
    }

    public int getAttributesScope(String s) {
        return this.wrapped.getAttributesScope(s);
    }

    public Enumeration<String> getAttributeNamesInScope(int i) {
        return this.wrapped.getAttributeNamesInScope(i);
    }

    public JspWriter getOut() {
        return this.wrapped.getOut();
    }

    public ExpressionEvaluator getExpressionEvaluator() {
        return this.wrapped.getExpressionEvaluator();
    }

    public ELContext getELContext() {
        return this.elContext;
    }

    public VariableResolver getVariableResolver() {
        return this.wrapped.getVariableResolver();
    }

    public BodyContent pushBody() {
        return this.wrapped.pushBody();
    }

    public ErrorData getErrorData() {
        return this.wrapped.getErrorData();
    }

    public JspWriter pushBody(Writer writer) {
        return this.wrapped.pushBody(writer);
    }

    public JspWriter popBody() {
        return this.wrapped.popBody();
    }
}


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
 *  javax.servlet.jsp.JspContext
 *  javax.servlet.jsp.JspWriter
 *  javax.servlet.jsp.PageContext
 *  javax.servlet.jsp.el.ELException
 *  javax.servlet.jsp.el.ExpressionEvaluator
 *  javax.servlet.jsp.el.VariableResolver
 *  javax.servlet.jsp.tagext.BodyContent
 */
package org.apache.sling.scripting.jsp.jasper.runtime;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.el.ELContext;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.el.ELException;
import javax.servlet.jsp.el.ExpressionEvaluator;
import javax.servlet.jsp.el.VariableResolver;
import javax.servlet.jsp.tagext.BodyContent;
import org.apache.sling.scripting.jsp.jasper.compiler.Localizer;

public class JspContextWrapper
extends PageContext
implements VariableResolver {
    private PageContext invokingJspCtxt;
    private transient HashMap<String, Object> pageAttributes;
    private ArrayList nestedVars;
    private ArrayList atBeginVars;
    private ArrayList atEndVars;
    private Map aliases;
    private HashMap<String, Object> originalNestedVars;

    public JspContextWrapper(JspContext jspContext, ArrayList nestedVars, ArrayList atBeginVars, ArrayList atEndVars, Map aliases) {
        this.invokingJspCtxt = (PageContext)jspContext;
        this.nestedVars = nestedVars;
        this.atBeginVars = atBeginVars;
        this.atEndVars = atEndVars;
        this.pageAttributes = new HashMap(16);
        this.aliases = aliases;
        if (nestedVars != null) {
            this.originalNestedVars = new HashMap(nestedVars.size());
        }
        this.syncBeginTagFile();
    }

    public void initialize(Servlet servlet, ServletRequest request, ServletResponse response, String errorPageURL, boolean needsSession, int bufferSize, boolean autoFlush) throws IOException, IllegalStateException, IllegalArgumentException {
    }

    public Object getAttribute(String name) {
        if (name == null) {
            throw new NullPointerException(Localizer.getMessage("jsp.error.attribute.null_name"));
        }
        return this.pageAttributes.get(name);
    }

    public Object getAttribute(String name, int scope) {
        if (name == null) {
            throw new NullPointerException(Localizer.getMessage("jsp.error.attribute.null_name"));
        }
        if (scope == 1) {
            return this.pageAttributes.get(name);
        }
        return this.invokingJspCtxt.getAttribute(name, scope);
    }

    public void setAttribute(String name, Object value) {
        if (name == null) {
            throw new NullPointerException(Localizer.getMessage("jsp.error.attribute.null_name"));
        }
        if (value != null) {
            this.pageAttributes.put(name, value);
        } else {
            this.removeAttribute(name, 1);
        }
    }

    public void setAttribute(String name, Object value, int scope) {
        if (name == null) {
            throw new NullPointerException(Localizer.getMessage("jsp.error.attribute.null_name"));
        }
        if (scope == 1) {
            if (value != null) {
                this.pageAttributes.put(name, value);
            } else {
                this.removeAttribute(name, 1);
            }
        } else {
            this.invokingJspCtxt.setAttribute(name, value, scope);
        }
    }

    public Object findAttribute(String name) {
        if (name == null) {
            throw new NullPointerException(Localizer.getMessage("jsp.error.attribute.null_name"));
        }
        Object o = this.pageAttributes.get(name);
        if (o == null && (o = this.invokingJspCtxt.getAttribute(name, 2)) == null) {
            if (this.getSession() != null) {
                o = this.invokingJspCtxt.getAttribute(name, 3);
            }
            if (o == null) {
                o = this.invokingJspCtxt.getAttribute(name, 4);
            }
        }
        return o;
    }

    public void removeAttribute(String name) {
        if (name == null) {
            throw new NullPointerException(Localizer.getMessage("jsp.error.attribute.null_name"));
        }
        this.pageAttributes.remove(name);
        this.invokingJspCtxt.removeAttribute(name, 2);
        if (this.getSession() != null) {
            this.invokingJspCtxt.removeAttribute(name, 3);
        }
        this.invokingJspCtxt.removeAttribute(name, 4);
    }

    public void removeAttribute(String name, int scope) {
        if (name == null) {
            throw new NullPointerException(Localizer.getMessage("jsp.error.attribute.null_name"));
        }
        if (scope == 1) {
            this.pageAttributes.remove(name);
        } else {
            this.invokingJspCtxt.removeAttribute(name, scope);
        }
    }

    public int getAttributesScope(String name) {
        if (name == null) {
            throw new NullPointerException(Localizer.getMessage("jsp.error.attribute.null_name"));
        }
        if (this.pageAttributes.get(name) != null) {
            return 1;
        }
        return this.invokingJspCtxt.getAttributesScope(name);
    }

    public Enumeration<String> getAttributeNamesInScope(int scope) {
        if (scope == 1) {
            return Collections.enumeration(this.pageAttributes.keySet());
        }
        return this.invokingJspCtxt.getAttributeNamesInScope(scope);
    }

    public void release() {
        this.invokingJspCtxt.release();
    }

    public JspWriter getOut() {
        return this.invokingJspCtxt.getOut();
    }

    public HttpSession getSession() {
        return this.invokingJspCtxt.getSession();
    }

    public Object getPage() {
        return this.invokingJspCtxt.getPage();
    }

    public ServletRequest getRequest() {
        return this.invokingJspCtxt.getRequest();
    }

    public ServletResponse getResponse() {
        return this.invokingJspCtxt.getResponse();
    }

    public Exception getException() {
        return this.invokingJspCtxt.getException();
    }

    public ServletConfig getServletConfig() {
        return this.invokingJspCtxt.getServletConfig();
    }

    public ServletContext getServletContext() {
        return this.invokingJspCtxt.getServletContext();
    }

    public void forward(String relativeUrlPath) throws ServletException, IOException {
        this.invokingJspCtxt.forward(relativeUrlPath);
    }

    public void include(String relativeUrlPath) throws ServletException, IOException {
        this.invokingJspCtxt.include(relativeUrlPath);
    }

    public void include(String relativeUrlPath, boolean flush) throws ServletException, IOException {
        this.include(relativeUrlPath, false);
    }

    public VariableResolver getVariableResolver() {
        return this;
    }

    public BodyContent pushBody() {
        return this.invokingJspCtxt.pushBody();
    }

    public JspWriter pushBody(Writer writer) {
        return this.invokingJspCtxt.pushBody(writer);
    }

    public JspWriter popBody() {
        return this.invokingJspCtxt.popBody();
    }

    public ExpressionEvaluator getExpressionEvaluator() {
        return this.invokingJspCtxt.getExpressionEvaluator();
    }

    public void handlePageException(Exception ex) throws IOException, ServletException {
        this.handlePageException((Throwable)ex);
    }

    public void handlePageException(Throwable t) throws IOException, ServletException {
        this.invokingJspCtxt.handlePageException(t);
    }

    public Object resolveVariable(String pName) throws ELException {
        ELContext ctx = this.getELContext();
        return ctx.getELResolver().getValue(ctx, null, (Object)pName);
    }

    public void syncBeginTagFile() {
        this.saveNestedVariables();
    }

    public void syncBeforeInvoke() {
        this.copyTagToPageScope(0);
        this.copyTagToPageScope(1);
    }

    public void syncEndTagFile() {
        this.copyTagToPageScope(1);
        this.copyTagToPageScope(2);
        this.restoreNestedVariables();
    }

    private void copyTagToPageScope(int scope) {
        Iterator iter = null;
        switch (scope) {
            case 0: {
                if (this.nestedVars == null) break;
                iter = this.nestedVars.iterator();
                break;
            }
            case 1: {
                if (this.atBeginVars == null) break;
                iter = this.atBeginVars.iterator();
                break;
            }
            case 2: {
                if (this.atEndVars == null) break;
                iter = this.atEndVars.iterator();
            }
        }
        while (iter != null && iter.hasNext()) {
            String varName = (String)iter.next();
            Object obj = this.getAttribute(varName);
            varName = this.findAlias(varName);
            if (obj != null) {
                this.invokingJspCtxt.setAttribute(varName, obj);
                continue;
            }
            this.invokingJspCtxt.removeAttribute(varName, 1);
        }
    }

    private void saveNestedVariables() {
        if (this.nestedVars != null) {
            for (String varName : this.nestedVars) {
                Object obj = this.invokingJspCtxt.getAttribute(varName = this.findAlias(varName));
                if (obj == null) continue;
                this.originalNestedVars.put(varName, obj);
            }
        }
    }

    private void restoreNestedVariables() {
        if (this.nestedVars != null) {
            for (String varName : this.nestedVars) {
                Object obj = this.originalNestedVars.get(varName = this.findAlias(varName));
                if (obj != null) {
                    this.invokingJspCtxt.setAttribute(varName, obj);
                    continue;
                }
                this.invokingJspCtxt.removeAttribute(varName, 1);
            }
        }
    }

    private String findAlias(String varName) {
        if (this.aliases == null) {
            return varName;
        }
        String alias = (String)this.aliases.get(varName);
        if (alias == null) {
            return varName;
        }
        return alias;
    }

    public ELContext getELContext() {
        return this.invokingJspCtxt.getELContext();
    }
}


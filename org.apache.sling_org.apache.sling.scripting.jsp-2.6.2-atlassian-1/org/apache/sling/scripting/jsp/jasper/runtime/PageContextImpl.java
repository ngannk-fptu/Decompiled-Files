/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.ELContext
 *  javax.el.ExpressionFactory
 *  javax.el.ValueExpression
 *  javax.servlet.Servlet
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpSession
 *  javax.servlet.jsp.JspContext
 *  javax.servlet.jsp.JspException
 *  javax.servlet.jsp.JspFactory
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
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspFactory;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.el.ELException;
import javax.servlet.jsp.el.ExpressionEvaluator;
import javax.servlet.jsp.el.VariableResolver;
import javax.servlet.jsp.tagext.BodyContent;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.sling.scripting.jsp.SlingPageException;
import org.apache.sling.scripting.jsp.jasper.compiler.Localizer;
import org.apache.sling.scripting.jsp.jasper.el.ELContextImpl;
import org.apache.sling.scripting.jsp.jasper.el.ExpressionEvaluatorImpl;
import org.apache.sling.scripting.jsp.jasper.el.FunctionMapperImpl;
import org.apache.sling.scripting.jsp.jasper.el.VariableResolverImpl;
import org.apache.sling.scripting.jsp.jasper.runtime.BodyContentImpl;
import org.apache.sling.scripting.jsp.jasper.runtime.InternalServletConfigWrapper;
import org.apache.sling.scripting.jsp.jasper.runtime.JspApplicationContextImpl;
import org.apache.sling.scripting.jsp.jasper.runtime.JspRuntimeLibrary;
import org.apache.sling.scripting.jsp.jasper.runtime.JspWriterImpl;
import org.apache.sling.scripting.jsp.jasper.runtime.ProtectedFunctionMapper;
import org.apache.sling.scripting.jsp.jasper.runtime.ServletResponseWrapperInclude;
import org.apache.sling.scripting.jsp.jasper.security.SecurityUtil;

public class PageContextImpl
extends PageContext {
    private Log log = LogFactory.getLog(PageContextImpl.class);
    private BodyContentImpl[] outs = new BodyContentImpl[0];
    private int depth = -1;
    private Servlet servlet;
    private ServletConfig config;
    private ServletContext context;
    private JspApplicationContextImpl applicationContext;
    private String errorPageURL;
    private transient HashMap<String, Object> attributes = new HashMap(16);
    private transient ServletRequest request;
    private transient ServletResponse response;
    private transient HttpSession session;
    private transient ELContextImpl elContext;
    private boolean isIncluded;
    private transient JspWriter out;
    private transient JspWriterImpl baseOut;

    PageContextImpl() {
    }

    public void initialize(Servlet servlet, ServletRequest request, ServletResponse response, String errorPageURL, boolean needsSession, int bufferSize, boolean autoFlush) throws IOException {
        this._initialize(servlet, request, response, errorPageURL, needsSession, bufferSize, autoFlush);
    }

    private void _initialize(Servlet servlet, ServletRequest request, ServletResponse response, String errorPageURL, boolean needsSession, int bufferSize, boolean autoFlush) throws IOException {
        this.servlet = servlet;
        this.config = new InternalServletConfigWrapper(servlet.getServletConfig(), this);
        this.context = this.config.getServletContext();
        this.errorPageURL = errorPageURL;
        this.request = request;
        this.response = response;
        this.applicationContext = JspApplicationContextImpl.getInstance(this.context);
        if (request instanceof HttpServletRequest && needsSession) {
            this.session = ((HttpServletRequest)request).getSession();
        }
        if (needsSession && this.session == null) {
            throw new IllegalStateException("Page needs a session and none is available");
        }
        this.depth = -1;
        if (this.baseOut == null) {
            this.baseOut = new JspWriterImpl(response, bufferSize, autoFlush);
        } else {
            this.baseOut.init(response, bufferSize, autoFlush);
        }
        this.out = this.baseOut;
        this.setAttribute("javax.servlet.jsp.jspOut", this.out);
        this.setAttribute("javax.servlet.jsp.jspRequest", request);
        this.setAttribute("javax.servlet.jsp.jspResponse", response);
        if (this.session != null) {
            this.setAttribute("javax.servlet.jsp.jspSession", this.session);
        }
        this.setAttribute("javax.servlet.jsp.jspPage", servlet);
        this.setAttribute("javax.servlet.jsp.jspConfig", this.config);
        this.setAttribute("javax.servlet.jsp.jspPageContext", (Object)this);
        this.setAttribute("javax.servlet.jsp.jspApplication", this.context);
        this.isIncluded = request.getAttribute("javax.servlet.include.servlet_path") != null;
    }

    public void release() {
        this.out = this.baseOut;
        try {
            if (this.isIncluded) {
                ((JspWriterImpl)this.out).flushBuffer();
            } else {
                ((JspWriterImpl)this.out).flushBuffer();
            }
        }
        catch (IOException ex) {
            IllegalStateException ise = new IllegalStateException(Localizer.getMessage("jsp.error.flush"), ex);
            throw ise;
        }
        finally {
            this.servlet = null;
            this.config = null;
            this.context = null;
            this.applicationContext = null;
            this.elContext = null;
            this.errorPageURL = null;
            this.request = null;
            this.response = null;
            this.depth = -1;
            this.baseOut.recycle();
            this.session = null;
            this.attributes.clear();
        }
    }

    public Object getAttribute(final String name) {
        if (name == null) {
            throw new NullPointerException(Localizer.getMessage("jsp.error.attribute.null_name"));
        }
        if (SecurityUtil.isPackageProtectionEnabled()) {
            return AccessController.doPrivileged(new PrivilegedAction(){

                public Object run() {
                    return PageContextImpl.this.doGetAttribute(name);
                }
            });
        }
        return this.doGetAttribute(name);
    }

    private Object doGetAttribute(String name) {
        return this.attributes.get(name);
    }

    public Object getAttribute(final String name, final int scope) {
        if (name == null) {
            throw new NullPointerException(Localizer.getMessage("jsp.error.attribute.null_name"));
        }
        if (SecurityUtil.isPackageProtectionEnabled()) {
            return AccessController.doPrivileged(new PrivilegedAction(){

                public Object run() {
                    return PageContextImpl.this.doGetAttribute(name, scope);
                }
            });
        }
        return this.doGetAttribute(name, scope);
    }

    private Object doGetAttribute(String name, int scope) {
        switch (scope) {
            case 1: {
                return this.attributes.get(name);
            }
            case 2: {
                return this.request.getAttribute(name);
            }
            case 3: {
                if (this.session == null) {
                    throw new IllegalStateException(Localizer.getMessage("jsp.error.page.noSession"));
                }
                return this.session.getAttribute(name);
            }
            case 4: {
                return this.context.getAttribute(name);
            }
        }
        throw new IllegalArgumentException("Invalid scope");
    }

    public void setAttribute(final String name, final Object attribute) {
        if (name == null) {
            throw new NullPointerException(Localizer.getMessage("jsp.error.attribute.null_name"));
        }
        if (SecurityUtil.isPackageProtectionEnabled()) {
            AccessController.doPrivileged(new PrivilegedAction(){

                public Object run() {
                    PageContextImpl.this.doSetAttribute(name, attribute);
                    return null;
                }
            });
        } else {
            this.doSetAttribute(name, attribute);
        }
    }

    private void doSetAttribute(String name, Object attribute) {
        if (attribute != null) {
            this.attributes.put(name, attribute);
        } else {
            this.removeAttribute(name, 1);
        }
    }

    public void setAttribute(final String name, final Object o, final int scope) {
        if (name == null) {
            throw new NullPointerException(Localizer.getMessage("jsp.error.attribute.null_name"));
        }
        if (SecurityUtil.isPackageProtectionEnabled()) {
            AccessController.doPrivileged(new PrivilegedAction(){

                public Object run() {
                    PageContextImpl.this.doSetAttribute(name, o, scope);
                    return null;
                }
            });
        } else {
            this.doSetAttribute(name, o, scope);
        }
    }

    private void doSetAttribute(String name, Object o, int scope) {
        block8: {
            block7: {
                if (o == null) break block7;
                switch (scope) {
                    case 1: {
                        this.attributes.put(name, o);
                        break block8;
                    }
                    case 2: {
                        this.request.setAttribute(name, o);
                        break block8;
                    }
                    case 3: {
                        if (this.session == null) {
                            throw new IllegalStateException(Localizer.getMessage("jsp.error.page.noSession"));
                        }
                        this.session.setAttribute(name, o);
                        break block8;
                    }
                    case 4: {
                        this.context.setAttribute(name, o);
                        break block8;
                    }
                    default: {
                        throw new IllegalArgumentException("Invalid scope");
                    }
                }
            }
            this.removeAttribute(name, scope);
        }
    }

    public void removeAttribute(final String name, final int scope) {
        if (name == null) {
            throw new NullPointerException(Localizer.getMessage("jsp.error.attribute.null_name"));
        }
        if (SecurityUtil.isPackageProtectionEnabled()) {
            AccessController.doPrivileged(new PrivilegedAction(){

                public Object run() {
                    PageContextImpl.this.doRemoveAttribute(name, scope);
                    return null;
                }
            });
        } else {
            this.doRemoveAttribute(name, scope);
        }
    }

    private void doRemoveAttribute(String name, int scope) {
        switch (scope) {
            case 1: {
                this.attributes.remove(name);
                break;
            }
            case 2: {
                this.request.removeAttribute(name);
                break;
            }
            case 3: {
                if (this.session == null) {
                    throw new IllegalStateException(Localizer.getMessage("jsp.error.page.noSession"));
                }
                this.session.removeAttribute(name);
                break;
            }
            case 4: {
                this.context.removeAttribute(name);
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid scope");
            }
        }
    }

    public int getAttributesScope(final String name) {
        if (name == null) {
            throw new NullPointerException(Localizer.getMessage("jsp.error.attribute.null_name"));
        }
        if (SecurityUtil.isPackageProtectionEnabled()) {
            return (Integer)AccessController.doPrivileged(new PrivilegedAction(){

                public Object run() {
                    return new Integer(PageContextImpl.this.doGetAttributeScope(name));
                }
            });
        }
        return this.doGetAttributeScope(name);
    }

    private int doGetAttributeScope(String name) {
        if (this.attributes.get(name) != null) {
            return 1;
        }
        if (this.request.getAttribute(name) != null) {
            return 2;
        }
        if (this.session != null && this.session.getAttribute(name) != null) {
            return 3;
        }
        if (this.context.getAttribute(name) != null) {
            return 4;
        }
        return 0;
    }

    public Object findAttribute(final String name) {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            return AccessController.doPrivileged(new PrivilegedAction(){

                public Object run() {
                    if (name == null) {
                        throw new NullPointerException(Localizer.getMessage("jsp.error.attribute.null_name"));
                    }
                    return PageContextImpl.this.doFindAttribute(name);
                }
            });
        }
        if (name == null) {
            throw new NullPointerException(Localizer.getMessage("jsp.error.attribute.null_name"));
        }
        return this.doFindAttribute(name);
    }

    private Object doFindAttribute(String name) {
        Object o = this.attributes.get(name);
        if (o != null) {
            return o;
        }
        o = this.request.getAttribute(name);
        if (o != null) {
            return o;
        }
        if (this.session != null && (o = this.session.getAttribute(name)) != null) {
            return o;
        }
        return this.context.getAttribute(name);
    }

    public Enumeration<String> getAttributeNamesInScope(final int scope) {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            return (Enumeration)AccessController.doPrivileged(new PrivilegedAction(){

                public Object run() {
                    return PageContextImpl.this.doGetAttributeNamesInScope(scope);
                }
            });
        }
        return this.doGetAttributeNamesInScope(scope);
    }

    private Enumeration doGetAttributeNamesInScope(int scope) {
        switch (scope) {
            case 1: {
                return Collections.enumeration(this.attributes.keySet());
            }
            case 2: {
                return this.request.getAttributeNames();
            }
            case 3: {
                if (this.session == null) {
                    throw new IllegalStateException(Localizer.getMessage("jsp.error.page.noSession"));
                }
                return this.session.getAttributeNames();
            }
            case 4: {
                return this.context.getAttributeNames();
            }
        }
        throw new IllegalArgumentException("Invalid scope");
    }

    public void removeAttribute(final String name) {
        if (name == null) {
            throw new NullPointerException(Localizer.getMessage("jsp.error.attribute.null_name"));
        }
        if (SecurityUtil.isPackageProtectionEnabled()) {
            AccessController.doPrivileged(new PrivilegedAction(){

                public Object run() {
                    PageContextImpl.this.doRemoveAttribute(name);
                    return null;
                }
            });
        } else {
            this.doRemoveAttribute(name);
        }
    }

    private void doRemoveAttribute(String name) {
        try {
            this.removeAttribute(name, 1);
            this.removeAttribute(name, 2);
            if (this.session != null) {
                this.removeAttribute(name, 3);
            }
            this.removeAttribute(name, 4);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public JspWriter getOut() {
        return this.out;
    }

    public HttpSession getSession() {
        return this.session;
    }

    public Servlet getServlet() {
        return this.servlet;
    }

    public ServletConfig getServletConfig() {
        return this.config;
    }

    public ServletContext getServletContext() {
        return this.config.getServletContext();
    }

    public ServletRequest getRequest() {
        return this.request;
    }

    public ServletResponse getResponse() {
        return this.response;
    }

    public Exception getException() {
        Throwable t = JspRuntimeLibrary.getThrowable(this.request);
        if (t != null && !(t instanceof Exception)) {
            t = new JspException(t);
        }
        return (Exception)t;
    }

    public Object getPage() {
        return this.servlet;
    }

    private final String getAbsolutePathRelativeToContext(String relativeUrlPath) {
        String path = relativeUrlPath;
        if (!path.startsWith("/")) {
            String uri = (String)this.request.getAttribute("javax.servlet.include.servlet_path");
            if (uri == null) {
                uri = ((HttpServletRequest)this.request).getServletPath();
            }
            String baseURI = uri.substring(0, uri.lastIndexOf(47));
            path = baseURI + '/' + path;
        }
        return path;
    }

    public void include(String relativeUrlPath) throws ServletException, IOException {
        JspRuntimeLibrary.include(this.request, this.response, relativeUrlPath, this.out, true);
    }

    public void include(final String relativeUrlPath, final boolean flush) throws ServletException, IOException {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            try {
                AccessController.doPrivileged(new PrivilegedExceptionAction(){

                    public Object run() throws Exception {
                        PageContextImpl.this.doInclude(relativeUrlPath, flush);
                        return null;
                    }
                });
            }
            catch (PrivilegedActionException e) {
                Exception ex = e.getException();
                if (ex instanceof IOException) {
                    throw (IOException)ex;
                }
                throw (ServletException)((Object)ex);
            }
        } else {
            this.doInclude(relativeUrlPath, flush);
        }
    }

    private void doInclude(String relativeUrlPath, boolean flush) throws ServletException, IOException {
        JspRuntimeLibrary.include(this.request, this.response, relativeUrlPath, this.out, flush);
    }

    public VariableResolver getVariableResolver() {
        return new VariableResolverImpl(this.getELContext());
    }

    public void forward(final String relativeUrlPath) throws ServletException, IOException {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            try {
                AccessController.doPrivileged(new PrivilegedExceptionAction(){

                    public Object run() throws Exception {
                        PageContextImpl.this.doForward(relativeUrlPath);
                        return null;
                    }
                });
            }
            catch (PrivilegedActionException e) {
                Exception ex = e.getException();
                if (ex instanceof IOException) {
                    throw (IOException)ex;
                }
                throw (ServletException)((Object)ex);
            }
        } else {
            this.doForward(relativeUrlPath);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void doForward(String relativeUrlPath) throws ServletException, IOException {
        try {
            this.out.clear();
        }
        catch (IOException ex) {
            IllegalStateException ise = new IllegalStateException(Localizer.getMessage("jsp.error.attempt_to_clear_flushed_buffer"));
            ise.initCause(ex);
            throw ise;
        }
        while (this.response instanceof ServletResponseWrapperInclude) {
            this.response = ((ServletResponseWrapperInclude)this.response).getResponse();
        }
        String path = this.getAbsolutePathRelativeToContext(relativeUrlPath);
        String includeUri = (String)this.request.getAttribute("javax.servlet.include.servlet_path");
        if (includeUri != null) {
            this.request.removeAttribute("javax.servlet.include.servlet_path");
        }
        try {
            this.context.getRequestDispatcher(path).forward(this.request, this.response);
        }
        finally {
            if (includeUri != null) {
                this.request.setAttribute("javax.servlet.include.servlet_path", (Object)includeUri);
            }
        }
    }

    public void forwardToErrorPage(final String relativeUrlPath) throws ServletException, IOException {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            try {
                AccessController.doPrivileged(new PrivilegedExceptionAction(){

                    public Object run() throws Exception {
                        PageContextImpl.this.doForwardToErrorPage(relativeUrlPath);
                        return null;
                    }
                });
            }
            catch (PrivilegedActionException e) {
                Exception ex = e.getException();
                if (ex instanceof IOException) {
                    throw (IOException)ex;
                }
                throw (ServletException)((Object)ex);
            }
        } else {
            this.doForwardToErrorPage(relativeUrlPath);
        }
    }

    private void doForwardToErrorPage(String relativeUrlPath) throws ServletException, IOException {
        try {
            this.out.clear();
        }
        catch (IOException ex) {
            IllegalStateException ise = new IllegalStateException(Localizer.getMessage("jsp.error.attempt_to_clear_flushed_buffer"));
            ise.initCause(ex);
            throw ise;
        }
        while (this.response instanceof ServletResponseWrapperInclude) {
            this.response = ((ServletResponseWrapperInclude)this.response).getResponse();
        }
        String path = this.getAbsolutePathRelativeToContext(relativeUrlPath);
        throw new SlingPageException(path);
    }

    public BodyContent pushBody() {
        return (BodyContent)this.pushBody(null);
    }

    public JspWriter pushBody(Writer writer) {
        ++this.depth;
        if (this.depth >= this.outs.length) {
            BodyContentImpl[] newOuts = new BodyContentImpl[this.depth + 1];
            for (int i = 0; i < this.outs.length; ++i) {
                newOuts[i] = this.outs[i];
            }
            newOuts[this.depth] = new BodyContentImpl(this.out);
            this.outs = newOuts;
        }
        this.outs[this.depth].setWriter(writer);
        this.out = this.outs[this.depth];
        this.setAttribute("javax.servlet.jsp.jspOut", this.out);
        return this.outs[this.depth];
    }

    public JspWriter popBody() {
        --this.depth;
        this.out = this.depth >= 0 ? this.outs[this.depth] : this.baseOut;
        this.setAttribute("javax.servlet.jsp.jspOut", this.out);
        return this.out;
    }

    public ExpressionEvaluator getExpressionEvaluator() {
        return new ExpressionEvaluatorImpl(this.applicationContext.getExpressionFactory());
    }

    public void handlePageException(Exception ex) throws IOException, ServletException {
        this.handlePageException((Throwable)ex);
    }

    public void handlePageException(final Throwable t) throws IOException, ServletException {
        if (t == null) {
            throw new NullPointerException("null Throwable");
        }
        if (SecurityUtil.isPackageProtectionEnabled()) {
            try {
                AccessController.doPrivileged(new PrivilegedExceptionAction(){

                    public Object run() throws Exception {
                        PageContextImpl.this.doHandlePageException(t);
                        return null;
                    }
                });
            }
            catch (PrivilegedActionException e) {
                Exception ex = e.getException();
                if (ex instanceof IOException) {
                    throw (IOException)ex;
                }
                throw (ServletException)((Object)ex);
            }
        } else {
            this.doHandlePageException(t);
        }
    }

    private void doHandlePageException(Throwable t) throws IOException, ServletException {
        if (this.errorPageURL != null && !this.errorPageURL.equals("")) {
            this.request.setAttribute("javax.servlet.jsp.jspException", (Object)t);
            this.request.setAttribute("javax.servlet.error.status_code", (Object)new Integer(500));
            this.request.setAttribute("javax.servlet.error.request_uri", (Object)((HttpServletRequest)this.request).getRequestURI());
            this.request.setAttribute("javax.servlet.error.servlet_name", (Object)this.config.getServletName());
            try {
                this.forwardToErrorPage(this.errorPageURL);
            }
            catch (IllegalStateException ise) {
                this.include(this.errorPageURL);
            }
            Object newException = this.request.getAttribute("javax.servlet.error.exception");
            if (newException != null && newException == t) {
                this.request.removeAttribute("javax.servlet.error.exception");
            }
        } else {
            if (t instanceof IOException) {
                throw (IOException)t;
            }
            if (t instanceof ServletException) {
                throw (ServletException)t;
            }
            if (t instanceof RuntimeException) {
                throw (RuntimeException)t;
            }
            Throwable rootCause = null;
            if (t instanceof JspException) {
                rootCause = ((JspException)t).getRootCause();
            } else if (t instanceof ELException) {
                rootCause = ((ELException)t).getRootCause();
            }
            if (rootCause != null) {
                throw new ServletException(t.getClass().getName() + ": " + t.getMessage(), rootCause);
            }
            throw new ServletException(t);
        }
        this.request.removeAttribute("javax.servlet.error.status_code");
        this.request.removeAttribute("javax.servlet.error.request_uri");
        this.request.removeAttribute("javax.servlet.error.status_code");
        this.request.removeAttribute("javax.servlet.jsp.jspException");
    }

    private static String XmlEscape(String s) {
        if (s == null) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (c == '<') {
                sb.append("&lt;");
                continue;
            }
            if (c == '>') {
                sb.append("&gt;");
                continue;
            }
            if (c == '\'') {
                sb.append("&#039;");
                continue;
            }
            if (c == '&') {
                sb.append("&amp;");
                continue;
            }
            if (c == '\"') {
                sb.append("&#034;");
                continue;
            }
            sb.append(c);
        }
        return sb.toString();
    }

    public static Object proprietaryEvaluate(final String expression, final Class expectedType, final PageContext pageContext, final ProtectedFunctionMapper functionMap, boolean escape) throws ELException {
        Object retValue;
        final ExpressionFactory exprFactory = JspFactory.getDefaultFactory().getJspApplicationContext(pageContext.getServletContext()).getExpressionFactory();
        if (SecurityUtil.isPackageProtectionEnabled()) {
            try {
                retValue = AccessController.doPrivileged(new PrivilegedExceptionAction(){

                    public Object run() throws Exception {
                        ELContextImpl ctx = (ELContextImpl)pageContext.getELContext();
                        ctx.setFunctionMapper(new FunctionMapperImpl(functionMap));
                        ValueExpression ve = exprFactory.createValueExpression((ELContext)ctx, expression, expectedType);
                        return ve.getValue((ELContext)ctx);
                    }
                });
            }
            catch (PrivilegedActionException ex) {
                Exception realEx = ex.getException();
                if (realEx instanceof ELException) {
                    throw (ELException)((Object)realEx);
                }
                throw new ELException((Throwable)realEx);
            }
        } else {
            ELContextImpl ctx = (ELContextImpl)pageContext.getELContext();
            ctx.setFunctionMapper(new FunctionMapperImpl(functionMap));
            ValueExpression ve = exprFactory.createValueExpression((ELContext)ctx, expression, expectedType);
            retValue = ve.getValue((ELContext)ctx);
        }
        if (escape && retValue != null) {
            retValue = PageContextImpl.XmlEscape(retValue.toString());
        }
        return retValue;
    }

    public ELContext getELContext() {
        if (this.elContext == null) {
            this.elContext = this.applicationContext.createELContext((JspContext)this);
        }
        return this.elContext;
    }
}


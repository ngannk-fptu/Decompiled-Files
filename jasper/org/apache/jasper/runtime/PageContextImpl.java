/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.ELContext
 *  javax.el.ELException
 *  javax.el.ExpressionFactory
 *  javax.el.ImportHandler
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
package org.apache.jasper.runtime;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Set;
import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ImportHandler;
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
import org.apache.jasper.compiler.Localizer;
import org.apache.jasper.el.ELContextImpl;
import org.apache.jasper.el.ExpressionEvaluatorImpl;
import org.apache.jasper.el.VariableResolverImpl;
import org.apache.jasper.runtime.BodyContentImpl;
import org.apache.jasper.runtime.JspApplicationContextImpl;
import org.apache.jasper.runtime.JspContextWrapper;
import org.apache.jasper.runtime.JspRuntimeLibrary;
import org.apache.jasper.runtime.JspSourceImports;
import org.apache.jasper.runtime.JspWriterImpl;
import org.apache.jasper.runtime.ProtectedFunctionMapper;
import org.apache.jasper.runtime.ServletResponseWrapperInclude;

public class PageContextImpl
extends PageContext {
    private static final JspFactory jspf = JspFactory.getDefaultFactory();
    private BodyContentImpl[] outs = new BodyContentImpl[0];
    private int depth = -1;
    private Servlet servlet;
    private ServletConfig config;
    private ServletContext context;
    private JspApplicationContextImpl applicationContext;
    private String errorPageURL;
    private final transient HashMap<String, Object> attributes = new HashMap(16);
    private transient ServletRequest request;
    private transient ServletResponse response;
    private transient HttpSession session;
    private transient ELContextImpl elContext;
    private transient JspWriter out;
    private transient JspWriterImpl baseOut;

    PageContextImpl() {
    }

    public void initialize(Servlet servlet, ServletRequest request, ServletResponse response, String errorPageURL, boolean needsSession, int bufferSize, boolean autoFlush) throws IOException {
        this.servlet = servlet;
        this.config = servlet.getServletConfig();
        this.context = this.config.getServletContext();
        this.errorPageURL = errorPageURL;
        this.request = request;
        this.response = response;
        this.applicationContext = JspApplicationContextImpl.getInstance(this.context);
        if (request instanceof HttpServletRequest && needsSession) {
            this.session = ((HttpServletRequest)request).getSession();
        }
        if (needsSession && this.session == null) {
            throw new IllegalStateException(Localizer.getMessage("jsp.error.page.sessionRequired"));
        }
        this.depth = -1;
        if (bufferSize == -1) {
            bufferSize = 8192;
        }
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
    }

    public void release() {
        this.out = this.baseOut;
        try {
            ((JspWriterImpl)this.out).flushBuffer();
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
            for (BodyContentImpl body : this.outs) {
                body.recycle();
            }
        }
    }

    public Object getAttribute(String name) {
        return this.getAttribute(name, 1);
    }

    public Object getAttribute(String name, int scope) {
        if (name == null) {
            throw new NullPointerException(Localizer.getMessage("jsp.error.attribute.null_name"));
        }
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
        throw new IllegalArgumentException(Localizer.getMessage("jsp.error.page.invalid.scope"));
    }

    public void setAttribute(String name, Object attribute) {
        this.setAttribute(name, attribute, 1);
    }

    public void setAttribute(String name, Object o, int scope) {
        if (name == null) {
            throw new NullPointerException(Localizer.getMessage("jsp.error.attribute.null_name"));
        }
        if (o == null) {
            this.removeAttribute(name, scope);
        } else {
            switch (scope) {
                case 1: {
                    this.attributes.put(name, o);
                    break;
                }
                case 2: {
                    this.request.setAttribute(name, o);
                    break;
                }
                case 3: {
                    if (this.session == null) {
                        throw new IllegalStateException(Localizer.getMessage("jsp.error.page.noSession"));
                    }
                    this.session.setAttribute(name, o);
                    break;
                }
                case 4: {
                    this.context.setAttribute(name, o);
                    break;
                }
                default: {
                    throw new IllegalArgumentException(Localizer.getMessage("jsp.error.page.invalid.scope"));
                }
            }
        }
    }

    public void removeAttribute(String name, int scope) {
        if (name == null) {
            throw new NullPointerException(Localizer.getMessage("jsp.error.attribute.null_name"));
        }
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
                throw new IllegalArgumentException(Localizer.getMessage("jsp.error.page.invalid.scope"));
            }
        }
    }

    public int getAttributesScope(String name) {
        if (name == null) {
            throw new NullPointerException(Localizer.getMessage("jsp.error.attribute.null_name"));
        }
        if (this.attributes.get(name) != null) {
            return 1;
        }
        if (this.request.getAttribute(name) != null) {
            return 2;
        }
        if (this.session != null) {
            try {
                if (this.session.getAttribute(name) != null) {
                    return 3;
                }
            }
            catch (IllegalStateException illegalStateException) {
                // empty catch block
            }
        }
        if (this.context.getAttribute(name) != null) {
            return 4;
        }
        return 0;
    }

    public Object findAttribute(String name) {
        if (name == null) {
            throw new NullPointerException(Localizer.getMessage("jsp.error.attribute.null_name"));
        }
        Object o = this.attributes.get(name);
        if (o != null) {
            return o;
        }
        o = this.request.getAttribute(name);
        if (o != null) {
            return o;
        }
        if (this.session != null) {
            try {
                o = this.session.getAttribute(name);
            }
            catch (IllegalStateException illegalStateException) {
                // empty catch block
            }
            if (o != null) {
                return o;
            }
        }
        return this.context.getAttribute(name);
    }

    public Enumeration<String> getAttributeNamesInScope(int scope) {
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
        throw new IllegalArgumentException(Localizer.getMessage("jsp.error.page.invalid.scope"));
    }

    public void removeAttribute(String name) {
        if (name == null) {
            throw new NullPointerException(Localizer.getMessage("jsp.error.attribute.null_name"));
        }
        this.removeAttribute(name, 1);
        this.removeAttribute(name, 2);
        if (this.session != null) {
            try {
                this.removeAttribute(name, 3);
            }
            catch (IllegalStateException illegalStateException) {
                // empty catch block
            }
        }
        this.removeAttribute(name, 4);
    }

    public JspWriter getOut() {
        return this.out;
    }

    public HttpSession getSession() {
        return this.session;
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

    private String getAbsolutePathRelativeToContext(String relativeUrlPath) {
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

    public void include(String relativeUrlPath, boolean flush) throws ServletException, IOException {
        JspRuntimeLibrary.include(this.request, this.response, relativeUrlPath, this.out, flush);
    }

    @Deprecated
    public VariableResolver getVariableResolver() {
        return new VariableResolverImpl(this.getELContext());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void forward(String relativeUrlPath) throws ServletException, IOException {
        try {
            this.out.clear();
            this.baseOut.clear();
        }
        catch (IOException ex) {
            throw new IllegalStateException(Localizer.getMessage("jsp.error.attempt_to_clear_flushed_buffer"), ex);
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

    public BodyContent pushBody() {
        return (BodyContent)this.pushBody(null);
    }

    public JspWriter pushBody(Writer writer) {
        ++this.depth;
        if (this.depth >= this.outs.length) {
            BodyContentImpl[] newOuts = Arrays.copyOf(this.outs, this.depth + 1);
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

    @Deprecated
    public ExpressionEvaluator getExpressionEvaluator() {
        return new ExpressionEvaluatorImpl(this.applicationContext.getExpressionFactory());
    }

    public void handlePageException(Exception ex) throws IOException, ServletException {
        this.handlePageException((Throwable)ex);
    }

    public void handlePageException(Throwable t) throws IOException, ServletException {
        if (t == null) {
            throw new NullPointerException(Localizer.getMessage("jsp.error.page.nullThrowable"));
        }
        if (this.errorPageURL != null && !this.errorPageURL.equals("")) {
            this.request.setAttribute("javax.servlet.jsp.jspException", (Object)t);
            this.request.setAttribute("javax.servlet.error.status_code", (Object)500);
            this.request.setAttribute("javax.servlet.error.request_uri", (Object)((HttpServletRequest)this.request).getRequestURI());
            this.request.setAttribute("javax.servlet.error.servlet_name", (Object)this.config.getServletName());
            try {
                this.forward(this.errorPageURL);
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
            if (t instanceof JspException || t instanceof javax.el.ELException || t instanceof ELException) {
                rootCause = t.getCause();
            }
            if (rootCause != null) {
                throw new ServletException(t.getClass().getName() + ": " + t.getMessage(), rootCause);
            }
            throw new ServletException(t);
        }
        this.request.removeAttribute("javax.servlet.error.status_code");
        this.request.removeAttribute("javax.servlet.error.request_uri");
        this.request.removeAttribute("javax.servlet.error.servlet_name");
        this.request.removeAttribute("javax.servlet.jsp.jspException");
    }

    public static Object proprietaryEvaluate(String expression, Class<?> expectedType, PageContext pageContext, ProtectedFunctionMapper functionMap) throws javax.el.ELException {
        ExpressionFactory exprFactory = jspf.getJspApplicationContext(pageContext.getServletContext()).getExpressionFactory();
        ELContext ctx = pageContext.getELContext();
        ELContextImpl ctxImpl = ctx instanceof JspContextWrapper.ELContextWrapper ? (ELContextImpl)((JspContextWrapper.ELContextWrapper)ctx).getWrappedELContext() : (ELContextImpl)ctx;
        ctxImpl.setFunctionMapper(functionMap);
        ValueExpression ve = exprFactory.createValueExpression(ctx, expression, expectedType);
        return ve.getValue(ctx);
    }

    public ELContext getELContext() {
        if (this.elContext == null) {
            this.elContext = this.applicationContext.createELContext((JspContext)this);
            if (this.servlet instanceof JspSourceImports) {
                Set<String> classImports;
                ImportHandler ih = this.elContext.getImportHandler();
                Set<String> packageImports = ((JspSourceImports)this.servlet).getPackageImports();
                if (packageImports != null) {
                    for (String packageImport : packageImports) {
                        ih.importPackage(packageImport);
                    }
                }
                if ((classImports = ((JspSourceImports)this.servlet).getClassImports()) != null) {
                    for (String classImport : classImports) {
                        if (classImport.startsWith("static ")) {
                            classImport = classImport.substring(7);
                            try {
                                ih.importStatic(classImport);
                            }
                            catch (javax.el.ELException eLException) {}
                            continue;
                        }
                        ih.importClass(classImport);
                    }
                }
            }
        }
        return this.elContext;
    }
}


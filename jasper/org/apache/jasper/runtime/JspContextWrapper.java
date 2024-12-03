/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.ELContext
 *  javax.el.ELResolver
 *  javax.el.EvaluationListener
 *  javax.el.FunctionMapper
 *  javax.el.ImportHandler
 *  javax.el.VariableMapper
 *  javax.servlet.Servlet
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpSession
 *  javax.servlet.jsp.JspApplicationContext
 *  javax.servlet.jsp.JspContext
 *  javax.servlet.jsp.JspFactory
 *  javax.servlet.jsp.JspWriter
 *  javax.servlet.jsp.PageContext
 *  javax.servlet.jsp.el.ELException
 *  javax.servlet.jsp.el.ExpressionEvaluator
 *  javax.servlet.jsp.el.VariableResolver
 *  javax.servlet.jsp.tagext.BodyContent
 *  javax.servlet.jsp.tagext.JspTag
 */
package org.apache.jasper.runtime;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.EvaluationListener;
import javax.el.FunctionMapper;
import javax.el.ImportHandler;
import javax.el.VariableMapper;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspApplicationContext;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspFactory;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.el.ELException;
import javax.servlet.jsp.el.ExpressionEvaluator;
import javax.servlet.jsp.el.VariableResolver;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.JspTag;
import org.apache.jasper.compiler.Localizer;
import org.apache.jasper.runtime.JspApplicationContextImpl;
import org.apache.jasper.runtime.JspSourceImports;

public class JspContextWrapper
extends PageContext
implements VariableResolver {
    private final JspTag jspTag;
    private final PageContext invokingJspCtxt;
    private final transient HashMap<String, Object> pageAttributes;
    private final ArrayList<String> nestedVars;
    private final ArrayList<String> atBeginVars;
    private final ArrayList<String> atEndVars;
    private final Map<String, String> aliases;
    private final HashMap<String, Object> originalNestedVars;
    private ServletContext servletContext = null;
    private ELContext elContext = null;
    private final PageContext rootJspCtxt;

    public JspContextWrapper(JspTag jspTag, JspContext jspContext, ArrayList<String> nestedVars, ArrayList<String> atBeginVars, ArrayList<String> atEndVars, Map<String, String> aliases) {
        this.jspTag = jspTag;
        this.invokingJspCtxt = (PageContext)jspContext;
        this.rootJspCtxt = jspContext instanceof JspContextWrapper ? ((JspContextWrapper)jspContext).rootJspCtxt : this.invokingJspCtxt;
        this.nestedVars = nestedVars;
        this.atBeginVars = atBeginVars;
        this.atEndVars = atEndVars;
        this.pageAttributes = new HashMap(16);
        this.aliases = aliases;
        this.originalNestedVars = nestedVars != null ? new HashMap(nestedVars.size()) : null;
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
        return this.rootJspCtxt.getAttribute(name, scope);
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
            this.rootJspCtxt.setAttribute(name, value, scope);
        }
    }

    public Object findAttribute(String name) {
        if (name == null) {
            throw new NullPointerException(Localizer.getMessage("jsp.error.attribute.null_name"));
        }
        Object o = this.pageAttributes.get(name);
        if (o == null && (o = this.rootJspCtxt.getAttribute(name, 2)) == null) {
            if (this.getSession() != null) {
                try {
                    o = this.rootJspCtxt.getAttribute(name, 3);
                }
                catch (IllegalStateException illegalStateException) {
                    // empty catch block
                }
            }
            if (o == null) {
                o = this.rootJspCtxt.getAttribute(name, 4);
            }
        }
        return o;
    }

    public void removeAttribute(String name) {
        if (name == null) {
            throw new NullPointerException(Localizer.getMessage("jsp.error.attribute.null_name"));
        }
        this.pageAttributes.remove(name);
        this.rootJspCtxt.removeAttribute(name, 2);
        if (this.getSession() != null) {
            this.rootJspCtxt.removeAttribute(name, 3);
        }
        this.rootJspCtxt.removeAttribute(name, 4);
    }

    public void removeAttribute(String name, int scope) {
        if (name == null) {
            throw new NullPointerException(Localizer.getMessage("jsp.error.attribute.null_name"));
        }
        if (scope == 1) {
            this.pageAttributes.remove(name);
        } else {
            this.rootJspCtxt.removeAttribute(name, scope);
        }
    }

    public int getAttributesScope(String name) {
        if (name == null) {
            throw new NullPointerException(Localizer.getMessage("jsp.error.attribute.null_name"));
        }
        if (this.pageAttributes.get(name) != null) {
            return 1;
        }
        return this.rootJspCtxt.getAttributesScope(name);
    }

    public Enumeration<String> getAttributeNamesInScope(int scope) {
        if (scope == 1) {
            return Collections.enumeration(this.pageAttributes.keySet());
        }
        return this.rootJspCtxt.getAttributeNamesInScope(scope);
    }

    public void release() {
        this.invokingJspCtxt.release();
    }

    public JspWriter getOut() {
        return this.rootJspCtxt.getOut();
    }

    public HttpSession getSession() {
        return this.rootJspCtxt.getSession();
    }

    public Object getPage() {
        return this.invokingJspCtxt.getPage();
    }

    public ServletRequest getRequest() {
        return this.invokingJspCtxt.getRequest();
    }

    public ServletResponse getResponse() {
        return this.rootJspCtxt.getResponse();
    }

    public Exception getException() {
        return this.invokingJspCtxt.getException();
    }

    public ServletConfig getServletConfig() {
        return this.invokingJspCtxt.getServletConfig();
    }

    public ServletContext getServletContext() {
        if (this.servletContext == null) {
            this.servletContext = this.rootJspCtxt.getServletContext();
        }
        return this.servletContext;
    }

    public void forward(String relativeUrlPath) throws ServletException, IOException {
        this.invokingJspCtxt.forward(relativeUrlPath);
    }

    public void include(String relativeUrlPath) throws ServletException, IOException {
        this.invokingJspCtxt.include(relativeUrlPath);
    }

    public void include(String relativeUrlPath, boolean flush) throws ServletException, IOException {
        this.invokingJspCtxt.include(relativeUrlPath, false);
    }

    @Deprecated
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

    @Deprecated
    public ExpressionEvaluator getExpressionEvaluator() {
        return this.invokingJspCtxt.getExpressionEvaluator();
    }

    public void handlePageException(Exception ex) throws IOException, ServletException {
        this.handlePageException((Throwable)ex);
    }

    public void handlePageException(Throwable t) throws IOException, ServletException {
        this.invokingJspCtxt.handlePageException(t);
    }

    @Deprecated
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
        Iterator<String> iter = null;
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
            String varName = iter.next();
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
        String alias = this.aliases.get(varName);
        if (alias == null) {
            return varName;
        }
        return alias;
    }

    public ELContext getELContext() {
        if (this.elContext == null) {
            this.elContext = new ELContextWrapper(this.rootJspCtxt.getELContext(), this.jspTag, this);
            JspFactory factory = JspFactory.getDefaultFactory();
            JspApplicationContext jspAppCtxt = factory.getJspApplicationContext(this.servletContext);
            if (jspAppCtxt instanceof JspApplicationContextImpl) {
                ((JspApplicationContextImpl)jspAppCtxt).fireListeners(this.elContext);
            }
        }
        return this.elContext;
    }

    static class ELContextWrapper
    extends ELContext {
        private final ELContext wrapped;
        private final JspTag jspTag;
        private final PageContext pageContext;
        private ImportHandler importHandler;

        private ELContextWrapper(ELContext wrapped, JspTag jspTag, PageContext pageContext) {
            this.wrapped = wrapped;
            this.jspTag = jspTag;
            this.pageContext = pageContext;
        }

        ELContext getWrappedELContext() {
            return this.wrapped;
        }

        public void setPropertyResolved(boolean resolved) {
            this.wrapped.setPropertyResolved(resolved);
        }

        public void setPropertyResolved(Object base, Object property) {
            this.wrapped.setPropertyResolved(base, property);
        }

        public boolean isPropertyResolved() {
            return this.wrapped.isPropertyResolved();
        }

        public void putContext(Class key, Object contextObject) {
            if (key != JspContext.class) {
                this.wrapped.putContext(key, contextObject);
            }
        }

        public Object getContext(Class key) {
            if (key == JspContext.class) {
                return this.pageContext;
            }
            return this.wrapped.getContext(key);
        }

        public ImportHandler getImportHandler() {
            if (this.importHandler == null) {
                this.importHandler = new ImportHandler();
                if (this.jspTag instanceof JspSourceImports) {
                    Set<String> classImports;
                    Set<String> packageImports = ((JspSourceImports)this.jspTag).getPackageImports();
                    if (packageImports != null) {
                        for (String packageImport : packageImports) {
                            this.importHandler.importPackage(packageImport);
                        }
                    }
                    if ((classImports = ((JspSourceImports)this.jspTag).getClassImports()) != null) {
                        for (String classImport : classImports) {
                            this.importHandler.importClass(classImport);
                        }
                    }
                }
            }
            return this.importHandler;
        }

        public Locale getLocale() {
            return this.wrapped.getLocale();
        }

        public void setLocale(Locale locale) {
            this.wrapped.setLocale(locale);
        }

        public void addEvaluationListener(EvaluationListener listener) {
            this.wrapped.addEvaluationListener(listener);
        }

        public List<EvaluationListener> getEvaluationListeners() {
            return this.wrapped.getEvaluationListeners();
        }

        public void notifyBeforeEvaluation(String expression) {
            this.wrapped.notifyBeforeEvaluation(expression);
        }

        public void notifyAfterEvaluation(String expression) {
            this.wrapped.notifyAfterEvaluation(expression);
        }

        public void notifyPropertyResolved(Object base, Object property) {
            this.wrapped.notifyPropertyResolved(base, property);
        }

        public boolean isLambdaArgument(String name) {
            return this.wrapped.isLambdaArgument(name);
        }

        public Object getLambdaArgument(String name) {
            return this.wrapped.getLambdaArgument(name);
        }

        public void enterLambdaScope(Map<String, Object> arguments) {
            this.wrapped.enterLambdaScope(arguments);
        }

        public void exitLambdaScope() {
            this.wrapped.exitLambdaScope();
        }

        public Object convertToType(Object obj, Class<?> type) {
            return this.wrapped.convertToType(obj, type);
        }

        public ELResolver getELResolver() {
            return this.wrapped.getELResolver();
        }

        public FunctionMapper getFunctionMapper() {
            return this.wrapped.getFunctionMapper();
        }

        public VariableMapper getVariableMapper() {
            return this.wrapped.getVariableMapper();
        }
    }
}


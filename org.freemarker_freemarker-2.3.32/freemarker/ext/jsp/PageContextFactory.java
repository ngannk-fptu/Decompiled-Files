/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.PageContext
 */
package freemarker.ext.jsp;

import freemarker.core.Environment;
import freemarker.ext.jsp.FreeMarkerPageContext;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.UndeclaredThrowableException;
import javax.servlet.jsp.PageContext;

class PageContextFactory {
    private static final Class pageContextImpl = PageContextFactory.getPageContextImpl();

    PageContextFactory() {
    }

    private static Class getPageContextImpl() {
        try {
            try {
                PageContext.class.getMethod("getELContext", null);
                return Class.forName("freemarker.ext.jsp._FreeMarkerPageContext21");
            }
            catch (NoSuchMethodException e1) {
                try {
                    PageContext.class.getMethod("getExpressionEvaluator", null);
                    return Class.forName("freemarker.ext.jsp._FreeMarkerPageContext2");
                }
                catch (NoSuchMethodException e2) {
                    throw new IllegalStateException("Since FreeMarker 2.3.24, JSP support requires at least JSP 2.0.");
                }
            }
        }
        catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError(e.getMessage());
        }
    }

    static FreeMarkerPageContext getCurrentPageContext() throws TemplateModelException {
        Environment env = Environment.getCurrentEnvironment();
        TemplateModel pageContextModel = env.getGlobalVariable("javax.servlet.jsp.jspPageContext");
        if (pageContextModel instanceof FreeMarkerPageContext) {
            return (FreeMarkerPageContext)pageContextModel;
        }
        try {
            FreeMarkerPageContext pageContext = (FreeMarkerPageContext)pageContextImpl.newInstance();
            env.setGlobalVariable("javax.servlet.jsp.jspPageContext", pageContext);
            return pageContext;
        }
        catch (IllegalAccessException e) {
            throw new IllegalAccessError(e.getMessage());
        }
        catch (InstantiationException e) {
            throw new UndeclaredThrowableException(e);
        }
    }
}


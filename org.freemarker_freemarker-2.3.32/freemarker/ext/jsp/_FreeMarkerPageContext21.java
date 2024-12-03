/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.ELContext
 *  javax.servlet.jsp.JspApplicationContext
 *  javax.servlet.jsp.JspContext
 *  javax.servlet.jsp.JspFactory
 *  javax.servlet.jsp.el.ELException
 *  javax.servlet.jsp.el.ExpressionEvaluator
 *  javax.servlet.jsp.el.VariableResolver
 */
package freemarker.ext.jsp;

import freemarker.ext.jsp.FreeMarkerJspApplicationContext;
import freemarker.ext.jsp.FreeMarkerJspFactory21;
import freemarker.ext.jsp.FreeMarkerPageContext;
import freemarker.log.Logger;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.ClassUtil;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.el.ELContext;
import javax.servlet.jsp.JspApplicationContext;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspFactory;
import javax.servlet.jsp.el.ELException;
import javax.servlet.jsp.el.ExpressionEvaluator;
import javax.servlet.jsp.el.VariableResolver;

public class _FreeMarkerPageContext21
extends FreeMarkerPageContext {
    private static final Logger LOG = Logger.getLogger("freemarker.jsp");
    private ELContext elContext;

    public ExpressionEvaluator getExpressionEvaluator() {
        try {
            Class<?> type = ((ClassLoader)AccessController.doPrivileged(new PrivilegedAction(){

                public Object run() {
                    return Thread.currentThread().getContextClassLoader();
                }
            })).loadClass("org.apache.commons.el.ExpressionEvaluatorImpl");
            return (ExpressionEvaluator)type.newInstance();
        }
        catch (Exception e) {
            throw new UnsupportedOperationException("In order for the getExpressionEvaluator() method to work, you must have downloaded the apache commons-el jar and made it available in the classpath.");
        }
    }

    public VariableResolver getVariableResolver() {
        final _FreeMarkerPageContext21 ctx = this;
        return new VariableResolver(){

            public Object resolveVariable(String name) throws ELException {
                return ctx.findAttribute(name);
            }
        };
    }

    public ELContext getELContext() {
        if (this.elContext == null) {
            JspApplicationContext jspctx = JspFactory.getDefaultFactory().getJspApplicationContext(this.getServletContext());
            if (jspctx instanceof FreeMarkerJspApplicationContext) {
                this.elContext = ((FreeMarkerJspApplicationContext)jspctx).createNewELContext(this);
                this.elContext.putContext(JspContext.class, (Object)this);
            } else {
                throw new UnsupportedOperationException("Can not create an ELContext using a foreign JspApplicationContext (of class " + ClassUtil.getShortClassNameOfObject(jspctx) + ").\nHint: The cause of this is often that you are trying to use JSTL tags/functions in FTL. In that case, know that that's not really suppored, and you are supposed to use FTL constrcuts instead, like #list instead of JSTL's forEach, etc.");
            }
        }
        return this.elContext;
    }

    static {
        if (JspFactory.getDefaultFactory() == null) {
            JspFactory.setDefaultFactory((JspFactory)new FreeMarkerJspFactory21());
        }
        LOG.debug("Using JspFactory implementation class " + JspFactory.getDefaultFactory().getClass().getName());
    }
}


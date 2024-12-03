/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.jsp.JspFactory
 *  javax.servlet.jsp.el.ELException
 *  javax.servlet.jsp.el.ExpressionEvaluator
 *  javax.servlet.jsp.el.VariableResolver
 */
package freemarker.ext.jsp;

import freemarker.ext.jsp.FreeMarkerJspFactory2;
import freemarker.ext.jsp.FreeMarkerPageContext;
import freemarker.log.Logger;
import freemarker.template.TemplateModelException;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.jsp.JspFactory;
import javax.servlet.jsp.el.ELException;
import javax.servlet.jsp.el.ExpressionEvaluator;
import javax.servlet.jsp.el.VariableResolver;

public class _FreeMarkerPageContext2
extends FreeMarkerPageContext {
    private static final Logger LOG = Logger.getLogger("freemarker.jsp");

    public ExpressionEvaluator getExpressionEvaluator() {
        try {
            Class<?> type = Thread.currentThread().getContextClassLoader().loadClass("org.apache.commons.el.ExpressionEvaluatorImpl");
            return (ExpressionEvaluator)type.newInstance();
        }
        catch (Exception e) {
            throw new UnsupportedOperationException("In order for the getExpressionEvaluator() method to work, you must have downloaded the apache commons-el jar and made it available in the classpath.");
        }
    }

    public VariableResolver getVariableResolver() {
        final _FreeMarkerPageContext2 ctx = this;
        return new VariableResolver(){

            public Object resolveVariable(String name) throws ELException {
                return ctx.findAttribute(name);
            }
        };
    }

    @Override
    public void include(String path, boolean flush) throws IOException, ServletException {
        super.include(path);
    }

    static {
        if (JspFactory.getDefaultFactory() == null) {
            JspFactory.setDefaultFactory((JspFactory)new FreeMarkerJspFactory2());
        }
        LOG.debug("Using JspFactory implementation class " + JspFactory.getDefaultFactory().getClass().getName());
    }
}


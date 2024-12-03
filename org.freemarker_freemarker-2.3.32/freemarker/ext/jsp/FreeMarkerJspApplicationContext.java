/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.ArrayELResolver
 *  javax.el.BeanELResolver
 *  javax.el.CompositeELResolver
 *  javax.el.ELContext
 *  javax.el.ELContextEvent
 *  javax.el.ELContextListener
 *  javax.el.ELResolver
 *  javax.el.ExpressionFactory
 *  javax.el.FunctionMapper
 *  javax.el.ListELResolver
 *  javax.el.MapELResolver
 *  javax.el.ResourceBundleELResolver
 *  javax.el.ValueExpression
 *  javax.el.VariableMapper
 *  javax.servlet.jsp.JspApplicationContext
 *  javax.servlet.jsp.el.ImplicitObjectELResolver
 *  javax.servlet.jsp.el.ScopedAttributeELResolver
 */
package freemarker.ext.jsp;

import freemarker.ext.jsp.FreeMarkerPageContext;
import freemarker.log.Logger;
import freemarker.template.utility.ClassUtil;
import java.util.LinkedList;
import javax.el.ArrayELResolver;
import javax.el.BeanELResolver;
import javax.el.CompositeELResolver;
import javax.el.ELContext;
import javax.el.ELContextEvent;
import javax.el.ELContextListener;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.el.FunctionMapper;
import javax.el.ListELResolver;
import javax.el.MapELResolver;
import javax.el.ResourceBundleELResolver;
import javax.el.ValueExpression;
import javax.el.VariableMapper;
import javax.servlet.jsp.JspApplicationContext;
import javax.servlet.jsp.el.ImplicitObjectELResolver;
import javax.servlet.jsp.el.ScopedAttributeELResolver;

class FreeMarkerJspApplicationContext
implements JspApplicationContext {
    private static final Logger LOG = Logger.getLogger("freemarker.jsp");
    private static final ExpressionFactory expressionFactoryImpl = FreeMarkerJspApplicationContext.findExpressionFactoryImplementation();
    private final LinkedList listeners = new LinkedList();
    private final CompositeELResolver elResolver = new CompositeELResolver();
    private final CompositeELResolver additionalResolvers = new CompositeELResolver();

    FreeMarkerJspApplicationContext() {
        this.elResolver.add((ELResolver)new ImplicitObjectELResolver());
        this.elResolver.add((ELResolver)this.additionalResolvers);
        this.elResolver.add((ELResolver)new MapELResolver());
        this.elResolver.add((ELResolver)new ResourceBundleELResolver());
        this.elResolver.add((ELResolver)new ListELResolver());
        this.elResolver.add((ELResolver)new ArrayELResolver());
        this.elResolver.add((ELResolver)new BeanELResolver());
        this.elResolver.add((ELResolver)new ScopedAttributeELResolver());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addELContextListener(ELContextListener listener) {
        LinkedList linkedList = this.listeners;
        synchronized (linkedList) {
            this.listeners.addLast(listener);
        }
    }

    private static ExpressionFactory findExpressionFactoryImplementation() {
        ExpressionFactory ef = FreeMarkerJspApplicationContext.tryExpressionFactoryImplementation("com.sun");
        if (ef == null && (ef = FreeMarkerJspApplicationContext.tryExpressionFactoryImplementation("org.apache")) == null) {
            LOG.warn("Could not find any implementation for " + ExpressionFactory.class.getName());
        }
        return ef;
    }

    private static ExpressionFactory tryExpressionFactoryImplementation(String packagePrefix) {
        String className = packagePrefix + ".el.ExpressionFactoryImpl";
        try {
            Class cl = ClassUtil.forName(className);
            if (ExpressionFactory.class.isAssignableFrom(cl)) {
                LOG.info("Using " + className + " as implementation of " + ExpressionFactory.class.getName());
                return (ExpressionFactory)cl.newInstance();
            }
            LOG.warn("Class " + className + " does not implement " + ExpressionFactory.class.getName());
        }
        catch (ClassNotFoundException cl) {
        }
        catch (Exception e) {
            LOG.error("Failed to instantiate " + className, e);
        }
        return null;
    }

    public void addELResolver(ELResolver resolver) {
        this.additionalResolvers.add(resolver);
    }

    public ExpressionFactory getExpressionFactory() {
        return expressionFactoryImpl;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    ELContext createNewELContext(FreeMarkerPageContext pageCtx) {
        FreeMarkerELContext ctx = new FreeMarkerELContext(pageCtx);
        ELContextEvent event = new ELContextEvent((ELContext)ctx);
        LinkedList linkedList = this.listeners;
        synchronized (linkedList) {
            for (ELContextListener l : this.listeners) {
                l.contextCreated(event);
            }
        }
        return ctx;
    }

    private class FreeMarkerELContext
    extends ELContext {
        private final FreeMarkerPageContext pageCtx;

        FreeMarkerELContext(FreeMarkerPageContext pageCtx) {
            this.pageCtx = pageCtx;
        }

        public ELResolver getELResolver() {
            return FreeMarkerJspApplicationContext.this.elResolver;
        }

        public FunctionMapper getFunctionMapper() {
            return null;
        }

        public VariableMapper getVariableMapper() {
            return new VariableMapper(){

                public ValueExpression resolveVariable(String name) {
                    Object obj = FreeMarkerELContext.this.pageCtx.findAttribute(name);
                    if (obj == null) {
                        return null;
                    }
                    return expressionFactoryImpl.createValueExpression(obj, obj.getClass());
                }

                public ValueExpression setVariable(String name, ValueExpression value) {
                    ValueExpression prev = this.resolveVariable(name);
                    FreeMarkerELContext.this.pageCtx.setAttribute(name, value.getValue((ELContext)FreeMarkerELContext.this));
                    return prev;
                }
            };
        }
    }
}


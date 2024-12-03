/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.ELContext
 *  javax.el.ELContextEvent
 *  javax.el.ELContextListener
 *  javax.el.ELResolver
 *  javax.el.ExpressionFactory
 *  javax.servlet.ServletContext
 *  javax.servlet.jsp.JspApplicationContext
 *  javax.servlet.jsp.JspContext
 */
package org.apache.jasper.runtime;

import java.security.AccessController;
import java.util.ArrayList;
import java.util.List;
import javax.el.ELContext;
import javax.el.ELContextEvent;
import javax.el.ELContextListener;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.servlet.ServletContext;
import javax.servlet.jsp.JspApplicationContext;
import javax.servlet.jsp.JspContext;
import org.apache.jasper.Constants;
import org.apache.jasper.compiler.Localizer;
import org.apache.jasper.el.ELContextImpl;
import org.apache.jasper.el.JasperELResolver;

public class JspApplicationContextImpl
implements JspApplicationContext {
    private static final String KEY = JspApplicationContextImpl.class.getName();
    private final ExpressionFactory expressionFactory = ExpressionFactory.newInstance();
    private final List<ELContextListener> contextListeners = new ArrayList<ELContextListener>();
    private final List<ELResolver> resolvers = new ArrayList<ELResolver>();
    private boolean instantiated = false;
    private ELResolver resolver;

    public void addELContextListener(ELContextListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException(Localizer.getMessage("jsp.error.nullArgument"));
        }
        this.contextListeners.add(listener);
    }

    public static JspApplicationContextImpl getInstance(ServletContext context) {
        if (context == null) {
            throw new IllegalArgumentException(Localizer.getMessage("jsp.error.nullArgument"));
        }
        JspApplicationContextImpl impl = (JspApplicationContextImpl)context.getAttribute(KEY);
        if (impl == null) {
            impl = new JspApplicationContextImpl();
            context.setAttribute(KEY, (Object)impl);
        }
        return impl;
    }

    public ELContextImpl createELContext(JspContext context) {
        if (context == null) {
            throw new IllegalArgumentException(Localizer.getMessage("jsp.error.nullArgument"));
        }
        ELResolver r = this.createELResolver();
        ELContextImpl ctx = Constants.IS_SECURITY_ENABLED ? AccessController.doPrivileged(() -> new ELContextImpl(r)) : new ELContextImpl(r);
        ctx.putContext(JspContext.class, context);
        this.fireListeners(ctx);
        return ctx;
    }

    protected void fireListeners(ELContext elContext) {
        ELContextEvent event = new ELContextEvent(elContext);
        for (ELContextListener contextListener : this.contextListeners) {
            contextListener.contextCreated(event);
        }
    }

    private ELResolver createELResolver() {
        this.instantiated = true;
        if (this.resolver == null) {
            JasperELResolver r = new JasperELResolver(this.resolvers, this.expressionFactory.getStreamELResolver());
            this.resolver = r;
        }
        return this.resolver;
    }

    public void addELResolver(ELResolver resolver) throws IllegalStateException {
        if (resolver == null) {
            throw new IllegalArgumentException(Localizer.getMessage("jsp.error.nullArgument"));
        }
        if (this.instantiated) {
            throw new IllegalStateException(Localizer.getMessage("jsp.error.cannotAddResolver"));
        }
        this.resolvers.add(resolver);
    }

    public ExpressionFactory getExpressionFactory() {
        return this.expressionFactory;
    }
}


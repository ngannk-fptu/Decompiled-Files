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
 *  javax.el.ListELResolver
 *  javax.el.MapELResolver
 *  javax.el.ResourceBundleELResolver
 *  javax.servlet.ServletContext
 *  javax.servlet.jsp.JspApplicationContext
 *  javax.servlet.jsp.JspContext
 *  javax.servlet.jsp.el.ImplicitObjectELResolver
 *  javax.servlet.jsp.el.ScopedAttributeELResolver
 */
package org.apache.sling.scripting.jsp.jasper.runtime;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.el.ArrayELResolver;
import javax.el.BeanELResolver;
import javax.el.CompositeELResolver;
import javax.el.ELContext;
import javax.el.ELContextEvent;
import javax.el.ELContextListener;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.el.ListELResolver;
import javax.el.MapELResolver;
import javax.el.ResourceBundleELResolver;
import javax.servlet.ServletContext;
import javax.servlet.jsp.JspApplicationContext;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.el.ImplicitObjectELResolver;
import javax.servlet.jsp.el.ScopedAttributeELResolver;
import org.apache.sling.scripting.jsp.jasper.el.ELContextImpl;

public class JspApplicationContextImpl
implements JspApplicationContext {
    private static final String KEY = JspApplicationContextImpl.class.getName();
    private static final ExpressionFactory expressionFactory = ExpressionFactory.newInstance();
    private final List<ELContextListener> contextListeners = new ArrayList<ELContextListener>();
    private final List<ELResolver> resolvers = new ArrayList<ELResolver>();
    private boolean instantiated = false;
    private ELResolver resolver;

    public void addELContextListener(ELContextListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("ELConextListener was null");
        }
        this.contextListeners.add(listener);
    }

    public static JspApplicationContextImpl getInstance(ServletContext context) {
        if (context == null) {
            throw new IllegalArgumentException("ServletContext was null");
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
            throw new IllegalArgumentException("JspContext was null");
        }
        ELResolver r = this.createELResolver();
        ELContextImpl ctx = new ELContextImpl(r);
        ctx.putContext(JspContext.class, context);
        ELContextEvent event = new ELContextEvent((ELContext)ctx);
        for (int i = 0; i < this.contextListeners.size(); ++i) {
            this.contextListeners.get(i).contextCreated(event);
        }
        return ctx;
    }

    private ELResolver createELResolver() {
        this.instantiated = true;
        if (this.resolver == null) {
            CompositeELResolver r = new CompositeELResolver();
            r.add((ELResolver)new ImplicitObjectELResolver());
            Iterator<ELResolver> itr = this.resolvers.iterator();
            while (itr.hasNext()) {
                r.add(itr.next());
            }
            r.add((ELResolver)new MapELResolver());
            r.add((ELResolver)new ResourceBundleELResolver());
            r.add((ELResolver)new ListELResolver());
            r.add((ELResolver)new ArrayELResolver());
            r.add((ELResolver)new BeanELResolver());
            r.add((ELResolver)new ScopedAttributeELResolver());
            this.resolver = r;
        }
        return this.resolver;
    }

    public void addELResolver(ELResolver resolver) throws IllegalStateException {
        if (resolver == null) {
            throw new IllegalArgumentException("ELResolver was null");
        }
        if (this.instantiated) {
            throw new IllegalStateException("cannot call addELResolver after the first request has been made");
        }
        this.resolvers.add(resolver);
    }

    public ExpressionFactory getExpressionFactory() {
        return expressionFactory;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.ArrayELResolver
 *  javax.el.BeanELResolver
 *  javax.el.CompositeELResolver
 *  javax.el.ELResolver
 *  javax.el.ExpressionFactory
 *  javax.el.ListELResolver
 *  javax.el.MapELResolver
 *  javax.el.ResourceBundleELResolver
 *  javax.el.StandardELContext
 */
package org.hibernate.validator.internal.engine.messageinterpolation.el;

import javax.el.ArrayELResolver;
import javax.el.BeanELResolver;
import javax.el.CompositeELResolver;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.el.ListELResolver;
import javax.el.MapELResolver;
import javax.el.ResourceBundleELResolver;
import javax.el.StandardELContext;
import org.hibernate.validator.internal.engine.messageinterpolation.el.RootResolver;

public class SimpleELContext
extends StandardELContext {
    private static final ELResolver DEFAULT_RESOLVER = new CompositeELResolver(){
        {
            this.add(new RootResolver());
            this.add((ELResolver)new ArrayELResolver(true));
            this.add((ELResolver)new ListELResolver(true));
            this.add((ELResolver)new MapELResolver(true));
            this.add((ELResolver)new ResourceBundleELResolver());
            this.add((ELResolver)new BeanELResolver(true));
        }
    };

    public SimpleELContext(ExpressionFactory expressionFactory) {
        super(expressionFactory);
        this.putContext(ExpressionFactory.class, expressionFactory);
    }

    public void addELResolver(ELResolver cELResolver) {
        throw new UnsupportedOperationException(((Object)((Object)this)).getClass().getSimpleName() + " does not support addELResolver.");
    }

    public ELResolver getELResolver() {
        return DEFAULT_RESOLVER;
    }
}


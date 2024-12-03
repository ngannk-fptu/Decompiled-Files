/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.ArrayELResolver
 *  javax.el.BeanELResolver
 *  javax.el.CompositeELResolver
 *  javax.el.ELContext
 *  javax.el.ELException
 *  javax.el.ELResolver
 *  javax.el.ListELResolver
 *  javax.el.MapELResolver
 *  javax.el.PropertyNotFoundException
 *  javax.el.PropertyNotWritableException
 *  javax.el.ResourceBundleELResolver
 *  javax.servlet.jsp.el.ELException
 *  javax.servlet.jsp.el.VariableResolver
 */
package org.apache.sling.scripting.jsp.jasper.el;

import java.beans.FeatureDescriptor;
import java.util.Iterator;
import javax.el.ArrayELResolver;
import javax.el.BeanELResolver;
import javax.el.CompositeELResolver;
import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.ListELResolver;
import javax.el.MapELResolver;
import javax.el.PropertyNotFoundException;
import javax.el.PropertyNotWritableException;
import javax.el.ResourceBundleELResolver;
import javax.servlet.jsp.el.ELException;
import javax.servlet.jsp.el.VariableResolver;

public final class ELResolverImpl
extends ELResolver {
    public static final ELResolver DefaultResolver = new CompositeELResolver();
    private final VariableResolver variableResolver;

    public ELResolverImpl(VariableResolver variableResolver) {
        this.variableResolver = variableResolver;
    }

    public Object getValue(ELContext context, Object base, Object property) throws NullPointerException, PropertyNotFoundException, javax.el.ELException {
        if (context == null) {
            throw new NullPointerException();
        }
        if (base == null) {
            context.setPropertyResolved(true);
            if (property != null) {
                try {
                    return this.variableResolver.resolveVariable(property.toString());
                }
                catch (ELException e) {
                    throw new javax.el.ELException(e.getMessage(), e.getCause());
                }
            }
        }
        if (!context.isPropertyResolved()) {
            return DefaultResolver.getValue(context, base, property);
        }
        return null;
    }

    public Class<?> getType(ELContext context, Object base, Object property) throws NullPointerException, PropertyNotFoundException, javax.el.ELException {
        if (context == null) {
            throw new NullPointerException();
        }
        if (base == null) {
            context.setPropertyResolved(true);
            if (property != null) {
                try {
                    Object obj = this.variableResolver.resolveVariable(property.toString());
                    return obj != null ? obj.getClass() : null;
                }
                catch (ELException e) {
                    throw new javax.el.ELException(e.getMessage(), e.getCause());
                }
            }
        }
        if (!context.isPropertyResolved()) {
            return DefaultResolver.getType(context, base, property);
        }
        return null;
    }

    public void setValue(ELContext context, Object base, Object property, Object value) throws NullPointerException, PropertyNotFoundException, PropertyNotWritableException, javax.el.ELException {
        if (context == null) {
            throw new NullPointerException();
        }
        if (base == null) {
            context.setPropertyResolved(true);
            throw new PropertyNotWritableException("Legacy VariableResolver wrapped, not writable");
        }
        if (!context.isPropertyResolved()) {
            DefaultResolver.setValue(context, base, property, value);
        }
    }

    public boolean isReadOnly(ELContext context, Object base, Object property) throws NullPointerException, PropertyNotFoundException, javax.el.ELException {
        if (context == null) {
            throw new NullPointerException();
        }
        if (base == null) {
            context.setPropertyResolved(true);
            return true;
        }
        return DefaultResolver.isReadOnly(context, base, property);
    }

    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        return DefaultResolver.getFeatureDescriptors(context, base);
    }

    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        if (base == null) {
            return String.class;
        }
        return DefaultResolver.getCommonPropertyType(context, base);
    }

    static {
        ((CompositeELResolver)DefaultResolver).add((ELResolver)new MapELResolver());
        ((CompositeELResolver)DefaultResolver).add((ELResolver)new ResourceBundleELResolver());
        ((CompositeELResolver)DefaultResolver).add((ELResolver)new ListELResolver());
        ((CompositeELResolver)DefaultResolver).add((ELResolver)new ArrayELResolver());
        ((CompositeELResolver)DefaultResolver).add((ELResolver)new BeanELResolver());
    }
}


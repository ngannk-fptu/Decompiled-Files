/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.ELContext
 *  javax.el.ELException
 *  javax.el.ELResolver
 *  javax.el.ExpressionFactory
 *  javax.el.PropertyNotWritableException
 *  javax.servlet.jsp.el.ELException
 *  javax.servlet.jsp.el.VariableResolver
 */
package org.apache.jasper.el;

import java.beans.FeatureDescriptor;
import java.util.Iterator;
import java.util.Objects;
import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.el.PropertyNotWritableException;
import javax.servlet.jsp.el.ELException;
import javax.servlet.jsp.el.VariableResolver;
import org.apache.jasper.el.ELContextImpl;

@Deprecated
public final class ELResolverImpl
extends ELResolver {
    private final VariableResolver variableResolver;
    private final ELResolver elResolver;

    public ELResolverImpl(VariableResolver variableResolver, ExpressionFactory factory) {
        this.variableResolver = variableResolver;
        this.elResolver = ELContextImpl.getDefaultResolver(factory);
    }

    public Object getValue(ELContext context, Object base, Object property) {
        Objects.requireNonNull(context);
        if (base == null) {
            context.setPropertyResolved(base, property);
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
            return this.elResolver.getValue(context, base, property);
        }
        return null;
    }

    public Class<?> getType(ELContext context, Object base, Object property) {
        Objects.requireNonNull(context);
        if (base == null) {
            context.setPropertyResolved(base, property);
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
            return this.elResolver.getType(context, base, property);
        }
        return null;
    }

    public void setValue(ELContext context, Object base, Object property, Object value) {
        Objects.requireNonNull(context);
        if (base == null) {
            context.setPropertyResolved(base, property);
            throw new PropertyNotWritableException("Legacy VariableResolver wrapped, not writable");
        }
        if (!context.isPropertyResolved()) {
            this.elResolver.setValue(context, base, property, value);
        }
    }

    public boolean isReadOnly(ELContext context, Object base, Object property) {
        Objects.requireNonNull(context);
        if (base == null) {
            context.setPropertyResolved(base, property);
            return true;
        }
        return this.elResolver.isReadOnly(context, base, property);
    }

    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        return this.elResolver.getFeatureDescriptors(context, base);
    }

    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        if (base == null) {
            return String.class;
        }
        return this.elResolver.getCommonPropertyType(context, base);
    }
}


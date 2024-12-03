/*
 * Decompiled with CFR 0.152.
 */
package javax.el;

import java.beans.FeatureDescriptor;
import java.util.Iterator;
import java.util.Objects;
import javax.el.BeanNameResolver;
import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ELResolver;
import javax.el.PropertyNotWritableException;
import javax.el.Util;

public class BeanNameELResolver
extends ELResolver {
    private final BeanNameResolver beanNameResolver;

    public BeanNameELResolver(BeanNameResolver beanNameResolver) {
        this.beanNameResolver = beanNameResolver;
    }

    @Override
    public Object getValue(ELContext context, Object base, Object property) {
        Objects.requireNonNull(context);
        if (base != null || !(property instanceof String)) {
            return null;
        }
        String beanName = (String)property;
        if (this.beanNameResolver.isNameResolved(beanName)) {
            try {
                Object result = this.beanNameResolver.getBean(beanName);
                context.setPropertyResolved(base, property);
                return result;
            }
            catch (Throwable t) {
                Util.handleThrowable(t);
                throw new ELException(t);
            }
        }
        return null;
    }

    @Override
    public void setValue(ELContext context, Object base, Object property, Object value) {
        boolean isReadOnly;
        Objects.requireNonNull(context);
        if (base != null || !(property instanceof String)) {
            return;
        }
        String beanName = (String)property;
        boolean isResolved = context.isPropertyResolved();
        try {
            isReadOnly = this.isReadOnly(context, base, property);
        }
        catch (Throwable t) {
            Util.handleThrowable(t);
            throw new ELException(t);
        }
        finally {
            context.setPropertyResolved(isResolved);
        }
        if (isReadOnly) {
            throw new PropertyNotWritableException(Util.message(context, "beanNameELResolver.beanReadOnly", beanName));
        }
        if (this.beanNameResolver.isNameResolved(beanName) || this.beanNameResolver.canCreateBean(beanName)) {
            try {
                this.beanNameResolver.setBeanValue(beanName, value);
                context.setPropertyResolved(base, property);
            }
            catch (Throwable t) {
                Util.handleThrowable(t);
                throw new ELException(t);
            }
        }
    }

    @Override
    public Class<?> getType(ELContext context, Object base, Object property) {
        Objects.requireNonNull(context);
        if (base != null || !(property instanceof String)) {
            return null;
        }
        String beanName = (String)property;
        try {
            if (this.beanNameResolver.isNameResolved(beanName)) {
                Class<?> result = this.beanNameResolver.getBean(beanName).getClass();
                context.setPropertyResolved(base, property);
                return result;
            }
        }
        catch (Throwable t) {
            Util.handleThrowable(t);
            throw new ELException(t);
        }
        return null;
    }

    @Override
    public boolean isReadOnly(ELContext context, Object base, Object property) {
        Objects.requireNonNull(context);
        if (base != null || !(property instanceof String)) {
            return false;
        }
        String beanName = (String)property;
        if (this.beanNameResolver.isNameResolved(beanName)) {
            boolean result;
            try {
                result = this.beanNameResolver.isReadOnly(beanName);
            }
            catch (Throwable t) {
                Util.handleThrowable(t);
                throw new ELException(t);
            }
            context.setPropertyResolved(base, property);
            return result;
        }
        return false;
    }

    @Override
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        return null;
    }

    @Override
    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        return String.class;
    }
}


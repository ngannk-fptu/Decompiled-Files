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
 *  javax.el.ResourceBundleELResolver
 *  javax.el.StaticFieldELResolver
 *  javax.servlet.jsp.el.ImplicitObjectELResolver
 *  javax.servlet.jsp.el.ScopedAttributeELResolver
 */
package org.apache.jasper.el;

import java.beans.FeatureDescriptor;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import javax.el.ArrayELResolver;
import javax.el.BeanELResolver;
import javax.el.CompositeELResolver;
import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ELResolver;
import javax.el.ListELResolver;
import javax.el.MapELResolver;
import javax.el.PropertyNotFoundException;
import javax.el.ResourceBundleELResolver;
import javax.el.StaticFieldELResolver;
import javax.servlet.jsp.el.ImplicitObjectELResolver;
import javax.servlet.jsp.el.ScopedAttributeELResolver;
import org.apache.jasper.runtime.ExceptionUtils;
import org.apache.jasper.runtime.JspRuntimeLibrary;

public class JasperELResolver
extends CompositeELResolver {
    private static final int STANDARD_RESOLVERS_COUNT = 9;
    private AtomicInteger resolversSize = new AtomicInteger(0);
    private volatile ELResolver[] resolvers;
    private final int appResolversSize;

    public JasperELResolver(List<ELResolver> appResolvers, ELResolver streamResolver) {
        this.appResolversSize = appResolvers.size();
        this.resolvers = new ELResolver[this.appResolversSize + 9];
        this.add((ELResolver)new ImplicitObjectELResolver());
        for (ELResolver appResolver : appResolvers) {
            this.add(appResolver);
        }
        this.add(streamResolver);
        this.add((ELResolver)new StaticFieldELResolver());
        this.add((ELResolver)new MapELResolver());
        this.add((ELResolver)new ResourceBundleELResolver());
        this.add((ELResolver)new ListELResolver());
        this.add((ELResolver)new ArrayELResolver());
        if (JspRuntimeLibrary.GRAAL) {
            this.add(new GraalBeanELResolver());
        }
        this.add((ELResolver)new BeanELResolver());
        this.add((ELResolver)new ScopedAttributeELResolver());
    }

    public synchronized void add(ELResolver elResolver) {
        super.add(elResolver);
        int size = this.resolversSize.get();
        if (this.resolvers.length > size) {
            this.resolvers[size] = elResolver;
        } else {
            ELResolver[] nr = new ELResolver[size + 1];
            System.arraycopy(this.resolvers, 0, nr, 0, size);
            nr[size] = elResolver;
            this.resolvers = nr;
        }
        this.resolversSize.incrementAndGet();
    }

    public Object getValue(ELContext context, Object base, Object property) throws NullPointerException, PropertyNotFoundException, ELException {
        int start;
        int i;
        context.setPropertyResolved(false);
        Object result = null;
        if (base == null) {
            int index = 1 + this.appResolversSize;
            for (i = 0; i < index; ++i) {
                result = this.resolvers[i].getValue(context, base, property);
                if (!context.isPropertyResolved()) continue;
                return result;
            }
            start = index + 7;
            if (JspRuntimeLibrary.GRAAL) {
                ++start;
            }
        } else {
            start = 1;
        }
        int size = this.resolversSize.get();
        for (i = start; i < size; ++i) {
            result = this.resolvers[i].getValue(context, base, property);
            if (!context.isPropertyResolved()) continue;
            return result;
        }
        return null;
    }

    public Object invoke(ELContext context, Object base, Object method, Class<?>[] paramTypes, Object[] params) {
        String targetMethod = JasperELResolver.coerceToString(method);
        if (targetMethod.length() == 0) {
            throw new ELException((Throwable)new NoSuchMethodException());
        }
        context.setPropertyResolved(false);
        Object result = null;
        int index = 1 + this.appResolversSize + 2;
        for (int i = 1; i < index; ++i) {
            result = this.resolvers[i].invoke(context, base, (Object)targetMethod, (Class[])paramTypes, params);
            if (!context.isPropertyResolved()) continue;
            return result;
        }
        int size = this.resolversSize.get();
        for (int i = index += 4; i < size; ++i) {
            result = this.resolvers[i].invoke(context, base, (Object)targetMethod, (Class[])paramTypes, params);
            if (!context.isPropertyResolved()) continue;
            return result;
        }
        return null;
    }

    private static String coerceToString(Object obj) {
        if (obj == null) {
            return "";
        }
        if (obj instanceof String) {
            return (String)obj;
        }
        if (obj instanceof Enum) {
            return ((Enum)obj).name();
        }
        return obj.toString();
    }

    public static class GraalBeanELResolver
    extends ELResolver {
        public Object getValue(ELContext context, Object base, Object property) {
            Objects.requireNonNull(context);
            if (base == null || property == null) {
                return null;
            }
            Object value = null;
            Method method = GraalBeanELResolver.getReadMethod(base.getClass(), property.toString());
            if (method != null) {
                context.setPropertyResolved(base, property);
                try {
                    method.setAccessible(true);
                    value = method.invoke(base, (Object[])null);
                }
                catch (Exception ex) {
                    Throwable thr = ExceptionUtils.unwrapInvocationTargetException(ex);
                    ExceptionUtils.handleThrowable(thr);
                }
            }
            return value;
        }

        public void setValue(ELContext context, Object base, Object property, Object value) {
            Objects.requireNonNull(context);
            if (base == null || property == null) {
                return;
            }
            Method method = GraalBeanELResolver.getWriteMethod(base.getClass(), property.toString(), value.getClass());
            if (method != null) {
                context.setPropertyResolved(base, property);
                try {
                    method.invoke(base, value);
                }
                catch (Exception ex) {
                    Throwable thr = ExceptionUtils.unwrapInvocationTargetException(ex);
                    ExceptionUtils.handleThrowable(thr);
                }
            }
        }

        public boolean isReadOnly(ELContext context, Object base, Object property) {
            String prop;
            Objects.requireNonNull(context);
            if (base == null || property == null) {
                return false;
            }
            Class<?> beanClass = base.getClass();
            Method readMethod = GraalBeanELResolver.getReadMethod(beanClass, prop = property.toString());
            return readMethod == null || GraalBeanELResolver.getWriteMethod(beanClass, prop, readMethod.getReturnType()) == null;
        }

        private static Method getReadMethod(Class<?> beanClass, String prop) {
            Method[] methods = beanClass.getMethods();
            String isGetter = "is" + GraalBeanELResolver.capitalize(prop);
            String getter = "get" + GraalBeanELResolver.capitalize(prop);
            for (Method method : methods) {
                if (method.getParameterCount() != 0) continue;
                if (isGetter.equals(method.getName()) && method.getReturnType().equals(Boolean.TYPE)) {
                    return method;
                }
                if (!getter.equals(method.getName())) continue;
                return method;
            }
            return null;
        }

        private static Method getWriteMethod(Class<?> beanClass, String prop, Class<?> valueClass) {
            Method[] methods;
            String setter = "set" + GraalBeanELResolver.capitalize(prop);
            for (Method method : methods = beanClass.getMethods()) {
                if (method.getParameterCount() != 1 || !setter.equals(method.getName()) || valueClass != null && !valueClass.isAssignableFrom(method.getParameterTypes()[0])) continue;
                return method;
            }
            return null;
        }

        private static String capitalize(String name) {
            if (name == null || name.length() == 0) {
                return name;
            }
            char[] chars = name.toCharArray();
            chars[0] = Character.toUpperCase(chars[0]);
            return new String(chars);
        }

        public Class<?> getType(ELContext context, Object base, Object property) {
            return null;
        }

        public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
            return null;
        }

        public Class<?> getCommonPropertyType(ELContext context, Object base) {
            if (base != null) {
                return Object.class;
            }
            return null;
        }
    }
}


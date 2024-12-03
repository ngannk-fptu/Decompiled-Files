/*
 * Decompiled with CFR 0.152.
 */
package javax.el;

import java.beans.FeatureDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.Objects;
import javax.el.ELClass;
import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ELResolver;
import javax.el.JreCompat;
import javax.el.MethodNotFoundException;
import javax.el.PropertyNotFoundException;
import javax.el.PropertyNotWritableException;
import javax.el.Util;

public class StaticFieldELResolver
extends ELResolver {
    @Override
    public Object getValue(ELContext context, Object base, Object property) {
        Objects.requireNonNull(context);
        if (base instanceof ELClass && property instanceof String) {
            context.setPropertyResolved(base, property);
            Class<?> clazz = ((ELClass)base).getKlass();
            String name = (String)property;
            Exception exception = null;
            try {
                Field field = clazz.getField(name);
                int modifiers = field.getModifiers();
                JreCompat jreCompat = JreCompat.getInstance();
                if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers) && jreCompat.canAccess(null, field)) {
                    return field.get(null);
                }
            }
            catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
                exception = e;
            }
            String msg = Util.message(context, "staticFieldELResolver.notFound", name, clazz.getName());
            if (exception == null) {
                throw new PropertyNotFoundException(msg);
            }
            throw new PropertyNotFoundException(msg, exception);
        }
        return null;
    }

    @Override
    public void setValue(ELContext context, Object base, Object property, Object value) {
        Objects.requireNonNull(context);
        if (base instanceof ELClass && property instanceof String) {
            Class<?> clazz = ((ELClass)base).getKlass();
            String name = (String)property;
            throw new PropertyNotWritableException(Util.message(context, "staticFieldELResolver.notWritable", name, clazz.getName()));
        }
    }

    @Override
    public Object invoke(ELContext context, Object base, Object method, Class<?>[] paramTypes, Object[] params) {
        Objects.requireNonNull(context);
        if (base instanceof ELClass && method instanceof String) {
            context.setPropertyResolved(base, method);
            Class<?> clazz = ((ELClass)base).getKlass();
            String methodName = (String)method;
            if ("<init>".equals(methodName)) {
                Constructor<?> match = Util.findConstructor(context, clazz, paramTypes, params);
                Object[] parameters = Util.buildParameters(context, match.getParameterTypes(), match.isVarArgs(), params);
                Object result = null;
                try {
                    result = match.newInstance(parameters);
                }
                catch (InvocationTargetException e) {
                    Throwable cause = e.getCause();
                    Util.handleThrowable(cause);
                    throw new ELException(cause);
                }
                catch (ReflectiveOperationException e) {
                    throw new ELException(e);
                }
                return result;
            }
            Method match = Util.findMethod(context, clazz, null, methodName, paramTypes, params);
            if (match == null || !Modifier.isStatic(match.getModifiers())) {
                throw new MethodNotFoundException(Util.message(context, "staticFieldELResolver.methodNotFound", methodName, clazz.getName()));
            }
            Object[] parameters = Util.buildParameters(context, match.getParameterTypes(), match.isVarArgs(), params);
            Object result = null;
            try {
                result = match.invoke(null, parameters);
            }
            catch (IllegalAccessException | IllegalArgumentException e) {
                throw new ELException(e);
            }
            catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                Util.handleThrowable(cause);
                throw new ELException(cause);
            }
            return result;
        }
        return null;
    }

    @Override
    public Class<?> getType(ELContext context, Object base, Object property) {
        Objects.requireNonNull(context);
        if (base instanceof ELClass && property instanceof String) {
            context.setPropertyResolved(base, property);
            Class<?> clazz = ((ELClass)base).getKlass();
            String name = (String)property;
            Exception exception = null;
            try {
                Field field = clazz.getField(name);
                int modifiers = field.getModifiers();
                JreCompat jreCompat = JreCompat.getInstance();
                if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers) && jreCompat.canAccess(null, field)) {
                    return field.getType();
                }
            }
            catch (IllegalArgumentException | NoSuchFieldException | SecurityException e) {
                exception = e;
            }
            String msg = Util.message(context, "staticFieldELResolver.notFound", name, clazz.getName());
            if (exception == null) {
                throw new PropertyNotFoundException(msg);
            }
            throw new PropertyNotFoundException(msg, exception);
        }
        return null;
    }

    @Override
    public boolean isReadOnly(ELContext context, Object base, Object property) {
        Objects.requireNonNull(context);
        if (base instanceof ELClass && property instanceof String) {
            context.setPropertyResolved(base, property);
        }
        return true;
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


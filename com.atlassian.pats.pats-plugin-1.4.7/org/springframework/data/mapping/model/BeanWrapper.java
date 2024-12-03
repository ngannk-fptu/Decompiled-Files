/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.reflect.KCallable
 *  kotlin.reflect.KParameter
 *  kotlin.reflect.KParameter$Kind
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ConcurrentReferenceHashMap
 *  org.springframework.util.ReflectionUtils
 */
package org.springframework.data.mapping.model;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import kotlin.reflect.KCallable;
import kotlin.reflect.KParameter;
import org.springframework.data.mapping.MappingException;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.PersistentPropertyAccessor;
import org.springframework.data.mapping.model.KotlinCopyMethod;
import org.springframework.data.util.KotlinReflectionUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ReflectionUtils;

class BeanWrapper<T>
implements PersistentPropertyAccessor<T> {
    private T bean;

    protected BeanWrapper(T bean) {
        Assert.notNull(bean, (String)"Bean must not be null!");
        this.bean = bean;
    }

    @Override
    public void setProperty(PersistentProperty<?> property, @Nullable Object value) {
        Assert.notNull(property, (String)"PersistentProperty must not be null!");
        try {
            if (property.isImmutable()) {
                Method wither = property.getWither();
                if (wither != null) {
                    ReflectionUtils.makeAccessible((Method)wither);
                    this.bean = ReflectionUtils.invokeMethod((Method)wither, this.bean, (Object[])new Object[]{value});
                    return;
                }
                if (KotlinReflectionUtils.isDataClass(property.getOwner().getType())) {
                    this.bean = KotlinCopyUtil.setProperty(property, this.bean, value);
                    return;
                }
                throw new UnsupportedOperationException(String.format("Cannot set immutable property %s.%s!", property.getOwner().getName(), property.getName()));
            }
            if (!property.usePropertyAccess()) {
                Field field = property.getRequiredField();
                ReflectionUtils.makeAccessible((Field)field);
                ReflectionUtils.setField((Field)field, this.bean, (Object)value);
                return;
            }
            Method setter = property.getRequiredSetter();
            ReflectionUtils.makeAccessible((Method)setter);
            ReflectionUtils.invokeMethod((Method)setter, this.bean, (Object[])new Object[]{value});
        }
        catch (IllegalStateException e) {
            throw new MappingException("Could not set object property!", e);
        }
    }

    @Override
    @Nullable
    public Object getProperty(PersistentProperty<?> property) {
        return this.getProperty(property, property.getType());
    }

    @Nullable
    public <S> Object getProperty(PersistentProperty<?> property, Class<? extends S> type) {
        Assert.notNull(property, (String)"PersistentProperty must not be null!");
        try {
            if (!property.usePropertyAccess()) {
                Field field = property.getRequiredField();
                ReflectionUtils.makeAccessible((Field)field);
                return ReflectionUtils.getField((Field)field, this.bean);
            }
            Method getter = property.getRequiredGetter();
            ReflectionUtils.makeAccessible((Method)getter);
            return ReflectionUtils.invokeMethod((Method)getter, this.bean);
        }
        catch (IllegalStateException e) {
            throw new MappingException(String.format("Could not read property %s of %s!", property.toString(), this.bean.toString()), e);
        }
    }

    @Override
    public T getBean() {
        return this.bean;
    }

    static class KotlinCopyUtil {
        private static final Map<Class<?>, KCallable<?>> copyMethodCache = new ConcurrentReferenceHashMap();

        KotlinCopyUtil() {
        }

        static <T> Object setProperty(PersistentProperty<?> property, T bean, @Nullable Object value) {
            Class<?> type = property.getOwner().getType();
            KCallable copy = copyMethodCache.computeIfAbsent(type, it -> KotlinCopyUtil.getCopyMethod(it, property));
            if (copy == null) {
                throw new UnsupportedOperationException(String.format("Kotlin class %s has no .copy(\u2026) method for property %s!", type.getName(), property.getName()));
            }
            return copy.callBy(KotlinCopyUtil.getCallArgs(copy, property, bean, value));
        }

        private static <T> Map<KParameter, Object> getCallArgs(KCallable<?> callable, PersistentProperty<?> property, T bean, @Nullable Object value) {
            LinkedHashMap<KParameter, Object> args = new LinkedHashMap<KParameter, Object>(2, 1.0f);
            List parameters = callable.getParameters();
            for (KParameter parameter : parameters) {
                if (parameter.getKind() == KParameter.Kind.INSTANCE) {
                    args.put(parameter, bean);
                }
                if (parameter.getKind() != KParameter.Kind.VALUE || parameter.getName() == null || !parameter.getName().equals(property.getName())) continue;
                args.put(parameter, value);
            }
            return args;
        }

        @Nullable
        private static KCallable<?> getCopyMethod(Class<?> type, PersistentProperty<?> property) {
            return KotlinCopyMethod.findCopyMethod(type).filter(it -> it.supportsProperty(property)).map(KotlinCopyMethod::getCopyFunction).orElse(null);
        }
    }
}


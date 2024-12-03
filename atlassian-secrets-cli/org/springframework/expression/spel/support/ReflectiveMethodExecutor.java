/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.expression.spel.support;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.MethodExecutor;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.support.ReflectionHelper;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;

public class ReflectiveMethodExecutor
implements MethodExecutor {
    private final Method method;
    @Nullable
    private final Integer varargsPosition;
    private boolean computedPublicDeclaringClass = false;
    @Nullable
    private Class<?> publicDeclaringClass;
    private boolean argumentConversionOccurred = false;

    public ReflectiveMethodExecutor(Method method) {
        this.method = method;
        if (method.isVarArgs()) {
            Class<?>[] paramTypes = method.getParameterTypes();
            this.varargsPosition = paramTypes.length - 1;
        } else {
            this.varargsPosition = null;
        }
    }

    public Method getMethod() {
        return this.method;
    }

    @Nullable
    public Class<?> getPublicDeclaringClass() {
        if (!this.computedPublicDeclaringClass) {
            this.publicDeclaringClass = this.discoverPublicDeclaringClass(this.method, this.method.getDeclaringClass());
            this.computedPublicDeclaringClass = true;
        }
        return this.publicDeclaringClass;
    }

    @Nullable
    private Class<?> discoverPublicDeclaringClass(Method method, Class<?> clazz) {
        if (Modifier.isPublic(clazz.getModifiers())) {
            try {
                clazz.getDeclaredMethod(method.getName(), method.getParameterTypes());
                return clazz;
            }
            catch (NoSuchMethodException noSuchMethodException) {
                // empty catch block
            }
        }
        if (clazz.getSuperclass() != null) {
            return this.discoverPublicDeclaringClass(method, clazz.getSuperclass());
        }
        return null;
    }

    public boolean didArgumentConversionOccur() {
        return this.argumentConversionOccurred;
    }

    @Override
    public TypedValue execute(EvaluationContext context, Object target, Object ... arguments) throws AccessException {
        try {
            this.argumentConversionOccurred = ReflectionHelper.convertArguments(context.getTypeConverter(), arguments, this.method, this.varargsPosition);
            if (this.method.isVarArgs()) {
                arguments = ReflectionHelper.setupArgumentsForVarargsInvocation(this.method.getParameterTypes(), arguments);
            }
            ReflectionUtils.makeAccessible(this.method);
            Object value = this.method.invoke(target, arguments);
            return new TypedValue(value, new TypeDescriptor(new MethodParameter(this.method, -1)).narrow(value));
        }
        catch (Exception ex) {
            throw new AccessException("Problem invoking method: " + this.method, ex);
        }
    }
}


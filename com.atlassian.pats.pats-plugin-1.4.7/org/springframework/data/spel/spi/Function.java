/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.convert.TypeDescriptor
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.data.spel.spi;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.data.util.ParameterTypes;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class Function {
    private final Method method;
    @Nullable
    private final Object target;

    public Function(Method method) {
        this(method, null);
        Assert.isTrue((boolean)Modifier.isStatic(method.getModifiers()), (String)"Method must be static!");
    }

    public Function(Method method, @Nullable Object target) {
        Assert.notNull((Object)method, (String)"Method must not be null!");
        Assert.isTrue((target != null || Modifier.isStatic(method.getModifiers()) ? 1 : 0) != 0, (String)"Method must either be static or a non-static one with a target object!");
        this.method = method;
        this.target = target;
    }

    public Object invoke(Object[] arguments) throws Exception {
        if (this.method.getParameterCount() == arguments.length) {
            return this.method.invoke(this.target, arguments);
        }
        Class<?>[] types = this.method.getParameterTypes();
        Class<?> tailType = types[types.length - 1];
        if (tailType.isArray()) {
            ArrayList<Object> argumentsToUse = new ArrayList<Object>(types.length);
            for (int i = 0; i < types.length - 1; ++i) {
                argumentsToUse.add(arguments[i]);
            }
            Object[] varargs = (Object[])Array.newInstance(tailType.getComponentType(), arguments.length - types.length + 1);
            int count = 0;
            for (int i = types.length - 1; i < arguments.length; ++i) {
                varargs[count++] = arguments[i];
            }
            argumentsToUse.add(varargs);
            return this.method.invoke(this.target, argumentsToUse.size() == 1 ? argumentsToUse.get(0) : argumentsToUse.toArray());
        }
        throw new IllegalStateException(String.format("Could not invoke method %s for arguments %s!", this.method, arguments));
    }

    public String getName() {
        return this.method.getName();
    }

    public Class<?> getDeclaringClass() {
        return this.method.getDeclaringClass();
    }

    public boolean supports(List<TypeDescriptor> argumentTypes) {
        return ParameterTypes.of(argumentTypes).areValidFor(this.method);
    }

    public int getParameterCount() {
        return this.method.getParameterCount();
    }

    public boolean supportsExact(List<TypeDescriptor> argumentTypes) {
        return ParameterTypes.of(argumentTypes).exactlyMatchParametersOf(this.method);
    }

    public boolean isSignatureEqual(Function other) {
        return this.getName().equals(other.getName()) && this.method.getParameterCount() == other.method.getParameterCount() && Arrays.equals(this.method.getParameterTypes(), other.method.getParameterTypes());
    }
}


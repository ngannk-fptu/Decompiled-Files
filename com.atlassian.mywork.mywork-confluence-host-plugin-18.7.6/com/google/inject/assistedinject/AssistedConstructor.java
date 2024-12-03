/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.assistedinject;

import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.Parameter;
import com.google.inject.assistedinject.ParameterListKey;
import com.google.inject.internal.util.$Lists;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
class AssistedConstructor<T> {
    private final Constructor<T> constructor;
    private final ParameterListKey assistedParameters;
    private final List<Parameter> allParameters;

    public AssistedConstructor(Constructor<T> constructor, List<TypeLiteral<?>> parameterTypes) {
        this.constructor = constructor;
        Annotation[][] annotations = constructor.getParameterAnnotations();
        ArrayList<Type> typeList = $Lists.newArrayList();
        this.allParameters = new ArrayList<Parameter>();
        for (int i = 0; i < parameterTypes.size(); ++i) {
            Parameter parameter = new Parameter(parameterTypes.get(i).getType(), annotations[i]);
            this.allParameters.add(parameter);
            if (!parameter.isProvidedByFactory()) continue;
            typeList.add(parameter.getType());
        }
        this.assistedParameters = new ParameterListKey(typeList);
    }

    public ParameterListKey getAssistedParameters() {
        return this.assistedParameters;
    }

    public List<Parameter> getAllParameters() {
        return this.allParameters;
    }

    public Set<Class<?>> getDeclaredExceptions() {
        return new HashSet(Arrays.asList(this.constructor.getExceptionTypes()));
    }

    public T newInstance(Object[] args) throws Throwable {
        this.constructor.setAccessible(true);
        try {
            return this.constructor.newInstance(args);
        }
        catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    public String toString() {
        return this.constructor.toString();
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.annotations.common.annotationfactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import org.hibernate.annotations.common.annotationfactory.AnnotationDescriptor;

public final class AnnotationProxy
implements Annotation,
InvocationHandler {
    private final Class<? extends Annotation> annotationType;
    private final Map<Method, Object> values;

    public AnnotationProxy(AnnotationDescriptor descriptor) {
        this.annotationType = descriptor.type();
        this.values = AnnotationProxy.getAnnotationValues(this.annotationType, descriptor);
    }

    private static Map<Method, Object> getAnnotationValues(Class<? extends Annotation> annotationType, AnnotationDescriptor descriptor) {
        HashMap<Method, Object> result = new HashMap<Method, Object>();
        int processedValuesFromDescriptor = 0;
        for (Method m : annotationType.getDeclaredMethods()) {
            if (descriptor.containsElement(m.getName())) {
                result.put(m, descriptor.valueOf(m.getName()));
                ++processedValuesFromDescriptor;
                continue;
            }
            if (m.getDefaultValue() != null) {
                result.put(m, m.getDefaultValue());
                continue;
            }
            throw new IllegalArgumentException("No value provided for " + m.getName());
        }
        if (processedValuesFromDescriptor != descriptor.numberOfElements()) {
            throw new RuntimeException("Trying to instanciate " + annotationType + " with unknown elements");
        }
        return AnnotationProxy.toSmallMap(result);
    }

    static <K, V> Map<K, V> toSmallMap(Map<K, V> map) {
        switch (map.size()) {
            case 0: {
                return Collections.emptyMap();
            }
            case 1: {
                Map.Entry<K, V> entry = map.entrySet().iterator().next();
                return Collections.singletonMap(entry.getKey(), entry.getValue());
            }
        }
        return map;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (this.values.containsKey(method)) {
            return this.values.get(method);
        }
        return method.invoke((Object)this, args);
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return this.annotationType;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append('@').append(this.annotationType().getName()).append('(');
        for (Method m : this.getRegisteredMethodsInAlphabeticalOrder()) {
            result.append(m.getName()).append('=').append(this.values.get(m)).append(", ");
        }
        if (this.values.size() > 0) {
            result.delete(result.length() - 2, result.length());
            result.append(")");
        } else {
            result.delete(result.length() - 1, result.length());
        }
        return result.toString();
    }

    private SortedSet<Method> getRegisteredMethodsInAlphabeticalOrder() {
        TreeSet<Method> result = new TreeSet<Method>(new Comparator<Method>(){

            @Override
            public int compare(Method o1, Method o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        result.addAll(this.values.keySet());
        return result;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ReflectionUtils;

final class AttributeMethods {
    static final AttributeMethods NONE = new AttributeMethods(null, new Method[0]);
    static final Map<Class<? extends Annotation>, AttributeMethods> cache = new ConcurrentReferenceHashMap<Class<? extends Annotation>, AttributeMethods>();
    private static final Comparator<Method> methodComparator = (m1, m2) -> {
        if (m1 != null && m2 != null) {
            return m1.getName().compareTo(m2.getName());
        }
        return m1 != null ? -1 : 1;
    };
    @Nullable
    private final Class<? extends Annotation> annotationType;
    private final Method[] attributeMethods;
    private final boolean[] canThrowTypeNotPresentException;
    private final boolean hasDefaultValueMethod;
    private final boolean hasNestedAnnotation;

    private AttributeMethods(@Nullable Class<? extends Annotation> annotationType, Method[] attributeMethods) {
        this.annotationType = annotationType;
        this.attributeMethods = attributeMethods;
        this.canThrowTypeNotPresentException = new boolean[attributeMethods.length];
        boolean foundDefaultValueMethod = false;
        boolean foundNestedAnnotation = false;
        for (int i2 = 0; i2 < attributeMethods.length; ++i2) {
            Method method = this.attributeMethods[i2];
            Class<?> type = method.getReturnType();
            if (!foundDefaultValueMethod && method.getDefaultValue() != null) {
                foundDefaultValueMethod = true;
            }
            if (!foundNestedAnnotation && (type.isAnnotation() || type.isArray() && type.getComponentType().isAnnotation())) {
                foundNestedAnnotation = true;
            }
            ReflectionUtils.makeAccessible(method);
            this.canThrowTypeNotPresentException[i2] = type == Class.class || type == Class[].class || type.isEnum();
        }
        this.hasDefaultValueMethod = foundDefaultValueMethod;
        this.hasNestedAnnotation = foundNestedAnnotation;
    }

    boolean isValid(Annotation annotation) {
        this.assertAnnotation(annotation);
        for (int i2 = 0; i2 < this.size(); ++i2) {
            if (!this.canThrowTypeNotPresentException(i2)) continue;
            try {
                AnnotationUtils.invokeAnnotationMethod(this.get(i2), annotation);
                continue;
            }
            catch (Throwable ex) {
                return false;
            }
        }
        return true;
    }

    void validate(Annotation annotation) {
        this.assertAnnotation(annotation);
        for (int i2 = 0; i2 < this.size(); ++i2) {
            if (!this.canThrowTypeNotPresentException(i2)) continue;
            try {
                AnnotationUtils.invokeAnnotationMethod(this.get(i2), annotation);
                continue;
            }
            catch (Throwable ex) {
                throw new IllegalStateException("Could not obtain annotation attribute value for " + this.get(i2).getName() + " declared on " + annotation.annotationType(), ex);
            }
        }
    }

    private void assertAnnotation(Annotation annotation) {
        Assert.notNull((Object)annotation, "Annotation must not be null");
        if (this.annotationType != null) {
            Assert.isInstanceOf(this.annotationType, annotation);
        }
    }

    @Nullable
    Method get(String name) {
        int index = this.indexOf(name);
        return index != -1 ? this.attributeMethods[index] : null;
    }

    Method get(int index) {
        return this.attributeMethods[index];
    }

    boolean canThrowTypeNotPresentException(int index) {
        return this.canThrowTypeNotPresentException[index];
    }

    int indexOf(String name) {
        for (int i2 = 0; i2 < this.attributeMethods.length; ++i2) {
            if (!this.attributeMethods[i2].getName().equals(name)) continue;
            return i2;
        }
        return -1;
    }

    int indexOf(Method attribute) {
        for (int i2 = 0; i2 < this.attributeMethods.length; ++i2) {
            if (!this.attributeMethods[i2].equals(attribute)) continue;
            return i2;
        }
        return -1;
    }

    int size() {
        return this.attributeMethods.length;
    }

    boolean hasDefaultValueMethod() {
        return this.hasDefaultValueMethod;
    }

    boolean hasNestedAnnotation() {
        return this.hasNestedAnnotation;
    }

    static AttributeMethods forAnnotationType(@Nullable Class<? extends Annotation> annotationType) {
        if (annotationType == null) {
            return NONE;
        }
        return cache.computeIfAbsent(annotationType, AttributeMethods::compute);
    }

    private static AttributeMethods compute(Class<? extends Annotation> annotationType) {
        Method[] methods = annotationType.getDeclaredMethods();
        int size = methods.length;
        for (int i2 = 0; i2 < methods.length; ++i2) {
            if (AttributeMethods.isAttributeMethod(methods[i2])) continue;
            methods[i2] = null;
            --size;
        }
        if (size == 0) {
            return NONE;
        }
        Arrays.sort(methods, methodComparator);
        Method[] attributeMethods = Arrays.copyOf(methods, size);
        return new AttributeMethods(annotationType, attributeMethods);
    }

    private static boolean isAttributeMethod(Method method) {
        return method.getParameterCount() == 0 && method.getReturnType() != Void.TYPE;
    }

    static String describe(@Nullable Method attribute) {
        if (attribute == null) {
            return "(none)";
        }
        return AttributeMethods.describe(attribute.getDeclaringClass(), attribute.getName());
    }

    static String describe(@Nullable Class<?> annotationType, @Nullable String attributeName) {
        if (attributeName == null) {
            return "(none)";
        }
        String in = annotationType != null ? " in annotation [" + annotationType.getName() + "]" : "";
        return "attribute '" + attributeName + "'" + in;
    }
}


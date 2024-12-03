/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.BridgeMethodResolver
 *  org.springframework.core.ParameterNameDiscoverer
 *  org.springframework.core.annotation.AnnotationUtils
 *  org.springframework.util.Assert
 *  org.springframework.util.ReflectionUtils
 */
package org.springframework.security.core.parameters;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

public class AnnotationParameterNameDiscoverer
implements ParameterNameDiscoverer {
    private static final ParameterNameFactory<Constructor<?>> CONSTRUCTOR_METHODPARAM_FACTORY = constructor -> constructor.getParameterAnnotations();
    private static final ParameterNameFactory<Method> METHOD_METHODPARAM_FACTORY = Method::getParameterAnnotations;
    private final Set<String> annotationClassesToUse;

    public AnnotationParameterNameDiscoverer(String ... annotationClassToUse) {
        this(new HashSet<String>(Arrays.asList(annotationClassToUse)));
    }

    public AnnotationParameterNameDiscoverer(Set<String> annotationClassesToUse) {
        Assert.notEmpty(annotationClassesToUse, (String)"annotationClassesToUse cannot be null or empty");
        this.annotationClassesToUse = annotationClassesToUse;
    }

    public String[] getParameterNames(Method method) {
        Class<?>[] interfaces;
        Method originalMethod = BridgeMethodResolver.findBridgedMethod((Method)method);
        String[] paramNames = this.lookupParameterNames(METHOD_METHODPARAM_FACTORY, originalMethod);
        if (paramNames != null) {
            return paramNames;
        }
        Class<?> declaringClass = method.getDeclaringClass();
        for (Class<?> intrfc : interfaces = declaringClass.getInterfaces()) {
            Method intrfcMethod = ReflectionUtils.findMethod(intrfc, (String)method.getName(), (Class[])method.getParameterTypes());
            if (intrfcMethod == null) continue;
            return this.lookupParameterNames(METHOD_METHODPARAM_FACTORY, intrfcMethod);
        }
        return paramNames;
    }

    public String[] getParameterNames(Constructor<?> constructor) {
        return this.lookupParameterNames(CONSTRUCTOR_METHODPARAM_FACTORY, constructor);
    }

    private <T extends AccessibleObject> String[] lookupParameterNames(ParameterNameFactory<T> parameterNameFactory, T t) {
        Annotation[][] parameterAnnotations = parameterNameFactory.findParameterAnnotations(t);
        int parameterCount = parameterAnnotations.length;
        String[] paramNames = new String[parameterCount];
        boolean found = false;
        for (int i = 0; i < parameterCount; ++i) {
            Annotation[] annotations = parameterAnnotations[i];
            String parameterName = this.findParameterName(annotations);
            if (parameterName == null) continue;
            found = true;
            paramNames[i] = parameterName;
        }
        return found ? paramNames : null;
    }

    private String findParameterName(Annotation[] parameterAnnotations) {
        for (Annotation paramAnnotation : parameterAnnotations) {
            if (!this.annotationClassesToUse.contains(paramAnnotation.annotationType().getName())) continue;
            return (String)AnnotationUtils.getValue((Annotation)paramAnnotation, (String)"value");
        }
        return null;
    }

    @FunctionalInterface
    private static interface ParameterNameFactory<T extends AccessibleObject> {
        public Annotation[][] findParameterAnnotations(T var1);
    }
}


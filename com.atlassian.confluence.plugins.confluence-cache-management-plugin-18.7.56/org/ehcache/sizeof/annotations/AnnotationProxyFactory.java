/*
 * Decompiled with CFR 0.152.
 */
package org.ehcache.sizeof.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public final class AnnotationProxyFactory {
    private AnnotationProxyFactory() {
    }

    public static <T extends Annotation> T getAnnotationProxy(Annotation customAnnotation, Class<T> referenceAnnotation) {
        AnnotationInvocationHandler handler = new AnnotationInvocationHandler(customAnnotation);
        return (T)((Annotation)Proxy.newProxyInstance(referenceAnnotation.getClassLoader(), new Class[]{referenceAnnotation}, (InvocationHandler)handler));
    }

    private static class AnnotationInvocationHandler
    implements InvocationHandler {
        private final Annotation customAnnotation;

        public AnnotationInvocationHandler(Annotation customAnnotation) {
            this.customAnnotation = customAnnotation;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Method methodOnCustom = this.getMatchingMethodOnGivenAnnotation(method);
            if (methodOnCustom != null) {
                return methodOnCustom.invoke((Object)this.customAnnotation, args);
            }
            Object defaultValue = method.getDefaultValue();
            if (defaultValue != null) {
                return defaultValue;
            }
            throw new UnsupportedOperationException("The method \"" + method.getName() + "\" does not exist in the custom annotation, and there is no default value for it in the reference annotation, please implement this method in your custom annotation.");
        }

        private Method getMatchingMethodOnGivenAnnotation(Method method) {
            try {
                Method customMethod = this.customAnnotation.getClass().getDeclaredMethod(method.getName(), method.getParameterTypes());
                if (customMethod.getReturnType().isAssignableFrom(method.getReturnType())) {
                    return customMethod;
                }
                return null;
            }
            catch (NoSuchMethodException e) {
                return null;
            }
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.annotations.common.annotationfactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import org.hibernate.annotations.common.annotationfactory.AnnotationDescriptor;
import org.hibernate.annotations.common.annotationfactory.AnnotationProxy;

public final class AnnotationFactory {
    public static <T extends Annotation> T create(AnnotationDescriptor descriptor) {
        return AnnotationFactory.create(descriptor, descriptor.type().getClassLoader());
    }

    public static <T extends Annotation> T createUsingTccl(AnnotationDescriptor descriptor) {
        return AnnotationFactory.create(descriptor, Thread.currentThread().getContextClassLoader());
    }

    public static <T extends Annotation> T create(AnnotationDescriptor descriptor, ClassLoader classLoader) {
        return (T)((Annotation)Proxy.newProxyInstance(classLoader, new Class[]{descriptor.type()}, (InvocationHandler)new AnnotationProxy(descriptor)));
    }
}


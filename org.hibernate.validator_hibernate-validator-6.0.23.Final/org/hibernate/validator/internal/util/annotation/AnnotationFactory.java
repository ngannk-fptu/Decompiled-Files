/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.util.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.hibernate.validator.internal.util.annotation.AnnotationDescriptor;
import org.hibernate.validator.internal.util.annotation.AnnotationProxy;
import org.hibernate.validator.internal.util.privilegedactions.GetClassLoader;
import org.hibernate.validator.internal.util.privilegedactions.NewProxyInstance;

public class AnnotationFactory {
    private AnnotationFactory() {
    }

    public static <T extends Annotation> T create(AnnotationDescriptor<T> descriptor) {
        return (T)((Annotation)AnnotationFactory.run(NewProxyInstance.action(AnnotationFactory.run(GetClassLoader.fromClass(descriptor.getType())), descriptor.getType(), (InvocationHandler)new AnnotationProxy(descriptor))));
    }

    private static <T> T run(PrivilegedAction<T> action) {
        return System.getSecurityManager() != null ? AccessController.doPrivileged(action) : action.run();
    }
}


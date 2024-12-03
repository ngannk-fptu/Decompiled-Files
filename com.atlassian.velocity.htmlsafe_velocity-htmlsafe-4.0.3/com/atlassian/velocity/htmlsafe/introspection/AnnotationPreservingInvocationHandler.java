/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.atlassian.velocity.htmlsafe.introspection;

import com.atlassian.velocity.htmlsafe.introspection.AnnotatedValue;
import com.atlassian.velocity.htmlsafe.introspection.AnnotationBoxedElement;
import com.google.common.base.Preconditions;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Set;

final class AnnotationPreservingInvocationHandler
implements InvocationHandler {
    private final Collection<Annotation> annotations;
    private final Object targetObject;
    private final Set<Method> preservingMethods;

    private AnnotationPreservingInvocationHandler(Collection<Annotation> annotations, Object targetObject, Set<Method> proxiedMethods) {
        Preconditions.checkArgument((!proxiedMethods.isEmpty() ? 1 : 0) != 0, (Object)"proxiedMethods must not be empty");
        this.annotations = annotations;
        this.targetObject = Preconditions.checkNotNull((Object)targetObject, (Object)"targetObject must not be null");
        this.preservingMethods = proxiedMethods;
    }

    public AnnotationPreservingInvocationHandler(AnnotationBoxedElement<?> value, Set<Method> preservingMethods) {
        this(value.getAnnotationCollection(), value.unbox(), preservingMethods);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object returnValue = method.invoke(this.targetObject, args);
        return this.preservingMethods.contains(method) ? new AnnotatedValue<Object>(returnValue, this.annotations) : returnValue;
    }
}


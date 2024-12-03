/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.velocity.htmlsafe.introspection;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

final class InterfaceMethods {
    private final Set<Method> methods;
    private final Class declaringInterface;

    public InterfaceMethods(Method ... methods) {
        if (methods.length == 0) {
            throw new IllegalArgumentException("At least one method must be provided");
        }
        HashSet declaringClasses = new HashSet();
        for (Method method : methods) {
            if (!method.getDeclaringClass().isInterface()) {
                throw new IllegalArgumentException("Provided methods must be from an interface");
            }
            declaringClasses.add(method.getDeclaringClass());
            if (declaringClasses.size() == 1) continue;
            throw new IllegalArgumentException("Provided methods must be from the same interface");
        }
        this.declaringInterface = (Class)declaringClasses.iterator().next();
        this.methods = Collections.unmodifiableSet(new HashSet<Method>(Arrays.asList(methods)));
    }

    public boolean isImplementation(Class clazz) {
        return this.getDeclaringInterface().isAssignableFrom(clazz);
    }

    public Set<Method> getMethods() {
        return this.methods;
    }

    public Class getDeclaringInterface() {
        return this.declaringInterface;
    }
}


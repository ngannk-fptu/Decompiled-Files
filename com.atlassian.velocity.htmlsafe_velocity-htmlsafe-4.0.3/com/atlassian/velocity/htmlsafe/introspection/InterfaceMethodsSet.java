/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 */
package com.atlassian.velocity.htmlsafe.introspection;

import com.atlassian.velocity.htmlsafe.introspection.InterfaceMethods;
import com.google.common.collect.ImmutableSet;
import java.lang.reflect.Method;
import java.util.Set;

final class InterfaceMethodsSet {
    private final Set<InterfaceMethods> interfaceMethodsSet;

    InterfaceMethodsSet() {
        this.interfaceMethodsSet = ImmutableSet.of();
    }

    InterfaceMethodsSet(Set<InterfaceMethods> interfaceMethodsSet) {
        if (interfaceMethodsSet == null) {
            throw new NullPointerException("interfaceMethodsSet");
        }
        this.interfaceMethodsSet = ImmutableSet.copyOf(interfaceMethodsSet);
    }

    public Set<Class<?>> getInterfaces() {
        ImmutableSet.Builder builder = ImmutableSet.builder();
        for (InterfaceMethods interfaceMethods : this.interfaceMethodsSet) {
            builder.add((Object)interfaceMethods.getDeclaringInterface());
        }
        return builder.build();
    }

    public Set<Method> getMethods() {
        ImmutableSet.Builder builder = ImmutableSet.builder();
        for (InterfaceMethods interfaceMethods : this.interfaceMethodsSet) {
            builder.addAll(interfaceMethods.getMethods());
        }
        return builder.build();
    }

    public boolean isEmpty() {
        return this.interfaceMethodsSet.isEmpty();
    }

    public InterfaceMethodsSet getImplementedMethods(Class clazz) {
        ImmutableSet.Builder implementedMethods = ImmutableSet.builder();
        for (InterfaceMethods methods : this.interfaceMethodsSet) {
            if (!methods.isImplementation(clazz)) continue;
            implementedMethods.add((Object)methods);
        }
        return new InterfaceMethodsSet((Set<InterfaceMethods>)implementedMethods.build());
    }
}


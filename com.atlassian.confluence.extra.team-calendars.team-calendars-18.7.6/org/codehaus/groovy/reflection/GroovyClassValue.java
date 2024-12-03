/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.reflection;

public interface GroovyClassValue<T> {
    public T get(Class<?> var1);

    public void remove(Class<?> var1);

    public static interface ComputeValue<T> {
        public T computeValue(Class<?> var1);
    }
}


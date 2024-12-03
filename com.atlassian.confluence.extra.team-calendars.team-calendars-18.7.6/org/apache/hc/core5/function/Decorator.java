/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.function;

@FunctionalInterface
public interface Decorator<T> {
    public T decorate(T var1);
}


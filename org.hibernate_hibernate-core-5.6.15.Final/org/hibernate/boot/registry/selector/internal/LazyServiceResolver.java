/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.registry.selector.internal;

@FunctionalInterface
public interface LazyServiceResolver<T> {
    public Class<? extends T> resolve(String var1);
}


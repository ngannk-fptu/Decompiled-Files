/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.registry.selector.spi;

public interface StrategyCreator<T> {
    public T create(Class<? extends T> var1);
}


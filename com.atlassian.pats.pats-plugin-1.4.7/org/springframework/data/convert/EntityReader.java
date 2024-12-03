/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.convert;

public interface EntityReader<T, S> {
    public <R extends T> R read(Class<R> var1, S var2);
}


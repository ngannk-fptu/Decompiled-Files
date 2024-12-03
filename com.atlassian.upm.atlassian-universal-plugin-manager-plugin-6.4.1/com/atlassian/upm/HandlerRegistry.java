/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm;

public interface HandlerRegistry<T> {
    public Iterable<T> getHandlers();

    public Class<T> getHandlerClass();
}


/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.createcontent.concurrent;

public interface LazyInserter<T> {
    public T read();

    public T insert();
}


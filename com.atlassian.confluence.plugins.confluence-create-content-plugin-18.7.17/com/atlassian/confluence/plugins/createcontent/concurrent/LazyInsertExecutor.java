/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.createcontent.concurrent;

import com.atlassian.confluence.plugins.createcontent.concurrent.LazyInserter;

public interface LazyInsertExecutor {
    public <T> T lazyInsertAndRead(LazyInserter<T> var1, String var2);
}


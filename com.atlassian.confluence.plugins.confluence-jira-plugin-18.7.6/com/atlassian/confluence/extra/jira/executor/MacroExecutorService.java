/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.jira.executor;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public interface MacroExecutorService {
    public <T> Future<T> submit(Callable<T> var1);
}


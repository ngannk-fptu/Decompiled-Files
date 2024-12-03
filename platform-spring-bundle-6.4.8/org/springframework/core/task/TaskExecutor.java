/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.task;

import java.util.concurrent.Executor;

@FunctionalInterface
public interface TaskExecutor
extends Executor {
    @Override
    public void execute(Runnable var1);
}


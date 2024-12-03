/*
 * Decompiled with CFR 0.152.
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.common.util.concurrent.ElementTypesAreNonnullByDefault;
import java.util.concurrent.Executor;

@ElementTypesAreNonnullByDefault
@GwtCompatible
enum DirectExecutor implements Executor
{
    INSTANCE;


    @Override
    public void execute(Runnable command) {
        command.run();
    }

    public String toString() {
        return "MoreExecutors.directExecutor()";
    }
}


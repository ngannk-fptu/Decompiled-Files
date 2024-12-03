/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.scheduledexecutor;

import com.hazelcast.scheduledexecutor.impl.NamedTaskDecorator;
import java.util.concurrent.Callable;

public final class TaskUtils {
    private TaskUtils() {
    }

    public static Runnable named(String name, Runnable runnable) {
        return NamedTaskDecorator.named(name, runnable);
    }

    public static <V> Callable<V> named(String name, Callable<V> callable) {
        return NamedTaskDecorator.named(name, callable);
    }
}


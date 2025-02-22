/*
 * Decompiled with CFR 0.152.
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.J2ktIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.util.concurrent.AsyncCallable;
import com.google.common.util.concurrent.ElementTypesAreNonnullByDefault;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.ParametricNullness;
import java.util.concurrent.Callable;

@ElementTypesAreNonnullByDefault
@GwtCompatible(emulated=true)
public final class Callables {
    private Callables() {
    }

    public static <T> Callable<T> returning(@ParametricNullness T value) {
        return () -> value;
    }

    @J2ktIncompatible
    @GwtIncompatible
    public static <T> AsyncCallable<T> asAsyncCallable(Callable<T> callable, ListeningExecutorService listeningExecutorService) {
        Preconditions.checkNotNull(callable);
        Preconditions.checkNotNull(listeningExecutorService);
        return () -> listeningExecutorService.submit(callable);
    }

    @J2ktIncompatible
    @GwtIncompatible
    static <T> Callable<T> threadRenaming(Callable<T> callable, Supplier<String> nameSupplier) {
        Preconditions.checkNotNull(nameSupplier);
        Preconditions.checkNotNull(callable);
        return () -> {
            Thread currentThread = Thread.currentThread();
            String oldName = currentThread.getName();
            boolean restoreName = Callables.trySetName((String)nameSupplier.get(), currentThread);
            try {
                Object v = callable.call();
                return v;
            }
            finally {
                if (restoreName) {
                    boolean bl = Callables.trySetName(oldName, currentThread);
                }
            }
        };
    }

    @J2ktIncompatible
    @GwtIncompatible
    static Runnable threadRenaming(Runnable task, Supplier<String> nameSupplier) {
        Preconditions.checkNotNull(nameSupplier);
        Preconditions.checkNotNull(task);
        return () -> {
            Thread currentThread = Thread.currentThread();
            String oldName = currentThread.getName();
            boolean restoreName = Callables.trySetName((String)nameSupplier.get(), currentThread);
            try {
                task.run();
            }
            finally {
                if (restoreName) {
                    boolean bl = Callables.trySetName(oldName, currentThread);
                }
            }
        };
    }

    @J2ktIncompatible
    @GwtIncompatible
    private static boolean trySetName(String threadName, Thread currentThread) {
        try {
            currentThread.setName(threadName);
            return true;
        }
        catch (SecurityException e) {
            return false;
        }
    }
}


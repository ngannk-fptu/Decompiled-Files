/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 */
package software.amazon.awssdk.utils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.slf4j.Logger;
import software.amazon.awssdk.annotations.SdkProtectedApi;

@SdkProtectedApi
public final class FunctionalUtils {
    private FunctionalUtils() {
    }

    public static void runAndLogError(Logger log, String errorMsg, UnsafeRunnable runnable) {
        try {
            runnable.run();
        }
        catch (Exception e) {
            log.error(errorMsg, (Throwable)e);
        }
    }

    public static <T> Consumer<T> noOpConsumer() {
        return ignored -> {};
    }

    public static Runnable noOpRunnable() {
        return () -> {};
    }

    public static <I> Consumer<I> safeConsumer(UnsafeConsumer<I> unsafeConsumer) {
        return input -> {
            try {
                unsafeConsumer.accept(input);
            }
            catch (Exception e) {
                throw FunctionalUtils.asRuntimeException(e);
            }
        };
    }

    public static <T, R> Function<T, R> safeFunction(UnsafeFunction<T, R> unsafeFunction) {
        return t -> {
            try {
                return unsafeFunction.apply(t);
            }
            catch (Exception e) {
                throw FunctionalUtils.asRuntimeException(e);
            }
        };
    }

    public static <T> Supplier<T> safeSupplier(UnsafeSupplier<T> unsafeSupplier) {
        return () -> {
            try {
                return unsafeSupplier.get();
            }
            catch (Exception e) {
                throw FunctionalUtils.asRuntimeException(e);
            }
        };
    }

    public static Runnable safeRunnable(UnsafeRunnable unsafeRunnable) {
        return () -> {
            try {
                unsafeRunnable.run();
            }
            catch (Exception e) {
                throw FunctionalUtils.asRuntimeException(e);
            }
        };
    }

    public static <I, O> Function<I, O> toFunction(Supplier<O> supplier) {
        return ignore -> supplier.get();
    }

    public static <T> T invokeSafely(UnsafeSupplier<T> unsafeSupplier) {
        return FunctionalUtils.safeSupplier(unsafeSupplier).get();
    }

    public static void invokeSafely(UnsafeRunnable unsafeRunnable) {
        FunctionalUtils.safeRunnable(unsafeRunnable).run();
    }

    private static RuntimeException asRuntimeException(Exception exception) {
        if (exception instanceof RuntimeException) {
            return (RuntimeException)exception;
        }
        if (exception instanceof IOException) {
            return new UncheckedIOException((IOException)exception);
        }
        if (exception instanceof InterruptedException) {
            Thread.currentThread().interrupt();
        }
        return new RuntimeException(exception);
    }

    @FunctionalInterface
    public static interface UnsafeRunnable {
        public void run() throws Exception;
    }

    @FunctionalInterface
    public static interface UnsafeSupplier<T> {
        public T get() throws Exception;
    }

    @FunctionalInterface
    public static interface UnsafeFunction<T, R> {
        public R apply(T var1) throws Exception;
    }

    @FunctionalInterface
    public static interface UnsafeConsumer<I> {
        public void accept(I var1) throws Exception;
    }
}


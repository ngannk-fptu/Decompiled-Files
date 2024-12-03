/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 */
package software.amazon.awssdk.utils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.function.Function;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.utils.Logger;

@SdkProtectedApi
public final class CompletableFutureUtils {
    private static final Logger log = Logger.loggerFor(CompletableFutureUtils.class);

    private CompletableFutureUtils() {
    }

    public static <U> CompletableFuture<U> failedFuture(Throwable t) {
        CompletableFuture cf = new CompletableFuture();
        cf.completeExceptionally(t);
        return cf;
    }

    public static CompletionException errorAsCompletionException(Throwable t) {
        if (t instanceof CompletionException) {
            return (CompletionException)t;
        }
        return new CompletionException(t);
    }

    public static <T> CompletableFuture<T> forwardExceptionTo(CompletableFuture<T> src, CompletableFuture<?> dst) {
        src.whenComplete((r, e) -> {
            if (e != null) {
                dst.completeExceptionally((Throwable)e);
            }
        });
        return src;
    }

    public static <T> CompletableFuture<T> forwardTransformedExceptionTo(CompletableFuture<T> src, CompletableFuture<?> dst, Function<Throwable, Throwable> transformationFunction) {
        src.whenComplete((r, e) -> {
            if (e != null) {
                dst.completeExceptionally((Throwable)transformationFunction.apply((Throwable)e));
            }
        });
        return src;
    }

    public static <T> CompletableFuture<T> forwardResultTo(CompletableFuture<T> src, CompletableFuture<T> dst) {
        src.whenComplete((r, e) -> {
            if (e != null) {
                dst.completeExceptionally((Throwable)e);
            } else {
                dst.complete(r);
            }
        });
        return src;
    }

    public static <T> CompletableFuture<T> forwardResultTo(CompletableFuture<T> src, CompletableFuture<T> dst, Executor executor) {
        src.whenCompleteAsync((r, e) -> {
            if (e != null) {
                dst.completeExceptionally((Throwable)e);
            } else {
                dst.complete(r);
            }
        }, executor);
        return src;
    }

    public static <SourceT, DestT> CompletableFuture<SourceT> forwardTransformedResultTo(CompletableFuture<SourceT> src, CompletableFuture<DestT> dst, Function<SourceT, DestT> function) {
        src.whenComplete((r, e) -> {
            if (e != null) {
                dst.completeExceptionally((Throwable)e);
            } else {
                dst.complete(function.apply(r));
            }
        });
        return src;
    }

    public static CompletableFuture<Void> allOfExceptionForwarded(CompletableFuture<?>[] futures) {
        CompletableFuture<Void> anyFail = CompletableFutureUtils.anyFail(futures);
        anyFail.whenComplete((r, t) -> {
            if (t != null) {
                for (CompletableFuture cf : futures) {
                    cf.completeExceptionally((Throwable)t);
                }
            }
        });
        return CompletableFuture.allOf(futures);
    }

    static CompletableFuture<Void> anyFail(CompletableFuture<?>[] futures) {
        CompletableFuture<Void> completableFuture = new CompletableFuture<Void>();
        for (CompletableFuture<?> future : futures) {
            future.whenComplete((r, t) -> {
                if (t != null) {
                    completableFuture.completeExceptionally((Throwable)t);
                }
            });
        }
        return completableFuture;
    }

    public static <T> T joinInterruptibly(CompletableFuture<T> future) {
        try {
            return future.get();
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CompletionException("Interrupted while waiting on a future.", e);
        }
        catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof Error) {
                throw (Error)cause;
            }
            throw new CompletionException(cause);
        }
    }

    public static void joinInterruptiblyIgnoringFailures(CompletableFuture<?> future) {
        try {
            future.get();
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        catch (ExecutionException executionException) {
            // empty catch block
        }
    }

    public static <T> T joinLikeSync(CompletableFuture<T> future) {
        try {
            return CompletableFutureUtils.joinInterruptibly(future);
        }
        catch (CompletionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                cause.addSuppressed(new RuntimeException("Task failed."));
                throw (RuntimeException)cause;
            }
            throw e;
        }
    }
}


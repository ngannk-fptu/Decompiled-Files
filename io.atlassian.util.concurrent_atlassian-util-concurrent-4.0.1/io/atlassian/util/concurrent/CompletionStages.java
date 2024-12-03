/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package io.atlassian.util.concurrent;

import io.atlassian.util.concurrent.Timeout;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public final class CompletionStages {
    private CompletionStages() {
    }

    public static <T> CompletionStage<T> fail(Throwable throwable) {
        CompletableFuture future = new CompletableFuture();
        future.completeExceptionally(throwable);
        return future;
    }

    public static <T> T unsafeBlockAndGet(CompletionStage<T> completionStage, Function<Throwable, ? extends T> onError) {
        try {
            return completionStage.toCompletableFuture().get();
        }
        catch (Throwable throwable) {
            return onError.apply(throwable);
        }
    }

    public static <T> T unsafeBlockAndGet(CompletionStage<T> completionStage, Timeout timeout, Function<Throwable, ? extends T> onError) {
        try {
            return completionStage.toCompletableFuture().get(timeout.getTimeoutPeriod(), timeout.getUnit());
        }
        catch (Throwable throwable) {
            return onError.apply(throwable);
        }
    }
}


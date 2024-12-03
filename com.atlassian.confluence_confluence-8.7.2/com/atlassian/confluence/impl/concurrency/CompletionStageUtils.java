/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.concurrency;

import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;
import java.util.function.Function;

public class CompletionStageUtils {
    public static <T> T foldResult(CompletionStage<T> completionStage, Function<Throwable, T> onError) {
        return (T)CompletionStageUtils.joinResult(completionStage.handle((result, ex) -> result != null ? result : onError.apply((Throwable)ex)));
    }

    public static <T, R> R foldResult(CompletionStage<T> completionStage, Function<T, R> onSuccess, Function<Throwable, R> onError) {
        return (R)CompletionStageUtils.joinResult(completionStage.handle((result, ex) -> result != null ? onSuccess.apply(result) : onError.apply((Throwable)ex)));
    }

    public static void joinResult(CompletionStage<?> completionStage, Consumer<Throwable> onError) {
        CompletionStageUtils.joinResult(completionStage.handle((result, ex) -> {
            if (ex != null) {
                onError.accept((Throwable)ex);
            }
            return result;
        }));
    }

    private static <T> T joinResult(CompletionStage<T> completionStage) {
        return completionStage.toCompletableFuture().join();
    }
}


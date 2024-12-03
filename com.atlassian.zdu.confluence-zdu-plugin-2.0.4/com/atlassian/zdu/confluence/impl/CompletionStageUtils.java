/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.zdu.confluence.impl;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

public class CompletionStageUtils {
    public static <T, R> R foldResult(CompletionStage<T> completionStage, Function<T, R> onSuccess, Function<Throwable, R> onError, long timeOut, TimeUnit unit) {
        try {
            return (R)CompletionStageUtils.getResult(completionStage.handle((result, ex) -> result != null ? onSuccess.apply(result) : onError.apply((Throwable)ex)), timeOut, unit);
        }
        catch (InterruptedException | ExecutionException | TimeoutException e) {
            return onError.apply(e);
        }
    }

    private static <T> T getResult(CompletionStage<T> completionStage, long timeOut, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return completionStage.toCompletableFuture().get(timeOut, unit);
    }
}


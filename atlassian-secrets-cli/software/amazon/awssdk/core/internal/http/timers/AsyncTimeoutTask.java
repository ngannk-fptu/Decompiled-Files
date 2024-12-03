/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.http.timers;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.internal.http.timers.TimeoutTask;
import software.amazon.awssdk.utils.Logger;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public final class AsyncTimeoutTask
implements TimeoutTask {
    private static final Logger log = Logger.loggerFor(AsyncTimeoutTask.class);
    private final Supplier<SdkClientException> exception;
    private volatile boolean hasExecuted;
    private final CompletableFuture<?> completableFuture;

    public AsyncTimeoutTask(CompletableFuture<?> completableFuture, Supplier<SdkClientException> exceptionSupplier) {
        this.completableFuture = Validate.paramNotNull(completableFuture, "completableFuture");
        this.exception = Validate.paramNotNull(exceptionSupplier, "exceptionSupplier");
    }

    @Override
    public void run() {
        this.hasExecuted = true;
        if (!this.completableFuture.isDone()) {
            this.completableFuture.completeExceptionally(this.exception.get());
        }
    }

    @Override
    public boolean hasExecuted() {
        return this.hasExecuted;
    }
}


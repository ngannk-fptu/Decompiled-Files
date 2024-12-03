/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.waiters;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.internal.waiters.WaiterConfiguration;
import software.amazon.awssdk.core.internal.waiters.WaiterExecutorHelper;
import software.amazon.awssdk.core.waiters.WaiterAcceptor;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.core.waiters.WaiterState;
import software.amazon.awssdk.utils.Either;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
@ThreadSafe
public final class AsyncWaiterExecutor<T> {
    private final ScheduledExecutorService executorService;
    private final WaiterExecutorHelper<T> executorHelper;

    public AsyncWaiterExecutor(WaiterConfiguration configuration, List<WaiterAcceptor<? super T>> waiterAcceptors, ScheduledExecutorService executorService) {
        Validate.paramNotNull(waiterAcceptors, "waiterAcceptors");
        this.executorService = Validate.paramNotNull(executorService, "executorService");
        this.executorHelper = new WaiterExecutorHelper<T>(waiterAcceptors, configuration);
    }

    CompletableFuture<WaiterResponse<T>> execute(Supplier<CompletableFuture<T>> asyncPollingFunction) {
        CompletableFuture<WaiterResponse<T>> future = new CompletableFuture<WaiterResponse<T>>();
        this.doExecute(asyncPollingFunction, future, 0, System.currentTimeMillis());
        return future;
    }

    private void doExecute(Supplier<CompletableFuture<T>> asyncPollingFunction, CompletableFuture<WaiterResponse<T>> future, int attemptNumber, long startTime) {
        this.runAsyncPollingFunction(asyncPollingFunction, future, ++attemptNumber, startTime);
    }

    private void runAsyncPollingFunction(Supplier<CompletableFuture<T>> asyncPollingFunction, CompletableFuture<WaiterResponse<T>> future, int attemptNumber, long startTime) {
        asyncPollingFunction.get().whenComplete((response, exception) -> {
            try {
                Either<Object, Object> responseOrException = exception == null ? Either.left(response) : (exception instanceof CompletionException ? Either.right(exception.getCause()) : Either.right(exception));
                Optional<WaiterAcceptor<Object>> optionalWaiterAcceptor = this.executorHelper.firstWaiterAcceptorIfMatched(responseOrException);
                if (optionalWaiterAcceptor.isPresent()) {
                    WaiterAcceptor<Object> acceptor = optionalWaiterAcceptor.get();
                    WaiterState state = acceptor.waiterState();
                    switch (state) {
                        case SUCCESS: {
                            future.complete(this.executorHelper.createWaiterResponse(responseOrException, attemptNumber));
                            break;
                        }
                        case RETRY: {
                            this.maybeRetry(asyncPollingFunction, future, attemptNumber, startTime);
                            break;
                        }
                        case FAILURE: {
                            future.completeExceptionally(this.executorHelper.waiterFailureException(acceptor));
                            break;
                        }
                        default: {
                            future.completeExceptionally(new UnsupportedOperationException());
                            break;
                        }
                    }
                } else {
                    Optional<Object> t = responseOrException.right();
                    if (t.isPresent() && t.get() instanceof Error) {
                        future.completeExceptionally((Throwable)t.get());
                    } else {
                        future.completeExceptionally(this.executorHelper.noneMatchException(responseOrException));
                    }
                }
            }
            catch (Throwable t) {
                Throwable cause;
                Throwable throwable = cause = t instanceof CompletionException ? t.getCause() : t;
                if (cause instanceof Error) {
                    future.completeExceptionally(cause);
                }
                future.completeExceptionally(SdkClientException.create("Encountered unexpected exception.", cause));
            }
        });
    }

    private void maybeRetry(Supplier<CompletableFuture<T>> asyncPollingFunction, CompletableFuture<WaiterResponse<T>> future, int attemptNumber, long startTime) {
        Either<Long, SdkClientException> nextDelayOrUnretryableException = this.executorHelper.nextDelayOrUnretryableException(attemptNumber, startTime);
        nextDelayOrUnretryableException.apply(nextDelay -> this.executorService.schedule(() -> this.lambda$null$1((Supplier)asyncPollingFunction, future, attemptNumber, startTime), (long)nextDelay, TimeUnit.MILLISECONDS), future::completeExceptionally);
    }

    private /* synthetic */ void lambda$null$1(Supplier asyncPollingFunction, CompletableFuture future, int attemptNumber, long startTime) {
        this.doExecute(asyncPollingFunction, future, attemptNumber, startTime);
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.waiters;

import java.util.List;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.internal.waiters.WaiterConfiguration;
import software.amazon.awssdk.core.internal.waiters.WaiterExecutorHelper;
import software.amazon.awssdk.core.waiters.WaiterAcceptor;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.utils.Either;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
@ThreadSafe
public final class WaiterExecutor<T> {
    private final WaiterExecutorHelper<T> executorHelper;

    public WaiterExecutor(WaiterConfiguration configuration, List<WaiterAcceptor<? super T>> waiterAcceptors) {
        Validate.paramNotNull(configuration, "configuration");
        Validate.paramNotNull(waiterAcceptors, "waiterAcceptors");
        this.executorHelper = new WaiterExecutorHelper<T>(waiterAcceptors, configuration);
    }

    WaiterResponse<T> execute(Supplier<T> pollingFunction) {
        int attemptNumber = 0;
        long startTime = System.currentTimeMillis();
        block5: while (true) {
            ++attemptNumber;
            Either<T, Throwable> polledResponse = this.pollResponse(pollingFunction);
            WaiterAcceptor<T> waiterAcceptor = this.firstWaiterAcceptor(polledResponse);
            switch (waiterAcceptor.waiterState()) {
                case SUCCESS: {
                    return this.executorHelper.createWaiterResponse(polledResponse, attemptNumber);
                }
                case RETRY: {
                    this.waitToRetry(attemptNumber, startTime);
                    continue block5;
                }
                case FAILURE: {
                    throw this.executorHelper.waiterFailureException(waiterAcceptor);
                }
            }
            break;
        }
        throw new UnsupportedOperationException();
    }

    private Either<T, Throwable> pollResponse(Supplier<T> pollingFunction) {
        try {
            return Either.left(pollingFunction.get());
        }
        catch (Exception exception) {
            return Either.right(exception);
        }
    }

    private WaiterAcceptor<? super T> firstWaiterAcceptor(Either<T, Throwable> responseOrException) {
        return this.executorHelper.firstWaiterAcceptorIfMatched(responseOrException).orElseThrow(() -> this.executorHelper.noneMatchException(responseOrException));
    }

    private void waitToRetry(int attemptNumber, long startTime) {
        Either<Long, SdkClientException> nextDelayOrUnretryableException = this.executorHelper.nextDelayOrUnretryableException(attemptNumber, startTime);
        if (nextDelayOrUnretryableException.right().isPresent()) {
            throw nextDelayOrUnretryableException.right().get();
        }
        try {
            Thread.sleep(nextDelayOrUnretryableException.left().get());
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw SdkClientException.create("The thread got interrupted", e);
        }
    }
}


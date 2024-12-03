/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.waiters;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.internal.waiters.DefaultWaiterResponse;
import software.amazon.awssdk.core.internal.waiters.WaiterConfiguration;
import software.amazon.awssdk.core.retry.RetryPolicyContext;
import software.amazon.awssdk.core.retry.backoff.BackoffStrategy;
import software.amazon.awssdk.core.waiters.WaiterAcceptor;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.utils.Either;

@SdkInternalApi
public final class WaiterExecutorHelper<T> {
    private final List<WaiterAcceptor<? super T>> waiterAcceptors;
    private final BackoffStrategy backoffStrategy;
    private final Duration waitTimeout;
    private final int maxAttempts;

    public WaiterExecutorHelper(List<WaiterAcceptor<? super T>> waiterAcceptors, WaiterConfiguration configuration) {
        this.waiterAcceptors = waiterAcceptors;
        this.backoffStrategy = configuration.backoffStrategy();
        this.waitTimeout = configuration.waitTimeout();
        this.maxAttempts = configuration.maxAttempts();
    }

    public WaiterResponse<T> createWaiterResponse(Either<T, Throwable> responseOrException, int attempts) {
        return responseOrException.map(r -> DefaultWaiterResponse.builder().response(r).attemptsExecuted(attempts).build(), e -> DefaultWaiterResponse.builder().exception((Throwable)e).attemptsExecuted(attempts).build());
    }

    public Optional<WaiterAcceptor<? super T>> firstWaiterAcceptorIfMatched(Either<T, Throwable> responseOrException) {
        return responseOrException.map(this::responseMatches, this::exceptionMatches);
    }

    public long computeNextDelayInMills(int attemptNumber) {
        return this.backoffStrategy.computeDelayBeforeNextRetry(RetryPolicyContext.builder().retriesAttempted(attemptNumber).build()).toMillis();
    }

    public boolean exceedsMaxWaitTime(long startTime, long nextDelayInMills) {
        if (this.waitTimeout == null) {
            return false;
        }
        long elapsedTime = System.currentTimeMillis() - startTime;
        return elapsedTime + nextDelayInMills > this.waitTimeout.toMillis();
    }

    public Either<Long, SdkClientException> nextDelayOrUnretryableException(int attemptNumber, long startTime) {
        if (attemptNumber >= this.maxAttempts) {
            return Either.right(SdkClientException.create("The waiter has exceeded the max retry attempts: " + this.maxAttempts));
        }
        long nextDelay = this.computeNextDelayInMills(attemptNumber);
        if (this.exceedsMaxWaitTime(startTime, nextDelay)) {
            return Either.right(SdkClientException.create("The waiter has exceeded the max wait time or the next retry will exceed the max wait time + " + this.waitTimeout));
        }
        return Either.left(nextDelay);
    }

    public SdkClientException noneMatchException(Either<T, Throwable> responseOrException) {
        return responseOrException.map(r -> SdkClientException.create("No acceptor was matched for the response: " + r), t -> SdkClientException.create("An exception was thrown and did not match any waiter acceptors", t));
    }

    public SdkClientException waiterFailureException(WaiterAcceptor<? super T> acceptor) {
        return SdkClientException.create(acceptor.message().orElse("A waiter acceptor was matched and transitioned the waiter to failure state"));
    }

    private Optional<WaiterAcceptor<? super T>> responseMatches(T response) {
        return this.waiterAcceptors.stream().filter(acceptor -> acceptor.matches(response)).findFirst();
    }

    private Optional<WaiterAcceptor<? super T>> exceptionMatches(Throwable exception) {
        return this.waiterAcceptors.stream().filter(acceptor -> acceptor.matches(exception)).findFirst();
    }
}


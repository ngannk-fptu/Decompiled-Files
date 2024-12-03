/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jodah.failsafe.RetryPolicy
 *  org.slf4j.Logger
 *  org.springframework.http.HttpStatus
 *  org.springframework.http.HttpStatus$Series
 */
package com.atlassian.migration.agent.okhttp;

import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.okhttp.ErrorCode;
import com.atlassian.migration.agent.okhttp.HttpServiceException;
import com.atlassian.migration.agent.okhttp.IOHttpException;
import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.jodah.failsafe.RetryPolicy;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;

public class RetryPolicyBuilder<T> {
    private static final Logger log = ContextLoggerFactory.getLogger(RetryPolicyBuilder.class);
    public static final String MEDIA_ERROR = "Declared content hash does not match actual content hash";
    private RetryPolicy<T> policy;
    private Collection<Predicate<Throwable>> predicates = new ArrayList<Predicate<Throwable>>();

    private RetryPolicyBuilder(RetryPolicy<T> policy) {
        this.policy = policy;
    }

    public static <T> RetryPolicy<T> policyForMigrationServices() {
        RetryPolicyBuilder<T> builder = RetryPolicyBuilder.defaultPolicy();
        return builder.withErrorCode(ErrorCode.GENERIC, ErrorCode.UNHANDLED_COMMUNICATION_ERROR_WITH_DOWNSTREAM_SERVER, ErrorCode.FAILED_TO_START_MIGRATION).handle(Collections.singletonList(IOHttpException.class)).build();
    }

    public static <T> RetryPolicy<T> policyForUserMigrationService() {
        return new RetryPolicyBuilder<T>(new RetryPolicy()).withMaxRetries(5).withBackoff(10L, 90L, ChronoUnit.SECONDS).withErrorCode(ErrorCode.GENERIC, ErrorCode.UNHANDLED_COMMUNICATION_ERROR_WITH_DOWNSTREAM_SERVER, ErrorCode.FAILED_TO_START_MIGRATION).withStatusCode(HttpStatus.Series.SERVER_ERROR).withStatusCode(429).handle(Arrays.asList(IOHttpException.class, IOException.class)).build();
    }

    public static <T> RetryPolicy<T> policyForMigrationOrchestratorService() {
        RetryPolicyBuilder<T> builder = RetryPolicyBuilder.defaultPolicy();
        return builder.withErrorCode(ErrorCode.GENERIC, ErrorCode.UNHANDLED_COMMUNICATION_ERROR_WITH_DOWNSTREAM_SERVER).handle(Collections.singletonList(IOHttpException.class)).build();
    }

    public static <T> RetryPolicy<T> policyForInitiateUsersAndGroups() {
        return new RetryPolicyBuilder<T>(new RetryPolicy().withMaxRetries(5).withBackoff(10L, 90L, ChronoUnit.SECONDS)).withErrorCode(ErrorCode.GENERIC, ErrorCode.UNHANDLED_COMMUNICATION_ERROR_WITH_DOWNSTREAM_SERVER, ErrorCode.FAILED_TO_START_MIGRATION).withStatusCode(429).handle(Arrays.asList(IOHttpException.class, IOException.class)).build();
    }

    public static <T> RetryPolicyBuilder<T> defaultPolicy() {
        return new RetryPolicyBuilder<T>(new RetryPolicy()).withMaxRetries(5).withBackoff(1L, 20L, ChronoUnit.SECONDS).withStatusCode(HttpStatus.Series.SERVER_ERROR).handle(Arrays.asList(IOHttpException.class, IOException.class));
    }

    public static <T> RetryPolicyBuilder<T> enterpriseGatekeeperClientRetryPolicy() {
        return new RetryPolicyBuilder<T>(new RetryPolicy()).withMaxRetries(5).withBackoff(10L, 120L, ChronoUnit.SECONDS).handle(Arrays.asList(IOHttpException.class, IOException.class)).withStatusCode(HttpStatus.Series.SERVER_ERROR).withStatusCode(HttpStatus.BAD_REQUEST.value()).withStatusCode(HttpStatus.TOO_MANY_REQUESTS.value());
    }

    public static <T> RetryPolicyBuilder<T> amsClientPolicy() {
        return new RetryPolicyBuilder<T>(new RetryPolicy()).withMaxRetries(5).withBackoff(1L, 30L, ChronoUnit.SECONDS).handle(Arrays.asList(IOHttpException.class, IOException.class)).withStatusCode(HttpStatus.TOO_MANY_REQUESTS.value()).withStatusCode(HttpStatus.Series.SERVER_ERROR);
    }

    public static <T> RetryPolicyBuilder<T> maaClientPolicy() {
        return new RetryPolicyBuilder<T>(new RetryPolicy()).withMaxRetries(5).withBackoff(1L, 20L, ChronoUnit.SECONDS).handle(Arrays.asList(IOHttpException.class, IOException.class)).withStatusCode(HttpStatus.Series.SERVER_ERROR).withStatusCode(HttpStatus.TOO_MANY_REQUESTS.value());
    }

    public static <T> RetryPolicyBuilder<T> mediaRateLimitPolicy() {
        return new RetryPolicyBuilder<T>(new RetryPolicy()).withMaxRetries(6).withBackoff(30L, 120L, ChronoUnit.SECONDS, 2.0).handle(IOHttpException.class).withStatusCode(403, 404, 408, 409, 429).withStatusCode(HttpStatus.Series.SERVER_ERROR).withStatusCodeAndMsg(HttpStatus.BAD_REQUEST.value(), MEDIA_ERROR);
    }

    public static <T> RetryPolicyBuilder<T> s3policy() {
        return new RetryPolicyBuilder<T>(new RetryPolicy().withMaxRetries(5).withBackoff(1L, 30L, ChronoUnit.SECONDS)).handle(Arrays.asList(IOHttpException.class, IOException.class)).withStatusCode(HttpStatus.TOO_MANY_REQUESTS.value()).withStatusCode(HttpStatus.Series.SERVER_ERROR);
    }

    public static <T> RetryPolicyBuilder<T> createFromBase(RetryPolicy<T> baseRetryPolicy) {
        return new RetryPolicyBuilder<T>(baseRetryPolicy);
    }

    public RetryPolicyBuilder<T> withStatusCode(Integer ... allowRetryCodes) {
        return this.withStatusCode(Stream.of(allowRetryCodes).collect(Collectors.toSet()));
    }

    public RetryPolicyBuilder<T> withStatusCode(Set<Integer> retryStatusCodes) {
        this.predicates.add(exception -> {
            if (exception instanceof HttpServiceException) {
                Integer statusCode = ((HttpServiceException)exception).getStatusCode();
                return retryStatusCodes.contains(statusCode);
            }
            return false;
        });
        return this;
    }

    public RetryPolicyBuilder<T> withErrorCode(ErrorCode ... retryErrorCodes) {
        this.predicates.add(exception -> {
            if (exception instanceof HttpServiceException) {
                Integer errorCode = ((HttpServiceException)exception).getErrorCode();
                return Arrays.stream(retryErrorCodes).map(ErrorCode::getCode).anyMatch(code -> code.equals(errorCode));
            }
            return false;
        });
        return this;
    }

    public RetryPolicyBuilder<T> withStatusCodeAndMsg(Integer retryCode, String errorMsg) {
        this.predicates.add(exception -> {
            if (exception instanceof HttpServiceException) {
                Integer statusCode = ((HttpServiceException)exception).getStatusCode();
                String exceptionMsg = exception.getMessage();
                return retryCode.equals(statusCode) && exceptionMsg.contains(errorMsg);
            }
            return false;
        });
        return this;
    }

    public RetryPolicy<T> build() {
        this.policy.handleIf((o, exception) -> this.evaluatePredicates((Throwable)exception));
        return this.policy;
    }

    private boolean evaluatePredicates(Throwable exception) {
        return !this.predicates.isEmpty() && this.predicates.stream().anyMatch(predicate -> predicate.test(exception));
    }

    public RetryPolicyBuilder<T> handle(List<Class<? extends Throwable>> asList) {
        this.policy.handle(asList);
        return this;
    }

    public RetryPolicyBuilder<T> handle(Class<? extends Throwable> exceptionClass) {
        this.policy.handle(exceptionClass);
        return this;
    }

    public RetryPolicyBuilder<T> withPredicate(Predicate<Throwable> predicate) {
        this.predicates.add(predicate);
        return this;
    }

    public RetryPolicyBuilder<T> withStatusCode(HttpStatus.Series statusSeries) {
        this.predicates.add(exception -> {
            if (exception instanceof HttpServiceException) {
                int statusCode = ((HttpServiceException)exception).getStatusCode();
                try {
                    return HttpStatus.Series.valueOf((int)statusCode) == statusSeries;
                }
                catch (IllegalArgumentException e) {
                    log.warn("Unknown HTTP code: {}", (Object)statusCode);
                    return false;
                }
            }
            return false;
        });
        return this;
    }

    public RetryPolicyBuilder<T> withMaxRetries(int maxRetries) {
        this.policy.withMaxRetries(maxRetries);
        return this;
    }

    public RetryPolicyBuilder<T> withBackoff(long delay, long maxDelay, ChronoUnit chronoUnit) {
        this.policy.withBackoff(delay, maxDelay, chronoUnit);
        return this;
    }

    public RetryPolicyBuilder<T> withBackoff(long delay, long maxDelay, ChronoUnit chronoUnit, double delayFactor) {
        this.policy.withBackoff(delay, maxDelay, chronoUnit, delayFactor);
        return this;
    }
}


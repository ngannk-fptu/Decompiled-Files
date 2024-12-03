/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.spring.container.LazyComponentReference
 *  javax.servlet.http.HttpServletRequest
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.core.persistence.hibernate;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.impl.core.persistence.hibernate.ExceptionMonitor;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.spring.container.LazyComponentReference;
import java.time.Duration;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ExceptionMonitorPredicates {
    private static final Logger log = LoggerFactory.getLogger(ExceptionMonitorPredicates.class);
    @VisibleForTesting
    public static final Duration CONNECTION_PROVIDER_EXCEPTION_AGE_THRESHOLD = Duration.parse(System.getProperty("confluence.ConnectionProvider.exceptionAgeThreshold", "PT5M"));
    private static final Supplier<ExceptionMonitor> connectionProviderMonitorRef = new LazyComponentReference("connectionProviderMonitor");

    public static Predicate<HttpServletRequest> shortCircuitRequestTester() {
        return new ExceptionAgePredicate(() -> ExceptionMonitorPredicates.getTimeSinceLastException(connectionProviderMonitorRef), CONNECTION_PROVIDER_EXCEPTION_AGE_THRESHOLD);
    }

    private static Optional<Duration> getTimeSinceLastException(Supplier<ExceptionMonitor> monitorRef) {
        return ContainerManager.isContainerSetup() ? monitorRef.get().getTimeSinceLastException() : Optional.empty();
    }

    static class ExceptionAgePredicate
    implements Predicate<HttpServletRequest> {
        private final Supplier<Optional<Duration>> timeSinceLastException;
        private final Duration defaultExceptionAgeThreshold;

        ExceptionAgePredicate(Supplier<Optional<Duration>> timeSinceLastException, Duration defaultExceptionAgeThreshold) {
            this.timeSinceLastException = timeSinceLastException;
            this.defaultExceptionAgeThreshold = defaultExceptionAgeThreshold;
        }

        @Override
        public boolean test(HttpServletRequest request) {
            return this.hasExceptionYoungerThan(this.getExceptionAgeThreshold(request));
        }

        private boolean hasExceptionYoungerThan(Duration exceptionAgeThreshold) {
            return this.timeSinceLastException.get().map(timeSinceLastException -> ExceptionAgePredicate.isWithinThreshold(exceptionAgeThreshold, timeSinceLastException)).orElse(false);
        }

        private static boolean isWithinThreshold(Duration threshold, Duration duration) {
            if (duration.compareTo(threshold) < 0) {
                log.debug("Time since last ConnectionProvider exception is {}, which is less than the threshold of {}", (Object)duration, (Object)threshold);
                return true;
            }
            log.debug("Time since last ConnectionProvider exception is {}, which is longer than the threshold of {}", (Object)duration, (Object)threshold);
            return false;
        }

        private Duration getExceptionAgeThreshold(HttpServletRequest request) {
            return Optional.ofNullable(request.getHeader("X-Confluence-ExceptionAgeThreshold")).map(Duration::parse).orElse(this.defaultExceptionAgeThreshold);
        }
    }
}


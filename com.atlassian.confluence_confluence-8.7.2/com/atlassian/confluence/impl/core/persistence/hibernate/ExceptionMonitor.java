/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.aop.ThrowsAdvice
 */
package com.atlassian.confluence.impl.core.persistence.hibernate;

import com.atlassian.annotations.VisibleForTesting;
import java.lang.reflect.Method;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.ThrowsAdvice;

public final class ExceptionMonitor {
    private static final Logger log = LoggerFactory.getLogger(ExceptionMonitor.class);
    private final AtomicReference<Instant> lastExceptionTime = new AtomicReference();
    private final Clock clock;

    ExceptionMonitor(Clock clock) {
        this.clock = clock;
    }

    ThrowsAdvice exceptionCapturingAdvice() {
        return new ExceptionCapturingAdvice();
    }

    @VisibleForTesting
    public void setLastExceptionTime(Instant instant) {
        this.lastExceptionTime.set(instant);
    }

    @VisibleForTesting
    public void clearLastExceptionTime() {
        this.lastExceptionTime.set(null);
    }

    Optional<Duration> getTimeSinceLastException() {
        return Optional.ofNullable(this.lastExceptionTime.get()).map(instant -> Duration.between(instant, this.clock.instant()));
    }

    public class ExceptionCapturingAdvice
    implements ThrowsAdvice {
        public void afterThrowing(Method method, Object[] args, Object target, Exception ex) {
            log.warn("Recording exception of type {} thrown by {}: {}", new Object[]{ex.getClass().getName(), method, ex.getMessage()});
            log.debug("Exception thrown by {}", (Object)method, (Object)ex);
            ExceptionMonitor.this.setLastExceptionTime(ExceptionMonitor.this.clock.instant());
        }
    }
}


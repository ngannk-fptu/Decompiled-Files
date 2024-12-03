/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.confluence.api.util.JodaTimeUtils
 *  com.atlassian.fugue.Maybe
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Stopwatch
 *  com.google.common.base.Supplier
 *  com.google.common.base.Ticker
 *  net.jcip.annotations.NotThreadSafe
 *  net.jcip.annotations.ThreadSafe
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.joda.time.Duration
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.util.profiling;

import com.atlassian.annotations.Internal;
import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.api.util.JodaTimeUtils;
import com.atlassian.fugue.Maybe;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Stopwatch;
import com.google.common.base.Supplier;
import com.google.common.base.Ticker;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import net.jcip.annotations.NotThreadSafe;
import net.jcip.annotations.ThreadSafe;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ThreadSafe
@ParametersAreNonnullByDefault
@Internal
public class DurationThresholdWarningTimingHelper {
    private static final Logger log = LoggerFactory.getLogger(DurationThresholdWarningTimingHelper.class);
    private final WarnLog warnLog;
    private final java.time.Duration optionalWarningThreshold;
    private final Ticker ticker;
    private static final Timer NO_OP_TIMER = new Timer(){

        @Override
        public Timer start() {
            return this;
        }

        @Override
        public void stopAndCheckTiming() {
        }
    };

    @Deprecated
    DurationThresholdWarningTimingHelper(Maybe<Duration> warningThreshold, Ticker ticker, WarnLog warnLog) {
        this(Optional.ofNullable((Duration)warningThreshold.getOrNull()), ticker, warnLog);
    }

    @Deprecated(forRemoval=true)
    DurationThresholdWarningTimingHelper(Optional<Duration> warningThreshold, Ticker ticker, WarnLog warnLog) {
        this(JodaTimeUtils.convert((Duration)warningThreshold.orElse(null)), ticker, warnLog);
    }

    DurationThresholdWarningTimingHelper(@Nullable java.time.Duration warningThreshold, Ticker ticker, WarnLog warnLog) {
        this.warnLog = Objects.requireNonNull(warnLog);
        this.ticker = Objects.requireNonNull(ticker);
        this.optionalWarningThreshold = warningThreshold;
    }

    @VisibleForTesting
    java.time.Duration getWarningThreshold() {
        return this.optionalWarningThreshold;
    }

    @Deprecated
    public Timer newTimer(Supplier<String> actionDescriptionSupplier, boolean disableWarnings) {
        return this.newDescribedTimer(() -> actionDescriptionSupplier.get(), disableWarnings);
    }

    public Timer newDescribedTimer(java.util.function.Supplier<String> actionDescriptionSupplier, boolean disableWarnings) {
        if (disableWarnings || this.optionalWarningThreshold == null) {
            log.debug("Returning no-op timer");
            return NO_OP_TIMER;
        }
        log.debug("Returning warning timer");
        return new WarningTimer(actionDescriptionSupplier);
    }

    public Timer newWarningTimer(String actionDescriptionFormat, Object ... actionDescriptionArgs) {
        return this.newDescribedTimer(() -> String.format(actionDescriptionFormat, actionDescriptionArgs), false);
    }

    private void checkElapsedTimeAndWarnIfThresholdExceeded(long elapsedMillis, java.util.function.Supplier<String> actionDescriptionSupplier) {
        long thresholdMillis = this.optionalWarningThreshold.toMillis();
        if (elapsedMillis > thresholdMillis) {
            if (this.warnLog.isLogEnabled()) {
                this.warnLog.logMessage("Execution time for {} took {} ms (warning threshold is {} ms)", actionDescriptionSupplier.get(), elapsedMillis, thresholdMillis);
            } else if (log.isDebugEnabled()) {
                log.debug("Execution time for {} took {} ms (warning threshold is {} ms)", new Object[]{actionDescriptionSupplier.get(), elapsedMillis, thresholdMillis});
            }
        }
    }

    @VisibleForTesting
    static interface WarnLog {
        public boolean isLogEnabled();

        public void logMessage(String var1, Object ... var2);
    }

    private class WarningTimer
    implements Timer {
        private final Stopwatch stopwatch;
        private final java.util.function.Supplier<String> actionDescriptionSupplier;

        public WarningTimer(java.util.function.Supplier<String> actionDescriptionSupplier) {
            this.stopwatch = Stopwatch.createUnstarted((Ticker)DurationThresholdWarningTimingHelper.this.ticker);
            this.actionDescriptionSupplier = actionDescriptionSupplier;
        }

        @Override
        public Timer start() {
            this.stopwatch.reset().start();
            return this;
        }

        @Override
        public void stopAndCheckTiming() {
            DurationThresholdWarningTimingHelper.this.checkElapsedTimeAndWarnIfThresholdExceeded(this.stopwatch.stop().elapsed(TimeUnit.MILLISECONDS), this.actionDescriptionSupplier);
        }
    }

    @NotThreadSafe
    public static interface Timer {
        public Timer start();

        public void stopAndCheckTiming();
    }
}


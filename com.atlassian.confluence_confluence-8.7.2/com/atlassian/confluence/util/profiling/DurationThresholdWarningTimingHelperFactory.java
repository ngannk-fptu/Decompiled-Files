/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.confluence.api.util.JodaTimeUtils
 *  com.google.common.base.Ticker
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
import com.atlassian.confluence.util.profiling.DurationThresholdWarningTimingHelper;
import com.google.common.base.Ticker;
import net.jcip.annotations.ThreadSafe;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ThreadSafe
@ParametersAreNonnullByDefault
@Internal
public class DurationThresholdWarningTimingHelperFactory {
    private static final Logger log = LoggerFactory.getLogger(DurationThresholdWarningTimingHelperFactory.class);
    private static final DurationThresholdWarningTimingHelper.WarnLog SLF4J = new DurationThresholdWarningTimingHelper.WarnLog(){

        @Override
        public boolean isLogEnabled() {
            return log.isWarnEnabled();
        }

        @Override
        public void logMessage(String format, Object ... args) {
            log.warn(format, args);
        }
    };

    @Deprecated(forRemoval=true)
    public static DurationThresholdWarningTimingHelper createFromSystemProperty(String millisPropertyName, @Nullable Duration defaultThreshold) {
        return DurationThresholdWarningTimingHelperFactory.createFromSystemProperty(millisPropertyName, JodaTimeUtils.convert((Duration)defaultThreshold));
    }

    public static DurationThresholdWarningTimingHelper createFromSystemProperty(String millisPropertyName, @Nullable java.time.Duration defaultThreshold) {
        String propertyValue = System.getProperty(millisPropertyName);
        if (propertyValue == null) {
            if (defaultThreshold == null) {
                log.debug("Creating helper with warning threshold disabled by default since sysprop {} was not defined", (Object)millisPropertyName);
                return DurationThresholdWarningTimingHelperFactory.createDisabled();
            }
            log.debug("Creating helper with default warning threshold of {} since sysprop {} was not defined", (Object)defaultThreshold, (Object)millisPropertyName);
            return DurationThresholdWarningTimingHelperFactory.create(defaultThreshold);
        }
        int thresholdMillis = Integer.parseInt(propertyValue);
        if (thresholdMillis > 0) {
            java.time.Duration threshold = java.time.Duration.ofMillis(thresholdMillis);
            log.debug("Creating helper with configured warning threshold of {} from sysprop {}", (Object)threshold, (Object)millisPropertyName);
            return DurationThresholdWarningTimingHelperFactory.create(threshold);
        }
        log.debug("Creating helper with warning threshold disabled by sysprop {}", (Object)millisPropertyName);
        return DurationThresholdWarningTimingHelperFactory.createDisabled();
    }

    private static DurationThresholdWarningTimingHelper create(java.time.Duration warningThreshold) {
        return new DurationThresholdWarningTimingHelper(warningThreshold, Ticker.systemTicker(), SLF4J);
    }

    private static DurationThresholdWarningTimingHelper createDisabled() {
        return new DurationThresholdWarningTimingHelper((java.time.Duration)null, Ticker.systemTicker(), SLF4J);
    }
}


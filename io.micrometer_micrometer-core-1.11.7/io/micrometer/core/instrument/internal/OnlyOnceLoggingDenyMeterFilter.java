/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.util.internal.logging.InternalLogger
 *  io.micrometer.common.util.internal.logging.InternalLoggerFactory
 */
package io.micrometer.core.instrument.internal;

import io.micrometer.common.util.internal.logging.InternalLogger;
import io.micrometer.common.util.internal.logging.InternalLoggerFactory;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.config.MeterFilterReply;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public final class OnlyOnceLoggingDenyMeterFilter
implements MeterFilter {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(OnlyOnceLoggingDenyMeterFilter.class);
    private final AtomicBoolean alreadyWarned = new AtomicBoolean();
    private final Supplier<String> message;

    public OnlyOnceLoggingDenyMeterFilter(Supplier<String> message) {
        this.message = message;
    }

    @Override
    public MeterFilterReply accept(Meter.Id id) {
        if (logger.isWarnEnabled() && this.alreadyWarned.compareAndSet(false, true)) {
            logger.warn(this.message.get());
        }
        return MeterFilterReply.DENY;
    }
}


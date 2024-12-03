/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.streams.internal.throttling;

import java.time.Instant;
import java.util.function.Supplier;

public abstract class CallTiming {
    protected final Instant callStart;
    protected final Instant callEnd;

    public abstract long getCallDurationMs();

    public abstract boolean mayEndAfter(Instant var1);

    public abstract boolean endedBefore(Instant var1);

    public abstract CallTiming end();

    private CallTiming(Instant callStart, Instant callEnd) {
        this.callStart = callStart;
        this.callEnd = callEnd;
    }

    public static CallTiming start(Supplier<Instant> clock) {
        return new RunningCallTiming(clock);
    }

    public static final class RunningCallTiming
    extends CallTiming {
        private final Supplier<Instant> clock;

        private RunningCallTiming(Supplier<Instant> clock) {
            super(clock.get(), null);
            this.clock = clock;
        }

        @Override
        public long getCallDurationMs() {
            Instant endOrNow = this.callEnd == null ? this.clock.get() : this.callEnd;
            return endOrNow.toEpochMilli() - this.callStart.toEpochMilli();
        }

        @Override
        public CallTiming end() {
            return new FinishedCallTiming(this.callStart, this.clock.get());
        }

        @Override
        public boolean mayEndAfter(Instant instant) {
            return true;
        }

        @Override
        public boolean endedBefore(Instant instant) {
            return false;
        }
    }

    private static final class FinishedCallTiming
    extends CallTiming {
        private FinishedCallTiming(Instant callStart, Instant callEnd) {
            super(callStart, callEnd);
        }

        @Override
        public long getCallDurationMs() {
            return this.callEnd.toEpochMilli() - this.callStart.toEpochMilli();
        }

        @Override
        public CallTiming end() {
            throw new IllegalStateException("Attempted to finish a call that already ended");
        }

        @Override
        public boolean mayEndAfter(Instant instant) {
            return this.callEnd.isAfter(instant);
        }

        @Override
        public boolean endedBefore(Instant instant) {
            return this.callEnd.isBefore(instant);
        }
    }
}


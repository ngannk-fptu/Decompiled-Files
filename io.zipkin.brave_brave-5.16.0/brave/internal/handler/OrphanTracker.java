/*
 * Decompiled with CFR 0.152.
 */
package brave.internal.handler;

import brave.Clock;
import brave.Tracing;
import brave.handler.MutableSpan;
import brave.handler.SpanHandler;
import brave.internal.Nullable;
import brave.internal.collect.WeakConcurrentMap;
import brave.propagation.TraceContext;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OrphanTracker
extends SpanHandler {
    final MutableSpan defaultSpan;
    final Clock clock;
    final WeakConcurrentMap<MutableSpan, Throwable> spanToCaller = new WeakConcurrentMap();
    final Level logLevel;

    public static Builder newBuilder() {
        return new Builder();
    }

    OrphanTracker(Builder builder) {
        this.defaultSpan = builder.defaultSpan;
        this.clock = builder.clock;
        this.logLevel = builder.logLevel;
    }

    @Override
    public boolean begin(TraceContext context, MutableSpan span, @Nullable TraceContext parent) {
        Throwable oldCaller = this.spanToCaller.putIfProbablyAbsent(span, new Throwable("Thread " + Thread.currentThread().getName() + " allocated span here"));
        assert (oldCaller == null) : "Bug: unexpected to have an existing reference to a new MutableSpan!";
        return true;
    }

    @Override
    public boolean end(TraceContext context, MutableSpan span, SpanHandler.Cause cause) {
        Throwable caller = this.spanToCaller.remove(span);
        if (cause != SpanHandler.Cause.ORPHANED) {
            return true;
        }
        boolean allocatedButNotUsed = span.equals(new MutableSpan(context, this.defaultSpan));
        if (caller != null) {
            this.log(context, allocatedButNotUsed, caller);
        }
        if (allocatedButNotUsed) {
            return true;
        }
        span.annotate(this.clock.currentTimeMicroseconds(), "brave.flush");
        return true;
    }

    void log(TraceContext context, boolean allocatedButNotUsed, Throwable caller) {
        Logger logger = this.logger();
        if (!logger.isLoggable(this.logLevel)) {
            return;
        }
        String message = allocatedButNotUsed ? "Span " + context + " was allocated but never used" : "Span " + context + " neither finished nor flushed before GC";
        logger.log(this.logLevel, message, caller);
    }

    Logger logger() {
        return LoggerHolder.LOG;
    }

    public String toString() {
        return "OrphanTracker{}";
    }

    static final class LoggerHolder {
        static final Logger LOG = Logger.getLogger(Tracing.class.getName());

        LoggerHolder() {
        }
    }

    public static final class Builder {
        MutableSpan defaultSpan;
        Clock clock;
        Level logLevel = Level.FINE;

        public Builder defaultSpan(MutableSpan defaultSpan) {
            this.defaultSpan = defaultSpan;
            return this;
        }

        public Builder clock(Clock clock) {
            this.clock = clock;
            return this;
        }

        public Builder logLevel(Level logLevel) {
            this.logLevel = logLevel;
            return this;
        }

        public SpanHandler build() {
            if (this.defaultSpan == null) {
                throw new NullPointerException("defaultSpan == null");
            }
            if (this.clock == null) {
                throw new NullPointerException("clock == null");
            }
            return new OrphanTracker(this);
        }

        Builder() {
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package brave.internal.recorder;

import brave.Clock;
import brave.handler.MutableSpan;
import brave.handler.SpanHandler;
import brave.internal.Nullable;
import brave.internal.collect.WeakConcurrentMap;
import brave.internal.recorder.PendingSpan;
import brave.internal.recorder.TickClock;
import brave.propagation.TraceContext;
import java.lang.ref.Reference;
import java.util.concurrent.atomic.AtomicBoolean;

public final class PendingSpans
extends WeakConcurrentMap<TraceContext, PendingSpan> {
    final MutableSpan defaultSpan;
    final Clock clock;
    final SpanHandler spanHandler;
    final AtomicBoolean noop;

    public PendingSpans(MutableSpan defaultSpan, Clock clock, SpanHandler spanHandler, AtomicBoolean noop) {
        this.defaultSpan = defaultSpan;
        this.clock = clock;
        this.spanHandler = spanHandler;
        this.noop = noop;
    }

    @Nullable
    public PendingSpan get(TraceContext context) {
        return (PendingSpan)this.getIfPresent(context);
    }

    public PendingSpan getOrCreate(@Nullable TraceContext parent, TraceContext context, boolean start) {
        TickClock clock;
        PendingSpan parentSpan;
        PendingSpan result = this.get(context);
        if (result != null) {
            return result;
        }
        MutableSpan span = new MutableSpan(context, this.defaultSpan);
        PendingSpan pendingSpan = parentSpan = parent != null ? this.get(parent) : null;
        if (parentSpan != null) {
            TraceContext parentContext = parentSpan.context();
            if (parentContext != null) {
                parent = parentContext;
            }
            clock = parentSpan.clock;
            if (start) {
                span.startTimestamp(clock.currentTimeMicroseconds());
            }
        } else {
            long currentTimeMicroseconds = this.clock.currentTimeMicroseconds();
            clock = new TickClock(currentTimeMicroseconds, System.nanoTime());
            if (start) {
                span.startTimestamp(currentTimeMicroseconds);
            }
        }
        PendingSpan newSpan = new PendingSpan(context, span, clock);
        PendingSpan previousSpan = this.putIfProbablyAbsent(context, newSpan);
        if (previousSpan != null) {
            return previousSpan;
        }
        assert (parent != null || context.isLocalRoot()) : "Bug (or unexpected call to internal code): parent can only be null in a local root!";
        this.spanHandler.begin(newSpan.handlerContext, newSpan.span, parentSpan != null ? parentSpan.handlerContext : null);
        return newSpan;
    }

    public void abandon(TraceContext context) {
        PendingSpan last = (PendingSpan)this.remove(context);
        if (last != null && this.spanHandler.handlesAbandoned()) {
            this.spanHandler.end(last.handlerContext, last.span, SpanHandler.Cause.ABANDONED);
        }
    }

    public void flush(TraceContext context) {
        PendingSpan last = (PendingSpan)this.remove(context);
        if (last != null) {
            this.spanHandler.end(last.handlerContext, last.span, SpanHandler.Cause.FLUSHED);
        }
    }

    public void finish(TraceContext context, long timestamp) {
        PendingSpan last = (PendingSpan)this.remove(context);
        if (last == null) {
            return;
        }
        last.span.finishTimestamp(timestamp != 0L ? timestamp : last.clock.currentTimeMicroseconds());
        this.spanHandler.end(last.handlerContext, last.span, SpanHandler.Cause.FINISHED);
    }

    @Override
    protected void expungeStaleEntries() {
        Reference reference;
        boolean noop = this.noop.get();
        while ((reference = this.poll()) != null) {
            PendingSpan value = (PendingSpan)this.removeStaleEntry(reference);
            if (noop || value == null) continue;
            assert (value.context() == null) : "unexpected for the weak referent to be present after GC!";
            this.spanHandler.end(value.handlerContext, value.span, SpanHandler.Cause.ORPHANED);
        }
    }
}


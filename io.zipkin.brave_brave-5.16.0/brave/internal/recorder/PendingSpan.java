/*
 * Decompiled with CFR 0.152.
 */
package brave.internal.recorder;

import brave.Clock;
import brave.handler.MutableSpan;
import brave.internal.InternalPropagation;
import brave.internal.Nullable;
import brave.internal.recorder.TickClock;
import brave.propagation.TraceContext;
import java.lang.ref.WeakReference;

public final class PendingSpan
extends WeakReference<TraceContext> {
    final MutableSpan span;
    final TickClock clock;
    final TraceContext handlerContext;

    PendingSpan(TraceContext context, MutableSpan span, TickClock clock) {
        super(context);
        this.span = span;
        this.clock = clock;
        this.handlerContext = InternalPropagation.instance.shallowCopy(context);
    }

    @Nullable
    public TraceContext context() {
        return (TraceContext)this.get();
    }

    public MutableSpan state() {
        return this.span;
    }

    public Clock clock() {
        return this.clock;
    }
}


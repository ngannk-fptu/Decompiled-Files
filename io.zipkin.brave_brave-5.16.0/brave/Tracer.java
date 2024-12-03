/*
 * Decompiled with CFR 0.152.
 */
package brave;

import brave.Clock;
import brave.LazySpan;
import brave.NoopScopedSpan;
import brave.NoopSpan;
import brave.NoopSpanCustomizer;
import brave.RealScopedSpan;
import brave.RealSpan;
import brave.ScopedSpan;
import brave.Span;
import brave.SpanCustomizer;
import brave.SpanCustomizerShield;
import brave.handler.MutableSpan;
import brave.handler.SpanHandler;
import brave.internal.InternalPropagation;
import brave.internal.Nullable;
import brave.internal.Platform;
import brave.internal.collect.Lists;
import brave.internal.recorder.PendingSpan;
import brave.internal.recorder.PendingSpans;
import brave.propagation.CurrentTraceContext;
import brave.propagation.Propagation;
import brave.propagation.SamplingFlags;
import brave.propagation.TraceContext;
import brave.propagation.TraceContextOrSamplingFlags;
import brave.propagation.TraceIdContext;
import brave.sampler.Sampler;
import brave.sampler.SamplerFunction;
import java.io.Closeable;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Tracer {
    final Clock clock;
    final Propagation.Factory propagationFactory;
    final SpanHandler spanHandler;
    final PendingSpans pendingSpans;
    final Sampler sampler;
    final CurrentTraceContext currentTraceContext;
    final boolean traceId128Bit;
    final boolean supportsJoin;
    final boolean alwaysSampleLocal;
    final AtomicBoolean noop;

    Tracer(Clock clock, Propagation.Factory propagationFactory, SpanHandler spanHandler, PendingSpans pendingSpans, Sampler sampler, CurrentTraceContext currentTraceContext, boolean traceId128Bit, boolean supportsJoin, boolean alwaysSampleLocal, AtomicBoolean noop) {
        this.clock = clock;
        this.propagationFactory = propagationFactory;
        this.spanHandler = spanHandler;
        this.pendingSpans = pendingSpans;
        this.sampler = sampler;
        this.currentTraceContext = currentTraceContext;
        this.traceId128Bit = traceId128Bit;
        this.supportsJoin = supportsJoin;
        this.alwaysSampleLocal = alwaysSampleLocal;
        this.noop = noop;
    }

    @Deprecated
    public Tracer withSampler(Sampler sampler) {
        if (sampler == null) {
            throw new NullPointerException("sampler == null");
        }
        return new Tracer(this.clock, this.propagationFactory, this.spanHandler, this.pendingSpans, sampler, this.currentTraceContext, this.traceId128Bit, this.supportsJoin, this.alwaysSampleLocal, this.noop);
    }

    public Span newTrace() {
        return this._toSpan(null, this.newRootContext(0));
    }

    public final Span joinSpan(TraceContext context) {
        if (context == null) {
            throw new NullPointerException("context == null");
        }
        if (!this.supportsJoin) {
            return this.newChild(context);
        }
        int flags = InternalPropagation.instance.flags(context);
        if (!context.shared()) {
            return this.toSpan(context, InternalPropagation.instance.withFlags(context, flags |= 0x10));
        }
        return this.toSpan(InternalPropagation.instance.withFlags(context, flags &= 0xFFFFFFEF), context);
    }

    TraceContext swapForPendingContext(TraceContext context) {
        PendingSpan pendingSpan = this.pendingSpans.get(context);
        return pendingSpan != null ? pendingSpan.context() : null;
    }

    public Span newChild(TraceContext parent) {
        if (parent == null) {
            throw new NullPointerException("parent == null");
        }
        return this._toSpan(parent, this.decorateContext(parent, parent.spanId()));
    }

    TraceContext newRootContext(int flags) {
        return this.decorateContext(flags &= 0xFFFFFFEF, 0L, 0L, 0L, 0L, 0L, Collections.<Object>emptyList());
    }

    TraceContext decorateContext(TraceContext parent, long parentId) {
        int flags = InternalPropagation.instance.flags(parent);
        return this.decorateContext(flags &= 0xFFFFFFEF, parent.traceIdHigh(), parent.traceId(), parent.localRootId(), parentId, 0L, parent.extra());
    }

    TraceContext decorateContext(int flags, long traceIdHigh, long traceId, long localRootId, long parentId, long spanId, List<Object> extra) {
        if (this.alwaysSampleLocal && (flags & 0x20) != 32) {
            flags |= 0x20;
        }
        if (spanId == 0L) {
            spanId = this.nextId();
        }
        if (traceId == 0L) {
            traceIdHigh = this.traceId128Bit ? Platform.get().nextTraceIdHigh() : 0L;
            traceId = spanId;
        }
        if ((flags & 4) != 4) {
            flags = InternalPropagation.sampled(this.sampler.isSampled(traceId), flags);
            flags &= 0xFFFFFFEF;
        }
        if (localRootId == 0L) {
            localRootId = spanId;
            flags |= 0x40;
        } else {
            flags &= 0xFFFFFFBF;
        }
        return this.propagationFactory.decorate(InternalPropagation.instance.newTraceContext(flags, traceIdHigh, traceId, localRootId, parentId, spanId, extra));
    }

    public Span nextSpan(TraceContextOrSamplingFlags extracted) {
        int flags;
        if (extracted == null) {
            throw new NullPointerException("extracted == null");
        }
        TraceContext context = extracted.context();
        if (context != null) {
            return this.newChild(context);
        }
        TraceIdContext traceIdContext = extracted.traceIdContext();
        if (traceIdContext != null) {
            return this._toSpan(null, this.decorateContext(InternalPropagation.instance.flags(extracted.traceIdContext()), traceIdContext.traceIdHigh(), traceIdContext.traceId(), 0L, 0L, 0L, extracted.extra()));
        }
        SamplingFlags samplingFlags = extracted.samplingFlags();
        List<Object> extra = extracted.extra();
        TraceContext parent = this.currentTraceContext.get();
        long traceIdHigh = 0L;
        long traceId = 0L;
        long localRootId = 0L;
        long spanId = 0L;
        if (parent != null) {
            flags = InternalPropagation.instance.flags(parent);
            traceIdHigh = parent.traceIdHigh();
            traceId = parent.traceId();
            localRootId = parent.localRootId();
            spanId = parent.spanId();
            extra = Lists.concat(extra, parent.extra());
        } else {
            flags = InternalPropagation.instance.flags(samplingFlags);
        }
        return this._toSpan(parent, this.decorateContext(flags, traceIdHigh, traceId, localRootId, spanId, 0L, extra));
    }

    public Span toSpan(TraceContext context) {
        return this.toSpan(null, context);
    }

    Span toSpan(@Nullable TraceContext parent, TraceContext context) {
        TraceContext pendingContext = this.swapForPendingContext(context);
        if (pendingContext != null) {
            return this._toSpan(parent, pendingContext);
        }
        TraceContext decorated = this.decorateContext(InternalPropagation.instance.flags(context), context.traceIdHigh(), context.traceId(), parent != null ? context.localRootId() : 0L, context.parentIdAsLong(), context.spanId(), context.extra());
        return this._toSpan(parent, decorated);
    }

    Span _toSpan(@Nullable TraceContext parent, TraceContext context) {
        if (this.isNoop(context)) {
            return new NoopSpan(context);
        }
        PendingSpan pendingSpan = this.pendingSpans.getOrCreate(parent, context, false);
        TraceContext pendingContext = pendingSpan.context();
        if (pendingContext != null) {
            context = pendingContext;
        }
        return new RealSpan(context, this.pendingSpans, pendingSpan.state(), pendingSpan.clock());
    }

    public SpanInScope withSpanInScope(@Nullable Span span) {
        return new SpanInScope(this.currentTraceContext.newScope(span != null ? span.context() : null));
    }

    public SpanCustomizer currentSpanCustomizer() {
        TraceContext context = this.currentTraceContext.get();
        if (context == null || this.isNoop(context)) {
            return NoopSpanCustomizer.INSTANCE;
        }
        return new SpanCustomizerShield(this.toSpan(context));
    }

    @Nullable
    public Span currentSpan() {
        TraceContext context = this.currentTraceContext.get();
        if (context == null) {
            return null;
        }
        return new LazySpan(this, context);
    }

    public Span nextSpan() {
        TraceContext parent = this.currentTraceContext.get();
        return parent != null ? this.newChild(parent) : this.newTrace();
    }

    public ScopedSpan startScopedSpan(String name) {
        return this.startScopedSpanWithParent(name, this.currentTraceContext.get());
    }

    public <T> ScopedSpan startScopedSpan(String name, SamplerFunction<T> samplerFunction, T arg) {
        if (name == null) {
            throw new NullPointerException("name == null");
        }
        TraceContext parent = this.currentTraceContext.get();
        return this.newScopedSpan(parent, this.nextContext(samplerFunction, arg, parent), name);
    }

    public <T> Span nextSpan(SamplerFunction<T> samplerFunction, T arg) {
        TraceContext parent = this.currentTraceContext.get();
        return this._toSpan(parent, this.nextContext(samplerFunction, arg, parent));
    }

    public <T> Span nextSpanWithParent(SamplerFunction<T> samplerFunction, T arg, @Nullable TraceContext parent) {
        return this._toSpan(parent, this.nextContext(samplerFunction, arg, parent));
    }

    <T> TraceContext nextContext(SamplerFunction<T> samplerFunction, T arg, TraceContext parent) {
        if (samplerFunction == null) {
            throw new NullPointerException("samplerFunction == null");
        }
        if (arg == null) {
            throw new NullPointerException("arg == null");
        }
        if (parent != null) {
            return this.decorateContext(parent, parent.spanId());
        }
        Boolean sampled = samplerFunction.trySample(arg);
        SamplingFlags flags = sampled != null ? (sampled.booleanValue() ? SamplingFlags.SAMPLED : SamplingFlags.NOT_SAMPLED) : SamplingFlags.EMPTY;
        return this.newRootContext(InternalPropagation.instance.flags(flags));
    }

    public ScopedSpan startScopedSpanWithParent(String name, @Nullable TraceContext parent) {
        if (name == null) {
            throw new NullPointerException("name == null");
        }
        TraceContext context = parent != null ? this.decorateContext(parent, parent.spanId()) : this.newRootContext(0);
        return this.newScopedSpan(parent, context, name);
    }

    ScopedSpan newScopedSpan(@Nullable TraceContext parent, TraceContext context, String name) {
        CurrentTraceContext.Scope scope = this.currentTraceContext.newScope(context);
        if (this.isNoop(context)) {
            return new NoopScopedSpan(context, scope);
        }
        PendingSpan pendingSpan = this.pendingSpans.getOrCreate(parent, context, true);
        Clock clock = pendingSpan.clock();
        MutableSpan state = pendingSpan.state();
        state.name(name);
        return new RealScopedSpan(context, scope, state, clock, this.pendingSpans);
    }

    public String toString() {
        TraceContext currentSpan = this.currentTraceContext.get();
        return "Tracer{" + (currentSpan != null ? "currentSpan=" + currentSpan + ", " : "") + (this.noop.get() ? "noop=true, " : "") + "spanHandler=" + this.spanHandler + "}";
    }

    boolean isNoop(TraceContext context) {
        if (this.noop.get()) {
            return true;
        }
        int flags = InternalPropagation.instance.flags(context);
        if ((flags & 0x20) == 32) {
            return false;
        }
        return (flags & 2) != 2;
    }

    long nextId() {
        long nextId = Platform.get().randomLong();
        while (nextId == 0L) {
            nextId = Platform.get().randomLong();
        }
        return nextId;
    }

    public static final class SpanInScope
    implements Closeable {
        final CurrentTraceContext.Scope scope;

        SpanInScope(CurrentTraceContext.Scope scope) {
            if (scope == null) {
                throw new NullPointerException("scope == null");
            }
            this.scope = scope;
        }

        @Override
        public void close() {
            this.scope.close();
        }

        public String toString() {
            return this.scope.toString();
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package brave.propagation;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import brave.internal.Nullable;
import brave.propagation.TraceContextOrSamplingFlags;
import java.util.ArrayDeque;

public class ThreadLocalSpan {
    public static final ThreadLocalSpan CURRENT_TRACER = new ThreadLocalSpan(null);
    @Nullable
    final Tracer tracer;
    final ThreadLocal<ArrayDeque<SpanAndScope>> currentSpanInScopeStack = new ThreadLocal();

    public static ThreadLocalSpan create(Tracer tracer) {
        if (tracer == null) {
            throw new NullPointerException("tracer == null");
        }
        return new ThreadLocalSpan(tracer);
    }

    ThreadLocalSpan(Tracer tracer) {
        this.tracer = tracer;
    }

    Tracer tracer() {
        return this.tracer != null ? this.tracer : Tracing.currentTracer();
    }

    @Nullable
    public Span next(TraceContextOrSamplingFlags extracted) {
        Tracer tracer = this.tracer();
        if (tracer == null) {
            return null;
        }
        Span next = tracer.nextSpan(extracted);
        SpanAndScope spanAndScope = new SpanAndScope(next, tracer.withSpanInScope(next));
        this.getCurrentSpanInScopeStack().addFirst(spanAndScope);
        return next;
    }

    @Nullable
    public Span next() {
        Tracer tracer = this.tracer();
        if (tracer == null) {
            return null;
        }
        Span next = tracer.nextSpan();
        SpanAndScope spanAndScope = new SpanAndScope(next, tracer.withSpanInScope(next));
        this.getCurrentSpanInScopeStack().addFirst(spanAndScope);
        return next;
    }

    @Nullable
    public Span remove() {
        Tracer tracer = this.tracer();
        Span currentSpan = tracer != null ? tracer.currentSpan() : null;
        SpanAndScope spanAndScope = this.getCurrentSpanInScopeStack().pollFirst();
        if (spanAndScope == null) {
            return currentSpan;
        }
        Span span = spanAndScope.span;
        spanAndScope.spanInScope.close();
        assert (span.equals(currentSpan)) : "Misalignment: scoped span " + span + " !=  current span " + currentSpan;
        return currentSpan;
    }

    ArrayDeque<SpanAndScope> getCurrentSpanInScopeStack() {
        ArrayDeque<SpanAndScope> stack = this.currentSpanInScopeStack.get();
        if (stack == null) {
            stack = new ArrayDeque();
            this.currentSpanInScopeStack.set(stack);
        }
        return stack;
    }

    static final class SpanAndScope {
        final Span span;
        final Tracer.SpanInScope spanInScope;

        SpanAndScope(Span span, Tracer.SpanInScope spanInScope) {
            this.span = span;
            this.spanInScope = spanInScope;
        }
    }
}


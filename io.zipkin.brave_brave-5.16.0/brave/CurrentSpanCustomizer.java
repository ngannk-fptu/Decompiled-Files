/*
 * Decompiled with CFR 0.152.
 */
package brave;

import brave.SpanCustomizer;
import brave.Tracer;
import brave.Tracing;

public final class CurrentSpanCustomizer
implements SpanCustomizer {
    private final Tracer tracer;

    public static CurrentSpanCustomizer create(Tracing tracing) {
        return new CurrentSpanCustomizer(tracing);
    }

    CurrentSpanCustomizer(Tracing tracing) {
        this.tracer = tracing.tracer();
    }

    @Override
    public SpanCustomizer name(String name) {
        return this.tracer.currentSpanCustomizer().name(name);
    }

    @Override
    public SpanCustomizer tag(String key, String value) {
        return this.tracer.currentSpanCustomizer().tag(key, value);
    }

    @Override
    public SpanCustomizer annotate(String value) {
        return this.tracer.currentSpanCustomizer().annotate(value);
    }
}


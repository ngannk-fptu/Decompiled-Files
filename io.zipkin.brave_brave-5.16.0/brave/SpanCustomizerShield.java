/*
 * Decompiled with CFR 0.152.
 */
package brave;

import brave.Span;
import brave.SpanCustomizer;

final class SpanCustomizerShield
implements SpanCustomizer {
    final Span delegate;

    SpanCustomizerShield(Span delegate) {
        this.delegate = delegate;
    }

    @Override
    public SpanCustomizer name(String name) {
        this.delegate.name(name);
        return this;
    }

    @Override
    public SpanCustomizer annotate(String value) {
        this.delegate.annotate(value);
        return this;
    }

    @Override
    public SpanCustomizer tag(String key, String value) {
        this.delegate.tag(key, value);
        return this;
    }

    public String toString() {
        return "SpanCustomizer(" + this.delegate + ")";
    }
}


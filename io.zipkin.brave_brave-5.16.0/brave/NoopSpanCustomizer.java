/*
 * Decompiled with CFR 0.152.
 */
package brave;

import brave.SpanCustomizer;

public enum NoopSpanCustomizer implements SpanCustomizer
{
    INSTANCE;


    @Override
    public SpanCustomizer name(String name) {
        return this;
    }

    @Override
    public SpanCustomizer tag(String key, String value) {
        return this;
    }

    @Override
    public SpanCustomizer annotate(String value) {
        return this;
    }

    public String toString() {
        return "NoopSpanCustomizer{}";
    }
}


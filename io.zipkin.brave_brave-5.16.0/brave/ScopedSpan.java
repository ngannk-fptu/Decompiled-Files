/*
 * Decompiled with CFR 0.152.
 */
package brave;

import brave.SpanCustomizer;
import brave.propagation.TraceContext;

public abstract class ScopedSpan
implements SpanCustomizer {
    public abstract boolean isNoop();

    public abstract TraceContext context();

    @Override
    public abstract ScopedSpan name(String var1);

    @Override
    public abstract ScopedSpan tag(String var1, String var2);

    @Override
    public abstract ScopedSpan annotate(String var1);

    public abstract ScopedSpan error(Throwable var1);

    public abstract void finish();

    ScopedSpan() {
    }
}


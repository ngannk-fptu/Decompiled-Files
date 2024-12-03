/*
 * Decompiled with CFR 0.152.
 */
package brave;

import brave.Request;
import brave.Span;
import brave.internal.Nullable;

public abstract class Response {
    public abstract Span.Kind spanKind();

    @Nullable
    public Request request() {
        return null;
    }

    @Nullable
    public abstract Throwable error();

    @Nullable
    public abstract Object unwrap();

    public String toString() {
        Object unwrapped = this.unwrap();
        if (unwrapped == null || unwrapped == this) {
            return this.getClass().getSimpleName();
        }
        return this.getClass().getSimpleName() + "{" + unwrapped + "}";
    }

    protected Response() {
    }
}


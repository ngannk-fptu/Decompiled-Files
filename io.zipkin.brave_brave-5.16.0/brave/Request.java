/*
 * Decompiled with CFR 0.152.
 */
package brave;

import brave.Span;

public abstract class Request {
    public abstract Span.Kind spanKind();

    public abstract Object unwrap();

    public String toString() {
        Object unwrapped = this.unwrap();
        if (unwrapped == null || unwrapped == this) {
            return this.getClass().getSimpleName();
        }
        return this.getClass().getSimpleName() + "{" + unwrapped + "}";
    }

    protected Request() {
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.util.internal.logging;

@Deprecated
final class FormattingTuple {
    private final String message;
    private final Throwable throwable;

    FormattingTuple(String message, Throwable throwable) {
        this.message = message;
        this.throwable = throwable;
    }

    public String getMessage() {
        return this.message;
    }

    public Throwable getThrowable() {
        return this.throwable;
    }
}


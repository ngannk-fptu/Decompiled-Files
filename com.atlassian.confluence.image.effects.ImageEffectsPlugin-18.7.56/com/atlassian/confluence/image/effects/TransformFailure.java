/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.image.effects;

import java.util.Objects;
import java.util.Optional;

final class TransformFailure {
    private final Reason reason;
    private final Optional<Throwable> cause;

    TransformFailure(Reason reason) {
        this.reason = Objects.requireNonNull(reason);
        this.cause = Optional.empty();
    }

    TransformFailure(Reason reason, Throwable cause) {
        this.reason = Objects.requireNonNull(reason);
        this.cause = Optional.of(cause);
    }

    Reason getReason() {
        return this.reason;
    }

    Optional<Throwable> getCause() {
        return this.cause;
    }

    public static enum Reason {
        IMAGE_DATA_MISSING,
        IMAGE_DATA_TOO_LARGE,
        IMAGE_PIXEL_TOO_LARGE,
        TRANSFORM_FAILURE;

    }
}


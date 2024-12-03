/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.internal.status.error;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ApplinkErrors {
    private ApplinkErrors() {
        throw new UnsupportedOperationException("Do not instantiate " + this.getClass().getSimpleName());
    }

    @Nonnull
    public static String toDetails(@Nonnull Throwable error) {
        return String.format("%s: %s", error.getClass().getName(), error.getMessage());
    }

    @Nullable
    public static Throwable findCauseMatching(@Nonnull Throwable error, @Nonnull Predicate<? super Throwable> matcher) {
        Objects.requireNonNull(error, "error");
        Objects.requireNonNull(matcher, "matcher");
        for (Throwable cause = error; cause != null; cause = cause.getCause()) {
            if (!matcher.apply((Object)cause)) continue;
            return cause;
        }
        return null;
    }

    @Nullable
    public static <E extends Throwable> E findCauseOfType(@Nonnull Throwable error, @Nonnull Class<E> expected) {
        Objects.requireNonNull(expected, "expected");
        return (E)((Throwable)expected.cast(ApplinkErrors.findCauseMatching(error, (Predicate<? super Throwable>)Predicates.instanceOf(expected))));
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.internal.common.exception;

import com.atlassian.applinks.internal.common.exception.DetailedError;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SimpleDetailedError
implements DetailedError {
    private final String context;
    private final String summary;
    private final String details;

    public SimpleDetailedError(@Nullable String context, @Nonnull String summary, @Nullable String details) {
        this.context = context;
        this.summary = Objects.requireNonNull(summary, "summary");
        this.details = details;
    }

    public SimpleDetailedError(@Nonnull String summary) {
        this(null, summary, null);
    }

    @Override
    @Nullable
    public String getContext() {
        return this.context;
    }

    @Override
    @Nonnull
    public String getSummary() {
        return this.summary;
    }

    @Override
    @Nullable
    public String getDetails() {
        return this.details;
    }
}


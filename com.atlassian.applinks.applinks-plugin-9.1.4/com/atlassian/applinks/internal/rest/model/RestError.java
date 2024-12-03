/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Splitter
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.internal.rest.model;

import com.atlassian.applinks.internal.common.exception.DetailedError;
import com.atlassian.applinks.internal.rest.model.BaseRestEntity;
import com.atlassian.applinks.internal.rest.model.ReadOnlyRestRepresentation;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RestError
extends BaseRestEntity
implements ReadOnlyRestRepresentation<DetailedError> {
    public static final String CONTEXT = "context";
    public static final String SUMMARY = "summary";
    public static final String DETAILS = "details";

    public RestError(@Nonnull String summary) {
        this(null, summary, null);
    }

    public RestError(@Nullable String context, @Nonnull String summary, @Nullable Object details) {
        Objects.requireNonNull(summary, SUMMARY);
        this.put(SUMMARY, (Object)summary);
        this.putIfNotNull(CONTEXT, context);
        this.putIfNotNull(DETAILS, details);
    }

    public RestError(@Nonnull Exception javaError) {
        this(null, Objects.requireNonNull(javaError, "javaError").getLocalizedMessage(), javaError.toString());
    }

    public RestError(@Nonnull DetailedError error) {
        this(error.getContext(), error.getSummary(), RestError.splitLines(error));
    }

    private static Iterable<String> splitLines(@Nonnull DetailedError error) {
        return error.getDetails() != null ? ImmutableList.copyOf((Iterable)Splitter.on((String)System.lineSeparator()).split((CharSequence)error.getDetails())) : null;
    }
}


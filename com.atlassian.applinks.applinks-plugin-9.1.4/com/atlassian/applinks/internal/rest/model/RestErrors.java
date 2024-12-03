/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.applinks.internal.rest.model;

import com.atlassian.applinks.internal.common.exception.DetailedErrors;
import com.atlassian.applinks.internal.rest.model.BaseRestEntity;
import com.atlassian.applinks.internal.rest.model.RestError;
import java.util.Collections;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.ws.rs.core.Response;

public class RestErrors
extends BaseRestEntity {
    public static final String STATUS = "status";
    public static final String ERRORS = "errors";

    public RestErrors(@Nonnull Response.Status status, @Nonnull Iterable<RestError> errors) {
        this.put(STATUS, (Object)Objects.requireNonNull(status, STATUS).getStatusCode());
        this.put(ERRORS, (Object)Objects.requireNonNull(errors, ERRORS));
    }

    public RestErrors(@Nonnull Response.Status status, @Nonnull DetailedErrors errors) {
        this.put(STATUS, (Object)Objects.requireNonNull(status, STATUS).getStatusCode());
        this.putIterableOf(ERRORS, Objects.requireNonNull(errors, ERRORS).getErrors(), RestError.class);
    }

    public RestErrors(@Nonnull Response.Status status, @Nonnull RestError error) {
        this(status, Collections.singletonList(error));
    }

    public RestErrors(@Nonnull Response.Status status, @Nonnull String summary) {
        this(status, new RestError(summary));
    }

    public RestErrors(@Nonnull Response.Status status, @Nonnull Exception javaError) {
        this(status, new RestError(javaError));
    }
}


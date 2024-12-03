/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.applinks.internal.common.rest.util;

import com.atlassian.applinks.internal.common.exception.DetailedError;
import com.atlassian.applinks.internal.common.exception.DetailedErrors;
import com.atlassian.applinks.internal.rest.model.RestError;
import com.atlassian.applinks.internal.rest.model.RestErrors;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.ws.rs.core.Response;

public final class RestErrorsFactory {
    private RestErrorsFactory() {
    }

    @Nonnull
    public static RestErrors fromException(@Nonnull Response.Status status, @Nonnull Exception exception) {
        Objects.requireNonNull(status, "status");
        Objects.requireNonNull(exception, "exception");
        if (exception instanceof DetailedErrors) {
            return new RestErrors(status, (DetailedErrors)DetailedErrors.class.cast(exception));
        }
        if (exception instanceof DetailedError) {
            return new RestErrors(status, new RestError((DetailedError)DetailedError.class.cast(exception)));
        }
        return new RestErrors(status, exception);
    }
}


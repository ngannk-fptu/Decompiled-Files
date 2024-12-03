/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseProtocolException
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.internal.common.net;

import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseProtocolException;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ResponseContentException
extends ResponseProtocolException {
    private final Response response;

    public ResponseContentException(@Nonnull Response response, @Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
        this.response = Objects.requireNonNull(response, "response");
    }

    public ResponseContentException(@Nonnull Response response, @Nullable String message) {
        this(response, message, null);
    }

    public ResponseContentException(@Nonnull Response response, @Nullable Throwable cause) {
        this(response, null, cause);
    }

    public ResponseContentException(@Nonnull Response response) {
        this(response, null, null);
    }

    @Nonnull
    public Response getResponse() {
        return this.response;
    }
}


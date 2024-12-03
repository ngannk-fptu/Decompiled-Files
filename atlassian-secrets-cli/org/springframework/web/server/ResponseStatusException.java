/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.server;

import org.springframework.core.NestedExceptionUtils;
import org.springframework.core.NestedRuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class ResponseStatusException
extends NestedRuntimeException {
    private final HttpStatus status;
    @Nullable
    private final String reason;

    public ResponseStatusException(HttpStatus status) {
        this(status, null, null);
    }

    public ResponseStatusException(HttpStatus status, @Nullable String reason) {
        this(status, reason, null);
    }

    public ResponseStatusException(HttpStatus status, @Nullable String reason, @Nullable Throwable cause) {
        super(null, cause);
        Assert.notNull((Object)status, "HttpStatus is required");
        this.status = status;
        this.reason = reason;
    }

    public HttpStatus getStatus() {
        return this.status;
    }

    @Nullable
    public String getReason() {
        return this.reason;
    }

    @Override
    public String getMessage() {
        String msg = "Response status " + (Object)((Object)this.status) + (this.reason != null ? " with reason \"" + this.reason + "\"" : "");
        return NestedExceptionUtils.buildMessage(msg, this.getCause());
    }
}


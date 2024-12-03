/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.NestedExceptionUtils
 *  org.springframework.core.NestedRuntimeException
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.web.server;

import java.util.Collections;
import java.util.Map;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.core.NestedRuntimeException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class ResponseStatusException
extends NestedRuntimeException {
    private final int status;
    @Nullable
    private final String reason;

    public ResponseStatusException(HttpStatus status) {
        this(status, null);
    }

    public ResponseStatusException(HttpStatus status, @Nullable String reason) {
        super("");
        Assert.notNull((Object)((Object)status), (String)"HttpStatus is required");
        this.status = status.value();
        this.reason = reason;
    }

    public ResponseStatusException(HttpStatus status, @Nullable String reason, @Nullable Throwable cause) {
        super(null, cause);
        Assert.notNull((Object)((Object)status), (String)"HttpStatus is required");
        this.status = status.value();
        this.reason = reason;
    }

    public ResponseStatusException(int rawStatusCode, @Nullable String reason, @Nullable Throwable cause) {
        super(null, cause);
        this.status = rawStatusCode;
        this.reason = reason;
    }

    public HttpStatus getStatus() {
        return HttpStatus.valueOf(this.status);
    }

    public int getRawStatusCode() {
        return this.status;
    }

    @Deprecated
    public Map<String, String> getHeaders() {
        return Collections.emptyMap();
    }

    public HttpHeaders getResponseHeaders() {
        Map<String, String> headers = this.getHeaders();
        if (headers.isEmpty()) {
            return HttpHeaders.EMPTY;
        }
        HttpHeaders result = new HttpHeaders();
        this.getHeaders().forEach(result::add);
        return result;
    }

    @Nullable
    public String getReason() {
        return this.reason;
    }

    public String getMessage() {
        HttpStatus code = HttpStatus.resolve(this.status);
        String msg = (code != null ? code : Integer.valueOf(this.status)) + (this.reason != null ? " \"" + this.reason + "\"" : "");
        return NestedExceptionUtils.buildMessage((String)msg, (Throwable)this.getCause());
    }
}


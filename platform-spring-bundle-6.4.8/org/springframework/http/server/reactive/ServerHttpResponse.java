/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.server.reactive;

import org.springframework.http.HttpStatus;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.http.ResponseCookie;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;

public interface ServerHttpResponse
extends ReactiveHttpOutputMessage {
    public boolean setStatusCode(@Nullable HttpStatus var1);

    @Nullable
    public HttpStatus getStatusCode();

    default public boolean setRawStatusCode(@Nullable Integer value) {
        if (value == null) {
            return this.setStatusCode(null);
        }
        HttpStatus httpStatus = HttpStatus.resolve(value);
        if (httpStatus == null) {
            throw new IllegalStateException("Unresolvable HttpStatus for general ServerHttpResponse: " + value);
        }
        return this.setStatusCode(httpStatus);
    }

    @Nullable
    default public Integer getRawStatusCode() {
        HttpStatus httpStatus = this.getStatusCode();
        return httpStatus != null ? Integer.valueOf(httpStatus.value()) : null;
    }

    public MultiValueMap<String, ResponseCookie> getCookies();

    public void addCookie(ResponseCookie var1);
}


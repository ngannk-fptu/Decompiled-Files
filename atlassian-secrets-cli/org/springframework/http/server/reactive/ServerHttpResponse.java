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

    public MultiValueMap<String, ResponseCookie> getCookies();

    public void addCookie(ResponseCookie var1);
}


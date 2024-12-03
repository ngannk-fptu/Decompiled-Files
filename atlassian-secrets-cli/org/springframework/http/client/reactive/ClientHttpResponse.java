/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.client.reactive;

import org.springframework.http.HttpStatus;
import org.springframework.http.ReactiveHttpInputMessage;
import org.springframework.http.ResponseCookie;
import org.springframework.util.MultiValueMap;

public interface ClientHttpResponse
extends ReactiveHttpInputMessage {
    public HttpStatus getStatusCode();

    public int getRawStatusCode();

    public MultiValueMap<String, ResponseCookie> getCookies();
}


/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.client;

import java.nio.charset.Charset;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.client.RestClientResponseException;

public abstract class HttpStatusCodeException
extends RestClientResponseException {
    private static final long serialVersionUID = 5696801857651587810L;
    private final HttpStatus statusCode;

    protected HttpStatusCodeException(HttpStatus statusCode) {
        this(statusCode, statusCode.name(), null, null, null);
    }

    protected HttpStatusCodeException(HttpStatus statusCode, String statusText) {
        this(statusCode, statusText, null, null, null);
    }

    protected HttpStatusCodeException(HttpStatus statusCode, String statusText, @Nullable byte[] responseBody, @Nullable Charset responseCharset) {
        this(statusCode, statusText, null, responseBody, responseCharset);
    }

    protected HttpStatusCodeException(HttpStatus statusCode, String statusText, @Nullable HttpHeaders responseHeaders, @Nullable byte[] responseBody, @Nullable Charset responseCharset) {
        super(statusCode.value() + " " + statusText, statusCode.value(), statusText, responseHeaders, responseBody, responseCharset);
        this.statusCode = statusCode;
    }

    public HttpStatus getStatusCode() {
        return this.statusCode;
    }
}


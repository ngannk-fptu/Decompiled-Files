/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.client;

import java.nio.charset.Charset;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;

public class HttpClientErrorException
extends HttpStatusCodeException {
    private static final long serialVersionUID = 5177019431887513952L;

    public HttpClientErrorException(HttpStatus statusCode) {
        super(statusCode);
    }

    public HttpClientErrorException(HttpStatus statusCode, String statusText) {
        super(statusCode, statusText);
    }

    public HttpClientErrorException(HttpStatus statusCode, String statusText, @Nullable byte[] responseBody, @Nullable Charset responseCharset) {
        super(statusCode, statusText, responseBody, responseCharset);
    }

    public HttpClientErrorException(HttpStatus statusCode, String statusText, @Nullable HttpHeaders responseHeaders, @Nullable byte[] responseBody, @Nullable Charset responseCharset) {
        super(statusCode, statusText, responseHeaders, responseBody, responseCharset);
    }
}


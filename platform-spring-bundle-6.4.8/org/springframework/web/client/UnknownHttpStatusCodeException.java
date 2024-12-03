/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.client;

import java.nio.charset.Charset;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.web.client.RestClientResponseException;

public class UnknownHttpStatusCodeException
extends RestClientResponseException {
    private static final long serialVersionUID = 7103980251635005491L;

    public UnknownHttpStatusCodeException(int rawStatusCode, String statusText, @Nullable HttpHeaders responseHeaders, @Nullable byte[] responseBody, @Nullable Charset responseCharset) {
        this("Unknown status code [" + rawStatusCode + "] " + statusText, rawStatusCode, statusText, responseHeaders, responseBody, responseCharset);
    }

    public UnknownHttpStatusCodeException(String message, int rawStatusCode, String statusText, @Nullable HttpHeaders responseHeaders, @Nullable byte[] responseBody, @Nullable Charset responseCharset) {
        super(message, rawStatusCode, statusText, responseHeaders, responseBody, responseCharset);
    }
}


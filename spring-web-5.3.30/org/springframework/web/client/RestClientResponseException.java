/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.web.client;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.web.client.RestClientException;

public class RestClientResponseException
extends RestClientException {
    private static final long serialVersionUID = -8803556342728481792L;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final int rawStatusCode;
    private final String statusText;
    private final byte[] responseBody;
    @Nullable
    private final HttpHeaders responseHeaders;
    @Nullable
    private final String responseCharset;

    public RestClientResponseException(String message, int statusCode, String statusText, @Nullable HttpHeaders responseHeaders, @Nullable byte[] responseBody, @Nullable Charset responseCharset) {
        super(message);
        this.rawStatusCode = statusCode;
        this.statusText = statusText;
        this.responseHeaders = responseHeaders;
        this.responseBody = responseBody != null ? responseBody : new byte[]{};
        this.responseCharset = responseCharset != null ? responseCharset.name() : null;
    }

    public int getRawStatusCode() {
        return this.rawStatusCode;
    }

    public String getStatusText() {
        return this.statusText;
    }

    @Nullable
    public HttpHeaders getResponseHeaders() {
        return this.responseHeaders;
    }

    public byte[] getResponseBodyAsByteArray() {
        return this.responseBody;
    }

    public String getResponseBodyAsString() {
        return this.getResponseBodyAsString(DEFAULT_CHARSET);
    }

    public String getResponseBodyAsString(Charset fallbackCharset) {
        if (this.responseCharset == null) {
            return new String(this.responseBody, fallbackCharset);
        }
        try {
            return new String(this.responseBody, this.responseCharset);
        }
        catch (UnsupportedEncodingException ex) {
            throw new IllegalStateException(ex);
        }
    }
}


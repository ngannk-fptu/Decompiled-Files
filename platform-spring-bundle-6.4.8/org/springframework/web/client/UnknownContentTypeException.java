/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.client;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.web.client.RestClientException;

public class UnknownContentTypeException
extends RestClientException {
    private static final long serialVersionUID = 2759516676367274084L;
    private final Type targetType;
    private final MediaType contentType;
    private final int rawStatusCode;
    private final String statusText;
    private final byte[] responseBody;
    private final HttpHeaders responseHeaders;

    public UnknownContentTypeException(Type targetType, MediaType contentType, int statusCode, String statusText, HttpHeaders responseHeaders, byte[] responseBody) {
        super("Could not extract response: no suitable HttpMessageConverter found for response type [" + targetType + "] and content type [" + contentType + "]");
        this.targetType = targetType;
        this.contentType = contentType;
        this.rawStatusCode = statusCode;
        this.statusText = statusText;
        this.responseHeaders = responseHeaders;
        this.responseBody = responseBody;
    }

    public Type getTargetType() {
        return this.targetType;
    }

    public MediaType getContentType() {
        return this.contentType;
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

    public byte[] getResponseBody() {
        return this.responseBody;
    }

    public String getResponseBodyAsString() {
        return new String(this.responseBody, this.contentType.getCharset() != null ? this.contentType.getCharset() : StandardCharsets.UTF_8);
    }
}


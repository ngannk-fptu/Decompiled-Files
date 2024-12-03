/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.web;

import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.web.HttpMediaTypeException;

public class HttpMediaTypeNotSupportedException
extends HttpMediaTypeException {
    @Nullable
    private final MediaType contentType;

    public HttpMediaTypeNotSupportedException(String message) {
        super(message);
        this.contentType = null;
    }

    public HttpMediaTypeNotSupportedException(@Nullable MediaType contentType, List<MediaType> supportedMediaTypes) {
        this(contentType, supportedMediaTypes, "Content type '" + (contentType != null ? contentType : "") + "' not supported");
    }

    public HttpMediaTypeNotSupportedException(@Nullable MediaType contentType, List<MediaType> supportedMediaTypes, String msg) {
        super(msg, supportedMediaTypes);
        this.contentType = contentType;
    }

    @Nullable
    public MediaType getContentType() {
        return this.contentType;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.server;

import java.util.Collections;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.web.server.ResponseStatusException;

public class UnsupportedMediaTypeStatusException
extends ResponseStatusException {
    @Nullable
    private final MediaType contentType;
    private final List<MediaType> supportedMediaTypes;

    public UnsupportedMediaTypeStatusException(@Nullable String reason) {
        super(HttpStatus.UNSUPPORTED_MEDIA_TYPE, reason);
        this.contentType = null;
        this.supportedMediaTypes = Collections.emptyList();
    }

    public UnsupportedMediaTypeStatusException(@Nullable MediaType contentType, List<MediaType> supportedMediaTypes) {
        super(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Content type '" + (contentType != null ? contentType : "") + "' not supported");
        this.contentType = contentType;
        this.supportedMediaTypes = Collections.unmodifiableList(supportedMediaTypes);
    }

    @Nullable
    public MediaType getContentType() {
        return this.contentType;
    }

    public List<MediaType> getSupportedMediaTypes() {
        return this.supportedMediaTypes;
    }
}


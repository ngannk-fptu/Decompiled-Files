/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.ResolvableType
 *  org.springframework.lang.Nullable
 *  org.springframework.util.CollectionUtils
 */
package org.springframework.web.server;

import java.util.Collections;
import java.util.List;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ResponseStatusException;

public class UnsupportedMediaTypeStatusException
extends ResponseStatusException {
    @Nullable
    private final MediaType contentType;
    private final List<MediaType> supportedMediaTypes;
    @Nullable
    private final ResolvableType bodyType;
    @Nullable
    private final HttpMethod method;

    public UnsupportedMediaTypeStatusException(@Nullable String reason) {
        super(HttpStatus.UNSUPPORTED_MEDIA_TYPE, reason);
        this.contentType = null;
        this.supportedMediaTypes = Collections.emptyList();
        this.bodyType = null;
        this.method = null;
    }

    public UnsupportedMediaTypeStatusException(@Nullable MediaType contentType, List<MediaType> supportedTypes) {
        this(contentType, supportedTypes, null, null);
    }

    public UnsupportedMediaTypeStatusException(@Nullable MediaType contentType, List<MediaType> supportedTypes, @Nullable ResolvableType bodyType) {
        this(contentType, supportedTypes, bodyType, null);
    }

    public UnsupportedMediaTypeStatusException(@Nullable MediaType contentType, List<MediaType> supportedTypes, @Nullable HttpMethod method) {
        this(contentType, supportedTypes, null, method);
    }

    public UnsupportedMediaTypeStatusException(@Nullable MediaType contentType, List<MediaType> supportedTypes, @Nullable ResolvableType bodyType, @Nullable HttpMethod method) {
        super(HttpStatus.UNSUPPORTED_MEDIA_TYPE, UnsupportedMediaTypeStatusException.initReason(contentType, bodyType));
        this.contentType = contentType;
        this.supportedMediaTypes = Collections.unmodifiableList(supportedTypes);
        this.bodyType = bodyType;
        this.method = method;
    }

    private static String initReason(@Nullable MediaType contentType, @Nullable ResolvableType bodyType) {
        return "Content type '" + (contentType != null ? contentType : "") + "' not supported" + (bodyType != null ? " for bodyType=" + bodyType.toString() : "");
    }

    @Nullable
    public MediaType getContentType() {
        return this.contentType;
    }

    public List<MediaType> getSupportedMediaTypes() {
        return this.supportedMediaTypes;
    }

    @Nullable
    public ResolvableType getBodyType() {
        return this.bodyType;
    }

    @Override
    public HttpHeaders getResponseHeaders() {
        if (HttpMethod.PATCH != this.method || CollectionUtils.isEmpty(this.supportedMediaTypes)) {
            return HttpHeaders.EMPTY;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setAcceptPatch(this.supportedMediaTypes);
        return headers;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.server;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ResponseStatusException;

public class NotAcceptableStatusException
extends ResponseStatusException {
    private final List<MediaType> supportedMediaTypes;

    public NotAcceptableStatusException(String reason) {
        super(HttpStatus.NOT_ACCEPTABLE, reason);
        this.supportedMediaTypes = Collections.emptyList();
    }

    public NotAcceptableStatusException(List<MediaType> supportedMediaTypes) {
        super(HttpStatus.NOT_ACCEPTABLE, "Could not find acceptable representation");
        this.supportedMediaTypes = Collections.unmodifiableList(supportedMediaTypes);
    }

    @Override
    public Map<String, String> getHeaders() {
        return this.getResponseHeaders().toSingleValueMap();
    }

    @Override
    public HttpHeaders getResponseHeaders() {
        if (CollectionUtils.isEmpty(this.supportedMediaTypes)) {
            return HttpHeaders.EMPTY;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(this.supportedMediaTypes);
        return headers;
    }

    public List<MediaType> getSupportedMediaTypes() {
        return this.supportedMediaTypes;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.server;

import java.util.Collections;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    public List<MediaType> getSupportedMediaTypes() {
        return this.supportedMediaTypes;
    }
}


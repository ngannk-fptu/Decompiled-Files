/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 */
package org.springframework.web;

import java.util.Collections;
import java.util.List;
import javax.servlet.ServletException;
import org.springframework.http.MediaType;

public abstract class HttpMediaTypeException
extends ServletException {
    private final List<MediaType> supportedMediaTypes;

    protected HttpMediaTypeException(String message) {
        super(message);
        this.supportedMediaTypes = Collections.emptyList();
    }

    protected HttpMediaTypeException(String message, List<MediaType> supportedMediaTypes) {
        super(message);
        this.supportedMediaTypes = Collections.unmodifiableList(supportedMediaTypes);
    }

    public List<MediaType> getSupportedMediaTypes() {
        return this.supportedMediaTypes;
    }
}


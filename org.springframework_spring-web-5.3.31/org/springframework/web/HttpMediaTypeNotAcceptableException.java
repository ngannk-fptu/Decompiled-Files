/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web;

import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.HttpMediaTypeException;

public class HttpMediaTypeNotAcceptableException
extends HttpMediaTypeException {
    public HttpMediaTypeNotAcceptableException(String message) {
        super(message);
    }

    public HttpMediaTypeNotAcceptableException(List<MediaType> supportedMediaTypes) {
        super("Could not find acceptable representation", supportedMediaTypes);
    }
}


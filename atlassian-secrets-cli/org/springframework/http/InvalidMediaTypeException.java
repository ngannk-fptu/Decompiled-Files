/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http;

import org.springframework.util.InvalidMimeTypeException;

public class InvalidMediaTypeException
extends IllegalArgumentException {
    private String mediaType;

    public InvalidMediaTypeException(String mediaType, String message) {
        super("Invalid media type \"" + mediaType + "\": " + message);
        this.mediaType = mediaType;
    }

    InvalidMediaTypeException(InvalidMimeTypeException ex) {
        super(ex.getMessage(), ex);
        this.mediaType = ex.getMimeType();
    }

    public String getMediaType() {
        return this.mediaType;
    }
}


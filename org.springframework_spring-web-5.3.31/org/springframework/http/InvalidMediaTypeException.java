/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.InvalidMimeTypeException
 */
package org.springframework.http;

import org.springframework.util.InvalidMimeTypeException;

public class InvalidMediaTypeException
extends IllegalArgumentException {
    private final String mediaType;

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


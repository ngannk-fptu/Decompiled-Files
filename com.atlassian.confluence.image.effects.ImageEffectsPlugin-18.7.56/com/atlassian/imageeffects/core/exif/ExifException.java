/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.imageeffects.core.exif;

public class ExifException
extends RuntimeException {
    public ExifException(String message) {
        this(message, null);
    }

    public ExifException(String message, Throwable cause) {
        super(message, cause);
    }
}


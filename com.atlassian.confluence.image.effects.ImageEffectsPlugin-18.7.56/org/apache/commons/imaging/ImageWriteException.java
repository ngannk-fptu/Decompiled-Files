/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging;

import org.apache.commons.imaging.ImagingException;

public class ImageWriteException
extends ImagingException {
    private static final long serialVersionUID = -1L;

    public ImageWriteException(String message) {
        super(message);
    }

    public ImageWriteException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImageWriteException(String message, Object data) {
        super(message + ": " + data + " (" + ImageWriteException.getType(data) + ")");
    }

    private static String getType(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof Object[]) {
            return "[Object[]: " + ((Object[])value).length + "]";
        }
        if (value instanceof char[]) {
            return "[char[]: " + ((char[])value).length + "]";
        }
        if (value instanceof byte[]) {
            return "[byte[]: " + ((byte[])value).length + "]";
        }
        if (value instanceof short[]) {
            return "[short[]: " + ((short[])value).length + "]";
        }
        if (value instanceof int[]) {
            return "[int[]: " + ((int[])value).length + "]";
        }
        if (value instanceof long[]) {
            return "[long[]: " + ((long[])value).length + "]";
        }
        if (value instanceof float[]) {
            return "[float[]: " + ((float[])value).length + "]";
        }
        if (value instanceof double[]) {
            return "[double[]: " + ((double[])value).length + "]";
        }
        if (value instanceof boolean[]) {
            return "[boolean[]: " + ((boolean[])value).length + "]";
        }
        return value.getClass().getName();
    }
}


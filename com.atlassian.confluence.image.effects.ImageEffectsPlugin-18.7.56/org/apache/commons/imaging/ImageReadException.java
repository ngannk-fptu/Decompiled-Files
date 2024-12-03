/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging;

import org.apache.commons.imaging.ImagingException;

public class ImageReadException
extends ImagingException {
    private static final long serialVersionUID = -1L;

    public ImageReadException(String message) {
        super(message);
    }

    public ImageReadException(String message, Throwable cause) {
        super(message, cause);
    }
}


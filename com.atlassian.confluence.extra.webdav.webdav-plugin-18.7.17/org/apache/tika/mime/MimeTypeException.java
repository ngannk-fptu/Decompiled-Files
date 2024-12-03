/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.mime;

import org.apache.tika.exception.TikaException;

public class MimeTypeException
extends TikaException {
    public MimeTypeException(String message) {
        super(message);
    }

    public MimeTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}


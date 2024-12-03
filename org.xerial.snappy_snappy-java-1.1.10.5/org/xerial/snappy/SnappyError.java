/*
 * Decompiled with CFR 0.152.
 */
package org.xerial.snappy;

import org.xerial.snappy.SnappyErrorCode;

public class SnappyError
extends Error {
    private static final long serialVersionUID = 1L;
    public final SnappyErrorCode errorCode;

    public SnappyError(SnappyErrorCode snappyErrorCode) {
        this.errorCode = snappyErrorCode;
    }

    public SnappyError(SnappyErrorCode snappyErrorCode, Error error) {
        super(error);
        this.errorCode = snappyErrorCode;
    }

    public SnappyError(SnappyErrorCode snappyErrorCode, String string) {
        super(string);
        this.errorCode = snappyErrorCode;
    }

    @Override
    public String getMessage() {
        return String.format("[%s] %s", this.errorCode.name(), super.getMessage());
    }
}


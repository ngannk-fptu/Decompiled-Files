/*
 * Decompiled with CFR 0.152.
 */
package org.xerial.snappy;

import java.io.IOException;
import org.xerial.snappy.SnappyErrorCode;

public class SnappyIOException
extends IOException {
    private final SnappyErrorCode errorCode;

    public SnappyIOException(SnappyErrorCode snappyErrorCode, String string) {
        super(string);
        this.errorCode = snappyErrorCode;
    }

    @Override
    public String getMessage() {
        return String.format("[%s] %s", this.errorCode.name(), super.getMessage());
    }

    public SnappyErrorCode getErrorCode() {
        return this.errorCode;
    }
}


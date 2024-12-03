/*
 * Decompiled with CFR 0.152.
 */
package org.xerial.snappy;

import org.xerial.snappy.SnappyErrorCode;

@Deprecated
public class SnappyException
extends Exception {
    private static final long serialVersionUID = 1L;
    public final SnappyErrorCode errorCode;

    public SnappyException(int n) {
        this(SnappyErrorCode.getErrorCode(n));
    }

    public SnappyException(SnappyErrorCode snappyErrorCode) {
        this.errorCode = snappyErrorCode;
    }

    public SnappyException(SnappyErrorCode snappyErrorCode, Exception exception) {
        super(exception);
        this.errorCode = snappyErrorCode;
    }

    public SnappyException(SnappyErrorCode snappyErrorCode, String string) {
        super(string);
        this.errorCode = snappyErrorCode;
    }

    public SnappyErrorCode getErrorCode() {
        return this.errorCode;
    }

    public static void throwException(int n) throws SnappyException {
        throw new SnappyException(n);
    }

    @Override
    public String getMessage() {
        return String.format("[%s] %s", this.errorCode.name(), super.getMessage());
    }
}


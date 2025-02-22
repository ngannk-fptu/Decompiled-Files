/*
 * Decompiled with CFR 0.152.
 */
package org.xerial.snappy;

public enum SnappyErrorCode {
    UNKNOWN(0),
    FAILED_TO_LOAD_NATIVE_LIBRARY(1),
    PARSING_ERROR(2),
    NOT_A_DIRECT_BUFFER(3),
    OUT_OF_MEMORY(4),
    FAILED_TO_UNCOMPRESS(5),
    EMPTY_INPUT(6),
    INCOMPATIBLE_VERSION(7),
    INVALID_CHUNK_SIZE(8),
    UNSUPPORTED_PLATFORM(9),
    TOO_LARGE_INPUT(10);

    public final int id;

    private SnappyErrorCode(int n2) {
        this.id = n2;
    }

    public static SnappyErrorCode getErrorCode(int n) {
        for (SnappyErrorCode snappyErrorCode : SnappyErrorCode.values()) {
            if (snappyErrorCode.id != n) continue;
            return snappyErrorCode;
        }
        return UNKNOWN;
    }

    public static String getErrorMessage(int n) {
        return SnappyErrorCode.getErrorCode(n).name();
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.internal.eventstreaming;

public enum HeaderType {
    TRUE(0),
    FALSE(1),
    BYTE(2),
    SHORT(3),
    INTEGER(4),
    LONG(5),
    BYTE_ARRAY(6),
    STRING(7),
    TIMESTAMP(8),
    UUID(9);

    final byte headerTypeId;

    private HeaderType(int headerTypeId) {
        this.headerTypeId = (byte)headerTypeId;
    }

    static HeaderType fromTypeId(byte headerTypeId) {
        switch (headerTypeId) {
            case 0: {
                return TRUE;
            }
            case 1: {
                return FALSE;
            }
            case 2: {
                return BYTE;
            }
            case 3: {
                return SHORT;
            }
            case 4: {
                return INTEGER;
            }
            case 5: {
                return LONG;
            }
            case 6: {
                return BYTE_ARRAY;
            }
            case 7: {
                return STRING;
            }
            case 8: {
                return TIMESTAMP;
            }
            case 9: {
                return UUID;
            }
        }
        throw new IllegalArgumentException("Got unknown headerTypeId " + headerTypeId);
    }
}


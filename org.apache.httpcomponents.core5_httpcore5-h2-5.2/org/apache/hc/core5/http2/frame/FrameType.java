/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http2.frame;

public enum FrameType {
    DATA(0),
    HEADERS(1),
    PRIORITY(2),
    RST_STREAM(3),
    SETTINGS(4),
    PUSH_PROMISE(5),
    PING(6),
    GOAWAY(7),
    WINDOW_UPDATE(8),
    CONTINUATION(9);

    int value;
    private static final FrameType[] LOOKUP_TABLE;

    private FrameType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static FrameType valueOf(int value) {
        if (value < 0 || value >= LOOKUP_TABLE.length) {
            return null;
        }
        return LOOKUP_TABLE[value];
    }

    public static String toString(int value) {
        if (value < 0 || value >= LOOKUP_TABLE.length) {
            return Integer.toString(value);
        }
        return LOOKUP_TABLE[value].name();
    }

    static {
        LOOKUP_TABLE = new FrameType[10];
        FrameType[] frameTypeArray = FrameType.values();
        int n = frameTypeArray.length;
        for (int i = 0; i < n; ++i) {
            FrameType frameType;
            FrameType.LOOKUP_TABLE[frameType.value] = frameType = frameTypeArray[i];
        }
    }
}


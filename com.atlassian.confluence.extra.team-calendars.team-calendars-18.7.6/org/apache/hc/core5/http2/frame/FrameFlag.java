/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http2.frame;

public enum FrameFlag {
    END_STREAM(1),
    ACK(1),
    END_HEADERS(4),
    PADDED(8),
    PRIORITY(32);

    final int value;

    private FrameFlag(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static int of(FrameFlag ... flags) {
        int value = 0;
        for (FrameFlag flag : flags) {
            value |= flag.value;
        }
        return value;
    }
}


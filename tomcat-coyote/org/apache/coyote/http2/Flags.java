/*
 * Decompiled with CFR 0.152.
 */
package org.apache.coyote.http2;

class Flags {
    private Flags() {
    }

    static boolean isEndOfStream(int flags) {
        return (flags & 1) != 0;
    }

    static boolean isAck(int flags) {
        return (flags & 1) != 0;
    }

    static boolean isEndOfHeaders(int flags) {
        return (flags & 4) != 0;
    }

    static boolean hasPadding(int flags) {
        return (flags & 8) != 0;
    }

    static boolean hasPriority(int flags) {
        return (flags & 0x20) != 0;
    }
}


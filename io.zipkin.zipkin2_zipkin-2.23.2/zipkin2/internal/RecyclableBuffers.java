/*
 * Decompiled with CFR 0.152.
 */
package zipkin2.internal;

public final class RecyclableBuffers {
    static final ThreadLocal<char[]> SHORT_STRING_BUFFER = new ThreadLocal();
    public static final int SHORT_STRING_LENGTH = 256;

    RecyclableBuffers() {
    }

    public static char[] shortStringBuffer() {
        char[] shortStringBuffer = SHORT_STRING_BUFFER.get();
        if (shortStringBuffer == null) {
            shortStringBuffer = new char[256];
            SHORT_STRING_BUFFER.set(shortStringBuffer);
        }
        return shortStringBuffer;
    }
}


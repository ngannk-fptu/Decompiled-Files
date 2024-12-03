/*
 * Decompiled with CFR 0.152.
 */
package brave.internal;

public final class RecyclableBuffers {
    private static final ThreadLocal<char[]> PARSE_BUFFER = new ThreadLocal();

    public static char[] parseBuffer() {
        char[] idBuffer = PARSE_BUFFER.get();
        if (idBuffer == null) {
            idBuffer = new char[68];
            PARSE_BUFFER.set(idBuffer);
        }
        return idBuffer;
    }

    private RecyclableBuffers() {
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.ri.typed;

public abstract class AsciiValueEncoder {
    protected static final int MIN_CHARS_WITHOUT_FLUSH = 64;

    protected AsciiValueEncoder() {
    }

    public final boolean bufferNeedsFlush(int freeChars) {
        return freeChars < 64;
    }

    public abstract boolean isCompleted();

    public abstract int encodeMore(char[] var1, int var2, int var3);

    public abstract int encodeMore(byte[] var1, int var2, int var3);
}


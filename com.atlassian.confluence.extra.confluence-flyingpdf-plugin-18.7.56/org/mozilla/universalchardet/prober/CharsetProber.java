/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.universalchardet.prober;

import java.nio.ByteBuffer;

public abstract class CharsetProber {
    public static final float SHORTCUT_THRESHOLD = 0.95f;
    public static final int ASCII_A = 97;
    public static final int ASCII_Z = 122;
    public static final int ASCII_A_CAPITAL = 65;
    public static final int ASCII_Z_CAPITAL = 90;
    public static final int ASCII_LT = 60;
    public static final int ASCII_GT = 62;
    public static final int ASCII_SP = 32;
    private boolean active = true;

    public abstract String getCharSetName();

    public abstract ProbingState handleData(byte[] var1, int var2, int var3);

    public abstract ProbingState getState();

    public abstract void reset();

    public abstract float getConfidence();

    public abstract void setOption();

    public ByteBuffer filterWithoutEnglishLetters(byte[] buf, int offset, int length) {
        int curPtr;
        ByteBuffer out = ByteBuffer.allocate(length);
        boolean meetMSB = false;
        int prevPtr = offset;
        int maxPtr = offset + length;
        for (curPtr = offset; curPtr < maxPtr; ++curPtr) {
            byte c = buf[curPtr];
            if (!this.isAscii(c)) {
                meetMSB = true;
                continue;
            }
            if (!this.isAsciiSymbol(c)) continue;
            if (meetMSB && curPtr > prevPtr) {
                out.put(buf, prevPtr, curPtr - prevPtr);
                out.put((byte)32);
                prevPtr = curPtr + 1;
                meetMSB = false;
                continue;
            }
            prevPtr = curPtr + 1;
        }
        if (meetMSB && curPtr > prevPtr) {
            out.put(buf, prevPtr, curPtr - prevPtr);
        }
        return out;
    }

    public ByteBuffer filterWithEnglishLetters(byte[] buf, int offset, int length) {
        int curPtr;
        ByteBuffer out = ByteBuffer.allocate(length);
        boolean isInTag = false;
        int prevPtr = offset;
        int maxPtr = offset + length;
        for (curPtr = offset; curPtr < maxPtr; ++curPtr) {
            byte c = buf[curPtr];
            if (c == 62) {
                isInTag = false;
            } else if (c == 60) {
                isInTag = true;
            }
            if (!this.isAscii(c) || !this.isAsciiSymbol(c)) continue;
            if (curPtr > prevPtr && !isInTag) {
                out.put(buf, prevPtr, curPtr - prevPtr);
                out.put((byte)32);
                prevPtr = curPtr + 1;
                continue;
            }
            prevPtr = curPtr + 1;
        }
        if (!isInTag && curPtr > prevPtr) {
            out.put(buf, prevPtr, curPtr - prevPtr);
        }
        return out;
    }

    private boolean isAscii(byte b) {
        return (b & 0x80) == 0;
    }

    private boolean isAsciiSymbol(byte b) {
        int c = b & 0xFF;
        return c < 65 || c > 90 && c < 97 || c > 122;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public static enum ProbingState {
        DETECTING,
        FOUND_IT,
        NOT_ME;

    }
}


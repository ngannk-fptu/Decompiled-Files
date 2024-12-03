/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.buf;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.StandardCharsets;

public class Utf8Decoder
extends CharsetDecoder {
    private static final int[] remainingBytes = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    private static final int[] remainingNumbers = new int[]{0, 4224, 401536, 29892736};
    private static final int[] lowerEncodingLimit = new int[]{-1, 128, 2048, 65536};

    public Utf8Decoder() {
        super(StandardCharsets.UTF_8, 1.0f, 1.0f);
    }

    @Override
    protected CoderResult decodeLoop(ByteBuffer in, CharBuffer out) {
        if (in.hasArray() && out.hasArray()) {
            return this.decodeHasArray(in, out);
        }
        return this.decodeNotHasArray(in, out);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private CoderResult decodeNotHasArray(ByteBuffer in, CharBuffer out) {
        int pos;
        int outRemaining = out.remaining();
        int limit = in.limit();
        try {
            for (pos = in.position(); pos < limit; ++pos) {
                if (outRemaining == 0) {
                    CoderResult coderResult = CoderResult.OVERFLOW;
                    return coderResult;
                }
                int jchar = in.get();
                if (jchar < 0) {
                    int tail = remainingBytes[jchar &= 0x7F];
                    if (tail == -1) {
                        CoderResult coderResult = CoderResult.malformedForLength(1);
                        return coderResult;
                    }
                    if (limit - pos < 1 + tail) {
                        CoderResult coderResult = CoderResult.UNDERFLOW;
                        return coderResult;
                    }
                    for (int i = 0; i < tail; ++i) {
                        int nextByte = in.get() & 0xFF;
                        if ((nextByte & 0xC0) != 128) {
                            CoderResult coderResult = CoderResult.malformedForLength(1 + i);
                            return coderResult;
                        }
                        jchar = (jchar << 6) + nextByte;
                    }
                    if ((jchar -= remainingNumbers[tail]) < lowerEncodingLimit[tail]) {
                        CoderResult coderResult = CoderResult.malformedForLength(1);
                        return coderResult;
                    }
                    pos += tail;
                }
                if (jchar >= 55296 && jchar <= 57343) {
                    CoderResult coderResult = CoderResult.unmappableForLength(3);
                    return coderResult;
                }
                if (jchar > 0x10FFFF) {
                    CoderResult coderResult = CoderResult.unmappableForLength(4);
                    return coderResult;
                }
                if (jchar <= 65535) {
                    out.put((char)jchar);
                    --outRemaining;
                    continue;
                }
                if (outRemaining < 2) {
                    CoderResult coderResult = CoderResult.OVERFLOW;
                    return coderResult;
                }
                out.put((char)((jchar >> 10) + 55232));
                out.put((char)((jchar & 0x3FF) + 56320));
                outRemaining -= 2;
            }
            CoderResult coderResult = CoderResult.UNDERFLOW;
            return coderResult;
        }
        finally {
            in.position(pos);
        }
    }

    private CoderResult decodeHasArray(ByteBuffer in, CharBuffer out) {
        int inIndex;
        int outRemaining = out.remaining();
        int pos = in.position();
        int limit = in.limit();
        byte[] bArr = in.array();
        char[] cArr = out.array();
        int inIndexLimit = limit + in.arrayOffset();
        int outIndex = out.position() + out.arrayOffset();
        for (inIndex = pos + in.arrayOffset(); inIndex < inIndexLimit && outRemaining > 0; ++inIndex) {
            int jchar = bArr[inIndex];
            if (jchar < 0) {
                int tail = remainingBytes[jchar &= 0x7F];
                if (tail == -1) {
                    in.position(inIndex - in.arrayOffset());
                    out.position(outIndex - out.arrayOffset());
                    return CoderResult.malformedForLength(1);
                }
                int tailAvailable = inIndexLimit - inIndex - 1;
                if (tailAvailable > 0) {
                    if (jchar > 65 && jchar < 96 && (bArr[inIndex + 1] & 0xC0) != 128) {
                        in.position(inIndex - in.arrayOffset());
                        out.position(outIndex - out.arrayOffset());
                        return CoderResult.malformedForLength(1);
                    }
                    if (jchar == 96 && (bArr[inIndex + 1] & 0xE0) != 160) {
                        in.position(inIndex - in.arrayOffset());
                        out.position(outIndex - out.arrayOffset());
                        return CoderResult.malformedForLength(1);
                    }
                    if (jchar > 96 && jchar < 109 && (bArr[inIndex + 1] & 0xC0) != 128) {
                        in.position(inIndex - in.arrayOffset());
                        out.position(outIndex - out.arrayOffset());
                        return CoderResult.malformedForLength(1);
                    }
                    if (jchar == 109 && (bArr[inIndex + 1] & 0xE0) != 128) {
                        in.position(inIndex - in.arrayOffset());
                        out.position(outIndex - out.arrayOffset());
                        return CoderResult.malformedForLength(1);
                    }
                    if (jchar > 109 && jchar < 112 && (bArr[inIndex + 1] & 0xC0) != 128) {
                        in.position(inIndex - in.arrayOffset());
                        out.position(outIndex - out.arrayOffset());
                        return CoderResult.malformedForLength(1);
                    }
                    if (jchar == 112 && ((bArr[inIndex + 1] & 0xFF) < 144 || (bArr[inIndex + 1] & 0xFF) > 191)) {
                        in.position(inIndex - in.arrayOffset());
                        out.position(outIndex - out.arrayOffset());
                        return CoderResult.malformedForLength(1);
                    }
                    if (jchar > 112 && jchar < 116 && (bArr[inIndex + 1] & 0xC0) != 128) {
                        in.position(inIndex - in.arrayOffset());
                        out.position(outIndex - out.arrayOffset());
                        return CoderResult.malformedForLength(1);
                    }
                    if (jchar == 116 && (bArr[inIndex + 1] & 0xF0) != 128) {
                        in.position(inIndex - in.arrayOffset());
                        out.position(outIndex - out.arrayOffset());
                        return CoderResult.malformedForLength(1);
                    }
                }
                if (tailAvailable > 1 && tail > 1 && (bArr[inIndex + 2] & 0xC0) != 128) {
                    in.position(inIndex - in.arrayOffset());
                    out.position(outIndex - out.arrayOffset());
                    return CoderResult.malformedForLength(2);
                }
                if (tailAvailable > 2 && tail > 2 && (bArr[inIndex + 3] & 0xC0) != 128) {
                    in.position(inIndex - in.arrayOffset());
                    out.position(outIndex - out.arrayOffset());
                    return CoderResult.malformedForLength(3);
                }
                if (tailAvailable < tail) break;
                for (int i = 0; i < tail; ++i) {
                    int nextByte = bArr[inIndex + i + 1] & 0xFF;
                    if ((nextByte & 0xC0) != 128) {
                        in.position(inIndex - in.arrayOffset());
                        out.position(outIndex - out.arrayOffset());
                        return CoderResult.malformedForLength(1 + i);
                    }
                    jchar = (jchar << 6) + nextByte;
                }
                if ((jchar -= remainingNumbers[tail]) < lowerEncodingLimit[tail]) {
                    in.position(inIndex - in.arrayOffset());
                    out.position(outIndex - out.arrayOffset());
                    return CoderResult.malformedForLength(1);
                }
                inIndex += tail;
            }
            if (jchar >= 55296 && jchar <= 57343) {
                return CoderResult.unmappableForLength(3);
            }
            if (jchar > 0x10FFFF) {
                return CoderResult.unmappableForLength(4);
            }
            if (jchar <= 65535) {
                cArr[outIndex++] = (char)jchar;
                --outRemaining;
                continue;
            }
            if (outRemaining < 2) {
                in.position((inIndex -= 3) - in.arrayOffset());
                out.position(outIndex - out.arrayOffset());
                return CoderResult.OVERFLOW;
            }
            cArr[outIndex++] = (char)((jchar >> 10) + 55232);
            cArr[outIndex++] = (char)((jchar & 0x3FF) + 56320);
            outRemaining -= 2;
        }
        in.position(inIndex - in.arrayOffset());
        out.position(outIndex - out.arrayOffset());
        return outRemaining == 0 && inIndex < inIndexLimit ? CoderResult.OVERFLOW : CoderResult.UNDERFLOW;
    }
}


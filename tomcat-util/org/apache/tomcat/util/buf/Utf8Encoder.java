/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.buf;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.StandardCharsets;

public class Utf8Encoder
extends CharsetEncoder {
    public Utf8Encoder() {
        super(StandardCharsets.UTF_8, 1.1f, 4.0f);
    }

    @Override
    protected CoderResult encodeLoop(CharBuffer in, ByteBuffer out) {
        if (in.hasArray() && out.hasArray()) {
            return this.encodeHasArray(in, out);
        }
        return this.encodeNotHasArray(in, out);
    }

    private CoderResult encodeHasArray(CharBuffer in, ByteBuffer out) {
        int outRemaining = out.remaining();
        int pos = in.position();
        int limit = in.limit();
        int x = pos;
        byte[] bArr = out.array();
        char[] cArr = in.array();
        int outPos = out.position();
        int rem = in.remaining();
        for (x = pos; x < pos + rem; ++x) {
            int jchar = cArr[x] & 0xFFFF;
            if (jchar <= 127) {
                if (outRemaining < 1) {
                    in.position(x);
                    out.position(outPos);
                    return CoderResult.OVERFLOW;
                }
                bArr[outPos++] = (byte)(jchar & 0xFF);
                --outRemaining;
            } else if (jchar <= 2047) {
                if (outRemaining < 2) {
                    in.position(x);
                    out.position(outPos);
                    return CoderResult.OVERFLOW;
                }
                bArr[outPos++] = (byte)(192 + (jchar >> 6 & 0x1F));
                bArr[outPos++] = (byte)(128 + (jchar & 0x3F));
                outRemaining -= 2;
            } else if (jchar >= 55296 && jchar <= 57343) {
                if (limit <= x + 1) {
                    in.position(x);
                    out.position(outPos);
                    return CoderResult.UNDERFLOW;
                }
                if (outRemaining < 4) {
                    in.position(x);
                    out.position(outPos);
                    return CoderResult.OVERFLOW;
                }
                if (jchar >= 56320) {
                    in.position(x);
                    out.position(outPos);
                    return CoderResult.malformedForLength(1);
                }
                int jchar2 = cArr[x + 1] & 0xFFFF;
                if (jchar2 < 56320) {
                    in.position(x);
                    out.position(outPos);
                    return CoderResult.malformedForLength(1);
                }
                int n = (jchar << 10) + jchar2 + -56613888;
                bArr[outPos++] = (byte)(240 + (n >> 18 & 7));
                bArr[outPos++] = (byte)(128 + (n >> 12 & 0x3F));
                bArr[outPos++] = (byte)(128 + (n >> 6 & 0x3F));
                bArr[outPos++] = (byte)(128 + (n & 0x3F));
                outRemaining -= 4;
                ++x;
            } else {
                if (outRemaining < 3) {
                    in.position(x);
                    out.position(outPos);
                    return CoderResult.OVERFLOW;
                }
                bArr[outPos++] = (byte)(224 + (jchar >> 12 & 0xF));
                bArr[outPos++] = (byte)(128 + (jchar >> 6 & 0x3F));
                bArr[outPos++] = (byte)(128 + (jchar & 0x3F));
                outRemaining -= 3;
            }
            if (outRemaining != 0) continue;
            in.position(x + 1);
            out.position(outPos);
            if (x + 1 == limit) {
                return CoderResult.UNDERFLOW;
            }
            return CoderResult.OVERFLOW;
        }
        if (rem != 0) {
            in.position(x);
            out.position(outPos);
        }
        return CoderResult.UNDERFLOW;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private CoderResult encodeNotHasArray(CharBuffer in, ByteBuffer out) {
        int pos;
        int outRemaining = out.remaining();
        int limit = in.limit();
        try {
            for (pos = in.position(); pos < limit; ++pos) {
                if (outRemaining == 0) {
                    CoderResult coderResult = CoderResult.OVERFLOW;
                    return coderResult;
                }
                int jchar = in.get() & 0xFFFF;
                if (jchar <= 127) {
                    if (outRemaining < 1) {
                        CoderResult coderResult = CoderResult.OVERFLOW;
                        return coderResult;
                    }
                    out.put((byte)jchar);
                    --outRemaining;
                    continue;
                }
                if (jchar <= 2047) {
                    if (outRemaining < 2) {
                        CoderResult coderResult = CoderResult.OVERFLOW;
                        return coderResult;
                    }
                    out.put((byte)(192 + (jchar >> 6 & 0x1F)));
                    out.put((byte)(128 + (jchar & 0x3F)));
                    outRemaining -= 2;
                    continue;
                }
                if (jchar >= 55296 && jchar <= 57343) {
                    if (limit <= pos + 1) {
                        CoderResult coderResult = CoderResult.UNDERFLOW;
                        return coderResult;
                    }
                    if (outRemaining < 4) {
                        CoderResult coderResult = CoderResult.OVERFLOW;
                        return coderResult;
                    }
                    if (jchar >= 56320) {
                        CoderResult coderResult = CoderResult.malformedForLength(1);
                        return coderResult;
                    }
                    int jchar2 = in.get() & 0xFFFF;
                    if (jchar2 < 56320) {
                        CoderResult coderResult = CoderResult.malformedForLength(1);
                        return coderResult;
                    }
                    int n = (jchar << 10) + jchar2 + -56613888;
                    out.put((byte)(240 + (n >> 18 & 7)));
                    out.put((byte)(128 + (n >> 12 & 0x3F)));
                    out.put((byte)(128 + (n >> 6 & 0x3F)));
                    out.put((byte)(128 + (n & 0x3F)));
                    outRemaining -= 4;
                    ++pos;
                    continue;
                }
                if (outRemaining < 3) {
                    CoderResult coderResult = CoderResult.OVERFLOW;
                    return coderResult;
                }
                out.put((byte)(224 + (jchar >> 12 & 0xF)));
                out.put((byte)(128 + (jchar >> 6 & 0x3F)));
                out.put((byte)(128 + (jchar & 0x3F)));
                outRemaining -= 3;
            }
        }
        finally {
            in.position(pos);
        }
        return CoderResult.UNDERFLOW;
    }
}


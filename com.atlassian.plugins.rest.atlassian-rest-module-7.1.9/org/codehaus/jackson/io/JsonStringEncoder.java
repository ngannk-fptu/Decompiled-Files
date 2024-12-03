/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.io;

import java.lang.ref.SoftReference;
import org.codehaus.jackson.util.ByteArrayBuilder;
import org.codehaus.jackson.util.CharTypes;
import org.codehaus.jackson.util.TextBuffer;

public final class JsonStringEncoder {
    private static final char[] HEX_CHARS = CharTypes.copyHexChars();
    private static final byte[] HEX_BYTES = CharTypes.copyHexBytes();
    private static final int SURR1_FIRST = 55296;
    private static final int SURR1_LAST = 56319;
    private static final int SURR2_FIRST = 56320;
    private static final int SURR2_LAST = 57343;
    private static final int INT_BACKSLASH = 92;
    private static final int INT_U = 117;
    private static final int INT_0 = 48;
    protected static final ThreadLocal<SoftReference<JsonStringEncoder>> _threadEncoder = new ThreadLocal();
    protected TextBuffer _textBuffer;
    protected ByteArrayBuilder _byteBuilder;
    protected final char[] _quoteBuffer = new char[6];

    public JsonStringEncoder() {
        this._quoteBuffer[0] = 92;
        this._quoteBuffer[2] = 48;
        this._quoteBuffer[3] = 48;
    }

    public static JsonStringEncoder getInstance() {
        JsonStringEncoder enc;
        SoftReference<JsonStringEncoder> ref = _threadEncoder.get();
        JsonStringEncoder jsonStringEncoder = enc = ref == null ? null : ref.get();
        if (enc == null) {
            enc = new JsonStringEncoder();
            _threadEncoder.set(new SoftReference<JsonStringEncoder>(enc));
        }
        return enc;
    }

    public char[] quoteAsString(String input) {
        TextBuffer textBuffer = this._textBuffer;
        if (textBuffer == null) {
            this._textBuffer = textBuffer = new TextBuffer(null);
        }
        char[] outputBuffer = textBuffer.emptyAndGetCurrentSegment();
        int[] escCodes = CharTypes.get7BitOutputEscapes();
        int escCodeCount = escCodes.length;
        int inPtr = 0;
        int inputLen = input.length();
        int outPtr = 0;
        block0: while (inPtr < inputLen) {
            char d;
            int escCode;
            int length;
            char c;
            while ((c = input.charAt(inPtr)) >= escCodeCount || escCodes[c] == 0) {
                if (outPtr >= outputBuffer.length) {
                    outputBuffer = textBuffer.finishCurrentSegment();
                    outPtr = 0;
                }
                outputBuffer[outPtr++] = c;
                if (++inPtr < inputLen) continue;
                break block0;
            }
            int n = length = (escCode = escCodes[d = input.charAt(inPtr++)]) < 0 ? this._appendNumericEscape(d, this._quoteBuffer) : this._appendNamedEscape(escCode, this._quoteBuffer);
            if (outPtr + length > outputBuffer.length) {
                int first = outputBuffer.length - outPtr;
                if (first > 0) {
                    System.arraycopy(this._quoteBuffer, 0, outputBuffer, outPtr, first);
                }
                outputBuffer = textBuffer.finishCurrentSegment();
                int second = length - first;
                System.arraycopy(this._quoteBuffer, first, outputBuffer, 0, second);
                outPtr = second;
                continue;
            }
            System.arraycopy(this._quoteBuffer, 0, outputBuffer, outPtr, length);
            outPtr += length;
        }
        textBuffer.setCurrentLength(outPtr);
        return textBuffer.contentsAsArray();
    }

    public byte[] quoteAsUTF8(String text) {
        ByteArrayBuilder byteBuilder = this._byteBuilder;
        if (byteBuilder == null) {
            this._byteBuilder = byteBuilder = new ByteArrayBuilder(null);
        }
        int inputPtr = 0;
        int inputEnd = text.length();
        int outputPtr = 0;
        byte[] outputBuffer = byteBuilder.resetAndGetFirstSegment();
        block0: while (inputPtr < inputEnd) {
            int ch;
            int[] escCodes = CharTypes.get7BitOutputEscapes();
            while ((ch = text.charAt(inputPtr)) <= 127 && escCodes[ch] == 0) {
                if (outputPtr >= outputBuffer.length) {
                    outputBuffer = byteBuilder.finishCurrentSegment();
                    outputPtr = 0;
                }
                outputBuffer[outputPtr++] = (byte)ch;
                if (++inputPtr < inputEnd) continue;
                break block0;
            }
            if (outputPtr >= outputBuffer.length) {
                outputBuffer = byteBuilder.finishCurrentSegment();
                outputPtr = 0;
            }
            if ((ch = text.charAt(inputPtr++)) <= 127) {
                int escape = escCodes[ch];
                outputPtr = this._appendByteEscape(ch, escape, byteBuilder, outputPtr);
                outputBuffer = byteBuilder.getCurrentSegment();
                continue;
            }
            if (ch <= 2047) {
                outputBuffer[outputPtr++] = (byte)(0xC0 | ch >> 6);
                ch = 0x80 | ch & 0x3F;
            } else if (ch < 55296 || ch > 57343) {
                outputBuffer[outputPtr++] = (byte)(0xE0 | ch >> 12);
                if (outputPtr >= outputBuffer.length) {
                    outputBuffer = byteBuilder.finishCurrentSegment();
                    outputPtr = 0;
                }
                outputBuffer[outputPtr++] = (byte)(0x80 | ch >> 6 & 0x3F);
                ch = 0x80 | ch & 0x3F;
            } else {
                if (ch > 56319) {
                    this._throwIllegalSurrogate(ch);
                }
                if (inputPtr >= inputEnd) {
                    this._throwIllegalSurrogate(ch);
                }
                if ((ch = this._convertSurrogate(ch, text.charAt(inputPtr++))) > 0x10FFFF) {
                    this._throwIllegalSurrogate(ch);
                }
                outputBuffer[outputPtr++] = (byte)(0xF0 | ch >> 18);
                if (outputPtr >= outputBuffer.length) {
                    outputBuffer = byteBuilder.finishCurrentSegment();
                    outputPtr = 0;
                }
                outputBuffer[outputPtr++] = (byte)(0x80 | ch >> 12 & 0x3F);
                if (outputPtr >= outputBuffer.length) {
                    outputBuffer = byteBuilder.finishCurrentSegment();
                    outputPtr = 0;
                }
                outputBuffer[outputPtr++] = (byte)(0x80 | ch >> 6 & 0x3F);
                ch = 0x80 | ch & 0x3F;
            }
            if (outputPtr >= outputBuffer.length) {
                outputBuffer = byteBuilder.finishCurrentSegment();
                outputPtr = 0;
            }
            outputBuffer[outputPtr++] = (byte)ch;
        }
        return this._byteBuilder.completeAndCoalesce(outputPtr);
    }

    public byte[] encodeAsUTF8(String text) {
        ByteArrayBuilder byteBuilder = this._byteBuilder;
        if (byteBuilder == null) {
            this._byteBuilder = byteBuilder = new ByteArrayBuilder(null);
        }
        int inputPtr = 0;
        int inputEnd = text.length();
        int outputPtr = 0;
        byte[] outputBuffer = byteBuilder.resetAndGetFirstSegment();
        int outputEnd = outputBuffer.length;
        block0: while (inputPtr < inputEnd) {
            int c = text.charAt(inputPtr++);
            while (c <= 127) {
                if (outputPtr >= outputEnd) {
                    outputBuffer = byteBuilder.finishCurrentSegment();
                    outputEnd = outputBuffer.length;
                    outputPtr = 0;
                }
                outputBuffer[outputPtr++] = (byte)c;
                if (inputPtr >= inputEnd) break block0;
                c = text.charAt(inputPtr++);
            }
            if (outputPtr >= outputEnd) {
                outputBuffer = byteBuilder.finishCurrentSegment();
                outputEnd = outputBuffer.length;
                outputPtr = 0;
            }
            if (c < 2048) {
                outputBuffer[outputPtr++] = (byte)(0xC0 | c >> 6);
            } else if (c < 55296 || c > 57343) {
                outputBuffer[outputPtr++] = (byte)(0xE0 | c >> 12);
                if (outputPtr >= outputEnd) {
                    outputBuffer = byteBuilder.finishCurrentSegment();
                    outputEnd = outputBuffer.length;
                    outputPtr = 0;
                }
                outputBuffer[outputPtr++] = (byte)(0x80 | c >> 6 & 0x3F);
            } else {
                if (c > 56319) {
                    this._throwIllegalSurrogate(c);
                }
                if (inputPtr >= inputEnd) {
                    this._throwIllegalSurrogate(c);
                }
                if ((c = this._convertSurrogate(c, text.charAt(inputPtr++))) > 0x10FFFF) {
                    this._throwIllegalSurrogate(c);
                }
                outputBuffer[outputPtr++] = (byte)(0xF0 | c >> 18);
                if (outputPtr >= outputEnd) {
                    outputBuffer = byteBuilder.finishCurrentSegment();
                    outputEnd = outputBuffer.length;
                    outputPtr = 0;
                }
                outputBuffer[outputPtr++] = (byte)(0x80 | c >> 12 & 0x3F);
                if (outputPtr >= outputEnd) {
                    outputBuffer = byteBuilder.finishCurrentSegment();
                    outputEnd = outputBuffer.length;
                    outputPtr = 0;
                }
                outputBuffer[outputPtr++] = (byte)(0x80 | c >> 6 & 0x3F);
            }
            if (outputPtr >= outputEnd) {
                outputBuffer = byteBuilder.finishCurrentSegment();
                outputEnd = outputBuffer.length;
                outputPtr = 0;
            }
            outputBuffer[outputPtr++] = (byte)(0x80 | c & 0x3F);
        }
        return this._byteBuilder.completeAndCoalesce(outputPtr);
    }

    private int _appendNumericEscape(int value, char[] quoteBuffer) {
        quoteBuffer[1] = 117;
        quoteBuffer[4] = HEX_CHARS[value >> 4];
        quoteBuffer[5] = HEX_CHARS[value & 0xF];
        return 6;
    }

    private int _appendNamedEscape(int escCode, char[] quoteBuffer) {
        quoteBuffer[1] = (char)escCode;
        return 2;
    }

    private int _appendByteEscape(int ch, int escCode, ByteArrayBuilder byteBuilder, int ptr) {
        byteBuilder.setCurrentSegmentLength(ptr);
        byteBuilder.append(92);
        if (escCode < 0) {
            byteBuilder.append(117);
            if (ch > 255) {
                int hi = ch >> 8;
                byteBuilder.append(HEX_BYTES[hi >> 4]);
                byteBuilder.append(HEX_BYTES[hi & 0xF]);
                ch &= 0xFF;
            } else {
                byteBuilder.append(48);
                byteBuilder.append(48);
            }
            byteBuilder.append(HEX_BYTES[ch >> 4]);
            byteBuilder.append(HEX_BYTES[ch & 0xF]);
        } else {
            byteBuilder.append((byte)escCode);
        }
        return byteBuilder.getCurrentSegmentLength();
    }

    private int _convertSurrogate(int firstPart, int secondPart) {
        if (secondPart < 56320 || secondPart > 57343) {
            throw new IllegalArgumentException("Broken surrogate pair: first char 0x" + Integer.toHexString(firstPart) + ", second 0x" + Integer.toHexString(secondPart) + "; illegal combination");
        }
        return 65536 + (firstPart - 55296 << 10) + (secondPart - 56320);
    }

    private void _throwIllegalSurrogate(int code) {
        if (code > 0x10FFFF) {
            throw new IllegalArgumentException("Illegal character point (0x" + Integer.toHexString(code) + ") to output; max is 0x10FFFF as per RFC 4627");
        }
        if (code >= 55296) {
            if (code <= 56319) {
                throw new IllegalArgumentException("Unmatched first part of surrogate pair (0x" + Integer.toHexString(code) + ")");
            }
            throw new IllegalArgumentException("Unmatched second part of surrogate pair (0x" + Integer.toHexString(code) + ")");
        }
        throw new IllegalArgumentException("Illegal character point (0x" + Integer.toHexString(code) + ") to output");
    }
}


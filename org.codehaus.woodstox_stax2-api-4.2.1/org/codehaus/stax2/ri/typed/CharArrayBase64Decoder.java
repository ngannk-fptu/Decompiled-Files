/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.ri.typed;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.codehaus.stax2.ri.typed.Base64DecoderBase;
import org.codehaus.stax2.typed.Base64Variant;

public class CharArrayBase64Decoder
extends Base64DecoderBase {
    protected char[] _currSegment;
    protected int _currSegmentPtr;
    protected int _currSegmentEnd;
    protected final ArrayList<char[]> _nextSegments = new ArrayList();
    protected int _lastSegmentOffset;
    protected int _lastSegmentEnd;
    protected int _nextSegmentIndex;

    public void init(Base64Variant variant, boolean firstChunk, char[] lastSegment, int lastOffset, int lastLen, List<char[]> segments) {
        this._variant = variant;
        if (firstChunk) {
            this._state = 0;
        }
        this._nextSegments.clear();
        if (segments == null || segments.isEmpty()) {
            this._currSegment = lastSegment;
            this._currSegmentPtr = lastOffset;
            this._currSegmentEnd = lastOffset + lastLen;
        } else {
            if (lastSegment == null) {
                throw new IllegalArgumentException();
            }
            Iterator<char[]> it = segments.iterator();
            this._currSegment = it.next();
            this._currSegmentPtr = 0;
            this._currSegmentEnd = this._currSegment.length;
            while (it.hasNext()) {
                this._nextSegments.add(it.next());
            }
            this._nextSegmentIndex = 0;
            this._nextSegments.add(lastSegment);
            this._lastSegmentOffset = lastOffset;
            this._lastSegmentEnd = lastOffset + lastLen;
        }
    }

    @Override
    public int decode(byte[] resultBuffer, int resultOffset, int maxLength) throws IllegalArgumentException {
        int origResultOffset = resultOffset;
        int resultBufferEnd = resultOffset + maxLength;
        block10: while (true) {
            switch (this._state) {
                case 0: {
                    int bits;
                    char ch;
                    while (this._currSegmentPtr < this._currSegmentEnd || this.nextSegment()) {
                        if ((ch = this._currSegment[this._currSegmentPtr++]) <= ' ') continue;
                        bits = this._variant.decodeBase64Char(ch);
                        if (bits < 0) {
                            throw this.reportInvalidChar(ch, 0);
                        }
                        this._decodedData = bits;
                    }
                    break block10;
                }
                {
                    int bits;
                    char ch;
                    case 1: {
                        if (this._currSegmentPtr >= this._currSegmentEnd && !this.nextSegment()) {
                            this._state = 1;
                            break block10;
                        }
                        if ((bits = this._variant.decodeBase64Char(ch = this._currSegment[this._currSegmentPtr++])) < 0) {
                            throw this.reportInvalidChar(ch, 1);
                        }
                        this._decodedData = this._decodedData << 6 | bits;
                    }
                    case 2: {
                        if (this._currSegmentPtr >= this._currSegmentEnd && !this.nextSegment()) {
                            this._state = 2;
                            break block10;
                        }
                        if ((bits = this._variant.decodeBase64Char(ch = this._currSegment[this._currSegmentPtr++])) < 0) {
                            if (bits != -2) {
                                throw this.reportInvalidChar(ch, 2);
                            }
                            this._state = 7;
                            continue block10;
                        }
                        this._decodedData = this._decodedData << 6 | bits;
                    }
                    case 3: {
                        if (this._currSegmentPtr >= this._currSegmentEnd && !this.nextSegment()) {
                            this._state = 3;
                            break block10;
                        }
                        if ((bits = this._variant.decodeBase64Char(ch = this._currSegment[this._currSegmentPtr++])) < 0) {
                            if (bits != -2) {
                                throw this.reportInvalidChar(ch, 3);
                            }
                            this._decodedData >>= 2;
                            this._state = 5;
                            continue block10;
                        }
                        this._decodedData = this._decodedData << 6 | bits;
                    }
                    case 4: {
                        if (resultOffset >= resultBufferEnd) {
                            this._state = 4;
                            break block10;
                        }
                        resultBuffer[resultOffset++] = (byte)(this._decodedData >> 16);
                    }
                    case 5: {
                        if (resultOffset >= resultBufferEnd) {
                            this._state = 5;
                            break block10;
                        }
                        resultBuffer[resultOffset++] = (byte)(this._decodedData >> 8);
                    }
                    case 6: {
                        if (resultOffset >= resultBufferEnd) {
                            this._state = 6;
                            break block10;
                        }
                        resultBuffer[resultOffset++] = (byte)this._decodedData;
                        this._state = 0;
                        continue block10;
                        break;
                    }
                }
                case 7: {
                    char ch;
                    if (this._currSegmentPtr >= this._currSegmentEnd && !this.nextSegment()) break block10;
                    if (!this._variant.usesPaddingChar(ch = this._currSegment[this._currSegmentPtr++])) {
                        throw this.reportInvalidChar(ch, 3, "expected padding character '" + this._variant.getPaddingChar() + "'");
                    }
                    this._state = 6;
                    this._decodedData >>= 4;
                    continue block10;
                }
                default: {
                    throw new IllegalStateException("Illegal internal state " + this._state);
                }
            }
            break;
        }
        return resultOffset - origResultOffset;
    }

    private boolean nextSegment() {
        if (this._nextSegmentIndex < this._nextSegments.size()) {
            this._currSegment = this._nextSegments.get(this._nextSegmentIndex++);
            if (this._nextSegmentIndex == this._nextSegments.size()) {
                this._currSegmentPtr = this._lastSegmentOffset;
                this._currSegmentEnd = this._lastSegmentEnd;
            } else {
                this._currSegmentPtr = 0;
                this._currSegmentEnd = this._currSegment.length;
            }
            return true;
        }
        return false;
    }
}


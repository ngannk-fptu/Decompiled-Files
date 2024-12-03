/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.ri.typed;

import org.codehaus.stax2.ri.typed.Base64DecoderBase;
import org.codehaus.stax2.typed.Base64Variant;

public final class StringBase64Decoder
extends Base64DecoderBase {
    String _currSegment;
    int _currSegmentPtr;
    int _currSegmentEnd;

    public void init(Base64Variant base64Variant, boolean bl, String string) {
        this._variant = base64Variant;
        if (bl) {
            this._state = 0;
        }
        this._currSegment = string;
        this._currSegmentPtr = 0;
        this._currSegmentEnd = string.length();
    }

    public int decode(byte[] byArray, int n, int n2) throws IllegalArgumentException {
        int n3 = n;
        int n4 = n + n2;
        block10: while (true) {
            switch (this._state) {
                case 0: {
                    int n5;
                    char c;
                    while (this._currSegmentPtr < this._currSegmentEnd) {
                        if ((c = this._currSegment.charAt(this._currSegmentPtr++)) <= ' ') continue;
                        n5 = this._variant.decodeBase64Char(c);
                        if (n5 < 0) {
                            throw this.reportInvalidChar(c, 0);
                        }
                        this._decodedData = n5;
                    }
                    break block10;
                }
                {
                    int n5;
                    char c;
                    case 1: {
                        if (this._currSegmentPtr >= this._currSegmentEnd) {
                            this._state = 1;
                            break block10;
                        }
                        if ((n5 = this._variant.decodeBase64Char(c = this._currSegment.charAt(this._currSegmentPtr++))) < 0) {
                            throw this.reportInvalidChar(c, 1);
                        }
                        this._decodedData = this._decodedData << 6 | n5;
                    }
                    case 2: {
                        if (this._currSegmentPtr >= this._currSegmentEnd) {
                            this._state = 2;
                            break block10;
                        }
                        if ((n5 = this._variant.decodeBase64Char(c = this._currSegment.charAt(this._currSegmentPtr++))) < 0) {
                            if (n5 != -2) {
                                throw this.reportInvalidChar(c, 2);
                            }
                            this._state = 7;
                            continue block10;
                        }
                        this._decodedData = this._decodedData << 6 | n5;
                    }
                    case 3: {
                        if (this._currSegmentPtr >= this._currSegmentEnd) {
                            this._state = 3;
                            break block10;
                        }
                        if ((n5 = this._variant.decodeBase64Char(c = this._currSegment.charAt(this._currSegmentPtr++))) < 0) {
                            if (n5 != -2) {
                                throw this.reportInvalidChar(c, 3);
                            }
                            this._decodedData >>= 2;
                            this._state = 5;
                            continue block10;
                        }
                        this._decodedData = this._decodedData << 6 | n5;
                    }
                    case 4: {
                        if (n >= n4) {
                            this._state = 4;
                            break block10;
                        }
                        byArray[n++] = (byte)(this._decodedData >> 16);
                    }
                    case 5: {
                        if (n >= n4) {
                            this._state = 5;
                            break block10;
                        }
                        byArray[n++] = (byte)(this._decodedData >> 8);
                    }
                    case 6: {
                        if (n >= n4) {
                            this._state = 6;
                            break block10;
                        }
                        byArray[n++] = (byte)this._decodedData;
                        this._state = 0;
                        continue block10;
                        break;
                    }
                }
                case 7: {
                    char c;
                    if (this._currSegmentPtr >= this._currSegmentEnd) break block10;
                    if (!this._variant.usesPaddingChar(c = this._currSegment.charAt(this._currSegmentPtr++))) {
                        throw this.reportInvalidChar(c, 3, "expected padding character '='");
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
        return n - n3;
    }
}


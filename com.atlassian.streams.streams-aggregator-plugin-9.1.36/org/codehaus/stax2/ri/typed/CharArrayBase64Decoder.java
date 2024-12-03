/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.ri.typed;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.codehaus.stax2.ri.typed.Base64DecoderBase;
import org.codehaus.stax2.typed.Base64Variant;

public final class CharArrayBase64Decoder
extends Base64DecoderBase {
    char[] _currSegment;
    int _currSegmentPtr;
    int _currSegmentEnd;
    final ArrayList _nextSegments = new ArrayList();
    int _lastSegmentOffset;
    int _lastSegmentEnd;
    int _nextSegmentIndex;

    public void init(Base64Variant base64Variant, boolean bl, char[] cArray, int n, int n2, List list) {
        this._variant = base64Variant;
        if (bl) {
            this._state = 0;
        }
        this._nextSegments.clear();
        if (list == null || list.isEmpty()) {
            this._currSegment = cArray;
            this._currSegmentPtr = n;
            this._currSegmentEnd = n + n2;
        } else {
            if (cArray == null) {
                throw new IllegalArgumentException();
            }
            Iterator iterator = list.iterator();
            this._currSegment = (char[])iterator.next();
            this._currSegmentPtr = 0;
            this._currSegmentEnd = this._currSegment.length;
            while (iterator.hasNext()) {
                this._nextSegments.add(iterator.next());
            }
            this._nextSegmentIndex = 0;
            this._nextSegments.add(cArray);
            this._lastSegmentOffset = n;
            this._lastSegmentEnd = n + n2;
        }
    }

    public int decode(byte[] byArray, int n, int n2) throws IllegalArgumentException {
        int n3 = n;
        int n4 = n + n2;
        block10: while (true) {
            switch (this._state) {
                case 0: {
                    int n5;
                    char c;
                    while (this._currSegmentPtr < this._currSegmentEnd || this.nextSegment()) {
                        if ((c = this._currSegment[this._currSegmentPtr++]) <= ' ') continue;
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
                        if (this._currSegmentPtr >= this._currSegmentEnd && !this.nextSegment()) {
                            this._state = 1;
                            break block10;
                        }
                        if ((n5 = this._variant.decodeBase64Char(c = this._currSegment[this._currSegmentPtr++])) < 0) {
                            throw this.reportInvalidChar(c, 1);
                        }
                        this._decodedData = this._decodedData << 6 | n5;
                    }
                    case 2: {
                        if (this._currSegmentPtr >= this._currSegmentEnd && !this.nextSegment()) {
                            this._state = 2;
                            break block10;
                        }
                        if ((n5 = this._variant.decodeBase64Char(c = this._currSegment[this._currSegmentPtr++])) < 0) {
                            if (n5 != -2) {
                                throw this.reportInvalidChar(c, 2);
                            }
                            this._state = 7;
                            continue block10;
                        }
                        this._decodedData = this._decodedData << 6 | n5;
                    }
                    case 3: {
                        if (this._currSegmentPtr >= this._currSegmentEnd && !this.nextSegment()) {
                            this._state = 3;
                            break block10;
                        }
                        if ((n5 = this._variant.decodeBase64Char(c = this._currSegment[this._currSegmentPtr++])) < 0) {
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
                    if (this._currSegmentPtr >= this._currSegmentEnd && !this.nextSegment()) break block10;
                    if (!this._variant.usesPaddingChar(c = this._currSegment[this._currSegmentPtr++])) {
                        throw this.reportInvalidChar(c, 3, "expected padding character '" + this._variant.getPaddingChar() + "'");
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

    private boolean nextSegment() {
        if (this._nextSegmentIndex < this._nextSegments.size()) {
            this._currSegment = (char[])this._nextSegments.get(this._nextSegmentIndex++);
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


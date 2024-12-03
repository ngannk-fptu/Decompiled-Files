/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.ri.typed;

import org.codehaus.stax2.ri.Stax2Util;
import org.codehaus.stax2.typed.Base64Variant;

abstract class Base64DecoderBase {
    static final int STATE_INITIAL = 0;
    static final int STATE_VALID_1 = 1;
    static final int STATE_VALID_2 = 2;
    static final int STATE_VALID_3 = 3;
    static final int STATE_OUTPUT_3 = 4;
    static final int STATE_OUTPUT_2 = 5;
    static final int STATE_OUTPUT_1 = 6;
    static final int STATE_VALID_2_AND_PADDING = 7;
    static final int INT_SPACE = 32;
    Base64Variant _variant;
    int _state = 0;
    int _decodedData;
    Stax2Util.ByteAggregator _byteAggr = null;

    protected Base64DecoderBase() {
    }

    public abstract int decode(byte[] var1, int var2, int var3) throws IllegalArgumentException;

    public final boolean hasData() {
        return this._state >= 4 && this._state <= 6;
    }

    public final int endOfContent() {
        if (this._state == 0 || this._state == 4 || this._state == 5 || this._state == 6) {
            return 0;
        }
        if (this._variant.usesPadding()) {
            return -1;
        }
        if (this._state == 2) {
            this._state = 6;
            this._decodedData >>= 4;
            return 1;
        }
        if (this._state == 3) {
            this._decodedData >>= 2;
            this._state = 5;
            return 2;
        }
        return -1;
    }

    public byte[] decodeCompletely() {
        Stax2Util.ByteAggregator aggr = this.getByteAggregator();
        byte[] buffer = aggr.startAggregation();
        while (true) {
            int offset = 0;
            int len = buffer.length;
            do {
                int readCount;
                if ((readCount = this.decode(buffer, offset, len)) < 1) {
                    int left = this.endOfContent();
                    if (left < 0) {
                        throw new IllegalArgumentException("Incomplete base64 triplet at the end of decoded content");
                    }
                    if (left > 0) continue;
                    return aggr.aggregateAll(buffer, offset);
                }
                offset += readCount;
                len -= readCount;
            } while (len > 0);
            buffer = aggr.addFullBlock(buffer);
        }
    }

    public Stax2Util.ByteAggregator getByteAggregator() {
        if (this._byteAggr == null) {
            this._byteAggr = new Stax2Util.ByteAggregator();
        }
        return this._byteAggr;
    }

    protected IllegalArgumentException reportInvalidChar(char ch, int bindex) throws IllegalArgumentException {
        return this.reportInvalidChar(ch, bindex, null);
    }

    protected IllegalArgumentException reportInvalidChar(char ch, int bindex, String msg) throws IllegalArgumentException {
        String base = ch <= ' ' ? "Illegal white space character (code 0x" + Integer.toHexString(ch) + ") as character #" + (bindex + 1) + " of 4-char base64 unit: can only used between units" : (this._variant.usesPaddingChar(ch) ? "Unexpected padding character ('" + this._variant.getPaddingChar() + "') as character #" + (bindex + 1) + " of 4-char base64 unit: padding only legal as 3rd or 4th character" : (!Character.isDefined(ch) || Character.isISOControl(ch) ? "Illegal character (code 0x" + Integer.toHexString(ch) + ") in base64 content" : "Illegal character '" + ch + "' (code 0x" + Integer.toHexString(ch) + ") in base64 content"));
        if (msg != null) {
            base = base + ": " + msg;
        }
        return new IllegalArgumentException(base);
    }
}


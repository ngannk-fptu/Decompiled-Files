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
        Stax2Util.ByteAggregator byteAggregator = this.getByteAggregator();
        byte[] byArray = byteAggregator.startAggregation();
        while (true) {
            int n = 0;
            int n2 = byArray.length;
            do {
                int n3;
                if ((n3 = this.decode(byArray, n, n2)) < 1) {
                    int n4 = this.endOfContent();
                    if (n4 < 0) {
                        throw new IllegalArgumentException("Incomplete base64 triplet at the end of decoded content");
                    }
                    if (n4 > 0) continue;
                    return byteAggregator.aggregateAll(byArray, n);
                }
                n += n3;
                n2 -= n3;
            } while (n2 > 0);
            byArray = byteAggregator.addFullBlock(byArray);
        }
    }

    public Stax2Util.ByteAggregator getByteAggregator() {
        if (this._byteAggr == null) {
            this._byteAggr = new Stax2Util.ByteAggregator();
        }
        return this._byteAggr;
    }

    protected IllegalArgumentException reportInvalidChar(char c, int n) throws IllegalArgumentException {
        return this.reportInvalidChar(c, n, null);
    }

    protected IllegalArgumentException reportInvalidChar(char c, int n, String string) throws IllegalArgumentException {
        String string2 = c <= ' ' ? "Illegal white space character (code 0x" + Integer.toHexString(c) + ") as character #" + (n + 1) + " of 4-char base64 unit: can only used between units" : (this._variant.usesPaddingChar(c) ? "Unexpected padding character ('" + this._variant.getPaddingChar() + "') as character #" + (n + 1) + " of 4-char base64 unit: padding only legal as 3rd or 4th character" : (!Character.isDefined(c) || Character.isISOControl(c) ? "Illegal character (code 0x" + Integer.toHexString(c) + ") in base64 content" : "Illegal character '" + c + "' (code 0x" + Integer.toHexString(c) + ") in base64 content"));
        if (string != null) {
            string2 = string2 + ": " + string;
        }
        return new IllegalArgumentException(string2);
    }
}


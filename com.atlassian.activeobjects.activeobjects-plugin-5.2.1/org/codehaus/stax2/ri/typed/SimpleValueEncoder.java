/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.ri.typed;

import org.codehaus.stax2.ri.typed.AsciiValueEncoder;
import org.codehaus.stax2.ri.typed.ValueEncoderFactory;
import org.codehaus.stax2.typed.Base64Variant;

public class SimpleValueEncoder {
    protected final char[] mBuffer = new char[500];
    protected final ValueEncoderFactory mEncoderFactory = new ValueEncoderFactory();

    public String encodeAsString(int[] nArray, int n, int n2) {
        return this.encode(this.mEncoderFactory.getEncoder(nArray, n, n2));
    }

    public String encodeAsString(long[] lArray, int n, int n2) {
        return this.encode(this.mEncoderFactory.getEncoder(lArray, n, n2));
    }

    public String encodeAsString(float[] fArray, int n, int n2) {
        return this.encode(this.mEncoderFactory.getEncoder(fArray, n, n2));
    }

    public String encodeAsString(double[] dArray, int n, int n2) {
        return this.encode(this.mEncoderFactory.getEncoder(dArray, n, n2));
    }

    public String encodeAsString(Base64Variant base64Variant, byte[] byArray, int n, int n2) {
        return this.encode(this.mEncoderFactory.getEncoder(base64Variant, byArray, n, n2));
    }

    protected String encode(AsciiValueEncoder asciiValueEncoder) {
        int n = asciiValueEncoder.encodeMore(this.mBuffer, 0, this.mBuffer.length);
        if (asciiValueEncoder.isCompleted()) {
            return new String(this.mBuffer, 0, n);
        }
        StringBuffer stringBuffer = new StringBuffer(this.mBuffer.length << 1);
        stringBuffer.append(this.mBuffer, 0, n);
        do {
            n = asciiValueEncoder.encodeMore(this.mBuffer, 0, this.mBuffer.length);
            stringBuffer.append(this.mBuffer, 0, n);
        } while (!asciiValueEncoder.isCompleted());
        return stringBuffer.toString();
    }
}


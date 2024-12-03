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

    public String encodeAsString(int[] value, int from, int length) {
        return this.encode(this.mEncoderFactory.getEncoder(value, from, length));
    }

    public String encodeAsString(long[] value, int from, int length) {
        return this.encode(this.mEncoderFactory.getEncoder(value, from, length));
    }

    public String encodeAsString(float[] value, int from, int length) {
        return this.encode(this.mEncoderFactory.getEncoder(value, from, length));
    }

    public String encodeAsString(double[] value, int from, int length) {
        return this.encode(this.mEncoderFactory.getEncoder(value, from, length));
    }

    public String encodeAsString(Base64Variant v, byte[] value, int from, int length) {
        return this.encode(this.mEncoderFactory.getEncoder(v, value, from, length));
    }

    protected String encode(AsciiValueEncoder enc) {
        int last = enc.encodeMore(this.mBuffer, 0, this.mBuffer.length);
        if (enc.isCompleted()) {
            return new String(this.mBuffer, 0, last);
        }
        StringBuffer sb = new StringBuffer(this.mBuffer.length << 1);
        sb.append(this.mBuffer, 0, last);
        do {
            last = enc.encodeMore(this.mBuffer, 0, this.mBuffer.length);
            sb.append(this.mBuffer, 0, last);
        } while (!enc.isCompleted());
        return sb.toString();
    }
}


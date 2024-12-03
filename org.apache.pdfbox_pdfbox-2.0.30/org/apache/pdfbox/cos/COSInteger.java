/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.cos;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.cos.ICOSVisitor;

public final class COSInteger
extends COSNumber {
    private static final int LOW = -100;
    private static final int HIGH = 256;
    private static final COSInteger[] STATIC = new COSInteger[357];
    public static final COSInteger ZERO = COSInteger.get(0L);
    public static final COSInteger ONE = COSInteger.get(1L);
    public static final COSInteger TWO = COSInteger.get(2L);
    public static final COSInteger THREE = COSInteger.get(3L);
    protected static final COSInteger OUT_OF_RANGE_MAX = COSInteger.getInvalid(true);
    protected static final COSInteger OUT_OF_RANGE_MIN = COSInteger.getInvalid(false);
    private final long value;
    private final boolean isValid;

    public static COSInteger get(long val) {
        if (-100L <= val && val <= 256L) {
            int index = (int)val - -100;
            if (STATIC[index] == null) {
                COSInteger.STATIC[index] = new COSInteger(val, true);
            }
            return STATIC[index];
        }
        return new COSInteger(val, true);
    }

    private static COSInteger getInvalid(boolean maxValue) {
        return maxValue ? new COSInteger(Long.MAX_VALUE, false) : new COSInteger(Long.MIN_VALUE, false);
    }

    private COSInteger(long val, boolean valid) {
        this.value = val;
        this.isValid = valid;
    }

    public boolean equals(Object o) {
        return o instanceof COSInteger && ((COSInteger)o).intValue() == this.intValue();
    }

    public int hashCode() {
        return (int)(this.value ^ this.value >> 32);
    }

    public String toString() {
        return "COSInt{" + this.value + "}";
    }

    @Override
    public float floatValue() {
        return this.value;
    }

    @Override
    public double doubleValue() {
        return this.value;
    }

    @Override
    public int intValue() {
        return (int)this.value;
    }

    @Override
    public long longValue() {
        return this.value;
    }

    public boolean isValid() {
        return this.isValid;
    }

    @Override
    public Object accept(ICOSVisitor visitor) throws IOException {
        return visitor.visitFromInt(this);
    }

    public void writePDF(OutputStream output) throws IOException {
        output.write(String.valueOf(this.value).getBytes("ISO-8859-1"));
    }
}


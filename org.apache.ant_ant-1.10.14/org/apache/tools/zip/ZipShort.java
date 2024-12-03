/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.zip;

public final class ZipShort
implements Cloneable {
    private static final int BYTE_1_MASK = 65280;
    private static final int BYTE_1_SHIFT = 8;
    private final int value;

    public ZipShort(int value) {
        this.value = value;
    }

    public ZipShort(byte[] bytes) {
        this(bytes, 0);
    }

    public ZipShort(byte[] bytes, int offset) {
        this.value = ZipShort.getValue(bytes, offset);
    }

    public byte[] getBytes() {
        byte[] result = new byte[2];
        ZipShort.putShort(this.value, result, 0);
        return result;
    }

    public static void putShort(int value, byte[] buf, int offset) {
        buf[offset] = (byte)(value & 0xFF);
        buf[offset + 1] = (byte)((value & 0xFF00) >> 8);
    }

    public int getValue() {
        return this.value;
    }

    public static byte[] getBytes(int value) {
        byte[] result = new byte[]{(byte)(value & 0xFF), (byte)((value & 0xFF00) >> 8)};
        return result;
    }

    public static int getValue(byte[] bytes, int offset) {
        int value = bytes[offset + 1] << 8 & 0xFF00;
        return value += bytes[offset] & 0xFF;
    }

    public static int getValue(byte[] bytes) {
        return ZipShort.getValue(bytes, 0);
    }

    public boolean equals(Object o) {
        return o instanceof ZipShort && this.value == ((ZipShort)o).getValue();
    }

    public int hashCode() {
        return this.value;
    }

    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException cnfe) {
            throw new RuntimeException(cnfe);
        }
    }

    public String toString() {
        return "ZipShort value: " + this.value;
    }
}


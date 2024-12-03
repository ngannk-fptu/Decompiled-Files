/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hpsf;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import org.apache.poi.common.Duplicatable;
import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianOutput;

public class ClassID
implements Duplicatable,
GenericRecord {
    public static final int LENGTH = 16;
    private final byte[] bytes = new byte[16];

    public ClassID(byte[] src, int offset) {
        this.read(src, offset);
    }

    public ClassID() {
        Arrays.fill(this.bytes, (byte)0);
    }

    public ClassID(ClassID other) {
        System.arraycopy(other.bytes, 0, this.bytes, 0, this.bytes.length);
    }

    public ClassID(String externalForm) {
        String clsStr = externalForm.replaceAll("[{}-]", "");
        for (int i = 0; i < clsStr.length(); i += 2) {
            this.bytes[i / 2] = (byte)Integer.parseInt(clsStr.substring(i, i + 2), 16);
        }
    }

    public ClassID(LittleEndianInput lei) {
        byte[] buf = (byte[])this.bytes.clone();
        lei.readFully(buf);
        this.read(buf, 0);
    }

    public int length() {
        return 16;
    }

    public byte[] getBytes() {
        return this.bytes;
    }

    public void setBytes(byte[] bytes) {
        System.arraycopy(bytes, 0, this.bytes, 0, 16);
    }

    public byte[] read(byte[] src, int offset) {
        this.bytes[0] = src[3 + offset];
        this.bytes[1] = src[2 + offset];
        this.bytes[2] = src[1 + offset];
        this.bytes[3] = src[0 + offset];
        this.bytes[4] = src[5 + offset];
        this.bytes[5] = src[4 + offset];
        this.bytes[6] = src[7 + offset];
        this.bytes[7] = src[6 + offset];
        System.arraycopy(src, 8 + offset, this.bytes, 8, 8);
        return this.bytes;
    }

    public void write(byte[] dst, int offset) throws ArrayStoreException {
        if (dst.length < 16) {
            throw new ArrayStoreException("Destination byte[] must have room for at least 16 bytes, but has a length of only " + dst.length + ".");
        }
        dst[0 + offset] = this.bytes[3];
        dst[1 + offset] = this.bytes[2];
        dst[2 + offset] = this.bytes[1];
        dst[3 + offset] = this.bytes[0];
        dst[4 + offset] = this.bytes[5];
        dst[5 + offset] = this.bytes[4];
        dst[6 + offset] = this.bytes[7];
        dst[7 + offset] = this.bytes[6];
        System.arraycopy(this.bytes, 8, dst, 8 + offset, 8);
    }

    public void write(LittleEndianOutput leo) {
        byte[] buf = (byte[])this.bytes.clone();
        this.write(buf, 0);
        leo.write(buf);
    }

    public boolean equals(Object o) {
        return o instanceof ClassID && Arrays.equals(this.bytes, ((ClassID)o).bytes);
    }

    public boolean equalsInverted(ClassID o) {
        return o.bytes[0] == this.bytes[3] && o.bytes[1] == this.bytes[2] && o.bytes[2] == this.bytes[1] && o.bytes[3] == this.bytes[0] && o.bytes[4] == this.bytes[5] && o.bytes[5] == this.bytes[4] && o.bytes[6] == this.bytes[7] && o.bytes[7] == this.bytes[6] && o.bytes[8] == this.bytes[8] && o.bytes[9] == this.bytes[9] && o.bytes[10] == this.bytes[10] && o.bytes[11] == this.bytes[11] && o.bytes[12] == this.bytes[12] && o.bytes[13] == this.bytes[13] && o.bytes[14] == this.bytes[14] && o.bytes[15] == this.bytes[15];
    }

    public int hashCode() {
        return this.toString().hashCode();
    }

    public String toString() {
        return "{" + this.toUUIDString() + "}";
    }

    public String toUUIDString() {
        return this.toUUID().toString().toUpperCase(Locale.ROOT);
    }

    public UUID toUUID() {
        long mostSigBits = ByteBuffer.wrap(this.bytes, 0, 8).getLong();
        long leastSigBits = ByteBuffer.wrap(this.bytes, 8, 8).getLong();
        return new UUID(mostSigBits, leastSigBits);
    }

    @Override
    public ClassID copy() {
        return new ClassID(this);
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("uuid", this::toString);
    }
}


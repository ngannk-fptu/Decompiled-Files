/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model.types;

import java.util.Objects;
import org.apache.poi.util.BitField;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;

@Internal
public abstract class SHD80AbstractType {
    private static final BitField icoFore = new BitField(31);
    private static final BitField icoBack = new BitField(992);
    private static final BitField ipat = new BitField(64512);
    protected short field_1_value;

    protected SHD80AbstractType() {
    }

    protected SHD80AbstractType(SHD80AbstractType other) {
        this.field_1_value = other.field_1_value;
    }

    protected void fillFields(byte[] data, int offset) {
        this.field_1_value = LittleEndian.getShort(data, 0 + offset);
    }

    public void serialize(byte[] data, int offset) {
        LittleEndian.putShort(data, 0 + offset, this.field_1_value);
    }

    public byte[] serialize() {
        byte[] result = new byte[SHD80AbstractType.getSize()];
        this.serialize(result, 0);
        return result;
    }

    public static int getSize() {
        return 2;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        SHD80AbstractType other = (SHD80AbstractType)obj;
        return this.field_1_value == other.field_1_value;
    }

    public int hashCode() {
        return Objects.hash(this.field_1_value);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[SHD80]\n");
        builder.append("    .value                = ");
        builder.append(" (").append(this.getValue()).append(" )\n");
        builder.append("         .icoFore                  = ").append(this.getIcoFore()).append('\n');
        builder.append("         .icoBack                  = ").append(this.getIcoBack()).append('\n');
        builder.append("         .ipat                     = ").append(this.getIpat()).append('\n');
        builder.append("[/SHD80]\n");
        return builder.toString();
    }

    @Internal
    public short getValue() {
        return this.field_1_value;
    }

    @Internal
    public void setValue(short field_1_value) {
        this.field_1_value = field_1_value;
    }

    @Internal
    public void setIcoFore(byte value) {
        this.field_1_value = (short)icoFore.setValue(this.field_1_value, value);
    }

    @Internal
    public byte getIcoFore() {
        return (byte)icoFore.getValue(this.field_1_value);
    }

    @Internal
    public void setIcoBack(byte value) {
        this.field_1_value = (short)icoBack.setValue(this.field_1_value, value);
    }

    @Internal
    public byte getIcoBack() {
        return (byte)icoBack.getValue(this.field_1_value);
    }

    @Internal
    public void setIpat(byte value) {
        this.field_1_value = (short)ipat.setValue(this.field_1_value, value);
    }

    @Internal
    public byte getIpat() {
        return (byte)ipat.getValue(this.field_1_value);
    }
}


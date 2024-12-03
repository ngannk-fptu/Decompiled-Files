/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model.types;

import java.util.Objects;
import org.apache.poi.util.BitField;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;

@Internal
public abstract class LFOLVLBaseAbstractType {
    protected int field_1_iStartAt;
    protected int field_2_flags;
    private static final BitField iLvl = new BitField(15);
    private static final BitField fStartAt = new BitField(16);
    private static final BitField fFormatting = new BitField(32);
    private static final BitField grfhic = new BitField(16320);
    private static final BitField unused1 = new BitField(536854528);
    private static final BitField unused2 = new BitField(-536870912);

    protected LFOLVLBaseAbstractType() {
    }

    protected void fillFields(byte[] data, int offset) {
        this.field_1_iStartAt = LittleEndian.getInt(data, 0 + offset);
        this.field_2_flags = LittleEndian.getInt(data, 4 + offset);
    }

    public void serialize(byte[] data, int offset) {
        LittleEndian.putInt(data, 0 + offset, this.field_1_iStartAt);
        LittleEndian.putInt(data, 4 + offset, this.field_2_flags);
    }

    public byte[] serialize() {
        byte[] result = new byte[LFOLVLBaseAbstractType.getSize()];
        this.serialize(result, 0);
        return result;
    }

    public static int getSize() {
        return 8;
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
        LFOLVLBaseAbstractType other = (LFOLVLBaseAbstractType)obj;
        if (this.field_1_iStartAt != other.field_1_iStartAt) {
            return false;
        }
        return this.field_2_flags == other.field_2_flags;
    }

    public int hashCode() {
        return Objects.hash(this.field_1_iStartAt, this.field_2_flags);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[LFOLVLBase]\n");
        builder.append("    .iStartAt             = ");
        builder.append(" (").append(this.getIStartAt()).append(" )\n");
        builder.append("    .flags                = ");
        builder.append(" (").append(this.getFlags()).append(" )\n");
        builder.append("         .iLvl                     = ").append(this.getILvl()).append('\n');
        builder.append("         .fStartAt                 = ").append(this.isFStartAt()).append('\n');
        builder.append("         .fFormatting              = ").append(this.isFFormatting()).append('\n');
        builder.append("         .grfhic                   = ").append(this.getGrfhic()).append('\n');
        builder.append("         .unused1                  = ").append(this.getUnused1()).append('\n');
        builder.append("         .unused2                  = ").append(this.getUnused2()).append('\n');
        builder.append("[/LFOLVLBase]\n");
        return builder.toString();
    }

    @Internal
    public int getIStartAt() {
        return this.field_1_iStartAt;
    }

    @Internal
    public void setIStartAt(int field_1_iStartAt) {
        this.field_1_iStartAt = field_1_iStartAt;
    }

    @Internal
    public int getFlags() {
        return this.field_2_flags;
    }

    @Internal
    public void setFlags(int field_2_flags) {
        this.field_2_flags = field_2_flags;
    }

    @Internal
    public void setILvl(byte value) {
        this.field_2_flags = iLvl.setValue(this.field_2_flags, value);
    }

    @Internal
    public byte getILvl() {
        return (byte)iLvl.getValue(this.field_2_flags);
    }

    @Internal
    public void setFStartAt(boolean value) {
        this.field_2_flags = fStartAt.setBoolean(this.field_2_flags, value);
    }

    @Internal
    public boolean isFStartAt() {
        return fStartAt.isSet(this.field_2_flags);
    }

    @Internal
    public void setFFormatting(boolean value) {
        this.field_2_flags = fFormatting.setBoolean(this.field_2_flags, value);
    }

    @Internal
    public boolean isFFormatting() {
        return fFormatting.isSet(this.field_2_flags);
    }

    @Internal
    public void setGrfhic(short value) {
        this.field_2_flags = grfhic.setValue(this.field_2_flags, value);
    }

    @Internal
    public short getGrfhic() {
        return (short)grfhic.getValue(this.field_2_flags);
    }

    @Internal
    public void setUnused1(short value) {
        this.field_2_flags = unused1.setValue(this.field_2_flags, value);
    }

    @Internal
    @Deprecated
    public short getUnused1() {
        return (short)unused1.getValue(this.field_2_flags);
    }

    @Internal
    public void setUnused2(byte value) {
        this.field_2_flags = unused2.setValue(this.field_2_flags, value);
    }

    @Internal
    @Deprecated
    public byte getUnused2() {
        return (byte)unused2.getValue(this.field_2_flags);
    }
}


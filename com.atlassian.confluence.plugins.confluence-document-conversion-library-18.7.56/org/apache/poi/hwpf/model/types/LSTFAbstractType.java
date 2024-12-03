/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model.types;

import java.util.Arrays;
import org.apache.poi.hwpf.model.Grfhic;
import org.apache.poi.util.BitField;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;

@Internal
public abstract class LSTFAbstractType {
    protected int field_1_lsid;
    protected int field_2_tplc;
    protected short[] field_3_rgistdPara = new short[0];
    protected byte field_4_flags;
    private static final BitField fSimpleList = new BitField(1);
    private static final BitField unused1 = new BitField(2);
    private static final BitField fAutoNum = new BitField(4);
    private static final BitField unused2 = new BitField(8);
    private static final BitField fHybrid = new BitField(16);
    private static final BitField reserved1 = new BitField(224);
    protected Grfhic field_5_grfhic = new Grfhic();

    protected LSTFAbstractType() {
    }

    protected void fillFields(byte[] data, int offset) {
        this.field_1_lsid = LittleEndian.getInt(data, 0 + offset);
        this.field_2_tplc = LittleEndian.getInt(data, 4 + offset);
        this.field_3_rgistdPara = LittleEndian.getShortArray(data, 8 + offset, 18);
        this.field_4_flags = data[26 + offset];
        this.field_5_grfhic = new Grfhic(data, 27 + offset);
    }

    public void serialize(byte[] data, int offset) {
        LittleEndian.putInt(data, 0 + offset, this.field_1_lsid);
        LittleEndian.putInt(data, 4 + offset, this.field_2_tplc);
        LittleEndian.putShortArray(data, 8 + offset, this.field_3_rgistdPara);
        data[26 + offset] = this.field_4_flags;
        this.field_5_grfhic.serialize(data, 27 + offset);
    }

    public byte[] serialize() {
        byte[] result = new byte[LSTFAbstractType.getSize()];
        this.serialize(result, 0);
        return result;
    }

    public static int getSize() {
        return 28;
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
        LSTFAbstractType other = (LSTFAbstractType)obj;
        if (this.field_1_lsid != other.field_1_lsid) {
            return false;
        }
        if (this.field_2_tplc != other.field_2_tplc) {
            return false;
        }
        if (!Arrays.equals(this.field_3_rgistdPara, other.field_3_rgistdPara)) {
            return false;
        }
        if (this.field_4_flags != other.field_4_flags) {
            return false;
        }
        return !(this.field_5_grfhic == null ? other.field_5_grfhic != null : !this.field_5_grfhic.equals(other.field_5_grfhic));
    }

    public int hashCode() {
        return Arrays.deepHashCode(new Object[]{this.field_1_lsid, this.field_2_tplc, this.field_3_rgistdPara, this.field_4_flags, this.field_5_grfhic});
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[LSTF]\n");
        builder.append("    .lsid                 = ");
        builder.append(" (").append(this.getLsid()).append(" )\n");
        builder.append("    .tplc                 = ");
        builder.append(" (").append(this.getTplc()).append(" )\n");
        builder.append("    .rgistdPara           = ");
        builder.append(" (").append(Arrays.toString(this.getRgistdPara())).append(" )\n");
        builder.append("    .flags                = ");
        builder.append(" (").append(this.getFlags()).append(" )\n");
        builder.append("         .fSimpleList              = ").append(this.isFSimpleList()).append('\n');
        builder.append("         .unused1                  = ").append(this.isUnused1()).append('\n');
        builder.append("         .fAutoNum                 = ").append(this.isFAutoNum()).append('\n');
        builder.append("         .unused2                  = ").append(this.isUnused2()).append('\n');
        builder.append("         .fHybrid                  = ").append(this.isFHybrid()).append('\n');
        builder.append("         .reserved1                = ").append(this.getReserved1()).append('\n');
        builder.append("    .grfhic               = ");
        builder.append(" (").append(this.getGrfhic()).append(" )\n");
        builder.append("[/LSTF]\n");
        return builder.toString();
    }

    @Internal
    public int getLsid() {
        return this.field_1_lsid;
    }

    @Internal
    public void setLsid(int field_1_lsid) {
        this.field_1_lsid = field_1_lsid;
    }

    @Internal
    public int getTplc() {
        return this.field_2_tplc;
    }

    @Internal
    public void setTplc(int field_2_tplc) {
        this.field_2_tplc = field_2_tplc;
    }

    @Internal
    public short[] getRgistdPara() {
        return this.field_3_rgistdPara;
    }

    @Internal
    public void setRgistdPara(short[] field_3_rgistdPara) {
        this.field_3_rgistdPara = field_3_rgistdPara;
    }

    @Internal
    public byte getFlags() {
        return this.field_4_flags;
    }

    @Internal
    public void setFlags(byte field_4_flags) {
        this.field_4_flags = field_4_flags;
    }

    @Internal
    public Grfhic getGrfhic() {
        return this.field_5_grfhic;
    }

    @Internal
    public void setGrfhic(Grfhic field_5_grfhic) {
        this.field_5_grfhic = field_5_grfhic;
    }

    @Internal
    public void setFSimpleList(boolean value) {
        this.field_4_flags = (byte)fSimpleList.setBoolean(this.field_4_flags, value);
    }

    @Internal
    public boolean isFSimpleList() {
        return fSimpleList.isSet(this.field_4_flags);
    }

    @Internal
    public void setUnused1(boolean value) {
        this.field_4_flags = (byte)unused1.setBoolean(this.field_4_flags, value);
    }

    @Internal
    @Deprecated
    public boolean isUnused1() {
        return unused1.isSet(this.field_4_flags);
    }

    @Internal
    public void setFAutoNum(boolean value) {
        this.field_4_flags = (byte)fAutoNum.setBoolean(this.field_4_flags, value);
    }

    @Internal
    public boolean isFAutoNum() {
        return fAutoNum.isSet(this.field_4_flags);
    }

    @Internal
    public void setUnused2(boolean value) {
        this.field_4_flags = (byte)unused2.setBoolean(this.field_4_flags, value);
    }

    @Internal
    @Deprecated
    public boolean isUnused2() {
        return unused2.isSet(this.field_4_flags);
    }

    @Internal
    public void setFHybrid(boolean value) {
        this.field_4_flags = (byte)fHybrid.setBoolean(this.field_4_flags, value);
    }

    @Internal
    public boolean isFHybrid() {
        return fHybrid.isSet(this.field_4_flags);
    }

    @Internal
    public void setReserved1(byte value) {
        this.field_4_flags = (byte)reserved1.setValue(this.field_4_flags, value);
    }

    @Internal
    @Deprecated
    public byte getReserved1() {
        return (byte)reserved1.getValue(this.field_4_flags);
    }
}


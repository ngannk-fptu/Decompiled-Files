/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model.types;

import java.util.Objects;
import org.apache.poi.util.BitField;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;

@Internal
public abstract class FFDataBaseAbstractType {
    protected long field_1_version;
    protected short field_2_bits;
    private static final BitField iType = new BitField(3);
    public static final byte ITYPE_TEXT = 0;
    public static final byte ITYPE_CHCK = 1;
    public static final byte ITYPE_DROP = 2;
    private static final BitField iRes = new BitField(124);
    private static final BitField fOwnHelp = new BitField(128);
    private static final BitField fOwnStat = new BitField(256);
    private static final BitField fProt = new BitField(512);
    private static final BitField iSize = new BitField(1024);
    private static final BitField iTypeTxt = new BitField(14336);
    public static final byte ITYPETXT_REG = 0;
    public static final byte ITYPETXT_NUM = 0;
    public static final byte ITYPETXT_DATE = 0;
    public static final byte ITYPETXT_CURDATE = 0;
    public static final byte ITYPETXT_CURTIME = 0;
    protected static final byte ITYPETXT_CALC = 0;
    private static final BitField fRecalc = new BitField(16384);
    private static final BitField fHasListBox = new BitField(32768);
    protected int field_3_cch;
    protected int field_4_hps;

    protected FFDataBaseAbstractType() {
    }

    protected void fillFields(byte[] data, int offset) {
        this.field_1_version = LittleEndian.getUInt(data, 0 + offset);
        this.field_2_bits = LittleEndian.getShort(data, 4 + offset);
        this.field_3_cch = LittleEndian.getShort(data, 6 + offset);
        this.field_4_hps = LittleEndian.getShort(data, 8 + offset);
    }

    public void serialize(byte[] data, int offset) {
        LittleEndian.putUInt(data, 0 + offset, this.field_1_version);
        LittleEndian.putShort(data, 4 + offset, this.field_2_bits);
        LittleEndian.putUShort(data, 6 + offset, this.field_3_cch);
        LittleEndian.putUShort(data, 8 + offset, this.field_4_hps);
    }

    public byte[] serialize() {
        byte[] result = new byte[FFDataBaseAbstractType.getSize()];
        this.serialize(result, 0);
        return result;
    }

    public static int getSize() {
        return 10;
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
        FFDataBaseAbstractType other = (FFDataBaseAbstractType)obj;
        if (this.field_1_version != other.field_1_version) {
            return false;
        }
        if (this.field_2_bits != other.field_2_bits) {
            return false;
        }
        if (this.field_3_cch != other.field_3_cch) {
            return false;
        }
        return this.field_4_hps == other.field_4_hps;
    }

    public int hashCode() {
        return Objects.hash(this.field_1_version, this.field_2_bits, this.field_3_cch, this.field_4_hps);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[FFDataBase]\n");
        builder.append("    .version              = ");
        builder.append(" ( ").append(this.field_1_version).append(" )\n");
        builder.append("    .bits                 = ");
        builder.append(" ( ").append(this.field_2_bits).append(" )\n");
        builder.append("         .iType                    = ").append(this.getIType()).append('\n');
        builder.append("         .iRes                     = ").append(this.getIRes()).append('\n');
        builder.append("         .fOwnHelp                 = ").append(this.isFOwnHelp()).append('\n');
        builder.append("         .fOwnStat                 = ").append(this.isFOwnStat()).append('\n');
        builder.append("         .fProt                    = ").append(this.isFProt()).append('\n');
        builder.append("         .iSize                    = ").append(this.isISize()).append('\n');
        builder.append("         .iTypeTxt                 = ").append(this.getITypeTxt()).append('\n');
        builder.append("         .fRecalc                  = ").append(this.isFRecalc()).append('\n');
        builder.append("         .fHasListBox              = ").append(this.isFHasListBox()).append('\n');
        builder.append("    .cch                  = ");
        builder.append(" ( ").append(this.field_3_cch).append(" )\n");
        builder.append("    .hps                  = ");
        builder.append(" ( ").append(this.field_4_hps).append(" )\n");
        builder.append("[/FFDataBase]");
        return builder.toString();
    }

    @Internal
    public long getVersion() {
        return this.field_1_version;
    }

    @Internal
    public void setVersion(long field_1_version) {
        this.field_1_version = field_1_version;
    }

    @Internal
    public short getBits() {
        return this.field_2_bits;
    }

    @Internal
    public void setBits(short field_2_bits) {
        this.field_2_bits = field_2_bits;
    }

    @Internal
    public int getCch() {
        return this.field_3_cch;
    }

    @Internal
    public void setCch(int field_3_cch) {
        this.field_3_cch = field_3_cch;
    }

    @Internal
    public int getHps() {
        return this.field_4_hps;
    }

    @Internal
    public void setHps(int field_4_hps) {
        this.field_4_hps = field_4_hps;
    }

    @Internal
    public void setIType(byte value) {
        this.field_2_bits = (short)iType.setValue(this.field_2_bits, value);
    }

    @Internal
    public byte getIType() {
        return (byte)iType.getValue(this.field_2_bits);
    }

    @Internal
    public void setIRes(byte value) {
        this.field_2_bits = (short)iRes.setValue(this.field_2_bits, value);
    }

    @Internal
    public byte getIRes() {
        return (byte)iRes.getValue(this.field_2_bits);
    }

    @Internal
    public void setFOwnHelp(boolean value) {
        this.field_2_bits = (short)fOwnHelp.setBoolean(this.field_2_bits, value);
    }

    @Internal
    public boolean isFOwnHelp() {
        return fOwnHelp.isSet(this.field_2_bits);
    }

    @Internal
    public void setFOwnStat(boolean value) {
        this.field_2_bits = (short)fOwnStat.setBoolean(this.field_2_bits, value);
    }

    @Internal
    public boolean isFOwnStat() {
        return fOwnStat.isSet(this.field_2_bits);
    }

    @Internal
    public void setFProt(boolean value) {
        this.field_2_bits = (short)fProt.setBoolean(this.field_2_bits, value);
    }

    @Internal
    public boolean isFProt() {
        return fProt.isSet(this.field_2_bits);
    }

    @Internal
    public void setISize(boolean value) {
        this.field_2_bits = (short)iSize.setBoolean(this.field_2_bits, value);
    }

    @Internal
    public boolean isISize() {
        return iSize.isSet(this.field_2_bits);
    }

    @Internal
    public void setITypeTxt(byte value) {
        this.field_2_bits = (short)iTypeTxt.setValue(this.field_2_bits, value);
    }

    @Internal
    public byte getITypeTxt() {
        return (byte)iTypeTxt.getValue(this.field_2_bits);
    }

    @Internal
    public void setFRecalc(boolean value) {
        this.field_2_bits = (short)fRecalc.setBoolean(this.field_2_bits, value);
    }

    @Internal
    public boolean isFRecalc() {
        return fRecalc.isSet(this.field_2_bits);
    }

    @Internal
    public void setFHasListBox(boolean value) {
        this.field_2_bits = (short)fHasListBox.setBoolean(this.field_2_bits, value);
    }

    @Internal
    public boolean isFHasListBox() {
        return fHasListBox.isSet(this.field_2_bits);
    }
}


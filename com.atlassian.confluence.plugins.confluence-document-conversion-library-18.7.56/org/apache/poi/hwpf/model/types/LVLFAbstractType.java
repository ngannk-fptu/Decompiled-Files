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
public abstract class LVLFAbstractType {
    protected int field_1_iStartAt;
    protected byte field_2_nfc;
    protected byte field_3_info;
    private static final BitField jc = new BitField(3);
    private static final BitField fLegal = new BitField(4);
    private static final BitField fNoRestart = new BitField(8);
    private static final BitField fIndentSav = new BitField(16);
    private static final BitField fConverted = new BitField(32);
    private static final BitField unused1 = new BitField(64);
    private static final BitField fTentative = new BitField(128);
    protected byte[] field_4_rgbxchNums = new byte[9];
    protected byte field_5_ixchFollow;
    protected int field_6_dxaIndentSav;
    protected int field_7_unused2;
    protected short field_8_cbGrpprlChpx;
    protected short field_9_cbGrpprlPapx;
    protected short field_10_ilvlRestartLim;
    protected Grfhic field_11_grfhic = new Grfhic();

    protected LVLFAbstractType() {
    }

    protected void fillFields(byte[] data, int offset) {
        this.field_1_iStartAt = LittleEndian.getInt(data, 0 + offset);
        this.field_2_nfc = data[4 + offset];
        this.field_3_info = data[5 + offset];
        this.field_4_rgbxchNums = Arrays.copyOfRange(data, 6 + offset, 6 + offset + 9);
        this.field_5_ixchFollow = data[15 + offset];
        this.field_6_dxaIndentSav = LittleEndian.getInt(data, 16 + offset);
        this.field_7_unused2 = LittleEndian.getInt(data, 20 + offset);
        this.field_8_cbGrpprlChpx = LittleEndian.getUByte(data, 24 + offset);
        this.field_9_cbGrpprlPapx = LittleEndian.getUByte(data, 25 + offset);
        this.field_10_ilvlRestartLim = LittleEndian.getUByte(data, 26 + offset);
        this.field_11_grfhic = new Grfhic(data, 27 + offset);
    }

    public void serialize(byte[] data, int offset) {
        LittleEndian.putInt(data, 0 + offset, this.field_1_iStartAt);
        data[4 + offset] = this.field_2_nfc;
        data[5 + offset] = this.field_3_info;
        System.arraycopy(this.field_4_rgbxchNums, 0, data, 6 + offset, this.field_4_rgbxchNums.length);
        data[15 + offset] = this.field_5_ixchFollow;
        LittleEndian.putInt(data, 16 + offset, this.field_6_dxaIndentSav);
        LittleEndian.putInt(data, 20 + offset, this.field_7_unused2);
        LittleEndian.putUByte(data, 24 + offset, this.field_8_cbGrpprlChpx);
        LittleEndian.putUByte(data, 25 + offset, this.field_9_cbGrpprlPapx);
        LittleEndian.putUByte(data, 26 + offset, this.field_10_ilvlRestartLim);
        this.field_11_grfhic.serialize(data, 27 + offset);
    }

    public byte[] serialize() {
        byte[] result = new byte[LVLFAbstractType.getSize()];
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
        LVLFAbstractType other = (LVLFAbstractType)obj;
        if (this.field_1_iStartAt != other.field_1_iStartAt) {
            return false;
        }
        if (this.field_2_nfc != other.field_2_nfc) {
            return false;
        }
        if (this.field_3_info != other.field_3_info) {
            return false;
        }
        if (!Arrays.equals(this.field_4_rgbxchNums, other.field_4_rgbxchNums)) {
            return false;
        }
        if (this.field_5_ixchFollow != other.field_5_ixchFollow) {
            return false;
        }
        if (this.field_6_dxaIndentSav != other.field_6_dxaIndentSav) {
            return false;
        }
        if (this.field_7_unused2 != other.field_7_unused2) {
            return false;
        }
        if (this.field_8_cbGrpprlChpx != other.field_8_cbGrpprlChpx) {
            return false;
        }
        if (this.field_9_cbGrpprlPapx != other.field_9_cbGrpprlPapx) {
            return false;
        }
        if (this.field_10_ilvlRestartLim != other.field_10_ilvlRestartLim) {
            return false;
        }
        return !(this.field_11_grfhic == null ? other.field_11_grfhic != null : !this.field_11_grfhic.equals(other.field_11_grfhic));
    }

    public int hashCode() {
        return Arrays.deepHashCode(new Object[]{this.field_1_iStartAt, this.field_2_nfc, this.field_3_info, this.field_4_rgbxchNums, this.field_5_ixchFollow, this.field_6_dxaIndentSav, this.field_7_unused2, this.field_8_cbGrpprlChpx, this.field_9_cbGrpprlPapx, this.field_10_ilvlRestartLim, this.field_11_grfhic});
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[LVLF]\n");
        builder.append("    .iStartAt             = ");
        builder.append(" (").append(this.getIStartAt()).append(" )\n");
        builder.append("    .nfc                  = ");
        builder.append(" (").append(this.getNfc()).append(" )\n");
        builder.append("    .info                 = ");
        builder.append(" (").append(this.getInfo()).append(" )\n");
        builder.append("         .jc                       = ").append(this.getJc()).append('\n');
        builder.append("         .fLegal                   = ").append(this.isFLegal()).append('\n');
        builder.append("         .fNoRestart               = ").append(this.isFNoRestart()).append('\n');
        builder.append("         .fIndentSav               = ").append(this.isFIndentSav()).append('\n');
        builder.append("         .fConverted               = ").append(this.isFConverted()).append('\n');
        builder.append("         .unused1                  = ").append(this.isUnused1()).append('\n');
        builder.append("         .fTentative               = ").append(this.isFTentative()).append('\n');
        builder.append("    .rgbxchNums           = ");
        builder.append(" (").append(Arrays.toString(this.getRgbxchNums())).append(" )\n");
        builder.append("    .ixchFollow           = ");
        builder.append(" (").append(this.getIxchFollow()).append(" )\n");
        builder.append("    .dxaIndentSav         = ");
        builder.append(" (").append(this.getDxaIndentSav()).append(" )\n");
        builder.append("    .unused2              = ");
        builder.append(" (").append(this.getUnused2()).append(" )\n");
        builder.append("    .cbGrpprlChpx         = ");
        builder.append(" (").append(this.getCbGrpprlChpx()).append(" )\n");
        builder.append("    .cbGrpprlPapx         = ");
        builder.append(" (").append(this.getCbGrpprlPapx()).append(" )\n");
        builder.append("    .ilvlRestartLim       = ");
        builder.append(" (").append(this.getIlvlRestartLim()).append(" )\n");
        builder.append("    .grfhic               = ");
        builder.append(" (").append(this.getGrfhic()).append(" )\n");
        builder.append("[/LVLF]\n");
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
    public byte getNfc() {
        return this.field_2_nfc;
    }

    @Internal
    public void setNfc(byte field_2_nfc) {
        this.field_2_nfc = field_2_nfc;
    }

    @Internal
    public byte getInfo() {
        return this.field_3_info;
    }

    @Internal
    public void setInfo(byte field_3_info) {
        this.field_3_info = field_3_info;
    }

    @Internal
    public byte[] getRgbxchNums() {
        return this.field_4_rgbxchNums;
    }

    @Internal
    public void setRgbxchNums(byte[] field_4_rgbxchNums) {
        this.field_4_rgbxchNums = field_4_rgbxchNums;
    }

    @Internal
    public byte getIxchFollow() {
        return this.field_5_ixchFollow;
    }

    @Internal
    public void setIxchFollow(byte field_5_ixchFollow) {
        this.field_5_ixchFollow = field_5_ixchFollow;
    }

    @Internal
    public int getDxaIndentSav() {
        return this.field_6_dxaIndentSav;
    }

    @Internal
    public void setDxaIndentSav(int field_6_dxaIndentSav) {
        this.field_6_dxaIndentSav = field_6_dxaIndentSav;
    }

    @Internal
    public int getUnused2() {
        return this.field_7_unused2;
    }

    @Internal
    public void setUnused2(int field_7_unused2) {
        this.field_7_unused2 = field_7_unused2;
    }

    @Internal
    public short getCbGrpprlChpx() {
        return this.field_8_cbGrpprlChpx;
    }

    @Internal
    public void setCbGrpprlChpx(short field_8_cbGrpprlChpx) {
        this.field_8_cbGrpprlChpx = field_8_cbGrpprlChpx;
    }

    @Internal
    public short getCbGrpprlPapx() {
        return this.field_9_cbGrpprlPapx;
    }

    @Internal
    public void setCbGrpprlPapx(short field_9_cbGrpprlPapx) {
        this.field_9_cbGrpprlPapx = field_9_cbGrpprlPapx;
    }

    @Internal
    public short getIlvlRestartLim() {
        return this.field_10_ilvlRestartLim;
    }

    @Internal
    public void setIlvlRestartLim(short field_10_ilvlRestartLim) {
        this.field_10_ilvlRestartLim = field_10_ilvlRestartLim;
    }

    @Internal
    public Grfhic getGrfhic() {
        return this.field_11_grfhic;
    }

    @Internal
    public void setGrfhic(Grfhic field_11_grfhic) {
        this.field_11_grfhic = field_11_grfhic;
    }

    @Internal
    public void setJc(byte value) {
        this.field_3_info = (byte)jc.setValue(this.field_3_info, value);
    }

    @Internal
    public byte getJc() {
        return (byte)jc.getValue(this.field_3_info);
    }

    @Internal
    public void setFLegal(boolean value) {
        this.field_3_info = (byte)fLegal.setBoolean(this.field_3_info, value);
    }

    @Internal
    public boolean isFLegal() {
        return fLegal.isSet(this.field_3_info);
    }

    @Internal
    public void setFNoRestart(boolean value) {
        this.field_3_info = (byte)fNoRestart.setBoolean(this.field_3_info, value);
    }

    @Internal
    public boolean isFNoRestart() {
        return fNoRestart.isSet(this.field_3_info);
    }

    @Internal
    public void setFIndentSav(boolean value) {
        this.field_3_info = (byte)fIndentSav.setBoolean(this.field_3_info, value);
    }

    @Internal
    public boolean isFIndentSav() {
        return fIndentSav.isSet(this.field_3_info);
    }

    @Internal
    public void setFConverted(boolean value) {
        this.field_3_info = (byte)fConverted.setBoolean(this.field_3_info, value);
    }

    @Internal
    public boolean isFConverted() {
        return fConverted.isSet(this.field_3_info);
    }

    @Internal
    public void setUnused1(boolean value) {
        this.field_3_info = (byte)unused1.setBoolean(this.field_3_info, value);
    }

    @Internal
    @Deprecated
    public boolean isUnused1() {
        return unused1.isSet(this.field_3_info);
    }

    @Internal
    public void setFTentative(boolean value) {
        this.field_3_info = (byte)fTentative.setBoolean(this.field_3_info, value);
    }

    @Internal
    public boolean isFTentative() {
        return fTentative.isSet(this.field_3_info);
    }
}


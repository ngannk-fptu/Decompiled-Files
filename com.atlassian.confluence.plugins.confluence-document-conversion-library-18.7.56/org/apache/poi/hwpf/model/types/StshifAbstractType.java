/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model.types;

import org.apache.poi.util.BitField;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;

@Internal
public abstract class StshifAbstractType {
    protected int field_1_cstd;
    protected int field_2_cbSTDBaseInFile;
    protected int field_3_info3;
    private static final BitField fHasOriginalStyle = new BitField(1);
    private static final BitField fReserved = new BitField(65534);
    protected int field_4_stiMaxWhenSaved;
    protected int field_5_istdMaxFixedWhenSaved;
    protected int field_6_nVerBuiltInNamesWhenSaved;
    protected short field_7_ftcAsci;
    protected short field_8_ftcFE;
    protected short field_9_ftcOther;

    protected StshifAbstractType() {
    }

    protected void fillFields(byte[] data, int offset) {
        this.field_1_cstd = LittleEndian.getShort(data, 0 + offset);
        this.field_2_cbSTDBaseInFile = LittleEndian.getShort(data, 2 + offset);
        this.field_3_info3 = LittleEndian.getShort(data, 4 + offset);
        this.field_4_stiMaxWhenSaved = LittleEndian.getShort(data, 6 + offset);
        this.field_5_istdMaxFixedWhenSaved = LittleEndian.getShort(data, 8 + offset);
        this.field_6_nVerBuiltInNamesWhenSaved = LittleEndian.getShort(data, 10 + offset);
        this.field_7_ftcAsci = LittleEndian.getShort(data, 12 + offset);
        this.field_8_ftcFE = LittleEndian.getShort(data, 14 + offset);
        this.field_9_ftcOther = LittleEndian.getShort(data, 16 + offset);
    }

    public void serialize(byte[] data, int offset) {
        LittleEndian.putUShort(data, 0 + offset, this.field_1_cstd);
        LittleEndian.putUShort(data, 2 + offset, this.field_2_cbSTDBaseInFile);
        LittleEndian.putUShort(data, 4 + offset, this.field_3_info3);
        LittleEndian.putUShort(data, 6 + offset, this.field_4_stiMaxWhenSaved);
        LittleEndian.putUShort(data, 8 + offset, this.field_5_istdMaxFixedWhenSaved);
        LittleEndian.putUShort(data, 10 + offset, this.field_6_nVerBuiltInNamesWhenSaved);
        LittleEndian.putShort(data, 12 + offset, this.field_7_ftcAsci);
        LittleEndian.putShort(data, 14 + offset, this.field_8_ftcFE);
        LittleEndian.putShort(data, 16 + offset, this.field_9_ftcOther);
    }

    public static int getSize() {
        return 18;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[Stshif]\n");
        builder.append("    .cstd                 = ");
        builder.append(" (").append(this.getCstd()).append(" )\n");
        builder.append("    .cbSTDBaseInFile      = ");
        builder.append(" (").append(this.getCbSTDBaseInFile()).append(" )\n");
        builder.append("    .info3                = ");
        builder.append(" (").append(this.getInfo3()).append(" )\n");
        builder.append("         .fHasOriginalStyle        = ").append(this.isFHasOriginalStyle()).append('\n');
        builder.append("         .fReserved                = ").append(this.getFReserved()).append('\n');
        builder.append("    .stiMaxWhenSaved      = ");
        builder.append(" (").append(this.getStiMaxWhenSaved()).append(" )\n");
        builder.append("    .istdMaxFixedWhenSaved = ");
        builder.append(" (").append(this.getIstdMaxFixedWhenSaved()).append(" )\n");
        builder.append("    .nVerBuiltInNamesWhenSaved = ");
        builder.append(" (").append(this.getNVerBuiltInNamesWhenSaved()).append(" )\n");
        builder.append("    .ftcAsci              = ");
        builder.append(" (").append(this.getFtcAsci()).append(" )\n");
        builder.append("    .ftcFE                = ");
        builder.append(" (").append(this.getFtcFE()).append(" )\n");
        builder.append("    .ftcOther             = ");
        builder.append(" (").append(this.getFtcOther()).append(" )\n");
        builder.append("[/Stshif]\n");
        return builder.toString();
    }

    @Internal
    public int getCstd() {
        return this.field_1_cstd;
    }

    @Internal
    public void setCstd(int field_1_cstd) {
        this.field_1_cstd = field_1_cstd;
    }

    @Internal
    public int getCbSTDBaseInFile() {
        return this.field_2_cbSTDBaseInFile;
    }

    @Internal
    public void setCbSTDBaseInFile(int field_2_cbSTDBaseInFile) {
        this.field_2_cbSTDBaseInFile = field_2_cbSTDBaseInFile;
    }

    @Internal
    public int getInfo3() {
        return this.field_3_info3;
    }

    @Internal
    public void setInfo3(int field_3_info3) {
        this.field_3_info3 = field_3_info3;
    }

    @Internal
    public int getStiMaxWhenSaved() {
        return this.field_4_stiMaxWhenSaved;
    }

    @Internal
    public void setStiMaxWhenSaved(int field_4_stiMaxWhenSaved) {
        this.field_4_stiMaxWhenSaved = field_4_stiMaxWhenSaved;
    }

    @Internal
    public int getIstdMaxFixedWhenSaved() {
        return this.field_5_istdMaxFixedWhenSaved;
    }

    @Internal
    public void setIstdMaxFixedWhenSaved(int field_5_istdMaxFixedWhenSaved) {
        this.field_5_istdMaxFixedWhenSaved = field_5_istdMaxFixedWhenSaved;
    }

    @Internal
    public int getNVerBuiltInNamesWhenSaved() {
        return this.field_6_nVerBuiltInNamesWhenSaved;
    }

    @Internal
    public void setNVerBuiltInNamesWhenSaved(int field_6_nVerBuiltInNamesWhenSaved) {
        this.field_6_nVerBuiltInNamesWhenSaved = field_6_nVerBuiltInNamesWhenSaved;
    }

    @Internal
    public short getFtcAsci() {
        return this.field_7_ftcAsci;
    }

    @Internal
    public void setFtcAsci(short field_7_ftcAsci) {
        this.field_7_ftcAsci = field_7_ftcAsci;
    }

    @Internal
    public short getFtcFE() {
        return this.field_8_ftcFE;
    }

    @Internal
    public void setFtcFE(short field_8_ftcFE) {
        this.field_8_ftcFE = field_8_ftcFE;
    }

    @Internal
    public short getFtcOther() {
        return this.field_9_ftcOther;
    }

    @Internal
    public void setFtcOther(short field_9_ftcOther) {
        this.field_9_ftcOther = field_9_ftcOther;
    }

    @Internal
    public void setFHasOriginalStyle(boolean value) {
        this.field_3_info3 = fHasOriginalStyle.setBoolean(this.field_3_info3, value);
    }

    @Internal
    public boolean isFHasOriginalStyle() {
        return fHasOriginalStyle.isSet(this.field_3_info3);
    }

    @Internal
    public void setFReserved(short value) {
        this.field_3_info3 = fReserved.setValue(this.field_3_info3, value);
    }

    @Internal
    public short getFReserved() {
        return (short)fReserved.getValue(this.field_3_info3);
    }
}


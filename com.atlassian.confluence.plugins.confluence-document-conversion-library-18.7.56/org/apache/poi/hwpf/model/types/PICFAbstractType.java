/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model.types;

import java.util.Arrays;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;

@Internal
public abstract class PICFAbstractType {
    protected int field_1_lcb;
    protected int field_2_cbHeader;
    protected short field_3_mm;
    protected short field_4_xExt;
    protected short field_5_yExt;
    protected short field_6_swHMF;
    protected int field_7_grf;
    protected int field_8_padding;
    protected int field_9_mmPM;
    protected int field_10_padding2;
    protected short field_11_dxaGoal;
    protected short field_12_dyaGoal;
    protected int field_13_mx;
    protected int field_14_my;
    protected short field_15_dxaReserved1;
    protected short field_16_dyaReserved1;
    protected short field_17_dxaReserved2;
    protected short field_18_dyaReserved2;
    protected byte field_19_fReserved;
    protected byte field_20_bpp;
    protected byte[] field_21_brcTop80 = new byte[4];
    protected byte[] field_22_brcLeft80 = new byte[4];
    protected byte[] field_23_brcBottom80 = new byte[4];
    protected byte[] field_24_brcRight80 = new byte[4];
    protected short field_25_dxaReserved3;
    protected short field_26_dyaReserved3;
    protected short field_27_cProps;

    protected PICFAbstractType() {
    }

    protected void fillFields(byte[] data, int offset) {
        this.field_1_lcb = LittleEndian.getInt(data, 0 + offset);
        this.field_2_cbHeader = LittleEndian.getShort(data, 4 + offset);
        this.field_3_mm = LittleEndian.getShort(data, 6 + offset);
        this.field_4_xExt = LittleEndian.getShort(data, 8 + offset);
        this.field_5_yExt = LittleEndian.getShort(data, 10 + offset);
        this.field_6_swHMF = LittleEndian.getShort(data, 12 + offset);
        this.field_7_grf = LittleEndian.getInt(data, 14 + offset);
        this.field_8_padding = LittleEndian.getInt(data, 18 + offset);
        this.field_9_mmPM = LittleEndian.getShort(data, 22 + offset);
        this.field_10_padding2 = LittleEndian.getInt(data, 24 + offset);
        this.field_11_dxaGoal = LittleEndian.getShort(data, 28 + offset);
        this.field_12_dyaGoal = LittleEndian.getShort(data, 30 + offset);
        this.field_13_mx = LittleEndian.getShort(data, 32 + offset);
        this.field_14_my = LittleEndian.getShort(data, 34 + offset);
        this.field_15_dxaReserved1 = LittleEndian.getShort(data, 36 + offset);
        this.field_16_dyaReserved1 = LittleEndian.getShort(data, 38 + offset);
        this.field_17_dxaReserved2 = LittleEndian.getShort(data, 40 + offset);
        this.field_18_dyaReserved2 = LittleEndian.getShort(data, 42 + offset);
        this.field_19_fReserved = data[44 + offset];
        this.field_20_bpp = data[45 + offset];
        this.field_21_brcTop80 = Arrays.copyOfRange(data, 46 + offset, 46 + offset + 4);
        this.field_22_brcLeft80 = Arrays.copyOfRange(data, 50 + offset, 50 + offset + 4);
        this.field_23_brcBottom80 = Arrays.copyOfRange(data, 54 + offset, 54 + offset + 4);
        this.field_24_brcRight80 = Arrays.copyOfRange(data, 58 + offset, 58 + offset + 4);
        this.field_25_dxaReserved3 = LittleEndian.getShort(data, 62 + offset);
        this.field_26_dyaReserved3 = LittleEndian.getShort(data, 64 + offset);
        this.field_27_cProps = LittleEndian.getShort(data, 66 + offset);
    }

    public void serialize(byte[] data, int offset) {
        LittleEndian.putInt(data, 0 + offset, this.field_1_lcb);
        LittleEndian.putUShort(data, 4 + offset, this.field_2_cbHeader);
        LittleEndian.putShort(data, 6 + offset, this.field_3_mm);
        LittleEndian.putShort(data, 8 + offset, this.field_4_xExt);
        LittleEndian.putShort(data, 10 + offset, this.field_5_yExt);
        LittleEndian.putShort(data, 12 + offset, this.field_6_swHMF);
        LittleEndian.putInt(data, 14 + offset, this.field_7_grf);
        LittleEndian.putInt(data, 18 + offset, this.field_8_padding);
        LittleEndian.putUShort(data, 22 + offset, this.field_9_mmPM);
        LittleEndian.putInt(data, 24 + offset, this.field_10_padding2);
        LittleEndian.putShort(data, 28 + offset, this.field_11_dxaGoal);
        LittleEndian.putShort(data, 30 + offset, this.field_12_dyaGoal);
        LittleEndian.putUShort(data, 32 + offset, this.field_13_mx);
        LittleEndian.putUShort(data, 34 + offset, this.field_14_my);
        LittleEndian.putShort(data, 36 + offset, this.field_15_dxaReserved1);
        LittleEndian.putShort(data, 38 + offset, this.field_16_dyaReserved1);
        LittleEndian.putShort(data, 40 + offset, this.field_17_dxaReserved2);
        LittleEndian.putShort(data, 42 + offset, this.field_18_dyaReserved2);
        data[44 + offset] = this.field_19_fReserved;
        data[45 + offset] = this.field_20_bpp;
        System.arraycopy(this.field_21_brcTop80, 0, data, 46 + offset, this.field_21_brcTop80.length);
        System.arraycopy(this.field_22_brcLeft80, 0, data, 50 + offset, this.field_22_brcLeft80.length);
        System.arraycopy(this.field_23_brcBottom80, 0, data, 54 + offset, this.field_23_brcBottom80.length);
        System.arraycopy(this.field_24_brcRight80, 0, data, 58 + offset, this.field_24_brcRight80.length);
        LittleEndian.putShort(data, 62 + offset, this.field_25_dxaReserved3);
        LittleEndian.putShort(data, 64 + offset, this.field_26_dyaReserved3);
        LittleEndian.putShort(data, 66 + offset, this.field_27_cProps);
    }

    public byte[] serialize() {
        byte[] result = new byte[PICFAbstractType.getSize()];
        this.serialize(result, 0);
        return result;
    }

    public static int getSize() {
        return 68;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[PICF]\n");
        builder.append("    .lcb                  = ");
        builder.append(" (").append(this.getLcb()).append(" )\n");
        builder.append("    .cbHeader             = ");
        builder.append(" (").append(this.getCbHeader()).append(" )\n");
        builder.append("    .mm                   = ");
        builder.append(" (").append(this.getMm()).append(" )\n");
        builder.append("    .xExt                 = ");
        builder.append(" (").append(this.getXExt()).append(" )\n");
        builder.append("    .yExt                 = ");
        builder.append(" (").append(this.getYExt()).append(" )\n");
        builder.append("    .swHMF                = ");
        builder.append(" (").append(this.getSwHMF()).append(" )\n");
        builder.append("    .grf                  = ");
        builder.append(" (").append(this.getGrf()).append(" )\n");
        builder.append("    .padding              = ");
        builder.append(" (").append(this.getPadding()).append(" )\n");
        builder.append("    .mmPM                 = ");
        builder.append(" (").append(this.getMmPM()).append(" )\n");
        builder.append("    .padding2             = ");
        builder.append(" (").append(this.getPadding2()).append(" )\n");
        builder.append("    .dxaGoal              = ");
        builder.append(" (").append(this.getDxaGoal()).append(" )\n");
        builder.append("    .dyaGoal              = ");
        builder.append(" (").append(this.getDyaGoal()).append(" )\n");
        builder.append("    .mx                   = ");
        builder.append(" (").append(this.getMx()).append(" )\n");
        builder.append("    .my                   = ");
        builder.append(" (").append(this.getMy()).append(" )\n");
        builder.append("    .dxaReserved1         = ");
        builder.append(" (").append(this.getDxaReserved1()).append(" )\n");
        builder.append("    .dyaReserved1         = ");
        builder.append(" (").append(this.getDyaReserved1()).append(" )\n");
        builder.append("    .dxaReserved2         = ");
        builder.append(" (").append(this.getDxaReserved2()).append(" )\n");
        builder.append("    .dyaReserved2         = ");
        builder.append(" (").append(this.getDyaReserved2()).append(" )\n");
        builder.append("    .fReserved            = ");
        builder.append(" (").append(this.getFReserved()).append(" )\n");
        builder.append("    .bpp                  = ");
        builder.append(" (").append(this.getBpp()).append(" )\n");
        builder.append("    .brcTop80             = ");
        builder.append(" (").append(Arrays.toString(this.getBrcTop80())).append(" )\n");
        builder.append("    .brcLeft80            = ");
        builder.append(" (").append(Arrays.toString(this.getBrcLeft80())).append(" )\n");
        builder.append("    .brcBottom80          = ");
        builder.append(" (").append(Arrays.toString(this.getBrcBottom80())).append(" )\n");
        builder.append("    .brcRight80           = ");
        builder.append(" (").append(Arrays.toString(this.getBrcRight80())).append(" )\n");
        builder.append("    .dxaReserved3         = ");
        builder.append(" (").append(this.getDxaReserved3()).append(" )\n");
        builder.append("    .dyaReserved3         = ");
        builder.append(" (").append(this.getDyaReserved3()).append(" )\n");
        builder.append("    .cProps               = ");
        builder.append(" (").append(this.getCProps()).append(" )\n");
        builder.append("[/PICF]\n");
        return builder.toString();
    }

    @Internal
    public int getLcb() {
        return this.field_1_lcb;
    }

    @Internal
    public void setLcb(int field_1_lcb) {
        this.field_1_lcb = field_1_lcb;
    }

    @Internal
    public int getCbHeader() {
        return this.field_2_cbHeader;
    }

    @Internal
    public void setCbHeader(int field_2_cbHeader) {
        this.field_2_cbHeader = field_2_cbHeader;
    }

    @Internal
    public short getMm() {
        return this.field_3_mm;
    }

    @Internal
    public void setMm(short field_3_mm) {
        this.field_3_mm = field_3_mm;
    }

    @Internal
    public short getXExt() {
        return this.field_4_xExt;
    }

    @Internal
    public void setXExt(short field_4_xExt) {
        this.field_4_xExt = field_4_xExt;
    }

    @Internal
    public short getYExt() {
        return this.field_5_yExt;
    }

    @Internal
    public void setYExt(short field_5_yExt) {
        this.field_5_yExt = field_5_yExt;
    }

    @Internal
    public short getSwHMF() {
        return this.field_6_swHMF;
    }

    @Internal
    public void setSwHMF(short field_6_swHMF) {
        this.field_6_swHMF = field_6_swHMF;
    }

    @Internal
    public int getGrf() {
        return this.field_7_grf;
    }

    @Internal
    public void setGrf(int field_7_grf) {
        this.field_7_grf = field_7_grf;
    }

    @Internal
    public int getPadding() {
        return this.field_8_padding;
    }

    @Internal
    public void setPadding(int field_8_padding) {
        this.field_8_padding = field_8_padding;
    }

    @Internal
    public int getMmPM() {
        return this.field_9_mmPM;
    }

    @Internal
    public void setMmPM(int field_9_mmPM) {
        this.field_9_mmPM = field_9_mmPM;
    }

    @Internal
    public int getPadding2() {
        return this.field_10_padding2;
    }

    @Internal
    public void setPadding2(int field_10_padding2) {
        this.field_10_padding2 = field_10_padding2;
    }

    @Internal
    public short getDxaGoal() {
        return this.field_11_dxaGoal;
    }

    @Internal
    public void setDxaGoal(short field_11_dxaGoal) {
        this.field_11_dxaGoal = field_11_dxaGoal;
    }

    @Internal
    public short getDyaGoal() {
        return this.field_12_dyaGoal;
    }

    @Internal
    public void setDyaGoal(short field_12_dyaGoal) {
        this.field_12_dyaGoal = field_12_dyaGoal;
    }

    @Internal
    public int getMx() {
        return this.field_13_mx;
    }

    @Internal
    public void setMx(int field_13_mx) {
        this.field_13_mx = field_13_mx;
    }

    @Internal
    public int getMy() {
        return this.field_14_my;
    }

    @Internal
    public void setMy(int field_14_my) {
        this.field_14_my = field_14_my;
    }

    @Internal
    public short getDxaReserved1() {
        return this.field_15_dxaReserved1;
    }

    @Internal
    public void setDxaReserved1(short field_15_dxaReserved1) {
        this.field_15_dxaReserved1 = field_15_dxaReserved1;
    }

    @Internal
    public short getDyaReserved1() {
        return this.field_16_dyaReserved1;
    }

    @Internal
    public void setDyaReserved1(short field_16_dyaReserved1) {
        this.field_16_dyaReserved1 = field_16_dyaReserved1;
    }

    @Internal
    public short getDxaReserved2() {
        return this.field_17_dxaReserved2;
    }

    @Internal
    public void setDxaReserved2(short field_17_dxaReserved2) {
        this.field_17_dxaReserved2 = field_17_dxaReserved2;
    }

    @Internal
    public short getDyaReserved2() {
        return this.field_18_dyaReserved2;
    }

    @Internal
    public void setDyaReserved2(short field_18_dyaReserved2) {
        this.field_18_dyaReserved2 = field_18_dyaReserved2;
    }

    @Internal
    public byte getFReserved() {
        return this.field_19_fReserved;
    }

    @Internal
    public void setFReserved(byte field_19_fReserved) {
        this.field_19_fReserved = field_19_fReserved;
    }

    @Internal
    public byte getBpp() {
        return this.field_20_bpp;
    }

    @Internal
    public void setBpp(byte field_20_bpp) {
        this.field_20_bpp = field_20_bpp;
    }

    @Internal
    public byte[] getBrcTop80() {
        return this.field_21_brcTop80;
    }

    @Internal
    public void setBrcTop80(byte[] field_21_brcTop80) {
        this.field_21_brcTop80 = field_21_brcTop80;
    }

    @Internal
    public byte[] getBrcLeft80() {
        return this.field_22_brcLeft80;
    }

    @Internal
    public void setBrcLeft80(byte[] field_22_brcLeft80) {
        this.field_22_brcLeft80 = field_22_brcLeft80;
    }

    @Internal
    public byte[] getBrcBottom80() {
        return this.field_23_brcBottom80;
    }

    @Internal
    public void setBrcBottom80(byte[] field_23_brcBottom80) {
        this.field_23_brcBottom80 = field_23_brcBottom80;
    }

    @Internal
    public byte[] getBrcRight80() {
        return this.field_24_brcRight80;
    }

    @Internal
    public void setBrcRight80(byte[] field_24_brcRight80) {
        this.field_24_brcRight80 = field_24_brcRight80;
    }

    @Internal
    public short getDxaReserved3() {
        return this.field_25_dxaReserved3;
    }

    @Internal
    public void setDxaReserved3(short field_25_dxaReserved3) {
        this.field_25_dxaReserved3 = field_25_dxaReserved3;
    }

    @Internal
    public short getDyaReserved3() {
        return this.field_26_dyaReserved3;
    }

    @Internal
    public void setDyaReserved3(short field_26_dyaReserved3) {
        this.field_26_dyaReserved3 = field_26_dyaReserved3;
    }

    @Internal
    public short getCProps() {
        return this.field_27_cProps;
    }

    @Internal
    public void setCProps(short field_27_cProps) {
        this.field_27_cProps = field_27_cProps;
    }
}


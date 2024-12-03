/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model.types;

import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;

@Internal
public abstract class FibRgW97AbstractType {
    @Deprecated
    protected short field_1_reserved1;
    @Deprecated
    protected short field_2_reserved2;
    @Deprecated
    protected short field_3_reserved3;
    @Deprecated
    protected short field_4_reserved4;
    @Deprecated
    protected short field_5_reserved5;
    @Deprecated
    protected short field_6_reserved6;
    @Deprecated
    protected short field_7_reserved7;
    @Deprecated
    protected short field_8_reserved8;
    @Deprecated
    protected short field_9_reserved9;
    @Deprecated
    protected short field_10_reserved10;
    @Deprecated
    protected short field_11_reserved11;
    @Deprecated
    protected short field_12_reserved12;
    @Deprecated
    protected short field_13_reserved13;
    protected short field_14_lidFE;

    protected FibRgW97AbstractType() {
    }

    protected void fillFields(byte[] data, int offset) {
        this.field_1_reserved1 = LittleEndian.getShort(data, 0 + offset);
        this.field_2_reserved2 = LittleEndian.getShort(data, 2 + offset);
        this.field_3_reserved3 = LittleEndian.getShort(data, 4 + offset);
        this.field_4_reserved4 = LittleEndian.getShort(data, 6 + offset);
        this.field_5_reserved5 = LittleEndian.getShort(data, 8 + offset);
        this.field_6_reserved6 = LittleEndian.getShort(data, 10 + offset);
        this.field_7_reserved7 = LittleEndian.getShort(data, 12 + offset);
        this.field_8_reserved8 = LittleEndian.getShort(data, 14 + offset);
        this.field_9_reserved9 = LittleEndian.getShort(data, 16 + offset);
        this.field_10_reserved10 = LittleEndian.getShort(data, 18 + offset);
        this.field_11_reserved11 = LittleEndian.getShort(data, 20 + offset);
        this.field_12_reserved12 = LittleEndian.getShort(data, 22 + offset);
        this.field_13_reserved13 = LittleEndian.getShort(data, 24 + offset);
        this.field_14_lidFE = LittleEndian.getShort(data, 26 + offset);
    }

    public void serialize(byte[] data, int offset) {
        LittleEndian.putShort(data, 0 + offset, this.field_1_reserved1);
        LittleEndian.putShort(data, 2 + offset, this.field_2_reserved2);
        LittleEndian.putShort(data, 4 + offset, this.field_3_reserved3);
        LittleEndian.putShort(data, 6 + offset, this.field_4_reserved4);
        LittleEndian.putShort(data, 8 + offset, this.field_5_reserved5);
        LittleEndian.putShort(data, 10 + offset, this.field_6_reserved6);
        LittleEndian.putShort(data, 12 + offset, this.field_7_reserved7);
        LittleEndian.putShort(data, 14 + offset, this.field_8_reserved8);
        LittleEndian.putShort(data, 16 + offset, this.field_9_reserved9);
        LittleEndian.putShort(data, 18 + offset, this.field_10_reserved10);
        LittleEndian.putShort(data, 20 + offset, this.field_11_reserved11);
        LittleEndian.putShort(data, 22 + offset, this.field_12_reserved12);
        LittleEndian.putShort(data, 24 + offset, this.field_13_reserved13);
        LittleEndian.putShort(data, 26 + offset, this.field_14_lidFE);
    }

    public byte[] serialize() {
        byte[] result = new byte[FibRgW97AbstractType.getSize()];
        this.serialize(result, 0);
        return result;
    }

    public static int getSize() {
        return 28;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[FibRgW97]\n");
        builder.append("    .reserved1            = ");
        builder.append(" (").append(this.getReserved1()).append(" )\n");
        builder.append("    .reserved2            = ");
        builder.append(" (").append(this.getReserved2()).append(" )\n");
        builder.append("    .reserved3            = ");
        builder.append(" (").append(this.getReserved3()).append(" )\n");
        builder.append("    .reserved4            = ");
        builder.append(" (").append(this.getReserved4()).append(" )\n");
        builder.append("    .reserved5            = ");
        builder.append(" (").append(this.getReserved5()).append(" )\n");
        builder.append("    .reserved6            = ");
        builder.append(" (").append(this.getReserved6()).append(" )\n");
        builder.append("    .reserved7            = ");
        builder.append(" (").append(this.getReserved7()).append(" )\n");
        builder.append("    .reserved8            = ");
        builder.append(" (").append(this.getReserved8()).append(" )\n");
        builder.append("    .reserved9            = ");
        builder.append(" (").append(this.getReserved9()).append(" )\n");
        builder.append("    .reserved10           = ");
        builder.append(" (").append(this.getReserved10()).append(" )\n");
        builder.append("    .reserved11           = ");
        builder.append(" (").append(this.getReserved11()).append(" )\n");
        builder.append("    .reserved12           = ");
        builder.append(" (").append(this.getReserved12()).append(" )\n");
        builder.append("    .reserved13           = ");
        builder.append(" (").append(this.getReserved13()).append(" )\n");
        builder.append("    .lidFE                = ");
        builder.append(" (").append(this.getLidFE()).append(" )\n");
        builder.append("[/FibRgW97]\n");
        return builder.toString();
    }

    @Internal
    public short getReserved1() {
        return this.field_1_reserved1;
    }

    @Internal
    public void setReserved1(short field_1_reserved1) {
        this.field_1_reserved1 = field_1_reserved1;
    }

    @Internal
    public short getReserved2() {
        return this.field_2_reserved2;
    }

    @Internal
    public void setReserved2(short field_2_reserved2) {
        this.field_2_reserved2 = field_2_reserved2;
    }

    @Internal
    public short getReserved3() {
        return this.field_3_reserved3;
    }

    @Internal
    public void setReserved3(short field_3_reserved3) {
        this.field_3_reserved3 = field_3_reserved3;
    }

    @Internal
    public short getReserved4() {
        return this.field_4_reserved4;
    }

    @Internal
    public void setReserved4(short field_4_reserved4) {
        this.field_4_reserved4 = field_4_reserved4;
    }

    @Internal
    public short getReserved5() {
        return this.field_5_reserved5;
    }

    @Internal
    public void setReserved5(short field_5_reserved5) {
        this.field_5_reserved5 = field_5_reserved5;
    }

    @Internal
    public short getReserved6() {
        return this.field_6_reserved6;
    }

    @Internal
    public void setReserved6(short field_6_reserved6) {
        this.field_6_reserved6 = field_6_reserved6;
    }

    @Internal
    public short getReserved7() {
        return this.field_7_reserved7;
    }

    @Internal
    public void setReserved7(short field_7_reserved7) {
        this.field_7_reserved7 = field_7_reserved7;
    }

    @Internal
    public short getReserved8() {
        return this.field_8_reserved8;
    }

    @Internal
    public void setReserved8(short field_8_reserved8) {
        this.field_8_reserved8 = field_8_reserved8;
    }

    @Internal
    public short getReserved9() {
        return this.field_9_reserved9;
    }

    @Internal
    public void setReserved9(short field_9_reserved9) {
        this.field_9_reserved9 = field_9_reserved9;
    }

    @Internal
    public short getReserved10() {
        return this.field_10_reserved10;
    }

    @Internal
    public void setReserved10(short field_10_reserved10) {
        this.field_10_reserved10 = field_10_reserved10;
    }

    @Internal
    public short getReserved11() {
        return this.field_11_reserved11;
    }

    @Internal
    public void setReserved11(short field_11_reserved11) {
        this.field_11_reserved11 = field_11_reserved11;
    }

    @Internal
    public short getReserved12() {
        return this.field_12_reserved12;
    }

    @Internal
    public void setReserved12(short field_12_reserved12) {
        this.field_12_reserved12 = field_12_reserved12;
    }

    @Internal
    public short getReserved13() {
        return this.field_13_reserved13;
    }

    @Internal
    public void setReserved13(short field_13_reserved13) {
        this.field_13_reserved13 = field_13_reserved13;
    }

    @Internal
    public short getLidFE() {
        return this.field_14_lidFE;
    }

    @Internal
    public void setLidFE(short field_14_lidFE) {
        this.field_14_lidFE = field_14_lidFE;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model.types;

import java.util.Objects;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;

@Internal
public abstract class FibRgLw97AbstractType {
    protected int field_1_cbMac;
    @Deprecated
    protected int field_2_reserved1;
    @Deprecated
    protected int field_3_reserved2;
    protected int field_4_ccpText;
    protected int field_5_ccpFtn;
    protected int field_6_ccpHdd;
    @Deprecated
    protected int field_7_reserved3;
    protected int field_8_ccpAtn;
    protected int field_9_ccpEdn;
    protected int field_10_ccpTxbx;
    protected int field_11_ccpHdrTxbx;
    @Deprecated
    protected int field_12_reserved4;
    @Deprecated
    protected int field_13_reserved5;
    @Deprecated
    protected int field_14_reserved6;
    @Deprecated
    protected int field_15_reserved7;
    @Deprecated
    protected int field_16_reserved8;
    @Deprecated
    protected int field_17_reserved9;
    @Deprecated
    protected int field_18_reserved10;
    @Deprecated
    protected int field_19_reserved11;
    @Deprecated
    protected int field_20_reserved12;
    @Deprecated
    protected int field_21_reserved13;
    @Deprecated
    protected int field_22_reserved14;

    protected FibRgLw97AbstractType() {
    }

    protected void fillFields(byte[] data, int offset) {
        this.field_1_cbMac = LittleEndian.getInt(data, 0 + offset);
        this.field_2_reserved1 = LittleEndian.getInt(data, 4 + offset);
        this.field_3_reserved2 = LittleEndian.getInt(data, 8 + offset);
        this.field_4_ccpText = LittleEndian.getInt(data, 12 + offset);
        this.field_5_ccpFtn = LittleEndian.getInt(data, 16 + offset);
        this.field_6_ccpHdd = LittleEndian.getInt(data, 20 + offset);
        this.field_7_reserved3 = LittleEndian.getInt(data, 24 + offset);
        this.field_8_ccpAtn = LittleEndian.getInt(data, 28 + offset);
        this.field_9_ccpEdn = LittleEndian.getInt(data, 32 + offset);
        this.field_10_ccpTxbx = LittleEndian.getInt(data, 36 + offset);
        this.field_11_ccpHdrTxbx = LittleEndian.getInt(data, 40 + offset);
        this.field_12_reserved4 = LittleEndian.getInt(data, 44 + offset);
        this.field_13_reserved5 = LittleEndian.getInt(data, 48 + offset);
        this.field_14_reserved6 = LittleEndian.getInt(data, 52 + offset);
        this.field_15_reserved7 = LittleEndian.getInt(data, 56 + offset);
        this.field_16_reserved8 = LittleEndian.getInt(data, 60 + offset);
        this.field_17_reserved9 = LittleEndian.getInt(data, 64 + offset);
        this.field_18_reserved10 = LittleEndian.getInt(data, 68 + offset);
        this.field_19_reserved11 = LittleEndian.getInt(data, 72 + offset);
        this.field_20_reserved12 = LittleEndian.getInt(data, 76 + offset);
        this.field_21_reserved13 = LittleEndian.getInt(data, 80 + offset);
        this.field_22_reserved14 = LittleEndian.getInt(data, 84 + offset);
    }

    public void serialize(byte[] data, int offset) {
        LittleEndian.putInt(data, 0 + offset, this.field_1_cbMac);
        LittleEndian.putInt(data, 4 + offset, this.field_2_reserved1);
        LittleEndian.putInt(data, 8 + offset, this.field_3_reserved2);
        LittleEndian.putInt(data, 12 + offset, this.field_4_ccpText);
        LittleEndian.putInt(data, 16 + offset, this.field_5_ccpFtn);
        LittleEndian.putInt(data, 20 + offset, this.field_6_ccpHdd);
        LittleEndian.putInt(data, 24 + offset, this.field_7_reserved3);
        LittleEndian.putInt(data, 28 + offset, this.field_8_ccpAtn);
        LittleEndian.putInt(data, 32 + offset, this.field_9_ccpEdn);
        LittleEndian.putInt(data, 36 + offset, this.field_10_ccpTxbx);
        LittleEndian.putInt(data, 40 + offset, this.field_11_ccpHdrTxbx);
        LittleEndian.putInt(data, 44 + offset, this.field_12_reserved4);
        LittleEndian.putInt(data, 48 + offset, this.field_13_reserved5);
        LittleEndian.putInt(data, 52 + offset, this.field_14_reserved6);
        LittleEndian.putInt(data, 56 + offset, this.field_15_reserved7);
        LittleEndian.putInt(data, 60 + offset, this.field_16_reserved8);
        LittleEndian.putInt(data, 64 + offset, this.field_17_reserved9);
        LittleEndian.putInt(data, 68 + offset, this.field_18_reserved10);
        LittleEndian.putInt(data, 72 + offset, this.field_19_reserved11);
        LittleEndian.putInt(data, 76 + offset, this.field_20_reserved12);
        LittleEndian.putInt(data, 80 + offset, this.field_21_reserved13);
        LittleEndian.putInt(data, 84 + offset, this.field_22_reserved14);
    }

    public byte[] serialize() {
        byte[] result = new byte[FibRgLw97AbstractType.getSize()];
        this.serialize(result, 0);
        return result;
    }

    public static int getSize() {
        return 88;
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
        FibRgLw97AbstractType other = (FibRgLw97AbstractType)obj;
        if (this.field_1_cbMac != other.field_1_cbMac) {
            return false;
        }
        if (this.field_2_reserved1 != other.field_2_reserved1) {
            return false;
        }
        if (this.field_3_reserved2 != other.field_3_reserved2) {
            return false;
        }
        if (this.field_4_ccpText != other.field_4_ccpText) {
            return false;
        }
        if (this.field_5_ccpFtn != other.field_5_ccpFtn) {
            return false;
        }
        if (this.field_6_ccpHdd != other.field_6_ccpHdd) {
            return false;
        }
        if (this.field_7_reserved3 != other.field_7_reserved3) {
            return false;
        }
        if (this.field_8_ccpAtn != other.field_8_ccpAtn) {
            return false;
        }
        if (this.field_9_ccpEdn != other.field_9_ccpEdn) {
            return false;
        }
        if (this.field_10_ccpTxbx != other.field_10_ccpTxbx) {
            return false;
        }
        if (this.field_11_ccpHdrTxbx != other.field_11_ccpHdrTxbx) {
            return false;
        }
        if (this.field_12_reserved4 != other.field_12_reserved4) {
            return false;
        }
        if (this.field_13_reserved5 != other.field_13_reserved5) {
            return false;
        }
        if (this.field_14_reserved6 != other.field_14_reserved6) {
            return false;
        }
        if (this.field_15_reserved7 != other.field_15_reserved7) {
            return false;
        }
        if (this.field_16_reserved8 != other.field_16_reserved8) {
            return false;
        }
        if (this.field_17_reserved9 != other.field_17_reserved9) {
            return false;
        }
        if (this.field_18_reserved10 != other.field_18_reserved10) {
            return false;
        }
        if (this.field_19_reserved11 != other.field_19_reserved11) {
            return false;
        }
        if (this.field_20_reserved12 != other.field_20_reserved12) {
            return false;
        }
        if (this.field_21_reserved13 != other.field_21_reserved13) {
            return false;
        }
        return this.field_22_reserved14 == other.field_22_reserved14;
    }

    public int hashCode() {
        return Objects.hash(this.field_1_cbMac, this.field_2_reserved1, this.field_3_reserved2, this.field_4_ccpText, this.field_5_ccpFtn, this.field_6_ccpHdd, this.field_7_reserved3, this.field_8_ccpAtn, this.field_9_ccpEdn, this.field_10_ccpTxbx, this.field_11_ccpHdrTxbx, this.field_12_reserved4, this.field_13_reserved5, this.field_14_reserved6, this.field_15_reserved7, this.field_16_reserved8, this.field_17_reserved9, this.field_18_reserved10, this.field_19_reserved11, this.field_20_reserved12, this.field_21_reserved13, this.field_22_reserved14);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[FibRgLw97]\n");
        builder.append("    .cbMac                = ");
        builder.append(" (").append(this.getCbMac()).append(" )\n");
        builder.append("    .reserved1            = ");
        builder.append(" (").append(this.getReserved1()).append(" )\n");
        builder.append("    .reserved2            = ");
        builder.append(" (").append(this.getReserved2()).append(" )\n");
        builder.append("    .ccpText              = ");
        builder.append(" (").append(this.getCcpText()).append(" )\n");
        builder.append("    .ccpFtn               = ");
        builder.append(" (").append(this.getCcpFtn()).append(" )\n");
        builder.append("    .ccpHdd               = ");
        builder.append(" (").append(this.getCcpHdd()).append(" )\n");
        builder.append("    .reserved3            = ");
        builder.append(" (").append(this.getReserved3()).append(" )\n");
        builder.append("    .ccpAtn               = ");
        builder.append(" (").append(this.getCcpAtn()).append(" )\n");
        builder.append("    .ccpEdn               = ");
        builder.append(" (").append(this.getCcpEdn()).append(" )\n");
        builder.append("    .ccpTxbx              = ");
        builder.append(" (").append(this.getCcpTxbx()).append(" )\n");
        builder.append("    .ccpHdrTxbx           = ");
        builder.append(" (").append(this.getCcpHdrTxbx()).append(" )\n");
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
        builder.append("    .reserved14           = ");
        builder.append(" (").append(this.getReserved14()).append(" )\n");
        builder.append("[/FibRgLw97]\n");
        return builder.toString();
    }

    @Internal
    public int getCbMac() {
        return this.field_1_cbMac;
    }

    @Internal
    public void setCbMac(int field_1_cbMac) {
        this.field_1_cbMac = field_1_cbMac;
    }

    @Internal
    public int getReserved1() {
        return this.field_2_reserved1;
    }

    @Internal
    public void setReserved1(int field_2_reserved1) {
        this.field_2_reserved1 = field_2_reserved1;
    }

    @Internal
    public int getReserved2() {
        return this.field_3_reserved2;
    }

    @Internal
    public void setReserved2(int field_3_reserved2) {
        this.field_3_reserved2 = field_3_reserved2;
    }

    @Internal
    public int getCcpText() {
        return this.field_4_ccpText;
    }

    @Internal
    public void setCcpText(int field_4_ccpText) {
        this.field_4_ccpText = field_4_ccpText;
    }

    @Internal
    public int getCcpFtn() {
        return this.field_5_ccpFtn;
    }

    @Internal
    public void setCcpFtn(int field_5_ccpFtn) {
        this.field_5_ccpFtn = field_5_ccpFtn;
    }

    @Internal
    public int getCcpHdd() {
        return this.field_6_ccpHdd;
    }

    @Internal
    public void setCcpHdd(int field_6_ccpHdd) {
        this.field_6_ccpHdd = field_6_ccpHdd;
    }

    @Internal
    public int getReserved3() {
        return this.field_7_reserved3;
    }

    @Internal
    public void setReserved3(int field_7_reserved3) {
        this.field_7_reserved3 = field_7_reserved3;
    }

    @Internal
    public int getCcpAtn() {
        return this.field_8_ccpAtn;
    }

    @Internal
    public void setCcpAtn(int field_8_ccpAtn) {
        this.field_8_ccpAtn = field_8_ccpAtn;
    }

    @Internal
    public int getCcpEdn() {
        return this.field_9_ccpEdn;
    }

    @Internal
    public void setCcpEdn(int field_9_ccpEdn) {
        this.field_9_ccpEdn = field_9_ccpEdn;
    }

    @Internal
    public int getCcpTxbx() {
        return this.field_10_ccpTxbx;
    }

    @Internal
    public void setCcpTxbx(int field_10_ccpTxbx) {
        this.field_10_ccpTxbx = field_10_ccpTxbx;
    }

    @Internal
    public int getCcpHdrTxbx() {
        return this.field_11_ccpHdrTxbx;
    }

    @Internal
    public void setCcpHdrTxbx(int field_11_ccpHdrTxbx) {
        this.field_11_ccpHdrTxbx = field_11_ccpHdrTxbx;
    }

    @Internal
    public int getReserved4() {
        return this.field_12_reserved4;
    }

    @Internal
    public void setReserved4(int field_12_reserved4) {
        this.field_12_reserved4 = field_12_reserved4;
    }

    @Internal
    public int getReserved5() {
        return this.field_13_reserved5;
    }

    @Internal
    public void setReserved5(int field_13_reserved5) {
        this.field_13_reserved5 = field_13_reserved5;
    }

    @Internal
    public int getReserved6() {
        return this.field_14_reserved6;
    }

    @Internal
    public void setReserved6(int field_14_reserved6) {
        this.field_14_reserved6 = field_14_reserved6;
    }

    @Internal
    public int getReserved7() {
        return this.field_15_reserved7;
    }

    @Internal
    public void setReserved7(int field_15_reserved7) {
        this.field_15_reserved7 = field_15_reserved7;
    }

    @Internal
    public int getReserved8() {
        return this.field_16_reserved8;
    }

    @Internal
    public void setReserved8(int field_16_reserved8) {
        this.field_16_reserved8 = field_16_reserved8;
    }

    @Internal
    public int getReserved9() {
        return this.field_17_reserved9;
    }

    @Internal
    public void setReserved9(int field_17_reserved9) {
        this.field_17_reserved9 = field_17_reserved9;
    }

    @Internal
    public int getReserved10() {
        return this.field_18_reserved10;
    }

    @Internal
    public void setReserved10(int field_18_reserved10) {
        this.field_18_reserved10 = field_18_reserved10;
    }

    @Internal
    public int getReserved11() {
        return this.field_19_reserved11;
    }

    @Internal
    public void setReserved11(int field_19_reserved11) {
        this.field_19_reserved11 = field_19_reserved11;
    }

    @Internal
    public int getReserved12() {
        return this.field_20_reserved12;
    }

    @Internal
    public void setReserved12(int field_20_reserved12) {
        this.field_20_reserved12 = field_20_reserved12;
    }

    @Internal
    public int getReserved13() {
        return this.field_21_reserved13;
    }

    @Internal
    public void setReserved13(int field_21_reserved13) {
        this.field_21_reserved13 = field_21_reserved13;
    }

    @Internal
    public int getReserved14() {
        return this.field_22_reserved14;
    }

    @Internal
    public void setReserved14(int field_22_reserved14) {
        this.field_22_reserved14 = field_22_reserved14;
    }
}


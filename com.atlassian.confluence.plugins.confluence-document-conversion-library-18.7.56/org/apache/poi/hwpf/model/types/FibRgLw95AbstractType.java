/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model.types;

import java.util.Objects;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;

@Internal
public abstract class FibRgLw95AbstractType {
    protected int field_1_cbMac;
    @Deprecated
    protected int field_2_reserved1;
    @Deprecated
    protected int field_3_reserved2;
    @Deprecated
    protected int field_4_reserved3;
    @Deprecated
    protected int field_5_reserved4;
    protected int field_6_ccpText;
    protected int field_7_ccpFtn;
    protected int field_8_ccpHdd;
    protected int field_9_ccpMcr;
    protected int field_10_ccpAtn;
    protected int field_11_ccpEdn;
    protected int field_12_ccpTxbx;
    protected int field_13_ccpHdrTxbx;
    @Deprecated
    protected int field_14_reserved5;

    protected FibRgLw95AbstractType() {
    }

    protected void fillFields(byte[] data, int offset) {
        this.field_1_cbMac = LittleEndian.getInt(data, 0 + offset);
        this.field_2_reserved1 = LittleEndian.getInt(data, 4 + offset);
        this.field_3_reserved2 = LittleEndian.getInt(data, 8 + offset);
        this.field_4_reserved3 = LittleEndian.getInt(data, 12 + offset);
        this.field_5_reserved4 = LittleEndian.getInt(data, 16 + offset);
        this.field_6_ccpText = LittleEndian.getInt(data, 20 + offset);
        this.field_7_ccpFtn = LittleEndian.getInt(data, 24 + offset);
        this.field_8_ccpHdd = LittleEndian.getInt(data, 28 + offset);
        this.field_9_ccpMcr = LittleEndian.getInt(data, 32 + offset);
        this.field_10_ccpAtn = LittleEndian.getInt(data, 36 + offset);
        this.field_11_ccpEdn = LittleEndian.getInt(data, 40 + offset);
        this.field_12_ccpTxbx = LittleEndian.getInt(data, 44 + offset);
        this.field_13_ccpHdrTxbx = LittleEndian.getInt(data, 48 + offset);
        this.field_14_reserved5 = LittleEndian.getInt(data, 52 + offset);
    }

    public void serialize(byte[] data, int offset) {
        LittleEndian.putInt(data, 0 + offset, this.field_1_cbMac);
        LittleEndian.putInt(data, 4 + offset, this.field_2_reserved1);
        LittleEndian.putInt(data, 8 + offset, this.field_3_reserved2);
        LittleEndian.putInt(data, 12 + offset, this.field_4_reserved3);
        LittleEndian.putInt(data, 16 + offset, this.field_5_reserved4);
        LittleEndian.putInt(data, 20 + offset, this.field_6_ccpText);
        LittleEndian.putInt(data, 24 + offset, this.field_7_ccpFtn);
        LittleEndian.putInt(data, 28 + offset, this.field_8_ccpHdd);
        LittleEndian.putInt(data, 32 + offset, this.field_9_ccpMcr);
        LittleEndian.putInt(data, 36 + offset, this.field_10_ccpAtn);
        LittleEndian.putInt(data, 40 + offset, this.field_11_ccpEdn);
        LittleEndian.putInt(data, 44 + offset, this.field_12_ccpTxbx);
        LittleEndian.putInt(data, 48 + offset, this.field_13_ccpHdrTxbx);
        LittleEndian.putInt(data, 52 + offset, this.field_14_reserved5);
    }

    public byte[] serialize() {
        byte[] result = new byte[FibRgLw95AbstractType.getSize()];
        this.serialize(result, 0);
        return result;
    }

    public static int getSize() {
        return 56;
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
        FibRgLw95AbstractType other = (FibRgLw95AbstractType)obj;
        if (this.field_1_cbMac != other.field_1_cbMac) {
            return false;
        }
        if (this.field_2_reserved1 != other.field_2_reserved1) {
            return false;
        }
        if (this.field_3_reserved2 != other.field_3_reserved2) {
            return false;
        }
        if (this.field_4_reserved3 != other.field_4_reserved3) {
            return false;
        }
        if (this.field_5_reserved4 != other.field_5_reserved4) {
            return false;
        }
        if (this.field_6_ccpText != other.field_6_ccpText) {
            return false;
        }
        if (this.field_7_ccpFtn != other.field_7_ccpFtn) {
            return false;
        }
        if (this.field_8_ccpHdd != other.field_8_ccpHdd) {
            return false;
        }
        if (this.field_9_ccpMcr != other.field_9_ccpMcr) {
            return false;
        }
        if (this.field_10_ccpAtn != other.field_10_ccpAtn) {
            return false;
        }
        if (this.field_11_ccpEdn != other.field_11_ccpEdn) {
            return false;
        }
        if (this.field_12_ccpTxbx != other.field_12_ccpTxbx) {
            return false;
        }
        if (this.field_13_ccpHdrTxbx != other.field_13_ccpHdrTxbx) {
            return false;
        }
        return this.field_14_reserved5 == other.field_14_reserved5;
    }

    public int hashCode() {
        return Objects.hash(this.field_1_cbMac, this.field_2_reserved1, this.field_3_reserved2, this.field_4_reserved3, this.field_5_reserved4, this.field_6_ccpText, this.field_7_ccpFtn, this.field_8_ccpHdd, this.field_9_ccpMcr, this.field_10_ccpAtn, this.field_11_ccpEdn, this.field_12_ccpTxbx, this.field_13_ccpHdrTxbx, this.field_14_reserved5);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[FibRgLw95]\n");
        builder.append("    .cbMac                = ");
        builder.append(" (").append(this.getCbMac()).append(" )\n");
        builder.append("    .reserved1            = ");
        builder.append(" (").append(this.getReserved1()).append(" )\n");
        builder.append("    .reserved2            = ");
        builder.append(" (").append(this.getReserved2()).append(" )\n");
        builder.append("    .reserved3            = ");
        builder.append(" (").append(this.getReserved3()).append(" )\n");
        builder.append("    .reserved4            = ");
        builder.append(" (").append(this.getReserved4()).append(" )\n");
        builder.append("    .ccpText              = ");
        builder.append(" (").append(this.getCcpText()).append(" )\n");
        builder.append("    .ccpFtn               = ");
        builder.append(" (").append(this.getCcpFtn()).append(" )\n");
        builder.append("    .ccpHdd               = ");
        builder.append(" (").append(this.getCcpHdd()).append(" )\n");
        builder.append("    .ccpMcr               = ");
        builder.append(" (").append(this.getCcpMcr()).append(" )\n");
        builder.append("    .ccpAtn               = ");
        builder.append(" (").append(this.getCcpAtn()).append(" )\n");
        builder.append("    .ccpEdn               = ");
        builder.append(" (").append(this.getCcpEdn()).append(" )\n");
        builder.append("    .ccpTxbx              = ");
        builder.append(" (").append(this.getCcpTxbx()).append(" )\n");
        builder.append("    .ccpHdrTxbx           = ");
        builder.append(" (").append(this.getCcpHdrTxbx()).append(" )\n");
        builder.append("    .reserved5            = ");
        builder.append(" (").append(this.getReserved5()).append(" )\n");
        builder.append("[/FibRgLw95]\n");
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
    public int getReserved3() {
        return this.field_4_reserved3;
    }

    @Internal
    public void setReserved3(int field_4_reserved3) {
        this.field_4_reserved3 = field_4_reserved3;
    }

    @Internal
    public int getReserved4() {
        return this.field_5_reserved4;
    }

    @Internal
    public void setReserved4(int field_5_reserved4) {
        this.field_5_reserved4 = field_5_reserved4;
    }

    @Internal
    public int getCcpText() {
        return this.field_6_ccpText;
    }

    @Internal
    public void setCcpText(int field_6_ccpText) {
        this.field_6_ccpText = field_6_ccpText;
    }

    @Internal
    public int getCcpFtn() {
        return this.field_7_ccpFtn;
    }

    @Internal
    public void setCcpFtn(int field_7_ccpFtn) {
        this.field_7_ccpFtn = field_7_ccpFtn;
    }

    @Internal
    public int getCcpHdd() {
        return this.field_8_ccpHdd;
    }

    @Internal
    public void setCcpHdd(int field_8_ccpHdd) {
        this.field_8_ccpHdd = field_8_ccpHdd;
    }

    @Internal
    public int getCcpMcr() {
        return this.field_9_ccpMcr;
    }

    @Internal
    public void setCcpMcr(int field_9_ccpMcr) {
        this.field_9_ccpMcr = field_9_ccpMcr;
    }

    @Internal
    public int getCcpAtn() {
        return this.field_10_ccpAtn;
    }

    @Internal
    public void setCcpAtn(int field_10_ccpAtn) {
        this.field_10_ccpAtn = field_10_ccpAtn;
    }

    @Internal
    public int getCcpEdn() {
        return this.field_11_ccpEdn;
    }

    @Internal
    public void setCcpEdn(int field_11_ccpEdn) {
        this.field_11_ccpEdn = field_11_ccpEdn;
    }

    @Internal
    public int getCcpTxbx() {
        return this.field_12_ccpTxbx;
    }

    @Internal
    public void setCcpTxbx(int field_12_ccpTxbx) {
        this.field_12_ccpTxbx = field_12_ccpTxbx;
    }

    @Internal
    public int getCcpHdrTxbx() {
        return this.field_13_ccpHdrTxbx;
    }

    @Internal
    public void setCcpHdrTxbx(int field_13_ccpHdrTxbx) {
        this.field_13_ccpHdrTxbx = field_13_ccpHdrTxbx;
    }

    @Internal
    public int getReserved5() {
        return this.field_14_reserved5;
    }

    @Internal
    public void setReserved5(int field_14_reserved5) {
        this.field_14_reserved5 = field_14_reserved5;
    }
}


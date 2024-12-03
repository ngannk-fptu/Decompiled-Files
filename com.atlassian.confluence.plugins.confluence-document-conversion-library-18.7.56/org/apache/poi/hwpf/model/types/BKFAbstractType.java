/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model.types;

import org.apache.poi.util.BitField;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;

@Internal
public abstract class BKFAbstractType {
    private static final BitField itcFirst = new BitField(127);
    private static final BitField fPub = new BitField(128);
    private static final BitField itcLim = new BitField(32512);
    private static final BitField fCol = new BitField(32768);
    protected short field_1_ibkl;
    protected short field_2_bkf_flags;

    protected BKFAbstractType() {
    }

    protected BKFAbstractType(BKFAbstractType other) {
        this.field_1_ibkl = other.field_1_ibkl;
        this.field_2_bkf_flags = other.field_2_bkf_flags;
    }

    protected void fillFields(byte[] data, int offset) {
        this.field_1_ibkl = LittleEndian.getShort(data, 0 + offset);
        this.field_2_bkf_flags = LittleEndian.getShort(data, 2 + offset);
    }

    public void serialize(byte[] data, int offset) {
        LittleEndian.putShort(data, 0 + offset, this.field_1_ibkl);
        LittleEndian.putShort(data, 2 + offset, this.field_2_bkf_flags);
    }

    public static int getSize() {
        return 4;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[BKF]\n");
        builder.append("    .ibkl                 = ");
        builder.append(" (").append(this.getIbkl()).append(" )\n");
        builder.append("    .bkf_flags            = ");
        builder.append(" (").append(this.getBkf_flags()).append(" )\n");
        builder.append("         .itcFirst                 = ").append(this.getItcFirst()).append('\n');
        builder.append("         .fPub                     = ").append(this.isFPub()).append('\n');
        builder.append("         .itcLim                   = ").append(this.getItcLim()).append('\n');
        builder.append("         .fCol                     = ").append(this.isFCol()).append('\n');
        builder.append("[/BKF]\n");
        return builder.toString();
    }

    public short getIbkl() {
        return this.field_1_ibkl;
    }

    public void setIbkl(short field_1_ibkl) {
        this.field_1_ibkl = field_1_ibkl;
    }

    public short getBkf_flags() {
        return this.field_2_bkf_flags;
    }

    public void setBkf_flags(short field_2_bkf_flags) {
        this.field_2_bkf_flags = field_2_bkf_flags;
    }

    public void setItcFirst(byte value) {
        this.field_2_bkf_flags = (short)itcFirst.setValue(this.field_2_bkf_flags, value);
    }

    public byte getItcFirst() {
        return (byte)itcFirst.getValue(this.field_2_bkf_flags);
    }

    public void setFPub(boolean value) {
        this.field_2_bkf_flags = (short)fPub.setBoolean(this.field_2_bkf_flags, value);
    }

    public boolean isFPub() {
        return fPub.isSet(this.field_2_bkf_flags);
    }

    public void setItcLim(byte value) {
        this.field_2_bkf_flags = (short)itcLim.setValue(this.field_2_bkf_flags, value);
    }

    public byte getItcLim() {
        return (byte)itcLim.getValue(this.field_2_bkf_flags);
    }

    public void setFCol(boolean value) {
        this.field_2_bkf_flags = (short)fCol.setBoolean(this.field_2_bkf_flags, value);
    }

    public boolean isFCol() {
        return fCol.isSet(this.field_2_bkf_flags);
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model.types;

import org.apache.poi.util.BitField;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;

@Internal
public abstract class FSPAAbstractType {
    protected int field_1_spid;
    protected int field_2_xaLeft;
    protected int field_3_yaTop;
    protected int field_4_xaRight;
    protected int field_5_yaBottom;
    protected short field_6_flags;
    private static BitField fHdr = new BitField(1);
    private static BitField bx = new BitField(6);
    private static BitField by = new BitField(24);
    private static BitField wr = new BitField(480);
    private static BitField wrk = new BitField(7680);
    private static BitField fRcaSimple = new BitField(8192);
    private static BitField fBelowText = new BitField(16384);
    private static BitField fAnchorLock = new BitField(32768);
    protected int field_7_cTxbx;

    protected FSPAAbstractType() {
    }

    protected void fillFields(byte[] data, int offset) {
        this.field_1_spid = LittleEndian.getInt(data, 0 + offset);
        this.field_2_xaLeft = LittleEndian.getInt(data, 4 + offset);
        this.field_3_yaTop = LittleEndian.getInt(data, 8 + offset);
        this.field_4_xaRight = LittleEndian.getInt(data, 12 + offset);
        this.field_5_yaBottom = LittleEndian.getInt(data, 16 + offset);
        this.field_6_flags = LittleEndian.getShort(data, 20 + offset);
        this.field_7_cTxbx = LittleEndian.getInt(data, 22 + offset);
    }

    public void serialize(byte[] data, int offset) {
        LittleEndian.putInt(data, 0 + offset, this.field_1_spid);
        LittleEndian.putInt(data, 4 + offset, this.field_2_xaLeft);
        LittleEndian.putInt(data, 8 + offset, this.field_3_yaTop);
        LittleEndian.putInt(data, 12 + offset, this.field_4_xaRight);
        LittleEndian.putInt(data, 16 + offset, this.field_5_yaBottom);
        LittleEndian.putShort(data, 20 + offset, this.field_6_flags);
        LittleEndian.putInt(data, 22 + offset, this.field_7_cTxbx);
    }

    public static int getSize() {
        return 26;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[FSPA]\n");
        builder.append("    .spid                 = ");
        builder.append(" (").append(this.getSpid()).append(" )\n");
        builder.append("    .xaLeft               = ");
        builder.append(" (").append(this.getXaLeft()).append(" )\n");
        builder.append("    .yaTop                = ");
        builder.append(" (").append(this.getYaTop()).append(" )\n");
        builder.append("    .xaRight              = ");
        builder.append(" (").append(this.getXaRight()).append(" )\n");
        builder.append("    .yaBottom             = ");
        builder.append(" (").append(this.getYaBottom()).append(" )\n");
        builder.append("    .flags                = ");
        builder.append(" (").append(this.getFlags()).append(" )\n");
        builder.append("         .fHdr                     = ").append(this.isFHdr()).append('\n');
        builder.append("         .bx                       = ").append(this.getBx()).append('\n');
        builder.append("         .by                       = ").append(this.getBy()).append('\n');
        builder.append("         .wr                       = ").append(this.getWr()).append('\n');
        builder.append("         .wrk                      = ").append(this.getWrk()).append('\n');
        builder.append("         .fRcaSimple               = ").append(this.isFRcaSimple()).append('\n');
        builder.append("         .fBelowText               = ").append(this.isFBelowText()).append('\n');
        builder.append("         .fAnchorLock              = ").append(this.isFAnchorLock()).append('\n');
        builder.append("    .cTxbx                = ");
        builder.append(" (").append(this.getCTxbx()).append(" )\n");
        builder.append("[/FSPA]\n");
        return builder.toString();
    }

    @Internal
    public int getSpid() {
        return this.field_1_spid;
    }

    @Internal
    public void setSpid(int field_1_spid) {
        this.field_1_spid = field_1_spid;
    }

    @Internal
    public int getXaLeft() {
        return this.field_2_xaLeft;
    }

    @Internal
    public void setXaLeft(int field_2_xaLeft) {
        this.field_2_xaLeft = field_2_xaLeft;
    }

    @Internal
    public int getYaTop() {
        return this.field_3_yaTop;
    }

    @Internal
    public void setYaTop(int field_3_yaTop) {
        this.field_3_yaTop = field_3_yaTop;
    }

    @Internal
    public int getXaRight() {
        return this.field_4_xaRight;
    }

    @Internal
    public void setXaRight(int field_4_xaRight) {
        this.field_4_xaRight = field_4_xaRight;
    }

    @Internal
    public int getYaBottom() {
        return this.field_5_yaBottom;
    }

    @Internal
    public void setYaBottom(int field_5_yaBottom) {
        this.field_5_yaBottom = field_5_yaBottom;
    }

    @Internal
    public short getFlags() {
        return this.field_6_flags;
    }

    @Internal
    public void setFlags(short field_6_flags) {
        this.field_6_flags = field_6_flags;
    }

    @Internal
    public int getCTxbx() {
        return this.field_7_cTxbx;
    }

    @Internal
    public void setCTxbx(int field_7_cTxbx) {
        this.field_7_cTxbx = field_7_cTxbx;
    }

    @Internal
    public void setFHdr(boolean value) {
        this.field_6_flags = (short)fHdr.setBoolean(this.field_6_flags, value);
    }

    @Internal
    public boolean isFHdr() {
        return fHdr.isSet(this.field_6_flags);
    }

    @Internal
    public void setBx(byte value) {
        this.field_6_flags = (short)bx.setValue(this.field_6_flags, value);
    }

    @Internal
    public byte getBx() {
        return (byte)bx.getValue(this.field_6_flags);
    }

    @Internal
    public void setBy(byte value) {
        this.field_6_flags = (short)by.setValue(this.field_6_flags, value);
    }

    @Internal
    public byte getBy() {
        return (byte)by.getValue(this.field_6_flags);
    }

    @Internal
    public void setWr(byte value) {
        this.field_6_flags = (short)wr.setValue(this.field_6_flags, value);
    }

    @Internal
    public byte getWr() {
        return (byte)wr.getValue(this.field_6_flags);
    }

    @Internal
    public void setWrk(byte value) {
        this.field_6_flags = (short)wrk.setValue(this.field_6_flags, value);
    }

    @Internal
    public byte getWrk() {
        return (byte)wrk.getValue(this.field_6_flags);
    }

    @Internal
    public void setFRcaSimple(boolean value) {
        this.field_6_flags = (short)fRcaSimple.setBoolean(this.field_6_flags, value);
    }

    @Internal
    public boolean isFRcaSimple() {
        return fRcaSimple.isSet(this.field_6_flags);
    }

    @Internal
    public void setFBelowText(boolean value) {
        this.field_6_flags = (short)fBelowText.setBoolean(this.field_6_flags, value);
    }

    @Internal
    public boolean isFBelowText() {
        return fBelowText.isSet(this.field_6_flags);
    }

    @Internal
    public void setFAnchorLock(boolean value) {
        this.field_6_flags = (short)fAnchorLock.setBoolean(this.field_6_flags, value);
    }

    @Internal
    public boolean isFAnchorLock() {
        return fAnchorLock.isSet(this.field_6_flags);
    }
}


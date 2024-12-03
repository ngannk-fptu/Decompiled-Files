/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model.types;

import org.apache.poi.util.BitField;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;

@Internal
public abstract class FibBaseAbstractType {
    protected int field_1_wIdent;
    protected int field_2_nFib;
    protected int field_3_unused;
    protected int field_4_lid;
    protected int field_5_pnNext;
    protected short field_6_flags1;
    private static final BitField fDot = new BitField(1);
    private static final BitField fGlsy = new BitField(2);
    private static final BitField fComplex = new BitField(4);
    private static final BitField fHasPic = new BitField(8);
    private static final BitField cQuickSaves = new BitField(240);
    private static final BitField fEncrypted = new BitField(256);
    private static final BitField fWhichTblStm = new BitField(512);
    private static final BitField fReadOnlyRecommended = new BitField(1024);
    private static final BitField fWriteReservation = new BitField(2048);
    private static final BitField fExtChar = new BitField(4096);
    private static final BitField fLoadOverride = new BitField(8192);
    private static final BitField fFarEast = new BitField(16384);
    private static final BitField fObfuscated = new BitField(32768);
    protected int field_7_nFibBack;
    protected int field_8_lKey;
    @Deprecated
    protected byte field_9_envr;
    protected byte field_10_flags2;
    private static final BitField fMac = new BitField(1);
    private static final BitField fEmptySpecial = new BitField(2);
    private static final BitField fLoadOverridePage = new BitField(4);
    private static final BitField reserved1 = new BitField(8);
    private static final BitField reserved2 = new BitField(16);
    private static final BitField fSpare0 = new BitField(254);
    @Deprecated
    protected short field_11_Chs;
    @Deprecated
    protected short field_12_chsTables;
    @Deprecated
    protected int field_13_fcMin;
    @Deprecated
    protected int field_14_fcMac;

    protected FibBaseAbstractType() {
    }

    protected void fillFields(byte[] data, int offset) {
        this.field_1_wIdent = LittleEndian.getShort(data, 0 + offset);
        this.field_2_nFib = LittleEndian.getShort(data, 2 + offset);
        this.field_3_unused = LittleEndian.getShort(data, 4 + offset);
        this.field_4_lid = LittleEndian.getShort(data, 6 + offset);
        this.field_5_pnNext = LittleEndian.getShort(data, 8 + offset);
        this.field_6_flags1 = LittleEndian.getShort(data, 10 + offset);
        this.field_7_nFibBack = LittleEndian.getShort(data, 12 + offset);
        this.field_8_lKey = LittleEndian.getInt(data, 14 + offset);
        this.field_9_envr = data[18 + offset];
        this.field_10_flags2 = data[19 + offset];
        this.field_11_Chs = LittleEndian.getShort(data, 20 + offset);
        this.field_12_chsTables = LittleEndian.getShort(data, 22 + offset);
        this.field_13_fcMin = LittleEndian.getInt(data, 24 + offset);
        this.field_14_fcMac = LittleEndian.getInt(data, 28 + offset);
    }

    public void serialize(byte[] data, int offset) {
        LittleEndian.putUShort(data, 0 + offset, this.field_1_wIdent);
        LittleEndian.putUShort(data, 2 + offset, this.field_2_nFib);
        LittleEndian.putUShort(data, 4 + offset, this.field_3_unused);
        LittleEndian.putUShort(data, 6 + offset, this.field_4_lid);
        LittleEndian.putUShort(data, 8 + offset, this.field_5_pnNext);
        LittleEndian.putShort(data, 10 + offset, this.field_6_flags1);
        LittleEndian.putUShort(data, 12 + offset, this.field_7_nFibBack);
        LittleEndian.putInt(data, 14 + offset, this.field_8_lKey);
        data[18 + offset] = this.field_9_envr;
        data[19 + offset] = this.field_10_flags2;
        LittleEndian.putShort(data, 20 + offset, this.field_11_Chs);
        LittleEndian.putShort(data, 22 + offset, this.field_12_chsTables);
        LittleEndian.putInt(data, 24 + offset, this.field_13_fcMin);
        LittleEndian.putInt(data, 28 + offset, this.field_14_fcMac);
    }

    public byte[] serialize() {
        byte[] result = new byte[FibBaseAbstractType.getSize()];
        this.serialize(result, 0);
        return result;
    }

    public static int getSize() {
        return 32;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[FibBase]\n");
        builder.append("    .wIdent               = ");
        builder.append(" (").append(this.getWIdent()).append(" )\n");
        builder.append("    .nFib                 = ");
        builder.append(" (").append(this.getNFib()).append(" )\n");
        builder.append("    .unused               = ");
        builder.append(" (").append(this.getUnused()).append(" )\n");
        builder.append("    .lid                  = ");
        builder.append(" (").append(this.getLid()).append(" )\n");
        builder.append("    .pnNext               = ");
        builder.append(" (").append(this.getPnNext()).append(" )\n");
        builder.append("    .flags1               = ");
        builder.append(" (").append(this.getFlags1()).append(" )\n");
        builder.append("         .fDot                     = ").append(this.isFDot()).append('\n');
        builder.append("         .fGlsy                    = ").append(this.isFGlsy()).append('\n');
        builder.append("         .fComplex                 = ").append(this.isFComplex()).append('\n');
        builder.append("         .fHasPic                  = ").append(this.isFHasPic()).append('\n');
        builder.append("         .cQuickSaves              = ").append(this.getCQuickSaves()).append('\n');
        builder.append("         .fEncrypted               = ").append(this.isFEncrypted()).append('\n');
        builder.append("         .fWhichTblStm             = ").append(this.isFWhichTblStm()).append('\n');
        builder.append("         .fReadOnlyRecommended     = ").append(this.isFReadOnlyRecommended()).append('\n');
        builder.append("         .fWriteReservation        = ").append(this.isFWriteReservation()).append('\n');
        builder.append("         .fExtChar                 = ").append(this.isFExtChar()).append('\n');
        builder.append("         .fLoadOverride            = ").append(this.isFLoadOverride()).append('\n');
        builder.append("         .fFarEast                 = ").append(this.isFFarEast()).append('\n');
        builder.append("         .fObfuscated              = ").append(this.isFObfuscated()).append('\n');
        builder.append("    .nFibBack             = ");
        builder.append(" (").append(this.getNFibBack()).append(" )\n");
        builder.append("    .lKey                 = ");
        builder.append(" (").append(this.getLKey()).append(" )\n");
        builder.append("    .envr                 = ");
        builder.append(" (").append(this.getEnvr()).append(" )\n");
        builder.append("    .flags2               = ");
        builder.append(" (").append(this.getFlags2()).append(" )\n");
        builder.append("         .fMac                     = ").append(this.isFMac()).append('\n');
        builder.append("         .fEmptySpecial            = ").append(this.isFEmptySpecial()).append('\n');
        builder.append("         .fLoadOverridePage        = ").append(this.isFLoadOverridePage()).append('\n');
        builder.append("         .reserved1                = ").append(this.isReserved1()).append('\n');
        builder.append("         .reserved2                = ").append(this.isReserved2()).append('\n');
        builder.append("         .fSpare0                  = ").append(this.getFSpare0()).append('\n');
        builder.append("    .Chs                  = ");
        builder.append(" (").append(this.getChs()).append(" )\n");
        builder.append("    .chsTables            = ");
        builder.append(" (").append(this.getChsTables()).append(" )\n");
        builder.append("    .fcMin                = ");
        builder.append(" (").append(this.getFcMin()).append(" )\n");
        builder.append("    .fcMac                = ");
        builder.append(" (").append(this.getFcMac()).append(" )\n");
        builder.append("[/FibBase]\n");
        return builder.toString();
    }

    @Internal
    public int getWIdent() {
        return this.field_1_wIdent;
    }

    @Internal
    public void setWIdent(int field_1_wIdent) {
        this.field_1_wIdent = field_1_wIdent;
    }

    @Internal
    public int getNFib() {
        return this.field_2_nFib;
    }

    @Internal
    public void setNFib(int field_2_nFib) {
        this.field_2_nFib = field_2_nFib;
    }

    @Internal
    public int getUnused() {
        return this.field_3_unused;
    }

    @Internal
    public void setUnused(int field_3_unused) {
        this.field_3_unused = field_3_unused;
    }

    @Internal
    public int getLid() {
        return this.field_4_lid;
    }

    @Internal
    public void setLid(int field_4_lid) {
        this.field_4_lid = field_4_lid;
    }

    @Internal
    public int getPnNext() {
        return this.field_5_pnNext;
    }

    @Internal
    public void setPnNext(int field_5_pnNext) {
        this.field_5_pnNext = field_5_pnNext;
    }

    @Internal
    public short getFlags1() {
        return this.field_6_flags1;
    }

    @Internal
    public void setFlags1(short field_6_flags1) {
        this.field_6_flags1 = field_6_flags1;
    }

    @Internal
    public int getNFibBack() {
        return this.field_7_nFibBack;
    }

    @Internal
    public void setNFibBack(int field_7_nFibBack) {
        this.field_7_nFibBack = field_7_nFibBack;
    }

    @Internal
    public int getLKey() {
        return this.field_8_lKey;
    }

    @Internal
    public void setLKey(int field_8_lKey) {
        this.field_8_lKey = field_8_lKey;
    }

    @Internal
    public byte getEnvr() {
        return this.field_9_envr;
    }

    @Internal
    public void setEnvr(byte field_9_envr) {
        this.field_9_envr = field_9_envr;
    }

    @Internal
    public byte getFlags2() {
        return this.field_10_flags2;
    }

    @Internal
    public void setFlags2(byte field_10_flags2) {
        this.field_10_flags2 = field_10_flags2;
    }

    @Internal
    public short getChs() {
        return this.field_11_Chs;
    }

    @Internal
    public void setChs(short field_11_Chs) {
        this.field_11_Chs = field_11_Chs;
    }

    @Internal
    public short getChsTables() {
        return this.field_12_chsTables;
    }

    @Internal
    public void setChsTables(short field_12_chsTables) {
        this.field_12_chsTables = field_12_chsTables;
    }

    @Internal
    public int getFcMin() {
        return this.field_13_fcMin;
    }

    @Internal
    public void setFcMin(int field_13_fcMin) {
        this.field_13_fcMin = field_13_fcMin;
    }

    @Internal
    public int getFcMac() {
        return this.field_14_fcMac;
    }

    @Internal
    public void setFcMac(int field_14_fcMac) {
        this.field_14_fcMac = field_14_fcMac;
    }

    @Internal
    public void setFDot(boolean value) {
        this.field_6_flags1 = (short)fDot.setBoolean(this.field_6_flags1, value);
    }

    @Internal
    public boolean isFDot() {
        return fDot.isSet(this.field_6_flags1);
    }

    @Internal
    public void setFGlsy(boolean value) {
        this.field_6_flags1 = (short)fGlsy.setBoolean(this.field_6_flags1, value);
    }

    @Internal
    public boolean isFGlsy() {
        return fGlsy.isSet(this.field_6_flags1);
    }

    @Internal
    public void setFComplex(boolean value) {
        this.field_6_flags1 = (short)fComplex.setBoolean(this.field_6_flags1, value);
    }

    @Internal
    public boolean isFComplex() {
        return fComplex.isSet(this.field_6_flags1);
    }

    @Internal
    public void setFHasPic(boolean value) {
        this.field_6_flags1 = (short)fHasPic.setBoolean(this.field_6_flags1, value);
    }

    @Internal
    public boolean isFHasPic() {
        return fHasPic.isSet(this.field_6_flags1);
    }

    @Internal
    public void setCQuickSaves(byte value) {
        this.field_6_flags1 = (short)cQuickSaves.setValue(this.field_6_flags1, value);
    }

    @Internal
    public byte getCQuickSaves() {
        return (byte)cQuickSaves.getValue(this.field_6_flags1);
    }

    @Internal
    public void setFEncrypted(boolean value) {
        this.field_6_flags1 = (short)fEncrypted.setBoolean(this.field_6_flags1, value);
    }

    @Internal
    public boolean isFEncrypted() {
        return fEncrypted.isSet(this.field_6_flags1);
    }

    @Internal
    public void setFWhichTblStm(boolean value) {
        this.field_6_flags1 = (short)fWhichTblStm.setBoolean(this.field_6_flags1, value);
    }

    @Internal
    public boolean isFWhichTblStm() {
        return fWhichTblStm.isSet(this.field_6_flags1);
    }

    @Internal
    public void setFReadOnlyRecommended(boolean value) {
        this.field_6_flags1 = (short)fReadOnlyRecommended.setBoolean(this.field_6_flags1, value);
    }

    @Internal
    public boolean isFReadOnlyRecommended() {
        return fReadOnlyRecommended.isSet(this.field_6_flags1);
    }

    @Internal
    public void setFWriteReservation(boolean value) {
        this.field_6_flags1 = (short)fWriteReservation.setBoolean(this.field_6_flags1, value);
    }

    @Internal
    public boolean isFWriteReservation() {
        return fWriteReservation.isSet(this.field_6_flags1);
    }

    @Internal
    public void setFExtChar(boolean value) {
        this.field_6_flags1 = (short)fExtChar.setBoolean(this.field_6_flags1, value);
    }

    @Internal
    public boolean isFExtChar() {
        return fExtChar.isSet(this.field_6_flags1);
    }

    @Internal
    public void setFLoadOverride(boolean value) {
        this.field_6_flags1 = (short)fLoadOverride.setBoolean(this.field_6_flags1, value);
    }

    @Internal
    public boolean isFLoadOverride() {
        return fLoadOverride.isSet(this.field_6_flags1);
    }

    @Internal
    public void setFFarEast(boolean value) {
        this.field_6_flags1 = (short)fFarEast.setBoolean(this.field_6_flags1, value);
    }

    @Internal
    public boolean isFFarEast() {
        return fFarEast.isSet(this.field_6_flags1);
    }

    @Internal
    public void setFObfuscated(boolean value) {
        this.field_6_flags1 = (short)fObfuscated.setBoolean(this.field_6_flags1, value);
    }

    @Internal
    public boolean isFObfuscated() {
        return fObfuscated.isSet(this.field_6_flags1);
    }

    @Internal
    public void setFMac(boolean value) {
        this.field_10_flags2 = (byte)fMac.setBoolean(this.field_10_flags2, value);
    }

    @Internal
    @Deprecated
    public boolean isFMac() {
        return fMac.isSet(this.field_10_flags2);
    }

    @Internal
    public void setFEmptySpecial(boolean value) {
        this.field_10_flags2 = (byte)fEmptySpecial.setBoolean(this.field_10_flags2, value);
    }

    @Internal
    @Deprecated
    public boolean isFEmptySpecial() {
        return fEmptySpecial.isSet(this.field_10_flags2);
    }

    @Internal
    public void setFLoadOverridePage(boolean value) {
        this.field_10_flags2 = (byte)fLoadOverridePage.setBoolean(this.field_10_flags2, value);
    }

    @Internal
    public boolean isFLoadOverridePage() {
        return fLoadOverridePage.isSet(this.field_10_flags2);
    }

    @Internal
    public void setReserved1(boolean value) {
        this.field_10_flags2 = (byte)reserved1.setBoolean(this.field_10_flags2, value);
    }

    @Internal
    @Deprecated
    public boolean isReserved1() {
        return reserved1.isSet(this.field_10_flags2);
    }

    @Internal
    public void setReserved2(boolean value) {
        this.field_10_flags2 = (byte)reserved2.setBoolean(this.field_10_flags2, value);
    }

    @Internal
    @Deprecated
    public boolean isReserved2() {
        return reserved2.isSet(this.field_10_flags2);
    }

    @Internal
    public void setFSpare0(byte value) {
        this.field_10_flags2 = (byte)fSpare0.setValue(this.field_10_flags2, value);
    }

    @Internal
    @Deprecated
    public byte getFSpare0() {
        return (byte)fSpare0.getValue(this.field_10_flags2);
    }
}


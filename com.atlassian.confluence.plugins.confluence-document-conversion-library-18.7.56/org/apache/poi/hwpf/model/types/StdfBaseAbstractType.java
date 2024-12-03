/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model.types;

import org.apache.poi.util.BitField;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;

@Internal
public abstract class StdfBaseAbstractType {
    protected short field_1_info1;
    private static final BitField sti = new BitField(4095);
    private static final BitField fScratch = new BitField(4096);
    private static final BitField fInvalHeight = new BitField(8192);
    private static final BitField fHasUpe = new BitField(16384);
    private static final BitField fMassCopy = new BitField(32768);
    protected short field_2_info2;
    private static final BitField stk = new BitField(15);
    private static final BitField istdBase = new BitField(65520);
    protected short field_3_info3;
    private static final BitField cupx = new BitField(15);
    private static final BitField istdNext = new BitField(65520);
    protected int field_4_bchUpe;
    protected short field_5_grfstd;
    private static final BitField fAutoRedef = new BitField(1);
    private static final BitField fHidden = new BitField(2);
    private static final BitField f97LidsSet = new BitField(4);
    private static final BitField fCopyLang = new BitField(8);
    private static final BitField fPersonalCompose = new BitField(16);
    private static final BitField fPersonalReply = new BitField(32);
    private static final BitField fPersonal = new BitField(64);
    private static final BitField fNoHtmlExport = new BitField(128);
    private static final BitField fSemiHidden = new BitField(256);
    private static final BitField fLocked = new BitField(512);
    private static final BitField fInternalUse = new BitField(1024);
    private static final BitField fUnhideWhenUsed = new BitField(2048);
    private static final BitField fQFormat = new BitField(4096);
    private static final BitField fReserved = new BitField(57344);

    protected StdfBaseAbstractType() {
    }

    protected void fillFields(byte[] data, int offset) {
        this.field_1_info1 = LittleEndian.getShort(data, 0 + offset);
        this.field_2_info2 = LittleEndian.getShort(data, 2 + offset);
        this.field_3_info3 = LittleEndian.getShort(data, 4 + offset);
        this.field_4_bchUpe = LittleEndian.getShort(data, 6 + offset);
        this.field_5_grfstd = LittleEndian.getShort(data, 8 + offset);
    }

    public void serialize(byte[] data, int offset) {
        LittleEndian.putShort(data, 0 + offset, this.field_1_info1);
        LittleEndian.putShort(data, 2 + offset, this.field_2_info2);
        LittleEndian.putShort(data, 4 + offset, this.field_3_info3);
        LittleEndian.putUShort(data, 6 + offset, this.field_4_bchUpe);
        LittleEndian.putShort(data, 8 + offset, this.field_5_grfstd);
    }

    public static int getSize() {
        return 10;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[StdfBase]\n");
        builder.append("    .info1                = ");
        builder.append(" (").append(this.getInfo1()).append(" )\n");
        builder.append("         .sti                      = ").append(this.getSti()).append('\n');
        builder.append("         .fScratch                 = ").append(this.isFScratch()).append('\n');
        builder.append("         .fInvalHeight             = ").append(this.isFInvalHeight()).append('\n');
        builder.append("         .fHasUpe                  = ").append(this.isFHasUpe()).append('\n');
        builder.append("         .fMassCopy                = ").append(this.isFMassCopy()).append('\n');
        builder.append("    .info2                = ");
        builder.append(" (").append(this.getInfo2()).append(" )\n");
        builder.append("         .stk                      = ").append(this.getStk()).append('\n');
        builder.append("         .istdBase                 = ").append(this.getIstdBase()).append('\n');
        builder.append("    .info3                = ");
        builder.append(" (").append(this.getInfo3()).append(" )\n");
        builder.append("         .cupx                     = ").append(this.getCupx()).append('\n');
        builder.append("         .istdNext                 = ").append(this.getIstdNext()).append('\n');
        builder.append("    .bchUpe               = ");
        builder.append(" (").append(this.getBchUpe()).append(" )\n");
        builder.append("    .grfstd               = ");
        builder.append(" (").append(this.getGrfstd()).append(" )\n");
        builder.append("         .fAutoRedef               = ").append(this.isFAutoRedef()).append('\n');
        builder.append("         .fHidden                  = ").append(this.isFHidden()).append('\n');
        builder.append("         .f97LidsSet               = ").append(this.isF97LidsSet()).append('\n');
        builder.append("         .fCopyLang                = ").append(this.isFCopyLang()).append('\n');
        builder.append("         .fPersonalCompose         = ").append(this.isFPersonalCompose()).append('\n');
        builder.append("         .fPersonalReply           = ").append(this.isFPersonalReply()).append('\n');
        builder.append("         .fPersonal                = ").append(this.isFPersonal()).append('\n');
        builder.append("         .fNoHtmlExport            = ").append(this.isFNoHtmlExport()).append('\n');
        builder.append("         .fSemiHidden              = ").append(this.isFSemiHidden()).append('\n');
        builder.append("         .fLocked                  = ").append(this.isFLocked()).append('\n');
        builder.append("         .fInternalUse             = ").append(this.isFInternalUse()).append('\n');
        builder.append("         .fUnhideWhenUsed          = ").append(this.isFUnhideWhenUsed()).append('\n');
        builder.append("         .fQFormat                 = ").append(this.isFQFormat()).append('\n');
        builder.append("         .fReserved                = ").append(this.getFReserved()).append('\n');
        builder.append("[/StdfBase]\n");
        return builder.toString();
    }

    @Internal
    public short getInfo1() {
        return this.field_1_info1;
    }

    @Internal
    public void setInfo1(short field_1_info1) {
        this.field_1_info1 = field_1_info1;
    }

    @Internal
    public short getInfo2() {
        return this.field_2_info2;
    }

    @Internal
    public void setInfo2(short field_2_info2) {
        this.field_2_info2 = field_2_info2;
    }

    @Internal
    public short getInfo3() {
        return this.field_3_info3;
    }

    @Internal
    public void setInfo3(short field_3_info3) {
        this.field_3_info3 = field_3_info3;
    }

    @Internal
    public int getBchUpe() {
        return this.field_4_bchUpe;
    }

    @Internal
    public void setBchUpe(int field_4_bchUpe) {
        this.field_4_bchUpe = field_4_bchUpe;
    }

    @Internal
    public short getGrfstd() {
        return this.field_5_grfstd;
    }

    @Internal
    public void setGrfstd(short field_5_grfstd) {
        this.field_5_grfstd = field_5_grfstd;
    }

    @Internal
    public void setSti(short value) {
        this.field_1_info1 = (short)sti.setValue(this.field_1_info1, value);
    }

    @Internal
    public short getSti() {
        return (short)sti.getValue(this.field_1_info1);
    }

    @Internal
    public void setFScratch(boolean value) {
        this.field_1_info1 = (short)fScratch.setBoolean(this.field_1_info1, value);
    }

    @Internal
    public boolean isFScratch() {
        return fScratch.isSet(this.field_1_info1);
    }

    @Internal
    public void setFInvalHeight(boolean value) {
        this.field_1_info1 = (short)fInvalHeight.setBoolean(this.field_1_info1, value);
    }

    @Internal
    public boolean isFInvalHeight() {
        return fInvalHeight.isSet(this.field_1_info1);
    }

    @Internal
    public void setFHasUpe(boolean value) {
        this.field_1_info1 = (short)fHasUpe.setBoolean(this.field_1_info1, value);
    }

    @Internal
    public boolean isFHasUpe() {
        return fHasUpe.isSet(this.field_1_info1);
    }

    @Internal
    public void setFMassCopy(boolean value) {
        this.field_1_info1 = (short)fMassCopy.setBoolean(this.field_1_info1, value);
    }

    @Internal
    public boolean isFMassCopy() {
        return fMassCopy.isSet(this.field_1_info1);
    }

    @Internal
    public void setStk(byte value) {
        this.field_2_info2 = (short)stk.setValue(this.field_2_info2, value);
    }

    @Internal
    public byte getStk() {
        return (byte)stk.getValue(this.field_2_info2);
    }

    @Internal
    public void setIstdBase(short value) {
        this.field_2_info2 = (short)istdBase.setValue(this.field_2_info2, value);
    }

    @Internal
    public short getIstdBase() {
        return (short)istdBase.getValue(this.field_2_info2);
    }

    @Internal
    public void setCupx(byte value) {
        this.field_3_info3 = (short)cupx.setValue(this.field_3_info3, value);
    }

    @Internal
    public byte getCupx() {
        return (byte)cupx.getValue(this.field_3_info3);
    }

    @Internal
    public void setIstdNext(short value) {
        this.field_3_info3 = (short)istdNext.setValue(this.field_3_info3, value);
    }

    @Internal
    public short getIstdNext() {
        return (short)istdNext.getValue(this.field_3_info3);
    }

    @Internal
    public void setFAutoRedef(boolean value) {
        this.field_5_grfstd = (short)fAutoRedef.setBoolean(this.field_5_grfstd, value);
    }

    @Internal
    public boolean isFAutoRedef() {
        return fAutoRedef.isSet(this.field_5_grfstd);
    }

    @Internal
    public void setFHidden(boolean value) {
        this.field_5_grfstd = (short)fHidden.setBoolean(this.field_5_grfstd, value);
    }

    @Internal
    public boolean isFHidden() {
        return fHidden.isSet(this.field_5_grfstd);
    }

    @Internal
    public void setF97LidsSet(boolean value) {
        this.field_5_grfstd = (short)f97LidsSet.setBoolean(this.field_5_grfstd, value);
    }

    @Internal
    public boolean isF97LidsSet() {
        return f97LidsSet.isSet(this.field_5_grfstd);
    }

    @Internal
    public void setFCopyLang(boolean value) {
        this.field_5_grfstd = (short)fCopyLang.setBoolean(this.field_5_grfstd, value);
    }

    @Internal
    public boolean isFCopyLang() {
        return fCopyLang.isSet(this.field_5_grfstd);
    }

    @Internal
    public void setFPersonalCompose(boolean value) {
        this.field_5_grfstd = (short)fPersonalCompose.setBoolean(this.field_5_grfstd, value);
    }

    @Internal
    public boolean isFPersonalCompose() {
        return fPersonalCompose.isSet(this.field_5_grfstd);
    }

    @Internal
    public void setFPersonalReply(boolean value) {
        this.field_5_grfstd = (short)fPersonalReply.setBoolean(this.field_5_grfstd, value);
    }

    @Internal
    public boolean isFPersonalReply() {
        return fPersonalReply.isSet(this.field_5_grfstd);
    }

    @Internal
    public void setFPersonal(boolean value) {
        this.field_5_grfstd = (short)fPersonal.setBoolean(this.field_5_grfstd, value);
    }

    @Internal
    public boolean isFPersonal() {
        return fPersonal.isSet(this.field_5_grfstd);
    }

    @Internal
    public void setFNoHtmlExport(boolean value) {
        this.field_5_grfstd = (short)fNoHtmlExport.setBoolean(this.field_5_grfstd, value);
    }

    @Internal
    public boolean isFNoHtmlExport() {
        return fNoHtmlExport.isSet(this.field_5_grfstd);
    }

    @Internal
    public void setFSemiHidden(boolean value) {
        this.field_5_grfstd = (short)fSemiHidden.setBoolean(this.field_5_grfstd, value);
    }

    @Internal
    public boolean isFSemiHidden() {
        return fSemiHidden.isSet(this.field_5_grfstd);
    }

    @Internal
    public void setFLocked(boolean value) {
        this.field_5_grfstd = (short)fLocked.setBoolean(this.field_5_grfstd, value);
    }

    @Internal
    public boolean isFLocked() {
        return fLocked.isSet(this.field_5_grfstd);
    }

    @Internal
    public void setFInternalUse(boolean value) {
        this.field_5_grfstd = (short)fInternalUse.setBoolean(this.field_5_grfstd, value);
    }

    @Internal
    public boolean isFInternalUse() {
        return fInternalUse.isSet(this.field_5_grfstd);
    }

    @Internal
    public void setFUnhideWhenUsed(boolean value) {
        this.field_5_grfstd = (short)fUnhideWhenUsed.setBoolean(this.field_5_grfstd, value);
    }

    @Internal
    public boolean isFUnhideWhenUsed() {
        return fUnhideWhenUsed.isSet(this.field_5_grfstd);
    }

    @Internal
    public void setFQFormat(boolean value) {
        this.field_5_grfstd = (short)fQFormat.setBoolean(this.field_5_grfstd, value);
    }

    @Internal
    public boolean isFQFormat() {
        return fQFormat.isSet(this.field_5_grfstd);
    }

    @Internal
    public void setFReserved(byte value) {
        this.field_5_grfstd = (short)fReserved.setValue(this.field_5_grfstd, value);
    }

    @Internal
    public byte getFReserved() {
        return (byte)fReserved.getValue(this.field_5_grfstd);
    }
}


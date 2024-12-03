/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model.types;

import java.util.Arrays;
import org.apache.poi.hwpf.usermodel.BorderCode;
import org.apache.poi.hwpf.usermodel.DateAndTime;
import org.apache.poi.util.Internal;

@Internal
public abstract class SEPAbstractType {
    public static final byte BKC_NO_BREAK = 0;
    public static final byte BKC_NEW_COLUMN = 1;
    public static final byte BKC_NEW_PAGE = 2;
    public static final byte BKC_EVEN_PAGE = 3;
    public static final byte BKC_ODD_PAGE = 4;
    public static final byte NFCPGN_ARABIC = 0;
    public static final byte NFCPGN_ROMAN_UPPER_CASE = 1;
    public static final byte NFCPGN_ROMAN_LOWER_CASE = 2;
    public static final byte NFCPGN_LETTER_UPPER_CASE = 3;
    public static final byte NFCPGN_LETTER_LOWER_CASE = 4;
    public static final boolean DMORIENTPAGE_LANDSCAPE = false;
    public static final boolean DMORIENTPAGE_PORTRAIT = true;
    protected byte field_1_bkc;
    protected boolean field_2_fTitlePage;
    protected boolean field_3_fAutoPgn;
    protected byte field_4_nfcPgn;
    protected boolean field_5_fUnlocked;
    protected byte field_6_cnsPgn;
    protected boolean field_7_fPgnRestart;
    protected boolean field_8_fEndNote;
    protected byte field_9_lnc;
    protected byte field_10_grpfIhdt;
    protected int field_11_nLnnMod;
    protected int field_12_dxaLnn;
    protected int field_13_dxaPgn;
    protected int field_14_dyaPgn;
    protected boolean field_15_fLBetween;
    protected byte field_16_vjc;
    protected int field_17_dmBinFirst;
    protected int field_18_dmBinOther;
    protected int field_19_dmPaperReq;
    protected BorderCode field_20_brcTop;
    protected BorderCode field_21_brcLeft;
    protected BorderCode field_22_brcBottom;
    protected BorderCode field_23_brcRight;
    protected boolean field_24_fPropMark;
    protected int field_25_ibstPropRMark;
    protected DateAndTime field_26_dttmPropRMark;
    protected int field_27_dxtCharSpace;
    protected int field_28_dyaLinePitch;
    protected int field_29_clm;
    protected int field_30_unused2;
    protected boolean field_31_dmOrientPage;
    protected byte field_32_iHeadingPgn;
    protected int field_33_pgnStart;
    protected int field_34_lnnMin;
    protected int field_35_wTextFlow;
    protected short field_36_unused3;
    protected int field_37_pgbProp;
    protected short field_38_unused4;
    protected int field_39_xaPage;
    protected int field_40_yaPage;
    protected int field_41_xaPageNUp;
    protected int field_42_yaPageNUp;
    protected int field_43_dxaLeft;
    protected int field_44_dxaRight;
    protected int field_45_dyaTop;
    protected int field_46_dyaBottom;
    protected int field_47_dzaGutter;
    protected int field_48_dyaHdrTop;
    protected int field_49_dyaHdrBottom;
    protected int field_50_ccolM1;
    protected boolean field_51_fEvenlySpaced;
    protected byte field_52_unused5;
    protected int field_53_dxaColumns;
    protected int[] field_54_rgdxaColumn;
    protected int field_55_dxaColumnWidth;
    protected byte field_56_dmOrientFirst;
    protected byte field_57_fLayout;
    protected short field_58_unused6;
    protected byte[] field_59_olstAnm;

    protected SEPAbstractType() {
        this.field_1_bkc = (byte)2;
        this.field_8_fEndNote = true;
        this.field_13_dxaPgn = 720;
        this.field_14_dyaPgn = 720;
        this.field_31_dmOrientPage = true;
        this.field_33_pgnStart = 1;
        this.field_39_xaPage = 12240;
        this.field_40_yaPage = 15840;
        this.field_41_xaPageNUp = 12240;
        this.field_42_yaPageNUp = 15840;
        this.field_43_dxaLeft = 1800;
        this.field_44_dxaRight = 1800;
        this.field_45_dyaTop = 1440;
        this.field_46_dyaBottom = 1440;
        this.field_48_dyaHdrTop = 720;
        this.field_49_dyaHdrBottom = 720;
        this.field_51_fEvenlySpaced = true;
        this.field_53_dxaColumns = 720;
    }

    protected SEPAbstractType(SEPAbstractType other) {
        this.field_1_bkc = other.field_1_bkc;
        this.field_2_fTitlePage = other.field_2_fTitlePage;
        this.field_3_fAutoPgn = other.field_3_fAutoPgn;
        this.field_4_nfcPgn = other.field_4_nfcPgn;
        this.field_5_fUnlocked = other.field_5_fUnlocked;
        this.field_6_cnsPgn = other.field_6_cnsPgn;
        this.field_7_fPgnRestart = other.field_7_fPgnRestart;
        this.field_8_fEndNote = other.field_8_fEndNote;
        this.field_9_lnc = other.field_9_lnc;
        this.field_10_grpfIhdt = other.field_10_grpfIhdt;
        this.field_11_nLnnMod = other.field_11_nLnnMod;
        this.field_12_dxaLnn = other.field_12_dxaLnn;
        this.field_13_dxaPgn = other.field_13_dxaPgn;
        this.field_14_dyaPgn = other.field_14_dyaPgn;
        this.field_15_fLBetween = other.field_15_fLBetween;
        this.field_16_vjc = other.field_16_vjc;
        this.field_17_dmBinFirst = other.field_17_dmBinFirst;
        this.field_18_dmBinOther = other.field_18_dmBinOther;
        this.field_19_dmPaperReq = other.field_19_dmPaperReq;
        this.field_20_brcTop = other.field_20_brcTop == null ? null : other.field_20_brcTop.copy();
        this.field_21_brcLeft = other.field_21_brcLeft == null ? null : other.field_21_brcLeft.copy();
        this.field_22_brcBottom = other.field_22_brcBottom == null ? null : other.field_22_brcBottom.copy();
        this.field_23_brcRight = other.field_23_brcRight == null ? null : other.field_23_brcRight.copy();
        this.field_24_fPropMark = other.field_24_fPropMark;
        this.field_25_ibstPropRMark = other.field_25_ibstPropRMark;
        this.field_26_dttmPropRMark = other.field_26_dttmPropRMark == null ? null : other.field_26_dttmPropRMark.copy();
        this.field_27_dxtCharSpace = other.field_27_dxtCharSpace;
        this.field_28_dyaLinePitch = other.field_28_dyaLinePitch;
        this.field_29_clm = other.field_29_clm;
        this.field_30_unused2 = other.field_30_unused2;
        this.field_31_dmOrientPage = other.field_31_dmOrientPage;
        this.field_32_iHeadingPgn = other.field_32_iHeadingPgn;
        this.field_33_pgnStart = other.field_33_pgnStart;
        this.field_34_lnnMin = other.field_34_lnnMin;
        this.field_35_wTextFlow = other.field_35_wTextFlow;
        this.field_36_unused3 = other.field_36_unused3;
        this.field_37_pgbProp = other.field_37_pgbProp;
        this.field_38_unused4 = other.field_38_unused4;
        this.field_39_xaPage = other.field_39_xaPage;
        this.field_40_yaPage = other.field_40_yaPage;
        this.field_41_xaPageNUp = other.field_41_xaPageNUp;
        this.field_42_yaPageNUp = other.field_42_yaPageNUp;
        this.field_43_dxaLeft = other.field_43_dxaLeft;
        this.field_44_dxaRight = other.field_44_dxaRight;
        this.field_45_dyaTop = other.field_45_dyaTop;
        this.field_46_dyaBottom = other.field_46_dyaBottom;
        this.field_47_dzaGutter = other.field_47_dzaGutter;
        this.field_48_dyaHdrTop = other.field_48_dyaHdrTop;
        this.field_49_dyaHdrBottom = other.field_49_dyaHdrBottom;
        this.field_50_ccolM1 = other.field_50_ccolM1;
        this.field_51_fEvenlySpaced = other.field_51_fEvenlySpaced;
        this.field_52_unused5 = other.field_52_unused5;
        this.field_53_dxaColumns = other.field_53_dxaColumns;
        this.field_54_rgdxaColumn = other.field_54_rgdxaColumn == null ? null : (int[])other.field_54_rgdxaColumn.clone();
        this.field_55_dxaColumnWidth = other.field_55_dxaColumnWidth;
        this.field_56_dmOrientFirst = other.field_56_dmOrientFirst;
        this.field_57_fLayout = other.field_57_fLayout;
        this.field_58_unused6 = other.field_58_unused6;
        this.field_59_olstAnm = other.field_59_olstAnm == null ? null : (byte[])other.field_59_olstAnm.clone();
    }

    public String toString() {
        return "[SEP]\n    .bkc                  =  (" + this.getBkc() + " )\n    .fTitlePage           =  (" + this.getFTitlePage() + " )\n    .fAutoPgn             =  (" + this.getFAutoPgn() + " )\n    .nfcPgn               =  (" + this.getNfcPgn() + " )\n    .fUnlocked            =  (" + this.getFUnlocked() + " )\n    .cnsPgn               =  (" + this.getCnsPgn() + " )\n    .fPgnRestart          =  (" + this.getFPgnRestart() + " )\n    .fEndNote             =  (" + this.getFEndNote() + " )\n    .lnc                  =  (" + this.getLnc() + " )\n    .grpfIhdt             =  (" + this.getGrpfIhdt() + " )\n    .nLnnMod              =  (" + this.getNLnnMod() + " )\n    .dxaLnn               =  (" + this.getDxaLnn() + " )\n    .dxaPgn               =  (" + this.getDxaPgn() + " )\n    .dyaPgn               =  (" + this.getDyaPgn() + " )\n    .fLBetween            =  (" + this.getFLBetween() + " )\n    .vjc                  =  (" + this.getVjc() + " )\n    .dmBinFirst           =  (" + this.getDmBinFirst() + " )\n    .dmBinOther           =  (" + this.getDmBinOther() + " )\n    .dmPaperReq           =  (" + this.getDmPaperReq() + " )\n    .brcTop               =  (" + this.getBrcTop() + " )\n    .brcLeft              =  (" + this.getBrcLeft() + " )\n    .brcBottom            =  (" + this.getBrcBottom() + " )\n    .brcRight             =  (" + this.getBrcRight() + " )\n    .fPropMark            =  (" + this.getFPropMark() + " )\n    .ibstPropRMark        =  (" + this.getIbstPropRMark() + " )\n    .dttmPropRMark        =  (" + this.getDttmPropRMark() + " )\n    .dxtCharSpace         =  (" + this.getDxtCharSpace() + " )\n    .dyaLinePitch         =  (" + this.getDyaLinePitch() + " )\n    .clm                  =  (" + this.getClm() + " )\n    .unused2              =  (" + this.getUnused2() + " )\n    .dmOrientPage         =  (" + this.getDmOrientPage() + " )\n    .iHeadingPgn          =  (" + this.getIHeadingPgn() + " )\n    .pgnStart             =  (" + this.getPgnStart() + " )\n    .lnnMin               =  (" + this.getLnnMin() + " )\n    .wTextFlow            =  (" + this.getWTextFlow() + " )\n    .unused3              =  (" + this.getUnused3() + " )\n    .pgbProp              =  (" + this.getPgbProp() + " )\n    .unused4              =  (" + this.getUnused4() + " )\n    .xaPage               =  (" + this.getXaPage() + " )\n    .yaPage               =  (" + this.getYaPage() + " )\n    .xaPageNUp            =  (" + this.getXaPageNUp() + " )\n    .yaPageNUp            =  (" + this.getYaPageNUp() + " )\n    .dxaLeft              =  (" + this.getDxaLeft() + " )\n    .dxaRight             =  (" + this.getDxaRight() + " )\n    .dyaTop               =  (" + this.getDyaTop() + " )\n    .dyaBottom            =  (" + this.getDyaBottom() + " )\n    .dzaGutter            =  (" + this.getDzaGutter() + " )\n    .dyaHdrTop            =  (" + this.getDyaHdrTop() + " )\n    .dyaHdrBottom         =  (" + this.getDyaHdrBottom() + " )\n    .ccolM1               =  (" + this.getCcolM1() + " )\n    .fEvenlySpaced        =  (" + this.getFEvenlySpaced() + " )\n    .unused5              =  (" + this.getUnused5() + " )\n    .dxaColumns           =  (" + this.getDxaColumns() + " )\n    .rgdxaColumn          =  (" + Arrays.toString(this.getRgdxaColumn()) + " )\n    .dxaColumnWidth       =  (" + this.getDxaColumnWidth() + " )\n    .dmOrientFirst        =  (" + this.getDmOrientFirst() + " )\n    .fLayout              =  (" + this.getFLayout() + " )\n    .unused6              =  (" + this.getUnused6() + " )\n    .olstAnm              =  (" + Arrays.toString(this.getOlstAnm()) + " )\n[/SEP]\n";
    }

    public byte getBkc() {
        return this.field_1_bkc;
    }

    public void setBkc(byte field_1_bkc) {
        this.field_1_bkc = field_1_bkc;
    }

    public boolean getFTitlePage() {
        return this.field_2_fTitlePage;
    }

    public void setFTitlePage(boolean field_2_fTitlePage) {
        this.field_2_fTitlePage = field_2_fTitlePage;
    }

    public boolean getFAutoPgn() {
        return this.field_3_fAutoPgn;
    }

    public void setFAutoPgn(boolean field_3_fAutoPgn) {
        this.field_3_fAutoPgn = field_3_fAutoPgn;
    }

    public byte getNfcPgn() {
        return this.field_4_nfcPgn;
    }

    public void setNfcPgn(byte field_4_nfcPgn) {
        this.field_4_nfcPgn = field_4_nfcPgn;
    }

    public boolean getFUnlocked() {
        return this.field_5_fUnlocked;
    }

    public void setFUnlocked(boolean field_5_fUnlocked) {
        this.field_5_fUnlocked = field_5_fUnlocked;
    }

    public byte getCnsPgn() {
        return this.field_6_cnsPgn;
    }

    public void setCnsPgn(byte field_6_cnsPgn) {
        this.field_6_cnsPgn = field_6_cnsPgn;
    }

    public boolean getFPgnRestart() {
        return this.field_7_fPgnRestart;
    }

    public void setFPgnRestart(boolean field_7_fPgnRestart) {
        this.field_7_fPgnRestart = field_7_fPgnRestart;
    }

    public boolean getFEndNote() {
        return this.field_8_fEndNote;
    }

    public void setFEndNote(boolean field_8_fEndNote) {
        this.field_8_fEndNote = field_8_fEndNote;
    }

    public byte getLnc() {
        return this.field_9_lnc;
    }

    public void setLnc(byte field_9_lnc) {
        this.field_9_lnc = field_9_lnc;
    }

    public byte getGrpfIhdt() {
        return this.field_10_grpfIhdt;
    }

    public void setGrpfIhdt(byte field_10_grpfIhdt) {
        this.field_10_grpfIhdt = field_10_grpfIhdt;
    }

    public int getNLnnMod() {
        return this.field_11_nLnnMod;
    }

    public void setNLnnMod(int field_11_nLnnMod) {
        this.field_11_nLnnMod = field_11_nLnnMod;
    }

    public int getDxaLnn() {
        return this.field_12_dxaLnn;
    }

    public void setDxaLnn(int field_12_dxaLnn) {
        this.field_12_dxaLnn = field_12_dxaLnn;
    }

    public int getDxaPgn() {
        return this.field_13_dxaPgn;
    }

    public void setDxaPgn(int field_13_dxaPgn) {
        this.field_13_dxaPgn = field_13_dxaPgn;
    }

    public int getDyaPgn() {
        return this.field_14_dyaPgn;
    }

    public void setDyaPgn(int field_14_dyaPgn) {
        this.field_14_dyaPgn = field_14_dyaPgn;
    }

    public boolean getFLBetween() {
        return this.field_15_fLBetween;
    }

    public void setFLBetween(boolean field_15_fLBetween) {
        this.field_15_fLBetween = field_15_fLBetween;
    }

    public byte getVjc() {
        return this.field_16_vjc;
    }

    public void setVjc(byte field_16_vjc) {
        this.field_16_vjc = field_16_vjc;
    }

    public int getDmBinFirst() {
        return this.field_17_dmBinFirst;
    }

    public void setDmBinFirst(int field_17_dmBinFirst) {
        this.field_17_dmBinFirst = field_17_dmBinFirst;
    }

    public int getDmBinOther() {
        return this.field_18_dmBinOther;
    }

    public void setDmBinOther(int field_18_dmBinOther) {
        this.field_18_dmBinOther = field_18_dmBinOther;
    }

    public int getDmPaperReq() {
        return this.field_19_dmPaperReq;
    }

    public void setDmPaperReq(int field_19_dmPaperReq) {
        this.field_19_dmPaperReq = field_19_dmPaperReq;
    }

    public BorderCode getBrcTop() {
        return this.field_20_brcTop;
    }

    public void setBrcTop(BorderCode field_20_brcTop) {
        this.field_20_brcTop = field_20_brcTop;
    }

    public BorderCode getBrcLeft() {
        return this.field_21_brcLeft;
    }

    public void setBrcLeft(BorderCode field_21_brcLeft) {
        this.field_21_brcLeft = field_21_brcLeft;
    }

    public BorderCode getBrcBottom() {
        return this.field_22_brcBottom;
    }

    public void setBrcBottom(BorderCode field_22_brcBottom) {
        this.field_22_brcBottom = field_22_brcBottom;
    }

    public BorderCode getBrcRight() {
        return this.field_23_brcRight;
    }

    public void setBrcRight(BorderCode field_23_brcRight) {
        this.field_23_brcRight = field_23_brcRight;
    }

    public boolean getFPropMark() {
        return this.field_24_fPropMark;
    }

    public void setFPropMark(boolean field_24_fPropMark) {
        this.field_24_fPropMark = field_24_fPropMark;
    }

    public int getIbstPropRMark() {
        return this.field_25_ibstPropRMark;
    }

    public void setIbstPropRMark(int field_25_ibstPropRMark) {
        this.field_25_ibstPropRMark = field_25_ibstPropRMark;
    }

    public DateAndTime getDttmPropRMark() {
        return this.field_26_dttmPropRMark;
    }

    public void setDttmPropRMark(DateAndTime field_26_dttmPropRMark) {
        this.field_26_dttmPropRMark = field_26_dttmPropRMark;
    }

    public int getDxtCharSpace() {
        return this.field_27_dxtCharSpace;
    }

    public void setDxtCharSpace(int field_27_dxtCharSpace) {
        this.field_27_dxtCharSpace = field_27_dxtCharSpace;
    }

    public int getDyaLinePitch() {
        return this.field_28_dyaLinePitch;
    }

    public void setDyaLinePitch(int field_28_dyaLinePitch) {
        this.field_28_dyaLinePitch = field_28_dyaLinePitch;
    }

    public int getClm() {
        return this.field_29_clm;
    }

    public void setClm(int field_29_clm) {
        this.field_29_clm = field_29_clm;
    }

    public int getUnused2() {
        return this.field_30_unused2;
    }

    public void setUnused2(int field_30_unused2) {
        this.field_30_unused2 = field_30_unused2;
    }

    public boolean getDmOrientPage() {
        return this.field_31_dmOrientPage;
    }

    public void setDmOrientPage(boolean field_31_dmOrientPage) {
        this.field_31_dmOrientPage = field_31_dmOrientPage;
    }

    public byte getIHeadingPgn() {
        return this.field_32_iHeadingPgn;
    }

    public void setIHeadingPgn(byte field_32_iHeadingPgn) {
        this.field_32_iHeadingPgn = field_32_iHeadingPgn;
    }

    public int getPgnStart() {
        return this.field_33_pgnStart;
    }

    public void setPgnStart(int field_33_pgnStart) {
        this.field_33_pgnStart = field_33_pgnStart;
    }

    public int getLnnMin() {
        return this.field_34_lnnMin;
    }

    public void setLnnMin(int field_34_lnnMin) {
        this.field_34_lnnMin = field_34_lnnMin;
    }

    public int getWTextFlow() {
        return this.field_35_wTextFlow;
    }

    public void setWTextFlow(int field_35_wTextFlow) {
        this.field_35_wTextFlow = field_35_wTextFlow;
    }

    public short getUnused3() {
        return this.field_36_unused3;
    }

    public void setUnused3(short field_36_unused3) {
        this.field_36_unused3 = field_36_unused3;
    }

    public int getPgbProp() {
        return this.field_37_pgbProp;
    }

    public void setPgbProp(int field_37_pgbProp) {
        this.field_37_pgbProp = field_37_pgbProp;
    }

    public short getUnused4() {
        return this.field_38_unused4;
    }

    public void setUnused4(short field_38_unused4) {
        this.field_38_unused4 = field_38_unused4;
    }

    public int getXaPage() {
        return this.field_39_xaPage;
    }

    public void setXaPage(int field_39_xaPage) {
        this.field_39_xaPage = field_39_xaPage;
    }

    public int getYaPage() {
        return this.field_40_yaPage;
    }

    public void setYaPage(int field_40_yaPage) {
        this.field_40_yaPage = field_40_yaPage;
    }

    public int getXaPageNUp() {
        return this.field_41_xaPageNUp;
    }

    public void setXaPageNUp(int field_41_xaPageNUp) {
        this.field_41_xaPageNUp = field_41_xaPageNUp;
    }

    public int getYaPageNUp() {
        return this.field_42_yaPageNUp;
    }

    public void setYaPageNUp(int field_42_yaPageNUp) {
        this.field_42_yaPageNUp = field_42_yaPageNUp;
    }

    public int getDxaLeft() {
        return this.field_43_dxaLeft;
    }

    public void setDxaLeft(int field_43_dxaLeft) {
        this.field_43_dxaLeft = field_43_dxaLeft;
    }

    public int getDxaRight() {
        return this.field_44_dxaRight;
    }

    public void setDxaRight(int field_44_dxaRight) {
        this.field_44_dxaRight = field_44_dxaRight;
    }

    public int getDyaTop() {
        return this.field_45_dyaTop;
    }

    public void setDyaTop(int field_45_dyaTop) {
        this.field_45_dyaTop = field_45_dyaTop;
    }

    public int getDyaBottom() {
        return this.field_46_dyaBottom;
    }

    public void setDyaBottom(int field_46_dyaBottom) {
        this.field_46_dyaBottom = field_46_dyaBottom;
    }

    public int getDzaGutter() {
        return this.field_47_dzaGutter;
    }

    public void setDzaGutter(int field_47_dzaGutter) {
        this.field_47_dzaGutter = field_47_dzaGutter;
    }

    public int getDyaHdrTop() {
        return this.field_48_dyaHdrTop;
    }

    public void setDyaHdrTop(int field_48_dyaHdrTop) {
        this.field_48_dyaHdrTop = field_48_dyaHdrTop;
    }

    public int getDyaHdrBottom() {
        return this.field_49_dyaHdrBottom;
    }

    public void setDyaHdrBottom(int field_49_dyaHdrBottom) {
        this.field_49_dyaHdrBottom = field_49_dyaHdrBottom;
    }

    public int getCcolM1() {
        return this.field_50_ccolM1;
    }

    public void setCcolM1(int field_50_ccolM1) {
        this.field_50_ccolM1 = field_50_ccolM1;
    }

    public boolean getFEvenlySpaced() {
        return this.field_51_fEvenlySpaced;
    }

    public void setFEvenlySpaced(boolean field_51_fEvenlySpaced) {
        this.field_51_fEvenlySpaced = field_51_fEvenlySpaced;
    }

    public byte getUnused5() {
        return this.field_52_unused5;
    }

    public void setUnused5(byte field_52_unused5) {
        this.field_52_unused5 = field_52_unused5;
    }

    public int getDxaColumns() {
        return this.field_53_dxaColumns;
    }

    public void setDxaColumns(int field_53_dxaColumns) {
        this.field_53_dxaColumns = field_53_dxaColumns;
    }

    public int[] getRgdxaColumn() {
        return this.field_54_rgdxaColumn;
    }

    public void setRgdxaColumn(int[] field_54_rgdxaColumn) {
        this.field_54_rgdxaColumn = field_54_rgdxaColumn;
    }

    public int getDxaColumnWidth() {
        return this.field_55_dxaColumnWidth;
    }

    public void setDxaColumnWidth(int field_55_dxaColumnWidth) {
        this.field_55_dxaColumnWidth = field_55_dxaColumnWidth;
    }

    public byte getDmOrientFirst() {
        return this.field_56_dmOrientFirst;
    }

    public void setDmOrientFirst(byte field_56_dmOrientFirst) {
        this.field_56_dmOrientFirst = field_56_dmOrientFirst;
    }

    public byte getFLayout() {
        return this.field_57_fLayout;
    }

    public void setFLayout(byte field_57_fLayout) {
        this.field_57_fLayout = field_57_fLayout;
    }

    public short getUnused6() {
        return this.field_58_unused6;
    }

    public void setUnused6(short field_58_unused6) {
        this.field_58_unused6 = field_58_unused6;
    }

    public byte[] getOlstAnm() {
        return this.field_59_olstAnm;
    }

    public void setOlstAnm(byte[] field_59_olstAnm) {
        this.field_59_olstAnm = field_59_olstAnm;
    }
}


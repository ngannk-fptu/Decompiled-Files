/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model.types;

import java.util.Arrays;
import java.util.stream.Stream;
import org.apache.poi.hwpf.model.TabDescriptor;
import org.apache.poi.hwpf.usermodel.BorderCode;
import org.apache.poi.hwpf.usermodel.DateAndTime;
import org.apache.poi.hwpf.usermodel.DropCapSpecifier;
import org.apache.poi.hwpf.usermodel.LineSpacingDescriptor;
import org.apache.poi.hwpf.usermodel.ShadingDescriptor;
import org.apache.poi.util.BitField;
import org.apache.poi.util.Internal;

@Internal
public abstract class PAPAbstractType {
    protected static final byte BRCL_SINGLE = 0;
    protected static final byte BRCL_THICK = 1;
    protected static final byte BRCL_DOUBLE = 2;
    protected static final byte BRCL_SHADOW = 3;
    protected static final byte BRCP_NONE = 0;
    protected static final byte BRCP_BORDER_ABOVE = 1;
    protected static final byte BRCP_BORDER_BELOW = 2;
    protected static final byte BRCP_BOX_AROUND = 15;
    protected static final byte BRCP_BAR_TO_LEFT_OF_PARAGRAPH = 16;
    protected static final boolean FMINHEIGHT_EXACT = false;
    protected static final boolean FMINHEIGHT_AT_LEAST = true;
    protected static final byte WALIGNFONT_HANGING = 0;
    protected static final byte WALIGNFONT_CENTERED = 1;
    protected static final byte WALIGNFONT_ROMAN = 2;
    protected static final byte WALIGNFONT_VARIABLE = 3;
    protected static final byte WALIGNFONT_AUTO = 4;
    private static final BitField fVertical = new BitField(1);
    private static final BitField fBackward = new BitField(2);
    private static final BitField fRotateFont = new BitField(4);
    protected int field_1_istd;
    protected boolean field_2_fSideBySide;
    protected boolean field_3_fKeep;
    protected boolean field_4_fKeepFollow;
    protected boolean field_5_fPageBreakBefore;
    protected byte field_6_brcl;
    protected byte field_7_brcp;
    protected byte field_8_ilvl;
    protected int field_9_ilfo;
    protected boolean field_10_fNoLnn;
    protected LineSpacingDescriptor field_11_lspd;
    protected int field_12_dyaBefore;
    protected int field_13_dyaAfter;
    protected boolean field_14_fInTable;
    protected boolean field_15_finTableW97;
    protected boolean field_16_fTtp;
    protected int field_17_dxaAbs;
    protected int field_18_dyaAbs;
    protected int field_19_dxaWidth;
    protected boolean field_20_fBrLnAbove;
    protected boolean field_21_fBrLnBelow;
    protected byte field_22_pcVert;
    protected byte field_23_pcHorz;
    protected byte field_24_wr;
    protected boolean field_25_fNoAutoHyph;
    protected int field_26_dyaHeight;
    protected boolean field_27_fMinHeight;
    protected DropCapSpecifier field_28_dcs;
    protected int field_29_dyaFromText;
    protected int field_30_dxaFromText;
    protected boolean field_31_fLocked;
    protected boolean field_32_fWidowControl;
    protected boolean field_33_fKinsoku;
    protected boolean field_34_fWordWrap;
    protected boolean field_35_fOverflowPunct;
    protected boolean field_36_fTopLinePunct;
    protected boolean field_37_fAutoSpaceDE;
    protected boolean field_38_fAutoSpaceDN;
    protected int field_39_wAlignFont;
    protected short field_40_fontAlign;
    protected byte field_41_lvl;
    protected boolean field_42_fBiDi;
    protected boolean field_43_fNumRMIns;
    protected boolean field_44_fCrLf;
    protected boolean field_45_fUsePgsuSettings;
    protected boolean field_46_fAdjustRight;
    protected int field_47_itap;
    protected boolean field_48_fInnerTableCell;
    protected boolean field_49_fOpenTch;
    protected boolean field_50_fTtpEmbedded;
    protected short field_51_dxcRight;
    protected short field_52_dxcLeft;
    protected short field_53_dxcLeft1;
    protected boolean field_54_fDyaBeforeAuto;
    protected boolean field_55_fDyaAfterAuto;
    protected int field_56_dxaRight;
    protected int field_57_dxaLeft;
    protected int field_58_dxaLeft1;
    protected byte field_59_jc;
    protected BorderCode field_60_brcTop;
    protected BorderCode field_61_brcLeft;
    protected BorderCode field_62_brcBottom;
    protected BorderCode field_63_brcRight;
    protected BorderCode field_64_brcBetween;
    protected BorderCode field_65_brcBar;
    protected ShadingDescriptor field_66_shd;
    protected byte[] field_67_anld;
    protected byte[] field_68_phe;
    protected boolean field_69_fPropRMark;
    protected int field_70_ibstPropRMark;
    protected DateAndTime field_71_dttmPropRMark;
    protected int field_72_itbdMac;
    protected int[] field_73_rgdxaTab;
    protected TabDescriptor[] field_74_rgtbd;
    protected byte[] field_75_numrm;
    protected byte[] field_76_ptap;
    protected boolean field_77_fNoAllowOverlap;
    protected long field_78_ipgp;
    protected long field_79_rsid;

    protected PAPAbstractType() {
        this.field_11_lspd = new LineSpacingDescriptor();
        this.field_11_lspd = new LineSpacingDescriptor();
        this.field_28_dcs = new DropCapSpecifier();
        this.field_32_fWidowControl = true;
        this.field_41_lvl = (byte)9;
        this.field_60_brcTop = new BorderCode();
        this.field_61_brcLeft = new BorderCode();
        this.field_62_brcBottom = new BorderCode();
        this.field_63_brcRight = new BorderCode();
        this.field_64_brcBetween = new BorderCode();
        this.field_65_brcBar = new BorderCode();
        this.field_66_shd = new ShadingDescriptor();
        this.field_67_anld = new byte[0];
        this.field_68_phe = new byte[0];
        this.field_71_dttmPropRMark = new DateAndTime();
        this.field_73_rgdxaTab = new int[0];
        this.field_74_rgtbd = new TabDescriptor[0];
        this.field_75_numrm = new byte[0];
        this.field_76_ptap = new byte[0];
    }

    protected PAPAbstractType(PAPAbstractType other) {
        this.field_1_istd = other.field_1_istd;
        this.field_2_fSideBySide = other.field_2_fSideBySide;
        this.field_3_fKeep = other.field_3_fKeep;
        this.field_4_fKeepFollow = other.field_4_fKeepFollow;
        this.field_5_fPageBreakBefore = other.field_5_fPageBreakBefore;
        this.field_6_brcl = other.field_6_brcl;
        this.field_7_brcp = other.field_7_brcp;
        this.field_8_ilvl = other.field_8_ilvl;
        this.field_9_ilfo = other.field_9_ilfo;
        this.field_10_fNoLnn = other.field_10_fNoLnn;
        this.field_11_lspd = other.field_11_lspd == null ? null : other.field_11_lspd.copy();
        this.field_12_dyaBefore = other.field_12_dyaBefore;
        this.field_13_dyaAfter = other.field_13_dyaAfter;
        this.field_14_fInTable = other.field_14_fInTable;
        this.field_15_finTableW97 = other.field_15_finTableW97;
        this.field_16_fTtp = other.field_16_fTtp;
        this.field_17_dxaAbs = other.field_17_dxaAbs;
        this.field_18_dyaAbs = other.field_18_dyaAbs;
        this.field_19_dxaWidth = other.field_19_dxaWidth;
        this.field_20_fBrLnAbove = other.field_20_fBrLnAbove;
        this.field_21_fBrLnBelow = other.field_21_fBrLnBelow;
        this.field_22_pcVert = other.field_22_pcVert;
        this.field_23_pcHorz = other.field_23_pcHorz;
        this.field_24_wr = other.field_24_wr;
        this.field_25_fNoAutoHyph = other.field_25_fNoAutoHyph;
        this.field_26_dyaHeight = other.field_26_dyaHeight;
        this.field_27_fMinHeight = other.field_27_fMinHeight;
        this.field_28_dcs = other.field_28_dcs == null ? null : other.field_28_dcs.copy();
        this.field_29_dyaFromText = other.field_29_dyaFromText;
        this.field_30_dxaFromText = other.field_30_dxaFromText;
        this.field_31_fLocked = other.field_31_fLocked;
        this.field_32_fWidowControl = other.field_32_fWidowControl;
        this.field_33_fKinsoku = other.field_33_fKinsoku;
        this.field_34_fWordWrap = other.field_34_fWordWrap;
        this.field_35_fOverflowPunct = other.field_35_fOverflowPunct;
        this.field_36_fTopLinePunct = other.field_36_fTopLinePunct;
        this.field_37_fAutoSpaceDE = other.field_37_fAutoSpaceDE;
        this.field_38_fAutoSpaceDN = other.field_38_fAutoSpaceDN;
        this.field_39_wAlignFont = other.field_39_wAlignFont;
        this.field_40_fontAlign = other.field_40_fontAlign;
        this.field_41_lvl = other.field_41_lvl;
        this.field_42_fBiDi = other.field_42_fBiDi;
        this.field_43_fNumRMIns = other.field_43_fNumRMIns;
        this.field_44_fCrLf = other.field_44_fCrLf;
        this.field_45_fUsePgsuSettings = other.field_45_fUsePgsuSettings;
        this.field_46_fAdjustRight = other.field_46_fAdjustRight;
        this.field_47_itap = other.field_47_itap;
        this.field_48_fInnerTableCell = other.field_48_fInnerTableCell;
        this.field_49_fOpenTch = other.field_49_fOpenTch;
        this.field_50_fTtpEmbedded = other.field_50_fTtpEmbedded;
        this.field_51_dxcRight = other.field_51_dxcRight;
        this.field_52_dxcLeft = other.field_52_dxcLeft;
        this.field_53_dxcLeft1 = other.field_53_dxcLeft1;
        this.field_54_fDyaBeforeAuto = other.field_54_fDyaBeforeAuto;
        this.field_55_fDyaAfterAuto = other.field_55_fDyaAfterAuto;
        this.field_56_dxaRight = other.field_56_dxaRight;
        this.field_57_dxaLeft = other.field_57_dxaLeft;
        this.field_58_dxaLeft1 = other.field_58_dxaLeft1;
        this.field_59_jc = other.field_59_jc;
        this.field_60_brcTop = other.field_60_brcTop == null ? null : other.field_60_brcTop.copy();
        this.field_61_brcLeft = other.field_61_brcLeft == null ? null : other.field_61_brcLeft.copy();
        this.field_62_brcBottom = other.field_62_brcBottom == null ? null : other.field_62_brcBottom.copy();
        this.field_63_brcRight = other.field_63_brcRight == null ? null : other.field_63_brcRight.copy();
        this.field_64_brcBetween = other.field_64_brcBetween == null ? null : other.field_64_brcBetween.copy();
        this.field_65_brcBar = other.field_65_brcBar == null ? null : other.field_65_brcBar.copy();
        this.field_66_shd = other.field_66_shd == null ? null : other.field_66_shd.copy();
        this.field_67_anld = other.field_67_anld == null ? null : (byte[])other.field_67_anld.clone();
        this.field_68_phe = other.field_68_phe == null ? null : (byte[])other.field_68_phe.clone();
        this.field_69_fPropRMark = other.field_69_fPropRMark;
        this.field_70_ibstPropRMark = other.field_70_ibstPropRMark;
        this.field_71_dttmPropRMark = other.field_71_dttmPropRMark == null ? null : other.field_71_dttmPropRMark.copy();
        this.field_72_itbdMac = other.field_72_itbdMac;
        this.field_73_rgdxaTab = other.field_73_rgdxaTab == null ? null : (int[])other.field_73_rgdxaTab.clone();
        this.field_74_rgtbd = other.field_74_rgtbd == null ? null : (TabDescriptor[])Stream.of(other.field_74_rgtbd).map(TabDescriptor::copy).toArray(TabDescriptor[]::new);
        this.field_75_numrm = other.field_75_numrm == null ? null : (byte[])other.field_75_numrm.clone();
        this.field_76_ptap = other.field_76_ptap == null ? null : (byte[])other.field_76_ptap.clone();
        this.field_77_fNoAllowOverlap = other.field_77_fNoAllowOverlap;
        this.field_78_ipgp = other.field_78_ipgp;
        this.field_79_rsid = other.field_79_rsid;
    }

    public String toString() {
        return "[PAP]\n    .istd                 =  (" + this.getIstd() + " )\n    .fSideBySide          =  (" + this.getFSideBySide() + " )\n    .fKeep                =  (" + this.getFKeep() + " )\n    .fKeepFollow          =  (" + this.getFKeepFollow() + " )\n    .fPageBreakBefore     =  (" + this.getFPageBreakBefore() + " )\n    .brcl                 =  (" + this.getBrcl() + " )\n    .brcp                 =  (" + this.getBrcp() + " )\n    .ilvl                 =  (" + this.getIlvl() + " )\n    .ilfo                 =  (" + this.getIlfo() + " )\n    .fNoLnn               =  (" + this.getFNoLnn() + " )\n    .lspd                 =  (" + this.getLspd() + " )\n    .dyaBefore            =  (" + this.getDyaBefore() + " )\n    .dyaAfter             =  (" + this.getDyaAfter() + " )\n    .fInTable             =  (" + this.getFInTable() + " )\n    .finTableW97          =  (" + this.getFinTableW97() + " )\n    .fTtp                 =  (" + this.getFTtp() + " )\n    .dxaAbs               =  (" + this.getDxaAbs() + " )\n    .dyaAbs               =  (" + this.getDyaAbs() + " )\n    .dxaWidth             =  (" + this.getDxaWidth() + " )\n    .fBrLnAbove           =  (" + this.getFBrLnAbove() + " )\n    .fBrLnBelow           =  (" + this.getFBrLnBelow() + " )\n    .pcVert               =  (" + this.getPcVert() + " )\n    .pcHorz               =  (" + this.getPcHorz() + " )\n    .wr                   =  (" + this.getWr() + " )\n    .fNoAutoHyph          =  (" + this.getFNoAutoHyph() + " )\n    .dyaHeight            =  (" + this.getDyaHeight() + " )\n    .fMinHeight           =  (" + this.getFMinHeight() + " )\n    .dcs                  =  (" + this.getDcs() + " )\n    .dyaFromText          =  (" + this.getDyaFromText() + " )\n    .dxaFromText          =  (" + this.getDxaFromText() + " )\n    .fLocked              =  (" + this.getFLocked() + " )\n    .fWidowControl        =  (" + this.getFWidowControl() + " )\n    .fKinsoku             =  (" + this.getFKinsoku() + " )\n    .fWordWrap            =  (" + this.getFWordWrap() + " )\n    .fOverflowPunct       =  (" + this.getFOverflowPunct() + " )\n    .fTopLinePunct        =  (" + this.getFTopLinePunct() + " )\n    .fAutoSpaceDE         =  (" + this.getFAutoSpaceDE() + " )\n    .fAutoSpaceDN         =  (" + this.getFAutoSpaceDN() + " )\n    .wAlignFont           =  (" + this.getWAlignFont() + " )\n    .fontAlign            =  (" + this.getFontAlign() + " )\n         .fVertical                = " + this.isFVertical() + '\n' + "         .fBackward                = " + this.isFBackward() + '\n' + "         .fRotateFont              = " + this.isFRotateFont() + '\n' + "    .lvl                  =  (" + this.getLvl() + " )\n    .fBiDi                =  (" + this.getFBiDi() + " )\n    .fNumRMIns            =  (" + this.getFNumRMIns() + " )\n    .fCrLf                =  (" + this.getFCrLf() + " )\n    .fUsePgsuSettings     =  (" + this.getFUsePgsuSettings() + " )\n    .fAdjustRight         =  (" + this.getFAdjustRight() + " )\n    .itap                 =  (" + this.getItap() + " )\n    .fInnerTableCell      =  (" + this.getFInnerTableCell() + " )\n    .fOpenTch             =  (" + this.getFOpenTch() + " )\n    .fTtpEmbedded         =  (" + this.getFTtpEmbedded() + " )\n    .dxcRight             =  (" + this.getDxcRight() + " )\n    .dxcLeft              =  (" + this.getDxcLeft() + " )\n    .dxcLeft1             =  (" + this.getDxcLeft1() + " )\n    .fDyaBeforeAuto       =  (" + this.getFDyaBeforeAuto() + " )\n    .fDyaAfterAuto        =  (" + this.getFDyaAfterAuto() + " )\n    .dxaRight             =  (" + this.getDxaRight() + " )\n    .dxaLeft              =  (" + this.getDxaLeft() + " )\n    .dxaLeft1             =  (" + this.getDxaLeft1() + " )\n    .jc                   =  (" + this.getJc() + " )\n    .brcTop               =  (" + this.getBrcTop() + " )\n    .brcLeft              =  (" + this.getBrcLeft() + " )\n    .brcBottom            =  (" + this.getBrcBottom() + " )\n    .brcRight             =  (" + this.getBrcRight() + " )\n    .brcBetween           =  (" + this.getBrcBetween() + " )\n    .brcBar               =  (" + this.getBrcBar() + " )\n    .shd                  =  (" + this.getShd() + " )\n    .anld                 =  (" + Arrays.toString(this.getAnld()) + " )\n    .phe                  =  (" + Arrays.toString(this.getPhe()) + " )\n    .fPropRMark           =  (" + this.getFPropRMark() + " )\n    .ibstPropRMark        =  (" + this.getIbstPropRMark() + " )\n    .dttmPropRMark        =  (" + this.getDttmPropRMark() + " )\n    .itbdMac              =  (" + this.getItbdMac() + " )\n    .rgdxaTab             =  (" + Arrays.toString(this.getRgdxaTab()) + " )\n    .rgtbd                =  (" + Arrays.toString(this.getRgtbd()) + " )\n    .numrm                =  (" + Arrays.toString(this.getNumrm()) + " )\n    .ptap                 =  (" + Arrays.toString(this.getPtap()) + " )\n    .fNoAllowOverlap      =  (" + this.getFNoAllowOverlap() + " )\n    .ipgp                 =  (" + this.getIpgp() + " )\n    .rsid                 =  (" + this.getRsid() + " )\n[/PAP]\n";
    }

    @Internal
    public int getIstd() {
        return this.field_1_istd;
    }

    @Internal
    public void setIstd(int field_1_istd) {
        this.field_1_istd = field_1_istd;
    }

    @Internal
    public boolean getFSideBySide() {
        return this.field_2_fSideBySide;
    }

    @Internal
    public void setFSideBySide(boolean field_2_fSideBySide) {
        this.field_2_fSideBySide = field_2_fSideBySide;
    }

    @Internal
    public boolean getFKeep() {
        return this.field_3_fKeep;
    }

    @Internal
    public void setFKeep(boolean field_3_fKeep) {
        this.field_3_fKeep = field_3_fKeep;
    }

    @Internal
    public boolean getFKeepFollow() {
        return this.field_4_fKeepFollow;
    }

    @Internal
    public void setFKeepFollow(boolean field_4_fKeepFollow) {
        this.field_4_fKeepFollow = field_4_fKeepFollow;
    }

    @Internal
    public boolean getFPageBreakBefore() {
        return this.field_5_fPageBreakBefore;
    }

    @Internal
    public void setFPageBreakBefore(boolean field_5_fPageBreakBefore) {
        this.field_5_fPageBreakBefore = field_5_fPageBreakBefore;
    }

    @Internal
    public byte getBrcl() {
        return this.field_6_brcl;
    }

    @Internal
    public void setBrcl(byte field_6_brcl) {
        this.field_6_brcl = field_6_brcl;
    }

    @Internal
    public byte getBrcp() {
        return this.field_7_brcp;
    }

    @Internal
    public void setBrcp(byte field_7_brcp) {
        this.field_7_brcp = field_7_brcp;
    }

    @Internal
    public byte getIlvl() {
        return this.field_8_ilvl;
    }

    @Internal
    public void setIlvl(byte field_8_ilvl) {
        this.field_8_ilvl = field_8_ilvl;
    }

    @Internal
    public int getIlfo() {
        return this.field_9_ilfo;
    }

    @Internal
    public void setIlfo(int field_9_ilfo) {
        this.field_9_ilfo = field_9_ilfo;
    }

    @Internal
    public boolean getFNoLnn() {
        return this.field_10_fNoLnn;
    }

    @Internal
    public void setFNoLnn(boolean field_10_fNoLnn) {
        this.field_10_fNoLnn = field_10_fNoLnn;
    }

    @Internal
    public LineSpacingDescriptor getLspd() {
        return this.field_11_lspd;
    }

    @Internal
    public void setLspd(LineSpacingDescriptor field_11_lspd) {
        this.field_11_lspd = field_11_lspd;
    }

    @Internal
    public int getDyaBefore() {
        return this.field_12_dyaBefore;
    }

    @Internal
    public void setDyaBefore(int field_12_dyaBefore) {
        this.field_12_dyaBefore = field_12_dyaBefore;
    }

    @Internal
    public int getDyaAfter() {
        return this.field_13_dyaAfter;
    }

    @Internal
    public void setDyaAfter(int field_13_dyaAfter) {
        this.field_13_dyaAfter = field_13_dyaAfter;
    }

    @Internal
    public boolean getFInTable() {
        return this.field_14_fInTable;
    }

    @Internal
    public void setFInTable(boolean field_14_fInTable) {
        this.field_14_fInTable = field_14_fInTable;
    }

    @Internal
    public boolean getFinTableW97() {
        return this.field_15_finTableW97;
    }

    @Internal
    public void setFinTableW97(boolean field_15_finTableW97) {
        this.field_15_finTableW97 = field_15_finTableW97;
    }

    @Internal
    public boolean getFTtp() {
        return this.field_16_fTtp;
    }

    @Internal
    public void setFTtp(boolean field_16_fTtp) {
        this.field_16_fTtp = field_16_fTtp;
    }

    @Internal
    public int getDxaAbs() {
        return this.field_17_dxaAbs;
    }

    @Internal
    public void setDxaAbs(int field_17_dxaAbs) {
        this.field_17_dxaAbs = field_17_dxaAbs;
    }

    @Internal
    public int getDyaAbs() {
        return this.field_18_dyaAbs;
    }

    @Internal
    public void setDyaAbs(int field_18_dyaAbs) {
        this.field_18_dyaAbs = field_18_dyaAbs;
    }

    @Internal
    public int getDxaWidth() {
        return this.field_19_dxaWidth;
    }

    @Internal
    public void setDxaWidth(int field_19_dxaWidth) {
        this.field_19_dxaWidth = field_19_dxaWidth;
    }

    @Internal
    public boolean getFBrLnAbove() {
        return this.field_20_fBrLnAbove;
    }

    @Internal
    public void setFBrLnAbove(boolean field_20_fBrLnAbove) {
        this.field_20_fBrLnAbove = field_20_fBrLnAbove;
    }

    @Internal
    public boolean getFBrLnBelow() {
        return this.field_21_fBrLnBelow;
    }

    @Internal
    public void setFBrLnBelow(boolean field_21_fBrLnBelow) {
        this.field_21_fBrLnBelow = field_21_fBrLnBelow;
    }

    @Internal
    public byte getPcVert() {
        return this.field_22_pcVert;
    }

    @Internal
    public void setPcVert(byte field_22_pcVert) {
        this.field_22_pcVert = field_22_pcVert;
    }

    @Internal
    public byte getPcHorz() {
        return this.field_23_pcHorz;
    }

    @Internal
    public void setPcHorz(byte field_23_pcHorz) {
        this.field_23_pcHorz = field_23_pcHorz;
    }

    @Internal
    public byte getWr() {
        return this.field_24_wr;
    }

    @Internal
    public void setWr(byte field_24_wr) {
        this.field_24_wr = field_24_wr;
    }

    @Internal
    public boolean getFNoAutoHyph() {
        return this.field_25_fNoAutoHyph;
    }

    @Internal
    public void setFNoAutoHyph(boolean field_25_fNoAutoHyph) {
        this.field_25_fNoAutoHyph = field_25_fNoAutoHyph;
    }

    @Internal
    public int getDyaHeight() {
        return this.field_26_dyaHeight;
    }

    @Internal
    public void setDyaHeight(int field_26_dyaHeight) {
        this.field_26_dyaHeight = field_26_dyaHeight;
    }

    @Internal
    public boolean getFMinHeight() {
        return this.field_27_fMinHeight;
    }

    @Internal
    public void setFMinHeight(boolean field_27_fMinHeight) {
        this.field_27_fMinHeight = field_27_fMinHeight;
    }

    @Internal
    public DropCapSpecifier getDcs() {
        return this.field_28_dcs;
    }

    @Internal
    public void setDcs(DropCapSpecifier field_28_dcs) {
        this.field_28_dcs = field_28_dcs;
    }

    @Internal
    public int getDyaFromText() {
        return this.field_29_dyaFromText;
    }

    @Internal
    public void setDyaFromText(int field_29_dyaFromText) {
        this.field_29_dyaFromText = field_29_dyaFromText;
    }

    @Internal
    public int getDxaFromText() {
        return this.field_30_dxaFromText;
    }

    @Internal
    public void setDxaFromText(int field_30_dxaFromText) {
        this.field_30_dxaFromText = field_30_dxaFromText;
    }

    @Internal
    public boolean getFLocked() {
        return this.field_31_fLocked;
    }

    @Internal
    public void setFLocked(boolean field_31_fLocked) {
        this.field_31_fLocked = field_31_fLocked;
    }

    @Internal
    public boolean getFWidowControl() {
        return this.field_32_fWidowControl;
    }

    @Internal
    public void setFWidowControl(boolean field_32_fWidowControl) {
        this.field_32_fWidowControl = field_32_fWidowControl;
    }

    @Internal
    public boolean getFKinsoku() {
        return this.field_33_fKinsoku;
    }

    @Internal
    public void setFKinsoku(boolean field_33_fKinsoku) {
        this.field_33_fKinsoku = field_33_fKinsoku;
    }

    @Internal
    public boolean getFWordWrap() {
        return this.field_34_fWordWrap;
    }

    @Internal
    public void setFWordWrap(boolean field_34_fWordWrap) {
        this.field_34_fWordWrap = field_34_fWordWrap;
    }

    @Internal
    public boolean getFOverflowPunct() {
        return this.field_35_fOverflowPunct;
    }

    @Internal
    public void setFOverflowPunct(boolean field_35_fOverflowPunct) {
        this.field_35_fOverflowPunct = field_35_fOverflowPunct;
    }

    @Internal
    public boolean getFTopLinePunct() {
        return this.field_36_fTopLinePunct;
    }

    @Internal
    public void setFTopLinePunct(boolean field_36_fTopLinePunct) {
        this.field_36_fTopLinePunct = field_36_fTopLinePunct;
    }

    @Internal
    public boolean getFAutoSpaceDE() {
        return this.field_37_fAutoSpaceDE;
    }

    @Internal
    public void setFAutoSpaceDE(boolean field_37_fAutoSpaceDE) {
        this.field_37_fAutoSpaceDE = field_37_fAutoSpaceDE;
    }

    @Internal
    public boolean getFAutoSpaceDN() {
        return this.field_38_fAutoSpaceDN;
    }

    @Internal
    public void setFAutoSpaceDN(boolean field_38_fAutoSpaceDN) {
        this.field_38_fAutoSpaceDN = field_38_fAutoSpaceDN;
    }

    @Internal
    public int getWAlignFont() {
        return this.field_39_wAlignFont;
    }

    @Internal
    public void setWAlignFont(int field_39_wAlignFont) {
        this.field_39_wAlignFont = field_39_wAlignFont;
    }

    @Internal
    public short getFontAlign() {
        return this.field_40_fontAlign;
    }

    @Internal
    public void setFontAlign(short field_40_fontAlign) {
        this.field_40_fontAlign = field_40_fontAlign;
    }

    @Internal
    public byte getLvl() {
        return this.field_41_lvl;
    }

    @Internal
    public void setLvl(byte field_41_lvl) {
        this.field_41_lvl = field_41_lvl;
    }

    @Internal
    public boolean getFBiDi() {
        return this.field_42_fBiDi;
    }

    @Internal
    public void setFBiDi(boolean field_42_fBiDi) {
        this.field_42_fBiDi = field_42_fBiDi;
    }

    @Internal
    public boolean getFNumRMIns() {
        return this.field_43_fNumRMIns;
    }

    @Internal
    public void setFNumRMIns(boolean field_43_fNumRMIns) {
        this.field_43_fNumRMIns = field_43_fNumRMIns;
    }

    @Internal
    public boolean getFCrLf() {
        return this.field_44_fCrLf;
    }

    @Internal
    public void setFCrLf(boolean field_44_fCrLf) {
        this.field_44_fCrLf = field_44_fCrLf;
    }

    @Internal
    public boolean getFUsePgsuSettings() {
        return this.field_45_fUsePgsuSettings;
    }

    @Internal
    public void setFUsePgsuSettings(boolean field_45_fUsePgsuSettings) {
        this.field_45_fUsePgsuSettings = field_45_fUsePgsuSettings;
    }

    @Internal
    public boolean getFAdjustRight() {
        return this.field_46_fAdjustRight;
    }

    @Internal
    public void setFAdjustRight(boolean field_46_fAdjustRight) {
        this.field_46_fAdjustRight = field_46_fAdjustRight;
    }

    @Internal
    public int getItap() {
        return this.field_47_itap;
    }

    @Internal
    public void setItap(int field_47_itap) {
        this.field_47_itap = field_47_itap;
    }

    @Internal
    public boolean getFInnerTableCell() {
        return this.field_48_fInnerTableCell;
    }

    @Internal
    public void setFInnerTableCell(boolean field_48_fInnerTableCell) {
        this.field_48_fInnerTableCell = field_48_fInnerTableCell;
    }

    @Internal
    public boolean getFOpenTch() {
        return this.field_49_fOpenTch;
    }

    @Internal
    public void setFOpenTch(boolean field_49_fOpenTch) {
        this.field_49_fOpenTch = field_49_fOpenTch;
    }

    @Internal
    public boolean getFTtpEmbedded() {
        return this.field_50_fTtpEmbedded;
    }

    @Internal
    public void setFTtpEmbedded(boolean field_50_fTtpEmbedded) {
        this.field_50_fTtpEmbedded = field_50_fTtpEmbedded;
    }

    @Internal
    public short getDxcRight() {
        return this.field_51_dxcRight;
    }

    @Internal
    public void setDxcRight(short field_51_dxcRight) {
        this.field_51_dxcRight = field_51_dxcRight;
    }

    @Internal
    public short getDxcLeft() {
        return this.field_52_dxcLeft;
    }

    @Internal
    public void setDxcLeft(short field_52_dxcLeft) {
        this.field_52_dxcLeft = field_52_dxcLeft;
    }

    @Internal
    public short getDxcLeft1() {
        return this.field_53_dxcLeft1;
    }

    @Internal
    public void setDxcLeft1(short field_53_dxcLeft1) {
        this.field_53_dxcLeft1 = field_53_dxcLeft1;
    }

    @Internal
    public boolean getFDyaBeforeAuto() {
        return this.field_54_fDyaBeforeAuto;
    }

    @Internal
    public void setFDyaBeforeAuto(boolean field_54_fDyaBeforeAuto) {
        this.field_54_fDyaBeforeAuto = field_54_fDyaBeforeAuto;
    }

    @Internal
    public boolean getFDyaAfterAuto() {
        return this.field_55_fDyaAfterAuto;
    }

    @Internal
    public void setFDyaAfterAuto(boolean field_55_fDyaAfterAuto) {
        this.field_55_fDyaAfterAuto = field_55_fDyaAfterAuto;
    }

    @Internal
    public int getDxaRight() {
        return this.field_56_dxaRight;
    }

    @Internal
    public void setDxaRight(int field_56_dxaRight) {
        this.field_56_dxaRight = field_56_dxaRight;
    }

    @Internal
    public int getDxaLeft() {
        return this.field_57_dxaLeft;
    }

    @Internal
    public void setDxaLeft(int field_57_dxaLeft) {
        this.field_57_dxaLeft = field_57_dxaLeft;
    }

    @Internal
    public int getDxaLeft1() {
        return this.field_58_dxaLeft1;
    }

    @Internal
    public void setDxaLeft1(int field_58_dxaLeft1) {
        this.field_58_dxaLeft1 = field_58_dxaLeft1;
    }

    @Internal
    public byte getJc() {
        return this.field_59_jc;
    }

    @Internal
    public void setJc(byte field_59_jc) {
        this.field_59_jc = field_59_jc;
    }

    @Internal
    public BorderCode getBrcTop() {
        return this.field_60_brcTop;
    }

    @Internal
    public void setBrcTop(BorderCode field_60_brcTop) {
        this.field_60_brcTop = field_60_brcTop;
    }

    @Internal
    public BorderCode getBrcLeft() {
        return this.field_61_brcLeft;
    }

    @Internal
    public void setBrcLeft(BorderCode field_61_brcLeft) {
        this.field_61_brcLeft = field_61_brcLeft;
    }

    @Internal
    public BorderCode getBrcBottom() {
        return this.field_62_brcBottom;
    }

    @Internal
    public void setBrcBottom(BorderCode field_62_brcBottom) {
        this.field_62_brcBottom = field_62_brcBottom;
    }

    @Internal
    public BorderCode getBrcRight() {
        return this.field_63_brcRight;
    }

    @Internal
    public void setBrcRight(BorderCode field_63_brcRight) {
        this.field_63_brcRight = field_63_brcRight;
    }

    @Internal
    public BorderCode getBrcBetween() {
        return this.field_64_brcBetween;
    }

    @Internal
    public void setBrcBetween(BorderCode field_64_brcBetween) {
        this.field_64_brcBetween = field_64_brcBetween;
    }

    @Internal
    public BorderCode getBrcBar() {
        return this.field_65_brcBar;
    }

    @Internal
    public void setBrcBar(BorderCode field_65_brcBar) {
        this.field_65_brcBar = field_65_brcBar;
    }

    @Internal
    public ShadingDescriptor getShd() {
        return this.field_66_shd;
    }

    @Internal
    public void setShd(ShadingDescriptor field_66_shd) {
        this.field_66_shd = field_66_shd;
    }

    @Internal
    public byte[] getAnld() {
        return this.field_67_anld;
    }

    @Internal
    public void setAnld(byte[] field_67_anld) {
        this.field_67_anld = field_67_anld;
    }

    @Internal
    public byte[] getPhe() {
        return this.field_68_phe;
    }

    @Internal
    public void setPhe(byte[] field_68_phe) {
        this.field_68_phe = field_68_phe;
    }

    @Internal
    public boolean getFPropRMark() {
        return this.field_69_fPropRMark;
    }

    @Internal
    public void setFPropRMark(boolean field_69_fPropRMark) {
        this.field_69_fPropRMark = field_69_fPropRMark;
    }

    @Internal
    public int getIbstPropRMark() {
        return this.field_70_ibstPropRMark;
    }

    @Internal
    public void setIbstPropRMark(int field_70_ibstPropRMark) {
        this.field_70_ibstPropRMark = field_70_ibstPropRMark;
    }

    @Internal
    public DateAndTime getDttmPropRMark() {
        return this.field_71_dttmPropRMark;
    }

    @Internal
    public void setDttmPropRMark(DateAndTime field_71_dttmPropRMark) {
        this.field_71_dttmPropRMark = field_71_dttmPropRMark;
    }

    @Internal
    public int getItbdMac() {
        return this.field_72_itbdMac;
    }

    @Internal
    public void setItbdMac(int field_72_itbdMac) {
        this.field_72_itbdMac = field_72_itbdMac;
    }

    @Internal
    public int[] getRgdxaTab() {
        return this.field_73_rgdxaTab;
    }

    @Internal
    public void setRgdxaTab(int[] field_73_rgdxaTab) {
        this.field_73_rgdxaTab = field_73_rgdxaTab;
    }

    @Internal
    public TabDescriptor[] getRgtbd() {
        return this.field_74_rgtbd;
    }

    @Internal
    public void setRgtbd(TabDescriptor[] field_74_rgtbd) {
        this.field_74_rgtbd = field_74_rgtbd;
    }

    @Internal
    public byte[] getNumrm() {
        return this.field_75_numrm;
    }

    @Internal
    public void setNumrm(byte[] field_75_numrm) {
        this.field_75_numrm = field_75_numrm;
    }

    @Internal
    public byte[] getPtap() {
        return this.field_76_ptap;
    }

    @Internal
    public void setPtap(byte[] field_76_ptap) {
        this.field_76_ptap = field_76_ptap;
    }

    @Internal
    public boolean getFNoAllowOverlap() {
        return this.field_77_fNoAllowOverlap;
    }

    @Internal
    public void setFNoAllowOverlap(boolean field_77_fNoAllowOverlap) {
        this.field_77_fNoAllowOverlap = field_77_fNoAllowOverlap;
    }

    @Internal
    public long getIpgp() {
        return this.field_78_ipgp;
    }

    @Internal
    public void setIpgp(long field_78_ipgp) {
        this.field_78_ipgp = field_78_ipgp;
    }

    @Internal
    public long getRsid() {
        return this.field_79_rsid;
    }

    @Internal
    public void setRsid(long field_79_rsid) {
        this.field_79_rsid = field_79_rsid;
    }

    @Internal
    public void setFVertical(boolean value) {
        this.field_40_fontAlign = (short)fVertical.setBoolean(this.field_40_fontAlign, value);
    }

    @Internal
    public boolean isFVertical() {
        return fVertical.isSet(this.field_40_fontAlign);
    }

    @Internal
    public void setFBackward(boolean value) {
        this.field_40_fontAlign = (short)fBackward.setBoolean(this.field_40_fontAlign, value);
    }

    @Internal
    public boolean isFBackward() {
        return fBackward.isSet(this.field_40_fontAlign);
    }

    @Internal
    public void setFRotateFont(boolean value) {
        this.field_40_fontAlign = (short)fRotateFont.setBoolean(this.field_40_fontAlign, value);
    }

    @Internal
    public boolean isFRotateFont() {
        return fRotateFont.isSet(this.field_40_fontAlign);
    }
}


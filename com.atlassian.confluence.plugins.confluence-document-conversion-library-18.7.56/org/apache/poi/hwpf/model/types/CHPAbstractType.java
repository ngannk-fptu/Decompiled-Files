/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model.types;

import java.util.Arrays;
import org.apache.poi.hwpf.model.Colorref;
import org.apache.poi.hwpf.model.Hyphenation;
import org.apache.poi.hwpf.usermodel.BorderCode;
import org.apache.poi.hwpf.usermodel.DateAndTime;
import org.apache.poi.hwpf.usermodel.ShadingDescriptor;
import org.apache.poi.util.BitField;
import org.apache.poi.util.Internal;

@Internal
public abstract class CHPAbstractType {
    private static final BitField fBold = new BitField(1);
    private static final BitField fItalic = new BitField(2);
    private static final BitField fRMarkDel = new BitField(4);
    private static final BitField fOutline = new BitField(8);
    private static final BitField fFldVanish = new BitField(16);
    private static final BitField fSmallCaps = new BitField(32);
    private static final BitField fCaps = new BitField(64);
    private static final BitField fVanish = new BitField(128);
    private static final BitField fRMark = new BitField(256);
    private static final BitField fSpec = new BitField(512);
    private static final BitField fStrike = new BitField(1024);
    private static final BitField fObj = new BitField(2048);
    private static final BitField fShadow = new BitField(4096);
    private static final BitField fLowerCase = new BitField(8192);
    private static final BitField fData = new BitField(16384);
    private static final BitField fOle2 = new BitField(32768);
    private static final BitField fEmboss = new BitField(65536);
    private static final BitField fImprint = new BitField(131072);
    private static final BitField fDStrike = new BitField(262144);
    private static final BitField fUsePgsuSettings = new BitField(524288);
    private static final BitField fBoldBi = new BitField(0x100000);
    private static final BitField fComplexScripts = new BitField(0x100000);
    private static final BitField fItalicBi = new BitField(0x200000);
    private static final BitField fBiDi = new BitField(0x400000);
    private static final BitField fIcoBi = new BitField(0x800000);
    private static final BitField fNonGlyph = new BitField(0x1000000);
    private static final BitField fBoldOther = new BitField(0x2000000);
    private static final BitField fItalicOther = new BitField(0x4000000);
    private static final BitField fNoProof = new BitField(0x8000000);
    private static final BitField fWebHidden = new BitField(0x10000000);
    private static final BitField fFitText = new BitField(0x20000000);
    private static final BitField fCalc = new BitField(0x40000000);
    private static final BitField fFmtLineProp = new BitField(Integer.MIN_VALUE);
    protected static final byte SFXTTEXT_NO = 0;
    protected static final byte SFXTTEXT_LAS_VEGAS_LIGHTS = 1;
    protected static final byte SFXTTEXT_BACKGROUND_BLINK = 2;
    protected static final byte SFXTTEXT_SPARKLE_TEXT = 3;
    protected static final byte SFXTTEXT_MARCHING_ANTS = 4;
    protected static final byte SFXTTEXT_MARCHING_RED_ANTS = 5;
    protected static final byte SFXTTEXT_SHIMMER = 6;
    protected static final byte KCD_NON = 0;
    protected static final byte KCD_DOT = 1;
    protected static final byte KCD_COMMA = 2;
    protected static final byte KCD_CIRCLE = 3;
    protected static final byte KCD_UNDER_DOT = 4;
    protected static final byte KUL_NONE = 0;
    protected static final byte KUL_SINGLE = 1;
    protected static final byte KUL_BY_WORD = 2;
    protected static final byte KUL_DOUBLE = 3;
    protected static final byte KUL_DOTTED = 4;
    protected static final byte KUL_HIDDEN = 5;
    protected static final byte KUL_THICK = 6;
    protected static final byte KUL_DASH = 7;
    protected static final byte KUL_DOT = 8;
    protected static final byte KUL_DOT_DASH = 9;
    protected static final byte KUL_DOT_DOT_DASH = 10;
    protected static final byte KUL_WAVE = 11;
    protected static final byte KUL_DOTTED_HEAVY = 20;
    protected static final byte KUL_DASHED_HEAVY = 23;
    protected static final byte KUL_DOT_DASH_HEAVY = 25;
    protected static final byte KUL_DOT_DOT_DASH_HEAVY = 26;
    protected static final byte KUL_WAVE_HEAVY = 27;
    protected static final byte KUL_DASH_LONG = 39;
    protected static final byte KUL_WAVE_DOUBLE = 43;
    protected static final byte KUL_DASH_LONG_HEAVY = 55;
    protected static final byte ISS_NONE = 0;
    protected static final byte ISS_SUPERSCRIPTED = 1;
    protected static final byte ISS_SUBSCRIPTED = 2;
    private static final BitField itypFELayout = new BitField(255);
    private static final BitField fTNY = new BitField(256);
    private static final BitField fWarichu = new BitField(512);
    private static final BitField fKumimoji = new BitField(1024);
    private static final BitField fRuby = new BitField(2048);
    private static final BitField fLSFitText = new BitField(4096);
    private static final BitField spare = new BitField(57344);
    private static final BitField iWarichuBracket = new BitField(7);
    private static final BitField fWarichuNoOpenBracket = new BitField(8);
    private static final BitField fTNYCompress = new BitField(16);
    private static final BitField fTNYFetchTxm = new BitField(32);
    private static final BitField fCellFitText = new BitField(64);
    private static final BitField unused = new BitField(128);
    private static final BitField icoHighlight = new BitField(31);
    private static final BitField fHighlight = new BitField(32);
    private static final BitField fChsDiff = new BitField(1);
    private static final BitField fMacChs = new BitField(32);
    protected static final byte LBRCRJ_NONE = 0;
    protected static final byte LBRCRJ_LEFT = 1;
    protected static final byte LBRCRJ_RIGHT = 2;
    protected static final byte LBRCRJ_BOTH = 3;
    protected int field_1_grpfChp;
    protected int field_2_hps;
    protected int field_3_ftcAscii;
    protected int field_4_ftcFE;
    protected int field_5_ftcOther;
    protected int field_6_ftcBi;
    protected int field_7_dxaSpace;
    protected Colorref field_8_cv;
    protected byte field_9_ico;
    protected int field_10_pctCharWidth;
    protected int field_11_lidDefault;
    protected int field_12_lidFE;
    protected byte field_13_kcd;
    protected boolean field_14_fUndetermine;
    protected byte field_15_iss;
    protected boolean field_16_fSpecSymbol;
    protected byte field_17_idct;
    protected byte field_18_idctHint;
    protected byte field_19_kul;
    protected Hyphenation field_20_hresi;
    protected int field_21_hpsKern;
    protected short field_22_hpsPos;
    protected ShadingDescriptor field_23_shd;
    protected BorderCode field_24_brc;
    protected int field_25_ibstRMark;
    protected byte field_26_sfxtText;
    protected boolean field_27_fDblBdr;
    protected boolean field_28_fBorderWS;
    protected short field_29_ufel;
    protected byte field_30_copt;
    protected int field_31_hpsAsci;
    protected int field_32_hpsFE;
    protected int field_33_hpsBi;
    protected int field_34_ftcSym;
    protected int field_35_xchSym;
    protected int field_36_fcPic;
    protected int field_37_fcObj;
    protected int field_38_lTagObj;
    protected int field_39_fcData;
    protected Hyphenation field_40_hresiOld;
    protected int field_41_ibstRMarkDel;
    protected DateAndTime field_42_dttmRMark;
    protected DateAndTime field_43_dttmRMarkDel;
    protected int field_44_istd;
    protected int field_45_idslRMReason;
    protected int field_46_idslReasonDel;
    protected int field_47_cpg;
    protected short field_48_Highlight;
    protected short field_49_CharsetFlags;
    protected short field_50_chse;
    protected boolean field_51_fPropRMark;
    protected int field_52_ibstPropRMark;
    protected DateAndTime field_53_dttmPropRMark;
    protected boolean field_54_fConflictOrig;
    protected boolean field_55_fConflictOtherDel;
    protected int field_56_wConflict;
    protected int field_57_IbstConflict;
    protected DateAndTime field_58_dttmConflict;
    protected boolean field_59_fDispFldRMark;
    protected int field_60_ibstDispFldRMark;
    protected DateAndTime field_61_dttmDispFldRMark;
    protected byte[] field_62_xstDispFldRMark;
    protected int field_63_fcObjp;
    protected byte field_64_lbrCRJ;
    protected boolean field_65_fSpecVanish;
    protected boolean field_66_fHasOldProps;
    protected boolean field_67_fSdtVanish;
    protected int field_68_wCharScale;

    protected CHPAbstractType() {
        this.field_2_hps = 20;
        this.field_8_cv = new Colorref();
        this.field_11_lidDefault = 1024;
        this.field_12_lidFE = 1024;
        this.field_20_hresi = new Hyphenation();
        this.field_23_shd = new ShadingDescriptor();
        this.field_24_brc = new BorderCode();
        this.field_36_fcPic = -1;
        this.field_40_hresiOld = new Hyphenation();
        this.field_42_dttmRMark = new DateAndTime();
        this.field_43_dttmRMarkDel = new DateAndTime();
        this.field_44_istd = 10;
        this.field_53_dttmPropRMark = new DateAndTime();
        this.field_58_dttmConflict = new DateAndTime();
        this.field_61_dttmDispFldRMark = new DateAndTime();
        this.field_62_xstDispFldRMark = new byte[32];
        this.field_68_wCharScale = 100;
    }

    protected CHPAbstractType(CHPAbstractType other) {
        this.field_1_grpfChp = other.field_1_grpfChp;
        this.field_2_hps = other.field_2_hps;
        this.field_3_ftcAscii = other.field_3_ftcAscii;
        this.field_4_ftcFE = other.field_4_ftcFE;
        this.field_5_ftcOther = other.field_5_ftcOther;
        this.field_6_ftcBi = other.field_6_ftcBi;
        this.field_7_dxaSpace = other.field_7_dxaSpace;
        this.field_8_cv = other.field_8_cv == null ? null : other.field_8_cv.copy();
        this.field_9_ico = other.field_9_ico;
        this.field_10_pctCharWidth = other.field_10_pctCharWidth;
        this.field_11_lidDefault = other.field_11_lidDefault;
        this.field_12_lidFE = other.field_12_lidFE;
        this.field_13_kcd = other.field_13_kcd;
        this.field_14_fUndetermine = other.field_14_fUndetermine;
        this.field_15_iss = other.field_15_iss;
        this.field_16_fSpecSymbol = other.field_16_fSpecSymbol;
        this.field_17_idct = other.field_17_idct;
        this.field_18_idctHint = other.field_18_idctHint;
        this.field_19_kul = other.field_19_kul;
        this.field_20_hresi = other.field_20_hresi == null ? null : other.field_20_hresi.copy();
        this.field_21_hpsKern = other.field_21_hpsKern;
        this.field_22_hpsPos = other.field_22_hpsPos;
        this.field_23_shd = other.field_23_shd == null ? null : other.field_23_shd.copy();
        this.field_24_brc = other.field_24_brc == null ? null : other.field_24_brc.copy();
        this.field_25_ibstRMark = other.field_25_ibstRMark;
        this.field_26_sfxtText = other.field_26_sfxtText;
        this.field_27_fDblBdr = other.field_27_fDblBdr;
        this.field_28_fBorderWS = other.field_28_fBorderWS;
        this.field_29_ufel = other.field_29_ufel;
        this.field_30_copt = other.field_30_copt;
        this.field_31_hpsAsci = other.field_31_hpsAsci;
        this.field_32_hpsFE = other.field_32_hpsFE;
        this.field_33_hpsBi = other.field_33_hpsBi;
        this.field_34_ftcSym = other.field_34_ftcSym;
        this.field_35_xchSym = other.field_35_xchSym;
        this.field_36_fcPic = other.field_36_fcPic;
        this.field_37_fcObj = other.field_37_fcObj;
        this.field_38_lTagObj = other.field_38_lTagObj;
        this.field_39_fcData = other.field_39_fcData;
        this.field_40_hresiOld = other.field_40_hresiOld == null ? null : other.field_40_hresiOld.copy();
        this.field_41_ibstRMarkDel = other.field_41_ibstRMarkDel;
        this.field_42_dttmRMark = other.field_42_dttmRMark == null ? null : other.field_42_dttmRMark.copy();
        this.field_43_dttmRMarkDel = other.field_43_dttmRMarkDel == null ? null : other.field_43_dttmRMarkDel.copy();
        this.field_44_istd = other.field_44_istd;
        this.field_45_idslRMReason = other.field_45_idslRMReason;
        this.field_46_idslReasonDel = other.field_46_idslReasonDel;
        this.field_47_cpg = other.field_47_cpg;
        this.field_48_Highlight = other.field_48_Highlight;
        this.field_49_CharsetFlags = other.field_49_CharsetFlags;
        this.field_50_chse = other.field_50_chse;
        this.field_51_fPropRMark = other.field_51_fPropRMark;
        this.field_52_ibstPropRMark = other.field_52_ibstPropRMark;
        this.field_53_dttmPropRMark = other.field_53_dttmPropRMark == null ? null : other.field_53_dttmPropRMark.copy();
        this.field_54_fConflictOrig = other.field_54_fConflictOrig;
        this.field_55_fConflictOtherDel = other.field_55_fConflictOtherDel;
        this.field_56_wConflict = other.field_56_wConflict;
        this.field_57_IbstConflict = other.field_57_IbstConflict;
        this.field_58_dttmConflict = other.field_58_dttmConflict == null ? null : other.field_58_dttmConflict.copy();
        this.field_59_fDispFldRMark = other.field_59_fDispFldRMark;
        this.field_60_ibstDispFldRMark = other.field_60_ibstDispFldRMark;
        this.field_61_dttmDispFldRMark = other.field_61_dttmDispFldRMark == null ? null : other.field_61_dttmDispFldRMark.copy();
        this.field_62_xstDispFldRMark = other.field_62_xstDispFldRMark == null ? null : (byte[])other.field_62_xstDispFldRMark.clone();
        this.field_63_fcObjp = other.field_63_fcObjp;
        this.field_64_lbrCRJ = other.field_64_lbrCRJ;
        this.field_65_fSpecVanish = other.field_65_fSpecVanish;
        this.field_66_fHasOldProps = other.field_66_fHasOldProps;
        this.field_67_fSdtVanish = other.field_67_fSdtVanish;
        this.field_68_wCharScale = other.field_68_wCharScale;
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
        CHPAbstractType other = (CHPAbstractType)obj;
        if (this.field_1_grpfChp != other.field_1_grpfChp) {
            return false;
        }
        if (this.field_2_hps != other.field_2_hps) {
            return false;
        }
        if (this.field_3_ftcAscii != other.field_3_ftcAscii) {
            return false;
        }
        if (this.field_4_ftcFE != other.field_4_ftcFE) {
            return false;
        }
        if (this.field_5_ftcOther != other.field_5_ftcOther) {
            return false;
        }
        if (this.field_6_ftcBi != other.field_6_ftcBi) {
            return false;
        }
        if (this.field_7_dxaSpace != other.field_7_dxaSpace) {
            return false;
        }
        if (this.field_8_cv == null ? other.field_8_cv != null : !this.field_8_cv.equals(other.field_8_cv)) {
            return false;
        }
        if (this.field_9_ico != other.field_9_ico) {
            return false;
        }
        if (this.field_10_pctCharWidth != other.field_10_pctCharWidth) {
            return false;
        }
        if (this.field_11_lidDefault != other.field_11_lidDefault) {
            return false;
        }
        if (this.field_12_lidFE != other.field_12_lidFE) {
            return false;
        }
        if (this.field_13_kcd != other.field_13_kcd) {
            return false;
        }
        if (this.field_14_fUndetermine != other.field_14_fUndetermine) {
            return false;
        }
        if (this.field_15_iss != other.field_15_iss) {
            return false;
        }
        if (this.field_16_fSpecSymbol != other.field_16_fSpecSymbol) {
            return false;
        }
        if (this.field_17_idct != other.field_17_idct) {
            return false;
        }
        if (this.field_18_idctHint != other.field_18_idctHint) {
            return false;
        }
        if (this.field_19_kul != other.field_19_kul) {
            return false;
        }
        if (this.field_20_hresi == null ? other.field_20_hresi != null : !this.field_20_hresi.equals(other.field_20_hresi)) {
            return false;
        }
        if (this.field_21_hpsKern != other.field_21_hpsKern) {
            return false;
        }
        if (this.field_22_hpsPos != other.field_22_hpsPos) {
            return false;
        }
        if (this.field_23_shd == null ? other.field_23_shd != null : !this.field_23_shd.equals(other.field_23_shd)) {
            return false;
        }
        if (this.field_24_brc == null ? other.field_24_brc != null : !this.field_24_brc.equals(other.field_24_brc)) {
            return false;
        }
        if (this.field_25_ibstRMark != other.field_25_ibstRMark) {
            return false;
        }
        if (this.field_26_sfxtText != other.field_26_sfxtText) {
            return false;
        }
        if (this.field_27_fDblBdr != other.field_27_fDblBdr) {
            return false;
        }
        if (this.field_28_fBorderWS != other.field_28_fBorderWS) {
            return false;
        }
        if (this.field_29_ufel != other.field_29_ufel) {
            return false;
        }
        if (this.field_30_copt != other.field_30_copt) {
            return false;
        }
        if (this.field_31_hpsAsci != other.field_31_hpsAsci) {
            return false;
        }
        if (this.field_32_hpsFE != other.field_32_hpsFE) {
            return false;
        }
        if (this.field_33_hpsBi != other.field_33_hpsBi) {
            return false;
        }
        if (this.field_34_ftcSym != other.field_34_ftcSym) {
            return false;
        }
        if (this.field_35_xchSym != other.field_35_xchSym) {
            return false;
        }
        if (this.field_36_fcPic != other.field_36_fcPic) {
            return false;
        }
        if (this.field_37_fcObj != other.field_37_fcObj) {
            return false;
        }
        if (this.field_38_lTagObj != other.field_38_lTagObj) {
            return false;
        }
        if (this.field_39_fcData != other.field_39_fcData) {
            return false;
        }
        if (this.field_40_hresiOld == null ? other.field_40_hresiOld != null : !this.field_40_hresiOld.equals(other.field_40_hresiOld)) {
            return false;
        }
        if (this.field_41_ibstRMarkDel != other.field_41_ibstRMarkDel) {
            return false;
        }
        if (this.field_42_dttmRMark == null ? other.field_42_dttmRMark != null : !this.field_42_dttmRMark.equals(other.field_42_dttmRMark)) {
            return false;
        }
        if (this.field_43_dttmRMarkDel == null ? other.field_43_dttmRMarkDel != null : !this.field_43_dttmRMarkDel.equals(other.field_43_dttmRMarkDel)) {
            return false;
        }
        if (this.field_44_istd != other.field_44_istd) {
            return false;
        }
        if (this.field_45_idslRMReason != other.field_45_idslRMReason) {
            return false;
        }
        if (this.field_46_idslReasonDel != other.field_46_idslReasonDel) {
            return false;
        }
        if (this.field_47_cpg != other.field_47_cpg) {
            return false;
        }
        if (this.field_48_Highlight != other.field_48_Highlight) {
            return false;
        }
        if (this.field_49_CharsetFlags != other.field_49_CharsetFlags) {
            return false;
        }
        if (this.field_50_chse != other.field_50_chse) {
            return false;
        }
        if (this.field_51_fPropRMark != other.field_51_fPropRMark) {
            return false;
        }
        if (this.field_52_ibstPropRMark != other.field_52_ibstPropRMark) {
            return false;
        }
        if (this.field_53_dttmPropRMark == null ? other.field_53_dttmPropRMark != null : !this.field_53_dttmPropRMark.equals(other.field_53_dttmPropRMark)) {
            return false;
        }
        if (this.field_54_fConflictOrig != other.field_54_fConflictOrig) {
            return false;
        }
        if (this.field_55_fConflictOtherDel != other.field_55_fConflictOtherDel) {
            return false;
        }
        if (this.field_56_wConflict != other.field_56_wConflict) {
            return false;
        }
        if (this.field_57_IbstConflict != other.field_57_IbstConflict) {
            return false;
        }
        if (this.field_58_dttmConflict == null ? other.field_58_dttmConflict != null : !this.field_58_dttmConflict.equals(other.field_58_dttmConflict)) {
            return false;
        }
        if (this.field_59_fDispFldRMark != other.field_59_fDispFldRMark) {
            return false;
        }
        if (this.field_60_ibstDispFldRMark != other.field_60_ibstDispFldRMark) {
            return false;
        }
        if (this.field_61_dttmDispFldRMark == null ? other.field_61_dttmDispFldRMark != null : !this.field_61_dttmDispFldRMark.equals(other.field_61_dttmDispFldRMark)) {
            return false;
        }
        if (!Arrays.equals(this.field_62_xstDispFldRMark, other.field_62_xstDispFldRMark)) {
            return false;
        }
        if (this.field_63_fcObjp != other.field_63_fcObjp) {
            return false;
        }
        if (this.field_64_lbrCRJ != other.field_64_lbrCRJ) {
            return false;
        }
        if (this.field_65_fSpecVanish != other.field_65_fSpecVanish) {
            return false;
        }
        if (this.field_66_fHasOldProps != other.field_66_fHasOldProps) {
            return false;
        }
        if (this.field_67_fSdtVanish != other.field_67_fSdtVanish) {
            return false;
        }
        return this.field_68_wCharScale == other.field_68_wCharScale;
    }

    public int hashCode() {
        return Arrays.deepHashCode(new Object[]{this.field_1_grpfChp, this.field_2_hps, this.field_3_ftcAscii, this.field_4_ftcFE, this.field_5_ftcOther, this.field_6_ftcBi, this.field_7_dxaSpace, this.field_8_cv, this.field_9_ico, this.field_10_pctCharWidth, this.field_11_lidDefault, this.field_12_lidFE, this.field_13_kcd, this.field_14_fUndetermine, this.field_15_iss, this.field_16_fSpecSymbol, this.field_17_idct, this.field_18_idctHint, this.field_19_kul, this.field_20_hresi, this.field_21_hpsKern, this.field_22_hpsPos, this.field_23_shd, this.field_24_brc, this.field_25_ibstRMark, this.field_26_sfxtText, this.field_27_fDblBdr, this.field_28_fBorderWS, this.field_29_ufel, this.field_30_copt, this.field_31_hpsAsci, this.field_32_hpsFE, this.field_33_hpsBi, this.field_34_ftcSym, this.field_35_xchSym, this.field_36_fcPic, this.field_37_fcObj, this.field_38_lTagObj, this.field_39_fcData, this.field_40_hresiOld, this.field_41_ibstRMarkDel, this.field_42_dttmRMark, this.field_43_dttmRMarkDel, this.field_44_istd, this.field_45_idslRMReason, this.field_46_idslReasonDel, this.field_47_cpg, this.field_48_Highlight, this.field_49_CharsetFlags, this.field_50_chse, this.field_51_fPropRMark, this.field_52_ibstPropRMark, this.field_53_dttmPropRMark, this.field_54_fConflictOrig, this.field_55_fConflictOtherDel, this.field_56_wConflict, this.field_57_IbstConflict, this.field_58_dttmConflict, this.field_59_fDispFldRMark, this.field_60_ibstDispFldRMark, this.field_61_dttmDispFldRMark, this.field_62_xstDispFldRMark, this.field_63_fcObjp, this.field_64_lbrCRJ, this.field_65_fSpecVanish, this.field_66_fHasOldProps, this.field_67_fSdtVanish, this.field_68_wCharScale});
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[CHP]\n");
        builder.append("    .grpfChp              = ");
        builder.append(" (").append(this.getGrpfChp()).append(" )\n");
        builder.append("         .fBold                    = ").append(this.isFBold()).append('\n');
        builder.append("         .fItalic                  = ").append(this.isFItalic()).append('\n');
        builder.append("         .fRMarkDel                = ").append(this.isFRMarkDel()).append('\n');
        builder.append("         .fOutline                 = ").append(this.isFOutline()).append('\n');
        builder.append("         .fFldVanish               = ").append(this.isFFldVanish()).append('\n');
        builder.append("         .fSmallCaps               = ").append(this.isFSmallCaps()).append('\n');
        builder.append("         .fCaps                    = ").append(this.isFCaps()).append('\n');
        builder.append("         .fVanish                  = ").append(this.isFVanish()).append('\n');
        builder.append("         .fRMark                   = ").append(this.isFRMark()).append('\n');
        builder.append("         .fSpec                    = ").append(this.isFSpec()).append('\n');
        builder.append("         .fStrike                  = ").append(this.isFStrike()).append('\n');
        builder.append("         .fObj                     = ").append(this.isFObj()).append('\n');
        builder.append("         .fShadow                  = ").append(this.isFShadow()).append('\n');
        builder.append("         .fLowerCase               = ").append(this.isFLowerCase()).append('\n');
        builder.append("         .fData                    = ").append(this.isFData()).append('\n');
        builder.append("         .fOle2                    = ").append(this.isFOle2()).append('\n');
        builder.append("         .fEmboss                  = ").append(this.isFEmboss()).append('\n');
        builder.append("         .fImprint                 = ").append(this.isFImprint()).append('\n');
        builder.append("         .fDStrike                 = ").append(this.isFDStrike()).append('\n');
        builder.append("         .fUsePgsuSettings         = ").append(this.isFUsePgsuSettings()).append('\n');
        builder.append("         .fBoldBi                  = ").append(this.isFBoldBi()).append('\n');
        builder.append("         .fComplexScripts          = ").append(this.isFComplexScripts()).append('\n');
        builder.append("         .fItalicBi                = ").append(this.isFItalicBi()).append('\n');
        builder.append("         .fBiDi                    = ").append(this.isFBiDi()).append('\n');
        builder.append("         .fIcoBi                   = ").append(this.isFIcoBi()).append('\n');
        builder.append("         .fNonGlyph                = ").append(this.isFNonGlyph()).append('\n');
        builder.append("         .fBoldOther               = ").append(this.isFBoldOther()).append('\n');
        builder.append("         .fItalicOther             = ").append(this.isFItalicOther()).append('\n');
        builder.append("         .fNoProof                 = ").append(this.isFNoProof()).append('\n');
        builder.append("         .fWebHidden               = ").append(this.isFWebHidden()).append('\n');
        builder.append("         .fFitText                 = ").append(this.isFFitText()).append('\n');
        builder.append("         .fCalc                    = ").append(this.isFCalc()).append('\n');
        builder.append("         .fFmtLineProp             = ").append(this.isFFmtLineProp()).append('\n');
        builder.append("    .hps                  = ");
        builder.append(" (").append(this.getHps()).append(" )\n");
        builder.append("    .ftcAscii             = ");
        builder.append(" (").append(this.getFtcAscii()).append(" )\n");
        builder.append("    .ftcFE                = ");
        builder.append(" (").append(this.getFtcFE()).append(" )\n");
        builder.append("    .ftcOther             = ");
        builder.append(" (").append(this.getFtcOther()).append(" )\n");
        builder.append("    .ftcBi                = ");
        builder.append(" (").append(this.getFtcBi()).append(" )\n");
        builder.append("    .dxaSpace             = ");
        builder.append(" (").append(this.getDxaSpace()).append(" )\n");
        builder.append("    .cv                   = ");
        builder.append(" (").append(this.getCv()).append(" )\n");
        builder.append("    .ico                  = ");
        builder.append(" (").append(this.getIco()).append(" )\n");
        builder.append("    .pctCharWidth         = ");
        builder.append(" (").append(this.getPctCharWidth()).append(" )\n");
        builder.append("    .lidDefault           = ");
        builder.append(" (").append(this.getLidDefault()).append(" )\n");
        builder.append("    .lidFE                = ");
        builder.append(" (").append(this.getLidFE()).append(" )\n");
        builder.append("    .kcd                  = ");
        builder.append(" (").append(this.getKcd()).append(" )\n");
        builder.append("    .fUndetermine         = ");
        builder.append(" (").append(this.getFUndetermine()).append(" )\n");
        builder.append("    .iss                  = ");
        builder.append(" (").append(this.getIss()).append(" )\n");
        builder.append("    .fSpecSymbol          = ");
        builder.append(" (").append(this.getFSpecSymbol()).append(" )\n");
        builder.append("    .idct                 = ");
        builder.append(" (").append(this.getIdct()).append(" )\n");
        builder.append("    .idctHint             = ");
        builder.append(" (").append(this.getIdctHint()).append(" )\n");
        builder.append("    .kul                  = ");
        builder.append(" (").append(this.getKul()).append(" )\n");
        builder.append("    .hresi                = ");
        builder.append(" (").append(this.getHresi()).append(" )\n");
        builder.append("    .hpsKern              = ");
        builder.append(" (").append(this.getHpsKern()).append(" )\n");
        builder.append("    .hpsPos               = ");
        builder.append(" (").append(this.getHpsPos()).append(" )\n");
        builder.append("    .shd                  = ");
        builder.append(" (").append(this.getShd()).append(" )\n");
        builder.append("    .brc                  = ");
        builder.append(" (").append(this.getBrc()).append(" )\n");
        builder.append("    .ibstRMark            = ");
        builder.append(" (").append(this.getIbstRMark()).append(" )\n");
        builder.append("    .sfxtText             = ");
        builder.append(" (").append(this.getSfxtText()).append(" )\n");
        builder.append("    .fDblBdr              = ");
        builder.append(" (").append(this.getFDblBdr()).append(" )\n");
        builder.append("    .fBorderWS            = ");
        builder.append(" (").append(this.getFBorderWS()).append(" )\n");
        builder.append("    .ufel                 = ");
        builder.append(" (").append(this.getUfel()).append(" )\n");
        builder.append("         .itypFELayout             = ").append(this.getItypFELayout()).append('\n');
        builder.append("         .fTNY                     = ").append(this.isFTNY()).append('\n');
        builder.append("         .fWarichu                 = ").append(this.isFWarichu()).append('\n');
        builder.append("         .fKumimoji                = ").append(this.isFKumimoji()).append('\n');
        builder.append("         .fRuby                    = ").append(this.isFRuby()).append('\n');
        builder.append("         .fLSFitText               = ").append(this.isFLSFitText()).append('\n');
        builder.append("         .spare                    = ").append(this.getSpare()).append('\n');
        builder.append("    .copt                 = ");
        builder.append(" (").append(this.getCopt()).append(" )\n");
        builder.append("         .iWarichuBracket          = ").append(this.getIWarichuBracket()).append('\n');
        builder.append("         .fWarichuNoOpenBracket     = ").append(this.isFWarichuNoOpenBracket()).append('\n');
        builder.append("         .fTNYCompress             = ").append(this.isFTNYCompress()).append('\n');
        builder.append("         .fTNYFetchTxm             = ").append(this.isFTNYFetchTxm()).append('\n');
        builder.append("         .fCellFitText             = ").append(this.isFCellFitText()).append('\n');
        builder.append("         .unused                   = ").append(this.isUnused()).append('\n');
        builder.append("    .hpsAsci              = ");
        builder.append(" (").append(this.getHpsAsci()).append(" )\n");
        builder.append("    .hpsFE                = ");
        builder.append(" (").append(this.getHpsFE()).append(" )\n");
        builder.append("    .hpsBi                = ");
        builder.append(" (").append(this.getHpsBi()).append(" )\n");
        builder.append("    .ftcSym               = ");
        builder.append(" (").append(this.getFtcSym()).append(" )\n");
        builder.append("    .xchSym               = ");
        builder.append(" (").append(this.getXchSym()).append(" )\n");
        builder.append("    .fcPic                = ");
        builder.append(" (").append(this.getFcPic()).append(" )\n");
        builder.append("    .fcObj                = ");
        builder.append(" (").append(this.getFcObj()).append(" )\n");
        builder.append("    .lTagObj              = ");
        builder.append(" (").append(this.getLTagObj()).append(" )\n");
        builder.append("    .fcData               = ");
        builder.append(" (").append(this.getFcData()).append(" )\n");
        builder.append("    .hresiOld             = ");
        builder.append(" (").append(this.getHresiOld()).append(" )\n");
        builder.append("    .ibstRMarkDel         = ");
        builder.append(" (").append(this.getIbstRMarkDel()).append(" )\n");
        builder.append("    .dttmRMark            = ");
        builder.append(" (").append(this.getDttmRMark()).append(" )\n");
        builder.append("    .dttmRMarkDel         = ");
        builder.append(" (").append(this.getDttmRMarkDel()).append(" )\n");
        builder.append("    .istd                 = ");
        builder.append(" (").append(this.getIstd()).append(" )\n");
        builder.append("    .idslRMReason         = ");
        builder.append(" (").append(this.getIdslRMReason()).append(" )\n");
        builder.append("    .idslReasonDel        = ");
        builder.append(" (").append(this.getIdslReasonDel()).append(" )\n");
        builder.append("    .cpg                  = ");
        builder.append(" (").append(this.getCpg()).append(" )\n");
        builder.append("    .Highlight            = ");
        builder.append(" (").append(this.getHighlight()).append(" )\n");
        builder.append("         .icoHighlight             = ").append(this.getIcoHighlight()).append('\n');
        builder.append("         .fHighlight               = ").append(this.isFHighlight()).append('\n');
        builder.append("    .CharsetFlags         = ");
        builder.append(" (").append(this.getCharsetFlags()).append(" )\n");
        builder.append("         .fChsDiff                 = ").append(this.isFChsDiff()).append('\n');
        builder.append("         .fMacChs                  = ").append(this.isFMacChs()).append('\n');
        builder.append("    .chse                 = ");
        builder.append(" (").append(this.getChse()).append(" )\n");
        builder.append("    .fPropRMark           = ");
        builder.append(" (").append(this.getFPropRMark()).append(" )\n");
        builder.append("    .ibstPropRMark        = ");
        builder.append(" (").append(this.getIbstPropRMark()).append(" )\n");
        builder.append("    .dttmPropRMark        = ");
        builder.append(" (").append(this.getDttmPropRMark()).append(" )\n");
        builder.append("    .fConflictOrig        = ");
        builder.append(" (").append(this.getFConflictOrig()).append(" )\n");
        builder.append("    .fConflictOtherDel    = ");
        builder.append(" (").append(this.getFConflictOtherDel()).append(" )\n");
        builder.append("    .wConflict            = ");
        builder.append(" (").append(this.getWConflict()).append(" )\n");
        builder.append("    .IbstConflict         = ");
        builder.append(" (").append(this.getIbstConflict()).append(" )\n");
        builder.append("    .dttmConflict         = ");
        builder.append(" (").append(this.getDttmConflict()).append(" )\n");
        builder.append("    .fDispFldRMark        = ");
        builder.append(" (").append(this.getFDispFldRMark()).append(" )\n");
        builder.append("    .ibstDispFldRMark     = ");
        builder.append(" (").append(this.getIbstDispFldRMark()).append(" )\n");
        builder.append("    .dttmDispFldRMark     = ");
        builder.append(" (").append(this.getDttmDispFldRMark()).append(" )\n");
        builder.append("    .xstDispFldRMark      = ");
        builder.append(" (").append(Arrays.toString(this.getXstDispFldRMark())).append(" )\n");
        builder.append("    .fcObjp               = ");
        builder.append(" (").append(this.getFcObjp()).append(" )\n");
        builder.append("    .lbrCRJ               = ");
        builder.append(" (").append(this.getLbrCRJ()).append(" )\n");
        builder.append("    .fSpecVanish          = ");
        builder.append(" (").append(this.getFSpecVanish()).append(" )\n");
        builder.append("    .fHasOldProps         = ");
        builder.append(" (").append(this.getFHasOldProps()).append(" )\n");
        builder.append("    .fSdtVanish           = ");
        builder.append(" (").append(this.getFSdtVanish()).append(" )\n");
        builder.append("    .wCharScale           = ");
        builder.append(" (").append(this.getWCharScale()).append(" )\n");
        builder.append("[/CHP]\n");
        return builder.toString();
    }

    @Internal
    public int getGrpfChp() {
        return this.field_1_grpfChp;
    }

    @Internal
    public void setGrpfChp(int field_1_grpfChp) {
        this.field_1_grpfChp = field_1_grpfChp;
    }

    @Internal
    public int getHps() {
        return this.field_2_hps;
    }

    @Internal
    public void setHps(int field_2_hps) {
        this.field_2_hps = field_2_hps;
    }

    @Internal
    public int getFtcAscii() {
        return this.field_3_ftcAscii;
    }

    @Internal
    public void setFtcAscii(int field_3_ftcAscii) {
        this.field_3_ftcAscii = field_3_ftcAscii;
    }

    @Internal
    public int getFtcFE() {
        return this.field_4_ftcFE;
    }

    @Internal
    public void setFtcFE(int field_4_ftcFE) {
        this.field_4_ftcFE = field_4_ftcFE;
    }

    @Internal
    public int getFtcOther() {
        return this.field_5_ftcOther;
    }

    @Internal
    public void setFtcOther(int field_5_ftcOther) {
        this.field_5_ftcOther = field_5_ftcOther;
    }

    @Internal
    public int getFtcBi() {
        return this.field_6_ftcBi;
    }

    @Internal
    public void setFtcBi(int field_6_ftcBi) {
        this.field_6_ftcBi = field_6_ftcBi;
    }

    @Internal
    public int getDxaSpace() {
        return this.field_7_dxaSpace;
    }

    @Internal
    public void setDxaSpace(int field_7_dxaSpace) {
        this.field_7_dxaSpace = field_7_dxaSpace;
    }

    @Internal
    public Colorref getCv() {
        return this.field_8_cv;
    }

    @Internal
    public void setCv(Colorref field_8_cv) {
        this.field_8_cv = field_8_cv;
    }

    @Internal
    public byte getIco() {
        return this.field_9_ico;
    }

    @Internal
    public void setIco(byte field_9_ico) {
        this.field_9_ico = field_9_ico;
    }

    @Internal
    public int getPctCharWidth() {
        return this.field_10_pctCharWidth;
    }

    @Internal
    public void setPctCharWidth(int field_10_pctCharWidth) {
        this.field_10_pctCharWidth = field_10_pctCharWidth;
    }

    @Internal
    public int getLidDefault() {
        return this.field_11_lidDefault;
    }

    @Internal
    public void setLidDefault(int field_11_lidDefault) {
        this.field_11_lidDefault = field_11_lidDefault;
    }

    @Internal
    public int getLidFE() {
        return this.field_12_lidFE;
    }

    @Internal
    public void setLidFE(int field_12_lidFE) {
        this.field_12_lidFE = field_12_lidFE;
    }

    @Internal
    public byte getKcd() {
        return this.field_13_kcd;
    }

    @Internal
    public void setKcd(byte field_13_kcd) {
        this.field_13_kcd = field_13_kcd;
    }

    @Internal
    public boolean getFUndetermine() {
        return this.field_14_fUndetermine;
    }

    @Internal
    public void setFUndetermine(boolean field_14_fUndetermine) {
        this.field_14_fUndetermine = field_14_fUndetermine;
    }

    @Internal
    public byte getIss() {
        return this.field_15_iss;
    }

    @Internal
    public void setIss(byte field_15_iss) {
        this.field_15_iss = field_15_iss;
    }

    @Internal
    public boolean getFSpecSymbol() {
        return this.field_16_fSpecSymbol;
    }

    @Internal
    public void setFSpecSymbol(boolean field_16_fSpecSymbol) {
        this.field_16_fSpecSymbol = field_16_fSpecSymbol;
    }

    @Internal
    public byte getIdct() {
        return this.field_17_idct;
    }

    @Internal
    public void setIdct(byte field_17_idct) {
        this.field_17_idct = field_17_idct;
    }

    @Internal
    public byte getIdctHint() {
        return this.field_18_idctHint;
    }

    @Internal
    public void setIdctHint(byte field_18_idctHint) {
        this.field_18_idctHint = field_18_idctHint;
    }

    @Internal
    public byte getKul() {
        return this.field_19_kul;
    }

    @Internal
    public void setKul(byte field_19_kul) {
        this.field_19_kul = field_19_kul;
    }

    @Internal
    public Hyphenation getHresi() {
        return this.field_20_hresi;
    }

    @Internal
    public void setHresi(Hyphenation field_20_hresi) {
        this.field_20_hresi = field_20_hresi;
    }

    @Internal
    public int getHpsKern() {
        return this.field_21_hpsKern;
    }

    @Internal
    public void setHpsKern(int field_21_hpsKern) {
        this.field_21_hpsKern = field_21_hpsKern;
    }

    @Internal
    public short getHpsPos() {
        return this.field_22_hpsPos;
    }

    @Internal
    public void setHpsPos(short field_22_hpsPos) {
        this.field_22_hpsPos = field_22_hpsPos;
    }

    @Internal
    public ShadingDescriptor getShd() {
        return this.field_23_shd;
    }

    @Internal
    public void setShd(ShadingDescriptor field_23_shd) {
        this.field_23_shd = field_23_shd;
    }

    @Internal
    public BorderCode getBrc() {
        return this.field_24_brc;
    }

    @Internal
    public void setBrc(BorderCode field_24_brc) {
        this.field_24_brc = field_24_brc;
    }

    @Internal
    public int getIbstRMark() {
        return this.field_25_ibstRMark;
    }

    @Internal
    public void setIbstRMark(int field_25_ibstRMark) {
        this.field_25_ibstRMark = field_25_ibstRMark;
    }

    @Internal
    public byte getSfxtText() {
        return this.field_26_sfxtText;
    }

    @Internal
    public void setSfxtText(byte field_26_sfxtText) {
        this.field_26_sfxtText = field_26_sfxtText;
    }

    @Internal
    public boolean getFDblBdr() {
        return this.field_27_fDblBdr;
    }

    @Internal
    public void setFDblBdr(boolean field_27_fDblBdr) {
        this.field_27_fDblBdr = field_27_fDblBdr;
    }

    @Internal
    public boolean getFBorderWS() {
        return this.field_28_fBorderWS;
    }

    @Internal
    public void setFBorderWS(boolean field_28_fBorderWS) {
        this.field_28_fBorderWS = field_28_fBorderWS;
    }

    @Internal
    public short getUfel() {
        return this.field_29_ufel;
    }

    @Internal
    public void setUfel(short field_29_ufel) {
        this.field_29_ufel = field_29_ufel;
    }

    @Internal
    public byte getCopt() {
        return this.field_30_copt;
    }

    @Internal
    public void setCopt(byte field_30_copt) {
        this.field_30_copt = field_30_copt;
    }

    @Internal
    public int getHpsAsci() {
        return this.field_31_hpsAsci;
    }

    @Internal
    public void setHpsAsci(int field_31_hpsAsci) {
        this.field_31_hpsAsci = field_31_hpsAsci;
    }

    @Internal
    public int getHpsFE() {
        return this.field_32_hpsFE;
    }

    @Internal
    public void setHpsFE(int field_32_hpsFE) {
        this.field_32_hpsFE = field_32_hpsFE;
    }

    @Internal
    public int getHpsBi() {
        return this.field_33_hpsBi;
    }

    @Internal
    public void setHpsBi(int field_33_hpsBi) {
        this.field_33_hpsBi = field_33_hpsBi;
    }

    @Internal
    public int getFtcSym() {
        return this.field_34_ftcSym;
    }

    @Internal
    public void setFtcSym(int field_34_ftcSym) {
        this.field_34_ftcSym = field_34_ftcSym;
    }

    @Internal
    public int getXchSym() {
        return this.field_35_xchSym;
    }

    @Internal
    public void setXchSym(int field_35_xchSym) {
        this.field_35_xchSym = field_35_xchSym;
    }

    @Internal
    public int getFcPic() {
        return this.field_36_fcPic;
    }

    @Internal
    public void setFcPic(int field_36_fcPic) {
        this.field_36_fcPic = field_36_fcPic;
    }

    @Internal
    public int getFcObj() {
        return this.field_37_fcObj;
    }

    @Internal
    public void setFcObj(int field_37_fcObj) {
        this.field_37_fcObj = field_37_fcObj;
    }

    @Internal
    public int getLTagObj() {
        return this.field_38_lTagObj;
    }

    @Internal
    public void setLTagObj(int field_38_lTagObj) {
        this.field_38_lTagObj = field_38_lTagObj;
    }

    @Internal
    public int getFcData() {
        return this.field_39_fcData;
    }

    @Internal
    public void setFcData(int field_39_fcData) {
        this.field_39_fcData = field_39_fcData;
    }

    @Internal
    public Hyphenation getHresiOld() {
        return this.field_40_hresiOld;
    }

    @Internal
    public void setHresiOld(Hyphenation field_40_hresiOld) {
        this.field_40_hresiOld = field_40_hresiOld;
    }

    @Internal
    public int getIbstRMarkDel() {
        return this.field_41_ibstRMarkDel;
    }

    @Internal
    public void setIbstRMarkDel(int field_41_ibstRMarkDel) {
        this.field_41_ibstRMarkDel = field_41_ibstRMarkDel;
    }

    @Internal
    public DateAndTime getDttmRMark() {
        return this.field_42_dttmRMark;
    }

    @Internal
    public void setDttmRMark(DateAndTime field_42_dttmRMark) {
        this.field_42_dttmRMark = field_42_dttmRMark;
    }

    @Internal
    public DateAndTime getDttmRMarkDel() {
        return this.field_43_dttmRMarkDel;
    }

    @Internal
    public void setDttmRMarkDel(DateAndTime field_43_dttmRMarkDel) {
        this.field_43_dttmRMarkDel = field_43_dttmRMarkDel;
    }

    @Internal
    public int getIstd() {
        return this.field_44_istd;
    }

    @Internal
    public void setIstd(int field_44_istd) {
        this.field_44_istd = field_44_istd;
    }

    @Internal
    public int getIdslRMReason() {
        return this.field_45_idslRMReason;
    }

    @Internal
    public void setIdslRMReason(int field_45_idslRMReason) {
        this.field_45_idslRMReason = field_45_idslRMReason;
    }

    @Internal
    public int getIdslReasonDel() {
        return this.field_46_idslReasonDel;
    }

    @Internal
    public void setIdslReasonDel(int field_46_idslReasonDel) {
        this.field_46_idslReasonDel = field_46_idslReasonDel;
    }

    @Internal
    public int getCpg() {
        return this.field_47_cpg;
    }

    @Internal
    public void setCpg(int field_47_cpg) {
        this.field_47_cpg = field_47_cpg;
    }

    @Internal
    public short getHighlight() {
        return this.field_48_Highlight;
    }

    @Internal
    public void setHighlight(short field_48_Highlight) {
        this.field_48_Highlight = field_48_Highlight;
    }

    @Internal
    public short getCharsetFlags() {
        return this.field_49_CharsetFlags;
    }

    @Internal
    public void setCharsetFlags(short field_49_CharsetFlags) {
        this.field_49_CharsetFlags = field_49_CharsetFlags;
    }

    @Internal
    public short getChse() {
        return this.field_50_chse;
    }

    @Internal
    public void setChse(short field_50_chse) {
        this.field_50_chse = field_50_chse;
    }

    @Internal
    public boolean getFPropRMark() {
        return this.field_51_fPropRMark;
    }

    @Internal
    public void setFPropRMark(boolean field_51_fPropRMark) {
        this.field_51_fPropRMark = field_51_fPropRMark;
    }

    @Internal
    public int getIbstPropRMark() {
        return this.field_52_ibstPropRMark;
    }

    @Internal
    public void setIbstPropRMark(int field_52_ibstPropRMark) {
        this.field_52_ibstPropRMark = field_52_ibstPropRMark;
    }

    @Internal
    public DateAndTime getDttmPropRMark() {
        return this.field_53_dttmPropRMark;
    }

    @Internal
    public void setDttmPropRMark(DateAndTime field_53_dttmPropRMark) {
        this.field_53_dttmPropRMark = field_53_dttmPropRMark;
    }

    @Internal
    public boolean getFConflictOrig() {
        return this.field_54_fConflictOrig;
    }

    @Internal
    public void setFConflictOrig(boolean field_54_fConflictOrig) {
        this.field_54_fConflictOrig = field_54_fConflictOrig;
    }

    @Internal
    public boolean getFConflictOtherDel() {
        return this.field_55_fConflictOtherDel;
    }

    @Internal
    public void setFConflictOtherDel(boolean field_55_fConflictOtherDel) {
        this.field_55_fConflictOtherDel = field_55_fConflictOtherDel;
    }

    @Internal
    public int getWConflict() {
        return this.field_56_wConflict;
    }

    @Internal
    public void setWConflict(int field_56_wConflict) {
        this.field_56_wConflict = field_56_wConflict;
    }

    @Internal
    public int getIbstConflict() {
        return this.field_57_IbstConflict;
    }

    @Internal
    public void setIbstConflict(int field_57_IbstConflict) {
        this.field_57_IbstConflict = field_57_IbstConflict;
    }

    @Internal
    public DateAndTime getDttmConflict() {
        return this.field_58_dttmConflict;
    }

    @Internal
    public void setDttmConflict(DateAndTime field_58_dttmConflict) {
        this.field_58_dttmConflict = field_58_dttmConflict;
    }

    @Internal
    public boolean getFDispFldRMark() {
        return this.field_59_fDispFldRMark;
    }

    @Internal
    public void setFDispFldRMark(boolean field_59_fDispFldRMark) {
        this.field_59_fDispFldRMark = field_59_fDispFldRMark;
    }

    @Internal
    public int getIbstDispFldRMark() {
        return this.field_60_ibstDispFldRMark;
    }

    @Internal
    public void setIbstDispFldRMark(int field_60_ibstDispFldRMark) {
        this.field_60_ibstDispFldRMark = field_60_ibstDispFldRMark;
    }

    @Internal
    public DateAndTime getDttmDispFldRMark() {
        return this.field_61_dttmDispFldRMark;
    }

    @Internal
    public void setDttmDispFldRMark(DateAndTime field_61_dttmDispFldRMark) {
        this.field_61_dttmDispFldRMark = field_61_dttmDispFldRMark;
    }

    @Internal
    public byte[] getXstDispFldRMark() {
        return this.field_62_xstDispFldRMark;
    }

    @Internal
    public void setXstDispFldRMark(byte[] field_62_xstDispFldRMark) {
        this.field_62_xstDispFldRMark = field_62_xstDispFldRMark;
    }

    @Internal
    public int getFcObjp() {
        return this.field_63_fcObjp;
    }

    @Internal
    public void setFcObjp(int field_63_fcObjp) {
        this.field_63_fcObjp = field_63_fcObjp;
    }

    @Internal
    public byte getLbrCRJ() {
        return this.field_64_lbrCRJ;
    }

    @Internal
    public void setLbrCRJ(byte field_64_lbrCRJ) {
        this.field_64_lbrCRJ = field_64_lbrCRJ;
    }

    @Internal
    public boolean getFSpecVanish() {
        return this.field_65_fSpecVanish;
    }

    @Internal
    public void setFSpecVanish(boolean field_65_fSpecVanish) {
        this.field_65_fSpecVanish = field_65_fSpecVanish;
    }

    @Internal
    public boolean getFHasOldProps() {
        return this.field_66_fHasOldProps;
    }

    @Internal
    public void setFHasOldProps(boolean field_66_fHasOldProps) {
        this.field_66_fHasOldProps = field_66_fHasOldProps;
    }

    @Internal
    public boolean getFSdtVanish() {
        return this.field_67_fSdtVanish;
    }

    @Internal
    public void setFSdtVanish(boolean field_67_fSdtVanish) {
        this.field_67_fSdtVanish = field_67_fSdtVanish;
    }

    @Internal
    public int getWCharScale() {
        return this.field_68_wCharScale;
    }

    @Internal
    public void setWCharScale(int field_68_wCharScale) {
        this.field_68_wCharScale = field_68_wCharScale;
    }

    @Internal
    public void setFBold(boolean value) {
        this.field_1_grpfChp = fBold.setBoolean(this.field_1_grpfChp, value);
    }

    @Internal
    public boolean isFBold() {
        return fBold.isSet(this.field_1_grpfChp);
    }

    @Internal
    public void setFItalic(boolean value) {
        this.field_1_grpfChp = fItalic.setBoolean(this.field_1_grpfChp, value);
    }

    @Internal
    public boolean isFItalic() {
        return fItalic.isSet(this.field_1_grpfChp);
    }

    @Internal
    public void setFRMarkDel(boolean value) {
        this.field_1_grpfChp = fRMarkDel.setBoolean(this.field_1_grpfChp, value);
    }

    @Internal
    public boolean isFRMarkDel() {
        return fRMarkDel.isSet(this.field_1_grpfChp);
    }

    @Internal
    public void setFOutline(boolean value) {
        this.field_1_grpfChp = fOutline.setBoolean(this.field_1_grpfChp, value);
    }

    @Internal
    public boolean isFOutline() {
        return fOutline.isSet(this.field_1_grpfChp);
    }

    @Internal
    public void setFFldVanish(boolean value) {
        this.field_1_grpfChp = fFldVanish.setBoolean(this.field_1_grpfChp, value);
    }

    @Internal
    public boolean isFFldVanish() {
        return fFldVanish.isSet(this.field_1_grpfChp);
    }

    @Internal
    public void setFSmallCaps(boolean value) {
        this.field_1_grpfChp = fSmallCaps.setBoolean(this.field_1_grpfChp, value);
    }

    @Internal
    public boolean isFSmallCaps() {
        return fSmallCaps.isSet(this.field_1_grpfChp);
    }

    @Internal
    public void setFCaps(boolean value) {
        this.field_1_grpfChp = fCaps.setBoolean(this.field_1_grpfChp, value);
    }

    @Internal
    public boolean isFCaps() {
        return fCaps.isSet(this.field_1_grpfChp);
    }

    @Internal
    public void setFVanish(boolean value) {
        this.field_1_grpfChp = fVanish.setBoolean(this.field_1_grpfChp, value);
    }

    @Internal
    public boolean isFVanish() {
        return fVanish.isSet(this.field_1_grpfChp);
    }

    @Internal
    public void setFRMark(boolean value) {
        this.field_1_grpfChp = fRMark.setBoolean(this.field_1_grpfChp, value);
    }

    @Internal
    public boolean isFRMark() {
        return fRMark.isSet(this.field_1_grpfChp);
    }

    @Internal
    public void setFSpec(boolean value) {
        this.field_1_grpfChp = fSpec.setBoolean(this.field_1_grpfChp, value);
    }

    @Internal
    public boolean isFSpec() {
        return fSpec.isSet(this.field_1_grpfChp);
    }

    @Internal
    public void setFStrike(boolean value) {
        this.field_1_grpfChp = fStrike.setBoolean(this.field_1_grpfChp, value);
    }

    @Internal
    public boolean isFStrike() {
        return fStrike.isSet(this.field_1_grpfChp);
    }

    @Internal
    public void setFObj(boolean value) {
        this.field_1_grpfChp = fObj.setBoolean(this.field_1_grpfChp, value);
    }

    @Internal
    public boolean isFObj() {
        return fObj.isSet(this.field_1_grpfChp);
    }

    @Internal
    public void setFShadow(boolean value) {
        this.field_1_grpfChp = fShadow.setBoolean(this.field_1_grpfChp, value);
    }

    @Internal
    public boolean isFShadow() {
        return fShadow.isSet(this.field_1_grpfChp);
    }

    @Internal
    public void setFLowerCase(boolean value) {
        this.field_1_grpfChp = fLowerCase.setBoolean(this.field_1_grpfChp, value);
    }

    @Internal
    public boolean isFLowerCase() {
        return fLowerCase.isSet(this.field_1_grpfChp);
    }

    @Internal
    public void setFData(boolean value) {
        this.field_1_grpfChp = fData.setBoolean(this.field_1_grpfChp, value);
    }

    @Internal
    public boolean isFData() {
        return fData.isSet(this.field_1_grpfChp);
    }

    @Internal
    public void setFOle2(boolean value) {
        this.field_1_grpfChp = fOle2.setBoolean(this.field_1_grpfChp, value);
    }

    @Internal
    public boolean isFOle2() {
        return fOle2.isSet(this.field_1_grpfChp);
    }

    @Internal
    public void setFEmboss(boolean value) {
        this.field_1_grpfChp = fEmboss.setBoolean(this.field_1_grpfChp, value);
    }

    @Internal
    public boolean isFEmboss() {
        return fEmboss.isSet(this.field_1_grpfChp);
    }

    @Internal
    public void setFImprint(boolean value) {
        this.field_1_grpfChp = fImprint.setBoolean(this.field_1_grpfChp, value);
    }

    @Internal
    public boolean isFImprint() {
        return fImprint.isSet(this.field_1_grpfChp);
    }

    @Internal
    public void setFDStrike(boolean value) {
        this.field_1_grpfChp = fDStrike.setBoolean(this.field_1_grpfChp, value);
    }

    @Internal
    public boolean isFDStrike() {
        return fDStrike.isSet(this.field_1_grpfChp);
    }

    @Internal
    public void setFUsePgsuSettings(boolean value) {
        this.field_1_grpfChp = fUsePgsuSettings.setBoolean(this.field_1_grpfChp, value);
    }

    @Internal
    public boolean isFUsePgsuSettings() {
        return fUsePgsuSettings.isSet(this.field_1_grpfChp);
    }

    @Internal
    public void setFBoldBi(boolean value) {
        this.field_1_grpfChp = fBoldBi.setBoolean(this.field_1_grpfChp, value);
    }

    @Internal
    public boolean isFBoldBi() {
        return fBoldBi.isSet(this.field_1_grpfChp);
    }

    @Internal
    public void setFComplexScripts(boolean value) {
        this.field_1_grpfChp = fComplexScripts.setBoolean(this.field_1_grpfChp, value);
    }

    @Internal
    public boolean isFComplexScripts() {
        return fComplexScripts.isSet(this.field_1_grpfChp);
    }

    @Internal
    public void setFItalicBi(boolean value) {
        this.field_1_grpfChp = fItalicBi.setBoolean(this.field_1_grpfChp, value);
    }

    @Internal
    public boolean isFItalicBi() {
        return fItalicBi.isSet(this.field_1_grpfChp);
    }

    @Internal
    public void setFBiDi(boolean value) {
        this.field_1_grpfChp = fBiDi.setBoolean(this.field_1_grpfChp, value);
    }

    @Internal
    public boolean isFBiDi() {
        return fBiDi.isSet(this.field_1_grpfChp);
    }

    @Internal
    public void setFIcoBi(boolean value) {
        this.field_1_grpfChp = fIcoBi.setBoolean(this.field_1_grpfChp, value);
    }

    @Internal
    public boolean isFIcoBi() {
        return fIcoBi.isSet(this.field_1_grpfChp);
    }

    @Internal
    public void setFNonGlyph(boolean value) {
        this.field_1_grpfChp = fNonGlyph.setBoolean(this.field_1_grpfChp, value);
    }

    @Internal
    public boolean isFNonGlyph() {
        return fNonGlyph.isSet(this.field_1_grpfChp);
    }

    @Internal
    public void setFBoldOther(boolean value) {
        this.field_1_grpfChp = fBoldOther.setBoolean(this.field_1_grpfChp, value);
    }

    @Internal
    public boolean isFBoldOther() {
        return fBoldOther.isSet(this.field_1_grpfChp);
    }

    @Internal
    public void setFItalicOther(boolean value) {
        this.field_1_grpfChp = fItalicOther.setBoolean(this.field_1_grpfChp, value);
    }

    @Internal
    public boolean isFItalicOther() {
        return fItalicOther.isSet(this.field_1_grpfChp);
    }

    @Internal
    public void setFNoProof(boolean value) {
        this.field_1_grpfChp = fNoProof.setBoolean(this.field_1_grpfChp, value);
    }

    @Internal
    public boolean isFNoProof() {
        return fNoProof.isSet(this.field_1_grpfChp);
    }

    @Internal
    public void setFWebHidden(boolean value) {
        this.field_1_grpfChp = fWebHidden.setBoolean(this.field_1_grpfChp, value);
    }

    @Internal
    public boolean isFWebHidden() {
        return fWebHidden.isSet(this.field_1_grpfChp);
    }

    @Internal
    public void setFFitText(boolean value) {
        this.field_1_grpfChp = fFitText.setBoolean(this.field_1_grpfChp, value);
    }

    @Internal
    public boolean isFFitText() {
        return fFitText.isSet(this.field_1_grpfChp);
    }

    @Internal
    public void setFCalc(boolean value) {
        this.field_1_grpfChp = fCalc.setBoolean(this.field_1_grpfChp, value);
    }

    @Internal
    public boolean isFCalc() {
        return fCalc.isSet(this.field_1_grpfChp);
    }

    @Internal
    public void setFFmtLineProp(boolean value) {
        this.field_1_grpfChp = fFmtLineProp.setBoolean(this.field_1_grpfChp, value);
    }

    @Internal
    public boolean isFFmtLineProp() {
        return fFmtLineProp.isSet(this.field_1_grpfChp);
    }

    @Internal
    public void setItypFELayout(short value) {
        this.field_29_ufel = (short)itypFELayout.setValue(this.field_29_ufel, value);
    }

    @Internal
    public short getItypFELayout() {
        return (short)itypFELayout.getValue(this.field_29_ufel);
    }

    @Internal
    public void setFTNY(boolean value) {
        this.field_29_ufel = (short)fTNY.setBoolean(this.field_29_ufel, value);
    }

    @Internal
    public boolean isFTNY() {
        return fTNY.isSet(this.field_29_ufel);
    }

    @Internal
    public void setFWarichu(boolean value) {
        this.field_29_ufel = (short)fWarichu.setBoolean(this.field_29_ufel, value);
    }

    @Internal
    public boolean isFWarichu() {
        return fWarichu.isSet(this.field_29_ufel);
    }

    @Internal
    public void setFKumimoji(boolean value) {
        this.field_29_ufel = (short)fKumimoji.setBoolean(this.field_29_ufel, value);
    }

    @Internal
    public boolean isFKumimoji() {
        return fKumimoji.isSet(this.field_29_ufel);
    }

    @Internal
    public void setFRuby(boolean value) {
        this.field_29_ufel = (short)fRuby.setBoolean(this.field_29_ufel, value);
    }

    @Internal
    public boolean isFRuby() {
        return fRuby.isSet(this.field_29_ufel);
    }

    @Internal
    public void setFLSFitText(boolean value) {
        this.field_29_ufel = (short)fLSFitText.setBoolean(this.field_29_ufel, value);
    }

    @Internal
    public boolean isFLSFitText() {
        return fLSFitText.isSet(this.field_29_ufel);
    }

    @Internal
    public void setSpare(byte value) {
        this.field_29_ufel = (short)spare.setValue(this.field_29_ufel, value);
    }

    @Internal
    public byte getSpare() {
        return (byte)spare.getValue(this.field_29_ufel);
    }

    @Internal
    public void setIWarichuBracket(byte value) {
        this.field_30_copt = (byte)iWarichuBracket.setValue(this.field_30_copt, value);
    }

    @Internal
    public byte getIWarichuBracket() {
        return (byte)iWarichuBracket.getValue(this.field_30_copt);
    }

    @Internal
    public void setFWarichuNoOpenBracket(boolean value) {
        this.field_30_copt = (byte)fWarichuNoOpenBracket.setBoolean(this.field_30_copt, value);
    }

    @Internal
    public boolean isFWarichuNoOpenBracket() {
        return fWarichuNoOpenBracket.isSet(this.field_30_copt);
    }

    @Internal
    public void setFTNYCompress(boolean value) {
        this.field_30_copt = (byte)fTNYCompress.setBoolean(this.field_30_copt, value);
    }

    @Internal
    public boolean isFTNYCompress() {
        return fTNYCompress.isSet(this.field_30_copt);
    }

    @Internal
    public void setFTNYFetchTxm(boolean value) {
        this.field_30_copt = (byte)fTNYFetchTxm.setBoolean(this.field_30_copt, value);
    }

    @Internal
    public boolean isFTNYFetchTxm() {
        return fTNYFetchTxm.isSet(this.field_30_copt);
    }

    @Internal
    public void setFCellFitText(boolean value) {
        this.field_30_copt = (byte)fCellFitText.setBoolean(this.field_30_copt, value);
    }

    @Internal
    public boolean isFCellFitText() {
        return fCellFitText.isSet(this.field_30_copt);
    }

    @Internal
    public void setUnused(boolean value) {
        this.field_30_copt = (byte)unused.setBoolean(this.field_30_copt, value);
    }

    @Internal
    public boolean isUnused() {
        return unused.isSet(this.field_30_copt);
    }

    @Internal
    public void setIcoHighlight(byte value) {
        this.field_48_Highlight = (short)icoHighlight.setValue(this.field_48_Highlight, value);
    }

    @Internal
    public byte getIcoHighlight() {
        return (byte)icoHighlight.getValue(this.field_48_Highlight);
    }

    @Internal
    public void setFHighlight(boolean value) {
        this.field_48_Highlight = (short)fHighlight.setBoolean(this.field_48_Highlight, value);
    }

    @Internal
    public boolean isFHighlight() {
        return fHighlight.isSet(this.field_48_Highlight);
    }

    @Internal
    public void setFChsDiff(boolean value) {
        this.field_49_CharsetFlags = (short)fChsDiff.setBoolean(this.field_49_CharsetFlags, value);
    }

    @Internal
    public boolean isFChsDiff() {
        return fChsDiff.isSet(this.field_49_CharsetFlags);
    }

    @Internal
    public void setFMacChs(boolean value) {
        this.field_49_CharsetFlags = (short)fMacChs.setBoolean(this.field_49_CharsetFlags, value);
    }

    @Internal
    public boolean isFMacChs() {
        return fMacChs.isSet(this.field_49_CharsetFlags);
    }
}


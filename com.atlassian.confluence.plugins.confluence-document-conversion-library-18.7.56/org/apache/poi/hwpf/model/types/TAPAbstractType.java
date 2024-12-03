/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model.types;

import java.util.Arrays;
import java.util.stream.Stream;
import org.apache.poi.hwpf.usermodel.BorderCode;
import org.apache.poi.hwpf.usermodel.ShadingDescriptor;
import org.apache.poi.hwpf.usermodel.TableAutoformatLookSpecifier;
import org.apache.poi.hwpf.usermodel.TableCellDescriptor;
import org.apache.poi.util.BitField;
import org.apache.poi.util.Internal;

@Internal
public abstract class TAPAbstractType {
    private static final BitField fAutofit = new BitField(1);
    private static final BitField fKeepFollow = new BitField(2);
    private static final BitField ftsWidth = new BitField(28);
    private static final BitField ftsWidthIndent = new BitField(224);
    private static final BitField ftsWidthBefore = new BitField(1792);
    private static final BitField ftsWidthAfter = new BitField(14336);
    private static final BitField fNeverBeenAutofit = new BitField(16384);
    private static final BitField fInvalAutofit = new BitField(32768);
    private static final BitField widthAndFitsFlags_empty1 = new BitField(458752);
    private static final BitField fVert = new BitField(524288);
    private static final BitField pcVert = new BitField(0x300000);
    private static final BitField pcHorz = new BitField(0xC00000);
    private static final BitField widthAndFitsFlags_empty2 = new BitField(-16777216);
    private static final BitField fFirstRow = new BitField(1);
    private static final BitField fLastRow = new BitField(2);
    private static final BitField fOutline = new BitField(4);
    private static final BitField fOrigWordTableRules = new BitField(8);
    private static final BitField fCellSpacing = new BitField(16);
    private static final BitField grpfTap_unused = new BitField(65504);
    private static final BitField fWrapToWwd = new BitField(1);
    private static final BitField fNotPageView = new BitField(2);
    private static final BitField viewFlags_unused1 = new BitField(4);
    private static final BitField fWebView = new BitField(8);
    private static final BitField fAdjusted = new BitField(16);
    private static final BitField viewFlags_unused2 = new BitField(65504);
    protected short field_1_istd;
    protected short field_2_jc;
    protected int field_3_dxaGapHalf;
    protected int field_4_dyaRowHeight;
    protected boolean field_5_fCantSplit;
    protected boolean field_6_fCantSplit90;
    protected boolean field_7_fTableHeader;
    protected TableAutoformatLookSpecifier field_8_tlp;
    protected short field_9_wWidth;
    protected short field_10_wWidthIndent;
    protected short field_11_wWidthBefore;
    protected short field_12_wWidthAfter;
    protected int field_13_widthAndFitsFlags;
    protected int field_14_dxaAbs;
    protected int field_15_dyaAbs;
    protected int field_16_dxaFromText;
    protected int field_17_dyaFromText;
    protected int field_18_dxaFromTextRight;
    protected int field_19_dyaFromTextBottom;
    protected byte field_20_fBiDi;
    protected byte field_21_fRTL;
    protected byte field_22_fNoAllowOverlap;
    protected byte field_23_fSpare;
    protected int field_24_grpfTap;
    protected int field_25_internalFlags;
    protected short field_26_itcMac;
    protected int field_27_dxaAdjust;
    protected int field_28_dxaWebView;
    protected int field_29_dxaRTEWrapWidth;
    protected int field_30_dxaColWidthWwd;
    protected short field_31_pctWwd;
    protected int field_32_viewFlags;
    protected short[] field_33_rgdxaCenter;
    protected short[] field_34_rgdxaCenterPrint;
    protected ShadingDescriptor field_35_shdTable;
    protected BorderCode field_36_brcBottom;
    protected BorderCode field_37_brcTop;
    protected BorderCode field_38_brcLeft;
    protected BorderCode field_39_brcRight;
    protected BorderCode field_40_brcVertical;
    protected BorderCode field_41_brcHorizontal;
    protected short field_42_wCellPaddingDefaultTop;
    protected short field_43_wCellPaddingDefaultLeft;
    protected short field_44_wCellPaddingDefaultBottom;
    protected short field_45_wCellPaddingDefaultRight;
    protected byte field_46_ftsCellPaddingDefaultTop;
    protected byte field_47_ftsCellPaddingDefaultLeft;
    protected byte field_48_ftsCellPaddingDefaultBottom;
    protected byte field_49_ftsCellPaddingDefaultRight;
    protected short field_50_wCellSpacingDefaultTop;
    protected short field_51_wCellSpacingDefaultLeft;
    protected short field_52_wCellSpacingDefaultBottom;
    protected short field_53_wCellSpacingDefaultRight;
    protected byte field_54_ftsCellSpacingDefaultTop;
    protected byte field_55_ftsCellSpacingDefaultLeft;
    protected byte field_56_ftsCellSpacingDefaultBottom;
    protected byte field_57_ftsCellSpacingDefaultRight;
    protected short field_58_wCellPaddingOuterTop;
    protected short field_59_wCellPaddingOuterLeft;
    protected short field_60_wCellPaddingOuterBottom;
    protected short field_61_wCellPaddingOuterRight;
    protected byte field_62_ftsCellPaddingOuterTop;
    protected byte field_63_ftsCellPaddingOuterLeft;
    protected byte field_64_ftsCellPaddingOuterBottom;
    protected byte field_65_ftsCellPaddingOuterRight;
    protected short field_66_wCellSpacingOuterTop;
    protected short field_67_wCellSpacingOuterLeft;
    protected short field_68_wCellSpacingOuterBottom;
    protected short field_69_wCellSpacingOuterRight;
    protected byte field_70_ftsCellSpacingOuterTop;
    protected byte field_71_ftsCellSpacingOuterLeft;
    protected byte field_72_ftsCellSpacingOuterBottom;
    protected byte field_73_ftsCellSpacingOuterRight;
    protected TableCellDescriptor[] field_74_rgtc;
    protected ShadingDescriptor[] field_75_rgshd;
    protected byte field_76_fPropRMark;
    protected byte field_77_fHasOldProps;
    protected short field_78_cHorzBands;
    protected short field_79_cVertBands;
    protected BorderCode field_80_rgbrcInsideDefault_0;
    protected BorderCode field_81_rgbrcInsideDefault_1;

    protected TAPAbstractType() {
        this.field_8_tlp = new TableAutoformatLookSpecifier();
        this.field_33_rgdxaCenter = new short[0];
        this.field_34_rgdxaCenterPrint = new short[0];
        this.field_35_shdTable = new ShadingDescriptor();
        this.field_36_brcBottom = new BorderCode();
        this.field_37_brcTop = new BorderCode();
        this.field_38_brcLeft = new BorderCode();
        this.field_39_brcRight = new BorderCode();
        this.field_40_brcVertical = new BorderCode();
        this.field_41_brcHorizontal = new BorderCode();
        this.field_74_rgtc = new TableCellDescriptor[0];
        this.field_75_rgshd = new ShadingDescriptor[0];
        this.field_80_rgbrcInsideDefault_0 = new BorderCode();
        this.field_81_rgbrcInsideDefault_1 = new BorderCode();
    }

    protected TAPAbstractType(TAPAbstractType other) {
        this.field_1_istd = other.field_1_istd;
        this.field_2_jc = other.field_2_jc;
        this.field_3_dxaGapHalf = other.field_3_dxaGapHalf;
        this.field_4_dyaRowHeight = other.field_4_dyaRowHeight;
        this.field_5_fCantSplit = other.field_5_fCantSplit;
        this.field_6_fCantSplit90 = other.field_6_fCantSplit90;
        this.field_7_fTableHeader = other.field_7_fTableHeader;
        this.field_8_tlp = other.field_8_tlp == null ? null : other.field_8_tlp.copy();
        this.field_9_wWidth = other.field_9_wWidth;
        this.field_10_wWidthIndent = other.field_10_wWidthIndent;
        this.field_11_wWidthBefore = other.field_11_wWidthBefore;
        this.field_12_wWidthAfter = other.field_12_wWidthAfter;
        this.field_13_widthAndFitsFlags = other.field_13_widthAndFitsFlags;
        this.field_14_dxaAbs = other.field_14_dxaAbs;
        this.field_15_dyaAbs = other.field_15_dyaAbs;
        this.field_16_dxaFromText = other.field_16_dxaFromText;
        this.field_17_dyaFromText = other.field_17_dyaFromText;
        this.field_18_dxaFromTextRight = other.field_18_dxaFromTextRight;
        this.field_19_dyaFromTextBottom = other.field_19_dyaFromTextBottom;
        this.field_20_fBiDi = other.field_20_fBiDi;
        this.field_21_fRTL = other.field_21_fRTL;
        this.field_22_fNoAllowOverlap = other.field_22_fNoAllowOverlap;
        this.field_23_fSpare = other.field_23_fSpare;
        this.field_24_grpfTap = other.field_24_grpfTap;
        this.field_25_internalFlags = other.field_25_internalFlags;
        this.field_26_itcMac = other.field_26_itcMac;
        this.field_27_dxaAdjust = other.field_27_dxaAdjust;
        this.field_28_dxaWebView = other.field_28_dxaWebView;
        this.field_29_dxaRTEWrapWidth = other.field_29_dxaRTEWrapWidth;
        this.field_30_dxaColWidthWwd = other.field_30_dxaColWidthWwd;
        this.field_31_pctWwd = other.field_31_pctWwd;
        this.field_32_viewFlags = other.field_32_viewFlags;
        this.field_33_rgdxaCenter = other.field_33_rgdxaCenter == null ? null : (short[])other.field_33_rgdxaCenter.clone();
        this.field_34_rgdxaCenterPrint = other.field_34_rgdxaCenterPrint == null ? null : (short[])other.field_34_rgdxaCenterPrint.clone();
        this.field_35_shdTable = other.field_35_shdTable == null ? null : other.field_35_shdTable.copy();
        this.field_36_brcBottom = other.field_36_brcBottom == null ? null : other.field_36_brcBottom.copy();
        this.field_37_brcTop = other.field_37_brcTop == null ? null : other.field_37_brcTop.copy();
        this.field_38_brcLeft = other.field_38_brcLeft == null ? null : other.field_38_brcLeft.copy();
        this.field_39_brcRight = other.field_39_brcRight == null ? null : other.field_39_brcRight.copy();
        this.field_40_brcVertical = other.field_40_brcVertical == null ? null : other.field_40_brcVertical.copy();
        this.field_41_brcHorizontal = other.field_41_brcHorizontal == null ? null : other.field_41_brcHorizontal.copy();
        this.field_42_wCellPaddingDefaultTop = other.field_42_wCellPaddingDefaultTop;
        this.field_43_wCellPaddingDefaultLeft = other.field_43_wCellPaddingDefaultLeft;
        this.field_44_wCellPaddingDefaultBottom = other.field_44_wCellPaddingDefaultBottom;
        this.field_45_wCellPaddingDefaultRight = other.field_45_wCellPaddingDefaultRight;
        this.field_46_ftsCellPaddingDefaultTop = other.field_46_ftsCellPaddingDefaultTop;
        this.field_47_ftsCellPaddingDefaultLeft = other.field_47_ftsCellPaddingDefaultLeft;
        this.field_48_ftsCellPaddingDefaultBottom = other.field_48_ftsCellPaddingDefaultBottom;
        this.field_49_ftsCellPaddingDefaultRight = other.field_49_ftsCellPaddingDefaultRight;
        this.field_50_wCellSpacingDefaultTop = other.field_50_wCellSpacingDefaultTop;
        this.field_51_wCellSpacingDefaultLeft = other.field_51_wCellSpacingDefaultLeft;
        this.field_52_wCellSpacingDefaultBottom = other.field_52_wCellSpacingDefaultBottom;
        this.field_53_wCellSpacingDefaultRight = other.field_53_wCellSpacingDefaultRight;
        this.field_54_ftsCellSpacingDefaultTop = other.field_54_ftsCellSpacingDefaultTop;
        this.field_55_ftsCellSpacingDefaultLeft = other.field_55_ftsCellSpacingDefaultLeft;
        this.field_56_ftsCellSpacingDefaultBottom = other.field_56_ftsCellSpacingDefaultBottom;
        this.field_57_ftsCellSpacingDefaultRight = other.field_57_ftsCellSpacingDefaultRight;
        this.field_58_wCellPaddingOuterTop = other.field_58_wCellPaddingOuterTop;
        this.field_59_wCellPaddingOuterLeft = other.field_59_wCellPaddingOuterLeft;
        this.field_60_wCellPaddingOuterBottom = other.field_60_wCellPaddingOuterBottom;
        this.field_61_wCellPaddingOuterRight = other.field_61_wCellPaddingOuterRight;
        this.field_62_ftsCellPaddingOuterTop = other.field_62_ftsCellPaddingOuterTop;
        this.field_63_ftsCellPaddingOuterLeft = other.field_63_ftsCellPaddingOuterLeft;
        this.field_64_ftsCellPaddingOuterBottom = other.field_64_ftsCellPaddingOuterBottom;
        this.field_65_ftsCellPaddingOuterRight = other.field_65_ftsCellPaddingOuterRight;
        this.field_66_wCellSpacingOuterTop = other.field_66_wCellSpacingOuterTop;
        this.field_67_wCellSpacingOuterLeft = other.field_67_wCellSpacingOuterLeft;
        this.field_68_wCellSpacingOuterBottom = other.field_68_wCellSpacingOuterBottom;
        this.field_69_wCellSpacingOuterRight = other.field_69_wCellSpacingOuterRight;
        this.field_70_ftsCellSpacingOuterTop = other.field_70_ftsCellSpacingOuterTop;
        this.field_71_ftsCellSpacingOuterLeft = other.field_71_ftsCellSpacingOuterLeft;
        this.field_72_ftsCellSpacingOuterBottom = other.field_72_ftsCellSpacingOuterBottom;
        this.field_73_ftsCellSpacingOuterRight = other.field_73_ftsCellSpacingOuterRight;
        this.field_74_rgtc = other.field_74_rgtc == null ? null : (TableCellDescriptor[])Stream.of(other.field_74_rgtc).map(TableCellDescriptor::copy).toArray(TableCellDescriptor[]::new);
        this.field_75_rgshd = other.field_75_rgshd == null ? null : (ShadingDescriptor[])Stream.of(other.field_75_rgshd).map(ShadingDescriptor::copy).toArray(ShadingDescriptor[]::new);
        this.field_76_fPropRMark = other.field_76_fPropRMark;
        this.field_77_fHasOldProps = other.field_77_fHasOldProps;
        this.field_78_cHorzBands = other.field_78_cHorzBands;
        this.field_79_cVertBands = other.field_79_cVertBands;
        this.field_80_rgbrcInsideDefault_0 = other.field_80_rgbrcInsideDefault_0 == null ? null : other.field_80_rgbrcInsideDefault_0.copy();
        this.field_81_rgbrcInsideDefault_1 = other.field_81_rgbrcInsideDefault_1 == null ? null : other.field_81_rgbrcInsideDefault_1.copy();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[TAP]\n");
        builder.append("    .istd                 = ");
        builder.append(" (").append(this.getIstd()).append(" )\n");
        builder.append("    .jc                   = ");
        builder.append(" (").append(this.getJc()).append(" )\n");
        builder.append("    .dxaGapHalf           = ");
        builder.append(" (").append(this.getDxaGapHalf()).append(" )\n");
        builder.append("    .dyaRowHeight         = ");
        builder.append(" (").append(this.getDyaRowHeight()).append(" )\n");
        builder.append("    .fCantSplit           = ");
        builder.append(" (").append(this.getFCantSplit()).append(" )\n");
        builder.append("    .fCantSplit90         = ");
        builder.append(" (").append(this.getFCantSplit90()).append(" )\n");
        builder.append("    .fTableHeader         = ");
        builder.append(" (").append(this.getFTableHeader()).append(" )\n");
        builder.append("    .tlp                  = ");
        builder.append(" (").append(this.getTlp()).append(" )\n");
        builder.append("    .wWidth               = ");
        builder.append(" (").append(this.getWWidth()).append(" )\n");
        builder.append("    .wWidthIndent         = ");
        builder.append(" (").append(this.getWWidthIndent()).append(" )\n");
        builder.append("    .wWidthBefore         = ");
        builder.append(" (").append(this.getWWidthBefore()).append(" )\n");
        builder.append("    .wWidthAfter          = ");
        builder.append(" (").append(this.getWWidthAfter()).append(" )\n");
        builder.append("    .widthAndFitsFlags    = ");
        builder.append(" (").append(this.getWidthAndFitsFlags()).append(" )\n");
        builder.append("         .fAutofit                 = ").append(this.isFAutofit()).append('\n');
        builder.append("         .fKeepFollow              = ").append(this.isFKeepFollow()).append('\n');
        builder.append("         .ftsWidth                 = ").append(this.getFtsWidth()).append('\n');
        builder.append("         .ftsWidthIndent           = ").append(this.getFtsWidthIndent()).append('\n');
        builder.append("         .ftsWidthBefore           = ").append(this.getFtsWidthBefore()).append('\n');
        builder.append("         .ftsWidthAfter            = ").append(this.getFtsWidthAfter()).append('\n');
        builder.append("         .fNeverBeenAutofit        = ").append(this.isFNeverBeenAutofit()).append('\n');
        builder.append("         .fInvalAutofit            = ").append(this.isFInvalAutofit()).append('\n');
        builder.append("         .widthAndFitsFlags_empty1     = ").append(this.getWidthAndFitsFlags_empty1()).append('\n');
        builder.append("         .fVert                    = ").append(this.isFVert()).append('\n');
        builder.append("         .pcVert                   = ").append(this.getPcVert()).append('\n');
        builder.append("         .pcHorz                   = ").append(this.getPcHorz()).append('\n');
        builder.append("         .widthAndFitsFlags_empty2     = ").append(this.getWidthAndFitsFlags_empty2()).append('\n');
        builder.append("    .dxaAbs               = ");
        builder.append(" (").append(this.getDxaAbs()).append(" )\n");
        builder.append("    .dyaAbs               = ");
        builder.append(" (").append(this.getDyaAbs()).append(" )\n");
        builder.append("    .dxaFromText          = ");
        builder.append(" (").append(this.getDxaFromText()).append(" )\n");
        builder.append("    .dyaFromText          = ");
        builder.append(" (").append(this.getDyaFromText()).append(" )\n");
        builder.append("    .dxaFromTextRight     = ");
        builder.append(" (").append(this.getDxaFromTextRight()).append(" )\n");
        builder.append("    .dyaFromTextBottom    = ");
        builder.append(" (").append(this.getDyaFromTextBottom()).append(" )\n");
        builder.append("    .fBiDi                = ");
        builder.append(" (").append(this.getFBiDi()).append(" )\n");
        builder.append("    .fRTL                 = ");
        builder.append(" (").append(this.getFRTL()).append(" )\n");
        builder.append("    .fNoAllowOverlap      = ");
        builder.append(" (").append(this.getFNoAllowOverlap()).append(" )\n");
        builder.append("    .fSpare               = ");
        builder.append(" (").append(this.getFSpare()).append(" )\n");
        builder.append("    .grpfTap              = ");
        builder.append(" (").append(this.getGrpfTap()).append(" )\n");
        builder.append("    .internalFlags        = ");
        builder.append(" (").append(this.getInternalFlags()).append(" )\n");
        builder.append("         .fFirstRow                = ").append(this.isFFirstRow()).append('\n');
        builder.append("         .fLastRow                 = ").append(this.isFLastRow()).append('\n');
        builder.append("         .fOutline                 = ").append(this.isFOutline()).append('\n');
        builder.append("         .fOrigWordTableRules      = ").append(this.isFOrigWordTableRules()).append('\n');
        builder.append("         .fCellSpacing             = ").append(this.isFCellSpacing()).append('\n');
        builder.append("         .grpfTap_unused           = ").append(this.getGrpfTap_unused()).append('\n');
        builder.append("    .itcMac               = ");
        builder.append(" (").append(this.getItcMac()).append(" )\n");
        builder.append("    .dxaAdjust            = ");
        builder.append(" (").append(this.getDxaAdjust()).append(" )\n");
        builder.append("    .dxaWebView           = ");
        builder.append(" (").append(this.getDxaWebView()).append(" )\n");
        builder.append("    .dxaRTEWrapWidth      = ");
        builder.append(" (").append(this.getDxaRTEWrapWidth()).append(" )\n");
        builder.append("    .dxaColWidthWwd       = ");
        builder.append(" (").append(this.getDxaColWidthWwd()).append(" )\n");
        builder.append("    .pctWwd               = ");
        builder.append(" (").append(this.getPctWwd()).append(" )\n");
        builder.append("    .viewFlags            = ");
        builder.append(" (").append(this.getViewFlags()).append(" )\n");
        builder.append("         .fWrapToWwd               = ").append(this.isFWrapToWwd()).append('\n');
        builder.append("         .fNotPageView             = ").append(this.isFNotPageView()).append('\n');
        builder.append("         .viewFlags_unused1        = ").append(this.isViewFlags_unused1()).append('\n');
        builder.append("         .fWebView                 = ").append(this.isFWebView()).append('\n');
        builder.append("         .fAdjusted                = ").append(this.isFAdjusted()).append('\n');
        builder.append("         .viewFlags_unused2        = ").append(this.getViewFlags_unused2()).append('\n');
        builder.append("    .rgdxaCenter          = ");
        builder.append(" (").append(Arrays.toString(this.getRgdxaCenter())).append(" )\n");
        builder.append("    .rgdxaCenterPrint     = ");
        builder.append(" (").append(Arrays.toString(this.getRgdxaCenterPrint())).append(" )\n");
        builder.append("    .shdTable             = ");
        builder.append(" (").append(this.getShdTable()).append(" )\n");
        builder.append("    .brcBottom            = ");
        builder.append(" (").append(this.getBrcBottom()).append(" )\n");
        builder.append("    .brcTop               = ");
        builder.append(" (").append(this.getBrcTop()).append(" )\n");
        builder.append("    .brcLeft              = ");
        builder.append(" (").append(this.getBrcLeft()).append(" )\n");
        builder.append("    .brcRight             = ");
        builder.append(" (").append(this.getBrcRight()).append(" )\n");
        builder.append("    .brcVertical          = ");
        builder.append(" (").append(this.getBrcVertical()).append(" )\n");
        builder.append("    .brcHorizontal        = ");
        builder.append(" (").append(this.getBrcHorizontal()).append(" )\n");
        builder.append("    .wCellPaddingDefaultTop = ");
        builder.append(" (").append(this.getWCellPaddingDefaultTop()).append(" )\n");
        builder.append("    .wCellPaddingDefaultLeft = ");
        builder.append(" (").append(this.getWCellPaddingDefaultLeft()).append(" )\n");
        builder.append("    .wCellPaddingDefaultBottom = ");
        builder.append(" (").append(this.getWCellPaddingDefaultBottom()).append(" )\n");
        builder.append("    .wCellPaddingDefaultRight = ");
        builder.append(" (").append(this.getWCellPaddingDefaultRight()).append(" )\n");
        builder.append("    .ftsCellPaddingDefaultTop = ");
        builder.append(" (").append(this.getFtsCellPaddingDefaultTop()).append(" )\n");
        builder.append("    .ftsCellPaddingDefaultLeft = ");
        builder.append(" (").append(this.getFtsCellPaddingDefaultLeft()).append(" )\n");
        builder.append("    .ftsCellPaddingDefaultBottom = ");
        builder.append(" (").append(this.getFtsCellPaddingDefaultBottom()).append(" )\n");
        builder.append("    .ftsCellPaddingDefaultRight = ");
        builder.append(" (").append(this.getFtsCellPaddingDefaultRight()).append(" )\n");
        builder.append("    .wCellSpacingDefaultTop = ");
        builder.append(" (").append(this.getWCellSpacingDefaultTop()).append(" )\n");
        builder.append("    .wCellSpacingDefaultLeft = ");
        builder.append(" (").append(this.getWCellSpacingDefaultLeft()).append(" )\n");
        builder.append("    .wCellSpacingDefaultBottom = ");
        builder.append(" (").append(this.getWCellSpacingDefaultBottom()).append(" )\n");
        builder.append("    .wCellSpacingDefaultRight = ");
        builder.append(" (").append(this.getWCellSpacingDefaultRight()).append(" )\n");
        builder.append("    .ftsCellSpacingDefaultTop = ");
        builder.append(" (").append(this.getFtsCellSpacingDefaultTop()).append(" )\n");
        builder.append("    .ftsCellSpacingDefaultLeft = ");
        builder.append(" (").append(this.getFtsCellSpacingDefaultLeft()).append(" )\n");
        builder.append("    .ftsCellSpacingDefaultBottom = ");
        builder.append(" (").append(this.getFtsCellSpacingDefaultBottom()).append(" )\n");
        builder.append("    .ftsCellSpacingDefaultRight = ");
        builder.append(" (").append(this.getFtsCellSpacingDefaultRight()).append(" )\n");
        builder.append("    .wCellPaddingOuterTop = ");
        builder.append(" (").append(this.getWCellPaddingOuterTop()).append(" )\n");
        builder.append("    .wCellPaddingOuterLeft = ");
        builder.append(" (").append(this.getWCellPaddingOuterLeft()).append(" )\n");
        builder.append("    .wCellPaddingOuterBottom = ");
        builder.append(" (").append(this.getWCellPaddingOuterBottom()).append(" )\n");
        builder.append("    .wCellPaddingOuterRight = ");
        builder.append(" (").append(this.getWCellPaddingOuterRight()).append(" )\n");
        builder.append("    .ftsCellPaddingOuterTop = ");
        builder.append(" (").append(this.getFtsCellPaddingOuterTop()).append(" )\n");
        builder.append("    .ftsCellPaddingOuterLeft = ");
        builder.append(" (").append(this.getFtsCellPaddingOuterLeft()).append(" )\n");
        builder.append("    .ftsCellPaddingOuterBottom = ");
        builder.append(" (").append(this.getFtsCellPaddingOuterBottom()).append(" )\n");
        builder.append("    .ftsCellPaddingOuterRight = ");
        builder.append(" (").append(this.getFtsCellPaddingOuterRight()).append(" )\n");
        builder.append("    .wCellSpacingOuterTop = ");
        builder.append(" (").append(this.getWCellSpacingOuterTop()).append(" )\n");
        builder.append("    .wCellSpacingOuterLeft = ");
        builder.append(" (").append(this.getWCellSpacingOuterLeft()).append(" )\n");
        builder.append("    .wCellSpacingOuterBottom = ");
        builder.append(" (").append(this.getWCellSpacingOuterBottom()).append(" )\n");
        builder.append("    .wCellSpacingOuterRight = ");
        builder.append(" (").append(this.getWCellSpacingOuterRight()).append(" )\n");
        builder.append("    .ftsCellSpacingOuterTop = ");
        builder.append(" (").append(this.getFtsCellSpacingOuterTop()).append(" )\n");
        builder.append("    .ftsCellSpacingOuterLeft = ");
        builder.append(" (").append(this.getFtsCellSpacingOuterLeft()).append(" )\n");
        builder.append("    .ftsCellSpacingOuterBottom = ");
        builder.append(" (").append(this.getFtsCellSpacingOuterBottom()).append(" )\n");
        builder.append("    .ftsCellSpacingOuterRight = ");
        builder.append(" (").append(this.getFtsCellSpacingOuterRight()).append(" )\n");
        builder.append("    .rgtc                 = ");
        builder.append(" (").append(Arrays.toString(this.getRgtc())).append(" )\n");
        builder.append("    .rgshd                = ");
        builder.append(" (").append(Arrays.toString(this.getRgshd())).append(" )\n");
        builder.append("    .fPropRMark           = ");
        builder.append(" (").append(this.getFPropRMark()).append(" )\n");
        builder.append("    .fHasOldProps         = ");
        builder.append(" (").append(this.getFHasOldProps()).append(" )\n");
        builder.append("    .cHorzBands           = ");
        builder.append(" (").append(this.getCHorzBands()).append(" )\n");
        builder.append("    .cVertBands           = ");
        builder.append(" (").append(this.getCVertBands()).append(" )\n");
        builder.append("    .rgbrcInsideDefault_0 = ");
        builder.append(" (").append(this.getRgbrcInsideDefault_0()).append(" )\n");
        builder.append("    .rgbrcInsideDefault_1 = ");
        builder.append(" (").append(this.getRgbrcInsideDefault_1()).append(" )\n");
        builder.append("[/TAP]\n");
        return builder.toString();
    }

    @Internal
    public short getIstd() {
        return this.field_1_istd;
    }

    @Internal
    public void setIstd(short field_1_istd) {
        this.field_1_istd = field_1_istd;
    }

    @Internal
    public short getJc() {
        return this.field_2_jc;
    }

    @Internal
    public void setJc(short field_2_jc) {
        this.field_2_jc = field_2_jc;
    }

    @Internal
    public int getDxaGapHalf() {
        return this.field_3_dxaGapHalf;
    }

    @Internal
    public void setDxaGapHalf(int field_3_dxaGapHalf) {
        this.field_3_dxaGapHalf = field_3_dxaGapHalf;
    }

    @Internal
    public int getDyaRowHeight() {
        return this.field_4_dyaRowHeight;
    }

    @Internal
    public void setDyaRowHeight(int field_4_dyaRowHeight) {
        this.field_4_dyaRowHeight = field_4_dyaRowHeight;
    }

    @Internal
    public boolean getFCantSplit() {
        return this.field_5_fCantSplit;
    }

    @Internal
    public void setFCantSplit(boolean field_5_fCantSplit) {
        this.field_5_fCantSplit = field_5_fCantSplit;
    }

    @Internal
    public boolean getFCantSplit90() {
        return this.field_6_fCantSplit90;
    }

    @Internal
    public void setFCantSplit90(boolean field_6_fCantSplit90) {
        this.field_6_fCantSplit90 = field_6_fCantSplit90;
    }

    @Internal
    public boolean getFTableHeader() {
        return this.field_7_fTableHeader;
    }

    @Internal
    public void setFTableHeader(boolean field_7_fTableHeader) {
        this.field_7_fTableHeader = field_7_fTableHeader;
    }

    @Internal
    public TableAutoformatLookSpecifier getTlp() {
        return this.field_8_tlp;
    }

    @Internal
    public void setTlp(TableAutoformatLookSpecifier field_8_tlp) {
        this.field_8_tlp = field_8_tlp;
    }

    @Internal
    public short getWWidth() {
        return this.field_9_wWidth;
    }

    @Internal
    public void setWWidth(short field_9_wWidth) {
        this.field_9_wWidth = field_9_wWidth;
    }

    @Internal
    public short getWWidthIndent() {
        return this.field_10_wWidthIndent;
    }

    @Internal
    public void setWWidthIndent(short field_10_wWidthIndent) {
        this.field_10_wWidthIndent = field_10_wWidthIndent;
    }

    @Internal
    public short getWWidthBefore() {
        return this.field_11_wWidthBefore;
    }

    @Internal
    public void setWWidthBefore(short field_11_wWidthBefore) {
        this.field_11_wWidthBefore = field_11_wWidthBefore;
    }

    @Internal
    public short getWWidthAfter() {
        return this.field_12_wWidthAfter;
    }

    @Internal
    public void setWWidthAfter(short field_12_wWidthAfter) {
        this.field_12_wWidthAfter = field_12_wWidthAfter;
    }

    @Internal
    public int getWidthAndFitsFlags() {
        return this.field_13_widthAndFitsFlags;
    }

    @Internal
    public void setWidthAndFitsFlags(int field_13_widthAndFitsFlags) {
        this.field_13_widthAndFitsFlags = field_13_widthAndFitsFlags;
    }

    @Internal
    public int getDxaAbs() {
        return this.field_14_dxaAbs;
    }

    @Internal
    public void setDxaAbs(int field_14_dxaAbs) {
        this.field_14_dxaAbs = field_14_dxaAbs;
    }

    @Internal
    public int getDyaAbs() {
        return this.field_15_dyaAbs;
    }

    @Internal
    public void setDyaAbs(int field_15_dyaAbs) {
        this.field_15_dyaAbs = field_15_dyaAbs;
    }

    @Internal
    public int getDxaFromText() {
        return this.field_16_dxaFromText;
    }

    @Internal
    public void setDxaFromText(int field_16_dxaFromText) {
        this.field_16_dxaFromText = field_16_dxaFromText;
    }

    @Internal
    public int getDyaFromText() {
        return this.field_17_dyaFromText;
    }

    @Internal
    public void setDyaFromText(int field_17_dyaFromText) {
        this.field_17_dyaFromText = field_17_dyaFromText;
    }

    @Internal
    public int getDxaFromTextRight() {
        return this.field_18_dxaFromTextRight;
    }

    @Internal
    public void setDxaFromTextRight(int field_18_dxaFromTextRight) {
        this.field_18_dxaFromTextRight = field_18_dxaFromTextRight;
    }

    @Internal
    public int getDyaFromTextBottom() {
        return this.field_19_dyaFromTextBottom;
    }

    @Internal
    public void setDyaFromTextBottom(int field_19_dyaFromTextBottom) {
        this.field_19_dyaFromTextBottom = field_19_dyaFromTextBottom;
    }

    @Internal
    public byte getFBiDi() {
        return this.field_20_fBiDi;
    }

    @Internal
    public void setFBiDi(byte field_20_fBiDi) {
        this.field_20_fBiDi = field_20_fBiDi;
    }

    @Internal
    public byte getFRTL() {
        return this.field_21_fRTL;
    }

    @Internal
    public void setFRTL(byte field_21_fRTL) {
        this.field_21_fRTL = field_21_fRTL;
    }

    @Internal
    public byte getFNoAllowOverlap() {
        return this.field_22_fNoAllowOverlap;
    }

    @Internal
    public void setFNoAllowOverlap(byte field_22_fNoAllowOverlap) {
        this.field_22_fNoAllowOverlap = field_22_fNoAllowOverlap;
    }

    @Internal
    public byte getFSpare() {
        return this.field_23_fSpare;
    }

    @Internal
    public void setFSpare(byte field_23_fSpare) {
        this.field_23_fSpare = field_23_fSpare;
    }

    @Internal
    public int getGrpfTap() {
        return this.field_24_grpfTap;
    }

    @Internal
    public void setGrpfTap(int field_24_grpfTap) {
        this.field_24_grpfTap = field_24_grpfTap;
    }

    @Internal
    public int getInternalFlags() {
        return this.field_25_internalFlags;
    }

    @Internal
    public void setInternalFlags(int field_25_internalFlags) {
        this.field_25_internalFlags = field_25_internalFlags;
    }

    @Internal
    public short getItcMac() {
        return this.field_26_itcMac;
    }

    @Internal
    public void setItcMac(short field_26_itcMac) {
        this.field_26_itcMac = field_26_itcMac;
    }

    @Internal
    public int getDxaAdjust() {
        return this.field_27_dxaAdjust;
    }

    @Internal
    public void setDxaAdjust(int field_27_dxaAdjust) {
        this.field_27_dxaAdjust = field_27_dxaAdjust;
    }

    @Internal
    public int getDxaWebView() {
        return this.field_28_dxaWebView;
    }

    @Internal
    public void setDxaWebView(int field_28_dxaWebView) {
        this.field_28_dxaWebView = field_28_dxaWebView;
    }

    @Internal
    public int getDxaRTEWrapWidth() {
        return this.field_29_dxaRTEWrapWidth;
    }

    @Internal
    public void setDxaRTEWrapWidth(int field_29_dxaRTEWrapWidth) {
        this.field_29_dxaRTEWrapWidth = field_29_dxaRTEWrapWidth;
    }

    @Internal
    public int getDxaColWidthWwd() {
        return this.field_30_dxaColWidthWwd;
    }

    @Internal
    public void setDxaColWidthWwd(int field_30_dxaColWidthWwd) {
        this.field_30_dxaColWidthWwd = field_30_dxaColWidthWwd;
    }

    @Internal
    public short getPctWwd() {
        return this.field_31_pctWwd;
    }

    @Internal
    public void setPctWwd(short field_31_pctWwd) {
        this.field_31_pctWwd = field_31_pctWwd;
    }

    @Internal
    public int getViewFlags() {
        return this.field_32_viewFlags;
    }

    @Internal
    public void setViewFlags(int field_32_viewFlags) {
        this.field_32_viewFlags = field_32_viewFlags;
    }

    @Internal
    public short[] getRgdxaCenter() {
        return this.field_33_rgdxaCenter;
    }

    @Internal
    public void setRgdxaCenter(short[] field_33_rgdxaCenter) {
        this.field_33_rgdxaCenter = field_33_rgdxaCenter;
    }

    @Internal
    public short[] getRgdxaCenterPrint() {
        return this.field_34_rgdxaCenterPrint;
    }

    @Internal
    public void setRgdxaCenterPrint(short[] field_34_rgdxaCenterPrint) {
        this.field_34_rgdxaCenterPrint = field_34_rgdxaCenterPrint;
    }

    @Internal
    public ShadingDescriptor getShdTable() {
        return this.field_35_shdTable;
    }

    @Internal
    public void setShdTable(ShadingDescriptor field_35_shdTable) {
        this.field_35_shdTable = field_35_shdTable;
    }

    @Internal
    public BorderCode getBrcBottom() {
        return this.field_36_brcBottom;
    }

    @Internal
    public void setBrcBottom(BorderCode field_36_brcBottom) {
        this.field_36_brcBottom = field_36_brcBottom;
    }

    @Internal
    public BorderCode getBrcTop() {
        return this.field_37_brcTop;
    }

    @Internal
    public void setBrcTop(BorderCode field_37_brcTop) {
        this.field_37_brcTop = field_37_brcTop;
    }

    @Internal
    public BorderCode getBrcLeft() {
        return this.field_38_brcLeft;
    }

    @Internal
    public void setBrcLeft(BorderCode field_38_brcLeft) {
        this.field_38_brcLeft = field_38_brcLeft;
    }

    @Internal
    public BorderCode getBrcRight() {
        return this.field_39_brcRight;
    }

    @Internal
    public void setBrcRight(BorderCode field_39_brcRight) {
        this.field_39_brcRight = field_39_brcRight;
    }

    @Internal
    public BorderCode getBrcVertical() {
        return this.field_40_brcVertical;
    }

    @Internal
    public void setBrcVertical(BorderCode field_40_brcVertical) {
        this.field_40_brcVertical = field_40_brcVertical;
    }

    @Internal
    public BorderCode getBrcHorizontal() {
        return this.field_41_brcHorizontal;
    }

    @Internal
    public void setBrcHorizontal(BorderCode field_41_brcHorizontal) {
        this.field_41_brcHorizontal = field_41_brcHorizontal;
    }

    @Internal
    public short getWCellPaddingDefaultTop() {
        return this.field_42_wCellPaddingDefaultTop;
    }

    @Internal
    public void setWCellPaddingDefaultTop(short field_42_wCellPaddingDefaultTop) {
        this.field_42_wCellPaddingDefaultTop = field_42_wCellPaddingDefaultTop;
    }

    @Internal
    public short getWCellPaddingDefaultLeft() {
        return this.field_43_wCellPaddingDefaultLeft;
    }

    @Internal
    public void setWCellPaddingDefaultLeft(short field_43_wCellPaddingDefaultLeft) {
        this.field_43_wCellPaddingDefaultLeft = field_43_wCellPaddingDefaultLeft;
    }

    @Internal
    public short getWCellPaddingDefaultBottom() {
        return this.field_44_wCellPaddingDefaultBottom;
    }

    @Internal
    public void setWCellPaddingDefaultBottom(short field_44_wCellPaddingDefaultBottom) {
        this.field_44_wCellPaddingDefaultBottom = field_44_wCellPaddingDefaultBottom;
    }

    @Internal
    public short getWCellPaddingDefaultRight() {
        return this.field_45_wCellPaddingDefaultRight;
    }

    @Internal
    public void setWCellPaddingDefaultRight(short field_45_wCellPaddingDefaultRight) {
        this.field_45_wCellPaddingDefaultRight = field_45_wCellPaddingDefaultRight;
    }

    @Internal
    public byte getFtsCellPaddingDefaultTop() {
        return this.field_46_ftsCellPaddingDefaultTop;
    }

    @Internal
    public void setFtsCellPaddingDefaultTop(byte field_46_ftsCellPaddingDefaultTop) {
        this.field_46_ftsCellPaddingDefaultTop = field_46_ftsCellPaddingDefaultTop;
    }

    @Internal
    public byte getFtsCellPaddingDefaultLeft() {
        return this.field_47_ftsCellPaddingDefaultLeft;
    }

    @Internal
    public void setFtsCellPaddingDefaultLeft(byte field_47_ftsCellPaddingDefaultLeft) {
        this.field_47_ftsCellPaddingDefaultLeft = field_47_ftsCellPaddingDefaultLeft;
    }

    @Internal
    public byte getFtsCellPaddingDefaultBottom() {
        return this.field_48_ftsCellPaddingDefaultBottom;
    }

    @Internal
    public void setFtsCellPaddingDefaultBottom(byte field_48_ftsCellPaddingDefaultBottom) {
        this.field_48_ftsCellPaddingDefaultBottom = field_48_ftsCellPaddingDefaultBottom;
    }

    @Internal
    public byte getFtsCellPaddingDefaultRight() {
        return this.field_49_ftsCellPaddingDefaultRight;
    }

    @Internal
    public void setFtsCellPaddingDefaultRight(byte field_49_ftsCellPaddingDefaultRight) {
        this.field_49_ftsCellPaddingDefaultRight = field_49_ftsCellPaddingDefaultRight;
    }

    @Internal
    public short getWCellSpacingDefaultTop() {
        return this.field_50_wCellSpacingDefaultTop;
    }

    @Internal
    public void setWCellSpacingDefaultTop(short field_50_wCellSpacingDefaultTop) {
        this.field_50_wCellSpacingDefaultTop = field_50_wCellSpacingDefaultTop;
    }

    @Internal
    public short getWCellSpacingDefaultLeft() {
        return this.field_51_wCellSpacingDefaultLeft;
    }

    @Internal
    public void setWCellSpacingDefaultLeft(short field_51_wCellSpacingDefaultLeft) {
        this.field_51_wCellSpacingDefaultLeft = field_51_wCellSpacingDefaultLeft;
    }

    @Internal
    public short getWCellSpacingDefaultBottom() {
        return this.field_52_wCellSpacingDefaultBottom;
    }

    @Internal
    public void setWCellSpacingDefaultBottom(short field_52_wCellSpacingDefaultBottom) {
        this.field_52_wCellSpacingDefaultBottom = field_52_wCellSpacingDefaultBottom;
    }

    @Internal
    public short getWCellSpacingDefaultRight() {
        return this.field_53_wCellSpacingDefaultRight;
    }

    @Internal
    public void setWCellSpacingDefaultRight(short field_53_wCellSpacingDefaultRight) {
        this.field_53_wCellSpacingDefaultRight = field_53_wCellSpacingDefaultRight;
    }

    @Internal
    public byte getFtsCellSpacingDefaultTop() {
        return this.field_54_ftsCellSpacingDefaultTop;
    }

    @Internal
    public void setFtsCellSpacingDefaultTop(byte field_54_ftsCellSpacingDefaultTop) {
        this.field_54_ftsCellSpacingDefaultTop = field_54_ftsCellSpacingDefaultTop;
    }

    @Internal
    public byte getFtsCellSpacingDefaultLeft() {
        return this.field_55_ftsCellSpacingDefaultLeft;
    }

    @Internal
    public void setFtsCellSpacingDefaultLeft(byte field_55_ftsCellSpacingDefaultLeft) {
        this.field_55_ftsCellSpacingDefaultLeft = field_55_ftsCellSpacingDefaultLeft;
    }

    @Internal
    public byte getFtsCellSpacingDefaultBottom() {
        return this.field_56_ftsCellSpacingDefaultBottom;
    }

    @Internal
    public void setFtsCellSpacingDefaultBottom(byte field_56_ftsCellSpacingDefaultBottom) {
        this.field_56_ftsCellSpacingDefaultBottom = field_56_ftsCellSpacingDefaultBottom;
    }

    @Internal
    public byte getFtsCellSpacingDefaultRight() {
        return this.field_57_ftsCellSpacingDefaultRight;
    }

    @Internal
    public void setFtsCellSpacingDefaultRight(byte field_57_ftsCellSpacingDefaultRight) {
        this.field_57_ftsCellSpacingDefaultRight = field_57_ftsCellSpacingDefaultRight;
    }

    @Internal
    public short getWCellPaddingOuterTop() {
        return this.field_58_wCellPaddingOuterTop;
    }

    @Internal
    public void setWCellPaddingOuterTop(short field_58_wCellPaddingOuterTop) {
        this.field_58_wCellPaddingOuterTop = field_58_wCellPaddingOuterTop;
    }

    @Internal
    public short getWCellPaddingOuterLeft() {
        return this.field_59_wCellPaddingOuterLeft;
    }

    @Internal
    public void setWCellPaddingOuterLeft(short field_59_wCellPaddingOuterLeft) {
        this.field_59_wCellPaddingOuterLeft = field_59_wCellPaddingOuterLeft;
    }

    @Internal
    public short getWCellPaddingOuterBottom() {
        return this.field_60_wCellPaddingOuterBottom;
    }

    @Internal
    public void setWCellPaddingOuterBottom(short field_60_wCellPaddingOuterBottom) {
        this.field_60_wCellPaddingOuterBottom = field_60_wCellPaddingOuterBottom;
    }

    @Internal
    public short getWCellPaddingOuterRight() {
        return this.field_61_wCellPaddingOuterRight;
    }

    @Internal
    public void setWCellPaddingOuterRight(short field_61_wCellPaddingOuterRight) {
        this.field_61_wCellPaddingOuterRight = field_61_wCellPaddingOuterRight;
    }

    @Internal
    public byte getFtsCellPaddingOuterTop() {
        return this.field_62_ftsCellPaddingOuterTop;
    }

    @Internal
    public void setFtsCellPaddingOuterTop(byte field_62_ftsCellPaddingOuterTop) {
        this.field_62_ftsCellPaddingOuterTop = field_62_ftsCellPaddingOuterTop;
    }

    @Internal
    public byte getFtsCellPaddingOuterLeft() {
        return this.field_63_ftsCellPaddingOuterLeft;
    }

    @Internal
    public void setFtsCellPaddingOuterLeft(byte field_63_ftsCellPaddingOuterLeft) {
        this.field_63_ftsCellPaddingOuterLeft = field_63_ftsCellPaddingOuterLeft;
    }

    @Internal
    public byte getFtsCellPaddingOuterBottom() {
        return this.field_64_ftsCellPaddingOuterBottom;
    }

    @Internal
    public void setFtsCellPaddingOuterBottom(byte field_64_ftsCellPaddingOuterBottom) {
        this.field_64_ftsCellPaddingOuterBottom = field_64_ftsCellPaddingOuterBottom;
    }

    @Internal
    public byte getFtsCellPaddingOuterRight() {
        return this.field_65_ftsCellPaddingOuterRight;
    }

    @Internal
    public void setFtsCellPaddingOuterRight(byte field_65_ftsCellPaddingOuterRight) {
        this.field_65_ftsCellPaddingOuterRight = field_65_ftsCellPaddingOuterRight;
    }

    @Internal
    public short getWCellSpacingOuterTop() {
        return this.field_66_wCellSpacingOuterTop;
    }

    @Internal
    public void setWCellSpacingOuterTop(short field_66_wCellSpacingOuterTop) {
        this.field_66_wCellSpacingOuterTop = field_66_wCellSpacingOuterTop;
    }

    @Internal
    public short getWCellSpacingOuterLeft() {
        return this.field_67_wCellSpacingOuterLeft;
    }

    @Internal
    public void setWCellSpacingOuterLeft(short field_67_wCellSpacingOuterLeft) {
        this.field_67_wCellSpacingOuterLeft = field_67_wCellSpacingOuterLeft;
    }

    @Internal
    public short getWCellSpacingOuterBottom() {
        return this.field_68_wCellSpacingOuterBottom;
    }

    @Internal
    public void setWCellSpacingOuterBottom(short field_68_wCellSpacingOuterBottom) {
        this.field_68_wCellSpacingOuterBottom = field_68_wCellSpacingOuterBottom;
    }

    @Internal
    public short getWCellSpacingOuterRight() {
        return this.field_69_wCellSpacingOuterRight;
    }

    @Internal
    public void setWCellSpacingOuterRight(short field_69_wCellSpacingOuterRight) {
        this.field_69_wCellSpacingOuterRight = field_69_wCellSpacingOuterRight;
    }

    @Internal
    public byte getFtsCellSpacingOuterTop() {
        return this.field_70_ftsCellSpacingOuterTop;
    }

    @Internal
    public void setFtsCellSpacingOuterTop(byte field_70_ftsCellSpacingOuterTop) {
        this.field_70_ftsCellSpacingOuterTop = field_70_ftsCellSpacingOuterTop;
    }

    @Internal
    public byte getFtsCellSpacingOuterLeft() {
        return this.field_71_ftsCellSpacingOuterLeft;
    }

    @Internal
    public void setFtsCellSpacingOuterLeft(byte field_71_ftsCellSpacingOuterLeft) {
        this.field_71_ftsCellSpacingOuterLeft = field_71_ftsCellSpacingOuterLeft;
    }

    @Internal
    public byte getFtsCellSpacingOuterBottom() {
        return this.field_72_ftsCellSpacingOuterBottom;
    }

    @Internal
    public void setFtsCellSpacingOuterBottom(byte field_72_ftsCellSpacingOuterBottom) {
        this.field_72_ftsCellSpacingOuterBottom = field_72_ftsCellSpacingOuterBottom;
    }

    @Internal
    public byte getFtsCellSpacingOuterRight() {
        return this.field_73_ftsCellSpacingOuterRight;
    }

    @Internal
    public void setFtsCellSpacingOuterRight(byte field_73_ftsCellSpacingOuterRight) {
        this.field_73_ftsCellSpacingOuterRight = field_73_ftsCellSpacingOuterRight;
    }

    @Internal
    public TableCellDescriptor[] getRgtc() {
        return this.field_74_rgtc;
    }

    @Internal
    public void setRgtc(TableCellDescriptor[] field_74_rgtc) {
        this.field_74_rgtc = field_74_rgtc;
    }

    @Internal
    public ShadingDescriptor[] getRgshd() {
        return this.field_75_rgshd;
    }

    @Internal
    public void setRgshd(ShadingDescriptor[] field_75_rgshd) {
        this.field_75_rgshd = field_75_rgshd;
    }

    @Internal
    public byte getFPropRMark() {
        return this.field_76_fPropRMark;
    }

    @Internal
    public void setFPropRMark(byte field_76_fPropRMark) {
        this.field_76_fPropRMark = field_76_fPropRMark;
    }

    @Internal
    public byte getFHasOldProps() {
        return this.field_77_fHasOldProps;
    }

    @Internal
    public void setFHasOldProps(byte field_77_fHasOldProps) {
        this.field_77_fHasOldProps = field_77_fHasOldProps;
    }

    @Internal
    public short getCHorzBands() {
        return this.field_78_cHorzBands;
    }

    @Internal
    public void setCHorzBands(short field_78_cHorzBands) {
        this.field_78_cHorzBands = field_78_cHorzBands;
    }

    @Internal
    public short getCVertBands() {
        return this.field_79_cVertBands;
    }

    @Internal
    public void setCVertBands(short field_79_cVertBands) {
        this.field_79_cVertBands = field_79_cVertBands;
    }

    @Internal
    public BorderCode getRgbrcInsideDefault_0() {
        return this.field_80_rgbrcInsideDefault_0;
    }

    @Internal
    public void setRgbrcInsideDefault_0(BorderCode field_80_rgbrcInsideDefault_0) {
        this.field_80_rgbrcInsideDefault_0 = field_80_rgbrcInsideDefault_0;
    }

    @Internal
    public BorderCode getRgbrcInsideDefault_1() {
        return this.field_81_rgbrcInsideDefault_1;
    }

    @Internal
    public void setRgbrcInsideDefault_1(BorderCode field_81_rgbrcInsideDefault_1) {
        this.field_81_rgbrcInsideDefault_1 = field_81_rgbrcInsideDefault_1;
    }

    @Internal
    public void setFAutofit(boolean value) {
        this.field_13_widthAndFitsFlags = fAutofit.setBoolean(this.field_13_widthAndFitsFlags, value);
    }

    @Internal
    public boolean isFAutofit() {
        return fAutofit.isSet(this.field_13_widthAndFitsFlags);
    }

    @Internal
    public void setFKeepFollow(boolean value) {
        this.field_13_widthAndFitsFlags = fKeepFollow.setBoolean(this.field_13_widthAndFitsFlags, value);
    }

    @Internal
    public boolean isFKeepFollow() {
        return fKeepFollow.isSet(this.field_13_widthAndFitsFlags);
    }

    @Internal
    public void setFtsWidth(byte value) {
        this.field_13_widthAndFitsFlags = ftsWidth.setValue(this.field_13_widthAndFitsFlags, value);
    }

    @Internal
    public byte getFtsWidth() {
        return (byte)ftsWidth.getValue(this.field_13_widthAndFitsFlags);
    }

    @Internal
    public void setFtsWidthIndent(byte value) {
        this.field_13_widthAndFitsFlags = ftsWidthIndent.setValue(this.field_13_widthAndFitsFlags, value);
    }

    @Internal
    public byte getFtsWidthIndent() {
        return (byte)ftsWidthIndent.getValue(this.field_13_widthAndFitsFlags);
    }

    @Internal
    public void setFtsWidthBefore(byte value) {
        this.field_13_widthAndFitsFlags = ftsWidthBefore.setValue(this.field_13_widthAndFitsFlags, value);
    }

    @Internal
    public byte getFtsWidthBefore() {
        return (byte)ftsWidthBefore.getValue(this.field_13_widthAndFitsFlags);
    }

    @Internal
    public void setFtsWidthAfter(byte value) {
        this.field_13_widthAndFitsFlags = ftsWidthAfter.setValue(this.field_13_widthAndFitsFlags, value);
    }

    @Internal
    public byte getFtsWidthAfter() {
        return (byte)ftsWidthAfter.getValue(this.field_13_widthAndFitsFlags);
    }

    @Internal
    public void setFNeverBeenAutofit(boolean value) {
        this.field_13_widthAndFitsFlags = fNeverBeenAutofit.setBoolean(this.field_13_widthAndFitsFlags, value);
    }

    @Internal
    public boolean isFNeverBeenAutofit() {
        return fNeverBeenAutofit.isSet(this.field_13_widthAndFitsFlags);
    }

    @Internal
    public void setFInvalAutofit(boolean value) {
        this.field_13_widthAndFitsFlags = fInvalAutofit.setBoolean(this.field_13_widthAndFitsFlags, value);
    }

    @Internal
    public boolean isFInvalAutofit() {
        return fInvalAutofit.isSet(this.field_13_widthAndFitsFlags);
    }

    @Internal
    public void setWidthAndFitsFlags_empty1(byte value) {
        this.field_13_widthAndFitsFlags = widthAndFitsFlags_empty1.setValue(this.field_13_widthAndFitsFlags, value);
    }

    @Internal
    public byte getWidthAndFitsFlags_empty1() {
        return (byte)widthAndFitsFlags_empty1.getValue(this.field_13_widthAndFitsFlags);
    }

    @Internal
    public void setFVert(boolean value) {
        this.field_13_widthAndFitsFlags = fVert.setBoolean(this.field_13_widthAndFitsFlags, value);
    }

    @Internal
    public boolean isFVert() {
        return fVert.isSet(this.field_13_widthAndFitsFlags);
    }

    @Internal
    public void setPcVert(byte value) {
        this.field_13_widthAndFitsFlags = pcVert.setValue(this.field_13_widthAndFitsFlags, value);
    }

    @Internal
    public byte getPcVert() {
        return (byte)pcVert.getValue(this.field_13_widthAndFitsFlags);
    }

    @Internal
    public void setPcHorz(byte value) {
        this.field_13_widthAndFitsFlags = pcHorz.setValue(this.field_13_widthAndFitsFlags, value);
    }

    @Internal
    public byte getPcHorz() {
        return (byte)pcHorz.getValue(this.field_13_widthAndFitsFlags);
    }

    @Internal
    public void setWidthAndFitsFlags_empty2(short value) {
        this.field_13_widthAndFitsFlags = widthAndFitsFlags_empty2.setValue(this.field_13_widthAndFitsFlags, value);
    }

    @Internal
    public short getWidthAndFitsFlags_empty2() {
        return (short)widthAndFitsFlags_empty2.getValue(this.field_13_widthAndFitsFlags);
    }

    @Internal
    public void setFFirstRow(boolean value) {
        this.field_25_internalFlags = fFirstRow.setBoolean(this.field_25_internalFlags, value);
    }

    @Internal
    public boolean isFFirstRow() {
        return fFirstRow.isSet(this.field_25_internalFlags);
    }

    @Internal
    public void setFLastRow(boolean value) {
        this.field_25_internalFlags = fLastRow.setBoolean(this.field_25_internalFlags, value);
    }

    @Internal
    public boolean isFLastRow() {
        return fLastRow.isSet(this.field_25_internalFlags);
    }

    @Internal
    public void setFOutline(boolean value) {
        this.field_25_internalFlags = fOutline.setBoolean(this.field_25_internalFlags, value);
    }

    @Internal
    public boolean isFOutline() {
        return fOutline.isSet(this.field_25_internalFlags);
    }

    @Internal
    public void setFOrigWordTableRules(boolean value) {
        this.field_25_internalFlags = fOrigWordTableRules.setBoolean(this.field_25_internalFlags, value);
    }

    @Internal
    public boolean isFOrigWordTableRules() {
        return fOrigWordTableRules.isSet(this.field_25_internalFlags);
    }

    @Internal
    public void setFCellSpacing(boolean value) {
        this.field_25_internalFlags = fCellSpacing.setBoolean(this.field_25_internalFlags, value);
    }

    @Internal
    public boolean isFCellSpacing() {
        return fCellSpacing.isSet(this.field_25_internalFlags);
    }

    @Internal
    public void setGrpfTap_unused(short value) {
        this.field_25_internalFlags = grpfTap_unused.setValue(this.field_25_internalFlags, value);
    }

    @Internal
    public short getGrpfTap_unused() {
        return (short)grpfTap_unused.getValue(this.field_25_internalFlags);
    }

    @Internal
    public void setFWrapToWwd(boolean value) {
        this.field_32_viewFlags = fWrapToWwd.setBoolean(this.field_32_viewFlags, value);
    }

    @Internal
    public boolean isFWrapToWwd() {
        return fWrapToWwd.isSet(this.field_32_viewFlags);
    }

    @Internal
    public void setFNotPageView(boolean value) {
        this.field_32_viewFlags = fNotPageView.setBoolean(this.field_32_viewFlags, value);
    }

    @Internal
    public boolean isFNotPageView() {
        return fNotPageView.isSet(this.field_32_viewFlags);
    }

    @Internal
    public void setViewFlags_unused1(boolean value) {
        this.field_32_viewFlags = viewFlags_unused1.setBoolean(this.field_32_viewFlags, value);
    }

    @Internal
    public boolean isViewFlags_unused1() {
        return viewFlags_unused1.isSet(this.field_32_viewFlags);
    }

    @Internal
    public void setFWebView(boolean value) {
        this.field_32_viewFlags = fWebView.setBoolean(this.field_32_viewFlags, value);
    }

    @Internal
    public boolean isFWebView() {
        return fWebView.isSet(this.field_32_viewFlags);
    }

    @Internal
    public void setFAdjusted(boolean value) {
        this.field_32_viewFlags = fAdjusted.setBoolean(this.field_32_viewFlags, value);
    }

    @Internal
    public boolean isFAdjusted() {
        return fAdjusted.isSet(this.field_32_viewFlags);
    }

    @Internal
    public void setViewFlags_unused2(short value) {
        this.field_32_viewFlags = viewFlags_unused2.setValue(this.field_32_viewFlags, value);
    }

    @Internal
    public short getViewFlags_unused2() {
        return (short)viewFlags_unused2.getValue(this.field_32_viewFlags);
    }
}


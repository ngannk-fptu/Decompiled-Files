/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model.types;

import java.util.Arrays;
import org.apache.poi.util.BitField;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;

@Internal
public abstract class DOPAbstractType {
    protected byte field_1_formatFlags;
    private static final BitField fFacingPages = new BitField(1);
    private static final BitField fWidowControl = new BitField(2);
    private static final BitField fPMHMainDoc = new BitField(4);
    private static final BitField grfSupression = new BitField(24);
    private static final BitField fpc = new BitField(96);
    private static final BitField unused1 = new BitField(128);
    protected byte field_2_unused2;
    protected short field_3_footnoteInfo;
    private static final BitField rncFtn = new BitField(3);
    private static final BitField nFtn = new BitField(65532);
    protected byte field_4_fOutlineDirtySave;
    protected byte field_5_docinfo;
    private static final BitField fOnlyMacPics = new BitField(1);
    private static final BitField fOnlyWinPics = new BitField(2);
    private static final BitField fLabelDoc = new BitField(4);
    private static final BitField fHyphCapitals = new BitField(8);
    private static final BitField fAutoHyphen = new BitField(16);
    private static final BitField fFormNoFields = new BitField(32);
    private static final BitField fLinkStyles = new BitField(64);
    private static final BitField fRevMarking = new BitField(128);
    protected byte field_6_docinfo1;
    private static final BitField fBackup = new BitField(1);
    private static final BitField fExactCWords = new BitField(2);
    private static final BitField fPagHidden = new BitField(4);
    private static final BitField fPagResults = new BitField(8);
    private static final BitField fLockAtn = new BitField(16);
    private static final BitField fMirrorMargins = new BitField(32);
    private static final BitField unused3 = new BitField(64);
    private static final BitField fDfltTrueType = new BitField(128);
    protected byte field_7_docinfo2;
    private static final BitField fPagSupressTopSpacing = new BitField(1);
    private static final BitField fProtEnabled = new BitField(2);
    private static final BitField fDispFormFldSel = new BitField(4);
    private static final BitField fRMView = new BitField(8);
    private static final BitField fRMPrint = new BitField(16);
    private static final BitField unused4 = new BitField(32);
    private static final BitField fLockRev = new BitField(64);
    private static final BitField fEmbedFonts = new BitField(128);
    protected short field_8_docinfo3;
    private static final BitField oldfNoTabForInd = new BitField(1);
    private static final BitField oldfNoSpaceRaiseLower = new BitField(2);
    private static final BitField oldfSuppressSpbfAfterPageBreak = new BitField(4);
    private static final BitField oldfWrapTrailSpaces = new BitField(8);
    private static final BitField oldfMapPrintTextColor = new BitField(16);
    private static final BitField oldfNoColumnBalance = new BitField(32);
    private static final BitField oldfConvMailMergeEsc = new BitField(64);
    private static final BitField oldfSupressTopSpacing = new BitField(128);
    private static final BitField oldfOrigWordTableRules = new BitField(256);
    private static final BitField oldfTransparentMetafiles = new BitField(512);
    private static final BitField oldfShowBreaksInFrames = new BitField(1024);
    private static final BitField oldfSwapBordersFacingPgs = new BitField(2048);
    private static final BitField unused5 = new BitField(61440);
    protected int field_9_dxaTab;
    protected int field_10_wSpare;
    protected int field_11_dxaHotz;
    protected int field_12_cConsexHypLim;
    protected int field_13_wSpare2;
    protected int field_14_dttmCreated;
    protected int field_15_dttmRevised;
    protected int field_16_dttmLastPrint;
    protected int field_17_nRevision;
    protected int field_18_tmEdited;
    protected int field_19_cWords;
    protected int field_20_cCh;
    protected int field_21_cPg;
    protected int field_22_cParas;
    protected short field_23_Edn;
    private static final BitField rncEdn = new BitField(3);
    private static final BitField nEdn = new BitField(65532);
    protected short field_24_Edn1;
    private static final BitField epc = new BitField(3);
    private static final BitField nfcFtnRef1 = new BitField(60);
    private static final BitField nfcEdnRef1 = new BitField(960);
    private static final BitField fPrintFormData = new BitField(1024);
    private static final BitField fSaveFormData = new BitField(2048);
    private static final BitField fShadeFormData = new BitField(4096);
    private static final BitField fWCFtnEdn = new BitField(32768);
    protected int field_25_cLines;
    protected int field_26_cWordsFtnEnd;
    protected int field_27_cChFtnEdn;
    protected short field_28_cPgFtnEdn;
    protected int field_29_cParasFtnEdn;
    protected int field_30_cLinesFtnEdn;
    protected int field_31_lKeyProtDoc;
    protected short field_32_view;
    private static final BitField wvkSaved = new BitField(7);
    private static final BitField wScaleSaved = new BitField(4088);
    private static final BitField zkSaved = new BitField(12288);
    private static final BitField fRotateFontW6 = new BitField(16384);
    private static final BitField iGutterPos = new BitField(32768);
    protected int field_33_docinfo4;
    private static final BitField fNoTabForInd = new BitField(1);
    private static final BitField fNoSpaceRaiseLower = new BitField(2);
    private static final BitField fSupressSpdfAfterPageBreak = new BitField(4);
    private static final BitField fWrapTrailSpaces = new BitField(8);
    private static final BitField fMapPrintTextColor = new BitField(16);
    private static final BitField fNoColumnBalance = new BitField(32);
    private static final BitField fConvMailMergeEsc = new BitField(64);
    private static final BitField fSupressTopSpacing = new BitField(128);
    private static final BitField fOrigWordTableRules = new BitField(256);
    private static final BitField fTransparentMetafiles = new BitField(512);
    private static final BitField fShowBreaksInFrames = new BitField(1024);
    private static final BitField fSwapBordersFacingPgs = new BitField(2048);
    private static final BitField fSuppressTopSPacingMac5 = new BitField(65536);
    private static final BitField fTruncDxaExpand = new BitField(131072);
    private static final BitField fPrintBodyBeforeHdr = new BitField(262144);
    private static final BitField fNoLeading = new BitField(524288);
    private static final BitField fMWSmallCaps = new BitField(0x200000);
    protected short field_34_adt;
    protected byte[] field_35_doptypography = new byte[0];
    protected byte[] field_36_dogrid = new byte[0];
    protected short field_37_docinfo5;
    private static final BitField lvl = new BitField(30);
    private static final BitField fGramAllDone = new BitField(32);
    private static final BitField fGramAllClean = new BitField(64);
    private static final BitField fSubsetFonts = new BitField(128);
    private static final BitField fHideLastVersion = new BitField(256);
    private static final BitField fHtmlDoc = new BitField(512);
    private static final BitField fSnapBorder = new BitField(2048);
    private static final BitField fIncludeHeader = new BitField(4096);
    private static final BitField fIncludeFooter = new BitField(8192);
    private static final BitField fForcePageSizePag = new BitField(16384);
    private static final BitField fMinFontSizePag = new BitField(32768);
    protected short field_38_docinfo6;
    private static final BitField fHaveVersions = new BitField(1);
    private static final BitField fAutoVersions = new BitField(2);
    protected byte[] field_39_asumyi = new byte[0];
    protected int field_40_cChWS;
    protected int field_41_cChWSFtnEdn;
    protected int field_42_grfDocEvents;
    protected int field_43_virusinfo;
    private static final BitField fVirusPrompted = new BitField(1);
    private static final BitField fVirusLoadSafe = new BitField(2);
    private static final BitField KeyVirusSession30 = new BitField(-4);
    protected byte[] field_44_Spare = new byte[0];
    protected int field_45_reserved1;
    protected int field_46_reserved2;
    protected int field_47_cDBC;
    protected int field_48_cDBCFtnEdn;
    protected int field_49_reserved;
    protected short field_50_nfcFtnRef;
    protected short field_51_nfcEdnRef;
    protected short field_52_hpsZoonFontPag;
    protected short field_53_dywDispPag;

    protected DOPAbstractType() {
    }

    protected void fillFields(byte[] data, int offset) {
        this.field_1_formatFlags = data[0 + offset];
        this.field_2_unused2 = data[1 + offset];
        this.field_3_footnoteInfo = LittleEndian.getShort(data, 2 + offset);
        this.field_4_fOutlineDirtySave = data[4 + offset];
        this.field_5_docinfo = data[5 + offset];
        this.field_6_docinfo1 = data[6 + offset];
        this.field_7_docinfo2 = data[7 + offset];
        this.field_8_docinfo3 = LittleEndian.getShort(data, 8 + offset);
        this.field_9_dxaTab = LittleEndian.getShort(data, 10 + offset);
        this.field_10_wSpare = LittleEndian.getShort(data, 12 + offset);
        this.field_11_dxaHotz = LittleEndian.getShort(data, 14 + offset);
        this.field_12_cConsexHypLim = LittleEndian.getShort(data, 16 + offset);
        this.field_13_wSpare2 = LittleEndian.getShort(data, 18 + offset);
        this.field_14_dttmCreated = LittleEndian.getInt(data, 20 + offset);
        this.field_15_dttmRevised = LittleEndian.getInt(data, 24 + offset);
        this.field_16_dttmLastPrint = LittleEndian.getInt(data, 28 + offset);
        this.field_17_nRevision = LittleEndian.getShort(data, 32 + offset);
        this.field_18_tmEdited = LittleEndian.getInt(data, 34 + offset);
        this.field_19_cWords = LittleEndian.getInt(data, 38 + offset);
        this.field_20_cCh = LittleEndian.getInt(data, 42 + offset);
        this.field_21_cPg = LittleEndian.getShort(data, 46 + offset);
        this.field_22_cParas = LittleEndian.getInt(data, 48 + offset);
        this.field_23_Edn = LittleEndian.getShort(data, 52 + offset);
        this.field_24_Edn1 = LittleEndian.getShort(data, 54 + offset);
        this.field_25_cLines = LittleEndian.getInt(data, 56 + offset);
        this.field_26_cWordsFtnEnd = LittleEndian.getInt(data, 60 + offset);
        this.field_27_cChFtnEdn = LittleEndian.getInt(data, 64 + offset);
        this.field_28_cPgFtnEdn = LittleEndian.getShort(data, 68 + offset);
        this.field_29_cParasFtnEdn = LittleEndian.getInt(data, 70 + offset);
        this.field_30_cLinesFtnEdn = LittleEndian.getInt(data, 74 + offset);
        this.field_31_lKeyProtDoc = LittleEndian.getInt(data, 78 + offset);
        this.field_32_view = LittleEndian.getShort(data, 82 + offset);
        this.field_33_docinfo4 = LittleEndian.getInt(data, 84 + offset);
        this.field_34_adt = LittleEndian.getShort(data, 88 + offset);
        this.field_35_doptypography = Arrays.copyOfRange(data, 90 + offset, 90 + offset + 310);
        this.field_36_dogrid = Arrays.copyOfRange(data, 400 + offset, 400 + offset + 10);
        this.field_37_docinfo5 = LittleEndian.getShort(data, 410 + offset);
        this.field_38_docinfo6 = LittleEndian.getShort(data, 412 + offset);
        this.field_39_asumyi = Arrays.copyOfRange(data, 414 + offset, 414 + offset + 12);
        this.field_40_cChWS = LittleEndian.getInt(data, 426 + offset);
        this.field_41_cChWSFtnEdn = LittleEndian.getInt(data, 430 + offset);
        this.field_42_grfDocEvents = LittleEndian.getInt(data, 434 + offset);
        this.field_43_virusinfo = LittleEndian.getInt(data, 438 + offset);
        this.field_44_Spare = Arrays.copyOfRange(data, 442 + offset, 442 + offset + 30);
        this.field_45_reserved1 = LittleEndian.getInt(data, 472 + offset);
        this.field_46_reserved2 = LittleEndian.getInt(data, 476 + offset);
        this.field_47_cDBC = LittleEndian.getInt(data, 480 + offset);
        this.field_48_cDBCFtnEdn = LittleEndian.getInt(data, 484 + offset);
        this.field_49_reserved = LittleEndian.getInt(data, 488 + offset);
        this.field_50_nfcFtnRef = LittleEndian.getShort(data, 492 + offset);
        this.field_51_nfcEdnRef = LittleEndian.getShort(data, 494 + offset);
        this.field_52_hpsZoonFontPag = LittleEndian.getShort(data, 496 + offset);
        this.field_53_dywDispPag = LittleEndian.getShort(data, 498 + offset);
    }

    public void serialize(byte[] data, int offset) {
        data[0 + offset] = this.field_1_formatFlags;
        data[1 + offset] = this.field_2_unused2;
        LittleEndian.putShort(data, 2 + offset, this.field_3_footnoteInfo);
        data[4 + offset] = this.field_4_fOutlineDirtySave;
        data[5 + offset] = this.field_5_docinfo;
        data[6 + offset] = this.field_6_docinfo1;
        data[7 + offset] = this.field_7_docinfo2;
        LittleEndian.putShort(data, 8 + offset, this.field_8_docinfo3);
        LittleEndian.putShort(data, 10 + offset, (short)this.field_9_dxaTab);
        LittleEndian.putShort(data, 12 + offset, (short)this.field_10_wSpare);
        LittleEndian.putShort(data, 14 + offset, (short)this.field_11_dxaHotz);
        LittleEndian.putShort(data, 16 + offset, (short)this.field_12_cConsexHypLim);
        LittleEndian.putShort(data, 18 + offset, (short)this.field_13_wSpare2);
        LittleEndian.putInt(data, 20 + offset, this.field_14_dttmCreated);
        LittleEndian.putInt(data, 24 + offset, this.field_15_dttmRevised);
        LittleEndian.putInt(data, 28 + offset, this.field_16_dttmLastPrint);
        LittleEndian.putShort(data, 32 + offset, (short)this.field_17_nRevision);
        LittleEndian.putInt(data, 34 + offset, this.field_18_tmEdited);
        LittleEndian.putInt(data, 38 + offset, this.field_19_cWords);
        LittleEndian.putInt(data, 42 + offset, this.field_20_cCh);
        LittleEndian.putShort(data, 46 + offset, (short)this.field_21_cPg);
        LittleEndian.putInt(data, 48 + offset, this.field_22_cParas);
        LittleEndian.putShort(data, 52 + offset, this.field_23_Edn);
        LittleEndian.putShort(data, 54 + offset, this.field_24_Edn1);
        LittleEndian.putInt(data, 56 + offset, this.field_25_cLines);
        LittleEndian.putInt(data, 60 + offset, this.field_26_cWordsFtnEnd);
        LittleEndian.putInt(data, 64 + offset, this.field_27_cChFtnEdn);
        LittleEndian.putShort(data, 68 + offset, this.field_28_cPgFtnEdn);
        LittleEndian.putInt(data, 70 + offset, this.field_29_cParasFtnEdn);
        LittleEndian.putInt(data, 74 + offset, this.field_30_cLinesFtnEdn);
        LittleEndian.putInt(data, 78 + offset, this.field_31_lKeyProtDoc);
        LittleEndian.putShort(data, 82 + offset, this.field_32_view);
        LittleEndian.putInt(data, 84 + offset, this.field_33_docinfo4);
        LittleEndian.putShort(data, 88 + offset, this.field_34_adt);
        System.arraycopy(this.field_35_doptypography, 0, data, 90 + offset, this.field_35_doptypography.length);
        System.arraycopy(this.field_36_dogrid, 0, data, 400 + offset, this.field_36_dogrid.length);
        LittleEndian.putShort(data, 410 + offset, this.field_37_docinfo5);
        LittleEndian.putShort(data, 412 + offset, this.field_38_docinfo6);
        System.arraycopy(this.field_39_asumyi, 0, data, 414 + offset, this.field_39_asumyi.length);
        LittleEndian.putInt(data, 426 + offset, this.field_40_cChWS);
        LittleEndian.putInt(data, 430 + offset, this.field_41_cChWSFtnEdn);
        LittleEndian.putInt(data, 434 + offset, this.field_42_grfDocEvents);
        LittleEndian.putInt(data, 438 + offset, this.field_43_virusinfo);
        System.arraycopy(this.field_44_Spare, 0, data, 442 + offset, this.field_44_Spare.length);
        LittleEndian.putInt(data, 472 + offset, this.field_45_reserved1);
        LittleEndian.putInt(data, 476 + offset, this.field_46_reserved2);
        LittleEndian.putInt(data, 480 + offset, this.field_47_cDBC);
        LittleEndian.putInt(data, 484 + offset, this.field_48_cDBCFtnEdn);
        LittleEndian.putInt(data, 488 + offset, this.field_49_reserved);
        LittleEndian.putShort(data, 492 + offset, this.field_50_nfcFtnRef);
        LittleEndian.putShort(data, 494 + offset, this.field_51_nfcEdnRef);
        LittleEndian.putShort(data, 496 + offset, this.field_52_hpsZoonFontPag);
        LittleEndian.putShort(data, 498 + offset, this.field_53_dywDispPag);
    }

    public static int getSize() {
        return 500;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[DOP]\n");
        builder.append("    .formatFlags          = ");
        builder.append(" (").append(this.getFormatFlags()).append(" )\n");
        builder.append("         .fFacingPages             = ").append(this.isFFacingPages()).append('\n');
        builder.append("         .fWidowControl            = ").append(this.isFWidowControl()).append('\n');
        builder.append("         .fPMHMainDoc              = ").append(this.isFPMHMainDoc()).append('\n');
        builder.append("         .grfSupression            = ").append(this.getGrfSupression()).append('\n');
        builder.append("         .fpc                      = ").append(this.getFpc()).append('\n');
        builder.append("         .unused1                  = ").append(this.isUnused1()).append('\n');
        builder.append("    .unused2              = ");
        builder.append(" (").append(this.getUnused2()).append(" )\n");
        builder.append("    .footnoteInfo         = ");
        builder.append(" (").append(this.getFootnoteInfo()).append(" )\n");
        builder.append("         .rncFtn                   = ").append(this.getRncFtn()).append('\n');
        builder.append("         .nFtn                     = ").append(this.getNFtn()).append('\n');
        builder.append("    .fOutlineDirtySave    = ");
        builder.append(" (").append(this.getFOutlineDirtySave()).append(" )\n");
        builder.append("    .docinfo              = ");
        builder.append(" (").append(this.getDocinfo()).append(" )\n");
        builder.append("         .fOnlyMacPics             = ").append(this.isFOnlyMacPics()).append('\n');
        builder.append("         .fOnlyWinPics             = ").append(this.isFOnlyWinPics()).append('\n');
        builder.append("         .fLabelDoc                = ").append(this.isFLabelDoc()).append('\n');
        builder.append("         .fHyphCapitals            = ").append(this.isFHyphCapitals()).append('\n');
        builder.append("         .fAutoHyphen              = ").append(this.isFAutoHyphen()).append('\n');
        builder.append("         .fFormNoFields            = ").append(this.isFFormNoFields()).append('\n');
        builder.append("         .fLinkStyles              = ").append(this.isFLinkStyles()).append('\n');
        builder.append("         .fRevMarking              = ").append(this.isFRevMarking()).append('\n');
        builder.append("    .docinfo1             = ");
        builder.append(" (").append(this.getDocinfo1()).append(" )\n");
        builder.append("         .fBackup                  = ").append(this.isFBackup()).append('\n');
        builder.append("         .fExactCWords             = ").append(this.isFExactCWords()).append('\n');
        builder.append("         .fPagHidden               = ").append(this.isFPagHidden()).append('\n');
        builder.append("         .fPagResults              = ").append(this.isFPagResults()).append('\n');
        builder.append("         .fLockAtn                 = ").append(this.isFLockAtn()).append('\n');
        builder.append("         .fMirrorMargins           = ").append(this.isFMirrorMargins()).append('\n');
        builder.append("         .unused3                  = ").append(this.isUnused3()).append('\n');
        builder.append("         .fDfltTrueType            = ").append(this.isFDfltTrueType()).append('\n');
        builder.append("    .docinfo2             = ");
        builder.append(" (").append(this.getDocinfo2()).append(" )\n");
        builder.append("         .fPagSupressTopSpacing     = ").append(this.isFPagSupressTopSpacing()).append('\n');
        builder.append("         .fProtEnabled             = ").append(this.isFProtEnabled()).append('\n');
        builder.append("         .fDispFormFldSel          = ").append(this.isFDispFormFldSel()).append('\n');
        builder.append("         .fRMView                  = ").append(this.isFRMView()).append('\n');
        builder.append("         .fRMPrint                 = ").append(this.isFRMPrint()).append('\n');
        builder.append("         .unused4                  = ").append(this.isUnused4()).append('\n');
        builder.append("         .fLockRev                 = ").append(this.isFLockRev()).append('\n');
        builder.append("         .fEmbedFonts              = ").append(this.isFEmbedFonts()).append('\n');
        builder.append("    .docinfo3             = ");
        builder.append(" (").append(this.getDocinfo3()).append(" )\n");
        builder.append("         .oldfNoTabForInd          = ").append(this.isOldfNoTabForInd()).append('\n');
        builder.append("         .oldfNoSpaceRaiseLower     = ").append(this.isOldfNoSpaceRaiseLower()).append('\n');
        builder.append("         .oldfSuppressSpbfAfterPageBreak     = ").append(this.isOldfSuppressSpbfAfterPageBreak()).append('\n');
        builder.append("         .oldfWrapTrailSpaces      = ").append(this.isOldfWrapTrailSpaces()).append('\n');
        builder.append("         .oldfMapPrintTextColor     = ").append(this.isOldfMapPrintTextColor()).append('\n');
        builder.append("         .oldfNoColumnBalance      = ").append(this.isOldfNoColumnBalance()).append('\n');
        builder.append("         .oldfConvMailMergeEsc     = ").append(this.isOldfConvMailMergeEsc()).append('\n');
        builder.append("         .oldfSupressTopSpacing     = ").append(this.isOldfSupressTopSpacing()).append('\n');
        builder.append("         .oldfOrigWordTableRules     = ").append(this.isOldfOrigWordTableRules()).append('\n');
        builder.append("         .oldfTransparentMetafiles     = ").append(this.isOldfTransparentMetafiles()).append('\n');
        builder.append("         .oldfShowBreaksInFrames     = ").append(this.isOldfShowBreaksInFrames()).append('\n');
        builder.append("         .oldfSwapBordersFacingPgs     = ").append(this.isOldfSwapBordersFacingPgs()).append('\n');
        builder.append("         .unused5                  = ").append(this.getUnused5()).append('\n');
        builder.append("    .dxaTab               = ");
        builder.append(" (").append(this.getDxaTab()).append(" )\n");
        builder.append("    .wSpare               = ");
        builder.append(" (").append(this.getWSpare()).append(" )\n");
        builder.append("    .dxaHotz              = ");
        builder.append(" (").append(this.getDxaHotz()).append(" )\n");
        builder.append("    .cConsexHypLim        = ");
        builder.append(" (").append(this.getCConsexHypLim()).append(" )\n");
        builder.append("    .wSpare2              = ");
        builder.append(" (").append(this.getWSpare2()).append(" )\n");
        builder.append("    .dttmCreated          = ");
        builder.append(" (").append(this.getDttmCreated()).append(" )\n");
        builder.append("    .dttmRevised          = ");
        builder.append(" (").append(this.getDttmRevised()).append(" )\n");
        builder.append("    .dttmLastPrint        = ");
        builder.append(" (").append(this.getDttmLastPrint()).append(" )\n");
        builder.append("    .nRevision            = ");
        builder.append(" (").append(this.getNRevision()).append(" )\n");
        builder.append("    .tmEdited             = ");
        builder.append(" (").append(this.getTmEdited()).append(" )\n");
        builder.append("    .cWords               = ");
        builder.append(" (").append(this.getCWords()).append(" )\n");
        builder.append("    .cCh                  = ");
        builder.append(" (").append(this.getCCh()).append(" )\n");
        builder.append("    .cPg                  = ");
        builder.append(" (").append(this.getCPg()).append(" )\n");
        builder.append("    .cParas               = ");
        builder.append(" (").append(this.getCParas()).append(" )\n");
        builder.append("    .Edn                  = ");
        builder.append(" (").append(this.getEdn()).append(" )\n");
        builder.append("         .rncEdn                   = ").append(this.getRncEdn()).append('\n');
        builder.append("         .nEdn                     = ").append(this.getNEdn()).append('\n');
        builder.append("    .Edn1                 = ");
        builder.append(" (").append(this.getEdn1()).append(" )\n");
        builder.append("         .epc                      = ").append(this.getEpc()).append('\n');
        builder.append("         .nfcFtnRef1               = ").append(this.getNfcFtnRef1()).append('\n');
        builder.append("         .nfcEdnRef1               = ").append(this.getNfcEdnRef1()).append('\n');
        builder.append("         .fPrintFormData           = ").append(this.isFPrintFormData()).append('\n');
        builder.append("         .fSaveFormData            = ").append(this.isFSaveFormData()).append('\n');
        builder.append("         .fShadeFormData           = ").append(this.isFShadeFormData()).append('\n');
        builder.append("         .fWCFtnEdn                = ").append(this.isFWCFtnEdn()).append('\n');
        builder.append("    .cLines               = ");
        builder.append(" (").append(this.getCLines()).append(" )\n");
        builder.append("    .cWordsFtnEnd         = ");
        builder.append(" (").append(this.getCWordsFtnEnd()).append(" )\n");
        builder.append("    .cChFtnEdn            = ");
        builder.append(" (").append(this.getCChFtnEdn()).append(" )\n");
        builder.append("    .cPgFtnEdn            = ");
        builder.append(" (").append(this.getCPgFtnEdn()).append(" )\n");
        builder.append("    .cParasFtnEdn         = ");
        builder.append(" (").append(this.getCParasFtnEdn()).append(" )\n");
        builder.append("    .cLinesFtnEdn         = ");
        builder.append(" (").append(this.getCLinesFtnEdn()).append(" )\n");
        builder.append("    .lKeyProtDoc          = ");
        builder.append(" (").append(this.getLKeyProtDoc()).append(" )\n");
        builder.append("    .view                 = ");
        builder.append(" (").append(this.getView()).append(" )\n");
        builder.append("         .wvkSaved                 = ").append(this.getWvkSaved()).append('\n');
        builder.append("         .wScaleSaved              = ").append(this.getWScaleSaved()).append('\n');
        builder.append("         .zkSaved                  = ").append(this.getZkSaved()).append('\n');
        builder.append("         .fRotateFontW6            = ").append(this.isFRotateFontW6()).append('\n');
        builder.append("         .iGutterPos               = ").append(this.isIGutterPos()).append('\n');
        builder.append("    .docinfo4             = ");
        builder.append(" (").append(this.getDocinfo4()).append(" )\n");
        builder.append("         .fNoTabForInd             = ").append(this.isFNoTabForInd()).append('\n');
        builder.append("         .fNoSpaceRaiseLower       = ").append(this.isFNoSpaceRaiseLower()).append('\n');
        builder.append("         .fSupressSpdfAfterPageBreak     = ").append(this.isFSupressSpdfAfterPageBreak()).append('\n');
        builder.append("         .fWrapTrailSpaces         = ").append(this.isFWrapTrailSpaces()).append('\n');
        builder.append("         .fMapPrintTextColor       = ").append(this.isFMapPrintTextColor()).append('\n');
        builder.append("         .fNoColumnBalance         = ").append(this.isFNoColumnBalance()).append('\n');
        builder.append("         .fConvMailMergeEsc        = ").append(this.isFConvMailMergeEsc()).append('\n');
        builder.append("         .fSupressTopSpacing       = ").append(this.isFSupressTopSpacing()).append('\n');
        builder.append("         .fOrigWordTableRules      = ").append(this.isFOrigWordTableRules()).append('\n');
        builder.append("         .fTransparentMetafiles     = ").append(this.isFTransparentMetafiles()).append('\n');
        builder.append("         .fShowBreaksInFrames      = ").append(this.isFShowBreaksInFrames()).append('\n');
        builder.append("         .fSwapBordersFacingPgs     = ").append(this.isFSwapBordersFacingPgs()).append('\n');
        builder.append("         .fSuppressTopSPacingMac5     = ").append(this.isFSuppressTopSPacingMac5()).append('\n');
        builder.append("         .fTruncDxaExpand          = ").append(this.isFTruncDxaExpand()).append('\n');
        builder.append("         .fPrintBodyBeforeHdr      = ").append(this.isFPrintBodyBeforeHdr()).append('\n');
        builder.append("         .fNoLeading               = ").append(this.isFNoLeading()).append('\n');
        builder.append("         .fMWSmallCaps             = ").append(this.isFMWSmallCaps()).append('\n');
        builder.append("    .adt                  = ");
        builder.append(" (").append(this.getAdt()).append(" )\n");
        builder.append("    .doptypography        = ");
        builder.append(" (").append(Arrays.toString(this.getDoptypography())).append(" )\n");
        builder.append("    .dogrid               = ");
        builder.append(" (").append(Arrays.toString(this.getDogrid())).append(" )\n");
        builder.append("    .docinfo5             = ");
        builder.append(" (").append(this.getDocinfo5()).append(" )\n");
        builder.append("         .lvl                      = ").append(this.getLvl()).append('\n');
        builder.append("         .fGramAllDone             = ").append(this.isFGramAllDone()).append('\n');
        builder.append("         .fGramAllClean            = ").append(this.isFGramAllClean()).append('\n');
        builder.append("         .fSubsetFonts             = ").append(this.isFSubsetFonts()).append('\n');
        builder.append("         .fHideLastVersion         = ").append(this.isFHideLastVersion()).append('\n');
        builder.append("         .fHtmlDoc                 = ").append(this.isFHtmlDoc()).append('\n');
        builder.append("         .fSnapBorder              = ").append(this.isFSnapBorder()).append('\n');
        builder.append("         .fIncludeHeader           = ").append(this.isFIncludeHeader()).append('\n');
        builder.append("         .fIncludeFooter           = ").append(this.isFIncludeFooter()).append('\n');
        builder.append("         .fForcePageSizePag        = ").append(this.isFForcePageSizePag()).append('\n');
        builder.append("         .fMinFontSizePag          = ").append(this.isFMinFontSizePag()).append('\n');
        builder.append("    .docinfo6             = ");
        builder.append(" (").append(this.getDocinfo6()).append(" )\n");
        builder.append("         .fHaveVersions            = ").append(this.isFHaveVersions()).append('\n');
        builder.append("         .fAutoVersions            = ").append(this.isFAutoVersions()).append('\n');
        builder.append("    .asumyi               = ");
        builder.append(" (").append(Arrays.toString(this.getAsumyi())).append(" )\n");
        builder.append("    .cChWS                = ");
        builder.append(" (").append(this.getCChWS()).append(" )\n");
        builder.append("    .cChWSFtnEdn          = ");
        builder.append(" (").append(this.getCChWSFtnEdn()).append(" )\n");
        builder.append("    .grfDocEvents         = ");
        builder.append(" (").append(this.getGrfDocEvents()).append(" )\n");
        builder.append("    .virusinfo            = ");
        builder.append(" (").append(this.getVirusinfo()).append(" )\n");
        builder.append("         .fVirusPrompted           = ").append(this.isFVirusPrompted()).append('\n');
        builder.append("         .fVirusLoadSafe           = ").append(this.isFVirusLoadSafe()).append('\n');
        builder.append("         .KeyVirusSession30        = ").append(this.getKeyVirusSession30()).append('\n');
        builder.append("    .Spare                = ");
        builder.append(" (").append(Arrays.toString(this.getSpare())).append(" )\n");
        builder.append("    .reserved1            = ");
        builder.append(" (").append(this.getReserved1()).append(" )\n");
        builder.append("    .reserved2            = ");
        builder.append(" (").append(this.getReserved2()).append(" )\n");
        builder.append("    .cDBC                 = ");
        builder.append(" (").append(this.getCDBC()).append(" )\n");
        builder.append("    .cDBCFtnEdn           = ");
        builder.append(" (").append(this.getCDBCFtnEdn()).append(" )\n");
        builder.append("    .reserved             = ");
        builder.append(" (").append(this.getReserved()).append(" )\n");
        builder.append("    .nfcFtnRef            = ");
        builder.append(" (").append(this.getNfcFtnRef()).append(" )\n");
        builder.append("    .nfcEdnRef            = ");
        builder.append(" (").append(this.getNfcEdnRef()).append(" )\n");
        builder.append("    .hpsZoonFontPag       = ");
        builder.append(" (").append(this.getHpsZoonFontPag()).append(" )\n");
        builder.append("    .dywDispPag           = ");
        builder.append(" (").append(this.getDywDispPag()).append(" )\n");
        builder.append("[/DOP]\n");
        return builder.toString();
    }

    @Internal
    public byte getFormatFlags() {
        return this.field_1_formatFlags;
    }

    @Internal
    public void setFormatFlags(byte field_1_formatFlags) {
        this.field_1_formatFlags = field_1_formatFlags;
    }

    @Internal
    public byte getUnused2() {
        return this.field_2_unused2;
    }

    @Internal
    public void setUnused2(byte field_2_unused2) {
        this.field_2_unused2 = field_2_unused2;
    }

    @Internal
    public short getFootnoteInfo() {
        return this.field_3_footnoteInfo;
    }

    @Internal
    public void setFootnoteInfo(short field_3_footnoteInfo) {
        this.field_3_footnoteInfo = field_3_footnoteInfo;
    }

    @Internal
    public byte getFOutlineDirtySave() {
        return this.field_4_fOutlineDirtySave;
    }

    @Internal
    public void setFOutlineDirtySave(byte field_4_fOutlineDirtySave) {
        this.field_4_fOutlineDirtySave = field_4_fOutlineDirtySave;
    }

    @Internal
    public byte getDocinfo() {
        return this.field_5_docinfo;
    }

    @Internal
    public void setDocinfo(byte field_5_docinfo) {
        this.field_5_docinfo = field_5_docinfo;
    }

    @Internal
    public byte getDocinfo1() {
        return this.field_6_docinfo1;
    }

    @Internal
    public void setDocinfo1(byte field_6_docinfo1) {
        this.field_6_docinfo1 = field_6_docinfo1;
    }

    @Internal
    public byte getDocinfo2() {
        return this.field_7_docinfo2;
    }

    @Internal
    public void setDocinfo2(byte field_7_docinfo2) {
        this.field_7_docinfo2 = field_7_docinfo2;
    }

    @Internal
    public short getDocinfo3() {
        return this.field_8_docinfo3;
    }

    @Internal
    public void setDocinfo3(short field_8_docinfo3) {
        this.field_8_docinfo3 = field_8_docinfo3;
    }

    @Internal
    public int getDxaTab() {
        return this.field_9_dxaTab;
    }

    @Internal
    public void setDxaTab(int field_9_dxaTab) {
        this.field_9_dxaTab = field_9_dxaTab;
    }

    @Internal
    public int getWSpare() {
        return this.field_10_wSpare;
    }

    @Internal
    public void setWSpare(int field_10_wSpare) {
        this.field_10_wSpare = field_10_wSpare;
    }

    @Internal
    public int getDxaHotz() {
        return this.field_11_dxaHotz;
    }

    @Internal
    public void setDxaHotz(int field_11_dxaHotz) {
        this.field_11_dxaHotz = field_11_dxaHotz;
    }

    @Internal
    public int getCConsexHypLim() {
        return this.field_12_cConsexHypLim;
    }

    @Internal
    public void setCConsexHypLim(int field_12_cConsexHypLim) {
        this.field_12_cConsexHypLim = field_12_cConsexHypLim;
    }

    @Internal
    public int getWSpare2() {
        return this.field_13_wSpare2;
    }

    @Internal
    public void setWSpare2(int field_13_wSpare2) {
        this.field_13_wSpare2 = field_13_wSpare2;
    }

    @Internal
    public int getDttmCreated() {
        return this.field_14_dttmCreated;
    }

    @Internal
    public void setDttmCreated(int field_14_dttmCreated) {
        this.field_14_dttmCreated = field_14_dttmCreated;
    }

    @Internal
    public int getDttmRevised() {
        return this.field_15_dttmRevised;
    }

    @Internal
    public void setDttmRevised(int field_15_dttmRevised) {
        this.field_15_dttmRevised = field_15_dttmRevised;
    }

    @Internal
    public int getDttmLastPrint() {
        return this.field_16_dttmLastPrint;
    }

    @Internal
    public void setDttmLastPrint(int field_16_dttmLastPrint) {
        this.field_16_dttmLastPrint = field_16_dttmLastPrint;
    }

    @Internal
    public int getNRevision() {
        return this.field_17_nRevision;
    }

    @Internal
    public void setNRevision(int field_17_nRevision) {
        this.field_17_nRevision = field_17_nRevision;
    }

    @Internal
    public int getTmEdited() {
        return this.field_18_tmEdited;
    }

    @Internal
    public void setTmEdited(int field_18_tmEdited) {
        this.field_18_tmEdited = field_18_tmEdited;
    }

    @Internal
    public int getCWords() {
        return this.field_19_cWords;
    }

    @Internal
    public void setCWords(int field_19_cWords) {
        this.field_19_cWords = field_19_cWords;
    }

    @Internal
    public int getCCh() {
        return this.field_20_cCh;
    }

    @Internal
    public void setCCh(int field_20_cCh) {
        this.field_20_cCh = field_20_cCh;
    }

    @Internal
    public int getCPg() {
        return this.field_21_cPg;
    }

    @Internal
    public void setCPg(int field_21_cPg) {
        this.field_21_cPg = field_21_cPg;
    }

    @Internal
    public int getCParas() {
        return this.field_22_cParas;
    }

    @Internal
    public void setCParas(int field_22_cParas) {
        this.field_22_cParas = field_22_cParas;
    }

    @Internal
    public short getEdn() {
        return this.field_23_Edn;
    }

    @Internal
    public void setEdn(short field_23_Edn) {
        this.field_23_Edn = field_23_Edn;
    }

    @Internal
    public short getEdn1() {
        return this.field_24_Edn1;
    }

    @Internal
    public void setEdn1(short field_24_Edn1) {
        this.field_24_Edn1 = field_24_Edn1;
    }

    @Internal
    public int getCLines() {
        return this.field_25_cLines;
    }

    @Internal
    public void setCLines(int field_25_cLines) {
        this.field_25_cLines = field_25_cLines;
    }

    @Internal
    public int getCWordsFtnEnd() {
        return this.field_26_cWordsFtnEnd;
    }

    @Internal
    public void setCWordsFtnEnd(int field_26_cWordsFtnEnd) {
        this.field_26_cWordsFtnEnd = field_26_cWordsFtnEnd;
    }

    @Internal
    public int getCChFtnEdn() {
        return this.field_27_cChFtnEdn;
    }

    @Internal
    public void setCChFtnEdn(int field_27_cChFtnEdn) {
        this.field_27_cChFtnEdn = field_27_cChFtnEdn;
    }

    @Internal
    public short getCPgFtnEdn() {
        return this.field_28_cPgFtnEdn;
    }

    @Internal
    public void setCPgFtnEdn(short field_28_cPgFtnEdn) {
        this.field_28_cPgFtnEdn = field_28_cPgFtnEdn;
    }

    @Internal
    public int getCParasFtnEdn() {
        return this.field_29_cParasFtnEdn;
    }

    @Internal
    public void setCParasFtnEdn(int field_29_cParasFtnEdn) {
        this.field_29_cParasFtnEdn = field_29_cParasFtnEdn;
    }

    @Internal
    public int getCLinesFtnEdn() {
        return this.field_30_cLinesFtnEdn;
    }

    @Internal
    public void setCLinesFtnEdn(int field_30_cLinesFtnEdn) {
        this.field_30_cLinesFtnEdn = field_30_cLinesFtnEdn;
    }

    @Internal
    public int getLKeyProtDoc() {
        return this.field_31_lKeyProtDoc;
    }

    @Internal
    public void setLKeyProtDoc(int field_31_lKeyProtDoc) {
        this.field_31_lKeyProtDoc = field_31_lKeyProtDoc;
    }

    @Internal
    public short getView() {
        return this.field_32_view;
    }

    @Internal
    public void setView(short field_32_view) {
        this.field_32_view = field_32_view;
    }

    @Internal
    public int getDocinfo4() {
        return this.field_33_docinfo4;
    }

    @Internal
    public void setDocinfo4(int field_33_docinfo4) {
        this.field_33_docinfo4 = field_33_docinfo4;
    }

    @Internal
    public short getAdt() {
        return this.field_34_adt;
    }

    @Internal
    public void setAdt(short field_34_adt) {
        this.field_34_adt = field_34_adt;
    }

    @Internal
    public byte[] getDoptypography() {
        return this.field_35_doptypography;
    }

    @Internal
    public void setDoptypography(byte[] field_35_doptypography) {
        this.field_35_doptypography = field_35_doptypography;
    }

    @Internal
    public byte[] getDogrid() {
        return this.field_36_dogrid;
    }

    @Internal
    public void setDogrid(byte[] field_36_dogrid) {
        this.field_36_dogrid = field_36_dogrid;
    }

    @Internal
    public short getDocinfo5() {
        return this.field_37_docinfo5;
    }

    @Internal
    public void setDocinfo5(short field_37_docinfo5) {
        this.field_37_docinfo5 = field_37_docinfo5;
    }

    @Internal
    public short getDocinfo6() {
        return this.field_38_docinfo6;
    }

    @Internal
    public void setDocinfo6(short field_38_docinfo6) {
        this.field_38_docinfo6 = field_38_docinfo6;
    }

    @Internal
    public byte[] getAsumyi() {
        return this.field_39_asumyi;
    }

    @Internal
    public void setAsumyi(byte[] field_39_asumyi) {
        this.field_39_asumyi = field_39_asumyi;
    }

    @Internal
    public int getCChWS() {
        return this.field_40_cChWS;
    }

    @Internal
    public void setCChWS(int field_40_cChWS) {
        this.field_40_cChWS = field_40_cChWS;
    }

    @Internal
    public int getCChWSFtnEdn() {
        return this.field_41_cChWSFtnEdn;
    }

    @Internal
    public void setCChWSFtnEdn(int field_41_cChWSFtnEdn) {
        this.field_41_cChWSFtnEdn = field_41_cChWSFtnEdn;
    }

    @Internal
    public int getGrfDocEvents() {
        return this.field_42_grfDocEvents;
    }

    @Internal
    public void setGrfDocEvents(int field_42_grfDocEvents) {
        this.field_42_grfDocEvents = field_42_grfDocEvents;
    }

    @Internal
    public int getVirusinfo() {
        return this.field_43_virusinfo;
    }

    @Internal
    public void setVirusinfo(int field_43_virusinfo) {
        this.field_43_virusinfo = field_43_virusinfo;
    }

    @Internal
    public byte[] getSpare() {
        return this.field_44_Spare;
    }

    @Internal
    public void setSpare(byte[] field_44_Spare) {
        this.field_44_Spare = field_44_Spare;
    }

    @Internal
    public int getReserved1() {
        return this.field_45_reserved1;
    }

    @Internal
    public void setReserved1(int field_45_reserved1) {
        this.field_45_reserved1 = field_45_reserved1;
    }

    @Internal
    public int getReserved2() {
        return this.field_46_reserved2;
    }

    @Internal
    public void setReserved2(int field_46_reserved2) {
        this.field_46_reserved2 = field_46_reserved2;
    }

    @Internal
    public int getCDBC() {
        return this.field_47_cDBC;
    }

    @Internal
    public void setCDBC(int field_47_cDBC) {
        this.field_47_cDBC = field_47_cDBC;
    }

    @Internal
    public int getCDBCFtnEdn() {
        return this.field_48_cDBCFtnEdn;
    }

    @Internal
    public void setCDBCFtnEdn(int field_48_cDBCFtnEdn) {
        this.field_48_cDBCFtnEdn = field_48_cDBCFtnEdn;
    }

    @Internal
    public int getReserved() {
        return this.field_49_reserved;
    }

    @Internal
    public void setReserved(int field_49_reserved) {
        this.field_49_reserved = field_49_reserved;
    }

    @Internal
    public short getNfcFtnRef() {
        return this.field_50_nfcFtnRef;
    }

    @Internal
    public void setNfcFtnRef(short field_50_nfcFtnRef) {
        this.field_50_nfcFtnRef = field_50_nfcFtnRef;
    }

    @Internal
    public short getNfcEdnRef() {
        return this.field_51_nfcEdnRef;
    }

    @Internal
    public void setNfcEdnRef(short field_51_nfcEdnRef) {
        this.field_51_nfcEdnRef = field_51_nfcEdnRef;
    }

    @Internal
    public short getHpsZoonFontPag() {
        return this.field_52_hpsZoonFontPag;
    }

    @Internal
    public void setHpsZoonFontPag(short field_52_hpsZoonFontPag) {
        this.field_52_hpsZoonFontPag = field_52_hpsZoonFontPag;
    }

    @Internal
    public short getDywDispPag() {
        return this.field_53_dywDispPag;
    }

    @Internal
    public void setDywDispPag(short field_53_dywDispPag) {
        this.field_53_dywDispPag = field_53_dywDispPag;
    }

    @Internal
    public void setFFacingPages(boolean value) {
        this.field_1_formatFlags = (byte)fFacingPages.setBoolean(this.field_1_formatFlags, value);
    }

    @Internal
    public boolean isFFacingPages() {
        return fFacingPages.isSet(this.field_1_formatFlags);
    }

    @Internal
    public void setFWidowControl(boolean value) {
        this.field_1_formatFlags = (byte)fWidowControl.setBoolean(this.field_1_formatFlags, value);
    }

    @Internal
    public boolean isFWidowControl() {
        return fWidowControl.isSet(this.field_1_formatFlags);
    }

    @Internal
    public void setFPMHMainDoc(boolean value) {
        this.field_1_formatFlags = (byte)fPMHMainDoc.setBoolean(this.field_1_formatFlags, value);
    }

    @Internal
    public boolean isFPMHMainDoc() {
        return fPMHMainDoc.isSet(this.field_1_formatFlags);
    }

    @Internal
    public void setGrfSupression(byte value) {
        this.field_1_formatFlags = (byte)grfSupression.setValue(this.field_1_formatFlags, value);
    }

    @Internal
    public byte getGrfSupression() {
        return (byte)grfSupression.getValue(this.field_1_formatFlags);
    }

    @Internal
    public void setFpc(byte value) {
        this.field_1_formatFlags = (byte)fpc.setValue(this.field_1_formatFlags, value);
    }

    @Internal
    public byte getFpc() {
        return (byte)fpc.getValue(this.field_1_formatFlags);
    }

    @Internal
    public void setUnused1(boolean value) {
        this.field_1_formatFlags = (byte)unused1.setBoolean(this.field_1_formatFlags, value);
    }

    @Internal
    public boolean isUnused1() {
        return unused1.isSet(this.field_1_formatFlags);
    }

    @Internal
    public void setRncFtn(byte value) {
        this.field_3_footnoteInfo = (short)rncFtn.setValue(this.field_3_footnoteInfo, value);
    }

    @Internal
    public byte getRncFtn() {
        return (byte)rncFtn.getValue(this.field_3_footnoteInfo);
    }

    @Internal
    public void setNFtn(short value) {
        this.field_3_footnoteInfo = (short)nFtn.setValue(this.field_3_footnoteInfo, value);
    }

    @Internal
    public short getNFtn() {
        return (short)nFtn.getValue(this.field_3_footnoteInfo);
    }

    @Internal
    public void setFOnlyMacPics(boolean value) {
        this.field_5_docinfo = (byte)fOnlyMacPics.setBoolean(this.field_5_docinfo, value);
    }

    @Internal
    public boolean isFOnlyMacPics() {
        return fOnlyMacPics.isSet(this.field_5_docinfo);
    }

    @Internal
    public void setFOnlyWinPics(boolean value) {
        this.field_5_docinfo = (byte)fOnlyWinPics.setBoolean(this.field_5_docinfo, value);
    }

    @Internal
    public boolean isFOnlyWinPics() {
        return fOnlyWinPics.isSet(this.field_5_docinfo);
    }

    @Internal
    public void setFLabelDoc(boolean value) {
        this.field_5_docinfo = (byte)fLabelDoc.setBoolean(this.field_5_docinfo, value);
    }

    @Internal
    public boolean isFLabelDoc() {
        return fLabelDoc.isSet(this.field_5_docinfo);
    }

    @Internal
    public void setFHyphCapitals(boolean value) {
        this.field_5_docinfo = (byte)fHyphCapitals.setBoolean(this.field_5_docinfo, value);
    }

    @Internal
    public boolean isFHyphCapitals() {
        return fHyphCapitals.isSet(this.field_5_docinfo);
    }

    @Internal
    public void setFAutoHyphen(boolean value) {
        this.field_5_docinfo = (byte)fAutoHyphen.setBoolean(this.field_5_docinfo, value);
    }

    @Internal
    public boolean isFAutoHyphen() {
        return fAutoHyphen.isSet(this.field_5_docinfo);
    }

    @Internal
    public void setFFormNoFields(boolean value) {
        this.field_5_docinfo = (byte)fFormNoFields.setBoolean(this.field_5_docinfo, value);
    }

    @Internal
    public boolean isFFormNoFields() {
        return fFormNoFields.isSet(this.field_5_docinfo);
    }

    @Internal
    public void setFLinkStyles(boolean value) {
        this.field_5_docinfo = (byte)fLinkStyles.setBoolean(this.field_5_docinfo, value);
    }

    @Internal
    public boolean isFLinkStyles() {
        return fLinkStyles.isSet(this.field_5_docinfo);
    }

    @Internal
    public void setFRevMarking(boolean value) {
        this.field_5_docinfo = (byte)fRevMarking.setBoolean(this.field_5_docinfo, value);
    }

    @Internal
    public boolean isFRevMarking() {
        return fRevMarking.isSet(this.field_5_docinfo);
    }

    @Internal
    public void setFBackup(boolean value) {
        this.field_6_docinfo1 = (byte)fBackup.setBoolean(this.field_6_docinfo1, value);
    }

    @Internal
    public boolean isFBackup() {
        return fBackup.isSet(this.field_6_docinfo1);
    }

    @Internal
    public void setFExactCWords(boolean value) {
        this.field_6_docinfo1 = (byte)fExactCWords.setBoolean(this.field_6_docinfo1, value);
    }

    @Internal
    public boolean isFExactCWords() {
        return fExactCWords.isSet(this.field_6_docinfo1);
    }

    @Internal
    public void setFPagHidden(boolean value) {
        this.field_6_docinfo1 = (byte)fPagHidden.setBoolean(this.field_6_docinfo1, value);
    }

    @Internal
    public boolean isFPagHidden() {
        return fPagHidden.isSet(this.field_6_docinfo1);
    }

    @Internal
    public void setFPagResults(boolean value) {
        this.field_6_docinfo1 = (byte)fPagResults.setBoolean(this.field_6_docinfo1, value);
    }

    @Internal
    public boolean isFPagResults() {
        return fPagResults.isSet(this.field_6_docinfo1);
    }

    @Internal
    public void setFLockAtn(boolean value) {
        this.field_6_docinfo1 = (byte)fLockAtn.setBoolean(this.field_6_docinfo1, value);
    }

    @Internal
    public boolean isFLockAtn() {
        return fLockAtn.isSet(this.field_6_docinfo1);
    }

    @Internal
    public void setFMirrorMargins(boolean value) {
        this.field_6_docinfo1 = (byte)fMirrorMargins.setBoolean(this.field_6_docinfo1, value);
    }

    @Internal
    public boolean isFMirrorMargins() {
        return fMirrorMargins.isSet(this.field_6_docinfo1);
    }

    @Internal
    public void setUnused3(boolean value) {
        this.field_6_docinfo1 = (byte)unused3.setBoolean(this.field_6_docinfo1, value);
    }

    @Internal
    public boolean isUnused3() {
        return unused3.isSet(this.field_6_docinfo1);
    }

    @Internal
    public void setFDfltTrueType(boolean value) {
        this.field_6_docinfo1 = (byte)fDfltTrueType.setBoolean(this.field_6_docinfo1, value);
    }

    @Internal
    public boolean isFDfltTrueType() {
        return fDfltTrueType.isSet(this.field_6_docinfo1);
    }

    @Internal
    public void setFPagSupressTopSpacing(boolean value) {
        this.field_7_docinfo2 = (byte)fPagSupressTopSpacing.setBoolean(this.field_7_docinfo2, value);
    }

    @Internal
    public boolean isFPagSupressTopSpacing() {
        return fPagSupressTopSpacing.isSet(this.field_7_docinfo2);
    }

    @Internal
    public void setFProtEnabled(boolean value) {
        this.field_7_docinfo2 = (byte)fProtEnabled.setBoolean(this.field_7_docinfo2, value);
    }

    @Internal
    public boolean isFProtEnabled() {
        return fProtEnabled.isSet(this.field_7_docinfo2);
    }

    @Internal
    public void setFDispFormFldSel(boolean value) {
        this.field_7_docinfo2 = (byte)fDispFormFldSel.setBoolean(this.field_7_docinfo2, value);
    }

    @Internal
    public boolean isFDispFormFldSel() {
        return fDispFormFldSel.isSet(this.field_7_docinfo2);
    }

    @Internal
    public void setFRMView(boolean value) {
        this.field_7_docinfo2 = (byte)fRMView.setBoolean(this.field_7_docinfo2, value);
    }

    @Internal
    public boolean isFRMView() {
        return fRMView.isSet(this.field_7_docinfo2);
    }

    @Internal
    public void setFRMPrint(boolean value) {
        this.field_7_docinfo2 = (byte)fRMPrint.setBoolean(this.field_7_docinfo2, value);
    }

    @Internal
    public boolean isFRMPrint() {
        return fRMPrint.isSet(this.field_7_docinfo2);
    }

    @Internal
    public void setUnused4(boolean value) {
        this.field_7_docinfo2 = (byte)unused4.setBoolean(this.field_7_docinfo2, value);
    }

    @Internal
    public boolean isUnused4() {
        return unused4.isSet(this.field_7_docinfo2);
    }

    @Internal
    public void setFLockRev(boolean value) {
        this.field_7_docinfo2 = (byte)fLockRev.setBoolean(this.field_7_docinfo2, value);
    }

    @Internal
    public boolean isFLockRev() {
        return fLockRev.isSet(this.field_7_docinfo2);
    }

    @Internal
    public void setFEmbedFonts(boolean value) {
        this.field_7_docinfo2 = (byte)fEmbedFonts.setBoolean(this.field_7_docinfo2, value);
    }

    @Internal
    public boolean isFEmbedFonts() {
        return fEmbedFonts.isSet(this.field_7_docinfo2);
    }

    @Internal
    public void setOldfNoTabForInd(boolean value) {
        this.field_8_docinfo3 = (short)oldfNoTabForInd.setBoolean(this.field_8_docinfo3, value);
    }

    @Internal
    public boolean isOldfNoTabForInd() {
        return oldfNoTabForInd.isSet(this.field_8_docinfo3);
    }

    @Internal
    public void setOldfNoSpaceRaiseLower(boolean value) {
        this.field_8_docinfo3 = (short)oldfNoSpaceRaiseLower.setBoolean(this.field_8_docinfo3, value);
    }

    @Internal
    public boolean isOldfNoSpaceRaiseLower() {
        return oldfNoSpaceRaiseLower.isSet(this.field_8_docinfo3);
    }

    @Internal
    public void setOldfSuppressSpbfAfterPageBreak(boolean value) {
        this.field_8_docinfo3 = (short)oldfSuppressSpbfAfterPageBreak.setBoolean(this.field_8_docinfo3, value);
    }

    @Internal
    public boolean isOldfSuppressSpbfAfterPageBreak() {
        return oldfSuppressSpbfAfterPageBreak.isSet(this.field_8_docinfo3);
    }

    @Internal
    public void setOldfWrapTrailSpaces(boolean value) {
        this.field_8_docinfo3 = (short)oldfWrapTrailSpaces.setBoolean(this.field_8_docinfo3, value);
    }

    @Internal
    public boolean isOldfWrapTrailSpaces() {
        return oldfWrapTrailSpaces.isSet(this.field_8_docinfo3);
    }

    @Internal
    public void setOldfMapPrintTextColor(boolean value) {
        this.field_8_docinfo3 = (short)oldfMapPrintTextColor.setBoolean(this.field_8_docinfo3, value);
    }

    @Internal
    public boolean isOldfMapPrintTextColor() {
        return oldfMapPrintTextColor.isSet(this.field_8_docinfo3);
    }

    @Internal
    public void setOldfNoColumnBalance(boolean value) {
        this.field_8_docinfo3 = (short)oldfNoColumnBalance.setBoolean(this.field_8_docinfo3, value);
    }

    @Internal
    public boolean isOldfNoColumnBalance() {
        return oldfNoColumnBalance.isSet(this.field_8_docinfo3);
    }

    @Internal
    public void setOldfConvMailMergeEsc(boolean value) {
        this.field_8_docinfo3 = (short)oldfConvMailMergeEsc.setBoolean(this.field_8_docinfo3, value);
    }

    @Internal
    public boolean isOldfConvMailMergeEsc() {
        return oldfConvMailMergeEsc.isSet(this.field_8_docinfo3);
    }

    @Internal
    public void setOldfSupressTopSpacing(boolean value) {
        this.field_8_docinfo3 = (short)oldfSupressTopSpacing.setBoolean(this.field_8_docinfo3, value);
    }

    @Internal
    public boolean isOldfSupressTopSpacing() {
        return oldfSupressTopSpacing.isSet(this.field_8_docinfo3);
    }

    @Internal
    public void setOldfOrigWordTableRules(boolean value) {
        this.field_8_docinfo3 = (short)oldfOrigWordTableRules.setBoolean(this.field_8_docinfo3, value);
    }

    @Internal
    public boolean isOldfOrigWordTableRules() {
        return oldfOrigWordTableRules.isSet(this.field_8_docinfo3);
    }

    @Internal
    public void setOldfTransparentMetafiles(boolean value) {
        this.field_8_docinfo3 = (short)oldfTransparentMetafiles.setBoolean(this.field_8_docinfo3, value);
    }

    @Internal
    public boolean isOldfTransparentMetafiles() {
        return oldfTransparentMetafiles.isSet(this.field_8_docinfo3);
    }

    @Internal
    public void setOldfShowBreaksInFrames(boolean value) {
        this.field_8_docinfo3 = (short)oldfShowBreaksInFrames.setBoolean(this.field_8_docinfo3, value);
    }

    @Internal
    public boolean isOldfShowBreaksInFrames() {
        return oldfShowBreaksInFrames.isSet(this.field_8_docinfo3);
    }

    @Internal
    public void setOldfSwapBordersFacingPgs(boolean value) {
        this.field_8_docinfo3 = (short)oldfSwapBordersFacingPgs.setBoolean(this.field_8_docinfo3, value);
    }

    @Internal
    public boolean isOldfSwapBordersFacingPgs() {
        return oldfSwapBordersFacingPgs.isSet(this.field_8_docinfo3);
    }

    @Internal
    public void setUnused5(byte value) {
        this.field_8_docinfo3 = (short)unused5.setValue(this.field_8_docinfo3, value);
    }

    @Internal
    public byte getUnused5() {
        return (byte)unused5.getValue(this.field_8_docinfo3);
    }

    @Internal
    public void setRncEdn(byte value) {
        this.field_23_Edn = (short)rncEdn.setValue(this.field_23_Edn, value);
    }

    @Internal
    public byte getRncEdn() {
        return (byte)rncEdn.getValue(this.field_23_Edn);
    }

    @Internal
    public void setNEdn(short value) {
        this.field_23_Edn = (short)nEdn.setValue(this.field_23_Edn, value);
    }

    @Internal
    public short getNEdn() {
        return (short)nEdn.getValue(this.field_23_Edn);
    }

    @Internal
    public void setEpc(byte value) {
        this.field_24_Edn1 = (short)epc.setValue(this.field_24_Edn1, value);
    }

    @Internal
    public byte getEpc() {
        return (byte)epc.getValue(this.field_24_Edn1);
    }

    @Internal
    public void setNfcFtnRef1(byte value) {
        this.field_24_Edn1 = (short)nfcFtnRef1.setValue(this.field_24_Edn1, value);
    }

    @Internal
    public byte getNfcFtnRef1() {
        return (byte)nfcFtnRef1.getValue(this.field_24_Edn1);
    }

    @Internal
    public void setNfcEdnRef1(byte value) {
        this.field_24_Edn1 = (short)nfcEdnRef1.setValue(this.field_24_Edn1, value);
    }

    @Internal
    public byte getNfcEdnRef1() {
        return (byte)nfcEdnRef1.getValue(this.field_24_Edn1);
    }

    @Internal
    public void setFPrintFormData(boolean value) {
        this.field_24_Edn1 = (short)fPrintFormData.setBoolean(this.field_24_Edn1, value);
    }

    @Internal
    public boolean isFPrintFormData() {
        return fPrintFormData.isSet(this.field_24_Edn1);
    }

    @Internal
    public void setFSaveFormData(boolean value) {
        this.field_24_Edn1 = (short)fSaveFormData.setBoolean(this.field_24_Edn1, value);
    }

    @Internal
    public boolean isFSaveFormData() {
        return fSaveFormData.isSet(this.field_24_Edn1);
    }

    @Internal
    public void setFShadeFormData(boolean value) {
        this.field_24_Edn1 = (short)fShadeFormData.setBoolean(this.field_24_Edn1, value);
    }

    @Internal
    public boolean isFShadeFormData() {
        return fShadeFormData.isSet(this.field_24_Edn1);
    }

    @Internal
    public void setFWCFtnEdn(boolean value) {
        this.field_24_Edn1 = (short)fWCFtnEdn.setBoolean(this.field_24_Edn1, value);
    }

    @Internal
    public boolean isFWCFtnEdn() {
        return fWCFtnEdn.isSet(this.field_24_Edn1);
    }

    @Internal
    public void setWvkSaved(byte value) {
        this.field_32_view = (short)wvkSaved.setValue(this.field_32_view, value);
    }

    @Internal
    public byte getWvkSaved() {
        return (byte)wvkSaved.getValue(this.field_32_view);
    }

    @Internal
    public void setWScaleSaved(short value) {
        this.field_32_view = (short)wScaleSaved.setValue(this.field_32_view, value);
    }

    @Internal
    public short getWScaleSaved() {
        return (short)wScaleSaved.getValue(this.field_32_view);
    }

    @Internal
    public void setZkSaved(byte value) {
        this.field_32_view = (short)zkSaved.setValue(this.field_32_view, value);
    }

    @Internal
    public byte getZkSaved() {
        return (byte)zkSaved.getValue(this.field_32_view);
    }

    @Internal
    public void setFRotateFontW6(boolean value) {
        this.field_32_view = (short)fRotateFontW6.setBoolean(this.field_32_view, value);
    }

    @Internal
    public boolean isFRotateFontW6() {
        return fRotateFontW6.isSet(this.field_32_view);
    }

    @Internal
    public void setIGutterPos(boolean value) {
        this.field_32_view = (short)iGutterPos.setBoolean(this.field_32_view, value);
    }

    @Internal
    public boolean isIGutterPos() {
        return iGutterPos.isSet(this.field_32_view);
    }

    @Internal
    public void setFNoTabForInd(boolean value) {
        this.field_33_docinfo4 = fNoTabForInd.setBoolean(this.field_33_docinfo4, value);
    }

    @Internal
    public boolean isFNoTabForInd() {
        return fNoTabForInd.isSet(this.field_33_docinfo4);
    }

    @Internal
    public void setFNoSpaceRaiseLower(boolean value) {
        this.field_33_docinfo4 = fNoSpaceRaiseLower.setBoolean(this.field_33_docinfo4, value);
    }

    @Internal
    public boolean isFNoSpaceRaiseLower() {
        return fNoSpaceRaiseLower.isSet(this.field_33_docinfo4);
    }

    @Internal
    public void setFSupressSpdfAfterPageBreak(boolean value) {
        this.field_33_docinfo4 = fSupressSpdfAfterPageBreak.setBoolean(this.field_33_docinfo4, value);
    }

    @Internal
    public boolean isFSupressSpdfAfterPageBreak() {
        return fSupressSpdfAfterPageBreak.isSet(this.field_33_docinfo4);
    }

    @Internal
    public void setFWrapTrailSpaces(boolean value) {
        this.field_33_docinfo4 = fWrapTrailSpaces.setBoolean(this.field_33_docinfo4, value);
    }

    @Internal
    public boolean isFWrapTrailSpaces() {
        return fWrapTrailSpaces.isSet(this.field_33_docinfo4);
    }

    @Internal
    public void setFMapPrintTextColor(boolean value) {
        this.field_33_docinfo4 = fMapPrintTextColor.setBoolean(this.field_33_docinfo4, value);
    }

    @Internal
    public boolean isFMapPrintTextColor() {
        return fMapPrintTextColor.isSet(this.field_33_docinfo4);
    }

    @Internal
    public void setFNoColumnBalance(boolean value) {
        this.field_33_docinfo4 = fNoColumnBalance.setBoolean(this.field_33_docinfo4, value);
    }

    @Internal
    public boolean isFNoColumnBalance() {
        return fNoColumnBalance.isSet(this.field_33_docinfo4);
    }

    @Internal
    public void setFConvMailMergeEsc(boolean value) {
        this.field_33_docinfo4 = fConvMailMergeEsc.setBoolean(this.field_33_docinfo4, value);
    }

    @Internal
    public boolean isFConvMailMergeEsc() {
        return fConvMailMergeEsc.isSet(this.field_33_docinfo4);
    }

    @Internal
    public void setFSupressTopSpacing(boolean value) {
        this.field_33_docinfo4 = fSupressTopSpacing.setBoolean(this.field_33_docinfo4, value);
    }

    @Internal
    public boolean isFSupressTopSpacing() {
        return fSupressTopSpacing.isSet(this.field_33_docinfo4);
    }

    @Internal
    public void setFOrigWordTableRules(boolean value) {
        this.field_33_docinfo4 = fOrigWordTableRules.setBoolean(this.field_33_docinfo4, value);
    }

    @Internal
    public boolean isFOrigWordTableRules() {
        return fOrigWordTableRules.isSet(this.field_33_docinfo4);
    }

    @Internal
    public void setFTransparentMetafiles(boolean value) {
        this.field_33_docinfo4 = fTransparentMetafiles.setBoolean(this.field_33_docinfo4, value);
    }

    @Internal
    public boolean isFTransparentMetafiles() {
        return fTransparentMetafiles.isSet(this.field_33_docinfo4);
    }

    @Internal
    public void setFShowBreaksInFrames(boolean value) {
        this.field_33_docinfo4 = fShowBreaksInFrames.setBoolean(this.field_33_docinfo4, value);
    }

    @Internal
    public boolean isFShowBreaksInFrames() {
        return fShowBreaksInFrames.isSet(this.field_33_docinfo4);
    }

    @Internal
    public void setFSwapBordersFacingPgs(boolean value) {
        this.field_33_docinfo4 = fSwapBordersFacingPgs.setBoolean(this.field_33_docinfo4, value);
    }

    @Internal
    public boolean isFSwapBordersFacingPgs() {
        return fSwapBordersFacingPgs.isSet(this.field_33_docinfo4);
    }

    @Internal
    public void setFSuppressTopSPacingMac5(boolean value) {
        this.field_33_docinfo4 = fSuppressTopSPacingMac5.setBoolean(this.field_33_docinfo4, value);
    }

    @Internal
    public boolean isFSuppressTopSPacingMac5() {
        return fSuppressTopSPacingMac5.isSet(this.field_33_docinfo4);
    }

    @Internal
    public void setFTruncDxaExpand(boolean value) {
        this.field_33_docinfo4 = fTruncDxaExpand.setBoolean(this.field_33_docinfo4, value);
    }

    @Internal
    public boolean isFTruncDxaExpand() {
        return fTruncDxaExpand.isSet(this.field_33_docinfo4);
    }

    @Internal
    public void setFPrintBodyBeforeHdr(boolean value) {
        this.field_33_docinfo4 = fPrintBodyBeforeHdr.setBoolean(this.field_33_docinfo4, value);
    }

    @Internal
    public boolean isFPrintBodyBeforeHdr() {
        return fPrintBodyBeforeHdr.isSet(this.field_33_docinfo4);
    }

    @Internal
    public void setFNoLeading(boolean value) {
        this.field_33_docinfo4 = fNoLeading.setBoolean(this.field_33_docinfo4, value);
    }

    @Internal
    public boolean isFNoLeading() {
        return fNoLeading.isSet(this.field_33_docinfo4);
    }

    @Internal
    public void setFMWSmallCaps(boolean value) {
        this.field_33_docinfo4 = fMWSmallCaps.setBoolean(this.field_33_docinfo4, value);
    }

    @Internal
    public boolean isFMWSmallCaps() {
        return fMWSmallCaps.isSet(this.field_33_docinfo4);
    }

    @Internal
    public void setLvl(byte value) {
        this.field_37_docinfo5 = (short)lvl.setValue(this.field_37_docinfo5, value);
    }

    @Internal
    public byte getLvl() {
        return (byte)lvl.getValue(this.field_37_docinfo5);
    }

    @Internal
    public void setFGramAllDone(boolean value) {
        this.field_37_docinfo5 = (short)fGramAllDone.setBoolean(this.field_37_docinfo5, value);
    }

    @Internal
    public boolean isFGramAllDone() {
        return fGramAllDone.isSet(this.field_37_docinfo5);
    }

    @Internal
    public void setFGramAllClean(boolean value) {
        this.field_37_docinfo5 = (short)fGramAllClean.setBoolean(this.field_37_docinfo5, value);
    }

    @Internal
    public boolean isFGramAllClean() {
        return fGramAllClean.isSet(this.field_37_docinfo5);
    }

    @Internal
    public void setFSubsetFonts(boolean value) {
        this.field_37_docinfo5 = (short)fSubsetFonts.setBoolean(this.field_37_docinfo5, value);
    }

    @Internal
    public boolean isFSubsetFonts() {
        return fSubsetFonts.isSet(this.field_37_docinfo5);
    }

    @Internal
    public void setFHideLastVersion(boolean value) {
        this.field_37_docinfo5 = (short)fHideLastVersion.setBoolean(this.field_37_docinfo5, value);
    }

    @Internal
    public boolean isFHideLastVersion() {
        return fHideLastVersion.isSet(this.field_37_docinfo5);
    }

    @Internal
    public void setFHtmlDoc(boolean value) {
        this.field_37_docinfo5 = (short)fHtmlDoc.setBoolean(this.field_37_docinfo5, value);
    }

    @Internal
    public boolean isFHtmlDoc() {
        return fHtmlDoc.isSet(this.field_37_docinfo5);
    }

    @Internal
    public void setFSnapBorder(boolean value) {
        this.field_37_docinfo5 = (short)fSnapBorder.setBoolean(this.field_37_docinfo5, value);
    }

    @Internal
    public boolean isFSnapBorder() {
        return fSnapBorder.isSet(this.field_37_docinfo5);
    }

    @Internal
    public void setFIncludeHeader(boolean value) {
        this.field_37_docinfo5 = (short)fIncludeHeader.setBoolean(this.field_37_docinfo5, value);
    }

    @Internal
    public boolean isFIncludeHeader() {
        return fIncludeHeader.isSet(this.field_37_docinfo5);
    }

    @Internal
    public void setFIncludeFooter(boolean value) {
        this.field_37_docinfo5 = (short)fIncludeFooter.setBoolean(this.field_37_docinfo5, value);
    }

    @Internal
    public boolean isFIncludeFooter() {
        return fIncludeFooter.isSet(this.field_37_docinfo5);
    }

    @Internal
    public void setFForcePageSizePag(boolean value) {
        this.field_37_docinfo5 = (short)fForcePageSizePag.setBoolean(this.field_37_docinfo5, value);
    }

    @Internal
    public boolean isFForcePageSizePag() {
        return fForcePageSizePag.isSet(this.field_37_docinfo5);
    }

    @Internal
    public void setFMinFontSizePag(boolean value) {
        this.field_37_docinfo5 = (short)fMinFontSizePag.setBoolean(this.field_37_docinfo5, value);
    }

    @Internal
    public boolean isFMinFontSizePag() {
        return fMinFontSizePag.isSet(this.field_37_docinfo5);
    }

    @Internal
    public void setFHaveVersions(boolean value) {
        this.field_38_docinfo6 = (short)fHaveVersions.setBoolean(this.field_38_docinfo6, value);
    }

    @Internal
    public boolean isFHaveVersions() {
        return fHaveVersions.isSet(this.field_38_docinfo6);
    }

    @Internal
    public void setFAutoVersions(boolean value) {
        this.field_38_docinfo6 = (short)fAutoVersions.setBoolean(this.field_38_docinfo6, value);
    }

    @Internal
    public boolean isFAutoVersions() {
        return fAutoVersions.isSet(this.field_38_docinfo6);
    }

    @Internal
    public void setFVirusPrompted(boolean value) {
        this.field_43_virusinfo = fVirusPrompted.setBoolean(this.field_43_virusinfo, value);
    }

    @Internal
    public boolean isFVirusPrompted() {
        return fVirusPrompted.isSet(this.field_43_virusinfo);
    }

    @Internal
    public void setFVirusLoadSafe(boolean value) {
        this.field_43_virusinfo = fVirusLoadSafe.setBoolean(this.field_43_virusinfo, value);
    }

    @Internal
    public boolean isFVirusLoadSafe() {
        return fVirusLoadSafe.isSet(this.field_43_virusinfo);
    }

    @Internal
    public void setKeyVirusSession30(int value) {
        this.field_43_virusinfo = KeyVirusSession30.setValue(this.field_43_virusinfo, value);
    }

    @Internal
    public int getKeyVirusSession30() {
        return KeyVirusSession30.getValue(this.field_43_virusinfo);
    }
}


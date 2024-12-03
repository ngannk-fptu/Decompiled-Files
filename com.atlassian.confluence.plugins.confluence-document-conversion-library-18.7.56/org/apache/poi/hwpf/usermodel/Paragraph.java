/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.usermodel;

import java.util.NoSuchElementException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.common.Duplicatable;
import org.apache.poi.hwpf.HWPFDocumentCore;
import org.apache.poi.hwpf.model.LFO;
import org.apache.poi.hwpf.model.ListLevel;
import org.apache.poi.hwpf.model.ListTables;
import org.apache.poi.hwpf.model.PAPX;
import org.apache.poi.hwpf.model.StyleSheet;
import org.apache.poi.hwpf.sprm.ParagraphSprmUncompressor;
import org.apache.poi.hwpf.sprm.SprmBuffer;
import org.apache.poi.hwpf.sprm.TableSprmCompressor;
import org.apache.poi.hwpf.usermodel.BorderCode;
import org.apache.poi.hwpf.usermodel.DropCapSpecifier;
import org.apache.poi.hwpf.usermodel.HWPFList;
import org.apache.poi.hwpf.usermodel.LineSpacingDescriptor;
import org.apache.poi.hwpf.usermodel.ListEntry;
import org.apache.poi.hwpf.usermodel.ParagraphProperties;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.hwpf.usermodel.ShadingDescriptor;
import org.apache.poi.hwpf.usermodel.TableProperties;
import org.apache.poi.util.Internal;

public class Paragraph
extends Range
implements Duplicatable {
    private static final Logger LOGGER = LogManager.getLogger(Paragraph.class);
    public static final short SPRM_JC = 9219;
    public static final short SPRM_FSIDEBYSIDE = 9220;
    public static final short SPRM_FKEEP = 9221;
    public static final short SPRM_FKEEPFOLLOW = 9222;
    public static final short SPRM_FPAGEBREAKBEFORE = 9223;
    public static final short SPRM_BRCL = 9224;
    public static final short SPRM_BRCP = 9225;
    public static final short SPRM_ILVL = 9738;
    public static final short SPRM_ILFO = 17931;
    public static final short SPRM_FNOLINENUMB = 9228;
    public static final short SPRM_CHGTABSPAPX = -14835;
    public static final short SPRM_DXARIGHT = -31730;
    public static final short SPRM_DXALEFT = -31729;
    public static final short SPRM_DXALEFT1 = -31727;
    public static final short SPRM_DYALINE = 25618;
    public static final short SPRM_DYABEFORE = -23533;
    public static final short SPRM_DYAAFTER = -23532;
    public static final short SPRM_CHGTABS = -14827;
    public static final short SPRM_FINTABLE = 9238;
    public static final short SPRM_FTTP = 9239;
    public static final short SPRM_DXAABS = -31720;
    public static final short SPRM_DYAABS = -31719;
    public static final short SPRM_DXAWIDTH = -31718;
    public static final short SPRM_PC = 9755;
    public static final short SPRM_WR = 9251;
    public static final short SPRM_BRCTOP = 25636;
    public static final short SPRM_BRCLEFT = 25637;
    public static final short SPRM_BRCBOTTOM = 25638;
    public static final short SPRM_BRCRIGHT = 25639;
    public static final short SPRM_BRCBAR = 26153;
    public static final short SPRM_FNOAUTOHYPH = 9258;
    public static final short SPRM_WHEIGHTABS = 17451;
    public static final short SPRM_DCS = 17452;
    public static final short SPRM_SHD80 = 17453;
    public static final short SPRM_SHD = -14771;
    public static final short SPRM_DYAFROMTEXT = -31698;
    public static final short SPRM_DXAFROMTEXT = -31697;
    public static final short SPRM_FLOCKED = 9264;
    public static final short SPRM_FWIDOWCONTROL = 9265;
    public static final short SPRM_RULER = -14798;
    public static final short SPRM_FKINSOKU = 9267;
    public static final short SPRM_FWORDWRAP = 9268;
    public static final short SPRM_FOVERFLOWPUNCT = 9269;
    public static final short SPRM_FTOPLINEPUNCT = 9270;
    public static final short SPRM_AUTOSPACEDE = 9271;
    public static final short SPRM_AUTOSPACEDN = 9272;
    public static final short SPRM_WALIGNFONT = 17465;
    public static final short SPRM_FRAMETEXTFLOW = 17466;
    public static final short SPRM_ANLD = -14786;
    public static final short SPRM_PROPRMARK = -14785;
    public static final short SPRM_OUTLVL = 9792;
    public static final short SPRM_FBIDI = 9281;
    public static final short SPRM_FNUMRMLNS = 9283;
    public static final short SPRM_CRLF = 9284;
    public static final short SPRM_NUMRM = -14779;
    public static final short SPRM_USEPGSUSETTINGS = 9287;
    public static final short SPRM_FADJUSTRIGHT = 9288;
    protected short _istd;
    protected ParagraphProperties _props;
    protected SprmBuffer _papx;

    @Internal
    public static Paragraph newParagraph(Range parent, PAPX papx) {
        HWPFDocumentCore doc = parent._doc;
        ListTables listTables = doc.getListTables();
        StyleSheet styleSheet = doc.getStyleSheet();
        ParagraphProperties properties = new ParagraphProperties();
        properties.setIstd(papx.getIstd());
        properties = Paragraph.newParagraph_applyStyleProperties(styleSheet, papx, properties);
        properties = ParagraphSprmUncompressor.uncompressPAP(properties, papx.getGrpprl(), 2);
        if (properties.getIlfo() != 0 && listTables != null) {
            ListLevel listLevel;
            LFO lfo = null;
            try {
                lfo = listTables.getLfo(properties.getIlfo());
            }
            catch (NoSuchElementException exc) {
                LOGGER.atWarn().log("Paragraph refers to LFO #{} that does not exists", (Object)Unbox.box(properties.getIlfo()));
            }
            if (lfo != null && (listLevel = listTables.getLevel(lfo.getLsid(), properties.getIlvl())) != null && listLevel.getGrpprlPapx() != null) {
                properties = ParagraphSprmUncompressor.uncompressPAP(properties, listLevel.getGrpprlPapx(), 0);
                properties = Paragraph.newParagraph_applyStyleProperties(styleSheet, papx, properties);
                properties = ParagraphSprmUncompressor.uncompressPAP(properties, papx.getGrpprl(), 2);
            }
        }
        if (properties.getIlfo() > 0) {
            return new ListEntry(papx, properties, parent);
        }
        return new Paragraph(papx, properties, parent);
    }

    protected static ParagraphProperties newParagraph_applyStyleProperties(StyleSheet styleSheet, PAPX papx, ParagraphProperties properties) {
        if (styleSheet == null) {
            return properties;
        }
        short style = papx.getIstd();
        byte[] grpprl = styleSheet.getPAPX(style);
        return ParagraphSprmUncompressor.uncompressPAP(properties, grpprl, 2);
    }

    @Internal
    Paragraph(PAPX papx, ParagraphProperties properties, Range parent) {
        super(Math.max(parent._start, papx.getStart()), Math.min(parent._end, papx.getEnd()), parent);
        this._props = properties;
        this._papx = papx.getSprmBuf();
        this._istd = papx.getIstd();
    }

    Paragraph(Paragraph other) {
        super(other);
        this._istd = other._istd;
        this._props = other._props == null ? null : other._props.copy();
        this._papx = other._papx == null ? null : other._papx.copy();
    }

    public short getStyleIndex() {
        return this._istd;
    }

    public boolean isInTable() {
        return this._props.getFInTable();
    }

    public boolean isTableRowEnd() {
        return this._props.getFTtp() || this._props.getFTtpEmbedded();
    }

    public int getTableLevel() {
        return this._props.getItap();
    }

    public boolean isEmbeddedCellMark() {
        return this._props.getFInnerTableCell();
    }

    public int getJustification() {
        return this._props.getJc();
    }

    public void setJustification(byte jc) {
        this._props.setJc(jc);
        this._papx.updateSprm((short)9219, jc);
    }

    public boolean keepOnPage() {
        return this._props.getFKeep();
    }

    public void setKeepOnPage(boolean fKeep) {
        this._props.setFKeep(fKeep);
        this._papx.updateSprm((short)9221, fKeep);
    }

    public boolean keepWithNext() {
        return this._props.getFKeepFollow();
    }

    public void setKeepWithNext(boolean fKeepFollow) {
        this._props.setFKeepFollow(fKeepFollow);
        this._papx.updateSprm((short)9222, fKeepFollow);
    }

    public boolean pageBreakBefore() {
        return this._props.getFPageBreakBefore();
    }

    public void setPageBreakBefore(boolean fPageBreak) {
        this._props.setFPageBreakBefore(fPageBreak);
        this._papx.updateSprm((short)9223, fPageBreak);
    }

    public boolean isLineNotNumbered() {
        return this._props.getFNoLnn();
    }

    public void setLineNotNumbered(boolean fNoLnn) {
        this._props.setFNoLnn(fNoLnn);
        this._papx.updateSprm((short)9228, fNoLnn);
    }

    public boolean isSideBySide() {
        return this._props.getFSideBySide();
    }

    public void setSideBySide(boolean fSideBySide) {
        this._props.setFSideBySide(fSideBySide);
        this._papx.updateSprm((short)9220, fSideBySide);
    }

    public boolean isAutoHyphenated() {
        return !this._props.getFNoAutoHyph();
    }

    public void setAutoHyphenated(boolean autoHyph) {
        this._props.setFNoAutoHyph(!autoHyph);
        this._papx.updateSprm((short)9258, !autoHyph);
    }

    public boolean isWidowControlled() {
        return this._props.getFWidowControl();
    }

    public void setWidowControl(boolean widowControl) {
        this._props.setFWidowControl(widowControl);
        this._papx.updateSprm((short)9265, widowControl);
    }

    public int getIndentFromRight() {
        return this._props.getDxaRight();
    }

    public void setIndentFromRight(int dxaRight) {
        this._props.setDxaRight(dxaRight);
        this._papx.updateSprm((short)-31730, (short)dxaRight);
    }

    public int getIndentFromLeft() {
        return this._props.getDxaLeft();
    }

    public void setIndentFromLeft(int dxaLeft) {
        this._props.setDxaLeft(dxaLeft);
        this._papx.updateSprm((short)-31729, (short)dxaLeft);
    }

    public int getFirstLineIndent() {
        return this._props.getDxaLeft1();
    }

    public void setFirstLineIndent(int first) {
        this._props.setDxaLeft1(first);
        this._papx.updateSprm((short)-31727, (short)first);
    }

    public LineSpacingDescriptor getLineSpacing() {
        return this._props.getLspd();
    }

    public void setLineSpacing(LineSpacingDescriptor lspd) {
        this._props.setLspd(lspd);
        this._papx.updateSprm((short)25618, lspd.toInt());
    }

    public int getSpacingBefore() {
        return this._props.getDyaBefore();
    }

    public void setSpacingBefore(int before) {
        this._props.setDyaBefore(before);
        this._papx.updateSprm((short)-23533, (short)before);
    }

    public int getSpacingAfter() {
        return this._props.getDyaAfter();
    }

    public void setSpacingAfter(int after) {
        this._props.setDyaAfter(after);
        this._papx.updateSprm((short)-23532, (short)after);
    }

    public boolean isKinsoku() {
        return this._props.getFKinsoku();
    }

    public void setKinsoku(boolean kinsoku) {
        this._props.setFKinsoku(kinsoku);
        this._papx.updateSprm((short)9267, kinsoku);
    }

    public boolean isWordWrapped() {
        return this._props.getFWordWrap();
    }

    public void setWordWrapped(boolean wrap) {
        this._props.setFWordWrap(wrap);
        this._papx.updateSprm((short)9268, wrap);
    }

    public int getFontAlignment() {
        return this._props.getWAlignFont();
    }

    public void setFontAlignment(int align) {
        this._props.setWAlignFont(align);
        this._papx.updateSprm((short)17465, (short)align);
    }

    public boolean isVertical() {
        return this._props.isFVertical();
    }

    public void setVertical(boolean vertical) {
        this._props.setFVertical(vertical);
        this._papx.updateSprm((short)17466, this.getFrameTextFlow());
    }

    public boolean isBackward() {
        return this._props.isFBackward();
    }

    public void setBackward(boolean bward) {
        this._props.setFBackward(bward);
        this._papx.updateSprm((short)17466, this.getFrameTextFlow());
    }

    public BorderCode getTopBorder() {
        return this._props.getBrcTop();
    }

    public void setTopBorder(BorderCode top) {
        this._props.setBrcTop(top);
        this._papx.updateSprm((short)25636, top.toInt());
    }

    public BorderCode getLeftBorder() {
        return this._props.getBrcLeft();
    }

    public void setLeftBorder(BorderCode left) {
        this._props.setBrcLeft(left);
        this._papx.updateSprm((short)25637, left.toInt());
    }

    public BorderCode getBottomBorder() {
        return this._props.getBrcBottom();
    }

    public void setBottomBorder(BorderCode bottom) {
        this._props.setBrcBottom(bottom);
        this._papx.updateSprm((short)25638, bottom.toInt());
    }

    public BorderCode getRightBorder() {
        return this._props.getBrcRight();
    }

    public void setRightBorder(BorderCode right) {
        this._props.setBrcRight(right);
        this._papx.updateSprm((short)25639, right.toInt());
    }

    public BorderCode getBarBorder() {
        return this._props.getBrcBar();
    }

    public void setBarBorder(BorderCode bar) {
        this._props.setBrcBar(bar);
        this._papx.updateSprm((short)26153, bar.toInt());
    }

    public ShadingDescriptor getShading() {
        return this._props.getShd();
    }

    public void setShading(ShadingDescriptor shd) {
        this._props.setShd(shd);
        this._papx.addSprm((short)-14771, shd.serialize());
    }

    public DropCapSpecifier getDropCap() {
        return this._props.getDcs();
    }

    public void setDropCap(DropCapSpecifier dcs) {
        this._props.setDcs(dcs);
        this._papx.updateSprm((short)17452, dcs.toShort());
    }

    public int getIlfo() {
        return this._props.getIlfo();
    }

    public int getIlvl() {
        return this._props.getIlvl();
    }

    public int getLvl() {
        return this._props.getLvl();
    }

    void setTableRowEnd(TableProperties props) {
        this.setTableRowEnd(true);
        byte[] grpprl = TableSprmCompressor.compressTableProperty(props);
        this._papx.append(grpprl);
    }

    private void setTableRowEnd(boolean val) {
        this._props.setFTtp(val);
        this._papx.updateSprm((short)9239, val);
    }

    public int getTabStopsNumber() {
        return this._props.getItbdMac();
    }

    public int[] getTabStopsPositions() {
        return this._props.getRgdxaTab();
    }

    public HWPFList getList() {
        if (this.getIlfo() == 0 || this.getIlfo() == 63489) {
            throw new IllegalStateException("Paragraph not in list");
        }
        return new HWPFList(this.getDocument().getStyleSheet(), this.getDocument().getListTables(), this.getIlfo());
    }

    public boolean isInList() {
        return this.getIlfo() != 0 && this.getIlfo() != 63489;
    }

    public ParagraphProperties cloneProperties() {
        return this._props.copy();
    }

    @Override
    public Paragraph copy() {
        return new Paragraph(this);
    }

    private short getFrameTextFlow() {
        short retVal = 0;
        if (this._props.isFVertical()) {
            retVal = (short)(retVal | 1);
        }
        if (this._props.isFBackward()) {
            retVal = (short)(retVal | 2);
        }
        if (this._props.isFRotateFont()) {
            retVal = (short)(retVal | 4);
        }
        return retVal;
    }

    @Override
    public String toString() {
        return "Paragraph [" + this.getStartOffset() + "; " + this.getEndOffset() + ")";
    }

    @Internal
    public ParagraphProperties getProps() {
        return this._props;
    }
}


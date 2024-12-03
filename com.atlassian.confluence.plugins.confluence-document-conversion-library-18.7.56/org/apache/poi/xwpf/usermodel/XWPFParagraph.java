/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xwpf.usermodel;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.ooxml.util.POIXMLUnits;
import org.apache.poi.util.Internal;
import org.apache.poi.util.Units;
import org.apache.poi.wp.usermodel.Paragraph;
import org.apache.poi.xwpf.usermodel.BodyElementType;
import org.apache.poi.xwpf.usermodel.BodyType;
import org.apache.poi.xwpf.usermodel.Borders;
import org.apache.poi.xwpf.usermodel.IBody;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.IRunBody;
import org.apache.poi.xwpf.usermodel.IRunElement;
import org.apache.poi.xwpf.usermodel.ISDTContents;
import org.apache.poi.xwpf.usermodel.LineSpacingRule;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.PositionInParagraph;
import org.apache.poi.xwpf.usermodel.TextAlignment;
import org.apache.poi.xwpf.usermodel.TextSegment;
import org.apache.poi.xwpf.usermodel.XWPFAbstractFootnoteEndnote;
import org.apache.poi.xwpf.usermodel.XWPFAbstractNum;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFEndnote;
import org.apache.poi.xwpf.usermodel.XWPFFieldRun;
import org.apache.poi.xwpf.usermodel.XWPFHyperlinkRun;
import org.apache.poi.xwpf.usermodel.XWPFNum;
import org.apache.poi.xwpf.usermodel.XWPFNumbering;
import org.apache.poi.xwpf.usermodel.XWPFRelation;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFSDT;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STOnOff1;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTAbstractNum;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFtnEdnRef;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHyperlink;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTInd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLvl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNum;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNumLvl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPBdr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTProofErr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRunTrackChange;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtBlock;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSimpleField;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSmartTagRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSpacing;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTString;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTextAlignment;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STLineSpacingRule;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTextAlignment;

public class XWPFParagraph
implements IBodyElement,
IRunBody,
ISDTContents,
Paragraph {
    private final CTP paragraph;
    protected IBody part;
    protected XWPFDocument document;
    protected List<XWPFRun> runs;
    protected List<IRunElement> iruns;
    private final StringBuilder footnoteText = new StringBuilder(64);

    public XWPFParagraph(CTP prgrph, IBody part) {
        this.paragraph = prgrph;
        this.part = part;
        this.document = part.getXWPFDocument();
        if (this.document == null) {
            throw new NullPointerException();
        }
        this.runs = new ArrayList<XWPFRun>();
        this.iruns = new ArrayList<IRunElement>();
        this.buildRunsInOrderFromXml(this.paragraph);
        for (XWPFRun run : this.runs) {
            CTR r = run.getCTR();
            XmlCursor c = r.newCursor();
            Throwable throwable = null;
            try {
                c.selectPath("child::*");
                while (c.toNextSelection()) {
                    XWPFAbstractFootnoteEndnote footnote;
                    XmlObject o = c.getObject();
                    if (!(o instanceof CTFtnEdnRef)) continue;
                    CTFtnEdnRef ftn = (CTFtnEdnRef)o;
                    this.footnoteText.append(" [").append(ftn.getId()).append(": ");
                    XWPFAbstractFootnoteEndnote xWPFAbstractFootnoteEndnote = footnote = ftn.getDomNode().getLocalName().equals("footnoteReference") ? this.document.getFootnoteByID(ftn.getId().intValue()) : this.document.getEndnoteByID(ftn.getId().intValue());
                    if (null != footnote) {
                        boolean first = true;
                        for (XWPFParagraph p : footnote.getParagraphs()) {
                            if (!first) {
                                this.footnoteText.append("\n");
                            }
                            first = false;
                            this.footnoteText.append(p.getText());
                        }
                    } else {
                        this.footnoteText.append("!!! End note with ID \"").append(ftn.getId()).append("\" not found in document.");
                    }
                    this.footnoteText.append("] ");
                }
            }
            catch (Throwable throwable2) {
                throwable = throwable2;
                throw throwable2;
            }
            finally {
                if (c == null) continue;
                if (throwable != null) {
                    try {
                        c.close();
                    }
                    catch (Throwable throwable3) {
                        throwable.addSuppressed(throwable3);
                    }
                    continue;
                }
                c.close();
            }
        }
    }

    /*
     * Could not resolve type clashes
     */
    private void buildRunsInOrderFromXml(XmlObject object) {
        try (XmlCursor c = object.newCursor();){
            c.selectPath("child::*");
            while (c.toNextSelection()) {
                XWPFSDT cc;
                CTR r;
                int n;
                int n2;
                CTR[] cTRArray;
                XmlObject o = c.getObject();
                if (o instanceof CTR) {
                    XWPFRun r2 = new XWPFRun((CTR)o, this);
                    this.runs.add(r2);
                    this.iruns.add(r2);
                }
                if (o instanceof CTHyperlink) {
                    CTHyperlink link = (CTHyperlink)o;
                    cTRArray = link.getRArray();
                    n2 = cTRArray.length;
                    for (n = 0; n < n2; ++n) {
                        r = cTRArray[n];
                        XWPFHyperlinkRun hr = new XWPFHyperlinkRun(link, r, this);
                        this.runs.add(hr);
                        this.iruns.add(hr);
                    }
                }
                if (o instanceof CTSimpleField) {
                    CTSimpleField field = (CTSimpleField)o;
                    cTRArray = field.getRArray();
                    n2 = cTRArray.length;
                    for (n = 0; n < n2; ++n) {
                        r = cTRArray[n];
                        XWPFFieldRun fr = new XWPFFieldRun(field, r, this);
                        this.runs.add(fr);
                        this.iruns.add(fr);
                    }
                }
                if (o instanceof CTSdtBlock) {
                    cc = new XWPFSDT((CTSdtBlock)o, this.part);
                    this.iruns.add(cc);
                }
                if (o instanceof CTSdtRun) {
                    cc = new XWPFSDT((CTSdtRun)o, this.part);
                    this.iruns.add(cc);
                }
                if (o instanceof CTRunTrackChange) {
                    for (XmlObject r3 : ((CTRunTrackChange)o).getRArray()) {
                        XWPFRun cr = new XWPFRun((CTR)r3, this);
                        this.runs.add(cr);
                        this.iruns.add(cr);
                    }
                }
                if (o instanceof CTSmartTagRun) {
                    this.buildRunsInOrderFromXml(o);
                }
                if (!(o instanceof CTRunTrackChange)) continue;
                for (XmlObject change : ((CTRunTrackChange)o).getInsArray()) {
                    this.buildRunsInOrderFromXml(change);
                }
            }
        }
    }

    @Internal
    public CTP getCTP() {
        return this.paragraph;
    }

    public List<XWPFRun> getRuns() {
        return Collections.unmodifiableList(this.runs);
    }

    public boolean runsIsEmpty() {
        return this.runs.isEmpty();
    }

    public List<IRunElement> getIRuns() {
        return Collections.unmodifiableList(this.iruns);
    }

    public boolean isEmpty() {
        return !this.paragraph.getDomNode().hasChildNodes();
    }

    @Override
    public XWPFDocument getDocument() {
        return this.document;
    }

    public String getText() {
        StringBuilder out = new StringBuilder(64);
        for (IRunElement run : this.iruns) {
            if (run instanceof XWPFRun) {
                XWPFRun xRun = (XWPFRun)run;
                if (xRun.getCTR().getDelTextArray().length != 0) continue;
                out.append(xRun);
                continue;
            }
            if (run instanceof XWPFSDT) {
                out.append(((XWPFSDT)run).getContent().getText());
                continue;
            }
            out.append(run);
        }
        out.append((CharSequence)this.footnoteText);
        return out.toString();
    }

    public String getStyleID() {
        if (this.paragraph.getPPr() != null && this.paragraph.getPPr().getPStyle() != null && this.paragraph.getPPr().getPStyle().getVal() != null) {
            return this.paragraph.getPPr().getPStyle().getVal();
        }
        return null;
    }

    public BigInteger getNumID() {
        if (this.paragraph.getPPr() != null && this.paragraph.getPPr().getNumPr() != null && this.paragraph.getPPr().getNumPr().getNumId() != null) {
            return this.paragraph.getPPr().getNumPr().getNumId().getVal();
        }
        return null;
    }

    public void setNumID(BigInteger numPos) {
        if (this.paragraph.getPPr() == null) {
            this.paragraph.addNewPPr();
        }
        if (this.paragraph.getPPr().getNumPr() == null) {
            this.paragraph.getPPr().addNewNumPr();
        }
        if (this.paragraph.getPPr().getNumPr().getNumId() == null) {
            this.paragraph.getPPr().getNumPr().addNewNumId();
        }
        this.paragraph.getPPr().getNumPr().getNumId().setVal(numPos);
    }

    public void setNumILvl(BigInteger iLvl) {
        if (this.paragraph.getPPr() == null) {
            this.paragraph.addNewPPr();
        }
        if (this.paragraph.getPPr().getNumPr() == null) {
            this.paragraph.getPPr().addNewNumPr();
        }
        if (this.paragraph.getPPr().getNumPr().getIlvl() == null) {
            this.paragraph.getPPr().getNumPr().addNewIlvl();
        }
        this.paragraph.getPPr().getNumPr().getIlvl().setVal(iLvl);
    }

    public BigInteger getNumIlvl() {
        if (this.paragraph.getPPr() != null && this.paragraph.getPPr().getNumPr() != null && this.paragraph.getPPr().getNumPr().getIlvl() != null) {
            return this.paragraph.getPPr().getNumPr().getIlvl().getVal();
        }
        return null;
    }

    public String getNumFmt() {
        XWPFNum num;
        BigInteger numID = this.getNumID();
        XWPFNumbering numbering = this.document.getNumbering();
        if (numID != null && numbering != null && (num = numbering.getNum(numID)) != null) {
            BigInteger ilvl = this.getNumIlvl();
            BigInteger abstractNumId = num.getCTNum().getAbstractNumId().getVal();
            CTAbstractNum anum = numbering.getAbstractNum(abstractNumId).getAbstractNum();
            CTLvl level = null;
            for (int i = 0; i < anum.sizeOfLvlArray(); ++i) {
                CTLvl lvl = anum.getLvlArray(i);
                if (!lvl.getIlvl().equals(ilvl)) continue;
                level = lvl;
                break;
            }
            if (level != null && level.getNumFmt() != null && level.getNumFmt().getVal() != null) {
                return level.getNumFmt().getVal().toString();
            }
        }
        return null;
    }

    public String getNumLevelText() {
        XWPFNum num;
        BigInteger numID = this.getNumID();
        XWPFNumbering numbering = this.document.getNumbering();
        if (numID != null && numbering != null && (num = numbering.getNum(numID)) != null) {
            BigInteger ilvl = this.getNumIlvl();
            CTNum ctNum = num.getCTNum();
            if (ctNum == null) {
                return null;
            }
            CTDecimalNumber ctDecimalNumber = ctNum.getAbstractNumId();
            if (ctDecimalNumber == null) {
                return null;
            }
            BigInteger abstractNumId = ctDecimalNumber.getVal();
            if (abstractNumId == null) {
                return null;
            }
            XWPFAbstractNum xwpfAbstractNum = numbering.getAbstractNum(abstractNumId);
            if (xwpfAbstractNum == null) {
                return null;
            }
            CTAbstractNum anum = xwpfAbstractNum.getCTAbstractNum();
            if (anum == null) {
                return null;
            }
            CTLvl level = null;
            for (int i = 0; i < anum.sizeOfLvlArray(); ++i) {
                CTLvl lvl = anum.getLvlArray(i);
                if (lvl == null || lvl.getIlvl() == null || !lvl.getIlvl().equals(ilvl)) continue;
                level = lvl;
                break;
            }
            if (level != null && level.getLvlText() != null && level.getLvlText().getVal() != null) {
                return level.getLvlText().getVal();
            }
        }
        return null;
    }

    public BigInteger getNumStartOverride() {
        XWPFNum num;
        BigInteger numID = this.getNumID();
        XWPFNumbering numbering = this.document.getNumbering();
        if (numID != null && numbering != null && (num = numbering.getNum(numID)) != null) {
            CTNum ctNum = num.getCTNum();
            if (ctNum == null) {
                return null;
            }
            BigInteger ilvl = this.getNumIlvl();
            CTNumLvl level = null;
            for (int i = 0; i < ctNum.sizeOfLvlOverrideArray(); ++i) {
                CTNumLvl ctNumLvl = ctNum.getLvlOverrideArray(i);
                if (ctNumLvl == null || ctNumLvl.getIlvl() == null || !ctNumLvl.getIlvl().equals(ilvl)) continue;
                level = ctNumLvl;
                break;
            }
            if (level != null && level.getStartOverride() != null) {
                return level.getStartOverride().getVal();
            }
        }
        return null;
    }

    public boolean isKeepNext() {
        if (this.getCTP() != null && this.getCTP().getPPr() != null && this.getCTP().getPPr().isSetKeepNext()) {
            return POIXMLUnits.parseOnOff(this.getCTP().getPPr().getKeepNext().xgetVal());
        }
        return false;
    }

    public void setKeepNext(boolean keepNext) {
        CTOnOff state = CTOnOff.Factory.newInstance();
        state.setVal(keepNext ? STOnOff1.ON : STOnOff1.OFF);
        this.getCTP().getPPr().setKeepNext(state);
    }

    public String getParagraphText() {
        StringBuilder out = new StringBuilder(64);
        for (XWPFRun run : this.runs) {
            out.append(run);
        }
        return out.toString();
    }

    public String getPictureText() {
        StringBuilder out = new StringBuilder(64);
        for (XWPFRun run : this.runs) {
            out.append(run.getPictureText());
        }
        return out.toString();
    }

    public String getFootnoteText() {
        return this.footnoteText.toString();
    }

    public ParagraphAlignment getAlignment() {
        CTPPr pr = this.getCTPPr();
        return pr == null || !pr.isSetJc() ? ParagraphAlignment.LEFT : ParagraphAlignment.valueOf(pr.getJc().getVal().intValue());
    }

    public void setAlignment(ParagraphAlignment align) {
        CTPPr pr = this.getCTPPr();
        CTJc jc = pr.isSetJc() ? pr.getJc() : pr.addNewJc();
        STJc.Enum en = STJc.Enum.forInt(align.getValue());
        jc.setVal(en);
    }

    @Override
    public int getFontAlignment() {
        return this.getAlignment().getValue();
    }

    @Override
    public void setFontAlignment(int align) {
        ParagraphAlignment pAlign = ParagraphAlignment.valueOf(align);
        this.setAlignment(pAlign);
    }

    public TextAlignment getVerticalAlignment() {
        CTPPr pr = this.getCTPPr();
        return pr == null || !pr.isSetTextAlignment() ? TextAlignment.AUTO : TextAlignment.valueOf(pr.getTextAlignment().getVal().intValue());
    }

    public void setVerticalAlignment(TextAlignment valign) {
        CTPPr pr = this.getCTPPr();
        CTTextAlignment textAlignment = pr.isSetTextAlignment() ? pr.getTextAlignment() : pr.addNewTextAlignment();
        STTextAlignment.Enum en = STTextAlignment.Enum.forInt(valign.getValue());
        textAlignment.setVal(en);
    }

    public Borders getBorderTop() {
        CTPBdr border = this.getCTPBrd(false);
        CTBorder ct = null;
        if (border != null) {
            ct = border.getTop();
        }
        STBorder.Enum ptrn = ct != null ? ct.getVal() : STBorder.NONE;
        return Borders.valueOf(ptrn.intValue());
    }

    public void setBorderTop(Borders border) {
        CTBorder pr;
        CTPBdr ct = this.getCTPBrd(true);
        if (ct == null) {
            throw new RuntimeException("invalid paragraph state");
        }
        CTBorder cTBorder = pr = ct.isSetTop() ? ct.getTop() : ct.addNewTop();
        if (border.getValue() == Borders.NONE.getValue()) {
            ct.unsetTop();
        } else {
            pr.setVal(STBorder.Enum.forInt(border.getValue()));
        }
    }

    public Borders getBorderBottom() {
        CTPBdr border = this.getCTPBrd(false);
        CTBorder ct = null;
        if (border != null) {
            ct = border.getBottom();
        }
        STBorder.Enum ptrn = ct != null ? ct.getVal() : STBorder.NONE;
        return Borders.valueOf(ptrn.intValue());
    }

    public void setBorderBottom(Borders border) {
        CTBorder pr;
        CTPBdr ct = this.getCTPBrd(true);
        CTBorder cTBorder = pr = ct.isSetBottom() ? ct.getBottom() : ct.addNewBottom();
        if (border.getValue() == Borders.NONE.getValue()) {
            ct.unsetBottom();
        } else {
            pr.setVal(STBorder.Enum.forInt(border.getValue()));
        }
    }

    public Borders getBorderLeft() {
        CTPBdr border = this.getCTPBrd(false);
        CTBorder ct = null;
        if (border != null) {
            ct = border.getLeft();
        }
        STBorder.Enum ptrn = ct != null ? ct.getVal() : STBorder.NONE;
        return Borders.valueOf(ptrn.intValue());
    }

    public void setBorderLeft(Borders border) {
        CTBorder pr;
        CTPBdr ct = this.getCTPBrd(true);
        CTBorder cTBorder = pr = ct.isSetLeft() ? ct.getLeft() : ct.addNewLeft();
        if (border.getValue() == Borders.NONE.getValue()) {
            ct.unsetLeft();
        } else {
            pr.setVal(STBorder.Enum.forInt(border.getValue()));
        }
    }

    public Borders getBorderRight() {
        CTPBdr border = this.getCTPBrd(false);
        CTBorder ct = null;
        if (border != null) {
            ct = border.getRight();
        }
        STBorder.Enum ptrn = ct != null ? ct.getVal() : STBorder.NONE;
        return Borders.valueOf(ptrn.intValue());
    }

    public void setBorderRight(Borders border) {
        CTBorder pr;
        CTPBdr ct = this.getCTPBrd(true);
        CTBorder cTBorder = pr = ct.isSetRight() ? ct.getRight() : ct.addNewRight();
        if (border.getValue() == Borders.NONE.getValue()) {
            ct.unsetRight();
        } else {
            pr.setVal(STBorder.Enum.forInt(border.getValue()));
        }
    }

    public Borders getBorderBetween() {
        CTPBdr border = this.getCTPBrd(false);
        CTBorder ct = null;
        if (border != null) {
            ct = border.getBetween();
        }
        STBorder.Enum ptrn = ct != null ? ct.getVal() : STBorder.NONE;
        return Borders.valueOf(ptrn.intValue());
    }

    public void setBorderBetween(Borders border) {
        CTBorder pr;
        CTPBdr ct = this.getCTPBrd(true);
        CTBorder cTBorder = pr = ct.isSetBetween() ? ct.getBetween() : ct.addNewBetween();
        if (border.getValue() == Borders.NONE.getValue()) {
            ct.unsetBetween();
        } else {
            pr.setVal(STBorder.Enum.forInt(border.getValue()));
        }
    }

    public boolean isPageBreak() {
        CTOnOff ctPageBreak;
        CTPPr ppr = this.getCTPPr();
        CTOnOff cTOnOff = ctPageBreak = ppr.isSetPageBreakBefore() ? ppr.getPageBreakBefore() : null;
        if (ctPageBreak == null) {
            return false;
        }
        return POIXMLUnits.parseOnOff(ctPageBreak.xgetVal());
    }

    public void setPageBreak(boolean pageBreak) {
        CTPPr ppr = this.getCTPPr();
        CTOnOff ctPageBreak = ppr.isSetPageBreakBefore() ? ppr.getPageBreakBefore() : ppr.addNewPageBreakBefore();
        ctPageBreak.setVal(pageBreak ? STOnOff1.ON : STOnOff1.OFF);
    }

    public int getSpacingAfter() {
        CTSpacing spacing = this.getCTSpacing(false);
        return spacing != null && spacing.isSetAfter() ? (int)Units.toDXA(POIXMLUnits.parseLength(spacing.xgetAfter())) : -1;
    }

    public void setSpacingAfter(int spaces) {
        CTSpacing spacing = this.getCTSpacing(true);
        if (spacing != null) {
            BigInteger bi = new BigInteger(Integer.toString(spaces));
            spacing.setAfter(bi);
        }
    }

    public int getSpacingAfterLines() {
        CTSpacing spacing = this.getCTSpacing(false);
        return spacing != null && spacing.isSetAfterLines() ? spacing.getAfterLines().intValue() : -1;
    }

    public void setSpacingAfterLines(int spaces) {
        CTSpacing spacing = this.getCTSpacing(true);
        BigInteger bi = new BigInteger(Integer.toString(spaces));
        spacing.setAfterLines(bi);
    }

    public int getSpacingBefore() {
        CTSpacing spacing = this.getCTSpacing(false);
        return spacing != null && spacing.isSetBefore() ? (int)Units.toDXA(POIXMLUnits.parseLength(spacing.xgetBefore())) : -1;
    }

    public void setSpacingBefore(int spaces) {
        CTSpacing spacing = this.getCTSpacing(true);
        BigInteger bi = new BigInteger(Integer.toString(spaces));
        spacing.setBefore(bi);
    }

    public int getSpacingBeforeLines() {
        CTSpacing spacing = this.getCTSpacing(false);
        return spacing != null && spacing.isSetBeforeLines() ? spacing.getBeforeLines().intValue() : -1;
    }

    public void setSpacingBeforeLines(int spaces) {
        CTSpacing spacing = this.getCTSpacing(true);
        BigInteger bi = new BigInteger(Integer.toString(spaces));
        spacing.setBeforeLines(bi);
    }

    public LineSpacingRule getSpacingLineRule() {
        CTSpacing spacing = this.getCTSpacing(false);
        return spacing != null && spacing.isSetLineRule() ? LineSpacingRule.valueOf(spacing.getLineRule().intValue()) : LineSpacingRule.AUTO;
    }

    public void setSpacingLineRule(LineSpacingRule rule) {
        CTSpacing spacing = this.getCTSpacing(true);
        spacing.setLineRule(STLineSpacingRule.Enum.forInt(rule.getValue()));
    }

    public double getSpacingBetween() {
        CTSpacing spacing = this.getCTSpacing(false);
        if (spacing == null || !spacing.isSetLine()) {
            return -1.0;
        }
        double twips = Units.toDXA(POIXMLUnits.parseLength(spacing.xgetLine()));
        return twips / (double)(spacing.getLineRule() == null || spacing.getLineRule() == STLineSpacingRule.AUTO ? 240 : 20);
    }

    public void setSpacingBetween(double spacing, LineSpacingRule rule) {
        CTSpacing ctSp = this.getCTSpacing(true);
        if (rule == LineSpacingRule.AUTO) {
            ctSp.setLine(new BigInteger(String.valueOf(Math.round(spacing * 240.0))));
        } else {
            ctSp.setLine(new BigInteger(String.valueOf(Math.round(spacing * 20.0))));
        }
        ctSp.setLineRule(STLineSpacingRule.Enum.forInt(rule.getValue()));
    }

    public void setSpacingBetween(double spacing) {
        this.setSpacingBetween(spacing, LineSpacingRule.AUTO);
    }

    public int getIndentationLeft() {
        CTInd indentation = this.getCTInd(false);
        return indentation != null && indentation.isSetLeft() ? (int)Units.toDXA(POIXMLUnits.parseLength(indentation.xgetLeft())) : -1;
    }

    public void setIndentationLeft(int indentation) {
        CTInd indent = this.getCTInd(true);
        BigInteger bi = new BigInteger(Integer.toString(indentation));
        indent.setLeft(bi);
    }

    public int getIndentationLeftChars() {
        CTInd indentation = this.getCTInd(false);
        return indentation != null && indentation.isSetLeftChars() ? indentation.getLeftChars().intValue() : -1;
    }

    public void setIndentationLeftChars(int indentation) {
        CTInd indent = this.getCTInd(true);
        BigInteger bi = new BigInteger(Integer.toString(indentation));
        indent.setLeftChars(bi);
    }

    public int getIndentationRight() {
        CTInd indentation = this.getCTInd(false);
        return indentation != null && indentation.isSetRight() ? (int)Units.toDXA(POIXMLUnits.parseLength(indentation.xgetRight())) : -1;
    }

    public void setIndentationRight(int indentation) {
        CTInd indent = this.getCTInd(true);
        BigInteger bi = new BigInteger(Integer.toString(indentation));
        indent.setRight(bi);
    }

    public int getIndentationRightChars() {
        CTInd indentation = this.getCTInd(false);
        return indentation != null && indentation.isSetRightChars() ? indentation.getRightChars().intValue() : -1;
    }

    public void setIndentationRightChars(int indentation) {
        CTInd indent = this.getCTInd(true);
        BigInteger bi = new BigInteger(Integer.toString(indentation));
        indent.setRightChars(bi);
    }

    public int getIndentationHanging() {
        CTInd indentation = this.getCTInd(false);
        return indentation != null && indentation.isSetHanging() ? (int)Units.toDXA(POIXMLUnits.parseLength(indentation.xgetHanging())) : -1;
    }

    public void setIndentationHanging(int indentation) {
        CTInd indent = this.getCTInd(true);
        BigInteger bi = new BigInteger(Integer.toString(indentation));
        indent.setHanging(bi);
    }

    public int getIndentationFirstLine() {
        CTInd indentation = this.getCTInd(false);
        return indentation != null && indentation.isSetFirstLine() ? (int)Units.toDXA(POIXMLUnits.parseLength(indentation.xgetFirstLine())) : -1;
    }

    public void setIndentationFirstLine(int indentation) {
        CTInd indent = this.getCTInd(true);
        BigInteger bi = new BigInteger(Integer.toString(indentation));
        indent.setFirstLine(bi);
    }

    @Override
    public int getIndentFromLeft() {
        return this.getIndentationLeft();
    }

    @Override
    public void setIndentFromLeft(int dxaLeft) {
        this.setIndentationLeft(dxaLeft);
    }

    @Override
    public int getIndentFromRight() {
        return this.getIndentationRight();
    }

    @Override
    public void setIndentFromRight(int dxaRight) {
        this.setIndentationRight(dxaRight);
    }

    @Override
    public int getFirstLineIndent() {
        return this.getIndentationFirstLine();
    }

    @Override
    public void setFirstLineIndent(int first) {
        this.setIndentationFirstLine(first);
    }

    @Override
    public boolean isWordWrapped() {
        return this.getCTPPr().isSetWordWrap() && POIXMLUnits.parseOnOff(this.getCTPPr().getWordWrap());
    }

    @Override
    public void setWordWrapped(boolean wrap) {
        CTPPr ppr = this.getCTPPr();
        if (wrap) {
            CTOnOff wordWrap = ppr.isSetWordWrap() ? ppr.getWordWrap() : ppr.addNewWordWrap();
            wordWrap.setVal(STOnOff1.ON);
        } else if (ppr.isSetWordWrap()) {
            ppr.unsetWordWrap();
        }
    }

    public boolean isWordWrap() {
        return this.isWordWrapped();
    }

    @Deprecated
    public void setWordWrap(boolean wrap) {
        this.setWordWrapped(wrap);
    }

    public String getStyle() {
        CTPPr pr = this.getCTPPr();
        CTString style = pr.isSetPStyle() ? pr.getPStyle() : null;
        return style != null ? style.getVal() : null;
    }

    public void setStyle(String styleId) {
        CTPPr pr = this.getCTPPr();
        CTString style = pr.getPStyle() != null ? pr.getPStyle() : pr.addNewPStyle();
        style.setVal(styleId);
    }

    private CTPBdr getCTPBrd(boolean create) {
        CTPBdr ct;
        CTPPr pr = this.getCTPPr();
        CTPBdr cTPBdr = ct = pr.isSetPBdr() ? pr.getPBdr() : null;
        if (create && ct == null) {
            ct = pr.addNewPBdr();
        }
        return ct;
    }

    private CTSpacing getCTSpacing(boolean create) {
        CTPPr pr = this.getCTPPr();
        CTSpacing ct = pr.getSpacing();
        if (create && ct == null) {
            ct = pr.addNewSpacing();
        }
        return ct;
    }

    private CTInd getCTInd(boolean create) {
        CTPPr pr = this.getCTPPr();
        CTInd ct = pr.getInd();
        if (create && ct == null) {
            ct = pr.addNewInd();
        }
        return ct;
    }

    @Internal
    public CTPPr getCTPPr() {
        return this.paragraph.getPPr() == null ? this.paragraph.addNewPPr() : this.paragraph.getPPr();
    }

    protected void addRun(CTR run) {
        int pos = this.paragraph.sizeOfRArray();
        this.paragraph.addNewR();
        this.paragraph.setRArray(pos, run);
    }

    public XWPFRun createRun() {
        XWPFRun xwpfRun = new XWPFRun(this.paragraph.addNewR(), (IRunBody)this);
        this.runs.add(xwpfRun);
        this.iruns.add(xwpfRun);
        return xwpfRun;
    }

    public XWPFHyperlinkRun createHyperlinkRun(String uri) {
        String rId = this.getPart().getPackagePart().addExternalRelationship(uri, XWPFRelation.HYPERLINK.getRelation()).getId();
        CTHyperlink ctHyperLink = this.getCTP().addNewHyperlink();
        ctHyperLink.setId(rId);
        ctHyperLink.addNewR();
        XWPFHyperlinkRun link = new XWPFHyperlinkRun(ctHyperLink, ctHyperLink.getRArray(0), this);
        this.runs.add(link);
        this.iruns.add(link);
        return link;
    }

    public XWPFFieldRun createFieldRun() {
        CTSimpleField ctSimpleField = this.paragraph.addNewFldSimple();
        XWPFFieldRun newRun = new XWPFFieldRun(ctSimpleField, ctSimpleField.addNewR(), this);
        this.runs.add(newRun);
        this.iruns.add(newRun);
        return newRun;
    }

    public XWPFRun insertNewRun(int pos) {
        if (pos == this.runs.size()) {
            return this.createRun();
        }
        return this.insertNewProvidedRun(pos, newCursor -> {
            String uri = CTR.type.getName().getNamespaceURI();
            String localPart = "r";
            newCursor.beginElement(localPart, uri);
            newCursor.toParent();
            CTR r = (CTR)newCursor.getObject();
            return new XWPFRun(r, (IRunBody)this);
        });
    }

    public XWPFHyperlinkRun insertNewHyperlinkRun(int pos, String uri) {
        if (pos == this.runs.size()) {
            return this.createHyperlinkRun(uri);
        }
        XWPFHyperlinkRun newRun = this.insertNewProvidedRun(pos, newCursor -> {
            String namespaceURI = CTHyperlink.type.getName().getNamespaceURI();
            String localPart = "hyperlink";
            newCursor.beginElement(localPart, namespaceURI);
            newCursor.toParent();
            CTHyperlink ctHyperLink = (CTHyperlink)newCursor.getObject();
            return new XWPFHyperlinkRun(ctHyperLink, ctHyperLink.addNewR(), this);
        });
        if (newRun != null) {
            String rId = this.getPart().getPackagePart().addExternalRelationship(uri, XWPFRelation.HYPERLINK.getRelation()).getId();
            newRun.getCTHyperlink().setId(rId);
        }
        return newRun;
    }

    public XWPFFieldRun insertNewFieldRun(int pos) {
        if (pos == this.runs.size()) {
            return this.createFieldRun();
        }
        return this.insertNewProvidedRun(pos, newCursor -> {
            String uri = CTSimpleField.type.getName().getNamespaceURI();
            String localPart = "fldSimple";
            newCursor.beginElement(localPart, uri);
            newCursor.toParent();
            CTSimpleField ctSimpleField = (CTSimpleField)newCursor.getObject();
            return new XWPFFieldRun(ctSimpleField, ctSimpleField.addNewR(), this);
        });
    }

    private <T extends XWPFRun> T insertNewProvidedRun(int pos, Function<XmlCursor, T> provider) {
        if (pos >= 0 && pos < this.runs.size()) {
            XWPFRun run = this.runs.get(pos);
            CTR ctr = run.getCTR();
            try (XmlCursor newCursor = ctr.newCursor();){
                if (!this.isCursorInParagraph(newCursor)) {
                    newCursor.toParent();
                }
                if (this.isCursorInParagraph(newCursor)) {
                    XWPFRun newRun = (XWPFRun)provider.apply(newCursor);
                    int iPos = this.iruns.size();
                    int oldAt = this.iruns.indexOf(run);
                    if (oldAt != -1) {
                        iPos = oldAt;
                    }
                    this.iruns.add(iPos, newRun);
                    this.runs.add(pos, newRun);
                    XWPFRun xWPFRun = newRun;
                    return (T)xWPFRun;
                }
            }
        }
        return null;
    }

    private boolean isCursorInParagraph(XmlCursor cursor) {
        try (XmlCursor verify = cursor.newCursor();){
            verify.toParent();
            boolean bl = verify.getObject() == this.paragraph;
            return bl;
        }
    }

    public TextSegment searchText(String searched, PositionInParagraph startPos) {
        int startRun = startPos.getRun();
        int startText = startPos.getText();
        int startChar = startPos.getChar();
        int beginRunPos = 0;
        int candCharPos = 0;
        boolean newList = false;
        CTR[] rArray = this.paragraph.getRArray();
        for (int runPos = startRun; runPos < rArray.length; ++runPos) {
            int beginTextPos = 0;
            int beginCharPos = 0;
            int textPos = 0;
            CTR ctRun = rArray[runPos];
            try (XmlCursor c = ctRun.newCursor();){
                c.selectPath("./*");
                while (c.toNextSelection()) {
                    XmlObject o = c.getObject();
                    if (o instanceof CTText) {
                        if (textPos >= startText) {
                            String candidate = ((CTText)o).getStringValue();
                            for (int charPos = runPos == startRun ? startChar : 0; charPos < candidate.length(); ++charPos) {
                                if (candidate.charAt(charPos) == searched.charAt(0) && candCharPos == 0) {
                                    beginTextPos = textPos;
                                    beginCharPos = charPos;
                                    beginRunPos = runPos;
                                    newList = true;
                                }
                                if (candidate.charAt(charPos) == searched.charAt(candCharPos)) {
                                    if (candCharPos + 1 < searched.length()) {
                                        ++candCharPos;
                                        continue;
                                    }
                                    if (!newList) continue;
                                    TextSegment segment = new TextSegment();
                                    segment.setBeginRun(beginRunPos);
                                    segment.setBeginText(beginTextPos);
                                    segment.setBeginChar(beginCharPos);
                                    segment.setEndRun(runPos);
                                    segment.setEndText(textPos);
                                    segment.setEndChar(charPos);
                                    TextSegment textSegment = segment;
                                    return textSegment;
                                }
                                candCharPos = 0;
                            }
                        }
                        ++textPos;
                        continue;
                    }
                    if (o instanceof CTProofErr) {
                        c.removeXml();
                        continue;
                    }
                    if (o instanceof CTRPr) continue;
                    candCharPos = 0;
                }
                continue;
            }
        }
        return null;
    }

    public String getText(TextSegment segment) {
        int runBegin = segment.getBeginRun();
        int textBegin = segment.getBeginText();
        int charBegin = segment.getBeginChar();
        int runEnd = segment.getEndRun();
        int textEnd = segment.getEndText();
        int charEnd = segment.getEndChar();
        StringBuilder out = new StringBuilder();
        CTR[] rArray = this.paragraph.getRArray();
        for (int i = runBegin; i <= runEnd; ++i) {
            CTText[] tArray = rArray[i].getTArray();
            int startText = 0;
            int endText = tArray.length - 1;
            if (i == runBegin) {
                startText = textBegin;
            }
            if (i == runEnd) {
                endText = textEnd;
            }
            for (int j = startText; j <= endText; ++j) {
                String tmpText = tArray[j].getStringValue();
                int startChar = 0;
                int endChar = tmpText.length() - 1;
                if (j == textBegin && i == runBegin) {
                    startChar = charBegin;
                }
                if (j == textEnd && i == runEnd) {
                    endChar = charEnd;
                }
                out.append(tmpText, startChar, endChar + 1);
            }
        }
        return out.toString();
    }

    public boolean removeRun(int pos) {
        if (pos >= 0 && pos < this.runs.size()) {
            XWPFRun run = this.runs.get(pos);
            if (run instanceof XWPFHyperlinkRun && this.isTheOnlyCTHyperlinkInRuns((XWPFHyperlinkRun)run)) {
                try (XmlCursor c = ((XWPFHyperlinkRun)run).getCTHyperlink().newCursor();){
                    c.removeXml();
                }
                this.runs.remove(pos);
                this.iruns.remove(run);
                return true;
            }
            if (run instanceof XWPFFieldRun && this.isTheOnlyCTFieldInRuns((XWPFFieldRun)run)) {
                try (XmlCursor c = ((XWPFFieldRun)run).getCTField().newCursor();){
                    c.removeXml();
                }
                this.runs.remove(pos);
                this.iruns.remove(run);
                return true;
            }
            try (XmlCursor c = run.getCTR().newCursor();){
                c.removeXml();
            }
            this.runs.remove(pos);
            this.iruns.remove(run);
            return true;
        }
        return false;
    }

    private boolean isTheOnlyCTHyperlinkInRuns(XWPFHyperlinkRun run) {
        CTHyperlink ctHyperlink = run.getCTHyperlink();
        long count = this.runs.stream().filter(r -> r instanceof XWPFHyperlinkRun && ctHyperlink == ((XWPFHyperlinkRun)r).getCTHyperlink()).count();
        return count <= 1L;
    }

    private boolean isTheOnlyCTFieldInRuns(XWPFFieldRun run) {
        CTSimpleField ctField = run.getCTField();
        long count = this.runs.stream().filter(r -> r instanceof XWPFFieldRun && ctField == ((XWPFFieldRun)r).getCTField()).count();
        return count <= 1L;
    }

    @Override
    public BodyElementType getElementType() {
        return BodyElementType.PARAGRAPH;
    }

    @Override
    public IBody getBody() {
        return this.part;
    }

    @Override
    public POIXMLDocumentPart getPart() {
        if (this.part != null) {
            return this.part.getPart();
        }
        return null;
    }

    @Override
    public BodyType getPartType() {
        return this.part.getPartType();
    }

    public void addRun(XWPFRun r) {
        if (!this.runs.contains(r)) {
            this.runs.add(r);
            this.iruns.add(r);
        }
    }

    public XWPFRun getRun(CTR r) {
        for (int i = 0; i < this.getRuns().size(); ++i) {
            if (this.getRuns().get(i).getCTR() != r) continue;
            return this.getRuns().get(i);
        }
        return null;
    }

    public void addFootnoteReference(XWPFAbstractFootnoteEndnote footnote) {
        XWPFRun run = this.createRun();
        CTR ctRun = run.getCTR();
        ctRun.addNewRPr().addNewRStyle().setVal("FootnoteReference");
        if (footnote instanceof XWPFEndnote) {
            ctRun.addNewEndnoteReference().setId(footnote.getId());
        } else {
            ctRun.addNewFootnoteReference().setId(footnote.getId());
        }
    }
}


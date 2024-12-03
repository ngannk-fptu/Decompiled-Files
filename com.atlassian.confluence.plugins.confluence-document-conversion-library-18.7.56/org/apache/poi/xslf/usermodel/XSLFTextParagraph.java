/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xslf.usermodel;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.apache.poi.ooxml.util.POIXMLUnits;
import org.apache.poi.sl.draw.DrawPaint;
import org.apache.poi.sl.usermodel.AutoNumberingScheme;
import org.apache.poi.sl.usermodel.PaintStyle;
import org.apache.poi.sl.usermodel.TabStop;
import org.apache.poi.sl.usermodel.TextParagraph;
import org.apache.poi.util.Internal;
import org.apache.poi.util.Units;
import org.apache.poi.xslf.model.ParagraphPropertyFetcher;
import org.apache.poi.xslf.usermodel.XSLFColor;
import org.apache.poi.xslf.usermodel.XSLFLineBreak;
import org.apache.poi.xslf.usermodel.XSLFPlaceholderDetails;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSheet;
import org.apache.poi.xslf.usermodel.XSLFSlideMaster;
import org.apache.poi.xslf.usermodel.XSLFTabStop;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.apache.poi.xslf.usermodel.XSLFTheme;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTRegularTextRun;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSRgbColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextAutonumberBullet;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBulletSizePercent;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBulletSizePoint;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextCharBullet;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextCharacterProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextField;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextFont;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextLineBreak;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextNormalAutofit;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraph;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraphProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextSpacing;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextSpacingPercent;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextSpacingPoint;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextTabStop;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextTabStopList;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextAlignType;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextAutonumberScheme;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextFontAlignType;
import org.openxmlformats.schemas.presentationml.x2006.main.CTPlaceholder;

public class XSLFTextParagraph
implements TextParagraph<XSLFShape, XSLFTextParagraph, XSLFTextRun> {
    private final CTTextParagraph _p;
    private final List<XSLFTextRun> _runs;
    private final XSLFTextShape _shape;

    XSLFTextParagraph(CTTextParagraph p, XSLFTextShape shape) {
        this._p = p;
        this._runs = new ArrayList<XSLFTextRun>();
        this._shape = shape;
        try (XmlCursor c = this._p.newCursor();){
            if (c.toFirstChild()) {
                do {
                    XmlObject r;
                    if ((r = c.getObject()) instanceof CTTextLineBreak) {
                        this._runs.add(new XSLFLineBreak((CTTextLineBreak)r, this));
                        continue;
                    }
                    if (!(r instanceof CTRegularTextRun) && !(r instanceof CTTextField)) continue;
                    this._runs.add(new XSLFTextRun(r, this));
                } while (c.toNextSibling());
            }
        }
    }

    public String getText() {
        StringBuilder out = new StringBuilder();
        for (XSLFTextRun r : this._runs) {
            out.append(r.getRawText());
        }
        return out.toString();
    }

    @Internal
    public CTTextParagraph getXmlObject() {
        return this._p;
    }

    public XSLFTextShape getParentShape() {
        return this._shape;
    }

    @Override
    public List<XSLFTextRun> getTextRuns() {
        return Collections.unmodifiableList(this._runs);
    }

    @Override
    public Iterator<XSLFTextRun> iterator() {
        return this.getTextRuns().iterator();
    }

    public XSLFTextRun addNewTextRun() {
        CTRegularTextRun r = this._p.addNewR();
        CTTextCharacterProperties rPr = r.addNewRPr();
        rPr.setLang("en-US");
        XSLFTextRun run = this.newTextRun(r);
        this._runs.add(run);
        return run;
    }

    public boolean removeTextRun(XSLFTextRun textRun) {
        if (this._runs.remove(textRun)) {
            XmlObject xo = textRun.getXmlObject();
            if (xo instanceof CTRegularTextRun) {
                for (int i = 0; i < this.getXmlObject().sizeOfRArray(); ++i) {
                    if (!this.getXmlObject().getRArray(i).equals(xo)) continue;
                    this.getXmlObject().removeR(i);
                    return true;
                }
            } else if (xo instanceof CTTextField) {
                for (int i = 0; i < this.getXmlObject().sizeOfFldArray(); ++i) {
                    if (!this.getXmlObject().getFldArray(i).equals(xo)) continue;
                    this.getXmlObject().removeFld(i);
                    return true;
                }
            } else if (xo instanceof CTTextLineBreak) {
                for (int i = 0; i < this.getXmlObject().sizeOfBrArray(); ++i) {
                    if (!this.getXmlObject().getBrArray(i).equals(xo)) continue;
                    this.getXmlObject().removeBr(i);
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public XSLFTextRun addLineBreak() {
        XSLFLineBreak run = new XSLFLineBreak(this._p.addNewBr(), this);
        CTTextCharacterProperties brProps = run.getRPr(true);
        if (!this._runs.isEmpty()) {
            CTTextCharacterProperties prevRun = this._runs.get(this._runs.size() - 1).getRPr(true);
            brProps.set(prevRun);
            if (brProps.isSetHlinkClick()) {
                brProps.unsetHlinkClick();
            }
            if (brProps.isSetHlinkMouseOver()) {
                brProps.unsetHlinkMouseOver();
            }
        }
        this._runs.add(run);
        return run;
    }

    @Override
    public TextParagraph.TextAlign getTextAlign() {
        return (TextParagraph.TextAlign)((Object)this.fetchParagraphProperty((props, val) -> {
            if (props.isSetAlgn()) {
                val.accept(TextParagraph.TextAlign.values()[props.getAlgn().intValue() - 1]);
            }
        }));
    }

    @Override
    public void setTextAlign(TextParagraph.TextAlign align) {
        CTTextParagraphProperties pr;
        CTTextParagraphProperties cTTextParagraphProperties = pr = this._p.isSetPPr() ? this._p.getPPr() : this._p.addNewPPr();
        if (align == null) {
            if (pr.isSetAlgn()) {
                pr.unsetAlgn();
            }
        } else {
            pr.setAlgn(STTextAlignType.Enum.forInt(align.ordinal() + 1));
        }
    }

    @Override
    public TextParagraph.FontAlign getFontAlign() {
        return (TextParagraph.FontAlign)((Object)this.fetchParagraphProperty((props, val) -> {
            if (props.isSetFontAlgn()) {
                val.accept(TextParagraph.FontAlign.values()[props.getFontAlgn().intValue() - 1]);
            }
        }));
    }

    public void setFontAlign(TextParagraph.FontAlign align) {
        CTTextParagraphProperties pr;
        CTTextParagraphProperties cTTextParagraphProperties = pr = this._p.isSetPPr() ? this._p.getPPr() : this._p.addNewPPr();
        if (align == null) {
            if (pr.isSetFontAlgn()) {
                pr.unsetFontAlgn();
            }
        } else {
            pr.setFontAlgn(STTextFontAlignType.Enum.forInt(align.ordinal() + 1));
        }
    }

    public String getBulletFont() {
        return (String)this.fetchParagraphProperty((props, val) -> {
            if (props.isSetBuFont()) {
                val.accept(props.getBuFont().getTypeface());
            }
        });
    }

    public void setBulletFont(String typeface) {
        CTTextParagraphProperties pr = this._p.isSetPPr() ? this._p.getPPr() : this._p.addNewPPr();
        CTTextFont font = pr.isSetBuFont() ? pr.getBuFont() : pr.addNewBuFont();
        font.setTypeface(typeface);
    }

    public String getBulletCharacter() {
        return (String)this.fetchParagraphProperty((props, val) -> {
            if (props.isSetBuChar()) {
                val.accept(props.getBuChar().getChar());
            }
        });
    }

    public void setBulletCharacter(String str) {
        CTTextParagraphProperties pr = this._p.isSetPPr() ? this._p.getPPr() : this._p.addNewPPr();
        CTTextCharBullet c = pr.isSetBuChar() ? pr.getBuChar() : pr.addNewBuChar();
        c.setChar(str);
    }

    public PaintStyle getBulletFontColor() {
        Color col = (Color)this.fetchParagraphProperty(this::fetchBulletFontColor);
        return col == null ? null : DrawPaint.createSolidPaint(col);
    }

    private void fetchBulletFontColor(CTTextParagraphProperties props, Consumer<Color> val) {
        XSLFSheet sheet = this.getParentShape().getSheet();
        XSLFTheme theme = sheet.getTheme();
        if (props.isSetBuClr()) {
            XSLFColor c = new XSLFColor(props.getBuClr(), theme, null, sheet);
            val.accept(c.getColor());
        }
    }

    public void setBulletFontColor(Color color) {
        this.setBulletFontColor(DrawPaint.createSolidPaint(color));
    }

    public void setBulletFontColor(PaintStyle color) {
        if (!(color instanceof PaintStyle.SolidPaint)) {
            throw new IllegalArgumentException("Currently XSLF only supports SolidPaint");
        }
        PaintStyle.SolidPaint sp = (PaintStyle.SolidPaint)color;
        Color col = DrawPaint.applyColorTransform(sp.getSolidColor());
        CTTextParagraphProperties pr = this._p.isSetPPr() ? this._p.getPPr() : this._p.addNewPPr();
        CTColor c = pr.isSetBuClr() ? pr.getBuClr() : pr.addNewBuClr();
        CTSRgbColor clr = c.isSetSrgbClr() ? c.getSrgbClr() : c.addNewSrgbClr();
        clr.setVal(new byte[]{(byte)col.getRed(), (byte)col.getGreen(), (byte)col.getBlue()});
    }

    public Double getBulletFontSize() {
        return (Double)this.fetchParagraphProperty(XSLFTextParagraph::fetchBulletFontSize);
    }

    private static void fetchBulletFontSize(CTTextParagraphProperties props, Consumer<Double> val) {
        if (props.isSetBuSzPct()) {
            val.accept((double)POIXMLUnits.parsePercent(props.getBuSzPct().xgetVal()) * 0.001);
        }
        if (props.isSetBuSzPts()) {
            val.accept((double)(-props.getBuSzPts().getVal()) * 0.01);
        }
    }

    public void setBulletFontSize(double bulletSize) {
        CTTextParagraphProperties pr;
        CTTextParagraphProperties cTTextParagraphProperties = pr = this._p.isSetPPr() ? this._p.getPPr() : this._p.addNewPPr();
        if (bulletSize >= 0.0) {
            CTTextBulletSizePercent pt = pr.isSetBuSzPct() ? pr.getBuSzPct() : pr.addNewBuSzPct();
            pt.setVal(Integer.toString((int)(bulletSize * 1000.0)));
            if (pr.isSetBuSzPts()) {
                pr.unsetBuSzPts();
            }
        } else {
            CTTextBulletSizePoint pt = pr.isSetBuSzPts() ? pr.getBuSzPts() : pr.addNewBuSzPts();
            pt.setVal((int)(-bulletSize * 100.0));
            if (pr.isSetBuSzPct()) {
                pr.unsetBuSzPct();
            }
        }
    }

    public AutoNumberingScheme getAutoNumberingScheme() {
        return (AutoNumberingScheme)((Object)this.fetchParagraphProperty(XSLFTextParagraph::fetchAutoNumberingScheme));
    }

    private static void fetchAutoNumberingScheme(CTTextParagraphProperties props, Consumer<AutoNumberingScheme> val) {
        AutoNumberingScheme ans;
        if (props.isSetBuAutoNum() && (ans = AutoNumberingScheme.forOoxmlID(props.getBuAutoNum().getType().intValue())) != null) {
            val.accept(ans);
        }
    }

    public Integer getAutoNumberingStartAt() {
        return (Integer)this.fetchParagraphProperty((props, val) -> {
            if (props.isSetBuAutoNum() && props.getBuAutoNum().isSetStartAt()) {
                val.accept(props.getBuAutoNum().getStartAt());
            }
        });
    }

    @Override
    public void setIndent(Double indent) {
        CTTextParagraphProperties pr;
        if (indent == null && !this._p.isSetPPr()) {
            return;
        }
        CTTextParagraphProperties cTTextParagraphProperties = pr = this._p.isSetPPr() ? this._p.getPPr() : this._p.addNewPPr();
        if (indent == null) {
            if (pr.isSetIndent()) {
                pr.unsetIndent();
            }
        } else {
            pr.setIndent(Units.toEMU(indent));
        }
    }

    @Override
    public Double getIndent() {
        return (Double)this.fetchParagraphProperty((props, val) -> {
            if (props.isSetIndent()) {
                val.accept(Units.toPoints(props.getIndent()));
            }
        });
    }

    @Override
    public void setLeftMargin(Double leftMargin) {
        CTTextParagraphProperties pr;
        if (leftMargin == null && !this._p.isSetPPr()) {
            return;
        }
        CTTextParagraphProperties cTTextParagraphProperties = pr = this._p.isSetPPr() ? this._p.getPPr() : this._p.addNewPPr();
        if (leftMargin == null) {
            if (pr.isSetMarL()) {
                pr.unsetMarL();
            }
        } else {
            pr.setMarL(Units.toEMU(leftMargin));
        }
    }

    @Override
    public Double getLeftMargin() {
        return (Double)this.fetchParagraphProperty((props, val) -> {
            if (props.isSetMarL()) {
                val.accept(Units.toPoints(props.getMarL()));
            }
        });
    }

    @Override
    public void setRightMargin(Double rightMargin) {
        CTTextParagraphProperties pr;
        if (rightMargin == null && !this._p.isSetPPr()) {
            return;
        }
        CTTextParagraphProperties cTTextParagraphProperties = pr = this._p.isSetPPr() ? this._p.getPPr() : this._p.addNewPPr();
        if (rightMargin == null) {
            if (pr.isSetMarR()) {
                pr.unsetMarR();
            }
        } else {
            pr.setMarR(Units.toEMU(rightMargin));
        }
    }

    @Override
    public Double getRightMargin() {
        return (Double)this.fetchParagraphProperty((props, val) -> {
            if (props.isSetMarR()) {
                val.accept(Units.toPoints(props.getMarR()));
            }
        });
    }

    @Override
    public Double getDefaultTabSize() {
        return (Double)this.fetchParagraphProperty((props, val) -> {
            if (props.isSetDefTabSz()) {
                val.accept(Units.toPoints(POIXMLUnits.parseLength(props.xgetDefTabSz())));
            }
        });
    }

    public double getTabStop(int idx) {
        Double d = (Double)this.fetchParagraphProperty((props, val) -> XSLFTextParagraph.fetchTabStop(idx, props, val));
        return d == null ? 0.0 : d;
    }

    private static void fetchTabStop(int idx, CTTextParagraphProperties props, Consumer<Double> val) {
        CTTextTabStopList tabStops;
        if (props.isSetTabLst() && idx < (tabStops = props.getTabLst()).sizeOfTabArray()) {
            CTTextTabStop ts = tabStops.getTabArray(idx);
            val.accept(Units.toPoints(POIXMLUnits.parseLength(ts.xgetPos())));
        }
    }

    public void addTabStop(double value) {
        CTTextParagraphProperties pr = this._p.isSetPPr() ? this._p.getPPr() : this._p.addNewPPr();
        CTTextTabStopList tabStops = pr.isSetTabLst() ? pr.getTabLst() : pr.addNewTabLst();
        tabStops.addNewTab().setPos(Units.toEMU(value));
    }

    @Override
    public void setLineSpacing(Double lineSpacing) {
        this.setSpacing(lineSpacing, props -> props::getLnSpc, props -> props::addNewLnSpc, props -> props::unsetLnSpc);
    }

    @Override
    public Double getLineSpacing() {
        CTTextNormalAutofit normAutofit;
        Double lnSpc = this.getSpacing(props -> props::getLnSpc);
        if (lnSpc != null && lnSpc > 0.0 && (normAutofit = this.getParentShape().getTextBodyPr().getNormAutofit()) != null) {
            double scale = 1.0 - (double)POIXMLUnits.parsePercent(normAutofit.xgetLnSpcReduction()) / 100000.0;
            return lnSpc * scale;
        }
        return lnSpc;
    }

    @Override
    public void setSpaceBefore(Double spaceBefore) {
        this.setSpacing(spaceBefore, props -> props::getSpcBef, props -> props::addNewSpcBef, props -> props::unsetSpcBef);
    }

    @Override
    public Double getSpaceBefore() {
        return this.getSpacing(props -> props::getSpcBef);
    }

    @Override
    public void setSpaceAfter(Double spaceAfter) {
        this.setSpacing(spaceAfter, props -> props::getSpcAft, props -> props::addNewSpcAft, props -> props::unsetSpcAft);
    }

    @Override
    public Double getSpaceAfter() {
        return this.getSpacing(props -> props::getSpcAft);
    }

    private void setSpacing(Double space, Function<CTTextParagraphProperties, Supplier<CTTextSpacing>> getSpc, Function<CTTextParagraphProperties, Supplier<CTTextSpacing>> addSpc, Function<CTTextParagraphProperties, Procedure> unsetSpc) {
        CTTextParagraphProperties pPr;
        CTTextParagraphProperties cTTextParagraphProperties = pPr = space == null || this._p.isSetPPr() ? this._p.getPPr() : this._p.addNewPPr();
        if (pPr == null) {
            return;
        }
        CTTextSpacing spc = getSpc.apply(pPr).get();
        if (space == null) {
            if (spc != null) {
                unsetSpc.apply(pPr).accept();
            }
            return;
        }
        if (spc == null) {
            spc = addSpc.apply(pPr).get();
        }
        if (space >= 0.0) {
            if (spc.isSetSpcPts()) {
                spc.unsetSpcPts();
            }
            CTTextSpacingPercent pct = spc.isSetSpcPct() ? spc.getSpcPct() : spc.addNewSpcPct();
            pct.setVal((int)(space * 1000.0));
        } else {
            if (spc.isSetSpcPct()) {
                spc.unsetSpcPct();
            }
            CTTextSpacingPoint pts = spc.isSetSpcPts() ? spc.getSpcPts() : spc.addNewSpcPts();
            pts.setVal((int)(-space.doubleValue() * 100.0));
        }
    }

    private Double getSpacing(Function<CTTextParagraphProperties, Supplier<CTTextSpacing>> getSpc) {
        return (Double)this.fetchParagraphProperty((props, val) -> XSLFTextParagraph.fetchSpacing(getSpc, props, val));
    }

    private static void fetchSpacing(Function<CTTextParagraphProperties, Supplier<CTTextSpacing>> getSpc, CTTextParagraphProperties props, Consumer<Double> val) {
        CTTextSpacing spc = getSpc.apply(props).get();
        if (spc != null) {
            if (spc.isSetSpcPct()) {
                val.accept((double)POIXMLUnits.parsePercent(spc.getSpcPct().xgetVal()) * 0.001);
            } else if (spc.isSetSpcPts()) {
                val.accept((double)(-spc.getSpcPts().getVal()) * 0.01);
            }
        }
    }

    @Override
    public void setIndentLevel(int level) {
        CTTextParagraphProperties pr = this._p.isSetPPr() ? this._p.getPPr() : this._p.addNewPPr();
        pr.setLvl(level);
    }

    @Override
    public int getIndentLevel() {
        CTTextParagraphProperties pr = this._p.getPPr();
        return pr == null || !pr.isSetLvl() ? 0 : pr.getLvl();
    }

    public boolean isBullet() {
        Boolean b = (Boolean)this.fetchParagraphProperty(XSLFTextParagraph::fetchIsBullet);
        return b == null ? false : b;
    }

    private static void fetchIsBullet(CTTextParagraphProperties props, Consumer<Boolean> val) {
        if (props.isSetBuNone()) {
            val.accept(false);
        } else if (props.isSetBuFont() || props.isSetBuChar()) {
            val.accept(true);
        }
    }

    public void setBullet(boolean flag) {
        CTTextParagraphProperties pr;
        if (this.isBullet() == flag) {
            return;
        }
        CTTextParagraphProperties cTTextParagraphProperties = pr = this._p.isSetPPr() ? this._p.getPPr() : this._p.addNewPPr();
        if (flag) {
            pr.addNewBuFont().setTypeface("Arial");
            pr.addNewBuChar().setChar("\u2022");
        } else {
            if (pr.isSetBuFont()) {
                pr.unsetBuFont();
            }
            if (pr.isSetBuChar()) {
                pr.unsetBuChar();
            }
            if (pr.isSetBuAutoNum()) {
                pr.unsetBuAutoNum();
            }
            if (pr.isSetBuBlip()) {
                pr.unsetBuBlip();
            }
            if (pr.isSetBuClr()) {
                pr.unsetBuClr();
            }
            if (pr.isSetBuClrTx()) {
                pr.unsetBuClrTx();
            }
            if (pr.isSetBuFont()) {
                pr.unsetBuFont();
            }
            if (pr.isSetBuFontTx()) {
                pr.unsetBuFontTx();
            }
            if (pr.isSetBuSzPct()) {
                pr.unsetBuSzPct();
            }
            if (pr.isSetBuSzPts()) {
                pr.unsetBuSzPts();
            }
            if (pr.isSetBuSzTx()) {
                pr.unsetBuSzTx();
            }
            pr.addNewBuNone();
        }
    }

    public void setBulletAutoNumber(AutoNumberingScheme scheme, int startAt) {
        if (startAt < 1) {
            throw new IllegalArgumentException("Start Number must be greater or equal that 1");
        }
        CTTextParagraphProperties pr = this._p.isSetPPr() ? this._p.getPPr() : this._p.addNewPPr();
        CTTextAutonumberBullet lst = pr.isSetBuAutoNum() ? pr.getBuAutoNum() : pr.addNewBuAutoNum();
        lst.setType(STTextAutonumberScheme.Enum.forInt(scheme.ooxmlId));
        lst.setStartAt(startAt);
    }

    public String toString() {
        return "[" + this.getClass() + "]" + this.getText();
    }

    @Internal
    public CTTextParagraphProperties getDefaultMasterStyle() {
        XSLFSheet masterSheet;
        String defaultStyleSelector;
        CTPlaceholder ph = ((XSLFPlaceholderDetails)this._shape.getPlaceholderDetails()).getCTPlaceholder(false);
        switch (ph == null ? -1 : ph.getType().intValue()) {
            case 1: 
            case 3: {
                defaultStyleSelector = "titleStyle";
                break;
            }
            case -1: 
            case 5: 
            case 6: 
            case 7: {
                defaultStyleSelector = "otherStyle";
                break;
            }
            default: {
                defaultStyleSelector = "bodyStyle";
            }
        }
        int level = this.getIndentLevel();
        String nsPML = "http://schemas.openxmlformats.org/presentationml/2006/main";
        for (XSLFSheet m = masterSheet = this._shape.getSheet(); m != null; m = (XSLFSheet)((Object)m.getMasterSheet())) {
            masterSheet = m;
            XmlObject xo = masterSheet.getXmlObject();
            try (XmlCursor cur = xo.newCursor();){
                cur.push();
                if ((!cur.toChild("http://schemas.openxmlformats.org/presentationml/2006/main", "txStyles") || !cur.toChild("http://schemas.openxmlformats.org/presentationml/2006/main", defaultStyleSelector)) && (!cur.pop() || !cur.toChild("http://schemas.openxmlformats.org/presentationml/2006/main", "notesStyle"))) continue;
                while (level >= 0) {
                    cur.push();
                    if (cur.toChild("http://schemas.openxmlformats.org/drawingml/2006/main", "lvl" + (level + 1) + "pPr")) {
                        CTTextParagraphProperties cTTextParagraphProperties = (CTTextParagraphProperties)cur.getObject();
                        return cTTextParagraphProperties;
                    }
                    cur.pop();
                    --level;
                }
                continue;
            }
        }
        return null;
    }

    private <T> T fetchParagraphProperty(ParagraphPropertyFetcher.ParaPropFetcher<T> fetcher) {
        XSLFTextShape shape = this.getParentShape();
        return new ParagraphPropertyFetcher<T>(this, fetcher).fetchProperty(shape);
    }

    void copy(XSLFTextParagraph other) {
        Double lineSpacing;
        Double spaceBefore;
        Double spaceAfter;
        Double indent;
        Double leftMargin;
        boolean isBullet;
        int i;
        if (other == this) {
            return;
        }
        CTTextParagraph thisP = this.getXmlObject();
        CTTextParagraph otherP = other.getXmlObject();
        if (thisP.isSetPPr()) {
            thisP.unsetPPr();
        }
        if (thisP.isSetEndParaRPr()) {
            thisP.unsetEndParaRPr();
        }
        this._runs.clear();
        for (i = thisP.sizeOfBrArray(); i > 0; --i) {
            thisP.removeBr(i - 1);
        }
        for (i = thisP.sizeOfRArray(); i > 0; --i) {
            thisP.removeR(i - 1);
        }
        for (i = thisP.sizeOfFldArray(); i > 0; --i) {
            thisP.removeFld(i - 1);
        }
        for (XSLFTextRun tr : other.getTextRuns()) {
            XmlObject xo = tr.getXmlObject().copy();
            XSLFTextRun run = this.addNewTextRun();
            run.getXmlObject().set(xo);
            run.copy(tr);
        }
        TextParagraph.TextAlign srcAlign = other.getTextAlign();
        if (srcAlign != this.getTextAlign()) {
            this.setTextAlign(srcAlign);
        }
        if ((isBullet = other.isBullet()) != this.isBullet()) {
            this.setBullet(isBullet);
            if (isBullet) {
                Double buSize;
                PaintStyle buColor;
                String buChar;
                String buFont = other.getBulletFont();
                if (buFont != null && !buFont.equals(this.getBulletFont())) {
                    this.setBulletFont(buFont);
                }
                if ((buChar = other.getBulletCharacter()) != null && !buChar.equals(this.getBulletCharacter())) {
                    this.setBulletCharacter(buChar);
                }
                if ((buColor = other.getBulletFontColor()) != null && !buColor.equals(this.getBulletFontColor())) {
                    this.setBulletFontColor(buColor);
                }
                if (XSLFTextParagraph.doubleNotEquals(buSize = other.getBulletFontSize(), this.getBulletFontSize())) {
                    this.setBulletFontSize(buSize);
                }
            }
        }
        if (XSLFTextParagraph.doubleNotEquals(leftMargin = other.getLeftMargin(), this.getLeftMargin())) {
            this.setLeftMargin(leftMargin);
        }
        if (XSLFTextParagraph.doubleNotEquals(indent = other.getIndent(), this.getIndent())) {
            this.setIndent(indent);
        }
        if (XSLFTextParagraph.doubleNotEquals(spaceAfter = other.getSpaceAfter(), this.getSpaceAfter())) {
            this.setSpaceAfter(spaceAfter);
        }
        if (XSLFTextParagraph.doubleNotEquals(spaceBefore = other.getSpaceBefore(), this.getSpaceBefore())) {
            this.setSpaceBefore(spaceBefore);
        }
        if (XSLFTextParagraph.doubleNotEquals(lineSpacing = other.getLineSpacing(), this.getLineSpacing())) {
            this.setLineSpacing(lineSpacing);
        }
    }

    private static boolean doubleNotEquals(Double d1, Double d2) {
        return !Objects.equals(d1, d2);
    }

    @Override
    public Double getDefaultFontSize() {
        CTTextParagraphProperties masterStyle;
        CTTextCharacterProperties endPr = this._p.getEndParaRPr();
        if (!(endPr != null && endPr.isSetSz() || (masterStyle = this.getDefaultMasterStyle()) == null)) {
            endPr = masterStyle.getDefRPr();
        }
        return endPr == null || !endPr.isSetSz() ? 12.0 : (double)endPr.getSz() / 100.0;
    }

    @Override
    public String getDefaultFontFamily() {
        String family = this._runs.isEmpty() ? null : this._runs.get(0).getFontFamily();
        return family == null ? "Arial" : family;
    }

    @Override
    public TextParagraph.BulletStyle getBulletStyle() {
        if (!this.isBullet()) {
            return null;
        }
        return new TextParagraph.BulletStyle(){

            @Override
            public String getBulletCharacter() {
                return XSLFTextParagraph.this.getBulletCharacter();
            }

            @Override
            public String getBulletFont() {
                return XSLFTextParagraph.this.getBulletFont();
            }

            @Override
            public Double getBulletFontSize() {
                return XSLFTextParagraph.this.getBulletFontSize();
            }

            @Override
            public PaintStyle getBulletFontColor() {
                return XSLFTextParagraph.this.getBulletFontColor();
            }

            @Override
            public void setBulletFontColor(Color color) {
                this.setBulletFontColor(DrawPaint.createSolidPaint(color));
            }

            @Override
            public void setBulletFontColor(PaintStyle color) {
                XSLFTextParagraph.this.setBulletFontColor(color);
            }

            @Override
            public AutoNumberingScheme getAutoNumberingScheme() {
                return XSLFTextParagraph.this.getAutoNumberingScheme();
            }

            @Override
            public Integer getAutoNumberingStartAt() {
                return XSLFTextParagraph.this.getAutoNumberingStartAt();
            }
        };
    }

    @Override
    public void setBulletStyle(Object ... styles) {
        if (styles.length == 0) {
            this.setBullet(false);
        } else {
            this.setBullet(true);
            for (Object ostyle : styles) {
                if (ostyle instanceof Number) {
                    this.setBulletFontSize(((Number)ostyle).doubleValue());
                    continue;
                }
                if (ostyle instanceof Color) {
                    this.setBulletFontColor((Color)ostyle);
                    continue;
                }
                if (ostyle instanceof Character) {
                    this.setBulletCharacter(ostyle.toString());
                    continue;
                }
                if (ostyle instanceof String) {
                    this.setBulletFont((String)ostyle);
                    continue;
                }
                if (!(ostyle instanceof AutoNumberingScheme)) continue;
                this.setBulletAutoNumber((AutoNumberingScheme)((Object)ostyle), 0);
            }
        }
    }

    @Override
    public List<XSLFTabStop> getTabStops() {
        return (List)this.fetchParagraphProperty(XSLFTextParagraph::fetchTabStops);
    }

    private static void fetchTabStops(CTTextParagraphProperties props, Consumer<List<XSLFTabStop>> val) {
        if (props.isSetTabLst()) {
            ArrayList<XSLFTabStop> list = new ArrayList<XSLFTabStop>();
            for (CTTextTabStop ta : props.getTabLst().getTabArray()) {
                list.add(new XSLFTabStop(ta));
            }
            val.accept(list);
        }
    }

    @Override
    public void addTabStops(double positionInPoints, TabStop.TabStopType tabStopType) {
        CTTextParagraphProperties tpp;
        XSLFSheet sheet = this.getParentShape().getSheet();
        if (sheet instanceof XSLFSlideMaster) {
            tpp = this.getDefaultMasterStyle();
        } else {
            CTTextParagraph xo = this.getXmlObject();
            CTTextParagraphProperties cTTextParagraphProperties = tpp = xo.isSetPPr() ? xo.getPPr() : xo.addNewPPr();
        }
        if (tpp == null) {
            return;
        }
        CTTextTabStopList stl = tpp.isSetTabLst() ? tpp.getTabLst() : tpp.addNewTabLst();
        XSLFTabStop tab = new XSLFTabStop(stl.addNewTab());
        tab.setPositionInPoints(positionInPoints);
        tab.setType(tabStopType);
    }

    @Override
    public void clearTabStops() {
        CTTextParagraphProperties tpp;
        XSLFSheet sheet = this.getParentShape().getSheet();
        CTTextParagraphProperties cTTextParagraphProperties = tpp = sheet instanceof XSLFSlideMaster ? this.getDefaultMasterStyle() : this.getXmlObject().getPPr();
        if (tpp != null && tpp.isSetTabLst()) {
            tpp.unsetTabLst();
        }
    }

    void clearButKeepProperties() {
        int i;
        CTTextParagraph thisP = this.getXmlObject();
        for (i = thisP.sizeOfBrArray(); i > 0; --i) {
            thisP.removeBr(i - 1);
        }
        for (i = thisP.sizeOfFldArray(); i > 0; --i) {
            thisP.removeFld(i - 1);
        }
        if (!this._runs.isEmpty()) {
            int size = this._runs.size();
            XSLFTextRun lastRun = this._runs.get(size - 1);
            CTTextCharacterProperties cpOther = lastRun.getRPr(false);
            if (cpOther != null) {
                if (thisP.isSetEndParaRPr()) {
                    thisP.unsetEndParaRPr();
                }
                CTTextCharacterProperties cp = thisP.addNewEndParaRPr();
                cp.set(cpOther);
            }
            for (int i2 = size; i2 > 0; --i2) {
                thisP.removeR(i2 - 1);
            }
            this._runs.clear();
        }
    }

    @Override
    public boolean isHeaderOrFooter() {
        CTPlaceholder ph = ((XSLFPlaceholderDetails)this._shape.getPlaceholderDetails()).getCTPlaceholder(false);
        int phId = ph == null ? -1 : ph.getType().intValue();
        switch (phId) {
            case 5: 
            case 6: 
            case 7: 
            case 8: {
                return true;
            }
        }
        return false;
    }

    protected XSLFTextRun newTextRun(XmlObject r) {
        return new XSLFTextRun(r, this);
    }

    protected XSLFTextRun newTextRun(CTTextLineBreak r) {
        return new XSLFLineBreak(r, this);
    }

    @FunctionalInterface
    private static interface Procedure {
        public void accept();
    }
}


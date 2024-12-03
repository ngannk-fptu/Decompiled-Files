/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.collections4.iterators.IteratorIterable
 *  org.apache.commons.collections4.iterators.ReverseListIterator
 */
package org.apache.poi.xddf.usermodel.text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Function;
import java.util.function.Predicate;
import org.apache.commons.collections4.iterators.IteratorIterable;
import org.apache.commons.collections4.iterators.ReverseListIterator;
import org.apache.poi.ooxml.util.POIXMLUnits;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LocaleUtil;
import org.apache.poi.util.Units;
import org.apache.poi.xddf.usermodel.XDDFColor;
import org.apache.poi.xddf.usermodel.text.FontAlignment;
import org.apache.poi.xddf.usermodel.text.TextAlignment;
import org.apache.poi.xddf.usermodel.text.XDDFBulletSize;
import org.apache.poi.xddf.usermodel.text.XDDFBulletStyle;
import org.apache.poi.xddf.usermodel.text.XDDFFont;
import org.apache.poi.xddf.usermodel.text.XDDFParagraphBulletProperties;
import org.apache.poi.xddf.usermodel.text.XDDFParagraphProperties;
import org.apache.poi.xddf.usermodel.text.XDDFRunProperties;
import org.apache.poi.xddf.usermodel.text.XDDFSpacing;
import org.apache.poi.xddf.usermodel.text.XDDFSpacingPercent;
import org.apache.poi.xddf.usermodel.text.XDDFSpacingPoints;
import org.apache.poi.xddf.usermodel.text.XDDFTabStop;
import org.apache.poi.xddf.usermodel.text.XDDFTextBody;
import org.apache.poi.xddf.usermodel.text.XDDFTextRun;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTRegularTextRun;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextCharacterProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextField;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextLineBreak;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraph;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraphProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextSpacing;

public class XDDFTextParagraph
implements Iterable<XDDFTextRun> {
    private XDDFTextBody _parent;
    private XDDFParagraphProperties _properties;
    private final CTTextParagraph _p;
    private final ArrayList<XDDFTextRun> _runs;

    @Internal
    protected XDDFTextParagraph(CTTextParagraph paragraph, XDDFTextBody parent) {
        this._p = paragraph;
        this._parent = parent;
        int count = paragraph.sizeOfBrArray() + paragraph.sizeOfFldArray() + paragraph.sizeOfRArray();
        this._runs = new ArrayList(count);
        for (XmlObject xo : paragraph.selectChildren(QNameSet.ALL)) {
            if (xo instanceof CTTextLineBreak) {
                this._runs.add(new XDDFTextRun((CTTextLineBreak)xo, this));
                continue;
            }
            if (xo instanceof CTTextField) {
                this._runs.add(new XDDFTextRun((CTTextField)xo, this));
                continue;
            }
            if (!(xo instanceof CTRegularTextRun)) continue;
            this._runs.add(new XDDFTextRun((CTRegularTextRun)xo, this));
        }
        this.addDefaultRunProperties();
        this.addAfterLastRunProperties();
    }

    public void setText(String text) {
        int i;
        XmlObject existing = null;
        if (!this._runs.isEmpty()) {
            existing = this._runs.get(this._runs.size() - 1).getProperties().copy();
        }
        for (i = this._p.sizeOfBrArray() - 1; i >= 0; --i) {
            this._p.removeBr(i);
        }
        for (i = this._p.sizeOfFldArray() - 1; i >= 0; --i) {
            this._p.removeFld(i);
        }
        for (i = this._p.sizeOfRArray() - 1; i >= 0; --i) {
            this._p.removeR(i);
        }
        this._runs.clear();
        XDDFTextRun run = this.appendRegularRun(text);
        if (existing != null) {
            run.getProperties().set(existing);
        }
    }

    public String getText() {
        StringBuilder out = new StringBuilder();
        for (XDDFTextRun r : this._runs) {
            out.append(r.getText());
        }
        return out.toString();
    }

    public XDDFTextBody getParentBody() {
        return this._parent;
    }

    public List<XDDFTextRun> getTextRuns() {
        return this._runs;
    }

    @Override
    public Iterator<XDDFTextRun> iterator() {
        return this._runs.iterator();
    }

    @Override
    public Spliterator<XDDFTextRun> spliterator() {
        return this._runs.spliterator();
    }

    public XDDFTextRun appendLineBreak() {
        CTTextLineBreak br = this._p.addNewBr();
        for (XDDFTextRun tr : new IteratorIterable((Iterator)new ReverseListIterator(this._runs))) {
            CTTextCharacterProperties prevProps = tr.getProperties();
            if (prevProps == null) continue;
            br.setRPr((CTTextCharacterProperties)prevProps.copy());
            break;
        }
        XDDFTextRun run = new XDDFTextRun(br, this);
        this._runs.add(run);
        return run;
    }

    public XDDFTextRun appendField(String id, String type, String text) {
        CTTextField f = this._p.addNewFld();
        f.setId(id);
        f.setType(type);
        f.setT(text);
        CTTextCharacterProperties rPr = f.addNewRPr();
        rPr.setLang(LocaleUtil.getUserLocale().toLanguageTag());
        XDDFTextRun run = new XDDFTextRun(f, this);
        this._runs.add(run);
        return run;
    }

    public XDDFTextRun appendRegularRun(String text) {
        CTRegularTextRun r = this._p.addNewR();
        r.setT(text);
        CTTextCharacterProperties rPr = r.addNewRPr();
        rPr.setLang(LocaleUtil.getUserLocale().toLanguageTag());
        XDDFTextRun run = new XDDFTextRun(r, this);
        this._runs.add(run);
        return run;
    }

    public int getIndentationLevel() {
        if (this._p.isSetPPr()) {
            return this.getProperties().getLevel();
        }
        return 0;
    }

    public void setIndentationLevel(Integer level) {
        if (this._p.isSetPPr()) {
            this.getProperties().setLevel(level);
        }
    }

    public TextAlignment getTextAlignment() {
        return this.findDefinedParagraphProperty(CTTextParagraphProperties::isSetAlgn, CTTextParagraphProperties::getAlgn).map(TextAlignment::valueOf).orElse(null);
    }

    public void setTextAlignment(TextAlignment align) {
        if (align != null || this._p.isSetPPr()) {
            this.getOrCreateProperties().setTextAlignment(align);
        }
    }

    public FontAlignment getFontAlignment() {
        return this.findDefinedParagraphProperty(CTTextParagraphProperties::isSetFontAlgn, CTTextParagraphProperties::getFontAlgn).map(FontAlignment::valueOf).orElse(null);
    }

    public void setFontAlignment(FontAlignment align) {
        if (align != null || this._p.isSetPPr()) {
            this.getOrCreateProperties().setFontAlignment(align);
        }
    }

    public Double getIndentation() {
        return this.findDefinedParagraphProperty(CTTextParagraphProperties::isSetIndent, CTTextParagraphProperties::getIndent).map(Units::toPoints).orElse(null);
    }

    public void setIndentation(Double points) {
        if (points != null || this._p.isSetPPr()) {
            this.getOrCreateProperties().setIndentation(points);
        }
    }

    public Double getMarginLeft() {
        return this.findDefinedParagraphProperty(CTTextParagraphProperties::isSetMarL, CTTextParagraphProperties::getMarL).map(Units::toPoints).orElse(null);
    }

    public void setMarginLeft(Double points) {
        if (points != null || this._p.isSetPPr()) {
            this.getOrCreateProperties().setMarginLeft(points);
        }
    }

    public Double getMarginRight() {
        return this.findDefinedParagraphProperty(CTTextParagraphProperties::isSetMarR, CTTextParagraphProperties::getMarR).map(Units::toPoints).orElse(null);
    }

    public void setMarginRight(Double points) {
        if (points != null || this._p.isSetPPr()) {
            this.getOrCreateProperties().setMarginRight(points);
        }
    }

    public Double getDefaultTabSize() {
        return this.findDefinedParagraphProperty(CTTextParagraphProperties::isSetDefTabSz, CTTextParagraphProperties::xgetDefTabSz).map(POIXMLUnits::parseLength).map(Units::toPoints).orElse(null);
    }

    public void setDefaultTabSize(Double points) {
        if (points != null || this._p.isSetPPr()) {
            this.getOrCreateProperties().setDefaultTabSize(points);
        }
    }

    public XDDFSpacing getLineSpacing() {
        return this.findDefinedParagraphProperty(CTTextParagraphProperties::isSetLnSpc, CTTextParagraphProperties::getLnSpc).map(this::extractSpacing).orElse(null);
    }

    public void setLineSpacing(XDDFSpacing linespacing) {
        if (linespacing != null || this._p.isSetPPr()) {
            this.getOrCreateProperties().setLineSpacing(linespacing);
        }
    }

    public XDDFSpacing getSpaceBefore() {
        return this.findDefinedParagraphProperty(CTTextParagraphProperties::isSetSpcBef, CTTextParagraphProperties::getSpcBef).map(this::extractSpacing).orElse(null);
    }

    public void setSpaceBefore(XDDFSpacing spaceBefore) {
        if (spaceBefore != null || this._p.isSetPPr()) {
            this.getOrCreateProperties().setSpaceBefore(spaceBefore);
        }
    }

    public XDDFSpacing getSpaceAfter() {
        return this.findDefinedParagraphProperty(CTTextParagraphProperties::isSetSpcAft, CTTextParagraphProperties::getSpcAft).map(this::extractSpacing).orElse(null);
    }

    public void setSpaceAfter(XDDFSpacing spaceAfter) {
        if (spaceAfter != null || this._p.isSetPPr()) {
            this.getOrCreateProperties().setSpaceAfter(spaceAfter);
        }
    }

    public XDDFColor getBulletColor() {
        return this.findDefinedParagraphProperty(props -> props.isSetBuClr() || props.isSetBuClrTx(), props -> new XDDFParagraphBulletProperties((CTTextParagraphProperties)props).getBulletColor()).orElse(null);
    }

    public void setBulletColor(XDDFColor color) {
        if (color != null || this._p.isSetPPr()) {
            this.getOrCreateBulletProperties().setBulletColor(color);
        }
    }

    public void setBulletColorFollowText() {
        this.getOrCreateBulletProperties().setBulletColorFollowText();
    }

    public XDDFFont getBulletFont() {
        return this.findDefinedParagraphProperty(props -> props.isSetBuFont() || props.isSetBuFontTx(), props -> new XDDFParagraphBulletProperties((CTTextParagraphProperties)props).getBulletFont()).orElse(null);
    }

    public void setBulletFont(XDDFFont font) {
        if (font != null || this._p.isSetPPr()) {
            this.getOrCreateBulletProperties().setBulletFont(font);
        }
    }

    public void setBulletFontFollowText() {
        this.getOrCreateBulletProperties().setBulletFontFollowText();
    }

    public XDDFBulletSize getBulletSize() {
        return this.findDefinedParagraphProperty(props -> props.isSetBuSzPct() || props.isSetBuSzPts() || props.isSetBuSzTx(), props -> new XDDFParagraphBulletProperties((CTTextParagraphProperties)props).getBulletSize()).orElse(null);
    }

    public void setBulletSize(XDDFBulletSize size) {
        if (size != null || this._p.isSetPPr()) {
            this.getOrCreateBulletProperties().setBulletSize(size);
        }
    }

    public XDDFBulletStyle getBulletStyle() {
        return this.findDefinedParagraphProperty(props -> props.isSetBuAutoNum() || props.isSetBuBlip() || props.isSetBuChar() || props.isSetBuNone(), props -> new XDDFParagraphBulletProperties((CTTextParagraphProperties)props).getBulletStyle()).orElse(null);
    }

    public void setBulletStyle(XDDFBulletStyle style) {
        if (style != null || this._p.isSetPPr()) {
            this.getOrCreateBulletProperties().setBulletStyle(style);
        }
    }

    public boolean hasEastAsianLineBreak() {
        return this.findDefinedParagraphProperty(CTTextParagraphProperties::isSetEaLnBrk, CTTextParagraphProperties::getEaLnBrk).orElse(false);
    }

    public void setEastAsianLineBreak(Boolean value) {
        if (value != null || this._p.isSetPPr()) {
            this.getOrCreateProperties().setEastAsianLineBreak(value);
        }
    }

    public boolean hasLatinLineBreak() {
        return this.findDefinedParagraphProperty(CTTextParagraphProperties::isSetLatinLnBrk, CTTextParagraphProperties::getLatinLnBrk).orElse(false);
    }

    public void setLatinLineBreak(Boolean value) {
        if (value != null || this._p.isSetPPr()) {
            this.getOrCreateProperties().setLatinLineBreak(value);
        }
    }

    public boolean hasHangingPunctuation() {
        return this.findDefinedParagraphProperty(CTTextParagraphProperties::isSetHangingPunct, CTTextParagraphProperties::getHangingPunct).orElse(false);
    }

    public void setHangingPunctuation(Boolean value) {
        if (value != null || this._p.isSetPPr()) {
            this.getOrCreateProperties().setHangingPunctuation(value);
        }
    }

    public boolean isRightToLeft() {
        return this.findDefinedParagraphProperty(CTTextParagraphProperties::isSetRtl, CTTextParagraphProperties::getRtl).orElse(false);
    }

    public void setRightToLeft(Boolean value) {
        if (value != null || this._p.isSetPPr()) {
            this.getOrCreateProperties().setRightToLeft(value);
        }
    }

    public XDDFTabStop addTabStop() {
        return this.getOrCreateProperties().addTabStop();
    }

    public XDDFTabStop insertTabStop(int index) {
        return this.getOrCreateProperties().insertTabStop(index);
    }

    public void removeTabStop(int index) {
        if (this._p.isSetPPr()) {
            this.getProperties().removeTabStop(index);
        }
    }

    public XDDFTabStop getTabStop(int index) {
        if (this._p.isSetPPr()) {
            return this.getProperties().getTabStop(index);
        }
        return null;
    }

    public List<XDDFTabStop> getTabStops() {
        if (this._p.isSetPPr()) {
            return this.getProperties().getTabStops();
        }
        return Collections.emptyList();
    }

    public int countTabStops() {
        if (this._p.isSetPPr()) {
            return this.getProperties().countTabStops();
        }
        return 0;
    }

    public XDDFParagraphBulletProperties getOrCreateBulletProperties() {
        return this.getOrCreateProperties().getBulletProperties();
    }

    public XDDFParagraphBulletProperties getBulletProperties() {
        if (this._p.isSetPPr()) {
            return this.getProperties().getBulletProperties();
        }
        return null;
    }

    public XDDFRunProperties addDefaultRunProperties() {
        return this.getOrCreateProperties().addDefaultRunProperties();
    }

    public XDDFRunProperties getDefaultRunProperties() {
        if (this._p.isSetPPr()) {
            return this.getProperties().getDefaultRunProperties();
        }
        return null;
    }

    public void setDefaultRunProperties(XDDFRunProperties properties) {
        if (properties != null || this._p.isSetPPr()) {
            this.getOrCreateProperties().setDefaultRunProperties(properties);
        }
    }

    public XDDFRunProperties addAfterLastRunProperties() {
        if (!this._p.isSetEndParaRPr()) {
            this._p.addNewEndParaRPr();
        }
        return this.getAfterLastRunProperties();
    }

    public XDDFRunProperties getAfterLastRunProperties() {
        if (this._p.isSetEndParaRPr()) {
            return new XDDFRunProperties(this._p.getEndParaRPr());
        }
        return null;
    }

    public void setAfterLastRunProperties(XDDFRunProperties properties) {
        if (properties == null) {
            if (this._p.isSetEndParaRPr()) {
                this._p.unsetEndParaRPr();
            }
        } else {
            this._p.setEndParaRPr(properties.getXmlObject());
        }
    }

    private XDDFSpacing extractSpacing(CTTextSpacing spacing) {
        if (spacing.isSetSpcPct()) {
            double scale = 1.0 - (double)this._parent.getBodyProperties().getAutoFit().getLineSpaceReduction() / 100000.0;
            return new XDDFSpacingPercent(spacing, spacing.getSpcPct(), scale);
        }
        if (spacing.isSetSpcPts()) {
            return new XDDFSpacingPoints(spacing, spacing.getSpcPts());
        }
        return null;
    }

    private XDDFParagraphProperties getProperties() {
        if (this._properties == null) {
            this._properties = new XDDFParagraphProperties(this._p.getPPr());
        }
        return this._properties;
    }

    private XDDFParagraphProperties getOrCreateProperties() {
        if (!this._p.isSetPPr()) {
            this._properties = new XDDFParagraphProperties(this._p.addNewPPr());
        }
        return this.getProperties();
    }

    protected <R> Optional<R> findDefinedParagraphProperty(Predicate<CTTextParagraphProperties> isSet, Function<CTTextParagraphProperties, R> getter) {
        if (this._p.isSetPPr()) {
            int level = this._p.getPPr().isSetLvl() ? 1 + this._p.getPPr().getLvl() : 0;
            return this.findDefinedParagraphProperty(isSet, getter, level);
        }
        return this._parent.findDefinedParagraphProperty(isSet, getter, 0);
    }

    private <R> Optional<R> findDefinedParagraphProperty(Predicate<CTTextParagraphProperties> isSet, Function<CTTextParagraphProperties, R> getter, int level) {
        CTTextParagraphProperties props = this._p.getPPr();
        if (props != null && isSet.test(props)) {
            return Optional.ofNullable(getter.apply(props));
        }
        return this._parent.findDefinedParagraphProperty(isSet, getter, level);
    }

    protected <R> Optional<R> findDefinedRunProperty(Predicate<CTTextCharacterProperties> isSet, Function<CTTextCharacterProperties, R> getter) {
        if (this._p.isSetPPr()) {
            int level = this._p.getPPr().isSetLvl() ? 1 + this._p.getPPr().getLvl() : 0;
            return this.findDefinedRunProperty(isSet, getter, level);
        }
        return this._parent.findDefinedRunProperty(isSet, getter, 0);
    }

    private <R> Optional<R> findDefinedRunProperty(Predicate<CTTextCharacterProperties> isSet, Function<CTTextCharacterProperties, R> getter, int level) {
        CTTextCharacterProperties props;
        CTTextCharacterProperties cTTextCharacterProperties = props = this._p.getPPr().isSetDefRPr() ? this._p.getPPr().getDefRPr() : null;
        if (props != null && isSet.test(props)) {
            return Optional.ofNullable(getter.apply(props));
        }
        return this._parent.findDefinedRunProperty(isSet, getter, level);
    }
}


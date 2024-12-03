/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xslf.usermodel;

import java.awt.Color;
import java.util.function.Consumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.common.usermodel.fonts.FontCharset;
import org.apache.poi.common.usermodel.fonts.FontFamily;
import org.apache.poi.common.usermodel.fonts.FontGroup;
import org.apache.poi.common.usermodel.fonts.FontInfo;
import org.apache.poi.common.usermodel.fonts.FontPitch;
import org.apache.poi.ooxml.util.POIXMLUnits;
import org.apache.poi.openxml4j.exceptions.OpenXML4JRuntimeException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.sl.draw.DrawPaint;
import org.apache.poi.sl.usermodel.PaintStyle;
import org.apache.poi.sl.usermodel.TextRun;
import org.apache.poi.util.Internal;
import org.apache.poi.util.Units;
import org.apache.poi.xslf.model.CharacterPropertyFetcher;
import org.apache.poi.xslf.usermodel.XSLFColor;
import org.apache.poi.xslf.usermodel.XSLFHyperlink;
import org.apache.poi.xslf.usermodel.XSLFPropertiesDelegate;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSheet;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.apache.poi.xslf.usermodel.XSLFTheme;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTFontCollection;
import org.openxmlformats.schemas.drawingml.x2006.main.CTFontScheme;
import org.openxmlformats.schemas.drawingml.x2006.main.CTHyperlink;
import org.openxmlformats.schemas.drawingml.x2006.main.CTRegularTextRun;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSchemeColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeStyle;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSolidColorFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBodyProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextCharacterProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextField;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextFont;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextLineBreak;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextNormalAutofit;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextStrikeType;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextUnderlineType;

public class XSLFTextRun
implements TextRun {
    private static final Logger LOG = LogManager.getLogger(XSLFTextRun.class);
    private final XmlObject _r;
    private final XSLFTextParagraph _p;

    protected XSLFTextRun(XmlObject r, XSLFTextParagraph p) {
        this._r = r;
        this._p = p;
        if (!(r instanceof CTRegularTextRun || r instanceof CTTextLineBreak || r instanceof CTTextField)) {
            throw new OpenXML4JRuntimeException("unsupported text run of type " + r.getClass());
        }
    }

    @Override
    public String getRawText() {
        if (this._r instanceof CTTextField) {
            return ((CTTextField)this._r).getT();
        }
        if (this._r instanceof CTTextLineBreak) {
            return "\n";
        }
        return ((CTRegularTextRun)this._r).getT();
    }

    @Override
    public void setText(String text) {
        if (this._r instanceof CTTextField) {
            ((CTTextField)this._r).setT(text);
        } else if (!(this._r instanceof CTTextLineBreak)) {
            ((CTRegularTextRun)this._r).setT(text);
        }
    }

    @Internal
    public XmlObject getXmlObject() {
        return this._r;
    }

    @Override
    public void setFontColor(Color color) {
        this.setFontColor(DrawPaint.createSolidPaint(color));
    }

    @Override
    public void setFontColor(PaintStyle color) {
        if (!(color instanceof PaintStyle.SolidPaint)) {
            LOG.atWarn().log("Currently only SolidPaint is supported!");
            return;
        }
        PaintStyle.SolidPaint sp = (PaintStyle.SolidPaint)color;
        Color c = DrawPaint.applyColorTransform(sp.getSolidColor());
        CTTextCharacterProperties rPr = this.getRPr(true);
        CTSolidColorFillProperties fill = rPr.isSetSolidFill() ? rPr.getSolidFill() : rPr.addNewSolidFill();
        XSLFSheet sheet = this.getParagraph().getParentShape().getSheet();
        XSLFColor col = new XSLFColor(fill, sheet.getTheme(), fill.getSchemeClr(), sheet);
        col.setColor(c);
    }

    @Override
    public PaintStyle getFontColor() {
        XSLFTextShape shape = this.getParagraph().getParentShape();
        boolean hasPlaceholder = shape.getPlaceholder() != null;
        return (PaintStyle)this.fetchCharacterProperty((props, val) -> XSLFTextRun.fetchFontColor(props, val, shape, hasPlaceholder));
    }

    private static void fetchFontColor(CTTextCharacterProperties props, Consumer<PaintStyle> val, XSLFShape shape, boolean hasPlaceholder) {
        XSLFTheme theme;
        XSLFSheet sheet;
        PackagePart pp;
        XSLFPropertiesDelegate.XSLFFillProperties fp;
        PaintStyle ps;
        if (props == null) {
            return;
        }
        CTShapeStyle style = shape.getSpStyle();
        CTSchemeColor phClr = null;
        if (style != null && style.getFontRef() != null) {
            phClr = style.getFontRef().getSchemeClr();
        }
        if ((ps = shape.selectPaint(fp = XSLFPropertiesDelegate.getFillDelegate(props), phClr, pp = (sheet = shape.getSheet()).getPackagePart(), theme = sheet.getTheme(), hasPlaceholder)) != null) {
            val.accept(ps);
        }
    }

    @Override
    public void setFontSize(Double fontSize) {
        CTTextCharacterProperties rPr = this.getRPr(true);
        if (fontSize == null) {
            if (rPr.isSetSz()) {
                rPr.unsetSz();
            }
        } else {
            if (fontSize < 1.0) {
                throw new IllegalArgumentException("Minimum font size is 1pt but was " + fontSize);
            }
            rPr.setSz((int)(100.0 * fontSize));
        }
    }

    @Override
    public Double getFontSize() {
        Double d;
        CTTextNormalAutofit afit;
        CTTextBodyProperties tbp;
        double scale = 1.0;
        XSLFTextShape ps = this.getParagraph().getParentShape();
        if (ps != null && (tbp = ps.getTextBodyPr()) != null && (afit = tbp.getNormAutofit()) != null && afit.isSetFontScale()) {
            scale = (double)POIXMLUnits.parsePercent(afit.xgetFontScale()) / 100000.0;
        }
        return (d = (Double)this.fetchCharacterProperty((props, val) -> {
            if (props.isSetSz()) {
                val.accept((double)props.getSz() * 0.01);
            }
        })) == null ? null : Double.valueOf(d * scale);
    }

    public double getCharacterSpacing() {
        Double d = (Double)this.fetchCharacterProperty((props, val) -> {
            if (props.isSetSpc()) {
                val.accept(Units.toPoints(POIXMLUnits.parseLength(props.xgetSpc())));
            }
        });
        return d == null ? 0.0 : d;
    }

    public void setCharacterSpacing(double spc) {
        CTTextCharacterProperties rPr = this.getRPr(true);
        if (spc == 0.0) {
            if (rPr.isSetSpc()) {
                rPr.unsetSpc();
            }
        } else {
            rPr.setSpc((int)(100.0 * spc));
        }
    }

    @Override
    public void setFontFamily(String typeface) {
        FontGroup fg = FontGroup.getFontGroupFirst(this.getRawText());
        new XSLFFontInfo(fg).setTypeface(typeface);
    }

    @Override
    public void setFontFamily(String typeface, FontGroup fontGroup) {
        new XSLFFontInfo(fontGroup).setTypeface(typeface);
    }

    @Override
    public void setFontInfo(FontInfo fontInfo, FontGroup fontGroup) {
        new XSLFFontInfo(fontGroup).copyFrom(fontInfo);
    }

    @Override
    public String getFontFamily() {
        FontGroup fg = FontGroup.getFontGroupFirst(this.getRawText());
        return new XSLFFontInfo(fg).getTypeface();
    }

    @Override
    public String getFontFamily(FontGroup fontGroup) {
        return new XSLFFontInfo(fontGroup).getTypeface();
    }

    @Override
    public FontInfo getFontInfo(FontGroup fontGroup) {
        XSLFFontInfo fontInfo = new XSLFFontInfo(fontGroup);
        return fontInfo.getTypeface() != null ? fontInfo : null;
    }

    @Override
    public byte getPitchAndFamily() {
        FontFamily family;
        FontGroup fg = FontGroup.getFontGroupFirst(this.getRawText());
        XSLFFontInfo fontInfo = new XSLFFontInfo(fg);
        FontPitch pitch = fontInfo.getPitch();
        if (pitch == null) {
            pitch = FontPitch.VARIABLE;
        }
        if ((family = fontInfo.getFamily()) == null) {
            family = FontFamily.FF_SWISS;
        }
        return FontPitch.getNativeId(pitch, family);
    }

    @Override
    public void setStrikethrough(boolean strike) {
        this.getRPr(true).setStrike(strike ? STTextStrikeType.SNG_STRIKE : STTextStrikeType.NO_STRIKE);
    }

    @Override
    public boolean isStrikethrough() {
        Boolean b = (Boolean)this.fetchCharacterProperty((props, val) -> {
            if (props.isSetStrike()) {
                val.accept(props.getStrike() != STTextStrikeType.NO_STRIKE);
            }
        });
        return b != null && b != false;
    }

    @Override
    public boolean isSuperscript() {
        Boolean b = (Boolean)this.fetchCharacterProperty((props, val) -> {
            if (props.isSetBaseline()) {
                val.accept(POIXMLUnits.parsePercent(props.xgetBaseline()) > 0);
            }
        });
        return b != null && b != false;
    }

    public void setBaselineOffset(double baselineOffset) {
        this.getRPr(true).setBaseline((int)baselineOffset * 1000);
    }

    public void setSuperscript(boolean flag) {
        this.setBaselineOffset(flag ? 30.0 : 0.0);
    }

    public void setSubscript(boolean flag) {
        this.setBaselineOffset(flag ? -25.0 : 0.0);
    }

    @Override
    public boolean isSubscript() {
        Boolean b = (Boolean)this.fetchCharacterProperty((props, val) -> {
            if (props.isSetBaseline()) {
                val.accept(POIXMLUnits.parsePercent(props.xgetBaseline()) < 0);
            }
        });
        return b != null && b != false;
    }

    @Override
    public TextRun.TextCap getTextCap() {
        TextRun.TextCap textCap = (TextRun.TextCap)((Object)this.fetchCharacterProperty((props, val) -> {
            if (props.isSetCap()) {
                val.accept(TextRun.TextCap.values()[props.getCap().intValue() - 1]);
            }
        }));
        return textCap == null ? TextRun.TextCap.NONE : textCap;
    }

    @Override
    public void setBold(boolean bold) {
        this.getRPr(true).setB(bold);
    }

    @Override
    public boolean isBold() {
        Boolean b = (Boolean)this.fetchCharacterProperty((props, val) -> {
            if (props.isSetB()) {
                val.accept(props.getB());
            }
        });
        return b != null && b != false;
    }

    @Override
    public void setItalic(boolean italic) {
        this.getRPr(true).setI(italic);
    }

    @Override
    public boolean isItalic() {
        Boolean b = (Boolean)this.fetchCharacterProperty((props, val) -> {
            if (props.isSetI()) {
                val.accept(props.getI());
            }
        });
        return b != null && b != false;
    }

    @Override
    public void setUnderlined(boolean underline) {
        this.getRPr(true).setU(underline ? STTextUnderlineType.SNG : STTextUnderlineType.NONE);
    }

    @Override
    public boolean isUnderlined() {
        Boolean b = (Boolean)this.fetchCharacterProperty((props, val) -> {
            if (props.isSetU()) {
                val.accept(props.getU() != STTextUnderlineType.NONE);
            }
        });
        return b != null && b != false;
    }

    @Internal
    public CTTextCharacterProperties getRPr(boolean create) {
        if (this._r instanceof CTTextField) {
            CTTextField tf = (CTTextField)this._r;
            if (tf.isSetRPr()) {
                return tf.getRPr();
            }
            if (create) {
                return tf.addNewRPr();
            }
        } else if (this._r instanceof CTTextLineBreak) {
            CTTextLineBreak tlb = (CTTextLineBreak)this._r;
            if (tlb.isSetRPr()) {
                return tlb.getRPr();
            }
            if (create) {
                return tlb.addNewRPr();
            }
        } else {
            CTRegularTextRun tr = (CTRegularTextRun)this._r;
            if (tr.isSetRPr()) {
                return tr.getRPr();
            }
            if (create) {
                return tr.addNewRPr();
            }
        }
        if (this._p.getXmlObject().isSetPPr() && this._p.getXmlObject().getPPr().isSetDefRPr()) {
            return this._p.getXmlObject().getPPr().getDefRPr();
        }
        return null;
    }

    public String toString() {
        return "[" + this.getClass() + "]" + this.getRawText();
    }

    public XSLFHyperlink createHyperlink() {
        XSLFHyperlink hl = this.getHyperlink();
        if (hl != null) {
            return hl;
        }
        CTTextCharacterProperties rPr = this.getRPr(true);
        return new XSLFHyperlink(rPr.addNewHlinkClick(), this._p.getParentShape().getSheet());
    }

    public XSLFHyperlink getHyperlink() {
        CTTextCharacterProperties rPr = this.getRPr(false);
        if (rPr == null) {
            return null;
        }
        CTHyperlink hl = rPr.getHlinkClick();
        if (hl == null) {
            return null;
        }
        return new XSLFHyperlink(hl, this._p.getParentShape().getSheet());
    }

    private <T> T fetchCharacterProperty(CharacterPropertyFetcher.CharPropFetcher<T> fetcher) {
        XSLFTextShape shape = this._p.getParentShape();
        return new CharacterPropertyFetcher<T>(this, fetcher).fetchProperty(shape);
    }

    void copy(XSLFTextRun r) {
        XSLFHyperlink hyperSrc;
        boolean strike;
        boolean underline;
        boolean italic;
        boolean bold;
        Double srcFontSize;
        PaintStyle srcFontColor;
        String srcFontFamily = r.getFontFamily();
        if (srcFontFamily != null && !srcFontFamily.equals(this.getFontFamily())) {
            this.setFontFamily(srcFontFamily);
        }
        if ((srcFontColor = r.getFontColor()) != null && !srcFontColor.equals(this.getFontColor())) {
            this.setFontColor(srcFontColor);
        }
        if ((srcFontSize = r.getFontSize()) == null) {
            if (this.getFontSize() != null) {
                this.setFontSize(null);
            }
        } else if (!srcFontSize.equals(this.getFontSize())) {
            this.setFontSize(srcFontSize);
        }
        if ((bold = r.isBold()) != this.isBold()) {
            this.setBold(bold);
        }
        if ((italic = r.isItalic()) != this.isItalic()) {
            this.setItalic(italic);
        }
        if ((underline = r.isUnderlined()) != this.isUnderlined()) {
            this.setUnderlined(underline);
        }
        if ((strike = r.isStrikethrough()) != this.isStrikethrough()) {
            this.setStrikethrough(strike);
        }
        if ((hyperSrc = r.getHyperlink()) != null) {
            XSLFHyperlink hyperDst = this.getHyperlink();
            hyperDst.copy(hyperSrc);
        }
    }

    @Override
    public TextRun.FieldType getFieldType() {
        CTTextField tf;
        if (this._r instanceof CTTextField && "slidenum".equals((tf = (CTTextField)this._r).getType())) {
            return TextRun.FieldType.SLIDE_NUMBER;
        }
        return null;
    }

    public XSLFTextParagraph getParagraph() {
        return this._p;
    }

    private final class XSLFFontInfo
    implements FontInfo {
        private final FontGroup fontGroup;

        private XSLFFontInfo(FontGroup fontGroup) {
            this.fontGroup = fontGroup != null ? fontGroup : FontGroup.getFontGroupFirst(XSLFTextRun.this.getRawText());
        }

        void copyFrom(FontInfo fontInfo) {
            CTTextFont tf = this.getXmlObject(true);
            if (tf == null) {
                return;
            }
            this.setTypeface(fontInfo.getTypeface());
            this.setCharset(fontInfo.getCharset());
            FontPitch pitch = fontInfo.getPitch();
            FontFamily family = fontInfo.getFamily();
            if (pitch == null && family == null) {
                if (tf.isSetPitchFamily()) {
                    tf.unsetPitchFamily();
                }
            } else {
                this.setPitch(pitch);
                this.setFamily(family);
            }
        }

        @Override
        public String getTypeface() {
            CTTextFont tf = this.getXmlObject(false);
            return tf != null ? tf.getTypeface() : null;
        }

        @Override
        public void setTypeface(String typeface) {
            if (typeface != null) {
                CTTextFont tf = this.getXmlObject(true);
                if (tf != null) {
                    tf.setTypeface(typeface);
                }
                return;
            }
            CTTextCharacterProperties props = XSLFTextRun.this.getRPr(false);
            if (props == null) {
                return;
            }
            FontGroup fg = FontGroup.getFontGroupFirst(XSLFTextRun.this.getRawText());
            switch (fg) {
                default: {
                    if (!props.isSetLatin()) break;
                    props.unsetLatin();
                    break;
                }
                case EAST_ASIAN: {
                    if (!props.isSetEa()) break;
                    props.unsetEa();
                    break;
                }
                case COMPLEX_SCRIPT: {
                    if (!props.isSetCs()) break;
                    props.unsetCs();
                    break;
                }
                case SYMBOL: {
                    if (!props.isSetSym()) break;
                    props.unsetSym();
                }
            }
        }

        @Override
        public FontCharset getCharset() {
            CTTextFont tf = this.getXmlObject(false);
            return tf != null && tf.isSetCharset() ? FontCharset.valueOf(tf.getCharset() & 0xFF) : null;
        }

        @Override
        public void setCharset(FontCharset charset) {
            CTTextFont tf = this.getXmlObject(true);
            if (tf == null) {
                return;
            }
            if (charset != null) {
                tf.setCharset((byte)charset.getNativeId());
            } else if (tf.isSetCharset()) {
                tf.unsetCharset();
            }
        }

        @Override
        public FontFamily getFamily() {
            CTTextFont tf = this.getXmlObject(false);
            return tf != null && tf.isSetPitchFamily() ? FontFamily.valueOfPitchFamily(tf.getPitchFamily()) : null;
        }

        @Override
        public void setFamily(FontFamily family) {
            CTTextFont tf = this.getXmlObject(true);
            if (tf == null || family == null && !tf.isSetPitchFamily()) {
                return;
            }
            FontPitch pitch = tf.isSetPitchFamily() ? FontPitch.valueOfPitchFamily(tf.getPitchFamily()) : FontPitch.VARIABLE;
            byte pitchFamily = FontPitch.getNativeId(pitch, family != null ? family : FontFamily.FF_SWISS);
            tf.setPitchFamily(pitchFamily);
        }

        @Override
        public FontPitch getPitch() {
            CTTextFont tf = this.getXmlObject(false);
            return tf != null && tf.isSetPitchFamily() ? FontPitch.valueOfPitchFamily(tf.getPitchFamily()) : null;
        }

        @Override
        public void setPitch(FontPitch pitch) {
            CTTextFont tf = this.getXmlObject(true);
            if (tf == null || pitch == null && !tf.isSetPitchFamily()) {
                return;
            }
            FontFamily family = tf.isSetPitchFamily() ? FontFamily.valueOfPitchFamily(tf.getPitchFamily()) : FontFamily.FF_SWISS;
            byte pitchFamily = FontPitch.getNativeId(pitch != null ? pitch : FontPitch.VARIABLE, family);
            tf.setPitchFamily(pitchFamily);
        }

        private CTTextFont getXmlObject(boolean create) {
            if (create) {
                return this.getCTTextFont(XSLFTextRun.this.getRPr(true), true);
            }
            return (CTTextFont)XSLFTextRun.this.fetchCharacterProperty((props, val) -> {
                CTTextFont font = this.getCTTextFont(props, false);
                if (font != null) {
                    val.accept(font);
                }
            });
        }

        private CTTextFont getCTTextFont(CTTextCharacterProperties props, boolean create) {
            CTTextFont font;
            if (props == null) {
                return null;
            }
            switch (this.fontGroup) {
                default: {
                    font = props.getLatin();
                    if (font != null || !create) break;
                    font = props.addNewLatin();
                    break;
                }
                case EAST_ASIAN: {
                    font = props.getEa();
                    if (font != null || !create) break;
                    font = props.addNewEa();
                    break;
                }
                case COMPLEX_SCRIPT: {
                    font = props.getCs();
                    if (font != null || !create) break;
                    font = props.addNewCs();
                    break;
                }
                case SYMBOL: {
                    font = props.getSym();
                    if (font != null || !create) break;
                    font = props.addNewSym();
                }
            }
            if (font == null) {
                return null;
            }
            String typeface = font.getTypeface();
            if (typeface == null) {
                typeface = "";
            }
            if (typeface.startsWith("+mj-") || typeface.startsWith("+mn-")) {
                XSLFTheme theme = XSLFTextRun.this._p.getParentShape().getSheet().getTheme();
                CTFontScheme fontTheme = theme.getXmlObject().getThemeElements().getFontScheme();
                CTFontCollection coll = typeface.startsWith("+mj-") ? fontTheme.getMajorFont() : fontTheme.getMinorFont();
                String fgStr = typeface.substring(4);
                font = "ea".equals(fgStr) ? coll.getEa() : ("cs".equals(fgStr) ? coll.getCs() : coll.getLatin());
                if (font == null || font.getTypeface() == null || "".equals(font.getTypeface())) {
                    return null;
                }
            }
            return font;
        }
    }
}


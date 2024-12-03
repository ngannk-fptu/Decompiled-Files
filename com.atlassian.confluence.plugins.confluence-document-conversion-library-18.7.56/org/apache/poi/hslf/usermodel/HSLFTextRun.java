/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.usermodel;

import java.awt.Color;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.common.usermodel.fonts.FontGroup;
import org.apache.poi.common.usermodel.fonts.FontInfo;
import org.apache.poi.hslf.exceptions.HSLFException;
import org.apache.poi.hslf.model.textproperties.BitMaskTextProp;
import org.apache.poi.hslf.model.textproperties.TextProp;
import org.apache.poi.hslf.model.textproperties.TextPropCollection;
import org.apache.poi.hslf.usermodel.HSLFFontInfo;
import org.apache.poi.hslf.usermodel.HSLFHyperlink;
import org.apache.poi.hslf.usermodel.HSLFMasterSheet;
import org.apache.poi.hslf.usermodel.HSLFSheet;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.hslf.usermodel.HSLFTextParagraph;
import org.apache.poi.hslf.usermodel.HSLFTextShape;
import org.apache.poi.sl.draw.DrawPaint;
import org.apache.poi.sl.usermodel.MasterSheet;
import org.apache.poi.sl.usermodel.PaintStyle;
import org.apache.poi.sl.usermodel.Placeholder;
import org.apache.poi.sl.usermodel.Shape;
import org.apache.poi.sl.usermodel.TextRun;
import org.apache.poi.sl.usermodel.TextShape;

public final class HSLFTextRun
implements TextRun {
    private static final Logger LOG = LogManager.getLogger(HSLFTextRun.class);
    private HSLFTextParagraph parentParagraph;
    private String _runText = "";
    private HSLFFontInfo[] cachedFontInfo;
    private HSLFHyperlink link;
    private TextPropCollection characterStyle = new TextPropCollection(1, TextPropCollection.TextPropType.character);

    public HSLFTextRun(HSLFTextParagraph parentParagraph) {
        this.parentParagraph = parentParagraph;
    }

    public TextPropCollection getCharacterStyle() {
        return this.characterStyle;
    }

    public void setCharacterStyle(TextPropCollection characterStyle) {
        this.characterStyle = characterStyle.copy();
        this.characterStyle.updateTextSize(this._runText.length());
    }

    public void updateSheet() {
        if (this.cachedFontInfo != null) {
            for (FontGroup tt : FontGroup.values()) {
                this.setFontInfo(this.cachedFontInfo[tt.ordinal()], tt);
            }
            this.cachedFontInfo = null;
        }
    }

    public int getLength() {
        return this._runText.length();
    }

    @Override
    public String getRawText() {
        return this._runText;
    }

    @Override
    public void setText(String text) {
        if (text == null) {
            throw new HSLFException("text must not be null");
        }
        String newText = HSLFTextParagraph.toInternalString(text);
        if (!newText.equals(this._runText)) {
            this._runText = newText;
            if (HSLFSlideShow.getLoadSavePhase() == HSLFSlideShow.LoadSavePhase.LOADED) {
                this.parentParagraph.setDirty();
            }
        }
    }

    private boolean isCharFlagsTextPropVal(int index) {
        return this.getFlag(index);
    }

    boolean getFlag(int index) {
        BitMaskTextProp prop;
        BitMaskTextProp bitMaskTextProp = prop = this.characterStyle == null ? null : (BitMaskTextProp)this.characterStyle.findByName("char_flags");
        if (prop == null || !prop.getSubPropMatches()[index]) {
            prop = (BitMaskTextProp)this.getMasterProp();
        }
        return prop != null && prop.getSubValue(index);
    }

    private <T extends TextProp> T getMasterProp() {
        int txtype = this.parentParagraph.getRunType();
        HSLFSheet sheet = this.parentParagraph.getSheet();
        if (sheet == null) {
            LOG.atError().log("Sheet is not available");
            return null;
        }
        HSLFMasterSheet master = sheet.getMasterSheet();
        if (master == null) {
            LOG.atWarn().log("MasterSheet is not available");
            return null;
        }
        String name = "char_flags";
        TextPropCollection col = master.getPropCollection(txtype, this.parentParagraph.getIndentLevel(), name, true);
        return col == null ? null : (T)col.findByName(name);
    }

    private void setCharFlagsTextPropVal(int index, boolean value) {
        if (this.getFlag(index) != value) {
            this.setFlag(index, value);
            this.parentParagraph.setDirty();
        }
    }

    public void setCharTextPropVal(String propName, Integer val) {
        this.getTextParagraph().setPropVal(this.characterStyle, propName, val);
    }

    @Override
    public boolean isBold() {
        return this.isCharFlagsTextPropVal(0);
    }

    @Override
    public void setBold(boolean bold) {
        this.setCharFlagsTextPropVal(0, bold);
    }

    @Override
    public boolean isItalic() {
        return this.isCharFlagsTextPropVal(1);
    }

    @Override
    public void setItalic(boolean italic) {
        this.setCharFlagsTextPropVal(1, italic);
    }

    @Override
    public boolean isUnderlined() {
        return this.isCharFlagsTextPropVal(2);
    }

    @Override
    public void setUnderlined(boolean underlined) {
        this.setCharFlagsTextPropVal(2, underlined);
    }

    public boolean isShadowed() {
        return this.isCharFlagsTextPropVal(4);
    }

    public void setShadowed(boolean flag) {
        this.setCharFlagsTextPropVal(4, flag);
    }

    public boolean isEmbossed() {
        return this.isCharFlagsTextPropVal(9);
    }

    public void setEmbossed(boolean flag) {
        this.setCharFlagsTextPropVal(9, flag);
    }

    @Override
    public boolean isStrikethrough() {
        return this.isCharFlagsTextPropVal(8);
    }

    @Override
    public void setStrikethrough(boolean flag) {
        this.setCharFlagsTextPropVal(8, flag);
    }

    public int getSuperscript() {
        Object tp = this.getTextParagraph().getPropVal(this.characterStyle, "superscript");
        return tp == null ? 0 : ((TextProp)tp).getValue();
    }

    public void setSuperscript(int val) {
        this.setCharTextPropVal("superscript", val);
    }

    @Override
    public Double getFontSize() {
        Object tp = this.getTextParagraph().getPropVal(this.characterStyle, "font.size");
        return tp == null ? null : Double.valueOf(((TextProp)tp).getValue());
    }

    @Override
    public void setFontSize(Double fontSize) {
        Integer iFontSize = fontSize == null ? null : Integer.valueOf(fontSize.intValue());
        this.setCharTextPropVal("font.size", iFontSize);
    }

    public int getFontIndex() {
        Object tp = this.getTextParagraph().getPropVal(this.characterStyle, "font.index");
        return tp == null ? -1 : ((TextProp)tp).getValue();
    }

    public void setFontIndex(int idx) {
        this.setCharTextPropVal("font.index", idx);
    }

    @Override
    public void setFontFamily(String typeface) {
        this.setFontFamily(typeface, FontGroup.LATIN);
    }

    @Override
    public void setFontFamily(String typeface, FontGroup fontGroup) {
        this.setFontInfo(new HSLFFontInfo(typeface), fontGroup);
    }

    @Override
    public void setFontInfo(FontInfo fontInfo, FontGroup fontGroup) {
        String propName;
        HSLFSlideShow slideShow;
        FontGroup fg = this.safeFontGroup(fontGroup);
        HSLFSheet sheet = this.parentParagraph.getSheet();
        HSLFSlideShow hSLFSlideShow = slideShow = sheet == null ? null : sheet.getSlideShow();
        if (sheet == null || slideShow == null) {
            if (this.cachedFontInfo == null) {
                this.cachedFontInfo = new HSLFFontInfo[FontGroup.values().length];
            }
            this.cachedFontInfo[fg.ordinal()] = fontInfo != null ? new HSLFFontInfo(fontInfo) : null;
            return;
        }
        switch (fg) {
            default: {
                propName = "ansi.font.index";
                break;
            }
            case COMPLEX_SCRIPT: 
            case EAST_ASIAN: {
                propName = "asian.font.index";
                break;
            }
            case SYMBOL: {
                propName = "symbol.font.index";
            }
        }
        Integer fontIdx = null;
        if (fontInfo != null) {
            fontIdx = slideShow.addFont(fontInfo).getIndex();
        }
        this.setCharTextPropVal("font.index", fontIdx);
        this.setCharTextPropVal(propName, fontIdx);
    }

    @Override
    public String getFontFamily() {
        return this.getFontFamily(null);
    }

    @Override
    public String getFontFamily(FontGroup fontGroup) {
        HSLFFontInfo fi = this.getFontInfo(fontGroup);
        return fi != null ? fi.getTypeface() : null;
    }

    @Override
    public HSLFFontInfo getFontInfo(FontGroup fontGroup) {
        String propName;
        HSLFSlideShow slideShow;
        FontGroup fg = this.safeFontGroup(fontGroup);
        HSLFSheet sheet = this.parentParagraph.getSheet();
        HSLFSlideShow hSLFSlideShow = slideShow = sheet == null ? null : sheet.getSlideShow();
        if (sheet == null || slideShow == null) {
            return this.cachedFontInfo != null ? this.cachedFontInfo[fg.ordinal()] : null;
        }
        switch (fg) {
            default: {
                propName = "font.index,ansi.font.index";
                break;
            }
            case COMPLEX_SCRIPT: 
            case EAST_ASIAN: {
                propName = "asian.font.index";
                break;
            }
            case SYMBOL: {
                propName = "symbol.font.index";
            }
        }
        Object tp = this.getTextParagraph().getPropVal(this.characterStyle, propName);
        return tp != null ? slideShow.getFont(((TextProp)tp).getValue()) : null;
    }

    @Override
    public PaintStyle.SolidPaint getFontColor() {
        Object tp = this.getTextParagraph().getPropVal(this.characterStyle, "font.color");
        if (tp == null) {
            return null;
        }
        Color color = HSLFTextParagraph.getColorFromColorIndexStruct(((TextProp)tp).getValue(), this.parentParagraph.getSheet());
        return DrawPaint.createSolidPaint(color);
    }

    public void setFontColor(int bgr) {
        this.setCharTextPropVal("font.color", bgr);
    }

    @Override
    public void setFontColor(Color color) {
        this.setFontColor(DrawPaint.createSolidPaint(color));
    }

    @Override
    public void setFontColor(PaintStyle color) {
        if (!(color instanceof PaintStyle.SolidPaint)) {
            throw new IllegalArgumentException("HSLF only supports solid paint");
        }
        PaintStyle.SolidPaint sp = (PaintStyle.SolidPaint)color;
        Color c = DrawPaint.applyColorTransform(sp.getSolidColor());
        int rgb = new Color(c.getBlue(), c.getGreen(), c.getRed(), 254).getRGB();
        this.setFontColor(rgb);
    }

    private void setFlag(int index, boolean value) {
        BitMaskTextProp prop = (BitMaskTextProp)this.characterStyle.addWithName("char_flags");
        prop.setSubValue(value, index);
    }

    public HSLFTextParagraph getTextParagraph() {
        return this.parentParagraph;
    }

    @Override
    public TextRun.TextCap getTextCap() {
        return TextRun.TextCap.NONE;
    }

    @Override
    public boolean isSubscript() {
        return this.getSuperscript() < 0;
    }

    @Override
    public boolean isSuperscript() {
        return this.getSuperscript() > 0;
    }

    @Override
    public byte getPitchAndFamily() {
        return 0;
    }

    void setHyperlink(HSLFHyperlink link) {
        this.link = link;
    }

    public HSLFHyperlink getHyperlink() {
        return this.link;
    }

    public HSLFHyperlink createHyperlink() {
        if (this.link == null) {
            this.link = HSLFHyperlink.createHyperlink(this);
            this.parentParagraph.setDirty();
        }
        return this.link;
    }

    @Override
    public TextRun.FieldType getFieldType() {
        Shape ms;
        HSLFTextShape ts = this.getTextParagraph().getParentShape();
        Placeholder ph = ts.getPlaceholder();
        if (ph != null) {
            switch (ph) {
                case SLIDE_NUMBER: {
                    return TextRun.FieldType.SLIDE_NUMBER;
                }
                case DATETIME: {
                    return TextRun.FieldType.DATE_TIME;
                }
            }
        }
        Shape shape = ms = ts.getSheet() instanceof MasterSheet ? ts.getMetroShape() : null;
        if (ms instanceof TextShape) {
            return Stream.of((TextShape)ms).flatMap(tsh -> tsh.getTextParagraphs().stream()).flatMap(tph -> tph.getTextRuns().stream()).findFirst().map(TextRun::getFieldType).orElse(null);
        }
        return null;
    }

    private FontGroup safeFontGroup(FontGroup fontGroup) {
        return fontGroup != null ? fontGroup : FontGroup.getFontGroupFirst(this.getRawText());
    }

    public HSLFTextParagraph getParagraph() {
        return this.parentParagraph;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf.PDFourColours;
import org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf.PDStandardAttributeObject;
import org.apache.pdfbox.pdmodel.graphics.color.PDGamma;

public class PDLayoutAttributeObject
extends PDStandardAttributeObject {
    public static final String OWNER_LAYOUT = "Layout";
    private static final String PLACEMENT = "Placement";
    private static final String WRITING_MODE = "WritingMode";
    private static final String BACKGROUND_COLOR = "BackgroundColor";
    private static final String BORDER_COLOR = "BorderColor";
    private static final String BORDER_STYLE = "BorderStyle";
    private static final String BORDER_THICKNESS = "BorderThickness";
    private static final String PADDING = "Padding";
    private static final String COLOR = "Color";
    private static final String SPACE_BEFORE = "SpaceBefore";
    private static final String SPACE_AFTER = "SpaceAfter";
    private static final String START_INDENT = "StartIndent";
    private static final String END_INDENT = "EndIndent";
    private static final String TEXT_INDENT = "TextIndent";
    private static final String TEXT_ALIGN = "TextAlign";
    private static final String BBOX = "BBox";
    private static final String WIDTH = "Width";
    private static final String HEIGHT = "Height";
    private static final String BLOCK_ALIGN = "BlockAlign";
    private static final String INLINE_ALIGN = "InlineAlign";
    private static final String T_BORDER_STYLE = "TBorderStyle";
    private static final String T_PADDING = "TPadding";
    private static final String BASELINE_SHIFT = "BaselineShift";
    private static final String LINE_HEIGHT = "LineHeight";
    private static final String TEXT_DECORATION_COLOR = "TextDecorationColor";
    private static final String TEXT_DECORATION_THICKNESS = "TextDecorationThickness";
    private static final String TEXT_DECORATION_TYPE = "TextDecorationType";
    private static final String RUBY_ALIGN = "RubyAlign";
    private static final String RUBY_POSITION = "RubyPosition";
    private static final String GLYPH_ORIENTATION_VERTICAL = "GlyphOrientationVertical";
    private static final String COLUMN_COUNT = "ColumnCount";
    private static final String COLUMN_GAP = "ColumnGap";
    private static final String COLUMN_WIDTHS = "ColumnWidths";
    public static final String PLACEMENT_BLOCK = "Block";
    public static final String PLACEMENT_INLINE = "Inline";
    public static final String PLACEMENT_BEFORE = "Before";
    public static final String PLACEMENT_START = "Start";
    public static final String PLACEMENT_END = "End";
    public static final String WRITING_MODE_LRTB = "LrTb";
    public static final String WRITING_MODE_RLTB = "RlTb";
    public static final String WRITING_MODE_TBRL = "TbRl";
    public static final String BORDER_STYLE_NONE = "None";
    public static final String BORDER_STYLE_HIDDEN = "Hidden";
    public static final String BORDER_STYLE_DOTTED = "Dotted";
    public static final String BORDER_STYLE_DASHED = "Dashed";
    public static final String BORDER_STYLE_SOLID = "Solid";
    public static final String BORDER_STYLE_DOUBLE = "Double";
    public static final String BORDER_STYLE_GROOVE = "Groove";
    public static final String BORDER_STYLE_RIDGE = "Ridge";
    public static final String BORDER_STYLE_INSET = "Inset";
    public static final String BORDER_STYLE_OUTSET = "Outset";
    public static final String TEXT_ALIGN_START = "Start";
    public static final String TEXT_ALIGN_CENTER = "Center";
    public static final String TEXT_ALIGN_END = "End";
    public static final String TEXT_ALIGN_JUSTIFY = "Justify";
    public static final String WIDTH_AUTO = "Auto";
    public static final String HEIGHT_AUTO = "Auto";
    public static final String BLOCK_ALIGN_BEFORE = "Before";
    public static final String BLOCK_ALIGN_MIDDLE = "Middle";
    public static final String BLOCK_ALIGN_AFTER = "After";
    public static final String BLOCK_ALIGN_JUSTIFY = "Justify";
    public static final String INLINE_ALIGN_START = "Start";
    public static final String INLINE_ALIGN_CENTER = "Center";
    public static final String INLINE_ALIGN_END = "End";
    public static final String LINE_HEIGHT_NORMAL = "Normal";
    public static final String LINE_HEIGHT_AUTO = "Auto";
    public static final String TEXT_DECORATION_TYPE_NONE = "None";
    public static final String TEXT_DECORATION_TYPE_UNDERLINE = "Underline";
    public static final String TEXT_DECORATION_TYPE_OVERLINE = "Overline";
    public static final String TEXT_DECORATION_TYPE_LINE_THROUGH = "LineThrough";
    public static final String RUBY_ALIGN_START = "Start";
    public static final String RUBY_ALIGN_CENTER = "Center";
    public static final String RUBY_ALIGN_END = "End";
    public static final String RUBY_ALIGN_JUSTIFY = "Justify";
    public static final String RUBY_ALIGN_DISTRIBUTE = "Distribute";
    public static final String RUBY_POSITION_BEFORE = "Before";
    public static final String RUBY_POSITION_AFTER = "After";
    public static final String RUBY_POSITION_WARICHU = "Warichu";
    public static final String RUBY_POSITION_INLINE = "Inline";
    public static final String GLYPH_ORIENTATION_VERTICAL_AUTO = "Auto";
    public static final String GLYPH_ORIENTATION_VERTICAL_MINUS_180_DEGREES = "-180";
    public static final String GLYPH_ORIENTATION_VERTICAL_MINUS_90_DEGREES = "-90";
    public static final String GLYPH_ORIENTATION_VERTICAL_ZERO_DEGREES = "0";
    public static final String GLYPH_ORIENTATION_VERTICAL_90_DEGREES = "90";
    public static final String GLYPH_ORIENTATION_VERTICAL_180_DEGREES = "180";
    public static final String GLYPH_ORIENTATION_VERTICAL_270_DEGREES = "270";
    public static final String GLYPH_ORIENTATION_VERTICAL_360_DEGREES = "360";

    public PDLayoutAttributeObject() {
        this.setOwner(OWNER_LAYOUT);
    }

    public PDLayoutAttributeObject(COSDictionary dictionary) {
        super(dictionary);
    }

    public String getPlacement() {
        return this.getName(PLACEMENT, "Inline");
    }

    public void setPlacement(String placement) {
        this.setName(PLACEMENT, placement);
    }

    public String getWritingMode() {
        return this.getName(WRITING_MODE, WRITING_MODE_LRTB);
    }

    public void setWritingMode(String writingMode) {
        this.setName(WRITING_MODE, writingMode);
    }

    public PDGamma getBackgroundColor() {
        return this.getColor(BACKGROUND_COLOR);
    }

    public void setBackgroundColor(PDGamma backgroundColor) {
        this.setColor(BACKGROUND_COLOR, backgroundColor);
    }

    public Object getBorderColors() {
        return this.getColorOrFourColors(BORDER_COLOR);
    }

    public void setAllBorderColors(PDGamma borderColor) {
        this.setColor(BORDER_COLOR, borderColor);
    }

    public void setBorderColors(PDFourColours borderColors) {
        this.setFourColors(BORDER_COLOR, borderColors);
    }

    public Object getBorderStyle() {
        return this.getNameOrArrayOfName(BORDER_STYLE, "None");
    }

    public void setAllBorderStyles(String borderStyle) {
        this.setName(BORDER_STYLE, borderStyle);
    }

    public void setBorderStyles(String[] borderStyles) {
        this.setArrayOfName(BORDER_STYLE, borderStyles);
    }

    public Object getBorderThickness() {
        return this.getNumberOrArrayOfNumber(BORDER_THICKNESS, -1.0f);
    }

    public void setAllBorderThicknesses(float borderThickness) {
        this.setNumber(BORDER_THICKNESS, borderThickness);
    }

    public void setAllBorderThicknesses(int borderThickness) {
        this.setNumber(BORDER_THICKNESS, borderThickness);
    }

    public void setBorderThicknesses(float[] borderThicknesses) {
        this.setArrayOfNumber(BORDER_THICKNESS, borderThicknesses);
    }

    public Object getPadding() {
        return this.getNumberOrArrayOfNumber(PADDING, 0.0f);
    }

    public void setAllPaddings(float padding) {
        this.setNumber(PADDING, padding);
    }

    public void setAllPaddings(int padding) {
        this.setNumber(PADDING, padding);
    }

    public void setPaddings(float[] paddings) {
        this.setArrayOfNumber(PADDING, paddings);
    }

    public PDGamma getColor() {
        return this.getColor(COLOR);
    }

    public void setColor(PDGamma color) {
        this.setColor(COLOR, color);
    }

    public float getSpaceBefore() {
        return this.getNumber(SPACE_BEFORE, 0.0f);
    }

    public void setSpaceBefore(float spaceBefore) {
        this.setNumber(SPACE_BEFORE, spaceBefore);
    }

    public void setSpaceBefore(int spaceBefore) {
        this.setNumber(SPACE_BEFORE, spaceBefore);
    }

    public float getSpaceAfter() {
        return this.getNumber(SPACE_AFTER, 0.0f);
    }

    public void setSpaceAfter(float spaceAfter) {
        this.setNumber(SPACE_AFTER, spaceAfter);
    }

    public void setSpaceAfter(int spaceAfter) {
        this.setNumber(SPACE_AFTER, spaceAfter);
    }

    public float getStartIndent() {
        return this.getNumber(START_INDENT, 0.0f);
    }

    public void setStartIndent(float startIndent) {
        this.setNumber(START_INDENT, startIndent);
    }

    public void setStartIndent(int startIndent) {
        this.setNumber(START_INDENT, startIndent);
    }

    public float getEndIndent() {
        return this.getNumber(END_INDENT, 0.0f);
    }

    public void setEndIndent(float endIndent) {
        this.setNumber(END_INDENT, endIndent);
    }

    public void setEndIndent(int endIndent) {
        this.setNumber(END_INDENT, endIndent);
    }

    public float getTextIndent() {
        return this.getNumber(TEXT_INDENT, 0.0f);
    }

    public void setTextIndent(float textIndent) {
        this.setNumber(TEXT_INDENT, textIndent);
    }

    public void setTextIndent(int textIndent) {
        this.setNumber(TEXT_INDENT, textIndent);
    }

    public String getTextAlign() {
        return this.getName(TEXT_ALIGN, "Start");
    }

    public void setTextAlign(String textIndent) {
        this.setName(TEXT_ALIGN, textIndent);
    }

    public PDRectangle getBBox() {
        COSArray array = (COSArray)this.getCOSObject().getDictionaryObject(BBOX);
        if (array != null) {
            return new PDRectangle(array);
        }
        return null;
    }

    public void setBBox(PDRectangle bbox) {
        String name = BBOX;
        COSBase oldValue = this.getCOSObject().getDictionaryObject(name);
        this.getCOSObject().setItem(name, (COSObjectable)bbox);
        COSBase newValue = bbox == null ? null : bbox.getCOSObject();
        this.potentiallyNotifyChanged(oldValue, newValue);
    }

    public Object getWidth() {
        return this.getNumberOrName(WIDTH, "Auto");
    }

    public void setWidthAuto() {
        this.setName(WIDTH, "Auto");
    }

    public void setWidth(float width) {
        this.setNumber(WIDTH, width);
    }

    public void setWidth(int width) {
        this.setNumber(WIDTH, width);
    }

    public Object getHeight() {
        return this.getNumberOrName(HEIGHT, "Auto");
    }

    public void setHeightAuto() {
        this.setName(HEIGHT, "Auto");
    }

    public void setHeight(float height) {
        this.setNumber(HEIGHT, height);
    }

    public void setHeight(int height) {
        this.setNumber(HEIGHT, height);
    }

    public String getBlockAlign() {
        return this.getName(BLOCK_ALIGN, "Before");
    }

    public void setBlockAlign(String blockAlign) {
        this.setName(BLOCK_ALIGN, blockAlign);
    }

    public String getInlineAlign() {
        return this.getName(INLINE_ALIGN, "Start");
    }

    public void setInlineAlign(String inlineAlign) {
        this.setName(INLINE_ALIGN, inlineAlign);
    }

    public Object getTBorderStyle() {
        return this.getNameOrArrayOfName(T_BORDER_STYLE, "None");
    }

    public void setAllTBorderStyles(String tBorderStyle) {
        this.setName(T_BORDER_STYLE, tBorderStyle);
    }

    public void setTBorderStyles(String[] tBorderStyles) {
        this.setArrayOfName(T_BORDER_STYLE, tBorderStyles);
    }

    public Object getTPadding() {
        return this.getNumberOrArrayOfNumber(T_PADDING, 0.0f);
    }

    public void setAllTPaddings(float tPadding) {
        this.setNumber(T_PADDING, tPadding);
    }

    public void setAllTPaddings(int tPadding) {
        this.setNumber(T_PADDING, tPadding);
    }

    public void setTPaddings(float[] tPaddings) {
        this.setArrayOfNumber(T_PADDING, tPaddings);
    }

    public float getBaselineShift() {
        return this.getNumber(BASELINE_SHIFT, 0.0f);
    }

    public void setBaselineShift(float baselineShift) {
        this.setNumber(BASELINE_SHIFT, baselineShift);
    }

    public void setBaselineShift(int baselineShift) {
        this.setNumber(BASELINE_SHIFT, baselineShift);
    }

    public Object getLineHeight() {
        return this.getNumberOrName(LINE_HEIGHT, LINE_HEIGHT_NORMAL);
    }

    public void setLineHeightNormal() {
        this.setName(LINE_HEIGHT, LINE_HEIGHT_NORMAL);
    }

    public void setLineHeightAuto() {
        this.setName(LINE_HEIGHT, "Auto");
    }

    public void setLineHeight(float lineHeight) {
        this.setNumber(LINE_HEIGHT, lineHeight);
    }

    public void setLineHeight(int lineHeight) {
        this.setNumber(LINE_HEIGHT, lineHeight);
    }

    public PDGamma getTextDecorationColor() {
        return this.getColor(TEXT_DECORATION_COLOR);
    }

    public void setTextDecorationColor(PDGamma textDecorationColor) {
        this.setColor(TEXT_DECORATION_COLOR, textDecorationColor);
    }

    public float getTextDecorationThickness() {
        return this.getNumber(TEXT_DECORATION_THICKNESS);
    }

    public void setTextDecorationThickness(float textDecorationThickness) {
        this.setNumber(TEXT_DECORATION_THICKNESS, textDecorationThickness);
    }

    public void setTextDecorationThickness(int textDecorationThickness) {
        this.setNumber(TEXT_DECORATION_THICKNESS, textDecorationThickness);
    }

    public String getTextDecorationType() {
        return this.getName(TEXT_DECORATION_TYPE, "None");
    }

    public void setTextDecorationType(String textDecorationType) {
        this.setName(TEXT_DECORATION_TYPE, textDecorationType);
    }

    public String getRubyAlign() {
        return this.getName(RUBY_ALIGN, RUBY_ALIGN_DISTRIBUTE);
    }

    public void setRubyAlign(String rubyAlign) {
        this.setName(RUBY_ALIGN, rubyAlign);
    }

    public String getRubyPosition() {
        return this.getName(RUBY_POSITION, "Before");
    }

    public void setRubyPosition(String rubyPosition) {
        this.setName(RUBY_POSITION, rubyPosition);
    }

    public String getGlyphOrientationVertical() {
        return this.getName(GLYPH_ORIENTATION_VERTICAL, "Auto");
    }

    public void setGlyphOrientationVertical(String glyphOrientationVertical) {
        this.setName(GLYPH_ORIENTATION_VERTICAL, glyphOrientationVertical);
    }

    public int getColumnCount() {
        return this.getInteger(COLUMN_COUNT, 1);
    }

    public void setColumnCount(int columnCount) {
        this.setInteger(COLUMN_COUNT, columnCount);
    }

    public Object getColumnGap() {
        return this.getNumberOrArrayOfNumber(COLUMN_GAP, -1.0f);
    }

    public void setColumnGap(float columnGap) {
        this.setNumber(COLUMN_GAP, columnGap);
    }

    public void setColumnGap(int columnGap) {
        this.setNumber(COLUMN_GAP, columnGap);
    }

    public void setColumnGaps(float[] columnGaps) {
        this.setArrayOfNumber(COLUMN_GAP, columnGaps);
    }

    public Object getColumnWidths() {
        return this.getNumberOrArrayOfNumber(COLUMN_WIDTHS, -1.0f);
    }

    public void setAllColumnWidths(float columnWidth) {
        this.setNumber(COLUMN_WIDTHS, columnWidth);
    }

    public void setAllColumnWidths(int columnWidth) {
        this.setNumber(COLUMN_WIDTHS, columnWidth);
    }

    public void setColumnWidths(float[] columnWidths) {
        this.setArrayOfNumber(COLUMN_WIDTHS, columnWidths);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder().append(super.toString());
        if (this.isSpecified(PLACEMENT)) {
            sb.append(", Placement=").append(this.getPlacement());
        }
        if (this.isSpecified(WRITING_MODE)) {
            sb.append(", WritingMode=").append(this.getWritingMode());
        }
        if (this.isSpecified(BACKGROUND_COLOR)) {
            sb.append(", BackgroundColor=").append(this.getBackgroundColor());
        }
        if (this.isSpecified(BORDER_COLOR)) {
            sb.append(", BorderColor=").append(this.getBorderColors());
        }
        if (this.isSpecified(BORDER_STYLE)) {
            Object borderStyle = this.getBorderStyle();
            sb.append(", BorderStyle=");
            if (borderStyle instanceof String[]) {
                sb.append(PDLayoutAttributeObject.arrayToString((String[])borderStyle));
            } else {
                sb.append(borderStyle);
            }
        }
        if (this.isSpecified(BORDER_THICKNESS)) {
            Object borderThickness = this.getBorderThickness();
            sb.append(", BorderThickness=");
            if (borderThickness instanceof float[]) {
                sb.append(PDLayoutAttributeObject.arrayToString((float[])borderThickness));
            } else {
                sb.append(borderThickness);
            }
        }
        if (this.isSpecified(PADDING)) {
            Object padding = this.getPadding();
            sb.append(", Padding=");
            if (padding instanceof float[]) {
                sb.append(PDLayoutAttributeObject.arrayToString((float[])padding));
            } else {
                sb.append(padding);
            }
        }
        if (this.isSpecified(COLOR)) {
            sb.append(", Color=").append(this.getColor());
        }
        if (this.isSpecified(SPACE_BEFORE)) {
            sb.append(", SpaceBefore=").append(this.getSpaceBefore());
        }
        if (this.isSpecified(SPACE_AFTER)) {
            sb.append(", SpaceAfter=").append(this.getSpaceAfter());
        }
        if (this.isSpecified(START_INDENT)) {
            sb.append(", StartIndent=").append(this.getStartIndent());
        }
        if (this.isSpecified(END_INDENT)) {
            sb.append(", EndIndent=").append(this.getEndIndent());
        }
        if (this.isSpecified(TEXT_INDENT)) {
            sb.append(", TextIndent=").append(this.getTextIndent());
        }
        if (this.isSpecified(TEXT_ALIGN)) {
            sb.append(", TextAlign=").append(this.getTextAlign());
        }
        if (this.isSpecified(BBOX)) {
            sb.append(", BBox=").append(this.getBBox());
        }
        if (this.isSpecified(WIDTH)) {
            sb.append(", Width=").append(this.getWidth());
        }
        if (this.isSpecified(HEIGHT)) {
            sb.append(", Height=").append(this.getHeight());
        }
        if (this.isSpecified(BLOCK_ALIGN)) {
            sb.append(", BlockAlign=").append(this.getBlockAlign());
        }
        if (this.isSpecified(INLINE_ALIGN)) {
            sb.append(", InlineAlign=").append(this.getInlineAlign());
        }
        if (this.isSpecified(T_BORDER_STYLE)) {
            Object tBorderStyle = this.getTBorderStyle();
            sb.append(", TBorderStyle=");
            if (tBorderStyle instanceof String[]) {
                sb.append(PDLayoutAttributeObject.arrayToString((String[])tBorderStyle));
            } else {
                sb.append(tBorderStyle);
            }
        }
        if (this.isSpecified(T_PADDING)) {
            Object tPadding = this.getTPadding();
            sb.append(", TPadding=");
            if (tPadding instanceof float[]) {
                sb.append(PDLayoutAttributeObject.arrayToString((float[])tPadding));
            } else {
                sb.append(tPadding);
            }
        }
        if (this.isSpecified(BASELINE_SHIFT)) {
            sb.append(", BaselineShift=").append(this.getBaselineShift());
        }
        if (this.isSpecified(LINE_HEIGHT)) {
            sb.append(", LineHeight=").append(this.getLineHeight());
        }
        if (this.isSpecified(TEXT_DECORATION_COLOR)) {
            sb.append(", TextDecorationColor=").append(this.getTextDecorationColor());
        }
        if (this.isSpecified(TEXT_DECORATION_THICKNESS)) {
            sb.append(", TextDecorationThickness=").append(this.getTextDecorationThickness());
        }
        if (this.isSpecified(TEXT_DECORATION_TYPE)) {
            sb.append(", TextDecorationType=").append(this.getTextDecorationType());
        }
        if (this.isSpecified(RUBY_ALIGN)) {
            sb.append(", RubyAlign=").append(this.getRubyAlign());
        }
        if (this.isSpecified(RUBY_POSITION)) {
            sb.append(", RubyPosition=").append(this.getRubyPosition());
        }
        if (this.isSpecified(GLYPH_ORIENTATION_VERTICAL)) {
            sb.append(", GlyphOrientationVertical=").append(this.getGlyphOrientationVertical());
        }
        if (this.isSpecified(COLUMN_COUNT)) {
            sb.append(", ColumnCount=").append(this.getColumnCount());
        }
        if (this.isSpecified(COLUMN_GAP)) {
            Object columnGap = this.getColumnGap();
            sb.append(", ColumnGap=");
            if (columnGap instanceof float[]) {
                sb.append(PDLayoutAttributeObject.arrayToString((float[])columnGap));
            } else {
                sb.append(columnGap);
            }
        }
        if (this.isSpecified(COLUMN_WIDTHS)) {
            Object columnWidth = this.getColumnWidths();
            sb.append(", ColumnWidths=");
            if (columnWidth instanceof float[]) {
                sb.append(PDLayoutAttributeObject.arrayToString((float[])columnWidth));
            } else {
                sb.append(columnWidth);
            }
        }
        return sb.toString();
    }
}


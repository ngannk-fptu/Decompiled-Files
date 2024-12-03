/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.gvt.text;

import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.Map;
import java.util.Set;

public interface GVTAttributedCharacterIterator
extends AttributedCharacterIterator {
    public void setString(String var1);

    public void setString(AttributedString var1);

    public void setAttributeArray(TextAttribute var1, Object[] var2, int var3, int var4);

    public Set getAllAttributeKeys();

    @Override
    public Object getAttribute(AttributedCharacterIterator.Attribute var1);

    public Map getAttributes();

    @Override
    public int getRunLimit();

    @Override
    public int getRunLimit(AttributedCharacterIterator.Attribute var1);

    public int getRunLimit(Set var1);

    @Override
    public int getRunStart();

    @Override
    public int getRunStart(AttributedCharacterIterator.Attribute var1);

    public int getRunStart(Set var1);

    @Override
    public Object clone();

    @Override
    public char current();

    @Override
    public char first();

    @Override
    public int getBeginIndex();

    @Override
    public int getEndIndex();

    @Override
    public int getIndex();

    @Override
    public char last();

    @Override
    public char next();

    @Override
    public char previous();

    @Override
    public char setIndex(int var1);

    public static interface AttributeFilter {
        public AttributedCharacterIterator mutateAttributes(AttributedCharacterIterator var1);
    }

    public static class TextAttribute
    extends AttributedCharacterIterator.Attribute {
        public static final TextAttribute FLOW_PARAGRAPH = new TextAttribute("FLOW_PARAGRAPH");
        public static final TextAttribute FLOW_EMPTY_PARAGRAPH = new TextAttribute("FLOW_EMPTY_PARAGRAPH");
        public static final TextAttribute FLOW_LINE_BREAK = new TextAttribute("FLOW_LINE_BREAK");
        public static final TextAttribute FLOW_REGIONS = new TextAttribute("FLOW_REGIONS");
        public static final TextAttribute LINE_HEIGHT = new TextAttribute("LINE_HEIGHT");
        public static final TextAttribute PREFORMATTED = new TextAttribute("PREFORMATTED");
        public static final TextAttribute TEXT_COMPOUND_DELIMITER = new TextAttribute("TEXT_COMPOUND_DELIMITER");
        public static final TextAttribute TEXT_COMPOUND_ID = new TextAttribute("TEXT_COMPOUND_ID");
        public static final TextAttribute ANCHOR_TYPE = new TextAttribute("ANCHOR_TYPE");
        public static final TextAttribute EXPLICIT_LAYOUT = new TextAttribute("EXPLICIT_LAYOUT");
        public static final TextAttribute X = new TextAttribute("X");
        public static final TextAttribute Y = new TextAttribute("Y");
        public static final TextAttribute DX = new TextAttribute("DX");
        public static final TextAttribute DY = new TextAttribute("DY");
        public static final TextAttribute ROTATION = new TextAttribute("ROTATION");
        public static final TextAttribute PAINT_INFO = new TextAttribute("PAINT_INFO");
        public static final TextAttribute BBOX_WIDTH = new TextAttribute("BBOX_WIDTH");
        public static final TextAttribute LENGTH_ADJUST = new TextAttribute("LENGTH_ADJUST");
        public static final TextAttribute CUSTOM_SPACING = new TextAttribute("CUSTOM_SPACING");
        public static final TextAttribute KERNING = new TextAttribute("KERNING");
        public static final TextAttribute LETTER_SPACING = new TextAttribute("LETTER_SPACING");
        public static final TextAttribute WORD_SPACING = new TextAttribute("WORD_SPACING");
        public static final TextAttribute TEXTPATH = new TextAttribute("TEXTPATH");
        public static final TextAttribute FONT_VARIANT = new TextAttribute("FONT_VARIANT");
        public static final TextAttribute BASELINE_SHIFT = new TextAttribute("BASELINE_SHIFT");
        public static final TextAttribute WRITING_MODE = new TextAttribute("WRITING_MODE");
        public static final TextAttribute VERTICAL_ORIENTATION = new TextAttribute("VERTICAL_ORIENTATION");
        public static final TextAttribute VERTICAL_ORIENTATION_ANGLE = new TextAttribute("VERTICAL_ORIENTATION_ANGLE");
        public static final TextAttribute HORIZONTAL_ORIENTATION_ANGLE = new TextAttribute("HORIZONTAL_ORIENTATION_ANGLE");
        public static final TextAttribute GVT_FONT_FAMILIES = new TextAttribute("GVT_FONT_FAMILIES");
        public static final TextAttribute GVT_FONTS = new TextAttribute("GVT_FONTS");
        public static final TextAttribute GVT_FONT = new TextAttribute("GVT_FONT");
        public static final TextAttribute ALT_GLYPH_HANDLER = new TextAttribute("ALT_GLYPH_HANDLER");
        public static final TextAttribute BIDI_LEVEL = new TextAttribute("BIDI_LEVEL");
        public static final TextAttribute CHAR_INDEX = new TextAttribute("CHAR_INDEX");
        public static final TextAttribute ARABIC_FORM = new TextAttribute("ARABIC_FORM");
        public static final TextAttribute SCRIPT = new TextAttribute("SCRIPT");
        public static final TextAttribute LANGUAGE = new TextAttribute("LANGUAGE");
        public static final Integer WRITING_MODE_LTR = 1;
        public static final Integer WRITING_MODE_RTL = 2;
        public static final Integer WRITING_MODE_TTB = 3;
        public static final Integer ORIENTATION_ANGLE = 1;
        public static final Integer ORIENTATION_AUTO = 2;
        public static final Integer SMALL_CAPS = 16;
        public static final Integer UNDERLINE_ON = java.awt.font.TextAttribute.UNDERLINE_ON;
        public static final Boolean OVERLINE_ON = Boolean.TRUE;
        public static final Boolean STRIKETHROUGH_ON = java.awt.font.TextAttribute.STRIKETHROUGH_ON;
        public static final Integer ADJUST_SPACING = 0;
        public static final Integer ADJUST_ALL = 1;
        public static final Integer ARABIC_NONE = 0;
        public static final Integer ARABIC_ISOLATED = 1;
        public static final Integer ARABIC_TERMINAL = 2;
        public static final Integer ARABIC_INITIAL = 3;
        public static final Integer ARABIC_MEDIAL = 4;

        public TextAttribute(String s) {
            super(s);
        }
    }
}


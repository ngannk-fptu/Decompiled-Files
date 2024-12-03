/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Chunk;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.SplitCharacter;
import com.lowagie.text.Utilities;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.DefaultSplitCharacter;
import com.lowagie.text.pdf.HyphenationEvent;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfFont;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public class PdfChunk {
    private static final char[] singleSpace = new char[]{' '};
    private static final PdfChunk[] thisChunk = new PdfChunk[1];
    private static final float ITALIC_ANGLE = 0.21256f;
    private static final Map<String, Object> keysAttributes = new HashMap<String, Object>();
    private static final Map<String, Object> keysNoStroke = new HashMap<String, Object>();
    protected String value = "";
    protected String encoding = "Cp1252";
    protected PdfFont font;
    protected BaseFont baseFont;
    protected SplitCharacter splitCharacter;
    protected Map<String, Object> attributes = new HashMap<String, Object>();
    protected Map<String, Object> noStroke = new HashMap<String, Object>();
    protected boolean newlineSplit;
    protected Image image;
    protected float offsetX;
    protected float offsetY;
    protected boolean changeLeading = false;

    PdfChunk(String string, PdfChunk other) {
        PdfChunk.thisChunk[0] = this;
        this.value = string;
        this.font = other.font;
        this.attributes = other.attributes;
        this.noStroke = other.noStroke;
        this.baseFont = other.baseFont;
        Object[] obj = (Object[])this.attributes.get("IMAGE");
        if (obj == null) {
            this.image = null;
        } else {
            this.image = (Image)obj[0];
            this.offsetX = ((Float)obj[1]).floatValue();
            this.offsetY = ((Float)obj[2]).floatValue();
            this.changeLeading = (Boolean)obj[3];
        }
        this.encoding = this.font.getFont().getEncoding();
        this.splitCharacter = (SplitCharacter)this.noStroke.get("SPLITCHARACTER");
        if (this.splitCharacter == null) {
            this.splitCharacter = DefaultSplitCharacter.DEFAULT;
        }
    }

    PdfChunk(Chunk chunk, PdfAction action) {
        Object[][] unders;
        Object[] obj;
        PdfChunk.thisChunk[0] = this;
        this.value = chunk.getContent();
        Font f = chunk.getFont();
        float size = f.getSize();
        if (size == -1.0f) {
            size = 12.0f;
        }
        this.baseFont = f.getBaseFont();
        int style = f.getStyle();
        if (style == -1) {
            style = 0;
        }
        if (this.baseFont == null) {
            this.baseFont = f.getCalculatedBaseFont(false);
        } else {
            if ((style & 1) != 0) {
                this.attributes.put("TEXTRENDERMODE", new Object[]{2, Float.valueOf(size / 30.0f), null});
            }
            if ((style & 2) != 0) {
                this.attributes.put("SKEW", new float[]{0.0f, 0.21256f});
            }
        }
        this.font = new PdfFont(this.baseFont, size);
        Map<String, Object> attr = chunk.getChunkAttributes();
        if (attr != null) {
            for (Map.Entry<String, Object> entry : attr.entrySet()) {
                String name = entry.getKey();
                if (keysAttributes.containsKey(name)) {
                    this.attributes.put(name, entry.getValue());
                    continue;
                }
                if (!keysNoStroke.containsKey(name)) continue;
                this.noStroke.put(name, entry.getValue());
            }
            if ("".equals(attr.get("GENERICTAG"))) {
                this.attributes.put("GENERICTAG", chunk.getContent());
            }
        }
        if (f.isUnderlined()) {
            obj = new Object[]{null, new float[]{0.0f, 0.06666667f, 0.0f, -0.33333334f, 0.0f}};
            unders = Utilities.addToArray((Object[][])this.attributes.get("UNDERLINE"), obj);
            this.attributes.put("UNDERLINE", unders);
        }
        if (f.isStrikethru()) {
            obj = new Object[]{null, new float[]{0.0f, 0.06666667f, 0.0f, 0.33333334f, 0.0f}};
            unders = Utilities.addToArray((Object[][])this.attributes.get("UNDERLINE"), obj);
            this.attributes.put("UNDERLINE", unders);
        }
        if (action != null) {
            this.attributes.put("ACTION", action);
        }
        this.noStroke.put("COLOR", f.getColor());
        this.noStroke.put("ENCODING", this.font.getFont().getEncoding());
        obj = (Object[])this.attributes.get("IMAGE");
        if (obj == null) {
            this.image = null;
        } else {
            this.attributes.remove("HSCALE");
            this.image = (Image)obj[0];
            this.offsetX = ((Float)obj[1]).floatValue();
            this.offsetY = ((Float)obj[2]).floatValue();
            this.changeLeading = (Boolean)obj[3];
        }
        this.font.setImage(this.image);
        Float hs = (Float)this.attributes.get("HSCALE");
        if (hs != null) {
            this.font.setHorizontalScaling(hs.floatValue());
        }
        this.encoding = this.font.getFont().getEncoding();
        this.splitCharacter = (SplitCharacter)this.noStroke.get("SPLITCHARACTER");
        if (this.splitCharacter == null) {
            this.splitCharacter = DefaultSplitCharacter.DEFAULT;
        }
    }

    public int getUnicodeEquivalent(int c) {
        return this.baseFont.getUnicodeEquivalent(c);
    }

    protected int getWord(String text, int start) {
        int len = text.length();
        while (start < len && Character.isLetter(text.charAt(start))) {
            ++start;
        }
        return start;
    }

    PdfChunk split(float width) {
        int wordIdx;
        int currentPosition;
        this.newlineSplit = false;
        if (this.image != null) {
            if (this.image.getScaledWidth() > width) {
                PdfChunk pc = new PdfChunk("\ufffc", this);
                this.value = "";
                this.attributes = new HashMap<String, Object>();
                this.image = null;
                this.font = PdfFont.getDefaultFont();
                return pc;
            }
            return null;
        }
        HyphenationEvent hyphenationEvent = (HyphenationEvent)this.noStroke.get("HYPHENATION");
        int splitPosition = -1;
        float currentWidth = 0.0f;
        int lastSpace = -1;
        float lastSpaceWidth = 0.0f;
        int length = this.value.length();
        char[] valueArray = this.value.toCharArray();
        char character = '\u0000';
        BaseFont ft = this.font.getFont();
        boolean surrogate = false;
        if (ft.getFontType() == 2 && ft.getUnicodeEquivalent(32) != 32) {
            for (currentPosition = 0; currentPosition < length; ++currentPosition) {
                char cidChar = valueArray[currentPosition];
                character = (char)ft.getUnicodeEquivalent(cidChar);
                if (character == '\n') {
                    this.newlineSplit = true;
                    String returnValue = this.value.substring(currentPosition + 1);
                    this.value = this.value.substring(0, currentPosition);
                    if (this.value.length() < 1) {
                        this.value = "\u0001";
                    }
                    return new PdfChunk(returnValue, this);
                }
                currentWidth += this.getCharWidth(cidChar);
                if (character == ' ') {
                    lastSpace = currentPosition + 1;
                    lastSpaceWidth = currentWidth;
                }
                if (!(currentWidth > width)) {
                    if (!this.splitCharacter.isSplitCharacter(0, currentPosition, length, valueArray, thisChunk)) continue;
                    splitPosition = currentPosition + 1;
                    continue;
                }
                break;
            }
        } else {
            while (currentPosition < length) {
                character = valueArray[currentPosition];
                if (character == '\r' || character == '\n') {
                    this.newlineSplit = true;
                    int inc = 1;
                    if (character == '\r' && currentPosition + 1 < length && valueArray[currentPosition + 1] == '\n') {
                        inc = 2;
                    }
                    String returnValue = this.value.substring(currentPosition + inc);
                    this.value = this.value.substring(0, currentPosition);
                    if (this.value.length() < 1) {
                        this.value = " ";
                    }
                    return new PdfChunk(returnValue, this);
                }
                surrogate = Utilities.isSurrogatePair(valueArray, currentPosition);
                currentWidth = surrogate ? (currentWidth += this.getCharWidth(Utilities.convertToUtf32(valueArray[currentPosition], valueArray[currentPosition + 1]))) : (currentWidth += this.getCharWidth(character));
                if (character == ' ') {
                    lastSpace = currentPosition + 1;
                    lastSpaceWidth = currentWidth;
                }
                if (surrogate) {
                    ++currentPosition;
                }
                if (!(currentWidth > width)) {
                    if (this.splitCharacter.isSplitCharacter(0, currentPosition, length, valueArray, null)) {
                        splitPosition = currentPosition + 1;
                    }
                    ++currentPosition;
                    continue;
                }
                break;
            }
        }
        if (currentPosition == length) {
            return null;
        }
        if (splitPosition < 0) {
            String returnValue = this.value;
            this.value = "";
            return new PdfChunk(returnValue, this);
        }
        if (lastSpace > splitPosition && this.splitCharacter.isSplitCharacter(0, 0, 1, singleSpace, null)) {
            splitPosition = lastSpace;
        }
        if (hyphenationEvent != null && lastSpace >= 0 && lastSpace < currentPosition && (wordIdx = this.getWord(this.value, lastSpace)) > lastSpace) {
            String pre = hyphenationEvent.getHyphenatedWordPre(this.value.substring(lastSpace, wordIdx), this.font.getFont(), this.font.size(), width - lastSpaceWidth);
            String post = hyphenationEvent.getHyphenatedWordPost();
            if (pre.length() > 0) {
                String returnValue = post + this.value.substring(wordIdx);
                this.value = this.trim(this.value.substring(0, lastSpace) + pre);
                return new PdfChunk(returnValue, this);
            }
        }
        String returnValue = this.value.substring(splitPosition);
        this.value = this.trim(this.value.substring(0, splitPosition));
        return new PdfChunk(returnValue, this);
    }

    PdfChunk truncate(float width) {
        int currentPosition;
        if (this.image != null) {
            if (this.image.getScaledWidth() > width) {
                PdfChunk pc = new PdfChunk("", this);
                this.value = "";
                this.attributes.remove("IMAGE");
                this.image = null;
                this.font = PdfFont.getDefaultFont();
                return pc;
            }
            return null;
        }
        float currentWidth = 0.0f;
        if (width < this.font.width()) {
            String returnValue = this.value.substring(1);
            this.value = this.value.substring(0, 1);
            return new PdfChunk(returnValue, this);
        }
        int length = this.value.length();
        boolean surrogate = false;
        for (currentPosition = 0; currentPosition < length; ++currentPosition) {
            surrogate = Utilities.isSurrogatePair(this.value, currentPosition);
            currentWidth = surrogate ? (currentWidth += this.getCharWidth(Utilities.convertToUtf32(this.value, currentPosition))) : (currentWidth += this.getCharWidth(this.value.charAt(currentPosition)));
            if (currentWidth > width) break;
            if (!surrogate) continue;
            ++currentPosition;
        }
        if (currentPosition == length) {
            return null;
        }
        if (currentPosition == 0) {
            currentPosition = 1;
            if (surrogate) {
                ++currentPosition;
            }
        }
        String returnValue = this.value.substring(currentPosition);
        this.value = this.value.substring(0, currentPosition);
        return new PdfChunk(returnValue, this);
    }

    PdfFont font() {
        return this.font;
    }

    Color color() {
        return (Color)this.noStroke.get("COLOR");
    }

    float width() {
        if (this.isAttribute("CHAR_SPACING")) {
            Float cs = (Float)this.getAttribute("CHAR_SPACING");
            return this.font.width(this.value) + (float)this.value.length() * cs.floatValue();
        }
        return this.font.width(this.value);
    }

    public boolean isNewlineSplit() {
        return this.newlineSplit;
    }

    public float getWidthCorrected(float charSpacing, float wordSpacing) {
        if (this.image != null) {
            return this.image.getScaledWidth() + charSpacing;
        }
        int numberOfSpaces = 0;
        int idx = -1;
        while ((idx = this.value.indexOf(32, idx + 1)) >= 0) {
            ++numberOfSpaces;
        }
        return this.width() + ((float)this.value.length() * charSpacing + (float)numberOfSpaces * wordSpacing);
    }

    public float getTextRise() {
        Float f = (Float)this.getAttribute("SUBSUPSCRIPT");
        if (f != null) {
            return f.floatValue();
        }
        return 0.0f;
    }

    public float trimLastSpace() {
        BaseFont ft = this.font.getFont();
        if (ft.getFontType() == 2 && ft.getUnicodeEquivalent(32) != 32) {
            if (this.value.length() > 1 && this.value.endsWith("\u0001")) {
                this.value = this.value.substring(0, this.value.length() - 1);
                return this.font.width(1);
            }
        } else if (this.value.length() > 1 && this.value.endsWith(" ")) {
            this.value = this.value.substring(0, this.value.length() - 1);
            return this.font.width(32);
        }
        return 0.0f;
    }

    public float trimFirstSpace() {
        BaseFont ft = this.font.getFont();
        if (ft.getFontType() == 2 && ft.getUnicodeEquivalent(32) != 32) {
            if (this.value.length() > 1 && this.value.startsWith("\u0001")) {
                this.value = this.value.substring(1);
                return this.font.width(1);
            }
        } else if (this.value.length() > 1 && this.value.startsWith(" ")) {
            this.value = this.value.substring(1);
            return this.font.width(32);
        }
        return 0.0f;
    }

    Object getAttribute(String name) {
        if (this.attributes.containsKey(name)) {
            return this.attributes.get(name);
        }
        return this.noStroke.get(name);
    }

    boolean isAttribute(String name) {
        if (this.attributes.containsKey(name)) {
            return true;
        }
        return this.noStroke.containsKey(name);
    }

    boolean isStroked() {
        return !this.attributes.isEmpty();
    }

    boolean isSeparator() {
        return this.isAttribute("SEPARATOR");
    }

    boolean isHorizontalSeparator() {
        if (this.isAttribute("SEPARATOR")) {
            Object[] o = (Object[])this.getAttribute("SEPARATOR");
            return (Boolean)o[1] == false;
        }
        return false;
    }

    boolean isTab() {
        return this.isAttribute("TAB");
    }

    void adjustLeft(float newValue) {
        Object[] o = (Object[])this.attributes.get("TAB");
        if (o != null) {
            this.attributes.put("TAB", new Object[]{o[0], o[1], o[2], Float.valueOf(newValue)});
        }
    }

    boolean isImage() {
        return this.image != null;
    }

    Image getImage() {
        return this.image;
    }

    void setImageOffsetX(float offsetX) {
        this.offsetX = offsetX;
    }

    float getImageOffsetX() {
        return this.offsetX;
    }

    void setImageOffsetY(float offsetY) {
        this.offsetY = offsetY;
    }

    float getImageOffsetY() {
        return this.offsetY;
    }

    void setValue(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }

    boolean isSpecialEncoding() {
        return this.encoding.equals("UnicodeBigUnmarked") || this.encoding.equals("Identity-H");
    }

    String getEncoding() {
        return this.encoding;
    }

    int length() {
        return this.value.length();
    }

    int lengthUtf32() {
        if (!"Identity-H".equals(this.encoding)) {
            return this.value.length();
        }
        int total = 0;
        int len = this.value.length();
        for (int k = 0; k < len; ++k) {
            if (Utilities.isSurrogateHigh(this.value.charAt(k))) {
                ++k;
            }
            ++total;
        }
        return total;
    }

    boolean isExtSplitCharacter(int start, int current, int end, char[] cc, PdfChunk[] ck) {
        return this.splitCharacter.isSplitCharacter(start, current, end, cc, ck);
    }

    String trim(String string) {
        BaseFont ft = this.font.getFont();
        if (ft.getFontType() == 2 && ft.getUnicodeEquivalent(32) != 32) {
            while (string.endsWith("\u0001")) {
                string = string.substring(0, string.length() - 1);
            }
        } else {
            while (string.endsWith(" ") || string.endsWith("\t")) {
                string = string.substring(0, string.length() - 1);
            }
        }
        return string;
    }

    public boolean changeLeading() {
        return this.changeLeading;
    }

    float getCharWidth(int c) {
        if (PdfChunk.noPrint(c)) {
            return 0.0f;
        }
        if (this.isAttribute("CHAR_SPACING")) {
            Float cs = (Float)this.getAttribute("CHAR_SPACING");
            return this.font.width(c) + cs.floatValue();
        }
        return this.font.width(c);
    }

    public static boolean noPrint(int c) {
        return c >= 8203 && c <= 8207 || c >= 8234 && c <= 8238;
    }

    static {
        keysAttributes.put("ACTION", null);
        keysAttributes.put("UNDERLINE", null);
        keysAttributes.put("REMOTEGOTO", null);
        keysAttributes.put("LOCALGOTO", null);
        keysAttributes.put("LOCALDESTINATION", null);
        keysAttributes.put("GENERICTAG", null);
        keysAttributes.put("NEWPAGE", null);
        keysAttributes.put("IMAGE", null);
        keysAttributes.put("BACKGROUND", null);
        keysAttributes.put("PDFANNOTATION", null);
        keysAttributes.put("SKEW", null);
        keysAttributes.put("HSCALE", null);
        keysAttributes.put("SEPARATOR", null);
        keysAttributes.put("TAB", null);
        keysAttributes.put("CHAR_SPACING", null);
        keysNoStroke.put("SUBSUPSCRIPT", null);
        keysNoStroke.put("SPLITCHARACTER", null);
        keysNoStroke.put("HYPHENATION", null);
        keysNoStroke.put("TEXTRENDERMODE", null);
    }
}


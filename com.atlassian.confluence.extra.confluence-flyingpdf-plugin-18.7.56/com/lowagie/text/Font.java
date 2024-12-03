/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.lowagie.text;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.FontFactory;
import com.lowagie.text.pdf.BaseFont;
import java.awt.Color;
import javax.annotation.Nullable;

public class Font
implements Comparable {
    public static final int COURIER = 0;
    public static final int HELVETICA = 1;
    public static final int TIMES_ROMAN = 2;
    public static final int SYMBOL = 3;
    public static final int ZAPFDINGBATS = 4;
    public static final int NORMAL = 0;
    public static final int BOLD = 1;
    public static final int ITALIC = 2;
    public static final int UNDERLINE = 4;
    public static final int STRIKETHRU = 8;
    public static final int BOLDITALIC = 3;
    public static final int UNDEFINED = -1;
    public static final int DEFAULTSIZE = 12;
    private int family = -1;
    private float size = -1.0f;
    private int style = -1;
    private Color color = null;
    private BaseFont baseFont = null;

    public Font(Font other) {
        this.family = other.family;
        this.size = other.size;
        this.style = other.style;
        this.color = other.color;
        this.baseFont = other.baseFont;
    }

    public Font(int family, float size, int style, @Nullable Color color) {
        this.family = family;
        this.size = size;
        this.style = style;
        this.color = color;
    }

    public Font(BaseFont bf, float size, int style, Color color) {
        this.baseFont = bf;
        this.size = size;
        this.style = style;
        this.color = color;
    }

    public Font(BaseFont bf, float size, int style) {
        this(bf, size, style, null);
    }

    public Font(BaseFont bf, float size) {
        this(bf, size, -1, null);
    }

    public Font(BaseFont bf) {
        this(bf, -1.0f, -1, null);
    }

    public Font(int family, float size, int style) {
        this(family, size, style, null);
    }

    public Font(int family, float size) {
        this(family, size, -1, null);
    }

    public Font(int family) {
        this(family, -1.0f, -1, null);
    }

    public Font() {
        this(-1, -1.0f, -1, null);
    }

    public int compareTo(Object object) {
        if (object == null) {
            return -1;
        }
        try {
            Font font = (Font)object;
            if (this.baseFont != null && !this.baseFont.equals(font.getBaseFont())) {
                return -2;
            }
            if (this.family != font.getFamily()) {
                return 1;
            }
            if (this.size != font.getSize()) {
                return 2;
            }
            if (this.style != font.getStyle()) {
                return 3;
            }
            if (this.color == null) {
                if (font.color == null) {
                    return 0;
                }
                return 4;
            }
            if (font.color == null) {
                return 4;
            }
            if (this.color.equals(font.getColor())) {
                return 0;
            }
            return 4;
        }
        catch (ClassCastException cce) {
            return -3;
        }
    }

    public int getFamily() {
        return this.family;
    }

    public String getFamilyname() {
        String tmp = "unknown";
        switch (this.getFamily()) {
            case 0: {
                return "Courier";
            }
            case 1: {
                return "Helvetica";
            }
            case 2: {
                return "Times-Roman";
            }
            case 3: {
                return "Symbol";
            }
            case 4: {
                return "ZapfDingbats";
            }
        }
        if (this.baseFont != null) {
            String[][] names;
            for (String[] name : names = this.baseFont.getFamilyFontName()) {
                if ("0".equals(name[2])) {
                    return name[3];
                }
                if ("1033".equals(name[2])) {
                    tmp = name[3];
                }
                if (!"".equals(name[2])) continue;
                tmp = name[3];
            }
        }
        return tmp;
    }

    public void setFamily(String family) {
        this.family = Font.getFamilyIndex(family);
    }

    public static int getFamilyIndex(String family) {
        if (family.equalsIgnoreCase("Courier")) {
            return 0;
        }
        if (family.equalsIgnoreCase("Helvetica")) {
            return 1;
        }
        if (family.equalsIgnoreCase("Times-Roman")) {
            return 2;
        }
        if (family.equalsIgnoreCase("Symbol")) {
            return 3;
        }
        if (family.equalsIgnoreCase("ZapfDingbats")) {
            return 4;
        }
        return -1;
    }

    public float getSize() {
        return this.size;
    }

    public float getCalculatedSize() {
        float s = this.size;
        if (s == -1.0f) {
            s = 12.0f;
        }
        return s;
    }

    public float getCalculatedLeading(float linespacing) {
        return linespacing * this.getCalculatedSize();
    }

    public void setSize(float size) {
        this.size = size;
    }

    public int getStyle() {
        return this.style;
    }

    public int getCalculatedStyle() {
        int style = this.style;
        if (style == -1) {
            style = 0;
        }
        if (this.baseFont != null) {
            return style;
        }
        if (this.family == 3 || this.family == 4) {
            return style;
        }
        return style & 0xFFFFFFFC;
    }

    public boolean isBold() {
        if (this.style == -1) {
            return false;
        }
        return (this.style & 1) == 1;
    }

    public boolean isItalic() {
        if (this.style == -1) {
            return false;
        }
        return (this.style & 2) == 2;
    }

    public boolean isUnderlined() {
        if (this.style == -1) {
            return false;
        }
        return (this.style & 4) == 4;
    }

    public boolean isStrikethru() {
        if (this.style == -1) {
            return false;
        }
        return (this.style & 8) == 8;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public void setStyle(String style) {
        if (this.style == -1) {
            this.style = 0;
        }
        this.style |= Font.getStyleValue(style);
    }

    public static int getStyleValue(String style) {
        int s = 0;
        if (style.contains("normal")) {
            s |= 0;
        }
        if (style.contains("bold")) {
            s |= 1;
        }
        if (style.contains("italic")) {
            s |= 2;
        }
        if (style.contains("oblique")) {
            s |= 2;
        }
        if (style.contains("underline")) {
            s |= 4;
        }
        if (style.contains("line-through")) {
            s |= 8;
        }
        return s;
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setColor(int red, int green, int blue) {
        this.color = new Color(red, green, blue);
    }

    public BaseFont getBaseFont() {
        return this.baseFont;
    }

    public BaseFont getCalculatedBaseFont(boolean specialEncoding) {
        if (this.baseFont != null) {
            return this.baseFont;
        }
        int style = this.style;
        if (style == -1) {
            style = 0;
        }
        String fontName = "Helvetica";
        String encoding = "Cp1252";
        BaseFont cfont = null;
        block1 : switch (this.family) {
            case 0: {
                switch (style & 3) {
                    case 1: {
                        fontName = "Courier-Bold";
                        break block1;
                    }
                    case 2: {
                        fontName = "Courier-Oblique";
                        break block1;
                    }
                    case 3: {
                        fontName = "Courier-BoldOblique";
                        break block1;
                    }
                }
                fontName = "Courier";
                break;
            }
            case 2: {
                switch (style & 3) {
                    case 1: {
                        fontName = "Times-Bold";
                        break block1;
                    }
                    case 2: {
                        fontName = "Times-Italic";
                        break block1;
                    }
                    case 3: {
                        fontName = "Times-BoldItalic";
                        break block1;
                    }
                }
                fontName = "Times-Roman";
                break;
            }
            case 3: {
                fontName = "Symbol";
                if (!specialEncoding) break;
                encoding = "Symbol";
                break;
            }
            case 4: {
                fontName = "ZapfDingbats";
                if (!specialEncoding) break;
                encoding = "ZapfDingbats";
                break;
            }
            default: {
                switch (style & 3) {
                    case 1: {
                        fontName = "Helvetica-Bold";
                        break block1;
                    }
                    case 2: {
                        fontName = "Helvetica-Oblique";
                        break block1;
                    }
                    case 3: {
                        fontName = "Helvetica-BoldOblique";
                        break block1;
                    }
                }
                fontName = "Helvetica";
            }
        }
        try {
            cfont = BaseFont.createFont(fontName, encoding, false);
        }
        catch (Exception ee) {
            throw new ExceptionConverter(ee);
        }
        return cfont;
    }

    public boolean isStandardFont() {
        return this.family == -1 && this.size == -1.0f && this.style == -1 && this.color == null && this.baseFont == null;
    }

    public Font difference(Font font) {
        Color dColor;
        if (font == null) {
            return this;
        }
        float dSize = font.size;
        if (dSize == -1.0f) {
            dSize = this.size;
        }
        int dStyle = -1;
        int style1 = this.style;
        int style2 = font.getStyle();
        if (style1 != -1 || style2 != -1) {
            if (style1 == -1) {
                style1 = 0;
            }
            if (style2 == -1) {
                style2 = 0;
            }
            dStyle = style1 | style2;
        }
        if ((dColor = font.color) == null) {
            dColor = this.color;
        }
        if (font.baseFont != null) {
            return new Font(font.baseFont, dSize, dStyle, dColor);
        }
        if (font.getFamily() != -1) {
            return new Font(font.family, dSize, dStyle, dColor);
        }
        if (this.baseFont != null) {
            if (dStyle == style1) {
                return new Font(this.baseFont, dSize, dStyle, dColor);
            }
            return FontFactory.getFont(this.getFamilyname(), dSize, dStyle, dColor);
        }
        return new Font(this.family, dSize, dStyle, dColor);
    }
}


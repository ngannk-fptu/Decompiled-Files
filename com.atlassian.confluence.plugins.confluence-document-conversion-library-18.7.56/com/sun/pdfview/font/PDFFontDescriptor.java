/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.font;

import com.sun.pdfview.PDFObject;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

public class PDFFontDescriptor {
    public static final int FIXED_PITCH = 1;
    public static final int SERIF = 2;
    public static final int SYMBOLIC = 4;
    public static final int SCRIPT = 8;
    public static final int NONSYMBOLIC = 32;
    public static final int ITALIC = 64;
    public static final int ALLCAP = 65536;
    public static final int SMALLCAP = 131072;
    public static final int FORCEBOLD = 262144;
    private int ascent;
    private int capHeight;
    private int descent;
    private int flags;
    private String fontFamily;
    private String fontName;
    private String fontStretch;
    private int fontWeight;
    private int italicAngle;
    private int stemV;
    private int avgWidth = 0;
    private PDFObject fontFile;
    private PDFObject fontFile2;
    private PDFObject fontFile3;
    private int leading = 0;
    private int maxWidth = 0;
    private int missingWidth = 0;
    private int stemH = 0;
    private int xHeight = 0;
    private PDFObject charSet;
    private Rectangle2D.Float fontBBox;

    public PDFFontDescriptor(String basefont) {
        this.setFontName(basefont);
    }

    public PDFFontDescriptor(PDFObject obj) throws IOException {
        this.setAscent(obj.getDictRef("Ascent").getIntValue());
        this.setCapHeight(obj.getDictRef("CapHeight").getIntValue());
        this.setDescent(obj.getDictRef("Descent").getIntValue());
        this.setFlags(obj.getDictRef("Flags").getIntValue());
        this.setFontName(obj.getDictRef("FontName").getStringValue());
        this.setItalicAngle(obj.getDictRef("ItalicAngle").getIntValue());
        this.setStemV(obj.getDictRef("StemV").getIntValue());
        PDFObject[] bboxdef = obj.getDictRef("FontBBox").getArray();
        float[] bboxfdef = new float[4];
        for (int i = 0; i < 4; ++i) {
            bboxfdef[i] = bboxdef[i].getFloatValue();
        }
        this.setFontBBox(new Rectangle2D.Float(bboxfdef[0], bboxfdef[1], bboxfdef[2] - bboxfdef[0], bboxfdef[3] - bboxfdef[1]));
        if (obj.getDictionary().containsKey("AvgWidth")) {
            this.setAvgWidth(obj.getDictRef("AvgWidth").getIntValue());
        }
        if (obj.getDictionary().containsKey("FontFile")) {
            this.setFontFile(obj.getDictRef("FontFile"));
        }
        if (obj.getDictionary().containsKey("FontFile2")) {
            this.setFontFile2(obj.getDictRef("FontFile2"));
        }
        if (obj.getDictionary().containsKey("FontFile3")) {
            this.setFontFile3(obj.getDictRef("FontFile3"));
        }
        if (obj.getDictionary().containsKey("Leading")) {
            this.setLeading(obj.getDictRef("Leading").getIntValue());
        }
        if (obj.getDictionary().containsKey("MaxWidth")) {
            this.setMaxWidth(obj.getDictRef("MaxWidth").getIntValue());
        }
        if (obj.getDictionary().containsKey("MissingWidth")) {
            this.setMissingWidth(obj.getDictRef("MissingWidth").getIntValue());
        }
        if (obj.getDictionary().containsKey("StemH")) {
            this.setStemH(obj.getDictRef("StemH").getIntValue());
        }
        if (obj.getDictionary().containsKey("XHeight")) {
            this.setXHeight(obj.getDictRef("XHeight").getIntValue());
        }
        if (obj.getDictionary().containsKey("CharSet")) {
            this.setCharSet(obj.getDictRef("CharSet"));
        }
        if (obj.getDictionary().containsKey("FontFamily")) {
            this.setFontFamily(obj.getDictRef("FontFamily").getStringValue());
        }
        if (obj.getDictionary().containsKey("FontWeight")) {
            this.setFontWeight(obj.getDictRef("FontWeight").getIntValue());
        }
        if (obj.getDictionary().containsKey("FontStretch")) {
            this.setFontStretch(obj.getDictRef("FontStretch").getStringValue());
        }
    }

    public int getAscent() {
        return this.ascent;
    }

    public void setAscent(int ascent) {
        this.ascent = ascent;
    }

    public int getCapHeight() {
        return this.capHeight;
    }

    public void setCapHeight(int capHeight) {
        this.capHeight = capHeight;
    }

    public int getDescent() {
        return this.descent;
    }

    public void setDescent(int descent) {
        this.descent = descent;
    }

    public int getFlags() {
        return this.flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public String getFontFamily() {
        return this.fontFamily;
    }

    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }

    public String getFontName() {
        return this.fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public String getFontStretch() {
        return this.fontStretch;
    }

    public void setFontStretch(String fontStretch) {
        this.fontStretch = fontStretch;
    }

    public int getFontWeight() {
        return this.fontWeight;
    }

    public void setFontWeight(int fontWeight) {
        this.fontWeight = fontWeight;
    }

    public int getItalicAngle() {
        return this.italicAngle;
    }

    public void setItalicAngle(int italicAngle) {
        this.italicAngle = italicAngle;
    }

    public int getStemV() {
        return this.stemV;
    }

    public void setStemV(int stemV) {
        this.stemV = stemV;
    }

    public int getAvgWidth() {
        return this.avgWidth;
    }

    public void setAvgWidth(int avgWidth) {
        this.avgWidth = avgWidth;
    }

    public PDFObject getFontFile() {
        return this.fontFile;
    }

    public void setFontFile(PDFObject fontFile) {
        this.fontFile = fontFile;
    }

    public PDFObject getFontFile2() {
        return this.fontFile2;
    }

    public void setFontFile2(PDFObject fontFile2) {
        this.fontFile2 = fontFile2;
    }

    public PDFObject getFontFile3() {
        return this.fontFile3;
    }

    public void setFontFile3(PDFObject fontFile3) {
        this.fontFile3 = fontFile3;
    }

    public int getLeading() {
        return this.leading;
    }

    public void setLeading(int leading) {
        this.leading = leading;
    }

    public int getMaxWidth() {
        return this.maxWidth;
    }

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    public int getMissingWidth() {
        return this.missingWidth;
    }

    public void setMissingWidth(int missingWidth) {
        this.missingWidth = missingWidth;
    }

    public int getStemH() {
        return this.stemH;
    }

    public void setStemH(int stemH) {
        this.stemH = stemH;
    }

    public int getXHeight() {
        return this.xHeight;
    }

    public void setXHeight(int xHeight) {
        this.xHeight = xHeight;
    }

    public PDFObject getCharSet() {
        return this.charSet;
    }

    public void setCharSet(PDFObject charSet) {
        this.charSet = charSet;
    }

    public Rectangle2D.Float getFontBBox() {
        return this.fontBBox;
    }

    public void setFontBBox(Rectangle2D.Float fontBBox) {
        this.fontBBox = fontBBox;
    }
}


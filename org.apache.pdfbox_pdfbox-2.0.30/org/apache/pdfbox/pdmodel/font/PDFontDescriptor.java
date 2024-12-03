/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.font;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.font.PDPanose;

public final class PDFontDescriptor
implements COSObjectable {
    private static final int FLAG_FIXED_PITCH = 1;
    private static final int FLAG_SERIF = 2;
    private static final int FLAG_SYMBOLIC = 4;
    private static final int FLAG_SCRIPT = 8;
    private static final int FLAG_NON_SYMBOLIC = 32;
    private static final int FLAG_ITALIC = 64;
    private static final int FLAG_ALL_CAP = 65536;
    private static final int FLAG_SMALL_CAP = 131072;
    private static final int FLAG_FORCE_BOLD = 262144;
    private final COSDictionary dic;
    private float xHeight = Float.NEGATIVE_INFINITY;
    private float capHeight = Float.NEGATIVE_INFINITY;
    private int flags = -1;

    PDFontDescriptor() {
        this.dic = new COSDictionary();
        this.dic.setItem(COSName.TYPE, (COSBase)COSName.FONT_DESC);
    }

    public PDFontDescriptor(COSDictionary desc) {
        this.dic = desc;
    }

    public boolean isFixedPitch() {
        return this.isFlagBitOn(1);
    }

    public void setFixedPitch(boolean flag) {
        this.setFlagBit(1, flag);
    }

    public boolean isSerif() {
        return this.isFlagBitOn(2);
    }

    public void setSerif(boolean flag) {
        this.setFlagBit(2, flag);
    }

    public boolean isSymbolic() {
        return this.isFlagBitOn(4);
    }

    public void setSymbolic(boolean flag) {
        this.setFlagBit(4, flag);
    }

    public boolean isScript() {
        return this.isFlagBitOn(8);
    }

    public void setScript(boolean flag) {
        this.setFlagBit(8, flag);
    }

    public boolean isNonSymbolic() {
        return this.isFlagBitOn(32);
    }

    public void setNonSymbolic(boolean flag) {
        this.setFlagBit(32, flag);
    }

    public boolean isItalic() {
        return this.isFlagBitOn(64);
    }

    public void setItalic(boolean flag) {
        this.setFlagBit(64, flag);
    }

    public boolean isAllCap() {
        return this.isFlagBitOn(65536);
    }

    public void setAllCap(boolean flag) {
        this.setFlagBit(65536, flag);
    }

    public boolean isSmallCap() {
        return this.isFlagBitOn(131072);
    }

    public void setSmallCap(boolean flag) {
        this.setFlagBit(131072, flag);
    }

    public boolean isForceBold() {
        return this.isFlagBitOn(262144);
    }

    public void setForceBold(boolean flag) {
        this.setFlagBit(262144, flag);
    }

    private boolean isFlagBitOn(int bit) {
        return (this.getFlags() & bit) != 0;
    }

    private void setFlagBit(int bit, boolean value) {
        int flags = this.getFlags();
        flags = value ? (flags |= bit) : (flags &= ~bit);
        this.setFlags(flags);
    }

    @Override
    public COSDictionary getCOSObject() {
        return this.dic;
    }

    public String getFontName() {
        String retval = null;
        COSBase base = this.dic.getDictionaryObject(COSName.FONT_NAME);
        if (base instanceof COSName) {
            retval = ((COSName)base).getName();
        }
        return retval;
    }

    public void setFontName(String fontName) {
        COSName name = null;
        if (fontName != null) {
            name = COSName.getPDFName(fontName);
        }
        this.dic.setItem(COSName.FONT_NAME, (COSBase)name);
    }

    public String getFontFamily() {
        String retval = null;
        COSString name = (COSString)this.dic.getDictionaryObject(COSName.FONT_FAMILY);
        if (name != null) {
            retval = name.getString();
        }
        return retval;
    }

    public void setFontFamily(String fontFamily) {
        COSString name = null;
        if (fontFamily != null) {
            name = new COSString(fontFamily);
        }
        this.dic.setItem(COSName.FONT_FAMILY, (COSBase)name);
    }

    public float getFontWeight() {
        return this.dic.getFloat(COSName.FONT_WEIGHT, 0.0f);
    }

    public void setFontWeight(float fontWeight) {
        this.dic.setFloat(COSName.FONT_WEIGHT, fontWeight);
    }

    public String getFontStretch() {
        String retval = null;
        COSName name = (COSName)this.dic.getDictionaryObject(COSName.FONT_STRETCH);
        if (name != null) {
            retval = name.getName();
        }
        return retval;
    }

    public void setFontStretch(String fontStretch) {
        COSName name = null;
        if (fontStretch != null) {
            name = COSName.getPDFName(fontStretch);
        }
        this.dic.setItem(COSName.FONT_STRETCH, (COSBase)name);
    }

    public int getFlags() {
        if (this.flags == -1) {
            this.flags = this.dic.getInt(COSName.FLAGS, 0);
        }
        return this.flags;
    }

    public void setFlags(int flags) {
        this.dic.setInt(COSName.FLAGS, flags);
        this.flags = flags;
    }

    public PDRectangle getFontBoundingBox() {
        COSArray rect = this.dic.getCOSArray(COSName.FONT_BBOX);
        PDRectangle retval = null;
        if (rect != null) {
            retval = new PDRectangle(rect);
        }
        return retval;
    }

    public void setFontBoundingBox(PDRectangle rect) {
        COSArray array = null;
        if (rect != null) {
            array = rect.getCOSArray();
        }
        this.dic.setItem(COSName.FONT_BBOX, (COSBase)array);
    }

    public float getItalicAngle() {
        return this.dic.getFloat(COSName.ITALIC_ANGLE, 0.0f);
    }

    public void setItalicAngle(float angle) {
        this.dic.setFloat(COSName.ITALIC_ANGLE, angle);
    }

    public float getAscent() {
        return this.dic.getFloat(COSName.ASCENT, 0.0f);
    }

    public void setAscent(float ascent) {
        this.dic.setFloat(COSName.ASCENT, ascent);
    }

    public float getDescent() {
        return this.dic.getFloat(COSName.DESCENT, 0.0f);
    }

    public void setDescent(float descent) {
        this.dic.setFloat(COSName.DESCENT, descent);
    }

    public float getLeading() {
        return this.dic.getFloat(COSName.LEADING, 0.0f);
    }

    public void setLeading(float leading) {
        this.dic.setFloat(COSName.LEADING, leading);
    }

    public float getCapHeight() {
        if (this.capHeight == Float.NEGATIVE_INFINITY) {
            this.capHeight = Math.abs(this.dic.getFloat(COSName.CAP_HEIGHT, 0.0f));
        }
        return this.capHeight;
    }

    public void setCapHeight(float capHeight) {
        this.dic.setFloat(COSName.CAP_HEIGHT, capHeight);
        this.capHeight = capHeight;
    }

    public float getXHeight() {
        if (this.xHeight == Float.NEGATIVE_INFINITY) {
            this.xHeight = Math.abs(this.dic.getFloat(COSName.XHEIGHT, 0.0f));
        }
        return this.xHeight;
    }

    public void setXHeight(float xHeight) {
        this.dic.setFloat(COSName.XHEIGHT, xHeight);
        this.xHeight = xHeight;
    }

    public float getStemV() {
        return this.dic.getFloat(COSName.STEM_V, 0.0f);
    }

    public void setStemV(float stemV) {
        this.dic.setFloat(COSName.STEM_V, stemV);
    }

    public float getStemH() {
        return this.dic.getFloat(COSName.STEM_H, 0.0f);
    }

    public void setStemH(float stemH) {
        this.dic.setFloat(COSName.STEM_H, stemH);
    }

    public float getAverageWidth() {
        return this.dic.getFloat(COSName.AVG_WIDTH, 0.0f);
    }

    public void setAverageWidth(float averageWidth) {
        this.dic.setFloat(COSName.AVG_WIDTH, averageWidth);
    }

    public float getMaxWidth() {
        return this.dic.getFloat(COSName.MAX_WIDTH, 0.0f);
    }

    public void setMaxWidth(float maxWidth) {
        this.dic.setFloat(COSName.MAX_WIDTH, maxWidth);
    }

    public boolean hasWidths() {
        return this.dic.containsKey(COSName.WIDTHS) || this.dic.containsKey(COSName.MISSING_WIDTH);
    }

    public boolean hasMissingWidth() {
        return this.dic.containsKey(COSName.MISSING_WIDTH);
    }

    public float getMissingWidth() {
        return this.dic.getFloat(COSName.MISSING_WIDTH, 0.0f);
    }

    public void setMissingWidth(float missingWidth) {
        this.dic.setFloat(COSName.MISSING_WIDTH, missingWidth);
    }

    public String getCharSet() {
        String retval = null;
        COSString name = (COSString)this.dic.getDictionaryObject(COSName.CHAR_SET);
        if (name != null) {
            retval = name.getString();
        }
        return retval;
    }

    public void setCharacterSet(String charSet) {
        COSString name = null;
        if (charSet != null) {
            name = new COSString(charSet);
        }
        this.dic.setItem(COSName.CHAR_SET, (COSBase)name);
    }

    public PDStream getFontFile() {
        PDStream retval = null;
        COSBase obj = this.dic.getDictionaryObject(COSName.FONT_FILE);
        if (obj instanceof COSStream) {
            retval = new PDStream((COSStream)obj);
        }
        return retval;
    }

    public void setFontFile(PDStream type1Stream) {
        this.dic.setItem(COSName.FONT_FILE, (COSObjectable)type1Stream);
    }

    public PDStream getFontFile2() {
        PDStream retval = null;
        COSBase obj = this.dic.getDictionaryObject(COSName.FONT_FILE2);
        if (obj instanceof COSStream) {
            retval = new PDStream((COSStream)obj);
        }
        return retval;
    }

    public void setFontFile2(PDStream ttfStream) {
        this.dic.setItem(COSName.FONT_FILE2, (COSObjectable)ttfStream);
    }

    public PDStream getFontFile3() {
        PDStream retval = null;
        COSBase obj = this.dic.getDictionaryObject(COSName.FONT_FILE3);
        if (obj instanceof COSStream) {
            retval = new PDStream((COSStream)obj);
        }
        return retval;
    }

    public void setFontFile3(PDStream stream) {
        this.dic.setItem(COSName.FONT_FILE3, (COSObjectable)stream);
    }

    public PDStream getCIDSet() {
        COSBase cidSet = this.dic.getDictionaryObject(COSName.CID_SET);
        if (cidSet instanceof COSStream) {
            return new PDStream((COSStream)cidSet);
        }
        return null;
    }

    public void setCIDSet(PDStream stream) {
        this.dic.setItem(COSName.CID_SET, (COSObjectable)stream);
    }

    public PDPanose getPanose() {
        COSString panose;
        byte[] bytes;
        COSDictionary style = (COSDictionary)this.dic.getDictionaryObject(COSName.STYLE);
        if (style != null && (bytes = (panose = (COSString)style.getDictionaryObject(COSName.PANOSE)).getBytes()).length >= 12) {
            return new PDPanose(bytes);
        }
        return null;
    }
}


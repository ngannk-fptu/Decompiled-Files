/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.font;

import com.sun.pdfview.PDFObject;
import com.sun.pdfview.PDFParseException;
import com.sun.pdfview.font.BuiltinFont;
import com.sun.pdfview.font.CIDFontType0;
import com.sun.pdfview.font.CIDFontType2;
import com.sun.pdfview.font.PDFCMap;
import com.sun.pdfview.font.PDFFontDescriptor;
import com.sun.pdfview.font.PDFFontEncoding;
import com.sun.pdfview.font.PDFGlyph;
import com.sun.pdfview.font.TTFFont;
import com.sun.pdfview.font.Type0Font;
import com.sun.pdfview.font.Type1CFont;
import com.sun.pdfview.font.Type1Font;
import com.sun.pdfview.font.Type3Font;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class PDFFont {
    private String subtype;
    private String baseFont;
    private PDFFontEncoding encoding;
    private PDFFontDescriptor descriptor;
    private PDFCMap unicodeMap;
    private Map<Character, PDFGlyph> charCache;

    public static synchronized PDFFont getFont(PDFObject obj, HashMap<String, PDFObject> resources) throws IOException {
        PDFFont font = (PDFFont)obj.getCache();
        if (font != null) {
            return font;
        }
        String baseFont = null;
        PDFFontEncoding encoding = null;
        PDFFontDescriptor descriptor = null;
        String subType = obj.getDictRef("Subtype").getStringValue();
        if (subType == null) {
            subType = obj.getDictRef("S").getStringValue();
        }
        PDFObject baseFontObj = obj.getDictRef("BaseFont");
        PDFObject encodingObj = obj.getDictRef("Encoding");
        PDFObject descObj = obj.getDictRef("FontDescriptor");
        if (baseFontObj != null) {
            baseFont = baseFontObj.getStringValue();
        } else {
            baseFontObj = obj.getDictRef("Name");
            if (baseFontObj != null) {
                baseFont = baseFontObj.getStringValue();
            }
        }
        if (encodingObj != null) {
            encoding = new PDFFontEncoding(subType, encodingObj);
        }
        descriptor = descObj != null ? new PDFFontDescriptor(descObj) : new PDFFontDescriptor(baseFont);
        if (subType.equals("Type0")) {
            font = new Type0Font(baseFont, obj, descriptor);
        } else if (subType.equals("Type1")) {
            font = descriptor.getFontFile() != null ? new Type1Font(baseFont, obj, descriptor) : (descriptor.getFontFile3() != null ? new Type1CFont(baseFont, obj, descriptor) : new BuiltinFont(baseFont, obj, descriptor));
        } else if (subType.equals("TrueType")) {
            font = descriptor.getFontFile2() != null ? new TTFFont(baseFont, obj, descriptor) : new BuiltinFont(baseFont, obj, descriptor);
        } else if (subType.equals("Type3")) {
            font = new Type3Font(baseFont, obj, resources, descriptor);
        } else if (subType.equals("CIDFontType2")) {
            font = descriptor.getFontFile2() != null ? new CIDFontType2(baseFont, obj, descriptor) : new BuiltinFont(baseFont, obj, descriptor);
        } else if (subType.equals("CIDFontType0")) {
            font = descriptor.getFontFile2() != null ? new CIDFontType2(baseFont, obj, descriptor) : new CIDFontType0(baseFont, obj, descriptor);
        } else {
            throw new PDFParseException("Don't know how to handle a '" + subType + "' font");
        }
        font.setSubtype(subType);
        font.setEncoding(encoding);
        obj.setCache(font);
        return font;
    }

    public String getSubtype() {
        return this.subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    public String getBaseFont() {
        return this.baseFont;
    }

    public void setBaseFont(String baseFont) {
        this.baseFont = baseFont;
    }

    public PDFFontEncoding getEncoding() {
        return this.encoding;
    }

    public void setEncoding(PDFFontEncoding encoding) {
        this.encoding = encoding;
    }

    public PDFFontDescriptor getDescriptor() {
        return this.descriptor;
    }

    public void setDescriptor(PDFFontDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    public PDFCMap getUnicodeMap() {
        return this.unicodeMap;
    }

    public void setUnicodeMap(PDFCMap unicodeMap) {
        this.unicodeMap = unicodeMap;
    }

    public List<PDFGlyph> getGlyphs(String text) {
        List<PDFGlyph> outList = null;
        if (this.encoding != null) {
            outList = this.encoding.getGlyphs(this, text);
        } else {
            char[] arry = text.toCharArray();
            outList = new ArrayList<PDFGlyph>(arry.length);
            for (int i = 0; i < arry.length; ++i) {
                char src = (char)(arry[i] & 0xFF);
                outList.add(this.getCachedGlyph(src, null));
            }
        }
        return outList;
    }

    public PDFGlyph getCachedGlyph(char src, String name) {
        PDFGlyph glyph;
        if (this.charCache == null) {
            this.charCache = new HashMap<Character, PDFGlyph>();
        }
        if ((glyph = this.charCache.get(new Character(src))) == null) {
            glyph = this.getGlyph(src, name);
            this.charCache.put(new Character(src), glyph);
        }
        return glyph;
    }

    protected PDFFont(String baseFont, PDFFontDescriptor descriptor) {
        this.setBaseFont(baseFont);
        this.setDescriptor(descriptor);
    }

    protected abstract PDFGlyph getGlyph(char var1, String var2);

    public String toString() {
        return this.getBaseFont();
    }

    public boolean equals(Object o) {
        if (!(o instanceof PDFFont)) {
            return false;
        }
        return ((PDFFont)o).getBaseFont().equals(this.getBaseFont());
    }

    public int hashCode() {
        return this.getBaseFont().hashCode();
    }
}


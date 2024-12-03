/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Utilities;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.CJKFont;
import com.lowagie.text.pdf.IntHashtable;
import com.lowagie.text.pdf.PdfEncodings;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.TrueTypeFontUnicode;
import java.awt.font.GlyphVector;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

class FontDetails {
    PdfIndirectReference indirectReference;
    PdfName fontName;
    BaseFont baseFont;
    TrueTypeFontUnicode ttu;
    CJKFont cjkFont;
    byte[] shortTag;
    HashMap<Integer, int[]> longTag;
    IntHashtable cjkTag;
    int fontType;
    boolean symbolic;
    protected boolean subset = true;

    FontDetails(PdfName fontName, PdfIndirectReference indirectReference, BaseFont baseFont) {
        this.fontName = fontName;
        this.indirectReference = indirectReference;
        this.baseFont = baseFont;
        this.fontType = baseFont.getFontType();
        switch (this.fontType) {
            case 0: 
            case 1: {
                this.shortTag = new byte[256];
                break;
            }
            case 2: {
                this.cjkTag = new IntHashtable();
                this.cjkFont = (CJKFont)baseFont;
                break;
            }
            case 3: {
                this.longTag = new HashMap();
                this.ttu = (TrueTypeFontUnicode)baseFont;
                this.symbolic = baseFont.isFontSpecific();
            }
        }
    }

    PdfIndirectReference getIndirectReference() {
        return this.indirectReference;
    }

    PdfName getFontName() {
        return this.fontName;
    }

    BaseFont getBaseFont() {
        return this.baseFont;
    }

    byte[] convertToBytes(String text) {
        byte[] b = null;
        switch (this.fontType) {
            case 5: {
                return this.baseFont.convertToBytes(text);
            }
            case 0: 
            case 1: {
                b = this.baseFont.convertToBytes(text);
                int len = b.length;
                for (byte b1 : b) {
                    this.shortTag[b1 & 0xFF] = 1;
                }
                break;
            }
            case 2: {
                int len = text.length();
                for (int k = 0; k < len; ++k) {
                    this.cjkTag.put(this.cjkFont.getCidCode(text.charAt(k)), 0);
                }
                b = this.baseFont.convertToBytes(text);
                break;
            }
            case 4: {
                b = this.baseFont.convertToBytes(text);
                break;
            }
            case 3: {
                try {
                    int k;
                    int len = text.length();
                    int[] metrics = null;
                    char[] glyph = new char[len];
                    int i = 0;
                    if (this.symbolic) {
                        b = PdfEncodings.convertToBytes(text, "symboltt");
                        len = b.length;
                        for (k = 0; k < len; ++k) {
                            metrics = this.ttu.getMetricsTT(b[k] & 0xFF);
                            if (metrics == null) continue;
                            this.longTag.put(metrics[0], new int[]{metrics[0], metrics[1], this.ttu.getUnicodeDifferences(b[k] & 0xFF)});
                            glyph[i++] = (char)metrics[0];
                        }
                    } else {
                        for (k = 0; k < len; ++k) {
                            int val;
                            if (Utilities.isSurrogatePair(text, k)) {
                                val = Utilities.convertToUtf32(text, k);
                                ++k;
                            } else {
                                val = text.charAt(k);
                            }
                            metrics = this.ttu.getMetricsTT(val);
                            if (metrics == null) continue;
                            int m0 = metrics[0];
                            Integer gl = m0;
                            if (!this.longTag.containsKey(gl)) {
                                this.longTag.put(gl, new int[]{m0, metrics[1], val});
                            }
                            glyph[i++] = (char)m0;
                        }
                    }
                    String s = new String(glyph, 0, i);
                    b = s.getBytes("UnicodeBigUnmarked");
                    break;
                }
                catch (UnsupportedEncodingException e) {
                    throw new ExceptionConverter(e);
                }
            }
        }
        return b;
    }

    byte[] convertToBytes(GlyphVector glyphVector) {
        if (this.fontType != 3 || this.symbolic) {
            throw new UnsupportedOperationException("Only supported for True Type Unicode fonts");
        }
        char[] glyphs = new char[glyphVector.getNumGlyphs()];
        int glyphCount = 0;
        for (int i = 0; i < glyphs.length; ++i) {
            int[] nArray;
            int code = glyphVector.getGlyphCode(i);
            if (code == 65534 || code == 65535) continue;
            glyphs[glyphCount++] = (char)code;
            Integer codeKey = code;
            if (this.longTag.containsKey(codeKey)) continue;
            int glyphWidth = this.ttu.getGlyphWidth(code);
            Integer charCode = this.ttu.getCharacterCode(code);
            if (charCode != null) {
                int[] nArray2 = new int[3];
                nArray2[0] = code;
                nArray2[1] = glyphWidth;
                nArray = nArray2;
                nArray2[2] = charCode;
            } else {
                int[] nArray3 = new int[2];
                nArray3[0] = code;
                nArray = nArray3;
                nArray3[1] = glyphWidth;
            }
            int[] metrics = nArray;
            this.longTag.put(codeKey, metrics);
        }
        String s = new String(glyphs, 0, glyphCount);
        try {
            byte[] b = s.getBytes("UnicodeBigUnmarked");
            return b;
        }
        catch (UnsupportedEncodingException e) {
            throw new ExceptionConverter(e);
        }
    }

    void writeFont(PdfWriter writer) {
        try {
            switch (this.fontType) {
                case 5: {
                    this.baseFont.writeFont(writer, this.indirectReference, null);
                    break;
                }
                case 0: 
                case 1: {
                    int lastChar;
                    int firstChar;
                    for (firstChar = 0; firstChar < 256 && this.shortTag[firstChar] == 0; ++firstChar) {
                    }
                    for (lastChar = 255; lastChar >= firstChar && this.shortTag[lastChar] == 0; --lastChar) {
                    }
                    if (firstChar > 255) {
                        firstChar = 255;
                        lastChar = 255;
                    }
                    this.baseFont.writeFont(writer, this.indirectReference, new Object[]{firstChar, lastChar, this.shortTag, this.subset});
                    break;
                }
                case 2: {
                    this.baseFont.writeFont(writer, this.indirectReference, new Object[]{this.cjkTag});
                    break;
                }
                case 3: {
                    this.baseFont.writeFont(writer, this.indirectReference, new Object[]{this.longTag, this.subset});
                }
            }
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    public boolean isSubset() {
        return this.subset;
    }

    public void setSubset(boolean subset) {
        this.subset = subset;
    }
}


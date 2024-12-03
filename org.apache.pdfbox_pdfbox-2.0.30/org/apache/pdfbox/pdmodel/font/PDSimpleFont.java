/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.apache.fontbox.FontBoxFont
 */
package org.apache.pdfbox.pdmodel.font;

import java.awt.geom.GeneralPath;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fontbox.FontBoxFont;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.font.encoding.DictionaryEncoding;
import org.apache.pdfbox.pdmodel.font.encoding.Encoding;
import org.apache.pdfbox.pdmodel.font.encoding.GlyphList;
import org.apache.pdfbox.pdmodel.font.encoding.MacRomanEncoding;
import org.apache.pdfbox.pdmodel.font.encoding.StandardEncoding;
import org.apache.pdfbox.pdmodel.font.encoding.WinAnsiEncoding;
import org.apache.pdfbox.pdmodel.font.encoding.ZapfDingbatsEncoding;

public abstract class PDSimpleFont
extends PDFont {
    private static final Log LOG = LogFactory.getLog(PDSimpleFont.class);
    protected Encoding encoding;
    protected GlyphList glyphList;
    private Boolean isSymbolic;
    private final Set<Integer> noUnicode = new HashSet<Integer>();

    PDSimpleFont() {
    }

    PDSimpleFont(String baseFont) {
        super(baseFont);
        this.assignGlyphList(baseFont);
    }

    PDSimpleFont(COSDictionary fontDictionary) throws IOException {
        super(fontDictionary);
    }

    protected void readEncoding() throws IOException {
        COSBase encodingBase = this.dict.getDictionaryObject(COSName.ENCODING);
        if (encodingBase instanceof COSName) {
            COSName encodingName = (COSName)encodingBase;
            if ("ZapfDingbats".equals(this.getName()) && !this.isEmbedded()) {
                this.encoding = ZapfDingbatsEncoding.INSTANCE;
            } else {
                this.encoding = Encoding.getInstance(encodingName);
                if (this.encoding == null) {
                    LOG.warn((Object)("Unknown encoding: " + encodingName.getName()));
                    this.encoding = this.readEncodingFromFont();
                }
            }
        } else if (encodingBase instanceof COSDictionary) {
            boolean hasValidBaseEncoding;
            COSDictionary encodingDict = (COSDictionary)encodingBase;
            Encoding builtIn = null;
            Boolean symbolic = this.getSymbolicFlag();
            COSName baseEncoding = encodingDict.getCOSName(COSName.BASE_ENCODING);
            boolean bl = hasValidBaseEncoding = baseEncoding != null && Encoding.getInstance(baseEncoding) != null;
            if (!hasValidBaseEncoding && Boolean.TRUE.equals(symbolic)) {
                builtIn = this.readEncodingFromFont();
            }
            if (symbolic == null) {
                symbolic = false;
            }
            this.encoding = new DictionaryEncoding(encodingDict, symbolic == false, builtIn);
        } else {
            this.encoding = this.readEncodingFromFont();
        }
        String standard14Name = Standard14Fonts.getMappedFontName(this.getName());
        this.assignGlyphList(standard14Name);
    }

    protected abstract Encoding readEncodingFromFont() throws IOException;

    public Encoding getEncoding() {
        return this.encoding;
    }

    public GlyphList getGlyphList() {
        return this.glyphList;
    }

    public final boolean isSymbolic() {
        if (this.isSymbolic == null) {
            Boolean result = this.isFontSymbolic();
            this.isSymbolic = result != null ? result : Boolean.valueOf(true);
        }
        return this.isSymbolic;
    }

    protected Boolean isFontSymbolic() {
        Boolean result = this.getSymbolicFlag();
        if (result != null) {
            return result;
        }
        if (this.isStandard14()) {
            String mappedName = Standard14Fonts.getMappedFontName(this.getName());
            return mappedName.equals("Symbol") || mappedName.equals("ZapfDingbats");
        }
        if (this.encoding == null) {
            if (!(this instanceof PDTrueTypeFont)) {
                throw new IllegalStateException("PDFBox bug: encoding should not be null!");
            }
            return true;
        }
        if (this.encoding instanceof WinAnsiEncoding || this.encoding instanceof MacRomanEncoding || this.encoding instanceof StandardEncoding) {
            return false;
        }
        if (this.encoding instanceof DictionaryEncoding) {
            for (String name : ((DictionaryEncoding)this.encoding).getDifferences().values()) {
                if (".notdef".equals(name) || WinAnsiEncoding.INSTANCE.contains(name) && MacRomanEncoding.INSTANCE.contains(name) && StandardEncoding.INSTANCE.contains(name)) continue;
                return true;
            }
            return false;
        }
        return null;
    }

    protected final Boolean getSymbolicFlag() {
        if (this.getFontDescriptor() != null) {
            return this.getFontDescriptor().isSymbolic();
        }
        return null;
    }

    @Override
    public String toUnicode(int code) throws IOException {
        return this.toUnicode(code, GlyphList.getAdobeGlyphList());
    }

    @Override
    public String toUnicode(int code, GlyphList customGlyphList) throws IOException {
        GlyphList unicodeGlyphList = this.glyphList == GlyphList.getAdobeGlyphList() ? customGlyphList : this.glyphList;
        String unicode = super.toUnicode(code);
        if (unicode != null) {
            return unicode;
        }
        String name = null;
        if (this.encoding != null && (unicode = unicodeGlyphList.toUnicode(name = this.encoding.getName(code))) != null) {
            return unicode;
        }
        if (LOG.isWarnEnabled() && !this.noUnicode.contains(code)) {
            this.noUnicode.add(code);
            if (name != null) {
                LOG.warn((Object)("No Unicode mapping for " + name + " (" + code + ") in font " + this.getName()));
            } else {
                LOG.warn((Object)("No Unicode mapping for character code " + code + " in font " + this.getName()));
            }
        }
        return null;
    }

    @Override
    public boolean isVertical() {
        return false;
    }

    @Override
    protected final float getStandard14Width(int code) {
        if (this.getStandard14AFM() != null) {
            String nameInAFM = this.getEncoding().getName(code);
            if (".notdef".equals(nameInAFM)) {
                return 250.0f;
            }
            if ("nbspace".equals(nameInAFM)) {
                nameInAFM = "space";
            } else if ("sfthyphen".equals(nameInAFM)) {
                nameInAFM = "hyphen";
            }
            return this.getStandard14AFM().getCharacterWidth(nameInAFM);
        }
        throw new IllegalStateException("No AFM");
    }

    @Override
    public boolean isStandard14() {
        DictionaryEncoding dictionary;
        if (this.getEncoding() instanceof DictionaryEncoding && (dictionary = (DictionaryEncoding)this.getEncoding()).getDifferences().size() > 0) {
            Encoding baseEncoding = dictionary.getBaseEncoding();
            for (Map.Entry<Integer, String> entry : dictionary.getDifferences().entrySet()) {
                if (entry.getValue().equals(baseEncoding.getName(entry.getKey()))) continue;
                return false;
            }
        }
        return super.isStandard14();
    }

    public abstract GeneralPath getPath(String var1) throws IOException;

    public abstract boolean hasGlyph(String var1) throws IOException;

    public abstract FontBoxFont getFontBoxFont();

    @Override
    public void addToSubset(int codePoint) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void subset() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean willBeSubset() {
        return false;
    }

    @Override
    public boolean hasExplicitWidth(int code) throws IOException {
        int firstChar;
        return this.dict.containsKey(COSName.WIDTHS) && code >= (firstChar = this.dict.getInt(COSName.FIRST_CHAR, -1)) && code - firstChar < this.getWidths().size();
    }

    private void assignGlyphList(String baseFont) {
        this.glyphList = "ZapfDingbats".equals(baseFont) ? GlyphList.getZapfDingbats() : GlyphList.getAdobeGlyphList();
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.CJKFont;
import com.lowagie.text.pdf.DocumentFont;
import com.lowagie.text.pdf.EnumerateTTC;
import com.lowagie.text.pdf.GlyphList;
import com.lowagie.text.pdf.IntHashtable;
import com.lowagie.text.pdf.PRIndirectReference;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfEncodings;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStream;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.TrueTypeFont;
import com.lowagie.text.pdf.TrueTypeFontUnicode;
import com.lowagie.text.pdf.Type1Font;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BaseFont {
    public static final String COURIER = "Courier";
    public static final String COURIER_BOLD = "Courier-Bold";
    public static final String COURIER_OBLIQUE = "Courier-Oblique";
    public static final String COURIER_BOLDOBLIQUE = "Courier-BoldOblique";
    public static final String HELVETICA = "Helvetica";
    public static final String HELVETICA_BOLD = "Helvetica-Bold";
    public static final String HELVETICA_OBLIQUE = "Helvetica-Oblique";
    public static final String HELVETICA_BOLDOBLIQUE = "Helvetica-BoldOblique";
    public static final String SYMBOL = "Symbol";
    public static final String TIMES_ROMAN = "Times-Roman";
    public static final String TIMES_BOLD = "Times-Bold";
    public static final String TIMES_ITALIC = "Times-Italic";
    public static final String TIMES_BOLDITALIC = "Times-BoldItalic";
    public static final String ZAPFDINGBATS = "ZapfDingbats";
    public static final int ASCENT = 1;
    public static final int CAPHEIGHT = 2;
    public static final int DESCENT = 3;
    public static final int ITALICANGLE = 4;
    public static final int BBOXLLX = 5;
    public static final int BBOXLLY = 6;
    public static final int BBOXURX = 7;
    public static final int BBOXURY = 8;
    public static final int AWT_ASCENT = 9;
    public static final int AWT_DESCENT = 10;
    public static final int AWT_LEADING = 11;
    public static final int AWT_MAXADVANCE = 12;
    public static final int UNDERLINE_POSITION = 13;
    public static final int UNDERLINE_THICKNESS = 14;
    public static final int STRIKETHROUGH_POSITION = 15;
    public static final int STRIKETHROUGH_THICKNESS = 16;
    public static final int SUBSCRIPT_SIZE = 17;
    public static final int SUBSCRIPT_OFFSET = 18;
    public static final int SUPERSCRIPT_SIZE = 19;
    public static final int SUPERSCRIPT_OFFSET = 20;
    public static final int FONT_TYPE_T1 = 0;
    public static final int FONT_TYPE_TT = 1;
    public static final int FONT_TYPE_CJK = 2;
    public static final int FONT_TYPE_TTUNI = 3;
    public static final int FONT_TYPE_DOCUMENT = 4;
    public static final int FONT_TYPE_T3 = 5;
    public static final String IDENTITY_H = "Identity-H";
    public static final String IDENTITY_V = "Identity-V";
    public static final String CP1250 = "Cp1250";
    public static final String CP1252 = "Cp1252";
    public static final String CP1257 = "Cp1257";
    public static final String WINANSI = "Cp1252";
    public static final String MACROMAN = "MacRoman";
    public static final int[] CHAR_RANGE_LATIN = new int[]{0, 383, 8192, 8303, 8352, 8399, 64256, 64262};
    public static final int[] CHAR_RANGE_ARABIC = new int[]{0, 127, 1536, 1663, 8352, 8399, 64336, 64511, 65136, 65279};
    public static final int[] CHAR_RANGE_HEBREW = new int[]{0, 127, 1424, 1535, 8352, 8399, 64285, 64335};
    public static final int[] CHAR_RANGE_CYRILLIC = new int[]{0, 127, 1024, 1327, 8192, 8303, 8352, 8399};
    public static final boolean EMBEDDED = true;
    public static final boolean NOT_EMBEDDED = false;
    public static final boolean CACHED = true;
    public static final boolean NOT_CACHED = false;
    public static final String RESOURCE_PATH = "com/lowagie/text/pdf/fonts/";
    public static final char CID_NEWLINE = Short.MAX_VALUE;
    protected ArrayList<int[]> subsetRanges;
    int fontType;
    public static final String notdef = ".notdef";
    protected int[] widths = new int[256];
    protected String[] differences = new String[256];
    protected char[] unicodeDifferences = new char[256];
    protected int[][] charBBoxes = new int[256][];
    protected String encoding;
    protected boolean embedded;
    protected int compressionLevel = -1;
    protected boolean fontSpecific = true;
    protected static ConcurrentHashMap<String, BaseFont> fontCache = new ConcurrentHashMap(500, 0.85f, 64);
    protected static final HashMap<String, PdfName> BuiltinFonts14 = new HashMap();
    protected boolean forceWidthsOutput = false;
    protected boolean directTextToByte = false;
    protected boolean subset = true;
    protected boolean fastWinansi = false;
    protected IntHashtable specialMap;

    protected BaseFont() {
    }

    public static BaseFont createFont() throws DocumentException, IOException {
        return BaseFont.createFont(HELVETICA, "Cp1252", false);
    }

    public static BaseFont createFont(String name, String encoding, boolean embedded) throws DocumentException, IOException {
        return BaseFont.createFont(name, encoding, embedded, true, null, null, false);
    }

    public static BaseFont createFont(String name, String encoding, boolean embedded, boolean forceRead) throws DocumentException, IOException {
        return BaseFont.createFont(name, encoding, embedded, true, null, null, forceRead);
    }

    public static BaseFont createFont(String name, String encoding, boolean embedded, boolean cached, byte[] ttfAfm, byte[] pfb) throws DocumentException, IOException {
        return BaseFont.createFont(name, encoding, embedded, cached, ttfAfm, pfb, false);
    }

    public static BaseFont createFont(String name, String encoding, boolean embedded, boolean cached, byte[] ttfAfm, byte[] pfb, boolean noThrow) throws DocumentException, IOException {
        return BaseFont.createFont(name, encoding, embedded, cached, ttfAfm, pfb, false, false);
    }

    public static BaseFont createFont(String name, String encoding, boolean embedded, boolean cached, byte[] ttfAfm, byte[] pfb, boolean noThrow, boolean forceRead) throws DocumentException, IOException {
        boolean isCJKFont;
        String nameBase = BaseFont.getBaseName(name);
        encoding = BaseFont.normalizeEncoding(encoding);
        boolean isBuiltinFonts14 = BuiltinFonts14.containsKey(name);
        boolean bl = isCJKFont = !isBuiltinFonts14 && CJKFont.isCJKFont(nameBase, encoding);
        if (isBuiltinFonts14 || isCJKFont) {
            embedded = false;
        } else if (encoding.equals(IDENTITY_H) || encoding.equals(IDENTITY_V)) {
            embedded = true;
        }
        BaseFont fontFound = null;
        BaseFont fontBuilt = null;
        String key = name + "\n" + encoding + "\n" + embedded;
        if (cached && (fontFound = fontCache.get(key)) != null) {
            return fontFound;
        }
        if (isBuiltinFonts14 || name.toLowerCase().endsWith(".afm") || name.toLowerCase().endsWith(".pfm")) {
            fontBuilt = new Type1Font(name, encoding, embedded, ttfAfm, pfb, forceRead);
            fontBuilt.fastWinansi = encoding.equals("Cp1252");
        } else if (nameBase.toLowerCase().endsWith(".ttf") || nameBase.toLowerCase().endsWith(".otf") || nameBase.toLowerCase().indexOf(".ttc,") > 0) {
            if (encoding.equals(IDENTITY_H) || encoding.equals(IDENTITY_V)) {
                fontBuilt = new TrueTypeFontUnicode(name, encoding, embedded, ttfAfm, forceRead);
            } else {
                fontBuilt = new TrueTypeFont(name, encoding, embedded, ttfAfm, false, forceRead);
                fontBuilt.fastWinansi = encoding.equals("Cp1252");
            }
        } else if (isCJKFont) {
            fontBuilt = new CJKFont(name, encoding, embedded);
        } else {
            if (noThrow) {
                return null;
            }
            throw new DocumentException(MessageLocalization.getComposedMessage("font.1.with.2.is.not.recognized", name, encoding));
        }
        if (cached) {
            fontCache.putIfAbsent(key, fontBuilt);
            return fontCache.get(key);
        }
        return fontBuilt;
    }

    public static BaseFont createFont(PRIndirectReference fontRef) {
        return new DocumentFont(fontRef);
    }

    protected static String getBaseName(String name) {
        if (name.endsWith(",Bold")) {
            return name.substring(0, name.length() - 5);
        }
        if (name.endsWith(",Italic")) {
            return name.substring(0, name.length() - 7);
        }
        if (name.endsWith(",BoldItalic")) {
            return name.substring(0, name.length() - 11);
        }
        return name;
    }

    protected static String normalizeEncoding(String enc) {
        if (enc.equals("winansi") || enc.equals("")) {
            return "Cp1252";
        }
        if (enc.equals("macroman")) {
            return MACROMAN;
        }
        return enc;
    }

    protected void createEncoding() {
        if (this.encoding.startsWith("#")) {
            int uni;
            this.specialMap = new IntHashtable();
            StringTokenizer tok = new StringTokenizer(this.encoding.substring(1), " ,\t\n\r\f");
            if (tok.nextToken().equals("full")) {
                while (tok.hasMoreTokens()) {
                    String order = tok.nextToken();
                    String name = tok.nextToken();
                    uni = Integer.parseInt(tok.nextToken(), 16);
                    int orderK = order.startsWith("'") ? (int)order.charAt(1) : Integer.parseInt(order);
                    this.specialMap.put(uni, orderK %= 256);
                    this.differences[orderK] = name;
                    this.unicodeDifferences[orderK] = uni;
                    this.widths[orderK] = this.getRawWidth(uni, name);
                    this.charBBoxes[orderK] = this.getRawCharBBox(uni, name);
                }
            } else {
                int k = 0;
                if (tok.hasMoreTokens()) {
                    k = Integer.parseInt(tok.nextToken());
                }
                while (tok.hasMoreTokens() && k < 256) {
                    String hex = tok.nextToken();
                    uni = Integer.parseInt(hex, 16) % 65536;
                    String name = GlyphList.unicodeToName(uni);
                    if (name == null) continue;
                    this.specialMap.put(uni, k);
                    this.differences[k] = name;
                    this.unicodeDifferences[k] = (char)uni;
                    this.widths[k] = this.getRawWidth(uni, name);
                    this.charBBoxes[k] = this.getRawCharBBox(uni, name);
                    ++k;
                }
            }
            for (int k = 0; k < 256; ++k) {
                if (this.differences[k] != null) continue;
                this.differences[k] = notdef;
            }
        } else if (this.fontSpecific) {
            for (int k = 0; k < 256; ++k) {
                this.widths[k] = this.getRawWidth(k, null);
                this.charBBoxes[k] = this.getRawCharBBox(k, null);
            }
        } else {
            byte[] b = new byte[1];
            for (int k = 0; k < 256; ++k) {
                b[0] = (byte)k;
                String s = PdfEncodings.convertToString(b, this.encoding);
                int c = s.length() > 0 ? (int)s.charAt(0) : 63;
                String name = GlyphList.unicodeToName(c);
                if (name == null) {
                    name = notdef;
                }
                this.differences[k] = name;
                this.unicodeDifferences[k] = c;
                this.widths[k] = this.getRawWidth(c, name);
                this.charBBoxes[k] = this.getRawCharBBox(c, name);
            }
        }
    }

    abstract int getRawWidth(int var1, String var2);

    public abstract int getKerning(int var1, int var2);

    public abstract boolean setKerning(int var1, int var2, int var3);

    public int getWidth(int char1) {
        byte[] mbytes;
        if (this.fastWinansi) {
            if (char1 < 128 || char1 >= 160 && char1 <= 255) {
                return this.widths[char1];
            }
            return this.widths[PdfEncodings.winansi.get(char1)];
        }
        int total = 0;
        for (byte mbyte : mbytes = this.convertToBytes((char)char1)) {
            total += this.widths[0xFF & mbyte];
        }
        return total;
    }

    public int getWidth(String text) {
        byte[] mbytes;
        int total = 0;
        if (this.fastWinansi) {
            int len = text.length();
            for (int k = 0; k < len; ++k) {
                char char1 = text.charAt(k);
                if (char1 < '\u0080' || char1 >= '\u00a0' && char1 <= '\u00ff') {
                    total += this.widths[char1];
                    continue;
                }
                total += this.widths[PdfEncodings.winansi.get(char1)];
            }
            return total;
        }
        for (byte mbyte : mbytes = this.convertToBytes(text)) {
            total += this.widths[0xFF & mbyte];
        }
        return total;
    }

    public int getDescent(String text) {
        char[] chars;
        int min = 0;
        for (char c : chars = text.toCharArray()) {
            int[] bbox = this.getCharBBox(c);
            if (bbox == null || bbox[1] >= min) continue;
            min = bbox[1];
        }
        return min;
    }

    public int getAscent(String text) {
        char[] chars;
        int max = 0;
        for (char c : chars = text.toCharArray()) {
            int[] bbox = this.getCharBBox(c);
            if (bbox == null || bbox[3] <= max) continue;
            max = bbox[3];
        }
        return max;
    }

    public float getDescentPoint(String text, float fontSize) {
        return (float)this.getDescent(text) * 0.001f * fontSize;
    }

    public float getAscentPoint(String text, float fontSize) {
        return (float)this.getAscent(text) * 0.001f * fontSize;
    }

    public float getWidthPointKerned(String text, float fontSize) {
        float size = (float)this.getWidth(text) * 0.001f * fontSize;
        if (!this.hasKernPairs()) {
            return size;
        }
        int len = text.length() - 1;
        int kern = 0;
        char[] c = text.toCharArray();
        for (int k = 0; k < len; ++k) {
            kern += this.getKerning(c[k], c[k + 1]);
        }
        return size + (float)kern * 0.001f * fontSize;
    }

    public float getWidthPoint(String text, float fontSize) {
        return (float)this.getWidth(text) * 0.001f * fontSize;
    }

    public float getWidthPoint(int char1, float fontSize) {
        return (float)this.getWidth(char1) * 0.001f * fontSize;
    }

    byte[] convertToBytes(String text) {
        if (this.directTextToByte) {
            return PdfEncodings.convertToBytes(text, null);
        }
        if (this.specialMap != null) {
            byte[] b = new byte[text.length()];
            int ptr = 0;
            int length = text.length();
            for (int k = 0; k < length; ++k) {
                char c = text.charAt(k);
                if (!this.specialMap.containsKey(c)) continue;
                b[ptr++] = (byte)this.specialMap.get(c);
            }
            if (ptr < length) {
                byte[] b2 = new byte[ptr];
                System.arraycopy(b, 0, b2, 0, ptr);
                return b2;
            }
            return b;
        }
        return PdfEncodings.convertToBytes(text, this.encoding);
    }

    byte[] convertToBytes(int char1) {
        if (this.directTextToByte) {
            return PdfEncodings.convertToBytes((char)char1, null);
        }
        if (this.specialMap != null) {
            if (this.specialMap.containsKey(char1)) {
                return new byte[]{(byte)this.specialMap.get(char1)};
            }
            return new byte[0];
        }
        return PdfEncodings.convertToBytes((char)char1, this.encoding);
    }

    abstract void writeFont(PdfWriter var1, PdfIndirectReference var2, Object[] var3) throws DocumentException, IOException;

    abstract PdfStream getFullFontStream() throws IOException, DocumentException;

    public String getEncoding() {
        return this.encoding;
    }

    public abstract float getFontDescriptor(int var1, float var2);

    public int getFontType() {
        return this.fontType;
    }

    public boolean isEmbedded() {
        return this.embedded;
    }

    public boolean isFontSpecific() {
        return this.fontSpecific;
    }

    public static String createSubsetPrefix() {
        String s = "";
        for (int k = 0; k < 6; ++k) {
            s = s + (char)(Math.random() * 26.0 + 65.0);
        }
        return s + "+";
    }

    char getUnicodeDifferences(int index) {
        return this.unicodeDifferences[index];
    }

    public abstract String getPostscriptFontName();

    public abstract void setPostscriptFontName(String var1);

    public abstract String[][] getFullFontName();

    public abstract String[][] getAllNameEntries();

    public static String[][] getFullFontName(String name, String encoding, byte[] ttfAfm) throws DocumentException, IOException {
        String nameBase = BaseFont.getBaseName(name);
        BaseFont fontBuilt = null;
        fontBuilt = nameBase.toLowerCase().endsWith(".ttf") || nameBase.toLowerCase().endsWith(".otf") || nameBase.toLowerCase().indexOf(".ttc,") > 0 ? new TrueTypeFont(name, "Cp1252", false, ttfAfm, true, false) : BaseFont.createFont(name, encoding, false, false, ttfAfm, null);
        return ((BaseFont)fontBuilt).getFullFontName();
    }

    public static Object[] getAllFontNames(String name, String encoding, byte[] ttfAfm) throws DocumentException, IOException {
        String nameBase = BaseFont.getBaseName(name);
        BaseFont fontBuilt = null;
        fontBuilt = nameBase.toLowerCase().endsWith(".ttf") || nameBase.toLowerCase().endsWith(".otf") || nameBase.toLowerCase().indexOf(".ttc,") > 0 ? new TrueTypeFont(name, "Cp1252", false, ttfAfm, true, false) : BaseFont.createFont(name, encoding, false, false, ttfAfm, null);
        return new Object[]{fontBuilt.getPostscriptFontName(), fontBuilt.getFamilyFontName(), fontBuilt.getFullFontName()};
    }

    public static String[][] getAllNameEntries(String name, String encoding, byte[] ttfAfm) throws DocumentException, IOException {
        String nameBase = BaseFont.getBaseName(name);
        BaseFont fontBuilt = null;
        fontBuilt = nameBase.toLowerCase().endsWith(".ttf") || nameBase.toLowerCase().endsWith(".otf") || nameBase.toLowerCase().indexOf(".ttc,") > 0 ? new TrueTypeFont(name, "Cp1252", false, ttfAfm, true, false) : BaseFont.createFont(name, encoding, false, false, ttfAfm, null);
        return ((BaseFont)fontBuilt).getAllNameEntries();
    }

    public abstract String[][] getFamilyFontName();

    public String[] getCodePagesSupported() {
        return new String[0];
    }

    public static String[] enumerateTTCNames(String ttcFile) throws DocumentException, IOException {
        return new EnumerateTTC(ttcFile).getNames();
    }

    public static String[] enumerateTTCNames(byte[] ttcArray) throws DocumentException, IOException {
        return new EnumerateTTC(ttcArray).getNames();
    }

    public int[] getWidths() {
        return this.widths;
    }

    public String[] getDifferences() {
        return this.differences;
    }

    public char[] getUnicodeDifferences() {
        return this.unicodeDifferences;
    }

    public boolean isForceWidthsOutput() {
        return this.forceWidthsOutput;
    }

    public void setForceWidthsOutput(boolean forceWidthsOutput) {
        this.forceWidthsOutput = forceWidthsOutput;
    }

    public boolean isDirectTextToByte() {
        return this.directTextToByte;
    }

    public void setDirectTextToByte(boolean directTextToByte) {
        this.directTextToByte = directTextToByte;
    }

    public boolean isSubset() {
        return this.subset;
    }

    public void setSubset(boolean subset) {
        this.subset = subset;
    }

    public static InputStream getResourceStream(String key) {
        return BaseFont.getResourceStream(key, null);
    }

    public static InputStream getResourceStream(String key, ClassLoader loader) {
        if (key.startsWith("/")) {
            key = key.substring(1);
        }
        InputStream is = null;
        if (loader != null && (is = loader.getResourceAsStream(key)) != null) {
            return is;
        }
        try {
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            if (contextClassLoader != null) {
                is = contextClassLoader.getResourceAsStream(key);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        if (is == null) {
            is = BaseFont.class.getResourceAsStream("/" + key);
        }
        if (is == null) {
            is = ClassLoader.getSystemResourceAsStream(key);
        }
        return is;
    }

    public int getUnicodeEquivalent(int c) {
        return c;
    }

    public int getCidCode(int c) {
        return c;
    }

    public abstract boolean hasKernPairs();

    public boolean charExists(int c) {
        byte[] b = this.convertToBytes(c);
        return b.length > 0;
    }

    public boolean setCharAdvance(int c, int advance) {
        byte[] b = this.convertToBytes(c);
        if (b.length == 0) {
            return false;
        }
        this.widths[0xFF & b[0]] = advance;
        return true;
    }

    private static void addFont(PRIndirectReference fontRef, IntHashtable hits, ArrayList<Object[]> fonts) {
        PdfObject obj = PdfReader.getPdfObject(fontRef);
        if (obj == null || !obj.isDictionary()) {
            return;
        }
        PdfDictionary font = (PdfDictionary)obj;
        PdfName subtype = font.getAsName(PdfName.SUBTYPE);
        if (!PdfName.TYPE1.equals(subtype) && !PdfName.TRUETYPE.equals(subtype)) {
            return;
        }
        PdfName name = font.getAsName(PdfName.BASEFONT);
        fonts.add(new Object[]{PdfName.decodeName(name.toString()), fontRef});
        hits.put(fontRef.getNumber(), 1);
    }

    private static void recourseFonts(PdfDictionary page, IntHashtable hits, ArrayList<Object[]> fonts, int level) {
        PdfDictionary xobj;
        if (++level > 50) {
            return;
        }
        PdfDictionary resources = page.getAsDict(PdfName.RESOURCES);
        if (resources == null) {
            return;
        }
        PdfDictionary font = resources.getAsDict(PdfName.FONT);
        if (font != null) {
            for (PdfName pdfName : font.getKeys()) {
                int hit;
                PdfObject ft = font.get(pdfName);
                if (ft == null || !ft.isIndirect() || hits.containsKey(hit = ((PRIndirectReference)ft).getNumber())) continue;
                BaseFont.addFont((PRIndirectReference)ft, hits, fonts);
            }
        }
        if ((xobj = resources.getAsDict(PdfName.XOBJECT)) != null) {
            for (PdfName pdfName : xobj.getKeys()) {
                BaseFont.recourseFonts(xobj.getAsDict(pdfName), hits, fonts, level);
            }
        }
    }

    public static ArrayList<Object[]> getDocumentFonts(PdfReader reader) {
        IntHashtable hits = new IntHashtable();
        ArrayList<Object[]> fonts = new ArrayList<Object[]>();
        int npages = reader.getNumberOfPages();
        for (int k = 1; k <= npages; ++k) {
            BaseFont.recourseFonts(reader.getPageN(k), hits, fonts, 1);
        }
        return fonts;
    }

    public static ArrayList<Object[]> getDocumentFonts(PdfReader reader, int page) {
        IntHashtable hits = new IntHashtable();
        ArrayList<Object[]> fonts = new ArrayList<Object[]>();
        BaseFont.recourseFonts(reader.getPageN(page), hits, fonts, 1);
        return fonts;
    }

    public int[] getCharBBox(int c) {
        byte[] b = this.convertToBytes(c);
        if (b.length == 0) {
            return null;
        }
        return this.charBBoxes[b[0] & 0xFF];
    }

    protected abstract int[] getRawCharBBox(int var1, String var2);

    public void correctArabicAdvance() {
        int c;
        for (c = 1611; c <= 1624; c = (int)((char)(c + 1))) {
            this.setCharAdvance(c, 0);
        }
        this.setCharAdvance(1648, 0);
        for (c = 1750; c <= 1756; c = (int)((char)(c + 1))) {
            this.setCharAdvance(c, 0);
        }
        for (c = 1759; c <= 1764; c = (int)((char)(c + 1))) {
            this.setCharAdvance(c, 0);
        }
        for (c = 1767; c <= 1768; c = (int)((char)(c + 1))) {
            this.setCharAdvance(c, 0);
        }
        for (c = 1770; c <= 1773; c = (int)((char)(c + 1))) {
            this.setCharAdvance(c, 0);
        }
    }

    public void addSubsetRange(int[] range) {
        if (this.subsetRanges == null) {
            this.subsetRanges = new ArrayList();
        }
        this.subsetRanges.add(range);
    }

    public int getCompressionLevel() {
        return this.compressionLevel;
    }

    public void setCompressionLevel(int compressionLevel) {
        this.compressionLevel = compressionLevel < 0 || compressionLevel > 9 ? -1 : compressionLevel;
    }

    static {
        BuiltinFonts14.put(COURIER, PdfName.COURIER);
        BuiltinFonts14.put(COURIER_BOLD, PdfName.COURIER_BOLD);
        BuiltinFonts14.put(COURIER_BOLDOBLIQUE, PdfName.COURIER_BOLDOBLIQUE);
        BuiltinFonts14.put(COURIER_OBLIQUE, PdfName.COURIER_OBLIQUE);
        BuiltinFonts14.put(HELVETICA, PdfName.HELVETICA);
        BuiltinFonts14.put(HELVETICA_BOLD, PdfName.HELVETICA_BOLD);
        BuiltinFonts14.put(HELVETICA_BOLDOBLIQUE, PdfName.HELVETICA_BOLDOBLIQUE);
        BuiltinFonts14.put(HELVETICA_OBLIQUE, PdfName.HELVETICA_OBLIQUE);
        BuiltinFonts14.put(SYMBOL, PdfName.SYMBOL);
        BuiltinFonts14.put(TIMES_ROMAN, PdfName.TIMES_ROMAN);
        BuiltinFonts14.put(TIMES_BOLD, PdfName.TIMES_BOLD);
        BuiltinFonts14.put(TIMES_BOLDITALIC, PdfName.TIMES_BOLDITALIC);
        BuiltinFonts14.put(TIMES_ITALIC, PdfName.TIMES_ITALIC);
        BuiltinFonts14.put(ZAPFDINGBATS, PdfName.ZAPFDINGBATS);
    }

    static class StreamFont
    extends PdfStream {
        public StreamFont(byte[] contents, int[] lengths, int compressionLevel) throws DocumentException {
            try {
                this.bytes = contents;
                this.put(PdfName.LENGTH, new PdfNumber(this.bytes.length));
                for (int k = 0; k < lengths.length; ++k) {
                    this.put(new PdfName("Length" + (k + 1)), new PdfNumber(lengths[k]));
                }
                this.flateCompress(compressionLevel);
            }
            catch (Exception e) {
                throw new DocumentException(e);
            }
        }

        public StreamFont(byte[] contents, String subType, int compressionLevel) throws DocumentException {
            try {
                this.bytes = contents;
                this.put(PdfName.LENGTH, new PdfNumber(this.bytes.length));
                if (subType != null) {
                    this.put(PdfName.SUBTYPE, new PdfName(subType));
                }
                this.flateCompress(compressionLevel);
            }
            catch (Exception e) {
                throw new DocumentException(e);
            }
        }
    }
}


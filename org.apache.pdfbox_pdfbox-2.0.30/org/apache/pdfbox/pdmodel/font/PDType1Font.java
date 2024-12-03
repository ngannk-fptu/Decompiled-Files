/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.apache.fontbox.EncodedFont
 *  org.apache.fontbox.FontBoxFont
 *  org.apache.fontbox.type1.DamagedFontException
 *  org.apache.fontbox.type1.Type1Font
 *  org.apache.fontbox.util.BoundingBox
 */
package org.apache.pdfbox.pdmodel.font;

import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fontbox.EncodedFont;
import org.apache.fontbox.FontBoxFont;
import org.apache.fontbox.type1.DamagedFontException;
import org.apache.fontbox.type1.Type1Font;
import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.font.FontMappers;
import org.apache.pdfbox.pdmodel.font.FontMapping;
import org.apache.pdfbox.pdmodel.font.PDFontDescriptor;
import org.apache.pdfbox.pdmodel.font.PDSimpleFont;
import org.apache.pdfbox.pdmodel.font.PDType1FontEmbedder;
import org.apache.pdfbox.pdmodel.font.UniUtil;
import org.apache.pdfbox.pdmodel.font.encoding.Encoding;
import org.apache.pdfbox.pdmodel.font.encoding.StandardEncoding;
import org.apache.pdfbox.pdmodel.font.encoding.SymbolEncoding;
import org.apache.pdfbox.pdmodel.font.encoding.Type1Encoding;
import org.apache.pdfbox.pdmodel.font.encoding.WinAnsiEncoding;
import org.apache.pdfbox.pdmodel.font.encoding.ZapfDingbatsEncoding;
import org.apache.pdfbox.util.Matrix;

public class PDType1Font
extends PDSimpleFont {
    private static final Log LOG = LogFactory.getLog(PDType1Font.class);
    private static final Map<String, String> ALT_NAMES = new HashMap<String, String>();
    private static final int PFB_START_MARKER = 128;
    public static final PDType1Font TIMES_ROMAN;
    public static final PDType1Font TIMES_BOLD;
    public static final PDType1Font TIMES_ITALIC;
    public static final PDType1Font TIMES_BOLD_ITALIC;
    public static final PDType1Font HELVETICA;
    public static final PDType1Font HELVETICA_BOLD;
    public static final PDType1Font HELVETICA_OBLIQUE;
    public static final PDType1Font HELVETICA_BOLD_OBLIQUE;
    public static final PDType1Font COURIER;
    public static final PDType1Font COURIER_BOLD;
    public static final PDType1Font COURIER_OBLIQUE;
    public static final PDType1Font COURIER_BOLD_OBLIQUE;
    public static final PDType1Font SYMBOL;
    public static final PDType1Font ZAPF_DINGBATS;
    private final Type1Font type1font;
    private final FontBoxFont genericFont;
    private final boolean isEmbedded;
    private final boolean isDamaged;
    private Matrix fontMatrix;
    private final AffineTransform fontMatrixTransform;
    private BoundingBox fontBBox;
    private final Map<Integer, byte[]> codeToBytesMap;

    private PDType1Font(String baseFont) {
        super(baseFont);
        this.dict.setItem(COSName.SUBTYPE, (COSBase)COSName.TYPE1);
        this.dict.setName(COSName.BASE_FONT, baseFont);
        if ("ZapfDingbats".equals(baseFont)) {
            this.encoding = ZapfDingbatsEncoding.INSTANCE;
        } else if ("Symbol".equals(baseFont)) {
            this.encoding = SymbolEncoding.INSTANCE;
        } else {
            this.encoding = WinAnsiEncoding.INSTANCE;
            this.dict.setItem(COSName.ENCODING, (COSBase)COSName.WIN_ANSI_ENCODING);
        }
        this.codeToBytesMap = new ConcurrentHashMap<Integer, byte[]>();
        this.type1font = null;
        FontMapping<FontBoxFont> mapping = FontMappers.instance().getFontBoxFont(this.getBaseFont(), this.getFontDescriptor());
        this.genericFont = mapping.getFont();
        if (mapping.isFallback()) {
            String fontName;
            try {
                fontName = this.genericFont.getName();
            }
            catch (IOException e) {
                fontName = "?";
            }
            LOG.warn((Object)("Using fallback font " + fontName + " for base font " + this.getBaseFont()));
        }
        this.isEmbedded = false;
        this.isDamaged = false;
        this.fontMatrixTransform = new AffineTransform();
    }

    public PDType1Font(PDDocument doc, InputStream pfbIn) throws IOException {
        this(doc, pfbIn, null);
    }

    public PDType1Font(PDDocument doc, InputStream pfbIn, Encoding encoding) throws IOException {
        PDType1FontEmbedder embedder = new PDType1FontEmbedder(doc, this.dict, pfbIn, encoding);
        this.encoding = encoding == null ? embedder.getFontEncoding() : encoding;
        this.glyphList = embedder.getGlyphList();
        this.type1font = embedder.getType1Font();
        this.genericFont = embedder.getType1Font();
        this.isEmbedded = true;
        this.isDamaged = false;
        this.fontMatrixTransform = new AffineTransform();
        this.codeToBytesMap = new HashMap<Integer, byte[]>();
    }

    public PDType1Font(COSDictionary fontDictionary) throws IOException {
        super(fontDictionary);
        this.codeToBytesMap = new HashMap<Integer, byte[]>();
        PDFontDescriptor fd = this.getFontDescriptor();
        Type1Font t1 = null;
        boolean fontIsDamaged = false;
        if (fd != null) {
            PDStream fontFile;
            PDStream fontFile3 = fd.getFontFile3();
            if (fontFile3 != null) {
                LOG.warn((Object)"/FontFile3 for Type1 font not supported");
            }
            if ((fontFile = fd.getFontFile()) != null) {
                try {
                    COSStream stream = fontFile.getCOSObject();
                    int length1 = stream.getInt(COSName.LENGTH1);
                    int length2 = stream.getInt(COSName.LENGTH2);
                    byte[] bytes = fontFile.toByteArray();
                    if (bytes.length == 0) {
                        throw new IOException("Font data unavailable");
                    }
                    length1 = this.repairLength1(bytes, length1);
                    length2 = this.repairLength2(bytes, length1, length2);
                    if ((bytes[0] & 0xFF) == 128) {
                        t1 = Type1Font.createWithPFB((byte[])bytes);
                    } else {
                        if (length1 < 0 || length1 > length1 + length2) {
                            throw new IOException("Invalid length data, actual length: " + bytes.length + ", /Length1: " + length1 + ", /Length2: " + length2);
                        }
                        byte[] segment1 = Arrays.copyOfRange(bytes, 0, length1);
                        byte[] segment2 = Arrays.copyOfRange(bytes, length1, length1 + length2);
                        if (length1 > 0 && length2 > 0) {
                            t1 = Type1Font.createWithSegments((byte[])segment1, (byte[])segment2);
                        }
                    }
                }
                catch (DamagedFontException e) {
                    LOG.warn((Object)("Can't read damaged embedded Type1 font " + fd.getFontName()));
                    fontIsDamaged = true;
                }
                catch (IOException e) {
                    LOG.error((Object)("Can't read the embedded Type1 font " + fd.getFontName()), (Throwable)e);
                    fontIsDamaged = true;
                }
            }
        }
        this.isEmbedded = t1 != null;
        this.isDamaged = fontIsDamaged;
        this.type1font = t1;
        if (this.type1font != null) {
            this.genericFont = this.type1font;
        } else {
            FontMapping<FontBoxFont> mapping = FontMappers.instance().getFontBoxFont(this.getBaseFont(), fd);
            this.genericFont = mapping.getFont();
            if (mapping.isFallback()) {
                LOG.warn((Object)("Using fallback font " + this.genericFont.getName() + " for " + this.getBaseFont()));
            }
        }
        this.readEncoding();
        this.fontMatrixTransform = this.getFontMatrix().createAffineTransform();
        this.fontMatrixTransform.scale(1000.0, 1000.0);
    }

    private int repairLength1(byte[] bytes, int length1) {
        int offset = Math.max(0, length1 - 4);
        if (offset <= 0 || offset > bytes.length - 4) {
            offset = bytes.length - 4;
        }
        if ((offset = PDType1Font.findBinaryOffsetAfterExec(bytes, offset)) == 0 && length1 > 0) {
            offset = PDType1Font.findBinaryOffsetAfterExec(bytes, bytes.length - 4);
        }
        if (length1 - offset != 0 && offset > 0) {
            if (LOG.isWarnEnabled()) {
                LOG.warn((Object)("Ignored invalid Length1 " + length1 + " for Type 1 font " + this.getName()));
            }
            return offset;
        }
        return length1;
    }

    private static int findBinaryOffsetAfterExec(byte[] bytes, int startOffset) {
        int offset;
        for (offset = startOffset; offset > 0; --offset) {
            if (bytes[offset + 0] != 101 || bytes[offset + 1] != 120 || bytes[offset + 2] != 101 || bytes[offset + 3] != 99) continue;
            offset += 4;
            while (offset < bytes.length && (bytes[offset] == 13 || bytes[offset] == 10 || bytes[offset] == 32 || bytes[offset] == 9)) {
                ++offset;
            }
            break;
        }
        return offset;
    }

    private int repairLength2(byte[] bytes, int length1, int length2) {
        if (length2 < 0 || length2 > bytes.length - length1) {
            LOG.warn((Object)("Ignored invalid Length2 " + length2 + " for Type 1 font " + this.getName()));
            return bytes.length - length1;
        }
        return length2;
    }

    public final String getBaseFont() {
        return this.dict.getNameAsString(COSName.BASE_FONT);
    }

    @Override
    public float getHeight(int code) throws IOException {
        if (this.getStandard14AFM() != null) {
            String afmName = this.getEncoding().getName(code);
            return this.getStandard14AFM().getCharacterHeight(afmName);
        }
        String name = this.codeToName(code);
        return (float)this.genericFont.getPath(name).getBounds().getHeight();
    }

    @Override
    protected byte[] encode(int unicode) throws IOException {
        Map<String, Integer> inverted;
        int code;
        byte[] bytes = this.codeToBytesMap.get(unicode);
        if (bytes != null) {
            return bytes;
        }
        String name = this.getGlyphList().codePointToName(unicode);
        if (this.isStandard14()) {
            if (!this.encoding.contains(name)) {
                throw new IllegalArgumentException(String.format("U+%04X ('%s') is not available in the font %s, encoding: %s", unicode, name, this.getName(), this.encoding.getEncodingName()));
            }
            if (".notdef".equals(name)) {
                throw new IllegalArgumentException(String.format("No glyph for U+%04X in the font %s", unicode, this.getName()));
            }
        } else {
            if (!this.encoding.contains(name)) {
                throw new IllegalArgumentException(String.format("U+%04X ('%s') is not available in the font %s (generic: %s), encoding: %s", unicode, name, this.getName(), this.genericFont.getName(), this.encoding.getEncodingName()));
            }
            String nameInFont = this.getNameInFont(name);
            if (nameInFont.equals(".notdef") || !this.genericFont.hasGlyph(nameInFont)) {
                throw new IllegalArgumentException(String.format("No glyph for U+%04X in the font %s (generic: %s)", unicode, this.getName(), this.genericFont.getName()));
            }
        }
        if ((code = (inverted = this.encoding.getNameToCodeMap()).get(name).intValue()) < 0) {
            throw new IllegalArgumentException(String.format("U+%04X ('%s') is not available in the font %s (generic: %s), encoding: %s", unicode, name, this.getName(), this.genericFont.getName(), this.encoding.getEncodingName()));
        }
        bytes = new byte[]{(byte)code};
        this.codeToBytesMap.put(unicode, bytes);
        return bytes;
    }

    @Override
    public float getWidthFromFont(int code) throws IOException {
        String name = this.codeToName(code);
        if (!this.isEmbedded && ".notdef".equals(name)) {
            return 250.0f;
        }
        float width = this.genericFont.getWidth(name);
        Point2D.Float p = new Point2D.Float(width, 0.0f);
        this.fontMatrixTransform.transform(p, p);
        return (float)((Point2D)p).getX();
    }

    @Override
    public boolean isEmbedded() {
        return this.isEmbedded;
    }

    @Override
    public float getAverageFontWidth() {
        if (this.getStandard14AFM() != null) {
            return this.getStandard14AFM().getAverageCharacterWidth();
        }
        return super.getAverageFontWidth();
    }

    @Override
    public int readCode(InputStream in) throws IOException {
        return in.read();
    }

    @Override
    protected Encoding readEncodingFromFont() throws IOException {
        if (!this.isEmbedded() && this.getStandard14AFM() != null) {
            return new Type1Encoding(this.getStandard14AFM());
        }
        if (this.genericFont instanceof EncodedFont) {
            return Type1Encoding.fromFontBox(((EncodedFont)this.genericFont).getEncoding());
        }
        return StandardEncoding.INSTANCE;
    }

    public Type1Font getType1Font() {
        return this.type1font;
    }

    @Override
    public FontBoxFont getFontBoxFont() {
        return this.genericFont;
    }

    @Override
    public String getName() {
        return this.getBaseFont();
    }

    @Override
    public BoundingBox getBoundingBox() throws IOException {
        if (this.fontBBox == null) {
            this.fontBBox = this.generateBoundingBox();
        }
        return this.fontBBox;
    }

    private BoundingBox generateBoundingBox() throws IOException {
        PDRectangle bbox;
        if (this.getFontDescriptor() != null && (bbox = this.getFontDescriptor().getFontBoundingBox()) != null && (bbox.getLowerLeftX() != 0.0f || bbox.getLowerLeftY() != 0.0f || bbox.getUpperRightX() != 0.0f || bbox.getUpperRightY() != 0.0f)) {
            return new BoundingBox(bbox.getLowerLeftX(), bbox.getLowerLeftY(), bbox.getUpperRightX(), bbox.getUpperRightY());
        }
        return this.genericFont.getFontBBox();
    }

    public String codeToName(int code) throws IOException {
        String name = this.getEncoding() != null ? this.getEncoding().getName(code) : ".notdef";
        return this.getNameInFont(name);
    }

    private String getNameInFont(String name) throws IOException {
        if (this.isEmbedded() || this.genericFont.hasGlyph(name)) {
            return name;
        }
        String altName = ALT_NAMES.get(name);
        if (altName != null && !name.equals(".notdef") && this.genericFont.hasGlyph(altName)) {
            return altName;
        }
        String unicodes = this.getGlyphList().toUnicode(name);
        if (unicodes != null && unicodes.length() == 1) {
            Integer code;
            String uniName = UniUtil.getUniNameOfCodePoint(unicodes.codePointAt(0));
            if (this.genericFont.hasGlyph(uniName)) {
                return uniName;
            }
            if ("SymbolMT".equals(this.genericFont.getName()) && (code = SymbolEncoding.INSTANCE.getNameToCodeMap().get(name)) != null && this.genericFont.hasGlyph(uniName = UniUtil.getUniNameOfCodePoint(code + 61440))) {
                return uniName;
            }
        }
        return ".notdef";
    }

    @Override
    public GeneralPath getPath(String name) throws IOException {
        if (name.equals(".notdef") && !this.isEmbedded) {
            return new GeneralPath();
        }
        return this.genericFont.getPath(this.getNameInFont(name));
    }

    @Override
    public boolean hasGlyph(String name) throws IOException {
        return this.genericFont.hasGlyph(this.getNameInFont(name));
    }

    @Override
    public final Matrix getFontMatrix() {
        if (this.fontMatrix == null) {
            List numbers = null;
            try {
                numbers = this.genericFont.getFontMatrix();
            }
            catch (IOException e) {
                this.fontMatrix = DEFAULT_FONT_MATRIX;
            }
            if (numbers != null && numbers.size() == 6) {
                this.fontMatrix = new Matrix(((Number)numbers.get(0)).floatValue(), ((Number)numbers.get(1)).floatValue(), ((Number)numbers.get(2)).floatValue(), ((Number)numbers.get(3)).floatValue(), ((Number)numbers.get(4)).floatValue(), ((Number)numbers.get(5)).floatValue());
            } else {
                return super.getFontMatrix();
            }
        }
        return this.fontMatrix;
    }

    @Override
    public boolean isDamaged() {
        return this.isDamaged;
    }

    static {
        ALT_NAMES.put("ff", "f_f");
        ALT_NAMES.put("ffi", "f_f_i");
        ALT_NAMES.put("ffl", "f_f_l");
        ALT_NAMES.put("fi", "f_i");
        ALT_NAMES.put("fl", "f_l");
        ALT_NAMES.put("st", "s_t");
        ALT_NAMES.put("IJ", "I_J");
        ALT_NAMES.put("ij", "i_j");
        ALT_NAMES.put("ellipsis", "elipsis");
        TIMES_ROMAN = new PDType1Font("Times-Roman");
        TIMES_BOLD = new PDType1Font("Times-Bold");
        TIMES_ITALIC = new PDType1Font("Times-Italic");
        TIMES_BOLD_ITALIC = new PDType1Font("Times-BoldItalic");
        HELVETICA = new PDType1Font("Helvetica");
        HELVETICA_BOLD = new PDType1Font("Helvetica-Bold");
        HELVETICA_OBLIQUE = new PDType1Font("Helvetica-Oblique");
        HELVETICA_BOLD_OBLIQUE = new PDType1Font("Helvetica-BoldOblique");
        COURIER = new PDType1Font("Courier");
        COURIER_BOLD = new PDType1Font("Courier-Bold");
        COURIER_OBLIQUE = new PDType1Font("Courier-Oblique");
        COURIER_BOLD_OBLIQUE = new PDType1Font("Courier-BoldOblique");
        SYMBOL = new PDType1Font("Symbol");
        ZAPF_DINGBATS = new PDType1Font("ZapfDingbats");
    }
}


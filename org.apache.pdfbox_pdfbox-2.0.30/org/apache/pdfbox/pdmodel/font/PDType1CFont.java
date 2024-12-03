/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.apache.fontbox.EncodedFont
 *  org.apache.fontbox.FontBoxFont
 *  org.apache.fontbox.cff.CFFFont
 *  org.apache.fontbox.cff.CFFParser
 *  org.apache.fontbox.cff.CFFParser$ByteSource
 *  org.apache.fontbox.cff.CFFType1Font
 *  org.apache.fontbox.util.BoundingBox
 */
package org.apache.pdfbox.pdmodel.font;

import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fontbox.EncodedFont;
import org.apache.fontbox.FontBoxFont;
import org.apache.fontbox.cff.CFFFont;
import org.apache.fontbox.cff.CFFParser;
import org.apache.fontbox.cff.CFFType1Font;
import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.font.FontMappers;
import org.apache.pdfbox.pdmodel.font.FontMapping;
import org.apache.pdfbox.pdmodel.font.PDFontDescriptor;
import org.apache.pdfbox.pdmodel.font.PDSimpleFont;
import org.apache.pdfbox.pdmodel.font.UniUtil;
import org.apache.pdfbox.pdmodel.font.encoding.Encoding;
import org.apache.pdfbox.pdmodel.font.encoding.StandardEncoding;
import org.apache.pdfbox.pdmodel.font.encoding.Type1Encoding;
import org.apache.pdfbox.util.Matrix;

public class PDType1CFont
extends PDSimpleFont {
    private static final Log LOG = LogFactory.getLog(PDType1CFont.class);
    private final Map<String, Float> glyphHeights = new HashMap<String, Float>();
    private Float avgWidth = null;
    private Matrix fontMatrix;
    private final AffineTransform fontMatrixTransform;
    private final CFFType1Font cffFont;
    private final FontBoxFont genericFont;
    private final boolean isEmbedded;
    private final boolean isDamaged;
    private BoundingBox fontBBox;

    public PDType1CFont(COSDictionary fontDictionary) throws IOException {
        super(fontDictionary);
        PDStream ff3Stream;
        PDFontDescriptor fd = this.getFontDescriptor();
        byte[] bytes = null;
        if (fd != null && (ff3Stream = fd.getFontFile3()) != null && (bytes = ff3Stream.toByteArray()).length == 0) {
            LOG.error((Object)("Invalid data for embedded Type1C font " + this.getName()));
            bytes = null;
        }
        boolean fontIsDamaged = false;
        CFFType1Font cffEmbedded = null;
        try {
            if (bytes != null) {
                CFFParser cffParser = new CFFParser();
                CFFFont parsedCffFont = (CFFFont)cffParser.parse(bytes, (CFFParser.ByteSource)new FF3ByteSource()).get(0);
                if (parsedCffFont instanceof CFFType1Font) {
                    cffEmbedded = (CFFType1Font)parsedCffFont;
                } else {
                    LOG.error((Object)("Expected CFFType1Font, got " + parsedCffFont.getClass().getSimpleName()));
                    fontIsDamaged = true;
                }
            }
        }
        catch (IOException e) {
            LOG.error((Object)("Can't read the embedded Type1C font " + this.getName()), (Throwable)e);
            fontIsDamaged = true;
        }
        this.isDamaged = fontIsDamaged;
        this.cffFont = cffEmbedded;
        if (this.cffFont != null) {
            this.genericFont = this.cffFont;
            this.isEmbedded = true;
        } else {
            FontMapping<FontBoxFont> mapping = FontMappers.instance().getFontBoxFont(this.getBaseFont(), fd);
            this.genericFont = mapping.getFont();
            if (mapping.isFallback()) {
                LOG.warn((Object)("Using fallback font " + this.genericFont.getName() + " for " + this.getBaseFont()));
            }
            this.isEmbedded = false;
        }
        this.readEncoding();
        this.fontMatrixTransform = this.getFontMatrix().createAffineTransform();
        this.fontMatrixTransform.scale(1000.0, 1000.0);
    }

    @Override
    public FontBoxFont getFontBoxFont() {
        return this.genericFont;
    }

    public final String getBaseFont() {
        return this.dict.getNameAsString(COSName.BASE_FONT);
    }

    @Override
    public GeneralPath getPath(String name) throws IOException {
        if (name.equals(".notdef") && !this.isEmbedded() && !this.isStandard14()) {
            return new GeneralPath();
        }
        if ("sfthyphen".equals(name)) {
            return this.genericFont.getPath("hyphen");
        }
        if ("nbspace".equals(name)) {
            if (!this.hasGlyph("space")) {
                return new GeneralPath();
            }
            return this.genericFont.getPath("space");
        }
        return this.genericFont.getPath(name);
    }

    @Override
    public boolean hasGlyph(String name) throws IOException {
        return this.genericFont.hasGlyph(name);
    }

    @Override
    public final String getName() {
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

    public String codeToName(int code) {
        return this.getEncoding().getName(code);
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

    @Override
    public int readCode(InputStream in) throws IOException {
        return in.read();
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

    @Override
    public float getWidthFromFont(int code) throws IOException {
        String name = this.codeToName(code);
        name = this.getNameInFont(name);
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
    public float getHeight(int code) throws IOException {
        float height;
        String name = this.codeToName(code);
        if (!this.glyphHeights.containsKey(name)) {
            if (this.cffFont == null) {
                LOG.warn((Object)"No embedded CFF font, returning 0");
                return 0.0f;
            }
            height = (float)this.cffFont.getType1CharString(name).getBounds().getHeight();
            this.glyphHeights.put(name, Float.valueOf(height));
        } else {
            height = this.glyphHeights.get(name).floatValue();
        }
        return height;
    }

    @Override
    protected byte[] encode(int unicode) throws IOException {
        String name = this.getGlyphList().codePointToName(unicode);
        if (!this.encoding.contains(name)) {
            throw new IllegalArgumentException(String.format("U+%04X ('%s') is not available in this font's encoding: %s", unicode, name, this.encoding.getEncodingName()));
        }
        String nameInFont = this.getNameInFont(name);
        Map<String, Integer> inverted = this.encoding.getNameToCodeMap();
        if (nameInFont.equals(".notdef") || !this.genericFont.hasGlyph(nameInFont)) {
            throw new IllegalArgumentException(String.format("No glyph for U+%04X in font %s", unicode, this.getName()));
        }
        int code = inverted.get(name);
        return new byte[]{(byte)code};
    }

    @Override
    public float getStringWidth(String string) throws IOException {
        if (this.cffFont == null) {
            LOG.warn((Object)"No embedded CFF font, returning 0");
            return 0.0f;
        }
        float width = 0.0f;
        for (int i = 0; i < string.length(); ++i) {
            int codePoint = string.codePointAt(i);
            String name = this.getGlyphList().codePointToName(codePoint);
            width += (float)this.cffFont.getType1CharString(name).getWidth();
        }
        return width;
    }

    @Override
    public float getAverageFontWidth() {
        if (this.avgWidth == null) {
            this.avgWidth = Float.valueOf(this.getAverageCharacterWidth());
        }
        return this.avgWidth.floatValue();
    }

    public CFFType1Font getCFFType1Font() {
        return this.cffFont;
    }

    private float getAverageCharacterWidth() {
        return 500.0f;
    }

    private String getNameInFont(String name) throws IOException {
        String uniName;
        if (this.isEmbedded() || this.genericFont.hasGlyph(name)) {
            return name;
        }
        String unicodes = this.getGlyphList().toUnicode(name);
        if (unicodes != null && unicodes.length() == 1 && this.genericFont.hasGlyph(uniName = UniUtil.getUniNameOfCodePoint(unicodes.codePointAt(0)))) {
            return uniName;
        }
        return ".notdef";
    }

    private class FF3ByteSource
    implements CFFParser.ByteSource {
        private FF3ByteSource() {
        }

        public byte[] getBytes() throws IOException {
            return PDType1CFont.this.getFontDescriptor().getFontFile3().toByteArray();
        }
    }
}


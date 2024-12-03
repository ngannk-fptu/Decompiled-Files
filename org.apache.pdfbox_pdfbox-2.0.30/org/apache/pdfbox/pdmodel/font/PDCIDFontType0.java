/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.apache.fontbox.FontBoxFont
 *  org.apache.fontbox.cff.CFFCIDFont
 *  org.apache.fontbox.cff.CFFFont
 *  org.apache.fontbox.cff.CFFParser
 *  org.apache.fontbox.cff.CFFParser$ByteSource
 *  org.apache.fontbox.cff.CFFType1Font
 *  org.apache.fontbox.cff.Type2CharString
 *  org.apache.fontbox.ttf.OpenTypeFont
 *  org.apache.fontbox.util.BoundingBox
 */
package org.apache.pdfbox.pdmodel.font;

import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fontbox.FontBoxFont;
import org.apache.fontbox.cff.CFFCIDFont;
import org.apache.fontbox.cff.CFFFont;
import org.apache.fontbox.cff.CFFParser;
import org.apache.fontbox.cff.CFFType1Font;
import org.apache.fontbox.cff.Type2CharString;
import org.apache.fontbox.ttf.OpenTypeFont;
import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.font.CIDFontMapping;
import org.apache.pdfbox.pdmodel.font.FontMappers;
import org.apache.pdfbox.pdmodel.font.PDCIDFont;
import org.apache.pdfbox.pdmodel.font.PDFontDescriptor;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.UniUtil;
import org.apache.pdfbox.util.Matrix;

public class PDCIDFontType0
extends PDCIDFont {
    private static final Log LOG = LogFactory.getLog(PDCIDFontType0.class);
    private final CFFCIDFont cidFont;
    private final FontBoxFont t1Font;
    private final Map<Integer, Float> glyphHeights = new HashMap<Integer, Float>();
    private final boolean isEmbedded;
    private final boolean isDamaged;
    private Float avgWidth = null;
    private Matrix fontMatrix;
    private final AffineTransform fontMatrixTransform;
    private BoundingBox fontBBox;
    private int[] cid2gid = null;

    public PDCIDFontType0(COSDictionary fontDictionary, PDType0Font parent) throws IOException {
        super(fontDictionary, parent);
        PDStream ff3Stream;
        PDFontDescriptor fd = this.getFontDescriptor();
        byte[] bytes = null;
        if (fd != null && (ff3Stream = fd.getFontFile3()) != null) {
            bytes = ff3Stream.toByteArray();
        }
        boolean fontIsDamaged = false;
        CFFFont cffFont = null;
        if (bytes != null && bytes.length > 0 && (bytes[0] & 0xFF) == 37) {
            LOG.warn((Object)("Found PFB but expected embedded CFF font " + fd.getFontName()));
            fontIsDamaged = true;
        } else if (bytes != null) {
            CFFParser cffParser = new CFFParser();
            try {
                cffFont = (CFFFont)cffParser.parse(bytes, (CFFParser.ByteSource)new FF3ByteSource()).get(0);
            }
            catch (IOException e) {
                LOG.error((Object)("Can't read the embedded CFF font " + fd.getFontName()), (Throwable)e);
                fontIsDamaged = true;
            }
        }
        if (cffFont != null) {
            if (cffFont instanceof CFFCIDFont) {
                this.cidFont = (CFFCIDFont)cffFont;
                this.t1Font = null;
            } else {
                this.cidFont = null;
                this.t1Font = cffFont;
            }
            this.cid2gid = this.readCIDToGIDMap();
            this.isEmbedded = true;
            this.isDamaged = false;
        } else {
            FontBoxFont font;
            CIDFontMapping mapping = FontMappers.instance().getCIDFont(this.getBaseFont(), this.getFontDescriptor(), this.getCIDSystemInfo());
            if (mapping.isCIDFont()) {
                cffFont = ((OpenTypeFont)mapping.getFont()).getCFF().getFont();
                if (cffFont instanceof CFFCIDFont) {
                    this.cidFont = (CFFCIDFont)cffFont;
                    this.t1Font = null;
                    font = this.cidFont;
                } else {
                    CFFType1Font f = (CFFType1Font)cffFont;
                    this.cidFont = null;
                    this.t1Font = f;
                    font = f;
                }
            } else {
                this.cidFont = null;
                font = this.t1Font = mapping.getTrueTypeFont();
            }
            if (mapping.isFallback()) {
                LOG.warn((Object)("Using fallback " + font.getName() + " for CID-keyed font " + this.getBaseFont()));
            }
            this.isEmbedded = false;
            this.isDamaged = fontIsDamaged;
        }
        this.fontMatrixTransform = this.getFontMatrix().createAffineTransform();
        this.fontMatrixTransform.scale(1000.0, 1000.0);
    }

    @Override
    public final Matrix getFontMatrix() {
        if (this.fontMatrix == null) {
            List numbers;
            if (this.cidFont != null) {
                numbers = this.cidFont.getFontMatrix();
            } else {
                try {
                    numbers = this.t1Font.getFontMatrix();
                }
                catch (IOException e) {
                    return new Matrix(0.001f, 0.0f, 0.0f, 0.001f, 0.0f, 0.0f);
                }
            }
            this.fontMatrix = numbers != null && numbers.size() == 6 ? new Matrix(((Number)numbers.get(0)).floatValue(), ((Number)numbers.get(1)).floatValue(), ((Number)numbers.get(2)).floatValue(), ((Number)numbers.get(3)).floatValue(), ((Number)numbers.get(4)).floatValue(), ((Number)numbers.get(5)).floatValue()) : new Matrix(0.001f, 0.0f, 0.0f, 0.001f, 0.0f, 0.0f);
        }
        return this.fontMatrix;
    }

    @Override
    public BoundingBox getBoundingBox() {
        if (this.fontBBox == null) {
            this.fontBBox = this.generateBoundingBox();
        }
        return this.fontBBox;
    }

    private BoundingBox generateBoundingBox() {
        PDRectangle bbox;
        if (this.getFontDescriptor() != null && ((bbox = this.getFontDescriptor().getFontBoundingBox()).getLowerLeftX() != 0.0f || bbox.getLowerLeftY() != 0.0f || bbox.getUpperRightX() != 0.0f || bbox.getUpperRightY() != 0.0f)) {
            return new BoundingBox(bbox.getLowerLeftX(), bbox.getLowerLeftY(), bbox.getUpperRightX(), bbox.getUpperRightY());
        }
        if (this.cidFont != null) {
            return this.cidFont.getFontBBox();
        }
        try {
            return this.t1Font.getFontBBox();
        }
        catch (IOException e) {
            return new BoundingBox();
        }
    }

    public CFFFont getCFFFont() {
        if (this.cidFont != null) {
            return this.cidFont;
        }
        if (this.t1Font instanceof CFFType1Font) {
            return (CFFType1Font)this.t1Font;
        }
        return null;
    }

    public FontBoxFont getFontBoxFont() {
        if (this.cidFont != null) {
            return this.cidFont;
        }
        return this.t1Font;
    }

    public Type2CharString getType2CharString(int cid) throws IOException {
        if (this.cidFont != null) {
            return this.cidFont.getType2CharString(cid);
        }
        if (this.t1Font instanceof CFFType1Font) {
            return ((CFFType1Font)this.t1Font).getType2CharString(cid);
        }
        return null;
    }

    private String getGlyphName(int code) throws IOException {
        String unicodes = this.parent.toUnicode(code);
        if (unicodes == null) {
            return ".notdef";
        }
        return UniUtil.getUniNameOfCodePoint(unicodes.codePointAt(0));
    }

    @Override
    public GeneralPath getPath(int code) throws IOException {
        Type2CharString charstring;
        int cid = this.codeToCID(code);
        if (this.cid2gid != null && this.isEmbedded) {
            cid = this.cid2gid[cid];
        }
        if ((charstring = this.getType2CharString(cid)) != null) {
            return charstring.getPath();
        }
        if (this.isEmbedded && this.t1Font instanceof CFFType1Font) {
            return ((CFFType1Font)this.t1Font).getType2CharString(cid).getPath();
        }
        return this.t1Font.getPath(this.getGlyphName(code));
    }

    @Override
    public boolean hasGlyph(int code) throws IOException {
        int cid = this.codeToCID(code);
        Type2CharString charstring = this.getType2CharString(cid);
        if (charstring != null) {
            return charstring.getGID() != 0;
        }
        if (this.isEmbedded && this.t1Font instanceof CFFType1Font) {
            return ((CFFType1Font)this.t1Font).getType2CharString(cid).getGID() != 0;
        }
        return this.t1Font.hasGlyph(this.getGlyphName(code));
    }

    @Override
    public int codeToCID(int code) {
        return this.parent.getCMap().toCID(code);
    }

    @Override
    public int codeToGID(int code) {
        int cid = this.codeToCID(code);
        if (this.cidFont != null) {
            return this.cidFont.getCharset().getGIDForCID(cid);
        }
        return cid;
    }

    @Override
    public byte[] encode(int unicode) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float getWidthFromFont(int code) throws IOException {
        int cid = this.codeToCID(code);
        float width = this.cidFont != null ? (float)this.getType2CharString(cid).getWidth() : (this.isEmbedded && this.t1Font instanceof CFFType1Font ? (float)((CFFType1Font)this.t1Font).getType2CharString(cid).getWidth() : this.t1Font.getWidth(this.getGlyphName(code)));
        Point2D.Float p = new Point2D.Float(width, 0.0f);
        this.fontMatrixTransform.transform(p, p);
        return (float)((Point2D)p).getX();
    }

    @Override
    public boolean isEmbedded() {
        return this.isEmbedded;
    }

    @Override
    public boolean isDamaged() {
        return this.isDamaged;
    }

    @Override
    public float getHeight(int code) throws IOException {
        float height;
        int cid = this.codeToCID(code);
        if (!this.glyphHeights.containsKey(cid)) {
            height = (float)this.getType2CharString(cid).getBounds().getHeight();
            this.glyphHeights.put(cid, Float.valueOf(height));
        } else {
            height = this.glyphHeights.get(cid).floatValue();
        }
        return height;
    }

    @Override
    public float getAverageFontWidth() {
        if (this.avgWidth == null) {
            this.avgWidth = Float.valueOf(this.getAverageCharacterWidth());
        }
        return this.avgWidth.floatValue();
    }

    private float getAverageCharacterWidth() {
        return 500.0f;
    }

    private class FF3ByteSource
    implements CFFParser.ByteSource {
        private FF3ByteSource() {
        }

        public byte[] getBytes() throws IOException {
            return PDCIDFontType0.this.getFontDescriptor().getFontFile3().toByteArray();
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.apache.fontbox.cff.Type2CharString
 *  org.apache.fontbox.cmap.CMap
 *  org.apache.fontbox.ttf.CmapLookup
 *  org.apache.fontbox.ttf.GlyphData
 *  org.apache.fontbox.ttf.OTFParser
 *  org.apache.fontbox.ttf.OpenTypeFont
 *  org.apache.fontbox.ttf.TrueTypeFont
 *  org.apache.fontbox.util.BoundingBox
 */
package org.apache.pdfbox.pdmodel.font;

import java.awt.geom.GeneralPath;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fontbox.cff.Type2CharString;
import org.apache.fontbox.cmap.CMap;
import org.apache.fontbox.ttf.CmapLookup;
import org.apache.fontbox.ttf.GlyphData;
import org.apache.fontbox.ttf.OTFParser;
import org.apache.fontbox.ttf.OpenTypeFont;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.font.CIDFontMapping;
import org.apache.pdfbox.pdmodel.font.FontMappers;
import org.apache.pdfbox.pdmodel.font.PDCIDFont;
import org.apache.pdfbox.pdmodel.font.PDFontDescriptor;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.util.Matrix;

public class PDCIDFontType2
extends PDCIDFont {
    private static final Log LOG = LogFactory.getLog(PDCIDFontType2.class);
    private final TrueTypeFont ttf;
    private final int[] cid2gid;
    private final boolean isEmbedded;
    private final boolean isDamaged;
    private final CmapLookup cmap;
    private Matrix fontMatrix;
    private BoundingBox fontBBox;
    private final Set<Integer> noMapping = new HashSet<Integer>();

    public PDCIDFontType2(COSDictionary fontDictionary, PDType0Font parent) throws IOException {
        this(fontDictionary, parent, null);
    }

    public PDCIDFontType2(COSDictionary fontDictionary, PDType0Font parent, TrueTypeFont trueTypeFont) throws IOException {
        super(fontDictionary, parent);
        PDFontDescriptor fd = this.getFontDescriptor();
        if (trueTypeFont != null) {
            this.ttf = trueTypeFont;
            this.isEmbedded = true;
            this.isDamaged = false;
        } else {
            boolean fontIsDamaged = false;
            OpenTypeFont ttfFont = null;
            PDStream stream = null;
            if (fd != null) {
                stream = fd.getFontFile2();
                if (stream == null) {
                    stream = fd.getFontFile3();
                }
                if (stream == null) {
                    stream = fd.getFontFile();
                }
            }
            if (stream != null) {
                try {
                    OpenTypeFont otf;
                    OTFParser otfParser = new OTFParser(true);
                    ttfFont = otf = otfParser.parse((InputStream)stream.createInputStream());
                    if (otf.isPostScript()) {
                        fontIsDamaged = true;
                        LOG.warn((Object)("Found CFF/OTF but expected embedded TTF font " + fd.getFontName()));
                    }
                }
                catch (IOException e) {
                    fontIsDamaged = true;
                    LOG.warn((Object)("Could not read embedded OTF for font " + this.getBaseFont()), (Throwable)e);
                }
            }
            this.isEmbedded = ttfFont != null;
            this.isDamaged = fontIsDamaged;
            if (ttfFont == null) {
                ttfFont = this.findFontOrSubstitute();
            }
            this.ttf = ttfFont;
        }
        this.cmap = this.ttf.getUnicodeCmapLookup(false);
        this.cid2gid = this.readCIDToGIDMap();
    }

    private TrueTypeFont findFontOrSubstitute() throws IOException {
        TrueTypeFont ttfFont;
        CIDFontMapping mapping = FontMappers.instance().getCIDFont(this.getBaseFont(), this.getFontDescriptor(), this.getCIDSystemInfo());
        if (mapping.isCIDFont()) {
            ttfFont = (TrueTypeFont)mapping.getFont();
        } else {
            ttfFont = (TrueTypeFont)mapping.getTrueTypeFont();
            if (ttfFont == null) {
                throw new IOException("mapping.getTrueTypeFont() returns null, please report");
            }
        }
        if (mapping.isFallback()) {
            LOG.warn((Object)("Using fallback font " + ttfFont.getName() + " for CID-keyed TrueType font " + this.getBaseFont()));
        }
        return ttfFont;
    }

    @Override
    public Matrix getFontMatrix() {
        if (this.fontMatrix == null) {
            this.fontMatrix = new Matrix(0.001f, 0.0f, 0.0f, 0.001f, 0.0f, 0.0f);
        }
        return this.fontMatrix;
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
        if (this.getFontDescriptor() != null && (bbox = this.getFontDescriptor().getFontBoundingBox()) != null && (Float.compare(bbox.getLowerLeftX(), 0.0f) != 0 || Float.compare(bbox.getLowerLeftY(), 0.0f) != 0 || Float.compare(bbox.getUpperRightX(), 0.0f) != 0 || Float.compare(bbox.getUpperRightY(), 0.0f) != 0)) {
            return new BoundingBox(bbox.getLowerLeftX(), bbox.getLowerLeftY(), bbox.getUpperRightX(), bbox.getUpperRightY());
        }
        return this.ttf.getFontBBox();
    }

    @Override
    public int codeToCID(int code) {
        String unicode;
        CMap cMap = this.parent.getCMap();
        if (!cMap.hasCIDMappings() && cMap.hasUnicodeMappings() && (unicode = cMap.toUnicode(code)) != null) {
            return unicode.codePointAt(0);
        }
        return cMap.toCID(code);
    }

    @Override
    public int codeToGID(int code) throws IOException {
        if (!this.isEmbedded) {
            String name = this.getName();
            if (this.cid2gid != null && !this.isDamaged && name != null && name.equals(this.ttf.getName())) {
                LOG.warn((Object)("Using non-embedded GIDs in font " + this.getName()));
                int cid = this.codeToCID(code);
                if (cid < this.cid2gid.length) {
                    return this.cid2gid[cid];
                }
                return 0;
            }
            String unicode = this.parent.toUnicode(code);
            if (unicode == null) {
                if (!this.noMapping.contains(code)) {
                    this.noMapping.add(code);
                    LOG.warn((Object)("Failed to find a character mapping for " + code + " in " + this.getName()));
                }
                return this.codeToCID(code);
            }
            if (unicode.length() > 1) {
                LOG.warn((Object)"Trying to map multi-byte character using 'cmap', result will be poor");
            }
            return this.cmap.getGlyphId(unicode.codePointAt(0));
        }
        int cid = this.codeToCID(code);
        if (this.cid2gid != null) {
            if (cid < this.cid2gid.length) {
                return this.cid2gid[cid];
            }
            return 0;
        }
        if (cid < this.ttf.getNumberOfGlyphs()) {
            return cid;
        }
        return 0;
    }

    @Override
    public float getHeight(int code) throws IOException {
        return (this.ttf.getHorizontalHeader().getAscender() + -this.ttf.getHorizontalHeader().getDescender()) / this.ttf.getUnitsPerEm();
    }

    @Override
    public float getWidthFromFont(int code) throws IOException {
        int gid = this.codeToGID(code);
        float width = this.ttf.getAdvanceWidth(gid);
        int unitsPerEM = this.ttf.getUnitsPerEm();
        if (unitsPerEM != 1000) {
            width *= 1000.0f / (float)unitsPerEM;
        }
        return width;
    }

    @Override
    public byte[] encode(int unicode) {
        int cid = -1;
        if (this.isEmbedded) {
            if (this.parent.getCMap().getName().startsWith("Identity-")) {
                if (this.cmap != null) {
                    cid = this.cmap.getGlyphId(unicode);
                }
            } else if (this.parent.getCMapUCS2() != null) {
                cid = this.parent.getCMapUCS2().toCID(unicode);
            }
            if (cid == -1) {
                byte[] codes;
                CMap toUnicodeCMap = this.parent.getToUnicodeCMap();
                if (toUnicodeCMap != null && (codes = toUnicodeCMap.getCodesFromUnicode(Character.toString((char)unicode))) != null) {
                    return codes;
                }
                cid = 0;
            }
        } else {
            cid = this.cmap.getGlyphId(unicode);
        }
        if (cid == 0) {
            throw new IllegalArgumentException(String.format("No glyph for U+%04X (%c) in font %s", unicode, Character.valueOf((char)unicode), this.getName()));
        }
        return new byte[]{(byte)(cid >> 8 & 0xFF), (byte)(cid & 0xFF)};
    }

    @Override
    public boolean isEmbedded() {
        return this.isEmbedded;
    }

    @Override
    public boolean isDamaged() {
        return this.isDamaged;
    }

    public TrueTypeFont getTrueTypeFont() {
        return this.ttf;
    }

    @Override
    public GeneralPath getPath(int code) throws IOException {
        if (this.ttf instanceof OpenTypeFont && ((OpenTypeFont)this.ttf).isPostScript()) {
            int cid = this.codeToGID(code);
            Type2CharString charstring = ((OpenTypeFont)this.ttf).getCFF().getFont().getType2CharString(cid);
            return charstring.getPath();
        }
        int gid = this.codeToGID(code);
        GlyphData glyph = this.ttf.getGlyph().getGlyph(gid);
        if (glyph != null) {
            return glyph.getPath();
        }
        return new GeneralPath();
    }

    @Override
    public boolean hasGlyph(int code) throws IOException {
        return this.codeToGID(code) != 0;
    }
}


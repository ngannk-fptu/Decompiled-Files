/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.apache.fontbox.cmap.CMap
 *  org.apache.fontbox.ttf.CmapLookup
 *  org.apache.fontbox.ttf.TTFParser
 *  org.apache.fontbox.ttf.TrueTypeFont
 *  org.apache.fontbox.util.BoundingBox
 */
package org.apache.pdfbox.pdmodel.font;

import java.awt.geom.GeneralPath;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fontbox.cmap.CMap;
import org.apache.fontbox.ttf.CmapLookup;
import org.apache.fontbox.ttf.TTFParser;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.CMapManager;
import org.apache.pdfbox.pdmodel.font.PDCIDFont;
import org.apache.pdfbox.pdmodel.font.PDCIDFontType2;
import org.apache.pdfbox.pdmodel.font.PDCIDFontType2Embedder;
import org.apache.pdfbox.pdmodel.font.PDCIDSystemInfo;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDFontDescriptor;
import org.apache.pdfbox.pdmodel.font.PDFontFactory;
import org.apache.pdfbox.pdmodel.font.PDVectorFont;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.Vector;

public class PDType0Font
extends PDFont
implements PDVectorFont {
    private static final Log LOG = LogFactory.getLog(PDType0Font.class);
    private final PDCIDFont descendantFont;
    private CMap cMap;
    private CMap cMapUCS2;
    private boolean isCMapPredefined;
    private boolean isDescendantCJK;
    private PDCIDFontType2Embedder embedder;
    private final Set<Integer> noUnicode = new HashSet<Integer>();
    private TrueTypeFont ttf;

    public static PDType0Font load(PDDocument doc, File file) throws IOException {
        return new PDType0Font(doc, new TTFParser().parse(file), true, true, false);
    }

    public static PDType0Font load(PDDocument doc, InputStream input) throws IOException {
        return PDType0Font.load(doc, input, true);
    }

    public static PDType0Font load(PDDocument doc, InputStream input, boolean embedSubset) throws IOException {
        return new PDType0Font(doc, new TTFParser().parse(input), embedSubset, true, false);
    }

    public static PDType0Font load(PDDocument doc, TrueTypeFont ttf, boolean embedSubset) throws IOException {
        return new PDType0Font(doc, ttf, embedSubset, false, false);
    }

    public static PDType0Font loadVertical(PDDocument doc, File file) throws IOException {
        return new PDType0Font(doc, new TTFParser().parse(file), true, true, true);
    }

    public static PDType0Font loadVertical(PDDocument doc, InputStream input) throws IOException {
        return new PDType0Font(doc, new TTFParser().parse(input), true, true, true);
    }

    public static PDType0Font loadVertical(PDDocument doc, InputStream input, boolean embedSubset) throws IOException {
        return new PDType0Font(doc, new TTFParser().parse(input), embedSubset, true, true);
    }

    public static PDType0Font loadVertical(PDDocument doc, TrueTypeFont ttf, boolean embedSubset) throws IOException {
        return new PDType0Font(doc, ttf, embedSubset, false, true);
    }

    public PDType0Font(COSDictionary fontDictionary) throws IOException {
        super(fontDictionary);
        COSBase base = this.dict.getDictionaryObject(COSName.DESCENDANT_FONTS);
        if (!(base instanceof COSArray)) {
            throw new IOException("Missing descendant font array");
        }
        COSArray descendantFonts = (COSArray)base;
        if (descendantFonts.size() == 0) {
            throw new IOException("Descendant font array is empty");
        }
        COSBase descendantFontDictBase = descendantFonts.getObject(0);
        if (!(descendantFontDictBase instanceof COSDictionary)) {
            throw new IOException("Missing descendant font dictionary");
        }
        if (!COSName.FONT.equals(((COSDictionary)descendantFontDictBase).getCOSName(COSName.TYPE, COSName.FONT))) {
            throw new IOException("Missing or wrong type in descendant font dictionary");
        }
        this.descendantFont = PDFontFactory.createDescendantFont((COSDictionary)descendantFontDictBase, this);
        this.readEncoding();
        this.fetchCMapUCS2();
    }

    private PDType0Font(PDDocument document, TrueTypeFont ttf, boolean embedSubset, boolean closeTTF, boolean vertical) throws IOException {
        if (vertical) {
            ttf.enableVerticalSubstitutions();
        }
        this.embedder = new PDCIDFontType2Embedder(document, this.dict, ttf, embedSubset, this, vertical);
        this.descendantFont = this.embedder.getCIDFont();
        this.readEncoding();
        this.fetchCMapUCS2();
        if (closeTTF) {
            if (embedSubset) {
                this.ttf = ttf;
                document.registerTrueTypeFontForClosing(ttf);
            } else {
                ttf.close();
            }
        }
    }

    @Override
    public void addToSubset(int codePoint) {
        if (!this.willBeSubset()) {
            throw new IllegalStateException("This font was created with subsetting disabled");
        }
        this.embedder.addToSubset(codePoint);
    }

    @Override
    public void subset() throws IOException {
        if (!this.willBeSubset()) {
            throw new IllegalStateException("This font was created with subsetting disabled");
        }
        this.embedder.subset();
        if (this.ttf != null) {
            this.ttf.close();
            this.ttf = null;
        }
    }

    @Override
    public boolean willBeSubset() {
        return this.embedder != null && this.embedder.needsSubset();
    }

    private void readEncoding() throws IOException {
        COSBase encoding = this.dict.getDictionaryObject(COSName.ENCODING);
        if (encoding instanceof COSName) {
            COSName encodingName = (COSName)encoding;
            this.cMap = CMapManager.getPredefinedCMap(encodingName.getName());
            this.isCMapPredefined = true;
        } else if (encoding != null) {
            this.cMap = this.readCMap(encoding);
            if (this.cMap == null) {
                throw new IOException("Missing required CMap");
            }
            if (!this.cMap.hasCIDMappings()) {
                LOG.warn((Object)("Invalid Encoding CMap in font " + this.getName()));
            }
        }
        PDCIDSystemInfo ros = this.descendantFont.getCIDSystemInfo();
        if (ros != null) {
            String ordering = ros.getOrdering();
            this.isDescendantCJK = "Adobe".equals(ros.getRegistry()) && ("GB1".equals(ordering) || "CNS1".equals(ordering) || "Japan1".equals(ordering) || "Korea1".equals(ordering));
        }
    }

    private void fetchCMapUCS2() throws IOException {
        COSName name = this.dict.getCOSName(COSName.ENCODING);
        if (this.isCMapPredefined && name != COSName.IDENTITY_H && name != COSName.IDENTITY_V || this.isDescendantCJK) {
            String strName = null;
            if (this.isDescendantCJK) {
                PDCIDSystemInfo cidSystemInfo = this.descendantFont.getCIDSystemInfo();
                if (cidSystemInfo != null) {
                    strName = cidSystemInfo.getRegistry() + "-" + cidSystemInfo.getOrdering() + "-" + cidSystemInfo.getSupplement();
                }
            } else if (name != null) {
                strName = name.getName();
            }
            if (strName != null) {
                try {
                    CMap prdCMap = CMapManager.getPredefinedCMap(strName);
                    String ucs2Name = prdCMap.getRegistry() + "-" + prdCMap.getOrdering() + "-UCS2";
                    this.cMapUCS2 = CMapManager.getPredefinedCMap(ucs2Name);
                }
                catch (IOException ex) {
                    LOG.warn((Object)("Could not get " + strName + " UC2 map for font " + this.getName()), (Throwable)ex);
                }
            }
        }
    }

    public String getBaseFont() {
        return this.dict.getNameAsString(COSName.BASE_FONT);
    }

    public PDCIDFont getDescendantFont() {
        return this.descendantFont;
    }

    public CMap getCMap() {
        return this.cMap;
    }

    public CMap getCMapUCS2() {
        return this.cMapUCS2;
    }

    @Override
    public PDFontDescriptor getFontDescriptor() {
        return this.descendantFont.getFontDescriptor();
    }

    @Override
    public Matrix getFontMatrix() {
        return this.descendantFont.getFontMatrix();
    }

    @Override
    public boolean isVertical() {
        return this.cMap != null && this.cMap.getWMode() == 1;
    }

    @Override
    public float getHeight(int code) throws IOException {
        return this.descendantFont.getHeight(code);
    }

    @Override
    protected byte[] encode(int unicode) throws IOException {
        return this.descendantFont.encode(unicode);
    }

    @Override
    public boolean hasExplicitWidth(int code) throws IOException {
        return this.descendantFont.hasExplicitWidth(code);
    }

    @Override
    public float getAverageFontWidth() {
        return this.descendantFont.getAverageFontWidth();
    }

    @Override
    public Vector getPositionVector(int code) {
        return this.descendantFont.getPositionVector(code).scale(-0.001f);
    }

    @Override
    public Vector getDisplacement(int code) throws IOException {
        if (this.isVertical()) {
            return new Vector(0.0f, this.descendantFont.getVerticalDisplacementVectorY(code) / 1000.0f);
        }
        return super.getDisplacement(code);
    }

    @Override
    public float getWidth(int code) throws IOException {
        return this.descendantFont.getWidth(code);
    }

    @Override
    protected float getStandard14Width(int code) {
        throw new UnsupportedOperationException("not supported");
    }

    @Override
    public float getWidthFromFont(int code) throws IOException {
        return this.descendantFont.getWidthFromFont(code);
    }

    @Override
    public boolean isEmbedded() {
        return this.descendantFont.isEmbedded();
    }

    @Override
    public String toUnicode(int code) throws IOException {
        TrueTypeFont font;
        String unicode = super.toUnicode(code);
        if (unicode != null) {
            return unicode;
        }
        if ((this.isCMapPredefined || this.isDescendantCJK) && this.cMapUCS2 != null) {
            int cid = this.codeToCID(code);
            return this.cMapUCS2.toUnicode(cid);
        }
        if (this.descendantFont instanceof PDCIDFontType2 && (font = ((PDCIDFontType2)this.descendantFont).getTrueTypeFont()) != null) {
            try {
                int gid;
                List codes;
                CmapLookup cmap = font.getUnicodeCmapLookup(false);
                if (cmap != null && (codes = cmap.getCharCodes(gid = this.descendantFont.isEmbedded() ? this.descendantFont.codeToGID(code) : this.descendantFont.codeToCID(code))) != null && !codes.isEmpty()) {
                    return Character.toString((char)((Integer)codes.get(0)).intValue());
                }
            }
            catch (IOException e) {
                LOG.warn((Object)"get unicode from font cmap fail", (Throwable)e);
            }
        }
        if (LOG.isWarnEnabled() && !this.noUnicode.contains(code)) {
            String cid = "CID+" + this.codeToCID(code);
            LOG.warn((Object)("No Unicode mapping for " + cid + " (" + code + ") in font " + this.getName()));
            this.noUnicode.add(code);
        }
        return null;
    }

    @Override
    public String getName() {
        return this.getBaseFont();
    }

    @Override
    public BoundingBox getBoundingBox() throws IOException {
        return this.descendantFont.getBoundingBox();
    }

    @Override
    public int readCode(InputStream in) throws IOException {
        if (this.cMap == null) {
            throw new IOException("required cmap is null");
        }
        return this.cMap.readCode(in);
    }

    public int codeToCID(int code) {
        return this.descendantFont.codeToCID(code);
    }

    public int codeToGID(int code) throws IOException {
        return this.descendantFont.codeToGID(code);
    }

    @Override
    public boolean isStandard14() {
        return false;
    }

    @Override
    public boolean isDamaged() {
        return this.descendantFont.isDamaged();
    }

    @Override
    public String toString() {
        String descendant = null;
        if (this.getDescendantFont() != null) {
            descendant = this.getDescendantFont().getClass().getSimpleName();
        }
        return this.getClass().getSimpleName() + "/" + descendant + ", PostScript name: " + this.getBaseFont();
    }

    @Override
    public GeneralPath getPath(int code) throws IOException {
        return this.descendantFont.getPath(code);
    }

    @Override
    public boolean hasGlyph(int code) throws IOException {
        return this.descendantFont.hasGlyph(code);
    }
}


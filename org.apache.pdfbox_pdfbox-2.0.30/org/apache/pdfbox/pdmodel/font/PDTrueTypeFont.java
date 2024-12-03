/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.apache.fontbox.FontBoxFont
 *  org.apache.fontbox.ttf.CmapSubtable
 *  org.apache.fontbox.ttf.CmapTable
 *  org.apache.fontbox.ttf.GlyphData
 *  org.apache.fontbox.ttf.GlyphTable
 *  org.apache.fontbox.ttf.PostScriptTable
 *  org.apache.fontbox.ttf.TTFParser
 *  org.apache.fontbox.ttf.TrueTypeFont
 *  org.apache.fontbox.util.BoundingBox
 */
package org.apache.pdfbox.pdmodel.font;

import java.awt.geom.GeneralPath;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fontbox.FontBoxFont;
import org.apache.fontbox.ttf.CmapSubtable;
import org.apache.fontbox.ttf.CmapTable;
import org.apache.fontbox.ttf.GlyphData;
import org.apache.fontbox.ttf.GlyphTable;
import org.apache.fontbox.ttf.PostScriptTable;
import org.apache.fontbox.ttf.TTFParser;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSInputStream;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.font.FontMappers;
import org.apache.pdfbox.pdmodel.font.FontMapping;
import org.apache.pdfbox.pdmodel.font.PDFontDescriptor;
import org.apache.pdfbox.pdmodel.font.PDSimpleFont;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFontEmbedder;
import org.apache.pdfbox.pdmodel.font.PDVectorFont;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.font.UniUtil;
import org.apache.pdfbox.pdmodel.font.encoding.BuiltInEncoding;
import org.apache.pdfbox.pdmodel.font.encoding.Encoding;
import org.apache.pdfbox.pdmodel.font.encoding.GlyphList;
import org.apache.pdfbox.pdmodel.font.encoding.MacOSRomanEncoding;
import org.apache.pdfbox.pdmodel.font.encoding.MacRomanEncoding;
import org.apache.pdfbox.pdmodel.font.encoding.StandardEncoding;
import org.apache.pdfbox.pdmodel.font.encoding.Type1Encoding;
import org.apache.pdfbox.pdmodel.font.encoding.WinAnsiEncoding;

public class PDTrueTypeFont
extends PDSimpleFont
implements PDVectorFont {
    private static final Log LOG = LogFactory.getLog(PDTrueTypeFont.class);
    private static final int START_RANGE_F000 = 61440;
    private static final int START_RANGE_F100 = 61696;
    private static final int START_RANGE_F200 = 61952;
    private static final Map<String, Integer> INVERTED_MACOS_ROMAN = new HashMap<String, Integer>(250);
    private CmapSubtable cmapWinUnicode = null;
    private CmapSubtable cmapWinSymbol = null;
    private CmapSubtable cmapMacRoman = null;
    private boolean cmapInitialized = false;
    private Map<Integer, Integer> gidToCode;
    private final TrueTypeFont ttf;
    private final boolean isEmbedded;
    private final boolean isDamaged;
    private BoundingBox fontBBox;

    public static PDTrueTypeFont load(PDDocument doc, File file, Encoding encoding) throws IOException {
        return new PDTrueTypeFont(doc, new TTFParser().parse(file), encoding, true);
    }

    public static PDTrueTypeFont load(PDDocument doc, InputStream input, Encoding encoding) throws IOException {
        return new PDTrueTypeFont(doc, new TTFParser().parse(input), encoding, true);
    }

    public static PDTrueTypeFont load(PDDocument doc, TrueTypeFont ttf, Encoding encoding) throws IOException {
        return new PDTrueTypeFont(doc, ttf, encoding, false);
    }

    @Deprecated
    public static PDTrueTypeFont loadTTF(PDDocument doc, File file) throws IOException {
        return new PDTrueTypeFont(doc, new TTFParser().parse(file), WinAnsiEncoding.INSTANCE, true);
    }

    @Deprecated
    public static PDTrueTypeFont loadTTF(PDDocument doc, InputStream input) throws IOException {
        return new PDTrueTypeFont(doc, new TTFParser().parse(input), WinAnsiEncoding.INSTANCE, true);
    }

    public PDTrueTypeFont(COSDictionary fontDictionary) throws IOException {
        super(fontDictionary);
        PDFontDescriptor fd;
        PDStream ff2Stream;
        TrueTypeFont ttfFont = null;
        boolean fontIsDamaged = false;
        if (this.getFontDescriptor() != null && (ff2Stream = (fd = super.getFontDescriptor()).getFontFile2()) != null) {
            COSInputStream is = null;
            try {
                TTFParser ttfParser = new TTFParser(true);
                is = ff2Stream.createInputStream();
                ttfFont = ttfParser.parse((InputStream)is);
            }
            catch (IOException e) {
                LOG.warn((Object)("Could not read embedded TTF for font " + this.getBaseFont()), (Throwable)e);
                fontIsDamaged = true;
                IOUtils.closeQuietly(is);
            }
        }
        this.isEmbedded = ttfFont != null;
        this.isDamaged = fontIsDamaged;
        if (ttfFont == null) {
            FontMapping<TrueTypeFont> mapping = FontMappers.instance().getTrueTypeFont(this.getBaseFont(), this.getFontDescriptor());
            ttfFont = mapping.getFont();
            if (mapping.isFallback()) {
                LOG.warn((Object)("Using fallback font " + ttfFont + " for " + this.getBaseFont()));
            }
        }
        this.ttf = ttfFont;
        this.readEncoding();
    }

    public final String getBaseFont() {
        return this.dict.getNameAsString(COSName.BASE_FONT);
    }

    @Override
    protected Encoding readEncodingFromFont() throws IOException {
        if (!this.isEmbedded() && this.getStandard14AFM() != null) {
            return new Type1Encoding(this.getStandard14AFM());
        }
        if (this.getSymbolicFlag() != null && !this.getSymbolicFlag().booleanValue()) {
            return StandardEncoding.INSTANCE;
        }
        String standard14Name = Standard14Fonts.getMappedFontName(this.getName());
        if (this.isStandard14() && !standard14Name.equals("Symbol") && !standard14Name.equals("ZapfDingbats")) {
            return StandardEncoding.INSTANCE;
        }
        PostScriptTable post = this.ttf.getPostScript();
        HashMap<Integer, String> codeToName = new HashMap<Integer, String>();
        for (int code = 0; code <= 256; ++code) {
            int gid = this.codeToGID(code);
            if (gid <= 0) continue;
            String name = null;
            if (post != null) {
                name = post.getName(gid);
            }
            if (name == null) {
                name = Integer.toString(gid);
            }
            codeToName.put(code, name);
        }
        return new BuiltInEncoding(codeToName);
    }

    private PDTrueTypeFont(PDDocument document, TrueTypeFont ttf, Encoding encoding, boolean closeTTF) throws IOException {
        PDTrueTypeFontEmbedder embedder = new PDTrueTypeFontEmbedder(document, this.dict, ttf, encoding);
        this.encoding = encoding;
        this.ttf = ttf;
        this.setFontDescriptor(embedder.getFontDescriptor());
        this.isEmbedded = true;
        this.isDamaged = false;
        this.glyphList = GlyphList.getAdobeGlyphList();
        if (closeTTF) {
            ttf.close();
        }
    }

    @Override
    public int readCode(InputStream in) throws IOException {
        return in.read();
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
        if (this.getFontDescriptor() != null && (bbox = this.getFontDescriptor().getFontBoundingBox()) != null) {
            return new BoundingBox(bbox.getLowerLeftX(), bbox.getLowerLeftY(), bbox.getUpperRightX(), bbox.getUpperRightY());
        }
        return this.ttf.getFontBBox();
    }

    @Override
    public boolean isDamaged() {
        return this.isDamaged;
    }

    public TrueTypeFont getTrueTypeFont() {
        return this.ttf;
    }

    @Override
    public float getWidthFromFont(int code) throws IOException {
        int gid = this.codeToGID(code);
        float width = this.ttf.getAdvanceWidth(gid);
        float unitsPerEM = this.ttf.getUnitsPerEm();
        if (unitsPerEM != 1000.0f) {
            width *= 1000.0f / unitsPerEM;
        }
        return width;
    }

    @Override
    public float getHeight(int code) throws IOException {
        int gid = this.codeToGID(code);
        GlyphData glyph = this.ttf.getGlyph().getGlyph(gid);
        if (glyph != null) {
            return glyph.getBoundingBox().getHeight();
        }
        return 0.0f;
    }

    @Override
    protected byte[] encode(int unicode) throws IOException {
        if (this.encoding != null) {
            String uniName;
            if (!this.encoding.contains(this.getGlyphList().codePointToName(unicode))) {
                throw new IllegalArgumentException(String.format("U+%04X is not available in this font's encoding: %s", unicode, this.encoding.getEncodingName()));
            }
            String name = this.getGlyphList().codePointToName(unicode);
            Map<String, Integer> inverted = this.encoding.getNameToCodeMap();
            if (!this.ttf.hasGlyph(name) && !this.ttf.hasGlyph(uniName = UniUtil.getUniNameOfCodePoint(unicode))) {
                throw new IllegalArgumentException(String.format("No glyph for U+%04X in font %s", unicode, this.getName()));
            }
            int code = inverted.get(name);
            return new byte[]{(byte)code};
        }
        String name = this.getGlyphList().codePointToName(unicode);
        if (!this.ttf.hasGlyph(name)) {
            throw new IllegalArgumentException(String.format("No glyph for U+%04X in font %s", unicode, this.getName()));
        }
        int gid = this.ttf.nameToGID(name);
        Integer code = this.getGIDToCode().get(gid);
        if (code == null) {
            throw new IllegalArgumentException(String.format("U+%04X is not available in this font's Encoding", unicode));
        }
        return new byte[]{(byte)code.intValue()};
    }

    protected Map<Integer, Integer> getGIDToCode() throws IOException {
        if (this.gidToCode != null) {
            return this.gidToCode;
        }
        this.gidToCode = new HashMap<Integer, Integer>();
        for (int code = 0; code <= 255; ++code) {
            int gid = this.codeToGID(code);
            if (this.gidToCode.containsKey(gid)) continue;
            this.gidToCode.put(gid, code);
        }
        return this.gidToCode;
    }

    @Override
    public boolean isEmbedded() {
        return this.isEmbedded;
    }

    @Override
    public GeneralPath getPath(int code) throws IOException {
        int gid = this.codeToGID(code);
        GlyphTable glyphTable = this.ttf.getGlyph();
        if (glyphTable == null) {
            throw new IOException("glyf table is missing in font " + this.getName() + ", please report this file");
        }
        GlyphData glyph = glyphTable.getGlyph(gid);
        if (glyph == null) {
            return new GeneralPath();
        }
        return glyph.getPath();
    }

    @Override
    public GeneralPath getPath(String name) throws IOException {
        int gid = this.ttf.nameToGID(name);
        if (gid == 0) {
            try {
                gid = Integer.parseInt(name);
                if (gid > this.ttf.getNumberOfGlyphs()) {
                    gid = 0;
                }
            }
            catch (NumberFormatException e) {
                gid = 0;
            }
        }
        if (gid == 0) {
            return new GeneralPath();
        }
        GlyphData glyph = this.ttf.getGlyph().getGlyph(gid);
        if (glyph != null) {
            return glyph.getPath();
        }
        return new GeneralPath();
    }

    @Override
    public boolean hasGlyph(String name) throws IOException {
        int gid = this.ttf.nameToGID(name);
        return gid != 0;
    }

    @Override
    public FontBoxFont getFontBoxFont() {
        return this.ttf;
    }

    @Override
    public boolean hasGlyph(int code) throws IOException {
        return this.codeToGID(code) != 0;
    }

    public int codeToGID(int code) throws IOException {
        this.extractCmapTable();
        int gid = 0;
        if (!this.isSymbolic()) {
            Integer macCode;
            String unicode;
            String name = this.encoding.getName(code);
            if (".notdef".equals(name)) {
                return 0;
            }
            if (this.cmapWinUnicode != null && (unicode = GlyphList.getAdobeGlyphList().toUnicode(name)) != null) {
                int uni = unicode.codePointAt(0);
                gid = this.cmapWinUnicode.getGlyphId(uni);
            }
            if (gid == 0 && this.cmapMacRoman != null && (macCode = INVERTED_MACOS_ROMAN.get(name)) != null) {
                gid = this.cmapMacRoman.getGlyphId(macCode.intValue());
            }
            if (gid == 0) {
                gid = this.ttf.nameToGID(name);
            }
        } else {
            if (gid == 0 && this.cmapWinUnicode != null) {
                if (this.encoding instanceof WinAnsiEncoding || this.encoding instanceof MacRomanEncoding) {
                    String name = this.encoding.getName(code);
                    if (".notdef".equals(name)) {
                        return 0;
                    }
                    String unicode = GlyphList.getAdobeGlyphList().toUnicode(name);
                    if (unicode != null) {
                        int uni = unicode.codePointAt(0);
                        gid = this.cmapWinUnicode.getGlyphId(uni);
                    }
                } else {
                    gid = this.cmapWinUnicode.getGlyphId(code);
                }
            }
            if (this.cmapWinSymbol != null) {
                gid = this.cmapWinSymbol.getGlyphId(code);
                if (code >= 0 && code <= 255) {
                    if (gid == 0) {
                        gid = this.cmapWinSymbol.getGlyphId(code + 61440);
                    }
                    if (gid == 0) {
                        gid = this.cmapWinSymbol.getGlyphId(code + 61696);
                    }
                    if (gid == 0) {
                        gid = this.cmapWinSymbol.getGlyphId(code + 61952);
                    }
                }
            }
            if (gid == 0 && this.cmapMacRoman != null) {
                gid = this.cmapMacRoman.getGlyphId(code);
            }
        }
        return gid;
    }

    private void extractCmapTable() throws IOException {
        if (this.cmapInitialized) {
            return;
        }
        CmapTable cmapTable = this.ttf.getCmap();
        if (cmapTable != null) {
            CmapSubtable[] cmaps;
            for (CmapSubtable cmap : cmaps = cmapTable.getCmaps()) {
                if (3 == cmap.getPlatformId()) {
                    if (1 == cmap.getPlatformEncodingId()) {
                        this.cmapWinUnicode = cmap;
                        continue;
                    }
                    if (0 != cmap.getPlatformEncodingId()) continue;
                    this.cmapWinSymbol = cmap;
                    continue;
                }
                if (1 == cmap.getPlatformId() && 0 == cmap.getPlatformEncodingId()) {
                    this.cmapMacRoman = cmap;
                    continue;
                }
                if (0 == cmap.getPlatformId() && 0 == cmap.getPlatformEncodingId()) {
                    this.cmapWinUnicode = cmap;
                    continue;
                }
                if (0 != cmap.getPlatformId() || 3 != cmap.getPlatformEncodingId()) continue;
                this.cmapWinUnicode = cmap;
            }
        }
        this.cmapInitialized = true;
    }

    static {
        Map<Integer, String> codeToName = MacOSRomanEncoding.INSTANCE.getCodeToNameMap();
        for (Map.Entry<Integer, String> entry : codeToName.entrySet()) {
            if (INVERTED_MACOS_ROMAN.containsKey(entry.getValue())) continue;
            INVERTED_MACOS_ROMAN.put(entry.getValue(), entry.getKey());
        }
    }
}


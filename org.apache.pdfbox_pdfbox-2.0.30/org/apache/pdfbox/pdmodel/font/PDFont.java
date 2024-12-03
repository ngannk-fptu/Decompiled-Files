/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.apache.fontbox.afm.FontMetrics
 *  org.apache.fontbox.cmap.CMap
 */
package org.apache.pdfbox.pdmodel.font;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fontbox.afm.FontMetrics;
import org.apache.fontbox.cmap.CMap;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSInputStream;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.common.COSArrayList;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.font.CMapManager;
import org.apache.pdfbox.pdmodel.font.PDFontDescriptor;
import org.apache.pdfbox.pdmodel.font.PDFontLike;
import org.apache.pdfbox.pdmodel.font.PDType1FontEmbedder;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.font.encoding.GlyphList;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.Vector;

public abstract class PDFont
implements COSObjectable,
PDFontLike {
    private static final Log LOG = LogFactory.getLog(PDFont.class);
    protected static final Matrix DEFAULT_FONT_MATRIX = new Matrix(0.001f, 0.0f, 0.0f, 0.001f, 0.0f, 0.0f);
    protected final COSDictionary dict;
    private final CMap toUnicodeCMap;
    private final FontMetrics afmStandard14;
    private PDFontDescriptor fontDescriptor;
    private List<Float> widths;
    private float avgFontWidth;
    private float fontWidthOfSpace = -1.0f;
    private final Map<Integer, Float> codeToWidthMap;

    PDFont() {
        this.dict = new COSDictionary();
        this.dict.setItem(COSName.TYPE, (COSBase)COSName.FONT);
        this.toUnicodeCMap = null;
        this.fontDescriptor = null;
        this.afmStandard14 = null;
        this.codeToWidthMap = new HashMap<Integer, Float>();
    }

    PDFont(String baseFont) {
        this.dict = new COSDictionary();
        this.dict.setItem(COSName.TYPE, (COSBase)COSName.FONT);
        this.toUnicodeCMap = null;
        this.afmStandard14 = Standard14Fonts.getAFM(baseFont);
        if (this.afmStandard14 == null) {
            throw new IllegalArgumentException("No AFM for font " + baseFont);
        }
        this.fontDescriptor = PDType1FontEmbedder.buildFontDescriptor(this.afmStandard14);
        this.codeToWidthMap = new ConcurrentHashMap<Integer, Float>();
    }

    protected PDFont(COSDictionary fontDictionary) throws IOException {
        this.dict = fontDictionary;
        this.codeToWidthMap = new HashMap<Integer, Float>();
        this.afmStandard14 = Standard14Fonts.getAFM(this.getName());
        this.fontDescriptor = this.loadFontDescriptor();
        this.toUnicodeCMap = this.loadUnicodeCmap();
    }

    private PDFontDescriptor loadFontDescriptor() {
        COSDictionary fd = this.dict.getCOSDictionary(COSName.FONT_DESC);
        if (fd != null) {
            return new PDFontDescriptor(fd);
        }
        if (this.afmStandard14 != null) {
            return PDType1FontEmbedder.buildFontDescriptor(this.afmStandard14);
        }
        return null;
    }

    private CMap loadUnicodeCmap() {
        COSBase toUnicode = this.dict.getDictionaryObject(COSName.TO_UNICODE);
        if (toUnicode == null) {
            return null;
        }
        CMap cmap = null;
        try {
            cmap = this.readCMap(toUnicode);
            if (cmap != null && !cmap.hasUnicodeMappings()) {
                COSDictionary encodingDict;
                LOG.warn((Object)("Invalid ToUnicode CMap in font " + this.getName()));
                String cmapName = cmap.getName() != null ? cmap.getName() : "";
                String ordering = cmap.getOrdering() != null ? cmap.getOrdering() : "";
                COSBase encoding = this.dict.getDictionaryObject(COSName.ENCODING);
                if ((cmapName.contains("Identity") || ordering.contains("Identity") || COSName.IDENTITY_H.equals(encoding) || COSName.IDENTITY_V.equals(encoding)) && ((encodingDict = this.dict.getCOSDictionary(COSName.ENCODING)) == null || !encodingDict.containsKey(COSName.DIFFERENCES))) {
                    cmap = CMapManager.getPredefinedCMap(COSName.IDENTITY_H.getName());
                    LOG.warn((Object)"Using predefined identity CMap instead");
                }
            }
        }
        catch (IOException ex) {
            LOG.error((Object)("Could not read ToUnicode CMap in font " + this.getName()), (Throwable)ex);
        }
        return cmap;
    }

    protected final FontMetrics getStandard14AFM() {
        return this.afmStandard14;
    }

    @Override
    public PDFontDescriptor getFontDescriptor() {
        return this.fontDescriptor;
    }

    protected final void setFontDescriptor(PDFontDescriptor fontDescriptor) {
        this.fontDescriptor = fontDescriptor;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected final CMap readCMap(COSBase base) throws IOException {
        if (base instanceof COSName) {
            String name = ((COSName)base).getName();
            return CMapManager.getPredefinedCMap(name);
        }
        if (base instanceof COSStream) {
            COSInputStream input = null;
            try {
                input = ((COSStream)base).createInputStream();
                CMap cMap = CMapManager.parseCMap(input);
                return cMap;
            }
            finally {
                IOUtils.closeQuietly(input);
            }
        }
        throw new IOException("Expected Name or Stream");
    }

    @Override
    public COSDictionary getCOSObject() {
        return this.dict;
    }

    @Override
    public Vector getPositionVector(int code) {
        throw new UnsupportedOperationException("Horizontal fonts have no position vector");
    }

    public Vector getDisplacement(int code) throws IOException {
        return new Vector(this.getWidth(code) / 1000.0f, 0.0f);
    }

    @Override
    public float getWidth(int code) throws IOException {
        Float width = this.codeToWidthMap.get(code);
        if (width != null) {
            return width.floatValue();
        }
        if (this.dict.getDictionaryObject(COSName.WIDTHS) != null || this.dict.containsKey(COSName.MISSING_WIDTH)) {
            int firstChar = this.dict.getInt(COSName.FIRST_CHAR, -1);
            int lastChar = this.dict.getInt(COSName.LAST_CHAR, -1);
            int siz = this.getWidths().size();
            int idx = code - firstChar;
            if (siz > 0 && code >= firstChar && code <= lastChar && idx < siz) {
                width = this.getWidths().get(idx);
                if (width == null) {
                    width = Float.valueOf(0.0f);
                }
                this.codeToWidthMap.put(code, width);
                return width.floatValue();
            }
            PDFontDescriptor fd = this.getFontDescriptor();
            if (fd != null) {
                width = Float.valueOf(fd.getMissingWidth());
                this.codeToWidthMap.put(code, width);
                return width.floatValue();
            }
        }
        if (this.isStandard14()) {
            width = Float.valueOf(this.getStandard14Width(code));
            this.codeToWidthMap.put(code, width);
            return width.floatValue();
        }
        width = Float.valueOf(this.getWidthFromFont(code));
        this.codeToWidthMap.put(code, width);
        return width.floatValue();
    }

    protected abstract float getStandard14Width(int var1);

    public final byte[] encode(String text) throws IOException {
        int codePoint;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (int offset = 0; offset < text.length(); offset += Character.charCount(codePoint)) {
            codePoint = text.codePointAt(offset);
            byte[] bytes = this.encode(codePoint);
            out.write(bytes);
        }
        return out.toByteArray();
    }

    protected abstract byte[] encode(int var1) throws IOException;

    public float getStringWidth(String text) throws IOException {
        byte[] bytes = this.encode(text);
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        float width = 0.0f;
        while (in.available() > 0) {
            int code = this.readCode(in);
            width += this.getWidth(code);
        }
        return width;
    }

    @Override
    public float getAverageFontWidth() {
        float average;
        if (this.avgFontWidth != 0.0f) {
            average = this.avgFontWidth;
        } else {
            float totalWidth = 0.0f;
            float characterCount = 0.0f;
            COSArray widths = this.dict.getCOSArray(COSName.WIDTHS);
            if (widths != null) {
                for (int i = 0; i < widths.size(); ++i) {
                    COSNumber fontWidth;
                    float floatValue;
                    COSBase base = widths.getObject(i);
                    if (!(base instanceof COSNumber) || !((floatValue = (fontWidth = (COSNumber)base).floatValue()) > 0.0f)) continue;
                    totalWidth += floatValue;
                    characterCount += 1.0f;
                }
            }
            average = totalWidth > 0.0f ? totalWidth / characterCount : 0.0f;
            this.avgFontWidth = average;
        }
        return average;
    }

    public abstract int readCode(InputStream var1) throws IOException;

    public String toUnicode(int code, GlyphList customGlyphList) throws IOException {
        return this.toUnicode(code);
    }

    public String toUnicode(int code) throws IOException {
        if (this.toUnicodeCMap != null) {
            if (this.toUnicodeCMap.getName() != null && this.toUnicodeCMap.getName().startsWith("Identity-") && (this.dict.getDictionaryObject(COSName.TO_UNICODE) instanceof COSName || !this.toUnicodeCMap.hasUnicodeMappings())) {
                return new String(new char[]{(char)code});
            }
            return this.toUnicodeCMap.toUnicode(code);
        }
        return null;
    }

    public String getType() {
        return this.dict.getNameAsString(COSName.TYPE);
    }

    public String getSubType() {
        return this.dict.getNameAsString(COSName.SUBTYPE);
    }

    protected final List<Float> getWidths() {
        if (this.widths == null) {
            COSArray array = this.dict.getCOSArray(COSName.WIDTHS);
            this.widths = array != null ? COSArrayList.convertFloatCOSArrayToList(array) : Collections.emptyList();
        }
        return this.widths;
    }

    @Override
    public Matrix getFontMatrix() {
        return DEFAULT_FONT_MATRIX;
    }

    public float getSpaceWidth() {
        if (this.fontWidthOfSpace == -1.0f) {
            try {
                if (this.toUnicodeCMap != null && this.dict.containsKey(COSName.TO_UNICODE)) {
                    int spaceMapping = this.toUnicodeCMap.getSpaceMapping();
                    if (spaceMapping > -1) {
                        this.fontWidthOfSpace = this.getWidth(spaceMapping);
                    }
                } else {
                    this.fontWidthOfSpace = this.getWidth(32);
                }
                if (this.fontWidthOfSpace <= 0.0f) {
                    this.fontWidthOfSpace = this.getWidthFromFont(32);
                    if (this.fontWidthOfSpace <= 0.0f) {
                        this.fontWidthOfSpace = this.getAverageFontWidth();
                    }
                }
            }
            catch (Exception e) {
                LOG.error((Object)"Can't determine the width of the space character, assuming 250", (Throwable)e);
                this.fontWidthOfSpace = 250.0f;
            }
        }
        return this.fontWidthOfSpace;
    }

    public abstract boolean isVertical();

    public boolean isStandard14() {
        if (this.isEmbedded()) {
            return false;
        }
        return Standard14Fonts.containsName(this.getName());
    }

    public abstract void addToSubset(int var1);

    public abstract void subset() throws IOException;

    public abstract boolean willBeSubset();

    public boolean equals(Object other) {
        return other instanceof PDFont && ((PDFont)other).getCOSObject() == this.getCOSObject();
    }

    public int hashCode() {
        return this.getCOSObject().hashCode();
    }

    public String toString() {
        return this.getClass().getSimpleName() + " " + this.getName();
    }

    protected CMap getToUnicodeCMap() {
        return this.toUnicodeCMap;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.apache.fontbox.ttf.GlyphData
 *  org.apache.fontbox.ttf.GlyphTable
 *  org.apache.fontbox.ttf.HorizontalMetricsTable
 *  org.apache.fontbox.ttf.TrueTypeFont
 *  org.apache.fontbox.ttf.VerticalHeaderTable
 *  org.apache.fontbox.ttf.VerticalMetricsTable
 */
package org.apache.pdfbox.pdmodel.font;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fontbox.ttf.GlyphData;
import org.apache.fontbox.ttf.GlyphTable;
import org.apache.fontbox.ttf.HorizontalMetricsTable;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.fontbox.ttf.VerticalHeaderTable;
import org.apache.fontbox.ttf.VerticalMetricsTable;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.font.PDCIDFont;
import org.apache.pdfbox.pdmodel.font.PDCIDFontType2;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.ToUnicodeWriter;
import org.apache.pdfbox.pdmodel.font.TrueTypeEmbedder;

final class PDCIDFontType2Embedder
extends TrueTypeEmbedder {
    private static final Log LOG = LogFactory.getLog(PDCIDFontType2Embedder.class);
    private final PDDocument document;
    private final PDType0Font parent;
    private final COSDictionary dict;
    private final COSDictionary cidFont;
    private final boolean vertical;

    PDCIDFontType2Embedder(PDDocument document, COSDictionary dict, TrueTypeFont ttf, boolean embedSubset, PDType0Font parent, boolean vertical) throws IOException {
        super(document, dict, ttf, embedSubset);
        this.document = document;
        this.dict = dict;
        this.parent = parent;
        this.vertical = vertical;
        dict.setItem(COSName.SUBTYPE, (COSBase)COSName.TYPE0);
        dict.setName(COSName.BASE_FONT, this.fontDescriptor.getFontName());
        dict.setItem(COSName.ENCODING, (COSBase)(vertical ? COSName.IDENTITY_V : COSName.IDENTITY_H));
        this.cidFont = this.createCIDFont();
        COSArray descendantFonts = new COSArray();
        descendantFonts.add(this.cidFont);
        dict.setItem(COSName.DESCENDANT_FONTS, (COSBase)descendantFonts);
        if (!embedSubset) {
            this.buildToUnicodeCMap(null);
        }
    }

    @Override
    protected void buildSubset(InputStream ttfSubset, String tag, Map<Integer, Integer> gidToCid) throws IOException {
        TreeMap<Integer, Integer> cidToGid = new TreeMap<Integer, Integer>();
        for (Map.Entry<Integer, Integer> entry : gidToCid.entrySet()) {
            int newGID = entry.getKey();
            int oldGID = entry.getValue();
            cidToGid.put(oldGID, newGID);
        }
        this.buildToUnicodeCMap(gidToCid);
        if (this.vertical) {
            this.buildVerticalMetrics(cidToGid);
        }
        this.buildFontFile2(ttfSubset);
        this.addNameTag(tag);
        this.buildWidths(cidToGid);
        this.buildCIDToGIDMap(cidToGid);
        this.buildCIDSet(cidToGid);
    }

    private void buildToUnicodeCMap(Map<Integer, Integer> newGIDToOldCID) throws IOException {
        float version;
        ToUnicodeWriter toUniWriter = new ToUnicodeWriter();
        boolean hasSurrogates = false;
        int max = this.ttf.getMaximumProfile().getNumGlyphs();
        for (int gid = 1; gid <= max; ++gid) {
            int cid;
            if (newGIDToOldCID != null) {
                if (!newGIDToOldCID.containsKey(gid)) continue;
                cid = newGIDToOldCID.get(gid);
            } else {
                cid = gid;
            }
            List codes = this.cmapLookup.getCharCodes(cid);
            if (codes == null) continue;
            int codePoint = (Integer)codes.get(0);
            if (codePoint > 65535) {
                hasSurrogates = true;
            }
            toUniWriter.add(cid, new String(new int[]{codePoint}, 0, 1));
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        toUniWriter.writeTo(out);
        ByteArrayInputStream cMapStream = new ByteArrayInputStream(out.toByteArray());
        PDStream stream = new PDStream(this.document, (InputStream)cMapStream, COSName.FLATE_DECODE);
        if (hasSurrogates && (double)(version = this.document.getVersion()) < 1.5) {
            this.document.setVersion(1.5f);
        }
        this.dict.setItem(COSName.TO_UNICODE, (COSObjectable)stream);
    }

    private COSDictionary toCIDSystemInfo(String registry, String ordering, int supplement) {
        COSDictionary info = new COSDictionary();
        info.setString(COSName.REGISTRY, registry);
        info.setString(COSName.ORDERING, ordering);
        info.setInt(COSName.SUPPLEMENT, supplement);
        return info;
    }

    private COSDictionary createCIDFont() throws IOException {
        COSDictionary cidFont = new COSDictionary();
        cidFont.setItem(COSName.TYPE, (COSBase)COSName.FONT);
        cidFont.setItem(COSName.SUBTYPE, (COSBase)COSName.CID_FONT_TYPE2);
        cidFont.setName(COSName.BASE_FONT, this.fontDescriptor.getFontName());
        COSDictionary info = this.toCIDSystemInfo("Adobe", "Identity", 0);
        cidFont.setItem(COSName.CIDSYSTEMINFO, (COSBase)info);
        cidFont.setItem(COSName.FONT_DESC, (COSBase)this.fontDescriptor.getCOSObject());
        this.buildWidths(cidFont);
        if (this.vertical) {
            this.buildVerticalMetrics(cidFont);
        }
        cidFont.setItem(COSName.CID_TO_GID_MAP, (COSBase)COSName.IDENTITY);
        return cidFont;
    }

    private void addNameTag(String tag) {
        String name = this.fontDescriptor.getFontName();
        String newName = tag + name;
        this.dict.setName(COSName.BASE_FONT, newName);
        this.fontDescriptor.setFontName(newName);
        this.cidFont.setName(COSName.BASE_FONT, newName);
    }

    private void buildCIDToGIDMap(TreeMap<Integer, Integer> cidToGid) throws IOException {
        int cidMax = cidToGid.lastKey();
        byte[] buffer = new byte[cidMax * 2 + 2];
        int bi = 0;
        for (int i = 0; i <= cidMax; ++i) {
            Integer gid = cidToGid.get(i);
            if (gid != null) {
                buffer[bi] = (byte)(gid >> 8 & 0xFF);
                buffer[bi + 1] = (byte)(gid & 0xFF);
            }
            bi += 2;
        }
        ByteArrayInputStream input = new ByteArrayInputStream(buffer);
        PDStream stream = new PDStream(this.document, (InputStream)input, COSName.FLATE_DECODE);
        this.cidFont.setItem(COSName.CID_TO_GID_MAP, (COSObjectable)stream);
    }

    private void buildCIDSet(TreeMap<Integer, Integer> cidToGid) throws IOException {
        int cidMax = cidToGid.lastKey();
        byte[] bytes = new byte[cidMax / 8 + 1];
        for (int cid = 0; cid <= cidMax; ++cid) {
            int mask = 1 << 7 - cid % 8;
            int n = cid / 8;
            bytes[n] = (byte)(bytes[n] | mask);
        }
        ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        PDStream stream = new PDStream(this.document, (InputStream)input, COSName.FLATE_DECODE);
        this.fontDescriptor.setCIDSet(stream);
    }

    private void buildWidths(TreeMap<Integer, Integer> cidToGid) throws IOException {
        float scaling = 1000.0f / (float)this.ttf.getHeader().getUnitsPerEm();
        COSArray widths = new COSArray();
        COSArray ws = new COSArray();
        int prev = Integer.MIN_VALUE;
        Set<Integer> keys = cidToGid.keySet();
        HorizontalMetricsTable horizontalMetricsTable = this.ttf.getHorizontalMetrics();
        for (int cid : keys) {
            int gid = cidToGid.get(cid);
            long width = Math.round((float)horizontalMetricsTable.getAdvanceWidth(gid) * scaling);
            if (width == 1000L) continue;
            if (prev != cid - 1) {
                ws = new COSArray();
                widths.add(COSInteger.get(cid));
                widths.add(ws);
            }
            ws.add(COSInteger.get(width));
            prev = cid;
        }
        this.cidFont.setItem(COSName.W, (COSBase)widths);
    }

    private boolean buildVerticalHeader(COSDictionary cidFont) throws IOException {
        VerticalHeaderTable vhea = this.ttf.getVerticalHeader();
        if (vhea == null) {
            LOG.warn((Object)"Font to be subset is set to vertical, but has no 'vhea' table");
            return false;
        }
        float scaling = 1000.0f / (float)this.ttf.getHeader().getUnitsPerEm();
        long v = Math.round((float)vhea.getAscender() * scaling);
        long w1 = Math.round((float)(-vhea.getAdvanceHeightMax()) * scaling);
        if (v != 880L || w1 != -1000L) {
            COSArray cosDw2 = new COSArray();
            cosDw2.add(COSInteger.get(v));
            cosDw2.add(COSInteger.get(w1));
            cidFont.setItem(COSName.DW2, (COSBase)cosDw2);
        }
        return true;
    }

    private void buildVerticalMetrics(TreeMap<Integer, Integer> cidToGid) throws IOException {
        if (!this.buildVerticalHeader(this.cidFont)) {
            return;
        }
        float scaling = 1000.0f / (float)this.ttf.getHeader().getUnitsPerEm();
        VerticalHeaderTable vhea = this.ttf.getVerticalHeader();
        VerticalMetricsTable vmtx = this.ttf.getVerticalMetrics();
        GlyphTable glyf = this.ttf.getGlyph();
        HorizontalMetricsTable hmtx = this.ttf.getHorizontalMetrics();
        long v_y = Math.round((float)vhea.getAscender() * scaling);
        long w1 = Math.round((float)(-vhea.getAdvanceHeightMax()) * scaling);
        COSArray heights = new COSArray();
        COSArray w2 = new COSArray();
        int prev = Integer.MIN_VALUE;
        Set<Integer> keys = cidToGid.keySet();
        for (int cid : keys) {
            GlyphData glyph = glyf.getGlyph(cid);
            if (glyph == null) continue;
            long height = Math.round((float)(glyph.getYMaximum() + vmtx.getTopSideBearing(cid)) * scaling);
            long advance = Math.round((float)(-vmtx.getAdvanceHeight(cid)) * scaling);
            if (height == v_y && advance == w1) continue;
            if (prev != cid - 1) {
                w2 = new COSArray();
                heights.add(COSInteger.get(cid));
                heights.add(w2);
            }
            w2.add(COSInteger.get(advance));
            long width = Math.round((float)hmtx.getAdvanceWidth(cid) * scaling);
            w2.add(COSInteger.get(width / 2L));
            w2.add(COSInteger.get(height));
            prev = cid;
        }
        this.cidFont.setItem(COSName.W2, (COSBase)heights);
    }

    private void buildWidths(COSDictionary cidFont) throws IOException {
        int cidMax = this.ttf.getNumberOfGlyphs();
        int[] gidwidths = new int[cidMax * 2];
        HorizontalMetricsTable horizontalMetricsTable = this.ttf.getHorizontalMetrics();
        for (int cid = 0; cid < cidMax; ++cid) {
            gidwidths[cid * 2] = cid;
            gidwidths[cid * 2 + 1] = horizontalMetricsTable.getAdvanceWidth(cid);
        }
        cidFont.setItem(COSName.W, (COSBase)this.getWidths(gidwidths));
    }

    private COSArray getWidths(int[] widths) throws IOException {
        if (widths.length < 2) {
            throw new IllegalArgumentException("length of widths must be >= 2");
        }
        float scaling = 1000.0f / (float)this.ttf.getHeader().getUnitsPerEm();
        long lastCid = widths[0];
        long lastValue = Math.round((float)widths[1] * scaling);
        COSArray inner = new COSArray();
        COSArray outer = new COSArray();
        outer.add(COSInteger.get(lastCid));
        State state = State.FIRST;
        for (int i = 2; i < widths.length - 1; i += 2) {
            long cid = widths[i];
            long value = Math.round((float)widths[i + 1] * scaling);
            switch (state) {
                case FIRST: {
                    if (cid == lastCid + 1L && value == lastValue) {
                        state = State.SERIAL;
                        break;
                    }
                    if (cid == lastCid + 1L) {
                        state = State.BRACKET;
                        inner = new COSArray();
                        inner.add(COSInteger.get(lastValue));
                        break;
                    }
                    inner = new COSArray();
                    inner.add(COSInteger.get(lastValue));
                    outer.add(inner);
                    outer.add(COSInteger.get(cid));
                    break;
                }
                case BRACKET: {
                    if (cid == lastCid + 1L && value == lastValue) {
                        state = State.SERIAL;
                        outer.add(inner);
                        outer.add(COSInteger.get(lastCid));
                        break;
                    }
                    if (cid == lastCid + 1L) {
                        inner.add(COSInteger.get(lastValue));
                        break;
                    }
                    state = State.FIRST;
                    inner.add(COSInteger.get(lastValue));
                    outer.add(inner);
                    outer.add(COSInteger.get(cid));
                    break;
                }
                case SERIAL: {
                    if (cid == lastCid + 1L && value == lastValue) break;
                    outer.add(COSInteger.get(lastCid));
                    outer.add(COSInteger.get(lastValue));
                    outer.add(COSInteger.get(cid));
                    state = State.FIRST;
                }
            }
            lastValue = value;
            lastCid = cid;
        }
        switch (state) {
            case FIRST: {
                inner = new COSArray();
                inner.add(COSInteger.get(lastValue));
                outer.add(inner);
                break;
            }
            case BRACKET: {
                inner.add(COSInteger.get(lastValue));
                outer.add(inner);
                break;
            }
            case SERIAL: {
                outer.add(COSInteger.get(lastCid));
                outer.add(COSInteger.get(lastValue));
            }
        }
        return outer;
    }

    private void buildVerticalMetrics(COSDictionary cidFont) throws IOException {
        if (!this.buildVerticalHeader(cidFont)) {
            return;
        }
        int cidMax = this.ttf.getNumberOfGlyphs();
        int[] gidMetrics = new int[cidMax * 4];
        GlyphTable glyphTable = this.ttf.getGlyph();
        VerticalMetricsTable verticalMetricsTable = this.ttf.getVerticalMetrics();
        HorizontalMetricsTable horizontalMetricsTable = this.ttf.getHorizontalMetrics();
        for (int cid = 0; cid < cidMax; ++cid) {
            GlyphData glyph = glyphTable.getGlyph(cid);
            if (glyph == null) {
                gidMetrics[cid * 4] = Integer.MIN_VALUE;
                continue;
            }
            gidMetrics[cid * 4] = cid;
            gidMetrics[cid * 4 + 1] = verticalMetricsTable.getAdvanceHeight(cid);
            gidMetrics[cid * 4 + 2] = horizontalMetricsTable.getAdvanceWidth(cid);
            gidMetrics[cid * 4 + 3] = glyph.getYMaximum() + verticalMetricsTable.getTopSideBearing(cid);
        }
        cidFont.setItem(COSName.W2, (COSBase)this.getVerticalMetrics(gidMetrics));
    }

    private COSArray getVerticalMetrics(int[] values) throws IOException {
        if (values.length < 4) {
            throw new IllegalArgumentException("length of values must be >= 4");
        }
        float scaling = 1000.0f / (float)this.ttf.getHeader().getUnitsPerEm();
        long lastCid = values[0];
        long lastW1Value = Math.round((float)(-values[1]) * scaling);
        long lastVxValue = Math.round((float)values[2] * scaling / 2.0f);
        long lastVyValue = Math.round((float)values[3] * scaling);
        COSArray inner = new COSArray();
        COSArray outer = new COSArray();
        outer.add(COSInteger.get(lastCid));
        State state = State.FIRST;
        for (int i = 4; i < values.length - 3; i += 4) {
            long cid = values[i];
            if (cid == Integer.MIN_VALUE) continue;
            long w1Value = Math.round((float)(-values[i + 1]) * scaling);
            long vxValue = Math.round((float)values[i + 2] * scaling / 2.0f);
            long vyValue = Math.round((float)values[i + 3] * scaling);
            switch (state) {
                case FIRST: {
                    if (cid == lastCid + 1L && w1Value == lastW1Value && vxValue == lastVxValue && vyValue == lastVyValue) {
                        state = State.SERIAL;
                        break;
                    }
                    if (cid == lastCid + 1L) {
                        state = State.BRACKET;
                        inner = new COSArray();
                        inner.add(COSInteger.get(lastW1Value));
                        inner.add(COSInteger.get(lastVxValue));
                        inner.add(COSInteger.get(lastVyValue));
                        break;
                    }
                    inner = new COSArray();
                    inner.add(COSInteger.get(lastW1Value));
                    inner.add(COSInteger.get(lastVxValue));
                    inner.add(COSInteger.get(lastVyValue));
                    outer.add(inner);
                    outer.add(COSInteger.get(cid));
                    break;
                }
                case BRACKET: {
                    if (cid == lastCid + 1L && w1Value == lastW1Value && vxValue == lastVxValue && vyValue == lastVyValue) {
                        state = State.SERIAL;
                        outer.add(inner);
                        outer.add(COSInteger.get(lastCid));
                        break;
                    }
                    if (cid == lastCid + 1L) {
                        inner.add(COSInteger.get(lastW1Value));
                        inner.add(COSInteger.get(lastVxValue));
                        inner.add(COSInteger.get(lastVyValue));
                        break;
                    }
                    state = State.FIRST;
                    inner.add(COSInteger.get(lastW1Value));
                    inner.add(COSInteger.get(lastVxValue));
                    inner.add(COSInteger.get(lastVyValue));
                    outer.add(inner);
                    outer.add(COSInteger.get(cid));
                    break;
                }
                case SERIAL: {
                    if (cid == lastCid + 1L && w1Value == lastW1Value && vxValue == lastVxValue && vyValue == lastVyValue) break;
                    outer.add(COSInteger.get(lastCid));
                    outer.add(COSInteger.get(lastW1Value));
                    outer.add(COSInteger.get(lastVxValue));
                    outer.add(COSInteger.get(lastVyValue));
                    outer.add(COSInteger.get(cid));
                    state = State.FIRST;
                }
            }
            lastW1Value = w1Value;
            lastVxValue = vxValue;
            lastVyValue = vyValue;
            lastCid = cid;
        }
        switch (state) {
            case FIRST: {
                inner = new COSArray();
                inner.add(COSInteger.get(lastW1Value));
                inner.add(COSInteger.get(lastVxValue));
                inner.add(COSInteger.get(lastVyValue));
                outer.add(inner);
                break;
            }
            case BRACKET: {
                inner.add(COSInteger.get(lastW1Value));
                inner.add(COSInteger.get(lastVxValue));
                inner.add(COSInteger.get(lastVyValue));
                outer.add(inner);
                break;
            }
            case SERIAL: {
                outer.add(COSInteger.get(lastCid));
                outer.add(COSInteger.get(lastW1Value));
                outer.add(COSInteger.get(lastVxValue));
                outer.add(COSInteger.get(lastVyValue));
            }
        }
        return outer;
    }

    public PDCIDFont getCIDFont() throws IOException {
        return new PDCIDFontType2(this.cidFont, this.parent, this.ttf);
    }

    static enum State {
        FIRST,
        BRACKET,
        SERIAL;

    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.fontbox.ttf;

import java.io.IOException;
import org.apache.fontbox.ttf.GlyphData;
import org.apache.fontbox.ttf.HorizontalMetricsTable;
import org.apache.fontbox.ttf.IndexToLocationTable;
import org.apache.fontbox.ttf.TTFDataStream;
import org.apache.fontbox.ttf.TTFTable;
import org.apache.fontbox.ttf.TrueTypeFont;

public class GlyphTable
extends TTFTable {
    public static final String TAG = "glyf";
    private GlyphData[] glyphs;
    private TTFDataStream data;
    private IndexToLocationTable loca;
    private int numGlyphs;
    private int cached = 0;
    private HorizontalMetricsTable hmt = null;
    private static final int MAX_CACHE_SIZE = 5000;
    private static final int MAX_CACHED_GLYPHS = 100;

    GlyphTable(TrueTypeFont font) {
        super(font);
    }

    @Override
    void read(TrueTypeFont ttf, TTFDataStream data) throws IOException {
        this.loca = ttf.getIndexToLocation();
        this.numGlyphs = ttf.getNumberOfGlyphs();
        if (this.numGlyphs < 5000) {
            this.glyphs = new GlyphData[this.numGlyphs];
        }
        this.data = data;
        this.hmt = this.font.getHorizontalMetrics();
        this.initialized = true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Deprecated
    public GlyphData[] getGlyphs() throws IOException {
        TTFDataStream tTFDataStream = this.data;
        synchronized (tTFDataStream) {
            long[] offsets = this.loca.getOffsets();
            long endOfGlyphs = offsets[this.numGlyphs];
            long offset = this.getOffset();
            if (this.glyphs == null) {
                this.glyphs = new GlyphData[this.numGlyphs];
            }
            for (int gid = 0; gid < this.numGlyphs && (endOfGlyphs == 0L || endOfGlyphs != offsets[gid]); ++gid) {
                if (offsets[gid + 1] <= offsets[gid] || this.glyphs[gid] != null) continue;
                this.data.seek(offset + offsets[gid]);
                if (this.glyphs[gid] == null) {
                    ++this.cached;
                }
                this.glyphs[gid] = this.getGlyphData(gid);
            }
            this.initialized = true;
            return this.glyphs;
        }
    }

    public void setGlyphs(GlyphData[] glyphsValue) {
        this.glyphs = glyphsValue;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public GlyphData getGlyph(int gid) throws IOException {
        if (gid < 0 || gid >= this.numGlyphs) {
            return null;
        }
        if (this.glyphs != null && this.glyphs[gid] != null) {
            return this.glyphs[gid];
        }
        TTFDataStream tTFDataStream = this.data;
        synchronized (tTFDataStream) {
            GlyphData glyph;
            long[] offsets = this.loca.getOffsets();
            if (offsets[gid] == offsets[gid + 1]) {
                glyph = new GlyphData();
                glyph.initEmptyData();
            } else {
                long currentPosition = this.data.getCurrentPosition();
                this.data.seek(this.getOffset() + offsets[gid]);
                glyph = this.getGlyphData(gid);
                this.data.seek(currentPosition);
            }
            if (this.glyphs != null && this.glyphs[gid] == null && this.cached < 100) {
                this.glyphs[gid] = glyph;
                ++this.cached;
            }
            return glyph;
        }
    }

    private GlyphData getGlyphData(int gid) throws IOException {
        GlyphData glyph = new GlyphData();
        int leftSideBearing = this.hmt == null ? 0 : this.hmt.getLeftSideBearing(gid);
        glyph.initData(this, this.data, leftSideBearing);
        if (glyph.getDescription().isComposite()) {
            glyph.getDescription().resolve();
        }
        return glyph;
    }
}


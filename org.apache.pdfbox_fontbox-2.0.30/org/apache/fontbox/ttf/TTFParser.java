/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.fontbox.ttf;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fontbox.ttf.CmapTable;
import org.apache.fontbox.ttf.DigitalSignatureTable;
import org.apache.fontbox.ttf.GlyphSubstitutionTable;
import org.apache.fontbox.ttf.GlyphTable;
import org.apache.fontbox.ttf.HeaderTable;
import org.apache.fontbox.ttf.HorizontalHeaderTable;
import org.apache.fontbox.ttf.HorizontalMetricsTable;
import org.apache.fontbox.ttf.IndexToLocationTable;
import org.apache.fontbox.ttf.KerningTable;
import org.apache.fontbox.ttf.MaximumProfileTable;
import org.apache.fontbox.ttf.MemoryTTFDataStream;
import org.apache.fontbox.ttf.NamingTable;
import org.apache.fontbox.ttf.OS2WindowsMetricsTable;
import org.apache.fontbox.ttf.OpenTypeFont;
import org.apache.fontbox.ttf.PostScriptTable;
import org.apache.fontbox.ttf.RAFDataStream;
import org.apache.fontbox.ttf.TTFDataStream;
import org.apache.fontbox.ttf.TTFTable;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.fontbox.ttf.VerticalHeaderTable;
import org.apache.fontbox.ttf.VerticalMetricsTable;
import org.apache.fontbox.ttf.VerticalOriginTable;

public class TTFParser {
    private static final Log LOG = LogFactory.getLog(TTFParser.class);
    private boolean isEmbedded = false;
    private boolean parseOnDemandOnly = false;

    public TTFParser() {
        this(false);
    }

    public TTFParser(boolean isEmbedded) {
        this(isEmbedded, false);
    }

    public TTFParser(boolean isEmbedded, boolean parseOnDemand) {
        this.isEmbedded = isEmbedded;
        this.parseOnDemandOnly = parseOnDemand;
    }

    public TrueTypeFont parse(String ttfFile) throws IOException {
        return this.parse(new File(ttfFile));
    }

    public TrueTypeFont parse(File ttfFile) throws IOException {
        RAFDataStream raf = new RAFDataStream(ttfFile, "r");
        try {
            return this.parse(raf);
        }
        catch (IOException ex) {
            raf.close();
            throw ex;
        }
    }

    public TrueTypeFont parse(InputStream inputStream) throws IOException {
        return this.parse(new MemoryTTFDataStream(inputStream));
    }

    public TrueTypeFont parseEmbedded(InputStream inputStream) throws IOException {
        this.isEmbedded = true;
        return this.parse(new MemoryTTFDataStream(inputStream));
    }

    TrueTypeFont parse(TTFDataStream raf) throws IOException {
        TrueTypeFont font = this.newFont(raf);
        font.setVersion(raf.read32Fixed());
        int numberOfTables = raf.readUnsignedShort();
        int searchRange = raf.readUnsignedShort();
        int entrySelector = raf.readUnsignedShort();
        int rangeShift = raf.readUnsignedShort();
        for (int i = 0; i < numberOfTables; ++i) {
            TTFTable table = this.readTableDirectory(font, raf);
            if (table == null) continue;
            if (table.getOffset() + table.getLength() > font.getOriginalDataSize()) {
                LOG.warn((Object)("Skip table '" + table.getTag() + "' which goes past the file size; offset: " + table.getOffset() + ", size: " + table.getLength() + ", font size: " + font.getOriginalDataSize()));
                continue;
            }
            font.addTable(table);
        }
        if (!this.parseOnDemandOnly) {
            this.parseTables(font);
        }
        return font;
    }

    TrueTypeFont newFont(TTFDataStream raf) {
        return new TrueTypeFont(raf);
    }

    private void parseTables(TrueTypeFont font) throws IOException {
        for (TTFTable table : font.getTables()) {
            if (table.getInitialized()) continue;
            font.readTable(table);
        }
        boolean hasCFF = font.tables.containsKey("CFF ");
        boolean isOTF = font instanceof OpenTypeFont;
        boolean isPostScript = isOTF ? ((OpenTypeFont)font).isPostScript() : hasCFF;
        HeaderTable head = font.getHeader();
        if (head == null) {
            throw new IOException("'head' table is mandatory");
        }
        HorizontalHeaderTable hh = font.getHorizontalHeader();
        if (hh == null) {
            throw new IOException("'hhea' table is mandatory");
        }
        MaximumProfileTable maxp = font.getMaximumProfile();
        if (maxp == null) {
            throw new IOException("'maxp' table is mandatory");
        }
        PostScriptTable post = font.getPostScript();
        if (post == null && !this.isEmbedded) {
            throw new IOException("'post' table is mandatory");
        }
        if (!isPostScript) {
            if (font.getIndexToLocation() == null) {
                throw new IOException("'loca' table is mandatory");
            }
            if (font.getGlyph() == null) {
                throw new IOException("'glyf' table is mandatory");
            }
        } else if (!isOTF) {
            throw new IOException("True Type fonts using CFF outlines are not supported");
        }
        if (font.getNaming() == null && !this.isEmbedded) {
            throw new IOException("'name' table is mandatory");
        }
        if (font.getHorizontalMetrics() == null) {
            throw new IOException("'hmtx' table is mandatory");
        }
        if (!this.isEmbedded && font.getCmap() == null) {
            throw new IOException("'cmap' table is mandatory");
        }
    }

    protected boolean allowCFF() {
        return false;
    }

    private TTFTable readTableDirectory(TrueTypeFont font, TTFDataStream raf) throws IOException {
        String tag = raf.readString(4);
        TTFTable table = tag.equals("cmap") ? new CmapTable(font) : (tag.equals("glyf") ? new GlyphTable(font) : (tag.equals("head") ? new HeaderTable(font) : (tag.equals("hhea") ? new HorizontalHeaderTable(font) : (tag.equals("hmtx") ? new HorizontalMetricsTable(font) : (tag.equals("loca") ? new IndexToLocationTable(font) : (tag.equals("maxp") ? new MaximumProfileTable(font) : (tag.equals("name") ? new NamingTable(font) : (tag.equals("OS/2") ? new OS2WindowsMetricsTable(font) : (tag.equals("post") ? new PostScriptTable(font) : (tag.equals("DSIG") ? new DigitalSignatureTable(font) : (tag.equals("kern") ? new KerningTable(font) : (tag.equals("vhea") ? new VerticalHeaderTable(font) : (tag.equals("vmtx") ? new VerticalMetricsTable(font) : (tag.equals("VORG") ? new VerticalOriginTable(font) : (tag.equals("GSUB") ? new GlyphSubstitutionTable(font) : this.readTable(font, tag))))))))))))))));
        table.setTag(tag);
        table.setCheckSum(raf.readUnsignedInt());
        table.setOffset(raf.readUnsignedInt());
        table.setLength(raf.readUnsignedInt());
        if (table.getLength() == 0L && !tag.equals("glyf")) {
            return null;
        }
        return table;
    }

    protected TTFTable readTable(TrueTypeFont font, String tag) {
        return new TTFTable(font);
    }
}


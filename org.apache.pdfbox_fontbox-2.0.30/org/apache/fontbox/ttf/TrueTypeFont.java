/*
 * Decompiled with CFR 0.152.
 */
package org.apache.fontbox.ttf;

import java.awt.geom.GeneralPath;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.fontbox.FontBoxFont;
import org.apache.fontbox.ttf.CmapLookup;
import org.apache.fontbox.ttf.CmapSubtable;
import org.apache.fontbox.ttf.CmapTable;
import org.apache.fontbox.ttf.GlyphData;
import org.apache.fontbox.ttf.GlyphSubstitutionTable;
import org.apache.fontbox.ttf.GlyphTable;
import org.apache.fontbox.ttf.HeaderTable;
import org.apache.fontbox.ttf.HorizontalHeaderTable;
import org.apache.fontbox.ttf.HorizontalMetricsTable;
import org.apache.fontbox.ttf.IndexToLocationTable;
import org.apache.fontbox.ttf.KerningTable;
import org.apache.fontbox.ttf.MaximumProfileTable;
import org.apache.fontbox.ttf.NamingTable;
import org.apache.fontbox.ttf.OS2WindowsMetricsTable;
import org.apache.fontbox.ttf.PostScriptTable;
import org.apache.fontbox.ttf.SubstitutingCmapLookup;
import org.apache.fontbox.ttf.TTFDataStream;
import org.apache.fontbox.ttf.TTFTable;
import org.apache.fontbox.ttf.VerticalHeaderTable;
import org.apache.fontbox.ttf.VerticalMetricsTable;
import org.apache.fontbox.ttf.VerticalOriginTable;
import org.apache.fontbox.util.BoundingBox;

public class TrueTypeFont
implements FontBoxFont,
Closeable {
    private float version;
    private int numberOfGlyphs = -1;
    private int unitsPerEm = -1;
    protected Map<String, TTFTable> tables = new HashMap<String, TTFTable>();
    private final TTFDataStream data;
    private Map<String, Integer> postScriptNames;
    private final List<String> enabledGsubFeatures = new ArrayList<String>();

    TrueTypeFont(TTFDataStream fontData) {
        this.data = fontData;
    }

    @Override
    public void close() throws IOException {
        this.data.close();
    }

    protected void finalize() throws Throwable {
        super.finalize();
        this.close();
    }

    public float getVersion() {
        return this.version;
    }

    void setVersion(float versionValue) {
        this.version = versionValue;
    }

    void addTable(TTFTable table) {
        this.tables.put(table.getTag(), table);
    }

    public Collection<TTFTable> getTables() {
        return this.tables.values();
    }

    public Map<String, TTFTable> getTableMap() {
        return this.tables;
    }

    public synchronized byte[] getTableBytes(TTFTable table) throws IOException {
        long currentPosition = this.data.getCurrentPosition();
        this.data.seek(table.getOffset());
        byte[] bytes = this.data.read((int)table.getLength());
        this.data.seek(currentPosition);
        return bytes;
    }

    protected synchronized TTFTable getTable(String tag) throws IOException {
        TTFTable ttfTable = this.tables.get(tag);
        if (ttfTable != null && !ttfTable.getInitialized()) {
            this.readTable(ttfTable);
        }
        return ttfTable;
    }

    public NamingTable getNaming() throws IOException {
        return (NamingTable)this.getTable("name");
    }

    public PostScriptTable getPostScript() throws IOException {
        return (PostScriptTable)this.getTable("post");
    }

    public OS2WindowsMetricsTable getOS2Windows() throws IOException {
        return (OS2WindowsMetricsTable)this.getTable("OS/2");
    }

    public MaximumProfileTable getMaximumProfile() throws IOException {
        return (MaximumProfileTable)this.getTable("maxp");
    }

    public HeaderTable getHeader() throws IOException {
        return (HeaderTable)this.getTable("head");
    }

    public HorizontalHeaderTable getHorizontalHeader() throws IOException {
        return (HorizontalHeaderTable)this.getTable("hhea");
    }

    public HorizontalMetricsTable getHorizontalMetrics() throws IOException {
        return (HorizontalMetricsTable)this.getTable("hmtx");
    }

    public IndexToLocationTable getIndexToLocation() throws IOException {
        return (IndexToLocationTable)this.getTable("loca");
    }

    public GlyphTable getGlyph() throws IOException {
        return (GlyphTable)this.getTable("glyf");
    }

    public CmapTable getCmap() throws IOException {
        return (CmapTable)this.getTable("cmap");
    }

    public VerticalHeaderTable getVerticalHeader() throws IOException {
        return (VerticalHeaderTable)this.getTable("vhea");
    }

    public VerticalMetricsTable getVerticalMetrics() throws IOException {
        return (VerticalMetricsTable)this.getTable("vmtx");
    }

    public VerticalOriginTable getVerticalOrigin() throws IOException {
        return (VerticalOriginTable)this.getTable("VORG");
    }

    public KerningTable getKerning() throws IOException {
        return (KerningTable)this.getTable("kern");
    }

    public GlyphSubstitutionTable getGsub() throws IOException {
        return (GlyphSubstitutionTable)this.getTable("GSUB");
    }

    public InputStream getOriginalData() throws IOException {
        return this.data.getOriginalData();
    }

    public long getOriginalDataSize() {
        return this.data.getOriginalDataSize();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void readTable(TTFTable table) throws IOException {
        TTFDataStream tTFDataStream = this.data;
        synchronized (tTFDataStream) {
            long currentPosition = this.data.getCurrentPosition();
            this.data.seek(table.getOffset());
            table.read(this, this.data);
            this.data.seek(currentPosition);
        }
    }

    public int getNumberOfGlyphs() throws IOException {
        if (this.numberOfGlyphs == -1) {
            MaximumProfileTable maximumProfile = this.getMaximumProfile();
            this.numberOfGlyphs = maximumProfile != null ? maximumProfile.getNumGlyphs() : 0;
        }
        return this.numberOfGlyphs;
    }

    public int getUnitsPerEm() throws IOException {
        if (this.unitsPerEm == -1) {
            HeaderTable header = this.getHeader();
            this.unitsPerEm = header != null ? header.getUnitsPerEm() : 0;
        }
        return this.unitsPerEm;
    }

    public int getAdvanceWidth(int gid) throws IOException {
        HorizontalMetricsTable hmtx = this.getHorizontalMetrics();
        if (hmtx != null) {
            return hmtx.getAdvanceWidth(gid);
        }
        return 250;
    }

    public int getAdvanceHeight(int gid) throws IOException {
        VerticalMetricsTable vmtx = this.getVerticalMetrics();
        if (vmtx != null) {
            return vmtx.getAdvanceHeight(gid);
        }
        return 250;
    }

    @Override
    public String getName() throws IOException {
        NamingTable namingTable = this.getNaming();
        if (namingTable != null) {
            return namingTable.getPostScriptName();
        }
        return null;
    }

    private synchronized void readPostScriptNames() throws IOException {
        if (this.postScriptNames == null && this.getPostScript() != null) {
            String[] names = this.getPostScript().getGlyphNames();
            if (names != null) {
                this.postScriptNames = new HashMap<String, Integer>(names.length);
                for (int i = 0; i < names.length; ++i) {
                    this.postScriptNames.put(names[i], i);
                }
            } else {
                this.postScriptNames = new HashMap<String, Integer>();
            }
        }
    }

    @Deprecated
    public CmapSubtable getUnicodeCmap() throws IOException {
        return this.getUnicodeCmap(true);
    }

    @Deprecated
    public CmapSubtable getUnicodeCmap(boolean isStrict) throws IOException {
        return this.getUnicodeCmapImpl(isStrict);
    }

    public CmapLookup getUnicodeCmapLookup() throws IOException {
        return this.getUnicodeCmapLookup(true);
    }

    public CmapLookup getUnicodeCmapLookup(boolean isStrict) throws IOException {
        GlyphSubstitutionTable table;
        CmapSubtable cmap = this.getUnicodeCmapImpl(isStrict);
        if (!this.enabledGsubFeatures.isEmpty() && (table = this.getGsub()) != null) {
            return new SubstitutingCmapLookup(cmap, table, Collections.unmodifiableList(this.enabledGsubFeatures));
        }
        return cmap;
    }

    private CmapSubtable getUnicodeCmapImpl(boolean isStrict) throws IOException {
        CmapTable cmapTable = this.getCmap();
        if (cmapTable == null) {
            if (isStrict) {
                throw new IOException("The TrueType font " + this.getName() + " does not contain a 'cmap' table");
            }
            return null;
        }
        CmapSubtable cmap = cmapTable.getSubtable(0, 4);
        if (cmap == null) {
            cmap = cmapTable.getSubtable(3, 10);
        }
        if (cmap == null) {
            cmap = cmapTable.getSubtable(0, 3);
        }
        if (cmap == null) {
            cmap = cmapTable.getSubtable(3, 1);
        }
        if (cmap == null) {
            cmap = cmapTable.getSubtable(3, 0);
        }
        if (cmap == null) {
            if (isStrict) {
                throw new IOException("The TrueType font does not contain a Unicode cmap");
            }
            if (cmapTable.getCmaps().length > 0) {
                cmap = cmapTable.getCmaps()[0];
            }
        }
        return cmap;
    }

    public int nameToGID(String name) throws IOException {
        Integer gid;
        this.readPostScriptNames();
        if (this.postScriptNames != null && (gid = this.postScriptNames.get(name)) != null && gid > 0 && gid < this.getMaximumProfile().getNumGlyphs()) {
            return gid;
        }
        int uni = this.parseUniName(name);
        if (uni > -1) {
            CmapLookup cmap = this.getUnicodeCmapLookup(false);
            return cmap.getGlyphId(uni);
        }
        if (name.matches("g\\d+")) {
            return Integer.parseInt(name.substring(1));
        }
        return 0;
    }

    private int parseUniName(String name) {
        if (name.startsWith("uni") && name.length() == 7) {
            int nameLength = name.length();
            StringBuilder uniStr = new StringBuilder();
            try {
                int chPos = 3;
                while (chPos + 4 <= nameLength) {
                    int codePoint = Integer.parseInt(name.substring(chPos, chPos + 4), 16);
                    if (codePoint <= 55295 || codePoint >= 57344) {
                        uniStr.append((char)codePoint);
                    }
                    chPos += 4;
                }
                String unicode = uniStr.toString();
                if (unicode.length() == 0) {
                    return -1;
                }
                return unicode.codePointAt(0);
            }
            catch (NumberFormatException e) {
                return -1;
            }
        }
        return -1;
    }

    @Override
    public GeneralPath getPath(String name) throws IOException {
        int gid = this.nameToGID(name);
        GlyphData glyph = this.getGlyph().getGlyph(gid);
        if (glyph == null) {
            return new GeneralPath();
        }
        return glyph.getPath();
    }

    @Override
    public float getWidth(String name) throws IOException {
        int gid = this.nameToGID(name);
        return this.getAdvanceWidth(gid);
    }

    @Override
    public boolean hasGlyph(String name) throws IOException {
        return this.nameToGID(name) != 0;
    }

    @Override
    public BoundingBox getFontBBox() throws IOException {
        HeaderTable headerTable = this.getHeader();
        short xMin = headerTable.getXMin();
        short xMax = headerTable.getXMax();
        short yMin = headerTable.getYMin();
        short yMax = headerTable.getYMax();
        float scale = 1000.0f / (float)this.getUnitsPerEm();
        return new BoundingBox((float)xMin * scale, (float)yMin * scale, (float)xMax * scale, (float)yMax * scale);
    }

    @Override
    public List<Number> getFontMatrix() throws IOException {
        float scale = 1000.0f / (float)this.getUnitsPerEm();
        return Arrays.asList(Float.valueOf(0.001f * scale), 0, 0, Float.valueOf(0.001f * scale), 0, 0);
    }

    public void enableGsubFeature(String featureTag) {
        this.enabledGsubFeatures.add(featureTag);
    }

    public void disableGsubFeature(String featureTag) {
        this.enabledGsubFeatures.remove(featureTag);
    }

    public void enableVerticalSubstitutions() {
        this.enableGsubFeature("vrt2");
        this.enableGsubFeature("vert");
    }

    public String toString() {
        try {
            NamingTable namingTable = this.getNaming();
            if (namingTable != null) {
                return namingTable.getPostScriptName();
            }
            return "(null)";
        }
        catch (IOException e) {
            return "(null - " + e.getMessage() + ")";
        }
    }
}


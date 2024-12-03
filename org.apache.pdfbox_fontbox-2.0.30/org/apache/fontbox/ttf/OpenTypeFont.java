/*
 * Decompiled with CFR 0.152.
 */
package org.apache.fontbox.ttf;

import java.awt.geom.GeneralPath;
import java.io.IOException;
import org.apache.fontbox.ttf.CFFTable;
import org.apache.fontbox.ttf.GlyphTable;
import org.apache.fontbox.ttf.TTFDataStream;
import org.apache.fontbox.ttf.TrueTypeFont;

public class OpenTypeFont
extends TrueTypeFont {
    private boolean isPostScript;

    OpenTypeFont(TTFDataStream fontData) {
        super(fontData);
    }

    @Override
    void setVersion(float versionValue) {
        this.isPostScript = Float.floatToIntBits(versionValue) == 1184802985;
        super.setVersion(versionValue);
    }

    public CFFTable getCFF() throws IOException {
        if (!this.isPostScript) {
            throw new UnsupportedOperationException("TTF fonts do not have a CFF table");
        }
        return (CFFTable)this.getTable("CFF ");
    }

    @Override
    public GlyphTable getGlyph() throws IOException {
        if (this.isPostScript) {
            throw new UnsupportedOperationException("OTF fonts do not have a glyf table");
        }
        return super.getGlyph();
    }

    @Override
    public GeneralPath getPath(String name) throws IOException {
        int gid = this.nameToGID(name);
        return this.getCFF().getFont().getType2CharString(gid).getPath();
    }

    public boolean isPostScript() {
        return this.isPostScript || this.tables.containsKey("CFF ") || this.tables.containsKey("CFF2");
    }

    public boolean isSupportedOTF() {
        return !this.isPostScript || this.tables.containsKey("CFF ") || !this.tables.containsKey("CFF2");
    }

    public boolean hasLayoutTables() {
        return this.tables.containsKey("BASE") || this.tables.containsKey("GDEF") || this.tables.containsKey("GPOS") || this.tables.containsKey("GSUB") || this.tables.containsKey("JSTF");
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.gvt.font;

import org.apache.batik.gvt.font.Kern;

public class KerningTable {
    private Kern[] entries;

    public KerningTable(Kern[] entries) {
        this.entries = entries;
    }

    public float getKerningValue(int glyphCode1, int glyphCode2, String glyphUnicode1, String glyphUnicode2) {
        for (Kern entry : this.entries) {
            if (!entry.matchesFirstGlyph(glyphCode1, glyphUnicode1) || !entry.matchesSecondGlyph(glyphCode2, glyphUnicode2)) continue;
            return entry.getAdjustValue();
        }
        return 0.0f;
    }
}


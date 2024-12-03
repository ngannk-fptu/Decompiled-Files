/*
 * Decompiled with CFR 0.152.
 */
package org.apache.fontbox.ttf;

import java.util.List;
import org.apache.fontbox.ttf.CmapLookup;
import org.apache.fontbox.ttf.CmapSubtable;
import org.apache.fontbox.ttf.GlyphSubstitutionTable;
import org.apache.fontbox.ttf.OpenTypeScript;

public class SubstitutingCmapLookup
implements CmapLookup {
    private final CmapSubtable cmap;
    private final GlyphSubstitutionTable gsub;
    private final List<String> enabledFeatures;

    public SubstitutingCmapLookup(CmapSubtable cmap, GlyphSubstitutionTable gsub, List<String> enabledFeatures) {
        this.cmap = cmap;
        this.gsub = gsub;
        this.enabledFeatures = enabledFeatures;
    }

    @Override
    public int getGlyphId(int characterCode) {
        int gid = this.cmap.getGlyphId(characterCode);
        String[] scriptTags = OpenTypeScript.getScriptTags(characterCode);
        return this.gsub.getSubstitution(gid, scriptTags, this.enabledFeatures);
    }

    @Override
    public List<Integer> getCharCodes(int gid) {
        return this.cmap.getCharCodes(this.gsub.getUnsubstitution(gid));
    }
}


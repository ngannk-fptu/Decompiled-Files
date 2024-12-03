/*
 * Decompiled with CFR 0.152.
 */
package org.apache.fontbox.ttf;

import java.util.List;

public interface CmapLookup {
    public int getGlyphId(int var1);

    public List<Integer> getCharCodes(int var1);
}


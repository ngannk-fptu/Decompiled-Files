/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.fontbox.FontBoxFont
 */
package org.apache.pdfbox.pdmodel.font;

import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.fontbox.FontBoxFont;
import org.apache.pdfbox.pdmodel.font.FontInfo;

public final class FontCache {
    private final Map<FontInfo, SoftReference<FontBoxFont>> cache = new ConcurrentHashMap<FontInfo, SoftReference<FontBoxFont>>();

    public void addFont(FontInfo info, FontBoxFont font) {
        this.cache.put(info, new SoftReference<FontBoxFont>(font));
    }

    public FontBoxFont getFont(FontInfo info) {
        SoftReference<FontBoxFont> reference = this.cache.get(info);
        return reference != null ? reference.get() : null;
    }
}

